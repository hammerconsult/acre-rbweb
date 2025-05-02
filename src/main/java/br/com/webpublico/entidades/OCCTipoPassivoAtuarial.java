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
public class OCCTipoPassivoAtuarial extends OrigemContaContabil implements Serializable {

    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo Passivo Atuarial")
    private TipoPassivoAtuarial tipoPassivoAtuarial;

    public OCCTipoPassivoAtuarial() {
    }

     public OCCTipoPassivoAtuarial(TipoPassivoAtuarial tipoPassivoAtuarial, TagOCC tagOCC, ContaContabil contaContabil, Date inicioVigencia, Date fimVigencia, Boolean reprocessar, OrigemContaContabil origem) {
        super(tagOCC, contaContabil, inicioVigencia, fimVigencia, reprocessar, origem);
        this.tipoPassivoAtuarial = tipoPassivoAtuarial;
    }

    public TipoPassivoAtuarial getTipoPassivoAtuarial() {
        return tipoPassivoAtuarial;
    }

    public void setTipoPassivoAtuarial(TipoPassivoAtuarial tipoPassivoAtuarial) {
        this.tipoPassivoAtuarial = tipoPassivoAtuarial;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.tipoPassivoAtuarial);
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
        final OCCTipoPassivoAtuarial other = (OCCTipoPassivoAtuarial) obj;
        if (!Objects.equals(this.tipoPassivoAtuarial, other.tipoPassivoAtuarial)) {
            return false;
        }
        return true;
    }


}
