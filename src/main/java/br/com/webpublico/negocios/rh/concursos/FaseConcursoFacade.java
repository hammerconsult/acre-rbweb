/*
 * Codigo gerado automaticamente em Fri Jul 13 15:21:46 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios.rh.concursos;


import br.com.webpublico.entidades.rh.concursos.Concurso;
import br.com.webpublico.entidades.rh.concursos.FaseConcurso;
import br.com.webpublico.enums.TipoEtapa;
import br.com.webpublico.negocios.AbstractFacade;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class FaseConcursoFacade extends AbstractFacade<FaseConcurso> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConcursoFacade concursoFacade;

    public FaseConcursoFacade() {
        super(FaseConcurso.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public void salvarConcurso(Concurso concurso) {
        concursoFacade.adicionarEtapaAoConcurso(concurso, TipoEtapa.FASES_E_PROVAS);
        concursoFacade.salvar(concurso);
    }

    public FaseConcurso recuperarFaseComProvas(Object id) {
        FaseConcurso fc =  super.recuperar(id);
        fc.getProvas().size();
        Hibernate.initialize(fc.getAnexos());
        return fc;
    }

    @Override
    public FaseConcurso recuperar(Object id) {
        FaseConcurso fase = super.recuperar(id);
        Hibernate.initialize(fase.getAnexos());
        Hibernate.initialize(fase.getProvas());
        return fase;
    }
}
