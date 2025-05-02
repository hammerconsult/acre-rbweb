/*
 * Codigo gerado automaticamente em Fri Jul 13 15:21:46 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios.rh.concursos;


import br.com.webpublico.entidades.rh.concursos.*;
import br.com.webpublico.enums.TipoEtapa;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class AvaliacaoProvaConcursoFacade extends AbstractFacade<AvaliacaoProvaConcurso> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConcursoFacade concursoFacade;
    @EJB
    private ClassificacaoConcursoFacade classificacaoConcursoFacade;

    public AvaliacaoProvaConcursoFacade() {
        super(AvaliacaoProvaConcurso.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public AvaliacaoProvaConcurso recuperar(Object id) {
        AvaliacaoProvaConcurso avaliacao = super.recuperar(id);
        avaliacao.getNotas().size();
        return avaliacao;
    }

    public AvaliacaoProvaConcurso recuperarAvaliacaoComNotas(Object id) {
        AvaliacaoProvaConcurso aval = em.find(AvaliacaoProvaConcurso.class, id);
        aval.getNotas().size();
        aval.getConcurso().getCargos().size();
        aval.getProva().getFaseConcurso().getProvas().size();
        return aval;
    }

    public Integer getProximaOrdemAvaliacao(Long provaId) {
        String sql = "select max(a.ordem) from avaliacaoprovaconcurso a where a.prova_id = :prova_id";
        Query q = em.createNativeQuery(sql);
        q.setParameter("prova_id", provaId);
        Integer resultado = (Integer) q.getSingleResult();
        return resultado == null ? 1 : resultado;
    }

    public AvaliacaoProvaConcurso buscarAvaliacaoPorProva(ProvaConcurso provaConcurso) {
        String sql = "select ava.* from avaliacaoprovaconcurso ava where ava.concurso_id = :idConcurso and ava.prova_id = :idProva";
        Query q = em.createNativeQuery(sql, AvaliacaoProvaConcurso.class);
        q.setParameter("idConcurso", provaConcurso.getFaseConcurso().getConcurso().getId());
        q.setParameter("idProva", provaConcurso.getId());

        if (q.getResultList().isEmpty()) {
            throw new ExcecaoNegocioGenerica("Não foi encontrado avaliação da prova " + provaConcurso.getNome() + " para o concurso " + provaConcurso.getFaseConcurso().getConcurso().getNome());
        } else {
            AvaliacaoProvaConcurso apc = (AvaliacaoProvaConcurso) q.getSingleResult();
            return recuperar(apc.getId());
        }
    }

    @Override
    public void salvarNovo(AvaliacaoProvaConcurso avaliacao) {
        concursoFacade.adicionarEtapaAoConcurso(avaliacao.getConcurso(), TipoEtapa.AVALIACAO_DE_PROVAS);
        super.salvarNovo(avaliacao);
    }

    public NotaCandidatoConcurso buscarNotaPorProvaAndCandidato(ProvaConcurso prova, InscricaoConcurso candidato) {
        AvaliacaoProvaConcurso avaliacao = buscarAvaliacaoPorProva(prova);
        for (NotaCandidatoConcurso nota : avaliacao.getNotas()) {
            if (nota.getInscricao().equals(candidato)) {
                return nota;
            }
        }
        return null;
    }
}
