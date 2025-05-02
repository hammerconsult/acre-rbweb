package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoSituacaoProgramacaoCobranca;
import br.com.webpublico.util.AssistenteBarraProgresso;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author octavio
 */
@Stateless
public class BaixaNotificacaoCobrancaAdministrativaFacade extends AbstractFacade<BaixaNotificacaoCobrancaAdministrativa> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private NotificacaoCobrancaAdministrativaFacade notificacaoCobrancaAdministrativaFacade;
    @EJB
    private ProgramacaoCobrancaFacade programacaoCobrancaFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public BaixaNotificacaoCobrancaAdministrativaFacade() {
        super(BaixaNotificacaoCobrancaAdministrativa.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public BaixaNotificacaoCobrancaAdministrativa recuperar(Object id) {
        BaixaNotificacaoCobrancaAdministrativa baixa = em.find(BaixaNotificacaoCobrancaAdministrativa.class, id);
        baixa.getItensNotificacao().size();
        return baixa;
    }


    public NotificacaoCobrancaAdministrativaFacade getNotificacaoCobrancaAdministrativaFacade() {
        return notificacaoCobrancaAdministrativaFacade;
    }

    public ProgramacaoCobrancaFacade getProgramacaoCobrancaFacade() {
        return programacaoCobrancaFacade;
    }

    public BaixaNotificacaoCobrancaAdministrativa salvarSelecionado(BaixaNotificacaoCobrancaAdministrativa selecionado) {
        return em.merge(selecionado);
    }

    public NotificacaoCobrancaAdmin recuperarNotificaPeloId(Long idNotificacao) {
        String hql = " from NotificacaoCobrancaAdmin nca " +
            " where nca.id = :idNotif ";

        Query q = em.createQuery(hql);
        q.setParameter("idNotif", idNotificacao);

        if(!q.getResultList().isEmpty()) {
            return (NotificacaoCobrancaAdmin) q.getResultList().get(0);
        }
        return null;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 5)
    @Asynchronous
    public Future<BaixaNotificacaoCobrancaAdministrativa> baixar(Aceite aceite, AssistenteBarraProgresso assistente) {
        BaixaNotificacaoCobrancaAdministrativa selecionado = (BaixaNotificacaoCobrancaAdministrativa) assistente.getSelecionado();

        assistente.setDescricaoProcesso("Gerando a Baixa de Aviso/Notificação de Cobrança Administrativa");
        assistente.setTotal(selecionado.getItensNotificacao().size());
        selecionado.setBaixado(Boolean.TRUE);

        for (BaixaCobrancaAdministrativaItemNotificacao itemBaixa : selecionado.getItensNotificacao()) {
            itemBaixa.getItemNotificacao().setAceite(aceite);
            assistente.conta();
            notificacaoCobrancaAdministrativaFacade.salvarObjeto(itemBaixa.getItemNotificacao());
        }
        ProgramacaoCobranca programacaoCobranca = getProgramacaoCobrancaFacade().recuperarSimples(selecionado.getProgramacaoCobranca().getId());
        SituacaoProgramacaoCobranca situacao = getProgramacaoCobrancaFacade().ultimaSituacaoProgramacaoCobranca(programacaoCobranca);
        if (TipoSituacaoProgramacaoCobranca.EXECUTANDO.equals(situacao.getSituacaoProgramacaoCobranca())) {

            programacaoCobranca.getListaSituacaoProgramacaoCobrancas().add(new SituacaoProgramacaoCobranca(programacaoCobranca,
                TipoSituacaoProgramacaoCobranca.CONCLUIDO, assistente.getDataAtual()));
            programacaoCobrancaFacade.salvar(programacaoCobranca);
        }
        novaSituacaoQuandoTodosTemAceite(selecionado, assistente, programacaoCobranca);
        assistente.setDescricaoProcesso("Salvando Baixa de Notificação de Cobrança Administrativa");
        selecionado = salvarSelecionado(selecionado);
        assistente.setExecutando(false);
        return new AsyncResult<>(selecionado);
    }

    private void novaSituacaoQuandoTodosTemAceite(BaixaNotificacaoCobrancaAdministrativa selecionado,
                                                  AssistenteBarraProgresso assistente, ProgramacaoCobranca programacaoCobranca) {

        if (notificacaoCobrancaAdministrativaFacade.recuperaItemNotificacaoSemAceite(selecionado.getItensNotificacao().get(0).getItemNotificacao().getNotificacaoADM()).isEmpty()) {
            programacaoCobranca.getListaSituacaoProgramacaoCobrancas().add(
                new SituacaoProgramacaoCobranca(programacaoCobranca, TipoSituacaoProgramacaoCobranca.CONCLUIDO, assistente.getDataAtual()));
            programacaoCobrancaFacade.salvar(programacaoCobranca);
        }
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public void setSistemaFacade(SistemaFacade sistemaFacade) {
        this.sistemaFacade = sistemaFacade;
    }
}
