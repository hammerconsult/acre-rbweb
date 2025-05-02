package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.BemVo;
import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.SituacaoMovimentoAdministrativo;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.negocios.tributario.singletons.SingletonConcorrenciaPatrimonio;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
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
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class SolicitacaoTransferenciaGrupoBemFacade extends AbstractFacade<SolicitacaoTransferenciaGrupoBem> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private InventarioBensMoveisFacade inventarioBensMoveisFacade;
    @EJB
    private ConfigMovimentacaoBemFacade configMovimentacaoBemFacade;
    @EJB
    private SingletonConcorrenciaPatrimonio singletonBloqueioPatrimonio;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private PesquisaBemFacade pesquisaBemFacade;
    @EJB
    private GrupoBemFacade grupoBemFacade;

    public SolicitacaoTransferenciaGrupoBemFacade() {
        super(SolicitacaoTransferenciaGrupoBem.class);
    }

    public SolicitacaoTransferenciaGrupoBem recuperarComDependenciasArquivo(Object id) {
        SolicitacaoTransferenciaGrupoBem entity = super.recuperar(id);
        if (entity.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(entity.getDetentorArquivoComposicao().getArquivosComposicao());
        }
        return entity;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public void remover(SolicitacaoTransferenciaGrupoBem entity, AssistenteMovimentacaoBens assistente) {
        removerItemEventoBemAndEstadoResultante(assistente, entity);
        super.remover(entity);
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public AssistenteMovimentacaoBens salvarSolicitacao(AssistenteMovimentacaoBens assistente) {
        SolicitacaoTransferenciaGrupoBem entity = (SolicitacaoTransferenciaGrupoBem) assistente.getSelecionado();
        try {
            entity.setDataVersao(new Date());
            entity = em.merge(entity);
            removerItemEventoBemAndEstadoResultante(assistente, entity);
            criarEventoBem(entity, assistente);

            assistente.zerarContadoresProcesso();
            assistente.setDescricaoProcesso("Finalizando Processo...");
            assistente.setSelecionado(entity);
            assistente.setDescricaoProcesso("");
        } catch (Exception ex) {
            logger.error("Erro ao salvar a solicitação de transferência grupo bem. ", ex);
            throw ex;
        }
        return assistente;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public AssistenteMovimentacaoBens concluirSolicitacao(AssistenteMovimentacaoBens assistente) {
        SistemaFacade.threadLocalUsuario.set(assistente.getUsuarioSistema().getLogin());
        SolicitacaoTransferenciaGrupoBem entity = (SolicitacaoTransferenciaGrupoBem) assistente.getSelecionado();
        try {
            if (entity.getNumero() == null) {
                entity.setNumero(singletonGeradorCodigo.getProximoCodigo(SolicitacaoTransferenciaGrupoBem.class, "numero"));
            }
            entity.setSituacao(SituacaoMovimentoAdministrativo.AGUARDANDO_EFETIVACAO);
            entity = em.merge(entity);
            concluirItemSolicitacao(entity, assistente);

            alterarSituacaoEventoMovimentoBloqueioBem(assistente);

            assistente.zerarContadoresProcesso();
            assistente.setSelecionado(entity);
            assistente.setDescricaoProcesso(" ");
        } catch (Exception ex) {
            logger.error("Erro ao concluir a solicitação de transferência grupo bem. ", ex);
            throw ex;
        }
        return assistente;
    }

    private void alterarSituacaoEventoMovimentoBloqueioBem(AssistenteMovimentacaoBens assistente) {
        List<Number> idsBens = Lists.newArrayList();
        for (BemVo bemVo : assistente.getBensMovimentadosVo()) {
            idsBens.add(bemVo.getBem().getId());
        }
        configMovimentacaoBemFacade.alterarSituacaoEvento(idsBens, SituacaoEventoBem.AGUARDANDO_EFETIVACAO, OperacaoMovimentacaoBem.SOLICITACAO_TRANSFERENCIA_GRUPO_PATRIMONIAL);
    }

    private void desbloquearBens(SolicitacaoTransferenciaGrupoBem entity, ConfigMovimentacaoBem configuracao) {
        List<Number> bensRecuperados = buscarIdsBemPorSolicitacao(entity);
        if (!bensRecuperados.isEmpty()) {
            configMovimentacaoBemFacade.desbloquearBens(configuracao, bensRecuperados);
        }
    }

    private void concluirItemSolicitacao(SolicitacaoTransferenciaGrupoBem entity, AssistenteMovimentacaoBens assistente) {
        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("Concluindo Evento da Solicitação de Transferência Grupo Bem...");
        assistente.setTotal(assistente.getBensMovimentadosVo().size());

        for (BemVo bemVo : assistente.getBensMovimentadosVo()) {
            ItemSolicitacaoTransferenciaGrupoBem itemSol = em.find(ItemSolicitacaoTransferenciaGrupoBem.class, bemVo.getIdEventoBem());
            itemSol.setDetentorArquivoComposicao(entity.getDetentorArquivoComposicao());
            itemSol.setSituacaoEventoBem(SituacaoEventoBem.AGUARDANDO_EFETIVACAO);
            em.merge(itemSol);
            assistente.conta();
        }
    }

    private void criarEventoBem(SolicitacaoTransferenciaGrupoBem entity, AssistenteMovimentacaoBens assistente) {

        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("Criando Evento Solicitação de Transferência Grupo Bem...");
        assistente.setTotal(assistente.getBensSelecionadosVo().size());

        List<Number> bensBloqueio = Lists.newArrayList();
        for (BemVo bemVo : assistente.getBensSelecionadosVo()) {
            Bem bem = bemVo.getBem();
            EstadoBem ultimoEstado = bemFacade.recuperarUltimoEstadoDoBem(bem);
            EstadoBem estadoResultante = em.merge(bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(ultimoEstado));
            ItemSolicitacaoTransferenciaGrupoBem novoItemSol = new ItemSolicitacaoTransferenciaGrupoBem(bem, entity, assistente.getDataLancamento());
            novoItemSol.setEstadoInicial(ultimoEstado);
            novoItemSol.setEstadoResultante(estadoResultante);
            em.merge(novoItemSol);

            bensBloqueio.add(bem.getId());
            assistente.conta();
        }
        configMovimentacaoBemFacade.bloquearBens(assistente.getConfigMovimentacaoBem(), bensBloqueio);
    }

    private void removerItemEventoBemAndEstadoResultante(AssistenteMovimentacaoBens assistente, SolicitacaoTransferenciaGrupoBem entity) {
        if (!assistente.isOperacaoNovo()) {
            assistente.setDescricaoProcesso("Atualizando dados da solicitação...");
            desbloquearBens(entity, assistente.getConfigMovimentacaoBem());

            List<Number> idsItens = buscarIdsItemSolicitacao(entity);
            List<Number> idsEstadoBem = buscarIdsEstadoResultante(entity);
            assistente.setTotal(idsItens.size());

            for (Number idItem : idsItens) {
                bemFacade.deletarItemEventoBem(idItem.longValue(), "itemsoltransfgrupobem");
                bemFacade.deletarEventoBem(idItem.longValue());
                assistente.conta();
            }
            for (Number idEstado : idsEstadoBem) {
                bemFacade.deletarEstadoBem(idEstado.longValue());
            }
        }
    }

    public List<Number> buscarIdsBemPorSolicitacao(SolicitacaoTransferenciaGrupoBem entity) {
        String sql = "" +
            " select " +
            "   ev.bem_id " +
            " from itemsoltransfgrupobem item " +
            "   inner join eventobem ev on ev.id = item.id " +
            " where item.solicitacao_id = :idSolicitacao";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacao", entity.getId());
        return ((List<Number>) q.getResultList());
    }

    public List<Number> buscarIdsItemSolicitacao(SolicitacaoTransferenciaGrupoBem entity) {
        String sql = "" +
            " select " +
            "  item.id " +
            " from itemsoltransfgrupobem item " +
            "  inner join eventobem ev on ev.id = item.id " +
            " where item.solicitacao_id = :idSelecionado ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSelecionado", entity.getId());
        return ((List<Number>) q.getResultList());
    }

    private List<Number> buscarIdsEstadoResultante(SolicitacaoTransferenciaGrupoBem entity) {
        String sql = "" +
            " select " +
            "  ev.estadoresultante_id " +
            "   from itemsoltransfgrupobem item " +
            "   inner join eventobem ev on ev.id = item.id " +
            "   where item.solicitacao_id = :idSolicitacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacao", entity.getId());
        return ((List<Number>) q.getResultList());
    }

    public List<SolicitacaoTransferenciaGrupoBem> buscarSolicitacoesPorSituacao(String parte, SituacaoMovimentoAdministrativo situacao) {
        String sql = " " +
            " select obj.* from solicitacaotransfgrupobem obj " +
            "  inner join vwhierarquiaadministrativa vwadm on vwadm.subordinada_id = obj.unidadeorganizacional_id " +
            "  inner join usuariounidadeorganizacio uuo on uuo.unidadeorganizacional_id = vwadm.subordinada_id " +
            "  left join usuariosistema us on us.id = obj.responsavel_id " +
            "  left join pessoafisica uspf on uspf.id = us.pessoafisica_id " +
            " where trunc(obj.datasolicitacao) between vwadm.iniciovigencia and coalesce(vwadm.fimvigencia, trunc(obj.datasolicitacao)) " +
            " and uuo.gestorpatrimonio = :gestorPatrimonio " +
            " and uuo.usuariosistema_id = :idUsuario" +
            " and obj.situacao = :situacao  " +
            " and (obj.numero like :parte " +
            "           or obj.descricao like :parte " +
            "           or lower(translate(uspf.nome,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like :parte " +
            "           or replace(replace(uspf.cpf,'.',''),'-','') like :parte " +
            "           or replace(vwadm.codigo, '.', '') like :parte " +
            "           or lower(translate(vwadm.descricao,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like :parte " +
            "           or uspf.cpf like :parte ) " +
            " order by obj.numero ";
        Query q = em.createNativeQuery(sql, SolicitacaoTransferenciaGrupoBem.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("situacao", situacao.name());
        q.setParameter("gestorPatrimonio", Boolean.TRUE);
        q.setParameter("idUsuario", sistemaFacade.getUsuarioCorrente().getId());
        return q.getResultList();
    }

    public BemFacade getBemFacade() {
        return bemFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public ConfigMovimentacaoBemFacade getConfigMovimentacaoBemFacade() {
        return configMovimentacaoBemFacade;
    }

    public InventarioBensMoveisFacade getInventarioBensMoveisFacade() {
        return inventarioBensMoveisFacade;
    }

    public SingletonConcorrenciaPatrimonio getSingletonBloqueioPatrimonio() {
        return singletonBloqueioPatrimonio;
    }

    public void setSingletonBloqueioPatrimonio(SingletonConcorrenciaPatrimonio singletonBloqueioPatrimonio) {
        this.singletonBloqueioPatrimonio = singletonBloqueioPatrimonio;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public PesquisaBemFacade getPesquisaBemFacade() {
        return pesquisaBemFacade;
    }

    public GrupoBemFacade getGrupoBemFacade() {
        return grupoBemFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}
