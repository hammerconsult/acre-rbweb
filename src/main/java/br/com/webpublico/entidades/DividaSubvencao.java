package br.com.webpublico.entidades;

import br.com.webpublico.enums.NaturezaDividaSubvencao;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 17/12/13
 * Time: 14:54
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Audited
public class DividaSubvencao implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private SubvencaoParametro subvencaoParametro;
    @ManyToOne
    private Exercicio exercicioInicial;
    @ManyToOne
    private Exercicio exercicioFinal;
    @Temporal(TemporalType.DATE)
    private Date dataInicialVigencia;
    @Temporal(TemporalType.DATE)
    private Date dataFinalVigencia;
    @ManyToOne
    private Divida divida;
    @Transient
    private Long criadoEm;
    private Integer qtdeMinimaParcela;
    private Integer qtdeMaximaParcela;
    @Enumerated(EnumType.STRING)
    private NaturezaDividaSubvencao naturezaDividaSubvencao;

    public DividaSubvencao() {
        this.criadoEm = System.nanoTime();
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SubvencaoParametro getSubvencaoParametro() {
        return subvencaoParametro;
    }

    public void setSubvencaoParametro(SubvencaoParametro subvencaoParametro) {
        this.subvencaoParametro = subvencaoParametro;
    }

    public Exercicio getExercicioInicial() {
        return exercicioInicial;
    }

    public void setExercicioInicial(Exercicio exercicioInicial) {
        this.exercicioInicial = exercicioInicial;
    }

    public Exercicio getExercicioFinal() {
        return exercicioFinal;
    }

    public void setExercicioFinal(Exercicio exercicioFinal) {
        this.exercicioFinal = exercicioFinal;
    }

    public Date getDataInicialVigencia() {
        return dataInicialVigencia;
    }

    public void setDataInicialVigencia(Date dataInicialVigencia) {
        this.dataInicialVigencia = dataInicialVigencia;
    }

    public Date getDataFinalVigencia() {
        return dataFinalVigencia;
    }

    public void setDataFinalVigencia(Date dataFinalVigencia) {
        this.dataFinalVigencia = dataFinalVigencia;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public Integer getQtdeMinimaParcela() {
        return qtdeMinimaParcela;
    }

    public void setQtdeMinimaParcela(Integer qtdeMinimaParcela) {
        this.qtdeMinimaParcela = qtdeMinimaParcela;
    }

    public Integer getQtdeMaximaParcela() {
        return qtdeMaximaParcela;
    }

    public void setQtdeMaximaParcela(Integer qtdeMaximaParcela) {
        this.qtdeMaximaParcela = qtdeMaximaParcela;
    }

    public NaturezaDividaSubvencao getNaturezaDividaSubvencao() {
        return naturezaDividaSubvencao;
    }

    public void setNaturezaDividaSubvencao(NaturezaDividaSubvencao naturezaDividaSubvencao) {
        this.naturezaDividaSubvencao = naturezaDividaSubvencao;
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }
}
