package br.com.webpublico.nfse.domain.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EmissaoRelatorioServicoDeclaradoDTO implements Serializable {

    private ConsultaServicoDeclaradoDTO consultaServicoDeclarado;
    private String criteriosUtilizados;
    private TipoRelatorio tipoRelatorio;

    public ConsultaServicoDeclaradoDTO getConsultaServicoDeclarado() {
        return consultaServicoDeclarado;
    }

    public void setConsultaServicoDeclarado(ConsultaServicoDeclaradoDTO consultaServicoDeclarado) {
        this.consultaServicoDeclarado = consultaServicoDeclarado;
    }

    public String getCriteriosUtilizados() {
        return criteriosUtilizados;
    }

    public void setCriteriosUtilizados(String criteriosUtilizados) {
        this.criteriosUtilizados = criteriosUtilizados;
    }

    public TipoRelatorio getTipoRelatorio() {
        return tipoRelatorio;
    }

    public void setTipoRelatorio(TipoRelatorio tipoRelatorio) {
        this.tipoRelatorio = tipoRelatorio;
    }


    public enum TipoRelatorio {
        RESUMIDO,
        DETALHADO
    }
}
