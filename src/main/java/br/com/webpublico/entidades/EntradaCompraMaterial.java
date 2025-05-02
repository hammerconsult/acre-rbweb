/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilBeanContabil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Sarruf
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Materiais")
@Etiqueta("Entrada de Materiais por Compra")
public class EntradaCompraMaterial extends EntradaMaterial {

    @Obrigatorio
    @Etiqueta("Requisição de Compra")
    @OneToOne
    private RequisicaoDeCompra requisicaoDeCompra;

    @OneToMany(mappedBy = "entradaCompraMaterial", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DoctoFiscalEntradaCompra> documentosFiscais;

    public EntradaCompraMaterial() {
        documentosFiscais = Lists.newArrayList();
    }

    public RequisicaoDeCompra getRequisicaoDeCompra() {
        return requisicaoDeCompra;
    }

    public void setRequisicaoDeCompra(RequisicaoDeCompra requisicaoDeCompra) {
        this.requisicaoDeCompra = requisicaoDeCompra;
    }

    public List<DoctoFiscalEntradaCompra> getDocumentosFiscais() {
        return documentosFiscais;
    }

    public void setDocumentosFiscais(List<DoctoFiscalEntradaCompra> documentosFiscais) {
        this.documentosFiscais = documentosFiscais;
    }

    public String getHistoricoRazao() {
        try {
            return "Entrada por Compra n° " + getNumero() + UtilBeanContabil.SEPARADOR_HISTORICO + getHistorico();
        } catch (Exception ex) {
            return "Entrada por Compra";
        }
    }

    public BigDecimal getQuantidadeTotalJaLancadaDoItem(ItemRequisicaoDeCompra itemReq) {
        BigDecimal total = BigDecimal.ZERO;
        for (DoctoFiscalEntradaCompra doc : documentosFiscais) {
            for (ItemDoctoItemEntrada item : doc.getItens()) {
                if (itemReq.equals(item.getItemEntradaMaterial().getItemCompraMaterial().getItemRequisicaoDeCompra())) {
                    total = total.add(item.getQuantidade());
                }
            }
        }
        return total;
    }

    public BigDecimal getValorTotalDocumento() {
        BigDecimal total = BigDecimal.ZERO;
        try {
            for (DoctoFiscalEntradaCompra docto : getDocumentosFiscais()) {
                total = total.add(docto.getValorTotalDocumento());
            }
            return total;
        } catch (Exception ex) {
            return total;
        }
    }

    public boolean hasDocumentosLiquidado() {
        return !Util.isListNullOrEmpty(documentosFiscais) &&
            (documentosFiscais.stream().anyMatch(doc -> doc.getSituacao().isLiquidado())
                || documentosFiscais.stream().anyMatch(doc -> doc.getSituacao().isLiquidadoParcialmente()));
    }

    @Override
    public String toString() {
        return this.getNumero() + " - " + DataUtil.getDataFormatada(this.getDataEntrada()) + " - " + this.getHistorico();
    }
}
