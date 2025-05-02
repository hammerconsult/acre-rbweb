package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 22/07/15
 * Time: 09:17
 * To change this template use File | Settings | File Templates.
 */
public class ReceitaFonteItem {

    private String destinacaoRecurso;
    private BigDecimal valor;

    public ReceitaFonteItem() {
    }


    public String getDestinacaoRecurso() {
        return destinacaoRecurso;
    }

    public void setDestinacaoRecurso(String destinacaoRecurso) {
        this.destinacaoRecurso = destinacaoRecurso;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
