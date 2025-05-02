package br.com.webpublico.entidades;

import br.com.webpublico.enums.Mes;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Audited
@Table(name = "COMPONENTEFORMULAVPADRAO")
public class ComponenteFormulaValorPadrao extends ComponenteFormula implements Serializable {
    private BigDecimal valorPadrao;
    @Enumerated(EnumType.STRING)
    private Mes mes;

    public ComponenteFormulaValorPadrao() {
    }

    public BigDecimal getValorPadrao() {
        return valorPadrao;
    }

    public void setValorPadrao(BigDecimal valorPadrao) {
        this.valorPadrao = valorPadrao;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    @Override
    public ComponenteFormula criarComponenteFormula() {
        ComponenteFormulaValorPadrao componenteDestino = new ComponenteFormulaValorPadrao();
        componenteDestino.setValorPadrao(getValorPadrao());
        componenteDestino.setMes(getMes());
        return componenteDestino;
    }
}
