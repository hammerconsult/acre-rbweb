package br.com.webpublico.negocios;

import br.com.webpublico.entidades.DerivacaoObjetoCompra;
import br.com.webpublico.entidades.DerivacaoObjetoCompraComponente;
import br.com.webpublico.exception.ValidacaoException;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class DerivacaoObjetoCompraFacade extends AbstractFacade<DerivacaoObjetoCompra> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private DerivacaoObjetoCompraComponenteFacade derivacaoObjetoCompraComponenteFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DerivacaoObjetoCompraFacade() { super(DerivacaoObjetoCompra.class); }

    @Override
    public DerivacaoObjetoCompra recuperar(Object id) {
        DerivacaoObjetoCompra entity = super.recuperar(id);
        Hibernate.initialize(entity.getDerivacaoComponentes());
        return entity;
    }

    public DerivacaoObjetoCompra salvarNovaDerivacao(DerivacaoObjetoCompra entity) {
        validarDerivacaoDuplicada(entity);
        return em.merge(entity);
    }

    public List<DerivacaoObjetoCompra> buscarDerivacoesObjetoCompra(String parte) {
        String sql =  " select d.* from derivacaoobjetocompra d " +
            " where lower(d.descricao) like :parte ";
        Query q = em.createNativeQuery(sql, DerivacaoObjetoCompra.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        try {
            return q.getResultList();
        } catch (NoResultException nre) {
            return new ArrayList<>();
        }
    }

    public List<DerivacaoObjetoCompraComponente> buscarComponentesPorDerivacao(DerivacaoObjetoCompra derivacao) {
       return derivacaoObjetoCompraComponenteFacade.buscarComponentesPorDerivacao(derivacao);
    }

    public boolean isDerivacaoJaCriada(DerivacaoObjetoCompra derivacao) {
        String sql = "select d.* from derivacaoobjetocompra d where lower(d.descricao) = :descricao";
        Query q = em.createNativeQuery(sql);
        q.setParameter("descricao", derivacao.getDescricao().toLowerCase());

        if (!q.getResultList().isEmpty()) {
            return true;
        }
        return false;
    }

    public void validarDerivacaoDuplicada(DerivacaoObjetoCompra derivacao) {
        ValidacaoException ve = new ValidacaoException();
        if (derivacao.getId() == null && isDerivacaoJaCriada(derivacao)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A derivação de objeto de compra '" + derivacao.getDescricao() + "' já existe.");
        }
        ve.lancarException();
    }

}
