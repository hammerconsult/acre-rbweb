package br.com.webpublico.seguranca;

import java.io.Serializable;

public class AplicacaoDTO implements Serializable {
    public String nome;
    public Boolean ativo;
    public Integer usuariosAtivos = 0;
    public String versao;

    public AplicacaoDTO() {
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Boolean getAtivo() {
        return this.ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Integer getUsuariosAtivos() {
        return this.usuariosAtivos;
    }

    public void setUsuariosAtivos(Integer usuariosAtivos) {
        this.usuariosAtivos = usuariosAtivos;
    }

    public String getVersao() {
        return this.versao;
    }

    public void setVersao(String versao) {
        this.versao = versao;
    }

    public String toString() {
        return "AplicacaoDTO{nome='" + this.nome + '\'' + ", ativo=" + this.ativo + ", usuariosAtivos=" + this.usuariosAtivos + ", versao='" + this.versao + '\'' + '}';
    }
}
