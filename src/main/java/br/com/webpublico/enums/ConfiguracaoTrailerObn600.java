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
public enum ConfiguracaoTrailerObn600 {
    NOVES("Noves", 35, 0, 35),
    BRANCOS("Brancos",285,35,320),
    SOMATORIADOVALORES("Somatória dos Valores", 17,320,337),
    SOMATORIADETODASSEQUENCAIS("Branco", 13, 337, 350);
    private String descricao;
    private int tamanho;
    private int inicio;
    private int fim;

    private ConfiguracaoTrailerObn600(String descricao, int tamanho, int inicio, int fim) {
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
