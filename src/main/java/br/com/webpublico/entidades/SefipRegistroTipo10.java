/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.StringUtil;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * @author andre
 */
public class SefipRegistroTipo10 implements Serializable {

    private String ordenacao;
    private final String tipoDeRegistro = "10";
    private String tipoDeInscricaoEmpresa;
    private String inscricaoDaEmpresa;
    private String zeros;
    private String nomeEmpresa;
    private String logradouro;
    private String bairro;
    private String cep;
    private String cidade;
    private String unidadeDaFederacao;
    private String telefone;
    private String indicadorDeAlteracaoDeEndereco;
    private String cnae;
    private String indicadorDeAlteracaoCNAE;
    private String aliquotaRAT;
    private String codigoCentralizacao;
    private String simples;
    private String fpas;
    private String codigoDeOutrasEntidades;
    private String codigoDePagamentoGPS;
    private String percentualIsencaoDeFilantropia;
    private String salarioFamilia;
    private String salarioMaternidade;
    private String contribuicaoDescontadaEmpregado13;
    private String indicadorValorNegativoPositivo;
    private String valorDevidoPrevidenciaSocial13;
    private String banco;
    private String agencia;
    private String contaCorrente;
    private String zeros2;
    private String brancos;
    private final String finalDeLinha = "*";

    public SefipRegistroTipo10() {
        contribuicaoDescontadaEmpregado13 = StringUtils.repeat("0", 15);
        indicadorValorNegativoPositivo = "0";
        valorDevidoPrevidenciaSocial13 = StringUtils.repeat("0", 14);
        zeros = StringUtils.repeat("0", 36);
        zeros2 = StringUtils.repeat("0", 45);
        brancos = StringUtils.repeat(" ", 4);
    }

    public SefipRegistroTipo10(String ordenacao, String tipoDeInscricaoEmpresa, String inscricaoDaEmpresa, String zeros, String nomeEmpresa, String logradouro, String bairro, String cep, String cidade, String unidadeDaFederacao, String telefone, String indicadorDeAlteracaoDeEndereco, String cnae, String indicadorDeAlteracaoCNAE, String aliquotaRAT, String codigoCentralizacao, String simples, String fpas, String codigoDeOutrasEntidades, String codigoDePagamentoGPS, String percentualIsencaoDeFilantropia, String salarioFamilia, String salarioMaternidade, String contribuicaoDescontadaEmpregado13, String indicadorValorNegativoPositivo, String valorDevidoPrevidenciaSocial13, String banco, String agencia, String contaCorrente, String zeros2, String brancos) {
        this.ordenacao = ordenacao;
        this.tipoDeInscricaoEmpresa = tipoDeInscricaoEmpresa;
        this.inscricaoDaEmpresa = inscricaoDaEmpresa;
        this.zeros = zeros;
        this.nomeEmpresa = nomeEmpresa;
        this.logradouro = logradouro;
        this.bairro = bairro;
        this.cep = cep;
        this.cidade = cidade;
        this.unidadeDaFederacao = unidadeDaFederacao;
        this.telefone = telefone;
        this.indicadorDeAlteracaoDeEndereco = indicadorDeAlteracaoDeEndereco;
        this.cnae = cnae;
        this.indicadorDeAlteracaoCNAE = indicadorDeAlteracaoCNAE;
        this.aliquotaRAT = aliquotaRAT;
        this.codigoCentralizacao = codigoCentralizacao;
        this.simples = simples;
        this.fpas = fpas;
        this.codigoDeOutrasEntidades = codigoDeOutrasEntidades;
        this.codigoDePagamentoGPS = codigoDePagamentoGPS;
        this.percentualIsencaoDeFilantropia = percentualIsencaoDeFilantropia;
        this.salarioFamilia = salarioFamilia;
        this.salarioMaternidade = salarioMaternidade;
        this.contribuicaoDescontadaEmpregado13 = contribuicaoDescontadaEmpregado13;
        this.indicadorValorNegativoPositivo = indicadorValorNegativoPositivo;
        this.valorDevidoPrevidenciaSocial13 = valorDevidoPrevidenciaSocial13;
        this.banco = banco;
        this.agencia = agencia;
        this.contaCorrente = contaCorrente;
        this.zeros2 = zeros2;
        this.brancos = brancos;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public String getAliquotaRAT() {
        return aliquotaRAT;
    }

    public void setAliquotaRAT(String aliquotaRAT) {
        this.aliquotaRAT = aliquotaRAT;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getBrancos() {
        return brancos;
    }

    public void setBrancos(String brancos) {
        this.brancos = brancos;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getCnae() {
        return cnae;
    }

    public void setCnae(String cnae) {
        this.cnae = cnae;
    }

    public String getCodigoCentralizacao() {
        return codigoCentralizacao;
    }

    public void setCodigoCentralizacao(String codigoCentralizacao) {
        this.codigoCentralizacao = codigoCentralizacao;
    }

    public String getCodigoDeOutrasEntidades() {
        return codigoDeOutrasEntidades;
    }

    public void setCodigoDeOutrasEntidades(String codigoDeOutrasEntidades) {
        this.codigoDeOutrasEntidades = codigoDeOutrasEntidades;
    }

    public String getCodigoDePagamentoGPS() {
        return codigoDePagamentoGPS;
    }

    public void setCodigoDePagamentoGPS(String codigoDePagamentoGPS) {
        this.codigoDePagamentoGPS = codigoDePagamentoGPS;
    }

    public String getContaCorrente() {
        return contaCorrente;
    }

    public void setContaCorrente(String contaCorrente) {
        this.contaCorrente = contaCorrente;
    }

    public String getContribuicaoDescontadaEmpregado13() {
        return contribuicaoDescontadaEmpregado13;
    }

    public void setContribuicaoDescontadaEmpregado13(String contribuicaoDescontadaEmpregado13) {
        this.contribuicaoDescontadaEmpregado13 = contribuicaoDescontadaEmpregado13;
    }

    public String getFinalDeLinha() {
        return finalDeLinha;
    }

    public String getFpas() {
        return fpas;
    }

    public void setFpas(String fpas) {
        this.fpas = fpas;
    }

    public String getIndicadorDeAlteracaoCNAE() {
        return indicadorDeAlteracaoCNAE;
    }

    public void setIndicadorDeAlteracaoCNAE(String indicadorDeAlteracaoCNAE) {
        this.indicadorDeAlteracaoCNAE = indicadorDeAlteracaoCNAE;
    }

    public String getIndicadorDeAlteracaoDeEndereco() {
        return indicadorDeAlteracaoDeEndereco;
    }

    public void setIndicadorDeAlteracaoDeEndereco(String indicadorDeAlteracaoDeEndereco) {
        this.indicadorDeAlteracaoDeEndereco = indicadorDeAlteracaoDeEndereco;
    }

    public String getIndicadorValorNegativoPositivo() {
        return indicadorValorNegativoPositivo;
    }

    public void setIndicadorValorNegativoPositivo(String indicadorValorNegativoPositivo) {
        this.indicadorValorNegativoPositivo = indicadorValorNegativoPositivo;
    }

    public String getInscricaoDaEmpresa() {
        return inscricaoDaEmpresa;
    }

    public void setInscricaoDaEmpresa(String inscricaoDaEmpresa) {
        this.inscricaoDaEmpresa = inscricaoDaEmpresa;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    public void setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
    }

    public String getPercentualIsencaoDeFilantropia() {
        return percentualIsencaoDeFilantropia;
    }

    public void setPercentualIsencaoDeFilantropia(String percentualIsencaoDeFilantropia) {
        this.percentualIsencaoDeFilantropia = percentualIsencaoDeFilantropia;
    }

    public String getSalarioFamilia() {
        return salarioFamilia;
    }

    public void setSalarioFamilia(String salarioFamilia) {
        this.salarioFamilia = salarioFamilia;
    }

    public String getSalarioMaternidade() {
        return salarioMaternidade;
    }

    public void setSalarioMaternidade(String salarioMaternidade) {
        this.salarioMaternidade = salarioMaternidade;
    }

    public String getSimples() {
        return simples;
    }

    public void setSimples(String simples) {
        this.simples = simples;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getTipoDeInscricaoEmpresa() {
        return tipoDeInscricaoEmpresa;
    }

    public void setTipoDeInscricaoEmpresa(String tipoDeInscricaoEmpresa) {
        this.tipoDeInscricaoEmpresa = tipoDeInscricaoEmpresa;
    }

    public String getTipoDeRegistro() {
        return tipoDeRegistro;
    }

    public String getUnidadeDaFederacao() {
        return unidadeDaFederacao;
    }

    public void setUnidadeDaFederacao(String unidadeDaFederacao) {
        this.unidadeDaFederacao = unidadeDaFederacao;
    }

    public String getValorDevidoPrevidenciaSocial13() {
        return valorDevidoPrevidenciaSocial13;
    }

    public void setValorDevidoPrevidenciaSocial13(String valorDevidoPrevidenciaSocial13) {
        this.valorDevidoPrevidenciaSocial13 = valorDevidoPrevidenciaSocial13;
    }

    public String getZeros() {
        return zeros;
    }

    public void setZeros(String zeros) {
        this.zeros = zeros;
    }

    public String getZeros2() {
        return zeros2;
    }

    public void setZeros2(String zeros2) {
        this.zeros2 = zeros2;
    }

    public String getOrdenacao() {
        return ordenacao;
    }

    public void setOrdenacao(String ordenacao) {
        this.ordenacao = ordenacao;
    }

    public String getRegistroTipo10() {
        StringBuilder texto = new StringBuilder("");

        //Ordenacão, é removido posteriormente
        texto.append(ordenacao);
        //Tipo de Registro - 2 Posições
        texto.append(StringUtil.cortaOuCompletaDireita(tipoDeRegistro, 2, " "));
        //Tipo de Inscriçao - 1 Posição
        texto.append(StringUtil.cortaOuCompletaDireita(tipoDeInscricaoEmpresa, 1, " "));
        //Inscrição da Empresa - 14 posições
        texto.append(StringUtil.cortaOuCompletaDireita(inscricaoDaEmpresa.replace(".", "").replace("/", ""), 14, " "));
        //Zeros - 36 posições
        texto.append(StringUtil.cortaOuCompletaDireita(zeros, 36, "0"));
        //Nome Empresa/Razão Social - 40 Posições
        texto.append(StringUtil.cortaOuCompletaDireita(nomeEmpresa, 40, " "));
        //logradouro - 50 posições
        logradouro = StringUtil.removeCaracteresEspeciais(logradouro);
        logradouro = StringUtil.removeEspacos(logradouro);
        texto.append(StringUtil.cortaOuCompletaDireita(logradouro, 50, " "));
        //Bairro - 20 posições
        texto.append(StringUtil.cortaOuCompletaDireita(bairro, 20, " "));
        //cep - 8 posições
        texto.append(StringUtil.cortaOuCompletaDireita(cep.replace("-", ""), 8, " "));
        //Cidade - 20 posições
        texto.append(StringUtil.cortaOuCompletaDireita(cidade, 20, " "));
        //Unidade da Federação - 2 posições
        texto.append(StringUtil.cortaOuCompletaDireita(unidadeDaFederacao, 2, " "));
        //telefone = 12 posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(telefone.replace("(", "").replace(")", "").replace("-", ""), 12, "0"));
        //Indicador de Alteração de endereço - 1 posição
        texto.append(StringUtil.cortaOuCompletaDireita(indicadorDeAlteracaoDeEndereco, 1, " "));
        //CNAE - 7 posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(cnae.replace("/", "").replace("-", ""), 7, "0"));
        //Indicador de Alteração CNAE - 1 posição
        texto.append(StringUtil.cortaOuCompletaDireita(indicadorDeAlteracaoCNAE, 1, " "));
        //Aliquota RAT - 2 posições
        texto.append(StringUtil.cortaOuCompletaDireita(aliquotaRAT, 2, "0"));
        //Código de centralização - 1 posição
        texto.append(StringUtil.cortaOuCompletaDireita(codigoCentralizacao, 1, " "));
        //simples - 1 posição
        texto.append(StringUtil.cortaOuCompletaDireita(simples, 1, " "));
        //FPAS - 3 Posições
        texto.append(StringUtil.cortaOuCompletaDireita(fpas, 3, " "));
        //Código de outras entidades - 4 posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(codigoDeOutrasEntidades, 4, "0"));
        //Código de Pagamento GPS - 4 posições
        texto.append(StringUtil.cortaOuCompletaDireita(codigoDePagamentoGPS, 4, " "));
        //Percentual de Isenção de Filantropia - 5 posições
        texto.append(StringUtil.cortaOuCompletaDireita(percentualIsencaoDeFilantropia, 5, " "));
        //Salário-Família - 15 posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(salarioFamilia, 15, "0"));
        //Salário-Maternidade - 15 posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(salarioMaternidade, 15, "0"));
        //Contrib Desc Empregado Referente a competencia 13 - 15 posicoes
        texto.append(StringUtil.cortaOuCompletaEsquerda(contribuicaoDescontadaEmpregado13, 15, "0"));
        //Indicador de Valor Negativo ou Positivo - 1 Posição
        texto.append(StringUtil.cortaOuCompletaEsquerda(indicadorValorNegativoPositivo, 1, "0"));
        //Valor Devido a previdencia social referente a comp. 13 - 14 posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(valorDevidoPrevidenciaSocial13, 14, "0"));
        //Banco - 3 Posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(banco, 3, " "));
        //Agência - 4 posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(agencia, 4, " "));
        //Conta Corrente - 9 Posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(contaCorrente, 9, " "));
        //Zeros - 45 Posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(zeros2, 45, "0"));
        //Brancos - 4 Posições
        texto.append(StringUtil.cortaOuCompletaEsquerda(brancos, 4, " "));

        String s = StringUtil.removeCaracteresEspeciais(texto.toString());

        //Final de Linha - 1 Posição
        s += StringUtil.cortaOuCompletaEsquerda(finalDeLinha, 1, "*");
        s += "\r\n";
        return s;
    }
}
