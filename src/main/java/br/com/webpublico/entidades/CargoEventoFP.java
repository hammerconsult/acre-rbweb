package br.com.webpublico.entidades;

import br.com.webpublico.geradores.CorEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 30/01/2015
 * Time: 16:55
 */
@GrupoDiagrama(nome = "Recursos Humanos")
@Audited
@CorEntidade(value = "#FFFF00")
@Entity
public class CargoEventoFP extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    @Tabelavel
    @Etiqueta("Início da Vigência")
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;
    @Etiqueta("Final da Vigência")
    @Temporal(TemporalType.DATE)
    private Date finalVigencia;
    @ManyToOne
    private Cargo cargo;
    @ManyToOne
    private EventoFP eventoFP;

    public CargoEventoFP() {
    }

    public CargoEventoFP(Cargo cargo) {
        this.cargo = cargo;
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

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    public EventoFP getEventoFP() {
        return eventoFP;
    }

    public void setEventoFP(EventoFP eventoFP) {
        this.eventoFP = eventoFP;
    }

    @Override
    public String toString() {
        if (cargo != null) {
            return cargo + " - " + eventoFP;
        }
        return eventoFP.toString();
    }
}
