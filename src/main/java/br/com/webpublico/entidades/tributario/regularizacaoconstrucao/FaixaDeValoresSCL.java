package br.com.webpublico.entidades.tributario.regularizacaoconstrucao;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.tributario.regularizacaoconstrucao.TipoMedida;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Audited
@GrupoDiagrama(nome = "Tributario")
@Etiqueta("Lista de Faixa de Valores de Serviços de Construção")
public class FaixaDeValoresSCL extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipos de Medida")
    private TipoMedida tipoMedida;
    @Etiqueta("Área Inicial")
    private BigDecimal areaInicial;
    @Etiqueta("Área Final")
    private BigDecimal areaFinal;
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    @Obrigatorio
    @Etiqueta("Valor em UFM")
    private BigDecimal valorUFM;
    @ManyToOne
    @Etiqueta("Item Serviço de Construção")
    private ItemServicoConstrucao itemServicoConstrucao;

    public FaixaDeValoresSCL() {
    }

    public FaixaDeValoresSCL(TipoMedida tipoMedida, Exercicio exercicio, BigDecimal areaInicial, BigDecimal areaFinal, BigDecimal valorUFM, ItemServicoConstrucao itemServicoConstrucao) {
        this.tipoMedida = tipoMedida;
        this.areaInicial = areaInicial;
        this.exercicio = exercicio;
        this.areaFinal = areaFinal;
        this.valorUFM = valorUFM;
        this.itemServicoConstrucao = itemServicoConstrucao;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoMedida getTipoMedida() {
        return tipoMedida;
    }

    public void setTipoMedida(TipoMedida tipoMedida) {
        this.tipoMedida = tipoMedida;
    }

    public BigDecimal getAreaInicial() {
        return areaInicial;
    }

    public void setAreaInicial(BigDecimal areaInicial) {
        this.areaInicial = areaInicial;
    }

    public BigDecimal getAreaFinal() {
        return areaFinal;
    }

    public void setAreaFinal(BigDecimal areaFinal) {
        this.areaFinal = areaFinal;
    }

    public BigDecimal getValorUFM() {
        return valorUFM;
    }

    public void setValorUFM(BigDecimal valorUFM) {
        this.valorUFM = valorUFM;
    }

    public ItemServicoConstrucao getItemServicoConstrucao() {
        return itemServicoConstrucao;
    }

    public void setItemServicoConstrucao(ItemServicoConstrucao itemServicoConstrucao) {
        this.itemServicoConstrucao = itemServicoConstrucao;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    @Override
    public String toString() {
        return "FaixaDeValoresSCL{" +
            "id=" + id +
            ", tipoMedida=" + tipoMedida +
            ", areaInicial=" + areaInicial +
            ", areaFinal=" + areaFinal +
            ", valorUFM=" + valorUFM +
            ", itemServicoConstrucao=" + itemServicoConstrucao +
            '}';
    }
}
