package br.com.webpublico.entidades;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 08/05/14
 * Time: 10:49
 * To change this template use File | Settings | File Templates.
 */

@Entity
public class AtributosPropriedadeAgata implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Propriedade propriedade;
    private Long atr01cdgPesFis;

    public AtributosPropriedadeAgata() {
    }

    public AtributosPropriedadeAgata(Long atr01cdgPesFis) {
        this.atr01cdgPesFis = atr01cdgPesFis;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Propriedade getPropriedade() {
        return propriedade;
    }

    public void setPropriedade(Propriedade propriedade) {
        this.propriedade = propriedade;
    }

    public Long getAtr01cdgPesFis() {
        return atr01cdgPesFis;
    }

    public void setAtr01cdgPesFis(Long atr01cdgPesFis) {
        this.atr01cdgPesFis = atr01cdgPesFis;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AtributosPropriedadeAgata that = (AtributosPropriedadeAgata) o;

        if (atr01cdgPesFis != null ? !atr01cdgPesFis.equals(that.atr01cdgPesFis) : that.atr01cdgPesFis != null)
            return false;
        if (!id.equals(that.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (atr01cdgPesFis != null ? atr01cdgPesFis.hashCode() : 0);
        return result;
    }
}
