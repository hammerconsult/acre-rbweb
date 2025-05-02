/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Paschualleto
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Contabil")
public class AditivosConvenioDespesa extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ConvenioDespesa convenioDespesa;
    @ManyToOne(cascade = CascadeType.ALL)
    @Tabelavel
    private Aditivos aditivos;

    public AditivosConvenioDespesa() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConvenioDespesa getConvenioDespesa() {
        return convenioDespesa;
    }

    public void setConvenioDespesa(ConvenioDespesa convenioDespesa) {
        this.convenioDespesa = convenioDespesa;
    }

    public Aditivos getAditivos() {
        return aditivos;
    }

    public void setAditivos(Aditivos aditivos) {
        this.aditivos = aditivos;
    }

    @Override
    public String toString() {
        return aditivos.toString();
    }
}
