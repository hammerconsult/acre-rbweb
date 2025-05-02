package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created by Mateus on 16/12/2014.
 */
public class ComprasResumoGeralTransparencia {
    private BigDecimal detalhado;
    private BigDecimal naoDetalhado;
    private BigDecimal liquidacaoResto;
    private BigDecimal anulacoesAnteriores;

    public ComprasResumoGeralTransparencia() {
    }

    public BigDecimal getDetalhado() {
        return detalhado;
    }

    public void setDetalhado(BigDecimal detalhado) {
        this.detalhado = detalhado;
    }

    public BigDecimal getNaoDetalhado() {
        return naoDetalhado;
    }

    public void setNaoDetalhado(BigDecimal naoDetalhado) {
        this.naoDetalhado = naoDetalhado;
    }

    public BigDecimal getLiquidacaoResto() {
        return liquidacaoResto;
    }

    public void setLiquidacaoResto(BigDecimal liquidacaoResto) {
        this.liquidacaoResto = liquidacaoResto;
    }

    public BigDecimal getAnulacoesAnteriores() {
        return anulacoesAnteriores;
    }

    public void setAnulacoesAnteriores(BigDecimal anulacoesAnteriores) {
        this.anulacoesAnteriores = anulacoesAnteriores;
    }
}
