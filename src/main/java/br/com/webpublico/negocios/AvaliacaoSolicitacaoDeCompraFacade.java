/*
 * Codigo gerado automaticamente em Tue Apr 17 09:33:08 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.enums.TipoStatusSolicitacao;
import br.com.webpublico.seguranca.NotificacaoService;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless

public class AvaliacaoSolicitacaoDeCompraFacade extends AbstractFacade<AvaliacaoSolicitacaoDeCompra> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SolicitacaoMaterialFacade solicitacaoMaterialFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;

    public AvaliacaoSolicitacaoDeCompraFacade() {
        super(AvaliacaoSolicitacaoDeCompra.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public SolicitacaoMaterialFacade getSolicitacaoMaterialFacade() {
        return solicitacaoMaterialFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public boolean isSituacaoDaAvaliacaoAlterada(AvaliacaoSolicitacaoDeCompra avaliacao) {
        if (avaliacao.getId() == null) {
            return true;
        }
        String sql = " select tipoStatusSolicitacao from AVALIACAOSOLCOMPRA where id = :idAvaliacao";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idAvaliacao", avaliacao.getId());
        TipoStatusSolicitacao tipoStatus = TipoStatusSolicitacao.valueOf(q.getSingleResult().toString());
        return !avaliacao.getTipoStatusSolicitacao().equals(tipoStatus);
    }

    public void gerarNotificacaoAvaliacaoSolicitacaoMaterialAvaliada(AvaliacaoSolicitacaoDeCompra avaliacao) {
        List<UsuarioSistema> usuarios = solicitacaoMaterialFacade.buscarUsuariosGestorLicitacaoPorTipoNotificacaoEUnidade(avaliacao.getSolicitacao().getUnidadeOrganizacional(), TipoNotificacao.AVALIACAO_SOLICITACAO_COMPRA_AVALIADA);
        if (usuarios != null && !usuarios.isEmpty()) {
            for (UsuarioSistema usuario : usuarios) {
                Notificacao notificacao = new Notificacao();
                notificacao.setDescricao("A solicitação de compra " + avaliacao.getSolicitacao().toString() + ", foi " + avaliacao.getTipoStatusSolicitacao().getDescricao().toLowerCase());
                notificacao.setGravidade(Notificacao.Gravidade.INFORMACAO);
                notificacao.setTitulo(TipoNotificacao.AVALIACAO_SOLICITACAO_COMPRA_AVALIADA.getDescricao());
                notificacao.setTipoNotificacao(TipoNotificacao.AVALIACAO_SOLICITACAO_COMPRA_AVALIADA);
                notificacao.setUsuarioSistema(usuario);
                notificacao.setLink("/solicitacao-de-compra/ver/" + avaliacao.getSolicitacao().getId() + "/");
                NotificacaoService.getService().notificar(notificacao);
            }
        }
    }

    public UsuarioSistema buscarUsuarioAprovouPorSolicitacaoMaterial(SolicitacaoMaterial solicitacaoMaterial) {
        String sql = "select us.* " +
            " from AVALIACAOSOLCOMPRA avaliacao " +
            "    inner join usuariosistema us on us.ID = avaliacao.USUARIOSISTEMA_ID " +
            " where avaliacao.TIPOSTATUSSOLICITACAO = '" + TipoStatusSolicitacao.APROVADA.name() + "'" +
            "   and avaliacao.SOLICITACAO_ID = :idSm " +
            " order by avaliacao.ID desc ";
        Query q = em.createNativeQuery(sql, UsuarioSistema.class);
        q.setParameter("idSm", solicitacaoMaterial.getId());
       List<UsuarioSistema> retorno = q.getResultList();
       return !retorno.isEmpty() ? retorno.get(0) : null;
    }
}

