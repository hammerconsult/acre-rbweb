package br.com.webpublico.negocios.tributario.regularizacaoconstrucao;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.ParametroRegularizacao;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.TipoDoctoOficialFacade;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ParametroRegularizacaoFacade extends AbstractFacade<ParametroRegularizacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private TipoDoctoOficialFacade tipoDoctoOficialFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public ParametroRegularizacaoFacade() {
        super(ParametroRegularizacao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ParametroRegularizacao recuperar(Object id) {
        return inicializar(super.recuperar(id));
    }

    @Override
    public Object recuperar(Class entidade, Object id) {
        return inicializar((ParametroRegularizacao) super.recuperar(entidade, id));
    }

    private ParametroRegularizacao inicializar(ParametroRegularizacao parametroRegularizacao) {
        Hibernate.initialize(parametroRegularizacao.getVinculosServicosTributos());
        return parametroRegularizacao;
    }

    public TipoDoctoOficialFacade getTipoDoctoOficialFacade() {
        return tipoDoctoOficialFacade;
    }

    public ParametroRegularizacao buscarParametroRegularizacaoPorExercicio(Exercicio exercicio) {
        Query q = em.createNativeQuery("select pr.* from parametroregularizacao pr where pr.exercicio_id = :idExercicio", ParametroRegularizacao.class);
        q.setParameter("idExercicio", exercicio.getId());
        List<ParametroRegularizacao> resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return resultList.get(0);
        }
        return null;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }
}
