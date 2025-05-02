package br.com.webpublico.negocios.rh.ponto;


import br.com.webpublico.entidades.Faltas;
import br.com.webpublico.entidades.Notificacao;
import br.com.webpublico.entidades.rh.ponto.SolicitacaoFaltas;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.util.DataUtil;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class SolicitacaoFaltasFacade extends AbstractFacade<SolicitacaoFaltas> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;


    public SolicitacaoFaltasFacade() {
        super(SolicitacaoFaltas.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SolicitacaoFaltas salvarECriarNotificacao(SolicitacaoFaltas entity) {
        SolicitacaoFaltas solicitacao = em.merge(entity);
        criarNotificacao(solicitacao);
        return entity;
    }

    private void criarNotificacao(SolicitacaoFaltas solicitacao) {
        Notificacao notificacao = new Notificacao();
        notificacao.setDescricao(montarDescricao(solicitacao));
        notificacao.setGravidade(Notificacao.Gravidade.INFORMACAO);
        notificacao.setTipoNotificacao(TipoNotificacao.SOLICITACAO_FALTAS_PONTO);
        notificacao.setTitulo("Solicitação de Faltas - PONTO");
        notificacao.setLink("/solicitacao-afastamento/ver/" + solicitacao.getId());
        NotificacaoService.getService().notificar(notificacao);
    }

    private String montarDescricao(SolicitacaoFaltas solicitacao) {
        return "Solicitação de afastamento do(a) Servidor(a) " + solicitacao.getContratoFP() + ", no período de  " + DataUtil.getDataFormatada(solicitacao.getDataInicio()) + " à " + DataUtil.getDataFormatada(solicitacao.getDataFim());
    }

    public void salvarSolicitacaoFaltas(SolicitacaoFaltas entity) {
        Faltas faltas = em.merge(entity.getFaltas());
        entity.setFaltas(faltas);
        em.merge(entity);
    }


}
