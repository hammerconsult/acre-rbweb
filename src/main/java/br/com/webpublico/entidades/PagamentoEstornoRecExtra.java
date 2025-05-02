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
@Etiqueta("Receitas do Estorno de Despesa Extra")
public class PagamentoEstornoRecExtra implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private PagamentoExtraEstorno pagamentoExtraEstorno;
    @ManyToOne
    private ReceitaExtra receitaExtra;
    @Transient
    private Long criadoEm;

    public PagamentoEstornoRecExtra() {
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

    public PagamentoExtraEstorno getPagamentoExtraEstorno() {
        return pagamentoExtraEstorno;
    }

    public void setPagamentoExtraEstorno(PagamentoExtraEstorno pagamentoExtraEstorno) {
        this.pagamentoExtraEstorno = pagamentoExtraEstorno;
    }

    public ReceitaExtra getReceitaExtra() {
        return receitaExtra;
    }

    public void setReceitaExtra(ReceitaExtra receitaExtra) {
        this.receitaExtra = receitaExtra;
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
