package br.com.webpublico.negocios;

import br.com.webpublico.entidades.GrupoAtributo;
import br.com.webpublico.enums.ClasseDoAtributo;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Comparator;
import java.util.List;

@Stateless
public class GrupoAtributoFacade extends AbstractFacade<GrupoAtributo> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;

    public GrupoAtributoFacade() {
        super(GrupoAtributo.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void preSave(GrupoAtributo entidade) {
        super.preSave(entidade);
        if (entidade.getId() == null) {
            entidade.setCodigo(singletonGeradorCodigo.getProximoCodigo(GrupoAtributo.class, "codigo"));
        }
        if (entidade.getPadrao()) {
            retirarPadraoOutrosGruposAtributo(entidade);
        }
    }

    public void retirarPadraoOutrosGruposAtributo(GrupoAtributo grupoAtributo) {
        Query query = em.createNativeQuery(" update grupoatributo set padrao = :padrao " +
            (grupoAtributo.getId() != null ? " where id != :id " : ""));
        query.setParameter("padrao", Boolean.FALSE);
        if (grupoAtributo.getId() != null) {
            query.setParameter("id", grupoAtributo.getId());
        }
        query.executeUpdate();
    }

    public List<GrupoAtributo> buscarGrupoAtributoAtivoPorCodigoOrDescricao(String parte) {
        return em.createQuery("from GrupoAtributo gr " +
                " where gr.ativo = :ativo " +
                "   and (to_char(gr.codigo) like :parte or trim(lower(gr.descricao)) = :parte)" +
                " order by gr.codigo ")
            .setParameter("ativo", Boolean.TRUE)
            .setParameter("parte", "%" + parte.trim().toLowerCase() + "%")
            .getResultList();
    }

    public List<GrupoAtributo> buscarGrupoAtributoPorClasse(ClasseDoAtributo classeDoAtributo) {
        List<GrupoAtributo> gruposAtributo = em.createQuery(" select distinct a.grupoAtributo from Atributo a " +
                " where a.classeDoAtributo = :classeDoAtributo ")
            .setParameter("classeDoAtributo", classeDoAtributo)
            .getResultList();
        if (gruposAtributo != null) {
            gruposAtributo.sort(Comparator.comparing(GrupoAtributo::getCodigo));
        }
        return gruposAtributo;
    }
}

