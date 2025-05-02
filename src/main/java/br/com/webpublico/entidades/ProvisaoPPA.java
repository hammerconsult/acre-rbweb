/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author Munif
 */
@GrupoDiagrama(nome = "PPA")
@Entity

@Audited
@Etiqueta("Provisão PPA")
public class ProvisaoPPA implements Serializable, Comparable<ProvisaoPPA> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Produto PPA")
    private ProdutoPPA produtoPPA;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Meta Física")
    private BigDecimal metaFisica;
    @Tabelavel
    @Monetario
    @Pesquisavel
    @Etiqueta("Meta Financeira Corrente")
    private BigDecimal metaFinanceiraCorrente;
    @Tabelavel
    @Monetario
    @Pesquisavel
    @Etiqueta("Meta Financeira de Capital")
    private BigDecimal metaFinanceiraCapital;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Meta Física Executada")
    private BigDecimal metaFisicaExecutada;
    @Tabelavel
    @Monetario
    @Pesquisavel
    @Etiqueta("Meta Financeira Corrente Executada")
    private BigDecimal metaFinancCorrenteExecutada;
    @Tabelavel
    @Monetario
    @Pesquisavel
    @Etiqueta("Meta Financeira Capital Executada")
    private BigDecimal metaFinancCapitalExecutada;
    @Invisivel
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @OneToOne
    private ProvisaoPPA origem;
    @Etiqueta("Somente Leitura")
    private Boolean somenteLeitura;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data Meta Financeira Capital Executada")
    private Date dataFinancCapitalExecutada;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data Meta Financeira Corrente Executada")
    private Date dataFinancCorrenteExecutada;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data Meta Financeira Corrente Executada")
    private Date dataMetaFisicaExecutada;
    @Transient
    private Long criadoEm;
    @OneToMany(mappedBy = "provisaoPPA", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicaoProvisaoPPA> medicaoProvisaoPPAS;

    public ProvisaoPPA() {
        somenteLeitura = false;
        dataRegistro = new Date();
        criadoEm = System.nanoTime();
        metaFinancCapitalExecutada = BigDecimal.ZERO;
        metaFinancCorrenteExecutada = BigDecimal.ZERO;
        metaFisicaExecutada = BigDecimal.ZERO;
    }

    public ProvisaoPPA(Exercicio exercicio, ProdutoPPA produtoPPA, BigDecimal metaFisica, BigDecimal metaFinanceiraCorrente, BigDecimal metaFinanceiraCapital, Date dataRegistro) {
        this.exercicio = exercicio;
        this.produtoPPA = produtoPPA;
        this.metaFisica = metaFisica;
        this.metaFinanceiraCorrente = metaFinanceiraCorrente;
        this.metaFinanceiraCapital = metaFinanceiraCapital;
        this.dataRegistro = dataRegistro;
        criadoEm = System.nanoTime();
    }

    public Date getDataFinancCapitalExecutada() {
        return dataFinancCapitalExecutada;
    }

    public void setDataFinancCapitalExecutada(Date dataFinancCapitalExecutada) {
        this.dataFinancCapitalExecutada = dataFinancCapitalExecutada;
    }

    public Date getDataFinancCorrenteExecutada() {
        return dataFinancCorrenteExecutada;
    }

    public void setDataFinancCorrenteExecutada(Date dataFinancCorrenteExecutada) {
        this.dataFinancCorrenteExecutada = dataFinancCorrenteExecutada;
    }

    public Date getDataMetaFisicaExecutada() {
        return dataMetaFisicaExecutada;
    }

    public void setDataMetaFisicaExecutada(Date dataMetaFisicaExecutada) {
        this.dataMetaFisicaExecutada = dataMetaFisicaExecutada;
    }

    public Boolean getSomenteLeitura() {
        return somenteLeitura;
    }

    public void setSomenteLeitura(Boolean somenteLeitura) {
        this.somenteLeitura = somenteLeitura;
    }

    public ProvisaoPPA getOrigem() {
        return origem;
    }

    public void setOrigem(ProvisaoPPA origem) {
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

    public ProdutoPPA getProdutoPPA() {
        return produtoPPA;
    }

    public void setProdutoPPA(ProdutoPPA produtoPPA) {
        this.produtoPPA = produtoPPA;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public BigDecimal getMetaFisica() {
        return metaFisica;
    }

    public void setMetaFisica(BigDecimal metaFisica) {
        this.metaFisica = metaFisica;
    }

    public BigDecimal getMetaFinanceira() {
        if (metaFinanceiraCapital != null) {
            return metaFinanceiraCorrente.add(metaFinanceiraCapital);
        }
        return metaFinanceiraCorrente;
    }

    public BigDecimal getMetaFinanceiraCorrente() {
        return metaFinanceiraCorrente;
    }

    public void setMetaFinanceiraCorrente(BigDecimal metaFinanceiraCorrente) {
        this.metaFinanceiraCorrente = metaFinanceiraCorrente;
    }

    public BigDecimal getMetaFinanceiraCapital() {
        return metaFinanceiraCapital;
    }

    public void setMetaFinanceiraCapital(BigDecimal metaFinanceiraCapital) {
        this.metaFinanceiraCapital = metaFinanceiraCapital;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public BigDecimal getMetaFisicaExecutada() {
        return metaFisicaExecutada;
    }

    public void setMetaFisicaExecutada(BigDecimal metaFisicaExecutada) {
        this.metaFisicaExecutada = metaFisicaExecutada;
    }

    public BigDecimal getMetaFinancCorrenteExecutada() {
        return metaFinancCorrenteExecutada;
    }

    public void setMetaFinancCorrenteExecutada(BigDecimal metaFinancCorrenteExecutada) {
        this.metaFinancCorrenteExecutada = metaFinancCorrenteExecutada;
    }

    public BigDecimal getMetaFinancCapitalExecutada() {
        return metaFinancCapitalExecutada;
    }

    public void setMetaFinancCapitalExecutada(BigDecimal metaFinancCapitalExecutada) {
        this.metaFinancCapitalExecutada = metaFinancCapitalExecutada;
    }

    public List<MedicaoProvisaoPPA> getMedicaoProvisaoPPAS() {
        return medicaoProvisaoPPAS;
    }

    public void setMedicaoProvisaoPPAS(List<MedicaoProvisaoPPA> medicaoProvisaoPPAS) {
        this.medicaoProvisaoPPAS = medicaoProvisaoPPAS;
    }

    public String toStringAcompanhamentoMetaFisica (){
        String retorno = "";
        if(exercicio != null){
            retorno += exercicio.getAno().toString() + " - ";
        }
        if(metaFinanceiraCorrente != null){
            retorno += "Meta Financeira Corrente:R$ " + metaFinanceiraCorrente.toString() + " ";
        }
        if(metaFinanceiraCapital != null){
            retorno += "Meta Financeira Capital:R$ " + metaFinanceiraCapital.toString();
        }
        if(metaFisica != null){
            retorno += " - " + "Meta Física: " + metaFisica.toString();
        }
        return retorno;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return exercicio + " - total : " + getMetaFinanceira();
    }

    public int compareTo(ProvisaoPPA ppa) {
        if (this.exercicio.getAno() < ppa.getExercicio().getAno()) {
            return -1;
        }
        if (this.exercicio.getAno() > ppa.getExercicio().getAno()) {
            return 1;
        }
        return 0;
    }
}
