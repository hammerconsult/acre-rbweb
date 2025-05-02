package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.transaction.SystemException;
import java.util.Calendar;
import java.util.List;

/**
 * Created by William on 07/01/2016.
 */
@Stateless
public class GeraValorDividaCeasaAutomatico extends ValorDividaFacade {
    private List<ValorDivida> debitos;
    @EJB
    private FacadeAutoGerenciado facadeAutoGerenciado;

    public GeraValorDividaCeasaAutomatico() {
        super(false);
    }

    @Override
    protected void lancaOpcaoPagamento(ValorDivida valorDivida, List<OpcaoPagamentoDivida> opcoesPagamento) throws Exception {
        for (OpcaoPagamentoDivida opcaoPagamentoDivida : opcoesPagamento) {
            geraParcelas(opcaoPagamentoDivida, valorDivida, ((CalculoCEASA) valorDivida.getCalculo()).getContrato().getQuantidadeParcelas());
        }
    }

    @Override
    protected void lancaItem(ValorDivida valorDivida) {
        for (ItensCalculoCEASA item : ((CalculoCEASA) valorDivida.getCalculo()).getItensCalculoCEASA()) {
            ItemValorDivida itemValorDivida = new ItemValorDivida();
            itemValorDivida.setTributo(item.getTributo());
            itemValorDivida.setValor(item.getValor());
            itemValorDivida.setValorDivida(valorDivida);
            valorDivida.getItemValorDividas().add(itemValorDivida);
        }
    }

    @Override
    protected void defineVencimentosParcelas(Calculo calculo, OpcaoPagamento op, int i, ParcelaValorDivida parcelaValorDivida, ValorDivida valorDivida, Calendar vencimento, Integer numeroParcelas) {

        int diaVencimento = ((CalculoCEASA) valorDivida.getCalculo()).getContrato().getDiaVencimento();


        Calendar c = Calendar.getInstance();
        c.setTime(((CalculoCEASA) valorDivida.getCalculo()).getContrato().getDataInicio());
        c.set(Calendar.DAY_OF_MONTH, ((CalculoCEASA) valorDivida.getCalculo()).getContrato().getDiaVencimento());

        Calendar dataAuxiliar = Calendar.getInstance();

        dataAuxiliar.setTime(((CalculoCEASA) valorDivida.getCalculo()).getContrato().getDataInicio());

        if (diaVencimento >= dataAuxiliar.get(Calendar.DAY_OF_MONTH)) {
            dataAuxiliar.set(Calendar.DAY_OF_MONTH, diaVencimento);
            dataAuxiliar.add(Calendar.MONTH, i);
            parcelaValorDivida.setVencimento(dataAuxiliar.getTime());
        } else {
            dataAuxiliar.set(Calendar.DAY_OF_MONTH, diaVencimento);
            dataAuxiliar.add(Calendar.MONTH, ++i);
            parcelaValorDivida.setVencimento(dataAuxiliar.getTime());

        }
        parcelaValorDivida.setVencimento(dataAuxiliar.getTime());
    }

    protected void geraDAM(ValorDivida valordivida) throws Exception {
        damFacade.geraDAM(valordivida, valordivida.getExercicio(), sistemaFacade.getUsuarioCorrente());
    }

    public void geraDebito(List<ProcessoCalculoCEASA> processosCalculo, UsuarioSistema usuario) {
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

    private void salvaCinquentaDebitos(UsuarioSistema usuario) {
        if (debitos.size() >= 50) {
            persisteValorDivida(usuario);
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
