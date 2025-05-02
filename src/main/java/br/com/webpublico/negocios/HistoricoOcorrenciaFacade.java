/*
 * Codigo gerado automaticamente em Fri Feb 11 09:06:37 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Ocorrencia;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.lang.reflect.Field;
import java.util.List;

@Stateless
public class HistoricoOcorrenciaFacade extends AbstractFacade<Ocorrencia> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public HistoricoOcorrenciaFacade() {
        super(Ocorrencia.class);
    }

    @Override
    public List<Ocorrencia> listaFiltrandoX(String s, int inicio, int max, Field... atributos) {
        Long longo = 0l;
        boolean eNumero = false;
        try {
            longo = Long.parseLong(s);
            eNumero = true;
        } catch (NumberFormatException ex) {
            eNumero = false;
        }

        String hql = "from Ocorrencia obj";

        Query q = getEntityManager().createQuery(hql);
        q.setMaxResults(max + 1);
        q.setFirstResult(inicio);
        return q.getResultList();
    }
}
