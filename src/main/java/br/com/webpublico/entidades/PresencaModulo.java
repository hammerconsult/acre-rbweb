package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by AndreGustavo on 20/10/2014.
 */
@Entity
@Audited
@Etiqueta("Presença no Módulo")
public class PresencaModulo extends SuperEntidade {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta("Módulo de Capacitação")
    private ModuloCapacitacao moduloCapacitacao;
    @ManyToOne
    @Etiqueta("Inscrição")
    private InscricaoCapacitacao inscricaoCapacitacao;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data da Presença")
    @Tabelavel
    @Pesquisavel
    private Date dia;
    private Boolean presente;

    public PresencaModulo() {
        presente = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ModuloCapacitacao getModuloCapacitacao() {
        return moduloCapacitacao;
    }

    public void setModuloCapacitacao(ModuloCapacitacao moduloCapacitacao) {
        this.moduloCapacitacao = moduloCapacitacao;
    }

    public InscricaoCapacitacao getInscricaoCapacitacao() {
        return inscricaoCapacitacao;
    }

    public void setInscricaoCapacitacao(InscricaoCapacitacao inscricaoCapacitacao) {
        this.inscricaoCapacitacao = inscricaoCapacitacao;
    }

    public Boolean getPresente() {
        return presente;
    }

    public void setPresente(Boolean presente) {
        this.presente = presente;
    }

    public Date getDia() {
        return dia;
    }

    public void setDia(Date dia) {
        this.dia = dia;
    }
}
