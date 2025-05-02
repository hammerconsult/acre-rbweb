package br.com.webpublico.negocios;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoContratoRendasPatrimoniais;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Stateless
public class GeraValorDividaRendasPatrimoniais extends ValorDividaFacade {

    @EJB
    private FeriadoFacade feriadoFacade;

    public GeraValorDividaRendasPatrimoniais() {
        super(false);
    }

    private Date primeiroVencimento;

    public void setPrimeiroVencimento(Date primeiroVencimento) {
        this.primeiroVencimento = primeiroVencimento;
    }

    @Override
    protected Date primeiroVencimento(Calculo calculo, Parcela p) {
        if (primeiroVencimento != null) {
            return primeiroVencimento;
        } else {
            ContratoRendasPatrimoniais contrato = ((CalculoRendas) calculo).getContrato();
            return contrato.getPrimeiroVencimentoContrato(feriadoFacade).getTime();
        }
    }

    @Override
    protected void lancaOpcaoPagamento(ValorDivida valorDivida, List<OpcaoPagamentoDivida> opcoesPagamento) throws Exception {
        for (OpcaoPagamentoDivida opcaoPagamentoDivida : opcoesPagamento) {
            geraParcelas(opcaoPagamentoDivida, valorDivida, ((CalculoRendas) valorDivida.getCalculo()).getContrato().getQuantidadeParcelas());
        }
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    @Override
    protected void defineVencimentosParcelas(Calculo calculo, OpcaoPagamento op, int i, ParcelaValorDivida parcelaValorDivida, ValorDivida valorDivida, Calendar vencimento, Integer numeroParcelas) {
        if (i == 0) {
            BigDecimal total = valorDivida.getValor().setScale(2, RoundingMode.HALF_EVEN);
            BigDecimal valorParcela = total.divide(new BigDecimal(numeroParcelas), RoundingMode.DOWN);
            BigDecimal somaParcelas = valorParcela.multiply(new BigDecimal(numeroParcelas));
            BigDecimal primeiraParcela = total.subtract(somaParcelas);
            primeiraParcela = primeiraParcela.add(valorParcela);
            parcelaValorDivida.setValor(primeiraParcela);
            parcelaValorDivida.setVencimento(vencimento.getTime());
        } else {
            ContratoRendasPatrimoniais contrato = ((CalculoRendas) calculo).getContrato();
            contrato.proximoVencimento(feriadoFacade, vencimento);
            parcelaValorDivida.setVencimento(vencimento.getTime());
        }
    }

    @Override
    protected void lancaItem(ValorDivida valorDivida) {
        ItemValorDivida itemValorDivida = new ItemValorDivida();
        itemValorDivida.setTributo(configuracaoTributarioFacade.retornaUltimo().getTributoRendasPatrimoniais());
        itemValorDivida.setValor(valorDivida.getValor());
        itemValorDivida.setValorDivida(valorDivida);
        valorDivida.getItemValorDividas().add(itemValorDivida);
    }

    protected void geraDAM(ValorDivida valordivida) throws Exception {
        damFacade.geraDAM(valordivida, valordivida.getExercicio(), sistemaFacade.getUsuarioCorrente());
    }


}
