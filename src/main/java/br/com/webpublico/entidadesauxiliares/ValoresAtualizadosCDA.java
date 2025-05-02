package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Tributo;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Fabio on 19/07/2018.
 */
public class ValoresAtualizadosCDA {

    private BigDecimal valorImposto = BigDecimal.ZERO;
    private BigDecimal valorTaxa = BigDecimal.ZERO;
    private BigDecimal valorJuros = BigDecimal.ZERO;
    private BigDecimal valorMulta = BigDecimal.ZERO;
    private BigDecimal valorCorrecao = BigDecimal.ZERO;
    private BigDecimal valorHonorarios = BigDecimal.ZERO;
    private BigDecimal valorTotal = BigDecimal.ZERO;
    private BigDecimal valorPago = BigDecimal.ZERO;
    private List<String> detalhamento = Lists.newArrayList();
    private List<ComposicaoCDA> composicao = Lists.newArrayList();

    public BigDecimal getValorPago() {
        return valorPago != null ? valorPago.setScale(2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;
    }

    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
    }

    public BigDecimal getValorPrincipal() {
        return getValorImposto().add(getValorTaxa());
    }

    public BigDecimal getValorImposto() {
        return valorImposto != null ? valorImposto.setScale(2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;
    }

    public void setValorImposto(BigDecimal valorImposto) {
        this.valorImposto = valorImposto;
    }

    public BigDecimal getValorTaxa() {
        return valorTaxa != null ? valorTaxa.setScale(2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;
    }

    public void setValorTaxa(BigDecimal valorTaxa) {
        this.valorTaxa = valorTaxa;
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

    public List<String> getDetalhamento() {
        return detalhamento;
    }

    public void setDetalhamento(List<String> detalhamento) {
        this.detalhamento = detalhamento;
    }

    public List<ComposicaoCDA> getComposicao() {
        return composicao;
    }

    public void setComposicao(List<ComposicaoCDA> composicao) {
        this.composicao = composicao;
    }

    public void arredondarValoresDaComposicao() {
        BigDecimal valorTotalComposicao = getValorTotalComposicao(null);
        BigDecimal diferenca = (valorTotalComposicao.subtract(getValorTotal()));
        if (diferenca.compareTo(BigDecimal.ZERO) != 0 && !composicao.isEmpty()) {
            composicao.get(0).setValorTributo(composicao.get(0).getValorTributo().subtract(diferenca));
            composicao.get(0).recalcularTotal();
        }
    }

    public void addComposicao(ComposicaoCDA composicaoCDA) {
        if (composicaoCDA.getValorTotal().compareTo(BigDecimal.ZERO) > 0) {
            composicao.add(composicaoCDA);
        }

    }

    public BigDecimal getValorTotalComposicao(Tributo.TipoTributo tipoTributo) {
        if (composicao != null && !composicao.isEmpty()) {
            BigDecimal valor = BigDecimal.ZERO;
            for (ComposicaoCDA composicaoCDA : composicao) {
                if (tipoTributo == null) {
                    valor = valor.add(composicaoCDA.getValorTotal());
                    continue;
                }
                switch (tipoTributo) {
                    case TAXA:
                        valor = valor.add(composicaoCDA.getValorTributo());
                        break;
                    case IMPOSTO:
                        valor = valor.add(composicaoCDA.getValorTributo());
                        break;
                    case CORRECAO:
                        valor = valor.add(composicaoCDA.getValorCorrecao());
                        break;
                    case JUROS:
                        valor = valor.add(composicaoCDA.getValorJuros());
                        break;
                    case MULTA:
                        valor = valor.add(composicaoCDA.getValorMulta());
                        break;
                    case HONORARIOS:
                        valor = valor.add(composicaoCDA.getValorHonorarios());
                        break;
                    default:
                        valor = valor.add(composicaoCDA.getValorTotal());
                        break;
                }
            }
            return valor;

        }

        return BigDecimal.ZERO;
    }
}
