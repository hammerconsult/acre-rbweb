package br.com.webpublico.negocios.jdbc;

import br.com.webpublico.entidades.TagOCC;

/**
 * Created by venom on 20/02/15.
 */
public class MapTagOcc {

    private TagOCC tag;
    private String idObjeto;

    public MapTagOcc() {
    }

    public MapTagOcc(TagOCC tag, String idObjeto) {
        this.tag = tag;
        this.idObjeto = idObjeto;
    }

    public TagOCC getTag() {
        return tag;
    }

    public void setTag(TagOCC tag) {
        this.tag = tag;
    }

    public String getIdObjeto() {
        return idObjeto;
    }

    public void setIdObjeto(String idObjeto) {
        this.idObjeto = idObjeto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapTagOcc mapTagOcc = (MapTagOcc) o;

        if (!idObjeto.equals(mapTagOcc.idObjeto)) return false;
        if (!tag.equals(mapTagOcc.tag)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = tag.hashCode();
        result = 31 * result + idObjeto.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "MapTagOcc{" +
                "tag=" + tag +
                ", idObjeto='" + idObjeto + '\'' +
                '}';
    }
}
