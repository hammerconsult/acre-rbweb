package br.com.webpublico.entidadesauxiliares.email;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

public class EmailDTO implements Serializable {

    private String sistema;
    private String de;
    private String para;
    private String assunto;
    private String conteudo;
    private Boolean html;
    private List<AnexoDTO> anexos;
    private Boolean aplicacaoProducao;

    public EmailDTO() {
        sistema = "rbweb";
        html = Boolean.FALSE;
        anexos = Lists.newArrayList();
        aplicacaoProducao = Boolean.FALSE;
    }

    public String getDe() {
        return de;
    }

    public void setDe(String de) {
        this.de = de;
    }

    public String getPara() {
        return para;
    }

    public void setPara(String para) {
        this.para = para;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public Boolean getHtml() {
        return html;
    }

    public void setHtml(Boolean html) {
        this.html = html;
    }

    public List<AnexoDTO> getAnexos() {
        return anexos;
    }

    public void setAnexos(List<AnexoDTO> anexos) {
        this.anexos = anexos;
    }

    public Boolean getAplicacaoProducao() {
        return aplicacaoProducao;
    }

    public void setAplicacaoProducao(Boolean aplicacaoProducao) {
        this.aplicacaoProducao = aplicacaoProducao;
    }

    public String getSistema() {
        return sistema;
    }

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }
}
