package br.com.webpublico.entidadesauxiliares.rh;

import java.util.Date;

public class FichaFinanceiraFPCamposVO {
    private Date dataNascimento;
    private Date admissao;
    private Date alteracaoVinculo;
    private String cpf;
    private String nome;
    private String matricula;
    private String cargo;
    private String unidade;
    private String instituidor;
    private String matriculaInstituidor;
    private String rg;
    private String fundamentacao;
    private String modalidaDeContrato;
    private String lotacaoFolha;

    public FichaFinanceiraFPCamposVO() {
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Date getAdmissao() {
        return admissao;
    }

    public void setAdmissao(Date admissao) {
        this.admissao = admissao;
    }

    public Date getAlteracaoVinculo() {
        return alteracaoVinculo;
    }

    public void setAlteracaoVinculo(Date alteracaoVinculo) {
        this.alteracaoVinculo = alteracaoVinculo;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
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

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public String getInstituidor() {
        return instituidor;
    }

    public void setInstituidor(String instituidor) {
        this.instituidor = instituidor;
    }

    public String getMatriculaInstituidor() {
        return matriculaInstituidor;
    }

    public void setMatriculaInstituidor(String matriculaInstituidor) {
        this.matriculaInstituidor = matriculaInstituidor;
    }

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public String getFundamentacao() {
        return fundamentacao;
    }

    public void setFundamentacao(String fundamentacao) {
        this.fundamentacao = fundamentacao;
    }

    public String getModalidaDeContrato() {
        return modalidaDeContrato;
    }

    public void setModalidaDeContrato(String modalidaDeContrato) {
        this.modalidaDeContrato = modalidaDeContrato;
    }

    public String getLotacaoFolha() {
        return lotacaoFolha;
    }

    public void setLotacaoFolha(String lotacaoFolha) {
        this.lotacaoFolha = lotacaoFolha;
    }
}
