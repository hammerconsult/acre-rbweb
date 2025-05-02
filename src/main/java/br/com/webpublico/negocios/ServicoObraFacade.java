package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ServicoObra;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by venom on 10/09/14.
 */
@Stateless
public class ServicoObraFacade extends AbstractFacade<ServicoObra> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager entityManager;

    public ServicoObraFacade() {
        super(ServicoObra.class);
    }


    @Override
    public List<ServicoObra> lista() {
        return super.lista();
    }

    @Override
    public ServicoObra recuperar(Object id) {
        ServicoObra so = super.recuperar(id);
        so.getFilhos().size();
        return so;
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    public List<ServicoObra> listaRecuperados() {
        List<ServicoObra> servicos = new ArrayList<>();
        for (ServicoObra so : getServicosReferencia()) {
            servicos.add(recuperar(so.getId()));
        }
        return servicos;
    }

    public ServicoObra getCloneInstance(ServicoObra so) {
        try {
            so = recuperar(so.getId());
            ServicoObra servico = new ServicoObra(so);
            return servico;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private List<ServicoObra> getServicosReferencia() {
        return entityManager.createQuery("select s from ServicoObra s where s.tipoServicoObra = 'REFERENCIA' ").getResultList();
    }

    public List<ServicoObra> getServicosSuperiores() {
        return entityManager.createQuery(" select pai from ServicoObra pai where pai.superior is null").getResultList();
    }

    public List<ServicoObra> getFilhosDoSuperior(ServicoObra servico) {
        return entityManager.createQuery(" select filho from ServicoObra filho where filho.superior = :pai").setParameter("pai", servico).getResultList();

    }
}
