package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.LocalEstoque;
import br.com.webpublico.entidades.Material;

public class MaterialLocalEstoqueVO {

    private Material material;
    private LocalEstoque localEstoque;

    public MaterialLocalEstoqueVO(Material material, LocalEstoque localEstoque) {
        this.material = material;
        this.localEstoque = localEstoque;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public LocalEstoque getLocalEstoque() {
        return localEstoque;
    }

    public void setLocalEstoque(LocalEstoque localEstoque) {
        this.localEstoque = localEstoque;
    }
}
