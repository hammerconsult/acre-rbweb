/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.util.obn350.*;

/**
 *
 * @author reidocrime
 */
public enum ConfiguracaoRegistroTipoUmOBN600 {

    TIPO("Tipo", 1, 0, 1),
    CODIGOAGENCIADEVOLUCAO("Agencia U.G Devolução", 5, 1, 6),
    CODIGOUNIDADEGESTORA("Código U.G", 11, 6, 17),
    CONTAUGDEVOLUCAO("Conta U.G Devolução", 10, 17, 27),
    BRANCOS("Brancos", 27, 26, 53),
    NOMEUG("Nome Unidade", 53, 45, 98),
    BRANCO("Branco", 98, 251, 350);
    private String descricao;
    private int tamanho;
    private int inicio;
    private int fim;

    private ConfiguracaoRegistroTipoUmOBN600(String descricao, int tamanho, int inicio, int fim) {
        this.descricao = descricao;
        this.tamanho = tamanho;
        this.inicio = inicio;
        this.fim = fim;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getTamanho() {
        return tamanho;
    }

    public int getInicio() {
        return inicio;
    }

    public int getFim() {
        return fim;
    }

}
