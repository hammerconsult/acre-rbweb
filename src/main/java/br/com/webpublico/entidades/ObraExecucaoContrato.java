package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited

@Etiqueta("Obras da Execução do Contrato")
public class ObraExecucaoContrato extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Execução Contrato")
    private ExecucaoContrato execucaoContrato;

    @Etiqueta("No Exercício (Medições)")
    private String noExercicio;

    @Etiqueta("Anterior")
    private Integer anterior;

    @Etiqueta("Acumulado")
    private Integer acumulado;

    @Etiqueta("Saldo")
    private Integer saldo;

    @Etiqueta("Número do Convênio")
    private Integer numeroConvenio;

    @Etiqueta("ano do Convênio")
    private Integer anoConvenio;

    @Etiqueta("Data do Convênio")
    @Temporal(TemporalType.DATE)
    private Date dataDoConvenio;

    @Etiqueta("Entidade do Convênio")
    private String entidadeConvenio;

    public ObraExecucaoContrato() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ExecucaoContrato getExecucaoContrato() {
        return execucaoContrato;
    }

    public void setExecucaoContrato(ExecucaoContrato execucaoContrato) {
        this.execucaoContrato = execucaoContrato;
    }

    public String getNoExercicio() {
        return noExercicio;
    }

    public void setNoExercicio(String noExercicio) {
        this.noExercicio = noExercicio;
    }

    public Integer getAnterior() {
        return anterior;
    }

    public void setAnterior(Integer anterior) {
        this.anterior = anterior;
    }

    public Integer getAcumulado() {
        return acumulado;
    }

    public void setAcumulado(Integer acumulado) {
        this.acumulado = acumulado;
    }

    public Integer getSaldo() {
        return saldo;
    }

    public void setSaldo(Integer saldo) {
        this.saldo = saldo;
    }

    public Integer getNumeroConvenio() {
        return numeroConvenio;
    }

    public void setNumeroConvenio(Integer numeroConvenio) {
        this.numeroConvenio = numeroConvenio;
    }

    public Integer getAnoConvenio() {
        return anoConvenio;
    }

    public void setAnoConvenio(Integer anoConvenio) {
        this.anoConvenio = anoConvenio;
    }

    public Date getDataDoConvenio() {
        return dataDoConvenio;
    }

    public void setDataDoConvenio(Date dataDoConvenio) {
        this.dataDoConvenio = dataDoConvenio;
    }

    public String getEntidadeConvenio() {
        return entidadeConvenio;
    }

    public void setEntidadeConvenio(String entidadeConvenio) {
        this.entidadeConvenio = entidadeConvenio;
    }

    @Override
    public String toString() {
        return "Obra: "+execucaoContrato;
    }
}
