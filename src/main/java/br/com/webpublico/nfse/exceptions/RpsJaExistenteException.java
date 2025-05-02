package br.com.webpublico.nfse.exceptions;


public class RpsJaExistenteException extends Exception{
    final String mensagem;

    public RpsJaExistenteException(String mensagem) {
        super(mensagem);
        this.mensagem = mensagem;
    }
}
