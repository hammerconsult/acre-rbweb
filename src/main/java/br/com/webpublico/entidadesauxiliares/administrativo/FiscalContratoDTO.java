package br.com.webpublico.entidadesauxiliares.administrativo;

import br.com.webpublico.enums.TipoFiscalContrato;

public class FiscalContratoDTO {
    private Long id;
    private String responsavel;
    private TipoFiscalContrato tipo;

    public FiscalContratoDTO (Long id, String responsavel, TipoFiscalContrato tipo) {
        this.id = id;
        this.responsavel = responsavel;
        this.tipo = tipo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public TipoFiscalContrato getTipo() {
        return tipo;
    }

    public void setTipo(TipoFiscalContrato tipo) {
        this.tipo = tipo;
    }
}
