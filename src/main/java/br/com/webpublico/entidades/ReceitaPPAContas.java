package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.*;
import org.hibernate.envers.Audited;

/**
 * @author Fabio
 */
@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity

@Etiqueta("Contas da Previsão Receita PPA")
public class ReceitaPPAContas implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ReceitaPPA receitaPPA;
    @Obrigatorio
    @ManyToOne
    private Exercicio exercicio;
    @ManyToOne
    private Conta contaReceita;
    private BigDecimal valor;
    @Transient
    private Long criadoEm;

    public ReceitaPPAContas() {
        criadoEm = System.nanoTime();
        valor = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReceitaPPA getReceitaPPA() {
        return receitaPPA;
    }

    public void setReceitaPPA(ReceitaPPA receitaPPA) {
        this.receitaPPA = receitaPPA;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Conta getContaReceita() {
        return contaReceita;
    }

    public void setContaReceita(Conta contaReceita) {
        this.contaReceita = contaReceita;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
