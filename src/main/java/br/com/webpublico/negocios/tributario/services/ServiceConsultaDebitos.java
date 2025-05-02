package br.com.webpublico.negocios.tributario.services;

import br.com.webpublico.entidades.DAM;
import br.com.webpublico.entidadesauxiliares.AbstractVOConsultaLancamento;
import br.com.webpublico.enums.SituacaoLoteBaixa;
import br.com.webpublico.enums.SituacaoProcessoDebito;
import br.com.webpublico.enums.TipoProcessoDebito;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.consultaparcela.DTO.ValoresPagosParcela;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.tributario.consultadebitos.calculadores.CalculadorAcrescimos;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ServiceConsultaDebitos {

    @PersistenceContext
    public transient EntityManager em;

    public ValoresPagosParcela buscarValorPagoDaParcelaNaArrecadacao(Long idParcela) {
        String sql = "SELECT COALESCE(IDM.VALORORIGINALDEVIDO,0) AS original, " +
            "  COALESCE(IDM.JUROS,0) AS juros, " +
            "  COALESCE(IDM.MULTA,0) AS multa, " +
            "  COALESCE(IDM.CORRECAOMONETARIA,0) AS correcao, " +
            "  COALESCE(IDM.HONORARIOS,0) AS honorarios, " +
            "  COALESCE(idm.desconto,0) AS desconto, " +
            "  ((coalesce(idm.juros,0) + coalesce(idm.multa,0) + coalesce(idm.correcaomonetaria,0) + coalesce(idm.honorarios,0) + coalesce(idm.valororiginaldevido,0)) - coalesce(idm.desconto,0)) + " +
            "  (coalesce((((coalesce(idm.juros,0) + coalesce(idm.multa,0) + coalesce(idm.correcaomonetaria,0) + coalesce(idm.honorarios,0) + coalesce(idm.valororiginaldevido,0)) - coalesce(idm.desconto,0)) * " +
            "  (((coalesce(d.juros,0) + coalesce(d.multa,0) + coalesce(d.correcaomonetaria,0) + coalesce(d.honorarios,0) + coalesce(d.valororiginal,0)) - coalesce(d.desconto,0)) - coalesce(ilb.valorpago,0))) / " +
            "  ((coalesce(d.juros,0) + coalesce(d.multa,0) + coalesce(d.correcaomonetaria,0) + coalesce(d.honorarios,0) + coalesce(d.valororiginal,0)) - coalesce(d.desconto,0)) ,0) * -1) as valorpago,  " +
            "  lb.datapagamento datapagamento " +
            " FROM PARCELAVALORDIVIDA PVD " +
            " LEFT JOIN itemdam idm ON idm.parcela_id = pvd.id " +
            " LEFT JOIN dam d on d.SITUACAO = :situacaoDam and d.id = idm.dam_id " +
            " LEFT JOIN itemlotebaixaparcela ilbp on ilbp.itemdam_id = idm.id " +
            " LEFT JOIN itemlotebaixa ilb ON ilb.id = ilbp.itemlotebaixa_id " +
            " LEFT JOIN LOTEBAIXA LB ON LB.ID = ILB.LOTEBAIXA_ID AND LB.SITUACAOLOTEBAIXA IN (:situacaoLoteBaixa) " +
            "WHERE pvd.id =  :parcela " +
            "  AND IDM.ID = (SELECT MAX(S_IDM.ID) " +
            "                  FROM ITEMDAM S_IDM " +
            "                 INNER JOIN DAM S_DAM ON S_DAM.ID = S_IDM.DAM_ID " +
            "                 WHERE S_DAM.SITUACAO = :situacaoDam " +
            "                   AND s_idm.parcela_id = :parcela) " +
            "  AND (lb.id is not null and ilb.id is not null or lb.id is  null and ilb.id is null)";
        Query q = em.createNativeQuery(sql);
        q.setParameter("parcela", idParcela);
        q.setParameter("situacaoDam", DAM.Situacao.PAGO.name());
        q.setParameter("situacaoLoteBaixa", Lists.newArrayList(SituacaoLoteBaixa.BAIXADO_INCONSITENTE.name(), SituacaoLoteBaixa.BAIXADO.name()));
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return new ValoresPagosParcela((Object[]) q.getResultList().get(0));
        }
        return null;
    }

    public ValoresPagosParcela recuperaValoresPagosParcela(ResultadoParcela resultadoParcela, Date dataPagamentoInicial, Date dataPagamentoFinal) {
        String sql = "SELECT COALESCE(SUM(IDM.VALORORIGINALDEVIDO),0) AS original, " +
            "  COALESCE(SUM(IDM.JUROS),0) AS juros, " +
            "  COALESCE(SUM(IDM.MULTA),0) AS multa, " +
            "  COALESCE(SUM(IDM.CORRECAOMONETARIA),0) AS correcao, " +
            "  COALESCE(SUM(IDM.HONORARIOS),0) AS honorarios, " +
            "  COALESCE(SUM(idm.desconto),0) AS desconto, " +
            "  coalesce(sum(ilbp.valorpago), ((sum(idm.juros) + sum(idm.multa) + sum(idm.correcaomonetaria) + sum(idm.honorarios) + sum(idm.valororiginaldevido)) - sum(idm.desconto))) as totalpago, " +
            "  COALESCE(MAX(lb.datapagamento), " +
            "  MAX((SELECT MAX(pd.DATAPAGAMENTO) " +
            "         FROM itemprocessodebito ipd " +
            "        INNER JOIN processodebito pd on pd.id = ipd.processodebito_id " +
            "        WHERE pd.tipo = :tipoProcessoDebito " +
            "          AND pd.situacao = :situacaoProcesso " +
            "          AND ipd.parcela_id = pvd.id)), " +
            "  MAX((SELECT MAX(AVULSO.DATAPAGAMENTO) " +
            "         FROM Pagamentoavulso avulso " +
            "        WHERE avulso.parcelavalordivida_id = pvd.id))) as datapagamento " +
            " FROM PARCELAVALORDIVIDA PVD " +
            " LEFT JOIN itemdam idm ON idm.parcela_id = pvd.id " +
            " LEFT JOIN dam d on d.SITUACAO = :situacaoDam and d.id = idm.dam_id " +
            " LEFT JOIN itemlotebaixaparcela ilbp on ilbp.itemdam_id = idm.id " +
            " LEFT JOIN itemlotebaixa ilb ON ilb.id = ilbp.itemlotebaixa_id " +
            " LEFT JOIN LOTEBAIXA LB ON LB.ID = ILB.LOTEBAIXA_ID AND LB.SITUACAOLOTEBAIXA IN (:situacaoLoteBaixa) " +
            "WHERE pvd.id = :parcela " +
            "  AND IDM.ID = (SELECT MAX(S_IDM.ID) " +
            "                  FROM ITEMDAM S_IDM " +
            "                 INNER JOIN DAM S_DAM ON S_DAM.ID = S_IDM.DAM_ID " +
            "                 WHERE S_DAM.SITUACAO = :situacaoDam " +
            "                   AND s_idm.parcela_id = :parcela) " +
            "  AND (lb.id is not null and ilb.id is not null or lb.id is  null and ilb.id is null)";

        if (dataPagamentoInicial != null) {
            sql += " AND trunc(LB.DATAPAGAMENTO) >= trunc(:dataPagamentoInicial) ";
        }
        if (dataPagamentoFinal != null) {
            sql += " AND trunc(LB.DATAPAGAMENTO) <= trunc(:dataPagamentoFinal) ";
        }
        Query q = em.createNativeQuery(sql);
        if (dataPagamentoInicial != null) {
            q.setParameter("dataPagamentoInicial", dataPagamentoInicial);
        }
        if (dataPagamentoFinal != null) {
            q.setParameter("dataPagamentoFinal", dataPagamentoFinal);
        }
        q.setParameter("parcela", resultadoParcela.getIdParcela());
        q.setParameter("situacaoDam", DAM.Situacao.PAGO.name());
        q.setParameter("tipoProcessoDebito", TipoProcessoDebito.BAIXA.name());
        q.setParameter("situacaoProcesso", SituacaoProcessoDebito.FINALIZADO.name());
        q.setParameter("situacaoLoteBaixa", Lists.newArrayList(SituacaoLoteBaixa.BAIXADO_INCONSITENTE.name(), SituacaoLoteBaixa.BAIXADO.name()));

        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return new ValoresPagosParcela((Object[]) q.getResultList().get(0));
        }
        return null;
    }


    @Transactional(timeout = 10000, propagation = Propagation.REQUIRES_NEW)
    public void calcularAcrescimos(List<? extends AbstractVOConsultaLancamento> vosConsultaLancamento, Date dataPagamentoInicial, Date dataPagamentoFinal, Date referenciaMulta, Date referenciaHonorarios) {
        CalculadorAcrescimos calculadorAcrescimos = new CalculadorAcrescimos(new ConsultaParcela().getServiceConsultaDebitos());
        calculadorAcrescimos.limparCaches();
        for (AbstractVOConsultaLancamento vo : vosConsultaLancamento) {
            calculadorAcrescimos.preencherValoresAcrescimosImpostoTaxaResultadoParcela(vo.getResultadoParcela(), new Date(), null, dataPagamentoInicial, dataPagamentoFinal, referenciaMulta, referenciaHonorarios);
        }
    }

}
