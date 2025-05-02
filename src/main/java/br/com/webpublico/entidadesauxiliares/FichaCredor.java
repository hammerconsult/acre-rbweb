package br.com.webpublico.entidadesauxiliares;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class FichaCredor {
    private String cpf;
    private Date datanascimento;
    private Boolean deficienteFisico;
    private Boolean doadorDeSangue;
    private String estadoCivil;
    private String mae;
    private String nome;
    private String pai;
    private String racaCor;
    private String sexo;
    private String tipoSanguineo;
    private String tipoDeficiencia;
    private BigDecimal anoChegada;
    private Date dataInvalidez;
    private String nomeAbreviado;
    private String nomeTratamento;
    private Date dataRegistro;
    private String email;
    private String homePage;
    private String situacaoCadastralPessoa;
    private String motivo;
    private Boolean bloqueado;
    private String classePessoa;
    private String observacao;
    private String nivelEscolaridade;
    private String unidade;
    private String nomeEstado;
    private String nomeCidade;
    private String nacionalidade;
    private BigDecimal idPessoa;
    private List<FichaCredorRG> fichaCredorRGS;
    private List<FichaCredorTituloEleitor> fichaCredorTituloEleitores;
    private List<FichaCredorCertidaoNascimento> fichaCredorCertidaoNascimentos;
    private List<FichaCredorCertidaoCasamento> fichaCredorCertidaoCasamentos;
    private List<FichaCredorClassePessoa> fichaCredorClassePessoas;
    private List<FichaCredorCNH> fichaCredorCNHS;
    private List<FichaCredorCarteiraTrabalho> fichaCredorCarteiraTrabalhos;
    private List<FichaCredorSituacaoMilitar> fichaCredorSituacaoMilitares;
    private List<FichaCredorTelefone> fichaCredorTelefones;
    private List<FichaCredorEndereco> fichaCredorEnderecos;
    private List<FichaCredorContaCBanc> fichaCredorContaCBancs;

    public FichaCredor() {
        fichaCredorRGS = Lists.newArrayList();
        fichaCredorTituloEleitores = Lists.newArrayList();
        fichaCredorCertidaoNascimentos = Lists.newArrayList();
        fichaCredorCertidaoCasamentos = Lists.newArrayList();
        fichaCredorClassePessoas = Lists.newArrayList();
        fichaCredorCNHS = Lists.newArrayList();
        fichaCredorCarteiraTrabalhos = Lists.newArrayList();
        fichaCredorSituacaoMilitares = Lists.newArrayList();
        fichaCredorTelefones = Lists.newArrayList();
        fichaCredorEnderecos = Lists.newArrayList();
        fichaCredorContaCBancs = Lists.newArrayList();
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Date getDatanascimento() {
        return datanascimento;
    }

    public void setDatanascimento(Date datanascimento) {
        this.datanascimento = datanascimento;
    }

    public Boolean getDeficienteFisico() {
        return deficienteFisico;
    }

    public void setDeficienteFisico(Boolean deficienteFisico) {
        this.deficienteFisico = deficienteFisico;
    }

    public Boolean getDoadorDeSangue() {
        return doadorDeSangue;
    }

    public void setDoadorDeSangue(Boolean doadorDeSangue) {
        this.doadorDeSangue = doadorDeSangue;
    }

    public String getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(String estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public String getMae() {
        return mae;
    }

    public void setMae(String mae) {
        this.mae = mae;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPai() {
        return pai;
    }

    public void setPai(String pai) {
        this.pai = pai;
    }

    public String getRacaCor() {
        return racaCor;
    }

    public void setRacaCor(String racaCor) {
        this.racaCor = racaCor;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getTipoSanguineo() {
        return tipoSanguineo;
    }

    public void setTipoSanguineo(String tipoSanguineo) {
        this.tipoSanguineo = tipoSanguineo;
    }

    public String getTipoDeficiencia() {
        return tipoDeficiencia;
    }

    public void setTipoDeficiencia(String tipoDeficiencia) {
        this.tipoDeficiencia = tipoDeficiencia;
    }

    public BigDecimal getAnoChegada() {
        return anoChegada;
    }

    public void setAnoChegada(BigDecimal anoChegada) {
        this.anoChegada = anoChegada;
    }

    public Date getDataInvalidez() {
        return dataInvalidez;
    }

    public void setDataInvalidez(Date dataInvalidez) {
        this.dataInvalidez = dataInvalidez;
    }

    public String getNomeAbreviado() {
        return nomeAbreviado;
    }

    public void setNomeAbreviado(String nomeAbreviado) {
        this.nomeAbreviado = nomeAbreviado;
    }

    public String getNomeTratamento() {
        return nomeTratamento;
    }

    public void setNomeTratamento(String nomeTratamento) {
        this.nomeTratamento = nomeTratamento;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public String getSituacaoCadastralPessoa() {
        return situacaoCadastralPessoa;
    }

    public void setSituacaoCadastralPessoa(String situacaoCadastralPessoa) {
        this.situacaoCadastralPessoa = situacaoCadastralPessoa;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Boolean getBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(Boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public String getClassePessoa() {
        return classePessoa;
    }

    public void setClassePessoa(String classePessoa) {
        this.classePessoa = classePessoa;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getNivelEscolaridade() {
        return nivelEscolaridade;
    }

    public void setNivelEscolaridade(String nivelEscolaridade) {
        this.nivelEscolaridade = nivelEscolaridade;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public String getNomeEstado() {
        return nomeEstado;
    }

    public void setNomeEstado(String nomeEstado) {
        this.nomeEstado = nomeEstado;
    }

    public String getNomeCidade() {
        return nomeCidade;
    }

    public void setNomeCidade(String nomeCidade) {
        this.nomeCidade = nomeCidade;
    }

    public String getNacionalidade() {
        return nacionalidade;
    }

    public void setNacionalidade(String nacionalidade) {
        this.nacionalidade = nacionalidade;
    }

    public BigDecimal getIdPessoa() {
        return idPessoa;
    }

    public void setIdPessoa(BigDecimal idPessoa) {
        this.idPessoa = idPessoa;
    }

    public List<FichaCredorRG> getFichaCredorRGS() {
        return fichaCredorRGS;
    }

    public void setFichaCredorRGS(List<FichaCredorRG> fichaCredorRGS) {
        this.fichaCredorRGS = fichaCredorRGS;
    }

    public List<FichaCredorTituloEleitor> getFichaCredorTituloEleitores() {
        return fichaCredorTituloEleitores;
    }

    public void setFichaCredorTituloEleitores(List<FichaCredorTituloEleitor> fichaCredorTituloEleitores) {
        this.fichaCredorTituloEleitores = fichaCredorTituloEleitores;
    }

    public List<FichaCredorCertidaoNascimento> getFichaCredorCertidaoNascimentos() {
        return fichaCredorCertidaoNascimentos;
    }

    public void setFichaCredorCertidaoNascimentos(List<FichaCredorCertidaoNascimento> fichaCredorCertidaoNascimentos) {
        this.fichaCredorCertidaoNascimentos = fichaCredorCertidaoNascimentos;
    }

    public List<FichaCredorCertidaoCasamento> getFichaCredorCertidaoCasamentos() {
        return fichaCredorCertidaoCasamentos;
    }

    public void setFichaCredorCertidaoCasamentos(List<FichaCredorCertidaoCasamento> fichaCredorCertidaoCasamentos) {
        this.fichaCredorCertidaoCasamentos = fichaCredorCertidaoCasamentos;
    }

    public List<FichaCredorClassePessoa> getFichaCredorClassePessoas() {
        return fichaCredorClassePessoas;
    }

    public void setFichaCredorClassePessoas(List<FichaCredorClassePessoa> fichaCredorClassePessoas) {
        this.fichaCredorClassePessoas = fichaCredorClassePessoas;
    }

    public List<FichaCredorCNH> getFichaCredorCNHS() {
        return fichaCredorCNHS;
    }

    public void setFichaCredorCNHS(List<FichaCredorCNH> fichaCredorCNHS) {
        this.fichaCredorCNHS = fichaCredorCNHS;
    }

    public List<FichaCredorCarteiraTrabalho> getFichaCredorCarteiraTrabalhos() {
        return fichaCredorCarteiraTrabalhos;
    }

    public void setFichaCredorCarteiraTrabalhos(List<FichaCredorCarteiraTrabalho> fichaCredorCarteiraTrabalhos) {
        this.fichaCredorCarteiraTrabalhos = fichaCredorCarteiraTrabalhos;
    }

    public List<FichaCredorSituacaoMilitar> getFichaCredorSituacaoMilitares() {
        return fichaCredorSituacaoMilitares;
    }

    public void setFichaCredorSituacaoMilitares(List<FichaCredorSituacaoMilitar> fichaCredorSituacaoMilitares) {
        this.fichaCredorSituacaoMilitares = fichaCredorSituacaoMilitares;
    }

    public List<FichaCredorTelefone> getFichaCredorTelefones() {
        return fichaCredorTelefones;
    }

    public void setFichaCredorTelefones(List<FichaCredorTelefone> fichaCredorTelefones) {
        this.fichaCredorTelefones = fichaCredorTelefones;
    }

    public List<FichaCredorEndereco> getFichaCredorEnderecos() {
        return fichaCredorEnderecos;
    }

    public void setFichaCredorEnderecos(List<FichaCredorEndereco> fichaCredorEnderecos) {
        this.fichaCredorEnderecos = fichaCredorEnderecos;
    }

    public List<FichaCredorContaCBanc> getFichaCredorContaCBancs() {
        return fichaCredorContaCBancs;
    }

    public void setFichaCredorContaCBancs(List<FichaCredorContaCBanc> fichaCredorContaCBancs) {
        this.fichaCredorContaCBancs = fichaCredorContaCBancs;
    }
}
