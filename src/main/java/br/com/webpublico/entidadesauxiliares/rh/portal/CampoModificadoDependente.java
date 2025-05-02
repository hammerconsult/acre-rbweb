package br.com.webpublico.entidadesauxiliares.rh.portal;

import br.com.webpublico.pessoa.enumeration.CamposPessoaDTO;

import java.io.Serializable;

public class CampoModificadoDependente implements Serializable {
    private CamposPessoaDTO campo;
    private String nome;
    private String conteudo;
    private Long idPessoa;
    private Long idDependente;

    public CampoModificadoDependente (CamposPessoaDTO campo, String nome, String conteudo, Long idPessoa, Long idDependente){
        this.campo = campo;
        this.nome = nome;
        this.conteudo = conteudo;
        this.idPessoa = idPessoa;
        this.idDependente = idDependente;
    }

    public CamposPessoaDTO getCampo() {
        return campo;
    }

    public void setCampo(CamposPessoaDTO campo) {
        this.campo = campo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public Long getIdPessoa() {
        return idPessoa;
    }

    public void setIdPessoa(Long idPessoa) {
        this.idPessoa = idPessoa;
    }

    public Long getIdDependente() {
        return idDependente;
    }

    public void setIdDependente(Long idDependente) {
        this.idDependente = idDependente;
    }
}
