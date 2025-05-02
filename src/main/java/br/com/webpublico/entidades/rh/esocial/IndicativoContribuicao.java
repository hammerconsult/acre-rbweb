package br.com.webpublico.entidades.rh.esocial;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.rh.esocial.IndicativoSubstituicao;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Audited
public class IndicativoContribuicao extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Exercicio exercicio;

    @Enumerated(EnumType.STRING)
    private Mes mes;

    private Boolean decimoTerceiro;

    @Enumerated(EnumType.STRING)
    private IndicativoSubstituicao indicativoSubstituicao;

    private BigDecimal percentualContribuicao;
    private BigDecimal fatorMes;
    private BigDecimal contribuicaoSocial;

    @ManyToOne
    private ConfiguracaoEmpregadorESocial empregador;

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

    public Boolean getDecimoTerceiro() {
        return decimoTerceiro != null ? decimoTerceiro : false;
    }

    public void setDecimoTerceiro(Boolean decimoTerceiro) {
        this.decimoTerceiro = decimoTerceiro;
    }

    public IndicativoSubstituicao getIndicativoSubstituicao() {
        return indicativoSubstituicao;
    }

    public void setIndicativoSubstituicao(IndicativoSubstituicao indicativoSubstituicao) {
        this.indicativoSubstituicao = indicativoSubstituicao;
    }

    public BigDecimal getPercentualContribuicao() {
        return percentualContribuicao;
    }

    public void setPercentualContribuicao(BigDecimal percentualContribuicao) {
        this.percentualContribuicao = percentualContribuicao;
    }

    public BigDecimal getFatorMes() {
        return fatorMes;
    }

    public void setFatorMes(BigDecimal fatorMes) {
        this.fatorMes = fatorMes;
    }

    public BigDecimal getContribuicaoSocial() {
        return contribuicaoSocial;
    }

    public void setContribuicaoSocial(BigDecimal contribuicaoSocial) {
        this.contribuicaoSocial = contribuicaoSocial;
    }

    public ConfiguracaoEmpregadorESocial getEmpregador() {
        return empregador;
    }

    public void setEmpregador(ConfiguracaoEmpregadorESocial empregador) {
        this.empregador = empregador;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
