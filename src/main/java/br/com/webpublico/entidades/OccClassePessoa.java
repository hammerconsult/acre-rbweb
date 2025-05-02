/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import java.io.Serializable;
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
public class OccClassePessoa extends OrigemContaContabil implements Serializable {

    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Classe de Pessoa")
    private ClasseCredor classePessoa;

    public OccClassePessoa() {
    }

    public ClasseCredor getClassePessoa() {
        return classePessoa;
    }

    public void setClassePessoa(ClasseCredor classePessoa) {
        this.classePessoa = classePessoa;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.classePessoa);
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
        final OccClassePessoa other = (OccClassePessoa) obj;
        if (!Objects.equals(this.classePessoa, other.classePessoa)) {
            return false;
        }
        return true;
    }

}
