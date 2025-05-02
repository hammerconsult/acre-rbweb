package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AgrupadorRelatorioCredito;
import br.com.webpublico.entidades.AgrupadorRelatorioCreditoDivida;
import br.com.webpublico.entidades.Divida;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class AgrupadorRelatorioCreditoFacade extends AbstractFacade<AgrupadorRelatorioCredito> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private DividaFacade dividaFacade;

    public AgrupadorRelatorioCreditoFacade() {
        super(AgrupadorRelatorioCredito.class);
    }

    @Override
    public AgrupadorRelatorioCredito recuperar(Object id) {
        AgrupadorRelatorioCredito agrupadorRelatorioCredito = super.recuperar(id);
        agrupadorRelatorioCredito.getDividas().size();
        return agrupadorRelatorioCredito;
    }

    @Override
    public Object recuperar(Class entidade, Object id) {
        AgrupadorRelatorioCredito agrupadorRelatorioCredito = (AgrupadorRelatorioCredito) super.recuperar(entidade, id);
        agrupadorRelatorioCredito.getDividas().size();
        return agrupadorRelatorioCredito;
    }

    public List<Divida> buscarDividasPorCredito(AgrupadorRelatorioCredito agrupadorRelatorioCredito, String filtro) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select distinct div.* from AGRUPADORRELATORIOCREDDIV agp ")
            .append(" inner join divida div on agp.divida_id = div.id ")
            .append(" where agp.agrupadorRelatorioCredito_id = :credito ")
            .append(" where lower(div.descricao) like :filtro ")
            .append(" order by div.descricao desc ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("credito", agrupadorRelatorioCredito.getId());
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DividaFacade getDividaFacade() {
        return dividaFacade;
    }
}
