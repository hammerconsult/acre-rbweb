package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 10/06/14
 * Time: 18:04
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Audited
@Etiqueta("Item de Solicitação Baixa Patrimonial")
@Table(name = "ITEMSOLICITBAIXAPATRIMONIO")
public class ItemSolicitacaoBaixaPatrimonial extends EventoBem {

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Solicitação de Baixa")
    private SolicitacaoBaixaPatrimonial solicitacaoBaixa;

    public ItemSolicitacaoBaixaPatrimonial() {
        super(TipoEventoBem.ITEMSOLICITACAOBAIXAPATRIMONIAL, TipoOperacao.NENHUMA_OPERACAO);
    }

    public SolicitacaoBaixaPatrimonial getSolicitacaoBaixa() {
        return solicitacaoBaixa;
    }

    public void setSolicitacaoBaixa(SolicitacaoBaixaPatrimonial solicitacaoBaixa) {
        this.solicitacaoBaixa = solicitacaoBaixa;
    }
}
