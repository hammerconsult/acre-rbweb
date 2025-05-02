package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity

@Audited
@Etiqueta("Agrupador de Dívida Ativa")
@GrupoDiagrama(nome = "Divida Ativa")
public class AgrupadorCDADivida extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
//    @NotNull
    private Divida divida;
    @ManyToOne
//    @NotNull
    private AgrupadorCDA agrupadorCDA;

    public AgrupadorCDADivida() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public AgrupadorCDA getAgrupadorCDA() {
        return agrupadorCDA;
    }

    public void setAgrupadorCDA(AgrupadorCDA agrupadorCDA) {
        this.agrupadorCDA = agrupadorCDA;
    }

    @Override
    public String toString() {
        return divida.getDescricao();
    }
}
