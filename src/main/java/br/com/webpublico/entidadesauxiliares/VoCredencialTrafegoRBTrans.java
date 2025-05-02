package br.com.webpublico.entidadesauxiliares;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: André Gustavo
 * Date: 03/03/14
 * Time: 11:19
 */
public class VoCredencialTrafegoRBTrans implements Serializable {
    private String placa;
    private String numeroPermissao;
    private String nomePermissionario;
    private String cpfPermissionario;
    private String numeroCMC;
    private String marcaModelo;
    private String anoFabricacao;
    private String dataEmissao;
    private String validaAte;
    private String tipoPermissao;
    private String pontoTaxi;
    private Boolean ponto;
    private InputStream inputStreamChancela;
    private List<VoCredencialTrafegoRBTrans> currentObject;

    public VoCredencialTrafegoRBTrans() {
        currentObject = new ArrayList<>();
        ponto = false;
        currentObject.add(this);
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getNumeroPermissao() {
        return numeroPermissao;
    }

    public void setNumeroPermissao(String numeroPermissao) {
        this.numeroPermissao = numeroPermissao;
    }

    public String getNomePermissionario() {
        return nomePermissionario;
    }

    public void setNomePermissionario(String nomePermissionario) {
        this.nomePermissionario = nomePermissionario;
    }

    public String getCpfPermissionario() {
        return cpfPermissionario;
    }

    public void setCpfPermissionario(String cpfPermissionario) {
        this.cpfPermissionario = cpfPermissionario;
    }

    public String getNumeroCMC() {
        return numeroCMC;
    }

    public void setNumeroCMC(String numeroCMC) {
        this.numeroCMC = numeroCMC;
    }

    public String getMarcaModelo() {
        return marcaModelo;
    }

    public void setMarcaModelo(String marcaModelo) {
        this.marcaModelo = marcaModelo;
    }

    public String getAnoFabricacao() {
        return anoFabricacao;
    }

    public void setAnoFabricacao(String anoFabricacao) {
        this.anoFabricacao = anoFabricacao;
    }

    public String getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(String dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public String getValidaAte() {
        return validaAte;
    }

    public void setValidaAte(String validaAte) {
        this.validaAte = validaAte;
    }

    public String getTipoPermissao() {
        return tipoPermissao;
    }

    public void setTipoPermissao(String tipoPermissao) {
        this.tipoPermissao = tipoPermissao;
    }

    public List<VoCredencialTrafegoRBTrans> getCurrentObject() {
        return currentObject;
    }

    public String getPontoTaxi() {
        return pontoTaxi;
    }

    public void setPontoTaxi(String pontoTaxi) {
        this.pontoTaxi = pontoTaxi;
    }

    public void setCurrentObject(List<VoCredencialTrafegoRBTrans> currentObject) {
        this.currentObject = currentObject;
    }

    public Boolean getPonto() {
        return ponto;
    }

    public void setPonto(Boolean ponto) {
        this.ponto = ponto;
    }

    public InputStream getInputStreamChancela() {
        return inputStreamChancela;
    }

    public void setInputStreamChancela(InputStream inputStreamChancela) {
        this.inputStreamChancela = inputStreamChancela;
    }
}
