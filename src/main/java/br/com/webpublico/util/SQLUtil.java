package br.com.webpublico.util;

import javax.persistence.Entity;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Wellington Abdo on 26/08/2016.
 */
public class SQLUtil implements Serializable {

    public static String montarClausulaIn(List registros) {
        StringBuilder in = new StringBuilder();
        if (registros != null && !registros.isEmpty()) {
            String juncao = "(";
            for (Object obj : registros) {
                String valor = obj.toString();
                if (obj.getClass().isAnnotationPresent(Entity.class)) {
                    valor = Persistencia.getId(obj).toString();
                }
                in.append(juncao);
                in.append(valor);
                juncao = ", ";
            }
            in.append(") ");
        }
        return in.toString();
    }

    public static String criarCondicaoWhere(String juncao, String campo, String operador, Object valor) {
        StringBuilder condicao = new StringBuilder();
        if (valor != null && !valor.toString().trim().isEmpty()) {
            if (valor.getClass().isAnnotationPresent(Entity.class)) {
                valor = Persistencia.getId(valor);
            }else if(valor instanceof List){
                valor = SQLUtil.montarClausulaIn((List) valor);
            }else if (valor instanceof String) {
                valor = "'" + valor + "'";
            }
            condicao.append(" ").append(juncao).append(" ").append(campo).append(" ").append(operador).append(" ").append(valor).append(" ");
        }
        return condicao.toString();
    }
}
