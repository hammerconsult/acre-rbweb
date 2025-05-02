/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoTributoTaxaDividasDiversas;
import br.com.webpublico.enums.TipoValorTributoTaxaDividasDiversas;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.*;

import org.hibernate.envers.Audited;

/**
 * @author gustavo
 */
@Entity

@Audited
@Etiqueta("Tributo de Taxas/Dívidas Diversas")
public class TributoTaxaDividasDiversas implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Tributo")
    private Tributo tributo;
    @Pesquisavel
    @Tabelavel(TIPOCAMPO = Tabelavel.TIPOCAMPO.DECIMAL_QUATRODIGITOS, ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Etiqueta("Valor (UFM)")
    private BigDecimal valor;
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Tipo de Valor")
    private TipoValorTributoTaxaDividasDiversas tipoValorTributo;
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Situação")
    private SituacaoTributoTaxaDividasDiversas situacaoTributo;
    private String migracaoChave;
    private Boolean bloqueiaCertidao;
    private Boolean bloqueiaAlvara;

    @Transient
    private Long criadoEm;

    public TributoTaxaDividasDiversas() {
        criadoEm = System.nanoTime();
    }

    public SituacaoTributoTaxaDividasDiversas getSituacaoTributo() {
        return situacaoTributo;
    }

    public void setSituacaoTributo(SituacaoTributoTaxaDividasDiversas situacaoTributo) {
        this.situacaoTributo = situacaoTributo;
    }

    public TipoValorTributoTaxaDividasDiversas getTipoValorTributo() {
        return tipoValorTributo;
    }

    public void setTipoValorTributo(TipoValorTributoTaxaDividasDiversas tipoValorTributo) {
        this.tipoValorTributo = tipoValorTributo;
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

    public Tributo getTributo() {
        return tributo;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getMigracaoChave() {
        return migracaoChave;
    }

    public void setMigracaoChave(String migracaoChave) {
        this.migracaoChave = migracaoChave;
    }

    public Boolean getBloqueiaCertidao() {
        return bloqueiaCertidao != null? bloqueiaCertidao : false;
    }

    public void setBloqueiaCertidao(Boolean bloqueiaCertidao) {
        this.bloqueiaCertidao = bloqueiaCertidao;
    }

    public Boolean getBloqueiaAlvara() {
        return bloqueiaAlvara != null ? bloqueiaAlvara : false;
    }

    public void setBloqueiaAlvara(Boolean bloqueiaAlvara) {
        this.bloqueiaAlvara = bloqueiaAlvara;
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
        try {
            return tributo.getDescricao() + ": " + valor;
        } catch (NullPointerException ex) {
            return super.toString();
        }
    }
}
