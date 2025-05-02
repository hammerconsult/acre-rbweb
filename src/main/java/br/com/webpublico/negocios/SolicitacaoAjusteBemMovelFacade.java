package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.BemVo;
import br.com.webpublico.entidadesauxiliares.FiltroPesquisaBem;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.negocios.tributario.singletons.SingletonConcorrenciaPatrimonio;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by mga on 06/03/2018.
 */
@Stateless
public class SolicitacaoAjusteBemMovelFacade extends AbstractFacade<SolicitacaoAjusteBemMovel> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private GrupoBemFacade grupoBemFacade;
    @EJB
    private ConfigMovimentacaoBemFacade configMovimentacaoBemFacade;
    @EJB
    private SingletonConcorrenciaPatrimonio singletonBloqueioPatrimonio;
    @EJB
    private ReducaoValorBemFacade reducaoValorBemFacade;

    public SolicitacaoAjusteBemMovelFacade() {
        super(SolicitacaoAjusteBemMovel.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public BemFacade getBemFacade() {
        return bemFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public GrupoBemFacade getGrupoBemFacade() {
        return grupoBemFacade;
    }

    public SolicitacaoAjusteBemMovel recuperarArquivo(Object id) {
        SolicitacaoAjusteBemMovel entity = super.recuperar(id);
        if (entity.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(entity.getDetentorArquivoComposicao().getArquivosComposicao());
        }
        return entity;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public void remover(SolicitacaoAjusteBemMovel entity, AssistenteMovimentacaoBens assistente) {
        removerItemEventoBemAndEstadoResultante(assistente, entity);
        super.remover(entity);
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<AssistenteMovimentacaoBens> salvarRetornando(SolicitacaoAjusteBemMovel entity, AssistenteMovimentacaoBens assistente) {
        try {
            entity.setDataVersao(new Date());
            entity = em.merge(entity);
            removerItemEventoBemAndEstadoResultante(assistente, entity);
            criarEventoBem(entity, assistente);

            assistente.zerarContadoresProcesso();
            assistente.setDescricaoProcesso("Persistindo Registros...");
            assistente.setDescricaoProcesso(" ");

            assistente.setSelecionado(entity);
        } catch (Exception ex) {
            logger.error("Erro ao salvar solicitação de ajuste de bens móveis. " + ex.getMessage());
            throw ex;
        }
        return new AsyncResult<>(assistente);
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<AssistenteMovimentacaoBens> concluirSolicitacao(SolicitacaoAjusteBemMovel selecionado, AssistenteMovimentacaoBens assistente) {

        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("Concluíndo Itens da Solicitação de Ajuste de Bens Móveis...");
        assistente.setTotal(assistente.getBensMovimentadosVo().size());

        if (selecionado.getCodigo() == null) {
            selecionado.setCodigo(getCodigoSequencial(selecionado.getTipoAjusteBemMovel()));
        }
        selecionado.setSituacao(SituacaoMovimentoAdministrativo.AGUARDANDO_EFETIVACAO);
        selecionado = em.merge(selecionado);
        concluirItemSolicitacao(assistente);

        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("Finalizando Processo...");
        alterarSituacaoEventoMovimentoBloqueioBem(assistente);

        assistente.setSelecionado(selecionado);
        assistente.setDescricaoProcesso("");
        return new AsyncResult<AssistenteMovimentacaoBens>(assistente);
    }

    private void desbloquearBens(SolicitacaoAjusteBemMovel entity, AssistenteMovimentacaoBens assistente) {
        if (entity.getId() != null) {
            List<Number> bensRecuperados = buscarIdsBemPorSolicitacaoAjuste(entity);
            if (!bensRecuperados.isEmpty()) {
                configMovimentacaoBemFacade.desbloquearBens(assistente.getConfigMovimentacaoBem(), bensRecuperados);
            }
        }
    }

    private void concluirItemSolicitacao(AssistenteMovimentacaoBens assistente) {
        for (BemVo bemVo : assistente.getBensMovimentadosVo()) {
            ItemSolicitacaoAjusteBemMovel itemSol = em.find(ItemSolicitacaoAjusteBemMovel.class, bemVo.getIdEventoBem());
            itemSol.setSituacaoEventoBem(SituacaoEventoBem.AGUARDANDO_EFETIVACAO);
            em.merge(itemSol);
            assistente.conta();
        }
    }

    private void alterarSituacaoEventoMovimentoBloqueioBem(AssistenteMovimentacaoBens assistente) {
        List<Number> idsBens = Lists.newArrayList();
        for (BemVo bemVo : assistente.getBensMovimentadosVo()) {
            idsBens.add(bemVo.getBem().getId());
        }
        configMovimentacaoBemFacade.alterarSituacaoEvento(idsBens, SituacaoEventoBem.AGUARDANDO_EFETIVACAO, assistente.getConfigMovimentacaoBem().getOperacaoMovimentacaoBem());
    }

    private void removerItemEventoBemAndEstadoResultante(AssistenteMovimentacaoBens assistente, SolicitacaoAjusteBemMovel entity) {
        if (entity.getId() != null) {

            assistente.zerarContadoresProcesso();
            assistente.setDescricaoProcesso("Removendo eventos ao editar solicitação de alteração de conservação...");
            desbloquearBens(entity, assistente);

            List<Number> idsItens = buscarIdsItemSolicitacao(entity);
            List<Number> idsEstadoBem = buscarIdsEstadoResultante(entity);
            assistente.setTotal(idsItens.size());

            for (Number idItem : idsItens) {
                bemFacade.deletarItemEventoBem(idItem.longValue(), "itemsolicitacaoajustemovel");
                bemFacade.deletarEventoBem(idItem.longValue());
                assistente.conta();
            }
            for (Number idEstado : idsEstadoBem) {
                bemFacade.deletarEstadoBem(idEstado.longValue());
            }
        }
    }

    private void criarEventoBem(SolicitacaoAjusteBemMovel selecionado, AssistenteMovimentacaoBens assistente) {
        List<Number> bensBloqueio = Lists.newArrayList();

        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("Criando Itens da Solicitação de Ajuste de Bens Móveis...");
        assistente.setTotal(assistente.getBensSelecionadosVo().size());

        limparDetentorDoItem(selecionado);
        for (BemVo bem : assistente.getBensSelecionadosVo()) {
            criarEventoAjusteAndItemSolicitacao(selecionado, bem, assistente);
            bensBloqueio.add(bem.getBem().getId());
            assistente.conta();
        }
        configMovimentacaoBemFacade.bloquearBens(assistente.getConfigMovimentacaoBem(), bensBloqueio);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private void limparDetentorDoItem(SolicitacaoAjusteBemMovel selecionado) {
        if (selecionado != null && selecionado.getId() != null && selecionado.getDetentorArquivoComposicao() != null) {
            Query q = em.createNativeQuery("update eventobem set detentorarquivocomposicao_id = null where id in( " +
                " select item.id FROM itemsolicitacaoajustemovel item inner join eventobem e on e.id = item.id " +
                " where e.detentorarquivocomposicao_id is not null " +
                " and item.solicitacaoajustebemmovel_id = :id) ");
            q.setParameter("id", selecionado.getId());
            q.executeUpdate();
        }
    }

    private void criarEventoAjusteAndItemSolicitacao(SolicitacaoAjusteBemMovel selecionado, BemVo bemVo, AssistenteMovimentacaoBens assistente) {
        TipoEventoBem tipoEventoBem = retornaTipoEventoBem(selecionado);
        Date dataLancamento = DataUtil.getDataComHoraAtual(assistente.getDataLancamento());

        ItemSolicitacaoAjusteBemMovel itemSolicitacao = new ItemSolicitacaoAjusteBemMovel(tipoEventoBem);
        itemSolicitacao.setSituacaoEventoBem(SituacaoEventoBem.EM_ELABORACAO);
        itemSolicitacao.setDataLancamento(dataLancamento);
        if (!selecionado.getContabilizar()){
            itemSolicitacao.setDataOperacao(dataLancamento);
        }
        itemSolicitacao.setSolicitacaoAjusteBemMovel(selecionado);
        itemSolicitacao.setValorAjuste(bemVo.getValorAjuste());
        itemSolicitacao.setValorDoLancamento(BigDecimal.ZERO);
        itemSolicitacao.setBem(bemVo.getBem());
        itemSolicitacao.setDetentorArquivoComposicao(selecionado.getDetentorArquivoComposicao());

        EstadoBem estadoInicial = selecionado.getContabilizar()
            ? bemFacade.recuperarUltimoEstadoDoBem(bemVo.getBem())
            : bemFacade.buscarEstadoDoBemPorData(bemVo.getBem(), assistente.getDataLancamento());


        EstadoBem estadoResultante = em.merge(bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(estadoInicial));

        itemSolicitacao.setEstadoInicial(estadoInicial);
        itemSolicitacao.setEstadoResultante(estadoResultante);
        em.persist(itemSolicitacao);
    }

    private TipoEventoBem retornaTipoEventoBem(SolicitacaoAjusteBemMovel selecionado) {
        if (selecionado.getTipoAjusteBemMovel().isOriginal()) {
            return TipoEventoBem.SOLICITACAO_AJUSTE_BEM_MOVEL_ORIGINAL;

        } else if (selecionado.getTipoAjusteBemMovel().isDepreciacao()) {
            return TipoEventoBem.SOLICITACAO_AJUSTE_BEM_MOVEL_DEPRECIACAO;

        } else {
            return TipoEventoBem.SOLICITACAO_AJUSTE_BEM_MOVEL_AMORTIZACAO;
        }
    }

    private Long getCodigoSequencial(TipoAjusteBemMovel tipoAjusteBemMovel) {
        String sql = " select coalesce(max(to_number(codigo)),0)+1 from SolicitacaoAjusteBemMovel where tipoAjusteBemMovel = :tipoAjuste ";
        Query q = getEntityManager().createNativeQuery(sql);
        q.setParameter("tipoAjuste", tipoAjusteBemMovel.name());
        try {
            BigDecimal b = (BigDecimal) q.getSingleResult();
            return b.longValue();
        } catch (NoResultException e) {
            return 1L;
        }
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<List<BemVo>> pesquisarAssincronoBensSolicitacao(SolicitacaoAjusteBemMovel entity, AssistenteMovimentacaoBens assistente) {
        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("Pesquisando bens para visualização...");
        return new AsyncResult<>(pesquisarBensSolicitacao(entity, assistente));
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<List<BemVo>> pesquisarBens(FiltroPesquisaBem filtro, AssistenteMovimentacaoBens assistente) {

        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("Pesquisando bens da unidade: " + filtro.getHierarquiaAdministrativa() + "...");
        SolicitacaoAjusteBemMovel selecionado = (SolicitacaoAjusteBemMovel) filtro.getSelecionado();
        ConfigMovimentacaoBem configuracao = assistente.getConfigMovimentacaoBem();

        String sql = "" +
            " select distinct " +
            "    bem.id as idBem, " +
            "    bem.identificacao, " +
            "    bem.descricao, " +
            "    eb.id as id_evento, " +
            "    eb.situacaoeventobem as situacao_evento, " +
            "    hoOrc.codigo || ' - ' || hoOrc.descricao as unidade,  " +
            "    estb.id as id_estado_resultante, " +
            "  (select tr.id " +
            "    from tiporeducao tr " +
            "    where tr.grupobem_id = gb.id " +
            "    and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(tr.iniciovigencia) and coalesce(trunc(tr.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "    and rownum = 1 " +
            "  ) tipo_reducao," +
            "    0 as valor_ajuste " +
            "  from eventobem eb   " +
            "    inner join estadobem estb on estb.id = eb.estadoresultante_id " +
            "    inner join hierarquiaorganizacional hoOrc on estb.detentoraorcamentaria_id = hoOrc.subordinada_id " +
            "    inner join hierarquiaorganizacional hoAdm on estb.detentoraadministrativa_id = hoAdm.subordinada_id " +
            "    inner join grupobem gb on estb.grupobem_id = gb.id  " +
            "    inner join bem on bem.id = eb.bem_id ";
        if (selecionado.getTipoAjusteBemMovel().isDepreciacao()
            || selecionado.getTipoAjusteBemMovel().isAmortizacao()) {
            sql += " inner join tiporeducao tr on tr.grupobem_id = gb.id " +
                "     and tr.tiporeducaovalorbem <> '" + TipoReducaoValorBem.NAO_APLICAVEL.name() + "' ";
        }
        sql += " where gb.tipobem = :tipoBem " +
            "    and hoAdm.codigo like :unidadeAdm " +
            "    and estb.estadoBem <> :baixado " +
            "    and eb.situacaoEventoBem <> :alienado " +
            "    and hoOrc.tipohierarquiaorganizacional = :tipoHoOrc " +
            "    and hoAdm.tipohierarquiaorganizacional = :tipoHoAdm " +
            "    and to_date(:dataOperacao, 'dd/MM/yyyy') between hoOrc.iniciovigencia and coalesce(hoOrc.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy'))  " +
            "    and to_date(:dataOperacao, 'dd/MM/yyyy') between hoAdm.iniciovigencia and coalesce(hoAdm.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy'))  " +
            "    and eb.dataoperacao = (select max(eb2.dataoperacao) from eventobem eb2 where eb2.bem_id = bem.id)";
        sql += FiltroPesquisaBem.getCondicaoExistsBloqueioPesquisaBem(configuracao);
        sql += filtro.getHierarquiaOrcamentaria() != null ? " and hoOrc.subordinada_id = :idUnidadeOrc" : "";
        sql += filtro.getGrupoBem() != null ? " and gb.id = :idGrupoBem" : "";
        sql += filtro.getBem() != null ? " and bem.id = :idBem" : "";
        sql += " order by to_number(bem.identificacao) ";
        Query q = em.createNativeQuery(sql);
        String codigoAdm = filtro.getHierarquiaAdministrativa().getCodigoSemZerosFinais();
        if (filtro.getHierarquiaAdministrativaFilha() != null) {
            codigoAdm = filtro.getHierarquiaAdministrativaFilha().getCodigoSemZerosFinais();
        }
        FiltroPesquisaBem.adicionarParametrosConfigMovimentacaoBem(q, configuracao);
        q.setParameter("unidadeAdm", codigoAdm + "%");
        q.setParameter("tipoBem", TipoBem.MOVEIS.name());
        q.setParameter("baixado", EstadoConservacaoBem.BAIXADO.name());
        q.setParameter("alienado", SituacaoEventoBem.ALIENADO.name());
        q.setParameter("tipoHoOrc", TipoHierarquiaOrganizacional.ORCAMENTARIA.name());
        q.setParameter("tipoHoAdm", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(selecionado.getDataSolicitacao()));
        if (filtro.getHierarquiaOrcamentaria() != null && filtro.getHierarquiaOrcamentaria().getSubordinada() != null) {
            q.setParameter("idUnidadeOrc", filtro.getHierarquiaOrcamentaria().getSubordinada().getId());
        }
        if (filtro.getGrupoBem() != null) {
            q.setParameter("idGrupoBem", filtro.getGrupoBem().getId());
        }
        if (filtro.getBem() != null) {
            q.setParameter("idBem", filtro.getBem().getId());
        }
        List<BemVo> bensPesquisados = preencherListaDeBensApartirArrayObject(q.getResultList(), assistente);
        List<BemVo> bensDisponiveis = configMovimentacaoBemFacade.validarRetornandoBensDisponiveisParaMovimentacaoComDataRetroativa(bensPesquisados, assistente);

        assistente.setBensMovimentadosVo(bensDisponiveis);
        if (assistente.isOperacaoEditar()) {
            assistente.setBensMovimentadosVo(pesquisarBensSolicitacao((SolicitacaoAjusteBemMovel) assistente.getSelecionado(), assistente));
        }
        configMovimentacaoBemFacade.verificarBensMoveisEmBloqueioParaMovimentacao(filtro, assistente);
        return new AsyncResult<List<BemVo>>(bensDisponiveis);
    }

    public List<BemVo> pesquisarBensSolicitacao(SolicitacaoAjusteBemMovel entity, AssistenteMovimentacaoBens assistente) {
        String sql = " " +
            " select distinct " +
            "    bem.id as idBem, " +
            "    bem.identificacao, " +
            "    bem.descricao, " +
            "    ev.id as id_evento, " +
            "    ev.situacaoeventobem as situacao_evento, " +
            "    vworc.codigo || ' - ' || vworc.descricao as unidade,  " +
            "    est.id as id_estado_resultante, " +
            "  (select tr.id " +
            "    from tiporeducao tr " +
            "    where tr.grupobem_id = est.grupobem_id " +
            "    and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(tr.iniciovigencia) and coalesce(trunc(tr.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "    and rownum = 1 " +
            "  ) tipo_reducao," +
            " item.valorajuste as valor_ajuste " +
            " from itemsolicitacaoajustemovel item " +
            "   inner join eventobem ev on ev.id = item.id " +
            "   inner join estadobem est on est.id = ev.estadoresultante_id " +
            "   inner join bem on bem.id = ev.bem_id " +
            "   inner join vwhierarquiaorcamentaria vworc on vworc.subordinada_id = est.detentoraorcamentaria_id " +
            "    and to_date(:dataOperacao, 'dd/MM/yyyy') between vworc.iniciovigencia and coalesce(vworc.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            " where item.solicitacaoajustebemmovel_id = :idSolicitacao " +
            " order by to_number(bem.identificacao) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacao", entity.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(entity.getDataSolicitacao()));
        List<BemVo> bensPesquiados = preencherListaDeBensApartirArrayObject(q.getResultList(), assistente);
        return configMovimentacaoBemFacade.validarRetornandoBensDisponiveisParaMovimentacaoComDataRetroativa(bensPesquiados, assistente);
    }

    private List<BemVo> preencherListaDeBensApartirArrayObject(List<Object[]> objetosConsulta, AssistenteMovimentacaoBens assistente) {
        List<BemVo> bensVo = new ArrayList<>();
        SolicitacaoAjusteBemMovel entity = (SolicitacaoAjusteBemMovel) assistente.getSelecionado();

        assistente.zerarContadoresProcesso();
        assistente.setTotal(objetosConsulta.size());

        for (Object[] obj : objetosConsulta) {
            BemVo bemVo = new BemVo();
            Bem bem = bemFacade.recuperarSemDependencias(((BigDecimal) obj[0]).longValue());
            bemVo.setIdentificacao((String) obj[1]);
            bemVo.setDescricao((String) obj[2]);
            bemVo.setIdEventoBem(((BigDecimal) obj[3]).longValue());
            bemVo.setSituacaoEventoBem(SituacaoEventoBem.valueOf((String) obj[4]));
            bemVo.setUnidadeOrcamentaria((String) obj[5]);
            bemVo.setEstadoResultante(bemFacade.recuperarEstadoBemPorId(((BigDecimal) obj[6]).longValue()));
            if (obj[7] != null) {
                bemVo.setTipoReducao(em.find(TipoReducao.class, ((BigDecimal) obj[7]).longValue()));
            }
            bem.setUltimoEstado(bemVo.getEstadoResultante());
            bem.setValorOriginal(bemVo.getEstadoResultante().getValorOriginal());
            bem.setValorAcumuladoDaDepreciacao(bemVo.getEstadoResultante().getValorAcumuladoDaDepreciacao());
            bem.setValorAcumuladoDaAmortizacao(bemVo.getEstadoResultante().getValorAcumuladoDaAmortizacao());
            bem.setValorLiquido(bemVo.getEstadoResultante().getValorLiquido());
            bemVo.setBem(bem);
            bemVo.setValorAjusteInicial(bemVo.getValorAjusteInicialPorTipoAjuste(entity.getTipoAjusteBemMovel(), false));
            bemVo.setValorAjuste((BigDecimal) obj[8]);
            bemVo.setValorAjusteFinal(bemVo.calcularValorAjusteFinal(entity.getOperacaoAjusteBemMovel()));
            bemVo.setUltimoEstado(bemFacade.recuperarUltimoEstadoDoBem(bem));
            bensVo.add(bemVo);
            assistente.conta();
        }
        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("");
        return bensVo;
    }

    public List<Number> buscarIdsItemSolicitacao(SolicitacaoAjusteBemMovel entity) {
        String sql = "" +
            " select " +
            "  item.id " +
            " from itemsolicitacaoajustemovel item " +
            "  inner join eventobem ev on ev.id = item.id " +
            " where item.solicitacaoajustebemmovel_id = :idSolicitacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacao", entity.getId());
        return ((List<Number>) q.getResultList());
    }

    private List<Number> buscarIdsEstadoResultante(SolicitacaoAjusteBemMovel entity) {
        String sql = "" +
            " select " +
            "  ev.estadoresultante_id " +
            "   from itemsolicitacaoajustemovel item " +
            "   inner join eventobem ev on ev.id = item.id " +
            "   where item.solicitacaoajustebemmovel_id = :idSolicitacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacao", entity.getId());
        return ((List<Number>) q.getResultList());
    }

    public List<SolicitacaoAjusteBemMovel> buscarSolicitacaoPorSituacao(String parte, SituacaoMovimentoAdministrativo situacao, TipoAjusteBemMovel tipoAjusteBemMovel) {
        String sql = " " +
            "  select " +
            "    sol.* " +
            "   from solicitacaoajustebemmovel sol " +
            "  where sol.situacao = :situacaoSol " +
            "    and sol.tipoajustebemmovel = :tipoAjuste" +
            "    and (sol.codigo like :parte or upper(sol.descricao) like :parte)" +
            "    order by sol.codigo desc";
        Query q = em.createNativeQuery(sql, SolicitacaoAjusteBemMovel.class);
        q.setParameter("parte", "%" + parte.trim().toUpperCase() + "%");
        q.setParameter("situacaoSol", situacao.name());
        q.setParameter("tipoAjuste", tipoAjusteBemMovel.name());
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public List<Number> buscarIdsBemPorSolicitacaoAjuste(SolicitacaoAjusteBemMovel entity) {
        String sql = "" +
            " select " +
            "   ev.bem_id " +
            " from itemsolicitacaoajustemovel item " +
            "   inner join eventobem ev on ev.id = item.id " +
            " where item.solicitacaoajustebemmovel_id = :isSolicitacao";
        Query q = em.createNativeQuery(sql);
        q.setParameter("isSolicitacao", entity.getId());
        return ((List<Number>) q.getResultList());
    }

    public ConfigMovimentacaoBem recuperarConfiguracaoMovimentacaoBem(SolicitacaoAjusteBemMovel entity) {
        OperacaoMovimentacaoBem operacaoMovimentacaoBem = null;
        if (entity.getOperacaoAjusteBemMovel() != null) {
            switch (entity.getOperacaoAjusteBemMovel()) {
                case AJUSTE_BEM_MOVEL_ORIGINAL_AUMENTATIVO:
                    operacaoMovimentacaoBem = OperacaoMovimentacaoBem.AJUSTE_BENS_ORIGINAL_AUMENTATIVO;
                    break;

                case AJUSTE_BEM_MOVEL_ORIGINAL_DIMINUTIVO:
                    operacaoMovimentacaoBem = OperacaoMovimentacaoBem.AJUSTE_BENS_ORIGINAL_DIMINUTIVO;
                    break;

                case AJUSTE_BEM_MOVEL_ORIGINAL_AUMENTATIVO_EMPRESA_PUBLICA:
                    operacaoMovimentacaoBem = OperacaoMovimentacaoBem.AJUSTE_BENS_ORIGINAL_AUMENTATIVO_EMPRESA_PUBLICA;
                    break;

                case AJUSTE_BEM_MOVEL_ORIGINAL_DIMINUTIVO_EMPRESA_PUBLICA:
                    operacaoMovimentacaoBem = OperacaoMovimentacaoBem.AJUSTE_BENS_ORIGINAL_DIMINUTIVO_EMPRESA_PULICA;
                    break;

                case AJUSTE_BEM_MOVEL_AMORTIZACAO_AUMENTATIVO:
                    operacaoMovimentacaoBem = OperacaoMovimentacaoBem.AJUSTE_BENS_AMORTIZACAO_AUMENTATIVO;
                    break;

                case AJUSTE_BEM_MOVEL_AMORTIZACAO_DIMINUTIVO:
                    operacaoMovimentacaoBem = OperacaoMovimentacaoBem.AJUSTE_BENS_AMORTIZACAO_DIMINUTIVO;
                    break;

                case AJUSTE_BEM_MOVEL_DEPRECIACAO_AUMENTATIVO:
                    operacaoMovimentacaoBem = OperacaoMovimentacaoBem.AJUSTE_BENS_DEPRECIACAO_AUMENTATIVO;
                    break;

                case AJUSTE_BEM_MOVEL_DEPRECIACAO_DIMINUTIVO:
                    operacaoMovimentacaoBem = OperacaoMovimentacaoBem.AJUSTE_BENS_DEPRECIACAO_DIMINUTIVO;
                    break;
            }
        }
        return configMovimentacaoBemFacade.buscarConfiguracaoMovimentacaoBem(entity.getDataSolicitacao(), operacaoMovimentacaoBem);
    }

    public ConfigMovimentacaoBemFacade getConfigMovimentacaoBemFacade() {
        return configMovimentacaoBemFacade;
    }

    public SingletonConcorrenciaPatrimonio getSingletonBloqueioPatrimonio() {
        return singletonBloqueioPatrimonio;
    }

    public ReducaoValorBemFacade getReducaoValorBemFacade() {
        return reducaoValorBemFacade;
    }
}
