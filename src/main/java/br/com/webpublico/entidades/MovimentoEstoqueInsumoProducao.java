/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@GrupoDiagrama(nome = "Materiais")
@Entity

@Audited
@Table(name = "MOVESTOQUEINSUMOPRODUCAO")
public class MovimentoEstoqueInsumoProducao implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Tabelavel
    @Etiqueta("Código")
    private Long id;
    @ManyToOne
    private MovimentoEstoque movimentoEstoque;
    @ManyToOne
    private InsumoProducao insumoProducao;

    public MovimentoEstoqueInsumoProducao() {
    }

    public MovimentoEstoqueInsumoProducao(MovimentoEstoque movimentoEstoque, InsumoProducao insumoProducao) {
        this.movimentoEstoque = movimentoEstoque;
        this.insumoProducao = insumoProducao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InsumoProducao getInsumoProducao() {
        return insumoProducao;
    }

    public void setInsumoProducao(InsumoProducao insumoProducao) {
        this.insumoProducao = insumoProducao;
    }

    public MovimentoEstoque getMovimentoEstoque() {
        return movimentoEstoque;
    }

    public void setMovimentoEstoque(MovimentoEstoque movimentoEstoque) {
        this.movimentoEstoque = movimentoEstoque;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MovimentoEstoqueInsumoProducao other = (MovimentoEstoqueInsumoProducao) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
}
