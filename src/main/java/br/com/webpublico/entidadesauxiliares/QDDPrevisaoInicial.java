package br.com.webpublico.entidadesauxiliares;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Mateus
 * @since 29/10/2015 10:36
 */
public class QDDPrevisaoInicial {

    private String orgao;
    private String unidade;
    private String projetoAtividade;
    private String subFuncao;
    private String esferaOrcamentaria;
    private String categoriaEconomica;
    private String fonteDeRecurso;
    private BigDecimal valor;
    private List<QDDPrevisaoInicial> listaCategoriasEconomicas;

    public QDDPrevisaoInicial() {
        listaCategoriasEconomicas = Lists.newArrayList();
    }

    public String getOrgao() {
        return orgao;
    }

    public void setOrgao(String orgao) {
        this.orgao = orgao;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public String getProjetoAtividade() {
        return projetoAtividade;
    }

    public void setProjetoAtividade(String projetoAtividade) {
        this.projetoAtividade = projetoAtividade;
    }

    public String getSubFuncao() {
        return subFuncao;
    }

    public void setSubFuncao(String subFuncao) {
        this.subFuncao = subFuncao;
    }

    public String getEsferaOrcamentaria() {
        return esferaOrcamentaria;
    }

    public void setEsferaOrcamentaria(String esferaOrcamentaria) {
        this.esferaOrcamentaria = esferaOrcamentaria;
    }

    public String getCategoriaEconomica() {
        return categoriaEconomica;
    }

    public void setCategoriaEconomica(String categoriaEconomica) {
        this.categoriaEconomica = categoriaEconomica;
    }

    public String getFonteDeRecurso() {
        return fonteDeRecurso;
    }

    public void setFonteDeRecurso(String fonteDeRecurso) {
        this.fonteDeRecurso = fonteDeRecurso;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public List<QDDPrevisaoInicial> getListaCategoriasEconomicas() {
        return listaCategoriasEconomicas;
    }

    public void setListaCategoriasEconomicas(List<QDDPrevisaoInicial> listaCategoriasEconomicas) {
        this.listaCategoriasEconomicas = listaCategoriasEconomicas;
    }
}
