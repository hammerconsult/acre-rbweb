package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Atributo;

import java.io.Serializable;

public class AtributoCadastroImobiliario implements Serializable {

    private Integer index;
    private Atributo atributo;

    public AtributoCadastroImobiliario() {
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Atributo getAtributo() {
        return atributo;
    }

    public void setAtributo(Atributo atributo) {
        this.atributo = atributo;
    }
}
