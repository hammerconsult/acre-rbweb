package br.com.webpublico.negocios.tributario.regularizacaoconstrucao;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.ServicoConstrucao;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class ServicoConstrucaoFacade extends AbstractFacade<ServicoConstrucao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ServicoConstrucaoFacade() {
        super(ServicoConstrucao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvarNovo(ServicoConstrucao entity) {
        entity.setCodigo(recuperarUltimoCodigo(entity));
        super.salvarNovo(entity);
    }

    public Integer recuperarUltimoCodigo(ServicoConstrucao entity) {
        String sql = " select COALESCE (max(s.codigo), 0) from ServicoConstrucao s ";
        Query q = em.createNativeQuery(sql);

        return (((BigDecimal) q.getSingleResult()).intValue() + 1);
    }

    public List<ServicoConstrucao> listarFiltrando(String parte) {
        String sql = "SELECT servico.* FROM SERVICOCONSTRUCAO servico WHERE servico.DESCRICAO like :parte";
        Query q = em.createNativeQuery(sql, ServicoConstrucao.class);
        q.setParameter("parte", "%" + parte + "%");
        return q.getResultList();
    }

    public List<ServicoConstrucao> buscarFiltrandoGeraHabitese(String parte) {
        String sql = "SELECT servico.* FROM SERVICOCONSTRUCAO servico WHERE servico.DESCRICAO like :parte and GERAHABITESE = 1";
        Query q = em.createNativeQuery(sql, ServicoConstrucao.class);
        q.setParameter("parte", "%" + parte + "%");
        return q.getResultList();
    }
}
