package br.com.webpublico.entidadesauxiliares.rh.previdencia;

import java.math.BigDecimal;

public class PercentualContribuicaoPrevidenciaComplementarVO {
    private BigDecimal valorReferenciaServidor;
    private BigDecimal valorReferenciaPatrocinador;
    private Integer quantidadeOcorrenciasServidor;
    private Integer quantidadeOcorrenciasPatrocinador;
    private String codigo;

    public BigDecimal getValorReferenciaServidor() {
        return valorReferenciaServidor;
    }

    public void setValorReferenciaServidor(BigDecimal valorReferenciaServidor) {
        this.valorReferenciaServidor = valorReferenciaServidor;
    }

    public BigDecimal getValorReferenciaPatrocinador() {
        return valorReferenciaPatrocinador;
    }

    public void setValorReferenciaPatrocinador(BigDecimal valorReferenciaPatrocinador) {
        this.valorReferenciaPatrocinador = valorReferenciaPatrocinador;
    }

    public Integer getQuantidadeOcorrenciasServidor() {
        return quantidadeOcorrenciasServidor;
    }

    public void setQuantidadeOcorrenciasServidor(Integer quantidadeOcorrenciasServidor) {
        this.quantidadeOcorrenciasServidor = quantidadeOcorrenciasServidor;
    }

    public Integer getQuantidadeOcorrenciasPatrocinador() {
        return quantidadeOcorrenciasPatrocinador;
    }

    public void setQuantidadeOcorrenciasPatrocinador(Integer quantidadeOcorrenciasPatrocinador) {
        this.quantidadeOcorrenciasPatrocinador = quantidadeOcorrenciasPatrocinador;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
}
