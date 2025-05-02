/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * @author Felipe Marinzeck
 */
@GrupoDiagrama(nome = "Contratos")
@Entity
@Audited
public class FiscalInternoContrato extends FiscalContrato {

    @ManyToOne
    @Etiqueta("Servidor")
    private ContratoFP servidor;

    @ManyToOne
    @Etiqueta("Pessoa Física")
    private PessoaFisica servidorPF;

    public ContratoFP getServidor() {
        return servidor;
    }

    public void setServidor(ContratoFP servidor) {
        if (servidor != null) {
            setServidorPF(servidor.getMatriculaFP().getPessoa());
        }
        this.servidor = servidor;
    }

    public PessoaFisica getServidorPF() {
        return servidorPF;
    }

    public void setServidorPF(PessoaFisica servidorPF) {
        this.servidorPF = servidorPF;
    }

    @Override
    public String getResponsavel() {
        if (this.servidor != null) {
            return "" + this.servidor;
        } else if (servidorPF != null) {
            return servidorPF.toString();
        }
        return "";
    }
}
