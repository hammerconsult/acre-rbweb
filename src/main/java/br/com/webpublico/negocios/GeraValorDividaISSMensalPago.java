package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ParcelaValorDivida;
import br.com.webpublico.entidades.SituacaoParcelaValorDivida;
import br.com.webpublico.entidades.ValorDivida;
import br.com.webpublico.enums.SituacaoParcela;

import javax.ejb.Stateless;

/**
 * Created by William on 11/04/2017.
 */
@Stateless
public class GeraValorDividaISSMensalPago extends GeraValorDividaISS {

    @Override
    protected void geraDAM(ValorDivida valordivida) throws Exception {

    }

    @Override
    protected void lancaSituacao(ParcelaValorDivida parcelaValorDivida) {
        parcelaValorDivida.getSituacoes().add(new SituacaoParcelaValorDivida(SituacaoParcela.PAGO, parcelaValorDivida, parcelaValorDivida.getValor()));
        parcelaValorDivida.setSituacaoAtual(parcelaValorDivida.getSituacoes().get(0));
    }
}
