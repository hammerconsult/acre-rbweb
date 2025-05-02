package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.LocalEstoque;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class ConsultaLocalEstoque implements Comparable<ConsultaLocalEstoque> {

    private LocalEstoque localEstoque;
    private List<ConsultaLocalEstoqueOrcamentario> orcamentarias;
    private List<ConsultaLocalEstoqueMaterial> materiais;

    public ConsultaLocalEstoque(LocalEstoque localEstoque) {
        this.localEstoque = localEstoque;
        orcamentarias = Lists.newArrayList();
        materiais = Lists.newArrayList();
    }

    public LocalEstoque getLocalEstoque() {
        return localEstoque;
    }

    public void setLocalEstoque(LocalEstoque localEstoque) {
        this.localEstoque = localEstoque;
    }

    public List<ConsultaLocalEstoqueMaterial> getMateriais() {
        return materiais;
    }

    public void setMateriais(List<ConsultaLocalEstoqueMaterial> materiais) {
        this.materiais = materiais;
    }

    public List<ConsultaLocalEstoqueOrcamentario> getOrcamentarias() {
        return orcamentarias;
    }

    public void setOrcamentarias(List<ConsultaLocalEstoqueOrcamentario> orcamentarias) {
        this.orcamentarias = orcamentarias;
    }

    public BigDecimal getValorTotalMateriais() {
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        for (ConsultaLocalEstoqueMaterial mp : this.materiais) {
            total = total.add(mp.getValorEstoque());
        }
        return total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConsultaLocalEstoque that = (ConsultaLocalEstoque) o;
        return localEstoque.equals(that.localEstoque);
    }

    @Override
    public int hashCode() {
        return localEstoque.hashCode();
    }

    @Override
    public int compareTo(ConsultaLocalEstoque o) {
        return this.getLocalEstoque().getCodigo().compareTo(o.getLocalEstoque().getCodigo());
    }
}
