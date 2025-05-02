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
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 29/10/14
 * Time: 17:19
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class SolicitacaoAlteracaoConservacaoBemFacade extends AbstractFacade<SolicitacaoAlteracaoConservacaoBem> {

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

    public SolicitacaoAlteracaoConservacaoBemFacade() {
        super(SolicitacaoAlteracaoConservacaoBem.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SolicitacaoAlteracaoConservacaoBem recuperarComDependenciasArquivo(Object id) {
        SolicitacaoAlteracaoConservacaoBem entity = super.recuperar(id);
        if (entity.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(entity.getDetentorArquivoComposicao().getArquivosComposicao());
        }
        return entity;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public void remover(SolicitacaoAlteracaoConservacaoBem entity, AssistenteMovimentacaoBens assistente) {
        removerItemEventoBemAndEstadoResultante(assistente, entity);
        super.remover(entity);
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<AssistenteMovimentacaoBens> salvarSolicitacao(AssistenteMovimentacaoBens assistente) {
        SolicitacaoAlteracaoConservacaoBem entity = (SolicitacaoAlteracaoConservacaoBem) assistente.getSelecionado();
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
            logger.error("Erro ao salvar alteração de conservação do bem. ", ex);
            throw ex;
        }
        return new AsyncResult<>(assistente);
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<AssistenteMovimentacaoBens> concluirSolicitacao(AssistenteMovimentacaoBens assistente) {
        SolicitacaoAlteracaoConservacaoBem entity = (SolicitacaoAlteracaoConservacaoBem) assistente.getSelecionado();
        try {
            if (entity.getCodigo() == null) {
                entity.setCodigo(singletonGeradorCodigo.getProximoCodigo(SolicitacaoAlteracaoConservacaoBem.class, "codigo"));
            }
            entity.setSituacao(SituacaoMovimentoAdministrativo.AGUARDANDO_EFETIVACAO);
            entity = em.merge(entity);
            concluirItemSolicitacao(entity, assistente);

            alterarSituacaoEventoMovimentoBloqueioBem(assistente);

            assistente.zerarContadoresProcesso();
            assistente.setSelecionado(entity);
            assistente.setDescricaoProcesso(" ");
        } catch (Exception ex) {
            logger.error("Erro ao concluir a solicitação de alteração de conservação do bem. ", ex);
            throw ex;
        }
        return new AsyncResult<>(assistente);
    }

    private void alterarSituacaoEventoMovimentoBloqueioBem(AssistenteMovimentacaoBens assistente) {
        List<Number> idsBens = Lists.newArrayList();
        for (BemVo bemVo : assistente.getBensMovimentadosVo()) {
            idsBens.add(bemVo.getBem().getId());
        }
        configMovimentacaoBemFacade.alterarSituacaoEvento(idsBens, SituacaoEventoBem.AGUARDANDO_EFETIVACAO, OperacaoMovimentacaoBem.SOLICITACAO_ALTERACAO_CONSERVACAO_BEM);
    }

    private void desbloquearBens(SolicitacaoAlteracaoConservacaoBem entity, ConfigMovimentacaoBem configuracao) {
        List<Number> bensRecuperados = buscarIdsBemPorSolicitacao(entity);
        if (!bensRecuperados.isEmpty()) {
            configMovimentacaoBemFacade.desbloquearBens(configuracao, bensRecuperados);
        }
    }

    private void concluirItemSolicitacao(SolicitacaoAlteracaoConservacaoBem entity, AssistenteMovimentacaoBens assistente) {
        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("Concluindo Evento da Solicitação de Alteração de Conservação de Bens...");
        assistente.setTotal(assistente.getBensMovimentadosVo().size());

        for (BemVo bemVo : assistente.getBensMovimentadosVo()) {
            ItemSolicitacaoAlteracaoConservacaoBem itemSol = em.find(ItemSolicitacaoAlteracaoConservacaoBem.class, bemVo.getIdEventoBem());
            itemSol.setDetentorArquivoComposicao(entity.getDetentorArquivoComposicao());
            itemSol.setSituacaoEventoBem(SituacaoEventoBem.AGUARDANDO_EFETIVACAO);
            em.merge(itemSol);
            assistente.conta();
        }
    }

    private void criarEventoBem(SolicitacaoAlteracaoConservacaoBem entity, AssistenteMovimentacaoBens assistente) {

        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("Criando Evento Solicitação de Alteração de Conservação de Bens...");
        assistente.setTotal(assistente.getBensSelecionadosVo().size());

        List<Number> bensBloqueio = Lists.newArrayList();
        for (BemVo bemVo : assistente.getBensSelecionadosVo()) {
            Bem bem = bemVo.getBem();
            EstadoBem ultimoEstado = bemFacade.recuperarUltimoEstadoDoBem(bem);
            EstadoBem estadoResultante = em.merge(bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(ultimoEstado));
            ItemSolicitacaoAlteracaoConservacaoBem novoItemSol = new ItemSolicitacaoAlteracaoConservacaoBem(bem, ultimoEstado, estadoResultante, entity, assistente.getDataLancamento());
            em.merge(novoItemSol);

            bensBloqueio.add(bem.getId());
            assistente.conta();
        }
        configMovimentacaoBemFacade.bloquearBens(assistente.getConfigMovimentacaoBem(), bensBloqueio);
    }

    private void removerItemEventoBemAndEstadoResultante(AssistenteMovimentacaoBens assistente, SolicitacaoAlteracaoConservacaoBem entity) {
        if (!assistente.isOperacaoNovo()) {
            assistente.setDescricaoProcesso("Removendo eventos ao editar solicitação de alteração de conservação...");
            desbloquearBens(entity, assistente.getConfigMovimentacaoBem());

            List<Number> idsItens = buscarIdsItemSolicitacao(entity);
            List<Number> idsEstadoBem = buscarIdsEstadoResultante(entity);
            assistente.setTotal(idsItens.size());

            for (Number idItem : idsItens) {
                bemFacade.deletarItemEventoBem(idItem.longValue(), "itemsolicitacaoaltconsbem");
                bemFacade.deletarEventoBem(idItem.longValue());
                assistente.conta();
            }
            for (Number idEstado : idsEstadoBem) {
                bemFacade.deletarEstadoBem(idEstado.longValue());
            }
        }
    }

    public List<Number> buscarIdsBemPorSolicitacao(SolicitacaoAlteracaoConservacaoBem entity) {
        String sql = "" +
            " select " +
            "   ev.bem_id " +
            " from itemsolicitacaoaltconsbem item " +
            "   inner join eventobem ev on ev.id = item.id " +
            " where item.solicitacaoalteracaoconsbem_id = :idSolicitacao";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacao", entity.getId());
        return ((List<Number>) q.getResultList());
    }

    public List<Number> buscarIdsItemSolicitacao(SolicitacaoAlteracaoConservacaoBem entity) {
        String sql = "" +
            " select " +
            "  item.id " +
            " from itemsolicitacaoaltconsbem item " +
            "  inner join eventobem ev on ev.id = item.id " +
            " where item.solicitacaoalteracaoconsbem_id = :idSelecionado ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSelecionado", entity.getId());
        return ((List<Number>) q.getResultList());
    }

    private List<Number> buscarIdsEstadoResultante(SolicitacaoAlteracaoConservacaoBem entity) {
        String sql = "" +
            " select " +
            "  ev.estadoresultante_id " +
            "   from itemsolicitacaoaltconsbem item " +
            "   inner join eventobem ev on ev.id = item.id " +
            "   where item.solicitacaoalteracaoconsbem_id = :idSolicitacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacao", entity.getId());
        return ((List<Number>) q.getResultList());
    }

    public List<SolicitacaoAlteracaoConservacaoBem> buscarSolicitacoesPorSituacao(String parte, SituacaoMovimentoAdministrativo situacao) {
        String sql = " " +
            " select obj.* from solicitacaoalterconservbem obj " +
            "  inner join vwhierarquiaadministrativa vwadm on vwadm.subordinada_id = obj.unidadeorganizacional_id " +
            "  inner join usuariounidadeorganizacio uuo on uuo.unidadeorganizacional_id = vwadm.subordinada_id " +
            "  left join usuariosistema us on us.id = obj.responsavel_id " +
            "  left join pessoafisica uspf on uspf.id = us.pessoafisica_id " +
            " where trunc(obj.datasolicitacao) between vwadm.iniciovigencia and coalesce(vwadm.fimvigencia, trunc(obj.datasolicitacao)) " +
            " and uuo.gestorpatrimonio = :gestorPatrimonio " +
            " and uuo.usuariosistema_id = :idUsuario" +
            " and obj.situacao = :situacao  " +
            " and (obj.codigo like :parte " +
            "           or obj.descricao like :parte " +
            "           or lower(translate(uspf.nome,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like :parte " +
            "           or replace(replace(uspf.cpf,'.',''),'-','') like :parte " +
            "           or replace(vwadm.codigo, '.', '') like :parte " +
            "           or lower(translate(vwadm.descricao,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like :parte " +
            "           or uspf.cpf like :parte ) " +
            " order by obj.codigo ";
        Query q = em.createNativeQuery(sql, SolicitacaoAlteracaoConservacaoBem.class);
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


}
