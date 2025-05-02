package br.com.webpublico.exception;

import br.com.webpublico.ott.enums.TipoRespostaOttDTO;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class ValidacaoExceptionOtt extends RuntimeException {
    private TipoRespostaOttDTO mensagem;

    public ValidacaoExceptionOtt() {
    }

    public ValidacaoExceptionOtt(TipoRespostaOttDTO mensagem) {
        this.mensagem = mensagem;
    }

    public void lancarExceptionOtt() {
        if(temMensagem()){
            throw this;
        }
    }

    public TipoRespostaOttDTO getMensagem() {
        return mensagem;
    }

    public boolean temMensagem(){
        return mensagem != null;
    }
}
