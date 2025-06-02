package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.VOItensAquisicao;
import br.com.webpublico.enums.SituacaoAquisicao;
import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.SituacaoRequisicaoCompra;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigoPatrimonio;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class EfetivacaoAquisicaoFacade extends AbstractFacade<Aquisicao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private SingletonGeradorCodigoPatrimonio singletonGeradorCodigoPatrimonio;
    @EJB
    private EntidadeFacade entidadeFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private RequisicaoDeCompraFacade requisicaoDeCompraFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SolicitacaoAquisicaoFacade solicitacaoAquisicaoFacade;
    @EJB
    private ParametroPatrimonioFacade parametroPatrimonioFacade;
    @EJB
    private GrupoObjetoCompraGrupoBemFacade grupoObjetoCompraGrupoBemFacade;
    @EJB
    private ObjetoCompraFacade objetoCompraFacade;
    @EJB
    private ConfigMovimentacaoBemFacade configMovimentacaoBemFacade;
    @EJB
    private ContratoFacade contratoFacade;

    public EfetivacaoAquisicaoFacade() {
        super(Aquisicao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<Aquisicao> efetivarAquisicao(Aquisicao entity, AssistenteMovimentacaoBens assistente, List<ItemSolicitacaoAquisicao> itensSolicitacao) {
        try {
            assistente.configurarInicializacao("Inicializando Processo...", 0);
            Boolean isSolicitacaoAceita = Aquisicao.SituacaoEfetivacao.ACEITA.equals(entity.getSituacaoSolicitacao());

            entity = movimentarAquisicao(entity, isSolicitacaoAceita);
            assistente.setSelecionado(entity);

            assistente.configurarInicializacao("Gerando Bens da Efetivação de Aquisição...", itensSolicitacao.size());
            for (ItemSolicitacaoAquisicao itemSolicitacao : itensSolicitacao) {
                ItemAquisicao itemAquisicao = criarItensDeAquisicao(itemSolicitacao, entity, isSolicitacaoAceita);
                entity.getItensAquisicao().add(itemAquisicao);
                assistente.conta();
            }

            if (isSolicitacaoAceita) {
                gerarRegistroPatrimonial(entity, assistente);
            }
            assistente.zerarContadoresProcesso();
            assistente.setDescricaoProcesso("Salvando Processo...");

            movimentarSolicitacaoAquisicao(entity, isSolicitacaoAceita);
            movimentarRequisicaoCompra(entity.getSolicitacaoAquisicao().getRequisicaoDeCompra(), isSolicitacaoAceita);
            assistente.setSelecionado(entity);
        } catch (Exception ex) {
            logger.error("Erro ao salvar aquisição de bens móveis. " + ex.getMessage());
        }
        return new AsyncResult<>(entity);
    }

    private Aquisicao movimentarAquisicao(Aquisicao entity, Boolean isSolicitacaoAceita) {
        entity.setSituacao(isSolicitacaoAceita ? SituacaoAquisicao.FINALIZADO : SituacaoAquisicao.RECUSADO);
        if (entity.getNumero() == null) {
            entity.setNumero(singletonGeradorCodigo.getProximoCodigo(Aquisicao.class, "numero"));
        }
        entity = em.merge(entity);
        return entity;
    }

    private void gerarRegistroPatrimonial(Aquisicao aquisicao, AssistenteMovimentacaoBens assistente) throws Exception {
        Entidade entidade;
        try {
            assistente.zerarContadoresProcesso();
            assistente.setDescricaoProcesso("Gerando Registro Patrimonial para os Bens...");
            assistente.setTotal(aquisicao.getItensAquisicao().size());

            RequisicaoDeCompra requisicaoCompra = aquisicao.getSolicitacaoAquisicao().getRequisicaoDeCompra();
            UnidadeOrganizacional unidade = requisicaoDeCompraFacade.getUnidadeAdministrativa(requisicaoCompra, assistente.getDataLancamento());
            entidade = entidadeFacade.recuperarEntidadePorUnidadeOrganizacional(unidade);
            ParametroPatrimonio parametroPatrimonio = parametroPatrimonioFacade.recuperarParametroComDependenciasEntidadeGeradoCodigo();
            singletonGeradorCodigoPatrimonio.inicializarReset();

            for (ItemAquisicao itemAquisicao : aquisicao.getItensAquisicao()) {
                String codigoPatrimonio = singletonGeradorCodigoPatrimonio.getProximoCodigoAquisicao(entidade, aquisicao.getTipoBem(), parametroPatrimonio).toString();
                itemAquisicao.getBem().setIdentificacao(codigoPatrimonio);
                em.merge(itemAquisicao.getBem());

                itemAquisicao.getEstadoResultante().setIdentificacao(codigoPatrimonio);
                em.merge(itemAquisicao.getEstadoResultante());
                assistente.conta();
            }
        } catch (ExcecaoNegocioGenerica exg) {
            singletonGeradorCodigoPatrimonio.reset();
            throw new ExcecaoNegocioGenerica(exg.getMessage());
        } catch (Exception ex) {
            singletonGeradorCodigoPatrimonio.reset();
            throw new Exception(ex.getMessage());
        } finally {
            singletonGeradorCodigoPatrimonio.finalizarReset();
        }
    }

    private ItemAquisicao criarItensDeAquisicao(ItemSolicitacaoAquisicao itemSol, Aquisicao entity, Boolean isSolicitacaoAceita) {
        ItemAquisicao itemAquisicao = new ItemAquisicao();
        itemAquisicao.setEstadoInicial(itemSol.getEstadoResultante());
        itemAquisicao.setEstadoResultante(itemSol.getEstadoResultante());
        Date dataLancamento = DataUtil.getDataComHoraAtual(entity.getDataDeAquisicao());
        itemAquisicao.setDataLancamento(dataLancamento);
        itemAquisicao.setSituacaoEventoBem(isSolicitacaoAceita ? SituacaoEventoBem.AGUARDANDO_LIQUIDACAO : SituacaoEventoBem.RECUSADO);
        itemAquisicao.setAquisicao(entity);
        itemAquisicao.setDetentorArquivoComposicao(itemSol.getDetentorArquivoComposicao());

        if (isSolicitacaoAceita) {
            Bem bem = new Bem(itemSol);
            bem.setFornecedor(requisicaoDeCompraFacade.getFornecedorRequisicao(entity.getSolicitacaoAquisicao().getRequisicaoDeCompra()));
            bem.setDataAquisicao(entity.getDataDeAquisicao());
            bem.setGarantia(itemSol.getItemDoctoItemAquisicao().getGarantia());
            bem.setSeguradora(itemSol.getItemDoctoItemAquisicao().getSeguradora());
            bem = em.merge(bem);

            itemAquisicao.setBem(bem);
            itemSol.setBem(bem);

            EstadoBem estadoResultante = bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(itemAquisicao.getEstadoInicial());
            estadoResultante.setValorOriginal(itemSol.getValorDoLancamento());
            estadoResultante = em.merge(estadoResultante);

            itemAquisicao.setEstadoResultante(estadoResultante);
            itemAquisicao.setValorDoLancamento(estadoResultante.getValorOriginal());
            configMovimentacaoBemFacade.bloquearBemAquisicao(bem);
        }
        itemSol.setSituacaoEventoBem(isSolicitacaoAceita ? SituacaoEventoBem.FINALIZADO : SituacaoEventoBem.RECUSADO);
        itemSol = em.merge(itemSol);
        itemAquisicao.setItemSolicitacaoAquisicao(itemSol);
        return em.merge(itemAquisicao);
    }

    private void movimentarRequisicaoCompra(RequisicaoDeCompra requisicaoDeCompra, Boolean solicitacaoAceita) {
        SituacaoRequisicaoCompra situacao = solicitacaoAceita ? SituacaoRequisicaoCompra.EFETIVADA : SituacaoRequisicaoCompra.RECUSADA;
        requisicaoDeCompraFacade.movimentarSituacaoRequisicaoCompra(requisicaoDeCompra, situacao);
    }

    private void movimentarSolicitacaoAquisicao(Aquisicao entity, Boolean isSolicitacaoAceita) {
        SolicitacaoAquisicao solicitacaoAquisicao = entity.getSolicitacaoAquisicao();
        solicitacaoAquisicao.setSituacao(isSolicitacaoAceita ? SituacaoAquisicao.FINALIZADO : SituacaoAquisicao.RECUSADO);
        solicitacaoAquisicao.setMotivo(entity.getSolicitacaoAquisicao().getMotivo());
        em.merge(solicitacaoAquisicao);
    }

    @Override
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public void remover(Aquisicao entity) {
        super.remover(entity);
    }

    @Override
    public Aquisicao recuperar(Object id) {
        Aquisicao aquisicao = super.recuperar(id);
        Hibernate.initialize(aquisicao.getItensAquisicao());
        if (aquisicao.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(aquisicao.getDetentorArquivoComposicao().getArquivosComposicao());
        }
        return aquisicao;
    }

    public Aquisicao recuperarComDepenciasDocumentosFiscais(Object id) {
        Aquisicao aquisicao = super.recuperar(id);
        if (aquisicao.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(aquisicao.getDetentorArquivoComposicao().getArquivosComposicao());
        }
        return aquisicao;
    }

    public List<Aquisicao> buscarAquisicaoFinalizadaSemLiquidacaoPorTipoBem(String filtro, TipoBem tipoBem) {
        String sql = " select aq.* " +
            "    from aquisicao aq   " +
            "   inner join solicitacaoaquisicao sol on sol.id = aq.solicitacaoaquisicao_id " +
            "   inner join requisicaodecompra req on req.id = sol.requisicaodecompra_id  " +
            "   inner join contrato cont on req.CONTRATO_ID = cont.id " +
            "   inner join unidadecontrato uc on uc.contrato_id = cont.id " +
            " where (aq.numero like :filtro or req.numero like :filtro or lower(req.descricao) like :filtro )  " +
            "   and to_date(:dataOperacao, 'dd/mm/yyyy') between trunc(uc.iniciovigencia) and coalesce(trunc(uc.fimvigencia), to_date(:dataOperacao, 'dd/mm/yyyy')) " +
            "   and aq.situacao = '" + SituacaoEventoBem.FINALIZADO.name() + "' " +
            "   and sol.situacao = '" + SituacaoEventoBem.FINALIZADO.name() + "' " +
            "   and aq.tipobem = :tipoBem " +
            "   and aq.id not in ( " +
            "   select item.aquisicao_id from itemaquisicao item  " +
            "   where exists (select 1 from eventobem ev  " +
            "                    inner join estadobem est on ev.estadoresultante_id = est.id " +
            "                    where ev.situacaoeventobem <> '" + SituacaoEventoBem.AGUARDANDO_LIQUIDACAO.name() + "' " +
            "                    and item.id = ev.id) " +
            "                    ) " +
            "   and exists (select 1 from usuariounidadeorganizacio u_un " +
            "               where u_un.usuariosistema_id = :id_usuario " +
            "                 and u_un.unidadeorganizacional_id = uc.unidadeadministrativa_id " +
            "                 and u_un.gestorPatrimonio = :gestor_patromonio) " +
            " order by aq.numero  ";
        Query q = em.createNativeQuery(sql, Aquisicao.class);
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        q.setParameter("tipoBem", tipoBem.name());
        q.setParameter("id_usuario", sistemaFacade.getUsuarioCorrente().getId());
        q.setParameter("gestor_patromonio", Boolean.TRUE);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public List<Aquisicao> buscarAquisicaoFinalizada(String filtro, TipoBem tipoBem) {
        String sql = " select aq.* " +
            "    from aquisicao aq   " +
            "   inner join solicitacaoaquisicao sol on sol.id = aq.solicitacaoaquisicao_id " +
            "   inner join requisicaodecompra req on req.id = sol.requisicaodecompra_id  " +
            "   inner join contrato cont on req.CONTRATO_ID = cont.id " +
            "   inner join unidadecontrato uc on uc.contrato_id = cont.id " +
            " where (aq.numero like :filtro or req.numero like :filtro or lower(req.descricao) like :filtro )  " +
            "   and to_date(:dataOperacao, 'dd/mm/yyyy') between trunc(uc.iniciovigencia) and coalesce(trunc(uc.fimvigencia), to_date(:dataOperacao, 'dd/mm/yyyy')) " +
            "   and aq.situacao = :finalizada " +
            "   and sol.situacao = :finalizada " +
            "   and aq.tipobem = :tipoBem " +
            "   and exists (select 1 from usuariounidadeorganizacio u_un " +
            "               where u_un.usuariosistema_id = :id_usuario " +
            "                 and u_un.unidadeorganizacional_id = uc.unidadeadministrativa_id " +
            "                 and u_un.gestorPatrimonio = :gestor_patromonio) " +
            " order by aq.numero  ";
        Query q = em.createNativeQuery(sql, Aquisicao.class);
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        q.setParameter("tipoBem", tipoBem.name());
        q.setParameter("finalizada", SituacaoEventoBem.FINALIZADO.name());
        q.setParameter("id_usuario", sistemaFacade.getUsuarioCorrente().getId());
        q.setParameter("gestor_patromonio", Boolean.TRUE);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public List<DoctoFiscalLiquidacao> buscarDocumentoFiscal(Aquisicao aquisicao) {
        String sql = " " +
            " select  " +
            "  doc.*  " +
            "from doctofiscalliquidacao doc " +
            "  inner join doctofiscalsolicaquisicao docSol on docSol.documentofiscal_id = doc.id " +
            "  inner join solicitacaoaquisicao sol on sol.id = docSol.solicitacaoaquisicao_id " +
            "  inner join aquisicao aq on aq.solicitacaoaquisicao_id = sol.id " +
            "where aq.id = :idAquisicao ";
        Query q = em.createNativeQuery(sql, DoctoFiscalLiquidacao.class);
        q.setParameter("idAquisicao", aquisicao.getId());
        return q.getResultList();
    }

    public List<DoctoFiscalSolicitacaoAquisicao> buscarDocumentoFiscalAquisicao(Aquisicao aquisicao) {
        String sql = " " +
            " select  " +
            "  docSol.*  " +
            "from doctofiscalliquidacao doc " +
            "  inner join doctofiscalsolicaquisicao docSol on docSol.documentofiscal_id = doc.id " +
            "  inner join solicitacaoaquisicao sol on sol.id = docSol.solicitacaoaquisicao_id " +
            "  inner join aquisicao aq on aq.solicitacaoaquisicao_id = sol.id " +
            "where aq.id = :idAquisicao ";
        Query q = em.createNativeQuery(sql, DoctoFiscalSolicitacaoAquisicao.class);
        q.setParameter("idAquisicao", aquisicao.getId());
        return q.getResultList();
    }


    public BigDecimal getValorOriginal(Aquisicao aquisicao) {
        String sql = " select " +
            "  coalesce(sum(est.valororiginal), 0) as valorOriginal " +
            "from itemaquisicao item " +
            "  inner join eventobem ev on ev.id = item.id " +
            "  inner join estadobem est on est.id = ev.estadoresultante_id " +
            "where item.aquisicao_id = :idAquisicao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idAquisicao", aquisicao.getId());
        return (BigDecimal) q.getSingleResult();
    }

    public BigDecimal getValorLiquidado(Aquisicao aquisicao) {
        String sql = " select " +
            "  coalesce(sum(est.valorliquidado), 0) as valorOriginal " +
            "from itemaquisicao item " +
            "  inner join eventobem ev on ev.id = item.id " +
            "  inner join estadobem est on est.id = ev.estadoresultante_id " +
            "where item.aquisicao_id = :idAquisicao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idAquisicao", aquisicao.getId());
        return (BigDecimal) q.getSingleResult();
    }

    public BigDecimal getValorLiquidadoPorGrupo(Long idGrupoBem, DoctoFiscalLiquidacao doctoFiscal) {
        String sql = " select " +
            "  coalesce(sum(est.valorliquidado), 0) as valorOriginal " +
            " from itemaquisicao item " +
            "  inner join itemsolicitacaoaquisicao itemsol on itemsol.id = item.itemSolicitacaoAquisicao_id " +
            "  inner join itemdoctoitemaquisicao idia on itemsol.itemDoctoItemAquisicao_id = idia.id " +
            "  inner join itemdoctofiscalliquidacao itemdoc on itemdoc.id = idia.itemDoctoFiscalLiquidacao_id " +
            "  inner join eventobem ev on ev.id = item.id " +
            "  inner join estadobem est on est.id = ev.estadoresultante_id " +
            " where est.grupobem_id = :idGrupo " +
            "  and itemdoc.doctofiscalliquidacao_id = :idDocumento ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idGrupo", idGrupoBem);
        q.setParameter("idDocumento", doctoFiscal.getId());
        return (BigDecimal) q.getSingleResult();
    }

    public BigDecimal getValorLiquidadoPorGrupo(GrupoBem grupoBem, DoctoFiscalLiquidacao doctoFiscal) {
        return getValorLiquidadoPorGrupo(grupoBem.getId(), doctoFiscal);
    }

    public List<BigDecimal> buscarItensEfetivacaoAquisicao(Aquisicao aquisicao) {
        String sql = " " +
            "select " +
            "  to_number(item.id) " +
            "from itemaquisicao item " +
            "where item.aquisicao_id = :idAquisicao";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idAquisicao", aquisicao.getId());
        return q.getResultList();
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<VOItensAquisicao> buscarItensEfetivacao(Aquisicao aquisicao) {
        VOItensAquisicao voItensAquisicao = new VOItensAquisicao();

        List<BigDecimal> idsItemAquisicao = buscarItensEfetivacaoAquisicao(aquisicao);
        List<ItemAquisicao> itensAquisicao = preencherItensAquisicao(idsItemAquisicao, null);
        voItensAquisicao.getItensEfetivacaoAquisicao().addAll(itensAquisicao);

        if (aquisicao.getSolicitacaoAquisicao() != null) {
            List<BigDecimal> idsItemSolicitacao = getSolicitacaoAquisicaoFacade().buscarIdsItemSolicitacaoAquisicao(aquisicao.getSolicitacaoAquisicao());
            List<ItemSolicitacaoAquisicao> itensSolicitacao = getSolicitacaoAquisicaoFacade().preencherItensSolicitacaoAquisicao(idsItemSolicitacao);
            voItensAquisicao.getItensSolicitacaoAquisicao().addAll(itensSolicitacao);
        }
        return new AsyncResult<VOItensAquisicao>(voItensAquisicao);
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<List<ItemAquisicao>> buscarItensAquisicao(Aquisicao aquisicao, AssistenteMovimentacaoBens assistente) {
        List<BigDecimal> idsItemAquisicao = buscarItensEfetivacaoAquisicao(aquisicao);
        List<ItemAquisicao> itensAquisicao = preencherItensAquisicao(idsItemAquisicao, assistente);
        return new AsyncResult<List<ItemAquisicao>>(itensAquisicao);
    }

    private List<ItemAquisicao> preencherItensAquisicao(List<BigDecimal> idsConsulta, AssistenteMovimentacaoBens assistente) {
        List<ItemAquisicao> itens = new ArrayList<>();
        for (BigDecimal obj : idsConsulta) {
            itens.add(em.find(ItemAquisicao.class, obj.longValue()));
        }
        if (assistente != null) {
            itens = validarBensMovimentoRetroativoEstornoAquisicao(itens, assistente);
        }
        return itens;
    }

    private List<ItemAquisicao> validarBensMovimentoRetroativoEstornoAquisicao(List<ItemAquisicao> itensAquisicao, AssistenteMovimentacaoBens assistente) {
        List<ItemAquisicao> bensDisponiveis = Lists.newArrayList();
        for (ItemAquisicao evento : itensAquisicao) {
            if (!configMovimentacaoBemFacade.validarMovimentoRetroativo(assistente, evento.getBem())) {
                bensDisponiveis.add(evento);
            }
        }
        return bensDisponiveis;
    }

    public BemFacade getBemFacade() {
        return bemFacade;
    }

    public EntidadeFacade getEntidadeFacade() {
        return entidadeFacade;
    }


    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public SingletonGeradorCodigo getSingletonGeradorCodigo() {
        return singletonGeradorCodigo;
    }

    public SolicitacaoAquisicaoFacade getSolicitacaoAquisicaoFacade() {
        return solicitacaoAquisicaoFacade;
    }

    public List<Aquisicao> buscarEfetivacaoAquisicaoSemSolicitacao(String parte) {
        String sql = "" +
            " select * from aquisicao " +
            " where solicitacaoaquisicao_id is null " +
            " and numero like :filtro";
        Query q = em.createNativeQuery(sql, Aquisicao.class);
        q.setParameter("filtro", "%" + parte.trim() + "%");
        return q.getResultList();
    }

    public List<Long> buscarIdsBens(Aquisicao aquisicao) {
        String sql = " select ev.bem_id from itemaquisicao item" +
            "          inner join eventobem ev on ev.id = item.id " +
            "          where item.aquisicao_id = :idAquisicao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idAquisicao", aquisicao.getId());
        return q.getResultList();
    }

    public List<Aquisicao> buscarAquisicaoAguardandoLiquidacao(String filtro) {
        String sql = " select distinct aq.* from aquisicao aq   " +
            "   inner join solicitacaoaquisicao sol on sol.id = aq.solicitacaoaquisicao_id " +
            "   inner join requisicaodecompra req on req.id = sol.requisicaodecompra_id " +
            "   inner join doctofiscalsolicaquisicao dsol on dsol.solicitacaoaquisicao_id = sol.id " +
            "   inner join liquidacaodoctofiscal ldf on ldf.doctofiscalliquidacao_id = dsol.documentofiscal_id " +
            " where (aq.numero like :filtro or req.numero like :filtro or lower(req.descricao) like :filtro )  " +
            "   and aq.situacao = :aquisicao_finalizada " +
            "   and sol.situacao = :aquisicao_finalizada " +
            "   and aq.id in ( " +
            "               select item.aquisicao_id from itemaquisicao item  " +
            "               where exists (select 1 from eventobem ev  " +
            "                    inner join estadobem est on ev.estadoresultante_id = est.id " +
            "                    where ev.situacaoeventobem = :aguardando_liquidacao " +
            "                    and item.id = ev.id) " +
            "                    ) " +
            " order by aq.numero ";
        Query q = em.createNativeQuery(sql, Aquisicao.class);
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        q.setParameter("aguardando_liquidacao", SituacaoEventoBem.AGUARDANDO_LIQUIDACAO.name());
        q.setParameter("aquisicao_finalizada", SituacaoEventoBem.FINALIZADO.name());
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public GrupoObjetoCompraGrupoBemFacade getGrupoObjetoCompraGrupoBemFacade() {
        return grupoObjetoCompraGrupoBemFacade;
    }

    public ConfigMovimentacaoBemFacade getConfigMovimentacaoBemFacade() {
        return configMovimentacaoBemFacade;
    }

    public ContratoFacade getContratoFacade() {
        return contratoFacade;
    }

    public ObjetoCompraFacade getObjetoCompraFacade() {
        return objetoCompraFacade;
    }

    public RequisicaoDeCompraFacade getRequisicaoDeCompraFacade() {
        return requisicaoDeCompraFacade;
    }
}
