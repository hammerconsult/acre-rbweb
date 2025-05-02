/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author WebPublico07
 */
@Entity

@Audited
@Etiqueta("Tomador de Serviço")
@GrupoDiagrama(nome = "RecursosHumanos")
public class TomadorDeServico implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Tomador de Serviço")
    @OneToOne
    private PessoaJuridica tomador;

    public PessoaJuridica getTomador() {
        return tomador;
    }

    public void setTomador(PessoaJuridica tomador) {
        this.tomador = tomador;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TomadorDeServico)) {
            return false;
        }
        TomadorDeServico other = (TomadorDeServico) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return tomador.getNomeFantasia() + " --- " + tomador.getCpf_Cnpj();
    }
}
