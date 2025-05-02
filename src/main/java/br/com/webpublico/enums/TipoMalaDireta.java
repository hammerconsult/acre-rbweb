package br.com.webpublico.enums;

/**
 * Created by Rodolfo on 04/05/2017.
 */
public enum TipoMalaDireta {
    NOTIFICACAO("Notificação", "Gera a carta com o texto pré configurado para encaminhamento ao contribuinte, podendo ou não ter relação com os débitos do contribuinte."),
    COBRANCA("Cobrança", "Gera o documento de arrecadação para encaminhamento ao contribuinte."),
    NOTIFICACAO_COBRANCA("Notificação e Cobrança", "Gera a carta e o documento de arrecadação, juntos, para encaminhamento ao contribuinte.");

    String descricao;
    String utilizacao;

    TipoMalaDireta(String descricao, String utilizacao) {
        this.descricao = descricao;
        this.utilizacao = utilizacao;
    }

    public boolean isCobranca() {
        return NOTIFICACAO_COBRANCA.equals(this) || COBRANCA.equals(this);
    }

    public String getDescricao() {
        return descricao;
    }

    public String getUtilizacao() {
        return utilizacao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
