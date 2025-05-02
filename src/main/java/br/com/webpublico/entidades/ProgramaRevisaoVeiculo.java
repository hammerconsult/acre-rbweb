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
 * @author Munif
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Frota")
public class ProgramaRevisaoVeiculo extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Veículo")
    private Veiculo veiculo;

    @Etiqueta("Quilômetros")
    @Obrigatorio
    @Tabelavel
    private BigDecimal km;

    @Etiqueta("Meses")
    @Obrigatorio
    @Tabelavel
    private Integer prazo;

    @Obrigatorio
    @Tabelavel
    @Etiqueta("Sequência")
    private Integer sequencia;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Etiqueta("Data de Lançamento")
    private Date dataLancamento;

    @Etiqueta("Revisão Realizada")
    private Boolean revisaoRealizada;

    public ProgramaRevisaoVeiculo() {
        super();
        this.dataLancamento = new Date();
        this.revisaoRealizada = Boolean.FALSE;
    }

    public void organizaSequenciaProgramaRevisao(Veiculo veiculo) {
        int tamanho = veiculo.getProgramaRevisao().size();
        for (int i = 0; i < tamanho; i++) {
            veiculo.getProgramaRevisao().get(i).setSequencia(i + 1);
        }
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

    public BigDecimal getKm() {
        return km;
    }

    public void setKm(BigDecimal km) {
        this.km = km;
    }

    public Integer getPrazo() {
        return prazo;
    }

    public void setPrazo(Integer prazo) {
        this.prazo = prazo;
    }

    public Integer getSequencia() {
        return sequencia;
    }

    public void setSequencia(Integer sequencia) {
        this.sequencia = sequencia;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
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
            return "" + prazo + " meses ou " + km + " quilômetros";
        } catch (NullPointerException ne) {
            return "";
        }
    }
}
