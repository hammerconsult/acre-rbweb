/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

/**
 * @author Terminal-2
 */
public class ConsultaDBGenerica {
    private String tabela;
    private String coluna;
    private String registro;

    public String getColuna() {
        return coluna;
    }

    public void setColuna(String coluna) {
        this.coluna = coluna;
    }

    public String getRegistro() {
        return registro;
    }

    public void setRegistro(String registro) {
        this.registro = registro;
    }

    public String getTabela() {
        return tabela;
    }

    public void setTabela(String tabela) {
        this.tabela = tabela;
    }

    public ConsultaDBGenerica(String tabela, String coluna, String registro) {
        this.tabela = tabela;
        this.coluna = coluna;
        this.registro = registro;
    }

}
