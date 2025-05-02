/*
 * Codigo gerado automaticamente em Fri Jul 13 15:21:46 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios.rh.avaliacao;

import br.com.webpublico.entidades.rh.avaliacao.MontagemAvaliacao;
import br.com.webpublico.entidades.rh.avaliacao.QuestaoAvaliacao;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class MontagemAvaliacaoFacade extends AbstractFacade<MontagemAvaliacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public MontagemAvaliacaoFacade() {
        super(MontagemAvaliacao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public MontagemAvaliacao recuperar(Object id) {
        MontagemAvaliacao recuperar = super.recuperar(id);
        recuperar.getQuestoes().size();
        for (QuestaoAvaliacao questao : recuperar.getQuestoes()) {
            questao.getAlternativas().size();
        }
        return recuperar;
    }
}
