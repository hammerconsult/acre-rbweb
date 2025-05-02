package br.com.webpublico.exception;

import br.com.webpublico.entidades.Tributo;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class ContaReceitaException extends Exception {

    public ContaReceitaException() {
    }

    public ContaReceitaException(String message) {
        super(message);
    }

    public ContaReceitaException(Tributo tributo, Integer exercicio) {
        super("O tributo " + tributo.getCodigo() + " - " + tributo.getDescricao() + " não está devidamente configurado. Insira os tributos de acréscimos e/ou as contas de receita corretas para o exercício " + exercicio);
    }
}
