package br.com.webpublico.dte.enums;

import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.faces.model.SelectItem;
import java.util.List;

public enum TipoParametroDte {

    URL_APLICACAO("Url da aplicação", null);

    private List<String> valoresPossiveis;
    private String descricao;

    TipoParametroDte(String descricao, List<String> valoresPossiveis) {
        this.valoresPossiveis = valoresPossiveis;
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public List<String> getValoresPossiveis() {
        return valoresPossiveis;
    }

    public List<SelectItem> getValoresPossiveisSelectItem() {
        return Util.getListSelectItem(valoresPossiveis != null ? valoresPossiveis : Lists.newArrayList());
    }

    public enum ParametroBoolean {
        TRUE, FALSE;

        static List<String> toStringArray() {
            List<String> retorno = Lists.newArrayList();
            for (int i = 0; i < values().length; i++) {
                ParametroBoolean value = values()[i];
                retorno.add(value.name());
            }
            return retorno;
        }
    }

}
