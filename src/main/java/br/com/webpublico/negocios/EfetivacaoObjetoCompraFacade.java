package br.com.webpublico.negocios;

import br.com.webpublico.entidades.EfetivacaoObjetoCompra;
import br.com.webpublico.entidades.ObjetoCompra;
import br.com.webpublico.enums.SituacaoObjetoCompra;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by HardRock on 30/03/2017.
 */
@Stateless
public class EfetivacaoObjetoCompraFacade extends AbstractFacade<EfetivacaoObjetoCompra> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ObjetoCompraFacade objetoCompraFacade;
    @EJB
    private GrupoObjetoCompraFacade grupoObjetoCompraFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public EfetivacaoObjetoCompraFacade() {
        super(EfetivacaoObjetoCompra.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public EfetivacaoObjetoCompra recuperar(Object id) {
        EfetivacaoObjetoCompra obj = super.recuperar(id);
        obj.getObjetosCompra().size();
        return obj;
    }

    public void deferir(EfetivacaoObjetoCompra entity) {
        entity = salvarComMerge(entity);
        alterarSituacaoObjetoCompra(entity, SituacaoObjetoCompra.DEFERIDO, Boolean.TRUE);
    }

    private EfetivacaoObjetoCompra salvarComMerge(EfetivacaoObjetoCompra entity) {
        return em.merge(entity);
    }

    public void indeferir(EfetivacaoObjetoCompra entity) {
        entity = salvarComMerge(entity);
        alterarSituacaoObjetoCompra(entity, SituacaoObjetoCompra.INDEFERIDO, Boolean.FALSE);
    }

    private void alterarSituacaoObjetoCompra(EfetivacaoObjetoCompra entity, SituacaoObjetoCompra situacao, Boolean ativarInativar) {
        for (ObjetoCompra objetoCompra : entity.getObjetosCompra()) {
            objetoCompra.setEfetivacaoObjetoCompra(entity);
            objetoCompra.setAtivo(ativarInativar);
            objetoCompra.setSituacaoObjetoCompra(situacao);
            objetoCompraFacade.salvar(objetoCompra);
        }
    }


    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ObjetoCompraFacade getObjetoCompraFacade() {
        return objetoCompraFacade;
    }

    public GrupoObjetoCompraFacade getGrupoObjetoCompraFacade() {
        return grupoObjetoCompraFacade;
    }
}
