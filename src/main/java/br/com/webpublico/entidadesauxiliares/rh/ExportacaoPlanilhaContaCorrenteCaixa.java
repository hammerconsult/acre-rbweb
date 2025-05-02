package br.com.webpublico.entidadesauxiliares.rh;

import br.com.webpublico.entidades.EnderecoCorreio;
import br.com.webpublico.entidades.RG;
import br.com.webpublico.entidades.Telefone;
import br.com.webpublico.enums.EstadoCivil;
import br.com.webpublico.enums.GrauInstrucaoCAGED;
import br.com.webpublico.enums.Sexo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author peixe on 31/03/2017  14:04.
 */
public class ExportacaoPlanilhaContaCorrenteCaixa {
    private String nomeCompleto;
    private String nomeReduzido; //32 caracteres
    private String cpf;
    private String pis;
    private Date nascimento;
    private String localNascimento;
    private String ufNascimento;
    private EstadoCivil estadoCivil; //todos como solteiro 1.
    private String nomeConjuge;
    private String nomePai;
    private String nomeMae;
    private Sexo sexo;
    private RG rg; //DOC(RG) NUMERO	- Orgão emissor do RG -	UFdo RG - DOC(Data de emissão)
    private String ocupacao;
    private Date dataAdmissao;
    private EnderecoCorreio enderecoCorreio; //rua e numero, - Bairro - Cidade - UF - CEP
    private Telefone telefone; //DDD -  TELEFONE
    private String email;
    private GrauInstrucaoCAGED grauInstrucao;
    private BigDecimal rendaValor;

    public ExportacaoPlanilhaContaCorrenteCaixa() {
    }


    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getNomeReduzido() {
        return nomeReduzido;
    }

    public void setNomeReduzido(String nomeReduzido) {
        this.nomeReduzido = nomeReduzido;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getPis() {
        return pis;
    }

    public void setPis(String pis) {
        this.pis = pis;
    }

    public Date getNascimento() {
        return nascimento;
    }

    public void setNascimento(Date nascimento) {
        this.nascimento = nascimento;
    }

    public String getLocalNascimento() {
        return localNascimento;
    }

    public void setLocalNascimento(String localNascimento) {
        this.localNascimento = localNascimento;
    }

    public String getUfNascimento() {
        return ufNascimento;
    }

    public void setUfNascimento(String ufNascimento) {
        this.ufNascimento = ufNascimento;
    }

    public EstadoCivil getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(EstadoCivil estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public String getNomeConjuge() {
        return nomeConjuge;
    }

    public void setNomeConjuge(String nomeConjuge) {
        this.nomeConjuge = nomeConjuge;
    }

    public String getNomePai() {
        return nomePai;
    }

    public void setNomePai(String nomePai) {
        this.nomePai = nomePai;
    }

    public String getNomeMae() {
        return nomeMae;
    }

    public void setNomeMae(String nomeMae) {
        this.nomeMae = nomeMae;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public RG getRg() {
        return rg;
    }

    public void setRg(RG rg) {
        this.rg = rg;
    }

    public String getOcupacao() {
        return ocupacao;
    }

    public void setOcupacao(String ocupacao) {
        this.ocupacao = ocupacao;
    }

    public Date getDataAdmissao() {
        return dataAdmissao;
    }

    public void setDataAdmissao(Date dataAdmissao) {
        this.dataAdmissao = dataAdmissao;
    }

    public EnderecoCorreio getEnderecoCorreio() {
        return enderecoCorreio;
    }

    public void setEnderecoCorreio(EnderecoCorreio enderecoCorreio) {
        this.enderecoCorreio = enderecoCorreio;
    }

    public Telefone getTelefone() {
        return telefone;
    }

    public void setTelefone(Telefone telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public GrauInstrucaoCAGED getGrauInstrucao() {
        return grauInstrucao;
    }

    public void setGrauInstrucao(GrauInstrucaoCAGED grauInstrucao) {
        this.grauInstrucao = grauInstrucao;
    }

    public BigDecimal getRendaValor() {
        return rendaValor;
    }

    public void setRendaValor(BigDecimal rendaValor) {
        this.rendaValor = rendaValor;
    }
}
