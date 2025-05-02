package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ItemInscricaoDividaAtiva;
import br.com.webpublico.entidades.Tributo;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class ComposicaoCDA {

    private Logger logger = LoggerFactory.getLogger(ComposicaoCDA.class);
    private Tributo.TipoTributo tipoTributo;
    private BigDecimal valorTributo = BigDecimal.ZERO;
    private BigDecimal valorJuros = BigDecimal.ZERO;
    private BigDecimal valorMulta = BigDecimal.ZERO;
    private BigDecimal valorCorrecao = BigDecimal.ZERO;
    private BigDecimal valorHonorarios = BigDecimal.ZERO;
    private BigDecimal valorTotal = BigDecimal.ZERO;
    private final ResultadoParcela resultadoParcela;
    private final Long idItemInscricaoDividaAtiva;


    public ComposicaoCDA(ResultadoParcela resultadoParcela, Long idItemInscricaoDividaAtiva) {
        this.resultadoParcela = resultadoParcela;
        this.idItemInscricaoDividaAtiva = idItemInscricaoDividaAtiva;
    }

    public BigDecimal getValorTributo() {
        return valorTributo != null ? valorTributo.setScale(2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;
    }

    public void setValorTributo(BigDecimal valorTributo) {
        this.valorTributo = valorTributo;
    }

    public BigDecimal getValorJuros() {
        return valorJuros != null ? valorJuros.setScale(2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;
    }

    public void setValorJuros(BigDecimal valorJuros) {
        this.valorJuros = valorJuros;
    }

    public BigDecimal getValorMulta() {
        return valorMulta != null ? valorMulta.setScale(2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;
    }

    public void setValorMulta(BigDecimal valorMulta) {
        this.valorMulta = valorMulta;
    }

    public BigDecimal getValorCorrecao() {
        return valorCorrecao != null ? valorCorrecao.setScale(2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;
    }

    public void setValorCorrecao(BigDecimal valorCorrecao) {
        this.valorCorrecao = valorCorrecao;
    }

    public BigDecimal getValorHonorarios() {
        return valorHonorarios != null ? valorHonorarios.setScale(2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;
    }

    public void setValorHonorarios(BigDecimal valorHonorarios) {
        this.valorHonorarios = valorHonorarios;
    }

    public BigDecimal getValorTotal() {
        return valorTotal != null ? valorTotal.setScale(2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Tributo.TipoTributo getTipoTributo() {
        return tipoTributo;
    }

    public void setTipoTributo(Tributo.TipoTributo tipoTributo) {
        this.tipoTributo = tipoTributo;
    }

    public Long getIdItemInscricaoDividaAtiva() {
        return idItemInscricaoDividaAtiva;
    }

    public ResultadoParcela getResultadoParcela() {
        return resultadoParcela;
    }

    public void recalcularTotal() {
        setValorTotal((getValorTributo().add(getValorMulta().add(getValorJuros().add(getValorCorrecao())))).add(getValorHonorarios()));
    }
}
