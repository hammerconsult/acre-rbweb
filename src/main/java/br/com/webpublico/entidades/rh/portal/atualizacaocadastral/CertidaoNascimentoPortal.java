package br.com.webpublico.entidades.rh.portal.atualizacaocadastral;

import br.com.webpublico.entidades.Cidade;
import br.com.webpublico.pessoa.dto.CertidaoNascimentoDTO;

import javax.persistence.*;
import java.util.Objects;

/**
 * Created by peixe on 29/08/17.
 */
@Entity
public class CertidaoNascimentoPortal {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nomeCartorio;
    private String numeroLivro;
    private String numeroFolha;
    private Integer numeroRegistro;
    @ManyToOne
    private Cidade cidade;

    public CertidaoNascimentoPortal() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeCartorio() {
        return nomeCartorio;
    }

    public void setNomeCartorio(String nomeCartorio) {
        this.nomeCartorio = nomeCartorio;
    }

    public String getNumeroLivro() {
        return numeroLivro;
    }

    public void setNumeroLivro(String numeroLivro) {
        this.numeroLivro = numeroLivro;
    }

    public String getNumeroFolha() {
        return numeroFolha;
    }

    public void setNumeroFolha(String numeroFolha) {
        this.numeroFolha = numeroFolha;
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

    public static CertidaoNascimentoPortal dtoToCertidaoNascimentoPortal(CertidaoNascimentoDTO dto) {
        CertidaoNascimentoPortal c = new CertidaoNascimentoPortal();
        c.setCidade(Cidade.dtoToCidade(dto.getCidadeNascimento()));
        c.setNomeCartorio(dto.getNomeCartorioNascimento());
        c.setNumeroFolha(dto.getNumeroFolhaNascimento());
        c.setNumeroLivro(dto.getNumeroLivroNascimento());
        c.setNumeroRegistro(dto.getNumeroRegistroNascimento());
        return c;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CertidaoNascimentoPortal that = (CertidaoNascimentoPortal) o;
        return Objects.equals(nomeCartorio != null ? nomeCartorio.toUpperCase().trim() : null, that.nomeCartorio != null ? that.nomeCartorio.toUpperCase().trim() : null)
            && Objects.equals(numeroLivro != null ? numeroLivro.trim() : null, that.numeroLivro != null ? that.numeroLivro.trim() : null)
            && Objects.equals(numeroFolha != null ? numeroFolha.trim() : null, that.numeroFolha != null ? that.numeroFolha.trim() : null)
            && Objects.equals(numeroRegistro, that.numeroRegistro)
            && Objects.equals(cidade, that.cidade);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nomeCartorio, numeroLivro, numeroFolha, numeroRegistro, cidade);
    }
}
