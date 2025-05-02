package br.com.webpublico.entidadesauxiliares.rh;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by fox_m on 06/04/2016.
 */
public class FolhaPorSecretariaSummary implements Serializable, Comparable<FolhaPorSecretariaSummary> {
    private String codigo;
    private String descricao;
    private String tipoRetorno;
    private String inss;
    private String rpps;
    private String irrf;
    private BigDecimal valor;
    private BigDecimal valorLiquido;
    private BigDecimal quantidadeServidores;

    public FolhaPorSecretariaSummary() {
        inss = "";
        rpps = "";
        irrf = "";
    }

    public String getInss() {
        return inss;
    }

    public void setInss(String inss) {
        this.inss = inss;
    }

    public String getRpps() {
        return rpps;
    }

    public void setRpps(String rpps) {
        this.rpps = rpps;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipoRetorno() {
        return tipoRetorno;
    }

    public void setTipoRetorno(String tipoRetorno) {
        this.tipoRetorno = tipoRetorno;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getQuantidadeServidores() {
        return quantidadeServidores;
    }

    public void setQuantidadeServidores(BigDecimal quantidadeServidores) {
        this.quantidadeServidores = quantidadeServidores;
    }

    public String getIrrf() {
        return irrf;
    }

    public void setIrrf(String irrf) {
        this.irrf = irrf;
    }

    public BigDecimal getValorLiquido() {
        return valorLiquido;
    }

    public void setValorLiquido(BigDecimal valorLiquido) {
        this.valorLiquido = valorLiquido;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FolhaPorSecretariaSummary that = (FolhaPorSecretariaSummary) o;

        if (!codigo.equals(that.codigo)) return false;
        return tipoRetorno.equals(that.tipoRetorno);

    }

    @Override
    public int hashCode() {
        int result = codigo.hashCode();
        result = 31 * result + tipoRetorno.hashCode();
        return result;
    }

    @Override
    public int compareTo(FolhaPorSecretariaSummary o) {
        int comparadorA = this.getTipoRetorno().compareTo(o.getTipoRetorno());
        if (comparadorA == 0) {
            return this.getCodigo().compareTo(o.getCodigo());
        }
        return comparadorA;
    }
}
