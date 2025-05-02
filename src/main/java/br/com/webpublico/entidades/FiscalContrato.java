/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoFiscalContrato;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Felipe Marinzeck
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Contratos")
@Etiqueta("Fiscal do Contrato")
@Inheritance(strategy = InheritanceType.JOINED)
public class FiscalContrato extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Contrato")
    private Contrato contrato;

    @Etiqueta("Tipo")
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private TipoFiscalContrato tipo;

    @Obrigatorio
    @Etiqueta("Inicio de Vigência")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date inicioVigencia;

    @Etiqueta("Fim de Vigência")
    @Temporal(TemporalType.DATE)
    private Date finalVigencia;

    @Etiqueta("Ato Legal")
    @OneToOne
    private AtoLegal atoLegal;

    public FiscalContrato() {
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public TipoFiscalContrato getTipo() {
        return tipo;
    }

    public void setTipo(TipoFiscalContrato tipo) {
        this.tipo = tipo;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public FiscalInternoContrato getFiscalInternoContrato(){
        return (FiscalInternoContrato) this;
    }

    public FiscalExternoContrato getFiscalExternoContrato(){
        return (FiscalExternoContrato) this;
    }

    public String getResponsavel(){
        return "";
    }
}
