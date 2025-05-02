package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroPesquisaBem;
import br.com.webpublico.entidadesauxiliares.VOItemSolicitacaoAlienacao;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.negocios.tributario.singletons.SingletonConcorrenciaPatrimonio;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
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
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 04/11/14
 * Time: 14:01
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class SolicitacaoAlienacaoFacade extends AbstractFacade<SolicitacaoAlienacao> {

    @PersistenceContext(name = "webpublicoPU")
    private EntityManager em;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private InventarioBensMoveisFacade inventarioBensMoveisFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ParametroPatrimonioFacade parametroPatrimonioFacade;
    @EJB
    private ConfigMovimentacaoBemFacade configMovimentacaoBemFacade;
    @EJB
    private SingletonConcorrenciaPatrimonio singletonBloqueioPatrimonio;

    public SolicitacaoAlienacaoFacade() {
        super(SolicitacaoAlienacao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public BemFacade getBemFacade() {
        return bemFacade;
    }

    @Override
    public SolicitacaoAlienacao recuperar(Object id) {
        SolicitacaoAlienacao solicitacaoAlienacao = super.recuperar(id);
        Hibernate.initialize(solicitacaoAlienacao.getItensLoteSolicitacaoAlienacao());
        if (solicitacaoAlienacao.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(solicitacaoAlienacao.getDetentorArquivoComposicao().getArquivosComposicao());
        }
        return solicitacaoAlienacao;
    }

    public SolicitacaoAlienacao recuperarComDependenciaArquivoComposicao(Object id) {
        SolicitacaoAlienacao solicitacaoAlienacao = super.recuperar(id);
        if (solicitacaoAlienacao.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(solicitacaoAlienacao.getDetentorArquivoComposicao().getArquivosComposicao());
        }
        return solicitacaoAlienacao;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public  SolicitacaoAlienacao salvarRetornando(SolicitacaoAlienacao entity, List<Bem> bensSelecionados, AssistenteMovimentacaoBens assistente) {
        try {
            entity = salvarSolicitacaoControleVersao(entity);
            processarBensRemovidos(entity, assistente.getConfigMovimentacaoBem());
            movimentarItemSolicitacaoAlienacao(entity, assistente, bensSelecionados);

            assistente.zerarContadoresProcesso();
            assistente.setDescricaoProcesso("Finalizando Processo...");
            assistente.setSelecionado(entity);
            assistente.setDescricaoProcesso("");
        } catch (Exception ex) {
            logger.error("Erro ao salvar solicitação de alienação de bens móveis. " + ex.getMessage());
            throw ex;
        }
        return entity;
    }

    public SolicitacaoAlienacao salvarSolicitacaoControleVersao(SolicitacaoAlienacao entity) {
        entity.setDataVersao(new Date());
        entity = em.merge(entity);
        return entity;
    }


    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public SolicitacaoAlienacao concluirSolicitacaoAlienacao(SolicitacaoAlienacao entity, AssistenteMovimentacaoBens assistente, List<Number> idsItemSolicitacao) {
        try {
            if (entity.getCodigo() == null) {
                entity.setCodigo(singletonGeradorCodigo.getProximoCodigo(SolicitacaoAlienacao.class, "codigo"));
            }
            entity.setSituacao(SituacaoAlienacao.AGUARDANDO_APROVACAO);
            entity = em.merge(entity);

            concluirItensSolicitacao(assistente, idsItemSolicitacao);

            assistente.zerarContadoresProcesso();
            assistente.setSelecionado(entity);
            assistente.setDescricaoProcesso(" ");
        } catch (Exception ex) {
            logger.error("Erro ao concluir solicitação de alienação de bens móveis. " + ex.getMessage());
            throw ex;
        }
        return entity;
    }

    private void concluirItensSolicitacao(AssistenteMovimentacaoBens assistente, List<Number> idsItemSolicitacao) {
        assistente.zerarContadoresProcesso();
        assistente.setTotal(idsItemSolicitacao.size());
        assistente.setDescricaoProcesso("Concluíndo Itens da Solicitação de Alienação...");
        for (Number idItem : idsItemSolicitacao) {
            concluirItemSolicitacao(idItem, SituacaoEventoBem.AGUARDANDO_EFETIVACAO);
            assistente.conta();
        }
        List<Number> idsBem = buscarIdsBemPorSolicitacaoAlienacao((SolicitacaoAlienacao) assistente.getSelecionado());
        configMovimentacaoBemFacade.alterarSituacaoEvento(idsBem, SituacaoEventoBem.AGUARDANDO_EFETIVACAO, OperacaoMovimentacaoBem.SOLICITACAO_ALIENACAO_BEM);
    }

    private void processarBensRemovidos(SolicitacaoAlienacao entity, ConfigMovimentacaoBem configMovimentacaoBem) {
        if (entity.getId() != null) {
            List<Number> idsItemSolicitacao = buscarIdsItemSolicitacao(entity);
            List<Number> bensRecuperados = buscarIdsBemPorSolicitacaoAlienacao(entity);
            configMovimentacaoBemFacade.desbloquearBens(configMovimentacaoBem, bensRecuperados);

            for (Number id : idsItemSolicitacao) {
                Query deleteItem = em.createNativeQuery(" delete from ItemSolicitacaoAlienacao where id = " + id.longValue());
                deleteItem.executeUpdate();

                Query deleteEvento = em.createNativeQuery(" delete from eventobem where id = " + id.longValue());
                deleteEvento.executeUpdate();
            }
        }
    }

    private void movimentarItemSolicitacaoAlienacao(SolicitacaoAlienacao selecionado, AssistenteMovimentacaoBens assistente, List<Bem> bens) {
        assistente.zerarContadoresProcesso();
        assistente.setTotal(bens.size());
        assistente.setDescricaoProcesso("Criando Itens da Solicitação de Alienação de Bens Móveis...");
        List<Number> bensBloqueio = Lists.newArrayList();
        for (Bem bem : bens) {
            ItemSolicitacaoAlienacao itemSolicitacao = criarItemSolicitacaoAlienacao(selecionado, bem, assistente);
            em.merge(itemSolicitacao);
            bensBloqueio.add(bem.getId());
            assistente.conta();
        }
        configMovimentacaoBemFacade.bloquearBens(assistente.getConfigMovimentacaoBem(), bensBloqueio);
    }

    public void concluirItemSolicitacao(Number idItemSolicitacao, SituacaoEventoBem situacaoEventoBem) {
        String sql = " " +
            "   update eventobem ev set ev.situacaoeventobem = :situacao " +
            "   where ev.id = :idItemSol " +
            "   and ev.tipoeventobem = '" + TipoEventoBem.ITEMLOTESOLICITACAOALIENACAO.name() + "'";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItemSol", idItemSolicitacao.longValue());
        q.setParameter("situacao", situacaoEventoBem.name());
        q.executeUpdate();
    }


    public void concluirSolicitacaoAlienacao(SolicitacaoAlienacao solicitacaoAlienacao, SituacaoAlienacao situacaoAlienacao) {
        String sql = " update solicitacaoAlienacao sol set sol.situacao = :situacao where sol.id = :idSolicitacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacao", solicitacaoAlienacao.getId());
        q.setParameter("situacao", situacaoAlienacao.name());
        q.executeUpdate();
    }


    public LoteAvaliacaoAlienacao recuperarLoteSolicitacaoAlienacao(Object id) {
        LoteAvaliacaoAlienacao loteAvaliacaoAlienacao = getEntityManager().find(LoteAvaliacaoAlienacao.class, id);
        loteAvaliacaoAlienacao.getItensSolicitacaoAlienacao().size();
        return loteAvaliacaoAlienacao;
    }

    private ItemSolicitacaoAlienacao criarItemSolicitacaoAlienacao(SolicitacaoAlienacao selecionado, Bem bem, AssistenteMovimentacaoBens assistente) {
        Date dataLancamento = DataUtil.getDataComHoraAtual(assistente.getDataLancamento());
        ItemSolicitacaoAlienacao novoItem = new ItemSolicitacaoAlienacao();
        novoItem.setSituacaoEventoBem(SituacaoEventoBem.EM_ELABORACAO);
        novoItem.setSolicitacaoAlienacao(selecionado);
        novoItem.setDataLancamento(dataLancamento);
        novoItem.setValorAvaliado(BigDecimal.ZERO);
        novoItem.setBem(bem);
        novoItem.setDetentorArquivoComposicao(selecionado.getDetentorArquivoComposicao());
        EstadoBem ultimoEstado = bemFacade.recuperarUltimoEstadoDoBem(bem);
        EstadoBem estadoResultante = em.merge(bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(ultimoEstado));
        novoItem.setEstadoInicial(ultimoEstado);
        novoItem.setEstadoResultante(estadoResultante);
        return novoItem;
    }

    public void verificarBensDisponiveisParaMovimentacao(AssistenteMovimentacaoBens assistente, List<VOItemSolicitacaoAlienacao> toReturn) {
        if (assistente.getConfigMovimentacaoBem().getValidarMovimentoRetroativo()) {
            for (VOItemSolicitacaoAlienacao item : toReturn) {
                String mensagem = configMovimentacaoBemFacade.validarMovimentoComDataRetroativaBem(item.getIdBem(), assistente.getConfigMovimentacaoBem().getOperacaoMovimentacaoBem().getDescricao(), assistente.getDataLancamento());
                if (!mensagem.isEmpty()) {
                    assistente.getMensagens().add(mensagem);
                }
            }
        }
    }

    public List<SolicitacaoAlienacao> buscarSolicitacoesAlienacaoPorSituacao(String parte, SituacaoAlienacao situacao) {
        String sql = " select sol.* " +
            "   from solicitacaoalienacao sol " +
            " where  (to_char(sol.codigo)  like :parte " +
            "     or (lower(sol.descricao) like :parte)" +
            "     or (to_char(sol.dataSolicitacao, 'dd/MM/yyyy') like :parte ))" +
            "   and sol.situacao = :situacao " +
            "   and not exists (select 1 " +
            "                     from AprovacaoAlienacao ap" +
            "                   where ap.solicitacaoAlienacao_id = sol.id  and ap.situacaoEfetivacao = :aprovada ) " +
            "   and exists (select 1 " +
            "                     from ItemSolicitacaoAlienacao isa" +
            "                   where isa.solicitacaoAlienacao_id = sol.id)" +
            " order by sol.codigo desc ";
        Query q = em.createNativeQuery(sql, SolicitacaoAlienacao.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("situacao", situacao.name());
        q.setParameter("aprovada", SituacaoAlienacao.APROVADA.name());
        return q.getResultList();
    }

    private List<Number> buscarIdsEstadoResultante(SolicitacaoAlienacao entity) {
        String sql = "" +
            " select " +
            "  ev.estadoresultante_id " +
            "from itemsolicitacaoalienacao item " +
            "inner join eventobem ev on ev.id = item.id " +
            "where item.solicitacaoalienacao_id = :idSolicitacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacao", entity.getId());
        return ((List<Number>) q.getResultList());
    }

    public List<Number> buscarIdsItemSolicitacao(SolicitacaoAlienacao entity) {
        String sql = "" +
            " select " +
            "  item.id " +
            " from itemsolicitacaoalienacao item " +
            " where item.solicitacaoalienacao_id = :idSolicitacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacao", entity.getId());
        return ((List<Number>) q.getResultList());
    }

    public List<Number> buscarIdsItemSolicitacaoPorLote(LoteAvaliacaoAlienacao loteAvaliacaoAlienacao) {
        String sql = "" +
            " select " +
            "  item.id " +
            " from itemsolicitacaoalienacao item " +
            "  inner join eventobem ev on ev.id = item.id " +
            " where item.loteavaliacaoalienacao_id = :idLote ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idLote", loteAvaliacaoAlienacao.getId());
        return ((List<Number>) q.getResultList());
    }

    public List<SolicitacaoAlienacao> buscarSolicitacaoPorAvaliacaoAlienacao(AvaliacaoAlienacao avaliacaoAlienacao) {
        String sql = " " +
            "select " +
            "  distinct sol.* " +
            "from solicitacaoalienacao sol " +
            "  inner join itemsolicitacaoalienacao item on item.solicitacaoalienacao_id = sol.id " +
            "  inner join loteavaliacaoalienacao lote on lote.id = item.loteavaliacaoalienacao_id " +
            "  inner join avaliacaoalienacao av on av.id = lote.avaliacaoalienacao_id " +
            "where av.id = :idAvaliacao ";
        Query q = em.createNativeQuery(sql, SolicitacaoAlienacao.class);
        q.setParameter("idAvaliacao", avaliacaoAlienacao.getId());
        return q.getResultList();
    }

    public String recuperarFonteRecurso(Long idBem) {
        String sql = "" +
            " select " +
            "   org.tipoRecursoAquisicaoBem " +
            " from detentororigemrecurso det " +
            "   inner join origemrecursobem org on org.detentororigemrecurso_id = det.id " +
            "   inner join bem on bem.detentororigemrecurso_id = det.id " +
            " where bem.id = :idBem ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idBem", idBem);
        String fonteRecurso = "";
        if (!q.getResultList().isEmpty()) {
            for (String obj : (List<String>) q.getResultList()) {
                String tipoFonte = obj != null ? TipoRecursoAquisicaoBem.valueOf(obj).getDescricao() : " ";
                if (fonteRecurso.isEmpty()) {
                    fonteRecurso = tipoFonte;
                } else {
                    fonteRecurso += ", " + tipoFonte;
                }
            }
        }
        return fonteRecurso;
    }

    public BigDecimal getValorOriginalEstadoResultante(SolicitacaoAlienacao solicitacaoAlienacao) {
        String sql = " select " +
            "  coalesce(sum(est.valororiginal), 0) as valorOriginal " +
            "from itemsolicitacaoalienacao item " +
            "  inner join eventobem ev on ev.id = item.id " +
            "  inner join estadobem est on est.id = ev.estadoresultante_id " +
            "where item.solicitacaoalienacao_id = :idSolicitacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacao", solicitacaoAlienacao.getId());
        return (BigDecimal) q.getSingleResult();
    }

    public BigDecimal getValorAjusteEstadoResultante(SolicitacaoAlienacao solicitacaoAlienacao) {
        String sql = " select " +
            "  coalesce(sum(est.valoracumuladodaamortizacao), 0) + " +
            "  coalesce(sum(est.valoracumuladodadepreciacao), 0) + " +
            "  coalesce(sum(est.valoracumuladodeajuste), 0) as valorAjuste " +
            "from itemsolicitacaoalienacao item " +
            "  inner join eventobem ev on ev.id = item.id " +
            "  inner join estadobem est on est.id = ev.estadoresultante_id " +
            "where item.solicitacaoalienacao_id = :idSolicitacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacao", solicitacaoAlienacao.getId());
        return (BigDecimal) q.getSingleResult();
    }

    public SolicitacaoAlienacao buscarSolicitacaoAlienacaoPorBem(Bem bem) {
        String sql = " " +
            " select  " +
            "    sol.* " +
            " from solicitacaoalienacao sol" +
            "    inner join itemsolicitacaoalienacao item on item.solicitacaoalienacao_id = sol.id" +
            "    inner join eventobem ev on item.id = ev.id" +
            "    inner join bem on bem.id = ev.bem_id" +
            " where bem.id = :idBem " +
            "    and sol.situacao <> '" + SituacaoAlienacao.REJEITADA.name() + "' ";
        Query q = em.createNativeQuery(sql, SolicitacaoAlienacao.class);
        q.setParameter("idBem", bem.getId());
        q.setMaxResults(1);
        try {
            return (SolicitacaoAlienacao) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public List<Bem> pesquisarBens(FiltroPesquisaBem filtro, AssistenteMovimentacaoBens assistente) {
        assistente.setDescricaoProcesso("Consultando bens da unidade: " + filtro.getHierarquiaAdministrativa());
        ConfigMovimentacaoBem configuracao = assistente.getConfigMovimentacaoBem();

        StringBuilder sql = new StringBuilder();
        sql.append(" select distinct bem.id, ")
            .append(" estb.id as estado, ")
            .append("  hoorc.codigo || ' - '|| hoorc.descricao as unidade ")
            .append("from eventobem eb ")
            .append("  inner join estadobem estb on estb.id = eb.estadoresultante_id ")
            .append("  inner join hierarquiaorganizacional hoorc on estb.detentoraorcamentaria_id = hoorc.subordinada_id ")
            .append("   and hoorc.tipohierarquiaorganizacional = :tipoHierarquiaOrc ")
            .append("  inner join hierarquiaorganizacional hoadm on estb.detentoraadministrativa_id = hoadm.subordinada_id ")
            .append("   and hoadm.tipohierarquiaorganizacional = :tipoHierarquiaAdm ")
            .append("  inner join grupobem gb on estb.grupobem_id = gb.id ")
            .append("  inner join bem on bem.id = eb.bem_id ")
            .append("where estb.estadobem = :estadoConservacao ")
            .append("  and hoadm.codigo like :unidadeAdm ")
            .append("  and gb.tipobem = :tipoBem ")
            .append("  and to_date(:dataOperacao, 'dd/MM/yyyy') between hoorc.iniciovigencia and coalesce(hoorc.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) ")
            .append("  and to_date(:dataOperacao, 'dd/MM/yyyy') between hoadm.iniciovigencia and coalesce(hoadm.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) ")
            .append("  and eb.dataoperacao = (select max(eb2.dataoperacao) from eventobem eb2 where eb2.bem_id = bem.id) ");
        if (!filtro.getIdsUnidadesControleSetorial().isEmpty()) {
            sql.append(" and not exists (select 1 from unidadeorganizacional unid ")
                .append("                inner join estadobem est on est.detentoraadministrativa_id = unid.id ")
                .append("                inner join eventobem ev on  coalesce(ev.estadoinicial_id, ev.estadoresultante_id) = est.id ")
                .append("                where upper(unid.id) in (:unidades) ")
                .append("                and ev.bem_id = bem.id) ");
        }
        sql.append(FiltroPesquisaBem.getCondicaoExistsBloqueioPesquisaBem(configuracao));
        sql.append(filtro.getHierarquiaOrcamentaria() != null ? " and hoorc.subordinada_id = :idUnidadeOrc" : "");
        sql.append(filtro.getGrupoBem() != null ? " and gb.id = :idGrupoBem" : "");
        sql.append(filtro.getBem() != null ? " and bem.id = :idBem" : "");

        Query q = em.createNativeQuery(sql.toString());
        FiltroPesquisaBem.adicionarParametrosConfigMovimentacaoBem(q, configuracao);
        q.setParameter("estadoConservacao", EstadoConservacaoBem.INSERVIVEL.name());
        q.setParameter("unidadeAdm", filtro.getHierarquiaAdministrativa().getCodigoSemZerosFinais() + "%");
        q.setParameter("tipoBem", filtro.getTipoBem().name());
        q.setParameter("tipoHierarquiaAdm", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setParameter("tipoHierarquiaOrc", TipoHierarquiaOrganizacional.ORCAMENTARIA.name());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(assistente.getDataLancamento()));
        if (!filtro.getIdsUnidadesControleSetorial().isEmpty()) {
            q.setParameter("unidades", filtro.getIdsUnidadesControleSetorial());
        }
        if (filtro.getHierarquiaOrcamentaria() != null && filtro.getHierarquiaOrcamentaria().getSubordinada() != null) {
            q.setParameter("idUnidadeOrc", filtro.getHierarquiaOrcamentaria().getSubordinada().getId());
        }
        if (filtro.getGrupoBem() != null) {
            q.setParameter("idGrupoBem", filtro.getGrupoBem().getId());
        }
        if (filtro.getBem() != null) {
            q.setParameter("idBem", filtro.getBem().getId());
        }
        List<Bem> bensPesquisados = preencherListaDeBensApartirArrayObject(q.getResultList());
        List<Bem> bensDisponiveis = configMovimentacaoBemFacade.validarAndRetornarBensDisponiveisParaMovimentacaoComDataRetroativa(bensPesquisados, assistente);
        if (assistente.isOperacaoEditar()) {
            assistente.setBensSalvos(pesquisarBensVinculadoAoItemSolicitacaoPorSolicitacao((SolicitacaoAlienacao) assistente.getSelecionado()));
        } else {
            assistente.setBensSalvos(bensDisponiveis);
        }
        verificarEstadoConservacaoDiferenteInservivel(filtro, assistente, bensPesquisados);
        configMovimentacaoBemFacade.verificarBensMoveisEmBloqueioParaMovimentacao(filtro, assistente);
        verificarBemMovelControleSetorial(filtro, assistente, bensDisponiveis);
        return bensDisponiveis;
    }

    private void verificarEstadoConservacaoDiferenteInservivel(FiltroPesquisaBem filtro, AssistenteMovimentacaoBens assistente, List<Bem> bensPesquisados) {
        if (bensPesquisados.isEmpty() && filtro.getBem() != null) {
            EventoBem eventoBem = bemFacade.recuperarUltimoEventoBem(filtro.getBem());
            if (!EstadoConservacaoBem.INSERVIVEL.equals(eventoBem.getEstadoResultante().getEstadoBem())) {
                assistente.getMensagens().add("O bem " + filtro.getBem() + " possui estado de conservação " + eventoBem.getEstadoResultante().getEstadoBem() + ". " +
                    " Não podendo ser movimentando na alienação.");
            }
        }
    }

    private void verificarBemMovelControleSetorial(FiltroPesquisaBem filtro, AssistenteMovimentacaoBens assistente, List<Bem> bensDisponiveis) {
        if (bensDisponiveis.isEmpty() && filtro.getBem() != null) {
            String msgValidacaoUnidadeControleSetorial = bemFacade.verificarUnidadeDoBemNoControleSetorial(filtro.getBem(), filtro.getIdsUnidadesControleSetorial());
            if (!msgValidacaoUnidadeControleSetorial.isEmpty()) {
                assistente.getMensagens().add(msgValidacaoUnidadeControleSetorial);
            }
        }
    }

    public List<Bem> pesquisarBensVinculadoAoItemSolicitacaoPorSolicitacao(SolicitacaoAlienacao solicitacaoAlienacao) {
        String sql = " " +
            " select " +
            "   bem.id, " +
            "   est.id as idestadobem, " +
            "   vworc.codigo || ' - ' || vworc.descricao as unidade " +
            " from itemsolicitacaoalienacao item " +
            "   inner join eventobem ev on ev.id = item.id " +
            "   inner join estadobem est on est.id = ev.estadoresultante_id " +
            "   inner join bem on bem.id = ev.bem_id " +
            "   inner join vwhierarquiaorcamentaria vworc on vworc.subordinada_id = est.detentoraorcamentaria_id " +
            " where item.solicitacaoalienacao_id = :idSolicitacao " +
            "   and ev.dataoperacao between vworc.iniciovigencia and coalesce(vworc.fimvigencia, ev.dataoperacao) " +
            " order by bem.identificacao, bem.descricao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacao", solicitacaoAlienacao.getId());
        return preencherListaDeBensApartirArrayObject(q.getResultList());
    }

    private List<Bem> preencherListaDeBensApartirArrayObject(List<Object[]> objetosDaConsulta) {
        List<Bem> retornaBens = new ArrayList<>();
        for (Object[] bem : objetosDaConsulta) {
            BigDecimal id = (BigDecimal) bem[0];
            Bem b = em.find(Bem.class, id.longValue());
            b.setIdUltimoEstado(((BigDecimal) bem[1]).longValue());
            b.setOrcamentaria((String) bem[2]);
            retornaBens.add(b);
        }
        for (Bem bem : retornaBens) {
            try {
                Bem.preencherDadosTrasientsDoBem(bem, bemFacade.recuperarEstadoBemPorId(bem.getIdUltimoEstado()));
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return retornaBens;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public void remover(SolicitacaoAlienacao entity, ConfigMovimentacaoBem configMovimentacaoBem) {
        processarBensRemovidos(entity, configMovimentacaoBem);
        super.remover(entity);
    }

    public int quantidadeItens(FiltroPesquisaBem filtro) {
        Criteria criteria = getCriteria(filtro);
        criteria.setProjection(Projections.rowCount());
        return ((Number) criteria.uniqueResult()).intValue();
    }

    private Criteria getCriteria(FiltroPesquisaBem filtro) {
        Session session = em.unwrap(Session.class);
        Criteria criteria = session.createCriteria(ItemSolicitacaoAlienacao.class);
        criteria.add(Restrictions.eq("solicitacaoAlienacao", (SolicitacaoAlienacao) filtro.getSelecionado()));
        return criteria;
    }

    @SuppressWarnings("unchecked")
    public List<ItemSolicitacaoAlienacao> recuperarItemSolicitacaoAlienacao(FiltroPesquisaBem filtro) {
        Criteria criteria = getCriteria(filtro);
        criteria.setFirstResult(filtro.getPrimeiroRegistro());
        criteria.setMaxResults(filtro.getQuantidadeRegistro());
        return criteria.list();
    }

    public List<Number> buscarIdsBemPorSolicitacaoAlienacao(SolicitacaoAlienacao entity) {
        String sql = "" +
            " select " +
            "   ev.bem_id " +
            " from ItemSolicitacaoAlienacao item " +
            "   inner join eventobem ev on ev.id = item.id " +
            "   inner join solicitacaoalienacao sol on sol.id = item.solicitacaoalienacao_id " +
            " where sol.id = :idSolicitacao";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacao", entity.getId());
        return ((List<Number>) q.getResultList());
    }

    public Boolean hasSolicitacaoEfetivada(Number idItemSolicitacao) {
        String sql = "  select item.* from leilaoalienacaolotebem item " +
            "           inner join leilaoalienacaolote lote on lote.id = item.leilaoalienacaolote_id " +
            "           where item.itemsolicitacaoalienacao_id = :idItemSolicitacao " +
            "           and lote.arrematado = :arrematado ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItemSolicitacao", idItemSolicitacao);
        q.setParameter("arrematado", Boolean.TRUE);
        return !q.getResultList().isEmpty();
    }


    public Boolean hasSolicitacaoAprovada(Number idItemSolicitacao) {
        String sql = "  select item.* from itemaprovacaoalienacao item " +
            "           inner join aprovacaoalienacao ap on ap.id = item.aprovacaoalienacao_id " +
            "            where itemsolicitacaoalienacao_id = :idItemSolicitacao " +
            "            and ap.SITUACAOEFETIVACAO <> 'REJEITADA' ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItemSolicitacao", idItemSolicitacao);
        return !q.getResultList().isEmpty();
    }

    public SingletonGeradorCodigo getSingletonGeradorCodigo() {
        return singletonGeradorCodigo;
    }

    public InventarioBensMoveisFacade getInventarioBensMoveisFacade() {
        return inventarioBensMoveisFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ParametroPatrimonioFacade getParametroPatrimonioFacade() {
        return parametroPatrimonioFacade;
    }

    public ConfigMovimentacaoBemFacade getConfigMovimentacaoBemFacade() {
        return configMovimentacaoBemFacade;
    }

    public SingletonConcorrenciaPatrimonio getSingletonBloqueioPatrimonio() {
        return singletonBloqueioPatrimonio;
    }
}
