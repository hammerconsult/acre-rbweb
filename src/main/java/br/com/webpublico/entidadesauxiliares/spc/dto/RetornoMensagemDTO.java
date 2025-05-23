package br.com.webpublico.entidadesauxiliares.spc.dto;

public class RetornoMensagemDTO {

    private Boolean ok;
    private String mensagem;

    public RetornoMensagemDTO() {
    }

    public Boolean getOk() {
        return ok;
    }

    public void setOk(Boolean ok) {
        this.ok = ok;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
