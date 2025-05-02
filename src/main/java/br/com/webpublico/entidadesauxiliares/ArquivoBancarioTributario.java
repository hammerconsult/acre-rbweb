package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.SuperEntidade;

/**
 * Created by William on 10/04/2017.
 */
public abstract class ArquivoBancarioTributario extends SuperEntidade {

    private Integer totalLinhas;

    public ArquivoBancarioTributario() {
        totalLinhas = 0;
    }

    public Integer getTotalLinhas() {
        return totalLinhas;
    }

    public void setTotalLinhas(Integer totalLinhas) {
        this.totalLinhas = totalLinhas;
    }

    public void contarLinha() {
        totalLinhas++;
    }

    public abstract int getQuantidadeRegistros();
}
