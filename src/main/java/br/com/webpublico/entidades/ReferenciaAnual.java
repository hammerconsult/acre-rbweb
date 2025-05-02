/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * @author edi
 */
@Entity
@Audited
@Etiqueta("Referência Anual")

public class ReferenciaAnual extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @ManyToOne
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    @Etiqueta("N° do PPA")
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Pesquisavel
    private String numeroPPA;
    @Etiqueta("N° da LDO")
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Pesquisavel
    private String numeroLDO;
    @Etiqueta("N° da LOA")
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Pesquisavel
    private String numeroLOA;
    @Etiqueta("Data do PPA")
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.CENTRO)
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    private Date dataPPA;
    @Etiqueta("Data da LDO")
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.CENTRO)
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    private Date dataLDO;
    @Etiqueta("Data da LOA")
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.CENTRO)
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    private Date dataLOA;
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Monetario
    @Etiqueta("Meta Res. Primário")
    private BigDecimal metaResultadoPrimario;
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Monetario
    @Etiqueta("Meta Res. Nominal")
    private BigDecimal metaResultadoNominal;
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Monetario
    @Etiqueta("Res. Primario Exercício Anterior")
    private BigDecimal resultadoPrimarioExercAnt;
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Monetario
    @Etiqueta("Saldo de Alienação de Ativos")
    private BigDecimal saldoAlienacaoAtivos;

    public ReferenciaAnual() {
        this.metaResultadoNominal = new BigDecimal(BigInteger.ZERO);
        this.metaResultadoPrimario = new BigDecimal(BigInteger.ZERO);
        this.resultadoPrimarioExercAnt = new BigDecimal(BigInteger.ZERO);
        this.saldoAlienacaoAtivos = new BigDecimal(BigInteger.ZERO);
    }

    public String getNumeroPPA() {
        return numeroPPA;
    }

    public void setNumeroPPA(String numeroPPA) {
        this.numeroPPA = numeroPPA;
    }

    public String getNumeroLDO() {
        return numeroLDO;
    }

    public void setNumeroLDO(String numeroLDO) {
        this.numeroLDO = numeroLDO;
    }

    public String getNumeroLOA() {
        return numeroLOA;
    }

    public void setNumeroLOA(String numeroLOA) {
        this.numeroLOA = numeroLOA;
    }

    public Date getDataPPA() {
        return dataPPA;
    }

    public void setDataPPA(Date dataPPA) {
        this.dataPPA = dataPPA;
    }

    public Date getDataLDO() {
        return dataLDO;
    }

    public void setDataLDO(Date dataLDO) {
        this.dataLDO = dataLDO;
    }

    public Date getDataLOA() {
        return dataLOA;
    }

    public void setDataLOA(Date dataLOA) {
        this.dataLOA = dataLOA;
    }

    public BigDecimal getMetaResultadoPrimario() {
        return metaResultadoPrimario;
    }

    public void setMetaResultadoPrimario(BigDecimal metaResultadoPrimario) {
        this.metaResultadoPrimario = metaResultadoPrimario;
    }

    public BigDecimal getMetaResultadoNominal() {
        return metaResultadoNominal;
    }

    public void setMetaResultadoNominal(BigDecimal metaResultadoNominal) {
        this.metaResultadoNominal = metaResultadoNominal;
    }

    public BigDecimal getResultadoPrimarioExercAnt() {
        return resultadoPrimarioExercAnt;
    }

    public void setResultadoPrimarioExercAnt(BigDecimal resultadoPrimarioExercAnt) {
        this.resultadoPrimarioExercAnt = resultadoPrimarioExercAnt;
    }

    public BigDecimal getSaldoAlienacaoAtivos() {
        return saldoAlienacaoAtivos;
    }

    public void setSaldoAlienacaoAtivos(BigDecimal saldoAlienacaoAtivos) {
        this.saldoAlienacaoAtivos = saldoAlienacaoAtivos;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
