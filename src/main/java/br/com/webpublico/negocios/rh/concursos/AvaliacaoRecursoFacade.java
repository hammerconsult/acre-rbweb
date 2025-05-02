package br.com.webpublico.negocios.rh.concursos;

import br.com.webpublico.entidades.rh.concursos.*;
import br.com.webpublico.enums.SituacaoRecurso;
import br.com.webpublico.enums.TipoEtapa;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.SistemaFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by Buzatto on 02/09/2015.
 */
@Stateless
public class AvaliacaoRecursoFacade extends AbstractFacade<AvaliacaoRecurso> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConcursoFacade concursoFacade;
    @EJB
    private FaseConcursoFacade faseConcursoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private AvaliacaoProvaConcursoFacade avaliacaoProvaConcursoFacade;
    @EJB
    private ClassificacaoConcursoFacade classificacaoConcursoFacade;

    public AvaliacaoRecursoFacade() {
        super(AvaliacaoRecurso.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConcursoFacade getConcursoFacade() {
        return concursoFacade;
    }

    public FaseConcursoFacade getFaseConcursoFacade() {
        return faseConcursoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public AvaliacaoProvaConcursoFacade getAvaliacaoProvaConcursoFacade() {
        return avaliacaoProvaConcursoFacade;
    }

    public ClassificacaoConcursoFacade getClassificacaoConcursoFacade() {
        return classificacaoConcursoFacade;
    }

    @Override
    public AvaliacaoRecurso recuperar(Object id) {
        AvaliacaoRecurso ar = super.recuperar(id);
        ar.setConcurso(concursoFacade.recuperarConcursoParaTelaDeAvaliacaoRecurso(ar.getConcurso().getId()));
        if (ar.temFase()) {
            ar.setFaseConcurso(faseConcursoFacade.recuperarFaseComProvas(ar.getFaseConcurso().getId()));
        }
        return ar;
    }

    public void salvar(AvaliacaoRecurso entity, AvaliacaoProvaConcurso avaliacaoAtualizada) {
        atualizarAvaliacaoAndClassificacao(avaliacaoAtualizada);
        concursoFacade.adicionarEtapaAoConcurso(entity.getConcurso(), TipoEtapa.AVALIACAO_DE_RECURSO);
        atualizarSituacaoDoRecurso(entity);
        salvarAvaliacaoRecurso(entity);
    }

    @Override
    public void remover(AvaliacaoRecurso entity) {
        entity.getRecursoConcurso().setSituacaoRecurso(SituacaoRecurso.EM_ANDAMENTO);
        entity = em.merge(entity);
        concursoFacade.removerEtapaDoConcurso(entity.getConcurso(), TipoEtapa.AVALIACAO_DE_RECURSO);
        super.remover(entity);
    }

    public void atualizarAvaliacaoAndClassificacao(AvaliacaoProvaConcurso avaliacaoAtualizada) {
        if (avaliacaoAtualizada != null) {
            AvaliacaoProvaConcurso avaliacaoNesteContexto = em.find(AvaliacaoProvaConcurso.class, avaliacaoAtualizada.getId());
            avaliacaoNesteContexto.getNotas().size();
            for (NotaCandidatoConcurso notaCandidatoConcurso : avaliacaoAtualizada.getNotas()) {
                if (notaCandidatoConcurso.getNovaNota() != null && !notaCandidatoConcurso.getNovaNota().toString().trim().isEmpty()) {
                    avaliacaoNesteContexto.getNotas().get(avaliacaoNesteContexto.getNotas().indexOf(notaCandidatoConcurso)).setNota(notaCandidatoConcurso.getNovaNota());
                }
            }
            em.merge(avaliacaoNesteContexto);
            em.flush();
            CargoConcurso cc = classificacaoConcursoFacade.gerarOrAtualizarClassificacaoDesteCargo(avaliacaoAtualizada.getProva().getCargoConcurso());
            em.merge(cc.getClassificacaoConcurso());
            em.merge(cc);
        }
    }

    public void salvarAvaliacaoRecurso(AvaliacaoRecurso entity) {
        if (entity.getId() == null) {
            super.salvarNovo(entity);
        } else {
            super.salvar(entity);
        }
    }

    public void atualizarSituacaoDoRecurso(AvaliacaoRecurso entity) {
        entity.setRecursoConcurso(em.find(RecursoConcurso.class, entity.getRecursoConcurso().getId()));
        if (entity.isAceito()) {
            entity.getRecursoConcurso().setSituacaoRecurso(SituacaoRecurso.ACEITO);
        } else {
            entity.getRecursoConcurso().setSituacaoRecurso(SituacaoRecurso.NAO_ACEITO);
        }
    }

    public List<AvaliacaoRecurso> buscarAvaliacoesPorConcurso(Concurso concurso) {
        return em.createQuery("select av from AvaliacaoRecurso av where av.concurso = :con").setParameter("con", concurso).getResultList();
    }
}
