/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author andre
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta("Registro de Óbito")
public class RegistroDeObito extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    @Etiqueta("Servidor")
    @Tabelavel
    @Pesquisavel
    private PessoaFisica pessoaFisica;
    @Pesquisavel
    @Etiqueta("Matrícula da Certidão de Óbito")
    @Tabelavel
    private String matriculaCertidao;
    @Etiqueta("Documento de Identificação")
    @Tabelavel
    @Pesquisavel
    private String numeroObito;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data do Falecimento")
    @Tabelavel
    @Pesquisavel
    private Date dataFalecimento;
    @Etiqueta("Livro")
    @Pesquisavel
    private String livro;
    @Etiqueta("Termo")
    @Pesquisavel
    private String termo;
    @Etiqueta("Folha")
    @Pesquisavel
    private String folha;
    @Pesquisavel
    @Etiqueta("Fé Pública")
    private Boolean fePublica;
    @Etiqueta("Observações / Averbações")
    private String observacao;
    @Etiqueta("Arquivos")
    @OneToMany(mappedBy = "registroDeObito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArquivoRegistroDeObito> arquivoRegistroDeObitos;
    @Etiqueta("Cartório")
    @Tabelavel
    @Pesquisavel
    private String cartorio;

    public RegistroDeObito() {
        arquivoRegistroDeObitos = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getMatriculaCertidao() {
        return matriculaCertidao;
    }

    public void setMatriculaCertidao(String matriculaCertidao) {
        this.matriculaCertidao = matriculaCertidao;
    }

    public Boolean getFePublica() {
        return fePublica;
    }

    public void setFePublica(Boolean fePublica) {
        this.fePublica = fePublica;
    }

    public Date getDataFalecimento() {
        return dataFalecimento;
    }

    public void setDataFalecimento(Date dataFalecimento) {
        this.dataFalecimento = dataFalecimento;
    }

    public String getFolha() {
        return folha;
    }

    public void setFolha(String folha) {
        this.folha = folha;
    }

    public String getLivro() {
        return livro;
    }

    public void setLivro(String livro) {
        this.livro = livro;
    }

    public String getNumeroObito() {
        return numeroObito;
    }

    public void setNumeroObito(String numeroObito) {
        this.numeroObito = numeroObito;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public String getTermo() {
        return termo;
    }

    public void setTermo(String termo) {
        this.termo = termo;
    }

    public List<ArquivoRegistroDeObito> getArquivoRegistroDeObitos() {
        return arquivoRegistroDeObitos;
    }

    public void setArquivoRegistroDeObitos(List<ArquivoRegistroDeObito> arquivoRegistroDeObitos) {
        this.arquivoRegistroDeObitos = arquivoRegistroDeObitos;
    }

    public String getCartorio() {
        return cartorio;
    }

    public void setCartorio(String cartorio) {
        this.cartorio = cartorio;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RegistroDeObito)) {
            return false;
        }
        RegistroDeObito other = (RegistroDeObito) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return pessoaFisica + " - " + numeroObito;
    }
}
