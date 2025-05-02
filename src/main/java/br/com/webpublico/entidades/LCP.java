/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.IndicadorSuperavitFinanceiro;
import br.com.webpublico.enums.TipoMovimentoTCE;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.Persistencia;
import br.com.webpublico.util.anotacoes.Equals;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * @author jaimaum
 */
@Entity

@Audited
public class LCP implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CLP clp;
    @Tabelavel
    @Etiqueta(value = "Código")
    private String codigo;
    @ManyToOne
    @Equals
    @Etiqueta(value = "Conta de Crédito")
    private Conta contaCredito;
    @ManyToOne
    @Equals
    @Etiqueta(value = "Conta de Crédito Intra")
    private Conta contaCreditoIntra;
    @ManyToOne
    @Equals
    @Etiqueta(value = "Conta de Crédito Inter União")
    private Conta contaCreditoInterUniao;
    @ManyToOne
    @Equals
    @Etiqueta(value = "Conta de Crédito Inter Estado")
    private Conta contaCreditoInterEstado;
    @ManyToOne
    @Equals
    @Etiqueta(value = "Conta de Crédito Inter Municipio")
    private Conta contaCreditoInterMunicipal;
    @ManyToOne
    @Equals
    @Etiqueta(value = "Conta de Débito")
    private Conta contaDebito;
    @ManyToOne
    @Equals
    @Etiqueta(value = "Conta de Débito Intra")
    private Conta contaDebitoIntra;
    @ManyToOne
    @Equals
    @Etiqueta(value = "Conta de Débito Inter União")
    private Conta contaDebitoInterUniao;
    @ManyToOne
    @Equals
    @Etiqueta(value = "Conta de Débito Inter Estado")
    private Conta contaDebitoInterEstado;
    @ManyToOne
    @Equals
    @Etiqueta(value = "Conta de Débito Inter Municipio")
    private Conta contaDebitoInterMunicipal;
    private Boolean obrigatorio;
    private Boolean variacao;
    @ManyToOne
    @Equals
    private TagOCC tagOCCCredito;
    @ManyToOne
    @Equals
    private TagOCC tagOCCDebito;
    @Transient
    private Long criadoEm;
    @ManyToOne
    @Equals
    private TipoContaAuxiliar tipoContaAuxiliarCredito;
    @ManyToOne
    @Equals
    private TipoContaAuxiliar tipoContaAuxCredSiconfi;
    @ManyToOne
    @Equals
    private TipoContaAuxiliar tipoContaAuxiliarDebito;
    @ManyToOne
    @Equals
    private TipoContaAuxiliar tipoContaAuxDebSiconfi;
    private Boolean variaOperacao;
    private Boolean variaOperacaoCredito;

    @Etiqueta(value = "Tipo Conta Auxiliar Débito")
    private Integer item;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo Movimento TCE(Crédito)")
    @Tabelavel
    private TipoMovimentoTCE tipoMovimentoTCECredito;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo Movimento TCE(Débito)")
    @Tabelavel
    private TipoMovimentoTCE tipoMovimentoTCEDebito;
    @Etiqueta("Uso Interno")
    private Boolean usoInterno;

    @Enumerated(EnumType.STRING)
    private IndicadorSuperavitFinanceiro indicadorSuperavitFinanCred;
    @Enumerated(EnumType.STRING)
    private IndicadorSuperavitFinanceiro indicadorSuperavitFinanDeb;

    public LCP() {
        criadoEm = System.nanoTime();
        obrigatorio = Boolean.FALSE;
        variacao = Boolean.FALSE;
        variaOperacao = Boolean.FALSE;
        variaOperacaoCredito = Boolean.FALSE;
    }

    public LCP(CLP clp, String codigo, Conta contaCredito, Conta contaCreditoIntra, Conta contaCreditoInterUniao, Conta contaCreditoInterEstado, Conta contaCreditoInterMunicipal, Conta contaDebito, Conta contaDebitoIntra, Conta contaDebitoInterUniao, Conta contaDebitoInterEstado, Conta contaDebitoInterMunicipal, Boolean obrigatorio, Boolean variacao, TagOCC tagOCCCredito, TagOCC tagOCCDebito, Long criadoEm, TipoContaAuxiliar tipoContaAuxiliarCredito, TipoContaAuxiliar tipoContaAuxiliarDebito, Boolean variaOperacao, Boolean variaOperacaoCredito) {
        this.clp = clp;
        this.codigo = codigo;
        this.contaCredito = contaCredito;
        this.contaCreditoIntra = contaCreditoIntra;
        this.contaCreditoInterUniao = contaCreditoInterUniao;
        this.contaCreditoInterEstado = contaCreditoInterEstado;
        this.contaCreditoInterMunicipal = contaCreditoInterMunicipal;
        this.contaDebito = contaDebito;
        this.contaDebitoIntra = contaDebitoIntra;
        this.contaDebitoInterUniao = contaDebitoInterUniao;
        this.contaDebitoInterEstado = contaDebitoInterEstado;
        this.contaDebitoInterMunicipal = contaDebitoInterMunicipal;
        this.obrigatorio = obrigatorio;
        this.variacao = variacao;
        this.tagOCCCredito = tagOCCCredito;
        this.tagOCCDebito = tagOCCDebito;
        this.criadoEm = criadoEm;
        this.tipoContaAuxiliarCredito = tipoContaAuxiliarCredito;
        this.tipoContaAuxiliarDebito = tipoContaAuxiliarDebito;
        this.variaOperacao = variaOperacao;
        this.variaOperacaoCredito = variaOperacaoCredito;
    }

    public Boolean getVariaOperacaoCredito() {
        return variaOperacaoCredito;
    }

    public void setVariaOperacaoCredito(Boolean variaOperacaoCredito) {
        this.variaOperacaoCredito = variaOperacaoCredito;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CLP getClp() {
        return clp;
    }

    public void setClp(CLP clp) {
        this.clp = clp;
    }

    public Boolean getObrigatorio() {
        return obrigatorio;
    }

    public void setObrigatorio(Boolean obrigatorio) {
        this.obrigatorio = obrigatorio;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Boolean getVariacao() {
        return variacao;
    }

    public void setVariacao(Boolean variacao) {
        this.variacao = variacao;
    }

    public TagOCC getTagOCCCredito() {
        return tagOCCCredito;
    }

    public void setTagOCCCredito(TagOCC tagOCCCredito) {
        this.tagOCCCredito = tagOCCCredito;
    }

    public TagOCC getTagOCCDebito() {
        return tagOCCDebito;
    }

    public void setTagOCCDebito(TagOCC tagOCCDebito) {
        this.tagOCCDebito = tagOCCDebito;
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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public TipoContaAuxiliar getTipoContaAuxiliarCredito() {
        return tipoContaAuxiliarCredito;
    }

    public void setTipoContaAuxiliarCredito(TipoContaAuxiliar tipoContaAuxiliarCredito) {
        this.tipoContaAuxiliarCredito = tipoContaAuxiliarCredito;
    }

    public TipoContaAuxiliar getTipoContaAuxiliarDebito() {
        return tipoContaAuxiliarDebito;
    }

    public void setTipoContaAuxiliarDebito(TipoContaAuxiliar tipoContaAuxiliarDebito) {
        this.tipoContaAuxiliarDebito = tipoContaAuxiliarDebito;
    }

    public TipoContaAuxiliar getTipoContaAuxCredSiconfi() {
        return tipoContaAuxCredSiconfi;
    }

    public void setTipoContaAuxCredSiconfi(TipoContaAuxiliar tipoContaAuxCredSiconfi) {
        this.tipoContaAuxCredSiconfi = tipoContaAuxCredSiconfi;
    }

    public TipoContaAuxiliar getTipoContaAuxDebSiconfi() {
        return tipoContaAuxDebSiconfi;
    }

    public void setTipoContaAuxDebSiconfi(TipoContaAuxiliar tipoContaAuxDebSiconfi) {
        this.tipoContaAuxDebSiconfi = tipoContaAuxDebSiconfi;
    }

    public IndicadorSuperavitFinanceiro getIndicadorSuperavitFinanCred() {
        return indicadorSuperavitFinanCred;
    }

    public void setIndicadorSuperavitFinanCred(IndicadorSuperavitFinanceiro indicadorSuperavitFinanCred) {
        this.indicadorSuperavitFinanCred = indicadorSuperavitFinanCred;
    }

    public IndicadorSuperavitFinanceiro getIndicadorSuperavitFinanDeb() {
        return indicadorSuperavitFinanDeb;
    }

    public void setIndicadorSuperavitFinanDeb(IndicadorSuperavitFinanceiro indicadorSuperavitFinanDeb) {
        this.indicadorSuperavitFinanDeb = indicadorSuperavitFinanDeb;
    }

    public Conta getContaCreditoInterEstado() {
        return contaCreditoInterEstado;
    }

    public void setContaCreditoInterEstado(Conta contaCreditoInterEstado) {
        this.contaCreditoInterEstado = contaCreditoInterEstado;
    }

    public Conta getContaCreditoInterMunicipal() {
        return contaCreditoInterMunicipal;
    }

    public void setContaCreditoInterMunicipal(Conta contaCreditoInterMunicipal) {
        this.contaCreditoInterMunicipal = contaCreditoInterMunicipal;
    }

    public Conta getContaCreditoInterUniao() {
        return contaCreditoInterUniao;
    }

    public void setContaCreditoInterUniao(Conta contaCreditoInterUniao) {
        this.contaCreditoInterUniao = contaCreditoInterUniao;
    }

    public Conta getContaCreditoIntra() {
        return contaCreditoIntra;
    }

    public void setContaCreditoIntra(Conta contaCreditoIntra) {
        this.contaCreditoIntra = contaCreditoIntra;
    }

    public Conta getContaDebitoInterEstado() {
        return contaDebitoInterEstado;
    }

    public void setContaDebitoInterEstado(Conta contaDebitoInterEstado) {
        this.contaDebitoInterEstado = contaDebitoInterEstado;
    }

    public Conta getContaDebitoInterMunicipal() {
        return contaDebitoInterMunicipal;
    }

    public void setContaDebitoInterMunicipal(Conta contaDebitoInterMunicipal) {
        this.contaDebitoInterMunicipal = contaDebitoInterMunicipal;
    }

    public Conta getContaDebitoInterUniao() {
        return contaDebitoInterUniao;
    }

    public void setContaDebitoInterUniao(Conta contaDebitoInterUniao) {
        this.contaDebitoInterUniao = contaDebitoInterUniao;
    }

    public Conta getContaDebitoIntra() {
        return contaDebitoIntra;
    }

    public void setContaDebitoIntra(Conta contaDebitoIntra) {
        this.contaDebitoIntra = contaDebitoIntra;
    }

    public Boolean getVariaOperacao() {
        return variaOperacao;
    }

    public void setVariaOperacao(Boolean variaOperacao) {
        this.variaOperacao = variaOperacao;
    }

    public Integer getItem() {
        return item;
    }

    public void setItem(Integer item) {
        this.item = item;
    }

    public TipoMovimentoTCE getTipoMovimentoTCECredito() {
        return tipoMovimentoTCECredito;
    }

    public void setTipoMovimentoTCECredito(TipoMovimentoTCE tipoMovimentoTCECredito) {
        this.tipoMovimentoTCECredito = tipoMovimentoTCECredito;
    }

    public TipoMovimentoTCE getTipoMovimentoTCEDebito() {
        return tipoMovimentoTCEDebito;
    }

    public void setTipoMovimentoTCEDebito(TipoMovimentoTCE tipoMovimentoTCEDebito) {
        this.tipoMovimentoTCEDebito = tipoMovimentoTCEDebito;
    }

    public Boolean getUsoInterno() {
        return usoInterno;
    }

    public void setUsoInterno(Boolean usoInterno) {
        this.usoInterno = usoInterno;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    public int meuHashCode() {
        int valorRetorno = 0;
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.getName() != "serialVersionUID") {
                field.setAccessible(true);
                Object value = Persistencia.getAttributeValue(this, field.getName());
                if (value != null) {
                    int hash = 7;
                    hash = 71 * hash + (value != null ? value.hashCode() : 0);
                    valorRetorno += hash;
                }
            }
        }
        return valorRetorno;
    }

    private Integer getTotalAtributosToEquals() {
        Integer i = 0;
        for (Field field : Persistencia.getAtributos(this.getClass())) {
            if (field.isAnnotationPresent(Equals.class)) {
                i++;
            }
        }
        return i;
    }

    @Override
    public boolean equals(Object object) {
//        if (!object.getClass().equals(LCP.class)) {
//            return false;
//        }
//
//        LCP outra = (LCP) object;
//
//        Integer total = getTotalAtributosToEquals();
//        Integer comparacao = 0;
//
//        for (Field field : Persistencia.getAtributos(this.getClass())) {
//            if (field.isAnnotationPresent(Equals.class)) {
//                Object valorDesta = Persistencia.getAttributeValue(this, field.getName());
//                Object valorDaOutra = Persistencia.getAttributeValue(outra, field.getName());
//
//                if (valorDesta == null && valorDaOutra == null) {
//                    comparacao++;
//                }
//
//                if (valorDesta != null && valorDaOutra != null && valorDesta.equals(valorDaOutra)) {
//                    comparacao++;
//                }
//            }
//        }
//
//        return total.compareTo(comparacao) == 0;
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        String toReturn = "";
        if (codigo != null) {
            toReturn = "Código: " + codigo;
        }
        return toReturn;
    }
//
//    @Override
//    public int compareTo(Object o) {
//        return item.compareTo(((LCP) o).getItem());
//    }
}
