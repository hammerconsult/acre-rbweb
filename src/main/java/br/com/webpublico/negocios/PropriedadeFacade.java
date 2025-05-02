/*
 * Codigo gerado automaticamente em Tue Feb 22 10:41:19 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CadastroImobiliario;
import br.com.webpublico.entidades.Propriedade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class PropriedadeFacade extends AbstractFacade<Propriedade> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PropriedadeFacade() {
        super(Propriedade.class);
    }

    public List<Propriedade> listaPropriedadesPorImovel(List<CadastroImobiliario> imoveis) {
        if (imoveis == null || imoveis.isEmpty()) {
            return null;
        }
        Query q = em.createQuery("from Propriedade p where p.imovel in (:imoveis)");
        q.setParameter("imoveis", imoveis);
        return q.getResultList();

    }

    public List<Propriedade> listaPropriedadesVigentesPorImovel(List<CadastroImobiliario> imoveis) {
        if (imoveis == null || imoveis.isEmpty()) {
            return null;
        }
        Query q = em.createQuery("from Propriedade p where p.imovel in (:imoveis) and p.finalVigencia is null");
        q.setParameter("imoveis", imoveis);
        return q.getResultList();
    }

    public List<Propriedade> buscarFiltrandoPropriedade(String parte) {
        String sql = "SELECT distinct prop.*  " +
            "FROM Propriedade prop " +
            "  INNER JOIN PESSOA p ON p.id = prop.PESSOA_ID " +
            "  LEFT JOIN PESSOAFISICA pf ON pf.id = p.ID " +
            "  LEFT JOIN PESSOAJURIDICA pj ON pj.id = p.id " +
            "WHERE (replace(replace(pf.cpf, '.', ''), '-', '') like :parte " +
            "       OR upper(pf.NOME) like :parte " +
            "       OR replace(replace(replace(pj.cnpj, '.', ''), '-', ''), '/', '') like :parte " +
            "       OR upper(pj.RAZAOSOCIAL) like :parte ) ";
        Query q = em.createNativeQuery(sql, Propriedade.class);
        q.setParameter("parte", "%" + parte.toUpperCase().trim() + "%");
        q.setMaxResults(10);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }
}
