/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @author major
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Contabil")
@Etiqueta("Plano de Aplicação")
public class PlanoAplicacao extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Valor Plano")
    @Obrigatorio
    @Tabelavel
    private BigDecimal valorConvenioPlano;
    @Etiqueta("Valor Contra Partida do Plano")
    @Obrigatorio
    @Tabelavel
    private BigDecimal valorContrapartidaPlano;
    @Obrigatorio
    @Etiqueta("Conta de Despesa")
    @Tabelavel
    @ManyToOne
    private Conta conta;
//    @Obrigatorio
//    @Etiqueta("Categoria de Despesa")
//    @ManyToOne
//    @Tabelavel
//    private CategoriaDespesa categoriaDespesa;
    @Obrigatorio
    @Etiqueta("Convênio de Despesa")
    @ManyToOne
    private ConvenioDespesa convenioDespesa;
    @Etiqueta("Metafísica")
    @Tabelavel
    private Double metaFisica;
    @ManyToOne
    @Etiqueta("Unidade de Medida")
    @Tabelavel
    private UnidadeMedida unidadeMedida;
    @Etiqueta("Número")
    @Tabelavel
    private String numero;
    @Etiqueta("Descrição")
    @Tabelavel
    private String descricao;

    @Invisivel
    @OneToMany(mappedBy = "planoAplicacao", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta(value = "Categoria da Despesa")
    @Tabelavel(campoSelecionado = false)
    private List<PlanoAplicacaoCategDesp> listaCategoriaDespesas;

    @Invisivel
    @OneToMany(mappedBy = "planoAplicacao", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta(value = "Categoria da Despesa")
    @Tabelavel(campoSelecionado = false)
    private List<PlanoAplicacaoContaDesp> listaElementosDespesa;

    public PlanoAplicacao() {
        valorConvenioPlano = new BigDecimal(BigInteger.ZERO);
        valorContrapartidaPlano = new BigDecimal(BigInteger.ZERO);
        metaFisica = 0.0;
        listaCategoriaDespesas = new ArrayList<>();
        listaElementosDespesa= new ArrayList<>();
    }

    public PlanoAplicacao(BigDecimal valorConvenioPlano, BigDecimal valorContrapartidaPlano, Conta conta, CategoriaDespesa categoriaDespesa, ConvenioDespesa convenioDespesa, Double metaFisica, UnidadeMedida unidadeMedida) {
        this.valorConvenioPlano = valorConvenioPlano;
        this.valorContrapartidaPlano = valorContrapartidaPlano;
        this.conta = conta;
//        this.categoriaDespesa = categoriaDespesa;
        this.convenioDespesa = convenioDespesa;
        this.metaFisica = metaFisica;
        this.unidadeMedida = unidadeMedida;
    }

    public List<PlanoAplicacaoCategDesp> getListaCategoriaDespesas() {
        return listaCategoriaDespesas;
    }

    public void setListaCategoriaDespesas(List<PlanoAplicacaoCategDesp> listaCategoriaDespesas) {
        this.listaCategoriaDespesas = listaCategoriaDespesas;
    }

    public List<PlanoAplicacaoContaDesp> getListaElementosDespesa() {
        return listaElementosDespesa;
    }

    public void setListaElementosDespesa(List<PlanoAplicacaoContaDesp> listaElementosDespesa) {
        this.listaElementosDespesa = listaElementosDespesa;
    }

    public ConvenioDespesa getConvenioDespesa() {
        return convenioDespesa;
    }

    public void setConvenioDespesa(ConvenioDespesa convenioDespesa) {
        this.convenioDespesa = convenioDespesa;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public BigDecimal getValorContrapartidaPlano() {
        return valorContrapartidaPlano;
    }

    public void setValorContrapartidaPlano(BigDecimal valorContrapartidaPlano) {
        this.valorContrapartidaPlano = valorContrapartidaPlano;
    }

    public BigDecimal getValorConvenioPlano() {
        return valorConvenioPlano;
    }

    public void setValorConvenioPlano(BigDecimal valorConvenioPlano) {
        this.valorConvenioPlano = valorConvenioPlano;
    }

    public Double getMetaFisica() {
        return metaFisica;
    }

    public void setMetaFisica(Double metaFisica) {
        this.metaFisica = metaFisica;
    }

    public UnidadeMedida getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.PlanoAplicacao[ id=" + id + " ]";
    }
}
