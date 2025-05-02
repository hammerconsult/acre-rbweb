package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

/**
 * Created by Wellington on 12/02/2016.
 */
@GrupoDiagrama(nome = "Contratos")
@Entity
@Audited
public class MultaPenalidadeContrato extends PenalidadeContrato {

    @Etiqueta("Dívida Diversa")
    @Obrigatorio
    @ManyToOne
    private CalculoDividaDiversa calculoDividaDiversa;

    @Etiqueta(" Percentual Multa")
    @Obrigatorio
    private BigDecimal percentual;

    public CalculoDividaDiversa getCalculoDividaDiversa() {
        return calculoDividaDiversa;
    }

    public void setCalculoDividaDiversa(CalculoDividaDiversa calculoDividaDiversa) {
        this.calculoDividaDiversa = calculoDividaDiversa;
    }

    public BigDecimal getPercentual() {
        return percentual;
    }

    public void setPercentual(BigDecimal percentual) {
        this.percentual = percentual;
    }
}
