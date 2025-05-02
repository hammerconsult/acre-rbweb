/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.ParametrosRelatorioConferenciaCreditoSalario;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import br.com.webpublico.webreportdto.dto.rh.GrupoRecursoFPDTO;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

/**
 * @author Claudio
 */
@GrupoDiagrama(nome = "Recursos Humanos")
@Entity
@Audited
@Etiqueta("Agrupamento de Recursos FP")
public class GrupoRecursoFP extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    @Invisivel
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Nome do Grupo")
    @Obrigatorio
    private String nome;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Nome da Empresa")
    @Obrigatorio
    private String nomeEmpresa;
    @Etiqueta("Hierarquia Organizacional")
    @ManyToOne
    @Tabelavel
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "grupoRecursoFP", orphanRemoval = true)
    private List<AgrupamentoRecursoFP> agrupamentoRecursoFP;
    @Transient
    @Invisivel
    private Long criadoEm;
    @Transient
    private ParametrosRelatorioConferenciaCreditoSalario parametrosRelatorioConferenciaCreditoSalario;
    @Transient
    @Invisivel
    private Boolean selecionado;
    private Boolean fundo;
    @Transient
    @Invisivel
    private Boolean hierarquiaVigente;

    public GrupoRecursoFP() {
        this.criadoEm = System.nanoTime();
        parametrosRelatorioConferenciaCreditoSalario = new ParametrosRelatorioConferenciaCreditoSalario();
        selecionado = Boolean.FALSE;
    }

    public static List<GrupoRecursoFPDTO> toDtos(List<GrupoRecursoFP> grupos) {
        List<GrupoRecursoFPDTO> gruposRecursoFPDTOs = Lists.newArrayList();
        for (GrupoRecursoFP grupo : grupos) {
            gruposRecursoFPDTOs.add(toDto(grupo));
        }
        return gruposRecursoFPDTOs;
    }

    private static GrupoRecursoFPDTO toDto(GrupoRecursoFP grupo) {
        GrupoRecursoFPDTO grupoRecursoFPDTO = new GrupoRecursoFPDTO();
        grupoRecursoFPDTO.setId(grupo.getId());
        grupoRecursoFPDTO.setNome(grupo.getNome());
        return grupoRecursoFPDTO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    public void setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
    }

    public List<AgrupamentoRecursoFP> getAgrupamentoRecursoFP() {
        return agrupamentoRecursoFP;
    }

    public void setAgrupamentoRecursoFP(List<AgrupamentoRecursoFP> agrupamentoRecursoFP) {
        this.agrupamentoRecursoFP = agrupamentoRecursoFP;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public ParametrosRelatorioConferenciaCreditoSalario getParametrosRelatorioConferenciaCreditoSalario() {
        return parametrosRelatorioConferenciaCreditoSalario;
    }

    public void setParametrosRelatorioConferenciaCreditoSalario(ParametrosRelatorioConferenciaCreditoSalario parametrosRelatorioConferenciaCreditoSalario) {
        this.parametrosRelatorioConferenciaCreditoSalario = parametrosRelatorioConferenciaCreditoSalario;
    }

    public Boolean getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Boolean selecionado) {
        this.selecionado = selecionado;
    }

    public Boolean estaSelecionado() {
        return selecionado != null ? selecionado : false;
    }

    public Boolean getFundo() {
        return fundo != null ? fundo : false;
    }

    public void setFundo(Boolean fundo) {
        this.fundo = fundo;
    }

    public Boolean getHierarquiaVigente() {
        return hierarquiaVigente == null ? false : hierarquiaVigente;
    }

    public void setHierarquiaVigente(Boolean hierarquiaVigente) {
        this.hierarquiaVigente = hierarquiaVigente;
    }

    @Override
    public String toString() {
        return nome + " - " + nomeEmpresa;
    }
}
