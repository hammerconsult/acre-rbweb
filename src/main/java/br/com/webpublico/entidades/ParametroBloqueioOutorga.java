package br.com.webpublico.entidades;

import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoPassageiro;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

@Audited
@Entity
@Etiqueta("Parametros de Refêrencia de Bloqueio Outorga")
@GrupoDiagrama(nome = "RBTrans")
public class ParametroBloqueioOutorga extends SuperEntidade {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @ManyToOne
    private Exercicio exercicio;
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private Mes mes;
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private TipoPassageiro tipoPassageiro;
    @ManyToOne
    private BloqueioOutorga bloqueioOutorga;
    private BigDecimal qtdPassageiros;
    private BigDecimal valorPassagem;
    @ManyToOne
    private ParametroOutorgaSubvencao parametroOutorgaSubvencao;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public TipoPassageiro getTipoPassageiro() {
        return tipoPassageiro;
    }

    public void setTipoPassageiro(TipoPassageiro tipoPassageiro) {
        this.tipoPassageiro = tipoPassageiro;
    }

    public BloqueioOutorga getBloqueioOutorga() {
        return bloqueioOutorga;
    }

    public void setBloqueioOutorga(BloqueioOutorga bloqueioOutorga) {
        this.bloqueioOutorga = bloqueioOutorga;
    }

    public BigDecimal getQtdPassageiros() {
        return qtdPassageiros;
    }

    public void setQtdPassageiros(BigDecimal qtdPassageiros) {
        this.qtdPassageiros = qtdPassageiros;
    }

    public BigDecimal getValorPassagem() {
        return valorPassagem;
    }

    public void setValorPassagem(BigDecimal valorPassagem) {
        this.valorPassagem = valorPassagem;
    }

    public ParametroOutorgaSubvencao getParametroOutorgaSubvencao() {
        return parametroOutorgaSubvencao;
    }

    public void setParametroOutorgaSubvencao(ParametroOutorgaSubvencao parametroOutorgaSubvencao) {
        this.parametroOutorgaSubvencao = parametroOutorgaSubvencao;
    }
}
