/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades.rh.concursos;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Felipe Marinzeck
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Concursos")
@Etiqueta("Banca Examinadora")
public class MembroBancaExaminadora extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private BancaExaminadora bancaExaminadora;
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Servidor")
    private ContratoFP servidor;

    public ContratoFP getServidor() {
        return servidor;
    }

    public void setServidor(ContratoFP servidor) {
        this.servidor = servidor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BancaExaminadora getBancaExaminadora() {
        return bancaExaminadora;
    }

    public void setBancaExaminadora(BancaExaminadora bancaExaminadora) {
        this.bancaExaminadora = bancaExaminadora;
    }
}
