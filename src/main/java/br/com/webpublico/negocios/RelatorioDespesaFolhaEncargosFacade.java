package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.contabil.financeiro.ConfigDespesaFolhaEncargos;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class RelatorioDespesaFolhaEncargosFacade {

    @PersistenceContext
    private EntityManager em;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;

    public ConfigDespesaFolhaEncargos salvarConfiguracao(ConfigDespesaFolhaEncargos entity) {
        return em.merge(entity);
    }

    public void removerConfiguracao(ConfigDespesaFolhaEncargos entity) {
        em.remove(em.find(ConfigDespesaFolhaEncargos.class, entity.getId()));
    }

    public List<ConfigDespesaFolhaEncargos> buscarConfiguracoes(Exercicio exercicio) {
        String sql = " select conf.* from ConfigDespesaFolhaEncargos conf " +
            " inner join conta c on conf.contadespesa_id = c.id " +
            " where conf.exercicio_id = :exercicio " +
            " order by conf.tipoDespesaFolhaEncargos desc, c.codigo ";
        Query q = em.createNativeQuery(sql, ConfigDespesaFolhaEncargos.class);
        q.setParameter("exercicio", exercicio.getId());
        return q.getResultList();
    }

    public boolean hasConfiguracao(ConfigDespesaFolhaEncargos entity) {
        String sql = " select conf.* from ConfigDespesaFolhaEncargos conf " +
            " where conf.contadespesa_id = :contaDespesa " +
            " and conf.tipoDespesaFolhaEncargos = :tipoDespesaFolhaEncargos " +
            " and conf.exercicio_id = :exercicio ";
        if (entity.getId() != null) {
            sql += " and conf.id <> :id ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("contaDespesa", entity.getContaDespesa().getId());
        q.setParameter("exercicio", entity.getExercicio().getId());
        q.setParameter("tipoDespesaFolhaEncargos", entity.getTipoDespesaFolhaEncargos().name());
        if (entity.getId() != null) {
            q.setParameter("id", entity.getId());
        }
        List<ConfigDespesaFolhaEncargos> resultado = q.getResultList();
        return resultado != null && !resultado.isEmpty();
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public FonteDeRecursosFacade getFonteDeRecursosFacade() {
        return fonteDeRecursosFacade;
    }
}
