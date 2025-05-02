/*
 * Codigo gerado automaticamente em Fri Feb 11 09:06:37 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.pesquisagenerica;

import br.com.webpublico.negocios.RecuperadorFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class PesquisaGenericaFacade {
    @EJB
    private RecuperadorFacade recuperadorFacade;


    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;


    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Object> filtar(PesquisaGenerica pesquisaGenerica, int inicio, int max) throws Exception {
        String hql = "select obj  from " + pesquisaGenerica.getClasse().getSimpleName() + " obj ";
        if (!pesquisaGenerica.getItensDePesquisa().isEmpty()) {
            hql += " where ";
        }
        for (ItemPesquisaGenerica itemPesquisaGenerica : pesquisaGenerica.getItensDePesquisa()) {
            if (itemPesquisaGenerica.getCondicao() != null && itemPesquisaGenerica.getValor() != null) {
                hql += "obj." + itemPesquisaGenerica.getCondicao() + " = '" + itemPesquisaGenerica.getValor() + "'";
            }
        }

        if (hql.endsWith(" where ")) {
            hql = hql.replace(" where ", "");
        }

        Query consulta = em.createQuery(hql, pesquisaGenerica.getClasse());
        if (max != 0) {
            consulta.setMaxResults(max + 1);
            consulta.setFirstResult(inicio);
        }
        List<Object> lista = consulta.getResultList();
        //System.out.println(lista.size());
//        for (Object object : lista) {
//            for (Field field : Persistencia.getAtributos(object.getClass())) {
//                field.setAccessible(true);
//                if (recuperadorFacade.isCampoTipoLista(field)) {
//                    field.set(object, initializeAndUnproxy(Persistencia.getAttributeValue(object, field.getName())));
//                }
//            }
//        }
        return lista;

    }
}
