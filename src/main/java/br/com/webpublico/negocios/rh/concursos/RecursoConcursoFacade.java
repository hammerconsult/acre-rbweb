package br.com.webpublico.negocios.rh.concursos;

import br.com.webpublico.entidades.rh.concursos.EtapaConcurso;
import br.com.webpublico.entidades.rh.concursos.ProvaConcurso;
import br.com.webpublico.entidades.rh.concursos.RecursoConcurso;
import br.com.webpublico.enums.TipoEtapa;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ArquivoFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.SistemaFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by Buzatto on 31/08/2015.
 */
@Stateless
public class RecursoConcursoFacade extends AbstractFacade<RecursoConcurso> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConcursoFacade concursoFacade;
    @EJB
    private InscricaoConcursoFacade inscricaoConcursoFacade;
    @EJB
    private FaseConcursoFacade faseConcursoFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ClassificacaoConcursoFacade classificacaoConcursoFacade;

    public RecursoConcursoFacade() {
        super(RecursoConcurso.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConcursoFacade getConcursoFacade() {
        return concursoFacade;
    }

    public InscricaoConcursoFacade getInscricaoConcursoFacade() {
        return inscricaoConcursoFacade;
    }

    public FaseConcursoFacade getFaseConcursoFacade() {
        return faseConcursoFacade;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ClassificacaoConcursoFacade getClassificacaoConcursoFacade() {
        return classificacaoConcursoFacade;
    }

    @Override
    public RecursoConcurso recuperar(Object id) {
        RecursoConcurso rc = super.recuperar(id);
        if (rc.temConcurso()) {
            rc.setConcurso(concursoFacade.recuperarConcursoParaTelaDeRecurso(rc.getConcurso().getId()));
        }
        return rc;
    }

    public void salvar(RecursoConcurso entity) {
        concursoFacade.adicionarEtapaAoConcurso(entity.getConcurso(), TipoEtapa.RECURSO);
        super.salvar(entity);
    }

    @Override
    public void remover(RecursoConcurso entity) {
        EtapaConcurso etapaDoTipoRecurso = entity.getConcurso().getEtapaDoTipo(TipoEtapa.RECURSO);
        if (etapaDoTipoRecurso != null) {
            entity.getConcurso().removeEtapa(etapaDoTipoRecurso);
        }
        concursoFacade.salvar(entity.getConcurso());
        super.remover(entity);
    }

    public RecursoConcurso buscarUltimoRecursoPorProva(ProvaConcurso prova) {
        String sql = "select rec.* from recursoconcurso rec where rec.provaconcurso_id = :idProva order by rec.data desc";
        Query q = em.createNativeQuery(sql, RecursoConcurso.class);
        q.setParameter("idProva", prova.getId());

        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (RecursoConcurso) q.getResultList().get(0);
        }
        return null;
    }
}
