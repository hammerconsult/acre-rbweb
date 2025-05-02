package br.com.webpublico.pncp.enums;

public enum SituacaoPncp {

    NAO_ENVIADO("Não enviado", "Não foi possível realizar a integração com o PNCP."),
    AGUARDANDO_ENVIO("Aguardando envio", "Aguardando envio para o PNCP. Aguarde alguns minutos e consulte os eventos enviados."),
    ENVIADO_COM_SUCESSO("Enviado com sucesso","Integração com o PNCP realizada com sucesso. Aguarde alguns minutos e consulte os eventos enviados."),
    ENVIADO_COM_ERRO("Enviado com erro", "Integração com o PNCP realizada com erro. Aguarde alguns minutos e consulte os eventos enviados."),
    ERRO("Erro", "Erro integração com o PNCP.");
    private final String descricao;
    private final String mensagem;

    SituacaoPncp(String descricao, String mensagem) {
        this.descricao = descricao;
        this.mensagem = mensagem;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getMensagem() {
        return mensagem;
    }

    @Override
    public String toString() {
        return descricao.toString();
    }
}
