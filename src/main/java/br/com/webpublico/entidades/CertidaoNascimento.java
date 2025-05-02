/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.rh.portal.atualizacaocadastral.CertidaoNascimentoPortal;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.pessoa.dto.CertidaoNascimentoDTO;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.io.Serializable;

/**
 * @author andre
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
public class CertidaoNascimento extends DocumentoPessoal implements Serializable {

    @Tabelavel
    @Etiqueta("Nome do cartório")
    private String nomeCartorio;
    @Tabelavel
    @Etiqueta("Número do livro")
    private String numeroLivro;
    @Tabelavel
    @Etiqueta("Número da folha")
    private String numeroFolha;
    @Tabelavel
    @Etiqueta("Número de Registro")
    private Integer numeroRegistro;
    @ManyToOne
    private Cidade cidade;
    private String nomeCompanheiro;
    private String cpfCompanheiro;

    public String getNomeCartorio() {
        return nomeCartorio;
    }

    public void setNomeCartorio(String nomeCartorio) {
        this.nomeCartorio = nomeCartorio;
    }

    public String getNumeroFolha() {
        return numeroFolha;
    }

    public void setNumeroFolha(String numeroFolha) {
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

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }

    public String getNomeCompanheiro() {
        return nomeCompanheiro;
    }

    public void setNomeCompanheiro(String nomeCompanheiro) {
        this.nomeCompanheiro = nomeCompanheiro;
    }

    public String getCpfCompanheiro() {
        return cpfCompanheiro;
    }

    public void setCpfCompanheiro(String cpfCompanheiro) {
        this.cpfCompanheiro = cpfCompanheiro;
    }

    @Override
    public String toString() {
        return "Nome do cartório : " + nomeCartorio + " Livro : " + numeroLivro + " Folha : " + numeroFolha + " Registro :" + numeroRegistro;
    }

    public static CertidaoNascimentoDTO toCertidaoNascimentoDTO(CertidaoNascimento certidaoNascimento) {
        if (certidaoNascimento == null) {
            return null;
        }
        CertidaoNascimentoDTO dto = new CertidaoNascimentoDTO();
        dto.setId(certidaoNascimento.getId());
        dto.setCidadeNascimento(Cidade.toCidadeDTO(certidaoNascimento.getCidade()));
        dto.setNomeCartorioNascimento(certidaoNascimento.getNomeCartorio());
        dto.setNumeroLivroNascimento(certidaoNascimento.getNumeroLivro());
        dto.setNumeroFolhaNascimento(certidaoNascimento.getNumeroFolha());
        dto.setNumeroRegistroNascimento(certidaoNascimento.getNumeroRegistro());
        return dto;
    }

    public static CertidaoNascimentoDTO toCertidaoNascimentoPortalDTO(CertidaoNascimentoPortal certidaoNascimento) {
        if (certidaoNascimento == null) {
            return null;
        }
        CertidaoNascimentoDTO dto = new CertidaoNascimentoDTO();
        dto.setId(certidaoNascimento.getId());
        dto.setCidadeNascimento(Cidade.toCidadeDTO(certidaoNascimento.getCidade()));
        dto.setNomeCartorioNascimento(certidaoNascimento.getNomeCartorio());
        dto.setNumeroLivroNascimento(certidaoNascimento.getNumeroLivro());
        dto.setNumeroFolhaNascimento(certidaoNascimento.getNumeroFolha());
        dto.setNumeroRegistroNascimento(certidaoNascimento.getNumeroRegistro());
        return dto;
    }

    public static CertidaoNascimento toCertidaoNascimento(PessoaFisica pessoaFisica, CertidaoNascimentoPortal certidaoNascimento) {
        if (certidaoNascimento == null) {
            return null;
        }
        CertidaoNascimento certidao = new CertidaoNascimento();
        certidao.setPessoaFisica(pessoaFisica);
        certidao.setCidade(certidaoNascimento.getCidade());
        certidao.setNomeCartorio(certidaoNascimento.getNomeCartorio());
        certidao.setNumeroLivro(certidaoNascimento.getNumeroLivro());
        certidao.setNumeroFolha(certidaoNascimento.getNumeroFolha());
        certidao.setNumeroRegistro(certidaoNascimento.getNumeroRegistro());
        return certidao;
    }
}
