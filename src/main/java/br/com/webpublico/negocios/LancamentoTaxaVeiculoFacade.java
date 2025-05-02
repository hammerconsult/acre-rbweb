package br.com.webpublico.negocios;

import br.com.webpublico.entidades.LancamentoTaxaVeiculo;
import br.com.webpublico.entidades.Veiculo;
import br.com.webpublico.negocios.administrativo.frotas.ParametroFrotasFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 23/09/14
 * Time: 10:28
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class LancamentoTaxaVeiculoFacade extends AbstractFacade<LancamentoTaxaVeiculo> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ParametroFrotasFacade parametroFrotasFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LancamentoTaxaVeiculoFacade() {
        super(LancamentoTaxaVeiculo.class);
    }

    @Override
    public LancamentoTaxaVeiculo recuperar(Object id) {
        LancamentoTaxaVeiculo lancamentoTaxaVeiculo = super.recuperar(id);
        lancamentoTaxaVeiculo.getItensLancamentoTaxaVeiculo().size();
        if (lancamentoTaxaVeiculo.getDetentorArquivoComposicao() != null) {
            lancamentoTaxaVeiculo.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        return lancamentoTaxaVeiculo;
    }

    public boolean existeLancamentoTaxaVeiculo(LancamentoTaxaVeiculo lancamentoTaxaVeiculo) {
        String hql = " select lanc from LancamentoTaxaVeiculo lanc " +
                " where lanc.ano = :ano and lanc.veiculo = :veiculo ";
        if (lancamentoTaxaVeiculo.getId() != null) {
            hql += " and lanc.id <> :id ";
        }
        Query q = em.createQuery(hql);
        q.setParameter("ano", lancamentoTaxaVeiculo.getAno());
        q.setParameter("veiculo", lancamentoTaxaVeiculo.getVeiculo());
        if (lancamentoTaxaVeiculo.getId() != null) {
            q.setParameter("id", lancamentoTaxaVeiculo.getId());
        }
        return q.getResultList() != null && q.getResultList().size() > 0;
    }

    public List<LancamentoTaxaVeiculo> taxasDoVeiculo(Veiculo veiculo) {
        String hql = "select taxas " +
                "   from LancamentoTaxaVeiculo taxas " +
                " where taxas.veiculo = :veiculo ";
        Query q = em.createQuery(hql);
        q.setParameter("veiculo", veiculo);
        if (q.getResultList() != null) {
            List<LancamentoTaxaVeiculo> taxas = q.getResultList();
            for (LancamentoTaxaVeiculo taxa : taxas) {
                taxa.getItensLancamentoTaxaVeiculo().size();
            }
            return taxas;
        }
        return new ArrayList();
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ParametroFrotasFacade getParametroFrotasFacade() {
        return parametroFrotasFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }
}
