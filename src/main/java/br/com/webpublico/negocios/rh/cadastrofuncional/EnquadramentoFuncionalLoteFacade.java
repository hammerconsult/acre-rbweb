package br.com.webpublico.negocios.rh.cadastrofuncional;


import br.com.webpublico.entidades.EnquadramentoFuncional;
import br.com.webpublico.entidades.rh.cadastrofuncional.EnquadramentoFuncionalLote;
import br.com.webpublico.entidades.rh.cadastrofuncional.EnquadramentoFuncionalLoteItem;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CargoFacade;
import br.com.webpublico.negocios.EnquadramentoFuncionalFacade;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class EnquadramentoFuncionalLoteFacade extends AbstractFacade<EnquadramentoFuncionalLote> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EnquadramentoFuncionalFacade enquadramentoFuncionalFacade;
    @EJB
    private CargoFacade cargoFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EnquadramentoFuncionalLoteFacade() {
        super(EnquadramentoFuncionalLote.class);
    }

    @Override
    public EnquadramentoFuncionalLote recuperar(Object id) {
        EnquadramentoFuncionalLote a = em.find(EnquadramentoFuncionalLote.class, id);
        Hibernate.initialize(a.getItens());
        return a;
    }

    @Override
    public void salvar(EnquadramentoFuncionalLote entity) {
        super.salvar(entity);
    }

    @Override
    public void salvarNovo(EnquadramentoFuncionalLote entity) {
        for (EnquadramentoFuncionalLoteItem item : entity.getItens()) {
            enquadramentoFuncionalFacade.salvar(item.getEnquadramentoFuncAntigo());
            enquadramentoFuncionalFacade.salvarNovo(item.getEnquadramentoFuncNovo());
        }
        super.salvarNovo(entity);
    }

    @Override
    public void remover(EnquadramentoFuncionalLote entity) {
        EnquadramentoFuncionalLote lote = recuperar(entity.getId());
        List<EnquadramentoFuncional> itensParaRemocao = Lists.newLinkedList();
        for (EnquadramentoFuncionalLoteItem item : lote.getItens()) {

            itensParaRemocao.add(item.getEnquadramentoFuncNovo());

            item.getEnquadramentoFuncAntigo().setFinalVigencia(null);
            enquadramentoFuncionalFacade.salvar(item.getEnquadramentoFuncAntigo());
        }
        super.remover(entity);
        for (EnquadramentoFuncional remover : itensParaRemocao) {
            enquadramentoFuncionalFacade.remove(remover);
        }
    }

    public CargoFacade getCargoFacade() {
        return cargoFacade;
    }
}
