package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AcidenteTrajeto;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by carlos on 05/10/15.
 */
@Stateless
public class AcidenteTrajetoFacade extends AbstractFacade<AcidenteTrajeto> {

    @EJB
    private TipoVeiculoFacade tipoVeiculoFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public AcidenteTrajetoFacade() {
        super(AcidenteTrajeto.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvar(AcidenteTrajeto entity) {
        super.salvar(entity);
    }

    @Override
    public void salvarNovo(AcidenteTrajeto entity) {
        super.salvarNovo(entity);
    }

    @Override
    public Object recuperar(Class entidade, Object id) {
        return super.recuperar(entidade, id);
    }

    public TipoVeiculoFacade getTipoVeiculoFacade() {
        return tipoVeiculoFacade;
    }

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public PessoaFisicaFacade getPessoaFisicaFacade() {
        return pessoaFisicaFacade;
    }

}
