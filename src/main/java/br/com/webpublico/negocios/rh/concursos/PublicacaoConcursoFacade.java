/*
 * Codigo gerado automaticamente em Fri Jul 13 15:21:46 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios.rh.concursos;


import br.com.webpublico.entidades.rh.concursos.Concurso;
import br.com.webpublico.entidades.rh.concursos.PublicacaoConcurso;
import br.com.webpublico.enums.TipoEtapa;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class PublicacaoConcursoFacade extends AbstractFacade<PublicacaoConcurso> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConcursoFacade concursoFacade;

    public PublicacaoConcursoFacade() {
        super(PublicacaoConcurso.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<PublicacaoConcurso> getPublicacoesDoConcurso(Concurso concurso) {
        String sql = "select p.* from publicacaoconcurso p where p.concurso_id = :concurso_id";
        Query q = em.createNativeQuery(sql, PublicacaoConcurso.class);
        q.setParameter("concurso_id", concurso.getId());
        return q.getResultList();
    }

    public void salvarConcurso(Concurso concurso) {
        concursoFacade.adicionarEtapaAoConcurso(concurso, TipoEtapa.PUBLICACAO);
        concursoFacade.salvar(concurso);
    }
}
