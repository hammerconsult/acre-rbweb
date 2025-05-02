package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.TipoVistoria;


import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * @author daniel
 */
@Stateless
public class TipoVistoriaFacade extends AbstractFacade<TipoVistoria> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public TipoVistoriaFacade() {
        super(TipoVistoria.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoVistoria recuperaPorCodigo(Long codigo) {
        Query q = em.createQuery("from TipoVistoria v where v.codigo = :codigo");
        q.setParameter("codigo", codigo);
        List<TipoVistoria> resultado = q.getResultList();
        if (resultado.isEmpty()) {
            return null;
        } else {
            return resultado.get(0);
        }
    }

    public TipoVistoria recuperaPorDescricao(String descricao) {
        Query q = em.createQuery("from TipoVistoria v where lower(v.descricao) like :descricao");
        q.setParameter("descricao", descricao.trim().toLowerCase());
        List<TipoVistoria> resultado = q.getResultList();
        if (resultado.isEmpty()) {
            return null;
        } else {
            return resultado.get(0);
        }
    }

    public boolean jaExisteCodigo(TipoVistoria vistoria) {
        if (vistoria == null || vistoria.getCodigo() == null) {
            return false;
        }
        String hql = "from TipoVistoria v where v.codigo = :codigo";
        if (vistoria.getId() != null) {
            hql += " and v <> :vistoria";
        }
        Query q = em.createQuery(hql);
        q.setParameter("codigo", vistoria.getCodigo());
        if (vistoria.getId() != null) {
            q.setParameter("vistoria", vistoria);
        }
        return !q.getResultList().isEmpty();
    }

    public boolean jaExisteDescricao(TipoVistoria vistoria) {
        if (vistoria == null || vistoria.getCodigo() == null) {
            return false;
        }
        String hql = "from TipoVistoria v where lower(v.descricao) like :descricao";
        if (vistoria.getId() != null) {
            hql += " and v <> :vistoria";
        }
        Query q = em.createQuery(hql);
        q.setParameter("descricao", vistoria.getDescricao().toLowerCase().trim());
        if (vistoria.getId() != null) {
            q.setParameter("vistoria", vistoria);
        }
        return !q.getResultList().isEmpty();
    }


    public List<TipoVistoria> listaFiltrandoSemCNAE(String s, String... atributos) {
        String hql = "from TipoVistoria v where v.cnae is null and ";
        for (String atributo : atributos) {
            hql += "lower(v." + atributo + ") like :filtro OR ";
        }
        hql = hql.substring(0, hql.length() - 3);
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(50);
        return q.getResultList();
    }

    @Override
    public TipoVistoria recuperar(Object id) {
        TipoVistoria retorno = (TipoVistoria) em.find(TipoVistoria.class, id);
         return retorno;
    }



    public List<TipoVistoria> tipoVistoriaPorEconomicoCnae(CadastroEconomico cadastroEconomico) {
        String sql = "select tv.* from tipovistoria tv "
                + "inner join tipovistoriacnae tvc on tv.id = tvc.tipovistoria_id "
                + "inner join cnae on cnae.id = tvc.cnae_id "
                + "inner join economicocnae on economicocnae.cnae_id = cnae.id "
                + "and economicocnae.cadastroeconomico_id = :id";
        Query q = em.createNativeQuery(sql, TipoVistoria.class);
        q.setParameter("id", cadastroEconomico.getId());
        return q.getResultList();
    }
}
