/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.StringUtil;

import java.io.Serializable;

/**
 * @author andre
 */
public class SefipRegistroTipo00 implements Serializable {

    private final String tipoDeRegistro = "00";
    private String brancos;
    private String tipoDeRemessa;
    private String tipoDeInscricao;
    private String inscricaoDoResponsavel;
    private String nomeResponsavel;
    private String nomePessaContato;
    private String logradouro;
    private String bairro;
    private String cep;
    private String cidade;
    private String unidadeDeFederacao;
    private String telefoneContato;
    private String enderecoInternet;
    private String competencia;
    private String codigoDeRecolhimento;
    private String indicadorRecolhimentoFGTS;
    private String modalidadeDoArquivo;
    private String dataRecolhimentoFGTS;
    private String indicadorRecolhimentoDaPrevidenciaSocial;
    private String dataRecolhimentoDaPrevidenciaSocial;
    private String indiceRecolhimentoEmAtrasoPrevidenciaSocial;
    private String tipoInscricaoFornecedorFP;
    private String inscricaoFornecedorFP;
    private String brancos2;
    private final String finalDeLinha = "*";

    public SefipRegistroTipo00() {
    }

    public SefipRegistroTipo00(String brancos, String tipoDeRemessa, String tipoDeInscricao, String inscricaoDoResponsavel, String nomeResponsavel, String nomePessaContato, String logradouro, String bairro, String cep, String cidade, String unidadeDeFederacao, String telefoneContato, String enderecoInternet, String competencia, String codigoDeRecolhimento, String indicadorRecolhimentoFGTS, String modalidadeDoArquivo, String dataRecolhimentoFGTS, String indicadorRecolhimentoDaPrevidenciaSocial, String dataRecolhimentoDaPrevidenciaSocial, String indiceRecolhimentoEmAtrasoPrevidenciaSocial, String tipoInscricaoFornecedorFP, String inscricaoFornecedorFP, String brancos2) {
        this.brancos = brancos;
        this.tipoDeRemessa = tipoDeRemessa;
        this.tipoDeInscricao = tipoDeInscricao;
        this.inscricaoDoResponsavel = inscricaoDoResponsavel;
        this.nomeResponsavel = nomeResponsavel;
        this.nomePessaContato = nomePessaContato;
        this.logradouro = logradouro;
        this.bairro = bairro;
        this.cep = cep;
        this.cidade = cidade;
        this.unidadeDeFederacao = unidadeDeFederacao;
        this.telefoneContato = telefoneContato;
        this.enderecoInternet = enderecoInternet;
        this.competencia = competencia;
        this.codigoDeRecolhimento = codigoDeRecolhimento;
        this.indicadorRecolhimentoFGTS = indicadorRecolhimentoFGTS;
        this.modalidadeDoArquivo = modalidadeDoArquivo;
        this.dataRecolhimentoFGTS = dataRecolhimentoFGTS;
        this.indicadorRecolhimentoDaPrevidenciaSocial = indicadorRecolhimentoDaPrevidenciaSocial;
        this.dataRecolhimentoDaPrevidenciaSocial = dataRecolhimentoDaPrevidenciaSocial;
        this.indiceRecolhimentoEmAtrasoPrevidenciaSocial = indiceRecolhimentoEmAtrasoPrevidenciaSocial;
        this.tipoInscricaoFornecedorFP = tipoInscricaoFornecedorFP;
        this.inscricaoFornecedorFP = inscricaoFornecedorFP;
        this.brancos2 = brancos2;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getBrancos() {
        return brancos;
    }

    public void setBrancos(String brancos) {
        this.brancos = brancos;
    }

    public String getBrancos2() {
        return brancos2;
    }

    public void setBrancos2(String brancos2) {
        this.brancos2 = brancos2;
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

    public String getCodigoDeRecolhimento() {
        return codigoDeRecolhimento;
    }

    public void setCodigoDeRecolhimento(String codigoDeRecolhimento) {
        this.codigoDeRecolhimento = codigoDeRecolhimento;
    }

    public String getCompetencia() {
        return competencia;
    }

    public void setCompetencia(String competencia) {
        this.competencia = competencia;
    }

    public String getDataRecolhimentoDaPrevidenciaSocial() {
        return dataRecolhimentoDaPrevidenciaSocial;
    }

    public void setDataRecolhimentoDaPrevidenciaSocial(String dataRecolhimentoDaPrevidenciaSocial) {
        this.dataRecolhimentoDaPrevidenciaSocial = dataRecolhimentoDaPrevidenciaSocial;
    }

    public String getDataRecolhimentoFGTS() {
        return dataRecolhimentoFGTS;
    }

    public void setDataRecolhimentoFGTS(String dataRecolhimentoFGTS) {
        this.dataRecolhimentoFGTS = dataRecolhimentoFGTS;
    }

    public String getEnderecoInternet() {
        return enderecoInternet;
    }

    public void setEnderecoInternet(String enderecoInternet) {
        this.enderecoInternet = enderecoInternet;
    }

    public String getFinalDeLinha() {
        return finalDeLinha;
    }

    public String getIndicadorRecolhimentoDaPrevidenciaSocial() {
        return indicadorRecolhimentoDaPrevidenciaSocial;
    }

    public void setIndicadorRecolhimentoDaPrevidenciaSocial(String indicadorRecolhimentoDaPrevidenciaSocial) {
        this.indicadorRecolhimentoDaPrevidenciaSocial = indicadorRecolhimentoDaPrevidenciaSocial;
    }

    public String getIndicadorRecolhimentoFGTS() {
        return indicadorRecolhimentoFGTS;
    }

    public void setIndicadorRecolhimentoFGTS(String indicadorRecolhimentoFGTS) {
        this.indicadorRecolhimentoFGTS = indicadorRecolhimentoFGTS;
    }

    public String getIndiceRecolhimentoEmAtrasoPrevidenciaSocial() {
        return indiceRecolhimentoEmAtrasoPrevidenciaSocial;
    }

    public void setIndiceRecolhimentoEmAtrasoPrevidenciaSocial(String indiceRecolhimentoEmAtrasoPrevidenciaSocial) {
        this.indiceRecolhimentoEmAtrasoPrevidenciaSocial = indiceRecolhimentoEmAtrasoPrevidenciaSocial;
    }

    public String getInscricaoDoResponsavel() {
        return inscricaoDoResponsavel;
    }

    public void setInscricaoDoResponsavel(String inscricaoDoResponsavel) {
        this.inscricaoDoResponsavel = inscricaoDoResponsavel;
    }

    public String getInscricaoFornecedorFP() {
        return inscricaoFornecedorFP;
    }

    public void setInscricaoFornecedorFP(String inscricaoFornecedorFP) {
        this.inscricaoFornecedorFP = inscricaoFornecedorFP;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getModalidadeDoArquivo() {
        return modalidadeDoArquivo;
    }

    public void setModalidadeDoArquivo(String modalidadeDoArquivo) {
        this.modalidadeDoArquivo = modalidadeDoArquivo;
    }

    public String getNomePessaContato() {
        return nomePessaContato;
    }

    public void setNomePessaContato(String nomePessaContato) {
        this.nomePessaContato = nomePessaContato;
    }

    public String getNomeResponsavel() {
        return nomeResponsavel;
    }

    public void setNomeResponsavel(String nomeResponsavel) {
        this.nomeResponsavel = nomeResponsavel;
    }

    public String getTelefoneContato() {
        return telefoneContato;
    }

    public void setTelefoneContato(String telefoneContato) {
        this.telefoneContato = telefoneContato;
    }

    public String getTipoDeInscricao() {
        return tipoDeInscricao;
    }

    public void setTipoDeInscricao(String tipoDeInscricao) {
        this.tipoDeInscricao = tipoDeInscricao;
    }

    public String getTipoDeRegistro() {
        return tipoDeRegistro;
    }

    public String getTipoDeRemessa() {
        return tipoDeRemessa;
    }

    public void setTipoDeRemessa(String tipoDeRemessa) {
        this.tipoDeRemessa = tipoDeRemessa;
    }

    public String getTipoInscricaoFornecedorFP() {
        return tipoInscricaoFornecedorFP;
    }

    public void setTipoInscricaoFornecedorFP(String tipoInscricaoFornecedorFP) {
        this.tipoInscricaoFornecedorFP = tipoInscricaoFornecedorFP;
    }

    public String getUnidadeDeFederacao() {
        return unidadeDeFederacao;
    }

    public void setUnidadeDeFederacao(String unidadeDeFederacao) {
        this.unidadeDeFederacao = unidadeDeFederacao;
    }

    public String getRegistroTipo00() {
        StringBuilder texto = new StringBuilder("");

        //tipo de registro - 2 Posições
        texto.append(StringUtil.removeCaracteresEspeciais(StringUtil.cortaOuCompletaDireita(tipoDeRegistro, 2, " ")));
        //Brancos - 51 posições
        texto.append(StringUtil.removeCaracteresEspeciais(StringUtil.cortaOuCompletaDireita(brancos, 51, " ")));
        //Tipo de Remessa - 1 posição
        texto.append(StringUtil.removeCaracteresEspeciais(StringUtil.cortaOuCompletaDireita(tipoDeRemessa, 1, " ")));
        //Tipo de Inscricao - 1 posição
        texto.append(StringUtil.removeCaracteresEspeciais(StringUtil.cortaOuCompletaDireita(tipoDeInscricao, 1, " ")));
        //Inscricao do Responsável - 14 posicões
        texto.append(StringUtil.removeCaracteresEspeciais(StringUtil.cortaOuCompletaEsquerda(inscricaoDoResponsavel.replace(".", "").replace("/", ""), 14, " ")));
        //Nome Responsável (Razão Social) - 30 posições
        texto.append(StringUtil.removeCaracteresEspeciais(StringUtil.cortaOuCompletaDireita(StringUtil.removeEspacos(nomeResponsavel), 30, " ")));
        //Nome Pessoa Contato - 20 Posições
        texto.append(StringUtil.removeCaracteresEspeciais(StringUtil.cortaOuCompletaDireita(StringUtil.removeCaracteresEspeciaisENumeros(nomePessaContato), 20, " ")));
        //Logradouro - 50 Posições
        texto.append(StringUtil.removeCaracteresEspeciais(StringUtil.cortaOuCompletaDireita(StringUtil.removeEspacos(StringUtil.removeCaracteresEspeciais(logradouro)), 50, " ")));
        //Bairro - 20 Posições
        texto.append(StringUtil.removeCaracteresEspeciais(StringUtil.cortaOuCompletaDireita(StringUtil.removeEspacos(StringUtil.removeCaracteresEspeciais(bairro)), 20, " ")));
        //CEP - 8 Posições
        texto.append(StringUtil.removeCaracteresEspeciais(StringUtil.cortaOuCompletaDireita(StringUtil.removeEspacos(cep.replace("-", "")), 8, " ")));
        //Cidade - 20 Posições
        texto.append(StringUtil.removeCaracteresEspeciais(StringUtil.cortaOuCompletaDireita(StringUtil.removeEspacos(StringUtil.removeCaracteresEspeciais(cidade)), 20, " ")));
        //Unidade de Federação - 2 Posições
        texto.append(StringUtil.removeCaracteresEspeciais(StringUtil.cortaOuCompletaDireita(unidadeDeFederacao, 2, " ")));
        //Telefone Contato - 12 Posições
        texto.append(StringUtil.removeCaracteresEspeciais(StringUtil.cortaOuCompletaEsquerda(telefoneContato.replace("(", "").replace(")", "").replace("-", ""), 12, "0")));
        //Endereco Internet - 60 Posições
        texto.append(StringUtil.removeCaracteresEspeciaisExcetoPonto(StringUtil.cortaOuCompletaDireita(enderecoInternet, 60, " ")));
        //Competência - 6 posições  AAAAMM
        texto.append(StringUtil.removeCaracteresEspeciais(StringUtil.cortaOuCompletaDireita(competencia, 6, " ")));
        //Código do Recolhimento - 3 posições
        texto.append(StringUtil.removeCaracteresEspeciais(StringUtil.cortaOuCompletaDireita(codigoDeRecolhimento, 3, " ")));
        //Indicador de Recolhimento FGTS - 1 posição
        texto.append(StringUtil.removeCaracteresEspeciais(StringUtil.cortaOuCompletaDireita(indicadorRecolhimentoFGTS, 1, " ")));
        //Modalidade do Arquivo - 1 posição
        texto.append(StringUtil.removeCaracteresEspeciais(StringUtil.cortaOuCompletaDireita(modalidadeDoArquivo, 1, " ")));
        //Data de Recolhimento do FGTS - 8 posições
        texto.append(StringUtil.removeCaracteresEspeciais(StringUtil.cortaOuCompletaDireita(dataRecolhimentoFGTS, 8, " ")));
        //Indicador de Recolhimento da Previdência Social - 1 posição
        texto.append(StringUtil.removeCaracteresEspeciais(StringUtil.cortaOuCompletaDireita(indicadorRecolhimentoDaPrevidenciaSocial, 1, " ")));
        //Data de Recolhimento da Previdência Social - 8 posições
        texto.append(StringUtil.removeCaracteresEspeciais(StringUtil.cortaOuCompletaDireita(dataRecolhimentoDaPrevidenciaSocial, 8, " ")));
        //Indice de Recolhimento em atraso da Previdência Social - 7 posições
        texto.append(StringUtil.removeCaracteresEspeciais(indiceRecolhimentoEmAtrasoPrevidenciaSocial.trim().length() > 0 ? StringUtil.cortaOuCompletaEsquerda(indiceRecolhimentoEmAtrasoPrevidenciaSocial, 7, "0") : StringUtil.cortaOuCompletaEsquerda(indiceRecolhimentoEmAtrasoPrevidenciaSocial, 7, " ")));
        //Tipo de Inscrição - Fornecedor FP - 1 posição
        texto.append(StringUtil.removeCaracteresEspeciais(StringUtil.cortaOuCompletaDireita(tipoDeInscricao, 1, " ")));
        //Inscrição do Fornecedor Folha de Pagamento - 14 posições
        texto.append(StringUtil.removeCaracteresEspeciais(StringUtil.cortaOuCompletaEsquerda(inscricaoFornecedorFP, 14, " ")));
        //Brancos - 18 posições
        texto.append(StringUtil.removeCaracteresEspeciais(StringUtil.cortaOuCompletaDireita(brancos2, 18, " ")));
        //Final de Linha 1 Posição
        texto.append(StringUtil.cortaOuCompletaDireita(finalDeLinha, 1, "*"));
        texto.append("\r\n");

        return texto.toString();
    }
}
