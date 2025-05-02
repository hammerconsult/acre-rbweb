package br.com.webpublico.entidadesauxiliares.rh;

import java.math.BigDecimal;

/**
 * Created by Tharlyson on 03/04/19.
 */
public class EventoRelatorioComparativoResumoGeralPorOrgao {

    private String codigo;
    private String descricao;
    private String tipo;
    private BigDecimal quantidadeComparativoUm;
    private BigDecimal quantidadeComparativoDois;
    private BigDecimal valorComparativoUm;
    private BigDecimal valorComparativoDois;
    private BigDecimal quantidadeDiferenca;
    private BigDecimal diferenca;

    private String grupo;

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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getQuantidadeComparativoUm() {
        return quantidadeComparativoUm;
    }

    public void setQuantidadeComparativoUm(BigDecimal quantidadeComparativoUm) {
        this.quantidadeComparativoUm = quantidadeComparativoUm;
    }

    public BigDecimal getQuantidadeComparativoDois() {
        return quantidadeComparativoDois;
    }

    public void setQuantidadeComparativoDois(BigDecimal quantidadeComparativoDois) {
        this.quantidadeComparativoDois = quantidadeComparativoDois;
    }

    public BigDecimal getValorComparativoUm() {
        return valorComparativoUm;
    }

    public void setValorComparativoUm(BigDecimal valorComparativoUm) {
        this.valorComparativoUm = valorComparativoUm;
    }

    public BigDecimal getValorComparativoDois() {
        return valorComparativoDois;
    }

    public void setValorComparativoDois(BigDecimal valorComparativoDois) {
        this.valorComparativoDois = valorComparativoDois;
    }

    public BigDecimal getQuantidadeDiferenca() {
        return quantidadeDiferenca;
    }

    public void setQuantidadeDiferenca(BigDecimal quantidadeDiferenca) {
        this.quantidadeDiferenca = quantidadeDiferenca;
    }

    public BigDecimal getDiferenca() {
        return diferenca;
    }

    public void setDiferenca(BigDecimal diferenca) {
        this.diferenca = diferenca;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }
}
