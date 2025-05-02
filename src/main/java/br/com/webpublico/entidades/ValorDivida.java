/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@GrupoDiagrama(nome = "Tributario")
@Entity
@Audited
public class ValorDivida extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date emissao;
    private BigDecimal valor;
    @ManyToOne(fetch = FetchType.LAZY)
    private Calculo calculo;
    @ManyToOne(fetch = FetchType.LAZY)
    private Entidade entidade;
    @ManyToOne
    private Divida divida;
    @ManyToOne
    private Exercicio exercicio;
    @OneToMany(mappedBy = "valorDivida", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemValorDivida> itemValorDividas;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "valorDivida", orphanRemoval = true)
    private List<ParcelaValorDivida> parcelaValorDividas;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataQuitacao;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "valorDivida")
    private List<OcorrenciaValorDivida> ocorrenciaValorDivida;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "valorDivida")
    private List<ValorDividaIntegracao> integracoes;

    public ValorDivida() {
        this.parcelaValorDividas = Lists.newArrayList();
        this.itemValorDividas = Lists.newArrayList();
        this.integracoes = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Calculo getCalculo() {
        return calculo;
    }

    public void setCalculo(Calculo calculo) {
        this.calculo = calculo;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public Date getEmissao() {
        return emissao;
    }

    public void setEmissao(Date emissao) {
        this.emissao = emissao;
    }

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public List<ItemValorDivida> getItemValorDividas() {
        return itemValorDividas;
    }

    public void setItemValorDividas(List<ItemValorDivida> itemValorDividas) {
        this.itemValorDividas = itemValorDividas;
    }

    public List<ParcelaValorDivida> getParcelaValorDividas() {
        return parcelaValorDividas;
    }

    public void setParcelaValorDividas(List<ParcelaValorDivida> parcelaValorDividas) {
        this.parcelaValorDividas = parcelaValorDividas;
    }

    public Date getDataQuitacao() {
        return dataQuitacao;
    }

    public void setDataQuitacao(Date dataQuitacao) {
        this.dataQuitacao = dataQuitacao;
    }

    public List<OcorrenciaValorDivida> getOcorrenciaValorDivida() {
        return ocorrenciaValorDivida;
    }

    public void setOcorrenciaValorDivida(List<OcorrenciaValorDivida> ocorrenciaValorDivida) {
        this.ocorrenciaValorDivida = ocorrenciaValorDivida;
    }

    public List<ValorDividaIntegracao> getIntegracoes() {
        return integracoes;
    }

    public void setIntegracoes(List<ValorDividaIntegracao> integracoes) {
        this.integracoes = integracoes;
    }

    @Override
    public String toString() {
        return "Calculo  " + calculo.getTipoCalculo() + " Exercicío.: " + exercicio.getAno() + ", Valor.: " + valor;
    }

    public ParcelaValorDivida getCotaUnica() {
        for (ParcelaValorDivida parcela : parcelaValorDividas) {
            if (parcela.isCotaUnica()) {
                return parcela;
            }
        }
        throw new ExcecaoNegocioGenerica("Nenhuma cota única encontrada para valor dívida " + this.toString());
    }

}
