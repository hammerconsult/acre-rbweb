package br.com.webpublico.negocios.jdbc;

import br.com.webpublico.enums.TagValor;

import java.util.Date;

/**
 * Created by venom on 24/02/15.
 */
public class MapCLP {
    private Long idEvento;
    private TagValor tagValor;
    private Date data;

    public MapCLP(Long idEvento, TagValor tagValor, Date data) {
        this.idEvento = idEvento;
        this.tagValor = tagValor;
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapCLP mapCLP = (MapCLP) o;

        if (!data.equals(mapCLP.data)) return false;
        if (!idEvento.equals(mapCLP.idEvento)) return false;
        if (tagValor != mapCLP.tagValor) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = idEvento.hashCode();
        result = 31 * result + tagValor.hashCode();
        result = 31 * result + data.hashCode();
        return result;
    }
}
