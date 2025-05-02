/*
 * Codigo gerado automaticamente em Fri Jul 13 15:21:46 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios.rh.concursos;


import br.com.webpublico.entidades.rh.concursos.CargoConcurso;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class CargoConcursoFacade extends AbstractFacade<CargoConcurso> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public CargoConcursoFacade() {
        super(CargoConcurso.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public CargoConcurso recuperar(Object id) {
        return super.recuperar(id);
    }

    public CargoConcurso buscarCargoConcursoComClassificados(long id) {
        CargoConcurso cc = em.find(CargoConcurso.class, id);
        if (cc.getClassificacaoConcurso() != null){
            cc.getClassificacaoConcurso().getInscricoes().size();
        }
        return cc;
    }

    public CargoConcurso buscarCargoComCBOsAndInscricoes(Object id) {
        CargoConcurso cc = em.find(CargoConcurso.class, id);
        cc.getCargo().getCbos().size();
        cc.getClassificacaoConcurso().getInscricoes().size();
        return cc;
    }
}
