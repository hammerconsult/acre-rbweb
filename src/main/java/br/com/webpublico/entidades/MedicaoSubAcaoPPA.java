/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Medição da execuçào do produto de determinada ação do PPA. Usado para acompanhar
 * o andamento da ação. Exemplo, de 50 (unidadeMedida) casas populares (descricaoProduto)
 * foram entregues 20 (valorFisico) no valor de 160.000 (valorFinanceiro).
 *
 * @author arthur
 */
@GrupoDiagrama(nome = "PPA")
@Entity

@Audited
@Etiqueta("Indicador")
public class MedicaoSubAcaoPPA implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataMedicao;
    private BigDecimal valorFisico;
    @Monetario
    private BigDecimal valorFinanceiro;
    @ManyToOne
    private SubAcaoPPA subAcaoPPA;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @OneToOne
    private MedicaoSubAcaoPPA origem;
    @Etiqueta("Somente Leitura")
    private Boolean somenteLeitura;

    public MedicaoSubAcaoPPA() {
        somenteLeitura = false;
        dataRegistro = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValorFinanceiro() {
        return valorFinanceiro;
    }

    public void setValorFinanceiro(BigDecimal valorFinanceiro) {
        this.valorFinanceiro = valorFinanceiro;
    }

    public BigDecimal getValorFisico() {
        return valorFisico;
    }

    public void setValorFisico(BigDecimal valorFisico) {
        this.valorFisico = valorFisico;
    }

    public SubAcaoPPA getSubAcaoPPA() {
        return subAcaoPPA;
    }

    public void setSubAcaoPPA(SubAcaoPPA subAcaoPPA) {
        this.subAcaoPPA = subAcaoPPA;
    }

    public Date getDataMedicao() {
        return dataMedicao;
    }

    public void setDataMedicao(Date dataMedicao) {
        this.dataMedicao = dataMedicao;
    }

    public MedicaoSubAcaoPPA getOrigem() {
        return origem;
    }

    public void setOrigem(MedicaoSubAcaoPPA origem) {
        this.origem = origem;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public Boolean getSomenteLeitura() {
        return somenteLeitura;
    }

    public void setSomenteLeitura(Boolean somenteLeitura) {
        this.somenteLeitura = somenteLeitura;
    }

    @Override
    public String toString() {
        return "Valor Fisico" + valorFisico + " Valor Financeiro=" + valorFinanceiro;
    }
}
