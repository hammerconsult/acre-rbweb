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
 * Time: 13:33
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Audited
public class ConvenioDespesaLiberacao implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Transient
    private Long criadoEm;
    @ManyToOne
    private ConvenioDespesa convenioDespesa;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Pesquisavel
    @Etiqueta("Data de Liberação do Recurso")
    private Date dataLiberacaoRecurso;
    @Monetario
    @Pesquisavel
    @Etiqueta("Valor Liberado Concedente (R$)")
    private BigDecimal valorLiberadoConcedente;
    @Monetario
    @Pesquisavel
    @Etiqueta("Valor Liberado Contrapartida (R$)")
    private BigDecimal valorLiberadoContrapartida;

    public ConvenioDespesaLiberacao() {
        this.criadoEm = System.nanoTime();
        this.valorLiberadoConcedente = new BigDecimal(BigInteger.ZERO);
        this.valorLiberadoContrapartida = new BigDecimal(BigInteger.ZERO);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConvenioDespesa getConvenioDespesa() {
        return convenioDespesa;
    }

    public void setConvenioDespesa(ConvenioDespesa convenioDespesa) {
        this.convenioDespesa = convenioDespesa;
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

}
