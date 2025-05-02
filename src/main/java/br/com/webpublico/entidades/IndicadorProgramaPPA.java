/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * @author Munif
 */
@GrupoDiagrama(nome = "PPA")
@Entity

@Audited
@Etiqueta("Indicador Programa PPA")
public class IndicadorProgramaPPA extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Obrigatorio
    private ProgramaPPA programa;
    @ManyToOne
    @Obrigatorio
    private Indicador indicador;
    /**
     * Determina o valor de referência do indicador no ato da concepção do Programa PPA.
     * Usado para determinar se o programa cumpriu seus objetivos, quando comparado com o
     * valorDesejado ao final do programa.
     */
    @ManyToOne
    private ValorIndicador valorReferencia;
    /**
     * Periodicidade na qual este indicador deve ser apurado para permitir o acompanhamento dos resultados deste programa.
     */
    @ManyToOne
    private Periodicidade periodicidade;
    /**
     * Valor fixado como meta a ser atingido ao término deste programa do PPA.
     */
    @Monetario
    private BigDecimal valorDesejado;
    /**
     * Campo utilizado somente para comparação dentra da lista.
     */
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @OneToOne
    private IndicadorProgramaPPA origem;
    @Etiqueta("Somente Leitura")
    private Boolean somenteLeitura;


    public IndicadorProgramaPPA() {
        dataRegistro = new Date();
        somenteLeitura = false;
    }

    public Boolean getSomenteLeitura() {
        return somenteLeitura;
    }

    public void setSomenteLeitura(Boolean somenteLeitura) {
        this.somenteLeitura = somenteLeitura;
    }

    public IndicadorProgramaPPA getOrigem() {
        return origem;
    }

    public void setOrigem(IndicadorProgramaPPA origem) {
        this.origem = origem;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Indicador getIndicador() {
        return indicador;
    }

    public void setIndicador(Indicador indicador) {
        this.indicador = indicador;
    }

    public Periodicidade getPeriodicidade() {
        return periodicidade;
    }

    public void setPeriodicidade(Periodicidade periodicidade) {
        this.periodicidade = periodicidade;
    }

    public ProgramaPPA getPrograma() {
        return programa;
    }

    public void setPrograma(ProgramaPPA programa) {
        this.programa = programa;
    }

    public BigDecimal getValorDesejado() {
        return valorDesejado;
    }

    public void setValorDesejado(BigDecimal valorDesejado) {
        this.valorDesejado = valorDesejado;
    }

    public ValorIndicador getValorReferencia() {
        return valorReferencia;
    }

    public void setValorReferencia(ValorIndicador valorReferencia) {
        this.valorReferencia = valorReferencia;
    }

    @Override
    public String toString() {
        return valorDesejado + "";
    }

}
