package br.com.webpublico.enums.tributario;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum StatusSolicitacaoPIX implements EnumComDescricao {
    ATIVA("Ativa"),
    CONCLUIDA("Concluída"),
    EM_PROCESSAMENTO("Liquidação em Processamento"),
    NAO_REALIZADO("Não Utilizada"),
    DEVOLVIDO("Devolução Realizada"),
    REMOVIDA_PELO_USUARIO_RECEBEDOR("Removido Pelo Usuário"),
    REMOVIDA_PELO_PSP("Removido Pelo PSP Recebedor");

    private final String descricao;

    StatusSolicitacaoPIX(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
