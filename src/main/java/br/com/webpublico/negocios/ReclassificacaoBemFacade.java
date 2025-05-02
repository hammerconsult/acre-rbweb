package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Bem;
import br.com.webpublico.entidades.EstadoBem;
import br.com.webpublico.entidades.LoteReclassificacaoBem;
import br.com.webpublico.entidades.ReclassificacaoBem;
import br.com.webpublico.enums.SituacaoEventoBem;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 30/01/14
 * Time: 19:15
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class ReclassificacaoBemFacade extends AbstractFacade<LoteReclassificacaoBem> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private IntegradorPatrimonialContabilFacade integradorPatrimonialContabilFacade;

    public ReclassificacaoBemFacade() {
        super(LoteReclassificacaoBem.class);
    }

    @Override
    public LoteReclassificacaoBem recuperar(Object id) {
        LoteReclassificacaoBem lote = super.recuperar(id);
        lote.getReclassificacoes().size();
        return lote;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public BemFacade getBemFacade() {
        return bemFacade;
    }

    public void salvar(LoteReclassificacaoBem loteReclassificacaoBem) {

        for (ReclassificacaoBem rb : loteReclassificacaoBem.getReclassificacoes()) {
            rb.setSituacaoEventoBem(SituacaoEventoBem.FINALIZADO);
            rb.setEstadoInicial(bemFacade.recuperarUltimoEstadoDoBem(rb.getBem()));

            EstadoBem estadoResultante = bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(rb.getEstadoInicial());
            estadoResultante.setGrupoObjetoCompra(loteReclassificacaoBem.getGrupoObjetoCompraGrupoBem().getGrupoObjetoCompra());
            estadoResultante.setGrupoBem(loteReclassificacaoBem.getGrupoObjetoCompraGrupoBem().getGrupoBem());

            rb.setEstadoResultante(em.merge(estadoResultante));
            rb.setLoteReclassificacaoBem(loteReclassificacaoBem);
        }
        em.merge(loteReclassificacaoBem);
    }

    public List<ReclassificacaoBem> criarListaReclassificacaoPesquisaDeBens(List<Bem> bens, LoteReclassificacaoBem lote) {
        List<ReclassificacaoBem> reclassificacaoBems = new ArrayList();
        if (bens != null) {
            for (Bem bem : bens) {
                reclassificacaoBems.add(new ReclassificacaoBem(bem, em.find(EstadoBem.class, bem.getIdUltimoEstado()), lote, sistemaFacade.getDataOperacao()));
            }
        }
        return reclassificacaoBems;
    }
}
