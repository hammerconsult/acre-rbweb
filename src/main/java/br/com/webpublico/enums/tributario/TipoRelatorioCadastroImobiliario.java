package br.com.webpublico.enums.tributario;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoRelatorioCadastroImobiliario implements EnumComDescricao {

    CADASTRO_IMOBILIARIO("Cadastro Imobiliário"),
    BCI("BCI");

    private String descricao;

    TipoRelatorioCadastroImobiliario(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
