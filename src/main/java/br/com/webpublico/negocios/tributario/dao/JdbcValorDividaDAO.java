package br.com.webpublico.negocios.tributario.dao;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.VOUsuarioIP;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.negocios.DAMFacade;
import br.com.webpublico.negocios.tributario.rowmapper.DescontoOpcaoPagamentoRowMapper;
import br.com.webpublico.negocios.tributario.rowmapper.OpcaoPagamentoRowMapper;
import br.com.webpublico.negocios.tributario.setter.*;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository("valorDividaDAO")
public abstract class JdbcValorDividaDAO extends AbstractJdbc {

    protected static final int NUMERO_CASAS_DECIMAIS = 2;
    protected static final RoundingMode MODO_ARREDONDAMENTO = RoundingMode.HALF_UP;
    protected static final BigDecimal CEM = BigDecimal.valueOf(100);
    protected List<OpcaoPagamento> opcoesPagamento;
    protected List<DAM> dans;
    protected List<ItemDAM> itensDAM;
    protected List<HistoricoImpressaoDAM> impressoesDAM;
    protected List<TributoDAM> tributosDAM;
    protected Map<OpcaoPagamento, List<ParcelaValorDivida>> parcelasOpcaoPagamento;
    private Boolean considerarDesconto;

    @Autowired
    private SingletonGeradorId geradorDeIds;
    private DAMFacade damFacade;

    public JdbcValorDividaDAO() {
        this.damFacade = (DAMFacade) Util.getFacadeViaLookup("java:module/DAMFacade");
        inicializarAtributos();
    }

    protected abstract Object criarItemValorDivida(ValorDivida valorDivida);

    protected abstract void definirVencimentosParcelas(Calculo calculo, OpcaoPagamento op, int i, ParcelaValorDivida parcelaValorDivida,
                                                       ValorDivida valorDivida, Calendar vencimento, Integer numeroParcelas);

    /**
     * Deve sempre ser chamado ao final da execução jdbc
     * para resetar os atributos.
     */
    protected void inicializarAtributos() {
        this.parcelasOpcaoPagamento = Maps.newHashMap();
        this.opcoesPagamento = Lists.newArrayList();
        this.dans = Lists.newArrayList();
        this.itensDAM = Lists.newArrayList();
        this.impressoesDAM = Lists.newArrayList();
        this.tributosDAM = Lists.newArrayList();
        definirDesconto(Boolean.FALSE);
    }

    protected void validarOpcoesPagamento(Divida divida) {
        validarOpcoesPagamento(divida, new Date());
    }

    protected void validarOpcoesPagamento(Divida divida, Date dataParametro) {
        String sql = " select op.id, op.numeroparcelas from opcaopagamento op " +
            " inner join opcaopagamentodivida opd on op.id = opd.opcaopagamento_id " +
            " where opd.divida_id = :idDivida " +
            " and to_date(:dataAtual, 'dd/MM/yyyy') between trunc(opd.iniciovigencia) " +
            " and trunc(coalesce(opd.finalvigencia, to_date(:dataAtual, 'dd/MM/yyyy'))) ";

        NamedParameterJdbcTemplate namedParameter = new NamedParameterJdbcTemplate(getJdbcTemplate());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("idDivida", divida.getId());
        parameters.addValue("dataAtual", DataUtil.getDataFormatada(dataParametro));

        opcoesPagamento = namedParameter.query(sql, parameters, new OpcaoPagamentoRowMapper());

        if (opcoesPagamento.isEmpty()) {
            throw new IllegalArgumentException("Nenhuma opção de pagamento válida para a dívida selecionada: " + divida.getDescricao());
        }
    }

    protected List<DescontoOpcaoPagamento> buscarDescontosDaOpcaoPagamento(OpcaoPagamento opcaoPagamento) {
        String sql = " select dop.id, dop.percentualdescontoadimplente, dop.percentualdescontoinadimplente from descontoopcaopagamento dop " +
            " where dop.opcaopagamento_id = ? ";

        return getJdbcTemplate().query(sql, new Object[]{opcaoPagamento.getId()}, new DescontoOpcaoPagamentoRowMapper());
    }

    protected void inserirValorDivida(ValorDivida valorDivida, VOUsuarioIP usuarioIP) {
        inserirValorDivida(valorDivida, true, true, usuarioIP);
    }

    protected void inserirRevisaoAuditoria(Long idRevisao, VOUsuarioIP usuarioIP) {
        String insert = " insert into revisaoauditoria (id, datahora, ip, usuario) " +
            " values (?, ?, ?, ?) ";
        getJdbcTemplate().update(insert, idRevisao, new Date(), usuarioIP.getIp(), usuarioIP.getUsuario());
    }

    protected void inserirAuditoriaValorDivida(ValorDivida valorDivida, Long idRevisao) {
        String sql = " insert into valordivida_aud (id, emissao, valor, divida_id, exercicio_id, calculo_id, rev, revtype) " +
            " values (?, ?, ?, ?, ?, ?, ?, ?)";

        getJdbcTemplate().batchUpdate(sql, new ValorDividaSetter(valorDivida, valorDivida.getId(), idRevisao));
    }

    protected void inserirValorDivida(ValorDivida valorDivida, boolean gerarReferencia, boolean gerarAuditoria, VOUsuarioIP usuarioIP) {
        String sql = " insert into valordivida (id, emissao, valor, divida_id, exercicio_id, calculo_id) " +
            " values (?, ?, ?, ?, ?, ?) ";

        getJdbcTemplate().batchUpdate(sql, new ValorDividaSetter(valorDivida, geradorDeIds.getProximoId()));

        inserirItemValorDivida(valorDivida.getItemValorDividas());
        inserirParcelaValorDivida(valorDivida.getParcelaValorDividas(), gerarReferencia);

        if (gerarAuditoria) {
            inserirAuditorias(valorDivida, gerarReferencia, usuarioIP);
        }
    }

    protected void inserirAuditorias(ValorDivida valorDivida, boolean gerarReferencia, VOUsuarioIP usuarioIP) {
        Long idRevisao = geradorDeIds.getProximoId();
        inserirRevisaoAuditoria(idRevisao, usuarioIP);
        inserirAuditoriaValorDivida(valorDivida, idRevisao);
        inserirAuditoriaItemValorDivida(valorDivida.getItemValorDividas(), idRevisao);
        inserirAuditoriaParcelaValorDivida(valorDivida.getParcelaValorDividas(), gerarReferencia, idRevisao);
    }

    protected void inserirAuditoriaItemValorDivida(List<ItemValorDivida> itens, Long idRevisao) {
        String sql = " insert into itemvalordivida_aud (id, valor, tributo_id, valordivida_id, isento, imune, rev, revtype) " +
            " values (?, ?, ?, ?, ?, ?, ?, ?) ";

        getJdbcTemplate().batchUpdate(sql, new ItemValorDividaSetter(itens, geradorDeIds.getIds(itens.size()), idRevisao));
    }

    protected void inserirItemValorDivida(List<ItemValorDivida> itens) {
        String sql = " insert into itemvalordivida (id, valor, tributo_id, valordivida_id, isento, imune) " +
            " values (?, ?, ?, ?, ?, ?) ";

        getJdbcTemplate().batchUpdate(sql, new ItemValorDividaSetter(itens, geradorDeIds.getIds(itens.size())));
    }

    protected void inserirAuditoriaParcelaValorDivida(List<ParcelaValorDivida> parcelas, boolean gerarReferencia, Long idRevisao) {
        String sql = " insert into parcelavalordivida_aud (id, opcaopagamento_id, valordivida_id, vencimento, percentualvalortotal," +
            " dataregistro, sequenciaparcela, dividaativa, dividaativaajuizada, valor, dataprescricao, rev, revtype) " +
            " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";

        getJdbcTemplate().batchUpdate(sql, new ParcelaValorDividaSetter(parcelas, geradorDeIds.getIds(parcelas.size()), idRevisao));

        for (ParcelaValorDivida parcela : parcelas) {
            inserirAuditoriaItemParcelaValorDivida(parcela.getItensParcelaValorDivida(), idRevisao);
        }
    }

    protected void inserirParcelaValorDivida(List<ParcelaValorDivida> parcelas, boolean gerarReferencia) {
        String sql = " insert into parcelavalordivida (id, opcaopagamento_id, valordivida_id, vencimento, percentualvalortotal," +
            " dataregistro, sequenciaparcela, dividaativa, dividaativaajuizada, valor, dataprescricao ) " +
            " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";

        getJdbcTemplate().batchUpdate(sql, new ParcelaValorDividaSetter(parcelas, geradorDeIds.getIds(parcelas.size())));

        for (ParcelaValorDivida parcela : parcelas) {
            inserirItemParcelaValorDivida(parcela.getItensParcelaValorDivida());
        }
        inserirSituacaoParcelaValorDivida(parcelas, gerarReferencia);
    }

    protected void inserirAuditoriaItemParcelaValorDivida(List<ItemParcelaValorDivida> itens, Long idRevisao) {
        String sql = " insert into itemparcelavalordivida_aud (id, parcelavalordivida_id, itemvalordivida_id, valor, rev, revtype) " +
            " values (?, ?, ?, ?, ?, ?) ";

        getJdbcTemplate().batchUpdate(sql, new ItemParcelaValorDividaSetter(itens, geradorDeIds.getIds(itens.size()), idRevisao));

        for (ItemParcelaValorDivida item : itens) {
            inserirAuditoriaDescontoItemParcela(item.getDescontos(), idRevisao);
        }
    }

    protected void inserirItemParcelaValorDivida(List<ItemParcelaValorDivida> itens) {
        String sql = " insert into itemparcelavalordivida (id, parcelavalordivida_id, itemvalordivida_id, valor) " +
            " values (?, ?, ?, ?) ";

        getJdbcTemplate().batchUpdate(sql, new ItemParcelaValorDividaSetter(itens, geradorDeIds.getIds(itens.size())));

        for (ItemParcelaValorDivida item : itens) {
            inserirDescontoItemParcela(item.getDescontos());
        }
    }

    protected void inserirDescontoItemParcela(List<DescontoItemParcela> descontos) {
        String sql = " insert into descontoitemparcela (id, itemparcelavalordivida_id, inicio, fim, tipo, desconto, origem) " +
            " values (?, ?, ?, ?, ?, ?, ?) ";

        getJdbcTemplate().batchUpdate(sql, new DescontoItemParcelaSetter(descontos, geradorDeIds.getIds(descontos.size())));
    }

    protected void inserirAuditoriaDescontoItemParcela(List<DescontoItemParcela> descontos, Long idRevisao) {
        String sql = " insert into descontoitemparcela_aud (id, itemparcelavalordivida_id, inicio, fim, tipo, desconto, origem, rev, revtype) " +
            " values (?, ?, ?, ?, ?, ?, ?, ?, ?) ";

        getJdbcTemplate().batchUpdate(sql, new DescontoItemParcelaSetter(descontos, geradorDeIds.getIds(descontos.size()), idRevisao));
    }

    protected void inserirSituacaoParcelaValorDivida(List<ParcelaValorDivida> parcelas, boolean gerarReferencia) {
        String sql = " insert into situacaoparcelavalordivida (id, datalancamento, situacaoparcela, parcela_id, saldo, referencia) " +
            " values (?, ?, ?, ?, ?, ?) ";

        getJdbcTemplate().batchUpdate(sql, new SituacaoParcelaValorDividaSetter(parcelas, SituacaoParcela.EM_ABERTO, geradorDeIds.getIds(parcelas.size()), gerarReferencia));
    }

    protected void gerarDebito(ProcessoCalculo processoCalculo, VOUsuarioIP usuarioIP) {
        validarOpcoesPagamento(processoCalculo.getDivida());
        gerarValorDivida(processoCalculo, usuarioIP);
    }

    protected void gerarValorDivida(ProcessoCalculo processoCalculo, VOUsuarioIP usuarioIP) {
        for (Calculo calculo : processoCalculo.getCalculos()) {
            gerarValorDivida(calculo, usuarioIP);
        }
    }

    protected void gerarValorDivida(Calculo calculo, VOUsuarioIP usuarioIP) {
        if (!calculo.getIsento()) {
            ValorDivida valorDivida = criarValorDivida(calculo);
            criarItemValorDivida(valorDivida);

            if (!valorDivida.getItemValorDividas().isEmpty()) {
                gerarParcelasPorOpcaoPagamento(valorDivida);
            }
            inserirValorDivida(valorDivida, usuarioIP);
        }
    }

    protected void gerarParcelasPorOpcaoPagamento(ValorDivida valorDivida) {
        for (OpcaoPagamento opcaoPagamento : this.opcoesPagamento) {
            gerarParcelas(opcaoPagamento, opcaoPagamento.getNumeroParcelas(), valorDivida);
        }
    }

    protected void gerarParcelas(OpcaoPagamento opcaoPagamento, Integer numeroParcelas, ValorDivida valorDivida) {
        parcelasOpcaoPagamento.put(opcaoPagamento, Lists.<ParcelaValorDivida>newArrayList());

        BigDecimal percentualParcela = CEM.divide(new BigDecimal(numeroParcelas), NUMERO_CASAS_DECIMAIS, MODO_ARREDONDAMENTO);
        BigDecimal valorTotal = valorDivida.getValor().setScale(NUMERO_CASAS_DECIMAIS, MODO_ARREDONDAMENTO);
        BigDecimal valorLancadoBruto = BigDecimal.ZERO;
        Calendar vencimento = Calendar.getInstance();
        Calculo calculo = valorDivida.getCalculo();
        vencimento.setTime(definirPrimeiroVencimento(valorDivida.getCalculo()));
        BigDecimal valorParcelaBruto = valorTotal.divide(BigDecimal.valueOf(numeroParcelas), NUMERO_CASAS_DECIMAIS, MODO_ARREDONDAMENTO);

        for (int i = 0; i < numeroParcelas; i++) {
            ParcelaValorDivida parcelaValorDivida = new ParcelaValorDivida();
            parcelaValorDivida.setDividaAtiva(definirComoDividaAtiva(calculo));
            parcelaValorDivida.setDividaAtivaAjuizada(definirComoDividaAtivaAjuizada(calculo));
            parcelaValorDivida.setSequenciaParcela((i + 1) + "");
            parcelaValorDivida.setOpcaoPagamento(opcaoPagamento);
            parcelaValorDivida.setValorDivida(valorDivida);
            parcelaValorDivida.setValor(valorParcelaBruto);
            parcelaValorDivida.setPercentualValorTotal(percentualParcela);
            parcelaValorDivida.setItensParcelaValorDivida(Lists.<ItemParcelaValorDivida>newArrayList());
            definirVencimentosParcelas(calculo, opcaoPagamento, i, parcelaValorDivida, valorDivida, vencimento, numeroParcelas);

            valorDivida.getParcelaValorDividas().add(parcelaValorDivida);
            valorLancadoBruto = valorLancadoBruto.add(parcelaValorDivida.getValor());
            parcelasOpcaoPagamento.get(opcaoPagamento).add(parcelaValorDivida);
        }
        if (valorLancadoBruto.compareTo(valorTotal) != 0) {
            BigDecimal valor = valorDivida.getParcelaValorDividas().get(0).getValor();
            valor = valor.add(valorTotal.subtract(valorLancadoBruto));
            valorDivida.getParcelaValorDividas().get(0).setValor(valor);
        }
        gerarSituacoesParcela(valorDivida);

    }

    protected void gerarSituacoesParcela(ValorDivida valorDivida) {
        for (ParcelaValorDivida parcelaValorDivida : valorDivida.getParcelaValorDividas()) {
            gerarSituacaoParcela(parcelaValorDivida);
        }
    }

    protected void gerarSituacaoParcela(ParcelaValorDivida parcelaValorDivida) {
        parcelaValorDivida.getSituacoes().add(new SituacaoParcelaValorDivida(SituacaoParcela.EM_ABERTO,
            parcelaValorDivida, parcelaValorDivida.getValor()));
    }

    protected void gerarDescontos(ItemParcelaValorDivida itemParcela, DescontoItemParcela.Origem origem) {
        if (considerarDesconto) {
            for (DescontoOpcaoPagamento desconto : buscarDescontosDaOpcaoPagamento(itemParcela.getParcelaValorDivida().getOpcaoPagamento())) {
                if (desconto.getTributo().equals(itemParcela.getItemValorDivida().getTributo())) {
                    if (isAdimplente(itemParcela.getParcelaValorDivida().getOpcaoPagamento())) {
                        gerarDesconto(itemParcela, desconto.getPercentualDescontoAdimplente(), origem);
                    } else {
                        gerarDesconto(itemParcela, desconto.getPercentualDescontoInadimplente(), origem);
                    }
                }
            }
        }
    }

    protected void gerarDesconto(ItemParcelaValorDivida item, BigDecimal porcentagemDesconto, DescontoItemParcela.Origem origem) {
        gerarDesconto(item, porcentagemDesconto, DescontoItemParcela.Tipo.PERCENTUAL, origem);
    }

    protected void gerarDesconto(ItemParcelaValorDivida item, BigDecimal porcentagemDesconto, DescontoItemParcela.Tipo tipo, DescontoItemParcela.Origem origem) {
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

    protected void corrigirDiferencaValoresParcela(BigDecimal valorParcelaBruto, BigDecimal valorLancadoBruto, ParcelaValorDivida parcela) {
        int indiceItem = 0;
        ItemParcelaValorDivida itemParcela = parcela.getItensParcelaValorDivida().get(indiceItem);
        BigDecimal diferencaBruta = valorParcelaBruto.subtract(valorLancadoBruto);
        if (diferencaBruta.compareTo(BigDecimal.ZERO) != 0
            && parcela.getItensParcelaValorDivida().get(indiceItem).getValor().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal valor = itemParcela.getValor();
            parcela.getItensParcelaValorDivida().get(indiceItem).setValor(valor.add(diferencaBruta));
        }
    }

    protected Date definirPrimeiroVencimento(Calculo calculo) {
        return new Date();
    }

    protected boolean definirComoDividaAtiva(Calculo calculo) {
        return false;
    }

    protected boolean definirComoDividaAtivaAjuizada(Calculo calculo) {
        return false;
    }

    protected boolean isAdimplente(OpcaoPagamento opcaoPagamento) {
        return true;
    }

    protected void definirDesconto(Boolean considerarDesconto) {
        this.considerarDesconto = considerarDesconto;
    }

    protected ValorDivida criarValorDivida(Calculo calculo) {
        return criarValorDivida(calculo, calculo.getProcessoCalculo().getExercicio(), calculo.getProcessoCalculo().getDivida(), calculo.getValorEfetivo());
    }

    protected ValorDivida criarValorDivida(Calculo calculo, Exercicio exercicio, Divida divida, BigDecimal valor) {
        ValorDivida valorDivida = new ValorDivida();
        valorDivida.setEmissao(new Date());
        valorDivida.setCalculo(calculo);
        valorDivida.setDivida(divida);
        valorDivida.setExercicio(exercicio);
        valorDivida.setValor(valor);
        valorDivida.setOcorrenciaValorDivida(Lists.<OcorrenciaValorDivida>newArrayList());
        valorDivida.setItemValorDividas(Lists.<ItemValorDivida>newArrayList());
        valorDivida.setParcelaValorDividas(Lists.<ParcelaValorDivida>newArrayList());
        return valorDivida;
    }

    protected void gerarDAMsEmLote(List<ResultadoParcela> parcelas, Exercicio exercicio, ConfiguracaoDAM configuracaoDAM, UsuarioSistema usuario,
                                   DAM.Tipo tipoDAM, boolean gerarHistorico) {
        if (DAM.Tipo.UNICO.equals(tipoDAM)) {
            for (ResultadoParcela parcela : parcelas) {
                damFacade.gerarDAMUnicoViaApi(usuario, parcela);
            }
        } else {
            damFacade.gerarDAMCompostoViaApi(usuario, parcelas, parcelas.get(0).getVencimento());
        }
    }
}
