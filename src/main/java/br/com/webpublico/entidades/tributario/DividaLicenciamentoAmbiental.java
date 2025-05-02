package br.com.webpublico.entidades.tributario;

import br.com.webpublico.entidades.Divida;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.Tributo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Audited
public class DividaLicenciamentoAmbiental extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Dívida")
    @ManyToOne
    private Divida divida;
    @Obrigatorio
    @Etiqueta("Dívida da taxa de expediente")
    @ManyToOne
    private Divida dividaTaxaExpediente;
    @Obrigatorio
    @Etiqueta("Tributo ta taxa de expediente")
    @ManyToOne
    private Tributo tributoTaxaExpediente;
    @Obrigatorio
    @Etiqueta("Valor da taxa de expediente")
    private BigDecimal valorTaxaExpediente;

    public DividaLicenciamentoAmbiental() {
        super();
        valorTaxaExpediente = BigDecimal.ZERO;
    }

    @Override
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

    public Divida getDividaTaxaExpediente() {
        return dividaTaxaExpediente;
    }

    public void setDividaTaxaExpediente(Divida dividaTaxaExpediente) {
        this.dividaTaxaExpediente = dividaTaxaExpediente;
    }

    public Tributo getTributoTaxaExpediente() {
        return tributoTaxaExpediente;
    }

    public void setTributoTaxaExpediente(Tributo tributoTaxaExpediente) {
        this.tributoTaxaExpediente = tributoTaxaExpediente;
    }

    public BigDecimal getValorTaxaExpediente() {
        return valorTaxaExpediente;
    }

    public void setValorTaxaExpediente(BigDecimal valorTaxaExpediente) {
        this.valorTaxaExpediente = valorTaxaExpediente;
    }
}
