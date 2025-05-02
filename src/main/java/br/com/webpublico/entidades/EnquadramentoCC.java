/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.ValidadorVigenciaFolha;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author peixe
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta("EnquadramentoCC")
public class EnquadramentoCC extends SuperEntidade implements Serializable, ValidadorVigenciaFolha {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Cargo de Confiança")
    @ManyToOne
    private CargoConfianca cargoConfianca;
    @ManyToOne
    private AcessoSubsidio acessoSubsidio;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Início de Vigência")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date inicioVigencia;
    @Tabelavel
    @Etiqueta("Final de Vigência")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date finalVigencia;
    @Tabelavel
    @Etiqueta("Categoria PCS")
    @ManyToOne
    private CategoriaPCS categoriaPCS;
    @Tabelavel
    @Etiqueta("Progressão PCS")
    @ManyToOne
    private ProgressaoPCS progressaoPCS;
    @Temporal(TemporalType.DATE)
    private Date dataCadastro;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataCadastroFinalVigencia;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @Tabelavel
    @Monetario
    @Etiqueta("Vencimento Base")
    @Transient
    private BigDecimal vencimentoBase;

    public EnquadramentoCC() {
        dataRegistro = new Date();
    }

    public EnquadramentoCC(Date inicioVigencia, Date finalVigencia, BigDecimal vencimentoBase) {
        this.inicioVigencia = inicioVigencia;
        this.finalVigencia = finalVigencia;
        this.vencimentoBase = vencimentoBase;
    }

    public Date getDataCadastroFinalVigencia() {
        return dataCadastroFinalVigencia;
    }

    public void setDataCadastroFinalVigencia(Date dataCadastroFinalVigencia) {
        this.dataCadastroFinalVigencia = dataCadastroFinalVigencia;
    }

    @Override
    public BigDecimal getValor() {
        return vencimentoBase;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public CargoConfianca getCargoConfianca() {
        return cargoConfianca;
    }

    public void setCargoConfianca(CargoConfianca cargoConfianca) {
        this.cargoConfianca = cargoConfianca;
    }

    public CategoriaPCS getCategoriaPCS() {
        return categoriaPCS;
    }

    public void setCategoriaPCS(CategoriaPCS categoriaPCS) {
        this.categoriaPCS = categoriaPCS;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    @Override
    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    @Override
    public Date getFimVigencia() {
        return getFinalVigencia();
    }

    @Override
    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    @Override
    public void setFimVigencia(Date data) {
        setFinalVigencia(data);
    }

    public ProgressaoPCS getProgressaoPCS() {
        return progressaoPCS;
    }

    public void setProgressaoPCS(ProgressaoPCS progressaoPCS) {
        this.progressaoPCS = progressaoPCS;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public BigDecimal getVencimentoBase() {
        return vencimentoBase;
    }

    public void setVencimentoBase(BigDecimal vencimentoBase) {
        this.vencimentoBase = vencimentoBase;
    }

    public AcessoSubsidio getAcessoSubsidio() {
        return acessoSubsidio;
    }

    public void setAcessoSubsidio(AcessoSubsidio acessoSubsidio) {
        this.acessoSubsidio = acessoSubsidio;
    }

    @Override
    public String toString() {
        return "Cargo Confiança: " + cargoConfianca + " Categoria: " + categoriaPCS + " Pregressão: " + progressaoPCS;
    }
}
