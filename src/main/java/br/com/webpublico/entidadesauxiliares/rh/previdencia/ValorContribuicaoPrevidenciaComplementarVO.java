package br.com.webpublico.entidadesauxiliares.rh.previdencia;

import java.math.BigDecimal;

public class ValorContribuicaoPrevidenciaComplementarVO {
    private BigDecimal valorServidor;
    private BigDecimal valorPatrocinador;
    private String codigo;

    public BigDecimal getValorServidor() {
        return valorServidor;
    }

    public void setValorServidor(BigDecimal valorServidor) {
        this.valorServidor = valorServidor;
    }

    public BigDecimal getValorPatrocinador() {
        return valorPatrocinador;
    }

    public void setValorPatrocinador(BigDecimal valorPatrocinador) {
        this.valorPatrocinador = valorPatrocinador;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
}
