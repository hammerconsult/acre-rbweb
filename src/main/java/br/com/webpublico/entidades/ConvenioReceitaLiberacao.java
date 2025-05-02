package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 11/02/14
 * Time: 13:22
 * To change this template use File | Settings | File Templates.
 */
@Entity

@Audited
public class ConvenioReceitaLiberacao implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Transient
    private Long criadoEm;
    @ManyToOne
    private ConvenioReceita convenioReceita;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Pesquisavel
    @Etiqueta("Data de Liberação do Repasse")
    private Date dataLiberacaoRecurso;
    @Monetario
    @Pesquisavel
    @Etiqueta("Valor Liberado Concedente (R$)")
    private BigDecimal valorLiberadoConcedente;
    @Monetario
    @Pesquisavel
    @Etiqueta("Valor Liberado Contrapartida (R$)")
    private BigDecimal valorLiberadoContrapartida;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Pesquisavel
    @Etiqueta("Data de Liberação da Contrapartida (R$)")
    private Date dataLiberacaoContraPartida;

    public ConvenioReceitaLiberacao() {
        this.criadoEm = System.nanoTime();
        valorLiberadoConcedente = new BigDecimal(BigInteger.ZERO);
        valorLiberadoContrapartida = new BigDecimal(BigInteger.ZERO);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConvenioReceita getConvenioReceita() {
        return convenioReceita;
    }

    public void setConvenioReceita(ConvenioReceita convenioReceita) {
        this.convenioReceita = convenioReceita;
    }

    public Date getDataLiberacaoRecurso() {
        return dataLiberacaoRecurso;
    }

    public void setDataLiberacaoRecurso(Date dataLiberacaoRecurso) {
        this.dataLiberacaoRecurso = dataLiberacaoRecurso;
    }

    public BigDecimal getValorLiberadoConcedente() {
        return valorLiberadoConcedente;
    }

    public void setValorLiberadoConcedente(BigDecimal valorLiberadoConcedente) {
        this.valorLiberadoConcedente = valorLiberadoConcedente;
    }

    public BigDecimal getValorLiberadoContrapartida() {
        return valorLiberadoContrapartida;
    }

    public void setValorLiberadoContrapartida(BigDecimal valorLiberadoContrapartida) {
        this.valorLiberadoContrapartida = valorLiberadoContrapartida;
    }

    public Date getDataLiberacaoContraPartida() {
        return dataLiberacaoContraPartida;
    }

    public void setDataLiberacaoContraPartida(Date dataLiberacaoContraPartida) {
        this.dataLiberacaoContraPartida = dataLiberacaoContraPartida;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }
}
