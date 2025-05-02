package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 30/07/13
 * Time: 19:48
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Cacheable
@Audited
@Table(name="OCORREEFETISSFIXOGERAL")
public class OcorrenciaEfetivacaoIssFixoGeral implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private EfetivacaoProcessoIssFixoGeral efetivacaoProcesso;
    @OneToOne
    private CalculoISS calculo;
    @OneToOne(cascade = CascadeType.ALL)
    private Ocorrencia ocorrencia;
    @Transient
    @Invisivel
    private Long criadoEm;

    public OcorrenciaEfetivacaoIssFixoGeral() {
        this.criadoEm = System.nanoTime();
    }

    public OcorrenciaEfetivacaoIssFixoGeral(String message, CalculoISS calculoISS, EfetivacaoProcessoIssFixoGeral efetivacaoProcesso) {
        this.criadoEm = System.nanoTime();
        this.ocorrencia = new Ocorrencia(message);
        this.calculo = calculoISS;
        this.efetivacaoProcesso = efetivacaoProcesso;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public EfetivacaoProcessoIssFixoGeral getEfetivacaoProcesso() {
        return efetivacaoProcesso;
    }

    public void setEfetivacaoProcesso(EfetivacaoProcessoIssFixoGeral efetivacaoProcesso) {
        this.efetivacaoProcesso = efetivacaoProcesso;
    }

    public CalculoISS getCalculo() {
        return calculo;
    }

    public void setCalculo(CalculoISS calculo) {
        this.calculo = calculo;
    }

    public Ocorrencia getOcorrencia() {
        return ocorrencia;
    }

    public void setOcorrencia(Ocorrencia ocorrencia) {
        this.ocorrencia = ocorrencia;
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
