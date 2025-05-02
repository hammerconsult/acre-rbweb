package br.com.webpublico.ws.model;

import java.math.BigDecimal;
import java.util.Date;

public class VinculoFPDTODetalhe extends WSVinculoFP {

    private Date admissao;
    private String nome;
    private String orgao;
    private String lotacao;

    private BigDecimal idPessoa;
    private String cpf;
    private String naturalidade;
    private Date dataNascimeto;
    private String rg;
    private String tituloEleitor;
    private String mae;
    private String pai;
    private String qrCode;
    private String foto;
    private BigDecimal idCargo;
    private String codigoCargo;
    private String cargo;

    private BigDecimal idLotacao;
    private String codigoLotacao;
    private String situacaoVinculo;
    private String situacaoFuncional;

    private BigDecimal horasSemanais;
    private BigDecimal horasMensais;

    private HorarioTrabalhoDTO horarioTrabalho;


    public VinculoFPDTODetalhe() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getOrgao() {
        return orgao;
    }

    public void setOrgao(String orgao) {
        this.orgao = orgao;
    }

    public String getLotacao() {
        return lotacao;
    }

    public void setLotacao(String lotacao) {
        this.lotacao = lotacao;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNaturalidade() {
        return naturalidade;
    }

    public void setNaturalidade(String naturalidade) {
        this.naturalidade = naturalidade;
    }

    public Date getDataNascimeto() {
        return dataNascimeto;
    }

    public void setDataNascimeto(Date dataNascimeto) {
        this.dataNascimeto = dataNascimeto;
    }

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public String getTituloEleitor() {
        return tituloEleitor;
    }

    public void setTituloEleitor(String tituloEleitor) {
        this.tituloEleitor = tituloEleitor;
    }

    public String getMae() {
        return mae;
    }

    public void setMae(String mae) {
        this.mae = mae;
    }

    public String getPai() {
        return pai;
    }

    public void setPai(String pai) {
        this.pai = pai;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public Date getAdmissao() {
        return admissao;
    }

    public void setAdmissao(Date admissao) {
        this.admissao = admissao;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getCodigoCargo() {
        return codigoCargo;
    }

    public void setCodigoCargo(String codigoCargo) {
        this.codigoCargo = codigoCargo;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public BigDecimal getIdCargo() {
        return idCargo;
    }

    public void setIdCargo(BigDecimal idCargo) {
        this.idCargo = idCargo;
    }

    public BigDecimal getIdLotacao() {
        return idLotacao;
    }

    public void setIdLotacao(BigDecimal idLotacao) {
        this.idLotacao = idLotacao;
    }

    public String getCodigoLotacao() {
        return codigoLotacao;
    }

    public void setCodigoLotacao(String codigoLotacao) {
        this.codigoLotacao = codigoLotacao;
    }


    public String getSituacaoVinculo() {
        return situacaoVinculo;
    }

    public void setSituacaoVinculo(String situacaoVinculo) {
        this.situacaoVinculo = situacaoVinculo;
    }

    public String getSituacaoFuncional() {
        return situacaoFuncional;
    }

    public void setSituacaoFuncional(String situacaoFuncional) {
        this.situacaoFuncional = situacaoFuncional;
    }

    public BigDecimal getHorasSemanais() {
        return horasSemanais;
    }

    public void setHorasSemanais(BigDecimal horasSemanais) {
        this.horasSemanais = horasSemanais;
    }

    public BigDecimal getHorasMensais() {
        return horasMensais;
    }

    public void setHorasMensais(BigDecimal horasMensais) {
        this.horasMensais = horasMensais;
    }

    public HorarioTrabalhoDTO getHorarioTrabalho() {
        return horarioTrabalho;
    }

    public void setHorarioTrabalho(HorarioTrabalhoDTO horarioTrabalho) {
        this.horarioTrabalho = horarioTrabalho;
    }

    public BigDecimal getIdPessoa() {
        return idPessoa;
    }

    public void setIdPessoa(BigDecimal idPessoa) {
        this.idPessoa = idPessoa;
    }
}
