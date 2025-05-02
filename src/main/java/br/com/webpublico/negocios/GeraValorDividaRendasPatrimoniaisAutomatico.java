package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.transaction.SystemException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Stateless
public class GeraValorDividaRendasPatrimoniaisAutomatico extends ValorDividaFacade {

    private List<ValorDivida> debitos;
    @EJB
    private FacadeAutoGerenciado facadeAutoGerenciado;

    public GeraValorDividaRendasPatrimoniaisAutomatico() {
        super(false);
    }

    @Override
    protected Date primeiroVencimento(Calculo calculo, Parcela p) {
        return new Date();
    }

    @Override
    protected void lancaOpcaoPagamento(ValorDivida valorDivida, List<OpcaoPagamentoDivida> opcoesPagamento) throws Exception {
        for (OpcaoPagamentoDivida opcaoPagamentoDivida : opcoesPagamento) {
            geraParcelas(opcaoPagamentoDivida, valorDivida, ((CalculoRendas) valorDivida.getCalculo()).getContrato().getQuantidadeParcelas());
        }
    }

    @Override
    protected void defineVencimentosParcelas(Calculo calculo, OpcaoPagamento op, int i, ParcelaValorDivida parcelaValorDivida, ValorDivida valorDivida, Calendar vencimento, Integer numeroParcelas) {
        int diaVencimento = ((CalculoRendas) valorDivida.getCalculo()).getContrato().getDiaVencimento();
        Calendar c = Calendar.getInstance();
        c.setTime(((CalculoRendas) valorDivida.getCalculo()).getContrato().getDataInicio());
        c.set(Calendar.DAY_OF_MONTH, ((CalculoRendas) valorDivida.getCalculo()).getContrato().getDiaVencimento());
        Calendar dataAuxiliar = Calendar.getInstance();
        dataAuxiliar.setTime(((CalculoRendas) valorDivida.getCalculo()).getContrato().getDataInicio());
        dataAuxiliar.set(Calendar.DAY_OF_MONTH, diaVencimento);

        Calendar dataAtual = Calendar.getInstance();
        if (diaVencimento >= dataAtual.get(Calendar.DAY_OF_MONTH)) {
            dataAuxiliar.add(Calendar.MONTH, i);
            parcelaValorDivida.setVencimento(dataAuxiliar.getTime());
        } else {
            dataAuxiliar.add(Calendar.MONTH, ++i);
            parcelaValorDivida.setVencimento(dataAuxiliar.getTime());

        }

        if (i == 0) {
            BigDecimal total = valorDivida.getValor().setScale(2, RoundingMode.HALF_EVEN);
            BigDecimal valorParcela = total.divide(new BigDecimal(numeroParcelas), RoundingMode.DOWN);
            BigDecimal somaParcelas = valorParcela.multiply(new BigDecimal(numeroParcelas));
            BigDecimal primeiraParcela = total.subtract(somaParcelas);
            primeiraParcela = primeiraParcela.add(valorParcela);
            parcelaValorDivida.setValor(primeiraParcela);
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

    public void geraDebito(List<ProcessoCalculoRendas> processosCalculo, UsuarioSistema usuario) {
        debitos = Lists.newArrayList();
        for (ProcessoCalculo processoCalculo : processosCalculo) {
            List<OpcaoPagamentoDivida> opcoesPagamento = validaOpcoesPagamento(processoCalculo.getDivida());
            try {
                geraValoresDivida(processoCalculo, opcoesPagamento);
                salvaCinquentaDebitos(usuario);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        salvaTodosDebitos(usuario);
    }

    private void salvaCinquentaDebitos(UsuarioSistema usuarioSistema) {
        if (debitos.size() >= 50) {
            persisteValorDivida(usuarioSistema);
        }
    }

    public void salvaValorDivida(ValorDivida valordivida) throws Exception {
        debitos.add(valordivida);
    }

    private void persisteValorDivida(UsuarioSistema usuarioSistema) {
        try {
            facadeAutoGerenciado.persisteValoresDivida(debitos, usuarioSistema);
        } catch (SystemException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        debitos = Lists.newArrayList();
    }

    private void salvaTodosDebitos(UsuarioSistema usuario) {
        if (!debitos.isEmpty()) {
            persisteValorDivida(usuario);
        }
    }
}
