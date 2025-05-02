package br.com.webpublico.entidadesauxiliares;

import com.beust.jcommander.internal.Lists;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class DadosImovelLaudoITBI implements Serializable {
    private Long idCadastro;

    private String inscricaoCadastral;
    private String incra;
    private String endereco;
    private String setor;
    private String quadra;
    private String lote;
    private String complemento;
    private String loteamento;
    private String propriedade;
    private String tipoArea;
    private String areaLote;

    private List<DadosConstrucaoImovelLaudoITBI> construcoes;

    public DadosImovelLaudoITBI() {
        this.areaLote = "0";
        this.construcoes = Lists.newArrayList();
    }

    public Long getIdCadastro() {
        return idCadastro;
    }

    public void setIdCadastro(Long idCadastro) {
        this.idCadastro = idCadastro;
    }

    public String getInscricaoCadastral() {
        return inscricaoCadastral;
    }

    public void setInscricaoCadastral(String inscricaoCadastral) {
        this.inscricaoCadastral = inscricaoCadastral;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }

    public String getQuadra() {
        return quadra;
    }

    public void setQuadra(String quadra) {
        this.quadra = quadra;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getLoteamento() {
        return loteamento;
    }

    public void setLoteamento(String loteamento) {
        this.loteamento = loteamento;
    }

    public String getPropriedade() {
        return propriedade;
    }

    public void setPropriedade(String propriedade) {
        this.propriedade = propriedade;
    }

    public String getTipoArea() {
        return tipoArea;
    }

    public void setTipoArea(String tipoArea) {
        this.tipoArea = tipoArea;
    }

    public String getAreaLote() {
        return areaLote;
    }

    public void setAreaLote(String areaLote) {
        this.areaLote = areaLote;
    }

    public List<DadosConstrucaoImovelLaudoITBI> getConstrucoes() {
        return construcoes;
    }

    public void setConstrucoes(List<DadosConstrucaoImovelLaudoITBI> construcoes) {
        this.construcoes = construcoes;
    }

    public String getIncra() {
        return incra;
    }

    public void setIncra(String incra) {
        this.incra = incra;
    }
}
