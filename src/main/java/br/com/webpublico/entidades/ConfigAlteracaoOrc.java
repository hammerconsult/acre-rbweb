package br.com.webpublico.entidades;

import br.com.webpublico.enums.*;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 05/02/14
 * Time: 17:34
 * To change this template use File | Settings | File Templates.
 */


@Entity

@Inheritance(strategy = InheritanceType.JOINED)
@Audited
@Etiqueta("Configuração de Alteração Orçamentária")
public class ConfigAlteracaoOrc extends ConfiguracaoEvento implements Serializable {

    @Tabelavel
    @Pesquisavel
    @ErroReprocessamentoContabil
    @Etiqueta(value = "Tipo de Crédito")
    @Enumerated(EnumType.STRING)
    private TipoDespesaORC tipoDespesaORC;
    @Tabelavel
    @Pesquisavel
    @Etiqueta(value = "Tipo de Alteração")
    @Enumerated(EnumType.STRING)
    private TipoAlteracaoORC tipoAlteracaoORC;
    @Tabelavel
    @Pesquisavel
    @Etiqueta(value = "Origem do Recurso")
    @Enumerated(EnumType.STRING)
    private OrigemSuplementacaoORC origemSuplementacaoORC;
    @Tabelavel
    @Pesquisavel
    @Etiqueta(value = "Tipo Configuração")
    @Enumerated(EnumType.STRING)
    private TipoConfigAlteracaoOrc tipoConfigAlteracaoOrc;
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private NumeroInicialContaReceita numeroInicialContaReceita;

    public ConfigAlteracaoOrc() {
        super();
    }

    public NumeroInicialContaReceita getNumeroInicialContaReceita() {
        return numeroInicialContaReceita;
    }

    public void setNumeroInicialContaReceita(NumeroInicialContaReceita numeroInicialContaReceita) {
        this.numeroInicialContaReceita = numeroInicialContaReceita;
    }

    public TipoDespesaORC getTipoDespesaORC() {
        return tipoDespesaORC;
    }

    public void setTipoDespesaORC(TipoDespesaORC tipoDespesaORC) {
        this.tipoDespesaORC = tipoDespesaORC;
    }

    public TipoAlteracaoORC getTipoAlteracaoORC() {
        return tipoAlteracaoORC;
    }

    public void setTipoAlteracaoORC(TipoAlteracaoORC tipoAlteracaoORC) {
        this.tipoAlteracaoORC = tipoAlteracaoORC;
    }

    public OrigemSuplementacaoORC getOrigemSuplementacaoORC() {
        return origemSuplementacaoORC;
    }

    public void setOrigemSuplementacaoORC(OrigemSuplementacaoORC origemSuplementacaoORC) {
        this.origemSuplementacaoORC = origemSuplementacaoORC;
    }

    public TipoConfigAlteracaoOrc getTipoConfigAlteracaoOrc() {
        return tipoConfigAlteracaoOrc;
    }

    public void setTipoConfigAlteracaoOrc(TipoConfigAlteracaoOrc tipoConfigAlteracaoOrc) {
        this.tipoConfigAlteracaoOrc = tipoConfigAlteracaoOrc;
    }
}
