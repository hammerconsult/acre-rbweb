package br.com.webpublico.negocios.tributario;


import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.tributario.ParametroLicenciamentoAmbiental;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ArquivoFacade;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class ParametroLicenciamentoAmbientalFacade extends AbstractFacade<ParametroLicenciamentoAmbiental> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private ArquivoFacade arquivoFacade;

    public ParametroLicenciamentoAmbientalFacade() {
        super(ParametroLicenciamentoAmbiental.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    @Override
    public ParametroLicenciamentoAmbiental recuperar(Object id) {
        ParametroLicenciamentoAmbiental parametro = super.recuperar(id);
        if (parametro.getDiretor().getArquivo() != null) {
            Hibernate.initialize(parametro.getDiretor().getArquivo().getPartes());
        }
        if (parametro.getSecretario().getArquivo() != null) {
            Hibernate.initialize(parametro.getSecretario().getArquivo().getPartes());
        }
        if (parametro.getSecretarioAdjunto().getArquivo() != null) {
            Hibernate.initialize(parametro.getSecretarioAdjunto().getArquivo().getPartes());
        }
        return parametro;
    }

    public boolean existeParametroComMesmoExercicio(Long id, int ano) {
        String sql = "select count(*) from parametroLicenciamentoAmbiental param " +
            " inner join exercicio ex on param.exercicio_id = ex.id " +
            " where ex.ano = :ano ";
        if (id != null) sql += " and param.id <> :id";
        Query q = em.createNativeQuery(sql);
        q.setParameter("ano", ano);
        if (id != null) q.setParameter("id", id);
        BigDecimal result = (BigDecimal) q.getResultList().get(0);
        return result.intValue() > 0;
    }

    public ParametroLicenciamentoAmbiental buscarParametroPeloExercicio(Exercicio ex) {
        String sql = "select param.* from parametroLicenciamentoAmbiental param " +
            " inner join exercicio ex on param.exercicio_id = ex.id " +
            " where ex.ano = :ano ";
        Query q = em.createNativeQuery(sql, ParametroLicenciamentoAmbiental.class);
        q.setParameter("ano", ex.getAno());
        List<ParametroLicenciamentoAmbiental> result = q.getResultList();
        return !result.isEmpty() ? recuperar(result.get(0).getId()) : null;
    }
}
