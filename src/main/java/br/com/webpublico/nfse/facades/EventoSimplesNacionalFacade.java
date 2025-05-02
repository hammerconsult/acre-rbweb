package br.com.webpublico.nfse.facades;

import br.com.webpublico.entidades.ArquivoComposicao;
import br.com.webpublico.entidades.EnquadramentoFiscal;
import br.com.webpublico.enums.RegimeTributario;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CadastroEconomicoFacade;
import br.com.webpublico.nfse.domain.EventoSimplesNacional;
import br.com.webpublico.nfse.domain.LinhaEventoSimplesNacional;
import br.com.webpublico.nfse.domain.TipoEventoSimplesNacional;
import br.com.webpublico.nfse.enums.NaturezaEventoSimplesNacional;
import br.com.webpublico.seguranca.service.SistemaService;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

@Stateless
public class EventoSimplesNacionalFacade extends AbstractFacade<EventoSimplesNacional> {


    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private LinhaEventoSimplesNacionalFacade linhaEventoSimplesNacionalFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private TipoEventoSimplesNacionalFacade tipoEventoSimplesNacionalFacade;

    private SistemaService sistemaService;

    @PostConstruct
    private void init() {
        sistemaService = (SistemaService) Util.getSpringBeanPeloNome("sistemaService");
    }


    public EventoSimplesNacionalFacade() {
        super(EventoSimplesNacional.class);
    }

    public SistemaService getSistemaService() {
        return sistemaService;
    }

    @Asynchronous
    public Future<List<LinhaEventoSimplesNacional>> lerArquivo(EventoSimplesNacional selecionado, AssistenteBarraProgresso assistente) {
        List<LinhaEventoSimplesNacional> toReturn = Lists.newArrayList();
        ArquivoComposicao arquivoComposicao = selecionado.getArquivoImportacao().getArquivoComposicao();
        arquivoComposicao.getArquivo().montarImputStream();
        InputStream inputStream = arquivoComposicao.getArquivo().getInputStream();
        BufferedReader br = null;

        try {
            br = new BufferedReader(new InputStreamReader(inputStream));

            List<String> linhas = Lists.newArrayList();
            String l = null;
            while ((l = br.readLine()) != null) {
                linhas.add(l);
            }

            assistente.setTotal(linhas.size());

            assistente.adicionarLogHtml("Início da Leitura do Arquivo");

            AtomicInteger numeroLinha = new AtomicInteger(0);
            for (String linha : linhas) {
                LinhaEventoSimplesNacional linhaEventoSimplesNacional = new LinhaEventoSimplesNacional();
                linhaEventoSimplesNacional.setNumeroLinha(numeroLinha.addAndGet(1));
                try {
                    preencherLinhaEventoSimplesNacional(linhaEventoSimplesNacional, linha);
                    toReturn.add(linhaEventoSimplesNacional);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    linhaEventoSimplesNacional.setErro(e.getMessage());
                    assistente.adicionarLogHtml("Linha " + linhaEventoSimplesNacional.getNumeroLinha() + " - " + e.getMessage());
                }
                assistente.conta();
            }
            assistente.adicionarLogHtml("Fim da Leitura do Arquivo");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

        return new AsyncResult<>(toReturn);
    }

    @Async
    @Transactional
    public Future<EventoSimplesNacional> processarArquivo(EventoSimplesNacional selecionado,
                                                          List<LinhaEventoSimplesNacional> linhas,
                                                          AssistenteBarraProgresso assistente) {
        selecionado = em.merge(selecionado);


        assistente.setTotal(linhas.size());

        assistente.adicionarLogHtml("Início do Processamento do Arquivo");

        for (LinhaEventoSimplesNacional linha : linhas) {
            try {
                linha.setEventoSimplesNacional(selecionado);
                atualizarCadastroEconomico(assistente, linha);
            } catch (Exception e) {
                assistente.adicionarLogHtml("Linha " + linha.getNumeroLinha() + " - " + e.getMessage());
            }
            assistente.conta();
        }
        assistente.adicionarLogHtml("Fim da Processamento do Arquivo");

        return new AsyncResult<>(selecionado);
    }

    public boolean hasLinhaRegistrada(List<LinhaEventoSimplesNacional> linhasEventoSimplesNacional, LinhaEventoSimplesNacional linha) {
        if (linhasEventoSimplesNacional != null && linhasEventoSimplesNacional.isEmpty()) {
            for (LinhaEventoSimplesNacional linhaEventoSimplesNacional : linhasEventoSimplesNacional) {
                if (linhaEventoSimplesNacional.getNumeroOpcao().equals(linha.getNumeroOpcao()) &&
                    linhaEventoSimplesNacional.getDataEfeito().equals(linha.getDataEfeito()) &&
                    linhaEventoSimplesNacional.getCodigoEvento().equals(linha.getCodigoEvento())) {
                    return true;
                }
            }
        }
        return false;
    }

    public LinhaEventoSimplesNacional ultimaLinha(List<LinhaEventoSimplesNacional> linhas) {
        Collections.sort(linhas, new Comparator<LinhaEventoSimplesNacional>() {
            @Override
            public int compare(LinhaEventoSimplesNacional o1, LinhaEventoSimplesNacional o2) {
                return o2.getDataEfeito().compareTo(o1.getDataEfeito());
            }
        });

        return linhas.get(0);
    }

    public void atualizarCadastroEconomico(AssistenteBarraProgresso assistenteBarraProgresso, LinhaEventoSimplesNacional linha) throws Exception {
        if (linha.getCadastroEconomico() == null) {
            assistenteBarraProgresso.adicionarLogHtml("Linha " + linha.getNumeroLinha() + " - CNPJ Base " + linha.getCnpj() + " não registrado para nenhum cadastro econômico");
            return;
        }

        if (linha.getTipoEventoSimplesNacional() == null) {
            assistenteBarraProgresso.adicionarLogHtml("Linha " + linha.getNumeroLinha() + " - Codigo de evento não encontrado " + linha.getCodigoEvento());
            return;
        }

        List<LinhaEventoSimplesNacional> linhasEventoSimplesNacional =
            linhaEventoSimplesNacionalFacade.buscarLinhasEventoSimplesNacionalPorEmpresa(linha.getCadastroEconomico());

        if (linhasEventoSimplesNacional != null && !linhasEventoSimplesNacional.isEmpty()) {
            if (hasLinhaRegistrada(linhasEventoSimplesNacional, linha)) {
                assistenteBarraProgresso.adicionarLogHtml("Linha " + linha.getNumeroLinha() + " - Evento já importado N° Opção (" +
                    linha.getNumeroOpcao() + ") Data de Efeito (" + Util.dateToString(linha.getDataEfeito()) + ") Código do Evento (" + linha.getCodigoEvento() + ")");
                return;
            }

            LinhaEventoSimplesNacional last = ultimaLinha(linhasEventoSimplesNacional);
            if (last.getDataEfeito().after(linha.getDataEfeito())) {
                em.merge(linha);
                return;
            }
        }

        EnquadramentoFiscal enquadramentoFiscal = linha.getCadastroEconomico().getEnquadramentoVigente();
        if (enquadramentoFiscal == null) {
            assistenteBarraProgresso.adicionarLogHtml("Linha " + linha.getNumeroLinha() + " - Enquadramento Fiscal não informado para o cadastro " +
                linha.getCadastroEconomico().getInscricaoCadastral());
            return;
        }
        if (linha.getTipoEventoSimplesNacional().isInclusao()) {
//            if (Boolean.TRUE.equals(linha.getTipoEventoSimplesNacional().getMei())) {
//                enquadramentoFiscal.setRegimeTributario(RegimeTributario.SIMPLES_NACIONAL);
//            } else {
//                if (!RegimeEspecialTributacao.MICROEMPRESARIO_INDIVIDUAL.equals(situacaoTributaria.getRegimeEspecialTributacao())) {
            enquadramentoFiscal.setRegimeTributario(RegimeTributario.SIMPLES_NACIONAL);
//                }

        } else {
            enquadramentoFiscal.setRegimeTributario(null);
        }
        em.merge(enquadramentoFiscal);
        em.merge(linha);
    }

    public LinhaEventoSimplesNacional preencherLinhaEventoSimplesNacional(LinhaEventoSimplesNacional linhaEventoSimplesNacional, String linha) throws Exception {
        linhaEventoSimplesNacional.setCnpj(linha.substring(0, 8));
        linhaEventoSimplesNacional.setNatureza(NaturezaEventoSimplesNacional.findByCodigo(new Integer(linha.substring(8, 9))));
        linhaEventoSimplesNacional.setCodigoEvento(getInteger(linha.substring(9, 12)));
        if (linhaEventoSimplesNacional.getCodigoEvento() != null) {
            TipoEventoSimplesNacional tipoEventoSimplesNacional = tipoEventoSimplesNacionalFacade.buscarTipoEventoSimplesNacionalPorCodigo(linhaEventoSimplesNacional.getCodigoEvento());
            linhaEventoSimplesNacional.setTipoEventoSimplesNacional(tipoEventoSimplesNacional);
        }
        linhaEventoSimplesNacional.setDataFatoMotivador(getDataFormadata(linha.substring(12, 20), "yyyyMMdd"));
        linhaEventoSimplesNacional.setDataEfeito(getDataFormadata(linha.substring(20, 28), "yyyyMMdd"));
        linhaEventoSimplesNacional.setNumeroProcessoJudicial(linha.substring(38, 78));
        linhaEventoSimplesNacional.setNumeroProcessoAdministrativo(linha.substring(78, 103));
        linhaEventoSimplesNacional.setObservacoes(linha.substring(103, 353));
        linhaEventoSimplesNacional.setCodigoUA(getInteger(linha.substring(353, 360)));
        linhaEventoSimplesNacional.setCodigoUF(linha.substring(360, 362));
        linhaEventoSimplesNacional.setCodigoMunicipio(getInteger(linha.substring(362, 366)));
        linhaEventoSimplesNacional.setDataOcorrencia(getDataFormadata(linha.substring(366, 375), "yyyyMMdd"));
        linhaEventoSimplesNacional.setHoraOcorrencia(getDataFormadata(linha.substring(375, 380), "hhmmss"));
        linhaEventoSimplesNacional.setNumeroOpcao(getInteger(linha.substring(380, 389)));
        linhaEventoSimplesNacional.setCadastroEconomico(cadastroEconomicoFacade.buscarCadastroEconomicoPorCnpjBase(linhaEventoSimplesNacional.getCnpj()));
        return linhaEventoSimplesNacional;
    }

    public Integer getInteger(String value) {
        if (value != null && !value.trim().isEmpty()) {
            return new Integer(value);
        }
        return null;
    }

    public Date getDataFormadata(String data, String pattern) {
        if (data != null && !data.trim().isEmpty()) {
            return DataUtil.getDateParse(data, pattern);
        }
        return null;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
