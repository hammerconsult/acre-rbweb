/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.report;

import br.com.webpublico.entidades.HierarquiaOrganizacional;

import java.math.BigDecimal;

/**
 * @author reidocrime
 */
public class RelatorioAnexoDoisDespesa {

    private long id;
    private String codigo;
    private String especificacao;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private BigDecimal desdobramento;
    private BigDecimal elemento;
    private BigDecimal categoriaEconomica;
    private String unidade;

    public BigDecimal getCategoriaEconomica() {
        return categoriaEconomica;
    }

    public void setCategoriaEconomica(BigDecimal categoriaEconomica) {
        this.categoriaEconomica = categoriaEconomica;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public BigDecimal getDesdobramento() {
        return desdobramento;
    }

    public void setDesdobramento(BigDecimal desdobramento) {
        this.desdobramento = desdobramento;
    }

    public BigDecimal getElemento() {
        return elemento;
    }

    public void setElemento(BigDecimal elemento) {
        this.elemento = elemento;
    }

    public String getEspecificacao() {
        return especificacao;
    }

    public void setEspecificacao(String especificacao) {
        this.especificacao = especificacao;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }
}
