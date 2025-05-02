package br.com.webpublico.nfse.exceptions;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by rodolfo on 16/10/17.
 */
public class NfseOperacaoNaoPermitidaException extends RuntimeException {

    public List<String> mensagens;

    public NfseOperacaoNaoPermitidaException() {
        super();
        mensagens = Lists.newArrayList();
    }

    public NfseOperacaoNaoPermitidaException(String message) {
        super(message);
        mensagens = Lists.newArrayList(message);
    }

    public NfseOperacaoNaoPermitidaException(String message, Throwable e) {
        super(message, e);
        mensagens = Lists.newArrayList();
    }

    public NfseOperacaoNaoPermitidaException(String message, List<String> mensagens) {
        super(message);
        this.mensagens = mensagens;
    }

    public boolean temMensagens() {
        return !mensagens.isEmpty();
    }

    public List<String> getMensagens() {
        return mensagens;
    }

    public void adicionarMensagem(List<String> mensagens) {
        for (String mensagen : mensagens) {
            adicionarMensagem(mensagen);
        }
    }

    public void adicionarMensagem(String mensagem) {
        mensagens.add(mensagem);
    }

    public void lancarException() throws NfseOperacaoNaoPermitidaException {
        if (temMensagens()) {
            throw this;
        }
    }

    @Override
    public String toString() {
        String message = getLocalizedMessage();
        return (message != null) ? message : "";
    }
}
