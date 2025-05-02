package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.DataUtil;
import org.hibernate.Hibernate;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Stateless
public abstract class ValorDividaFacade implements Serializable {

    public static final int NUMERO_CASAS_DECIMAIS = 2;
    public static final RoundingMode MODO_ARREDONDAMENTO = RoundingMode.HALF_UP;
    protected static final BigDecimal CEM = BigDecimal.valueOf(100);
    public Boolean consideraDesconto;
    @EJB
    protected FeriadoFacade feriadoFacade;
    @EJB
    protected DAMFacade damFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    protected EntityManager em;
    @EJB
    protected ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    protected CalculoDividaDiversaFacade calculoDividaDiversaFacade;
    protected Map<OpcaoPagamento, List<ParcelaValorDivida>> parcelasOpcaoPagamento = new HashMap<>();
    @EJB
    protected SistemaFacade sistemaFacade;
    @EJB
    protected ExercicioFacade exercicioFacade;
    @Resource
    protected SessionContext ctx;

    public ValorDividaFacade(Boolean consideraDesconto) {
        this.consideraDesconto = consideraDesconto;
    }

    public DAMFacade getDamFacade() {
        return damFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    protected Date primeiroVencimento(Calculo calculo, Parcela p) {
        return new Date();
    }

    protected abstract void defineVencimentosParcelas(Calculo calculo, OpcaoPagamento op, int i, ParcelaValorDivida parcelaValorDivida, ValorDivida valorDivida, Calendar vencimento, Integer numeroParcelas);

    protected abstract void lancaItem(ValorDivida valorDivida);

    public boolean definirComoDividaAtiva(Calculo calculo) {
        return false;
    }

    public boolean definirComoDividaAtivaAjuizada(Calculo calculo) {
        return false;
    }

    public void geraDebito(ProcessoCalculo processoCalculo) throws Exception {
        gerarDebito(processoCalculo, false);
    }

    public void gerarDebito(ProcessoCalculo processoCalculo, boolean rodarEmNovaTransacao) throws Exception {
        List<OpcaoPagamentoDivida> opcoesPagamento = validaOpcoesPagamento(processoCalculo.getDivida());
        gerarValoresDivida(processoCalculo, rodarEmNovaTransacao, opcoesPagamento);
    }

    public List<OpcaoPagamentoDivida> validaOpcoesPagamento(Divida divida, Date dataBase) throws IllegalArgumentException {
        Query q = em.createQuery(" from OpcaoPagamentoDivida op "
            + " where op.divida = :divida "
            + " and trunc(coalesce(op.inicioVigencia, current_date)) <= trunc(:inicioVigencia) "
            + " and trunc(coalesce(op.finalVigencia, current_date)) >= trunc(:finalVigencia) ");
        q.setParameter("divida", divida);
        q.setParameter("inicioVigencia", dataBase);
        q.setParameter("finalVigencia", dataBase);
        List<OpcaoPagamentoDivida> opcoesPagamento = q.getResultList();
        if (opcoesPagamento.isEmpty()) {
            throw new IllegalArgumentException("Nenhuma opção de pagamento válida para a dívida selecionada: " + divida.getDescricao());
        }
        return opcoesPagamento;
    }

    public List<OpcaoPagamentoDivida> validaOpcoesPagamento(Divida divida) throws IllegalArgumentException {
        return validaOpcoesPagamento(divida, new Date());
    }

    protected void geraValoresDivida(ProcessoCalculo processoCalculo, List<OpcaoPagamentoDivida> opcoes) throws Exception {
        gerarValoresDivida(processoCalculo, false, opcoes);
    }

    protected void gerarValoresDivida(ProcessoCalculo processoCalculo, boolean emNovaTransacao, List<OpcaoPagamentoDivida> opcoes) throws Exception {
        for (Calculo calculo : processoCalculo.getCalculos()) {
            gerarValorDivida(calculo, emNovaTransacao, opcoes);
        }
    }

    public void geraValorDivida(Calculo calculo, List<OpcaoPagamentoDivida> opcoes) throws Exception {
        gerarValorDivida(calculo, false, opcoes);
    }

    public void gerarValorDivida(Calculo calculo, boolean emNovaTransacao, List<OpcaoPagamentoDivida> opcoes) throws Exception {
        if (calculo.getIsento() == null || !calculo.getIsento()) {
            ValorDivida valorDivida = inicializaValorDivida(calculo.getValorEfetivo(), calculo, calculo.getProcessoCalculo().getDivida(), calculo.getProcessoCalculo().getExercicio());
            lancaItem(valorDivida);
            validarValorDivida(valorDivida);
            if (!valorDivida.getItemValorDividas().isEmpty()) {
                lancaOpcaoPagamento(valorDivida, opcoes);
            }
            if (emNovaTransacao) {
                ctx.getBusinessObject(this.getClass()).salvarValorDividaEmNovaTransacao(valorDivida);
            } else {
                salvaValorDivida(valorDivida);
            }
        }
    }

    protected ValorDivida inicializaValorDivida(BigDecimal valor, Calculo calculo, Divida divida, Exercicio exercicio) {
        ValorDivida valorDivida = new ValorDivida();
        valorDivida.setOcorrenciaValorDivida(new ArrayList<OcorrenciaValorDivida>());
        valorDivida.setCalculo(calculo);
        valorDivida.setItemValorDividas(new ArrayList<ItemValorDivida>());
        valorDivida.setParcelaValorDividas(new ArrayList<ParcelaValorDivida>());
        valorDivida.setDivida(divida);
        valorDivida.setEmissao(new Date());
        valorDivida.setExercicio(exercicio);
        valorDivida.setValor(valor);
        return valorDivida;
    }

    protected void lancaOpcaoPagamento(ValorDivida valorDivida, List<OpcaoPagamentoDivida> opcoesPagamento) throws Exception {
        for (OpcaoPagamentoDivida opcaoPagamentoDivida : opcoesPagamento) {
            geraParcelas(opcaoPagamentoDivida, valorDivida, opcaoPagamentoDivida.getOpcaoPagamento().getNumeroParcelas());
        }
    }

    protected void geraParcelas(OpcaoPagamentoDivida opcaoPagamentoDivida, ValorDivida valorDivida, Integer numeroParcelas) throws Exception {
        OpcaoPagamento op = recarregarOpcaoPagamento(opcaoPagamentoDivida.getOpcaoPagamento());
        Parcela p = op.getParcelas().get(0);
        parcelasOpcaoPagamento.put(op, new ArrayList<ParcelaValorDivida>());
        BigDecimal percentualParcela = CEM.divide(new BigDecimal(numeroParcelas), NUMERO_CASAS_DECIMAIS, MODO_ARREDONDAMENTO);
        BigDecimal valorTotal = valorDivida.getValor().setScale(NUMERO_CASAS_DECIMAIS, MODO_ARREDONDAMENTO);
        BigDecimal valorLancadoBruto = BigDecimal.ZERO;
        Calendar vencimento = Calendar.getInstance();
        Calculo calculo = valorDivida.getCalculo();
        vencimento.setTime(primeiroVencimento(valorDivida.getCalculo(), p));
        BigDecimal valorParcelaBruto = valorTotal.divide(BigDecimal.valueOf(numeroParcelas), NUMERO_CASAS_DECIMAIS, MODO_ARREDONDAMENTO);
        for (int i = 0; i < numeroParcelas; i++) {
            ParcelaValorDivida parcelaValorDivida = new ParcelaValorDivida();
            parcelaValorDivida.setDividaAtiva(definirComoDividaAtiva(calculo));
            parcelaValorDivida.setDividaAtivaAjuizada(definirComoDividaAtivaAjuizada(calculo));
            parcelaValorDivida.setSequenciaParcela((i + 1) + "");
            parcelaValorDivida.setItensParcelaValorDivida(new ArrayList<ItemParcelaValorDivida>());
            parcelaValorDivida.setOpcaoPagamento(op);
            parcelaValorDivida.setValorDivida(valorDivida);
            parcelaValorDivida.setValor(valorParcelaBruto);
            defineVencimentosParcelas(calculo, op, i, parcelaValorDivida, valorDivida, vencimento, numeroParcelas);
            parcelaValorDivida.setPercentualValorTotal(percentualParcela);
            parcelasOpcaoPagamento.get(op).add(parcelaValorDivida);
            valorDivida.getParcelaValorDividas().add(parcelaValorDivida);
            valorLancadoBruto = valorLancadoBruto.add(parcelaValorDivida.getValor());
        }
        if (valorLancadoBruto.compareTo(valorTotal) != 0) {
            BigDecimal valor = valorDivida.getParcelaValorDividas().get(0).getValor();
            valor = valor.add(valorTotal.subtract(valorLancadoBruto));
            valorDivida.getParcelaValorDividas().get(0).setValor(valor);
        }
        lancaSituacoes(valorDivida);
        lancaItensParcela(valorDivida, opcaoPagamentoDivida);
    }

    private void lancaSituacoes(ValorDivida valorDivida) {
        for (ParcelaValorDivida pvd : valorDivida.getParcelaValorDividas()) {
            lancaSituacao(pvd);
        }
    }

    protected void lancaSituacao(ParcelaValorDivida parcelaValorDivida) {
        parcelaValorDivida.getSituacoes().add(new SituacaoParcelaValorDivida(SituacaoParcela.EM_ABERTO, parcelaValorDivida, parcelaValorDivida.getValor()));
    }

    protected OpcaoPagamento recarregarOpcaoPagamento(OpcaoPagamento opcao) {
        return em.find(OpcaoPagamento.class, opcao.getId());
    }

    protected List<Parcela> recuperarParcelasDaOpcaoDePagamento(OpcaoPagamento op) {
        return em.createQuery("from Parcela where opcaoPagamento = :opcao", Parcela.class).setParameter("opcao", op).getResultList();
    }

    public void lancaItensParcela(ValorDivida valorDivida, OpcaoPagamentoDivida opcaoPagamentoDivida) {
        for (ParcelaValorDivida parcelaValorDivida : valorDivida.getParcelaValorDividas()) {
            if (parcelaValorDivida.getOpcaoPagamento().equals(opcaoPagamentoDivida.getOpcaoPagamento())) {
                BigDecimal valorTotalBruto = BigDecimal.ZERO;
                for (ItemValorDivida item : valorDivida.getItemValorDividas()) {
                    if (!item.getIsento()) {
                        ItemParcelaValorDivida itemParcela = new ItemParcelaValorDivida();
                        itemParcela.setItemValorDivida(item);
                        itemParcela.setParcelaValorDivida(parcelaValorDivida);
                        BigDecimal percentualParcela = parcelaValorDivida.getPercentualValorTotal().divide(CEM, 8, MODO_ARREDONDAMENTO);
                        BigDecimal valorBruto = item.getValor().multiply(percentualParcela).setScale(NUMERO_CASAS_DECIMAIS, MODO_ARREDONDAMENTO);
                        if (valorBruto.compareTo(BigDecimal.ZERO) > 0) {
                            itemParcela.setValor(valorBruto);
                        } else {
                            itemParcela.setValor(BigDecimal.ZERO);
                        }
                        lancaDescontos(itemParcela, valorDivida.getCalculo().getTipoCalculo().getOrigem());
                        parcelaValorDivida.getItensParcelaValorDivida().add(itemParcela);
                        valorTotalBruto = valorTotalBruto.add(itemParcela.getValor());
                    }
                }
                processaDiferencaParcela(parcelaValorDivida.getValor(), valorTotalBruto, parcelaValorDivida);
            }
        }
    }

    protected void lancaDescontos(ItemParcelaValorDivida item, DescontoItemParcela.Origem origem) {
        if (consideraDesconto) {
            for (DescontoOpcaoPagamento desconto : getDescontosDaOpcaoPagamento(item.getParcelaValorDivida().getOpcaoPagamento())) {
                if (desconto.getTributo().equals(item.getItemValorDivida().getTributo())) {
                    if (isAdimplente(item.getParcelaValorDivida().getOpcaoPagamento())) {
                        adicionaDescontoAoItem(item, desconto.getPercentualDescontoAdimplente(), origem);
                    } else {
                        adicionaDescontoAoItem(item, desconto.getPercentualDescontoInadimplente(), origem);
                    }

                }
            }
        }
    }

    public List<DescontoOpcaoPagamento> getDescontosDaOpcaoPagamento(OpcaoPagamento op) {
        return em.createQuery("select o from DescontoOpcaoPagamento o where o.opcaoPagamento = :op").setParameter("op", op).getResultList();
    }

    public boolean isAdimplente(OpcaoPagamento opcaoPagamento) {
        return true;
    }

    public void adicionaDescontoAoItem(ItemParcelaValorDivida item, BigDecimal porcentagemDesconto, DescontoItemParcela.Origem origem) {
        adicionaDescontoAoItem(item, porcentagemDesconto, DescontoItemParcela.Tipo.PERCENTUAL, origem);
    }

    public void adicionaDescontoAoItem(ItemParcelaValorDivida item, BigDecimal porcentagemDesconto, DescontoItemParcela.Tipo tipo, DescontoItemParcela.Origem origem) {
        DescontoItemParcela desconto = new DescontoItemParcela();
        desconto.setAtoLegal(null);
        desconto.setMotivo(null);
        desconto.setItemParcelaValorDivida(item);
        desconto.setInicio(new Date());
        desconto.setFim(item.getParcelaValorDivida().getVencimento());
        desconto.setDesconto(porcentagemDesconto);
        desconto.setTipo(tipo);
        desconto.setOrigem(origem);
        item.getDescontos().add(desconto);
    }

    public void processaDiferencaParcela(BigDecimal valorParcelaBruto, BigDecimal valorLancadoBruto, ParcelaValorDivida parcela) {
        int indiceItem = 0;
        ItemParcelaValorDivida itemParcela = parcela.getItensParcelaValorDivida().get(indiceItem);
        BigDecimal diferencaBruta = valorParcelaBruto.subtract(valorLancadoBruto);
        if (diferencaBruta.compareTo(BigDecimal.ZERO) != 0
            && parcela.getItensParcelaValorDivida().get(indiceItem).getValor().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal valor = itemParcela.getValor();
            parcela.getItensParcelaValorDivida().get(indiceItem).setValor(valor.add(diferencaBruta));
        }
    }

    public void processaDiferencaValorDivida(BigDecimal valorLancadoBruto, BigDecimal valorTotalBruto, OpcaoPagamento opcaoPagamento) {
        BigDecimal diferencaBruta = valorLancadoBruto.subtract(valorTotalBruto);
        if (diferencaBruta.doubleValue() > 0) {
            List<ParcelaValorDivida> parcelas = parcelasOpcaoPagamento.get(opcaoPagamento);
            ParcelaValorDivida ultimaParcela = parcelas.get(parcelas.size() - 1);
            ultimaParcela.setValor(ultimaParcela.getValor().subtract(diferencaBruta));
        } else if (diferencaBruta.doubleValue() < 0) {
            ParcelaValorDivida primeiraParcela = parcelasOpcaoPagamento.get(opcaoPagamento).get(0);
            primeiraParcela.setValor(primeiraParcela.getValor().add(diferencaBruta.abs()));
        }
    }

    public void salvarParcela(ParcelaValorDivida parcela) {
        em.merge(parcela);
    }

    public void salvaValorDivida(ValorDivida valordivida) throws Exception {
        em.persist(valordivida);
        em.flush();
//        integraValorDivida(valordivida);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void salvarValorDividaEmNovaTransacao(ValorDivida valordivida) throws Exception {
        em.persist(valordivida);
        em.flush();
    }

    public ValorDivida salvaValorDividaRetornando(ValorDivida valordivida) throws Exception {
        valordivida = em.merge(valordivida);
        em.flush();
        return valordivida;
    }

    public Date ajustaVencimento(Date vencimento, int tipoCampo, int i) {
        Calendar retorno = Calendar.getInstance();
        retorno.setTime(vencimento);
        retorno.add(tipoCampo, i);
        while (DataUtil.ehDiaNaoUtil(retorno.getTime(), feriadoFacade)) {
            retorno.add(Calendar.DAY_OF_MONTH, 1);
        }
        return retorno.getTime();
    }

    public ValorDivida recupera(Long id) {
        ValorDivida vd = em.find(ValorDivida.class, id);
        Hibernate.initialize(vd.getCalculo());// para carregar o calculo (LAZY)
        return vd;
    }

    public ValorDivida recuperar(Long id) {
        ValorDivida vd = em.find(ValorDivida.class, id);
        vd.getItemValorDividas().size();
        vd.getIntegracoes().size();
        vd.getOcorrenciaValorDivida().size();
        vd.getParcelaValorDividas().size();
        for (ParcelaValorDivida parcelaValorDivida : vd.getParcelaValorDividas()) {
            parcelaValorDivida.getSituacoes().size();
        }
        return vd;
    }

    protected List<Parcela> calculaPercentualParcelas(OpcaoPagamento opcaoPagamento, ValorDivida valorDivida) {
        opcaoPagamento = em.find(OpcaoPagamento.class, opcaoPagamento.getId());
        List<Parcela> retorno = new ArrayList<>();
        BigDecimal percentualMinimo = calculaPercentualMinimo(valorDivida, opcaoPagamento);
        BigDecimal percentualJaLancado = BigDecimal.ZERO;
        int i = 0;
        List<Parcela> parcelas = getParcelasaDaOpcaoDePagamento(opcaoPagamento);
        while ((percentualJaLancado.compareTo(CEM) < 0) && (i < parcelas.size())) {
            Parcela parcela = parcelas.get(i++);
            Parcela novaParcela;
            if (parcela instanceof ParcelaFixa) {
                novaParcela = new ParcelaFixa();
            } else {
                novaParcela = new ParcelaFixaPeriodica();
                ((ParcelaFixaPeriodica) novaParcela).setDiaVencimento(((ParcelaFixaPeriodica) parcela).getDiaVencimento());
            }
            novaParcela.setMensagem(parcela.getMensagem());
            novaParcela.setSequenciaParcela(parcela.getSequenciaParcela());
            novaParcela.setVencimento(parcela.getVencimento());
            novaParcela.setPercentualValorTotal(parcela.getPercentualValorTotal());
            if (novaParcela.getPercentualValorTotal().compareTo(percentualMinimo) < 0) {
                novaParcela.setPercentualValorTotal(percentualMinimo);
            }
            BigDecimal totalTemporario = percentualJaLancado.add(novaParcela.getPercentualValorTotal());
            if (totalTemporario.compareTo(CEM) > 0) {
                novaParcela.setPercentualValorTotal(novaParcela.getPercentualValorTotal().subtract(totalTemporario.subtract(CEM)));
            }
            percentualJaLancado = percentualJaLancado.add(novaParcela.getPercentualValorTotal());
            retorno.add(novaParcela);
        }
        BigDecimal percentualUltimaParcela;
        if (retorno.isEmpty()) {
            percentualUltimaParcela = BigDecimal.ZERO;
        } else {
            percentualUltimaParcela = retorno.get(retorno.size() - 1).getPercentualValorTotal();
        }
        if (percentualUltimaParcela.compareTo(percentualMinimo) < 0) {
            retorno.remove(retorno.size() - 1);
            if (!retorno.isEmpty()) {
                BigDecimal percentualADistribuir = percentualUltimaParcela.divide(new BigDecimal(retorno.size()), 4, MODO_ARREDONDAMENTO);
                BigDecimal novoValorJaLancado = BigDecimal.ZERO;
                for (Parcela parcela : retorno) {
                    parcela.setPercentualValorTotal(parcela.getPercentualValorTotal().add(percentualADistribuir));
                    novoValorJaLancado = novoValorJaLancado.add(parcela.getPercentualValorTotal());
                }
                BigDecimal diferenca = CEM.subtract(novoValorJaLancado);
                if (diferenca.compareTo(CEM) != 0) {
                    retorno.get(0).setPercentualValorTotal(retorno.get(0).getPercentualValorTotal().add(diferenca));
                }
            }
        }
        return retorno;
    }

    public List<Parcela> getParcelasaDaOpcaoDePagamento(OpcaoPagamento op) {
        return em.createQuery("select p from Parcela p where p.opcaoPagamento = :op").setParameter("op", op).getResultList();
    }

    private BigDecimal calculaPercentualMinimo(ValorDivida valorDivida, OpcaoPagamento opcaoPagamento) {
        BigDecimal retorno = BigDecimal.ZERO;
        try {
            BigDecimal valorMinimo = opcaoPagamento.getValorMinimoParcela();
            if (valorMinimo.compareTo(valorDivida.getValor()) > 0) {
                valorMinimo = valorDivida.getValor();
            }
            retorno = (valorMinimo.multiply(CEM)).divide(valorDivida.getValor(), NUMERO_CASAS_DECIMAIS, MODO_ARREDONDAMENTO);
        } catch (Exception e) {
            //NADA
        }
        return retorno;
    }

    public ValorDivida buscarValorDividaPorCalculo(Calculo calculo) {
        String sql = " select * from valordivida vd " +
            " where vd.calculo_id = :id_calculo ";
        Query q = em.createNativeQuery(sql, ValorDivida.class);
        q.setParameter("id_calculo", calculo.getId());
        if (!q.getResultList().isEmpty()) {
            return (ValorDivida) q.getSingleResult();
        }
        throw new ExcecaoNegocioGenerica("ValorDivida não encontrado para o calculo ID {" + calculo.getId() + "}");
    }

    public void validarValorDivida(ValorDivida valorDivida) {
        validarEntidadeDividaTributos(valorDivida);
    }


    public void validarEntidadeDividaTributos(ValorDivida valorDivida) {
        Entidade entidadeDivida = valorDivida.getDivida().getEntidade();
        if (valorDivida.getItemValorDividas().stream().anyMatch(iv -> !entidadeDivida.equals(iv.getTributo().getEntidade()))) {
            throw new ValidacaoException("Existem tributos de entidade diferente da entidade da dívida " + entidadeDivida.getNome());
        }
    }
}
