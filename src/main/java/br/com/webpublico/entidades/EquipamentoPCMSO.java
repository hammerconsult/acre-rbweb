package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by carlos on 04/08/15.
 */
@Entity
@Audited
@Etiqueta("Equipamento PCMSO")
public class EquipamentoPCMSO extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ASO aso;
    @ManyToOne
    private EquipamentoEPI equipamentoEPI;
    @ManyToOne
    private ProtecaoEPI protecaoEPI;
    @Temporal(TemporalType.DATE)
    private Date data;

    public EquipamentoPCMSO() {
        equipamentoEPI = new EquipamentoEPI();
        protecaoEPI = new ProtecaoEPI();
    }

    public ASO getAso() {
        return aso;
    }

    public void setAso(ASO aso) {
        this.aso = aso;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public EquipamentoEPI getEquipamentoEPI() {
        return equipamentoEPI;
    }

    public void setEquipamentoEPI(EquipamentoEPI equipamentoEPI) {
        this.equipamentoEPI = equipamentoEPI;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProtecaoEPI getProtecaoEPI() {
        return protecaoEPI;
    }

    public void setProtecaoEPI(ProtecaoEPI protecaoEPI) {
        this.protecaoEPI = protecaoEPI;
    }

    @Override
    public String toString() {
        return equipamentoEPI.getEquipamento();
    }
}
