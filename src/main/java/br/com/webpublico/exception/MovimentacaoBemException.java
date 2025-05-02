package br.com.webpublico.exception;

import br.com.webpublico.enums.SummaryMessages;

import javax.ejb.ApplicationException;
import javax.faces.application.FacesMessage;
import java.util.ArrayList;
import java.util.List;

@ApplicationException(rollback = true)
public class MovimentacaoBemException extends RuntimeException {

    private final List<FacesMessage> mensagens;

    public MovimentacaoBemException() {
        mensagens = new ArrayList<>();
    }

    public void lancarException() {
        if (temMensagens()) {
            throw this;
        }
    }

    public MovimentacaoBemException adicionarMensagemDeOperacaoNaoPermitida(String detail) {
        mensagens.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), detail));
        return this;
    }

    public boolean temMensagens() {
        return !mensagens.isEmpty();
    }

    public List<FacesMessage> getMensagens() {
        return mensagens;
    }
}
