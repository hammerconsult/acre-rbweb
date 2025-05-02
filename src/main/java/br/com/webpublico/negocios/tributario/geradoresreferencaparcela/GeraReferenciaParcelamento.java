package br.com.webpublico.negocios.tributario.geradoresreferencaparcela;

import br.com.webpublico.entidades.Calculo;
import br.com.webpublico.entidades.ProcessoParcelamento;
import br.com.webpublico.entidades.SituacaoParcelaValorDivida;
import br.com.webpublico.enums.SituacaoParcelamento;
import com.google.common.collect.Lists;

import javax.persistence.Query;


public class GeraReferenciaParcelamento extends GeraReferencia<ProcessoParcelamento> {

    private Long idParcelaOriginada;

    protected String getReferencia() {
        try {
            return "Exercício: " + calculo.getExercicio().getAno() + " Parcelamento: " + calculo.getNumeroComposto() + buscarNumeroDoParcelamento(calculo);
        } catch (Exception e) {
            e.printStackTrace();
            return " ";
        }
    }


    @Override
    public void geraReferencia(SituacaoParcelaValorDivida spvd) {
        calculo = (ProcessoParcelamento) getCalculo(spvd);
        this.idParcelaOriginada = spvd.getParcela().getId();
        spvd.setReferencia(getReferencia());
    }

    @Override
    public String geraReferencia(Long idParcela) {
        calculo = (ProcessoParcelamento) getCalculo(idParcela);
        this.idParcelaOriginada = idParcela;
        return getReferencia();
    }

    private String buscarNumeroDoParcelamento(Calculo calculo) {
        String sql = "select parcelamento.numero || " +
                "case when parcelamento.numeroreparcelamento > 0 then '-' || parcelamento.numeroreparcelamento else '' end ||" +
                " '/' || exercicio.ano " +
                " from processoparcelamento parcelamento\n" +
                " inner join itemprocessoparcelamento it on it.processoparcelamento_id = parcelamento.id\n" +
                " inner join exercicio on exercicio.id = parcelamento.exercicio_id\n" +
                " where it.parcelavalordivida_id = :parcela " +
                " and parcelamento.id <> :idCalculo " +
                " and parcelamento.situacaoParcelamento in (:situacao)" +
                " order by parcelamento.id desc";
        Query q = getEm().createNativeQuery(sql);
        q.setParameter("parcela", idParcelaOriginada);
        q.setParameter("idCalculo", calculo.getId());
        q.setParameter("situacao", Lists.newArrayList(SituacaoParcelamento.FINALIZADO.name(), SituacaoParcelamento.REATIVADO.name(), SituacaoParcelamento.PAGO.name()));
        q.setMaxResults(1);
        String retorno = "";
        if (!q.getResultList().isEmpty()) {
            retorno = " Reparcelamento: " + q.getResultList().get(0);
        }
        return retorno;
    }

}
