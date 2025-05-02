package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ContaReceita;
import br.com.webpublico.enums.OperacaoReceita;
import br.com.webpublico.tributario.consultadebitos.enums.TipoTributoDTO;
import com.google.common.base.Objects;

import java.math.BigDecimal;
import java.util.Date;

public class VOItemArrecadadoPorTributo {
    private Long idParcela;
    private Long idTributo;
    private Long idLoteBaixa;

    private BigDecimal valor;
    private BigDecimal desconto;

    private Integer exercicio;

    private Date dataPagamento;

    private Boolean dividaAtiva;

    private String codigoLoteBaixa;

    private TipoTributoDTO tipoTributo;
    private ContaReceita contaReceita;
    private OperacaoReceita operacaoReceita;

    public VOItemArrecadadoPorTributo() {
    }

    public Long getIdParcela() {
        return idParcela;
    }

    public void setIdParcela(Long idParcela) {
        this.idParcela = idParcela;
    }

    public Long getIdTributo() {
        return idTributo;
    }

    public void setIdTributo(Long idTributo) {
        this.idTributo = idTributo;
    }

    public Long getIdLoteBaixa() {
        return idLoteBaixa;
    }

    public void setIdLoteBaixa(Long idLoteBaixa) {
        this.idLoteBaixa = idLoteBaixa;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public Integer getExercicio() {
        return exercicio;
    }

    public void setExercicio(Integer exercicio) {
        this.exercicio = exercicio;
    }

    public Date getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public Boolean getDividaAtiva() {
        return dividaAtiva;
    }

    public void setDividaAtiva(Boolean dividaAtiva) {
        this.dividaAtiva = dividaAtiva;
    }

    public String getCodigoLoteBaixa() {
        return codigoLoteBaixa;
    }

    public void setCodigoLoteBaixa(String codigoLoteBaixa) {
        this.codigoLoteBaixa = codigoLoteBaixa;
    }

    public TipoTributoDTO getTipoTributo() {
        return tipoTributo;
    }

    public void setTipoTributo(TipoTributoDTO tipoTributo) {
        this.tipoTributo = tipoTributo;
    }

    public ContaReceita getContaReceita() {
        return contaReceita;
    }

    public void setContaReceita(ContaReceita contaReceita) {
        this.contaReceita = contaReceita;
    }

    public OperacaoReceita getOperacaoReceita() {
        return operacaoReceita;
    }

    public void setOperacaoReceita(OperacaoReceita operacaoReceita) {
        this.operacaoReceita = operacaoReceita;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VOItemArrecadadoPorTributo that = (VOItemArrecadadoPorTributo) o;
        return Objects.equal(idParcela, that.idParcela) && Objects.equal(idTributo, that.idTributo) && Objects.equal(idLoteBaixa, that.idLoteBaixa) && Objects.equal(dataPagamento, that.dataPagamento) && Objects.equal(contaReceita, that.contaReceita);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idParcela, idTributo, idLoteBaixa, dataPagamento, contaReceita);
    }

}
