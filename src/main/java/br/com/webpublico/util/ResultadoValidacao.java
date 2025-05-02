package br.com.webpublico.util;

import br.com.webpublico.entidades.PermissaoTransporte;

import java.util.ArrayList;
import java.util.List;


public class ResultadoValidacao {
    private Long idObjetoSalvo;
    private boolean validado = true;
    private List<MensagemValidacao> mensagens = new ArrayList<MensagemValidacao>();


    public Long getIdObjetoSalvo() {
        return idObjetoSalvo;
    }

    public void setIdObjetoSalvo(Long idObjetoSalvo) {
        this.idObjetoSalvo = idObjetoSalvo;
    }

    public ResultadoValidacao(boolean validado) {
        this.validado = validado;
    }

    public ResultadoValidacao() {
    }

    public void limpaMensagens() {
        mensagens.clear();
    }

    public boolean isValidado() {
        return validado;
    }

    public void addMensagem(MensagemValidacao msg) {
        this.mensagens.add(msg);
    }

    public void invalida() {
        this.validado = false;
    }

    public void valida() {
        this.validado = true;
    }

    public List<MensagemValidacao> getMensagens() {
        return new ArrayList<MensagemValidacao>(mensagens);
    }

    //erro - invalida obrigatoriamente
    public void addErro(String clientId, String summary, String detail) {
        this.invalida();
        this.addMensagem(MensagemValidacao.error(clientId, summary, detail));
    }

    //erro fatal - invalida obrigatoriamente
    public void addFatal(String clientId, String summary, String detail) {
        this.invalida();
        this.addMensagem(MensagemValidacao.fatal(clientId, summary, detail));
    }

    //alerta - pode ivalidar ou não
    public void addWarn(String clientId, String summary, String detail, boolean invalidaResultado) {
        if (invalidaResultado) {
            this.invalida();
        }
        this.addMensagem(MensagemValidacao.warn(clientId, summary, detail));
    }

    //informação - não invalida
    public void addInfo(String clientId, String summary, String detail) {
        this.addMensagem(MensagemValidacao.info(clientId, summary, detail));
    }
}
