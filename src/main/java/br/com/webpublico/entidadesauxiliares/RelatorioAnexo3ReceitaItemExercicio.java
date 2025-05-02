package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Romanini
 * Date: 02/09/13
 * Time: 09:26
 * To change this template use File | Settings | File Templates.
 */
public class RelatorioAnexo3ReceitaItemExercicio {
    private String descricao;
    private String ano;
    private BigDecimal valor;

    public RelatorioAnexo3ReceitaItemExercicio() {
    }

    public RelatorioAnexo3ReceitaItemExercicio(String ano, BigDecimal valor, String descricao) {
        this.ano = ano;
        this.valor = valor;
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
