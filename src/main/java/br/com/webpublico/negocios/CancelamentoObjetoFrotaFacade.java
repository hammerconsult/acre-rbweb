package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CancelamentoObjetoFrota;
import br.com.webpublico.entidades.Notificacao;
import br.com.webpublico.entidades.SolicitacaoObjetoFrota;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.enums.administrativo.frotas.SituacaoSolicitacaoObjetoFrota;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.seguranca.NotificacaoService;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author Octavio
 */

@Stateless
public class CancelamentoObjetoFrotaFacade extends AbstractFacade<CancelamentoObjetoFrota> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;

    public CancelamentoObjetoFrotaFacade() {
        super(CancelamentoObjetoFrota.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvarNovo(CancelamentoObjetoFrota entity) {
        if (entity.getCodigo() == null) {
            entity.setCodigo(singletonGeradorCodigo.getProximoCodigo(CancelamentoObjetoFrota.class, "codigo"));
        }
        cancelarSolicitacaoReserva(entity);
        entity = em.merge(entity);
        criarNotificacaoCancelamentoReserva(entity);
    }

    private void cancelarSolicitacaoReserva(CancelamentoObjetoFrota entity) {
        SolicitacaoObjetoFrota solicitacaoReserva = entity.getReservaObjetoFrota().getSolicitacaoObjetoFrota();
        solicitacaoReserva.setMotivo(entity.getMotivo());
        solicitacaoReserva.setSituacao(SituacaoSolicitacaoObjetoFrota.CANCELADA);
        em.merge(solicitacaoReserva);
    }

    private void criarNotificacaoCancelamentoReserva(CancelamentoObjetoFrota entity) {
        String msg = "Sua reserva foi cancelada. Solicitante: " + entity.getReservaObjetoFrota().getSolicitacaoObjetoFrota().getPessoaFisica().getNome();
        String link = "/frota/solicitacao/ver/" + entity.getReservaObjetoFrota().getSolicitacaoObjetoFrota().getId() + "/";
        Notificacao notificacao = new Notificacao();
        notificacao.setDescricao(msg);
        notificacao.setLink(link);
        notificacao.setGravidade(Notificacao.Gravidade.ATENCAO);
        notificacao.setTitulo(TipoNotificacao.AVISO_RESERVA_CANCELADA_VEICULO_EQUIPAMENTO.getDescricao());
        notificacao.setTipoNotificacao(TipoNotificacao.AVISO_RESERVA_CANCELADA_VEICULO_EQUIPAMENTO);
        notificacao.setUnidadeOrganizacional(entity.getReservaObjetoFrota().getSolicitacaoObjetoFrota().getUnidadeSolicitante());
        NotificacaoService.getService().notificar(notificacao);
    }

    @Override
    public void remover(CancelamentoObjetoFrota entity) {
        SolicitacaoObjetoFrota solicitacaoObjetoFrota = entity.getReservaObjetoFrota().getSolicitacaoObjetoFrota();
        solicitacaoObjetoFrota.setMotivo("");
        solicitacaoObjetoFrota.setSituacao(SituacaoSolicitacaoObjetoFrota.APROVADO);
        em.merge(solicitacaoObjetoFrota);
        super.remover(entity);
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }
}
