package br.com.webpublico.negocios;

import br.com.webpublico.entidades.GrupoBem;
import br.com.webpublico.entidades.TipoReducao;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 03/10/14
 * Time: 10:56
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class TipoReducaoFacade extends AbstractFacade<TipoReducao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private GrupoBemFacade grupoBemFacade;

    public TipoReducaoFacade() {
        super(TipoReducao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public GrupoBemFacade getGrupoBemFacade() {
        return grupoBemFacade;
    }

    public TipoReducao buscarTipoReducaoPorGrupoBem(GrupoBem grupoBem, Date dataAtual) {
        try {
            String sql = "select * \n" +
                    "       from tiporeducao\n" +
                    "     where grupobem_id = :grupo\n" +
                    "       and :dataatual between iniciovigencia and coalesce(fimvigencia, :dataatual)";
            return (TipoReducao) em.createNativeQuery(sql,TipoReducao.class)
                    .setParameter("grupo", grupoBem)
                    .setParameter("dataatual", dataAtual, TemporalType.DATE).getSingleResult();
        }catch (NoResultException ex) {
            return null;
        }
    }

    public List<TipoReducao> recuperarListaDeTipoReducaoDoGrupoBem(GrupoBem grupoBem) {
        try {
            String sql = "select * \n" +
                    "       from tiporeducao\n" +
                    "     where grupobem_id = :grupo\n" ;
            return   em.createNativeQuery(sql,TipoReducao.class)
                    .setParameter("grupo", grupoBem).getResultList();
        }catch (NoResultException ex) {
            return new ArrayList<>();
        }
    }

    @Override
    public void remover(TipoReducao entity) {
        super.remover(entity);
    }
}
