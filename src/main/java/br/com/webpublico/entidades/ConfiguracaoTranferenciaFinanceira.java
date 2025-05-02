/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.OrigemTipoTransferencia;
import br.com.webpublico.enums.ResultanteIndependente;
import br.com.webpublico.enums.TipoTransferenciaFinanceira;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * @author mga
 */
@Etiqueta("Configuração de Transferência Financeira")
@Table(name = "CONFLIBERACAOTRANSFFINANC")
@Entity

@Inheritance(strategy = InheritanceType.JOINED)
@Audited
public class ConfiguracaoTranferenciaFinanceira extends ConfiguracaoEvento {

    @Obrigatorio
    @Etiqueta("Operação")
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPOTRANSFINANC")
    @Tabelavel
    @Pesquisavel
    private TipoTransferenciaFinanceira operacao;
    @Obrigatorio
    @Etiqueta("Tipo Transfêrencia")
    @Column(name = "ORIGEMTIPOTRANSF")
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    private OrigemTipoTransferencia tipoTransferencia;
    @Obrigatorio
    @Etiqueta("Resultante / Independente")
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    private ResultanteIndependente resultanteIndependente;

    public TipoTransferenciaFinanceira getOperacao() {
        return operacao;
    }

    public void setOperacao(TipoTransferenciaFinanceira operacao) {
        this.operacao = operacao;
    }

    public OrigemTipoTransferencia getTipoTransferencia() {
        return tipoTransferencia;
    }

    public void setTipoTransferencia(OrigemTipoTransferencia tipoTransferencia) {
        this.tipoTransferencia = tipoTransferencia;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 19 * hash + (this.operacao != null ? this.operacao.hashCode() : 0);
        hash = 19 * hash + (this.tipoTransferencia != null ? this.tipoTransferencia.hashCode() : 0);
        hash = 19 * hash + (this.getTipoLancamento() != null ? this.getTipoLancamento().hashCode() : 0);
        return hash;
    }

    public ResultanteIndependente getResultanteIndependente() {
        return resultanteIndependente;
    }

    public void setResultanteIndependente(ResultanteIndependente resultanteIndependente) {
        this.resultanteIndependente = resultanteIndependente;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ConfiguracaoTranferenciaFinanceira other = (ConfiguracaoTranferenciaFinanceira) obj;
        if (this.operacao != other.operacao) {
            return false;
        }
        if (this.tipoTransferencia != other.tipoTransferencia) {
            return false;
        }
        if (this.getTipoLancamento() != other.getTipoLancamento()) {
            return false;
        }
        return true;
    }
}
