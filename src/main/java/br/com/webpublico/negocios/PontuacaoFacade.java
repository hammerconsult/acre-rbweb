/*
 * Codigo gerado automaticamente em Thu Mar 31 17:21:35 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.EventoCalculo;
import br.com.webpublico.entidades.Pontuacao;
import br.com.webpublico.entidadesauxiliares.GrupoPontuacao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class PontuacaoFacade extends AbstractFacade<Pontuacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public PontuacaoFacade() {
        super(Pontuacao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public Pontuacao recuperar(Object id) {
        Pontuacao p = em.find(Pontuacao.class, id);
        p.getItens().size();
        p.getAtributos().size();
        return p;
    }

    public void salvarImportacao(GrupoPontuacao grupo) {
        for (Pontuacao ponto : grupo.getPontos()) {
            // validar
        }
    }
}
