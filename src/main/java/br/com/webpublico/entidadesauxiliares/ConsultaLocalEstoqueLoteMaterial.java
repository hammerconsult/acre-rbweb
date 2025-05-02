package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.TipoMascaraUnidadeMedida;
import br.com.webpublico.util.Util;

import java.math.BigDecimal;
import java.util.Date;

public class ConsultaLocalEstoqueLoteMaterial implements Comparable<ConsultaLocalEstoqueLoteMaterial> {

    private String identificacao;
    private Date validade;
    private BigDecimal quantidade;
    private TipoMascaraUnidadeMedida mascaraQuantidade;
    private TipoMascaraUnidadeMedida mascaraValorUnitario;

    public ConsultaLocalEstoqueLoteMaterial() {
        this.quantidade = BigDecimal.ZERO;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public String getQuantidadeFormatada() {
        return Util.formataQuandoDecimal(quantidade, mascaraQuantidade);
    }

    public String getIdentificacao() {
        return identificacao;
    }

    public void setIdentificacao(String identificacao) {
        this.identificacao = identificacao;
    }

    public Date getValidade() {
        return validade;
    }

    public void setValidade(Date validade) {
        this.validade = validade;
    }

    public TipoMascaraUnidadeMedida getMascaraQuantidade() {
        return mascaraQuantidade;
    }

    public void setMascaraQuantidade(TipoMascaraUnidadeMedida mascaraQuantidade) {
        this.mascaraQuantidade = mascaraQuantidade;
    }

    public TipoMascaraUnidadeMedida getMascaraValorUnitario() {
        return mascaraValorUnitario;
    }

    public void setMascaraValorUnitario(TipoMascaraUnidadeMedida mascaraValorUnitario) {
        this.mascaraValorUnitario = mascaraValorUnitario;
    }

    @Override
    public int compareTo(ConsultaLocalEstoqueLoteMaterial o) {
        return this.getIdentificacao().compareTo(o.getIdentificacao());
    }
}
