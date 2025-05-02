package br.com.webpublico.negocios.tributario.services;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ExportacaoDebitosIPTU;
import br.com.webpublico.entidadesauxiliares.AssistenteExportacaoDebitosIPTU;
import br.com.webpublico.entidadesauxiliares.VOLinhasExportacaoDebitosIPTU;
import br.com.webpublico.entidadesauxiliares.VOParcelaExportacaoDebitosIPTU;
import br.com.webpublico.enums.SituacaoExportacaoDebitosIPTU;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.tributario.dao.JdbcDamDAO;
import br.com.webpublico.negocios.tributario.dao.JdbcExportacaoDebitosIptuDAO;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.nio.CharBuffer;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.*;

@Service
public class ServiceExportacaoDebitosIPTU {
    private final Logger logger = LoggerFactory.getLogger(ServiceExportacaoDebitosIPTU.class);
    private final int timeout = 36000;

    @PersistenceContext
    protected transient EntityManager em;

    @Autowired
    private JdbcExportacaoDebitosIptuDAO exportacaoDebitosIptuDAO;
    @Autowired
    private JdbcDamDAO damDAO;

    private SistemaFacade sistemaFacade;

    private Map<Long, AssistenteExportacaoDebitosIPTU> exportacoesIPTU;

    @PostConstruct
    public void init() {
        try {
            sistemaFacade = (SistemaFacade) Util.getFacadeViaLookup("java:module/SistemaFacade");
        } catch (Exception e) {
            logger.error("Erro ao recuperar sistema facade via lookup. ", e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, timeout = timeout)
    public Future<AssistenteExportacaoDebitosIPTU> gerarArquivoDeExportacao(AssistenteExportacaoDebitosIPTU assistente) {
        if (isEmptyExportacoes()) {
            ExecutorService executorService = Executors.newFixedThreadPool(1);
            ExportacaoDebitosIPTU exportacao = recuperarExportacao();
            if (exportacao != null) {
                assistente = assistente == null ? instanciarAssistente(exportacao) : assistente;
                adicionarExportacao(assistente);
                try {
                    Future<AssistenteExportacaoDebitosIPTU> futureParcelas = executorService.submit(buscarParcelasDeIPTU(assistente));
                    executorService.shutdown();
                    awaitTermination(futureParcelas);

                    assistente = futureParcelas.get();

                    executorService = Executors.newFixedThreadPool(1);
                    return executorService.submit(montarArquivo(assistente));
                } catch (Exception e) {
                    logger.error("Erro ao gerar arquivo de exportacao. ", e);
                    executarCasoException(assistente);
                } finally {
                    executorService.shutdown();
                }
            }
        }
        return null;
    }

    private ExportacaoDebitosIPTU recuperarExportacao() {
        String sql = " select exportacao.* from exportacaodebitosiptu exportacao " +
            " inner join exercicio ex on exportacao.exercicio_id = ex.id " +
            " where ex.ano = :ano " +
            " and exportacao.situacaoexportacaodebitosiptu = :situacao " +
            " order by exportacao.id desc ";

        try {
            Query q = em.createNativeQuery(sql, ExportacaoDebitosIPTU.class);
            q.setParameter("ano", Calendar.getInstance().get(Calendar.YEAR));
            q.setParameter("situacao", SituacaoExportacaoDebitosIPTU.ABERTO.name());

            List<ExportacaoDebitosIPTU> exportacoes = q.getResultList();

            if (exportacoes != null && !exportacoes.isEmpty()) {
                ExportacaoDebitosIPTU exportacao = exportacoes.get(0);
                exportacao.setSituacaoExportacaoDebitosIPTU(SituacaoExportacaoDebitosIPTU.GERANDO);
                return em.merge(exportacao);
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar exportacao de debitos de iptu. ", e);
        }
        return null;
    }

    private AssistenteExportacaoDebitosIPTU instanciarAssistente(ExportacaoDebitosIPTU exportacao) {
        AssistenteExportacaoDebitosIPTU assistente = new AssistenteExportacaoDebitosIPTU();
        assistente.setSelecionado(exportacao);
        assistente.setExercicio(exportacao.getExercicio());
        assistente.setPerfilDev(sistemaFacade.isPerfilDev());
        assistente.setExecutando(true);

        return assistente;
    }

    private Callable<AssistenteExportacaoDebitosIPTU> buscarParcelasDeIPTU(final AssistenteExportacaoDebitosIPTU assistente) {
        return new Callable<AssistenteExportacaoDebitosIPTU>() {
            @Override
            public AssistenteExportacaoDebitosIPTU call() {
                assistente.setDescricaoProcesso("Consultando Débitos...");
                assistente.setConfiguracaoDAM(exportacaoDebitosIptuDAO.buscarConfiguracaoDAMIptu());

                List<VOParcelaExportacaoDebitosIPTU> parcelasIPTU = exportacaoDebitosIptuDAO.buscarParcelasIPTU(assistente);

                assistente.setParcelasIPTU(parcelasIPTU);
                return assistente;
            }
        };
    }

    private Callable<AssistenteExportacaoDebitosIPTU> montarArquivo(final AssistenteExportacaoDebitosIPTU assistente) {
        return new Callable<AssistenteExportacaoDebitosIPTU>() {
            @Override
            public AssistenteExportacaoDebitosIPTU call() {
                return gerarArquivoDeExportacaoDebitosIPTU(assistente);
            }
        };
    }

    private AssistenteExportacaoDebitosIPTU persistirDadosPosExportacao(final AssistenteExportacaoDebitosIPTU assistente) {
        if (assistente.getDans().isEmpty() && assistente.getLinhas().isEmpty())
            return assistente;

        final ExecutorService executorService = Executors.newFixedThreadPool(1);

        final TransactionTemplate transactionTemplate = instanciarTransactionTemplate();
        transactionTemplate.setTimeout(timeout);

        return transactionTemplate.execute(new TransactionCallback<AssistenteExportacaoDebitosIPTU>() {
            @Override
            public AssistenteExportacaoDebitosIPTU doInTransaction(final TransactionStatus transactionStatus) {
                try {
                    Future<AssistenteExportacaoDebitosIPTU> futureLinhas = executorService.submit(new Callable<AssistenteExportacaoDebitosIPTU>() {
                        @Override
                        public AssistenteExportacaoDebitosIPTU call() {
                            damDAO.persisteDans(assistente.getDans(), assistente.getItensDAM(), assistente.getImpressoesDAM(),
                                assistente.getTributosDAM(), assistente.getUsuarioSistema(), assistente);

                            exportacaoDebitosIptuDAO.inserirLinhasDoArquivo(assistente);
                            return assistente;
                        }
                    });
                    awaitTermination(futureLinhas);
                    return futureLinhas.get();
                } catch (Exception e) {
                    transactionStatus.setRollbackOnly();
                    executarCasoException(assistente);
                    logger.error("Erro ao executar pos exportacao arquivo de debitos de iptu. ", e);
                } finally {
                    executorService.shutdown();
                }
                return assistente;
            }
        });
    }

    private void executarCasoException(AssistenteExportacaoDebitosIPTU assistente) {
        if (assistente != null && assistente.getSelecionado() != null && assistente.getSelecionado().getId() != null) {
            assistente.getSelecionado().setSituacaoExportacaoDebitosIPTU(SituacaoExportacaoDebitosIPTU.ABERTO);
            assistente.setSelecionado(em.merge(assistente.getSelecionado()));
            assistente.setExecutando(false);
            removerExportacao(assistente.getSelecionado().getId());
            exportacaoDebitosIptuDAO.excluirLinhasExportacaoIPTU(assistente.getSelecionado());
        }
    }

    private TransactionTemplate instanciarTransactionTemplate() {
        PlatformTransactionManager transactionManager = Util.recuperarSpringBean(PlatformTransactionManager.class);
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_DEFAULT);
        return transactionTemplate;
    }

    private AssistenteExportacaoDebitosIPTU gerarArquivoDeExportacaoDebitosIPTU(AssistenteExportacaoDebitosIPTU assistente) {
        ExportacaoDebitosIPTU selecionado = assistente.getSelecionado();
        Integer contador = 0;
        if (assistente.getParcelasIPTU() != null && !assistente.getParcelasIPTU().isEmpty()) {
            assistente.setDescricaoProcesso("Gerando arquivo...");
            exportacaoDebitosIptuDAO.adicionarNumeroRemessa(assistente);

            Collections.sort(assistente.getParcelasIPTU(), VOParcelaExportacaoDebitosIPTU.comparator());

            int total = ((assistente.getParcelasIPTU().size() * 2) + (assistente.getDans().size() * 2) +
                (assistente.getItensDAM().size() + assistente.getImpressoesDAM().size() +
                assistente.getTributosDAM().size()) + 4);

            assistente.zerarContadoresProcesso();
            assistente.setTotal(total);

            contador++;
            assistente.getLinhas().add(new VOLinhasExportacaoDebitosIPTU(selecionado.getId(), contador.longValue(),
                montarHeader(selecionado, assistente.getExercicio(), contador, assistente.isPerfilDev())));
            assistente.conta();

            int numParcelas = assistente.getParcelasIPTU().size();

            for (int i = 0; i < numParcelas; i++) {
                try {
                    VOParcelaExportacaoDebitosIPTU parcelaIPTU = assistente.getParcelasIPTU().get(i);

                    if (parcelaIPTU.getParcela().getIdCadastro() != null && parcelaIPTU.getParcela().getIdPessoa() != null) {
                        String detalhe = montarDetalhe(parcelaIPTU, contador);

                        if (!detalhe.isEmpty()) {
                            contador++;
                            assistente.getLinhas().add(new VOLinhasExportacaoDebitosIPTU(selecionado.getId(), contador.longValue(), detalhe));
                        }
                        logger.debug("Montando linha: " + assistente.getParcelasIPTU().indexOf(parcelaIPTU) + "/" + assistente.getParcelasIPTU().size());
                    }
                } finally {
                    assistente.conta();
                }
            }

            contador++;
            assistente.getLinhas().add(new VOLinhasExportacaoDebitosIPTU(selecionado.getId(), contador.longValue(), montarTrailer(contador)));
            assistente.conta();

            assistente = persistirDadosPosExportacao(assistente);
        } else {
            assistente.setEncontrouParcelas(false);
        }
        removerExportacao(assistente.getSelecionado().getId());
        assistente.setExecutando(false);
        return assistente;
    }

    private String montarHeader(ExportacaoDebitosIPTU selecionado, Exercicio exercicio, Integer contador, boolean isPerfilDev) {
        StringBuilder header = new StringBuilder();
        header.append(conteudoAlfanumerico("A", 1));
        header.append(conteudoNumerico(selecionado.getConvenioListaDebitos().getNumeroConvenio().toString(), 6));
        header.append(conteudoNumerico(DataUtil.converterAnoMesDia(new Date()), 8));
        header.append(conteudoAlfanumerico("RCB800", 7));
        if (isPerfilDev) {
            header.append(conteudoAlfanumerico("T", 2));
        } else {
            header.append(conteudoAlfanumerico("P", 2));
        }
        header.append(conteudoNumerico(selecionado.getConvenioListaDebitos().getAgencia().getNumeroAgencia(), 4));
        header.append(conteudoNumerico(exercicio.getAno().toString(), 4));
        header.append(conteudoNumerico(selecionado.getNumeroRemessa().toString(), 5));
        header.append(conteudoNumerico(DataUtil.converterAnoMesDia(selecionado.getConvenioListaDebitos().getDataInicialDispDebitos()), 8));
        header.append(conteudoNumerico(DataUtil.converterAnoMesDia(selecionado.getConvenioListaDebitos().getDataFinalDispDebitos()), 8));
        header.append(conteudoNumerico(selecionado.getConvenioListaDebitos().getCodigoConvenioMCI().toString(), 9));
        header.append(conteudoAlfanumerico("", 379));
        header.append(conteudoNumerico(contador.toString(), 9));

        return header.toString();
    }

    private String montarDetalhe(VOParcelaExportacaoDebitosIPTU parcelaIPTU, Integer contador) {
        if (parcelaIPTU.getDam() != null) {
            StringBuilder detalhe = new StringBuilder(450);

            detalhe.append(conteudoAlfanumerico("2", 1));
            detalhe.append(conteudoAlfanumerico("Prefeitura Municipal de Rio Branco", 60));
            detalhe.append(conteudoNumerico("1", 2));
            detalhe.append(conteudoNumerico(retornarNumeros(parcelaIPTU.getPessoa() != null ? parcelaIPTU.getPessoa().getCpfCnpj() : ""), 14));
            detalhe.append(conteudoAlfanumerico(parcelaIPTU.getParcela().getDivida(), 20));
            detalhe.append(conteudoAlfanumerico(parcelaIPTU.getParcela().getExercicio().toString(), 20));
            detalhe.append(conteudoAlfanumerico(parcelaIPTU.getCadastroImobiliario() != null ? parcelaIPTU.getCadastroImobiliario().getInscricaoCadastral() : "", 100));
            detalhe.append(conteudoNumerico(DataUtil.converterAnoMesDia(parcelaIPTU.getDam().getVencimento()), 8));
            detalhe.append(conteudoAlfanumerico(retornarNumeros(parcelaIPTU.getDam().getCodigoBarras()), 48));
            detalhe.append(montarValorDecimal(parcelaIPTU.getParcela().getValorTotal()));
            detalhe.append(conteudoNumerico("9", 2));

            if (parcelaIPTU.getParcela().getCotaUnica()) {
                detalhe.append(conteudoNumerico("0", 3));
            } else {
                detalhe.append(conteudoNumerico(parcelaIPTU.getParcela().getSequenciaParcela().toString(), 3));
            }
            detalhe.append(conteudoAlfanumerico("", 17));
            detalhe.append(montarValorDecimal(parcelaIPTU.getCadastroImobiliario() != null && parcelaIPTU.getCadastroImobiliario().getValorVenal() != null ? parcelaIPTU.getCadastroImobiliario().getValorVenal() : BigDecimal.ZERO));
            detalhe.append(conteudoAlfanumerico(retornarNumeros(parcelaIPTU.getDam().getCodigoBarrasCotaUnica()), 48));
            detalhe.append(conteudoAlfanumerico(parcelaIPTU.getPessoa() != null && parcelaIPTU.getPessoa().getTipoPessoa() != null ? parcelaIPTU.getPessoa().getTipoPessoa() : "", 1));

            detalhe.append(conteudoAlfanumerico("", 75));
            detalhe.append(conteudoNumerico(Integer.toString(contador + 1), 9));

            return detalhe.toString();
        }
        return "";
    }

    private String montarTrailer(Integer contador) {
        return conteudoAlfanumerico("Z", 1) + conteudoNumerico((contador - 2) + "", 9) + conteudoAlfanumerico("", 431) + conteudoNumerico(contador.toString(), 9);
    }

    private String conteudoNumerico(String texto, Integer tamanho) {
        return StringUtil.cortarOuCompletarEsquerda(texto != null ? texto : "", tamanho, "0");
    }

    private String conteudoAlfanumerico(String texto, Integer tamanho) {
        return StringUtil.cortaOuCompletaDireita(texto != null ? texto : "", tamanho, " ");
    }

    private String montarValorDecimal(BigDecimal valor) {
        valor = valor == null ? BigDecimal.ZERO : valor;
        String valorFormatado = new DecimalFormat("#.00").format(valor);
        valorFormatado = valorFormatado.replace(".", "").replace(",", "");

        return conteudoNumerico(valorFormatado, 11);
    }

    private String retornarNumeros(String texto) {
        if (texto == null || texto.isEmpty())
            return "";

        char[] result = new char[texto.length()];
        int cursor = 0;

        CharBuffer buffer = CharBuffer.wrap(texto);
        while (buffer.hasRemaining()) {
            char chr = buffer.get();
            if (chr > 47 && chr < 58)
                result[cursor++] = chr;
        }
        return String.valueOf(result, 0, cursor);
    }

    private void awaitTermination(Future<AssistenteExportacaoDebitosIPTU> future) {
        try {
            while (!future.isDone()) {
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (InterruptedException ie) {
            logger.error("Erro ao aguardar future. ", ie);
        }
    }

    public synchronized void adicionarExportacao(AssistenteExportacaoDebitosIPTU assistente) {
        if (exportacoesIPTU == null) {
            exportacoesIPTU = Maps.newHashMap();
        }
        exportacoesIPTU.put(assistente.getSelecionado().getId(), assistente);
    }

    public synchronized void removerExportacao(Long idExportacao) {
        if (exportacoesIPTU != null) {
            exportacoesIPTU.remove(idExportacao);
        }
    }

    public synchronized AssistenteExportacaoDebitosIPTU getExportacao(Long idExportacao) {
        if (exportacoesIPTU != null) {
            return exportacoesIPTU.get(idExportacao);
        }
        return null;
    }

    public synchronized boolean containsExportacao(Long idExportacao) {
        return exportacoesIPTU != null && exportacoesIPTU.containsKey(idExportacao);
    }

    public synchronized boolean isEmptyExportacoes() {
        return exportacoesIPTU == null || exportacoesIPTU.isEmpty();
    }
}
