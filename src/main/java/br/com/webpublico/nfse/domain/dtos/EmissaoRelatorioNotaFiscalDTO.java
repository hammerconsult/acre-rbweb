package br.com.webpublico.nfse.domain.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EmissaoRelatorioNotaFiscalDTO implements Serializable {

    private ConsultaGenericaNfseDTO consultaNotaFiscal;
    private String criteriosUtilizados;
    private TipoNota tipoNota;
    private TipoRelatorio tipoRelatorio;

    public ConsultaGenericaNfseDTO getConsultaNotaFiscal() {
        return consultaNotaFiscal;
    }

    public void setConsultaNotaFiscal(ConsultaGenericaNfseDTO consultaNotaFiscal) {
        this.consultaNotaFiscal = consultaNotaFiscal;
    }

    public String getCriteriosUtilizados() {
        return criteriosUtilizados;
    }

    public void setCriteriosUtilizados(String criteriosUtilizados) {
        this.criteriosUtilizados = criteriosUtilizados;
    }

    public TipoNota getTipoNota() {
        return tipoNota;
    }

    public void setTipoNota(TipoNota tipoNota) {
        this.tipoNota = tipoNota;
    }

    public TipoRelatorio getTipoRelatorio() {
        return tipoRelatorio;
    }

    public void setTipoRelatorio(TipoRelatorio tipoRelatorio) {
        this.tipoRelatorio = tipoRelatorio;
    }

    public enum TipoNota {
        EMITIDA,
        RECEBIDA
    }

    public enum TipoRelatorio {
        RESUMIDO,
        DETALHADO
    }
}
