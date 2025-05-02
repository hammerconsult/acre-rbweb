/*
 * Codigo gerado automaticamente em Fri Jul 13 15:21:46 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios.rh.concursos;

import br.com.webpublico.entidades.rh.concursos.AvaliacaoConcurso;
import br.com.webpublico.entidades.rh.concursos.Concurso;
import br.com.webpublico.enums.TipoEtapa;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Stateless
public class AvaliacaoConcursoFacade extends AbstractFacade<AvaliacaoConcurso> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConcursoFacade concursoFacade;

    public AvaliacaoConcursoFacade() {
        super(AvaliacaoConcurso.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvarNovo(AvaliacaoConcurso entity) {
        concursoFacade.adicionarEtapaAoConcurso(entity.getConcurso(), TipoEtapa.AVALIACAO_CONCURSO);
        super.salvarNovo(entity);
    }

    @Override
    public void remover(AvaliacaoConcurso entity) {
        concursoFacade.removerEtapaDoConcurso(entity.getConcurso(), TipoEtapa.AVALIACAO_CONCURSO);
        super.remover(entity);
    }

    public boolean temAvaliacaoParaConcurso(Concurso concurso) {
        try {
            if (buscarAvaliacaoPorConcurso(concurso) != null) {
                return true;
            }
        } catch (NoResultException nre) {
            return false;
        }
        return false;
    }

    public AvaliacaoConcurso buscarAvaliacaoPorConcurso(Concurso concurso) throws NoResultException {
        return (AvaliacaoConcurso) em.createQuery("select ac from AvaliacaoConcurso ac where ac.concurso = :con").setParameter("con", concurso).setMaxResults(1).getSingleResult();
    }

}
