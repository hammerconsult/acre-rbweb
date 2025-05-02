package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wanderley
 * Date: 05/11/13
 * Time: 10:49
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Audited
@Table(name = "PROC_CALCULOMULTAACESSORIA")
public class ProcessoCalculoMultaAcessoria extends ProcessoCalculo implements Serializable {

    private static final long serialVersionUID = 1L;
    @OneToMany(mappedBy = "procCalcMultaAcessoria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CalculoMultaAcessoria> calculos;
    @ManyToOne
    private ConfiguracaoTributario configuracaoTributario;
    @OneToOne
    private CalculoISS calculoISS;
    @OneToOne
    private LancamentoMultaAcessoria lancamentoMultaAcessoria;
    private String observacoes;

    @Override
    public List<CalculoMultaAcessoria> getCalculos() {
        return calculos;
    }

    public void setCalculos(List<CalculoMultaAcessoria> calculos) {
        this.calculos = calculos;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public ConfiguracaoTributario getConfiguracaoTributario() {
        return configuracaoTributario;
    }

    public void setConfiguracaoTributario(ConfiguracaoTributario configuracaoTributario) {
        this.configuracaoTributario = configuracaoTributario;
    }

    public CalculoISS getCalculoISS() {
        return calculoISS;
    }

    public void setCalculoISS(CalculoISS calculoISS) {
        this.calculoISS = calculoISS;
    }

    public LancamentoMultaAcessoria getLancamentoMultaAcessoria() {
        return lancamentoMultaAcessoria;
    }

    public void setLancamentoMultaAcessoria(LancamentoMultaAcessoria lancamentoMultaAcessoria) {
        this.lancamentoMultaAcessoria = lancamentoMultaAcessoria;
    }
}
