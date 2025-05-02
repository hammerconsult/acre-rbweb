/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoPagamento;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author heinz
 */
@Audited
@Entity

@GrupoDiagrama(nome = "Alvará")
public class BCECaracFuncionamento implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CadastroEconomico cadastroEconomico;
    @ManyToOne
    private CaracFuncionamento caracFuncionamento;
    private Long quantidade;
    @Enumerated(EnumType.STRING)
    private TipoPagamento formaPagamento;
    @Transient
    private Long criadoEm;

    public TipoPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(TipoPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public BCECaracFuncionamento() {
        this.criadoEm = System.nanoTime();
    }

    public BCECaracFuncionamento(CadastroEconomico cadastroEconomico, CaracFuncionamento caracFuncionamento) {
        this.cadastroEconomico = cadastroEconomico;
        this.caracFuncionamento = caracFuncionamento;
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public CaracFuncionamento getCaracFuncionamento() {
        return caracFuncionamento;
    }

    public void setCaracFuncionamento(CaracFuncionamento caracFuncionamento) {
        this.caracFuncionamento = caracFuncionamento;
    }

    public Long getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Long quantidade) {
        this.quantidade = quantidade;
    }


    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.BCECaracFuncionamento[ id=" + id + " ]";
    }
}
