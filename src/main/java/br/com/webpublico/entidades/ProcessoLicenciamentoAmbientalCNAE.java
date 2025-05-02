/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.tributario.ProcessoLicenciamentoAmbiental;
import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Pedro
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Tributario")
public class ProcessoLicenciamentoAmbientalCNAE implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CNAE cnae;
    @ManyToOne
    private ProcessoLicenciamentoAmbiental processoLicenciamentoAmbiental;

    public ProcessoLicenciamentoAmbientalCNAE() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CNAE getCnae() {
        return cnae;
    }

    public void setCnae(CNAE cnae) {
        this.cnae = cnae;
    }

    public ProcessoLicenciamentoAmbiental getProcessoLicenciamentoAmbiental() {
        return processoLicenciamentoAmbiental;
    }

    public void setProcessoLicenciamentoAmbiental(ProcessoLicenciamentoAmbiental processoLicenciamentoAmbiental) {
        this.processoLicenciamentoAmbiental = processoLicenciamentoAmbiental;
    }
}
