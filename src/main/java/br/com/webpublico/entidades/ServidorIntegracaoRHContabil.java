package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@GrupoDiagrama(nome = "Contábil")
@Entity
@Audited
@Etiqueta("Integração RH/Contábil - Servidor")
@Table(name = "SERVINTEGRACAORHCONTABIL")
public class ServidorIntegracaoRHContabil extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ItemIntegracaoRHContabil itemIntegracaoRHContabil;
    @ManyToOne
    @Tabelavel
    private VinculoFP vinculoFP;
    @Tabelavel
    private BigDecimal valor;
    /*Visualizar do empenho*/
    @Transient
    private List<RetencaoIntegracaoRHContabil> retencoes;
    @Transient
    private BigDecimal valorRetencao;
    @Transient
    private BigDecimal valorLiquido;
    /*Relatório por Servidor*/
    @Transient
    private List<ServidorRetencaoRHContabil> servidoresRetencao;
    @Transient
    private BigDecimal valorServidorRetencao;
    @Transient
    private BigDecimal valorServidorLiquido;

    public ServidorIntegracaoRHContabil() {

    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemIntegracaoRHContabil getItemIntegracaoRHContabil() {
        return itemIntegracaoRHContabil;
    }

    public void setItemIntegracaoRHContabil(ItemIntegracaoRHContabil itemIntegracaoRHContabil) {
        this.itemIntegracaoRHContabil = itemIntegracaoRHContabil;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public List<RetencaoIntegracaoRHContabil> getRetencoes() {
        return retencoes;
    }

    public void setRetencoes(List<RetencaoIntegracaoRHContabil> retencoes) {
        this.retencoes = retencoes;
    }

    public BigDecimal getValorRetencao() {
        return valorRetencao;
    }

    public void setValorRetencao(BigDecimal valorRetencao) {
        this.valorRetencao = valorRetencao;
    }

    public BigDecimal getValorLiquido() {
        return valorLiquido;
    }

    public void setValorLiquido(BigDecimal valorLiquido) {
        this.valorLiquido = valorLiquido;
    }

    public List<ServidorRetencaoRHContabil> getServidoresRetencao() {
        return servidoresRetencao;
    }

    public void setServidoresRetencao(List<ServidorRetencaoRHContabil> servidoresRetencao) {
        this.servidoresRetencao = servidoresRetencao;
    }

    public BigDecimal getValorServidorRetencao() {
        return valorServidorRetencao;
    }

    public void setValorServidorRetencao(BigDecimal valorServidorRetencao) {
        this.valorServidorRetencao = valorServidorRetencao;
    }

    public BigDecimal getValorServidorLiquido() {
        return valorServidorLiquido;
    }

    public void setValorServidorLiquido(BigDecimal valorServidorLiquido) {
        this.valorServidorLiquido = valorServidorLiquido;
    }

    public void calcularValores() {
        if (retencoes != null) {
            valorRetencao = BigDecimal.ZERO;
            for (RetencaoIntegracaoRHContabil retencao : retencoes) {
                valorRetencao = valorRetencao.add(retencao.getValor());
            }
            valorLiquido = valor.subtract(valorRetencao);
        }
    }

    public void calcularValoresPorServidor() {
        if (servidoresRetencao != null) {
            valorServidorRetencao = BigDecimal.ZERO;
            for (ServidorRetencaoRHContabil retencao : servidoresRetencao) {
                valorServidorRetencao = valorServidorRetencao.add(retencao.getValor());
            }
            valorServidorLiquido = valor.subtract(valorServidorRetencao);
        }
    }
}

