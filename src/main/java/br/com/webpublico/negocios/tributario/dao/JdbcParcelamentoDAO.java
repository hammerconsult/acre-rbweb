package br.com.webpublico.negocios.tributario.dao;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteParcelamento;
import br.com.webpublico.entidadesauxiliares.VOUsuarioIP;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.SituacaoParcelamento;
import br.com.webpublico.negocios.tributario.rowmapper.ItemParcelaValorDividaRoWMapper;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.tributario.consultadebitos.dtos.TributoDamDTO;
import br.com.webpublico.util.AssistenteBarraProgresso;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Repository("parcelamentoDAO")
public class JdbcParcelamentoDAO extends JdbcValorDividaDAO {

    @Autowired
    private JdbcParcelaValorDividaDAO parcelaValorDividaDAO;

    public JdbcParcelamentoDAO() {
        super();
        inicializarAtributos();
    }

    @Override
    public void inicializarAtributos() {
        super.inicializarAtributos();
    }

    private List<ItemParcelaValorDivida> buscarItensParcela(Long idParcela) {
        String sql = " select ipvd.id, tr.descricao as tributo_descricao, tr.tipotributo, ipvd.valor, tr.id as tributo_id," +
            "  tr_hn.id as tributo_hon_id, tr_hn.descricao as tributo_hn_descricao, tr_hn.tipotributo as tipotributo_hn, " +
            "  tr_cm.id as tributo_cm_id, tr_cm.descricao as tributo_cm_descricao, tr_cm.tipotributo as tipotributo_cm, " +
            "  tr_juros.id as tributo_juros_id, tr_juros.descricao as tributo_juros_descricao, tr_juros.tipotributo as tipotributo_juros, " +
            "  tr_multa.id as tributo_multa_id, tr_multa.descricao as tributo_multa_descricao, tr_multa.tipotributo as tipotributo_multa " +
            " from itemparcelavalordivida ipvd " +
            " inner join itemvalordivida ivd on ipvd.itemvalordivida_id = ivd.id " +
            " inner join tributo tr on ivd.tributo_id = tr.id " +
            " left join tributo tr_hn on tr.tributohonorarios_id = tr_hn.id " +
            " left join tributo tr_cm on tr.tributocorrecaomonetaria_id = tr_cm.id " +
            " left join tributo tr_juros on tr.tributojuros_id = tr_juros.id " +
            " left join tributo tr_multa on tr.tributomulta_id = tr_multa.id " +
            " where ipvd.parcelavalordivida_id = ? ";

        return getJdbcTemplate().query(sql, new Object[]{idParcela}, new ItemParcelaValorDividaRoWMapper(true));
    }

    public Map<String, Object> buscarInformacoesDaParcela(Long idParcela) {
        String sql = " select coalesce(pvd.valor, 0), spvd.situacaoparcela, spvd.referencia from parcelavalordivida pvd " +
            " inner join situacaoparcelavalordivida spvd on pvd.situacaoatual_id = spvd.id " +
            " where pvd.id = ? ";

        return getJdbcTemplate().queryForMap(sql, idParcela);
    }

    public String buscarNumeroDoParcelamento(Long idParcela, ProcessoParcelamento parcelamento) {

        String sql = " select coalesce(pp.numero || " +
            " case when pp.numeroreparcelamento > 0 then '-' || pp.numeroreparcelamento else '' end || '/' || ex.ano, '') " +
            " from processoparcelamento pp " +
            " inner join itemprocessoparcelamento ipp on pp.id = ipp.processoparcelamento_id" +
            " inner join exercicio ex on pp.exercicio_id = ex.id" +
            " where ipp.parcelavalordivida_id = :idParcela " +
            " and pp.id <> :idCalculo " +
            " and pp.situacaoparcelamento in (:situacoes) " +
            " order by pp.id desc fetch first 1 rows only ";

        NamedParameterJdbcTemplate namedParameter = new NamedParameterJdbcTemplate(getJdbcTemplate());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("idParcela", idParcela);
        parameters.addValue("idCalculo", parcelamento.getId());
        parameters.addValue("situacoes", Lists.newArrayList(SituacaoParcelamento.FINALIZADO.name(), SituacaoParcelamento.REATIVADO.name(), SituacaoParcelamento.PAGO.name()));

        String numeroParcelamento = null;
        try {
            numeroParcelamento = namedParameter.queryForObject(sql, parameters, String.class);
        } catch (EmptyResultDataAccessException ignored) {
        }
        return numeroParcelamento != null ? ("Reparcelamento: " + numeroParcelamento) : "";
    }

    @Override
    public void gerarDAMsEmLote(List<ResultadoParcela> parcelas, Exercicio exercicio, ConfiguracaoDAM configuracaoDAM, UsuarioSistema usuario, DAM.Tipo tipoDAM, boolean gerarHistorico) {
        super.gerarDAMsEmLote(parcelas, exercicio, configuracaoDAM, usuario, tipoDAM, gerarHistorico);
    }

    public ProcessoParcelamento gerarParcelamento(AssistenteBarraProgresso assistenteBarraProgresso, AssistenteParcelamento assistente) throws Exception {
        ProcessoParcelamento parcelamento = (ProcessoParcelamento) assistente.getSelecionado();
        if (parcelamento != null && parcelamento.getId() != null) {
            validarOpcoesPagamento(parcelamento.getParamParcelamento().getDividaParcelamento());
            ValorDivida valorDivida = criarValorDivida(parcelamento, parcelamento.getExercicio(), parcelamento.getParamParcelamento().getDividaParcelamento(), parcelamento.getTotalGeral());
            Map<Tributo.TipoTributo, Set<Tributo>> tributos = criarItemValorDivida(valorDivida);

            if (!valorDivida.getItemValorDividas().isEmpty()) {
                gerarParcelasPorOpcaoPagamento(assistenteBarraProgresso, assistente.getParcelas(), valorDivida, tributos);
            }
            String usuario = assistente.getUsuarioSistema() != null ? assistente.getUsuarioSistema().getLogin() : "";
            inserirValorDivida(valorDivida, new VOUsuarioIP(usuario, assistente.getIpUsuario()));
            cancelarParcelasAntigas(parcelamento);
        }
        return parcelamento;
    }

    @Override
    protected Map<Tributo.TipoTributo, Set<Tributo>> criarItemValorDivida(ValorDivida valorDivida) {
        Map<Tributo.TipoTributo, Set<Tributo>> tributos = Maps.newHashMap();
        Map<Tributo, BigDecimal> novosTributos = Maps.newHashMap();

        preencherMapaPorTipoTributo((ProcessoParcelamento) valorDivida.getCalculo(), tributos);
        preencherMapaPorTipoTributosAcrescimos(tributos);
        preencherMapaDeTributosPorValor((ProcessoParcelamento) valorDivida.getCalculo(), tributos, novosTributos);
        adicionarItemValorDividaPorTributo(valorDivida, novosTributos);

        return tributos;
    }

    @Override
    protected void definirVencimentosParcelas(Calculo calculo, OpcaoPagamento op, int i, ParcelaValorDivida parcelaValorDivida,
                                              ValorDivida valorDivida, Calendar vencimento, Integer numeroParcelas) {
    }

    private void gerarParcelasPorOpcaoPagamento(AssistenteBarraProgresso assistenteBarraProgresso, List<ResultadoParcela> parcelasSeremGeradas, ValorDivida valorDivida, Map<Tributo.TipoTributo, Set<Tributo>> tributos) {
        for (OpcaoPagamento opcaoPagamento : this.opcoesPagamento) {
            gerarParcelas(assistenteBarraProgresso, parcelasSeremGeradas, valorDivida, opcaoPagamento, tributos);
        }
    }

    private void gerarParcelas(AssistenteBarraProgresso assistenteBarraProgresso, List<ResultadoParcela> parcelasSeremGeradas, ValorDivida valorDivida, OpcaoPagamento opcaoPagamento, Map<Tributo.TipoTributo, Set<Tributo>> tributos) {
        int sequenciaParcela = 1;
        ProcessoParcelamento parcelamento = (ProcessoParcelamento) valorDivida.getCalculo();

        for (ResultadoParcela resultadoParcela : parcelasSeremGeradas) {
            ParcelaValorDivida parcelaValorDivida = new ParcelaValorDivida();

            parcelaValorDivida.setDividaAtiva(parcelamento.getParamParcelamento().getSituacaoDebito().getDividaAtiva());
            parcelaValorDivida.setDividaAtivaAjuizada(parcelamento.getParamParcelamento().getSituacaoDebito().getDividaAjuizada());

            parcelaValorDivida.setItensParcelaValorDivida(Lists.<ItemParcelaValorDivida>newArrayList());
            parcelaValorDivida.setOpcaoPagamento(opcaoPagamento);
            parcelaValorDivida.setValorDivida(valorDivida);
            parcelaValorDivida.setValor(resultadoParcela.getValorTotalSemDesconto());
            parcelaValorDivida.getSituacoes().add(new SituacaoParcelaValorDivida(SituacaoParcela.EM_ABERTO, parcelaValorDivida, parcelaValorDivida.getValor()));
            parcelaValorDivida.setVencimento(resultadoParcela.getVencimento());
            parcelaValorDivida.setSequenciaParcela(sequenciaParcela + "");
            parcelaValorDivida.setResultadoParcela(resultadoParcela);

            valorDivida.getParcelaValorDividas().add(parcelaValorDivida);
            BigDecimal percentual = (parcelaValorDivida.getValor().multiply(CEM)).divide(valorDivida.getValor(), 6, RoundingMode.HALF_UP);
            parcelaValorDivida.setPercentualValorTotal(percentual);

            gerarItensParcelaValorDivida(parcelaValorDivida, resultadoParcela, tributos);

            sequenciaParcela++;
            assistenteBarraProgresso.conta();
        }
    }

    private void gerarItensParcelaValorDivida(ParcelaValorDivida parcelaValorDivida, ResultadoParcela resultadoParcela, Map<Tributo.TipoTributo, Set<Tributo>> tributos) {
        ProcessoParcelamento parcelamento = (ProcessoParcelamento) parcelaValorDivida.getValorDivida().getCalculo();
        Map<Tributo, BigDecimal> novosTributos = Maps.newHashMap();

        for (Map.Entry<Tributo.TipoTributo, Set<Tributo>> tributoEntry : tributos.entrySet()) {
            BigDecimal valor = buscarValorTributo(tributoEntry.getKey(), resultadoParcela);
            BigDecimal valorAdicionado = BigDecimal.ZERO;
            valor = valor.divide(BigDecimal.valueOf(tributoEntry.getValue().size()), 2, RoundingMode.HALF_UP);

            for (Tributo tributo : tributoEntry.getValue()) {
                if (novosTributos.containsKey(tributo)) {
                    valor = valor.add(novosTributos.get(tributo));
                }
                novosTributos.put(tributo, valor);
                valorAdicionado = valorAdicionado.add(valor);
            }

            if (valorAdicionado.compareTo(buscarValorTributo(tributoEntry.getKey(), resultadoParcela)) != 0) {
                BigDecimal diferenca = buscarValorTributo(tributoEntry.getKey(), resultadoParcela).subtract(valorAdicionado);
                BigDecimal valorPrimeiroTributo = novosTributos.get(Lists.newArrayList(tributoEntry.getValue()).get(0));
                valorPrimeiroTributo = valorPrimeiroTributo.add(diferenca);
                novosTributos.put(Lists.newArrayList(tributoEntry.getValue()).get(0), valorPrimeiroTributo);
            }
        }

        BigDecimal valorDesconto = resultadoParcela.getValorDesconto();
        BigDecimal valorTotalTributosComDesconto = BigDecimal.ZERO;
        for (ItemValorDivida item : parcelaValorDivida.getValorDivida().getItemValorDividas()) {
            BigDecimal valor = novosTributos.get(item.getTributo());
            ItemParcelaValorDivida itemParcela = new ItemParcelaValorDivida();
            itemParcela.setItemValorDivida(item);
            itemParcela.setParcelaValorDivida(parcelaValorDivida);
            if (valor.compareTo(BigDecimal.ZERO) > 0) {
                itemParcela.setValor(valor.setScale(2, RoundingMode.HALF_UP));
            } else {
                itemParcela.setValor(BigDecimal.ZERO);
            }
            parcelaValorDivida.getItensParcelaValorDivida().add(itemParcela);
            if (hasDescontoAplicadoParaTipoTributo(parcelamento, itemParcela.getTributo().getTipoTributo())) {
                valorTotalTributosComDesconto = valorTotalTributosComDesconto.add(itemParcela.getValor());
            }
        }
        gerarDesconto(resultadoParcela, parcelaValorDivida, parcelaValorDivida.getVencimento(), valorDesconto, valorTotalTributosComDesconto, parcelamento);
    }

    private void gerarDesconto(ResultadoParcela resultadoParcela, ParcelaValorDivida parcelaValorDivida, Date vencimento, BigDecimal valorDesconto, BigDecimal valorTotalTributos, ProcessoParcelamento parcelamento) {
        BigDecimal totalDescontoAplicado = BigDecimal.ZERO;
        for (ItemParcelaValorDivida itemParcelaValorDivida : parcelaValorDivida.getItensParcelaValorDivida()) {
            if (itemParcelaValorDivida.getValor().compareTo(BigDecimal.ZERO) > 0 && hasDescontoAplicadoParaTipoTributo(parcelamento,
                itemParcelaValorDivida.getTributo().getTipoTributo())) {

                BigDecimal descontoProporcional = itemParcelaValorDivida.getValor().multiply(valorDesconto).divide(valorTotalTributos, 8, RoundingMode.HALF_UP);
                if (descontoProporcional.compareTo(BigDecimal.ZERO) > 0) {
                    DescontoItemParcela desconto = new DescontoItemParcela();
                    desconto.setItemParcelaValorDivida(itemParcelaValorDivida);
                    desconto.setInicio(new Date());
                    if (ParamParcelamento.TipoVigenciaDesconto.VENCIMENTO.equals(parcelamento.getParamParcelamento().getTipoVigenciaDesconto())) {
                        desconto.setFim(vencimento);
                    }
                    desconto.setDesconto(descontoProporcional.setScale(2, RoundingMode.HALF_UP));
                    desconto.setTipo(DescontoItemParcela.Tipo.VALOR);
                    desconto.setOrigem(DescontoItemParcela.Origem.PARCELAMENTO);
                    itemParcelaValorDivida.getDescontos().add(desconto);
                }
                totalDescontoAplicado = totalDescontoAplicado.add(descontoProporcional.setScale(2, RoundingMode.HALF_UP));
                resultadoParcela.getTributosDam().add(new TributoDamDTO(itemParcelaValorDivida.getTributo().getId(), itemParcelaValorDivida.getValor(), descontoProporcional));
            } else {
                resultadoParcela.getTributosDam().add(new TributoDamDTO(itemParcelaValorDivida.getTributo().getId(), itemParcelaValorDivida.getValor(), BigDecimal.ZERO));
            }
        }
        BigDecimal diferencaDesconto = valorDesconto.subtract(totalDescontoAplicado).setScale(2, RoundingMode.HALF_UP);
        for (TributoDamDTO tributoDamDTO : resultadoParcela.getTributosDam()) {
            if (diferencaDesconto.compareTo(BigDecimal.ZERO) > 0 && tributoDamDTO.getDesconto().compareTo(BigDecimal.ZERO) > 0) {
                if (diferencaDesconto.add(tributoDamDTO.getDesconto()).compareTo(tributoDamDTO.getValor()) < 0) {
                    tributoDamDTO.setDesconto(diferencaDesconto.add(tributoDamDTO.getDesconto()));
                } else {
                    tributoDamDTO.setDesconto(tributoDamDTO.getValor());
                    diferencaDesconto = diferencaDesconto.subtract(tributoDamDTO.getValor());
                }
            }
        }
    }

    private void adicionarItemValorDividaPorTributo(ValorDivida valorDivida, Map<Tributo, BigDecimal> novosTributos) {
        for (Map.Entry<Tributo, BigDecimal> valorEntry : novosTributos.entrySet()) {
            BigDecimal valor = valorEntry.getValue();
            if (valor.compareTo(BigDecimal.ZERO) > 0) {
                ItemValorDivida itemValorDivida = new ItemValorDivida();
                itemValorDivida.setTributo(valorEntry.getKey());
                itemValorDivida.setValor(valor);
                itemValorDivida.setValorDivida(valorDivida);
                valorDivida.getItemValorDividas().add(itemValorDivida);
            }
        }
    }

    private void preencherMapaPorTipoTributo(ProcessoParcelamento parcelamento, Map<Tributo.TipoTributo, Set<Tributo>> tributos) {
        for (ItemProcessoParcelamento itemParcelamento : parcelamento.getItensProcessoParcelamento()) {
            List<ItemParcelaValorDivida> itensParcela = buscarItensParcela(itemParcelamento.getParcelaValorDivida().getId());
            for (ItemParcelaValorDivida itemParcela : itensParcela) {
                if (itemParcela.getValor().compareTo(BigDecimal.ZERO) > 0) {
                    Tributo tributo = itemParcela.getItemValorDivida().getTributo();
                    Tributo.TipoTributo tipoTributo = tributo.getTipoTributo();
                    adicionarTributoNoMapa(tributos, tributo, tipoTributo);
                }
            }
        }
    }

    private void preencherMapaPorTipoTributosAcrescimos(Map<Tributo.TipoTributo, Set<Tributo>> tributos) {
        List<Tributo.TipoTributo> tributosPercorrer = Lists.newArrayList(tributos.keySet());
        for (Tributo.TipoTributo tipoTributo : tributosPercorrer) {
            for (Tributo tributo : tributos.get(tipoTributo)) {
                if (tributo.getTipoTributo().equals(Tributo.TipoTributo.TAXA) || tributo.getTipoTributo().equals(Tributo.TipoTributo.IMPOSTO)) {
                    adicionarTributoNoMapa(tributos, tributo.getTributoHonorarios(), Tributo.TipoTributo.HONORARIOS);
                    adicionarTributoNoMapa(tributos, tributo.getTributoCorrecaoMonetaria(), Tributo.TipoTributo.CORRECAO);
                    adicionarTributoNoMapa(tributos, tributo.getTributoJuros(), Tributo.TipoTributo.JUROS);
                    adicionarTributoNoMapa(tributos, tributo.getTributoMulta(), Tributo.TipoTributo.MULTA);
                }
            }
        }
    }

    private void preencherMapaDeTributosPorValor(ProcessoParcelamento parcelamento, Map<Tributo.TipoTributo, Set<Tributo>> tributos, Map<Tributo, BigDecimal> novosTributos) {
        for (Map.Entry<Tributo.TipoTributo, Set<Tributo>> tributoEntry : tributos.entrySet()) {
            BigDecimal valor = buscarValorTributo(tributoEntry.getKey(), parcelamento);
            BigDecimal valorAdicionado = BigDecimal.ZERO;
            valor = valor.divide(BigDecimal.valueOf(tributoEntry.getValue().size()), 6, RoundingMode.HALF_UP);

            for (Tributo tributo : tributoEntry.getValue()) {
                if (novosTributos.containsKey(tributo)) {
                    valor = valor.add(novosTributos.get(tributo));
                }
                novosTributos.put(tributo, valor);
                valorAdicionado = valorAdicionado.add(valor);
            }

            if (valorAdicionado.compareTo(buscarValorTributo(tributoEntry.getKey(), parcelamento)) != 0) {
                BigDecimal diferenca = buscarValorTributo(tributoEntry.getKey(), parcelamento).subtract(valorAdicionado);
                BigDecimal valorPrimeiroTributo = novosTributos.get(Lists.newArrayList(tributoEntry.getValue()).get(0));
                valorPrimeiroTributo = valorPrimeiroTributo.add(diferenca);
                novosTributos.put(Lists.newArrayList(tributoEntry.getValue()).get(0), valorPrimeiroTributo);
            }
        }
    }

    private void adicionarTributoNoMapa(Map<Tributo.TipoTributo, Set<Tributo>> tributos, Tributo tributo, Tributo.TipoTributo tipoTributo) {
        if (tributo != null) {
            if (!tributos.containsKey(tipoTributo)) {
                tributos.put(tipoTributo, Sets.<Tributo>newHashSet());
            }
            tributos.get(tipoTributo).add(tributo);
        }
    }

    private void cancelarParcelasAntigas(ProcessoParcelamento parcelamento) throws Exception {
        Map<Long, BigDecimal> valoresDesconto = Maps.newHashMap();
        BigDecimal descontoLancado = BigDecimal.ZERO;

        for (ItemProcessoParcelamento itemParcelamento : parcelamento.getItensProcessoParcelamento()) {
            Long idParcela = itemParcelamento.getParcelaValorDivida().getId();
            Map<String, Object> informacoesDaParcela = buscarInformacoesDaParcela(idParcela);

            Object resultadoSaldo = Lists.newArrayList(informacoesDaParcela.values()).get(0);
            Object resultadoSituacaoAtual = Lists.newArrayList(informacoesDaParcela.values()).get(1);
            Object resultadoReferencia = Lists.newArrayList(informacoesDaParcela.values()).get(2);

            BigDecimal saldo = resultadoSaldo != null ? (BigDecimal) resultadoSaldo : BigDecimal.ZERO;
            SituacaoParcela situacaoAtual = resultadoSituacaoAtual != null ? SituacaoParcela.valueOf((String) resultadoSituacaoAtual) : null;
            String refencia = "";
            if (SituacaoParcela.EM_ABERTO.equals(situacaoAtual)) {
                refencia = resultadoReferencia != null ? (String) resultadoReferencia : "";
            }
            refencia += " Parcelamento: " + parcelamento.getNumeroCompostoComAno() + buscarNumeroDoParcelamento(idParcela, parcelamento);
            parcelaValorDividaDAO.inserirSituacaoParcelaValorDivida(idParcela, refencia, saldo, SituacaoParcela.PARCELADO);

            BigDecimal aLancar = calcularDescontoParcelaAntiga(parcelamento, itemParcelamento);
            descontoLancado = descontoLancado.add(aLancar).setScale(2, RoundingMode.HALF_UP);
            valoresDesconto.put(idParcela, aLancar);
        }

        if (descontoLancado.compareTo(parcelamento.getValorDesconto()) != 0) {
            BigDecimal diferenca = parcelamento.getValorDesconto().subtract(descontoLancado);
            BigDecimal valorPrimeiraParcela = valoresDesconto.get(Lists.newArrayList(valoresDesconto.keySet()).get(0));
            valorPrimeiraParcela = valorPrimeiraParcela.add(diferenca);
            valoresDesconto.put(Lists.newArrayList(valoresDesconto.keySet()).get(0), valorPrimeiraParcela);
        }
    }

    private BigDecimal calcularDescontoParcelaAntiga(ProcessoParcelamento calculoParcelamento, ItemProcessoParcelamento item) {
        ParamParcelamentoTributo faixaDesconto = calculoParcelamento.getFaixaDesconto();
        BigDecimal aLancar = BigDecimal.ZERO;
        if (faixaDesconto != null) {
            aLancar = aLancar.add(calcularPercentualDesconto(faixaDesconto.getPercDescontoImposto(), item.getImposto()));
            aLancar = aLancar.add(calcularPercentualDesconto(faixaDesconto.getPercDescontoTaxa(), item.getTaxa()));
            aLancar = aLancar.add(calcularPercentualDesconto(faixaDesconto.getPercentualMulta(), item.getMulta()));
            aLancar = aLancar.add(calcularPercentualDesconto(faixaDesconto.getPercentualJuros(), item.getJuros()));
            aLancar = aLancar.add(calcularPercentualDesconto(faixaDesconto.getPercentualCorrecaoMonetaria(), item.getCorrecao()));
            aLancar = aLancar.add(calcularPercentualDesconto(faixaDesconto.getPercentualHonorarios(), item.getHonorarios()));
        }
        return aLancar;
    }

    private BigDecimal calcularPercentualDesconto(BigDecimal percentual, BigDecimal aplicavelSobre) {
        BigDecimal valorAplicavel = BigDecimal.ZERO;
        if (percentual != null && percentual.compareTo(BigDecimal.ZERO) > 0) {
            valorAplicavel = percentual.multiply(aplicavelSobre);
            valorAplicavel = valorAplicavel.divide(CEM, 2, RoundingMode.HALF_UP);
        }

        return valorAplicavel;
    }

    private BigDecimal buscarValorTributo(Tributo.TipoTributo tipoTributo, Object obj) {
        switch (tipoTributo) {
            case CORRECAO:
                return obj instanceof ResultadoParcela ? ((ResultadoParcela) obj).getValorCorrecao() : ((ProcessoParcelamento) obj).getValorTotalCorrecao();
            case HONORARIOS:
                return obj instanceof ResultadoParcela ? ((ResultadoParcela) obj).getValorHonorarios() : ((ProcessoParcelamento) obj).getValorTotalHonorariosAtualizado();
            case JUROS:
                return obj instanceof ResultadoParcela ? ((ResultadoParcela) obj).getValorJuros() : ((ProcessoParcelamento) obj).getValorTotalJuros();
            case MULTA:
                return obj instanceof ResultadoParcela ? ((ResultadoParcela) obj).getValorMulta() : ((ProcessoParcelamento) obj).getValorTotalMulta();
            case IMPOSTO:
                return obj instanceof ResultadoParcela ? ((ResultadoParcela) obj).getValorImposto() : ((ProcessoParcelamento) obj).getValorTotalImposto();
            case TAXA:
                return obj instanceof ResultadoParcela ? ((ResultadoParcela) obj).getValorTaxa() : ((ProcessoParcelamento) obj).getValorTotalTaxa();
            default:
                return BigDecimal.ZERO;
        }
    }

    private boolean hasDescontoAplicadoParaTipoTributo(ProcessoParcelamento parcelamento, Tributo.TipoTributo tipoTributo) {
        if (parcelamento.isDescontoImposto() && Tributo.TipoTributo.IMPOSTO.equals(tipoTributo)) {
            return true;
        } else if (parcelamento.isDescontoTaxa() && Tributo.TipoTributo.TAXA.equals(tipoTributo)) {
            return true;
        } else if (parcelamento.isDescontoJuros() && Tributo.TipoTributo.JUROS.equals(tipoTributo)) {
            return true;
        } else if (parcelamento.isDescontoMulta() && Tributo.TipoTributo.MULTA.equals(tipoTributo)) {
            return true;
        } else if (parcelamento.isDescontoCorrecao() && Tributo.TipoTributo.CORRECAO.equals(tipoTributo)) {
            return true;
        } else return parcelamento.isDescontoHonorarios() && Tributo.TipoTributo.HONORARIOS.equals(tipoTributo);
    }
}
