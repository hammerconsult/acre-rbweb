package br.com.webpublico.entidades;

import br.com.webpublico.enums.MedidaDeControleEficaz;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by carlos on 04/09/15.
 */
@Entity
@Audited
@Etiqueta("Medida de Controle PPRA")
public class MedidaDeControlePPRA extends SuperEntidade implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Risco")
    private Risco risco;
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Fator de Risco")
    private FatorDeRisco fatorDeRisco;
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Equipamento EPC")
    private EquipamentoEPC equipamentoEPC;
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Etiqueta("Medida de Controle Eficaz")
    private MedidaDeControleEficaz medidaDeControleEficaz;
    @ManyToOne
    private PPRA ppra;

    public MedidaDeControlePPRA() {
    }

    public EquipamentoEPC getEquipamentoEPC() {
        return equipamentoEPC;
    }

    public void setEquipamentoEPC(EquipamentoEPC equipamentoEPC) {
        this.equipamentoEPC = equipamentoEPC;
    }

    public FatorDeRisco getFatorDeRisco() {
        return fatorDeRisco;
    }

    public void setFatorDeRisco(FatorDeRisco fatorDeRisco) {
        this.fatorDeRisco = fatorDeRisco;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MedidaDeControleEficaz getMedidaDeControleEficaz() {
        return medidaDeControleEficaz;
    }

    public void setMedidaDeControleEficaz(MedidaDeControleEficaz medidaDeControleEficaz) {
        this.medidaDeControleEficaz = medidaDeControleEficaz;
    }

    public PPRA getPpra() {
        return ppra;
    }

    public void setPpra(PPRA ppra) {
        this.ppra = ppra;
    }

    public Risco getRisco() {
        return risco;
    }

    public void setRisco(Risco risco) {
        this.risco = risco;
    }

    @Override
    public String toString() {
        return equipamentoEPC.toString();
    }
}
