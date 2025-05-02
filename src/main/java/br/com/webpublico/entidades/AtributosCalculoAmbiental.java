package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoLocalizacao;
import br.com.webpublico.enums.TipoMateriaPrima;
import br.com.webpublico.enums.TipoSimNao;
import br.com.webpublico.enums.tributario.AtributoParamAmbiental;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Audited
@Etiqueta("Atributos Cálculo Ambiental")
public class AtributosCalculoAmbiental extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ParametrosCalcAmbiental parametroCalcAmbiental;
    @Enumerated(EnumType.STRING)
    private AtributoParamAmbiental atributo;
    @Enumerated(EnumType.STRING)
    private TipoMateriaPrima tipoMateriaPrima;
    @Enumerated(EnumType.STRING)
    private TipoLocalizacao tipoLocalizacao;
    @Enumerated(EnumType.STRING)
    private TipoSimNao tipoSimNao;
    private BigDecimal valoracao;
    private BigDecimal valorInicial;
    private BigDecimal valorFinal;

    public AtributosCalculoAmbiental() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ParametrosCalcAmbiental getParametroCalcAmbiental() {
        return parametroCalcAmbiental;
    }

    public void setParametroCalcAmbiental(ParametrosCalcAmbiental parametroCalcAmbiental) {
        this.parametroCalcAmbiental = parametroCalcAmbiental;
    }

    public AtributoParamAmbiental getAtributo() {
        return atributo;
    }

    public void setAtributo(AtributoParamAmbiental atributo) {
        this.atributo = atributo;
    }

    public TipoMateriaPrima getTipoMateriaPrima() {
        return tipoMateriaPrima;
    }

    public void setTipoMateriaPrima(TipoMateriaPrima tipoMateriaPrima) {
        this.tipoMateriaPrima = tipoMateriaPrima;
    }

    public TipoLocalizacao getTipoLocalizacao() {
        return tipoLocalizacao;
    }

    public void setTipoLocalizacao(TipoLocalizacao tipoLocalizacao) {
        this.tipoLocalizacao = tipoLocalizacao;
    }

    public BigDecimal getValoracao() {
        return valoracao;
    }

    public void setValoracao(BigDecimal valoracao) {
        this.valoracao = valoracao;
    }

    public BigDecimal getValorInicial() {
        return valorInicial;
    }

    public void setValorInicial(BigDecimal valorInicial) {
        this.valorInicial = valorInicial;
    }

    public BigDecimal getValorFinal() {
        return valorFinal;
    }

    public void setValorFinal(BigDecimal valorFinal) {
        this.valorFinal = valorFinal;
    }

    public TipoSimNao getTipoSimNao() {
        return tipoSimNao;
    }

    public void setTipoSimNao(TipoSimNao tipoSimNao) {
        this.tipoSimNao = tipoSimNao;
    }
}
