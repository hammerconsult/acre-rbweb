package br.com.webpublico.entidades.rh;

import br.com.webpublico.entidades.ReferenciaFP;
import br.com.webpublico.entidades.TipoRegime;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@GrupoDiagrama(nome = "Recursos Humanos")
@Entity
@Audited
@Etiqueta("Parametrização de Férias")
public class ParametroFerias implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    @ManyToOne
    @Etiqueta("Tipo de Regime")
    private TipoRegime tipoRegime;
    @Obrigatorio
    @Temporal(TemporalType.DATE)
    @Etiqueta("Início de Vigência")
    private Date inicioVigencia;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Final de Vigência")
    private Date finalVigencia;
    @Etiqueta("Quantidade Mínima de Dias por Parcela")
    private Integer quantidadeParcelaMaxima;
    @Etiqueta("Quantidade Mínima de Dias por Parcela")
    private Integer quantidMinimaDiasPorParcela;
    @ManyToOne
    private ReferenciaFP referenciaFP;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoRegime getTipoRegime() {
        return tipoRegime;
    }

    public void setTipoRegime(TipoRegime tipoRegime) {
        this.tipoRegime = tipoRegime;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public Integer getQuantidadeParcelaMaxima() {
        return quantidadeParcelaMaxima;
    }

    public void setQuantidadeParcelaMaxima(Integer quantidadeParcelaMaxima) {
        this.quantidadeParcelaMaxima = quantidadeParcelaMaxima;
    }

    public Integer getQuantidMinimaDiasPorParcela() {
        return quantidMinimaDiasPorParcela;
    }

    public void setQuantidMinimaDiasPorParcela(Integer quantidMinimaDiasPorParcela) {
        this.quantidMinimaDiasPorParcela = quantidMinimaDiasPorParcela;
    }

    public ReferenciaFP getReferenciaFP() {
        return referenciaFP;
    }

    public void setReferenciaFP(ReferenciaFP referenciaFP) {
        this.referenciaFP = referenciaFP;
    }
}
