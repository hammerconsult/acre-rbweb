package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Atributo;
import br.com.webpublico.entidades.GrupoAtributo;

import java.io.Serializable;
import java.util.List;

public class ComponenteAtributoGenerico implements Serializable {

    private List<GrupoAtributo> gruposAtributo;
    private List<Atributo> atributos;

    public ComponenteAtributoGenerico() {
    }

    public List<GrupoAtributo> getGruposAtributo() {
        return gruposAtributo;
    }

    public void setGruposAtributo(List<GrupoAtributo> gruposAtributo) {
        this.gruposAtributo = gruposAtributo;
    }

    public List<Atributo> getAtributos() {
        return atributos;
    }

    public void setAtributos(List<Atributo> atributos) {
        this.atributos = atributos;
    }
}
