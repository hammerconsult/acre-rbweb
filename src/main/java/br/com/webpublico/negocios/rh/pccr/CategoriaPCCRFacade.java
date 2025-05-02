package br.com.webpublico.negocios.rh.pccr;

import br.com.webpublico.entidades.Cargo;

import br.com.webpublico.entidades.rh.pccr.CargoCategoriaPCCR;
import br.com.webpublico.entidades.rh.pccr.CategoriaPCCR;
import br.com.webpublico.enums.TipoPCS;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CargoFacade;
import org.joda.time.DateTime;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 27/06/14
 * Time: 15:08
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class CategoriaPCCRFacade extends AbstractFacade<CategoriaPCCR> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private PlanoCargosSalariosPCCRFacade planoCargosSalariosPCCRFacade;
    @EJB
    private EnquadramentoPCCRFacade enquadramentoPCCRFacade;
    @EJB
    private ProgressaoPCCRFacade progressaoPCCRFacade;
    @EJB
    private CargoFacade cargoFacade;


    public CategoriaPCCRFacade() {
        super(CategoriaPCCR.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<CategoriaPCCR> findCategoriasByCargo(Cargo cargo) {
        Query q = em.createQuery("select cat from CategoriaPCCR  cat inner join cat.cargosCategoriaPCCR cargos left join cat.filhos f" +
                " where cargos.cargo = :cargo and :data between cargos.inicioVigencia and coalesce(cargos.finalVigencia,:data) order by cat.descricao ");
        q.setParameter("data", new Date());
        q.setParameter("cargo", cargo);
        //System.out.println("Carregou categoria");
        return q.getResultList();
    }

    public void preencherCategoriaPCCRParaTest() {
        CategoriaPCCR categoriaPCCR = new CategoriaPCCR();
        categoriaPCCR.setDescricao("G3 SUPERIOR N5");
//        categoriaPCCR.setProgressaoPCCR(progressaoPCCRFacade.lista().get(0));
        categoriaPCCR.setPlanoCargosSalariosPCCR(categoriaPCCR.getPlanoCargosSalariosPCCR());
        preencherCargos(categoriaPCCR);

    }

    private void preencherCargos(CategoriaPCCR categoriaPCCR) {
        for (Cargo c : cargoFacade.listaFiltrando(TipoPCS.QUADRO_EFETIVO, "auditor fiscal de meio")) {
            CargoCategoriaPCCR cargos = new CargoCategoriaPCCR();
            cargos.setInicioVigencia(new DateTime(2010, 1, 1, 1, 1).toDate());
            cargos.setCargo(c);
            cargos.setCategoriaPCCR(categoriaPCCR);
            em.persist(cargos);
            categoriaPCCR.getCargosCategoriaPCCR().add(cargos);
        }
        em.merge(categoriaPCCR);
    }

    @Override
    public CategoriaPCCR recuperar(Object id) {
        CategoriaPCCR cat = em.find(CategoriaPCCR.class, id);
        for (CategoriaPCCR filho : cat.getFilhos()) {
            filho.getCargosCategoriaPCCR().size();
        }
        return cat;
    }
}
