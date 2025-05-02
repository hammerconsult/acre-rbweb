package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Criado por Mateus
 * Data: 15/05/2017.
 */
public class DemonstrativoAuxilioFuneral {

    private String nomeFalecido;
    private String cpf;
    private String rg;
    private Date dataNascimento;
    private String idade;
    private String endereco;
    private String procedencia;
    private Date dataAtendimento;
    private String beneficios;
    private String numeroDeclaracaoObito;
    private Date dataFalecimento;
    private BigDecimal rendaBruta;
    private BigDecimal rendaPerCapita;
    private String funeraria;
    private String cemiterio;

    public DemonstrativoAuxilioFuneral() {
        rendaBruta = BigDecimal.ZERO;
        rendaPerCapita = BigDecimal.ZERO;
    }

    public String getNomeFalecido() {
        return nomeFalecido;
    }

    public void setNomeFalecido(String nomeFalecido) {
        this.nomeFalecido = nomeFalecido;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getIdade() {
        return idade;
    }

    public void setIdade(String idade) {
        this.idade = idade;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getProcedencia() {
        return procedencia;
    }

    public void setProcedencia(String procedencia) {
        this.procedencia = procedencia;
    }

    public Date getDataAtendimento() {
        return dataAtendimento;
    }

    public void setDataAtendimento(Date dataAtendimento) {
        this.dataAtendimento = dataAtendimento;
    }

    public String getBeneficios() {
        return beneficios;
    }

    public void setBeneficios(String beneficios) {
        this.beneficios = beneficios;
    }

    public String getNumeroDeclaracaoObito() {
        return numeroDeclaracaoObito;
    }

    public void setNumeroDeclaracaoObito(String numeroDeclaracaoObito) {
        this.numeroDeclaracaoObito = numeroDeclaracaoObito;
    }

    public Date getDataFalecimento() {
        return dataFalecimento;
    }

    public void setDataFalecimento(Date dataFalecimento) {
        this.dataFalecimento = dataFalecimento;
    }

    public BigDecimal getRendaBruta() {
        return rendaBruta != null ? rendaBruta : BigDecimal.ZERO;
    }

    public void setRendaBruta(BigDecimal rendaBruta) {
        this.rendaBruta = rendaBruta;
    }

    public BigDecimal getRendaPerCapita() {
        return rendaPerCapita != null ? rendaPerCapita : BigDecimal.ZERO;
    }

    public void setRendaPerCapita(BigDecimal rendaPerCapita) {
        this.rendaPerCapita = rendaPerCapita;
    }

    public String getFuneraria() {
        return funeraria;
    }

    public void setFuneraria(String funeraria) {
        this.funeraria = funeraria;
    }

    public String getCemiterio() {
        return cemiterio;
    }

    public void setCemiterio(String cemiterio) {
        this.cemiterio = cemiterio;
    }
}
