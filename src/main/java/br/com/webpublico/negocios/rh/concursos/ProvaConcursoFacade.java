/*
 * Codigo gerado automaticamente em Fri Jul 13 15:21:46 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios.rh.concursos;


import br.com.webpublico.entidades.rh.concursos.*;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class ProvaConcursoFacade extends AbstractFacade<ProvaConcurso> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ProvaConcursoFacade() {
        super(ProvaConcurso.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public boolean jaForamLancadasNotasParaProvaInformada(ProvaConcurso pc) {
        String sql = "select a.* from avaliacaoprovaconcurso a where a.prova_id = :prova_id";
        Query q = em.createNativeQuery(sql, AvaliacaoProvaConcurso.class);
        q.setParameter("prova_id", pc.getId());
        try {
            return q.getSingleResult() != null;
        } catch (NoResultException nre) {
            return false;
        }
    }

    public List<ProvaConcurso> buscarProvasDoConcursoAndCargo(Concurso c, CargoConcurso cc) {
        String sql = "     select pc.* from provaconcurso pc                           " +
            " inner join avaliacaoprovaconcurso apc on apc.prova_id = pc.id   " +
            " inner join concurso c                 on c.id = apc.concurso_id " +
            " inner join cargoconcurso cc           on cc.concurso_id = c.id  " +
            "      where c.id = :concurso_id                                  " +
            "        and cc.id = :cargo_id                                    " +
            "        and cc.id = pc.cargoconcurso_id                          " +
            "        order by pc.inicio, pc.fim";
        Query q = em.createNativeQuery(sql, ProvaConcurso.class);
        q.setParameter("concurso_id", c.getId());
        q.setParameter("cargo_id", cc.getId());
        return q.getResultList();
    }

    public BigDecimal buscarNotaDoCanditadoNaProva(InscricaoConcurso inscricaoConcurso, ProvaConcurso prova) {
        String sql = "      select nc.nota from notacandidatoconcurso nc" +
            "  inner join avaliacaoprovaconcurso apc on nc.avaliacaoprovaconcurso_id = apc.id" +
            "       where apc.prova_id = :prova_id" +
            "         and nc.inscricao_id = :inscricao_id";
        Query q = em.createNativeQuery(sql);
        q.setParameter("prova_id", prova.getId());
        q.setParameter("inscricao_id", inscricaoConcurso.getId());
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException nre) {
            return BigDecimal.ZERO;
        }
    }
}
