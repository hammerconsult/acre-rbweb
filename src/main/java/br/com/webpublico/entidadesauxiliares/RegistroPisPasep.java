/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;

/**
 * @author Claudio
 */
public class RegistroPisPasep implements Serializable {
    //atributos Header
    private String headerTipoRegistro;
    private String headerNomeArquivo;
    private String headerDataGeracao;
    private String headerCnpj;
    private String headerNumeroRemessa;
    private String headerAgenciaControle;
    private String headerDigitoVerificadorAgenciaControle;
    private String headerDataPagamento;
    private String headerNumeroConvenio;
    private String headerCodigoFormaRepasse;
    private String headerAgenciaLancamento;
    private String headerDigitoVerificadorAgenciaLancamento;
    private String headerContaCorrenteLancamento;
    private String headerDigitoVerificadorContaCorrenteLancamento;
    private String headerCodigoLancamentoGRU;
    private String headerCodigoBanco;
    private String headerDigitoVerificadorBanco;
    private String header14EspacoBranco;
    private String headerEmail;
    private String header43EspacoBranco;

    //atributos Detalhe
    private String detalheTipoRegistro;
    private String detalheInscricao;
    private String detalheNome;
    private String detalheMatricula;
    private String detalheEndereco;
    private String detalheNumero;
    private String detalheComplemento;
    private String detalheBairro;
    private String detalheMunicipio;
    private String detalheUF;
    private String detalheCEP;
    private String detalhe11EspacoBranco;

    //atributos Trailer
    private String trailerTipoRegistro;
    private String trailer221EspacoBranco;
    private String trailerQuantidadeRegistroDetalhe;

    public String getHeaderTipoRegistro() {
        return headerTipoRegistro;
    }

    public void setHeaderTipoRegistro(String headerTipoRegistro) {
        this.headerTipoRegistro = headerTipoRegistro;
    }

    public String getHeaderNomeArquivo() {
        return headerNomeArquivo;
    }

    public void setHeaderNomeArquivo(String headerNomeArquivo) {
        this.headerNomeArquivo = headerNomeArquivo;
    }

    public String getHeaderDataGeracao() {
        return headerDataGeracao;
    }

    public void setHeaderDataGeracao(String headerDataGeracao) {
        this.headerDataGeracao = headerDataGeracao;
    }

    public String getHeaderCnpj() {
        return headerCnpj;
    }

    public void setHeaderCnpj(String headerCnpj) {
        this.headerCnpj = headerCnpj;
    }

    public String getHeaderNumeroRemessa() {
        return headerNumeroRemessa;
    }

    public void setHeaderNumeroRemessa(String headerNumeroRemessa) {
        this.headerNumeroRemessa = headerNumeroRemessa;
    }

    public String getHeaderAgenciaControle() {
        return headerAgenciaControle;
    }

    public void setHeaderAgenciaControle(String headerAgenciaControle) {
        this.headerAgenciaControle = headerAgenciaControle;
    }

    public String getHeaderDigitoVerificadorAgenciaControle() {
        return headerDigitoVerificadorAgenciaControle;
    }

    public void setHeaderDigitoVerificadorAgenciaControle(String headerDigitoVerificadorAgenciaControle) {
        this.headerDigitoVerificadorAgenciaControle = headerDigitoVerificadorAgenciaControle;
    }

    public String getHeaderDataPagamento() {
        return headerDataPagamento;
    }

    public void setHeaderDataPagamento(String headerDataPagamento) {
        this.headerDataPagamento = headerDataPagamento;
    }

    public String getHeaderNumeroConvenio() {
        return headerNumeroConvenio;
    }

    public void setHeaderNumeroConvenio(String headerNumeroConvenio) {
        this.headerNumeroConvenio = headerNumeroConvenio;
    }

    public String getHeaderCodigoFormaRepasse() {
        return headerCodigoFormaRepasse;
    }

    public void setHeaderCodigoFormaRepasse(String headerCodigoFormaRepasse) {
        this.headerCodigoFormaRepasse = headerCodigoFormaRepasse;
    }

    public String getHeaderAgenciaLancamento() {
        return headerAgenciaLancamento;
    }

    public void setHeaderAgenciaLancamento(String headerAgenciaLancamento) {
        this.headerAgenciaLancamento = headerAgenciaLancamento;
    }

    public String getHeaderDigitoVerificadorAgenciaLancamento() {
        return headerDigitoVerificadorAgenciaLancamento;
    }

    public void setHeaderDigitoVerificadorAgenciaLancamento(String headerDigitoVerificadorAgenciaLancamento) {
        this.headerDigitoVerificadorAgenciaLancamento = headerDigitoVerificadorAgenciaLancamento;
    }

    public String getHeaderContaCorrenteLancamento() {
        return headerContaCorrenteLancamento;
    }

    public void setHeaderContaCorrenteLancamento(String headerContaCorrenteLancamento) {
        this.headerContaCorrenteLancamento = headerContaCorrenteLancamento;
    }

    public String getHeaderDigitoVerificadorContaCorrenteLancamento() {
        return headerDigitoVerificadorContaCorrenteLancamento;
    }

    public void setHeaderDigitoVerificadorContaCorrenteLancamento(String headerDigitoVerificadorContaCorrenteLancamento) {
        this.headerDigitoVerificadorContaCorrenteLancamento = headerDigitoVerificadorContaCorrenteLancamento;
    }

    public String getHeaderCodigoLancamentoGRU() {
        return headerCodigoLancamentoGRU;
    }

    public void setHeaderCodigoLancamentoGRU(String headerCodigoLancamentoGRU) {
        this.headerCodigoLancamentoGRU = headerCodigoLancamentoGRU;
    }

    public String getHeaderCodigoBanco() {
        return headerCodigoBanco;
    }

    public void setHeaderCodigoBanco(String headerCodigoBanco) {
        this.headerCodigoBanco = headerCodigoBanco;
    }

    public String getHeaderDigitoVerificadorBanco() {
        return headerDigitoVerificadorBanco;
    }

    public void setHeaderDigitoVerificadorBanco(String headerDigitoVerificadorBanco) {
        this.headerDigitoVerificadorBanco = headerDigitoVerificadorBanco;
    }

    public String getHeader14EspacoBranco() {
        return header14EspacoBranco;
    }

    public void setHeader14EspacoBranco(String header14EspacoBranco) {
        this.header14EspacoBranco = header14EspacoBranco;
    }

    public String getHeaderEmail() {
        return headerEmail;
    }

    public void setHeaderEmail(String headerEmail) {
        this.headerEmail = headerEmail;
    }

    public String getHeader43EspacoBranco() {
        return header43EspacoBranco;
    }

    public void setHeader43EspacoBranco(String header43EspacoBranco) {
        this.header43EspacoBranco = header43EspacoBranco;
    }

    public String getDetalheTipoRegistro() {
        return detalheTipoRegistro;
    }

    public void setDetalheTipoRegistro(String detalheTipoRegistro) {
        this.detalheTipoRegistro = detalheTipoRegistro;
    }

    public String getDetalheInscricao() {
        return detalheInscricao;
    }

    public void setDetalheInscricao(String detalheInscricao) {
        this.detalheInscricao = detalheInscricao;
    }

    public String getDetalheNome() {
        return detalheNome;
    }

    public void setDetalheNome(String detalheNome) {
        this.detalheNome = detalheNome;
    }

    public String getDetalheMatricula() {
        return detalheMatricula;
    }

    public void setDetalheMatricula(String detalheMatricula) {
        this.detalheMatricula = detalheMatricula;
    }

    public String getDetalheEndereco() {
        return detalheEndereco;
    }

    public void setDetalheEndereco(String detalheEndereco) {
        this.detalheEndereco = detalheEndereco;
    }

    public String getDetalheNumero() {
        return detalheNumero;
    }

    public void setDetalheNumero(String detalheNumero) {
        this.detalheNumero = detalheNumero;
    }

    public String getDetalheComplemento() {
        return detalheComplemento;
    }

    public void setDetalheComplemento(String detalheComplemento) {
        this.detalheComplemento = detalheComplemento;
    }

    public String getDetalheBairro() {
        return detalheBairro;
    }

    public void setDetalheBairro(String detalheBairro) {
        this.detalheBairro = detalheBairro;
    }

    public String getDetalheMunicipio() {
        return detalheMunicipio;
    }

    public void setDetalheMunicipio(String detalheMunicipio) {
        this.detalheMunicipio = detalheMunicipio;
    }

    public String getDetalheUF() {
        return detalheUF;
    }

    public void setDetalheUF(String detalheUF) {
        this.detalheUF = detalheUF;
    }

    public String getDetalheCEP() {
        return detalheCEP;
    }

    public void setDetalheCEP(String detalheCEP) {
        this.detalheCEP = detalheCEP;
    }

    public String getDetalhe11EspacoBranco() {
        return detalhe11EspacoBranco;
    }

    public void setDetalhe11EspacoBranco(String detalhe11EspacoBranco) {
        this.detalhe11EspacoBranco = detalhe11EspacoBranco;
    }

    public String getTrailerTipoRegistro() {
        return trailerTipoRegistro;
    }

    public void setTrailerTipoRegistro(String trailerTipoRegistro) {
        this.trailerTipoRegistro = trailerTipoRegistro;
    }

    public String getTrailer221EspacoBranco() {
        return trailer221EspacoBranco;
    }

    public void setTrailer221EspacoBranco(String trailer221EspacoBranco) {
        this.trailer221EspacoBranco = trailer221EspacoBranco;
    }

    public String getTrailerQuantidadeRegistroDetalhe() {
        return trailerQuantidadeRegistroDetalhe;
    }

    public void setTrailerQuantidadeRegistroDetalhe(String trailerQuantidadeRegistroDetalhe) {
        this.trailerQuantidadeRegistroDetalhe = trailerQuantidadeRegistroDetalhe;
    }

}
