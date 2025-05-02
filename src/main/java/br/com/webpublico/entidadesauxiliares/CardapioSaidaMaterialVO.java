package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.LocalEstoque;

import java.util.List;

public class CardapioSaidaMaterialVO {

    private LocalEstoque localEstoque;
    private String historico;
    private List<CardapioSaidaMaterialItemVO> itens;

    public LocalEstoque getLocalEstoque() {
        return localEstoque;
    }

    public void setLocalEstoque(LocalEstoque localEstoque) {
        this.localEstoque = localEstoque;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public List<CardapioSaidaMaterialItemVO> getItens() {
        return itens;
    }

    public void setItens(List<CardapioSaidaMaterialItemVO> itens) {
        this.itens = itens;
    }
}
