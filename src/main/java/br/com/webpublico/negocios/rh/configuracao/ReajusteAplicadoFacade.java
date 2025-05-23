package br.com.webpublico.negocios.rh.configuracao;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.rh.previdencia.ReajusteAplicado;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by Buzatto on 30/03/2016.
 */
@Stateless
public class ReajusteAplicadoFacade extends AbstractFacade<ReajusteAplicado> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ReajusteAplicadoFacade() {
        super(ReajusteAplicado.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ReajusteAplicado buscarPorExercicio(Exercicio exercicioAplicacao, Exercicio exercicioReferencia) {
        try {
            return (ReajusteAplicado) em.createQuery("select ra from ReajusteAplicado ra where ra.exercicio = :ex and ra.exercicioReferencia = :exReferencia").setParameter("ex", exercicioAplicacao).setParameter("exReferencia", exercicioReferencia).getResultList().get(0);
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Nenhum Reajuste Aplicado encontrado para o exercício de aplicação " + exercicioAplicacao.getAno() + " e exercício de referência " + exercicioReferencia.getAno());
        }
    }
}
