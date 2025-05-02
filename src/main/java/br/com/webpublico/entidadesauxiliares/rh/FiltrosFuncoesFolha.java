package br.com.webpublico.entidadesauxiliares.rh;

import java.util.List;

public class FiltrosFuncoesFolha {

    private String parteFuncaoRegra;
    private String parteFuncaoFormula;
    private String parteFuncaoFormulaValorIntegral;
    private String parteFuncaoReferencia;
    private String parteFuncaoValorBaseCalculo;
    private List<String> listaFiltradaMetodosRegra;
    private List<String> listaFiltradaMetodosFormula;
    private List<String> listaFiltradaMetodosFormulaValorIntegral;
    private List<String> listaFiltradaMetodosReferencia;
    private List<String> listaFiltradaMetodosValorBaseDeCalculo;

    public FiltrosFuncoesFolha(List<String> listaMetodosRegra,
                               List<String> listaMetodosFormula,
                               List<String> listaMetodosFormulaValorIntegral,
                               List<String> listaMetodosReferencia,
                               List<String> listaMetodosValorBaseDeCalculo) {
        this.parteFuncaoRegra = "";
        this.parteFuncaoFormula = "";
        this.parteFuncaoFormulaValorIntegral = "";
        this.parteFuncaoReferencia = "";
        this.parteFuncaoValorBaseCalculo = "";
        this.listaFiltradaMetodosRegra = listaMetodosRegra;
        this.listaFiltradaMetodosFormula = listaMetodosFormula;
        this.listaFiltradaMetodosFormulaValorIntegral = listaMetodosFormulaValorIntegral;
        this.listaFiltradaMetodosReferencia = listaMetodosReferencia;
        this.listaFiltradaMetodosValorBaseDeCalculo = listaMetodosValorBaseDeCalculo;
    }

    public String getParteFuncaoRegra() {
        return parteFuncaoRegra;
    }

    public void setParteFuncaoRegra(String parteFuncaoRegra) {
        this.parteFuncaoRegra = parteFuncaoRegra;
    }

    public String getParteFuncaoFormula() {
        return parteFuncaoFormula;
    }

    public void setParteFuncaoFormula(String parteFuncaoFormula) {
        this.parteFuncaoFormula = parteFuncaoFormula;
    }

    public String getParteFuncaoFormulaValorIntegral() {
        return parteFuncaoFormulaValorIntegral;
    }

    public void setParteFuncaoFormulaValorIntegral(String parteFuncaoFormulaValorIntegral) {
        this.parteFuncaoFormulaValorIntegral = parteFuncaoFormulaValorIntegral;
    }

    public String getParteFuncaoReferencia() {
        return parteFuncaoReferencia;
    }

    public void setParteFuncaoReferencia(String parteFuncaoReferencia) {
        this.parteFuncaoReferencia = parteFuncaoReferencia;
    }

    public String getParteFuncaoValorBaseCalculo() {
        return parteFuncaoValorBaseCalculo;
    }

    public void setParteFuncaoValorBaseCalculo(String parteFuncaoValorBaseCalculo) {
        this.parteFuncaoValorBaseCalculo = parteFuncaoValorBaseCalculo;
    }

    public List<String> getListaFiltradaMetodosRegra() {
        return listaFiltradaMetodosRegra;
    }

    public void setListaFiltradaMetodosRegra(List<String> listaFiltradaMetodosRegra) {
        this.listaFiltradaMetodosRegra = listaFiltradaMetodosRegra;
    }

    public List<String> getListaFiltradaMetodosFormula() {
        return listaFiltradaMetodosFormula;
    }

    public void setListaFiltradaMetodosFormula(List<String> listaFiltradaMetodosFormula) {
        this.listaFiltradaMetodosFormula = listaFiltradaMetodosFormula;
    }

    public List<String> getListaFiltradaMetodosFormulaValorIntegral() {
        return listaFiltradaMetodosFormulaValorIntegral;
    }

    public void setListaFiltradaMetodosFormulaValorIntegral(List<String> listaFiltradaMetodosFormulaValorIntegral) {
        this.listaFiltradaMetodosFormulaValorIntegral = listaFiltradaMetodosFormulaValorIntegral;
    }

    public List<String> getListaFiltradaMetodosReferencia() {
        return listaFiltradaMetodosReferencia;
    }

    public void setListaFiltradaMetodosReferencia(List<String> listaFiltradaMetodosReferencia) {
        this.listaFiltradaMetodosReferencia = listaFiltradaMetodosReferencia;
    }

    public List<String> getListaFiltradaMetodosValorBaseDeCalculo() {
        return listaFiltradaMetodosValorBaseDeCalculo;
    }

    public void setListaFiltradaMetodosValorBaseDeCalculo(List<String> listaFiltradaMetodosValorBaseDeCalculo) {
        this.listaFiltradaMetodosValorBaseDeCalculo = listaFiltradaMetodosValorBaseDeCalculo;
    }
}
