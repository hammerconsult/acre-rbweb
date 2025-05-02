package br.com.webpublico.entidadesauxiliares;

public class ValidacaoCertidaoDoctoOficial {

    private Boolean valido;
    private String mensagem;

    public ValidacaoCertidaoDoctoOficial(Boolean valido) {
        this.valido = valido;
        this.mensagem = "";
    }

    public ValidacaoCertidaoDoctoOficial() {
        this.valido = false;
        this.mensagem = "";
    }

    public Boolean getValido() {
        return valido != null ? valido : false;
    }

    public void setValido(Boolean valido) {
        this.valido = valido;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
