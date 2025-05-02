/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.exception;

import br.com.webpublico.enums.SummaryMessages;
import com.google.common.collect.Lists;

import javax.ejb.ApplicationException;
import javax.faces.application.FacesMessage;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Felipe Marinzeck
 */
@ApplicationException(rollback = true)
public class ValidacaoException extends RuntimeException {

    public static final String ERRO_GERAR_RELATORIO = "Ocorreu um erro ao gerar o relatório, se o problema persistir entre em contato com o suporte técnico.";
    private final List<FacesMessage> mensagens;
    public boolean validou;

    public ValidacaoException() {
        mensagens = new ArrayList<>();
        validou = true;
    }

    public ValidacaoException(String message) {
        super(message);
        mensagens = new ArrayList<>();
        validou = true;
        adicionarMensagemDeOperacaoNaoPermitida(message);
    }

    public ValidacaoException(String message, Throwable e) {
        super(message, e);
        mensagens = new ArrayList<>();
        validou = true;
        adicionarMensagemDeOperacaoNaoPermitida(message);
    }

    public void lancarException() {
        if (temMensagens()) {
            throw this;
        }
    }

    public void adicionarValidacaoException(ValidacaoException ve) {
        mensagens.addAll(ve.getMensagens());
    }

    public void adicionarMensagemError(SummaryMessages summary, String detail) {
        if(summary.equals(SummaryMessages.ATENCAO) || summary.equals(SummaryMessages.OPERACAO_REALIZADA)) {
            throw new IllegalArgumentException("O SummaryMessage do tipo " + summary + " não pode ser utilizado em uma mensagem de erro.");
        }

        mensagens.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, summary.getDescricao(), detail));
        validou = false;
    }

    public void adicionarMensagemWarn(SummaryMessages summary, String detail) {
        if(!summary.equals(SummaryMessages.ATENCAO)) {
            throw new IllegalArgumentException("O SummaryMessage do tipo " + summary + " não pode ser utilizado em uma mensagem de alerta.");
        }

        mensagens.add(new FacesMessage(FacesMessage.SEVERITY_WARN, summary.getDescricao(), detail));
    }

    public void adicionarMensagemWarn(String summary, String detail) {
        mensagens.add(new FacesMessage(FacesMessage.SEVERITY_WARN, summary, detail));
    }

    public void adicionarMensagemInfo(SummaryMessages summary, String detail) {
        if(!summary.equals(SummaryMessages.OPERACAO_REALIZADA)) {
            throw new IllegalArgumentException("O SummaryMessage do tipo " + summary + " não pode ser utilizado em uma mensagem de informação.");
        }

        mensagens.add(new FacesMessage(FacesMessage.SEVERITY_INFO, summary.getDescricao(), detail));
    }

    public void adicionarMensagensError(List<FacesMessage> msgs) {
        for (FacesMessage msg : msgs) {
            mensagens.add(msg);
        }

        validou = false;
    }

    public ValidacaoException adicionarMensagemDeCampoObrigatorio(String detail) {
        mensagens.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), detail));
        return this;
    }

    public ValidacaoException adicionarMensagemDeOperacaoNaoPermitida(String detail) {
        mensagens.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), detail));
        return this;
    }

    public ValidacaoException adicionarMensagemDeOperacaoNaoPermitida(String summary, String detail) {
        mensagens.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail));
        return this;
    }

    public ValidacaoException adicionarMensagemDeOperacaoNaoRealizada(String detail) {
        mensagens.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), detail));
        return this;
    }

    public void adicionarMensagemErroGerarRelatorio() {
        mensagens.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), ERRO_GERAR_RELATORIO));
    }

    public boolean temMensagens() {
        return !mensagens.isEmpty();
    }

    public List<FacesMessage> getMensagens() {
        return mensagens;
    }

    public List<FacesMessage> getAllMensagens() {
        if(mensagens != null && mensagens.isEmpty()){
            return Lists.newArrayList(new FacesMessage(getMessage()));
        }
        return mensagens;
    }

}
