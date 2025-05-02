package br.com.webpublico.entidadesauxiliares.rh;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by fox_m on 06/04/2016.
 */
public class FolhaPorSecretariaPrincipal {
    private String codigoHierarquia;
    private String descricaoHierarquia;
    private String codigoRecurso;
    private String descricaoRecurso;
    private String nome;
    private String cpf;
    private String matricula;
    private String contrato;
    private String situacao;
    private String cargo;
    private Date admissao;
    private String vinculoEmpregaticio;
    private Long idFicha;
    private List<FolhaPorSecretariaPrincipal> itens;
    private List<TermoRescisaoVerba> verbas;
    private List<FolhaPorSecretariaSummary> subReportGrupo;
    private Integer totalServidoresGrupo = 0;

    public FolhaPorSecretariaPrincipal() {
        verbas = new ArrayList<>();
        itens = new ArrayList<>();
        subReportGrupo = new ArrayList<>();
    }

    public String getDescricaoHierarquia() {
        return descricaoHierarquia;
    }

    public void setDescricaoHierarquia(String descricaoHierarquia) {
        this.descricaoHierarquia = descricaoHierarquia;
    }

    public String getCodigoRecurso() {
        return codigoRecurso;
    }

    public void setCodigoRecurso(String codigoRecurso) {
        this.codigoRecurso = codigoRecurso;
    }

    public String getDescricaoRecurso() {
        return descricaoRecurso;
    }

    public void setDescricaoRecurso(String descricaoRecurso) {
        this.descricaoRecurso = descricaoRecurso;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getContrato() {
        return contrato;
    }

    public void setContrato(String contrato) {
        this.contrato = contrato;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getVinculoEmpregaticio() {
        return vinculoEmpregaticio;
    }

    public void setVinculoEmpregaticio(String vinculoEmpregaticio) {
        this.vinculoEmpregaticio = vinculoEmpregaticio;
    }

    public Long getIdFicha() {
        return idFicha;
    }

    public void setIdFicha(Long idFicha) {
        this.idFicha = idFicha;
    }

    public Date getAdmissao() {
        return admissao;
    }

    public void setAdmissao(Date admissao) {
        this.admissao = admissao;
    }

    public List<TermoRescisaoVerba> getVerbas() {
        return verbas;
    }

    public void setVerbas(List<TermoRescisaoVerba> verbas) {
        this.verbas = verbas;
    }

    public List<FolhaPorSecretariaSummary> getSubReportGrupo() {
        return subReportGrupo;
    }

    public void setSubReportGrupo(List<FolhaPorSecretariaSummary> subReportGrupo) {
        this.subReportGrupo = subReportGrupo;
    }

    public List<FolhaPorSecretariaPrincipal> getItens() {
        return itens;
    }

    public void setItens(List<FolhaPorSecretariaPrincipal> itens) {
        this.itens = itens;
    }

    public String getCodigoHierarquia() {
        return codigoHierarquia;
    }

    public void setCodigoHierarquia(String codigoHierarquia) {
        this.codigoHierarquia = codigoHierarquia;
    }

    public Integer getTotalServidoresGrupo() {
        return totalServidoresGrupo;
    }

    public void setTotalServidoresGrupo(Integer totalServidoresGrupo) {
        this.totalServidoresGrupo = totalServidoresGrupo;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
}
