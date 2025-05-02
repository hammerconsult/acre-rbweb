package br.com.webpublico.negocios.rh.censo;

import br.com.webpublico.entidades.Dependente;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.rh.censo.VinculoFPCenso;
import br.com.webpublico.negocios.*;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class VinculoFPCensoFacade extends AbstractFacade<VinculoFPCenso> {

    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private DependenteFacade dependenteFacade;
    @EJB
    private TipoDependenteFacade tipoDependenteFacade;
    @EJB
    private GrauDeParentescoFacade grauDeParentescoFacade;
    @EJB
    private PessoaFacade pessoaFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public VinculoFPCensoFacade() {
        super(VinculoFPCenso.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public VinculoFPFacade getVinculoFPFacade() {
        return vinculoFPFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public List<Dependente> getDependentes(Pessoa pessoa) {
        return this.dependenteFacade.dependentesPorResponsavel(pessoa);
    }

    public TipoDependenteFacade getTipoDependenteFacade() {
        return tipoDependenteFacade;
    }

    public GrauDeParentescoFacade getGrauDeParentescoFacade() {
        return grauDeParentescoFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    @Override
    public VinculoFPCenso recuperar(Object id) {
        VinculoFPCenso vinculo = super.recuperar(id);
        Hibernate.initialize(vinculo.getDependentes());
        return vinculo;
    }

    public <T> void salvarDependencias(T entity) {
        em.merge(entity);
    }
}
