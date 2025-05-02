/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.entidades;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Felipe Marinzeck
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Contratos")
@Etiqueta("Ordem de Compra de Contrato")
public class OrdemDeCompraContrato implements Serializable, ValidadorEntidade {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Tabelavel
    @Etiqueta("Código")
    private Long id;
    @ManyToOne
    private Contrato contrato;
    private Integer numero;
    @Temporal(TemporalType.TIMESTAMP)
    private Date criadaEm;
    @Invisivel
    @OneToMany(mappedBy = "ordemDeCompraContrato", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemOrdemDeCompra> itens;
    @Transient
    @Invisivel
    private Long criadoEm;

    public OrdemDeCompraContrato() {
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Date getCriadaEm() {
        return criadaEm;
    }

    public void setCriadaEm(Date emitidaEm) {
        this.criadaEm = emitidaEm;
    }

    public List<ItemOrdemDeCompra> getItens() {
        if (itens == null){
            return new ArrayList<>();
        }
        return itens;
    }

    public void setItens(List<ItemOrdemDeCompra> itens) {
        this.itens = itens;
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
        return id == null ? "Dados ainda não gravados" : "Ordem de compra de contrato código: " + id;
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {

    }

    public boolean possuiItemContrato(ItemContrato ic){
        for (ItemOrdemDeCompra ioc : getItens()) {
            if (ioc.getItemContrato().equals(ic)){
                return true;
            }
        }
        return false;
    }

    public ItemOrdemDeCompra getItemOrdemDeCompraDoItemContrato(ItemContrato ic){
        for (ItemOrdemDeCompra ioc : getItens()) {
            if (ioc.getItemContrato().equals(ic)){
                return ioc;
            }
        }
        return null;
    }
}
