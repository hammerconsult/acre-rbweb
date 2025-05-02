package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoParcela;

import javax.ejb.Stateless;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Stateless
public class GeraValorDividaInconsistencia extends ValorDividaFacade {

    public GeraValorDividaInconsistencia() {
        super(false);
    }

    public List<ValorDivida> lancaValorDividaInconsistente(ProcessoInconsistencia processo, ConfiguracaoDAM configuracaoDAM, UsuarioSistema usuario) {
        List<ValorDivida> lista = new ArrayList<>();
        for (CalculoInconsistencia calculo : processo.getCalculos()) {
            ValorDivida valorDivida = geraValorInconsistencia(calculo, configuracaoDAM, usuario);
            lista.add(valorDivida);
        }
        return lista;
    }

    private ValorDivida geraValorInconsistencia(CalculoInconsistencia calculo, ConfiguracaoDAM configuracaoDAM, UsuarioSistema usuario) {
        ValorDivida valorDivida = inicializaValorDivida(calculo.getValorEfetivo(), calculo, configuracaoDAM != null ? configuracaoDAM.getDividaDamNaoEncontrado() : configuracaoTributarioFacade.retornaUltimo().getDividaInconsistencia(), calculo.getProcessoInconsistencia().getExercicio());
        try {
            lancaItemInconsistencia(valorDivida, calculo.getTributo());
            if (!valorDivida.getItemValorDividas().isEmpty()) {
                valorDivida.getDivida().getOpcaoPagamentosDivida();
                for (OpcaoPagamentoDivida op : valorDivida.getDivida().getOpcaoPagamentosDivida()) {
                    geraParcelasInconsistentes(valorDivida, op);
                }
            }
            valorDivida = em.merge(valorDivida);
            //geraDAM(valorDivida, usuario);
            return valorDivida;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void geraDAM(ValorDivida valorDivida, UsuarioSistema usuario, LoteBaixa loteBaixa) {
        try {
            DAM dam = damFacade.gerarDAMUnicoInconsistenciaViaApi(valorDivida, usuario);
            dam = damFacade.recuperar(dam.getId());
            ItemLoteBaixa item = null;
            for (ItemLoteBaixa itemLoteBaixa : loteBaixa.getItemLoteBaixa()) {
                if (itemLoteBaixa.getId().equals(((CalculoInconsistencia) valorDivida.getCalculo()).getItemLoteBaixa().getId())) {
                    item = itemLoteBaixa;
                    break;
                }
            }
            if (item != null) {
                item.setDam(dam);
                for (ItemDAM itemDAM : dam.getItens()) {
                    ItemLoteBaixaParcela itemLoteBaixaParcela = new ItemLoteBaixaParcela();
                    itemLoteBaixaParcela.setItemDam(itemDAM);
                    itemLoteBaixaParcela.setItemLoteBaixa(item);
                    itemLoteBaixaParcela.setValorPago(itemDAM.getValorTotal());
                    item.getItemParcelas().add(itemLoteBaixaParcela);
                }
                em.merge(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void lancaItemInconsistencia(ValorDivida valorDivida, Tributo tributo) {
        ItemValorDivida itemValorDivida = new ItemValorDivida();
        itemValorDivida.setTributo(tributo);
        itemValorDivida.setValor(valorDivida.getValor());
        itemValorDivida.setValorDivida(valorDivida);
        valorDivida.getItemValorDividas().add(itemValorDivida);
    }

    private void geraParcelasInconsistentes(ValorDivida valorDivida, OpcaoPagamentoDivida op) throws Exception {
        ParcelaValorDivida parcelaValorDivida = new ParcelaValorDivida();
        parcelaValorDivida.setDividaAtiva(Boolean.FALSE);
        parcelaValorDivida.setItensParcelaValorDivida(new ArrayList<ItemParcelaValorDivida>());
        parcelaValorDivida.setOpcaoPagamento(op.getOpcaoPagamento());
        parcelaValorDivida.setValorDivida(valorDivida);
        parcelaValorDivida.setValor(valorDivida.getCalculo().getValorReal());
        parcelaValorDivida.setValor(valorDivida.getCalculo().getValorEfetivo());
        BigDecimal valorPago = ((CalculoInconsistencia) valorDivida.getCalculo()).getValorEfetivo();
        BigDecimal saldo = valorDivida.getCalculo().getValorReal().subtract(valorPago);
        SituacaoParcela situacao = SituacaoParcela.PAGO;
        if (saldo.compareTo(BigDecimal.ZERO) > 0) {
            situacao = SituacaoParcela.EM_ABERTO;
        }
        parcelaValorDivida.getSituacoes().add(new SituacaoParcelaValorDivida(SituacaoParcela.EM_ABERTO, parcelaValorDivida, valorDivida.getCalculo().getValorEfetivo()));
        parcelaValorDivida.getSituacoes().add(new SituacaoParcelaValorDivida(situacao, parcelaValorDivida, saldo));
        parcelaValorDivida.setPercentualValorTotal(new BigDecimal("100"));
        parcelaValorDivida.setSequenciaParcela("01");
        parcelaValorDivida.setVencimento(((CalculoInconsistencia) valorDivida.getCalculo()).getVencimento());
        parcelaValorDivida.setValorDivida(valorDivida);
        valorDivida.getParcelaValorDividas().add(parcelaValorDivida);
        lancaItensParcela(valorDivida, op);
    }

    @Override
    protected void defineVencimentosParcelas(Calculo calculo, OpcaoPagamento op, int i, ParcelaValorDivida parcelaValorDivida, ValorDivida valorDivida, Calendar vencimento, Integer numeroParcelas) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void lancaItem(ValorDivida valorDivida) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
