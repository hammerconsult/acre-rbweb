package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Audited
@Etiqueta("Processo Cálculo Contrato")
@GrupoDiagrama(nome = "Tributario")
public class ProcessoCalculoContrato extends ProcessoCalculo {

    @Etiqueta("Número")
    private Long numero;

    @ManyToOne
    @Etiqueta("Contrato")
    private Contrato contrato;

    @OneToMany(mappedBy = "processoCalculoContrato")
    private List<CalculoContrato> calculos;

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public void setCalculos(List<CalculoContrato> calculos) {
        this.calculos = calculos;
    }

    @Override
    public List<CalculoContrato> getCalculos() {
        return calculos;
    }
}
