package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.SituacaoParcela;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VOParcelaParcelamentoCancelamento {

    private Long idParcelamento;
    private Long idParcela;
    private Long numeroParcelamento;
    private Integer numeroReparcelamento;
    private Integer exercicio;
    private Date dataParcelamento;
    private Date dataCancelamento;
    private String sequenciaParcela;
    private Date vencimentoParcela;
    private String situacaoParcela;
    private Date dataSituacao;
    private Date dataPagamento;

    public VOParcelaParcelamentoCancelamento() {
    }

    public Long getIdParcelamento() {
        return idParcelamento;
    }

    public void setIdParcelamento(Long idParcelamento) {
        this.idParcelamento = idParcelamento;
    }

    public Long getIdParcela() {
        return idParcela;
    }

    public void setIdParcela(Long idParcela) {
        this.idParcela = idParcela;
    }

    public Long getNumeroParcelamento() {
        return numeroParcelamento;
    }

    public void setNumeroParcelamento(Long numeroParcelamento) {
        this.numeroParcelamento = numeroParcelamento;
    }

    public int getNumeroReparcelamento() {
        return numeroReparcelamento;
    }

    public void setNumeroReparcelamento(int numeroReparcelamento) {
        this.numeroReparcelamento = numeroReparcelamento;
    }

    public Integer getExercicio() {
        return exercicio;
    }

    public void setExercicio(Integer exercicio) {
        this.exercicio = exercicio;
    }

    public Date getDataParcelamento() {
        return dataParcelamento;
    }

    public void setDataParcelamento(Date dataParcelamento) {
        this.dataParcelamento = dataParcelamento;
    }

    public Date getDataCancelamento() {
        return dataCancelamento;
    }

    public void setDataCancelamento(Date dataCancelamento) {
        this.dataCancelamento = dataCancelamento;
    }

    public String getSequenciaParcela() {
        return sequenciaParcela;
    }

    public void setSequenciaParcela(String sequenciaParcela) {
        this.sequenciaParcela = sequenciaParcela;
    }

    public Date getVencimentoParcela() {
        return vencimentoParcela;
    }

    public void setVencimentoParcela(Date vencimentoParcela) {
        this.vencimentoParcela = vencimentoParcela;
    }

    public String getSituacaoParcela() {
        return situacaoParcela;
    }

    public void setSituacaoParcela(String situacaoParcela) {
        this.situacaoParcela = situacaoParcela;
    }

    public Date getDataSituacao() {
        return dataSituacao;
    }

    public void setDataSituacao(Date dataSituacao) {
        this.dataSituacao = dataSituacao;
    }

    public Date getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public String getNumeroCompostoComAno() {
        return numeroParcelamento + (numeroReparcelamento != 0 ? numeroReparcelamento + "" : "") + "/" + exercicio;
    }

    public boolean isPago() {
        return SituacaoParcela.BAIXADO.name().equals(situacaoParcela) ||
            SituacaoParcela.BAIXADO_OUTRA_OPCAO.name().equals(situacaoParcela) ||
            SituacaoParcela.PAGO.name().equals(situacaoParcela) ||
            SituacaoParcela.PAGO_PARCELAMENTO.name().equals(situacaoParcela) ||
            SituacaoParcela.PAGO_REFIS.name().equals(situacaoParcela) ||
            SituacaoParcela.PAGO_SUBVENCAO.name().equals(situacaoParcela) ||
            SituacaoParcela.PAGO_BLOQUEIO_JUDICIAL.name().equals(situacaoParcela)
            ;
    }

    public boolean isVencido(Date dataOperacao) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date dataVencimento = null;

        try {
            dataOperacao = sdf.parse(sdf.format(dataOperacao));
            dataVencimento = sdf.parse(sdf.format(vencimentoParcela));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dataVencimento.before(dataOperacao);
    }
}
