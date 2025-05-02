package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Audited
@GrupoDiagrama(nome = "RBTrans")
@Etiqueta("Parâmetros de Trânsito da Transferência de Permissão")
@Table(name = "PARAMVALORESTRANSFERENCIA")
public class ParametrosValoresTransferencia implements Serializable, Comparable<ParametrosValoresTransferencia> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Integer quantidade;
    private BigDecimal valor;
    @ManyToOne
    private Divida divida;
    @ManyToOne
    private Tributo tributo;
    @ManyToOne
    @JoinColumn(name = "PARAMTRANSITOCONFIGURACAO_ID")
    private ParametrosTransitoConfiguracao parametrosTransitoConfiguracao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public Tributo getTributo() {
        return tributo;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
    }

    public ParametrosTransitoConfiguracao getParametrosTransitoConfiguracao() {
        return parametrosTransitoConfiguracao;
    }

    public void setParametrosTransitoConfiguracao(ParametrosTransitoConfiguracao parametrosTransitoConfiguracao) {
        this.parametrosTransitoConfiguracao = parametrosTransitoConfiguracao;
    }

    @Override
    public int compareTo(ParametrosValoresTransferencia o) {
        return this.quantidade.compareTo(o.getQuantidade());
    }
}
