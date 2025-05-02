package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoVencimentoParcela;
import br.com.webpublico.exception.UFMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Stateless
public class GeraValorDividaDividaAtiva extends ValorDividaFacade {

    private static final Logger logger = LoggerFactory.getLogger(GeraValorDividaDividaAtiva.class);
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;

    public GeraValorDividaDividaAtiva() {
        super(false);
    }

    public void geraValorDividaDaDividaAtiva(InscricaoDividaAtiva processo) throws Exception {

        for (DividaAtivaDivida divida : processo.getDividaAtivaDividas()) {
            for (ItemInscricaoDividaAtiva item : processo.getItensInscricaoDividaAtivas()) {
                Divida dividaDestino = em.find(Divida.class, divida.getDivida().getDivida().getId());
                ValorDivida valorDivida = inicializaValorDivida(BigDecimal.ZERO, item, dividaDestino, item.getExercicio());
                lancaItemDaDividaAtiva(valorDivida, item, divida.getDivida());
                if (!valorDivida.getItemValorDividas().isEmpty()) {
                    List<InscricaoDividaParcela> parcelasDaMesmaDivida = new ArrayList<>();
                    for (InscricaoDividaParcela parcela : item.getItensInscricaoDividaParcelas()) {
                        if (parcela.getParcelaValorDivida().getValorDivida().getDivida().equals(divida.getDivida())) {
                            parcelasDaMesmaDivida.add(parcela);
                        }
                    }
                    for (OpcaoPagamentoDivida opcao : dividaDestino.getOpcaoPagamentosDivida()) {
                        lancaParcelasDividaAtiva(valorDivida, item, parcelasDaMesmaDivida, opcao);
                    }
                }
                salvaValorDivida(valorDivida);
            }
        }

    }

    private void lancaItemDaDividaAtiva(ValorDivida valorDivida, ItemInscricaoDividaAtiva calculo, Divida divida) {
        Map<Tributo, BigDecimal> mapa = new HashMap<>();
        for (InscricaoDividaParcela inscricao : calculo.getItensInscricaoDividaParcelas()) {
            if (inscricao.getParcelaValorDivida().getValorDivida().getDivida().equals(divida)) {
                ParcelaValorDivida parcelaAntiga = em.find(ParcelaValorDivida.class, inscricao.getParcelaValorDivida().getId());
                BigDecimal saldo = consultaDebitoFacade.getUltimaSituacao(parcelaAntiga).getSaldo();
                for (ItemParcelaValorDivida itemAntigo : parcelaAntiga.getItensParcelaValorDivida()) {
                    if (isNotAcrescimoDeNotaFiscalEletronica(parcelaAntiga, itemAntigo)) {
                        BigDecimal valorParcelapPorCem = itemAntigo.getValor().multiply(CEM);
                        BigDecimal percentualValorTotal = valorParcelapPorCem.divide(parcelaAntiga.getValor(), 8, RoundingMode.HALF_EVEN);
                        BigDecimal valorProporcionalPago = percentualValorTotal.divide(CEM).multiply(saldo);
                        if (mapa.containsKey(itemAntigo.getItemValorDivida().getTributo())) {
                            BigDecimal novoValor = mapa.get(itemAntigo.getItemValorDivida().getTributo()).add(valorProporcionalPago);
                            mapa.put(itemAntigo.getItemValorDivida().getTributo(), novoValor);
                        } else {
                            mapa.put(itemAntigo.getItemValorDivida().getTributo(), valorProporcionalPago);
                        }
                    }
                }
            }
        }
        BigDecimal total = BigDecimal.ZERO;
        for (Tributo tributo : mapa.keySet()) {
            ItemValorDivida itemValorDivida = new ItemValorDivida();
            itemValorDivida.setTributo(tributo);
            itemValorDivida.setValor(mapa.get(tributo));
            itemValorDivida.setValorDivida(valorDivida);
            valorDivida.getItemValorDividas().add(itemValorDivida);
            total = total.add(mapa.get(tributo));
        }
        valorDivida.setValor(total);
    }

    private boolean isNotAcrescimoDeNotaFiscalEletronica(ParcelaValorDivida parcelaAntiga, ItemParcelaValorDivida itemAntigo) {
        return !itemAntigo.getItemValorDivida().getTributo().getTipoTributo().isAcrescimos() &&
            !parcelaAntiga.getValorDivida().getCalculo().getTipoCalculo().equals(Calculo.TipoCalculo.NFSE);
    }

    private void lancaParcelasDividaAtiva(ValorDivida valorDivida, ItemInscricaoDividaAtiva calculo, List<InscricaoDividaParcela> itens, OpcaoPagamentoDivida opcao) throws Exception {
        int sequenciaParcela = 1;
        Date vencimento = new Date();

        TipoVencimentoParcela tipoVencimento = calculo.getInscricaoDividaAtiva().getTipoVencimentoParcela();
        if (tipoVencimento.equals(TipoVencimentoParcela.PRIMEIRO_VENCIMENTO)) {
            vencimento = getPrimeiraParcelaInscricaoDividaAtiva(itens.get(0).getParcelaValorDivida().getValorDivida()).getVencimento();
        } else if (tipoVencimento.equals(TipoVencimentoParcela.PRIMEIRA_EM_ABERTO)) {
            vencimento = getPrimeiroVencimentoParcelaEmAberto(itens);
        }

        if (calculo.getInscricaoDividaAtiva().getAgruparParcelas()) {
            geraParcelaDividaAtiva(valorDivida, null, sequenciaParcela, vencimento, null, opcao);
        } else {
            for (InscricaoDividaParcela inscricao : itens) {
                ParcelaValorDivida parcelaAntiga = inscricao.getParcelaValorDivida();
                if (tipoVencimento.equals(TipoVencimentoParcela.NAO_ALTERAR)) {
                    vencimento = parcelaAntiga.getVencimento();
                }
                geraParcelaDividaAtiva(valorDivida, null, sequenciaParcela, vencimento, parcelaAntiga, opcao);
                sequenciaParcela++;
            }
        }
        lancaItensParcela(valorDivida, opcao);
    }

    private void geraParcelaDividaAtiva(ValorDivida valorDivida, Long numerosDAM, int sequenciaParcela, Date vencimento, ParcelaValorDivida parcelaAntiga, OpcaoPagamentoDivida opcao) throws UFMException, Exception {
        ParcelaValorDivida parcelaValorDivida = new ParcelaValorDivida();
        parcelaValorDivida.setDividaAtiva(Boolean.TRUE);
        parcelaValorDivida.setItensParcelaValorDivida(new ArrayList<ItemParcelaValorDivida>());
        parcelaValorDivida.setOpcaoPagamento(opcao.getOpcaoPagamento());
        parcelaValorDivida.setValorDivida(valorDivida);
        BigDecimal saldo = BigDecimal.ZERO;
        BigDecimal percentual = CEM;
        if (parcelaAntiga != null) {
            saldo = consultaDebitoFacade.getUltimaSituacao(parcelaAntiga).getSaldo();
            parcelaValorDivida.setValor(saldo);
            parcelaValorDivida.setValor(saldo);
            BigDecimal valorParcelapPorCem = parcelaValorDivida.getValor().multiply(CEM);
            percentual = valorParcelapPorCem.divide(valorDivida.getValor(), 8, RoundingMode.HALF_EVEN);
        } else {
            parcelaValorDivida.setValor(valorDivida.getValor());
            parcelaValorDivida.setValor(valorDivida.getValor());
            saldo = valorDivida.getValor();
        }
        parcelaValorDivida.getSituacoes().add(new SituacaoParcelaValorDivida(SituacaoParcela.EM_ABERTO, parcelaValorDivida, saldo));
        parcelaValorDivida.setPercentualValorTotal(percentual);
        parcelaValorDivida.setSequenciaParcela(String.valueOf(sequenciaParcela));
        parcelaValorDivida.setVencimento(vencimento);
        parcelaValorDivida.setValorDivida(valorDivida);
        valorDivida.getParcelaValorDividas().add(parcelaValorDivida);

    }

    private ParcelaValorDivida getPrimeiraParcelaInscricaoDividaAtiva(ValorDivida valorDivida) {
        String hql = "select pvd from ParcelaValorDivida pvd  " +
                "  where pvd.valorDivida = :valorDivida " +
                "  and pvd.vencimento = "
                + "(select min(pv.vencimento) from ParcelaValorDivida pv  "
                + "where pv.valorDivida = :valorDivida)";
        Query q = em.createQuery(hql).setParameter("valorDivida", valorDivida);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (ParcelaValorDivida) q.getResultList().get(0);
    }

    public Date getPrimeiroVencimentoParcelaEmAberto(List<InscricaoDividaParcela> itens) {
        Date data = itens.get(0).getParcelaValorDivida().getVencimento();
        for (InscricaoDividaParcela inscricaoDividaParcela : itens) {
            Date novaData = inscricaoDividaParcela.getParcelaValorDivida().getVencimento();
            if (novaData.compareTo(data) < 0) {
                data = novaData;
            }
        }
        return data;
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
