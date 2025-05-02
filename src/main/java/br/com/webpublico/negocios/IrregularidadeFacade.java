/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Irregularidade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Stateless
public class IrregularidadeFacade extends AbstractFacade<Irregularidade> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public IrregularidadeFacade() {
        super(Irregularidade.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Long ultimoCodigoMaisUm() {
        Query q = em.createNativeQuery("SELECT coalesce(max(codigo), 0) + 1 AS codigo FROM Irregularidade ti");
        BigDecimal resultado = (BigDecimal) q.getSingleResult();
        return resultado.longValue();
    }

    public Long ultimoCodigo() {
        String sql = "SELECT max(codigo) FROM Irregularidade";
        Query q = em.createNativeQuery(sql);
        String resultado = (String) q.getResultList().get(0);
        return resultado != null ? new BigDecimal(resultado).longValue() : 1;
    }

    public boolean existeCodigo(Long codigo) {
        String sql = "SELECT * FROM Irregularidade WHERE codigo = :codigo";
        Query q = em.createNativeQuery(sql);
        q.setParameter("codigo", codigo);
        return !q.getResultList().isEmpty();
    }

    public boolean existeCodigoTipoIrregularidade(Irregularidade tipo) {
        String sql = "SELECT * FROM Irregularidade WHERE codigo = :codigo AND id = :id";
        Query q = em.createNativeQuery(sql);
        q.setParameter("codigo", tipo.getCodigo());
        q.setParameter("id", tipo.getId());
        return !q.getResultList().isEmpty();
    }

    public List<Irregularidade> listarEmOrdemAlfabeticaParaTipos(Irregularidade.Tipo... tipos) {
        return buscarEmOrdemAlfabeticaParaTiposPorDescircao("", tipos);
    }

    public List<Irregularidade> buscarEmOrdemAlfabeticaParaTiposPorDescircao(String descricao, Irregularidade.Tipo... tipos){
        Query query = em.createQuery("from Irregularidade where tipoDeIrregularidade in (:tipos) and descricao like :descricao order by descricao");
        query.setParameter("tipos", Arrays.asList(tipos));
        query.setParameter("descricao", "%" + descricao + "%");
        return query.getResultList();
    }
}
