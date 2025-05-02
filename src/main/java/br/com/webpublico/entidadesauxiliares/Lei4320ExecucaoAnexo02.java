/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Edi
 */
public class Lei4320ExecucaoAnexo02 {

    private String codigo;
    private String descricao;
    private String codigoOrgao;
    private String descricaoOrgao;
    private String codigoUnidade;
    private String descricaoUnidade;
    private BigDecimal desdobramento;
    private BigDecimal modalidade;
    private BigDecimal categoriaEconomica;
    private BigDecimal valor;
    private Integer nivel;
    private Long idOrgao;
    private List<Lei4320ExecucaoAnexo02> itens;

    public Lei4320ExecucaoAnexo02() {
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

    public BigDecimal getDesdobramento() {
        return desdobramento;
    }

    public void setDesdobramento(BigDecimal desdobramento) {
        this.desdobramento = desdobramento;
    }

    public BigDecimal getModalidade() {
        return modalidade;
    }

    public void setModalidade(BigDecimal modalidade) {
        this.modalidade = modalidade;
    }

    public BigDecimal getCategoriaEconomica() {
        return categoriaEconomica;
    }

    public void setCategoriaEconomica(BigDecimal categoriaEconomica) {
        this.categoriaEconomica = categoriaEconomica;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }

    public String getCodigoOrgao() {
        return codigoOrgao;
    }

    public void setCodigoOrgao(String codigoOrgao) {
        this.codigoOrgao = codigoOrgao;
    }

    public String getDescricaoOrgao() {
        return descricaoOrgao;
    }

    public void setDescricaoOrgao(String descricaoOrgao) {
        this.descricaoOrgao = descricaoOrgao;
    }

    public String getCodigoUnidade() {
        return codigoUnidade;
    }

    public void setCodigoUnidade(String codigoUnidade) {
        this.codigoUnidade = codigoUnidade;
    }

    public String getDescricaoUnidade() {
        return descricaoUnidade;
    }

    public void setDescricaoUnidade(String descricaoUnidade) {
        this.descricaoUnidade = descricaoUnidade;
    }

    public Long getIdOrgao() {
        return idOrgao;
    }

    public void setIdOrgao(Long idOrgao) {
        this.idOrgao = idOrgao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public List<Lei4320ExecucaoAnexo02> getItens() {
        return itens;
    }

    public void setItens(List<Lei4320ExecucaoAnexo02> itens) {
        this.itens = itens;
    }
}
