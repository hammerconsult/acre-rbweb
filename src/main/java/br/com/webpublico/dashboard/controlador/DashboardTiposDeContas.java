package br.com.webpublico.dashboard.controlador;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

public enum DashboardTiposDeContas {

    DESPESACORRENTE("CORRENTE", "Despesa Corrente"),
    DESPESACAPITAL("CAPITAL", "Despesa Capital");

    private String descricao;
    private String descricaoLonga;

    DashboardTiposDeContas(String descricao, String descricaoLonga) {
        this.descricao = descricao;
        this.descricaoLonga = descricaoLonga;
    }

    public static List<SelectItem> asSelectItemList() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem(null, ""));
        for (DashboardTiposDeContas tipo : DashboardTiposDeContas.values()) {
            lista.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return lista;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricaoLonga() {
        return descricaoLonga;
    }

    public void setDescricaoLonga(String descricaoLonga) {
        this.descricaoLonga = descricaoLonga;
    }

}
