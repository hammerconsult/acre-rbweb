/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoObjetoFrota;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Felipe Marinzeck
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Frota")
@Etiqueta("Manutenções")
public class ManutencaoObjetoFrota implements Serializable, PossuidorArquivo {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Transient
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo")
    @Enumerated(EnumType.STRING)
    private TipoObjetoFrota tipoObjetoFrota;

    @OneToOne
    @Obrigatorio
    @Tabelavel
    @Etiqueta(value = "Veículo/Equipamento")
    @Pesquisavel
    private ObjetoFrota objetoFrota;

    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Etiqueta("Data de Manutenção")
    @Obrigatorio
    @Pesquisavel
    private Date realizadaEm;

    @Etiqueta("Descrição")
    @Obrigatorio
    @Pesquisavel
    private String descricao;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "manutencaoObjetoFrota")
    @Etiqueta("Lubrificação")
    @Tabelavel
    @Pesquisavel
    private ManutencaoObjLubrificacao manutencaoObjLubrificacao;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "manutencaoObjetoFrota")
    @Etiqueta("Peça(s) Instalada(s)")
    private List<PecaInstalada> pecaInstalada;

    @Invisivel
    @Transient
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;


    public ManutencaoObjetoFrota(ManutencaoObjetoFrota manutencaoObjetoFrota, HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.setId(manutencaoObjetoFrota.getId());
        this.setTipoObjetoFrota(manutencaoObjetoFrota.getTipoObjetoFrota());
        this.setObjetoFrota(manutencaoObjetoFrota.getObjetoFrota());
        this.setRealizadaEm(manutencaoObjetoFrota.getRealizadaEm());
        this.setManutencaoObjLubrificacao(manutencaoObjetoFrota.getManutencaoObjLubrificacao());
        this.setHierarquiaOrganizacional(hierarquiaOrganizacional);
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public ManutencaoObjetoFrota() {
        dataRegistro = new Date();
    }

    public TipoObjetoFrota getTipoObjetoFrota() {
        return tipoObjetoFrota;
    }

    public void setTipoObjetoFrota(TipoObjetoFrota tipoObjetoFrota) {
        this.tipoObjetoFrota = tipoObjetoFrota;
    }

    public ObjetoFrota getObjetoFrota() {
        return objetoFrota;
    }

    public void setObjetoFrota(ObjetoFrota objetoFrota) {
        this.objetoFrota = objetoFrota;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public ManutencaoObjLubrificacao getManutencaoObjLubrificacao() {
        return manutencaoObjLubrificacao;
    }

    public void setManutencaoObjLubrificacao(ManutencaoObjLubrificacao manutencaoObjLubrificacao) {
        this.manutencaoObjLubrificacao = manutencaoObjLubrificacao;
    }

    public List<PecaInstalada> getPecaInstalada() {
        return pecaInstalada;
    }

    public void setPecaInstalada(List<PecaInstalada> pecaInstalada) {
        this.pecaInstalada = pecaInstalada;
    }

    public Date getRealizadaEm() {
        return realizadaEm;
    }

    public void setRealizadaEm(Date realizadaEm) {
        this.realizadaEm = realizadaEm;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ManutencaoObjetoFrota)) {
            return false;
        }
        ManutencaoObjetoFrota other = (ManutencaoObjetoFrota) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)) || (this.dataRegistro == null && other.dataRegistro != null) || (this.dataRegistro != null && !this.dataRegistro.equals(other.dataRegistro))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        if (realizadaEm != null) {
            return "Manutenção realizada em " + new SimpleDateFormat("dd/MM/yyyy").format(realizadaEm);
        }
        return "";
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }
}
