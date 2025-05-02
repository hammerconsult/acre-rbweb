/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

public enum TipoAlteracaoCertidaoDA implements EnumComDescricao {

    CANCELAR("Cancelar", true),
    SUSPENDER("Suspender", true),
    REATIVAR("Reativar", true),
    AJUIZAR("Ajuizar", false),
    PENHORAR("Penhorar", false);

    private String descricao;
    private Boolean visivel;

    public String getDescricao() {
        return descricao;
    }

    public Boolean getVisivel() {
        return visivel;
    }

    private TipoAlteracaoCertidaoDA(String descricao, Boolean visivel) {
        this.descricao = descricao;
        this.visivel = visivel;
    }

    public static List<SelectItem> asSelectItemList() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        lista.add(new SelectItem(null, ""));
        for (TipoAlteracaoCertidaoDA tipo : TipoAlteracaoCertidaoDA.values()) {
            if (tipo.getVisivel()) {
                lista.add(new SelectItem(tipo, tipo.getDescricao()));
            }
        }
        return lista;
    }

}
