/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Licitacao;
import br.com.webpublico.entidades.RecursoLicitacao;
import br.com.webpublico.entidades.StatusLicitacao;
import br.com.webpublico.enums.TipoSituacaoLicitacao;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;

/**
 * @author renato
 */
@Stateless
public class RecursoLicitacaoFacade extends AbstractFacade<RecursoLicitacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private LicitacaoFacade licitacaoFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private VeiculoDePublicacaoFacade veiculoDePublicacaoFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public RecursoLicitacaoFacade() {
        super(RecursoLicitacao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LicitacaoFacade getLicitacaoFacade() {
        return licitacaoFacade;
    }

    public PessoaFisicaFacade getPessoaFisicaFacade() {
        return pessoaFisicaFacade;
    }

    public VeiculoDePublicacaoFacade getVeiculoDePublicacaoFacade() {
        return veiculoDePublicacaoFacade;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    private void salvarNovoStatus(RecursoLicitacao entity, TipoSituacaoLicitacao situacao, String motivo) {
        StatusLicitacao novoStatusLicitacao = getNovoStatusLicitacao(entity);
        novoStatusLicitacao.setTipoSituacaoLicitacao(situacao);
        novoStatusLicitacao.setMotivoStatusLicitacao(motivo);
        novoStatusLicitacao = em.merge(novoStatusLicitacao);
    }

    private StatusLicitacao getNovoStatusLicitacao(RecursoLicitacao entity) {
        Licitacao licitacao = licitacaoFacade.recuperar(entity.getLicitacao().getId());
        StatusLicitacao novoStatus = new StatusLicitacao();
        novoStatus.setLicitacao(licitacao);
        novoStatus.setNumero(licitacaoFacade.recuperarListaDeStatus(licitacao).size() + 1l);
        novoStatus.setResponsavel(sistemaFacade.getUsuarioCorrente());
        novoStatus.setDataStatus(new Date());
        return novoStatus;
    }

    @Override
    public RecursoLicitacao recuperar(Object id) {
        RecursoLicitacao entity = super.recuperar(id);
        if (entity.getDetentorDocumentoLicitacao() != null) {
            Hibernate.initialize(entity.getDetentorDocumentoLicitacao().getDocumentoLicitacaoList());
        }
        entity.getStatus().size();
        return entity;
    }

    public void salvarTipoRecursoSemJulgamento(RecursoLicitacao selecionado, TipoSituacaoLicitacao emRecurso, String motivoAguardandoJulgamento) {
        salvarNovoStatus(selecionado, emRecurso, motivoAguardandoJulgamento);
        super.salvar(selecionado);
    }

    public void salvarTipoRecursoJulgadoIndeferido(RecursoLicitacao selecionado, TipoSituacaoLicitacao emRecurso, TipoSituacaoLicitacao andamento, String motivoAguardandoJulgamento, String motivoJulgadoIndeferido) {
        if (TipoSituacaoLicitacao.EM_RECURSO.equals(selecionado.getLicitacao().getStatusAtualLicitacao().getTipoSituacaoLicitacao())) {
            salvarNovoStatus(selecionado, emRecurso, motivoAguardandoJulgamento);
        }
        salvarNovoStatus(selecionado, andamento, motivoJulgadoIndeferido);
        super.salvar(selecionado);
    }

    public void salvarTipoRecursoJulgadoDeferido(RecursoLicitacao selecionado, TipoSituacaoLicitacao emRecurso, String motivoAguardandoJulgamento, TipoSituacaoLicitacao tipo, String motivoJulgadoDeferido) {
        salvarNovoStatus(selecionado, emRecurso, motivoAguardandoJulgamento);
        salvarNovoStatus(selecionado, tipo, motivoJulgadoDeferido);
        super.salvar(selecionado);
    }
}
