package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by mga on 17/05/2018.
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Frota")
@Etiqueta("Km Percorrido do Veículo")
public class KmPercorridoVeiculo extends SuperEntidade implements Comparable<KmPercorridoVeiculo> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Veículo")
    private Veiculo veiculo;

    @Etiqueta("Km Atual")
    private BigDecimal kmAtual;

    @Etiqueta("Data de Lançamento")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataLancamento;

    public KmPercorridoVeiculo() {
    }

    public KmPercorridoVeiculo(Veiculo veiculo, BigDecimal kmAtual) {
        this.kmAtual = kmAtual;
        this.veiculo = veiculo;
        dataLancamento = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public BigDecimal getKmAtual() {
        return kmAtual;
    }

    public void setKmAtual(BigDecimal kmAtual) {
        this.kmAtual = kmAtual;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    @Override
    public int compareTo(KmPercorridoVeiculo o) {
        if (o.getDataLancamento() != null && this.dataLancamento != null) {
            return this.dataLancamento.compareTo(o.getDataLancamento());
        }
        return 0;
    }
}
