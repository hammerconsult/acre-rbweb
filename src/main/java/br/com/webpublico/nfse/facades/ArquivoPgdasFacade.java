package br.com.webpublico.nfse.facades;

import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfiguracaoTributarioFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.nfse.domain.pgdas.ArquivoPgdas;
import br.com.webpublico.nfse.domain.pgdas.registros.*;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ArquivoPgdasFacade extends AbstractFacade<ArquivoPgdas> {

    @EJB
    private SistemaFacade sistemaFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;

    public ArquivoPgdasFacade() {
        super(ArquivoPgdas.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ConfiguracaoTributarioFacade getConfiguracaoTributarioFacade() {
        return configuracaoTributarioFacade;
    }

    @Override
    public ArquivoPgdas recuperar(Object id) {
        ArquivoPgdas arquivo = super.recuperar(id);
        if(arquivo != null) {
            Hibernate.initialize(arquivo.getRegistrosAAAAA());
            Hibernate.initialize(arquivo.getRegistros00000());
            Hibernate.initialize(arquivo.getRegistros00001());
            Hibernate.initialize(arquivo.getRegistros01000());
            Hibernate.initialize(arquivo.getRegistros01500());
            Hibernate.initialize(arquivo.getRegistros01501());
            Hibernate.initialize(arquivo.getRegistros01502());
            Hibernate.initialize(arquivo.getRegistros02000());
            Hibernate.initialize(arquivo.getRegistros03000());
            Hibernate.initialize(arquivo.getRegistros03100());
            Hibernate.initialize(arquivo.getRegistros03100());
            Hibernate.initialize(arquivo.getRegistros03110());
            Hibernate.initialize(arquivo.getRegistros03120());
            Hibernate.initialize(arquivo.getRegistros03130());
            Hibernate.initialize(arquivo.getRegistros03111());
            Hibernate.initialize(arquivo.getRegistros03121());
            Hibernate.initialize(arquivo.getRegistros03131());
            Hibernate.initialize(arquivo.getRegistros03112());
            Hibernate.initialize(arquivo.getRegistros03122());
            Hibernate.initialize(arquivo.getRegistros03132());
            Hibernate.initialize(arquivo.getRegistros03500());
            Hibernate.initialize(arquivo.getRegistros04000());
            Hibernate.initialize(arquivo.getRegistros99999());
            Hibernate.initialize(arquivo.getRegistrosZZZZZ());
        }
        return arquivo;
    }

    public List<PgdasRegistro00000> buscarRegistro00000(String cnpj) {
        String sql = " select reg0000.* " +
            "  from arquivopgdas pgdas " +
            "       inner join pgdasregistroaaaaa aaaa on aaaa.arquivopgdas_id = pgdas.id " +
            "       inner join pgdasregistro00000 reg0000 on reg0000.pgdasregistroaaaaa_id = aaaa.id " +
            " where reg0000.cnpjmatriz = :cnpj ";
        Query q = em.createNativeQuery(sql, PgdasRegistro00000.class);
        q.setParameter("cnpj", cnpj.trim());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }


    public List<PgdasRegistro00001> buscarRegistro00001(String cnpj) {
        String sql = " select reg0001.* " +
            "from arquivopgdas pgdas " +
            "       inner join pgdasregistroaaaaa aaaa on aaaa.arquivopgdas_id = pgdas.id " +
            "       inner join pgdasregistro00000 reg0000 on reg0000.pgdasregistroaaaaa_id = aaaa.id " +
            "       inner join pgdasregistro00001 reg0001 on reg0001.pgdasregistro00000_id = reg0000.id " +
            "where reg0000.cnpjmatriz = :cnpj ";
        Query q = em.createNativeQuery(sql, PgdasRegistro00001.class);
        q.setParameter("cnpj", cnpj.trim());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public List<PgdasRegistro01000> buscarRegistro01000(String cnpj) {
        String sql = " select reg1000.* " +
            "from arquivopgdas pgdas " +
            "       inner join pgdasregistroaaaaa aaaa on aaaa.arquivopgdas_id = pgdas.id " +
            "       inner join pgdasregistro00000 reg0000 on reg0000.pgdasregistroaaaaa_id = aaaa.id " +
            "       inner join pgdasregistro01000 reg1000 on reg1000.pgdasregistro00000_id = reg0000.id " +
            "where reg0000.cnpjmatriz = :cnpj ";
        Query q = em.createNativeQuery(sql, PgdasRegistro01000.class);
        q.setParameter("cnpj", cnpj.trim());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public List<PgdasRegistro01500> buscarRegistro01500(String cnpj) {
        String sql = " select reg1500.* " +
            "from arquivopgdas pgdas " +
            "       inner join pgdasregistroaaaaa aaaa on aaaa.arquivopgdas_id = pgdas.id " +
            "       inner join pgdasregistro00000 reg0000 on reg0000.pgdasregistroaaaaa_id = aaaa.id " +
            "       inner join pgdasregistro01500 reg1500 on reg1500.pgdasregistro00000_id = reg0000.id " +
            "where reg0000.cnpjmatriz = :cnpj ";
        Query q = em.createNativeQuery(sql, PgdasRegistro01500.class);
        q.setParameter("cnpj", cnpj.trim());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public List<PgdasRegistro01501> buscarRegistro01501(String cnpj) {
        String sql = " select reg1501.* " +
            "from arquivopgdas pgdas " +
            "       inner join pgdasregistroaaaaa aaaa on aaaa.arquivopgdas_id = pgdas.id " +
            "       inner join pgdasregistro00000 reg0000 on reg0000.pgdasregistroaaaaa_id = aaaa.id " +
            "       inner join pgdasregistro01501 reg1501 on reg1501.pgdasregistro00000_id = reg0000.id " +
            "where reg0000.cnpjmatriz = :cnpj ";
        Query q = em.createNativeQuery(sql, PgdasRegistro01501.class);
        q.setParameter("cnpj", cnpj.trim());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public List<PgdasRegistro01502> buscarRegistro01502(String cnpj) {
        String sql = " select reg1502.* " +
            "from arquivopgdas pgdas " +
            "       inner join pgdasregistroaaaaa aaaa on aaaa.arquivopgdas_id = pgdas.id " +
            "       inner join pgdasregistro00000 reg0000 on reg0000.pgdasregistroaaaaa_id = aaaa.id " +
            "       inner join pgdasregistro01502 reg1502 on reg1502.pgdasregistro00000_id = reg0000.id " +
            "where reg0000.cnpjmatriz = :cnpj ";
        Query q = em.createNativeQuery(sql, PgdasRegistro01502.class);
        q.setParameter("cnpj", cnpj.trim());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public List<PgdasRegistro02000> buscarRegistro02000(String cnpj) {
        String sql = " select reg2000.* " +
            "from arquivopgdas pgdas " +
            "       inner join pgdasregistroaaaaa aaaa on aaaa.arquivopgdas_id = pgdas.id " +
            "       inner join pgdasregistro00000 reg0000 on reg0000.pgdasregistroaaaaa_id = aaaa.id " +
            "       inner join pgdasregistro02000 reg2000 on reg2000.pgdasregistro00000_id = reg0000.id " +
            "where reg0000.cnpjmatriz = :cnpj ";
        Query q = em.createNativeQuery(sql, PgdasRegistro02000.class);
        q.setParameter("cnpj", cnpj.trim());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public List<PgdasRegistro03000> buscarRegistro03000(String cnpj) {
        String sql = " select reg3000.* " +
            "from arquivopgdas pgdas " +
            "       inner join pgdasregistroaaaaa aaaa on aaaa.arquivopgdas_id = pgdas.id " +
            "       inner join pgdasregistro00000 reg0000 on reg0000.pgdasregistroaaaaa_id = aaaa.id " +
            "       inner join pgdasregistro03000 reg3000 on reg3000.pgdasregistro00000_id = reg0000.id " +
            "where reg0000.cnpjmatriz = :cnpj ";
        Query q = em.createNativeQuery(sql, PgdasRegistro03000.class);
        q.setParameter("cnpj", cnpj.trim());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public List<PgdasRegistro03100> buscarRegistro03100(String cnpj) {
        String sql = " select reg3100.* " +
            "from arquivopgdas pgdas " +
            "       inner join pgdasregistroaaaaa aaaa on aaaa.arquivopgdas_id = pgdas.id " +
            "       inner join pgdasregistro00000 reg0000 on reg0000.pgdasregistroaaaaa_id = aaaa.id " +
            "       inner join pgdasregistro03000 reg3000 on reg3000.pgdasregistro00000_id = reg0000.id " +
            "       inner join pgdasregistro03100 reg3100 on reg3100.pgdasregistro03000_id = reg3000.id " +
            "where reg0000.cnpjmatriz = :cnpj ";
        Query q = em.createNativeQuery(sql, PgdasRegistro03100.class);
        q.setParameter("cnpj", cnpj.trim());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public List<PgdasRegistro03110> buscarRegistro03110(String cnpj) {
        String sql = " select reg3110.* " +
            "from arquivopgdas pgdas " +
            "       inner join pgdasregistroaaaaa aaaa on aaaa.arquivopgdas_id = pgdas.id " +
            "       inner join pgdasregistro00000 reg0000 on reg0000.pgdasregistroaaaaa_id = aaaa.id " +
            "       inner join pgdasregistro03000 reg3000 on reg3000.pgdasregistro00000_id = reg0000.id " +
            "       inner join pgdasregistro03100 reg3100 on reg3100.pgdasregistro03000_id = reg3000.id " +
            "       inner join pgdasregistro03110 reg3110 on reg3110.pgdasregistro03100_id = reg3100.id " +
            "where reg0000.cnpjmatriz = :cnpj ";
        Query q = em.createNativeQuery(sql, PgdasRegistro03110.class);
        q.setParameter("cnpj", cnpj.trim());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public List<PgdasRegistro03120> buscarRegistro03120(String cnpj) {
        String sql = " select reg3120.* " +
            "from arquivopgdas pgdas " +
            "       inner join pgdasregistroaaaaa aaaa on aaaa.arquivopgdas_id = pgdas.id " +
            "       inner join pgdasregistro00000 reg0000 on reg0000.pgdasregistroaaaaa_id = aaaa.id " +
            "       inner join pgdasregistro03000 reg3000 on reg3000.pgdasregistro00000_id = reg0000.id " +
            "       inner join pgdasregistro03100 reg3100 on reg3100.pgdasregistro03000_id = reg3000.id " +
            "       inner join pgdasregistro03120 reg3120 on reg3120.pgdasregistro03100_id = reg3100.id " +
            "where reg0000.cnpjmatriz = :cnpj ";
        Query q = em.createNativeQuery(sql, PgdasRegistro03120.class);
        q.setParameter("cnpj", cnpj.trim());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public List<PgdasRegistro03130> buscarRegistro03130(String cnpj) {
        String sql = " select reg3130.* " +
            "from arquivopgdas pgdas " +
            "       inner join pgdasregistroaaaaa aaaa on aaaa.arquivopgdas_id = pgdas.id " +
            "       inner join pgdasregistro00000 reg0000 on reg0000.pgdasregistroaaaaa_id = aaaa.id " +
            "       inner join pgdasregistro03000 reg3000 on reg3000.pgdasregistro00000_id = reg0000.id " +
            "       inner join pgdasregistro03100 reg3100 on reg3100.pgdasregistro03000_id = reg3000.id " +
            "       inner join pgdasregistro03130 reg3130 on reg3130.pgdasregistro03100_id = reg3100.id " +
            "where reg0000.cnpjmatriz = :cnpj ";
        Query q = em.createNativeQuery(sql, PgdasRegistro03130.class);
        q.setParameter("cnpj", cnpj.trim());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public List<PgdasRegistro03111> buscarRegistro03111(String cnpj) {
        String sql = " select reg3111.* " +
            "from arquivopgdas pgdas " +
            "       inner join pgdasregistroaaaaa aaaa on aaaa.arquivopgdas_id = pgdas.id " +
            "       inner join pgdasregistro00000 reg0000 on reg0000.pgdasregistroaaaaa_id = aaaa.id " +
            "       inner join pgdasregistro03000 reg3000 on reg3000.pgdasregistro00000_id = reg0000.id " +
            "       inner join pgdasregistro03100 reg3100 on reg3100.pgdasregistro03000_id = reg3000.id " +
            "       inner join pgdasregistro03130 reg3130 on reg3130.pgdasregistro03100_id = reg3100.id " +
            "       inner join pgdasregistro03111 reg3111 on reg3111.pgdasregistro03130_id = reg3130.id " +
            "where reg0000.cnpjmatriz = :cnpj ";
        Query q = em.createNativeQuery(sql, PgdasRegistro03111.class);
        q.setParameter("cnpj", cnpj.trim());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public List<PgdasRegistro03121> buscarRegistro03121(String cnpj) {
        String sql = " select reg3121.* " +
            "from arquivopgdas pgdas " +
            "       inner join pgdasregistroaaaaa aaaa on aaaa.arquivopgdas_id = pgdas.id " +
            "       inner join pgdasregistro00000 reg0000 on reg0000.pgdasregistroaaaaa_id = aaaa.id " +
            "       inner join pgdasregistro03000 reg3000 on reg3000.pgdasregistro00000_id = reg0000.id " +
            "       inner join pgdasregistro03100 reg3100 on reg3100.pgdasregistro03000_id = reg3000.id " +
            "       inner join pgdasregistro03130 reg3130 on reg3130.pgdasregistro03100_id = reg3100.id " +
            "       inner join pgdasregistro03121 reg3121 on reg3121.pgdasregistro03130_id = reg3130.id " +
            "where reg0000.cnpjmatriz = :cnpj ";
        Query q = em.createNativeQuery(sql, PgdasRegistro03121.class);
        q.setParameter("cnpj", cnpj.trim());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public List<PgdasRegistro03131> buscarRegistro03131(String cnpj) {
        String sql = " select reg3131.* " +
            "from arquivopgdas pgdas " +
            "       inner join pgdasregistroaaaaa aaaa on aaaa.arquivopgdas_id = pgdas.id " +
            "       inner join pgdasregistro00000 reg0000 on reg0000.pgdasregistroaaaaa_id = aaaa.id " +
            "       inner join pgdasregistro03000 reg3000 on reg3000.pgdasregistro00000_id = reg0000.id " +
            "       inner join pgdasregistro03100 reg3100 on reg3100.pgdasregistro03000_id = reg3000.id " +
            "       inner join pgdasregistro03130 reg3130 on reg3130.pgdasregistro03100_id = reg3100.id " +
            "       inner join pgdasregistro03131 reg3131 on reg3131.pgdasregistro03130_id = reg3130.id " +
            "where reg0000.cnpjmatriz = :cnpj ";
        Query q = em.createNativeQuery(sql, PgdasRegistro03131.class);
        q.setParameter("cnpj", cnpj.trim());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public List<PgdasRegistro03112> buscarRegistro03112(String cnpj) {
        String sql = " select reg3112.* " +
            "from arquivopgdas pgdas " +
            "       inner join pgdasregistroaaaaa aaaa on aaaa.arquivopgdas_id = pgdas.id " +
            "       inner join pgdasregistro00000 reg0000 on reg0000.pgdasregistroaaaaa_id = aaaa.id " +
            "       inner join pgdasregistro03000 reg3000 on reg3000.pgdasregistro00000_id = reg0000.id " +
            "       inner join pgdasregistro03100 reg3100 on reg3100.pgdasregistro03000_id = reg3000.id " +
            "       inner join pgdasregistro03130 reg3130 on reg3130.pgdasregistro03100_id = reg3100.id " +
            "       inner join pgdasregistro03112 reg3112 on reg3112.pgdasregistro03130_id = reg3130.id " +
            "where reg0000.cnpjmatriz = :cnpj ";
        Query q = em.createNativeQuery(sql, PgdasRegistro03112.class);
        q.setParameter("cnpj", cnpj.trim());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public List<PgdasRegistro03122> buscarRegistro03122(String cnpj) {
        String sql = " select reg3122.* " +
            "from arquivopgdas pgdas " +
            "       inner join pgdasregistroaaaaa aaaa on aaaa.arquivopgdas_id = pgdas.id " +
            "       inner join pgdasregistro00000 reg0000 on reg0000.pgdasregistroaaaaa_id = aaaa.id " +
            "       inner join pgdasregistro03000 reg3000 on reg3000.pgdasregistro00000_id = reg0000.id " +
            "       inner join pgdasregistro03100 reg3100 on reg3100.pgdasregistro03000_id = reg3000.id " +
            "       inner join pgdasregistro03130 reg3130 on reg3130.pgdasregistro03100_id = reg3100.id " +
            "       inner join pgdasregistro03122 reg3122 on reg3122.pgdasregistro03130_id = reg3130.id " +
            "where reg0000.cnpjmatriz = :cnpj ";
        Query q = em.createNativeQuery(sql, PgdasRegistro03122.class);
        q.setParameter("cnpj", cnpj.trim());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public List<PgdasRegistro03132> buscarRegistro03132(String cnpj) {
        String sql = " select reg3132.* " +
            "from arquivopgdas pgdas " +
            "       inner join pgdasregistroaaaaa aaaa on aaaa.arquivopgdas_id = pgdas.id " +
            "       inner join pgdasregistro00000 reg0000 on reg0000.pgdasregistroaaaaa_id = aaaa.id " +
            "       inner join pgdasregistro03000 reg3000 on reg3000.pgdasregistro00000_id = reg0000.id " +
            "       inner join pgdasregistro03100 reg3100 on reg3100.pgdasregistro03000_id = reg3000.id " +
            "       inner join pgdasregistro03130 reg3130 on reg3130.pgdasregistro03100_id = reg3100.id " +
            "       inner join pgdasregistro03132 reg3132 on reg3132.pgdasregistro03130_id = reg3130.id " +
            "where reg0000.cnpjmatriz = :cnpj ";
        Query q = em.createNativeQuery(sql, PgdasRegistro03132.class);
        q.setParameter("cnpj", cnpj.trim());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public List<PgdasRegistro03500> buscarRegistro03500(String cnpj) {
        String sql = " select reg3500.* " +
            "from arquivopgdas pgdas " +
            "       inner join pgdasregistroaaaaa aaaa on aaaa.arquivopgdas_id = pgdas.id " +
            "       inner join pgdasregistro00000 reg0000 on reg0000.pgdasregistroaaaaa_id = aaaa.id " +
            "       inner join pgdasregistro03500 reg3500 on reg3500.pgdasregistro00000_id = reg0000.id " +
            "where reg0000.cnpjmatriz = :cnpj ";
        Query q = em.createNativeQuery(sql, PgdasRegistro03500.class);
        q.setParameter("cnpj", cnpj.trim());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public List<PgdasRegistro04000> buscarRegistro04000(String cnpj) {
        String sql = " select reg4000.* " +
            "from arquivopgdas pgdas " +
            "       inner join pgdasregistroaaaaa aaaa on aaaa.arquivopgdas_id = pgdas.id " +
            "       inner join pgdasregistro00000 reg0000 on reg0000.pgdasregistroaaaaa_id = aaaa.id " +
            "       inner join pgdasregistro04000 reg4000 on reg4000.pgdasregistro00000_id = reg0000.id " +
            "where reg0000.cnpjmatriz = :cnpj ";
        Query q = em.createNativeQuery(sql, PgdasRegistro04000.class);
        q.setParameter("cnpj", cnpj.trim());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }
}
