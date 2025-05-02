package br.com.webpublico.entidades;

import br.com.webpublico.enums.OperacaoCreditoReceber;
import br.com.webpublico.enums.TiposCredito;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Fabio
 */
@Entity

@Audited
@Etiqueta("Configuração de Crédito a Receber")
@Inheritance(strategy = InheritanceType.JOINED)
public class ConfigCreditoReceber extends ConfiguracaoEvento implements Serializable {

    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Etiqueta("Operação")
    @Tabelavel
    @Pesquisavel
    private OperacaoCreditoReceber operacaoCreditoReceber;
    @ManyToOne
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Conta de Receita")
    private ContaReceita contaReceita;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Conta de Receita")
    private TiposCredito tiposCredito;
    @Transient
    private Long criadoEm;

    public ContaReceita getContaReceita() {
        return contaReceita;
    }

    public void setContaReceita(ContaReceita contaReceita) {
        this.contaReceita = contaReceita;
    }

    public ConfigCreditoReceber() {
        criadoEm = System.nanoTime();
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public OperacaoCreditoReceber getOperacaoCreditoReceber() {
        return operacaoCreditoReceber;
    }

    public void setOperacaoCreditoReceber(OperacaoCreditoReceber operacaoCreditoReceber) {
        this.operacaoCreditoReceber = operacaoCreditoReceber;
    }

    public TiposCredito getTiposCredito() {
        return tiposCredito;
    }

    public void setTiposCredito(TiposCredito tiposCredito) {
        this.tiposCredito = tiposCredito;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }
}
