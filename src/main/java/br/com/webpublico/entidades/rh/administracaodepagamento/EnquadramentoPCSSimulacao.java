package br.com.webpublico.entidades.rh.administracaodepagamento;

import br.com.webpublico.entidades.CategoriaPCS;
import br.com.webpublico.entidades.ProgressaoPCS;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;

/**
 * @Author peixe on 03/05/2017  17:14.
 */
@GrupoDiagrama(nome = "RecursosHumanos")
@Entity
@Audited
@Etiqueta("Enquadramento PCCR Simulação")
public class EnquadramentoPCSSimulacao implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;
    @Tabelavel
    @Temporal(TemporalType.DATE)
    private Date finalVigencia;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Progressão")
    private ProgressaoPCS progressaoPCS;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Categoria PCS")
    private CategoriaPCS categoriaPCS;
    @Tabelavel
    @Monetario
    @Etiqueta("Vencimento Base")
    private BigDecimal vencimentoBase;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataCadastro;
    private BigDecimal percentualReajuste;
    @Transient
    private BigDecimal vencimentoBaseAntigo = BigDecimal.ZERO;

    public EnquadramentoPCSSimulacao() {
        this.dataCadastro = new Date();
        vencimentoBase = BigDecimal.ZERO;
        percentualReajuste = BigDecimal.ZERO;

    }

    public EnquadramentoPCSSimulacao(CategoriaPCS categoriaPCS, BigDecimal percentualReajuste) {
        this.categoriaPCS = categoriaPCS;
        this.percentualReajuste = percentualReajuste;
    }

    public BigDecimal getPercentualReajuste() {
        return percentualReajuste;
    }

    public void setPercentualReajuste(BigDecimal percentualReajuste) {
        this.percentualReajuste = percentualReajuste;
    }

    public EnquadramentoPCSSimulacao(Date inicioVigencia, Date finalVigencia, ProgressaoPCS progressaoPCS, CategoriaPCS categoriaPCS, BigDecimal vencimentoBase) {
        this.inicioVigencia = inicioVigencia;
        this.finalVigencia = finalVigencia;
        this.progressaoPCS = progressaoPCS;
        this.categoriaPCS = categoriaPCS;
        this.vencimentoBase = vencimentoBase;
        dataCadastro = new Date();
    }

    public BigDecimal getVencimentoBaseAntigo() {
        return vencimentoBaseAntigo;
    }

    public void setVencimentoBaseAntigo(BigDecimal vencimentoBaseAntigo) {
        this.vencimentoBaseAntigo = vencimentoBaseAntigo;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
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
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EnquadramentoPCSSimulacao other = (EnquadramentoPCSSimulacao) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "" + vencimentoBase;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public static class Comparators {

        public static Comparator<EnquadramentoPCSSimulacao> INICIOVIGENCIAASC = new Comparator<EnquadramentoPCSSimulacao>() {
            @Override
            public int compare(EnquadramentoPCSSimulacao o1, EnquadramentoPCSSimulacao o2) {
                return o1.inicioVigencia.compareTo(o2.inicioVigencia);
            }
        };
        public static Comparator<EnquadramentoPCSSimulacao> INICIOVIGENCIADESC = new Comparator<EnquadramentoPCSSimulacao>() {
            @Override
            public int compare(EnquadramentoPCSSimulacao o1, EnquadramentoPCSSimulacao o2) {
                return o2.inicioVigencia.compareTo(o1.inicioVigencia);
            }
        };
    }

}
