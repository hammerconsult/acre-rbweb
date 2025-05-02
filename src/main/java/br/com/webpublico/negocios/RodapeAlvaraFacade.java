package br.com.webpublico.negocios;

import br.com.webpublico.entidades.RodapeAlvara;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class RodapeAlvaraFacade extends AbstractFacade<RodapeAlvara> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public RodapeAlvaraFacade() {
        super(RodapeAlvara.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public boolean hasCombinacaoCheckboxJaSalva(RodapeAlvara rodapeAlvara) {
        String sql = " select ra.* from rodapealvara ra " +
            " where ra.sanitario = :sanitario " +
            " and ra.infraestrutura = :infra " +
            " and ra.ambiental = :ambiental "+
            " and ra.sanitarioestadual = :sanitarioEstadual ";

        if (rodapeAlvara.getId() != null) {
            sql += " and ra.id <> :idRodape ";
        }

        Query q = em.createNativeQuery(sql);
        q.setParameter("sanitario", rodapeAlvara.getSanitario() ? 1 : 0);
        q.setParameter("infra", rodapeAlvara.getInfraestrutura() ? 1 : 0);
        q.setParameter("ambiental", rodapeAlvara.getAmbiental() ? 1 : 0);
        q.setParameter("sanitarioEstadual", rodapeAlvara.getSanitarioEstadual() ? 1 : 0);

        if (rodapeAlvara.getId() != null) {
            q.setParameter("idRodape", rodapeAlvara.getId());
        }

        return !q.getResultList().isEmpty();
    }

    public String buscarRodapeAlvara(Long idAlvara) {
        String sql = " select coalesce(c.fiscalizacaosanitaria, 0), coalesce(c.seinfra, 0), coalesce(c.meioambiente, 0), coalesce(c.interessedoestado, 0) " +
            " from cnaealvara ca " +
            " inner join cnae c on ca.cnae_id = c.id " +
            " where ca.alvara_id = :idAlvara ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idAlvara", idAlvara);

        List<Object[]> rodapes = q.getResultList();

        boolean sanitario = false;
        boolean infra = false;
        boolean ambiental = false;
        boolean interesseEstadual = false;

        if (rodapes != null && !rodapes.isEmpty()) {
            for (Object[] rodape : rodapes) {
                if (((BigDecimal) rodape[0]).intValue() == 1 && !sanitario) {
                    sanitario = ((BigDecimal) rodape[0]).intValue() == 1;
                }
                if (((BigDecimal) rodape[1]).intValue() == 1 && !infra) {
                    infra = ((BigDecimal) rodape[1]).intValue() == 1;
                }
                if (((BigDecimal) rodape[2]).intValue() == 1 && !ambiental) {
                    ambiental = ((BigDecimal) rodape[2]).intValue() == 1;
                }
                if (((BigDecimal) rodape[3]).intValue() == 1 && !interesseEstadual) {
                    interesseEstadual = ((BigDecimal) rodape[3]).intValue() == 1;
                }
            }
        }
        return buscarTextoRodapeAlvara(sanitario, infra, ambiental, interesseEstadual);
    }

    public String buscarTextoRodapeAlvara(boolean sanitario, boolean infra, boolean ambiental, boolean interesseEstadual) {
        String sql = " select ra.textorodape from rodapealvara ra " +
            " where ra.sanitario = :sanitario " +
            " and ra.infraestrutura = :infra " +
            " and ra.ambiental = :ambiental "+
            " and ra.sanitarioestadual = :interesseEstadual ";


        Query q = em.createNativeQuery(sql);
        q.setParameter("sanitario", interesseEstadual ? 0 : (sanitario ? 1 : 0));
        q.setParameter("infra", interesseEstadual ? 0 : (infra ? 1 : 0));
        q.setParameter("ambiental", interesseEstadual ? 0 : (ambiental ? 1 : 0));
        q.setParameter("interesseEstadual", interesseEstadual ? 1 : 0);

        List<String> rodapes = q.getResultList();
        return (rodapes != null && !rodapes.isEmpty()) ? rodapes.get(0) : "";
    }
}
