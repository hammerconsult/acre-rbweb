/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.controle.ProcessoParcelamentoControlador;
import br.com.webpublico.entidades.EnderecoCorreio;
import br.com.webpublico.entidades.ParamParcelamentoDivida;
import br.com.webpublico.entidades.ParamParcelamentoTributo;
import br.com.webpublico.entidades.ProcessoParcelamento;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;

import java.util.List;

/**
 * @author Terminal-2
 */
public class DemonstrativoParcelamento {
    private ProcessoParcelamento parcelamento;
    private List<ResultadoParcela> originais;
    private List<ResultadoParcela> originadas;
    private List<ParamParcelamentoDivida> dividasOriginais;
    private String enderecoContribuinte;
    private EnderecoCorreio enderecoFiador;
    private EnderecoCorreio enderecoProcurador;
    private VOPessoa contribuinte;
    private ProcessoParcelamentoControlador.Valores valores;
    private ParamParcelamentoTributo paramDesconto;
    private Boolean mostraDesconto;


    public String getEnderecoContribuinte() {
        return enderecoContribuinte;
    }

    public void setEnderecoContribuinte(String enderecoContribuinte) {
        this.enderecoContribuinte = enderecoContribuinte;
    }

    public EnderecoCorreio getEnderecoFiador() {
        return enderecoFiador;
    }

    public void setEnderecoFiador(EnderecoCorreio enderecoFiador) {
        this.enderecoFiador = enderecoFiador;
    }

    public List<ResultadoParcela> getOriginais() {
        return originais;
    }

    public void setOriginais(List<ResultadoParcela> originais) {
        this.originais = originais;
    }

    public List<ResultadoParcela> getOriginadas() {
        return originadas;
    }

    public void setOriginadas(List<ResultadoParcela> originadas) {
        this.originadas = originadas;
    }

    public ProcessoParcelamento getParcelamento() {
        return parcelamento;
    }

    public void setParcelamento(ProcessoParcelamento parcelamento) {
        this.parcelamento = parcelamento;
    }

    public VOPessoa getContribuinte() {
        return contribuinte;
    }

    public void setContribuinte(VOPessoa contribuinte) {
        this.contribuinte = contribuinte;
    }

    public ProcessoParcelamentoControlador.Valores getValores() {
        return valores;
    }

    public void setValores(ProcessoParcelamentoControlador.Valores valores) {
        this.valores = valores;
    }

    public ParamParcelamentoTributo getParamDesconto() {
        return paramDesconto;
    }

    public void setParamDesconto(ParamParcelamentoTributo paramDesconto) {
        this.paramDesconto = paramDesconto;
    }

    public List<ParamParcelamentoDivida> getDividasOriginais() {
        return dividasOriginais;
    }

    public void setDividasOriginais(List<ParamParcelamentoDivida> dividasOriginais) {
        this.dividasOriginais = dividasOriginais;
    }

    public EnderecoCorreio getEnderecoProcurador() {
        return enderecoProcurador;
    }

    public void setEnderecoProcurador(EnderecoCorreio enderecoProcurador) {
        this.enderecoProcurador = enderecoProcurador;
    }

    public Boolean getMostraDesconto() {
        return mostraDesconto;
    }

    public void setMostraDesconto(Boolean mostraDesconto) {
        this.mostraDesconto = mostraDesconto;
    }
}
