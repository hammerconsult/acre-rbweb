package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CNAE;
import br.com.webpublico.entidades.ParametrosCalcAmbiental;
import br.com.webpublico.enums.TipoLicencaAmbiental;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ParametrosCalcAmbientalFacade extends AbstractFacade<ParametrosCalcAmbiental> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ParametrosCalcAmbientalFacade() {
        super(ParametrosCalcAmbiental.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ParametrosCalcAmbiental recuperar(Object id) {
        ParametrosCalcAmbiental param = em.find(ParametrosCalcAmbiental.class, id);
        Hibernate.initialize(param.getAtributosCalculo());
        return param;
    }

    public List<CNAE> buscarCnaesCalcAmbiental(String parte) {
        String sql = " select cnae.* from cnae where cnae.meioambiente = :meioAmbiente " +
            " and (cnae.codigoCnae like :parte or lower(cnae.descricaoDetalhada) like :parte)" +
            " order by cnae.codigoCnae ";
        Query q = em.createNativeQuery(sql, CNAE.class);
        q.setParameter("meioAmbiente", Boolean.TRUE);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");

        List<CNAE> cnaes = q.getResultList();
        return (cnaes != null && !cnaes.isEmpty()) ? cnaes : Lists.<CNAE>newArrayList();
    }

    public List<ParametrosCalcAmbiental> recuperarParametrosPorCnae(Long idCnae) {
        String sql = " select parametro.* from parametroscalcambiental parametro " +
            " where parametro.cnae_id = :idCnae ";

        Query q = em.createNativeQuery(sql, ParametrosCalcAmbiental.class);
        q.setParameter("idCnae", idCnae);

        List<ParametrosCalcAmbiental> parametros = q.getResultList();
        return (parametros != null) ? parametros : Lists.<ParametrosCalcAmbiental>newArrayList();
    }

    public ParametrosCalcAmbiental buscarParametroPorCnaeAndTipoLicenca(Long idCnae, TipoLicencaAmbiental tipoLicenca) {
        String sql = " select parametro.* from parametroscalcambiental parametro " +
            " where parametro.cnae_id = :idCnae " +
            " and parametro.licencaambiental = :licenca ";

        Query q = em.createNativeQuery(sql, ParametrosCalcAmbiental.class);
        q.setParameter("idCnae", idCnae);
        q.setParameter("licenca", tipoLicenca.name());

        List<ParametrosCalcAmbiental> parametros = q.getResultList();
        return (parametros != null && !parametros.isEmpty()) ? parametros.get(0) : null;
    }
}
