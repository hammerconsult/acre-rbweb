package br.com.webpublico.entidades;

import br.com.webpublico.enums.OrigemTipoTransferencia;
import br.com.webpublico.enums.ResultanteIndependente;
import br.com.webpublico.enums.TipoLiberacaoFinanceira;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 13/02/14
 * Time: 18:15
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Audited
@Etiqueta("Configuração de Liberação Financeira")
public class ConfigLiberacaoFinanceira extends ConfiguracaoEvento implements Serializable {

    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Operação")
    private TipoLiberacaoFinanceira operacao;
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo de Transfêrencia")
    private OrigemTipoTransferencia tipoTransferencia;
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Dependência da Execução Orçamentária")
    private ResultanteIndependente resultanteIndependente;

    public ConfigLiberacaoFinanceira() {
        super();
    }

    public TipoLiberacaoFinanceira getOperacao() {
        return operacao;
    }

    public void setOperacao(TipoLiberacaoFinanceira operacao) {
        this.operacao = operacao;
    }

    public OrigemTipoTransferencia getTipoTransferencia() {
        return tipoTransferencia;
    }

    public void setTipoTransferencia(OrigemTipoTransferencia tipoTransferencia) {
        this.tipoTransferencia = tipoTransferencia;
    }

    public ResultanteIndependente getResultanteIndependente() {
        return resultanteIndependente;
    }

    public void setResultanteIndependente(ResultanteIndependente resultanteIndependente) {
        this.resultanteIndependente = resultanteIndependente;
    }
}
