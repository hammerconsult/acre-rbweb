/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author peixe
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta("Enquadramento F G")
public class EnquadramentoFG extends SuperEntidade implements Serializable, ValidadorVigencia {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Função Gratificada")
    @ManyToOne
    private FuncaoGratificada funcaoGratificada;
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
    //@Obrigatorio
    @Etiqueta("Categoria PCS")
    @ManyToOne
    private CategoriaPCS categoriaPCS;
    @Tabelavel
    //@Obrigatorio
    @Etiqueta("Progressão PCS")
    @ManyToOne
    private ProgressaoPCS progressaoPCS;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @Monetario
    @Etiqueta("Vencimento Base")
    @Transient
    private BigDecimal vencimentoBase;
    @Temporal(TemporalType.DATE)
    private Date dataCadastro;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataCadastroFinalVigencia;
    @Transient
    private List<EnquadramentoPCS> enquadramentoPCSList;

    public List<EnquadramentoPCS> getEnquadramentoPCSList() {
        return enquadramentoPCSList;
    }

    public void setEnquadramentoPCSList(List<EnquadramentoPCS> enquadramentoPCSList) {
        this.enquadramentoPCSList = enquadramentoPCSList;
    }

    public EnquadramentoFG() {
        dataRegistro = new Date();
    }

    public Date getDataCadastroFinalVigencia() {
        return dataCadastroFinalVigencia;
    }

    public void setDataCadastroFinalVigencia(Date dataCadastroFinalVigencia) {
        this.dataCadastroFinalVigencia = dataCadastroFinalVigencia;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
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

    public FuncaoGratificada getFuncaoGratificada() {
        return funcaoGratificada;
    }

    public void setFuncaoGratificada(FuncaoGratificada funcaoGratificada) {
        this.funcaoGratificada = funcaoGratificada;
    }

    @Override
    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    @Override
    public Date getFimVigencia() {
        return finalVigencia;
    }

    @Override
    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    @Override
    public void setFimVigencia(Date data) {
        finalVigencia = data;
    }

    public ProgressaoPCS getProgressaoPCS() {
        return progressaoPCS;
    }

    public void setProgressaoPCS(ProgressaoPCS progressaoPCS) {
        this.progressaoPCS = progressaoPCS;
    }

    public BigDecimal getVencimentoBase() {
        return vencimentoBase;
    }

    public void setVencimentoBase(BigDecimal vencimentoBase) {
        this.vencimentoBase = vencimentoBase;
    }

    @Override
    public String toString() {
        return "CategoriaPCS: " + categoriaPCS + " Progressão: " + progressaoPCS;
    }

}
