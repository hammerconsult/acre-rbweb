/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
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
@GrupoDiagrama(nome = "Frota")
@Etiqueta("Reserva")

public class ReservaObjetoFrota extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Transient
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    @Tabelavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Realizada em")
    @Obrigatorio
    @Pesquisavel
    private Date realizadaEm;

    @Tabelavel
    @Etiqueta("Solicitação")
    @Pesquisavel
    @Obrigatorio
    @OneToOne
    private SolicitacaoObjetoFrota solicitacaoObjetoFrota;

    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Veículo/Equipamento")
    private ObjetoFrota objetoFrota;

    @Length(maximo = 3000)
    @Etiqueta("Observações")
    private String observacoes;

    public ReservaObjetoFrota() {
        super();
    }

    public ReservaObjetoFrota(ReservaObjetoFrota reservaObjetoFrota, HierarquiaOrganizacional hierarquiaOrganizacional) {
        super();
        setId(reservaObjetoFrota.getId());
        setRealizadaEm(reservaObjetoFrota.getRealizadaEm());
        setSolicitacaoObjetoFrota(reservaObjetoFrota.getSolicitacaoObjetoFrota());
        setHierarquiaOrganizacional(hierarquiaOrganizacional);
        setObservacoes(reservaObjetoFrota.getObservacoes());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public Date getRealizadaEm() {
        return realizadaEm;
    }

    public void setRealizadaEm(Date realizadaEm) {
        this.realizadaEm = realizadaEm;
    }

    public SolicitacaoObjetoFrota getSolicitacaoObjetoFrota() {
        return solicitacaoObjetoFrota;
    }

    public void setSolicitacaoObjetoFrota(SolicitacaoObjetoFrota solicitacaoObjetoFrota) {
        this.solicitacaoObjetoFrota = solicitacaoObjetoFrota;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public ObjetoFrota getObjetoFrota() {
        return objetoFrota;
    }

    public void setObjetoFrota(ObjetoFrota objetoFrota) {
        this.objetoFrota = objetoFrota;
    }

    @Override
    public String toString() {
        return  " realizada em " + DataUtil.getDataFormatada(realizadaEm) + " - " + objetoFrota;
    }
}
