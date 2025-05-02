package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Material;

public class RefeicaoMaterialVO {

    private Material material;
    private Boolean selecionado;


    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Boolean getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Boolean selecionado) {
        this.selecionado = selecionado;
    }
}
