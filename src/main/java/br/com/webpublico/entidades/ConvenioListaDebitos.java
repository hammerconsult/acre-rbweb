package br.com.webpublico.entidades;

import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.File;
import java.util.Date;

/**
 * @author octavio
 */
@Entity
@Audited
@Etiqueta("Convênio Lista de Débitos")
public class ConvenioListaDebitos extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Agência")
    private Agencia agencia;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Número do Convênio")
    private Long numeroConvenio;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Código de Convênio MCI")
    private Long codigoConvenioMCI;
    @Temporal(TemporalType.DATE)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Data Inicial da Disponibilização dos Débitos")
    private Date dataInicialDispDebitos;
    @Temporal(TemporalType.DATE)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Data Final da Disponibilização de Débitos")
    private Date dataFinalDispDebitos;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Agencia getAgencia() {
        return agencia;
    }

    public void setAgencia(Agencia agencia) {
        this.agencia = agencia;
    }

    public Long getNumeroConvenio() {
        return numeroConvenio;
    }

    public void setNumeroConvenio(Long numeroConvenio) {
        this.numeroConvenio = numeroConvenio;
    }

    public Long getCodigoConvenioMCI() {
        return codigoConvenioMCI;
    }

    public void setCodigoConvenioMCI(Long codigoConvenioMCI) {
        this.codigoConvenioMCI = codigoConvenioMCI;
    }

    public Date getDataInicialDispDebitos() {
        return dataInicialDispDebitos;
    }

    public void setDataInicialDispDebitos(Date dataInicialDispDebitos) {
        this.dataInicialDispDebitos = dataInicialDispDebitos;
    }

    public Date getDataFinalDispDebitos() {
        return dataFinalDispDebitos;
    }

    public void setDataFinalDispDebitos(Date dataFinalDispDebitos) {
        this.dataFinalDispDebitos = dataFinalDispDebitos;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    @Override
    public String toString() {
        return exercicio != null ? exercicio.getAno() + " - " +  (agencia != null ? agencia.toString() : "") : (agencia != null ? agencia.toString() : "");
    }
}
