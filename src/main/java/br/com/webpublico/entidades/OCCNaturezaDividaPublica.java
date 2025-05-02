/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import org.hibernate.envers.Audited;

/**
 *
 * @author Edi
 */
@Entity

@Audited
public class OCCNaturezaDividaPublica extends OrigemContaContabil implements Serializable {

    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Natureza da Dívida Pública")
    private CategoriaDividaPublica categoriaDividaPublica;

    public OCCNaturezaDividaPublica() {
    }

    public OCCNaturezaDividaPublica(CategoriaDividaPublica dividaPublica, TagOCC tagOCC, ContaContabil contaContabil, Date inicioVigencia, Date fimVigencia, Boolean reprocessar, OrigemContaContabil origem) {
        super(tagOCC, contaContabil, inicioVigencia, fimVigencia, reprocessar, origem);
        this.categoriaDividaPublica = dividaPublica;
    }

    public CategoriaDividaPublica getCategoriaDividaPublica() {
        return categoriaDividaPublica;
    }

    public void setCategoriaDividaPublica(CategoriaDividaPublica categoriaDividaPublica) {
        this.categoriaDividaPublica = categoriaDividaPublica;
    }


    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.categoriaDividaPublica);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OCCNaturezaDividaPublica other = (OCCNaturezaDividaPublica) obj;
        if (!Objects.equals(this.categoriaDividaPublica, other.categoriaDividaPublica)) {
            return false;
        }
        return true;
    }

}
