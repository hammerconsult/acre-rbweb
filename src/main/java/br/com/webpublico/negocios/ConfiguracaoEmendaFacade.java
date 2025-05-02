package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfiguracaoEmenda;
import br.com.webpublico.entidades.Exercicio;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ConfiguracaoEmendaFacade extends AbstractFacade<ConfiguracaoEmenda> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ContaFacade contaFacade;

    public ConfiguracaoEmendaFacade() {
        super(ConfiguracaoEmenda.class);
    }

    @Override
    public ConfiguracaoEmenda recuperar(Object id) {
        ConfiguracaoEmenda configuracaoEmenda = em.find(ConfiguracaoEmenda.class, id);
        configuracaoEmenda.getContasDeDespesa().size();
        configuracaoEmenda.getFontesDeRecurso().size();
        return configuracaoEmenda;
    }

    public boolean hasConfiguracaoParaExercicio(ConfiguracaoEmenda selecionado) {
        String sql = " select 1 from ConfiguracaoEmenda ce " +
            " where ce.exercicio_id = :exercicio ";
        if (selecionado.getId() != null) {
            sql += " and ce.id <> :id ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("exercicio", selecionado.getExercicio().getId());
        if (selecionado.getId() != null) {
            q.setParameter("id", selecionado.getId());
        }
        return !q.getResultList().isEmpty();
    }

    public ConfiguracaoEmenda buscarConfiguracaoPorExercicio(Exercicio exercicio) {
        String sql = " select ce.* from ConfiguracaoEmenda ce " +
            " where ce.exercicio_id = :exercicio ";
        Query q = em.createNativeQuery(sql, ConfiguracaoEmenda.class);
        q.setParameter("exercicio", exercicio.getId());
        q.setMaxResults(1);
        List<ConfiguracaoEmenda> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            return recuperar(resultado.get(0).getId());
        }
        return null;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }
}
