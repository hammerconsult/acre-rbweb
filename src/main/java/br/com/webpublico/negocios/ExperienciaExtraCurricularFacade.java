package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ExperienciaExtraCurricular;
import br.com.webpublico.entidades.VinculoFP;
import com.google.common.collect.Lists;
import org.springframework.util.Assert;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by mga on 01/06/2017.
 */
@Stateless
public class ExperienciaExtraCurricularFacade extends AbstractFacade<ExperienciaExtraCurricular> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private TipoComissaoFacade tipoComissaoFacade;
    @EJB
    private AtribuicaoComissaoFacade atribuicaoComissaoFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;


    public ExperienciaExtraCurricularFacade() {
        super(ExperienciaExtraCurricular.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<ExperienciaExtraCurricular> buscarTodasExperienciasExtraCurricularesPorContrato(VinculoFP vinculoFP) {
        Assert.notNull(vinculoFP);
        String sql = " " +
            "  select exp.* " +
            "   from experienciaextracurricular exp " +
            "  where exp.vinculofp_id = :idVinculo " +
            "  order by exp.inicioVigencia ";
        Query q = em.createNativeQuery(sql, ExperienciaExtraCurricular.class);
        q.setParameter("idVinculo", vinculoFP.getId());
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

    public TipoComissaoFacade getTipoComissaoFacade() {
        return tipoComissaoFacade;
    }

    public AtribuicaoComissaoFacade getAtribuicaoComissaoFacade() {
        return atribuicaoComissaoFacade;
    }

    public VinculoFPFacade getVinculoFPFacade() {
        return vinculoFPFacade;
    }
}
