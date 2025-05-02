package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * @author Mateus
 * @since 30/10/2015 11:50
 */
public class DemonstrativoDespesaPorFuncao {
    private String funcaoDescricao;
    private String fonteDeRecursoDescricao;
    private String contaCodigo;
    private String contaDescricao;
    private Integer nivelConta;
    private String esferaOrcamentaria;
    private BigDecimal valor;

    public DemonstrativoDespesaPorFuncao() {
    }

    public String getFuncaoDescricao() {
        return funcaoDescricao;
    }

    public void setFuncaoDescricao(String funcaoDescricao) {
        this.funcaoDescricao = funcaoDescricao;
    }

    public String getFonteDeRecursoDescricao() {
        return fonteDeRecursoDescricao;
    }

    public void setFonteDeRecursoDescricao(String fonteDeRecursoDescricao) {
        this.fonteDeRecursoDescricao = fonteDeRecursoDescricao;
    }

    public String getContaDescricao() {
        return contaDescricao;
    }

    public void setContaDescricao(String contaDescricao) {
        this.contaDescricao = contaDescricao;
    }

    public Integer getNivelConta() {
        return nivelConta;
    }

    public void setNivelConta(Integer nivelConta) {
        this.nivelConta = nivelConta;
    }

    public String getEsferaOrcamentaria() {
        return esferaOrcamentaria;
    }

    public void setEsferaOrcamentaria(String esferaOrcamentaria) {
        this.esferaOrcamentaria = esferaOrcamentaria;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getContaCodigo() {
        return contaCodigo;
    }

    public void setContaCodigo(String contaCodigo) {
        this.contaCodigo = contaCodigo;
    }
}
