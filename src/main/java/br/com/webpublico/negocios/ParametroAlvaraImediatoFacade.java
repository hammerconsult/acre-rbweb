package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ParametroAlvaraImediato;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.comum.FormularioCampoFacade;
import br.com.webpublico.negocios.comum.FormularioFacade;
import br.com.webpublico.negocios.tributario.regularizacaoconstrucao.ServicoConstrucaoFacade;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ParametroAlvaraImediatoFacade extends AbstractFacade<ParametroAlvaraImediato> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ServicoConstrucaoFacade servicoConstrucaoFacade;
    @EJB
    private FormularioFacade formularioFacade;
    @EJB
    private FormularioCampoFacade formularioCampoFacade;

    public ParametroAlvaraImediatoFacade() {
        super(ParametroAlvaraImediato.class);
    }

    @Override
    public ParametroAlvaraImediato recuperar(Object id) {
        ParametroAlvaraImediato parametro = super.recuperar(id);
        Hibernate.initialize(parametro.getCoibicoes());
        return parametro;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ServicoConstrucaoFacade getServicoConstrucaoFacade() {
        return servicoConstrucaoFacade;
    }

    public FormularioFacade getFormularioFacade() {
        return formularioFacade;
    }

    public FormularioCampoFacade getFormularioCampoFacade() {
        return formularioCampoFacade;
    }

    @Override
    public void preSave(ParametroAlvaraImediato parametro) {
        if (hasParametroRegistrado(parametro)) {
            throw new ValidacaoException("Parâmetro já registrado para o exercício de " + parametro.getExercicio().getAno() + ". ");
        }
    }

    public boolean hasParametroRegistrado(ParametroAlvaraImediato parametro) {
        String hql = " from ParametroAlvaraImediato p " +
                " where p.exercicio.id = :exercicio_id " +
                (parametro.getId() != null ? " and p.id <> :id " : "");
        Query query = em.createQuery(hql);
        query.setParameter("exercicio_id", parametro.getExercicio().getId());
        if (parametro.getId() != null) {
            query.setParameter("id", parametro.getId());
        }
        return !query.getResultList().isEmpty();
    }

    public ParametroAlvaraImediato findByExercicio(Exercicio exercicio) {
        List resultList = em.createQuery("select p from ParametroAlvaraImediato p " +
                        " where p.exercicio.id = :exercicio_id ")
                .setParameter("exercicio_id", exercicio.getId())
                .setMaxResults(1)
                .getResultList();
        return !resultList.isEmpty() ? (ParametroAlvaraImediato) resultList.get(0) : null;
    }
}
