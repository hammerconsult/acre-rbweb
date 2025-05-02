/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity

/**
 *
 * Um Lançamentos Padronizados determina quais contas contábeis serão utilizados para
 * registrar determinado fenônemo contábil.
 *
 * Não poderá ser alterado a partir do momento em que um Fato Contábil vinculado ao CLP
 * que está vinculado a determinado LançamentoPadronizado tenha sido realizado.
 *
 */
@Etiqueta("Lançamento Padronizado")
public class LancamentoPadronizado implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Obrigatorio
    private String codigo;
    @Tabelavel
    @Obrigatorio
    private String descricao;
    /**
     * Conta CONTÁBIL do MESMO plano de contas da contaDebito.
     */
    @ManyToOne
    private Conta contaCredito;
    /**
     * Conta CONTÁBIL do MESMO plano de contas da contaCredito
     */
    @ManyToOne
    private Conta contaDebito;
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;
    @Temporal(TemporalType.DATE)
    private Date finalVigencia;

    public LancamentoPadronizado() {
    }

    public LancamentoPadronizado(String codigo, String descricao, Conta contaCredito, Conta contaDebito, Date inicioVigencia, Date finalVigencia) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.contaCredito = contaCredito;
        this.contaDebito = contaDebito;
        this.inicioVigencia = inicioVigencia;
        this.finalVigencia = finalVigencia;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Conta getContaCredito() {
        return contaCredito;
    }

    public void setContaCredito(Conta contaCredito) {
        this.contaCredito = contaCredito;
    }

    public Conta getContaDebito() {
        return contaDebito;
    }

    public void setContaDebito(Conta contaDebito) {
        this.contaDebito = contaDebito;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LancamentoPadronizado other = (LancamentoPadronizado) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return descricao;
    }

}
