/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.*;
import org.hibernate.envers.Audited;

/**
 *
 * @author Usuario
 */
@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity

@Etiqueta("Desdobramento Diária")
public class DesdobramentoDiaria implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private DiariaContabilizacao diariaContabilizacao;
    @ManyToOne
    private ContaDespesa desdobramento;
    private BigDecimal valor;
    @Transient
    private Long criadoEm;

    public DesdobramentoDiaria() {
        criadoEm = System.nanoTime();
        valor = BigDecimal.ZERO;
    }

    public DesdobramentoDiaria(DiariaContabilizacao diariaContabilizacao, ContaDespesa desdobramento, BigDecimal valor, Long criadoEm) {
        this.diariaContabilizacao = diariaContabilizacao;
        this.desdobramento = desdobramento;
        this.valor = valor;
        this.criadoEm = criadoEm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DiariaContabilizacao getDiariaContabilizacao() {
        return diariaContabilizacao;
    }

    public void setDiariaContabilizacao(DiariaContabilizacao diariaContabilizacao) {
        this.diariaContabilizacao = diariaContabilizacao;
    }

    public ContaDespesa getDesdobramento() {
        return desdobramento;
    }

    public void setDesdobramento(ContaDespesa desdobramento) {
        this.desdobramento = desdobramento;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
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
        return "br.com.webpublico.entidades.DesdobramentoDiaria[ id=" + id + " ]";
    }
}
