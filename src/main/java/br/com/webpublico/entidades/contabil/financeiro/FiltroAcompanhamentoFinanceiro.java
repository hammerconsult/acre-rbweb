package br.com.webpublico.entidades.contabil.financeiro;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.TipoLiberacaoFinanceira;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by renatoromanini on 17/04/17.
 */
@GrupoDiagrama(nome = "Contábil")
@Entity
@Audited
@Etiqueta("Filtro de Acompanhamento Financeiro")
@Table(name = "FILTROACOMPAFINANCEIRO")
public class FiltroAcompanhamentoFinanceiro extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private UnidadeAcompanhamentoFinanceiro unidade;
    @Enumerated(EnumType.STRING)
    private TipoLiberacaoFinanceira tipoLiberacaoFinanceira;

    public FiltroAcompanhamentoFinanceiro() {
    }

    public FiltroAcompanhamentoFinanceiro(UnidadeAcompanhamentoFinanceiro unidadeAcompanhamentoFinanceiro, TipoLiberacaoFinanceira liberacaoFinanceira) {
        this.unidade = unidadeAcompanhamentoFinanceiro;
        this.tipoLiberacaoFinanceira = liberacaoFinanceira;
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

    public TipoLiberacaoFinanceira getTipoLiberacaoFinanceira() {
        return tipoLiberacaoFinanceira;
    }

    public void setTipoLiberacaoFinanceira(TipoLiberacaoFinanceira tipoLiberacaoFinanceira) {
        this.tipoLiberacaoFinanceira = tipoLiberacaoFinanceira;
    }

    @Override
    public String toString() {
        return " Tipo = " + tipoLiberacaoFinanceira ;
    }
}
