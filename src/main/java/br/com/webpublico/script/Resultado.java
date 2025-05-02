/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.script;

import br.com.webpublico.enums.TiposErroScript;

/**
 * Classe que representa o resultado da execução de uma função javascript
 *
 * @author Gécen Dacome De Marchi
 */
public class Resultado<E> {

    /**
     * Item de erro
     */
    private ItemErroScript itemErroScript;

    /**
     * Valor retornado pela função javascript
     */
    private E valor;

    /**
     * Retorna o valor
     *
     * @return Valor retornado pela função javascript
     */
    public E getValor() {
        return valor;
    }

    /**
     * Atribui o valor da função javascript
     *
     * @param valor Valor retornado pela função javascript
     */
    public void setValor(E valor) {
        this.valor = valor;
    }

    /**
     * Configura o resultado com um item de erro
     *
     * @param tiposErroScript   Tipo de erro retorntado na execução do javascript
     * @param nomeFunction      Nome da função que causou o erro
     * @param mensagemException Mensagem de erro retornada pela excessão
     */
    public void configuraErro(TiposErroScript tiposErroScript, String nomeFunction, String mensagemException) {
        itemErroScript = new ItemErroScript(tiposErroScript, nomeFunction, tiposErroScript.getMessage(), mensagemException);
    }

    /**
     * Retorna um @ItemErroScript caso existir
     *
     * @return Item de erro
     */
    public ItemErroScript getItemErroScript() {
        return itemErroScript;
    }

    /**
     * Verifica se tem erro
     *
     * @return true se o resultado tem um erro
     */
    public boolean temErro() {
        return itemErroScript != null;
    }

    /**
     * Verifica se tem erro
     *
     * @return true se o resultado não tem um erro
     */
    public boolean naoTemErro() {
        return itemErroScript == null;
    }

}
