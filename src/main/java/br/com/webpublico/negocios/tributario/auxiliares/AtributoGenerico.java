package br.com.webpublico.negocios.tributario.auxiliares;


import java.io.Serializable;

public class AtributoGenerico implements Serializable {
    private String identificacao;
    private Long idValoratributo;
    private boolean ativo;
    private transient Object valor;

    public AtributoGenerico() {

    }

    public String getIdentificacao() {
        return identificacao;
    }

    public Object getValor() {
        return valor;
    }

    public void setIdentificacao(String identificacao) {
        this.identificacao = identificacao;
    }

    public void setValor(Object valor) {
        this.valor = valor;
    }

    public Long getIdValoratributo() {
        return idValoratributo;
    }

    public void setIdValoratributo(Long idValoratributo) {
        this.idValoratributo = idValoratributo;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
