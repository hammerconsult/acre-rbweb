package br.com.webpublico.entidades;

import br.com.webpublico.interfaces.DetentorDeOcorrencia;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 15/07/13
 * Time: 17:44
 * To change this template use File | Settings | File Templates.
 */
@Entity

@Audited
@Table(name = "OCORREPROCCALCGERALISSFIXO")
public class OcorrenciaProcessoCalculoGeralIssFixo implements Serializable, DetentorDeOcorrencia {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ProcessoCalculoGeralIssFixo processoCalculoGeral;
    @ManyToOne
    private CadastroEconomico cadastroEconomico;
    @OneToOne
    private CalculoISS calculoISS;
    @OneToOne(cascade = CascadeType.ALL)
    private Ocorrencia ocorrencia;
    @Transient
    @Invisivel
    private Long criadoEm;

    public OcorrenciaProcessoCalculoGeralIssFixo() {
        this.criadoEm = System.nanoTime();
    }

    public OcorrenciaProcessoCalculoGeralIssFixo(CalculoISS calculoISS, ProcessoCalculoGeralIssFixo processoCalculoGeral, String msg) {
        this.id = id;
        this.calculoISS = calculoISS;
        this.processoCalculoGeral = processoCalculoGeral;
        this.ocorrencia = new Ocorrencia(msg);
        this.criadoEm = System.nanoTime();
    }

    public OcorrenciaProcessoCalculoGeralIssFixo(CadastroEconomico ce, ProcessoCalculoGeralIssFixo processoCalculoGeral, String msg) {
        this.id = id;
        this.cadastroEconomico = ce;
        this.processoCalculoGeral = processoCalculoGeral;
        this.ocorrencia = new Ocorrencia(msg);
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProcessoCalculoGeralIssFixo getProcessoCalculoGeral() {
        return processoCalculoGeral;
    }

    public void setProcessoCalculoGeral(ProcessoCalculoGeralIssFixo processoCalculoGeral) {
        this.processoCalculoGeral = processoCalculoGeral;
    }

    public Ocorrencia getOcorrencia() {
        return ocorrencia;
    }

    public void setOcorrencia(Ocorrencia ocorrencia) {
        this.ocorrencia = ocorrencia;
    }

    public CalculoISS getCalculoISS() {
        return calculoISS;
    }

    public void setCalculoISS(CalculoISS calculoISS) {
        this.calculoISS = calculoISS;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
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
