/*
 * Codigo gerado automaticamente em Fri Jul 13 15:21:46 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios.rh.concursos;

import br.com.webpublico.entidades.rh.concursos.AvaliacaoConcurso;
import br.com.webpublico.entidades.rh.concursos.BancaExaminadora;
import br.com.webpublico.entidades.rh.concursos.Concurso;
import br.com.webpublico.enums.TipoEtapa;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Stateless
public class BancaExaminadoraFacade extends AbstractFacade<BancaExaminadora> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConcursoFacade concursoFacade;

    public BancaExaminadoraFacade() {
        super(BancaExaminadora.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public BancaExaminadora recuperar(Object id) {
        BancaExaminadora be = super.recuperar(id);
        be.getMembros().size();
        return be;
    }

    @Override
    public void salvarNovo(BancaExaminadora entity) {
        concursoFacade.adicionarEtapaAoConcurso(entity.getConcurso(), TipoEtapa.BANCA_EXAMINADORA);
        super.salvarNovo(entity);
    }

    @Override
    public void remover(BancaExaminadora entity) {
        concursoFacade.removerEtapaDoConcurso(entity.getConcurso(), TipoEtapa.BANCA_EXAMINADORA);
        super.remover(entity);
    }

    public boolean temBancaExaminadoraParaConcurso(Concurso concurso) {
        try {
            if (buscarBancaExaminadoraPorConcurso(concurso) != null) {
                return true;
            }
        } catch (NoResultException nre) {
            return false;
        }
        return false;
    }

    public BancaExaminadora buscarBancaExaminadoraPorConcurso(Concurso concurso) throws NoResultException {
        return (BancaExaminadora) em.createQuery("select be from BancaExaminadora be where be.concurso = :con").setParameter("con", concurso).setMaxResults(1).getSingleResult();
    }
}
