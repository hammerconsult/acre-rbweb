package br.com.webpublico.entidadesauxiliares;

import java.util.List;

public class ConfiguracaoIPTUXML {
    private List<ConfiguracaoIPTUItemXML> itens;
    private String descricao;
    private String biblioteca;

    public List<ConfiguracaoIPTUItemXML> getItens() {
        return itens;
    }

    public void setItens(List<ConfiguracaoIPTUItemXML> itens) {
        this.itens = itens;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getBiblioteca() {
        return biblioteca;
    }

    public void setBiblioteca(String biblioteca) {
        this.biblioteca = biblioteca;
    }


}
