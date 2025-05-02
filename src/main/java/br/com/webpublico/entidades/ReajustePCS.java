package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 25/05/2015
 * Time: 11:18
 */
@GrupoDiagrama(nome = "RecursosHumanos")
@Entity

@Audited
@Etiqueta("Reajuste PCS")
public class ReajustePCS extends SuperEntidade implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    private BigDecimal percentual;
    @Temporal(TemporalType.DATE)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Data do Reajuste")
    private Date dataReajuste;
    @Invisivel
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCadastro = new Date();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "reajustePCS")
    private List<ReajusteEnquadramentoPCS> reajusteEnquadramentoPCS;
    @ManyToOne
    private PlanoCargosSalarios planoCargosSalarios;
    @ManyToOne
    private CategoriaPCS categoriaPCS;
    @ManyToOne
    private ProgressaoPCS progressaoPCS;

    //Verificar necessidade de associar com uma Lei.


    public ReajustePCS() {
        reajusteEnquadramentoPCS = new ArrayList<>();
        dataCadastro = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getPercentual() {
        return percentual;
    }

    public void setPercentual(BigDecimal percentual) {
        this.percentual = percentual;
    }

    public Date getDataReajuste() {
        return dataReajuste;
    }

    public void setDataReajuste(Date dataReajuste) {
        this.dataReajuste = dataReajuste;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public List<ReajusteEnquadramentoPCS> getReajusteEnquadramentoPCS() {
        return reajusteEnquadramentoPCS;
    }

    public void setReajusteEnquadramentoPCS(List<ReajusteEnquadramentoPCS> reajusteEnquadramentoPCS) {
        this.reajusteEnquadramentoPCS = reajusteEnquadramentoPCS;
    }

    public PlanoCargosSalarios getPlanoCargosSalarios() {
        return planoCargosSalarios;
    }

    public void setPlanoCargosSalarios(PlanoCargosSalarios planoCargosSalarios) {
        this.planoCargosSalarios = planoCargosSalarios;
    }

    public CategoriaPCS getCategoriaPCS() {
        return categoriaPCS;
    }

    public void setCategoriaPCS(CategoriaPCS categoriaPCS) {
        this.categoriaPCS = categoriaPCS;
    }

    public ProgressaoPCS getProgressaoPCS() {
        return progressaoPCS;
    }

    public void setProgressaoPCS(ProgressaoPCS progressaoPCS) {
        this.progressaoPCS = progressaoPCS;
    }

    @Override
    public String toString() {
        return percentual +" "+ dataReajuste ;
    }
}
