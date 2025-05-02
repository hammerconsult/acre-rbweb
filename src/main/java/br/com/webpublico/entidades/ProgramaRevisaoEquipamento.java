/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Wellington
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Frota")
@Etiqueta("Programa de Revisão do Equipamento")
public class ProgramaRevisaoEquipamento extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Equipamento")
    private Equipamento equipamento;

    @Etiqueta("Horas")
    @Obrigatorio
    @Tabelavel
    private BigDecimal prazoEmSegundos;

    @Etiqueta("Meses")
    @Obrigatorio
    @Tabelavel
    private Integer prazoEmMeses;

    @Obrigatorio
    @Tabelavel
    @Etiqueta("Sequência")
    private Integer sequencia;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Etiqueta("Data de ")
    private Date dataLancamento;

    @Etiqueta("Revisão Realizada")
    private Boolean revisaoRealizada;


    public ProgramaRevisaoEquipamento() {
        super();
        this.dataLancamento = new Date();
        this.revisaoRealizada = Boolean.FALSE;
    }

    public Boolean getRevisaoRealizada() {
        return revisaoRealizada;
    }

    public void setRevisaoRealizada(Boolean revisaoRealizada) {
        this.revisaoRealizada = revisaoRealizada;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getPrazoEmSegundos() {
        return prazoEmSegundos;
    }

    public void setPrazoEmSegundos(BigDecimal prazoEmSegundos) {
        this.prazoEmSegundos = prazoEmSegundos;
    }

    public Integer getPrazoEmMeses() {
        return prazoEmMeses;
    }

    public void setPrazoEmMeses(Integer prazoEmMeses) {
        this.prazoEmMeses = prazoEmMeses;
    }

    public Integer getSequencia() {
        return sequencia;
    }

    public void setSequencia(Integer sequencia) {
        this.sequencia = sequencia;
    }

    public Equipamento getEquipamento() {
        return equipamento;
    }

    public void setEquipamento(Equipamento equipamento) {
        this.equipamento = equipamento;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }


    @Override
    public String toString() {
        try {
            return "" + prazoEmMeses + " meses ou " + prazoEmSegundos + " horas";
        } catch (NullPointerException ex) {
            return "";
        }
    }
}
