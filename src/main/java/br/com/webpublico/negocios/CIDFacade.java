/*
 * Codigo gerado automaticamente em Mon Sep 05 08:40:28 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CID;
import br.com.webpublico.entidades.InvalidezCid;
import br.com.webpublico.pessoa.dto.CidDTO;
import com.google.common.collect.Lists;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class CIDFacade extends AbstractFacade<CID> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CIDFacade() {
        super(CID.class);
    }

    public void alterar(CID entity) throws Exception {
        if (!recuperarCidPorCodigo(entity.getCodigoDaCid()).equals(entity)) {
            throw new Exception("Já existe um CID cadastrado com o código informado. <b>Informe um código diferente e tente novamente.</b>");
        }

        super.salvar(entity);
    }

    public void inserirNovo(CID entity) throws Exception {
        if (recuperarCidPorCodigo(entity.getCodigoDaCid()) != null) {
            throw new Exception("Já existe um CID cadastrado com o código informado. <b>Informe um código diferente e tente novamente.</b>");
        }

        super.salvarNovo(entity);
    }

    public CID recuperarCidPorCodigo(String codigo) {
        String hql = "from CID c where c.codigoDaCid = :codigo";
        Query q = em.createQuery(hql);
        q.setParameter("codigo", codigo);
        q.setMaxResults(1);
        try {
            return (CID) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public boolean existeCodigo(CID cid) {
        String hql = " from CID cid where cid.codigoDaCid = :parametroCodigo ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("parametroCodigo", cid.getCodigoDaCid());

        List<CID> lista = new ArrayList<CID>();
        lista = q.getResultList();

        if (lista.contains(cid)) {
            return false;
        } else {
            return (!q.getResultList().isEmpty());
        }
    }

    public boolean isCodigoCidValido(String codigoDaCid) {

        String sql = "select * from CID where lower(CODIGODACID) = :codigoDaCid";
        Query q = getEntityManager().createNativeQuery(sql);
        q.setParameter("codigoDaCid", codigoDaCid.toLowerCase().trim());

        return (!q.getResultList().isEmpty());

    }

    public List<InvalidezCid> findInvalidezCid(String parte) {
        Query q = em.createQuery("select new br.com.webpublico.entidades.InvalidezCid(cid) from CID cid where" +
                " lower(cid.codigoDaCid) like :parte or lower(cid.descricao) like :parte ");
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setMaxResults(10);
        List<InvalidezCid> list = q.getResultList();
//        if (invalidezAposentado != null) {
//            for (InvalidezCid i : list) {
//                i.setInvalidezAposentado(invalidezAposentado);
//            }
//        }
        return list;
    }

    public List<CID> buscarTodosCid(String parte) {
        String sql = " select * from CID WHERE lower(descricao) like :filter or lower(codigodacid) like :filter or lower(codigodacid) || ' - ' || lower(descricao) like :filter ";
        Query q = em.createNativeQuery(sql, CID.class);
        q.setParameter("filter", "%" + parte.toLowerCase().trim() + "%");
        return q.getResultList();
    }

    public List<CidDTO> buscarTodosCidDTO(String parte) {
        List<CID> cids = buscarTodosCid(parte);
        List<CidDTO> retorno = Lists.newArrayList();
        if (cids != null && !cids.isEmpty()) {
            retorno.addAll(CID.toCidDTOs(cids));
        }
        return retorno;
    }
}
