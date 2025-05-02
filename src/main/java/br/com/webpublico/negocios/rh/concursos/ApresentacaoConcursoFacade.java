/*
 * Codigo gerado automaticamente em Fri Jul 13 15:21:46 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios.rh.concursos;


import br.com.webpublico.entidades.rh.concursos.ApresentacaoConcurso;
import br.com.webpublico.entidades.rh.concursos.ClassificacaoConcurso;
import br.com.webpublico.entidades.rh.concursos.ClassificacaoInscricao;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ApresentacaoConcursoFacade extends AbstractFacade<ApresentacaoConcurso> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ApresentacaoConcursoFacade() {
        super(ApresentacaoConcurso.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }


    public ApresentacaoConcurso salvar(ApresentacaoConcurso apresentacao, ClassificacaoConcurso classificacao) {
        classificacao = em.merge(classificacao);
        apresentacao = em.merge(apresentacao);
        return apresentacao;
    }

    public ClassificacaoInscricao salvarClassificacaoInscricao(ClassificacaoInscricao ci) {
        ci = em.merge(ci);
        return ci;
    }
}
