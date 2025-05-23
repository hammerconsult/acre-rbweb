package br.com.webpublico.entidades.usertype;


import br.com.webpublico.entidades.AtoLegal;
import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.ModalidadeContratoFP;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@GrupoDiagrama(nome = "RecursosHumanos")
@Entity
@Audited
@Etiqueta("Vigência da Modalidade de Contrato")
public class ModalidadeContratoFPData extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta("Modalidade de Contrato")
    private ModalidadeContratoFP modalidadeContratoFP;
    @ManyToOne
    @Etiqueta("Contrato FP")
    private ContratoFP contratoFP;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Inicio da Vigência")
    private Date inicioVigencia;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Final da Vigência")
    private Date finalVigencia;
    @ManyToOne
    @Etiqueta("Ato Legal")
    private AtoLegal atoLegal;

    public ModalidadeContratoFPData() {
    }

    public ModalidadeContratoFPData(ModalidadeContratoFP modalidadeContratoFP, ContratoFP contratoFP, Date inicioVigencia, Date finalVigencia) {
        this.modalidadeContratoFP = modalidadeContratoFP;
        this.contratoFP = contratoFP;
        this.inicioVigencia = inicioVigencia;
        this.finalVigencia = finalVigencia;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ModalidadeContratoFP getModalidadeContratoFP() {
        return modalidadeContratoFP;
    }

    public void setModalidadeContratoFP(ModalidadeContratoFP modalidadeContratoFP) {
        this.modalidadeContratoFP = modalidadeContratoFP;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
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

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }
}
