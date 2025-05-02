package br.com.webpublico.negocios;

import br.com.webpublico.controle.AbstractReducaoValorBemControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.BensMoveisBloqueio;
import br.com.webpublico.entidadesauxiliares.FiltroPesquisaBem;
import br.com.webpublico.entidadesauxiliares.VOReducaoValorBem;
import br.com.webpublico.entidadesauxiliares.VoReducaoValorBemContabil;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.administrativo.dao.JdbcReducaoValorBemDAO;
import br.com.webpublico.negocios.administrativo.reducaovalorbem.ProcessamentoReducaoValorBem;
import br.com.webpublico.negocios.comum.dao.JdbcDetentorArquivoComposicaoDAO;
import br.com.webpublico.negocios.tributario.singletons.SingletonConcorrenciaPatrimonio;
import br.com.webpublico.singletons.SingletonGeradorCodigoReducaoValorBem;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.springframework.web.context.ContextLoader;

import javax.annotation.PostConstruct;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 07/10/14
 * Time: 17:07
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class ReducaoValorBemFacade extends AbstractFacade<LoteReducaoValorBem> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private IntegradorPatrimonialContabilFacade integradorPatrimonialContabilFacade;
    @EJB
    private SingletonGeradorCodigoReducaoValorBem singletonGeradorCodigoReducaoValorBem;
    @EJB
    private ConfigMovimentacaoBemFacade configMovimentacaoBemFacade;
    @EJB
    private SingletonConcorrenciaPatrimonio singletonBloqueioPatrimonio;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private FaseFacade faseFacade;
    private JdbcReducaoValorBemDAO jdbcReducaoValorBemDAO;
    private JdbcDetentorArquivoComposicaoDAO jdbcDetentorArquivoComposicaoDAO;

    @PostConstruct
    public void init() {
        jdbcReducaoValorBemDAO = (JdbcReducaoValorBemDAO) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcReducaoValorBemDAO");
        jdbcDetentorArquivoComposicaoDAO = (JdbcDetentorArquivoComposicaoDAO) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcDetentorArquivoComposicaoDAO");
    }

    public ReducaoValorBemFacade() {
        super(LoteReducaoValorBem.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public LoteReducaoValorBem recuperar(Object id) {
        LoteReducaoValorBem lote = super.recuperar(id);
        Hibernate.initialize(lote.getLogReducaoOuEstorno().getMensagens());
        Hibernate.initialize(lote.getBensNaoAplicaveis());
        Hibernate.initialize(lote.getBensResidual());
        if (lote.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(lote.getDetentorArquivoComposicao().getArquivosComposicao());
        }
        if (lote.getUnidadeOrcamentaria() != null) {
            lote.setHierarquiaOrcamentaria(hierarquiaOrganizacionalFacade.retornarHierarquiaOrcamentariaPelaUnidadeOrcamentaria(lote.getUnidadeOrcamentaria(), lote.getDataLancamento()));
        }
        return lote;
    }

    private boolean hasReducaoBemDataLancamento(Bem b, Date dataLancamento) {
        String sql = "select  1  " +
            " from ReducaoValorBem reducao  " +
            "   inner join eventobem ev on ev.id = reducao.id   " +
            " where ev.bem_id = :bem" +
            "   and extract(month from ev.datalancamento) = extract(month from :data_lancamento) " +
            "   and extract(year from ev.datalancamento) = extract(year from :data_lancamento) " +
            "   and not exists(select 1 from EstornoReducaoValorBem estorno where estorno.reducaoValorBem_id = reducao.id) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("bem", b.getId());
        q.setParameter("data_lancamento", dataLancamento, TemporalType.DATE);
        return !q.getResultList().isEmpty();
    }

    public List<Bem> buscarBensVinculadosReducaoValorBem(LoteReducaoValorBem entity) {
        String sql = " " +
            " select " +
            "  bem.id as idbem," +
            "  coalesce(resultante.valororiginal, 0)               as valororiginal, " +
            "  coalesce(resultante.valoracumuladodaamortizacao, 0) as valoracumuladodaamortizacao, " +
            "  coalesce(resultante.valoracumuladodadepreciacao, 0) as valoracumuladodadepreciacao, " +
            "  coalesce(resultante.valoracumuladodaexaustao, 0)    as valoracumuladodaexaustao, " +
            "  coalesce(resultante.valoracumuladodeajuste, 0)      as valoracumuladodeajuste, " +
            "  resultante.id as idEstadoResultante, " +
            "  (select tr.id " +
            "    from tiporeducao tr " +
            "    where tr.grupobem_id = gb.id " +
            "    and :data_operacao between tr.iniciovigencia and coalesce(tr.fimvigencia, :data_operacao) " +
            "    and rownum = 1 " +
            "  ) tipo_reducao " +
            " from  eventobem eb " +
            "   inner join reducaovalorbem item on item.id = eb.id " +
            "   inner join bem on bem.id = eb.bem_id " +
            "   inner join estadobem resultante on resultante.id = eb.estadoresultante_id " +
            "   inner join grupobem gb on gb.id = resultante.grupobem_id " +
            " where item.lotereducaovalorbem_id = :idLote " +
            "   and gb.tipoBem = :tipo_bem " +
            "   and eb.dataoperacao = (select max(ev.dataoperacao) from eventobem ev where ev.bem_id = eb.bem_id) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("data_operacao", entity.getDataLancamento());
        q.setParameter("tipo_bem", entity.getTipoBem().name());
        q.setParameter("idLote", entity.getId());
        return preencherBensApartirArray(q);
    }

    public List<Bem> buscarBensReducaoValorBem(LoteReducaoValorBem entity, GrupoBem grupoBem) {
        String sql = " " +
            " select " +
            " distinct bem.* " +
            " from  eventobem eb " +
            "   inner join reducaovalorbem item on item.id = eb.id " +
            "   inner join bem on bem.id = eb.bem_id " +
            "   inner join estadobem resultante on resultante.id = eb.estadoresultante_id " +
            "   inner join grupobem gb on gb.id = resultante.grupobem_id " +
            " where item.lotereducaovalorbem_id = :idLote " +
            "   and gb.tipoBem = :tipo_bem ";
        sql += grupoBem !=null ? "and gb.id = :idGrupo " : "";
        Query q = em.createNativeQuery(sql, Bem.class);
        q.setParameter("tipo_bem", entity.getTipoBem().name());
        q.setParameter("idLote", entity.getId());
        if (grupoBem !=null) {
            q.setParameter("idGrupo", grupoBem.getId());
        }
        return q.getResultList();
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 5)
    @Asynchronous
    public Future<List<Bem>> pesquisarBens(AssistenteMovimentacaoBens assistente, LoteReducaoValorBem entity) {
        assistente.setDescricaoProcesso("Buscando bens da unidade orçamentária " + entity.getHierarquiaOrcamentaria() + ". ");
        List<Bem> bens;
        if (assistente.isOperacaoNovo()) {
            bens = buscarBensParaReducaoValorBem(assistente, entity);
        } else {
            bens = buscarBensVinculadosReducaoValorBem(entity);
        }
        return new AsyncResult<>(bens);
    }

    public List<Bem> buscarBensParaReducaoValorBem(AssistenteMovimentacaoBens assistente, LoteReducaoValorBem entity) {
        String sql = " " +
            " select " +
            "  bem.id as idbem, " +
            "  coalesce(resultante.valororiginal, 0)               as valororiginal, " +
            "  coalesce(resultante.valoracumuladodaamortizacao, 0) as valoracumuladodaamortizacao, " +
            "  coalesce(resultante.valoracumuladodadepreciacao, 0) as valoracumuladodadepreciacao, " +
            "  coalesce(resultante.valoracumuladodaexaustao, 0)    as valoracumuladodaexaustao, " +
            "  coalesce(resultante.valoracumuladodeajuste, 0)      as valoracumuladodeajuste, " +
            "  resultante.id as idEstadoResultante, " +
            "  (select tr.id " +
            "    from tiporeducao tr " +
            "    where tr.grupobem_id = gb.id " +
            "    and to_date(:data_operacao, 'dd/MM/yyyy') between trunc(tr.iniciovigencia) and coalesce(trunc(tr.fimvigencia), to_date(:data_operacao, 'dd/MM/yyyy')) " +
            "    and rownum = 1 " +
            "  ) tipo_reducao " +
            "from eventobem eb " +
            "  inner join bem on bem.id = eb.bem_id " +
            "  inner join movimentobloqueiobem mov on mov.bem_id = bem.id " +
            "  inner join estadobem resultante on resultante.id = eb.estadoresultante_id " +
            "  inner join grupobem gb on gb.id = resultante.grupobem_id " +
            "  inner join vwhierarquiaorcamentaria vworc on vworc.subordinada_id = resultante.detentoraorcamentaria_id " +
            " where eb.situacaoeventobem not in " + Util.montarClausulaIn(Arrays.asList(SituacaoEventoBem.situacoesQueNaoPermitemReducaoDoValorBem)) +
            "   and vworc.subordinada_id = :uni_orc_id " +
            "   and resultante.estadobem <> :estado_conservacao_bem " +
            "   and gb.tipoBem = :tipo_bem " +
            "   and to_date(:data_operacao, 'dd/MM/yyyy') between trunc(vworc.iniciovigencia) and coalesce(trunc(vworc.fimvigencia), to_date(:data_operacao, 'dd/MM/yyyy'))  " +
            "   and eb.dataoperacao = (select max(ev.dataoperacao) from eventobem ev where ev.bem_id = eb.bem_id) " +
            "   and mov.datamovimento = (select max(mov2.datamovimento) from movimentobloqueiobem mov2 where  bem.id = mov2.bem_id) ";
        sql += FiltroPesquisaBem.getCondicaoBloqueioPesquisaBem(assistente.getConfigMovimentacaoBem());
        sql += "  order by to_number(bem.identificacao) ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("data_operacao", DataUtil.getDataFormatada(entity.getDataLancamento()));
        q.setParameter("uni_orc_id", entity.getUnidadeOrcamentaria().getId());
        q.setParameter("estado_conservacao_bem", EstadoConservacaoBem.BAIXADO.name());
        q.setParameter("tipo_bem", entity.getTipoBem().name());
        FiltroPesquisaBem.adicionarParametrosConfigMovimentacaoBem(q, assistente.getConfigMovimentacaoBem());

        List<Bem> bensSelecionados = preencherBensApartirArray(q);
        assistente.setBensSalvos(bensSelecionados);
        verificarBensBloqueado(entity, assistente);
        return bensSelecionados;
    }

    private List<Bem> preencherBensApartirArray(Query q) {
        List<Bem> bens = Lists.newArrayList();
        for (Object[] bem : (List<Object[]>) q.getResultList()) {
            Bem b = bemFacade.recuperarSemDependencias(((BigDecimal) bem[0]).longValue());
            b.setValorOriginal((BigDecimal) bem[1]);
            b.setValorAcumuladoDaAmortizacao((BigDecimal) bem[2]);
            b.setValorAcumuladoDaDepreciacao((BigDecimal) bem[3]);
            b.setValorAcumuladoDaExaustao((BigDecimal) bem[4]);
            b.setValorAcumuladoDeAjuste((BigDecimal) bem[5]);
            b.setIdUltimoEstado(((BigDecimal) bem[6]).longValue());
            b.setUltimoEstado(em.find(EstadoBem.class, b.getIdUltimoEstado()));
            if (bem[7] != null) {
                b.setTipoReducao(em.find(TipoReducao.class, ((BigDecimal) bem[7]).longValue()));
            }
            bens.add(b);
        }
        return bens;
    }

    private void verificarBensBloqueado(LoteReducaoValorBem loteReducaoValorBem, AssistenteMovimentacaoBens assistente) {
        FiltroPesquisaBem filtro = new FiltroPesquisaBem(loteReducaoValorBem.getTipoBem(), loteReducaoValorBem);
        filtro.setDepreciacao(true);
        HierarquiaOrganizacional hierarquiaDaUnidade = getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), loteReducaoValorBem.getUnidadeOrcamentaria(), loteReducaoValorBem.getData());
        filtro.setHierarquiaOrcamentaria(hierarquiaDaUnidade);

        List<BensMoveisBloqueio> bensEmBloqueio = configMovimentacaoBemFacade.buscarBensEmBloqueioMovimentacao(filtro);

        for (BensMoveisBloqueio bemBloqueado : bensEmBloqueio) {
            Bem bem = bemFacade.recuperarBemPorIdBem(bemBloqueado.getIdBem());
            EstadoBem ultimoEstadoBem = bemFacade.recuperarUltimoEstadoDoBem(bem);
            if (assistente.getBensSalvos() != null && !assistente.getBensSalvos().isEmpty()) {
                if (!assistente.getBensSalvos().contains(bem)) {
                    MsgLogReducaoOuEstorno msgLogReducaoOuEstorno = criarMensagemLogReducaoOuEstorno(
                        loteReducaoValorBem.getLogReducaoOuEstorno(),
                        bem, ultimoEstadoBem.getValorOriginal(), bemBloqueado.toString());
                    loteReducaoValorBem.getLogReducaoOuEstorno().getMensagens().add(msgLogReducaoOuEstorno);

                }
            } else {
                MsgLogReducaoOuEstorno msgLogReducaoOuEstorno = criarMensagemLogReducaoOuEstorno(
                    loteReducaoValorBem.getLogReducaoOuEstorno(),
                    bem, ultimoEstadoBem.getValorOriginal(), bemBloqueado.toString());
                loteReducaoValorBem.getLogReducaoOuEstorno().getMensagens().add(msgLogReducaoOuEstorno);
            }
        }
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 5)
    @Asynchronous
    public Future<ProcessamentoReducaoValorBem> simularContabilizacaoReducaoValorBem(AssistenteMovimentacaoBens assistente, LoteReducaoValorBem loteReducaoValorBem, List<Bem> bens) {
        ProcessamentoReducaoValorBem processamentoReducaoValorBem = new ProcessamentoReducaoValorBem();

        for (Bem bem : bens) {
            try {
                if (bem.getTipoReducao() == null
                    || bem.getTipoReducao().getTipoReducaoValorBem().equals(TipoReducaoValorBem.NAO_APLICAVEL)
                    || bem.getTipoReducao().getTipoReducaoValorBem().equals(loteReducaoValorBem.getTipoReducao())) {

                    if (isPossivelReduzirValorBem(processamentoReducaoValorBem, loteReducaoValorBem, bem, assistente)) {
                        ReducaoValorBem reducaoValorBem = criarReducaoValorBem(loteReducaoValorBem, bem);
                        integradorPatrimonialContabilFacade.reduzirValorDoBem(
                            loteReducaoValorBem.getReducoesValorBemContabil(),
                            reducaoValorBem,
                            loteReducaoValorBem.getTipoReducao(),
                            loteReducaoValorBem.getTipoBem(), "", true);
                        processamentoReducaoValorBem.getReducoes().add(reducaoValorBem);
                        processamentoReducaoValorBem.atribuirBemPorTipoReducao(loteReducaoValorBem.getTipoReducao(), bem);
                    }
                } else {
                    processamentoReducaoValorBem.atribuirBemPorTipoReducao(bem.getTipoReducao().getTipoReducaoValorBem(), bem);
                }
            } catch (ExcecaoNegocioGenerica ex) {
                processamentoReducaoValorBem.getMsgs().add(criarMensagemLogReducaoOuEstorno(loteReducaoValorBem.getLogReducaoOuEstorno(),
                    bem, bem.getUltimoEstado().getValorOriginal(), ex.getMessage()));

            } catch (Exception e) {
                processamentoReducaoValorBem.getMsgs().add(criarMensagemLogReducaoOuEstorno(loteReducaoValorBem.getLogReducaoOuEstorno(),
                    bem, bem.getUltimoEstado().getValorOriginal(), "Ocorreu um erro inesperado ao simular a redução valor bem " + e.getMessage()));
            }
            assistente.conta();
        }
        return new AsyncResult<>(processamentoReducaoValorBem);
    }

    private ReducaoValorBem criarReducaoValorBem(LoteReducaoValorBem lote, Bem bem) {
        EstadoBem resultante = bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(bem.getUltimoEstado());
        BigDecimal valorDaReducao = bem.calcularReducaoValorBem(resultante, bem.getTipoReducao());

        ReducaoValorBem reducao = new ReducaoValorBem(lote.getTipoReducao().getTipoEventoBem());
        reducao.setLoteReducaoValorBem(lote);
        Date dataLancamento = DataUtil.getDataComHoraAtual(lote.getDataLancamento());
        reducao.setDataLancamento(dataLancamento);
        reducao.setEstadoInicial(bem.getUltimoEstado());
        reducao.setEstadoResultante(resultante);
        reducao.setValorDoLancamento(valorDaReducao);
        reducao.setBem(bem);
        reducao.setSituacaoEventoBem(SituacaoEventoBem.AGUARDANDO_EFETIVACAO);
        return reducao;
    }

    private boolean isPossivelReduzirValorBem(ProcessamentoReducaoValorBem processamentoReducaoValorBem,
                                              LoteReducaoValorBem selecionado, Bem bem,
                                              AssistenteMovimentacaoBens assistente) {
        boolean isPossivelDepreciar = true;
        if (selecionado.isEmElaboracao() && assistente.isOperacaoNovo()) {
            if (!validarLancamentoMesAno(processamentoReducaoValorBem, selecionado, bem)) {
                isPossivelDepreciar = false;
            }
            if (!validarDataAquisicao(processamentoReducaoValorBem, selecionado, bem)) {
                isPossivelDepreciar = false;
            }
            if (!validarTipoReducao(processamentoReducaoValorBem, selecionado, bem)) {
                isPossivelDepreciar = false;
            }
            if (!validarMovimentoRetroativoReducaoNormal(processamentoReducaoValorBem, selecionado, bem, assistente.getConfigMovimentacaoBem())) {
                isPossivelDepreciar = false;
            }
        }
        return isPossivelDepreciar;
    }

    private boolean validarTipoReducao(ProcessamentoReducaoValorBem assistente, LoteReducaoValorBem selecionado, Bem bem) {
        if (bem.getTipoReducao() == null) {
            assistente.getMsgs().add(criarMensagemLogReducaoOuEstorno(selecionado.getLogReducaoOuEstorno(),
                bem, bem.getUltimoEstado().getValorOriginal(), "O grupo bem " + bem.getUltimoEstado().getGrupoBem() + " não possui configuração de tipo de redução."));
            return false;
        }
        if (TipoReducaoValorBem.NAO_APLICAVEL.equals(bem.getTipoReducao().getTipoReducaoValorBem())) {
            assistente.getNaoAplicaeis().add(criarReducaoValorBemNaoAplicavel(selecionado, bem.getUltimoEstado().getGrupoBem(), bem,
                bem.getUltimoEstado().getValorOriginal()));
            return false;
        } else {
            BigDecimal valorMinimoRedutivel =  bem.getValorMinimoRedutivel(bem.getTipoReducao());
            if (bem.getUltimoEstado().getValorLiquido().compareTo(valorMinimoRedutivel) <= 0) {
                assistente.getBensResidual().add(new ReducaoValorBemResidual(selecionado, bem.getUltimoEstado().getGrupoBem(),
                    bem, bem.getUltimoEstado().getValorOriginal(), bem.getUltimoEstado().getValorLiquido()));
                return false;
            }
        }
        return true;
    }

    private boolean validarDataAquisicao(ProcessamentoReducaoValorBem processamentoReducaoValorBem, LoteReducaoValorBem selecionado, Bem bem) {
        if (bem.getDataAquisicao() != null && bem.getDataAquisicao().compareTo(selecionado.getDataLancamento()) > 0) {
            processamentoReducaoValorBem.getMsgs().add(criarMensagemLogReducaoOuEstorno(selecionado.getLogReducaoOuEstorno(),
                bem, bem.getUltimoEstado().getValorOriginal(), "Data de aquisição posterior a data de lançamento da " + selecionado.getTipoReducao().getDescricao() + "."));
            return false;
        }
        if (bem.getDataAquisicao() != null &&
            Util.getMesDaData(bem.getDataAquisicao()).equals(Util.getMesDaData(selecionado.getDataLancamento())) &&
            Util.getAnoDaData(bem.getDataAquisicao()).equals(Util.getAnoDaData(selecionado.getDataLancamento()))) {
            processamentoReducaoValorBem.getMsgs().add(criarMensagemLogReducaoOuEstorno(selecionado.getLogReducaoOuEstorno(),
                bem, bem.getUltimoEstado().getValorOriginal(), "O bem possui o mês da data de aquisição igual ao mês da data da " + selecionado.getTipoReducao().getDescricao() + "."));
            return false;
        }
        return true;
    }

    private boolean validarLancamentoMesAno(ProcessamentoReducaoValorBem processamentoReducaoValorBem, LoteReducaoValorBem selecionado, Bem bem) {
        if (hasReducaoBemDataLancamento(bem, selecionado.getDataLancamento())) {
            processamentoReducaoValorBem.getMsgs().add(criarMensagemLogReducaoOuEstorno(selecionado.getLogReducaoOuEstorno(),
                bem, bem.getUltimoEstado().getValorOriginal(), "Processo já realizado para o mês/ano selecionado."));
            return false;
        }
        return true;
    }

    private boolean validarUltimoEventoBemParaEstornarDepreciacao(LoteEstornoReducaoValorBem selecionado, Bem bem, AssistenteMovimentacaoBens assistente) {
        TipoReducaoValorBem tipoReducao = selecionado.getLoteReducaoValorBem().getTipoReducao();
        if (assistente.getConfigMovimentacaoBem().getValidarMovimentoRetroativo()) {
            String mensagem = configMovimentacaoBemFacade.validarMovimentoComDataRetroativaBem(bem.getId(), tipoReducao.getDescricao(), selecionado.getDataEstorno());

            if (!Strings.isNullOrEmpty(mensagem)) {
                selecionado.getLogReducaoOuEstorno().getMensagens().add(
                    criarMensagemLogReducaoOuEstorno(
                        selecionado.getLogReducaoOuEstorno(),
                        bem,
                        bem.getValorOriginal(),
                        mensagem));
                return false;
            }
        }
        return true;
    }

    private boolean validarMovimentoRetroativoReducaoNormal(ProcessamentoReducaoValorBem processamentoReducaoValorBem, LoteReducaoValorBem selecionado, Bem bem, ConfigMovimentacaoBem configuracao) {
        if (configuracao.getValidarMovimentoRetroativo()) {
            String mensagem = configMovimentacaoBemFacade.validarMovimentoComDataRetroativaBem(bem.getId(), selecionado.getTipoReducao().getDescricao(), selecionado.getDataLancamento());
            if (!Strings.isNullOrEmpty(mensagem)) {
                processamentoReducaoValorBem.getMsgs().add(criarMensagemLogReducaoOuEstorno(
                    selecionado.getLogReducaoOuEstorno(),
                    bem, bem.getUltimoEstado().getValorOriginal(),
                    mensagem));
                return false;
            }
        }
        return true;
    }

    private ReducaoValorBemNaoAplicavel criarReducaoValorBemNaoAplicavel(LoteReducaoValorBem selecionado, GrupoBem grupoBem,
                                                                         Bem bem, BigDecimal valorOriginal) {
        ReducaoValorBemNaoAplicavel reducaoValorBemNaoAplicavel = new ReducaoValorBemNaoAplicavel();
        reducaoValorBemNaoAplicavel.setLoteReducaoValorBem(selecionado);
        reducaoValorBemNaoAplicavel.setGrupoBem(grupoBem);
        reducaoValorBemNaoAplicavel.setBem(bem);
        reducaoValorBemNaoAplicavel.setValorOriginal(valorOriginal);
        return reducaoValorBemNaoAplicavel;
    }

    private List<MsgLogReducaoOuEstorno> criarMensagemLogReducaoOuEstorno(LogReducaoOuEstorno logReducaoOuEstorno, List<Bem> bens, String mensagem) {
        List<MsgLogReducaoOuEstorno> toReturn = Lists.newArrayList();
        for (Bem bem : bens) {
            toReturn.add(criarMensagemLogReducaoOuEstorno(logReducaoOuEstorno, bem, BigDecimal.ZERO, mensagem));
        }
        return toReturn;
    }

    private MsgLogReducaoOuEstorno criarMensagemLogReducaoOuEstorno(LogReducaoOuEstorno logReducaoOuEstorno, Bem bem, BigDecimal valorOriginal, String mensagem) {
        MsgLogReducaoOuEstorno msg = new MsgLogReducaoOuEstorno();
        msg.setLogReducaoOuEstorno(logReducaoOuEstorno);
        msg.setBem(bem);
        msg.setMensagem(mensagem);
        msg.setValor(valorOriginal);
        return msg;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 5)
    public void remover(LoteReducaoValorBem entity, ConfigMovimentacaoBem configuracao) {
        List<Number> bensRecuperados = buscarIdsBemReducaoValorBem(entity);
        if (!bensRecuperados.isEmpty()) {
            configMovimentacaoBemFacade.desbloquearBensJDBC(configuracao, bensRecuperados);
        }
        desbloquearBensNaoAplicaveisAndResiduais(entity, configuracao);
        deletarReducaoValorBemContabil(entity);
        entity = super.recuperar(entity.getId());
        Hibernate.initialize(entity.getReducoes());
        super.remover(entity);
    }

    private void deletarReducaoValorBemContabil(LoteReducaoValorBem entity) {
        Query update = em.createNativeQuery("delete from reducaovalorbemcontabil where lotereducaovalorbem_id = " + entity.getId());
        update.executeUpdate();
    }

    private void desbloquearBensNaoAplicaveisAndResiduais(LoteReducaoValorBem entity, ConfigMovimentacaoBem configuacao) {
        List<Number> bens = Lists.newArrayList();
        bens.addAll(buscarIdsBemNaoAplicaveis(entity));
        bens.addAll(buscarIdsBemResiduais(entity));
        bens.addAll(buscarIdsBemInconsistenciaDiferenteDoBloqueio(entity));
        configMovimentacaoBemFacade.desbloquearBensJDBC(configuacao, bens);
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 5)
    @Asynchronous
    public Future<LoteReducaoValorBem> concluirReducaoEmAndamento(AssistenteMovimentacaoBens assistente, LoteReducaoValorBem entity, List<ReducaoValorBem> reducoes) {
        concluir(assistente, entity, reducoes);
        return new AsyncResult(entity);
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 5)
    @Asynchronous
    public Future<LoteReducaoValorBem> concluirReducao(AssistenteMovimentacaoBens assistente, LoteReducaoValorBem entity) {
        concluir(assistente, entity, entity.getReducoes());
        return new AsyncResult(entity);
    }

    private void concluir(AssistenteMovimentacaoBens assistente, LoteReducaoValorBem entity, List<ReducaoValorBem> reducoes) {
        contabilizarLoteReducaoValorBem(assistente, entity, reducoes);

        assistente.setIdsBensBloqueados(buscarIdsBemReducaoValorBem(entity));
        configMovimentacaoBemFacade.desbloquearBensJDBCAssincrono(assistente);
        desbloquearBensNaoAplicaveisAndResiduais(entity, assistente.getConfigMovimentacaoBem());

        finalizarEventoReducaoValorBem(assistente, entity);

        if(!assistente.getTemErros()) {
            alterarSituacaoLoteReducaoValorBem(entity, SituacaoReducaoValorBem.CONCLUIDA);
        } else{
            alterarSituacaoLoteReducaoValorBem(entity, SituacaoReducaoValorBem.EM_ANDAMENTO);
        }


        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("Processo de " + entity.getTipoReducao().getDescricao() + " concluído com sucesso! ");
        assistente.setSelecionado(entity);
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 5)
    @Asynchronous
    public Future<LoteReducaoValorBem> salvar(AssistenteMovimentacaoBens assistente, LoteReducaoValorBem entity) {
        if (entity.getCodigo() == null) {
            entity.setCodigo(singletonGeradorCodigoReducaoValorBem.getProximoCodigo(entity.getTipoReducao()));
        }
        salvarLoteReducaoValorBem(assistente, entity);
        criarReducaoValorBemContabil(entity);
        bloquearBens(assistente);

        assistente.setSelecionado(entity);
        assistente.zerarContadoresProcesso();
        return new AsyncResult(entity);
    }

    private void bloquearBens(AssistenteMovimentacaoBens assistente) {
        List<Number> bens = Lists.newArrayList();
        for (Bem bem : assistente.getBensSelecionados()) {
            bens.add(bem.getId());
        }
        configMovimentacaoBemFacade.bloquearBensPorSituacaoJDBC(assistente.getConfigMovimentacaoBem(), bens, SituacaoEventoBem.AGUARDANDO_EFETIVACAO);
    }

    private void criarReducaoValorBemContabil(LoteReducaoValorBem entity) {
        List<ReducaoValorBemContabil> associacoes = entity.getReducoesValorBemContabil();
        for (ReducaoValorBem item : entity.getReducoes()) {
            ReducaoValorBemContabil associacao = new ReducaoValorBemContabil();
            associacao.setBem(item.getBem());
            associacao.setLoteReducaoValorBem(entity);
            associacao.setGrupoPatrimonial(item.getEstadoResultante().getGrupoBem());
            associacao.setValor(item.getValorDoLancamento());
            associacoes.add(associacao);
        }
        jdbcReducaoValorBemDAO.persistirReducoesValorBemContabil(associacoes);
    }

    private void criarReducaoValorBemEstornoContabil(LoteEstornoReducaoValorBem estorno) {
        for (EstornoReducaoValorBem item : estorno.getEstornos()) {
            ReducaoValorBemEstornoContabil associacao = new ReducaoValorBemEstornoContabil();
            associacao.setBem(item.getBem());
            associacao.setLoteEstornoReducaoValorBem(estorno);
            associacao.setGrupoPatrimonial(item.getEstadoResultante().getGrupoBem());
            associacao.setValor(item.getValorDoLancamento());
            associacao.setSituacao(SituacaoReducaoValorBem.EM_ANDAMENTO);
            estorno.getReducoesValorBemEstornoContabil().add(associacao);
        }
    }


    private void salvarLoteReducaoValorBem(AssistenteMovimentacaoBens assistente, LoteReducaoValorBem entity) {
        assistente.setDescricaoProcesso("Salvando informações de lançamento.");
        assistente.zerarContadoresProcesso();
        if (entity.getDetentorArquivoComposicao() != null && !entity.getDetentorArquivoComposicao().getArquivosComposicao().isEmpty()) {
            jdbcDetentorArquivoComposicaoDAO.persistirDetentorArquivoComposicao(entity.getDetentorArquivoComposicao());
            for (ArquivoComposicao arquivoComposicao : entity.getDetentorArquivoComposicao().getArquivosComposicao()) {
                jdbcDetentorArquivoComposicaoDAO.persistirArquivo(arquivoComposicao.getArquivo());
                jdbcDetentorArquivoComposicaoDAO.persistirArquivoParte(arquivoComposicao.getArquivo().getPartes());
                jdbcDetentorArquivoComposicaoDAO.persistirArquivoComposicao(arquivoComposicao);
            }
        }
        jdbcReducaoValorBemDAO.persistirLogReducaoOuEstorno(entity.getLogReducaoOuEstorno());
        jdbcReducaoValorBemDAO.persistirLoteReducaoValorBem(entity);
        jdbcReducaoValorBemDAO.persistirMensagensLog(entity.getLogReducaoOuEstorno().getMensagens());
        jdbcReducaoValorBemDAO.persistirBemNaoAplicavel(entity.getBensNaoAplicaveis());
        jdbcReducaoValorBemDAO.persistirBemResidual(entity.getBensResidual());

        List<EstadoBem> estadosResultante = Lists.newArrayList();
        for (Object obj : entity.getReducoes()) {
            estadosResultante.add(((ReducaoValorBem) obj).getEstadoResultante());
        }
        jdbcReducaoValorBemDAO.persistirEstadosBem(estadosResultante);
        jdbcReducaoValorBemDAO.persistirReducoesValorBem(entity.getReducoes());
    }


    public List<ReducaoValorBemContabil> buscarReducaoValorBemContabil(LoteReducaoValorBem entity) {
        String sql = "  select red.* from reducaovalorbemcontabil red where red.loteReducaoValorBem_id = :idLote";
        Query q = em.createNativeQuery(sql, ReducaoValorBemContabil.class);
        q.setParameter("idLote", entity.getId());
        return q.getResultList();
    }

    public List<ReducaoValorBemContabil> buscarReducaoValorBemContabil(LoteReducaoValorBem entity, Boolean contabilizado) {
        String sql = " select * from reducaovalorbemcontabil " +
            "          where lotereducaovalorbem_id = :idReducao ";
        sql += contabilizado ? " and bensmoveis_id is not null" : " and bensmoveis_id is null ";
        Query q = em.createNativeQuery(sql, ReducaoValorBemContabil.class);
        q.setParameter("idReducao", entity.getId());
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

    public List<ReducaoValorBemEstornoContabil> buscarReducaoValorBemEstornoContabil(LoteEstornoReducaoValorBem estorno) {
        String sql = "  select red.* from reducaovalorbemestornocontabil red where red.loteEstornoReducaoValorBem_id = :idLote";
        Query q = em.createNativeQuery(sql, ReducaoValorBemEstornoContabil.class);
        q.setParameter("idLote", estorno.getId());
        return q.getResultList();
    }

    public List<ReducaoValorBemEstornoContabil> buscarReducaoValorBemEstornoContabil(LoteEstornoReducaoValorBem estorno, Boolean contabilizado) {
        String sql = " select * from reducaovalorbemestornocontabil " +
            "          where loteEstornoReducaoValorBem_id = :idReducao ";
        sql += contabilizado ? " and bensmoveis_id is not null" : " and bensmoveis_id is null ";
        Query q = em.createNativeQuery(sql, ReducaoValorBemEstornoContabil.class);
        q.setParameter("idReducao", estorno.getId());
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

    public List<VoReducaoValorBemContabil> buscarReducaoValorBemContabilValoresContabilizados(LoteReducaoValorBem lote) {
        String sql = "select  " +
            "  coalesce(sum(depreciacao),0) as depreciacao, " +
            "  coalesce(bens_moveis, 0) as bens_moveis,  " +
            "  coalesce(mov_grupo_bem,0) as mov_grupo_bem, " +
            "  coalesce(contabil,0) as contabil," +
            "  grupo " +
            " from( " +
            "  select " +
            "  grupo.codigo as grupo, " +
            "  red.valor as depreciacao, " +
            "  bm.valor as bens_moveis, " +
            "   (select mov.credito from movimentogrupobensmoveis mov  " +
            "    where mov.operacao = :operacao " +
            "      and mov.tipolancamento = bm.tipolancamento" +
            "      and mov.tipogrupo = :tipoGrupo " +
            "      and mov.naturezatipogrupobem = :natureza " +
            "      and mov.datamovimento = bm.databensmoveis " +
            "      and mov.grupobem_id= bm.grupobem_id " +
            "      and mov.unidadeorganizacional_id = bm.unidadeorganizacional_id) as mov_grupo_bem, " +
            "  (select lanc.valor from lancamentocontabil lanc  " +
            "    where trim(lanc.complementohistorico) = trim(bm.historicorazao) " +
            "    and lanc.datalancamento = bm.databensmoveis  " +
            "    and lanc.unidadeorganizacional_id = bm.unidadeorganizacional_id " +
            "    ) as contabil " +
            " from reducaovalorbemcontabil red " +
            " inner join grupobem  grupo on grupo.id = red.grupopatrimonial_id " +
            " left join bensmoveis bm on bm.id = red.bensmoveis_id " +
            " where red.lotereducaovalorbem_id = :idReducao  " +
            " ) " +
            " group by grupo, bens_moveis, mov_grupo_bem, contabil " +
            " order by grupo ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idReducao", lote.getId());
        q.setParameter("operacao", TipoOperacaoBensMoveis.DEPRECIACAO_BENS_MOVEIS.name());
        q.setParameter("tipoGrupo", TipoGrupo.BEM_MOVEL_PRINCIPAL.name());
        q.setParameter("natureza", NaturezaTipoGrupoBem.DEPRECIACAO.name());
        List<Object[]> lista = q.getResultList();
        return VoReducaoValorBemContabil.preencherArrayObjeto(lista);
    }

    public List<VoReducaoValorBemContabil> buscarReducaoValorBemEstornoContabilValoresContabilizados(LoteEstornoReducaoValorBem lote) {
        String sql = "select  " +
            "  coalesce(sum(depreciacao),0) as depreciacao, " +
            "  coalesce(bens_moveis, 0) as bens_moveis,  " +
            "  coalesce(mov_grupo_bem,0) as mov_grupo_bem, " +
            "  coalesce(contabil,0) as contabil," +
            "  grupo " +
            " from( " +
            "  select  " +
            "  grupo.codigo as grupo, " +
            "  red.valor as depreciacao, " +
            "  bm.valor as bens_moveis, " +
            "   (select mov.credito from movimentogrupobensmoveis mov  " +
            "    where mov.operacao = :operacao " +
            "      and mov.tipolancamento= bm.tipolancamento " +
            "      and mov.tipogrupo = :tipoGrupo " +
            "      and mov.naturezatipogrupobem = :natureza " +
            "      and mov.datamovimento = bm.databensmoveis " +
            "      and mov.grupobem_id= bm.grupobem_id " +
            "      and mov.unidadeorganizacional_id = bm.unidadeorganizacional_id) as mov_grupo_bem, " +
            "  (select lanc.valor from lancamentocontabil lanc  " +
            "    where trim(lanc.complementohistorico) = trim(bm.historicorazao) " +
            "    and lanc.datalancamento = bm.databensmoveis  " +
            "    and lanc.unidadeorganizacional_id = bm.unidadeorganizacional_id " +
            "    ) as contabil " +
            " from reducaovalorbemestornocontabil red " +
            " inner join grupobem  grupo on grupo.id = red.grupopatrimonial_id " +
            " left join bensmoveis bm on bm.id = red.bensmoveis_id " +
            " where red.loteestornoreducaovalorbem_id = :idReducao  " +
            " ) " +
            " group by grupo, bens_moveis, mov_grupo_bem, contabil " +
            " order by grupo ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idReducao", lote.getId());
        q.setParameter("operacao", TipoOperacaoBensMoveis.DEPRECIACAO_BENS_MOVEIS.name());
        q.setParameter("tipoGrupo", TipoGrupo.BEM_MOVEL_PRINCIPAL.name());
        q.setParameter("natureza", NaturezaTipoGrupoBem.DEPRECIACAO.name());
        List<Object[]> lista = q.getResultList();
        return VoReducaoValorBemContabil.preencherArrayObjeto(lista);
    }

    private List<Number> buscarIdsBemReducaoValorBem(LoteReducaoValorBem entity) {
        String sql = "" +
            " select " +
            "   ev.bem_id " +
            " from reducaovalorbem item " +
            "   inner join eventobem ev on ev.id = item.id " +
            " where item.lotereducaovalorbem_id = :idLoteReducao";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idLoteReducao", entity.getId());
        return ((List<Number>) q.getResultList());
    }

    private List<Number> buscarIdsBemEstornoReducaoValorBem(LoteEstornoReducaoValorBem estornoReducao) {
        String sql = "" +
            " select " +
            "   ev.bem_id " +
            " from estornoreducaovalorbem item " +
            "   inner join eventobem ev on ev.id = item.id " +
            " where item.loteestornoreducaovalorbem_id = :idLoteReducao";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idLoteReducao", estornoReducao.getId());
        return ((List<Number>) q.getResultList());
    }

    private List<Number> buscarIdsBemNaoAplicaveis(LoteReducaoValorBem entity) {
        String sql = " select item.bem_id from reducaovalorbemnaoaplicave item where item.lotereducaovalorbem_id = :idLoteReducao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idLoteReducao", entity.getId());
        return ((List<Number>) q.getResultList());
    }

    private List<Number> buscarIdsBemInconsistenciaDiferenteDoBloqueio(LoteReducaoValorBem entity) {
        String sql = " select msg.bem_id from lotereducaovalorbem lote " +
            "           inner join LogReducaoOuEstorno logE on lote.logreducaoouestorno_id = loge.id " +
            "           inner join MsgLogReducaoOuEstorno msg on msg.logreducaoouestorno_id = logE.id " +
            "          where lote.id = :idLoteReducao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idLoteReducao", entity.getId());
        return ((List<Number>) q.getResultList());
    }

    private List<Number> buscarIdsBemResiduais(LoteReducaoValorBem entity) {
        String sql = " select item.bem_id from reducaovalorbemresidual item where item.lotereducaovalorbem_id = :idLoteReducao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idLoteReducao", entity.getId());
        return ((List<Number>) q.getResultList());
    }

    public List<Number> buscarIdsItemRedeucaoValorBem(LoteReducaoValorBem entity) {
        String sql = " " +
            " select " +
            "   item.id " +
            " from reducaovalorbem item " +
            "   where item.lotereducaovalorbem_id = :idLoteReducao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idLoteReducao", entity.getId());
        List resultList = q.getResultList();
        if (resultList == null || resultList.isEmpty()){
            return Lists.newArrayList();
        }
        return ((List<Number>) resultList);
    }


    public List<Number> buscarIdsItemEstornoRedeucaoValorBem(LoteEstornoReducaoValorBem estorno) {
        String sql = " " +
            " select " +
            "   item.id " +
            " from estornoreducaovalorbem item " +
            "   where item.loteestornoreducaovalorbem_id = :idLoteReducao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idLoteReducao", estorno.getId());
        return q.getResultList();
    }

    public List<LoteReducaoValorBem> buscarLoteReducaoValorBem(String parte) {
        String sql = " select red.* from lotereducaovalorbem red  where red.codigo like :parte order by red.codigo desc ";
        Query q = em.createNativeQuery(sql, LoteReducaoValorBem.class);
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        q.setParameter("parte", "%" + parte.trim() + "%");

        return q.getResultList();
    }

    private void contabilizarLoteReducaoValorBem(AssistenteMovimentacaoBens assistente, LoteReducaoValorBem loteReducaoValorBem, List<ReducaoValorBem> reducoes) {
        List<IntegradorPatrimonialContabilFacade.EventosAgrupados> eventosAgrupados = IntegradorPatrimonialContabilFacade.agruparListEventoBem(reducoes, loteReducaoValorBem.getDataLancamento());
        List<MsgLogReducaoOuEstorno> msgErrosContabilizacao = Lists.newArrayList();
        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("Contabilizando Processo de " + loteReducaoValorBem.getTipoReducao().getDescricao() + "...");
        assistente.setTotal(eventosAgrupados.size());

        for (IntegradorPatrimonialContabilFacade.EventosAgrupados evento : eventosAgrupados) {
            try {
                String historico = montarHistoricoReducaoValorBem(loteReducaoValorBem, evento);
                List<ReducaoValorBemContabil> associacoes = recuperarReducaoValorBemContabilAssociadaAoBemParaContabilizar(evento.getBens(), loteReducaoValorBem);
                integradorPatrimonialContabilFacade.reduzirValorDoBem(associacoes, evento, loteReducaoValorBem.getTipoReducao(), loteReducaoValorBem.getTipoBem(), historico, false);
            } catch (Exception e) {
                msgErrosContabilizacao.addAll(criarMensagemLogReducaoOuEstorno(loteReducaoValorBem.getLogReducaoOuEstorno(), evento.getBens(), e.getMessage()));
                logger.error("Erro inesperado na contabilizacao de reducao de valor do bem [{}]", e);
                assistente.setTemErros(true);
            }
            assistente.conta();
        }
        if (!msgErrosContabilizacao.isEmpty()) {
            jdbcReducaoValorBemDAO.persistirMensagensLog(msgErrosContabilizacao);
        }
    }

    private List<ReducaoValorBemContabil> recuperarReducaoValorBemContabilAssociadaAoBemParaContabilizar(List<Bem> bens, LoteReducaoValorBem entity) {
        List<ReducaoValorBemContabil> toReturn = Lists.newArrayList();
        List<ReducaoValorBemContabil> associacoes = Lists.newArrayList();
        if (associacoes.isEmpty()) {
            associacoes = buscarReducaoValorBemContabil(entity);
        }
        for (Bem bem : bens) {
            for (ReducaoValorBemContabil red : associacoes) {
                if (red.getBem().equals(bem)) {
                    toReturn.add(red);
                }
            }
        }
        return toReturn;
    }

    private List<ReducaoValorBemEstornoContabil> recuperarReducaoValorBemEstornoContabilAssociadaAoBemParaContabilizar(List<Bem> bens, LoteEstornoReducaoValorBem estorno) {
        List<ReducaoValorBemEstornoContabil> toReturn = Lists.newArrayList();
        List<ReducaoValorBemEstornoContabil> associacoes = Lists.newArrayList();
        if (associacoes.isEmpty()) {
            associacoes = buscarReducaoValorBemEstornoContabil(estorno);
        }
        for (Bem bem : bens) {
            for (ReducaoValorBemEstornoContabil red : associacoes) {
                if (red.getBem().equals(bem)) {
                    toReturn.add(red);
                }
            }
        }
        return toReturn;
    }

    public void atribuirReducaoValorBemContabilEmAndamento(LoteReducaoValorBem entity) {
        List<ReducaoValorBemContabil> reducoesValorBemContabil = buscarReducaoValorBemContabil(entity);
        for (ReducaoValorBemContabil associacao : reducoesValorBemContabil) {
            if (associacao.isEmElaboracao()) {
                associacao.setSituacao(SituacaoReducaoValorBem.EM_ANDAMENTO);
                jdbcReducaoValorBemDAO.atualizarReducaoValorBemContabil(associacao);
            }
        }
    }

    private void finalizarEventoReducaoValorBem(AssistenteMovimentacaoBens assistente, LoteReducaoValorBem entity) {
        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("Finalizando Evento de Redução do Valor Bem - " + entity.getTipoReducao().getDescricao() + "...");
        List<Number> itens = buscarIdsItemRedeucaoValorBem(entity);
        assistente.setTotal(itens.size());

        for (Number id : itens) {
            jdbcReducaoValorBemDAO.atualizarReducaoValorBem(id.longValue());
            assistente.conta();
        }
    }

    private List<EstornoReducaoValorBem> getEstornoReducaoValorBensNaoContabilizadas(LoteEstornoReducaoValorBem loteEstorno) {
        List<EstornoReducaoValorBem> reducoes = Lists.newArrayList();
        List<ReducaoValorBemEstornoContabil> list = buscarReducaoValorBemEstornoContabil(loteEstorno, false);
        for (ReducaoValorBemEstornoContabil redCont : list) {
            for (EstornoReducaoValorBem redBem : loteEstorno.getEstornos()) {
                if (redCont.getBem().equals(redBem.getBem())) {
                    reducoes.add(redBem);
                }
            }
        }
        return reducoes;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 5)
    public Future<LoteEstornoReducaoValorBem> estornarReducaoEmAndamento(AssistenteMovimentacaoBens assistente, LoteEstornoReducaoValorBem loteEstorno) {
        List<Number> idsEvento = buscarIdsItemEstornoRedeucaoValorBem(loteEstorno);
        assistente.setDescricaoProcesso("Recuperando bens para estornar a " + loteEstorno.getLoteReducaoValorBem().getTipoReducao().getDescricao() + "...");
        assistente.zerarContadoresProcesso();
        assistente.setTotal(idsEvento.size());

        List<EstornoReducaoValorBem> estornosReducoes = Lists.newArrayList();
        for (Number idEvento : idsEvento) {
            estornosReducoes.add(em.find(EstornoReducaoValorBem.class, idEvento.longValue()));
            assistente.conta();
        }
        loteEstorno.setEstornos(estornosReducoes);
        List<EstornoReducaoValorBem> bensNaoContabilzados = getEstornoReducaoValorBensNaoContabilizadas(loteEstorno);

        estornarReducaoValorBem(assistente, loteEstorno, bensNaoContabilzados);
        return new AsyncResult(loteEstorno);
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 5)
    public Future<LoteEstornoReducaoValorBem> estornarReducao(AssistenteMovimentacaoBens assistente, LoteEstornoReducaoValorBem loteEstorno) {
        alterarSituacaoLoteReducaoValorBem(loteEstorno.getLoteReducaoValorBem(), SituacaoReducaoValorBem.ESTORNO_EM_ANDAMENTO);
        estornarReducaoValorBem(assistente, loteEstorno, loteEstorno.getEstornos());
        return new AsyncResult(loteEstorno);
    }


    private void estornarReducaoValorBem(AssistenteMovimentacaoBens assistente, LoteEstornoReducaoValorBem loteEstorno, List<EstornoReducaoValorBem> estornosReducoes) {
        contabilizarEstornoReducaoValorBem(assistente, loteEstorno, estornosReducoes);
        alterarSituacaoLoteReducaoValorBem(loteEstorno.getLoteReducaoValorBem(), SituacaoReducaoValorBem.ESTORNADA);

        assistente.setIdsBensBloqueados(buscarIdsBemEstornoReducaoValorBem(loteEstorno));
        configMovimentacaoBemFacade.desbloquearBensJDBCAssincrono(assistente);

        finalizarEventoEstornoReducaoValorBem(assistente, loteEstorno);

        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("Estorno de <strong>" + loteEstorno.getLoteReducaoValorBem().getTipoReducao().getDescricao() + "</strong> realizado com sucesso.");
    }

    public void alterarSituacaoLoteReducaoValorBem(LoteReducaoValorBem loteReducao, SituacaoReducaoValorBem situacao) {
        loteReducao.setSituacao(situacao);
        jdbcReducaoValorBemDAO.atualizarLoteReducaoValorBem(loteReducao);
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 5)
    public Future<LoteEstornoReducaoValorBem> prepararEstornoReducaoValorBem(AssistenteMovimentacaoBens assistente, LoteReducaoValorBem entity) {
        assistente.setDescricaoProcesso("Preparando estorno do processo de <strong>" + entity.getTipoReducao().getDescricao() + "</strong>" +
            ": " + entity.getCodigo() + ". ");

        LoteEstornoReducaoValorBem loteEstorno = novoLoteEstornoReducaoValorBem(assistente, entity);
        List<VOReducaoValorBem> reducoes = recuperarReducoes(entity);
        assistente.setTotal(reducoes.size());

        for (VOReducaoValorBem reducaoValorBem : reducoes) {
            EstornoReducaoValorBem estornoReducaoValorBem = criarEstornoReducaoValorBem(loteEstorno, reducaoValorBem, assistente);
            loteEstorno.getEstornos().add(estornoReducaoValorBem);
            assistente.conta();
        }
        validarMovimentoRetroativoReducaoEstorno(loteEstorno, assistente);
        criarReducaoValorBemEstornoContabil(loteEstorno);
        salvarEstornoReducao(loteEstorno);
        bloquearBens(assistente);

        assistente.zerarContadoresProcesso();
        return new AsyncResult(loteEstorno);
    }

    public void salvarEstornoReducao(LoteEstornoReducaoValorBem loteEstorno) {
        jdbcReducaoValorBemDAO.persistirLogReducaoOuEstorno(loteEstorno.getLogReducaoOuEstorno());
        jdbcReducaoValorBemDAO.persistirLoteEstornoReducaoValorBem(loteEstorno);
        jdbcReducaoValorBemDAO.persistirReducoesValorBemEstornoContabil(loteEstorno.getReducoesValorBemEstornoContabil());
        jdbcReducaoValorBemDAO.persistirMensagensLog(loteEstorno.getLogReducaoOuEstorno().getMensagens());

        List<EstadoBem> estados = Lists.newArrayList();
        for (Object obj : loteEstorno.getEstornos()) {
            estados.add(((EstornoReducaoValorBem) obj).getEstadoResultante());
        }
        jdbcReducaoValorBemDAO.persistirEstadosBem(estados);
        jdbcReducaoValorBemDAO.persistirEstornosReducaoValorBem(loteEstorno.getEstornos());
    }

    public LoteEstornoReducaoValorBem novoLoteEstornoReducaoValorBem(AssistenteMovimentacaoBens assistente, LoteReducaoValorBem loteReducaoValorBem) {
        LoteEstornoReducaoValorBem loteEstorno = new LoteEstornoReducaoValorBem();
        loteEstorno.setLoteReducaoValorBem(loteReducaoValorBem);
        loteEstorno.setDataEstorno(assistente.getDataLancamento());
        loteEstorno.setUsuarioDoEstorno(assistente.getUsuarioSistema());
        loteEstorno.setLogReducaoOuEstorno(new LogReducaoOuEstorno());
        loteEstorno.setEstornos(new ArrayList());
        return loteEstorno;
    }

    private EstornoReducaoValorBem criarEstornoReducaoValorBem(LoteEstornoReducaoValorBem loteEstorno, VOReducaoValorBem reducaoValorBem, AssistenteMovimentacaoBens assistente) {
        EstadoBem ultimoEstadoBem = bemFacade.recuperarUltimoEstadoDoBem(reducaoValorBem.getIdBem());
        EstadoBem novoEstadoBem = bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(ultimoEstadoBem);
        EstornoReducaoValorBem estornoReducaoValorBem = new EstornoReducaoValorBem();
        estornoReducaoValorBem.setLoteEstornoReducaoValorBem(loteEstorno);
        estornoReducaoValorBem.setReducaoValorBem(new ReducaoValorBem(reducaoValorBem.getId(), reducaoValorBem.getValorDoLancamento()));
        Bem bem = bemFacade.recuperarSemDependencias(reducaoValorBem.getIdBem());
        if (bem != null) {
            estornoReducaoValorBem.setBem(bem);
            assistente.getBensSelecionados().add(bem);
        }
        Date dataLancamento = DataUtil.getDataComHoraAtual(assistente.getDataLancamento());
        estornoReducaoValorBem.setDataLancamento(dataLancamento);
        estornoReducaoValorBem.setEstadoInicial(ultimoEstadoBem);
        estornoReducaoValorBem.setEstadoResultante(novoEstadoBem);
        estornoReducaoValorBem.setSituacaoEventoBem(SituacaoEventoBem.AGUARDANDO_EFETIVACAO);
        estornoReducaoValorBem.setValorDoLancamento(reducaoValorBem.getValorDoLancamento());
        estornoReducaoValorBem.getBem().estornarReducao(reducaoValorBem.getValorDoLancamento(), reducaoValorBem.getTipoReducaoValorBem(), novoEstadoBem);
        return estornoReducaoValorBem;
    }

    private void contabilizarEstornoReducaoValorBem(AssistenteMovimentacaoBens assistente, LoteEstornoReducaoValorBem loteEstorno, List<EstornoReducaoValorBem> estornosReducoes) {
        LoteReducaoValorBem loteReducao = loteEstorno.getLoteReducaoValorBem();

        assistente.setDescricaoProcesso("Estornando o processo de <strong>" + loteEstorno.getLoteReducaoValorBem().getTipoReducao().getDescricao() + "</strong>" +
            ": " + loteEstorno.getLoteReducaoValorBem().getCodigo() + ". ");
        assistente.zerarContadoresProcesso();
        assistente.setTotal(estornosReducoes.size());

        List<IntegradorPatrimonialContabilFacade.EventosAgrupados> eventosAgrupados = IntegradorPatrimonialContabilFacade.agruparListEventoBem(estornosReducoes, loteEstorno.getDataEstorno());

        for (IntegradorPatrimonialContabilFacade.EventosAgrupados evento : eventosAgrupados) {
            logger.debug("EventoAgrupado {}", evento);
            try {
                String historico = montarHistoricoEstornoReducaoValorBem(loteEstorno, loteReducao.getTipoReducao(), evento);
                List<ReducaoValorBemEstornoContabil> associacoes = recuperarReducaoValorBemEstornoContabilAssociadaAoBemParaContabilizar(evento.getBens(), loteEstorno);
                integradorPatrimonialContabilFacade.reduzirValorDoBemEstorno(associacoes, evento, loteReducao.getTipoReducao(), loteReducao.getTipoBem(), historico, false);
            } catch (Exception e) {
                List<MsgLogReducaoOuEstorno> mensagem = criarMensagemLogReducaoOuEstorno(loteEstorno.getLogReducaoOuEstorno(), evento.getBens(), e.getMessage());
                loteEstorno.getLogReducaoOuEstorno().getMensagens().addAll(mensagem);
            }
            assistente.contar(evento.getBens().size());
        }
        jdbcReducaoValorBemDAO.persistirMensagensLog(loteEstorno.getLogReducaoOuEstorno().getMensagens());
    }

    private void finalizarEventoEstornoReducaoValorBem(AssistenteMovimentacaoBens assistente, LoteEstornoReducaoValorBem estorno) {
        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("Finalizando Evento de Estorno Redução Valor Bem - " + estorno.getLoteReducaoValorBem().getTipoReducao().getDescricao() + "...");
        List<Number> itens = Lists.newArrayList();
        itens.addAll(buscarIdsItemEstornoRedeucaoValorBem(estorno));

        assistente.setTotal(itens.size());
        for (Number id : itens) {
            Query update = em.createNativeQuery("update eventobem set situacaoeventobem = '" + SituacaoEventoBem.FINALIZADO.name() + "' where id = " + id);
            update.executeUpdate();
            assistente.conta();
        }
    }

    private List<EstornoReducaoValorBem> validarMovimentoRetroativoReducaoEstorno(LoteEstornoReducaoValorBem loteEstornoReducaoValorBem, AssistenteMovimentacaoBens assistente) {
        List<EstornoReducaoValorBem> estornosParaContabilizar = Lists.newArrayList();
        loteEstornoReducaoValorBem.getLogReducaoOuEstorno().setMensagens(new ArrayList<MsgLogReducaoOuEstorno>());
        for (EstornoReducaoValorBem eventoEstorno : loteEstornoReducaoValorBem.getEstornos()) {
            if (validarUltimoEventoBemParaEstornarDepreciacao(loteEstornoReducaoValorBem, eventoEstorno.getBem(), assistente)) {
                estornosParaContabilizar.add(eventoEstorno);
            }
        }
        return estornosParaContabilizar;
    }

    public void simularContabilizacaoEstornoReducaoValorBem(LoteEstornoReducaoValorBem loteEstorno) {
        HashSet<String> inconsistencias = new HashSet();
        LoteReducaoValorBem loteReducaoValorBem = loteEstorno.getLoteReducaoValorBem();
        TipoReducaoValorBem tipoReducao = loteReducaoValorBem.getTipoReducao();
        List<IntegradorPatrimonialContabilFacade.EventosAgrupados> eventosAgrupados = IntegradorPatrimonialContabilFacade.agruparListEventoBem(loteEstorno.getEstornos(), loteEstorno.getDataEstorno());

        for (IntegradorPatrimonialContabilFacade.EventosAgrupados evento : eventosAgrupados) {
            logger.debug("EventoAgrupado {}", evento);
            try {
                String historico = montarHistoricoEstornoReducaoValorBem(loteEstorno, tipoReducao, evento);
                integradorPatrimonialContabilFacade.reduzirValorDoBemEstorno(
                    loteEstorno.getReducoesValorBemEstornoContabil(),
                    evento, tipoReducao,
                    loteReducaoValorBem.getTipoBem(), historico, true);
            } catch (Exception e) {
                inconsistencias.add(e.getMessage());
                criarMensagemLogReducaoOuEstorno(loteEstorno.getLogReducaoOuEstorno(), evento.getBens(), e.getMessage());
            }
        }
        jdbcReducaoValorBemDAO.persistirMensagensLog(loteEstorno.getLogReducaoOuEstorno().getMensagens());
        lancarExceptionCapturada(loteEstorno, inconsistencias);
    }

    private void lancarExceptionCapturada(LoteEstornoReducaoValorBem loteEstornoReducaoValorBem, HashSet<String> inconsistencias) {
        ValidacaoException ve = new ValidacaoException();
        if (!loteEstornoReducaoValorBem.getMensagens().isEmpty()) {
            for (MsgLogReducaoOuEstorno msg : loteEstornoReducaoValorBem.getMensagens()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(msg.getMensagem());
            }
        }
        if (!inconsistencias.isEmpty()) {
            for (String inconsistencia : inconsistencias) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(inconsistencia);
            }
        }
        ve.lancarException();
    }

    private String montarHistoricoReducaoValorBem(LoteReducaoValorBem loteReducaoValorBem, IntegradorPatrimonialContabilFacade.EventosAgrupados evento) {
        return "Processo de " + loteReducaoValorBem.getTipoReducao().getDescricao() +
            " Código: " + loteReducaoValorBem.getCodigo() + " realizado no dia " + DataUtil.getDataFormatadaDiaHora(new Date())
            + ", Data de Lançamento: " + DataUtil.getDataFormatada(loteReducaoValorBem.getDataLancamento())
            + ", Valor: " + evento.getValorDoLancamento()
            + ", Tipo de Lançamento: Normal";
    }

    private String montarHistoricoEstornoReducaoValorBem(LoteEstornoReducaoValorBem loteEstornoReducaoValorBem, TipoReducaoValorBem tipoReducao, IntegradorPatrimonialContabilFacade.EventosAgrupados evento) {
        return "Estorno do Processo de " + tipoReducao.getDescricao() +
            " Código: " + loteEstornoReducaoValorBem.getLoteReducaoValorBem().getCodigo() + " realizado no dia " + DataUtil.getDataFormatadaDiaHora(new Date())
            + ", Data de Lançamento: " + DataUtil.getDataFormatada(loteEstornoReducaoValorBem.getLoteReducaoValorBem().getDataLancamento())
            + ", Data de Estorno: " + DataUtil.getDataFormatada(loteEstornoReducaoValorBem.getDataEstorno())
            + ", Grupo Bem: " + evento.getGrupoBemResultante()
            + ", Valor: " + evento.getValorDoLancamento()
            + ", Tipo de Lançamento: Estorno";
    }

    public LoteEstornoReducaoValorBem recuperarLoteEstornoReducaoValorBem(LoteReducaoValorBem selecionado) {
        String sql = " select lote.* from loteestornoreducaovalorbem lote where lote.lotereducaovalorbem_id = :id_lote ";
        Query q = em.createNativeQuery(sql, LoteEstornoReducaoValorBem.class);
        q.setParameter("id_lote", selecionado.getId());
        if (!q.getResultList().isEmpty()) {
            LoteEstornoReducaoValorBem loteEstorno = (LoteEstornoReducaoValorBem) q.getResultList().get(0);
            if (loteEstorno.getLogReducaoOuEstorno() != null) {
                Hibernate.initialize(loteEstorno.getLogReducaoOuEstorno().getMensagens());
            }
            return loteEstorno;
        }
        return null;
    }

    public int quantidadeReducoes(AbstractReducaoValorBemControlador.FiltroReducoes filtro) {
        if (filtro != null) {
            String hql = "Select count(rd) " +
                " from LoteReducaoValorBem lote " +
                "  inner join lote.reducoes rd " +
                "  inner join rd.bem bem" +
                "  inner join rd.estadoResultante estadoResultante " +
                " where lote.id = :lote ";
            hql += montarCondicaoPorMapFiltros(filtro.getFilters());
            Query q = em.createQuery(hql);
            q.setParameter("lote", filtro.getLoteReducaoValorBem().getId());
            return ((Long) q.getSingleResult()).intValue();
        }
        return 0;
    }

    public int quantidadeReducoesEstorno(AbstractReducaoValorBemControlador.FiltroReducoes filtro) {
        if (filtro != null) {
            String hql = "Select count(estorno) " +
                " from LoteEstornoReducaoValorBem loteestorno " +
                "  inner join loteestorno.estornos estorno " +
                "  inner join loteestorno.loteReducaoValorBem lote " +
                "  inner join estorno.bem bem " +
                " where lote.id = :lote ";
            hql += montarCondicaoPorMapFiltros(filtro.getFilters());
            Query q = em.createQuery(hql);
            q.setParameter("lote", filtro.getLoteReducaoValorBem().getId());
            return ((Long) q.getSingleResult()).intValue();
        }
        return 0;
    }

    public BigDecimal totalReducoes(AbstractReducaoValorBemControlador.FiltroReducoes filtro) {
        String hql = "Select coalesce(sum(rd.valorDoLancamento), 0) " +
            " from LoteReducaoValorBem lote " +
            "  inner join lote.reducoes rd " +
            " where lote.id = :lote";
        Query q = em.createQuery(hql);
        q.setParameter("lote", filtro.getLoteReducaoValorBem().getId());
        return !q.getResultList().isEmpty() ? (BigDecimal) q.getSingleResult() : BigDecimal.ZERO;
    }

    public BigDecimal totalReducoesEstorno(AbstractReducaoValorBemControlador.FiltroReducoes filtro) {
        String hql = "Select sum(est.valorDoLancamento) " +
            " from LoteEstornoReducaoValorBem loteestorno " +
            "  inner join loteestorno.estornos est " +
            "  inner join loteestorno.loteReducaoValorBem lote" +
            " where lote.id = :lote";
        Query q = em.createQuery(hql);
        q.setParameter("lote", filtro.getLoteReducaoValorBem().getId());
        return (BigDecimal) q.getSingleResult();
    }

    public BigDecimal totalBens(AbstractReducaoValorBemControlador.FiltroReducoes filtro) {
        String hql = "Select coalesce(sum(rd.estadoResultante.valorOriginal), 0) " +
            " from LoteReducaoValorBem lote " +
            "  inner join lote.reducoes rd " +
            " where lote.id = :lote";
        Query q = em.createQuery(hql);
        q.setParameter("lote", filtro.getLoteReducaoValorBem().getId());
        return q.getResultList().isEmpty() ? BigDecimal.ZERO : (BigDecimal) q.getSingleResult();
    }

    public List<ReducaoValorBem> recuperarReducoes(AbstractReducaoValorBemControlador.FiltroReducoes filtro) {
        String hql = "Select rd " +
            " from LoteReducaoValorBem lote " +
            "  inner join lote.reducoes rd " +
            "  inner join rd.bem bem" +
            "  inner join rd.estadoResultante estadoResultante " +
            " where lote.id = :lote ";
        hql += montarCondicaoPorMapFiltros(filtro.getFilters()) + " order by bem.identificacao";
        Query q = em.createQuery(hql, ReducaoValorBem.class);
        q.setParameter("lote", filtro.getLoteReducaoValorBem().getId());
        q.setMaxResults(filtro.getQuantidadeRegistro());
        q.setFirstResult(filtro.getPrimeiroRegistro());
        return q.getResultList();
    }

    private String montarCondicaoPorMapFiltros(Map<String, String> filters) {
        String condicao = "";
        try {
            Set set = filters.entrySet();
            Iterator i = set.iterator();
            if (i.hasNext()) {
                Map.Entry me = (Map.Entry) i.next();
                condicao += " and lower(" + me.getKey().toString() + ") like '%" + me.getValue().toString().trim().toLowerCase() + "%' ";
            }
        } catch (Exception e) {
        }
        return condicao;
    }

    public List<EstornoReducaoValorBem> recuperarEstornoReducaoValorBem(AbstractReducaoValorBemControlador.FiltroReducoes filtro) {
        String hql = "Select estorno " +
            " from LoteEstornoReducaoValorBem lote " +
            "  inner join lote.estornos estorno " +
            "  inner join estorno.bem bem " +
            " where lote.id = :lote";
        hql += montarCondicaoPorMapFiltros(filtro.getFilters()) + " order by bem.identificacao";
        Query q = em.createQuery(hql, EstornoReducaoValorBem.class);
        q.setParameter("lote", filtro.getLoteEstornoReducaoValorBem().getId());
        q.setMaxResults(filtro.getQuantidadeRegistro());
        q.setFirstResult(filtro.getPrimeiroRegistro());
        return q.getResultList();
    }

    public List<VOReducaoValorBem> recuperarReducoes(LoteReducaoValorBem lote) {
        String sql = "" +
            " select " +
            "   rvb.id, " +
            "   eb.bem_id, " +
            "   eb.valordolancamento, " +
            "   lote.tiporeducao " +
            " from LoteReducaoValorBem lote " +
            "   inner join ReducaoValorBem rvb on rvb.lotereducaovalorbem_id = lote.id " +
            "   inner join EventoBem eb on eb.id = rvb.id " +
            "   inner join bem on bem.id = eb.bem_id " +
            " where lote.id = :idLote " +
            "   and bem.movimentoTipoTres = :bloqueado ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idLote", lote.getId());
        q.setParameter("bloqueado", Boolean.FALSE);
        List<VOReducaoValorBem> reducoes = Lists.newArrayList();
        List<Object[]> lista = q.getResultList();
        for (Object[] obj : lista) {
            VOReducaoValorBem reducao = new VOReducaoValorBem();
            reducao.setId(((BigDecimal) obj[0]).longValue());
            reducao.setIdBem(((BigDecimal) obj[1]).longValue());
            reducao.setValorDoLancamento(((BigDecimal) obj[2]));
            reducao.setTipoReducaoValorBem(TipoReducaoValorBem.valueOf((String) obj[3]));
            reducoes.add(reducao);
        }
        return reducoes;
    }

    public BemFacade getBemFacade() {
        return bemFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ConfigMovimentacaoBemFacade getConfigMovimentacaoBemFacade() {
        return configMovimentacaoBemFacade;
    }

    public SingletonConcorrenciaPatrimonio getSingletonBloqueioPatrimonio() {
        return singletonBloqueioPatrimonio;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public FaseFacade getFaseFacade() {
        return faseFacade;
    }
}
