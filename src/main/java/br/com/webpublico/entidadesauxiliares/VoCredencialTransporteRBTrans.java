package br.com.webpublico.entidadesauxiliares;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Andre Gustavo
 * Date: 22/11/13
 * Time: 10:05
 * To change this template use File | Settings | File Templates.
 */
public class VoCredencialTransporteRBTrans implements Serializable {
    private String nomeReduzido;
    private String nomeCompletoRequerente;
    private String numeroCMCRequerente;
    private String numeroPermissao;
    private String classificacao;
    private String dataEmissao;
    private String dataValidade;
    private String placaVeiculo;
    private String validadePublicidade;
    private String tipoPermisssao;
    private InputStream inputStreamImagem;
    private InputStream inputStreamChancela;
    private List<VoCredencialTransporteRBTrans> currentObject;

    private byte[] imagem;

    public VoCredencialTransporteRBTrans() {
        currentObject = new ArrayList<>();
        currentObject.add(this);
    }

    public List<VoCredencialTransporteRBTrans> getCurrentObject() {
        return currentObject;
    }

    public void setCurrentObject(List<VoCredencialTransporteRBTrans> currentObject) {
        this.currentObject = currentObject;
    }

    public String getNomeReduzido() {
        return nomeReduzido;
    }

    public void setNomeReduzido(String nomeReduzido) {
        this.nomeReduzido = nomeReduzido;
    }

    public String getNomeCompletoRequerente() {
        return nomeCompletoRequerente;
    }

    public void setNomeCompletoRequerente(String nomeCompletoRequerente) {
        this.nomeCompletoRequerente = nomeCompletoRequerente;
    }

    public String getNumeroCMCRequerente() {
        return numeroCMCRequerente;
    }

    public void setNumeroCMCRequerente(String numeroCMCRequerente) {
        this.numeroCMCRequerente = numeroCMCRequerente;
    }

    public String getNumeroPermissao() {
        return numeroPermissao;
    }

    public void setNumeroPermissao(String numeroPermissao) {
        this.numeroPermissao = numeroPermissao;
    }

    public String getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(String classificacao) {
        this.classificacao = classificacao;
    }

    public String getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(String dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public String getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(String dataValidade) {
        this.dataValidade = dataValidade;
    }

    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    public void setPlacaVeiculo(String placaVeiculo) {
        this.placaVeiculo = placaVeiculo;
    }

    public String getValidadePublicidade() {
        return validadePublicidade;
    }

    public void setValidadePublicidade(String validadePublicidade) {
        this.validadePublicidade = validadePublicidade;
    }

    public String getTipoPermisssao() {
        return tipoPermisssao;
    }

    public void setTipoPermisssao(String tipoPermisssao) {
        this.tipoPermisssao = tipoPermisssao;
    }

    public InputStream getInputStreamImagem() {
        return inputStreamImagem;
    }

    public void setInputStreamImagem(InputStream inputStreamImagem) {
        this.inputStreamImagem = inputStreamImagem;
    }

    public byte[] getImagem() {
        return imagem;
    }

    public void setImagem(byte[] imagem) {
        this.imagem = imagem;
        this.inputStreamImagem = new ByteArrayInputStream(imagem);
    }

    public InputStream getInputStreamChancela() {
        return inputStreamChancela;
    }

    public void setInputStreamChancela(InputStream inputStreamChancela) {
        this.inputStreamChancela = inputStreamChancela;
    }
}
