package br.com.webpublico.ws.model;

import br.com.webpublico.enums.TipoAlvara;

import java.util.Date;

public class WsAutenticaAlvara {

    private String cpfCnpj;
    private TipoAlvara tipoAlvara;
    private Boolean provisorio;
    private Date dataEmissao;
    private String inscricaoCadastral;
    private String assinaturaParaAutenticar;

    public WsAutenticaAlvara() {
        this.provisorio = false;
    }

    public String getAssinaturaParaAutenticar() {
        return assinaturaParaAutenticar;
    }

    public void setAssinaturaParaAutenticar(String assinaturaParaAutenticar) {
        this.assinaturaParaAutenticar = assinaturaParaAutenticar;
    }

    public String getInscricaoCadastral() {
        return inscricaoCadastral;
    }

    public void setInscricaoCadastral(String inscricaoCadastral) {
        this.inscricaoCadastral = inscricaoCadastral;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public TipoAlvara getTipoAlvara() {
        return tipoAlvara;
    }

    public void setTipoAlvara(TipoAlvara tipoAlvara) {
        this.tipoAlvara = tipoAlvara;
    }

    public Boolean getProvisorio() {
        return provisorio;
    }

    public void setProvisorio(Boolean provisorio) {
        this.provisorio = provisorio;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }
}
