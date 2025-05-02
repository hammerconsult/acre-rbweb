/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;

/**
 * @author leonardo
 */
@GrupoDiagrama(nome = "Certidao")
public enum TipoCadastroDoctoOficial {

    CADASTROECONOMICO("Cadastro Econômico"),
    CADASTROIMOBILIARIO("Cadastro Imobiliário"),
    CADASTRORURAL("Cadastro Rural"),
    PESSOAFISICA("Pessoa Física"),
    PESSOAJURIDICA("Pessoa Jurídica"),
    NENHUM("Nenhum");
    private String descricao;

    private TipoCadastroDoctoOficial(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public boolean isPessoaFisica() {
        return PESSOAFISICA.equals(this);
    }

    public boolean isPessoaJuridica() {
        return PESSOAJURIDICA.equals(this);
    }

    public boolean isCadastroImobiliario() {
        return CADASTROIMOBILIARIO.equals(this);
    }

}
