
package br.com.webpublico.enums.tributario;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum CodigoFaceSimNao implements EnumComDescricao {

    SIM("Sim"),
    NAO("Não");

    private String descricao;

    CodigoFaceSimNao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
