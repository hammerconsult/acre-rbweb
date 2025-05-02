package br.com.webpublico.negocios.tributario.regularizacaoconstrucao;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.FaixaDeValoresHL;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.HabiteseClassesConstrucao;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.HabiteseFaixaDeValores;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.HabiteseGruposConstrucao;
import br.com.webpublico.negocios.AbstractFacade;
import org.hibernate.Hibernate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class HabiteseFaixaDeValoresFacade extends AbstractFacade<HabiteseFaixaDeValores> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public HabiteseFaixaDeValoresFacade() {
        super(HabiteseFaixaDeValores.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvarNovo(HabiteseFaixaDeValores entity) {
        entity.setCodigo(ultimoCodigoMaisUm());
        super.salvarNovo(entity);
    }

    private HabiteseFaixaDeValores inicializar(HabiteseFaixaDeValores obj) {
        Hibernate.initialize(obj.getListaFaixaDeValoresHL());
        return obj;
    }

    public HabiteseFaixaDeValores buscarFaixaPorExercicioClasse(Exercicio exercicio, HabiteseClassesConstrucao classe) {
        Query q = em.createQuery("from HabiteseFaixaDeValores where exercicio = :exer and habiteseClassesConstrucao = :classe");
        q.setParameter("exer", exercicio);
        q.setParameter("classe", classe);
        List<HabiteseFaixaDeValores> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            return inicializar(resultado.get(0));
        }
        return null;
    }

    public Integer ultimoCodigoMaisUm() {
        String sql = " select COALESCE (max(hfv.codigo), 0) + 1 from HabiteseFaixaDeValores hfv ";
        Query q = em.createNativeQuery(sql);
        return (((BigDecimal) q.getSingleResult()).intValue());
    }

    public List<HabiteseFaixaDeValores> buscarFaixasPorExercicio(Exercicio ex) {
        Query q = em.createNativeQuery("select hfv.* from HABITESEFAIXADEVALORES hfv where hfv.EXERCICIO_ID = :ex", HabiteseFaixaDeValores.class);
        q.setParameter("ex", ex.getId());
        return q.getResultList();
    }

    @Override
    public HabiteseFaixaDeValores recuperar(Object id) {
        HabiteseFaixaDeValores hfdv = super.recuperar(id);
        inicializar(hfdv);
        return hfdv;
    }
}
