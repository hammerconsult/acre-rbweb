package br.com.webpublico.entidadesauxiliares;

public class FiltroAssociacaoDocumentoFiscalLiquidacao {

    private String nomeColuna;
    private String valorColuna;
    private String condicao;
    private Boolean selecionado;
    private Boolean bloqueado;

    public FiltroAssociacaoDocumentoFiscalLiquidacao(String nomeColuna, String valorColuna, String condicao) {
        this.nomeColuna = nomeColuna;
        this.valorColuna = valorColuna;
        this.condicao = condicao;
        this.selecionado = true;
        this.bloqueado = false;
    }

    public String getNomeColuna() {
        return nomeColuna;
    }

    public void setNomeColuna(String nomeColuna) {
        this.nomeColuna = nomeColuna;
    }

    public String getValorColuna() {
        return valorColuna;
    }

    public void setValorColuna(String valorColuna) {
        this.valorColuna = valorColuna;
    }

    public String getCondicao() {
        return condicao;
    }

    public void setCondicao(String condicao) {
        this.condicao = condicao;
    }

    public Boolean getSelecionado() {
        return selecionado;
    }

    public Boolean getBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(Boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public void setSelecionado(Boolean selecionado) {
        this.selecionado = selecionado;
    }
}
