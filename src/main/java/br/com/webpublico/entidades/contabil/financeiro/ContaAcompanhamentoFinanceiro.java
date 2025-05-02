package br.com.webpublico.entidades.contabil.financeiro;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.TipoContaAcompanhamento;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by carlos on 22/06/17.
 */

@GrupoDiagrama(nome = "Contábil")
@Entity
@Audited
@Etiqueta("Conta de Acompanhamento Financeiro")
@Table(name = "CONTAACOMPAFINANCEIRO")
public class ContaAcompanhamentoFinanceiro extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private UnidadeAcompanhamentoFinanceiro unidade;
    @Enumerated(EnumType.STRING)
    private TipoContaAcompanhamento tipoContaAcompanhamento;
    private String intervaloInicial;
    private String intervaloFinal;

    public ContaAcompanhamentoFinanceiro() {
    }

    public ContaAcompanhamentoFinanceiro(UnidadeAcompanhamentoFinanceiro unidade, TipoContaAcompanhamento tipoContaAcompanhamento, String intervaloInicial, String intervaloFinal) {
        this.unidade = unidade;
        this.tipoContaAcompanhamento = tipoContaAcompanhamento;
        this.intervaloInicial = intervaloInicial;
        this.intervaloFinal = intervaloFinal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UnidadeAcompanhamentoFinanceiro getUnidade() {
        return unidade;
    }

    public void setUnidade(UnidadeAcompanhamentoFinanceiro unidade) {
        this.unidade = unidade;
    }

    public TipoContaAcompanhamento getTipoContaAcompanhamento() {
        return tipoContaAcompanhamento;
    }

    public void setTipoContaAcompanhamento(TipoContaAcompanhamento tipoContaAcompanhamento) {
        this.tipoContaAcompanhamento = tipoContaAcompanhamento;
    }

    public String getIntervaloInicial() {
        return intervaloInicial;
    }

    public void setIntervaloInicial(String intervaloInicial) {
        this.intervaloInicial = intervaloInicial;
    }

    public String getIntervaloFinal() {
        return intervaloFinal;
    }

    public void setIntervaloFinal(String intervaloFinal) {
        this.intervaloFinal = intervaloFinal;
    }
}

