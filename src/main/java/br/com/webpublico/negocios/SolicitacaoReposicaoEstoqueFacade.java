/*
 * Codigo gerado automaticamente em Wed Apr 11 19:47:48 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ItemSolicitacaoReposicaoEstoque;
import br.com.webpublico.entidades.SolicitacaoMaterial;
import br.com.webpublico.entidades.SolicitacaoReposicaoEstoque;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class SolicitacaoReposicaoEstoqueFacade extends AbstractFacade<SolicitacaoReposicaoEstoque> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SolicitacaoMaterialFacade solicitacaoMaterialFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SolicitacaoReposicaoEstoqueFacade() {
        super(SolicitacaoReposicaoEstoque.class);
    }

    public SolicitacaoMaterial salvarSolicitaoMaterialESolicitacaoReposicaoEstoque(SolicitacaoMaterial sm, SolicitacaoReposicaoEstoque sre) {
        if (sm.getNumero() == null) {
            sm.setNumero((singletonGeradorCodigo.getProximoCodigo(SolicitacaoMaterial.class, "numero")).intValue());
        }
        sm = em.merge(sm);
        em.merge(sre);
        return sm;
    }

    public List<ItemSolicitacaoReposicaoEstoque> recuperaItensDaSolicitacaoReposicaoEstoque(SolicitacaoReposicaoEstoque solicitacaoReposicaoEstoque) {
        String hql = "from ItemSolicitacaoReposicaoEstoque item"
                + " where item.solicitacaoRepEstoque = :solicitacao";
        Query consulta = em.createQuery(hql);
        consulta.setParameter("solicitacao", solicitacaoReposicaoEstoque);
        return (List<ItemSolicitacaoReposicaoEstoque>) consulta.getResultList();
    }

    public SolicitacaoMaterial recuperaSolicitacaoDeCompraVinculada(SolicitacaoReposicaoEstoque solrepest) {
        String sql = "          SELECT sm.*"
                + "               FROM solicitacaomaterial sm"
                + "         INNER JOIN lotesolicitacaomaterial lsm ON lsm.solicitacaomaterial_id = sm.id"
                + "         INNER JOIN itemsolicitacao itemsol ON itemsol.lotesolicitacaomaterial_id = lsm.id"
                + "         INNER JOIN itemsolrepestoque isre ON isre.itemsolicitacao_id = itemsol.id"
                + "         INNER JOIN solicitacaorepestoque srp ON srp.id = isre.solicitacaorepestoque_id"
                + "                AND srp.id = :solicitacaoReposicao";

        Query q = em.createNativeQuery(sql, SolicitacaoMaterial.class);
        q.setParameter("solicitacaoReposicao", solrepest.getId());

        if (q.getResultList().isEmpty()) {
            return null;
        } else {
            return (SolicitacaoMaterial) q.getSingleResult();
        }
    }

    public void removerAlternativo(SolicitacaoReposicaoEstoque sre, SolicitacaoMaterial sm) {
        super.remover(sre);
        solicitacaoMaterialFacade.remover(sm);
    }

    @Override
    public void remover(SolicitacaoReposicaoEstoque solicitacaoReposicao) {
        SolicitacaoMaterial solicitacaoMaterial = recuperaSolicitacaoDeCompraVinculada(solicitacaoReposicao);

        if (solicitacaoMaterial != null) {
            podeExcluir(solicitacaoMaterial);
        }

        super.remover(solicitacaoReposicao);
        solicitacaoMaterialFacade.remover(solicitacaoMaterial);
    }

    public void podeExcluir(SolicitacaoMaterial solicitacaoMaterial) throws ValidacaoException {
        ValidacaoException vex = new ValidacaoException();

            if (solicitacaoMaterial != null &&
                    (solicitacaoMaterialFacade.temVinculoComProcessoDeCompra(solicitacaoMaterial))) {
                vex.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "Esta solicitação de reposição já se encontra em processo de compra.");
            }

        if (vex.temMensagens()) {
            throw vex;
        }
    }
}
