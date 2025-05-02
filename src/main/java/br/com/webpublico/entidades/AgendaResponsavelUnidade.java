/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.entidades;

import br.com.webpublico.enums.FuncaoResponsavelUnidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Felipe Marinzeck
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Administrativo")
@Etiqueta("Agenda do Responsável por Unidade")
public class AgendaResponsavelUnidade extends SuperEntidade implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    private Long id;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Responsável")
    @Pesquisavel
    @Obrigatorio
    private ResponsavelUnidade responsavel;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private Date data;
    @Tabelavel
    @Temporal(TemporalType.TIME)
    @Obrigatorio
    @Etiqueta("Horário")
    @Pesquisavel
    private Date horario;
    @Obrigatorio
    @Etiqueta("Compromisso")
    @Pesquisavel
    private String compromisso;
    @Obrigatorio
    @Etiqueta("Local")
    @Pesquisavel
    private String local;

    public AgendaResponsavelUnidade() {

    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ResponsavelUnidade getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(ResponsavelUnidade responsavel) {
        this.responsavel = responsavel;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Date getHorario() {
        return horario;
    }

    public void setHorario(Date horario) {
        this.horario = horario;
    }

    public String getCompromisso() {
        return compromisso;
    }

    public void setCompromisso(String compromisso) {
        this.compromisso = compromisso;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }
}
