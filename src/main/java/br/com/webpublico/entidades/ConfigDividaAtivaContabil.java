/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.OperacaoDividaAtiva;
import br.com.webpublico.enums.TiposCredito;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import java.io.Serializable;

/**
 * @author Claudio
 */
@Entity

@Audited
@Etiqueta("Configuração de Dívida Ativa Contábil")
public class ConfigDividaAtivaContabil extends ConfiguracaoEvento implements Serializable {

    @Obrigatorio
    @Tabelavel
    @Etiqueta(value = "Operação")
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private OperacaoDividaAtiva operacaoDividaAtiva;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Conta de Receita")
    private ContaReceita contaReceita;
    @Etiqueta(value = "Tipo de Conta de Receita")
    @Enumerated(EnumType.STRING)
    private TiposCredito tiposCredito;

    public ConfigDividaAtivaContabil() {
    }

    public OperacaoDividaAtiva getOperacaoDividaAtiva() {
        return operacaoDividaAtiva;
    }

    public void setOperacaoDividaAtiva(OperacaoDividaAtiva operacaoDividaAtiva) {
        this.operacaoDividaAtiva = operacaoDividaAtiva;
    }

    public ContaReceita getContaReceita() {
        return contaReceita;
    }

    public void setContaReceita(ContaReceita contaReceita) {
        this.contaReceita = contaReceita;
    }

    public TiposCredito getTiposCredito() {
        return tiposCredito;
    }

    public void setTiposCredito(TiposCredito tiposCredito) {
        this.tiposCredito = tiposCredito;
    }
}
