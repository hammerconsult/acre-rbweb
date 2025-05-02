/*
 * Codigo gerado automaticamente em Fri Feb 11 09:06:37 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.datajud.RegistroDatajud;
import br.com.webpublico.enums.SituacaoCertidaoDA;
import br.com.webpublico.enums.SituacaoJudicial;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoAlteracaoCertidaoDA;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.procuradoria.ProcuradoriaParametroFacade;
import br.com.webpublico.negocios.tributario.procuradoria.SituacaoTramiteJudicialFacade;
import br.com.webpublico.negocios.tributario.procuradoria.VaraFacade;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.ws.procuradoria.IntegraSoftplanFacade;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.core.util.Throwables;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Stateless
public class ProcessoJudicialFacade extends AbstractFacade<ProcessoJudicial> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private CertidaoDividaAtivaFacade certidaoDividaAtivaFacade;
    @EJB
    private SituacaoTramiteJudicialFacade situacaoTramiteJudicialFacade;
    @EJB
    private VaraFacade varaFacade;
    @EJB
    private GeraValorDividaDividaAtiva valorDividaFacade;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private IntegraSoftplanFacade integraSoftplanFacade;
    @EJB
    private ProcuradoriaParametroFacade procuradoriaParametroFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private AlterarSituacaoCDAFacade alterarSituacaoCDAFacade;
    @EJB
    private CancelamentoParcelamentoFacade cancelamentoParcelamentoFacade;
    @EJB
    private ComunicaSofPlanFacade comunicaSofPlanFacade;

    public SituacaoTramiteJudicialFacade getSituacaoTramiteJudicialFacade() {
        return situacaoTramiteJudicialFacade;
    }

    public ProcessoJudicialFacade() {
        super(ProcessoJudicial.class);
    }

    public IntegraSoftplanFacade getIntegraSoftplanFacade() {
        return integraSoftplanFacade;
    }

    public ProcuradoriaParametroFacade getProcuradoriaParametroFacade() {
        return procuradoriaParametroFacade;
    }

    public ComunicaSofPlanFacade getComunicaSofPlanFacade() {
        return comunicaSofPlanFacade;
    }

    public GeraValorDividaDividaAtiva getValorDividaFacade() {
        return valorDividaFacade;
    }

    public ConsultaDebitoFacade getConsultaDebitoFacade() {
        return consultaDebitoFacade;
    }

    public CertidaoDividaAtivaFacade getCertidaoDividaAtivaFacade() {
        return certidaoDividaAtivaFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public VaraFacade getVaraFacade() {
        return varaFacade;
    }

    public ProcessoJudicial salvarProcesso(ProcessoJudicial processoJudicial) {
        return em.merge(processoJudicial);
    }

    private ParcelaValorDivida salvarParcelaValorDivida(ParcelaValorDivida parcela) {
        return em.merge(parcela);
    }

    public ProcessoJudicialCDA salvarProcessoCDA(ProcessoJudicialCDA ProcessoJudicialCDA) {
        return em.merge(ProcessoJudicialCDA);
    }

    public void verificarSeCdaTemParcelas(ProcessoJudicial processoJudicial, ValidacaoException ve) {
        for (ProcessoJudicialCDA processo : processoJudicial.getProcessos()) {
            CertidaoDividaAtiva cda = certidaoDividaAtivaFacade.recuperar(processo.getCertidaoDividaAtiva().getId());
            List<Long> idsCalculos = Lists.newArrayList();
            for (ItemCertidaoDividaAtiva item : cda.getItensCertidaoDividaAtiva()) {
                idsCalculos.add(item.getItemInscricaoDividaAtiva().getId());
            }
            List<ResultadoParcela> resultados = new ConsultaParcela()
                .addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IN, idsCalculos)
                .executaConsulta().getResultados();
            if (resultados.isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível ajuizar a CDA " + cda.getNumeroCdaComExercicio() + ", ela não possui vinculo com parcelas que podem ser ajuizadas.");
            }
        }
    }

    public void atualizarSituacoes(ProcessoJudicial processoJudicial) {
        try {
            for (ProcessoJudicialCDA processo : processoJudicial.getProcessos()) {
                CertidaoDividaAtiva cda = certidaoDividaAtivaFacade.recuperar(processo.getCertidaoDividaAtiva().getId());
                AlteracaoSituacaoCDA alteracaoCDA = new AlteracaoSituacaoCDA(TipoAlteracaoCertidaoDA.AJUIZAR,
                    processoJudicial.getDataAjuizamento(), sistemaFacade.getUsuarioCorrente(),
                    null, null, null, "Processo judicial número " + processoJudicial.getNumero(), cda);
                cda.setSituacaoJudicial(SituacaoJudicial.AJUIZADA);
                certidaoDividaAtivaFacade.salvaCertidao(cda);
                alterarSituacaoCDAFacade.salvar(alteracaoCDA);

                for (ItemCertidaoDividaAtiva item : cda.getItensCertidaoDividaAtiva()) {
                    List<ResultadoParcela> resultados = new ConsultaParcela()
                        .addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, item.getItemInscricaoDividaAtiva().getId())
                        .executaConsulta().getResultados();
                    for (ResultadoParcela resultado : resultados) {
                        ParcelaValorDivida pvd = consultaDebitoFacade.recuperaParcela(resultado.getId());
                        pvd.setDividaAtivaAjuizada(true);
                        salvarParcelaValorDivida(pvd);
                        if (SituacaoParcela.PAGO_PARCELAMENTO.equals(resultado.getSituacaoEnumValue())) {
                            Long idParcela = cancelamentoParcelamentoFacade.buscarIdParcelaDoCancelamentoDaParcelaOriginal(resultado.getIdParcela());
                            if (idParcela != null) {
                                ParcelaValorDivida parcelaCancelamento = consultaDebitoFacade.recuperaParcela(resultado.getId());
                                parcelaCancelamento.setDividaAtivaAjuizada(true);
                                salvarParcelaValorDivida(parcelaCancelamento);
                            }
                        }
                    }
                }
            }
            processoJudicial.setSituacao(ProcessoJudicial.Situacao.ATIVO);
        } catch (Exception e) {
            Throwables.rethrow(e);
        }
    }

    @Override
    public ProcessoJudicial recuperar(Object id) {
        ProcessoJudicial processo = em.find(ProcessoJudicial.class, id);
        Hibernate.initialize(processo.getProcessos());
        return processo;
    }

    public ProcessoJudicialExtincao recuperarExtincao(Object id) {
        ProcessoJudicialExtincao extincao = em.find(ProcessoJudicialExtincao.class, id);
        return extincao;
    }

    public ProcessoJudicialCDA recuperarProcessoJudicialCDA(Object id) {
        ProcessoJudicialCDA processo = em.find(ProcessoJudicialCDA.class, id);
        processo.getProcessoJudicial().getListaTramites().size();
        processo.getProcessoJudicial().getEnvolvidos().size();
        processo.getProcessoJudicial().getProcessos().size();
        if (processo.getProcessoJudicial().getDetentorArquivoComposicao() != null) {
            processo.getProcessoJudicial().getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        return processo;
    }

    public void voltarSituacaoCDAParaAberto(CertidaoDividaAtiva certidaoDividaAtiva) {
        if (SituacaoJudicial.AJUIZADA.equals(certidaoDividaAtiva.getSituacaoJudicial())) {
            certidaoDividaAtiva.setSituacaoCertidaoDA(SituacaoCertidaoDA.ABERTA);
            getCertidaoDividaAtivaFacade().salvar(certidaoDividaAtiva);
        }
    }

    public void voltarSituacaoCDAParaAjuizada(CertidaoDividaAtiva certidaoDividaAtiva) {
        if (SituacaoCertidaoDA.ABERTA.equals(certidaoDividaAtiva.getSituacaoCertidaoDA())) {
            certidaoDividaAtiva.setSituacaoJudicial(SituacaoJudicial.AJUIZADA);
            getCertidaoDividaAtivaFacade().salvar(certidaoDividaAtiva);
        }
    }

    public List<ProcessoJudicial> listarFiltrandoProcessoJudicialAtivo(String parte) {
        String hql = "from ProcessoJudicial " +
            " where numeroProcessoForum like :parte" +
            " and situacao = :situacao";
        Query q = em.createQuery(hql);
        q.setParameter("parte", "%" + parte.trim() + "%");
        q.setParameter("situacao", ProcessoJudicial.Situacao.ATIVO);
        return q.getResultList();
    }

    public List<ProcessoJudicial> recuperarProcessosPorNumeroForum(String numeroProcessoForum, ProcessoJudicial.Situacao situacao) {
        String hql = "from ProcessoJudicial " +
            " where replace(replace(replace(numeroProcessoForum,'.',''),'-',''),'/','') like :numeroProcessoForum" +
            " and situacao = :situacao";
        Query q = em.createQuery(hql);
        q.setParameter("numeroProcessoForum", "%" + StringUtil.removeZerosEsquerda(StringUtil.retornaApenasNumeros(numeroProcessoForum.trim())) + "%");
        q.setParameter("situacao", situacao);
        List<ProcessoJudicial> retorno = q.getResultList();
        for (ProcessoJudicial processo : retorno) {
            Hibernate.initialize(processo.getProcessos());
        }
        return retorno;
    }

    public ProcessoJudicial recuperarProcessoPorNumeroForum(String numeroProcessoForum, ProcessoJudicial.Situacao situacao) {
        String sql = "select id from ProcessoJudicial " +
            " where replace(replace(replace(numeroProcessoForum,'.',''),'-',''),'/','') = " +
            " replace(replace(replace(:numeroProcessoForum,'.',''),'-',''),'/','') " +
            " and situacao = :situacao";
        Query q = em.createNativeQuery(sql);
        q.setParameter("numeroProcessoForum", numeroProcessoForum.trim());
        q.setParameter("situacao", situacao.name());
        List<BigDecimal> result = q.getResultList();
        return !result.isEmpty() ? recuperar(result.get(0).longValue()) : null;
    }

    private List<SituacaoParcela> recuperarSituacoesDiferentesDeAbertoParcelasDoProcessoJudicial(String numeroProcessoForum) {
        String sql = "select distinct sit.situacaoparcela from parcelavalordivida pvd " +
            "inner join situacaoparcelavalordivida sit on sit.id = pvd.situacaoatual_id " +
            "inner join valordivida vd on vd.id = pvd.valordivida_id " +
            "inner join iteminscricaodividaativa itemIns on itemins.id = vd.calculo_id " +
            "inner join itemcertidaodividaativa itemcda on itemcda.iteminscricaodividaativa_id = itemins.id " +
            "inner join processojudicialcda procda on procda.certidaodividaativa_id = itemcda.certidao_id " +
            "inner join processojudicial pro on pro.id = procda.processojudicial_id " +
            "where pro.numeroprocessoforum = :numeroProcessoForum " +
            "  and sit.situacaoparcela <> :emAberto";
        Query q = em.createNativeQuery(sql);
        q.setParameter("numeroProcessoForum", numeroProcessoForum);
        q.setParameter("emAberto", SituacaoParcela.EM_ABERTO.name());
        List<String> retorno = q.getResultList();
        List<SituacaoParcela> situacoes = Lists.newArrayList();
        for (String situacao : retorno) {
            situacoes.add(SituacaoParcela.valueOf(situacao));
        }
        return situacoes;
    }


    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public void inativarProcessoJudicial(ProcessoJudicialExtincao processoJudicialExtincao) {
        List<CertidaoDividaAtiva> certidoesDividaAtiva = certidaoDividaAtivaFacade
            .buscarCertidoesComDebitosDiferentesAberto(processoJudicialExtincao.getProcessoJudicial());
        if (certidoesDividaAtiva != null && !certidoesDividaAtiva.isEmpty()) {
            String certidoes = StringUtils.join(certidoesDividaAtiva, ", ");
            throw new ExcecaoNegocioGenerica("As seguintes certidões: <strong>" + certidoes + "</strong>, " +
                "possuem débitos que não estão em aberto. Para realizar a extição do processo judicial, " +
                "essas certidões deverão ser removidas do processo judicial.");
        } else {
            List<ProcessoJudicial> processos = recuperarProcessosPorNumeroForum(processoJudicialExtincao.getProcessoJudicial().getNumeroProcessoForum(), ProcessoJudicial.Situacao.ATIVO);
            if (!processos.isEmpty()) {
                List<ResultadoParcela> parcelas = Lists.newArrayList();
                for (ProcessoJudicial processo : processos) {
                    for (ProcessoJudicialCDA processoCda : processo.getProcessos()) {
                        parcelas.addAll(getCertidaoDividaAtivaFacade().recuperaParcelasDaCertidaoDaView(processoCda.getCertidaoDividaAtiva()));
                    }
                }

                for (ResultadoParcela parcela : parcelas) {
                    ParcelaValorDivida pvd = getConsultaDebitoFacade().recuperaParcela(parcela.getIdParcela());
                    if (pvd.getDividaAtivaAjuizada()) {
                        pvd.setDividaAtivaAjuizada(false);
                        getValorDividaFacade().salvarParcela(pvd);
                    }
                }

                for (ProcessoJudicial processo : processos) {
                    for (ProcessoJudicialCDA proCda : processo.getProcessos()) {
                        voltarSituacaoCDAParaAberto(proCda.getCertidaoDividaAtiva());
                    }
                    processo.setSituacao(ProcessoJudicial.Situacao.INATIVO);
                    salvarProcesso(processo);
                }
            } else {
                throw new ExcecaoNegocioGenerica("Processo não localizado!");
            }
        }
    }

    private String montarStringSituacoes(List<SituacaoParcela> situacoes) {
        String strSituacoes = "";
        for (SituacaoParcela sit : situacoes) {
            strSituacoes += sit.getDescricao() + ", ";
        }
        return strSituacoes.substring(0, (strSituacoes.length() - 2));
    }


    public ProcessoJudicialExtincao buscarExtincaoPorNumero(String numeroProcessoForum) {
        Query q = em.createQuery("from ProcessoJudicialExtincao where processoJudicial.numeroProcessoForum = :numeroProcesso");
        q.setParameter("numeroProcesso", numeroProcessoForum);
        List<ProcessoJudicialExtincao> retorno = q.getResultList();
        if (!retorno.isEmpty()) {
            return retorno.get(0);
        }
        return null;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public void estornarExtincaoProcessoJudicial(ProcessoJudicialExtincao processoJudicialExtincao) {
        try {
            List<SituacaoParcela> situacoes = recuperarSituacoesDiferentesDeAbertoParcelasDoProcessoJudicial(processoJudicialExtincao.getProcessoJudicial().getNumeroProcessoForum());
            if (situacoes.isEmpty()) {
                List<ProcessoJudicial> processos = recuperarProcessosPorNumeroForum(processoJudicialExtincao.getProcessoJudicial().getNumeroProcessoForum(), ProcessoJudicial.Situacao.INATIVO);
                if (!processos.isEmpty()) {
                    List<ResultadoParcela> parcelas = Lists.newArrayList();
                    for (ProcessoJudicial processo : processos) {
                        for (ProcessoJudicialCDA processoCda : processo.getProcessos()) {
                            parcelas.addAll(getCertidaoDividaAtivaFacade().recuperaParcelasDaCertidaoDaView(processoCda.getCertidaoDividaAtiva()));
                            voltarSituacaoCDAParaAjuizada(processoCda.getCertidaoDividaAtiva());
                        }

                        for (ResultadoParcela parcela : parcelas) {
                            if (!SituacaoParcela.EM_ABERTO.equals(parcela.getSituacaoEnumValue()) && !situacoes.contains(parcela.getSituacaoEnumValue())) {
                                situacoes.add(SituacaoParcela.fromDto(parcela.getSituacaoEnumValue()));
                            }
                        }

                        processo.setSituacao(ProcessoJudicial.Situacao.ATIVO);
                        salvarProcesso(processo);
                    }

                    for (ResultadoParcela parcela : parcelas) {
                        ParcelaValorDivida pvd = getConsultaDebitoFacade().recuperaParcela(parcela.getIdParcela());
                        if (pvd != null && !pvd.getDividaAtivaAjuizada()) {
                            pvd.setDividaAtivaAjuizada(true);
                            getValorDividaFacade().salvarParcela(pvd);
                        }
                    }
                } else {
                    throw new ExcecaoNegocioGenerica("Processo não localizado!");
                }
            } else {
                String strSituacoes = montarStringSituacoes(situacoes);
                if (situacoes.size() > 1) {
                    throw new ExcecaoNegocioGenerica("Existem parcelas com situações: " + strSituacoes + "!");
                } else {
                    throw new ExcecaoNegocioGenerica("Existem parcelas com situação: " + strSituacoes + "!");
                }
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public void removerProcessoCDA(ProcessoJudicialCDA processoJudicialCDA) {
        processoJudicialCDA = em.find(ProcessoJudicialCDA.class, processoJudicialCDA.getId());
        em.remove(processoJudicialCDA);
    }

    public List<RegistroDatajud> buscarDadosDatajud(String numeroProcesso) throws IOException {
        ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
        if (Strings.isNullOrEmpty(configuracaoTributario.getEndpointDatajus())) {
            throw new ExcecaoNegocioGenerica("O Endpoint do datajus não está definido nas configurações do tributário.");
        }
        if (Strings.isNullOrEmpty(configuracaoTributario.getEndpointDatajus())) {
            throw new ExcecaoNegocioGenerica("A API Key do datajus não está definido nas configurações do tributário.");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", configuracaoTributario.getApiKeyDatajus());
        String jsonFiltro = "{\n" +
            "    \"query\": {\n" +
            "        \"match\": {\n" +
            "            \"numeroProcesso\": \"" + StringUtil.retornaApenasNumeros(numeroProcesso) + "\"\n" +
            "        }\n" +
            "    }\n" +
            "}";
        HttpEntity<String> request = new HttpEntity<>(jsonFiltro, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> exchange = restTemplate.exchange(configuracaoTributario.getEndpointDatajus(),
            HttpMethod.POST, request, String.class);
        return converterDadosDatajud(exchange.getBody());
    }

    private List<RegistroDatajud> converterDadosDatajud(String dados) throws IOException {
        List<RegistroDatajud> registrosDatajud = Lists.newArrayList();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JsonNode jsonNode = objectMapper.readTree(dados);
        if (jsonNode.get("hits") != null && jsonNode.get("hits").get("hits") != null) {
            JsonNode jsonNodeHits = jsonNode.get("hits").get("hits");
            Iterator<JsonNode> elements = jsonNodeHits.elements();
            while (elements.hasNext()) {
                JsonNode source = elements.next().get("_source");
                registrosDatajud.add(objectMapper.readValue(source.toString(), RegistroDatajud.class));
            }
        }
        return registrosDatajud;
    }

    public List<ProcessoJudicialCDA> buscarCDAsPorNumeroProcessoForum(String numeroProcessoForum) {
        Query q = em.createQuery("from ProcessoJudicialCDA pjc " +
            " where replace(replace(replace(pjc.processoJudicial.numeroProcessoForum,'.',''),'-',''),'/','') = " +
            " replace(replace(replace(:numeroProcessoForum,'.',''),'-',''),'/','') " +
            " and pjc.processoJudicial.situacao = :situacao");
        q.setParameter("numeroProcessoForum", numeroProcessoForum.trim());
        q.setParameter("situacao", ProcessoJudicial.Situacao.ATIVO);
        return q.getResultList();
    }
}
