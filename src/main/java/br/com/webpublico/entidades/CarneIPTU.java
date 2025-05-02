package br.com.webpublico.entidades;


import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
@Entity
@Audited
@Etiqueta("Carnê de IPTU")
public class CarneIPTU extends SuperEntidade implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private BigDecimal fracaoIdeal;
    private BigDecimal areaConstruida;
    private BigDecimal areaExcedente;
    private BigDecimal vlrM2Terreno;
    private BigDecimal vlrM2Construido;
    private BigDecimal vlrM2Excedente;
    private BigDecimal ufmrb;
    private BigDecimal fatorCorrecao;
    private BigDecimal fatorPeso;
    private BigDecimal vlrVenalTerreno;
    private BigDecimal vlrVenalEdificacao;
    private BigDecimal vlrVenalExcedente;
    private BigDecimal aliquota;
    @OneToOne
    private CalculoIPTU calculo;
    @ManyToOne
    private Construcao construcao;

    public BigDecimal getFracaoIdeal() {
        return fracaoIdeal;
    }

    public void setFracaoIdeal(BigDecimal fracaoIdeal) {
        this.fracaoIdeal = fracaoIdeal;
    }

    public BigDecimal getAreaConstruida() {
        return areaConstruida;
    }

    public void setAreaConstruida(BigDecimal areaConstruida) {
        this.areaConstruida = areaConstruida;
    }

    public BigDecimal getAreaExcedente() {
        return areaExcedente;
    }

    public void setAreaExcedente(BigDecimal areaExcedente) {
        this.areaExcedente = areaExcedente;
    }

    public BigDecimal getVlrM2Terreno() {
        return vlrM2Terreno;
    }

    public void setVlrM2Terreno(BigDecimal vlrM2Terreno) {
        this.vlrM2Terreno = vlrM2Terreno;
    }

    public BigDecimal getVlrM2Construido() {
        return vlrM2Construido;
    }

    public void setVlrM2Construido(BigDecimal vlrM2Construido) {
        this.vlrM2Construido = vlrM2Construido;
    }

    public BigDecimal getVlrM2Excedente() {
        return vlrM2Excedente;
    }

    public void setVlrM2Excedente(BigDecimal vlrM2Excedente) {
        this.vlrM2Excedente = vlrM2Excedente;
    }

    public BigDecimal getUfmrb() {
        return ufmrb;
    }

    public void setUfmrb(BigDecimal ufmrb) {
        this.ufmrb = ufmrb;
    }

    public BigDecimal getFatorCorrecao() {
        return fatorCorrecao;
    }

    public void setFatorCorrecao(BigDecimal fatorCorrecao) {
        this.fatorCorrecao = fatorCorrecao;
    }

    public BigDecimal getFatorPeso() {
        return fatorPeso;
    }

    public void setFatorPeso(BigDecimal fatorPeso) {
        this.fatorPeso = fatorPeso;
    }

    public BigDecimal getVlrVenalTerreno() {
        return vlrVenalTerreno;
    }

    public void setVlrVenalTerreno(BigDecimal vlrVenalTerreno) {
        this.vlrVenalTerreno = vlrVenalTerreno;
    }

    public BigDecimal getVlrVenalEdificacao() {
        return vlrVenalEdificacao;
    }

    public void setVlrVenalEdificacao(BigDecimal vlrVenalEdificacao) {
        this.vlrVenalEdificacao = vlrVenalEdificacao;
    }

    public BigDecimal getVlrVenalExcedente() {
        return vlrVenalExcedente;
    }

    public void setVlrVenalExcedente(BigDecimal vlrVenalExcedente) {
        this.vlrVenalExcedente = vlrVenalExcedente;
    }

    public CalculoIPTU getCalculo() {
        return calculo;
    }

    public void setCalculo(CalculoIPTU calculo) {
        this.calculo = calculo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAliquota() {
        return aliquota;
    }

    public void setAliquota(BigDecimal aliquota) {
        this.aliquota = aliquota;
    }

    public Construcao getConstrucao() {
        return construcao;
    }

    public void setConstrucao(Construcao construcao) {
        this.construcao = construcao;
    }
}
