/*
 * Codigo gerado automaticamente em Fri Jul 13 15:21:46 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios.rh.avaliacao;

import br.com.webpublico.entidades.rh.avaliacao.NivelResposta;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class NivelRespostaFacade extends AbstractFacade<NivelResposta> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public NivelRespostaFacade() {
        super(NivelResposta.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }


}
