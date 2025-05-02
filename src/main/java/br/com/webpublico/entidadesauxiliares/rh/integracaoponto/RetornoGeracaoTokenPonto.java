package br.com.webpublico.entidadesauxiliares.rh.integracaoponto;

public class RetornoGeracaoTokenPonto {

    private String token;
    private String statusGeracaoToken;


    public RetornoGeracaoTokenPonto() {
        this.token = "";
        this.statusGeracaoToken = "";
    }

    public RetornoGeracaoTokenPonto(String token, String statusGeracaoToken) {
        this.token = token;
        this.statusGeracaoToken = statusGeracaoToken;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getStatusGeracaoToken() {
        return statusGeracaoToken;
    }

    public void setStatusGeracaoToken(String statusGeracaoToken) {
        this.statusGeracaoToken = statusGeracaoToken;
    }
}
