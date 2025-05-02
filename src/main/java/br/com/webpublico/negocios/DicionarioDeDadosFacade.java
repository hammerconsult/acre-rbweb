package br.com.webpublico.negocios;

import br.com.webpublico.controle.portaltransparencia.PortalTransparenciaNovoFacade;
import br.com.webpublico.controle.portaltransparencia.entidades.DicionarioDeDadosPortal;
import br.com.webpublico.entidades.DicionarioDeDados;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class DicionarioDeDadosFacade extends AbstractFacade<DicionarioDeDados> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private PortalTransparenciaNovoFacade portalTransparenciaNovoFacade;

    public DicionarioDeDadosFacade() {
        super(DicionarioDeDados.class);
    }

    @Override
    public DicionarioDeDados recuperar(Object id) {
        DicionarioDeDados dicionarioDeDados = em.find(DicionarioDeDados.class, id);
        dicionarioDeDados.getColunas().size();
        return dicionarioDeDados;
    }

    @Override
    public void salvar(DicionarioDeDados entity) {
        super.salvar(entity);
        salvarPortal(entity);
    }

    private void salvarPortal(DicionarioDeDados entity) {
        portalTransparenciaNovoFacade.salvarPortal(new DicionarioDeDadosPortal(entity));
    }

    public boolean hasTipoCadastrado(DicionarioDeDados selecionado) {
        String sql = " select 1 from DicionarioDeDados where tipoDicionarioDeDados = :tipo ";
        if (selecionado.getId() != null) {
            sql += " and id <> :id ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("tipo", selecionado.getTipoDicionarioDeDados().name());
        if (selecionado.getId() != null) {
            q.setParameter("id", selecionado.getId());
        }
        return !q.getResultList().isEmpty();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
