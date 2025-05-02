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
public enum ConfiguracaoHeaderObn600 {

    ZEROS("N° Sequencial", 35, 0, 35),
    DATAGERACAOARQUIVO("Data", 8, 35, 43),
    HORAGERACAOARQUIVO("Hora", 4, 43, 47),
    NUMEROREMESSACONSECUTIVO("N° Remessa", 5, 47, 52),
    C10E001("10E001", 6, 52, 58),
    NUMEROCONTRATO("N° Contrato", 9, 58, 67),
    BRANCOS("Branco", 276, 67, 343),
    NUMEROSEQUENCIAL("N° Sequencial", 7, 343, 350);
    private String descricao;
    private int tamanho;
    private int inicio;
    private int fim;

    private ConfiguracaoHeaderObn600(String descricao, int tamanho, int inicio, int fim) {
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
