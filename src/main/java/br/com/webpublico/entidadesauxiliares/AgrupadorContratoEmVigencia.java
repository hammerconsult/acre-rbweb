package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.AgrupadorGOC;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class AgrupadorContratoEmVigencia {

    private AgrupadorGOC agrupadorGOC;
    private List<ContratoEmVigencia> contratos;
    private List<ContratoEmVigencia> contratosPesquisa;

    private String grupoObjetoCompra;
    private BigDecimal quantidadeContratada;
    private BigDecimal quantidadeLicitada;
    private BigDecimal quantidadeDisponivel;
    private List<AgrupadorContratoEmVigencia> gruposObjetoCompra;

    public AgrupadorContratoEmVigencia() {
        contratos = Lists.newArrayList();
        contratosPesquisa = Lists.newArrayList();
        gruposObjetoCompra = Lists.newArrayList();
        quantidadeContratada = BigDecimal.ZERO;
        quantidadeLicitada = BigDecimal.ZERO;
        quantidadeDisponivel = BigDecimal.ZERO;
    }

    public AgrupadorGOC getAgrupadorGOC() {
        return agrupadorGOC;
    }

    public void setAgrupadorGOC(AgrupadorGOC agrupadorGOC) {
        this.agrupadorGOC = agrupadorGOC;
    }

    public List<ContratoEmVigencia> getContratos() {
        return contratos;
    }

    public void setContratos(List<ContratoEmVigencia> contratos) {
        this.contratos = contratos;
    }

    public List<ContratoEmVigencia> getContratosPesquisa() {
        return contratosPesquisa;
    }

    public void setContratosPesquisa(List<ContratoEmVigencia> contratosPesquisa) {
        this.contratosPesquisa = contratosPesquisa;
    }

    public String getGrupoObjetoCompra() {
        return grupoObjetoCompra;
    }

    public void setGrupoObjetoCompra(String grupoObjetoCompra) {
        this.grupoObjetoCompra = grupoObjetoCompra;
    }

    public BigDecimal getQuantidadeContratada() {
        return quantidadeContratada;
    }

    public void setQuantidadeContratada(BigDecimal quantidadeContratada) {
        this.quantidadeContratada = quantidadeContratada;
    }

    public List<AgrupadorContratoEmVigencia> getGruposObjetoCompra() {
        return gruposObjetoCompra;
    }

    public void setGruposObjetoCompra(List<AgrupadorContratoEmVigencia> gruposObjetoCompra) {
        this.gruposObjetoCompra = gruposObjetoCompra;
    }

    public BigDecimal getQuantidadeLicitada() {
        return quantidadeLicitada;
    }

    public void setQuantidadeLicitada(BigDecimal quantidadeLicitada) {
        this.quantidadeLicitada = quantidadeLicitada;
    }

    public BigDecimal getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public void setQuantidadeDisponivel(BigDecimal quantidadeDisponivel) {
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    public BigDecimal getValorTotalContratos() {
        BigDecimal total = BigDecimal.ZERO;
        for (ContratoEmVigencia contrato : contratosPesquisa) {
            total = total.add(contrato.getValorTotal());
        }
        return total;
    }

    public Integer getQuantidadeContratos() {
        return contratosPesquisa.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgrupadorContratoEmVigencia that = (AgrupadorContratoEmVigencia) o;
        return Objects.equals(agrupadorGOC, that.agrupadorGOC);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agrupadorGOC);
    }
}
