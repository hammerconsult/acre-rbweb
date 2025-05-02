/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;

import java.util.Date;

/**
 * @author Leonardo
 */
public class ConsultaTermoInscricaoDividaAtiva implements Comparable<ConsultaTermoInscricaoDividaAtiva> {

    private String numeroCadastro;
    private String divida;
    private Integer exercicio;
    private Date dataInscricao;
    private Long numeroTermo;
    private Long numeroLivro;
    private Long paginaLivro;
    private Long sequencia;
    private TermoInscricaoDividaAtiva termo;
    private InscricaoDividaAtiva inscricao;
    private LinhaDoLivroDividaAtiva linha;
    private Cadastro cadastro;
    private Pessoa pessoa;

    public ConsultaTermoInscricaoDividaAtiva(Cadastro cadastro, Pessoa pessoa, Divida divida, InscricaoDividaAtiva inscricao, TermoInscricaoDividaAtiva termo, LinhaDoLivroDividaAtiva linha) {
        if (cadastro == null) {
        } else {
            this.numeroCadastro = cadastro.getNumeroCadastro();
        }
        this.cadastro = cadastro;
        this.pessoa = pessoa;
        this.divida = divida.getDescricao();
        this.inscricao = inscricao;
        this.exercicio = inscricao.getExercicio().getAno();
        this.dataInscricao = inscricao.getDataInscricao();
        this.numeroTermo = Long.parseLong(linha.getTermoInscricaoDividaAtiva().getNumero());
        this.numeroLivro = linha.getItemLivroDividaAtiva().getLivroDividaAtiva().getNumero();
        this.paginaLivro = linha.getPagina();
        this.sequencia = linha.getSequencia();
        this.linha = linha;
        this.termo = linha.getTermoInscricaoDividaAtiva();
    }


    public ConsultaTermoInscricaoDividaAtiva(Cadastro cadastro, Pessoa pessoa, Divida divida) {
        this.cadastro = cadastro;
        this.pessoa = pessoa;
        this.divida = divida.getDescricao();
    }

    public ConsultaTermoInscricaoDividaAtiva() {
    }

    public Date getDataInscricao() {
        return dataInscricao;
    }

    public void setDataInscricao(Date dataInscricao) {
        this.dataInscricao = dataInscricao;
    }

    public String getDivida() {
        return divida;
    }

    public void setDivida(String divida) {
        this.divida = divida;
    }

    public Integer getExercicio() {
        return exercicio;
    }

    public void setExercicio(Integer exercicio) {
        this.exercicio = exercicio;
    }

    public String getNumeroCadastro() {
        return numeroCadastro;
    }

    public void setNumeroCadastro(String numeroCadastro) {
        this.numeroCadastro = numeroCadastro;
    }

    public Long getNumeroTermo() {
        return numeroTermo;
    }

    public void setNumeroTermo(Long numeroTermo) {
        this.numeroTermo = numeroTermo;
    }

    public Long getNumeroLivro() {
        return numeroLivro;
    }

    public void setNumeroLivro(Long numeroLivro) {
        this.numeroLivro = numeroLivro;
    }

    public Long getPaginaLivro() {
        return paginaLivro;
    }

    public void setPaginaLivro(Long paginaLivro) {
        this.paginaLivro = paginaLivro;
    }

    public Long getSequencia() {
        return sequencia;
    }

    public void setSequencia(Long sequencia) {
        this.sequencia = sequencia;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Cadastro getCadastro() {
        return cadastro;
    }

    public void setCadastro(Cadastro cadastro) {
        this.cadastro = cadastro;
    }

    public TermoInscricaoDividaAtiva getTermo() {
        return termo;
    }

    public void setTermo(TermoInscricaoDividaAtiva termo) {
        this.termo = termo;
    }

    public InscricaoDividaAtiva getInscricao() {
        return inscricao;
    }

    public void setInscricao(InscricaoDividaAtiva inscricao) {
        this.inscricao = inscricao;
    }

    public LinhaDoLivroDividaAtiva getLinha() {
        return linha;
    }

    public void setLinha(LinhaDoLivroDividaAtiva linha) {
        this.linha = linha;
    }

    @Override
    public int compareTo(ConsultaTermoInscricaoDividaAtiva o) {
        if (o.getNumeroCadastro() == null) {
            return 0;
        }
        return this.numeroCadastro.compareTo(o.getNumeroCadastro());
    }
}
