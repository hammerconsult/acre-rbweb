/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author major
 */
@Entity
@Audited
@Etiqueta("Projeção Atuarial")

public class ProjecaoAtuarial implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Data de Avaliação")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataAvaliacao;
    @Etiqueta("Exercício")
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    private Exercicio exercicio;
    @Obrigatorio
    @Etiqueta("Notas")
    @Pesquisavel
    @Tabelavel
    private String hipoteses;
    @OneToMany(mappedBy = "projecaoAtuarial", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemProjecaoAtuarial> itemProjecaoAtuarials;
    @Etiqueta("Saldo Anterior")
    @Obrigatorio
    @Pesquisavel
    private BigDecimal saldoAnterior;
    @Transient
    private Long criadoEm;

    public ProjecaoAtuarial() {
        itemProjecaoAtuarials = new ArrayList<ItemProjecaoAtuarial>();
        criadoEm = System.nanoTime();
        saldoAnterior = BigDecimal.ZERO;
    }

    public ProjecaoAtuarial(Date dataAvaliacao, String hipoteses, Exercicio exercicio) {
        this.dataAvaliacao = dataAvaliacao;
        this.hipoteses = hipoteses;
        this.exercicio = exercicio;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataAvaliacao() {
        return dataAvaliacao;
    }

    public void setDataAvaliacao(Date dataAvaliacao) {
        this.dataAvaliacao = dataAvaliacao;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public String getHipoteses() {
        return hipoteses;
    }

    public void setHipoteses(String hipoteses) {
        this.hipoteses = hipoteses;
    }

    public List<ItemProjecaoAtuarial> getItemProjecaoAtuarials() {
        return itemProjecaoAtuarials;
    }

    public void setItemProjecaoAtuarials(List<ItemProjecaoAtuarial> itemProjecaoAtuarials) {
        this.itemProjecaoAtuarials = itemProjecaoAtuarials;
    }

    public BigDecimal getSaldoAnterior() {
        return saldoAnterior;
    }

    public void setSaldoAnterior(BigDecimal saldoAnterior) {
        this.saldoAnterior = saldoAnterior;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
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
        return "br.com.webpublico.entidades.ProjecaoAtuarial[ id=" + id + " ]";
    }

}
