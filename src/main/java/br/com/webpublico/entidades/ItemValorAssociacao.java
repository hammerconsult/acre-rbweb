/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.TipoAssociacao;
import br.com.webpublico.enums.TipoValorAssociacao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author boy
 */
@Etiqueta("Valor da Associação")
@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
@Entity

public class ItemValorAssociacao extends SuperEntidade implements Serializable, ValidadorVigencia {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Início de Vigência")
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Final de Vigência")
    private Date finalVigencia;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Valor")
    private BigDecimal valor;
    @Tabelavel
    @Etiqueta("Tipo de Associação")
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private TipoAssociacao tipoAssociacao;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Tipo do Valor")
    private TipoValorAssociacao tipoValorAssociacao;
    @Obrigatorio
    @ManyToOne
    private Associacao associacao;
    @Transient
    private Operacoes operacao;

    public ItemValorAssociacao() {
    }

    public ItemValorAssociacao(Associacao associacao) {
        setAssociacao(associacao);
    }

    public Associacao getAssociacao() {
        return associacao;
    }

    public void setAssociacao(Associacao associacao) {
        this.associacao = associacao;
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
    public Date getFimVigencia() {
        return getFinalVigencia();
    }

    @Override
    public void setFimVigencia(Date data) {
        setFinalVigencia(data);
    }

    public TipoAssociacao getTipoAssociacao() {
        return tipoAssociacao;
    }

    public void setTipoAssociacao(TipoAssociacao tipoAssociacao) {
        this.tipoAssociacao = tipoAssociacao;
    }

    public TipoValorAssociacao getTipoValorAssociacao() {
        return tipoValorAssociacao;
    }

    public void setTipoValorAssociacao(TipoValorAssociacao tipoValorAssociacao) {
        this.tipoValorAssociacao = tipoValorAssociacao;
        this.valor = null;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Operacoes getOperacao() {
        return operacao;
    }

    public void setOperacao(Operacoes operacao) {
        this.operacao = operacao;
    }

    @Override
    public String toString() {
        return valor + " ----- " + tipoValorAssociacao + " ----- " + tipoAssociacao;
    }

    public boolean estaEditando() {
        return Operacoes.EDITAR.equals(this.getOperacao());
    }

    public boolean isTipoValorFixo() {
        return TipoValorAssociacao.VALOR_FIXO.equals(this.getTipoValorAssociacao());
    }

    public boolean isTipoValorPercentual() {
        return TipoValorAssociacao.VALOR_PERCENTUAL.equals(this.getTipoValorAssociacao());
    }
}
