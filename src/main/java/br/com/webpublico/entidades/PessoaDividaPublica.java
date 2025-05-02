/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.QualificacaoBeneficiario;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author venon
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Contabil")
public class PessoaDividaPublica implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Tabelavel
    @Etiqueta(value = "Beneficiario")
    private Pessoa pessoa;
    @Tabelavel
    @Etiqueta(value = "Doença Grave?")
    private Boolean doencaGrave;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Etiqueta(value = "Qualificação")
    private QualificacaoBeneficiario qualificacaoBeneficiario;
    @Tabelavel
    @Etiqueta(value = "Valor")
    @Monetario
    private BigDecimal valor;
    private BigDecimal debitoCompensado;
    @Tabelavel
    @Etiqueta(value = "Saldo a Pagar")
    @Monetario
    private BigDecimal saldoAPagar;
    private BigDecimal contribPrev;
    private BigDecimal fgts;
    private BigDecimal ir;
    private BigDecimal honorarioAdvocaticio;
    private BigDecimal descontoAcordo;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataInformacao;
    private Boolean cancelado;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataCancelamento;
    private String motivoCancelamento;
    @ManyToOne
    private DividaPublica dividaPublica;
    @Transient
    private Long criadoEm;

    public PessoaDividaPublica() {
        criadoEm = System.nanoTime();
        doencaGrave = false;
        cancelado = false;
        valor = BigDecimal.ZERO;
        debitoCompensado = BigDecimal.ZERO;
        saldoAPagar = BigDecimal.ZERO;
        contribPrev = BigDecimal.ZERO;
        fgts = BigDecimal.ZERO;
        ir = BigDecimal.ZERO;
        honorarioAdvocaticio = BigDecimal.ZERO;
        descontoAcordo = BigDecimal.ZERO;
    }

    public PessoaDividaPublica(Boolean doencaGrave, QualificacaoBeneficiario qualificacaoBeneficiario, BigDecimal valor, BigDecimal debitoCompensado, BigDecimal saldoAPagar, BigDecimal contribPrev, BigDecimal fgts, BigDecimal ir, BigDecimal honorarioAdvocaticio, BigDecimal descontoAcordo, Date dataInformacao, Boolean cancelado, Date dataCancelamento, String motivoCancelamento, Pessoa pessoa, DividaPublica dividaPublica) {
        this.doencaGrave = doencaGrave;
        this.qualificacaoBeneficiario = qualificacaoBeneficiario;
        this.valor = valor;
        this.debitoCompensado = debitoCompensado;
        this.saldoAPagar = saldoAPagar;
        this.contribPrev = contribPrev;
        this.fgts = fgts;
        this.ir = ir;
        this.honorarioAdvocaticio = honorarioAdvocaticio;
        this.descontoAcordo = descontoAcordo;
        this.dataInformacao = dataInformacao;
        this.cancelado = cancelado;
        this.dataCancelamento = dataCancelamento;
        this.motivoCancelamento = motivoCancelamento;
        this.pessoa = pessoa;
        this.dividaPublica = dividaPublica;
    }

    public BigDecimal getDescontoAcordo() {
        return descontoAcordo;
    }

    public void setDescontoAcordo(BigDecimal descontoAcordo) {
        this.descontoAcordo = descontoAcordo;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getContribPrev() {
        return contribPrev;
    }

    public void setContribPrev(BigDecimal contribPrev) {
        this.contribPrev = contribPrev;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Date getDataInformacao() {
        return dataInformacao;
    }

    public void setDataInformacao(Date dataInformacao) {
        this.dataInformacao = dataInformacao;
    }

    public BigDecimal getFgts() {
        return fgts;
    }

    public void setFgts(BigDecimal fgts) {
        this.fgts = fgts;
    }

    public BigDecimal getHonorarioAdvocaticio() {
        return honorarioAdvocaticio;
    }

    public void setHonorarioAdvocaticio(BigDecimal honorarioAdvocaticio) {
        this.honorarioAdvocaticio = honorarioAdvocaticio;
    }

    public BigDecimal getIr() {
        return ir;
    }

    public void setIr(BigDecimal ir) {
        this.ir = ir;
    }

    public BigDecimal getDebitoCompensado() {
        return debitoCompensado;
    }

    public void setDebitoCompensado(BigDecimal debitoCompensado) {
        this.debitoCompensado = debitoCompensado;
    }

    public DividaPublica getDividaPublica() {
        return dividaPublica;
    }

    public void setDividaPublica(DividaPublica dividaPublica) {
        this.dividaPublica = dividaPublica;
    }

    public Boolean getDoencaGrave() {
        return doencaGrave;
    }

    public void setDoencaGrave(Boolean doencaGrave) {
        this.doencaGrave = doencaGrave;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public QualificacaoBeneficiario getQualificacaoBeneficiario() {
        return qualificacaoBeneficiario;
    }

    public void setQualificacaoBeneficiario(QualificacaoBeneficiario qualificacaoBeneficiario) {
        this.qualificacaoBeneficiario = qualificacaoBeneficiario;
    }

    public BigDecimal getSaldoAPagar() {
        this.saldoAPagar = this.valor.subtract(this.debitoCompensado).subtract(this.fgts).subtract(this.ir).subtract(this.honorarioAdvocaticio).subtract(this.descontoAcordo);
        return saldoAPagar;
    }

    public void setSaldoAPagar(BigDecimal saldoAPagar) {
        this.saldoAPagar = saldoAPagar;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Boolean getCancelado() {
        return cancelado;
    }

    public void setCancelado(Boolean cancelado) {
        this.cancelado = cancelado;
    }

    public Date getDataCancelamento() {
        return dataCancelamento;
    }

    public void setDataCancelamento(Date dataCancelamento) {
        this.dataCancelamento = dataCancelamento;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
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
        return "Pessoa: " + pessoa.getNome() + " - Valor Individualizado: " + valor + " - Débitos Compensados: " + debitoCompensado + " - Contribuição Previdênciaria: " + contribPrev;
    }
}
