/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.RegistroDAF607;
import br.com.webpublico.entidadesauxiliares.RegistroRCB001;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author heinz
 */
@GrupoDiagrama(nome = "Arrecadacao")
@Audited
@Entity
public class ItemLoteBaixa implements Serializable, Comparable<ItemLoteBaixa> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private BigDecimal valorPago;
    @ManyToOne
    private LoteBaixa loteBaixa;
    @ManyToOne
    private DAM dam;
    @Transient
    private Long criadoEm;
    @Transient
    private RegistroRCB001 registroRCB001;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "itemLoteBaixa")
    private List<ItemLoteBaixaParcela> itemParcelas;
    private String damInformado;
    private String codigoBarrasInformado;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataPagamento;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataCredito;
    @OneToOne(mappedBy = "itemLoteBaixa", cascade = CascadeType.ALL, orphanRemoval = true)
    private PagamentoCartao pagamentoCartao;
    private String autenticacao;

    public ItemLoteBaixa() {
        this.criadoEm = System.nanoTime();
        this.valorPago = BigDecimal.ZERO;
        this.itemParcelas = new ArrayList<>();
    }

    public String getDamInformado() {
        return damInformado;
    }

    public void setDamInformado(String damInformado) {
        this.damInformado = damInformado;
    }

    public Long getDamInformadoSoNumero() {
        try {
            return Long.parseLong(damInformado.substring(0, damInformado.indexOf("/")));
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getDamInformadoSoAno() {
        try {
            return Integer.parseInt(damInformado.substring(damInformado.indexOf("/") + 1, damInformado.length()));
        } catch (Exception e) {
            return null;
        }
    }

    public String getCodigoBarrasInformado() {
        return dam != null ? dam.getCodigoBarras() : codigoBarrasInformado != null ? codigoBarrasInformado : "";
    }

    public void setCodigoBarrasInformado(String codigoBarrasInformado) {
        this.codigoBarrasInformado = codigoBarrasInformado;
    }

    public RegistroRCB001 getRegistroRCB001() {
        return registroRCB001;
    }

    public void setRegistroRCB001(RegistroRCB001 registroRCB001) {
        this.registroRCB001 = registroRCB001;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LoteBaixa getLoteBaixa() {
        return loteBaixa;
    }

    public void setLoteBaixa(LoteBaixa loteBaixa) {
        this.loteBaixa = loteBaixa;
    }

    public DAM getDam() {
        return dam;
    }

    public void setDam(DAM dam) {
        this.dam = dam;
    }

    public PagamentoCartao getPagamentoCartao() {
        return pagamentoCartao;
    }

    public void setPagamentoCartao(PagamentoCartao pagamentoCartao) {
        this.pagamentoCartao = pagamentoCartao;
    }

    public BigDecimal getValorDiferenca() {
        BigDecimal valor = BigDecimal.ZERO;
        if (dam != null) {
            valor = dam.getValorOriginal();
            if (dam.getDesconto() != null) {
                valor = valor.subtract(dam.getDesconto());
            }
            if (dam.getJuros() != null) {
                valor = valor.add(dam.getJuros());
            }
            if (dam.getMulta() != null) {
                valor = valor.add(dam.getMulta());
            }
            if (dam.getCorrecaoMonetaria() != null) {
                valor = valor.add(dam.getCorrecaoMonetaria());
            }
            if (dam.getHonorarios() != null) {
                valor = valor.add(dam.getHonorarios());
            }
        }
        if (valorPago != null) {
            valor = valorPago.subtract(valor);
        }
        return valor;

    }

    public BigDecimal getValorPago() {
        return valorPago != null ? valorPago : BigDecimal.ZERO;
    }

    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public List<ItemLoteBaixaParcela> getItemParcelas() {
        return itemParcelas;
    }

    public void setItemParcelas(List<ItemLoteBaixaParcela> itemParcelas) {
        this.itemParcelas = itemParcelas;
    }

    public BigDecimal getTotal() {
        BigDecimal valor = BigDecimal.ZERO;
        if (dam != null) {
            valor = dam.getValorOriginal();
            if (dam.getDesconto() != null) {
                valor = valor.subtract(dam.getDesconto());
            }
            if (dam.getJuros() != null) {
                valor = valor.add(dam.getJuros());
            }
            if (dam.getMulta() != null) {
                valor = valor.add(dam.getMulta());
            }
            if (dam.getCorrecaoMonetaria() != null) {
                valor = valor.add(dam.getCorrecaoMonetaria());
            }
            if (dam.getHonorarios() != null) {
                valor = valor.add(dam.getHonorarios());
            }
        }
        return valor;
    }

    public Date getDataPagamento() {
        if (dataPagamento == null && loteBaixa != null && loteBaixa.getDataPagamento() != null) {
            dataPagamento = loteBaixa.getDataPagamento();
        }
        return dataPagamento;
    }

    public void setDataPagamento(Date dataPagamento) {

        this.dataPagamento = dataPagamento;
    }

    public Date getDataCredito() {
        return dataCredito;
    }

    public void setDataCredito(Date dataCredito) {
        this.dataCredito = dataCredito;
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
        return "br.com.webpublico.entidades.ItemLoteBaixa[ id=" + id + " ]";
    }

    @Override
    public int compareTo(ItemLoteBaixa o) {
        return this.getId().compareTo(o.getId());
    }
}
