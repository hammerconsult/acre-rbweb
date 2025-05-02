package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Exame;
import br.com.webpublico.entidades.ExameComplementar;
import br.com.webpublico.singletons.SingletonGeradorCodigoRH;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by carlos on 18/06/15.
 */
@Stateless
public class ExameComplementarFacade extends AbstractFacade<ExameComplementar> {

    @EJB
    private ExameFacade exameFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ExameComplementarFacade() {
        super(ExameComplementar.class);
    }

    @Override
    public void salvar(ExameComplementar entity) {
        super.salvar(entity);
    }

    @Override
    public void salvarNovo(ExameComplementar entity) {
        super.salvarNovo(entity);
    }

    public ExameFacade getExameFacade() {
        return exameFacade;
    }

}
