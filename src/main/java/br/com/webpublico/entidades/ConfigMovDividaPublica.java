/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.NaturezaDividaPublica;
import br.com.webpublico.enums.OperacaoMovimentoDividaPublica;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity

@Audited
@Etiqueta("Configuração do Movimento da Dívida Pública")
@Inheritance(strategy = InheritanceType.JOINED)
public class ConfigMovDividaPublica extends ConfiguracaoEvento implements Serializable {

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Operação")
    @Pesquisavel
    @Tabelavel
    private OperacaoMovimentoDividaPublica operacaoMovimentoDividaPublica;
    @Obrigatorio
    @Etiqueta("Natureza da Dívida Pública")
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Tabelavel
    private NaturezaDividaPublica naturezaDividaPublica;

    public ConfigMovDividaPublica(){
        super();
    }

    public OperacaoMovimentoDividaPublica getOperacaoMovimentoDividaPublica() {
        return operacaoMovimentoDividaPublica;
    }

    public void setOperacaoMovimentoDividaPublica(OperacaoMovimentoDividaPublica operacaoMovimentoDividaPublica) {
        this.operacaoMovimentoDividaPublica = operacaoMovimentoDividaPublica;
    }

    public NaturezaDividaPublica getNaturezaDividaPublica() {
        return naturezaDividaPublica;
    }

    public void setNaturezaDividaPublica(NaturezaDividaPublica naturezaDividaPublica) {
        this.naturezaDividaPublica = naturezaDividaPublica;
    }
}
