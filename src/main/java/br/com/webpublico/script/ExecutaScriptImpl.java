/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.script;

import br.com.webpublico.enums.TiposErroScript;
import br.com.webpublico.interfaces.ExecutaScript;

import javax.script.Invocable;
import javax.script.ScriptException;

/**
 * @author Gécen Dacome De Marchi
 */
public class ExecutaScriptImpl implements ExecutaScript {

    private Invocable invocable;
//    private Object objectLibJavaScript;

    /**
     * @param invocable           Objeto com o javascript avaliado.
     * @param objectLibJavaScript Referência ao objeto javascript que possui os métodos para invocação.
     */
    public ExecutaScriptImpl(Invocable invocable) {
        this.invocable = invocable;
//        this.objectLibJavaScript = objectLibJavaScript;
    }

    /**
     * Invoca a função do objeto passado no construtor
     *
     * @param <T>        Tipo que a função na biblioteca javascript retorna
     * @param nomeFuncao Nome da função que será invocada
     * @param clazz      Classe do tipo de retorno da função javascript
     * @param args       Argumentos da função javascript
     * @return Resultado da invocação da função
     */
    @Override
    public <T> Resultado<T> invocar(String nomeFuncao, Class<T> clazz, Object... args) {
        Resultado<T> resultado = new Resultado<T>();
        try {
            T valor = clazz.cast(invocable.invokeFunction(nomeFuncao, args));
            resultado.setValor(valor);
        } catch (ScriptException ex) {
            resultado.configuraErro(TiposErroScript.ERRO_GENERICO_EXECUCAO_SCRIPT, nomeFuncao, ex.getMessage());
        } catch (NoSuchMethodException ex) {
            resultado.configuraErro(TiposErroScript.ERRO_FUNCAO_NAO_ENCONTRADA, nomeFuncao, ex.getMessage());
        } catch (ClassCastException ex) {
            resultado.configuraErro(TiposErroScript.ERRO_TIPO_RETORNO_INCOMPATIVEL, nomeFuncao, ex.getMessage());
        } catch (Exception ex) {
            resultado.configuraErro(TiposErroScript.ERRO_DESCONHECIDO, nomeFuncao, ex.getMessage());
        }
        return resultado;
    }
}
