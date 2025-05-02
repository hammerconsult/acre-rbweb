/*
 * Codigo gerado automaticamente em Tue Feb 07 08:22:09 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.enums.CategoriaDeclaracaoPrestacaoContas;
import br.com.webpublico.entidades.DeclaracaoPrestacaoContas;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class DeclaracaoPrestacaoContasFacade extends AbstractFacade<DeclaracaoPrestacaoContas> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private UnidadeGestoraFacade unidadeGestoraFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DeclaracaoPrestacaoContasFacade() {
        super(DeclaracaoPrestacaoContas.class);
    }

    public boolean existeCodigo(DeclaracaoPrestacaoContas declaracaoPrestacaoContas) {
        String hql = " from DeclaracaoPrestacaoContas d where d.codigo = :parametroCodigo ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("parametroCodigo", declaracaoPrestacaoContas.getCodigo());

        List<DeclaracaoPrestacaoContas> lista = new ArrayList<DeclaracaoPrestacaoContas>();
        lista = q.getResultList();

        if (lista.contains(declaracaoPrestacaoContas)) {
            return false;
        } else {
            return (!q.getResultList().isEmpty());
        }
    }

    public List<DeclaracaoPrestacaoContas> buscarDeclaracaoPrestacaoDeContas(String parte) {
        String hql = "select d from DeclaracaoPrestacaoContas d where lower(to_char(d.codigo)) like :parte or lower(to_char(d.categoriaDeclaracaoPrestacaoContas)) like :parte order by d.codigo desc";
        Query q = em.createQuery(hql);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public DeclaracaoPrestacaoContas recuperarDeclaracaoParaFinalidade(CategoriaDeclaracaoPrestacaoContas cat){
        String hql = "select dpc from DeclaracaoPrestacaoContas dpc where dpc.categoriaDeclaracaoPrestacaoContas = :cat";
        Query q = em.createQuery(hql);
        q.setMaxResults(1);
        q.setParameter("cat",cat);
        try{
            DeclaracaoPrestacaoContas dpc = (DeclaracaoPrestacaoContas) q.getSingleResult();
            if (dpc.getUnidadeGestora() != null) {
                dpc.getUnidadeGestora().getUnidadeGestoraUnidadesOrganizacionais().size();
            }
            return dpc;
        }catch (NoResultException nre){
            return null;
        }
    }

    public UnidadeGestoraFacade getUnidadeGestoraFacade() {
        return unidadeGestoraFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }
}
