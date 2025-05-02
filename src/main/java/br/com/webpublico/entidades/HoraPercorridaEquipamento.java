package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by William on 24/07/2018.
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Frota")
@Etiqueta("Hora Percorrida do Equipamento")
public class HoraPercorridaEquipamento extends SuperEntidade implements Comparable<HoraPercorridaEquipamento> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Equipamento")
    private Equipamento equipamento;

    @Etiqueta("Hora Atual")
    private BigDecimal horaAtual;

    @Etiqueta("Data de Lançamento")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataLancamento;

    public HoraPercorridaEquipamento() {
    }

    public HoraPercorridaEquipamento(Equipamento equipamento, BigDecimal horaAtual) {
        this.horaAtual = horaAtual;
        this.equipamento = equipamento;
        dataLancamento = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Equipamento getEquipamento() {
        return equipamento;
    }

    public void setEquipamento(Equipamento equipamento) {
        this.equipamento = equipamento;
    }

    public BigDecimal getHoraAtual() {
        return horaAtual;
    }

    public void setHoraAtual(BigDecimal horaAtual) {
        this.horaAtual = horaAtual;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    @Override
    public int compareTo(HoraPercorridaEquipamento o) {
        if (o.getDataLancamento() != null && this.dataLancamento != null) {
            return this.dataLancamento.compareTo(o.getDataLancamento());
        }
        return 0;
    }
}
