package br.com.webpublico.entidades;

import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.FaixaDeValoresHL;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.HabiteseClassesConstrucao;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.HabitesePadroesConstrucao;
import br.com.webpublico.enums.tributario.regularizacaoconstrucao.TipoConstrucao;
import br.com.webpublico.interfaces.EnumComDescricao;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Audited
@Table(name = "CARACTCONSTRUHABITESE")
public class CaracteristicaConstrucaoHabitese extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    private Habitese habitese;
    @Enumerated(EnumType.STRING)
    private TipoConstrucao tipoConstrucao;
    @Enumerated(EnumType.STRING)
    private TipoHabitese tipoHabitese;
    private Integer quantidadeDePavimentos;
    private Integer tempoConstrucao;
    private BigDecimal valorMaoDeObra;
    private BigDecimal areaConstruida;
    private BigDecimal baseDeCalculo;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "caractConstruHabitese")
    private List<ServicosAlvaraConstrucao> servicos;
    @ManyToOne
    private HabiteseClassesConstrucao classe;
    @ManyToOne
    private HabitesePadroesConstrucao padrao;
    @ManyToOne
    private FaixaDeValoresHL faixaDeValor;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Habitese getHabitese() {
        return habitese;
    }

    public void setHabitese(Habitese habitese) {
        this.habitese = habitese;
    }

    public TipoHabitese getTipoHabitese() {
        return tipoHabitese;
    }

    public void setTipoHabitese(TipoHabitese tipoHabitese) {
        this.tipoHabitese = tipoHabitese;
    }

    public Integer getQuantidadeDePavimentos() {
        return quantidadeDePavimentos;
    }

    public void setQuantidadeDePavimentos(Integer quantidadeDePavimentos) {
        this.quantidadeDePavimentos = quantidadeDePavimentos;
    }

    public Integer getTempoConstrucao() {
        return tempoConstrucao;
    }

    public void setTempoConstrucao(Integer tempoConstrucao) {
        this.tempoConstrucao = tempoConstrucao;
    }

    public BigDecimal getValorMaoDeObra() {
        return valorMaoDeObra;
    }

    public void setValorMaoDeObra(BigDecimal valorMaoDeObra) {
        this.valorMaoDeObra = valorMaoDeObra;
    }

    public BigDecimal getAreaConstruida() {
        return areaConstruida == null ? BigDecimal.ZERO : areaConstruida;
    }

    public void setAreaConstruida(BigDecimal areaConstruida) {
        this.areaConstruida = areaConstruida;
    }

    public BigDecimal getBaseDeCalculo() {
        return baseDeCalculo;
    }

    public void setBaseDeCalculo(BigDecimal baseDeCalculo) {
        this.baseDeCalculo = baseDeCalculo;
    }

    public HabiteseClassesConstrucao getClasse() {
        return classe;
    }

    public void setClasse(HabiteseClassesConstrucao classe) {
        this.classe = classe;
    }

    public HabitesePadroesConstrucao getPadrao() {
        return padrao;
    }

    public void setPadrao(HabitesePadroesConstrucao padrao) {
        this.padrao = padrao;
    }

    public FaixaDeValoresHL getFaixaDeValor() {
        return faixaDeValor;
    }

    public void setFaixaDeValor(FaixaDeValoresHL faixaDeValor) {
        this.faixaDeValor = faixaDeValor;
    }

    public List<ServicosAlvaraConstrucao> getServicos() {
        if (servicos == null) {
            servicos = Lists.newArrayList();
        }
        return servicos;
    }

    public void setServicos(List<ServicosAlvaraConstrucao> servicos) {
        this.servicos = servicos;
    }

    public TipoConstrucao getTipoConstrucao() {
        return tipoConstrucao;
    }

    public void setTipoConstrucao(TipoConstrucao tipoConstrucao) {
        this.tipoConstrucao = tipoConstrucao;
    }

    public enum TipoHabitese implements EnumComDescricao {
        PARCIAL("Parcial"),
        TOTAL("Total");

        private String descricao;

        TipoHabitese(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public String getDescricao() {
            return descricao;
        }
    }

}
