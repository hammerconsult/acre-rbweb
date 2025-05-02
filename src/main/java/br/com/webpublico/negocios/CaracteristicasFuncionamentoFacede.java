/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CaracFuncionamento;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Heinz
 */
@Stateless
public class CaracteristicasFuncionamentoFacede extends AbstractFacade<CaracFuncionamento> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CaracteristicasFuncionamentoFacede() {
        super(CaracFuncionamento.class);
    }

    public boolean existeCodigoCategoria(Long codigo) {
        String sql = "SELECT * FROM caracfuncionamento WHERE codigo = :codigo";
        Query q = em.createNativeQuery(sql);
        q.setParameter("codigo", codigo);
        return !q.getResultList().isEmpty();
    }

    public boolean isDescricaoCurtaEmUso(CaracFuncionamento cf) {
        if (cf == null || cf.getDescricaoCurta() == null || cf.getDescricaoCurta().trim().isEmpty()) {
            return false;
        }
        String hql = "from CaracFuncionamento cf where lower(cf.descricaoCurta) = :descricao";
        if (cf.getId() != null) {
            hql += " and cf != :cf";
        }
        Query q = em.createQuery(hql);
        q.setParameter("descricao", cf.getDescricaoCurta().toLowerCase().trim());
        if (cf.getId() != null) {
            q.setParameter("cf", cf);
        }
        return !q.getResultList().isEmpty();
    }

    public boolean isDescricaoEmUso(CaracFuncionamento cf) {
        if (cf == null || cf.getDescricao() == null || cf.getDescricao().trim().isEmpty()) {
            return false;
        }
        String hql = "SELECT count(id) FROM CaracFuncionamento cf WHERE lower(cf.descricao) LIKE :descricao";
        if (cf.getId() != null) {
            hql += " AND cf.id != :cf";
        }
        Query q = em.createNativeQuery(hql);
        q.setParameter("descricao", cf.getDescricao().toLowerCase().trim());
        if (cf.getId() != null) {
            q.setParameter("cf", cf.getId());
        }
        return ((BigDecimal) q.getSingleResult()).intValue() != 0;
    }

    public Long sugereCodigo() {
        Query q = em.createNativeQuery("SELECT coalesce(max(codigo), 0) + 1 codigo FROM CaracFuncionamento");
        BigDecimal retorno = (BigDecimal) q.getSingleResult();
        return retorno.longValue();
    }

    public List<CaracFuncionamento> listaOrdenadoPorCodigo() {
        Query q = em.createQuery("select c from CaracFuncionamento c order by c.codigo");
        return q.getResultList();
    }

}
