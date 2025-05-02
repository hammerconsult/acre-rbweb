package br.com.webpublico.negocios.rh.concursos;

import br.com.webpublico.entidades.rh.concursos.Concurso;
import br.com.webpublico.entidades.rh.concursos.EtapaConcurso;
import br.com.webpublico.entidades.rh.concursos.HomologacaoConcurso;
import br.com.webpublico.enums.TipoEtapa;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.SistemaFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 * Created by Buzatto on 02/09/2015.
 */
@Stateless
public class HomologacaoConcursoFacade extends AbstractFacade<HomologacaoConcurso> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConcursoFacade concursoFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public HomologacaoConcursoFacade() {
        super(HomologacaoConcurso.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConcursoFacade getConcursoFacade() {
        return concursoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public HomologacaoConcurso getHomologacaoDoConcurso(Concurso concurso) {
        try {
            return (HomologacaoConcurso) em.createQuery("select h from HomologacaoConcurso h where h.concurso = :con").setParameter("con", concurso).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    @Override
    public void salvarNovo(HomologacaoConcurso entity) {
        concursoFacade.adicionarEtapaAoConcurso(entity.getConcurso(), TipoEtapa.HOMOLOGACAO);
        super.salvarNovo(entity);
    }

    @Override
    public void remover(HomologacaoConcurso entity) {
        concursoFacade.removerEtapaDoConcurso(entity.getConcurso(), TipoEtapa.HOMOLOGACAO);
        super.remover(entity);
    }
}
