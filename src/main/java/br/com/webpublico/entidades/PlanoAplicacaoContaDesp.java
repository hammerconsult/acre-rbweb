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
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author major
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Contabil")
@Etiqueta("Elemento de Despesa")
public class PlanoAplicacaoContaDesp extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Valor")
    @Tabelavel
    private BigDecimal valor;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Elemento de Despesa")
    private Conta elementoDespesa;
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Plano de Trabalho")
    private PlanoAplicacao planoAplicacao;

    public PlanoAplicacaoContaDesp() {
        valor = new BigDecimal(BigInteger.ZERO);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Conta getElementoDespesa() {
        return elementoDespesa;
    }

    public void setElementoDespesa(Conta elementoDespesa) {
        this.elementoDespesa = elementoDespesa;
    }

    public PlanoAplicacao getPlanoAplicacao() {
        return planoAplicacao;
    }

    public void setPlanoAplicacao(PlanoAplicacao planoAplicacao) {
        this.planoAplicacao = planoAplicacao;
    }

    @Override
    public String toString() {
        return elementoDespesa.getCodigo() + " - " + elementoDespesa.getDescricao();
    }
}
