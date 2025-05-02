package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Stateless
public class GeraValorDividaBloqueioJudicial extends ValorDividaFacade {

    private static final Logger log = LoggerFactory.getLogger(GeraValorDividaBloqueioJudicial.class);

    public GeraValorDividaBloqueioJudicial() {
        super(false);
    }

    @Override
    protected void defineVencimentosParcelas(Calculo calculo, OpcaoPagamento op, int i, ParcelaValorDivida parcelaValorDivida, ValorDivida valorDivida, Calendar vencimento, Integer numeroParcelas) {
        parcelaValorDivida.setVencimento(((CalculoBloqueioJudicial) calculo).getVencimento());
    }

    @Override
    protected void lancaItem(ValorDivida valorDivida) {
        CalculoBloqueioJudicial calculo = em.find(CalculoBloqueioJudicial.class, valorDivida.getCalculo().getId());
        for (ItemCalculoBloqueioJudicial item : calculo.getItensBloqueio()) {
            ItemValorDivida itemValorDivida = new ItemValorDivida();
            itemValorDivida.setTributo(item.getTributo());
            itemValorDivida.setValor(item.getValor());
            itemValorDivida.setValorDivida(valorDivida);
            valorDivida.getItemValorDividas().add(itemValorDivida);
        }
    }

    @Override
    public boolean definirComoDividaAtiva(Calculo calculo) {
        return ((CalculoBloqueioJudicial) calculo).getDividaAtiva();
    }

    @Override
    public boolean definirComoDividaAtivaAjuizada(Calculo calculo) {
        return ((CalculoBloqueioJudicial) calculo).getDividaAtivaAjuizada();
    }

    public void geraValorDivida(CalculoBloqueioJudicial calculoBloqueioJudicial) throws Exception {
        if (calculoBloqueioJudicial.getIsento() == null || !calculoBloqueioJudicial.getIsento()) {
            ValorDivida valorDivida = inicializaValorDivida(calculoBloqueioJudicial.getValorEfetivo(),
                calculoBloqueioJudicial, calculoBloqueioJudicial.getDivida(),
                calculoBloqueioJudicial.getExercicioParcelaOriginal());
            lancaItem(valorDivida);
            if (!valorDivida.getItemValorDividas().isEmpty()) {
                geraParcelas(valorDivida);
            }
            salvaValorDivida(valorDivida);
        }
    }

    public void geraParcelas(ValorDivida valorDivida) throws Exception {
        CalculoBloqueioJudicial calculoBloqueioJudicial = (CalculoBloqueioJudicial) valorDivida.getCalculo();
        ParcelaValorDivida parcelaOriginal = em.find(ParcelaValorDivida.class, calculoBloqueioJudicial.getIdParcelaOriginal());
        Hibernate.initialize(parcelaOriginal.getItensParcelaValorDivida());

        ParcelaValorDivida parcelaValorDivida = new ParcelaValorDivida();
        parcelaValorDivida.setDividaAtiva(parcelaOriginal.getDividaAtiva());
        parcelaValorDivida.setDividaAtivaAjuizada(parcelaOriginal.getDividaAtivaAjuizada());
        parcelaValorDivida.setSequenciaParcela(calculoBloqueioJudicial.getSequenciaParcela());
        parcelaValorDivida.setQuantidadeParcela(calculoBloqueioJudicial.getQuantidadeParcela());

        parcelaValorDivida.setOpcaoPagamento(parcelaOriginal.getOpcaoPagamento());
        parcelaValorDivida.setValorDivida(valorDivida);
        parcelaValorDivida.setValor(calculoBloqueioJudicial.getValorEfetivo());
        parcelaValorDivida.setVencimento(parcelaOriginal.getVencimento());
        parcelaValorDivida.setPercentualValorTotal(CEM);
        parcelaValorDivida.setItensParcelaValorDivida(Lists.newArrayList());
        for (ItemParcelaValorDivida ipo : parcelaOriginal.getItensParcelaValorDivida()) {
            ItemParcelaValorDivida itemParcelaValorDivida = new ItemParcelaValorDivida();
            itemParcelaValorDivida.setParcelaValorDivida(parcelaValorDivida);
            ItemValorDivida itemValorDivida = valorDivida.getItemValorDividas().stream()
                .filter((ivd) -> ivd.getTributo().equals(ipo.getTributo()))
                .findFirst().get();
            itemParcelaValorDivida.setItemValorDivida(itemValorDivida);
            ItemCalculoBloqueioJudicial itemCalculoBloqueioJudicial = calculoBloqueioJudicial.getItensBloqueio().stream()
                .filter((icb) -> icb.getTributo().equals(ipo.getTributo()))
                .findFirst().get();
            itemParcelaValorDivida.setValor(itemCalculoBloqueioJudicial.getValor());
            BigDecimal percentual = itemCalculoBloqueioJudicial.getProporcaoResidual().divide(CEM, 8, RoundingMode.HALF_UP);
            if (ipo.getDescontos() != null && !ipo.getDescontos().isEmpty()) {
                BigDecimal valorTotalDescontos = ipo.getDescontos().stream()
                    .filter((d) -> d.getTipo().equals(DescontoItemParcela.Tipo.VALOR))
                    .map(DescontoItemParcela::getDesconto)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                valorTotalDescontos = valorTotalDescontos.multiply(percentual);
                for (DescontoItemParcela dipo : ipo.getDescontos()) {
                    DescontoItemParcela descontoItemParcela = new DescontoItemParcela();
                    descontoItemParcela.setItemParcelaValorDivida(itemParcelaValorDivida);
                    descontoItemParcela.setTipo(dipo.getTipo());
                    descontoItemParcela.setOrigem(dipo.getOrigem());
                    descontoItemParcela.setMotivo(dipo.getMotivo());
                    descontoItemParcela.setInicio(dipo.getInicio());
                    descontoItemParcela.setFim(dipo.getFim());
                    descontoItemParcela.setAtoLegal(dipo.getAtoLegal());
                    if (DescontoItemParcela.Tipo.VALOR.equals(dipo.getTipo())) {
                        descontoItemParcela.setDesconto(dipo.getDesconto().multiply(percentual));
                    } else {
                        descontoItemParcela.setDesconto(dipo.getDesconto());
                    }
                    itemParcelaValorDivida.getDescontos().add(descontoItemParcela);
                }
                BigDecimal valorTotalDescontosAplicado = itemParcelaValorDivida.getDescontos().stream()
                    .filter((d) -> d.getTipo().equals(DescontoItemParcela.Tipo.VALOR))
                    .map(DescontoItemParcela::getDesconto)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal diferenca = valorTotalDescontos.subtract(valorTotalDescontosAplicado);
                if (diferenca.compareTo(BigDecimal.ZERO) != 0) {
                    DescontoItemParcela primeiroDescontoValor = itemParcelaValorDivida.getDescontos().stream()
                        .filter((d) -> DescontoItemParcela.Tipo.VALOR.equals(d.getTipo()))
                        .findFirst().get();
                    primeiroDescontoValor.setDesconto(primeiroDescontoValor.getDesconto().add(diferenca));
                }
            }
            parcelaValorDivida.getItensParcelaValorDivida().add(itemParcelaValorDivida);
        }
        valorDivida.getParcelaValorDividas().add(parcelaValorDivida);
        lancaSituacoes(valorDivida);
    }

    private void lancaSituacoes(ValorDivida valorDivida) {
        for (ParcelaValorDivida pvd : valorDivida.getParcelaValorDividas()) {
            lancaSituacao(pvd);
        }
    }
}
