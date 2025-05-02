/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@GrupoDiagrama(nome = "Administrativo")
@Audited
@Entity

@Inheritance(strategy = InheritanceType.JOINED)
//@DiscriminatorColumn(name = "dtype")
//@DiscriminatorValue(value = "ConfiguracaoModulo")
public class ConfiguracaoModulo implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Determina a data na qual este vínculo passou a existir.
     */
    @Tabelavel
    @Temporal(TemporalType.DATE)
    private Date desde;

    public ConfiguracaoModulo() {
        desde = new Date();
    }

    public ConfiguracaoModulo(Date desde) {
        this.desde = desde;
    }

    public Date getDesde() {
        return desde;
    }

    public void setDesde(Date desde) {
        this.desde = desde;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ConfiguracaoModulo other = (ConfiguracaoModulo) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

}
