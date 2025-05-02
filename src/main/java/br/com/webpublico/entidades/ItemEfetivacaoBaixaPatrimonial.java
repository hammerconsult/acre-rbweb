package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 10/06/14
 * Time: 18:43
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Item Efetivação de Baixa Patrimonial")
@Table(name = "ITEMEFETIVACAOBAIXA")
public class ItemEfetivacaoBaixaPatrimonial extends EventoBem {

    @Tabelavel
    @Pesquisavel
    @ManyToOne
    @Etiqueta("Efetivação de Baixa Patrimonial")
    private EfetivacaoBaixaPatrimonial efetivacaoBaixa;

    public ItemEfetivacaoBaixaPatrimonial() {
        super(TipoEventoBem.ITEMEFETIVACAOBAIXAPATRIMONIAL, TipoOperacao.CREDITO);
    }

    public EfetivacaoBaixaPatrimonial getEfetivacaoBaixa() {
        return efetivacaoBaixa;
    }

    public void setEfetivacaoBaixa(EfetivacaoBaixaPatrimonial efetivacaoBaixa) {
        this.efetivacaoBaixa = efetivacaoBaixa;
    }

    @Override
    public BigDecimal getValorDoLancamento() {
        return valorDoLancamento;
    }
}
