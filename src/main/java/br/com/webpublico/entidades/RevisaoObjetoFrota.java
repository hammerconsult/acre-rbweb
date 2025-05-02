/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoObjetoFrota;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Felipe Marinzeck
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Frota")
@Etiqueta("Revisão")
public class RevisaoObjetoFrota extends SuperEntidade implements PossuidorArquivo {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Transient
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo de Objeto")
    @Enumerated(EnumType.STRING)
    private TipoObjetoFrota tipoObjetoFrota;

    @OneToOne
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Veículo/Equipamento")
    private ObjetoFrota objetoFrota;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;

    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data da Realização")
    @Tabelavel
    @Obrigatorio
    private Date realizadoEm;

    @ManyToOne
    @Etiqueta("Realizada por")
    @Tabelavel
    @Obrigatorio
    private Pessoa revisadoPor;

    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Programada Para")
    @Tabelavel
    private Date programadaPara;

    @Etiqueta("Km Realizada")
    @Tabelavel
    private Integer kmRealizada;

    @Etiqueta("Km Programada")
    @Tabelavel
    private Integer kmProgramada;

    @ManyToOne
    private ManutencaoObjetoFrota manutencaoObjetoFrota;

    @OneToOne(cascade = CascadeType.ALL)
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @ManyToOne
    @Etiqueta("Contrato")
    private Contrato contrato;

    @ManyToOne
    @Etiqueta("Programa de Revisão do Veículo")
    private ProgramaRevisaoVeiculo programaRevisaoVeiculo;

    @ManyToOne
    @Etiqueta("Programa de Revisão do Equipamento")
    private ProgramaRevisaoEquipamento programaRevisaoEquipamento;

    public RevisaoObjetoFrota() {
    }

    public RevisaoObjetoFrota(RevisaoObjetoFrota revisaoObjetoFrota, HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.setId(revisaoObjetoFrota.getId());
        this.setDescricao(revisaoObjetoFrota.getDescricao());
        this.setTipoObjetoFrota(revisaoObjetoFrota.getTipoObjetoFrota());
        this.setObjetoFrota(revisaoObjetoFrota.getObjetoFrota());
        this.setRevisadoPor(revisaoObjetoFrota.getRevisadoPor());
        this.setRealizadoEm(revisaoObjetoFrota.getRealizadoEm());
        this.setProgramadaPara(revisaoObjetoFrota.getProgramadaPara());
        this.setKmRealizada(revisaoObjetoFrota.getKmRealizada());
        this.setKmProgramada(revisaoObjetoFrota.getKmProgramada());
        this.setManutencaoObjetoFrota(revisaoObjetoFrota.getManutencaoObjetoFrota());
        this.setHierarquiaOrganizacional(hierarquiaOrganizacional);
    }

    public ProgramaRevisaoVeiculo getProgramaRevisaoVeiculo() {
        return programaRevisaoVeiculo;
    }

    public void setProgramaRevisaoVeiculo(ProgramaRevisaoVeiculo programaRevisaoVeiculo) {
        this.programaRevisaoVeiculo = programaRevisaoVeiculo;
    }

    public ProgramaRevisaoEquipamento getProgramaRevisaoEquipamento() {
        return programaRevisaoEquipamento;
    }

    public void setProgramaRevisaoEquipamento(ProgramaRevisaoEquipamento programaRevisaoEquipamento) {
        this.programaRevisaoEquipamento = programaRevisaoEquipamento;
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

    public Integer getKmProgramada() {
        return kmProgramada;
    }

    public void setKmProgramada(Integer kmProgramada) {
        this.kmProgramada = kmProgramada;
    }

    public Integer getKmRealizada() {
        return kmRealizada;
    }

    public void setKmRealizada(Integer kmRealizada) {
        this.kmRealizada = kmRealizada;
    }

    public Date getProgramadaPara() {
        return programadaPara;
    }

    public void setProgramadaPara(Date programadaPara) {
        this.programadaPara = programadaPara;
    }

    public Date getRealizadoEm() {
        return realizadoEm;
    }

    public void setRealizadoEm(Date realizadoEm) {
        this.realizadoEm = realizadoEm;
    }

    public Pessoa getRevisadoPor() {
        return revisadoPor;
    }

    public void setRevisadoPor(Pessoa revisadoPor) {
        this.revisadoPor = revisadoPor;
    }

    public ObjetoFrota getObjetoFrota() {
        return objetoFrota;
    }

    public void setObjetoFrota(ObjetoFrota objetoFrota) {
        this.objetoFrota = objetoFrota;
    }

    public TipoObjetoFrota getTipoObjetoFrota() {
        return tipoObjetoFrota;
    }

    public void setTipoObjetoFrota(TipoObjetoFrota tipoObjetoFrota) {
        this.tipoObjetoFrota = tipoObjetoFrota;
    }

    public ManutencaoObjetoFrota getManutencaoObjetoFrota() {
        return manutencaoObjetoFrota;
    }

    public void setManutencaoObjetoFrota(ManutencaoObjetoFrota manutencaoObjetoFrota) {
        this.manutencaoObjetoFrota = manutencaoObjetoFrota;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof RevisaoObjetoFrota)) {
            return false;
        }
        RevisaoObjetoFrota other = (RevisaoObjetoFrota) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return id + " - " + descricao;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }
}
