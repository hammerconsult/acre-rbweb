/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @author renato
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Item Certame Lote Processo")
@Table(name = "ITEMCERTAMELOTEPRO")
public class ItemCertameLoteProcesso extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    private ItemCertame itemCertame;
    @OneToOne
    private LotePropostaFornecedor lotePropFornecedorVencedor;
    @OneToOne
    private LoteProcessoDeCompra loteProcessoDeCompra;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public br.com.webpublico.entidades.ItemCertame getItemCertame() {
        return itemCertame;
    }

    public void setItemCertame(br.com.webpublico.entidades.ItemCertame itemCertame) {
        this.itemCertame = itemCertame;
    }

    public LotePropostaFornecedor getLotePropFornecedorVencedor() {
        return lotePropFornecedorVencedor;
    }

    public void setLotePropFornecedorVencedor(LotePropostaFornecedor lotePropFornecedorVencedor) {
        this.lotePropFornecedorVencedor = lotePropFornecedorVencedor;
    }

    public LoteProcessoDeCompra getLoteProcessoDeCompra() {
        return loteProcessoDeCompra;
    }

    public void setLoteProcessoDeCompra(LoteProcessoDeCompra loteProcessoDeCompra) {
        this.loteProcessoDeCompra = loteProcessoDeCompra;
    }

    public String getDescricaoLote() {
        try {
            return this.getLotePropFornecedorVencedor().getDescricaoLote();
        } catch (NullPointerException npe) {
            return this.getLoteProcessoDeCompra().getDescricao();
        }
    }

    public Pessoa getFornecedor() {
        return this.getLotePropFornecedorVencedor().getFornecedor();
    }

    public BigDecimal getValorTotal() {
        return this.getLotePropFornecedorVencedor().getValor();
    }

    public Integer getNumeroLote() {
        try {
            return this.getLotePropFornecedorVencedor().getNumeroLote();
        } catch (NullPointerException npe) {
            return this.getLoteProcessoDeCompra().getNumero();
        }
    }

    public void setValorLote(BigDecimal precoProposto) {
        this.getLotePropFornecedorVencedor().setValor(precoProposto);
    }
}
