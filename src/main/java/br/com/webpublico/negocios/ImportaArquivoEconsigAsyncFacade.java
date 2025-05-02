/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ImportaMovimentoFinanceiro;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.TipoImportacao;
import br.com.webpublico.enums.TipoLancamentoFP;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author Peixe
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ImportaArquivoEconsigAsyncFacade {

    protected static final Logger logger = LoggerFactory.getLogger(ImportaArquivoEconsigAsyncFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private br.com.webpublico.negocios.MotivoRejeicaoFacade motivoRejeicaoFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private EntidadeConsignatariaFacade entidadeConsignatariaFacade;
    @EJB
    private EventoFPFacade eventoFPFacade;
    @EJB
    private LancamentoFPFacade facade;
    @EJB
    private LotacaoFuncionalFacade lotacaoFuncionalFacade;
    @EJB
    private HierarquiaOrganizacionalFacadeOLD hierarquiaOrganizacionalFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacadeNew;
    @EJB
    private LancamentoFPFacade lancamentoFPFacade;
    @EJB
    private ArquivoEconsigFacade arquivoEconsigFacade;
    @EJB
    private RecursoFPFacade recursoFPFacade;
    @EJB
    private RecursoDoVinculoFPFacade recursoDoVinculoFPFacade;

    protected EntityManager getEntityManager() {
        return em;
    }


    @Asynchronous
    public void processarArquivo(br.com.webpublico.entidadesauxiliares.ImportaMovimentoFinanceiro arquivo, Date dataOperacao) throws FileNotFoundException, IOException {
        arquivo.setLiberaCaixaDialogo(true);

        InputStreamReader streamReader = new InputStreamReader(arquivo.getFile().getInputstream());
        BufferedReader reader = new BufferedReader(streamReader);

        InputStreamReader streamReader2 = new InputStreamReader(arquivo.getFile().getInputstream());
        BufferedReader reader2 = new BufferedReader(streamReader2);
        String linha;
        int count = 0;
        while ((linha = reader2.readLine()) != null) {
            if (!linha.isEmpty() && linha.length() > 2) {
                count++;
            }
        }
        String line = null;
        String consignataria;


        LancamentoFP lancamento;
        List<MotivoRejeicao> listaMotivos = motivoRejeicaoFacade.lista();
        System.out.println(count);
        arquivo.setContadorTotal(count);

        DateTime dt = new DateTime();
        dt = dt.withMonthOfYear(arquivo.getMes());
        dt = dt.withYear(arquivo.getAno());
        dt = dt.withDayOfMonth(1);


        Map<String, HierarquiaOrganizacional> hierarquiaOrganizacionalCache = new LinkedHashMap<>();
        carregarHierarquias(hierarquiaOrganizacionalCache, dt.toDate());
        Map<Long, HierarquiaOrganizacional> hierarquiaOrganizacionalAdministrativas = new LinkedHashMap<>();
        carregarHierarquiasAdmin(hierarquiaOrganizacionalAdministrativas, dt.toDate());
        Map<String, EventoFP> stringEventoFPLinkedHashMap = new LinkedHashMap<>();
        carregarEventos(stringEventoFPLinkedHashMap);
        Map<String, EntidadeConsignataria> consignatariaLinkedHashMap = new LinkedHashMap<>();
        carregarConsignatarias(consignatariaLinkedHashMap);
        Map<String, RecursoFP> recursoFPMap = new LinkedHashMap<>();
        carregarRecursos(recursoFPMap);


        while ((line = reader.readLine()) != null) {
            if (arquivo.isCancelar()) {
                break;
            }
            if (line.isEmpty()) {
                continue;
            }
            arquivo.somaContador();
            lancamento = new LancamentoFP();
            lancamento.setTipoImportacao(TipoImportacao.ECONSIG);
            String matricula = "";
            String numeroContrato = "";
            String orgao = "";
            String valor = "";
            String dataInicial = "";
            String dataFinal = "";
            String situacao = "";
            String dataInclusao = "";
            String verba = "";
            String tipo = "";


            consignataria = line.substring(0, 3);
            //verba
            verba = line.substring(3, 7);

            //Matricula
            matricula = line.substring(7, 14);
            //Numero do contrato
            numeroContrato = line.substring(14, 16);

            //Órgão
            orgao = line.substring(16, 18);

            //Valor
            valor = line.substring(18, 27) + "." + line.substring(27, 29);
            //lancamento.set

            //Tipo

            tipo = line.substring(29, 30);
            TipoLancamentoFP tipoLancamentoFP = TipoLancamentoFP.find(tipo);

            //DataInicial
            dataInicial = line.substring(30, 36);

            //DataFinal
            dataFinal = line.substring(36, 42);

            //Situacao
            situacao = line.substring(42, 43);

            //Data de Inclusão
            dataInclusao = line.substring(43, 57);

            Calendar c = Calendar.getInstance();
            c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dataInclusao.substring(0, 2)));
            c.set(Calendar.MONTH, Integer.parseInt(dataInclusao.substring(2, 4)));
            c.set(Calendar.YEAR, Integer.parseInt(dataInclusao.substring(4, 8)));
            c.set(Calendar.HOUR, Integer.parseInt(dataInclusao.substring(8, 10)));
            c.set(Calendar.MINUTE, Integer.parseInt(dataInclusao.substring(10, 12)));
            c.set(Calendar.SECOND, Integer.parseInt(dataInclusao.substring(12, 14)));
            lancamento.setDataCadastroEconsig(c.getTime());

            Calendar ca = Calendar.getInstance();
            ca.set(Calendar.YEAR, Integer.parseInt(dataInicial.substring(0, 4)));
            ca.set(Calendar.MONTH, Integer.parseInt(dataInicial.substring(4, 6)) - 1);
            ca.set(Calendar.DAY_OF_MONTH, ca.getActualMinimum(Calendar.DAY_OF_MONTH));
            lancamento.setMesAnoInicial(ca.getTime());
            Calendar caf = Calendar.getInstance();
            caf.set(Calendar.YEAR, Integer.parseInt(dataFinal.substring(0, 4)));
            caf.set(Calendar.MONTH, Integer.parseInt(dataFinal.substring(4, 6)) - 1);
            caf.set(Calendar.DAY_OF_MONTH, caf.getActualMaximum(Calendar.DAY_OF_MONTH));
            lancamento.setMesAnoFinal(caf.getTime());


            lancamento.setTipoLancamentoFP(tipoLancamentoFP);
            lancamento.setLinhaArquivo(line);
            String numeroMatricula = StringUtil.removeZerosEsquerda(matricula);

            Integer numeroC = null;
            VinculoFP contrato = null;
            if(StringUtil.isNumerico(numeroContrato)) {
                 numeroC = Integer.parseInt(numeroContrato);
                 contrato = buscarAndDefinirContrato(numeroMatricula, String.valueOf(numeroC), dataOperacao, arquivo);
            }
            if (!StringUtil.isNumerico(matricula) || !StringUtil.isNumerico(numeroContrato)) {
                lancamento.setMotivoRejeicao(facade.getMotivoRejeicao(listaMotivos, 4));
                arquivo.somarConsignitarioNaoEncontrado();
                adicionarErroEconsig(new ErroEconsig(matricula, numeroC, "Matrícula/Contrato com carácter não numérico. Matrícula/Contrato: " + matricula + "/" + numeroContrato, verba, consignataria, line), arquivo.getErrosEconsig());
            }
            if(contrato == null && StringUtil.isNumerico(matricula) && StringUtil.isNumerico(numeroContrato)) {
                adicionarErroEconsig(new ErroEconsig(matricula, numeroC, facade.getMotivoRejeicao(listaMotivos, 4).toString(), verba, consignataria, line), arquivo.getErrosEconsig());
                arquivo.getLinhaProblema().add("Contrato não encontrado: matricula número:  " + numeroMatricula + " número contrato: " + numeroContrato);
            }

            if (contrato != null) {

                if (contrato.getInicioVigencia() != null && contrato.getFinalVigencia() != null && !new Interval(new DateTime(contrato.getInicioVigencia()), new DateTime(contrato.getFinalVigencia())).contains(new DateTime(dataOperacao))) {
                    lancamento.setMotivoRejeicao(facade.getMotivoRejeicao(listaMotivos, 6));
                    String erro = "Contrato não vigente:" + matricula + " contrato: " + contrato;
                    adicionarErroEconsig(new ErroEconsig(matricula, numeroC, "Contrato não está vigente", verba, consignataria, line), arquivo.getErrosEconsig());
                    arquivo.getLinhaProblema().add(erro);
                }
                lancamento.setVinculoFP(contrato);
                Integer v = Integer.parseInt(verba);
                int consig = Integer.parseInt(consignataria);
                EntidadeConsignataria ec = consignatariaLinkedHashMap.get(consig + "");


                EventoFP ev = stringEventoFPLinkedHashMap.get(String.valueOf(v));

                if (ev == null) {
                    lancamento.setMotivoRejeicao(facade.getMotivoRejeicao(listaMotivos, 2));
                    arquivo.somarEventoNaoEncontrado();
                    adicionarErroEconsig(new ErroEconsig(matricula, numeroC, facade.getMotivoRejeicao(listaMotivos, 2).toString(), verba, consignataria, line), arquivo.getErrosEconsig());
                }

                if (ev!= null && ev.getTipoDeConsignacao() != null && Double.parseDouble(valor) < 1) {
                    lancamento.setMotivoRejeicao(facade.getMotivoRejeicao(listaMotivos, 14));
                    arquivo.somarConsignitarioNaoEncontrado();
                    adicionarErroEconsig(new ErroEconsig(matricula, numeroC, facade.getMotivoRejeicao(listaMotivos, 14).toString(), verba, consignataria, line), arquivo.getErrosEconsig());
                }

                if (ec == null) {
                    lancamento.setMotivoRejeicao(facade.getMotivoRejeicao(listaMotivos, 1));
                    arquivo.somarConsignitarioNaoEncontrado();
                    adicionarErroEconsig(new ErroEconsig(matricula, numeroC, facade.getMotivoRejeicao(listaMotivos, 1).toString(), verba, consignataria, line), arquivo.getErrosEconsig());
                } else {

                    EntidadeConsignataria ecev = entidadeConsignatariaFacade.recuperaEntidadeConsignatariaPorEvento(ec, ev);

                    if (ecev == null) {
                        lancamento.setMotivoRejeicao(facade.getMotivoRejeicao(listaMotivos, 3));
                        arquivo.somarEventoEconsigNaoRelacionado();
                        adicionarErroEconsig(new ErroEconsig(matricula, numeroC, facade.getMotivoRejeicao(listaMotivos, 3).toString(), verba, consignataria, line), arquivo.getErrosEconsig());
                    }
                }
                Integer orgaoInt = Integer.parseInt(orgao);

                HierarquiaOrganizacional ho = hierarquiaOrganizacionalCache.get(orgaoInt + "");
                if (ho == null) {
                    lancamento.setMotivoRejeicao(facade.getMotivoRejeicao(listaMotivos, 7));
                    arquivo.somarSecretariaInexistente();
                    adicionarErroEconsig(new ErroEconsig(matricula, numeroC, facade.getMotivoRejeicao(listaMotivos, 7).toString(), verba, consignataria, line), arquivo.getErrosEconsig());
                } else {
                    int contador = 0;
                }

                lancamento.setEventoFP(ev);
                lancamento.setQuantificacao(new BigDecimal(valor));


                lancamento.setDataCadastro(dataOperacao);

            }

            if (StringUtil.isNumerico(matricula) && StringUtil.isNumerico(numeroContrato) && facade.validarLancamento(arquivo, lancamento, line, matricula, tipo, dataInicial, dataFinal, verba)) {
                if (facade.naoExisteLancamento(lancamento)) {
                    arquivo.somaTotalArquivosSeremSalvos();
                    lancamento.setTipoFolhaDePagamento(TipoFolhaDePagamento.NORMAL);
                    arquivo.getLancamentos().add(lancamento);
                }
                LancamentoFP l = facade.getLancamentoExistenteEconsig(lancamento);
                if (l != null) {
                    l.setTipoImportacao(TipoImportacao.ECONSIG);
                    l.setTipoFolhaDePagamento(TipoFolhaDePagamento.NORMAL);
                    l.setDataCadastroEconsig(lancamento.getDataCadastroEconsig());
                    arquivo.getLancamentos().add(l);
                }
            }
            if (lancamento.getMotivoRejeicao() != null) {
                arquivo.getLinhaProblema().add("AVISO: " + line + " " + lancamento.getMotivoRejeicao() + " - Cod Consig:" + consignataria + " - Matricula:" + matricula + "/" + numeroContrato + " - Orgão:" + orgao + " - Verba(Evento):" + verba);
            }


        }


        arquivo.setContadorOk(arquivo.getContadorTotal() - arquivo.getContadorProblema() - arquivo.getContadorRejeitados());
        reader.close();
        streamReader.close();
        arquivo.getFile().getInputstream().close();
        arquivo.setLiberaCaixaDialogo(false);
        arquivo.setPodeMostrarMensagem(true);
    }

    private VinculoFP buscarAndDefinirContrato(String matricula, String contrato, Date dataOperacao, ImportaMovimentoFinanceiro arquivo) {
        dataOperacao = Util.getDataHoraMinutoSegundoZerado(dataOperacao);
        List<VinculoFP> vinculos = vinculoFPFacade.recuperarContratoMatriculaVigente(matricula, contrato, dataOperacao);
        if (vinculos != null && !vinculos.isEmpty()) {
            if (vinculos.size() > 1) {
                VinculoFP vinculoFP = buscarTipoContrato(vinculos);
                if (vinculoFP != null) {
                    return vinculoFP;
                }
                vinculoFP = buscarTipoAposentadoria(vinculos);
                if (vinculoFP != null) {
                    return vinculoFP;
                }
                vinculoFP = buscarTipoPensionista(vinculos);
                if (vinculoFP != null) {
                    return vinculoFP;
                }
                vinculoFP = buscarTipoBeneficiario(vinculos);
                if (vinculoFP != null) {
                    return vinculoFP;
                }
            } else {
                return vinculos.get(0);
            }
        }
        List<VinculoFP> vinculoFPS = vinculoFPFacade.buscarVinculosVigentePorMatricula(matricula, dataOperacao);
        if (vinculoFPS != null && !vinculoFPS.isEmpty()) {
            if (vinculoFPS.size() == 1) {
                return vinculoFPS.get(0);
            }
            if (vinculoFPS.size() > 1) {
                logger.debug("A matricula {} possui mais de um vínculo ativo, o contrato {} será utilizado! ", matricula, vinculoFPS.get(0));
                return vinculoFPS.get(0);
            }
        }
        return vinculoFPFacade.recuperarVinculoPorMatriculaENumero(matricula, contrato);
    }

    private VinculoFP buscarTipoContrato(List<VinculoFP> vinculos) {
        for (VinculoFP vinculo : vinculos) {
            if (vinculo instanceof ContratoFP) {
                return vinculo;
            }
        }
        return null;
    }

    private VinculoFP buscarTipoAposentadoria(List<VinculoFP> vinculos) {
        for (VinculoFP vinculo : vinculos) {
            if (vinculo instanceof Aposentadoria) {
                return vinculo;
            }
        }
        return null;
    }

    private VinculoFP buscarTipoPensionista(List<VinculoFP> vinculos) {
        for (VinculoFP vinculo : vinculos) {
            if (vinculo instanceof Pensionista) {
                return vinculo;
            }
        }
        return null;
    }

    private VinculoFP buscarTipoBeneficiario(List<VinculoFP> vinculos) {
        for (VinculoFP vinculo : vinculos) {
            if (vinculo instanceof Beneficiario) {
                return vinculo;
            }
        }
        return null;
    }

    private void adicionarErroEconsig(ErroEconsig erro, List<ErroEconsig> lista) {
        lista.add(erro);
    }

    private void carregarHierarquias(Map<String, HierarquiaOrganizacional> hierarquiaOrganizacionalCache, Date data) {
//        AND SUBSTR(CODIGONO,1,3) = :codigo
        List<HierarquiaOrganizacional> hos = hierarquiaOrganizacionalFacade.recuperaHierarquiaOrgao();
        if (hos != null) {
            for (HierarquiaOrganizacional ho : hos) {
                hierarquiaOrganizacionalCache.put(ho.getCodigoNo(), ho);
            }
        }
    }

    private void carregarHierarquiasAdmin(Map<Long, HierarquiaOrganizacional> hierarquiaOrganizacionalCache, Date data) {
        List<HierarquiaOrganizacional> hos = hierarquiaOrganizacionalFacadeNew.getListaHierarquiasAdministrativasTodas();
        if (hos != null) {
            for (HierarquiaOrganizacional ho : hos) {
                hierarquiaOrganizacionalCache.put(ho.getSubordinada().getId(), ho);
            }
        }
    }

    private void carregarEventos(Map<String, EventoFP> stringEventoFPMap) {
        List<EventoFP> events = eventoFPFacade.listaEventosAtivosFolha();
        if (events != null) {
            for (EventoFP ev : events) {
                stringEventoFPMap.put(ev.getCodigo(), ev);
            }
        }
    }

    private void carregarConsignatarias(Map<String, EntidadeConsignataria> entidadeConsignatariaMap) {
        List<EntidadeConsignataria> events = entidadeConsignatariaFacade.lista();
        if (events != null) {
            for (EntidadeConsignataria ev : events) {
                entidadeConsignatariaMap.put(ev.getCodigo() + "", ev);
            }
        }
    }

    private void carregarRecursos(Map<String, RecursoFP> recursoFPMap) {
        List<RecursoFP> recursos = recursoFPFacade.lista();
        if (recursos != null) {
            for (RecursoFP ev : recursos) {
                recursoFPMap.put(ev.getCodigo().substring(0, 2) + "", ev);
            }
        }
    }

    public void salvar(Object obj) {
        try {
            em.merge(obj);
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Erro ao salvar importação do econsig", ex);
        }

    }
}
