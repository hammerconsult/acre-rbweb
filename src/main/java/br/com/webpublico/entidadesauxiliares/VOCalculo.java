package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Calculo;

public class VOCalculo {

    private Long idCalculo;
    private Calculo.TipoCalculo tipoCalculo;

    public VOCalculo() {
    }

    public Long getIdCalculo() {
        return idCalculo;
    }

    public void setIdCalculo(Long idCalculo) {
        this.idCalculo = idCalculo;
    }

    public Calculo.TipoCalculo getTipoCalculo() {
        return tipoCalculo;
    }

    public void setTipoCalculo(Calculo.TipoCalculo tipoCalculo) {
        this.tipoCalculo = tipoCalculo;
    }
}
