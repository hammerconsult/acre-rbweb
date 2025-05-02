package br.com.webpublico.entidades;

import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Desenvolvimento on 07/04/2017.
 */
@Entity
@Audited
public class ItemCargoSindicato extends SuperEntidade implements ValidadorVigencia{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;

    @Obrigatorio
    @ManyToOne
    @Etiqueta("Cargo")
    private Cargo cargo;

    @Obrigatorio
    @ManyToOne
    @Etiqueta("Sindicato")
    private Sindicato sindicato;

    @Obrigatorio
    @Temporal(TemporalType.DATE)
    @Etiqueta("Início de Vigência")
    private Date dataInicio;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Final de Vigência")
    private Date dataFim;

    public ItemCargoSindicato() {
        super();
    }

    public ItemCargoSindicato(Cargo cargo, Sindicato sindicato) {
        this.sindicato = sindicato;
        this.cargo = cargo;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    public Sindicato getSindicato() {
        return sindicato;
    }

    public void setSindicato(Sindicato sindicato) {
        this.sindicato = sindicato;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    @Override
    public Date getInicioVigencia() {
        return this.dataInicio;
    }

    @Override
    public Date getFimVigencia() {
        return this.dataFim;
    }

    @Override
    public void setInicioVigencia(Date data) {
        this.dataInicio = data;
    }

    @Override
    public void setFimVigencia(Date data) {
        this.dataFim = data;
    }
}
