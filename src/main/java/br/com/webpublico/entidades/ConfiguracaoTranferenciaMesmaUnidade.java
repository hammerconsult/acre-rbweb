/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.OrigemTipoTransferencia;
import br.com.webpublico.enums.ResultanteIndependente;
import br.com.webpublico.enums.TipoTransferenciaMesmaUnidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * @author mga
 */
@Table(name = "CONFIGTRANSFMESMAUNID")
@Entity

@Inheritance(strategy = InheritanceType.JOINED)
@Audited
@Etiqueta(value = "Configuração de Transferência de Mesma Unidade")
public class ConfiguracaoTranferenciaMesmaUnidade extends ConfiguracaoEvento {

    @Obrigatorio
    @Etiqueta("Operação")
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPOTRANSFMESMAUNID")
    @Tabelavel
    private TipoTransferenciaMesmaUnidade operacao;
    @Obrigatorio
    @Etiqueta("Tipo Transfêrencia")
    @Column(name = "ORIGEMTIPOTRANSF")
    @Enumerated(EnumType.STRING)
    @Tabelavel
    private OrigemTipoTransferencia tipoTransferencia;
    @Obrigatorio
    @Etiqueta("Resultante / Independente")
    @Enumerated(EnumType.STRING)
    @Tabelavel
    private ResultanteIndependente resultanteIndependente;

    public TipoTransferenciaMesmaUnidade getOperacao() {
        return operacao;
    }

    public void setOperacao(TipoTransferenciaMesmaUnidade operacao) {
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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + (this.operacao != null ? this.operacao.hashCode() : 0);
        hash = 29 * hash + (this.tipoTransferencia != null ? this.tipoTransferencia.hashCode() : 0);
        hash = 29 * hash + (this.getTipoLancamento() != null ? this.getTipoLancamento().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ConfiguracaoTranferenciaMesmaUnidade other = (ConfiguracaoTranferenciaMesmaUnidade) obj;
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
