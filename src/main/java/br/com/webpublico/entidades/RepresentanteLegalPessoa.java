package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoRepresentanteLegal;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Fabio on 09/10/2018.
 */
@Entity
@Audited
public class RepresentanteLegalPessoa extends SuperEntidade {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Pessoa representante;
    @Temporal(TemporalType.DATE)
    private Date dataInicio;
    @Temporal(TemporalType.DATE)
    private Date dataFim;
    @Enumerated(EnumType.STRING)
    private TipoRepresentanteLegal tipoRepresentanteLegal;
    @ManyToOne
    private Pessoa pessoa;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pessoa getRepresentante() {
        return representante;
    }

    public void setRepresentante(Pessoa representante) {
        this.representante = representante;
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

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public TipoRepresentanteLegal getTipoRepresentanteLegal() {
        return tipoRepresentanteLegal;
    }

    public void setTipoRepresentanteLegal(TipoRepresentanteLegal tipoRepresentanteLegal) {
        this.tipoRepresentanteLegal = tipoRepresentanteLegal;
    }
}
