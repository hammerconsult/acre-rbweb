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
 * Time: 18:27
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Audited
@Etiqueta("Item do Parecer de Baixa Patrimonial")
@Table(name = "ITEMPARECEREBAIXA")
public class ItemParecerBaixaPatrimonial extends EventoBem {

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Parecer Baixa")
    @ManyToOne
    @Obrigatorio
    private ParecerBaixaPatrimonial parecerBaixa;

    public ItemParecerBaixaPatrimonial() {
        super(TipoEventoBem.ITEMPARECERBAIXAPATRIMONIAL, TipoOperacao.NENHUMA_OPERACAO);
    }

    public ParecerBaixaPatrimonial getParecerBaixa() {
        return parecerBaixa;
    }

    public void setParecerBaixa(ParecerBaixaPatrimonial parecerBaixa) {
        this.parecerBaixa = parecerBaixa;
    }
}
