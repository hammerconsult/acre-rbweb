/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.CorEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@GrupoDiagrama(nome = "RecursosHumanos")
@Entity

@Audited
@CorEntidade(value = "#FF0000")
public class MatriculaFP implements Serializable, Comparable<MatriculaFP> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Matrícula")
    private String matricula;
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Pessoa Física")
    private PessoaFisica pessoa;
    @Obrigatorio
    @Pesquisavel
    @ManyToOne
    @Tabelavel
    @Etiqueta("Entidade Contratante")
    private UnidadeOrganizacional unidadeMatriculado;
    @Transient
    private String nome;

    public MatriculaFP() {
    }

    public MatriculaFP(String matricula, PessoaFisica pessoa, UnidadeOrganizacional unidadeMatriculado) {
        this.matricula = matricula;
        this.pessoa = pessoa;
        this.unidadeMatriculado = unidadeMatriculado;
    }

    public MatriculaFP(String matricula, PessoaFisica pessoa) {
        this.matricula = matricula;
        this.pessoa = pessoa;
    }

    public MatriculaFP(Long id, String matricula, String nomePessoa) {
        this.id = id;
        this.matricula = matricula;
        this.nome = nomePessoa;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public PessoaFisica getPessoa() {
        return pessoa;
    }

    public void setPessoa(PessoaFisica pessoa) {
        this.pessoa = pessoa;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public UnidadeOrganizacional getUnidadeMatriculado() {
        return unidadeMatriculado;
    }

    public void setUnidadeMatriculado(UnidadeOrganizacional unidadeMatriculado) {
        this.unidadeMatriculado = unidadeMatriculado;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        MatriculaFP other = (MatriculaFP) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    private String getToStringAlternativo() {
        return this.matricula + " - " + this.nome;
    }

    public String getToStringMatriculaNomeCPF() {
        if (pessoa.getNomeTratamento() != null) {
            return this.matricula + " - " + pessoa.getNome() + "(" + pessoa.getNomeTratamento().trim() + ")" + " - " + pessoa.getCpf();
        }
        return this.matricula + " - " + pessoa.getNome() + " - " + pessoa.getCpf();
    }

    @Override
    public String toString() {
        if (this.nome != null) {
            return getToStringAlternativo();
        }

        String numeroMat = " ";
        String nomePessoa = " ";
        if (matricula != null) {
            numeroMat = matricula;
        }
        if (pessoa != null && pessoa.getNome() != null) {
            if (pessoa.getNome() != null) {
                nomePessoa = pessoa.getNome();
                if (pessoa.getNomeTratamento() != null) {
                    nomePessoa += "(" + pessoa.getNomeTratamento() + ")";
                }
            }
        }
        return numeroMat + " - " + nomePessoa;
    }

    public String getMatriculaComCPF() {
        return toString() +  " - CPF: " + this.pessoa.getCpf();
    }

    @Override
    public int compareTo(MatriculaFP matriculaFP) {
        return Integer.valueOf(this.getMatricula()).compareTo(Integer.valueOf(matriculaFP.getMatricula()));

    }

    public Date getDataNascimento() throws NullPointerException {
        return this.getPessoa().getDataNascimento();
    }
}
