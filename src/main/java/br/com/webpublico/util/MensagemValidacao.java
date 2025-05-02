package br.com.webpublico.util;


import br.com.webpublico.enums.TipoMensagemValidacao;

public class MensagemValidacao {
    private TipoMensagemValidacao tipo;
    private String clientId;
    private String summary;
    private String detail;

    public MensagemValidacao(TipoMensagemValidacao tipo, String clientId, String summary, String detail) {
        this.tipo = tipo;
        this.clientId = clientId;
        this.summary = summary;
        this.detail = detail;
    }

    public static MensagemValidacao info(String clientId, String summary, String detail) {
        return new MensagemValidacao(TipoMensagemValidacao.INFORMACAO, clientId, summary, detail);
    }

    public static MensagemValidacao warn(String clientId, String summary, String detail) {
        return new MensagemValidacao(TipoMensagemValidacao.ALERTA, clientId, summary, detail);
    }

    public static MensagemValidacao error(String clientId, String summary, String detail) {
        return new MensagemValidacao(TipoMensagemValidacao.ERRO, clientId, summary, detail);
    }

    public static MensagemValidacao fatal(String clientId, String summary, String detail) {
        return new MensagemValidacao(TipoMensagemValidacao.ERRO_FATAL, clientId, summary, detail);
    }

    public TipoMensagemValidacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoMensagemValidacao tipo) {
        this.tipo = tipo;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

}
