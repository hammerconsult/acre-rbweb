package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoCalculoRBTRans;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Audited
@GrupoDiagrama(nome = "RBTrans")
@Etiqueta("Taxa de Trânsito")

public class TaxaTransito implements Serializable, Comparable<TaxaTransito> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Valor")
    private BigDecimal valor;
    @OneToOne
    @Etiqueta("Tributo")
    private Tributo tributo;
    @OneToOne
    @Etiqueta("Dívida")
    private Divida divida;
    @Etiqueta("Tipo de Taxa")
    @Enumerated(EnumType.STRING)
    private TipoCalculoRBTRans tipoCalculoRBTRans;
    @ManyToOne
    @JoinColumn(name = "PARAMETROSTRANSITOCONFIG_ID")
    private ParametrosTransitoConfiguracao parametrosTransitoConfiguracao;
    @Transient
    @Invisivel
    private Long criadoEm;

    public TaxaTransito() {
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
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

    public TipoCalculoRBTRans getTipoCalculoRBTRans() {
        return tipoCalculoRBTRans;
    }

    public void setTipoCalculoRBTRans(TipoCalculoRBTRans tipoCalculoRBTRans) {
        this.tipoCalculoRBTRans = tipoCalculoRBTRans;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public ParametrosTransitoConfiguracao getParametrosTransitoConfiguracao() {
        return parametrosTransitoConfiguracao;
    }

    public void setParametrosTransitoConfiguracao(ParametrosTransitoConfiguracao parametrosTransitoConfiguracao) {
        this.parametrosTransitoConfiguracao = parametrosTransitoConfiguracao;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        if (this.getTipoCalculoRBTRans() != null) {
            return this.getTipoCalculoRBTRans().equals(((TaxaTransito)object).getTipoCalculoRBTRans());
        }
        return false;
    }

    @Override
    public String toString() {
        return this.tributo + " " + this.valor;
    }

    @Override
    public int compareTo(TaxaTransito o) {
        try {
            if (o.getTipoCalculoRBTRans() != null && this.getTipoCalculoRBTRans() != null) {
                return this.getTipoCalculoRBTRans().getDescricao().compareTo(o.getTipoCalculoRBTRans().getDescricao());
            }
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }
}
