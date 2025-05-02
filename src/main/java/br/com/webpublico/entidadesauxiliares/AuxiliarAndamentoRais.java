/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ArquivoRAIS;
import br.com.webpublico.util.AssistenteBarraProgresso;
import com.google.common.collect.Lists;

import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * @author Felipe Marinzeck
 */
public class AuxiliarAndamentoRais extends AssistenteBarraProgresso {

    private List<String> log;
    private Boolean parado;
    // RAIS
    private ArquivoRAIS arquivoRAIS;
    private List<Long> itens;
    private RegistroRAISTipo0 registroRAISTipo0;
    private RegistroRAISTipo1 registroRAISTipo1;
    private List<RegistroRAISTipo2> registrosRAISTipo2;
    private RegistroRAISTipo9 registroRAISTipo9;

    public AuxiliarAndamentoRais() {
        super();
        iniciarProcesso();
    }

    public void iniciarProcesso() {
        setDescricaoProcesso("Exportação do Arquivo RAIS");
        this.log = Lists.newArrayList();
        this.registrosRAISTipo2 = Lists.newArrayList();
        this.parado = Boolean.FALSE;
    }

    public List<String> getLog() {
        return log;
    }

    public void setLog(List<String> log) {
        this.log = log;
    }

    public void pararProcessamento() {
        this.parado = Boolean.TRUE;
    }

    public Boolean getParado() {
        return parado;
    }

    public void setParado(Boolean parado) {
        this.parado = parado;
    }

    public String getSomenteStringDoLog() {
        try {
            return this.log.toString().replace("[", "").replace("]", "").replace(",", "");
        } catch (ConcurrentModificationException cme) {
            corrigeLog();
            return "";
        } catch (NullPointerException npe) {
            return "";
        }
    }

    public void corrigeLog() {
        log = Lists.newArrayList(log);
    }

    public ArquivoRAIS getArquivoRAIS() {
        return arquivoRAIS;
    }

    public void setArquivoRAIS(ArquivoRAIS arquivoRAIS) {
        this.arquivoRAIS = arquivoRAIS;
    }

    public RegistroRAISTipo0 getRegistroRAISTipo0() {
        return registroRAISTipo0;
    }

    public void setRegistroRAISTipo0(RegistroRAISTipo0 registroRAISTipo0) {
        this.registroRAISTipo0 = registroRAISTipo0;
    }

    public RegistroRAISTipo1 getRegistroRAISTipo1() {
        return registroRAISTipo1;
    }

    public void setRegistroRAISTipo1(RegistroRAISTipo1 registroRAISTipo1) {
        this.registroRAISTipo1 = registroRAISTipo1;
    }

    public List<RegistroRAISTipo2> getRegistrosRAISTipo2() {
        return registrosRAISTipo2;
    }

    public RegistroRAISTipo9 getRegistroRAISTipo9() {
        return registroRAISTipo9;
    }

    public void setRegistroRAISTipo9(RegistroRAISTipo9 registroRAISTipo9) {

        this.registroRAISTipo9 = registroRAISTipo9;
    }

    public List<Long> getItens() {
        return itens;
    }

    public void setItens(List<Long> itens) {
        this.itens = itens;
    }

    public synchronized void adicionar(RegistroRAISTipo2 registroRAISTipo2) {
        this.conta();
        this.registrosRAISTipo2.add(registroRAISTipo2);
    }
}
