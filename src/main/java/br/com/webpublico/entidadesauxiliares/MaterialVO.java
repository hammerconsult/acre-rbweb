package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Material;

import java.io.Serializable;
import java.util.Objects;

public class MaterialVO implements Serializable {

    private Material material;
    private Boolean movimentadoEstoque;
    private Long criadoEm;

    public MaterialVO(Material material, Boolean movimentadoEstoque) {
        this.material = material;
        this.movimentadoEstoque = movimentadoEstoque;
        this.criadoEm = System.nanoTime();
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Boolean getMovimentadoEstoque() {
        return movimentadoEstoque;
    }

    public void setMovimentadoEstoque(Boolean movimentadoEstoque) {
        this.movimentadoEstoque = movimentadoEstoque;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MaterialVO that = (MaterialVO) o;
        return Objects.equals(criadoEm, that.criadoEm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(criadoEm);
    }
}
