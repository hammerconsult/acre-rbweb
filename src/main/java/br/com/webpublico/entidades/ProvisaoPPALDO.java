/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Vincula as ações do PPA provisionadas para o exercício a Lei de Diretrizes Orçamentárias (LDO)
 * que, uma vez aprovada, dará origem ao orçamento anual (LOA).
 *
 * @author arthur
 */
@GrupoDiagrama(nome = "PPA")
@Audited
@Entity
@Etiqueta(value = "Sub-Ação LDO")
public class ProvisaoPPALDO extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
//    @ManyToOne
//    @Tabelavel
//    private SubAcaoPPA subAcaoPPA;
    @ManyToOne
    @Tabelavel
    private ProdutoPPA produtoPPA;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    private BigDecimal totalFisico;
    private BigDecimal totalFinanceiroCorrente;
    private BigDecimal totalFinanceiroCapital;
    @ManyToOne
    private LDO ldo;

    public ProvisaoPPALDO() {
        dataRegistro = new Date();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LDO getLdo() {
        return ldo;
    }

    public void setLdo(LDO ldo) {
        this.ldo = ldo;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

//    public SubAcaoPPA getSubAcaoPPA() {
//        return subAcaoPPA;
//    }
//
//    public void setSubAcaoPPA(SubAcaoPPA subAcaoPPA) {
//        this.subAcaoPPA = subAcaoPPA;
//    }

    public ProdutoPPA getProdutoPPA() {
        return produtoPPA;
    }

    public void setProdutoPPA(ProdutoPPA produtoPPA) {
        this.produtoPPA = produtoPPA;
    }

    public BigDecimal getTotalFisico() {
        return totalFisico;
    }

    public void setTotalFisico(BigDecimal totalFisico) {
        this.totalFisico = totalFisico;
    }

    public BigDecimal getTotalFinanceiroCorrente() {
        return totalFinanceiroCorrente;
    }

    public void setTotalFinanceiroCorrente(BigDecimal totalFinanceiroCorrente) {
        this.totalFinanceiroCorrente = totalFinanceiroCorrente;
    }

    public BigDecimal getTotalFinanceiroCapital() {
        return totalFinanceiroCapital;
    }

    public void setTotalFinanceiroCapital(BigDecimal totalFinanceiroCapital) {
        this.totalFinanceiroCapital = totalFinanceiroCapital;
    }

    @Override
    public String toString() {
        return produtoPPA.toString();
    }

}
