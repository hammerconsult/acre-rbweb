package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
@Etiqueta("Agrupador do Relatório de Créditos - Dívida")
@Table(name = "AGRUPADORRELATORIOCREDDIV")
public class AgrupadorRelatorioCreditoDivida extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    @ManyToOne
    private AgrupadorRelatorioCredito agrupadorRelatorioCredito;
    @ManyToOne
    private Divida divida;

    public AgrupadorRelatorioCreditoDivida() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AgrupadorRelatorioCredito getAgrupadorRelatorioCredito() {
        return agrupadorRelatorioCredito;
    }

    public void setAgrupadorRelatorioCredito(AgrupadorRelatorioCredito agrupadorRelatorioCredito) {
        this.agrupadorRelatorioCredito = agrupadorRelatorioCredito;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }
}
