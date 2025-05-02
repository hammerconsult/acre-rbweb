package br.com.webpublico.negocios.tributario;

import br.com.webpublico.entidades.tributario.ProcessoLicenciamentoAmbiental;
import br.com.webpublico.entidades.tributario.TecnicoLicenciamentoAmbiental;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.HashSet;
import java.util.List;

@Stateless
public class TecnicoLicenciamentoAmbientalFacade extends AbstractFacade<TecnicoLicenciamentoAmbiental> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;

    public TecnicoLicenciamentoAmbientalFacade() {
        super(TecnicoLicenciamentoAmbiental.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public boolean hasTecnicoRegistrado(TecnicoLicenciamentoAmbiental tecnico) {
        Query query = em.createNativeQuery(" select t.* from tecnicolicenciamentoambiental t " +
            " where t.usuario_id = :usuario_id " +
            (tecnico.getId() != null ? " and t.id != :id " : ""), TecnicoLicenciamentoAmbiental.class);
        query.setParameter("usuario_id", tecnico.getUsuario().getId());
        if (tecnico.getId() != null) {
            query.setParameter("id", tecnico.getId());
        }
        return !query.getResultList().isEmpty();
    }

    @Override
    public void preSave(TecnicoLicenciamentoAmbiental entidade) {
        if (hasTecnicoRegistrado(entidade)) {
            throw new ValidacaoException("A Pessoa informada já está registrada como Técnico de Licenciamento Ambiental.");
        }
        if (entidade.getId() == null) {
            entidade.setSequencial(singletonGeradorCodigo.getProximoCodigo(TecnicoLicenciamentoAmbiental.class, "sequencial"));
        }
    }

    public List<TecnicoLicenciamentoAmbiental> buscarTecnicosPorParte(String parte) {
        return em.createNativeQuery(" select t.* from tecnicolicenciamentoambiental t " +
                "  inner join usuariosistema u on u.id = t.usuario_id " +
                "  left join pessoafisica pf on pf.id = u.pessoafisica_id " +
                "  left join pessoajuridica pj on pj.id = u.pessoafisica_id " +
                " where coalesce(pf.cpf, pj.cnpj) like :parte " +
                "  or lower(coalesce(pf.nome, pj.razaosocial)) like :parte " +
                " order by t.id desc ", TecnicoLicenciamentoAmbiental.class)
            .setParameter("parte", "%" + parte.trim().toLowerCase() + "%")
            .getResultList();
    }

    public TecnicoLicenciamentoAmbiental getProximoTecnicoAtivoPorSequencial(Long sequencial) {
        Query query = em.createNativeQuery(" select t.* from tecnicolicenciamentoambiental t " +
            " where t.ativo = 1" +
            "   and t.sequencial >= :sequencial " +
            " order by t.sequencial " +
            " fetch first 1 row only ", TecnicoLicenciamentoAmbiental.class);
        query.setParameter("sequencial", sequencial);
        List resultList = query.getResultList();
        if (resultList.isEmpty()) {
            query = em.createNativeQuery(" select t.* from tecnicolicenciamentoambiental t " +
                " where t.ativo = 1" +
                "   and t.sequencial >= 1 " +
                " order by t.sequencial " +
                " fetch first 1 row only ", TecnicoLicenciamentoAmbiental.class);
            resultList = query.getResultList();
        }
        return !resultList.isEmpty() ? (TecnicoLicenciamentoAmbiental) resultList.get(0) : null;
    }
}
