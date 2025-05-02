package br.com.webpublico.negocios.tributario.geradoresreferencaparcela;

import br.com.webpublico.entidades.CalculoISS;
import br.com.webpublico.entidades.SituacaoParcelaValorDivida;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.util.StringUtil;

import javax.persistence.Query;

public class GeraReferenciaISS extends GeraReferencia<CalculoISS> {

    private SituacaoParcelaValorDivida situacaoParcelaValorDivida;


    @Override
    protected String getReferencia() {
        String referencia = "";
        if (calculo.getProcessoCalculo() != null && calculo.getProcessoCalculo().getExercicio() != null) {
            referencia += "Exercício: " + calculo.getProcessoCalculo().getExercicio().getAno();
        }
        if (calculo.getProcessoCalculoISS().getMesReferencia() != null && calculo.getProcessoCalculoISS().getMesReferencia() > 0) {
            referencia += " Mês: " + StringUtil.preencheString(calculo.getProcessoCalculoISS().getMesReferencia()+"", 2, '0');
        }
        if (calculo.getSubDivida() != null) {
            referencia += " Seq.: " + calculo.getSequenciaLancamento();
        }
        if (situacaoParcelaValorDivida != null && SituacaoParcela.PARCELADO.equals(situacaoParcelaValorDivida.getSituacaoParcela())) {
            referencia += " " + recuperaNumeroDoParcelamento();
        }
        return referencia;
    }

    @Override
    public void geraReferencia(SituacaoParcelaValorDivida spvd) {
        calculo = (CalculoISS) getCalculo(spvd);
        situacaoParcelaValorDivida = spvd;
        spvd.setReferencia(getReferencia());
    }

    @Override
    public String geraReferencia(Long idParcela) {
        calculo = (CalculoISS) getCalculo(idParcela);
        return getReferencia();
    }

    private String recuperaNumeroDoParcelamento() {
        String sql = "select parcelamento.numero || '/' || exercicio.ano " +
                " from processoparcelamento parcelamento " +
                " inner join itemprocessoparcelamento it on it.processoparcelamento_id = parcelamento.id " +
                " inner join exercicio on exercicio.id = parcelamento.exercicio_id " +
                " where it.parcelavalordivida_id = :parcela " +
                " and (parcelamento.situacaoParcelamento = 'FINALIZADO'" +
                " or parcelamento.situacaoParcelamento = 'PAGO')";
        Query q = getEm().createNativeQuery(sql);
        q.setParameter("parcela", situacaoParcelaValorDivida.getParcela().getId());
        q.setMaxResults(1);
        String retorno = "";
        if (!q.getResultList().isEmpty()) {
            retorno = "Parcelamento: " + q.getResultList().get(0);
        }
        return retorno;
    }

}
