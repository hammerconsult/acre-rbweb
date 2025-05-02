/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfiguracaoPlanilhaABRASF;
import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.Exercicio;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * @author Camila
 */
@Stateless
public class ConfiguracaoPlanilhaABRASFFacade extends AbstractFacade<ConfiguracaoPlanilhaABRASF> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private ContaFacade contaFacade;

    public ConfiguracaoPlanilhaABRASFFacade() {
        super(ConfiguracaoPlanilhaABRASF.class);
    }


    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Conta> completarContaReceita(String parte, Exercicio exercicio) {
        return contaFacade.listaFiltrandoReceita(parte, exercicio);
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    public boolean hasConfiguracaoPorExercicio(ConfiguracaoPlanilhaABRASF configuracaoPlanilhaABRASF) {
        String sql = " select configuracao.* from configuracaoplanilhaabrasf configuracao inner join exercicio ex " +
            " on ex.id = configuracao.exercicio_id where ex.ano = :exercicio " +
            (configuracaoPlanilhaABRASF.getTipoContaReceitaAbrasf() != null ?
                " and configuracao.tipoContaReceitaAbrasf = :tipo" : "");
        if (configuracaoPlanilhaABRASF.getId() != null) {
            sql += " and configuracao.id <> :id";
        }
        Query q = em.createNativeQuery(sql, ConfiguracaoPlanilhaABRASF.class);
        q.setParameter("exercicio", configuracaoPlanilhaABRASF.getExercicio().getAno());
        if (configuracaoPlanilhaABRASF.getTipoContaReceitaAbrasf() != null) {
            q.setParameter("tipo", configuracaoPlanilhaABRASF.getTipoContaReceitaAbrasf().name());
        }
        if (configuracaoPlanilhaABRASF.getId() != null) {
            q.setParameter("id", configuracaoPlanilhaABRASF.getId());
        }
        return !q.getResultList().isEmpty();
    }
}


