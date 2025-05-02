/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ErroEconsig;
import br.com.webpublico.entidades.LancamentoFP;
import org.primefaces.model.UploadedFile;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mga
 */
public class ImportaMovimentoFinanceiro {

    private Integer contadorProblema = 0;
    private Integer contadorTotal = 0;
    private Integer contadorRejeitados = 0;
    private Integer contadorOk = 0;
    private Integer secretariaInexiste = 0;
    private Integer servidorNaoPertence = 0;
    private Integer eventoNaoEncontrado = 0;
    private Integer eventoEConsigNaoRelac = 0;
    private Integer consignatariaNaoEncontrado = 0;
    private Integer contador = 0;
    private Integer totalArquivosSeremSalvos = 0;
    boolean liberaCaixaDialogo = true;
    List<LancamentoFP> lancamentos = new ArrayList<LancamentoFP>();
    private List<String> linhaProblema = new ArrayList<String>();
    private UploadedFile file;
    private boolean podeMostrarMensagem;
    private int mes;
    private int ano;
    private boolean cancelar;
    private List<ErroEconsig> errosEconsig;

    public List<ErroEconsig> getErrosEconsig() {
        return errosEconsig;
    }

    public void setErrosEconsig(List<ErroEconsig> errosEconsig) {
        this.errosEconsig = errosEconsig;
    }

    public Integer getContadorProblema() {
        return contadorProblema;
    }

    public void setContadorProblema(Integer contadorProblema) {
        this.contadorProblema = contadorProblema;
    }

    public Integer getContadorTotal() {
        return contadorTotal;
    }

    public void setContadorTotal(Integer contadorTotal) {
        this.contadorTotal = contadorTotal;
    }

    public Integer getContadorRejeitados() {
        return contadorRejeitados;
    }

    public void setContadorRejeitados(Integer contadorRejeitados) {
        this.contadorRejeitados = contadorRejeitados;
    }

    public Integer getContadorOk() {
        return contadorOk;
    }

    public void setContadorOk(Integer contadorOk) {
        this.contadorOk = contadorOk;
    }

    public Integer getSecretariaInexiste() {
        return secretariaInexiste;
    }

    public void setSecretariaInexiste(Integer secretariaInexiste) {
        this.secretariaInexiste = secretariaInexiste;
    }

    public Integer getServidorNaoPertence() {
        return servidorNaoPertence;
    }

    public void setServidorNaoPertence(Integer servidorNaoPertence) {
        this.servidorNaoPertence = servidorNaoPertence;
    }

    public Integer getEventoNaoEncontrado() {
        return eventoNaoEncontrado;
    }

    public void setEventoNaoEncontrado(Integer eventoNaoEncontrado) {
        this.eventoNaoEncontrado = eventoNaoEncontrado;
    }

    public Integer getEventoEConsigNaoRelac() {
        return eventoEConsigNaoRelac;
    }

    public void setEventoEConsigNaoRelac(Integer eventoEConsigNaoRelac) {
        this.eventoEConsigNaoRelac = eventoEConsigNaoRelac;
    }

    public Integer getConsignatariaNaoEncontrado() {
        return consignatariaNaoEncontrado;
    }

    public void setConsignatariaNaoEncontrado(Integer consignatariaNaoEncontrado) {
        this.consignatariaNaoEncontrado = consignatariaNaoEncontrado;
    }

    public Integer getContador() {
        return contador;
    }

    public void setContador(Integer contador) {
        this.contador = contador;
    }

    public boolean isLiberaCaixaDialogo() {
        return liberaCaixaDialogo;
    }

    public void setLiberaCaixaDialogo(boolean liberaCaixaDialogo) {
        this.liberaCaixaDialogo = liberaCaixaDialogo;
    }

    public List<LancamentoFP> getLancamentos() {
        return lancamentos;
    }

    public void setLancamentos(List<LancamentoFP> lancamentos) {
        this.lancamentos = lancamentos;
    }

    public List<String> getLinhaProblema() {
        return linhaProblema;
    }

    public void setLinhaProblema(List<String> linhaProblema) {
        this.linhaProblema = linhaProblema;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public ImportaMovimentoFinanceiro() {
        limpa();

    }

    public void somaContadorTotal() {
        contadorTotal++;
    }
    public void somaContador() {
        contador++;
    }

    public void somarEventoNaoEncontrado() {
        somarContadorRejeitados();
        eventoNaoEncontrado++;
    }

    public void somarConsignitarioNaoEncontrado() {
        somarContadorRejeitados();
        consignatariaNaoEncontrado++;
    }

    public void somarEventoEconsigNaoRelacionado() {
        somarContadorRejeitados();
        eventoEConsigNaoRelac++;
    }

    public void somarSecretariaInexistente() {
        somarContadorRejeitados();
        secretariaInexiste++;
    }

    public void somarServidorNaoPertence() {
        somarContadorRejeitados();
        servidorNaoPertence++;
    }

    public void somarContadorRejeitados() {
        contadorRejeitados++;
    }

    public void somaTotalArquivosSeremSalvos() {
        totalArquivosSeremSalvos++;
    }

    public Integer getTotalArquivosSeremSalvos() {
        return totalArquivosSeremSalvos;
    }

    public void setTotalArquivosSeremSalvos(Integer totalArquivosSeremSalvos) {
        this.totalArquivosSeremSalvos = totalArquivosSeremSalvos;
    }

    public boolean isPodeMostrarMensagem() {
        return podeMostrarMensagem;
    }

    public void setPodeMostrarMensagem(boolean podeMostrarMensagem) {
        this.podeMostrarMensagem = podeMostrarMensagem;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public void limpa() {
        contadorProblema = 0;
        contadorTotal = 0;
        contadorRejeitados = 0;
        contadorOk = 0;
        secretariaInexiste = 0;
        servidorNaoPertence = 0;
        eventoNaoEncontrado = 0;
        eventoEConsigNaoRelac = 0;
        consignatariaNaoEncontrado = 0;
        contador = 0;
        liberaCaixaDialogo = false;
        totalArquivosSeremSalvos = 0;
        lancamentos = new ArrayList<LancamentoFP>();
        linhaProblema = new ArrayList<String>();
        file = null;
        cancelar = false;
        podeMostrarMensagem = false;
        errosEconsig = new ArrayList<>();

    }

    public boolean isCancelar() {
        return cancelar;
    }

    public void setCancelar(boolean cancelar) {
        this.cancelar = cancelar;
    }

    public void cancelar() {
        cancelar = true;
    }
}
