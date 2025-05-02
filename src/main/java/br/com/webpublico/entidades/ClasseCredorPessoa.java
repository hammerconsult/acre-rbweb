/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.OperacaoClasseCredor;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author major
 */
@GrupoDiagrama(nome = "CadastroUnico")
@Entity

@Audited
public class ClasseCredorPessoa implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Transient
    private Long criadoEm;
    @ManyToOne
    private ClasseCredor classeCredor;
    @ManyToOne
    private Pessoa pessoa;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Operação")
    private OperacaoClasseCredor operacaoClasseCredor;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Início de Vigência")
    private Date dataInicioVigencia;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Fim de  Vigência")
    private Date dataFimVigencia;

    public ClasseCredorPessoa() {
        criadoEm = System.nanoTime();
        this.dataInicioVigencia = new Date();
    }

    public ClasseCredor getClasseCredor() {
        return classeCredor;
    }

    public void setClasseCredor(ClasseCredor classeCredor) {
        this.classeCredor = classeCredor;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public OperacaoClasseCredor getOperacaoClasseCredor() {
        return operacaoClasseCredor;
    }

    public void setOperacaoClasseCredor(OperacaoClasseCredor operacaoClasseCredor) {
        this.operacaoClasseCredor = operacaoClasseCredor;
    }

    public Date getDataInicioVigencia() {
        return dataInicioVigencia;
    }

    public void setDataInicioVigencia(Date dataInicioVigencia) {
        this.dataInicioVigencia = dataInicioVigencia;
    }

    public Date getDataFimVigencia() {
        return dataFimVigencia;
    }

    public void setDataFimVigencia(Date dataFimVigencia) {
        this.dataFimVigencia = dataFimVigencia;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "Classe Credor: " + classeCredor.getDescricao();
    }
}
