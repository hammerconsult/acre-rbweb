package br.com.webpublico.negocios.rh.concursos;

import br.com.webpublico.entidades.rh.concursos.CargoConcurso;
import br.com.webpublico.entidades.rh.concursos.Concurso;
import br.com.webpublico.entidades.rh.concursos.InscricaoConcurso;
import br.com.webpublico.enums.TipoEtapa;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by Buzatto on 26/08/2015.
 */
@Stateless
public class InscricaoConcursoFacade extends AbstractFacade<InscricaoConcurso> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager entityManager;
    @EJB
    private ConcursoFacade concursoFacade;

    public InscricaoConcursoFacade() {
        super(InscricaoConcurso.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    public ConcursoFacade getConcursoFacade() {
        return concursoFacade;
    }

    @Override
    public InscricaoConcurso recuperar(Object id) {
        InscricaoConcurso ic = super.recuperar(id);
        if (ic.getConcurso() != null) {
            ic.setConcurso(concursoFacade.recuperar(ic.getConcurso().getId()));
        }
        return ic;
    }

    public void salvar(InscricaoConcurso entity) {
        if (entity.getId() == null) {
            entityManager.persist(entity);
        } else {
            try {
                entityManager.merge(entity);
            } catch (Exception ex) {
                throw new ExcecaoNegocioGenerica("Erro ao atualizar a inscrição!");
            }
        }
    }

    public InscricaoConcurso salvarRetornando(InscricaoConcurso entity) {
        return entityManager.merge(entity);
    }

    public void salvarConcurso(Concurso concurso) {
        concursoFacade.adicionarEtapaAoConcurso(concurso, TipoEtapa.INSCRICAO);
        concursoFacade.salvar(concurso);
    }

    public List<InscricaoConcurso> getInscricoesDoCargo(CargoConcurso cargoConcurso) {
        String sql = "select i.* from inscricaoconcurso i where i.cargoconcurso_id = :cargo_id order by i.numero, i.nome";
        Query q = entityManager.createNativeQuery(sql, InscricaoConcurso.class);
        q.setParameter("cargo_id", cargoConcurso.getId());
        return q.getResultList();
    }

    public InscricaoConcurso buscarInscricaoDoCargoPorCpf(CargoConcurso cargoConcurso, String cpf) {
        String sql = "select i.* from inscricaoconcurso i where i.cargoconcurso_id = :cargo_id and replace(i.cpf, '.', '') like :cpf order by i.numero, i.nome";
        Query q = entityManager.createNativeQuery(sql, InscricaoConcurso.class);
        q.setParameter("cargo_id", cargoConcurso.getId());
        q.setParameter("cpf", cpf.replace(".", ""));
        if (!q.getResultList().isEmpty()) {
            return (InscricaoConcurso) q.getSingleResult();
        }
        return null;
    }
}
