package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Fabio
 */
@Entity

@GrupoDiagrama(nome = "Contabil")
@Audited
@Etiqueta("Receitas da Despesa Extra")
public class PagamentoReceitaExtra implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private PagamentoExtra pagamentoExtra;
    @ManyToOne
    private ReceitaExtra receitaExtra;
    @ManyToOne
    private PagamentoEstornoRecExtra pagamentoEstornoRecExtra;
    @Transient
    private Long criadoEm;

    public PagamentoReceitaExtra() {
        criadoEm = System.nanoTime();
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

    public PagamentoExtra getPagamentoExtra() {
        return pagamentoExtra;
    }

    public void setPagamentoExtra(PagamentoExtra pagamentoExtra) {
        this.pagamentoExtra = pagamentoExtra;
    }

    public ReceitaExtra getReceitaExtra() {
        return receitaExtra;
    }

    public void setReceitaExtra(ReceitaExtra receitaExtra) {
        this.receitaExtra = receitaExtra;
    }

    public PagamentoEstornoRecExtra getPagamentoEstornoRecExtra() {
        return pagamentoEstornoRecExtra;
    }

    public void setPagamentoEstornoRecExtra(PagamentoEstornoRecExtra pagamentoEstornoRecExtra) {
        this.pagamentoEstornoRecExtra = pagamentoEstornoRecExtra;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

}
