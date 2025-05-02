package br.com.webpublico.negocios;

import br.com.webpublico.entidades.PlanoAnualContratacao;
import br.com.webpublico.entidades.PlanoAnualContratacaoGrupoObjetoCompra;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class PlanoAnualContratacaoFacade extends AbstractFacade<PlanoAnualContratacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private GrupoObjetoCompraFacade grupoObjetoCompraFacade;
    @EJB
    private ObjetoCompraFacade objetoCompraFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private PlanoAnualContratacaoObjetoCompraFacade planoAnualContratacaoObjetoCompraFacade;

    public PlanoAnualContratacaoFacade() {
        super(PlanoAnualContratacao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public PlanoAnualContratacao recuperar(Object id) {
        PlanoAnualContratacao entity = super.recuperar(id);
        Hibernate.initialize(entity.getGruposObjetoCompra());
        for (PlanoAnualContratacaoGrupoObjetoCompra grupo : entity.getGruposObjetoCompra()) {
            Hibernate.initialize(grupo.getObjetosCompra());
        }
        return entity;
    }

    @Override
    public PlanoAnualContratacao salvarRetornando(PlanoAnualContratacao entity) {
        if (entity.getNumero() == null){
            entity.setNumero(singletonGeradorCodigo.getProximoCodigo(PlanoAnualContratacao.class, "numero").intValue());
        }
        return super.salvarRetornando(entity);
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public GrupoObjetoCompraFacade getGrupoObjetoCompraFacade() {
        return grupoObjetoCompraFacade;
    }

    public ObjetoCompraFacade getObjetoCompraFacade() {
        return objetoCompraFacade;
    }

    public PlanoAnualContratacaoObjetoCompraFacade getPlanoAnualContratacaoObjetoCompraFacade() {
        return planoAnualContratacaoObjetoCompraFacade;
    }
}
