package br.com.webpublico.negocios.tributario.services;

import br.com.webpublico.entidades.Notificacao;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.entidadesauxiliares.ContratoNotificacao;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class VencimentoContratoService {

    @PersistenceContext
    protected transient EntityManager em;

    @Transactional(timeout = 10000, propagation = Propagation.REQUIRES_NEW)
    public void getContratosParaNotificacoes() {
        List<ContratoNotificacao> contratosNotificacaoesVermelhas = getContratosComVencimentoNoPeriodo(getHoje(), DataUtil.adicionaDias(getHoje(), 5));
        List<ContratoNotificacao> contratosNotificacaoesAmarelas = getContratosComVencimentoNoPeriodo(DataUtil.adicionaDias(getHoje(), 6), DataUtil.adicionaDias(getHoje(), 10));
        List<ContratoNotificacao> contratosNotificacaoesAzuis = getContratosComVencimentoNoPeriodo(DataUtil.adicionaDias(getHoje(), 11), DataUtil.adicionaDias(getHoje(), 30));

        gerarNotificacoesDosContratos(contratosNotificacaoesVermelhas, Notificacao.Gravidade.ERRO);
        gerarNotificacoesDosContratos(contratosNotificacaoesAmarelas, Notificacao.Gravidade.ATENCAO);
        gerarNotificacoesDosContratos(contratosNotificacaoesAzuis, Notificacao.Gravidade.INFORMACAO);
    }

    private List<ContratoNotificacao> getContratosComVencimentoNoPeriodo(Date inicio, Date fim) {
        String sql = " select con.id as idContrato, " +
            "         'O contrato ' || con.numeroTermo||'/'||ex.ano||' vence '||case when trunc(to_date(con.terminoVigencia,'dd/MM/yyyy')-to_date(sysdate,'dd/MM/yyyy')) = 0 then 'hoje' else 'em ' end ||to_char(trunc(con.terminoVigencia-sysdate) + 1)||' dia(s). Clique no link para maiores detalhes', " +
            "          uc.unidadeadministrativa_id as idUnidade " +
            "        from Contrato con" +
            " inner join exercicio ex on ex.id = con.exerciciocontrato_id " +
            " inner join unidadecontrato uc on uc.contrato_id = con.id" +
            " where con.terminoVigencia >= :inicio and con.terminoVigencia <= :fim " +
            " and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(uc.inicioVigencia) and coalesce(trunc(uc.fimVigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("inicio", inicio);
        q.setParameter("fim", fim);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(fim));
        List<ContratoNotificacao> toReturn = Lists.newArrayList();
        for (Object[] obj : (List<Object[]>) q.getResultList()) {
            ContratoNotificacao contratoNotificacao = new ContratoNotificacao();
            contratoNotificacao.setIdContrato(((BigDecimal) obj[0]).longValue());
            contratoNotificacao.setMsgNotificacao((String) obj[1]);
            UnidadeOrganizacional unidadeOrganizacional = em.find(UnidadeOrganizacional.class, ((BigDecimal) obj[2]).longValue());
            contratoNotificacao.setUnidadeAdminisrativa(unidadeOrganizacional);
            toReturn.add(contratoNotificacao);
        }
        return toReturn;
    }

    private Date getHoje() {
        return Util.getDataHoraMinutoSegundoZerado(new Date());
    }

    private void gerarNotificacoesDosContratos(List<ContratoNotificacao> contratos, Notificacao.Gravidade gravidade) {
        if (contratos == null || contratos.isEmpty()) {
            return;
        }
        for (ContratoNotificacao contrato : contratos) {
            Notificacao not = new Notificacao();
            not.setGravidade(gravidade);
            not.setLink("/contrato-adm/ver/" + contrato.getIdContrato() + "/");
            not.setDescricao(contrato.getMsgNotificacao());
            not.setTitulo("Vencimento de Contrato");
            not.setUnidadeOrganizacional(contrato.getUnidadeAdminisrativa());
            not.setTipoNotificacao(TipoNotificacao.VENCIMENTO_CONTRATO);
            NotificacaoService.getService().notificar(not);
        }
    }
}
