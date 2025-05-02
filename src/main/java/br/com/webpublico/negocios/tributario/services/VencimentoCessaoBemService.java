package br.com.webpublico.negocios.tributario.services;

import br.com.webpublico.entidades.LoteCessao;
import br.com.webpublico.entidades.Notificacao;
import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.util.Util;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

@Service
public class VencimentoCessaoBemService {

    @PersistenceContext
    protected transient EntityManager em;

    @Transactional(timeout = 10000, propagation = Propagation.REQUIRES_NEW)
    public void gerarNotificacoesDasCessoes() {
        List<LoteCessao> listCessao = getContratosComVencimentoNoPeriodo();
        gerarNotificacoesDasCessoes(listCessao, Notificacao.Gravidade.INFORMACAO);
    }

    /*Recuperar Cessões que estão vencidas, que ainda tem bens para devolver e que não tenha notificação ainda*/
    public List<LoteCessao> getContratosComVencimentoNoPeriodo() {
        String sql =
            " select distinct lote.* " +
            "       FROM LOTECESSAO LOTE " +
            " inner join PrazoCessao prazo ON PRAZO.LOTECESSAO_ID = LOTE.ID " +
            " where " +
            " prazo.id = (select max(p.id)  " +
            "               FROM PRAZOCESSAO P  " +
            "             where p.loteCessao_ID = lote.ID  " +
            "               and p.prorrogacaoCessao_id is null " +
            "             )           " +
            " AND trunc(PRAZO.FIMDOPRAZO) <= trunc(:dataAtual) " +
            " AND EXISTS (SELECT 1 FROM CESSAO CS " +
            "             WHERE CS.LOTECESSAO_ID = LOTE.ID " +
            "               AND NOT EXISTS( SELECT 1 FROM CESSAODEVOLUCAO CD  " +
            "                               INNER JOIN EVENTOBEM EV ON EV.ID = CD.ID " +
            "                               WHERE CD.CESSAO_ID = CS.ID " +
            "                                 AND EV.SITUACAOEVENTOBEM = :FINALIZADO)) " +
            " AND NOT EXISTS ( SELECT 1 FROM NOTIFICACAO NT WHERE NT.LINK = '/cessao-de-bens/ver/' || LOTE.ID || '/') ";
        Query q = em.createNativeQuery(sql, LoteCessao.class);
        q.setParameter("dataAtual", getHoje(), TemporalType.DATE);
        q.setParameter("FINALIZADO", SituacaoEventoBem.FINALIZADO.name());
        return q.getResultList();
    }

    private Date getHoje(){
        return Util.getDataHoraMinutoSegundoZerado(new Date());
    }

    private void gerarNotificacoesDasCessoes(List<LoteCessao> listCessao, Notificacao.Gravidade gravidade){
        if (listCessao == null || listCessao.isEmpty()){
            return;
        }

        for (LoteCessao c : listCessao) {
            Notificacao notOrigem = new Notificacao();
            notOrigem.setGravidade(gravidade);
            notOrigem.setLink("/cessao-de-bens/ver/" + c.getId() + "/");
            notOrigem.setDescricao("Cessão " + c.toString() + " expirou o prazo!");
            notOrigem.setTitulo("Vencimento da Cessão");
            notOrigem.setUnidadeOrganizacional(c.getUnidadeOrigem());
            notOrigem.setTipoNotificacao(TipoNotificacao.CESSAO_BENS);
            NotificacaoService.getService().notificar(notOrigem);

            Notificacao notDestino = new Notificacao();
            notDestino.setGravidade(gravidade);
            notDestino.setLink("/cessao-de-bens/ver/" + c.getId() + "/");
            notDestino.setDescricao("Cessão " + c.toString() + " expirou o prazo!");
            notDestino.setTitulo("Vencimento da Cessão");
            notDestino.setUnidadeOrganizacional(c.getUnidadeDestino());
            notDestino.setTipoNotificacao(TipoNotificacao.CESSAO_BENS);
            NotificacaoService.getService().notificar(notDestino);
        }
    }
}
