/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.rh.portal.atualizacaocadastral.CertidaoCasamentoPortal;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.pessoa.dto.CertidaoCasamentoDTO;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 * @author andre
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
public class CertidaoCasamento extends DocumentoPessoal implements Serializable {

    @Tabelavel
    @Etiqueta("Nome do Cônjuge")
    private String nomeConjuge;
    @Tabelavel
    @Etiqueta("Nome do cartório")
    private String nomeCartorio;
    @Tabelavel
    @Etiqueta("Número do livro")
    private String numeroLivro;
    @Tabelavel
    @Etiqueta("Número da folha")
    private Integer numeroFolha;
    @Tabelavel
    @Etiqueta("Número de Registro")
    private Integer numeroRegistro;
    @Tabelavel
    @Etiqueta("Nacionalidade")
    @ManyToOne
    private Nacionalidade nacionalidade;
    @Etiqueta("UF")
    private String estado;
    @Etiqueta("Local de Registro")
    private String localTrabalhoConjuge;
    @Etiqueta("Data do Casamento")
    @Temporal(TemporalType.DATE)
    private Date dataCasamento;
    @Etiqueta("Data de Averbacao")
    @Temporal(TemporalType.DATE)
    private Date dataAverbacao;
    @Etiqueta("Data de Nascimento do Conjuge")
    @Temporal(TemporalType.DATE)
    private Date dataNascimentoConjuge;
    @Etiqueta("Cidade do Cartório")
    @ManyToOne
    private Cidade cidadeCartorio;
    @Etiqueta("Naturalidade do Conjuge")
    @ManyToOne
    private Cidade naturalidadeConjuge;

    private Date dataObito;
    private Boolean fePublica;
    private String cartorio;
    private String matriculaCertidaoObito;
    private String documentoIdentificacao;
    private String livro;
    private String termo;
    private String folha;
    private String observacoes;


    public Cidade getCidadeCartorio() {
        return cidadeCartorio;
    }

    public void setCidadeCartorio(Cidade cidadeCartorio) {
        this.cidadeCartorio = cidadeCartorio;
    }

    public Date getDataCasamento() {
        return dataCasamento;
    }

    public void setDataCasamento(Date dataCasamento) {
        this.dataCasamento = dataCasamento;
    }

    public Date getDataNascimentoConjuge() {
        return dataNascimentoConjuge;
    }

    public void setDataNascimentoConjuge(Date dataNascimentoConjuge) {
        this.dataNascimentoConjuge = dataNascimentoConjuge;
    }

    public String getNomeConjuge() {
        return nomeConjuge;
    }

    public void setNomeConjuge(String nomeConjuge) {
        this.nomeConjuge = nomeConjuge;
    }

    public String getNomeCartorio() {
        return nomeCartorio;
    }

    public void setNomeCartorio(String nomeCartorio) {
        this.nomeCartorio = nomeCartorio;
    }

    public Integer getNumeroFolha() {
        return numeroFolha;
    }

    public void setNumeroFolha(Integer numeroFolha) {
        this.numeroFolha = numeroFolha;
    }

    public String getNumeroLivro() {
        return numeroLivro;
    }

    public void setNumeroLivro(String numeroLivro) {
        this.numeroLivro = numeroLivro;
    }

    public Integer getNumeroRegistro() {
        return numeroRegistro;
    }

    public void setNumeroRegistro(Integer numeroRegistro) {
        this.numeroRegistro = numeroRegistro;
    }

    public Nacionalidade getNacionalidade() {
        return nacionalidade;
    }

    public void setNacionalidade(Nacionalidade nacionalidade) {
        this.nacionalidade = nacionalidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getLocalTrabalhoConjuge() {
        return localTrabalhoConjuge;
    }

    public void setLocalTrabalhoConjuge(String localTrabalhoConjuge) {
        this.localTrabalhoConjuge = localTrabalhoConjuge;
    }

    public Date getDataAverbacao() {
        return dataAverbacao;
    }

    public void setDataAverbacao(Date dataAverbacao) {
        this.dataAverbacao = dataAverbacao;
    }

    public Cidade getNaturalidadeConjuge() {
        return naturalidadeConjuge;
    }

    public Date getDataObito() {
        return dataObito;
    }

    public void setDataObito(Date dataObito) {
        this.dataObito = dataObito;
    }

    public Boolean getFePublica() {
        return fePublica;
    }

    public void setFePublica(Boolean fePublica) {
        this.fePublica = fePublica;
    }

    public String getCartorio() {
        return cartorio;
    }

    public void setCartorio(String cartorio) {
        this.cartorio = cartorio;
    }

    public String getMatriculaCertidaoObito() {
        return matriculaCertidaoObito;
    }

    public void setMatriculaCertidaoObito(String matriculaCertidaoObito) {
        this.matriculaCertidaoObito = matriculaCertidaoObito;
    }

    public String getDocumentoIdentificacao() {
        return documentoIdentificacao;
    }

    public void setDocumentoIdentificacao(String documentoIdentificacao) {
        this.documentoIdentificacao = documentoIdentificacao;
    }

    public String getLivro() {
        return livro;
    }

    public void setLivro(String livro) {
        this.livro = livro;
    }

    public String getTermo() {
        return termo;
    }

    public void setTermo(String termo) {
        this.termo = termo;
    }

    public String getFolha() {
        return folha;
    }

    public void setFolha(String folha) {
        this.folha = folha;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public void setNaturalidadeConjuge(Cidade naturalidadeConjuge) {
        this.naturalidadeConjuge = naturalidadeConjuge;
    }

    @Override
    public String toString() {
        return "Nome do cartório : " + nomeCartorio + " Livro : " + numeroLivro
            + " Folha : " + numeroFolha + " Registro :" + numeroRegistro
            + " Cônjuge : " + nomeConjuge + " Nacionalidade do Cônjuge : " + nacionalidade;
    }

    public static CertidaoCasamentoDTO toCertidaoCasamento(CertidaoCasamento certidaoCasamento) {
        if (certidaoCasamento == null) {
            return null;
        }
        CertidaoCasamentoDTO dto = new CertidaoCasamentoDTO();
        dto.setId(certidaoCasamento.getId());
        dto.setCidadeCartorio(Cidade.toCidadeDTO(certidaoCasamento.getCidadeCartorio()));
        dto.setDataCasamento(certidaoCasamento.getDataCasamento());
        dto.setDataNascimentoConjuge(certidaoCasamento.getDataNascimentoConjuge());
        dto.setEstado(certidaoCasamento.getEstado());
        dto.setLocalTrabalhoConjuge(certidaoCasamento.getLocalTrabalhoConjuge());
        dto.setNacionalidadeConjuge(Nacionalidade.toNacionalidadeDTO(certidaoCasamento.getNacionalidade()));
        dto.setNomeConjuge(certidaoCasamento.getNomeConjuge());
        dto.setNomeCartorio(certidaoCasamento.getNomeCartorio());
        dto.setNumeroLivro(certidaoCasamento.getNumeroLivro());
        dto.setNumeroFolha(certidaoCasamento.getNumeroFolha());
        dto.setNumeroRegistro(certidaoCasamento.getNumeroRegistro());
        dto.setNaturalidadeConjuge(Cidade.toCidadeDTO(certidaoCasamento.getNaturalidadeConjuge()));
        dto.setDataAverbacao(certidaoCasamento.getDataAverbacao());
        return dto;
    }

    public static CertidaoCasamentoDTO toCertidaoCasamentoPortalDTO(CertidaoCasamentoPortal certidaoCasamento) {
        if (certidaoCasamento == null) {
            return null;
        }
        CertidaoCasamentoDTO dto = new CertidaoCasamentoDTO();
        dto.setId(certidaoCasamento.getId());
        dto.setCidadeCartorio(Cidade.toCidadeDTO(certidaoCasamento.getCidadeCartorio()));
        dto.setDataCasamento(certidaoCasamento.getDataCasamento());
        dto.setDataNascimentoConjuge(certidaoCasamento.getDataNascimentoConjuge());
        dto.setEstado(certidaoCasamento.getEstado());
        dto.setLocalTrabalhoConjuge(certidaoCasamento.getLocalTrabalhoConjuge());
        dto.setNacionalidadeConjuge(Nacionalidade.toNacionalidadeDTO(certidaoCasamento.getNacionalidade()));
        dto.setNomeConjuge(certidaoCasamento.getNomeConjuge());
        dto.setNomeCartorio(certidaoCasamento.getNomeCartorio());
        dto.setNumeroLivro(certidaoCasamento.getNumeroLivro());
        dto.setNumeroFolha(certidaoCasamento.getNumeroFolha());
        dto.setNumeroRegistro(certidaoCasamento.getNumeroRegistro());
        dto.setNaturalidadeConjuge(Cidade.toCidadeDTO(certidaoCasamento.getNaturalidadeConjuge()));
        dto.setDataAverbacao(certidaoCasamento.getDataAverbacao());
        return dto;
    }

    public static CertidaoCasamento toCertidaoCasamentoPortal(PessoaFisica pessoaFisica, CertidaoCasamentoPortal certidaoCasamento) {
        if (certidaoCasamento == null) {
            return null;
        }
        CertidaoCasamento certidao = new CertidaoCasamento();
        certidao.setPessoaFisica(pessoaFisica);
        certidao.setCidadeCartorio(certidaoCasamento.getCidadeCartorio());
        certidao.setDataCasamento(certidaoCasamento.getDataCasamento());
        certidao.setDataNascimentoConjuge(certidaoCasamento.getDataNascimentoConjuge());
        certidao.setEstado(certidaoCasamento.getEstado());
        certidao.setLocalTrabalhoConjuge(certidaoCasamento.getLocalTrabalhoConjuge());
        certidao.setNacionalidade(certidaoCasamento.getNacionalidade());
        certidao.setNomeConjuge(certidaoCasamento.getNomeConjuge());
        certidao.setNomeCartorio(certidaoCasamento.getNomeCartorio());
        certidao.setNumeroLivro(certidaoCasamento.getNumeroLivro());
        certidao.setNumeroFolha(certidaoCasamento.getNumeroFolha());
        certidao.setNumeroRegistro(certidaoCasamento.getNumeroRegistro());
        certidao.setNaturalidadeConjuge(certidaoCasamento.getNaturalidadeConjuge());
        certidao.setDataAverbacao(certidaoCasamento.getDataAverbacao());
        return certidao;
    }
}
