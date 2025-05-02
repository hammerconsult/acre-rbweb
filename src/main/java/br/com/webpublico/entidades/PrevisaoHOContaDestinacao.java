/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Audited

@Etiqueta("Previsão Hierarquia Organizacional Destinação")
public class PrevisaoHOContaDestinacao implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Conta")
    @ManyToOne
    private Conta conta;
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    @Obrigatorio
    @Etiqueta("Valor")
    private BigDecimal valor;
    @Transient
    private Long criadoEm;
    @ManyToOne
    @Etiqueta("Unidade Organizacional")
    private UnidadeOrganizacional unidadeOrganizacional;

    public PrevisaoHOContaDestinacao() {
        this.valor = BigDecimal.ZERO;
        criadoEm = System.nanoTime();
    }

    public PrevisaoHOContaDestinacao(UnidadeOrganizacional unidadeOrganizacional, Conta conta, Exercicio exercicio, BigDecimal valor, Long criadoEm) {
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.conta = conta;
        this.exercicio = exercicio;
        this.valor = valor;
        this.criadoEm = criadoEm;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
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
        return "br.com.webpublico.entidades.PrevisaoHOContaDestinacao[ id=" + id + " ]";
    }
}
