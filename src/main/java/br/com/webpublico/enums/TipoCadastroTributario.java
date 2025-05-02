package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.tributario.TipoCadastroTributarioDTO;
import com.google.common.collect.Lists;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

public enum TipoCadastroTributario {

    IMOBILIARIO("IMOBILIÁRIO", "Cadastro Imobiliário", 2, "B.C.I."),
    ECONOMICO("ECONÔMICO", "Cadastro Econômico", 3, "C.M.C."),
    RURAL("RURAL", "Cadastro Rural", 4, "B.C.R."),
    PESSOA("CONTRIBUINTE GERAL", "Geral Por Contribuinte", 1, "");
    private String descricao;
    private String descricaoLonga;
    private Integer ordem;
    private String sigla;

    TipoCadastroTributario(String descricao, String descricaoLonga, Integer ordem, String sigla) {
        this.descricao = descricao;
        this.descricaoLonga = descricaoLonga;
        this.ordem = ordem;
        this.sigla = sigla;
    }

    public static List<SelectItem> asSelectItemList() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        lista.add(new SelectItem(null, ""));
        for (TipoCadastroTributario tipo : TipoCadastroTributario.values()) {
            lista.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return lista;
    }

    public static List<TipoCadastroTributario> getTodosSemRural() {
        return Lists.newArrayList(IMOBILIARIO, ECONOMICO, PESSOA);
    }

    public String getDescricao() {
        return descricao;
    }

    public String getDescricaoLonga() {
        return descricaoLonga;
    }

    public String getSigla() {
        return sigla;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public boolean isImobiliario() {
        return IMOBILIARIO.equals(this);
    }

    public boolean isEconomico() {
        return ECONOMICO.equals(this);
    }

    public boolean isPessoa() {
        return PESSOA.equals(this);
    }

    public boolean isRural() {
        return RURAL.equals(this);
    }

    public TipoCadastroTributarioDTO toWebreport() {
        try {
            return TipoCadastroTributarioDTO.valueOf(this.name());
        } catch (Exception e) {
            return TipoCadastroTributarioDTO.PESSOA;
        }
    }
}

