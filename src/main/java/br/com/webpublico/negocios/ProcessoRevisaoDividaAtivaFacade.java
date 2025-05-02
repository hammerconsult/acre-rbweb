package br.com.webpublico.negocios;

import br.com.webpublico.controle.ProcessoRevisaoDividaAtivaControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.VOSituacaoParcela;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.SituacaoProcessoRevisaoDividaAtiva;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.enums.TipoParcelaRevisaoDA;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.dao.JdbcParcelaValorDividaDAO;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class ProcessoRevisaoDividaAtivaFacade extends AbstractFacade<ProcessoRevisaoDividaAtiva> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    @EJB
    private CertidaoDividaAtivaFacade certidaoDividaAtivaFacade;
    @EJB
    private ComunicaSofPlanFacade comunicaSofPlanFacade;
    @EJB
    private ParametrosDividaAtivaFacade parametrosDividaAtivaFacade;
    @EJB
    private InscricaoDividaAtivaFacade inscricaoDividaAtivaFacade;
    private JdbcParcelaValorDividaDAO parcelaDAO;

    public ProcessoRevisaoDividaAtivaFacade() {
        super(ProcessoRevisaoDividaAtiva.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ProcessoRevisaoDividaAtiva recuperar(Object id) {
        ProcessoRevisaoDividaAtiva processo = super.recuperar(id);
        Hibernate.initialize(processo.getItens());
        Hibernate.initialize(processo.getExerciciosDebitos());
        return processo;
    }

    @PostConstruct
    public void init() {
        parcelaDAO = (JdbcParcelaValorDividaDAO) Util.getSpringBeanPeloNome("jdbcParcelaValorDividaDAO");
    }

    public List<ResultadoParcela> buscarParcelasFiltradas(ProcessoRevisaoDividaAtiva processoRevisaoDividaAtiva, ProcessoRevisaoDividaAtivaControlador.FiltroDebitos filtroDebitos) {
        ConsultaParcela consultaParcela = new ConsultaParcela();

        if (filtroDebitos.getExercicioCdaInicial() != null || filtroDebitos.getExercicioCdaFinal() != null ||
            filtroDebitos.getNumeroCdaInicial() != null || filtroDebitos.getNumeroCdaFinal() != null) {
            consultaParcela.addComplementoJoin(" inner join ItemInscricaoDividaAtiva itens on itens.id = vw.calculo_id ");
            consultaParcela.addComplementoJoin(" left join ItemCertidaoDividaAtiva itemCda on itemCda.itemInscricaoDividaAtiva_id = itens.id ");
            consultaParcela.addComplementoJoin(" left join CertidaoDividaAtiva cda on cda.id = itemCda.certidao_id ");
            consultaParcela.addComplementoJoin(" left join Exercicio exCda on exCda.id = cda.exercicio_id ");
        }

        consultaParcela.addParameter(ConsultaParcela.Campo.TIPO_CALCULO, ConsultaParcela.Operador.IGUAL, Calculo.TipoCalculo.INSCRICAO_DA);
        consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_DIVIDA_ATIVA, ConsultaParcela.Operador.IGUAL, 1);
        consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO);
        if (processoRevisaoDividaAtiva.getTipoCadastro().equals(TipoCadastroTributario.PESSOA)) {
            consultaParcela.addParameter(ConsultaParcela.Campo.PESSOA_ID, ConsultaParcela.Operador.IGUAL, processoRevisaoDividaAtiva.getPessoa().getId());
        } else {
            consultaParcela.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, processoRevisaoDividaAtiva.getCadastro().getId());
        }
        consultaParcela.addParameter(ConsultaParcela.Campo.DIVIDA_ID, ConsultaParcela.Operador.IGUAL, processoRevisaoDividaAtiva.getDivida().getDivida() != null ? processoRevisaoDividaAtiva.getDivida().getDivida().getId() : processoRevisaoDividaAtiva.getDivida().getId());
        if (processoRevisaoDividaAtiva.getExerciciosDebitos() != null &&
            !processoRevisaoDividaAtiva.getExerciciosDebitos().isEmpty()) {
            List<BigDecimal> anosDebitos = Lists.newArrayList();
            for (ProcessoRevisaoDividaAtivaExercicio pe : processoRevisaoDividaAtiva.getExerciciosDebitos()) {
                anosDebitos.add(BigDecimal.valueOf(pe.getExercicio().getAno()));
            }
            consultaParcela.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.IN, anosDebitos);
        }
        if (filtroDebitos.getVencimentoInicial() != null) {
            consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Operador.MAIOR_IGUAL, filtroDebitos.getVencimentoInicial());
        }
        if (filtroDebitos.getVencimentoFinal() != null) {
            consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Operador.MENOR_IGUAL, filtroDebitos.getVencimentoFinal());
        }
        if (filtroDebitos.getExercicioCdaInicial() != null) {
            consultaParcela.addComplementoDoWhere(" and exCda.ano >= " + filtroDebitos.getExercicioCdaInicial().getAno());
        }
        if (filtroDebitos.getExercicioCdaFinal() != null) {
            consultaParcela.addComplementoDoWhere(" and exCda.ano <= " + filtroDebitos.getExercicioCdaFinal().getAno());
        }
        if (filtroDebitos.getNumeroCdaInicial() != null) {
            consultaParcela.addComplementoDoWhere(" and cda.numero >= " + filtroDebitos.getNumeroCdaInicial());
        }
        if (filtroDebitos.getNumeroCdaFinal() != null) {
            consultaParcela.addComplementoDoWhere(" and cda.numero <= " + filtroDebitos.getNumeroCdaFinal());
        }
        return consultaParcela.executaConsulta().getResultados();
    }

    public ProcessoRevisaoDividaAtiva salvarRetornando(ProcessoRevisaoDividaAtiva processo) {
        return em.merge(processo);
    }

    public List<ResultadoParcela> buscarParcelasOriginaisDosItensDaRevisao(List<ItemProcessoRevisaoDividaAtiva> itens) {
        List<ResultadoParcela> parcelas = Lists.newArrayList();
        for (ItemProcessoRevisaoDividaAtiva itemProcessoRevisaoDividaAtiva : itens) {
            parcelas.addAll(buscarParcelasOriginaisDaDividaAtiva(itemProcessoRevisaoDividaAtiva));
        }
        return parcelas;
    }

    private List<ResultadoParcela> buscarParcelasOriginaisDaDividaAtiva(ItemProcessoRevisaoDividaAtiva itemProcessoRevisaoDividaAtiva) {
        return buscarParcelasOriginaisDaDividaAtiva(itemProcessoRevisaoDividaAtiva, true);
    }

    public List<ResultadoParcela> buscarParcelasOriginaisDaDividaAtiva(ItemProcessoRevisaoDividaAtiva itemProcessoRevisaoDividaAtiva, boolean filtrarSituacaoParcela) {
        List<ResultadoParcela> parcelas = Lists.newArrayList();
        if (TipoParcelaRevisaoDA.DIVIDA_ATIVA_EM_REVISAO.equals(itemProcessoRevisaoDividaAtiva.getTipoParcelaRevisaoDA())) {
            String sql = "select pvdOriginal.id from ParcelaValorDivida pvd " +
                "inner join ValorDivida vd on vd.id = pvd.valorDivida_id " +
                "inner join ItemInscricaoDividaAtiva itemIns on itemIns.id = vd.calculo_id " +
                "inner join InscricaoDividaParcela insParc on insParc.itemInscricaoDividaAtiva_id = itemIns.id " +
                "inner join ParcelaValorDivida pvdOriginal on pvdOriginal.id = insParc.parcelaValorDivida_id " +
                "inner join SituacaoParcelaValorDivida spvd on spvd.id = pvdOriginal.situacaoAtual_id " +
                "where pvd.id = :idParcelaDA " +
                (filtrarSituacaoParcela ? " and spvd.situacaoParcela in (:situacaoParcela) " : "");

            Query q = em.createNativeQuery(sql);
            q.setParameter("idParcelaDA", itemProcessoRevisaoDividaAtiva.getParcela().getId());
            if (filtrarSituacaoParcela) {
                q.setParameter("situacaoParcela", Lists.newArrayList(SituacaoParcela.INSCRITA_EM_DIVIDA_ATIVA.name(), SituacaoParcela.CANCELAMENTO.name()));
            }
            List<BigDecimal> idsParcelasOriginais = q.getResultList();

            if (!idsParcelasOriginais.isEmpty()) {
                ConsultaParcela consulta = new ConsultaParcela();
                consulta.addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IN, idsParcelasOriginais);
                consulta.executaConsulta();
                List<ResultadoParcela> parcelasDaDividaAtiva = consulta.getResultados();

                List<ResultadoParcela> parcelasOutraOpcao = buscarParcelasDeOutraOpcaoDePagamento(parcelasDaDividaAtiva);
                if (!parcelasOutraOpcao.isEmpty()) {
                    parcelas.addAll(parcelasOutraOpcao);
                }
                parcelas.addAll(parcelasDaDividaAtiva);
            }
        }
        return parcelas;
    }

    public List<ResultadoParcela> buscarParcelasPeloIdCalculo(Long idCalculo) {
        String sql = "select pvd.id from ParcelaValorDivida pvd " +
            "inner join ValorDivida vd on vd.id = pvd.valorDivida_id " +
            "where vd.calculo_id = :idCalculo ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idCalculo", idCalculo);
        List<BigDecimal> idsParcelasOriginais = q.getResultList();

        if (!idsParcelasOriginais.isEmpty()) {
            ConsultaParcela consulta = new ConsultaParcela();
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IN, idsParcelasOriginais);
            consulta.executaConsulta();
            return consulta.getResultados();
        }
        return Lists.newArrayList();
    }

    private List<ResultadoParcela> buscarParcelasDeOutraOpcaoDePagamento(List<ResultadoParcela> parcelas) {
        List<Long> idsValorDivida = Lists.newArrayList();
        List<Long> idsOpcaoPagamento = Lists.newArrayList();
        for (ResultadoParcela parcela : parcelas) {
            if (!idsValorDivida.contains(parcela.getIdValorDivida())) {
                idsValorDivida.add(parcela.getIdValorDivida());
            }
            if (!idsOpcaoPagamento.contains(parcela.getIdOpcaoPagamento())) {
                idsOpcaoPagamento.add(parcela.getIdOpcaoPagamento());
            }
        }
        if (!idsValorDivida.isEmpty() && !idsOpcaoPagamento.isEmpty()) {
            ConsultaParcela consulta = new ConsultaParcela();
            consulta.addParameter(ConsultaParcela.Campo.VALOR_DIVIDA_ID, ConsultaParcela.Operador.IN, idsValorDivida);
            consulta.addParameter(ConsultaParcela.Campo.OPCAO_PAGAMENTO_PARCELA, ConsultaParcela.Operador.NOT_IN, idsOpcaoPagamento);
            consulta.executaConsulta();
            return consulta.getResultados();
        }
        return Lists.newArrayList();
    }

    public List<ResultadoParcela> buscarParcelasEmAbertoDoNovoCalculo(ProcessoRevisaoDividaAtiva processo, List<ResultadoParcela> parcelas) {
        if (TipoCadastroTributario.IMOBILIARIO.equals(processo.getTipoCadastro()) && !parcelas.isEmpty()) {
            List<Long> idsValorDivida = Lists.newArrayList();
            List<Integer> exercicios = Lists.newArrayList();
            for (ResultadoParcela parcela : parcelas) {
                if (!idsValorDivida.contains(parcela.getIdValorDivida())) {
                    idsValorDivida.add(parcela.getIdValorDivida());
                }
                if (!exercicios.contains(parcela.getExercicio())) {
                    exercicios.add(parcela.getExercicio());
                }
            }

            List<SituacaoParcela> situacoes = Lists.newArrayList();
            situacoes.add(SituacaoParcela.EM_ABERTO);
            situacoes.add(SituacaoParcela.INSCRITA_EM_DIVIDA_ATIVA);
            situacoes.add(SituacaoParcela.CANCELAMENTO);

            ConsultaParcela consulta = new ConsultaParcela();
            consulta.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, processo.getCadastro().getId());
            consulta.addParameter(ConsultaParcela.Campo.DIVIDA_ID, ConsultaParcela.Operador.IGUAL, processo.getDivida().getId());
            consulta.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.IN, exercicios);
            consulta.addParameter(ConsultaParcela.Campo.VALOR_DIVIDA_ID, ConsultaParcela.Operador.NOT_IN, idsValorDivida);
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IN, situacoes);

            return consulta.executaConsulta().getResultados();
        }
        return Lists.newArrayList();
    }

    public void alterarSituacaoParcelas(ProcessoRevisaoDividaAtiva processoRevisaoDividaAtiva) {
        for (ItemProcessoRevisaoDividaAtiva itemProcessoRevisaoDividaAtiva : processoRevisaoDividaAtiva.getItens()) {
            salvaSituacaoParcela(itemProcessoRevisaoDividaAtiva.getParcela(), itemProcessoRevisaoDividaAtiva.getTipoParcelaRevisaoDA().getSituacaoParcela(), null);
        }
    }

    private void alterarSituacaoParcelasEstorno(ProcessoRevisaoDividaAtiva processoRevisaoDividaAtiva) {
        for (ItemProcessoRevisaoDividaAtiva itemProcessoRevisaoDividaAtiva : processoRevisaoDividaAtiva.getItens()) {
            salvaSituacaoParcela(itemProcessoRevisaoDividaAtiva.getParcela(), itemProcessoRevisaoDividaAtiva.getTipoParcelaRevisaoDA().getSituacaoParcelaEstorno(), null);
        }
    }

    private void alterarSituacaoParcelasExercicioEstorno(ProcessoRevisaoDividaAtiva processoRevisaoDividaAtiva, List<Integer> exercicios) {
        for (ItemProcessoRevisaoDividaAtiva itemProcessoRevisaoDividaAtiva : processoRevisaoDividaAtiva.getItens()) {
            if (exercicios.contains(itemProcessoRevisaoDividaAtiva.getParcela().getResultadoParcela().getExercicio())) {
                salvaSituacaoParcela(itemProcessoRevisaoDividaAtiva.getParcela(), itemProcessoRevisaoDividaAtiva.getTipoParcelaRevisaoDA().getSituacaoParcelaEstorno(), null);
            }
        }
    }

    private void alterarSituacaoParcelasFinalizacao(ProcessoRevisaoDividaAtiva processoRevisaoDividaAtiva) {
        for (ItemProcessoRevisaoDividaAtiva itemProcessoRevisaoDividaAtiva : processoRevisaoDividaAtiva.getItens()) {
            if (!TipoParcelaRevisaoDA.DIVIDA_ATIVA_EM_REVISAO.equals(itemProcessoRevisaoDividaAtiva.getTipoParcelaRevisaoDA())) {
                salvaSituacaoParcela(itemProcessoRevisaoDividaAtiva.getParcela(), itemProcessoRevisaoDividaAtiva.getTipoParcelaRevisaoDA().getSituacaoParcelaFinal(), null);
            }
        }
    }

    private void alterarSituacaoNovasParcelasFinalizacao(List<ResultadoParcela> novasParcelas) {
        for (ResultadoParcela novaParcela : novasParcelas) {
            ParcelaValorDivida parcela = consultaDebitoFacade.recuperaParcela(novaParcela.getIdParcela());
            SituacaoParcela situacao = TipoParcelaRevisaoDA.NOVO_DEBITO.getSituacaoParcelaFinal();
            if (parcela.getOpcaoPagamento().getPromocional()) {
                situacao = SituacaoParcela.CANCELAMENTO;
            }
            salvaSituacaoParcela(parcela, situacao, BigDecimal.ZERO);
        }
    }

    private void salvaSituacaoParcela(ParcelaValorDivida parcela, SituacaoParcela situacao, BigDecimal saldo) {
        try {
            salvaSituacaoParcela(parcela.getId(),
                parcela.getSituacaoAtual().getReferencia(), situacao, saldo);
        } catch (Exception ex) {
            logger.error("Erro ao salvar a nova situação da parcela: {}", ex.getMessage());
        }
    }

    public void salvaSituacaoParcela(Long idParcela, String referencia, SituacaoParcela situacao, BigDecimal saldo) {
        try {
            parcelaDAO.inserirSituacaoParcelaValorDivida(idParcela,
                referencia, saldo, situacao);
        } catch (Exception ex) {
            logger.error("Erro ao salvar a nova situação da parcela: {}", ex.getMessage());
        }
    }

    public ProcessoRevisaoDividaAtiva estornarProcesso(ProcessoRevisaoDividaAtiva processoRevisaoDividaAtiva) {
        processoRevisaoDividaAtiva.setSituacaoProcesso(SituacaoProcessoRevisaoDividaAtiva.ESTORNADO);
        alterarSituacaoParcelasEstorno(processoRevisaoDividaAtiva);
        return em.merge(processoRevisaoDividaAtiva);
    }

    public void estornarDebitosExercicioProcesso(ProcessoRevisaoDividaAtiva processoRevisaoDividaAtiva, List<Integer> exercicios) {
        alterarSituacaoParcelasExercicioEstorno(processoRevisaoDividaAtiva, exercicios);
    }

    public ProcessoRevisaoDividaAtiva finalizarProcesso(ProcessoRevisaoDividaAtiva processoRevisaoDividaAtiva, List<ResultadoParcela> novasParcelas, List<Integer> exerciciosNaoCalculados) {
        processoRevisaoDividaAtiva.setSituacaoProcesso(SituacaoProcessoRevisaoDividaAtiva.FINALIZADO);
        processoRevisaoDividaAtiva = em.merge(processoRevisaoDividaAtiva);

        buscarParcelasOriginaisAndEmAbertoDasParcelasEmRevisao(processoRevisaoDividaAtiva);
        alterarSituacaoParcelasFinalizacao(processoRevisaoDividaAtiva);
        alterarSituacaoNovasParcelasFinalizacao(novasParcelas);
        trocarParcelasNaInscricaoDeDividaAtiva(processoRevisaoDividaAtiva, novasParcelas, exerciciosNaoCalculados);
        atualizarValorDaDividaAtiva(processoRevisaoDividaAtiva);

        return processoRevisaoDividaAtiva;
    }

    private void buscarParcelasOriginaisAndEmAbertoDasParcelasEmRevisao(ProcessoRevisaoDividaAtiva processoRevisaoDividaAtiva) {
        for (ItemProcessoRevisaoDividaAtiva item : processoRevisaoDividaAtiva.getItens()) {
            if (TipoParcelaRevisaoDA.DIVIDA_ATIVA_EM_REVISAO.equals(item.getTipoParcelaRevisaoDA())) {
                List<ResultadoParcela> originais = buscarParcelasOriginaisDaDividaAtiva(item, false);
                item.setParcelas(buscarParcelasEmAbertoDoNovoCalculo(processoRevisaoDividaAtiva, originais));
            }
        }
    }

    private void atualizarValorDaDividaAtiva(ProcessoRevisaoDividaAtiva processoRevisaoDividaAtiva) {
        try {
            for (ItemProcessoRevisaoDividaAtiva itemProcessoRevisaoDividaAtiva : processoRevisaoDividaAtiva.getItens()) {
                if (itemProcessoRevisaoDividaAtiva.getTipoParcelaRevisaoDA().equals(TipoParcelaRevisaoDA.DIVIDA_ATIVA_EM_REVISAO)) {
                    List<ResultadoParcela> parcelas = itemProcessoRevisaoDividaAtiva.getParcelas();

                    BigDecimal imposto = BigDecimal.ZERO;
                    BigDecimal taxa = BigDecimal.ZERO;
                    for (ResultadoParcela parcela : parcelas) {
                        if (!parcela.getCotaUnica()) {
                            imposto = imposto.add(parcela.getValorImposto());
                            taxa = taxa.add(parcela.getValorTaxa());
                        }
                    }

                    BigDecimal total = imposto.add(taxa);
                    if (total.compareTo(BigDecimal.ZERO) > 0) {
                        ParcelaValorDivida parcela = consultaDebitoFacade.recuperaParcela(itemProcessoRevisaoDividaAtiva.getParcela().getId());
                        ValorDivida valorDivida = consultaDebitoFacade.recuperar(itemProcessoRevisaoDividaAtiva.getParcela().getValorDivida().getId());
                        valorDivida.setValor(total);

                        ItemValorDivida itemValorDividaImposto = getItemValorDividaPorTipo(valorDivida, Tributo.TipoTributo.IMPOSTO);
                        if (itemValorDividaImposto != null) {
                            itemValorDividaImposto.setValor(imposto);
                        } else {
                            itemValorDividaImposto = criarItemParaTipo(valorDivida, Tributo.TipoTributo.IMPOSTO, imposto, parcelas.get(0).getIdValorDivida());
                        }

                        ItemValorDivida itemValorDividaTaxa = getItemValorDividaPorTipo(valorDivida, Tributo.TipoTributo.TAXA);
                        if (itemValorDividaTaxa != null) {
                            itemValorDividaTaxa.setValor(taxa);
                        } else {
                            itemValorDividaTaxa = criarItemParaTipo(valorDivida, Tributo.TipoTributo.TAXA, taxa, parcelas.get(0).getIdValorDivida());
                        }

                        ItemParcelaValorDivida itemParcelaImposto = getItemParcelaValorDividaPorTipo(parcela, Tributo.TipoTributo.IMPOSTO);
                        if (itemParcelaImposto != null) {
                            itemParcelaImposto.setValor(imposto);
                        } else {
                            itemParcelaImposto = criarItemParcelaParaTipo(itemValorDividaImposto, parcela);
                        }

                        ItemParcelaValorDivida itemParcelaTaxa = getItemParcelaValorDividaPorTipo(parcela, Tributo.TipoTributo.TAXA);
                        if (itemParcelaTaxa != null) {
                            itemParcelaTaxa.setValor(taxa);
                        } else {
                            itemParcelaTaxa = criarItemParcelaParaTipo(itemValorDividaTaxa, parcela);
                        }
                        parcela.setValor(total);
                        Calculo calculo = valorDivida.getCalculo();
                        valorDivida = em.merge(valorDivida);
                        calculo.setSubDivida(Long.valueOf(parcelas.get(0).getSd()));

                        ParcelaValorDivida parcelaValorDivida = valorDivida.getParcelaValorDividas().get(0);
                        processoRevisaoDividaAtiva.getSituacoesDAEmRevisao().add(new VOSituacaoParcela(parcelaValorDivida.getId(), parcelaValorDivida.getSituacaoAtual()
                            .getReferencia(), itemProcessoRevisaoDividaAtiva.getTipoParcelaRevisaoDA().getSituacaoParcelaFinal(), total));
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Erro ao atualizar o valor da dívida ativa: {}", ex.getMessage());
            throw new ExcecaoNegocioGenerica("Não foi possível revisar a divida ativa");
        }
    }

    private ItemValorDivida criarItemParaTipo(ValorDivida valorDivida, Tributo.TipoTributo tipo, BigDecimal valor, Long idValorDividaOriginado) {
        ItemValorDivida item = new ItemValorDivida();
        item.setValor(valor);
        item.setValorDivida(valorDivida);
        item.setTributo(recuperarTributoPorTipoDoValorDivida(tipo, idValorDividaOriginado));
        valorDivida.getItemValorDividas().add(item);
        return item;
    }

    private ItemParcelaValorDivida criarItemParcelaParaTipo(ItemValorDivida itemValorDivida, ParcelaValorDivida parcela) {
        ItemParcelaValorDivida item = new ItemParcelaValorDivida();
        item.setValor(itemValorDivida.getValor());
        item.setParcelaValorDivida(parcela);
        item.setItemValorDivida(itemValorDivida);
        parcela.getItensParcelaValorDivida().add(item);
        return item;
    }

    private Tributo recuperarTributoPorTipoDoValorDivida(Tributo.TipoTributo tipo, Long idValorDivida) {
        Query query = em.createQuery("select item.tributo from ItemValorDivida  item where item.tributo.tipoTributo = :tipo and item.valorDivida.id = :idValorDivida");
        query.setParameter("tipo", tipo);
        query.setParameter("idValorDivida", idValorDivida);
        if (!query.getResultList().isEmpty()) {
            return (Tributo) query.getResultList().get(0);
        }
        return null;
    }

    private ItemValorDivida getItemValorDividaPorTipo(ValorDivida valorDivida, Tributo.TipoTributo tipo) {
        for (ItemValorDivida itemValorDivida : valorDivida.getItemValorDividas()) {
            if (itemValorDivida.getValor().compareTo(BigDecimal.ZERO) > 0
                && tipo.equals(itemValorDivida.getTributo().getTipoTributo())) {
                return itemValorDivida;
            }
        }
        return null;
    }

    private ItemParcelaValorDivida getItemParcelaValorDividaPorTipo(ParcelaValorDivida parcelaValorDivida, Tributo.TipoTributo tipo) {
        for (ItemParcelaValorDivida item : parcelaValorDivida.getItensParcelaValorDivida()) {
            if (item.getValor().compareTo(BigDecimal.ZERO) > 0
                && tipo.equals(item.getTributo().getTipoTributo())) {
                return item;
            }
        }
        return null;
    }

    public void retificarCertidoesDividaAtiva(ProcessoRevisaoDividaAtiva processoRevisaoDividaAtiva) {
        for (ItemProcessoRevisaoDividaAtiva itemProcessoRevisaoDividaAtiva : processoRevisaoDividaAtiva.getItens()) {
            if (TipoParcelaRevisaoDA.DIVIDA_ATIVA_EM_REVISAO.equals(itemProcessoRevisaoDividaAtiva.getTipoParcelaRevisaoDA())) {
                try {
                    List<CertidaoDividaAtiva> cdas = buscarCertidoesDividaAtivaDaParcela(itemProcessoRevisaoDividaAtiva.getParcela().getId());
                    if (!cdas.isEmpty()) {
                        CertidaoDividaAtiva certidaoDividaAtiva = cdas.get(0);
                        certidaoDividaAtiva = certidaoDividaAtivaFacade.atualizarValorCertidao(certidaoDividaAtiva);
                        certidaoDividaAtiva = em.merge(certidaoDividaAtiva);

                        comunicaSofPlanFacade.enviarCDA(Lists.newArrayList(certidaoDividaAtiva));
                    }
                } catch (Exception ex) {
                    logger.error("Erro ao retificar a CDA: {}", ex.getMessage());
                }
            }
        }
    }

    private void trocarParcelasNaInscricaoDeDividaAtiva(ProcessoRevisaoDividaAtiva processoRevisaoDividaAtiva, List<ResultadoParcela> novasParcelas, List<Integer> exerciciosNaoCalculados) {
        List<Long> idsParcelasAguardandoRevisao = Lists.newArrayList();

        for (ItemProcessoRevisaoDividaAtiva itemProcessoRevisaoDividaAtiva : processoRevisaoDividaAtiva.getItens()) {
            if (TipoParcelaRevisaoDA.AGUARDANDO_REVISAO.equals(itemProcessoRevisaoDividaAtiva.getTipoParcelaRevisaoDA())) {
                idsParcelasAguardandoRevisao.add(itemProcessoRevisaoDividaAtiva.getParcela().getId());
            }
        }
        if (idsParcelasAguardandoRevisao.size() > novasParcelas.size() && exerciciosNaoCalculados.isEmpty()) {
            removerParcelaEmInscricaoDividaParcela(idsParcelasAguardandoRevisao);
        }
        for (ItemProcessoRevisaoDividaAtiva itemProcessoRevisaoDividaAtiva : processoRevisaoDividaAtiva.getItens()) {
            if (TipoParcelaRevisaoDA.AGUARDANDO_REVISAO.equals(itemProcessoRevisaoDividaAtiva.getTipoParcelaRevisaoDA())) {
                for (ResultadoParcela parcela : novasParcelas) {
                    if (Integer.parseInt(itemProcessoRevisaoDividaAtiva.getParcela().getSequenciaParcela()) == parcela.getSequenciaParcela()
                        && itemProcessoRevisaoDividaAtiva.getParcela().getOpcaoPagamento().getPromocional().equals(parcela.getCotaUnica())
                        && itemProcessoRevisaoDividaAtiva.getParcela().getValorDivida().getExercicio().getAno().equals(parcela.getExercicio())) {
                        if (verificarExisteParcelaEmInscricaoDividaParcela(itemProcessoRevisaoDividaAtiva.getParcela().getId())) {
                            alterarIdParcelaEmInscricaoDividaParcela(itemProcessoRevisaoDividaAtiva.getParcela().getId(), parcela.getIdParcela());
                        } else {
                            Long idItemParcela = null;
                            for (ResultadoParcela np : novasParcelas) {
                                if (parcela.getIdCalculo().equals(np.getIdCalculo())) {
                                    if (verificarExisteParcelaEmInscricaoDividaParcela(np.getIdParcela())) {
                                        idItemParcela = buscarIdItemInscricaoDividaAtiva(np.getIdParcela());
                                        break;
                                    }
                                }
                            }
                            if (idItemParcela != null) {
                                adicionarParcelaEmInscricaoDividaParcela(idItemParcela, parcela.getIdParcela());
                            }
                        }
                    }
                }
            }
        }
    }


    public Long buscarIdItemInscricaoDividaAtiva(Long idParcela) {
        String sql = "SELECT ITEMINSCRICAODIVIDAATIVA_ID FROM INSCRICAODIVIDAPARCELA WHERE PARCELAVALORDIVIDA_ID = :idParcela";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idParcela", idParcela);
        List<Object> result = q.getResultList();
        return (result != null && !result.isEmpty()) ? ((BigDecimal) result.get(0)).longValue() : null;
    }

    private Boolean verificarExisteParcelaEmInscricaoDividaParcela(Long idParcelaOrigem) {
        try {
            String sql = "SELECT ID FROM INSCRICAODIVIDAPARCELA WHERE PARCELAVALORDIVIDA_ID = :idParcela";

            Query q = em.createNativeQuery(sql);
            q.setParameter("idParcela", idParcelaOrigem);
            List<Object> result = q.getResultList();
            return (result != null && !result.isEmpty()) ? Boolean.TRUE : Boolean.FALSE;
        } catch (Exception ex) {
            logger.error("Erro ao verificar parcela de origem da dívida ativa: {}", ex.getMessage());
            return Boolean.FALSE;
        }
    }

    private void alterarIdParcelaEmInscricaoDividaParcela(Long idParcelaOrigem, Long idParcelaDestino) {
        try {
            String sql = "update InscricaoDividaParcela set parcelaValorDivida_id = :idParcelaDestino " +
                " where parcelaValorDivida_id = :idParcelaOrigem";

            Query q = em.createNativeQuery(sql);
            q.setParameter("idParcelaOrigem", idParcelaOrigem);
            q.setParameter("idParcelaDestino", idParcelaDestino);
            q.executeUpdate();
        } catch (Exception ex) {
            logger.error("Erro ao altera a parcela de origem da dívida ativa: {}", ex.getMessage());
        }
    }

    private void adicionarParcelaEmInscricaoDividaParcela(Long idItem, Long idParcela) {
        try {
            String sql = "INSERT INTO INSCRICAODIVIDAPARCELA" +
                "(ID, ITEMINSCRICAODIVIDAATIVA_ID, PARCELAVALORDIVIDA_ID) " +
                " VALUES(hibernate_sequence.nextVal, :itemId, :pvdId) ";

            Query q = em.createNativeQuery(sql);
            q.setParameter("itemId", idItem);
            q.setParameter("pvdId", idParcela);
            q.executeUpdate();
        } catch (Exception ex) {
            logger.error("Erro ao adicionar a parcela de origem da dívida ativa: {}", ex.getMessage());
        }
    }

    private void removerParcelaEmInscricaoDividaParcela(List<Long> idParcelas) {
        try {
            String sql = "DELETE FROM INSCRICAODIVIDAPARCELA WHERE PARCELAVALORDIVIDA_ID in (:pvdIds)";
            Query q = em.createNativeQuery(sql);
            q.setParameter("pvdIds", idParcelas);
            q.executeUpdate();
        } catch (Exception ex) {
            logger.error("Erro ao remover a parcela de origem da dívida ativa: {}", ex.getMessage());
        }
    }

    private void alterarIdParcelaEmInscricaoDividaParcelaPeloId(Long idInscricaoDividaParcela, Long idParcelaDestino) {
        try {
            String sql = "update InscricaoDividaParcela set parcelaValorDivida_id = :idParcelaDestino " +
                " where id = :idInscricaoDividaParcela";

            Query q = em.createNativeQuery(sql);
            q.setParameter("idInscricaoDividaParcela", idInscricaoDividaParcela);
            q.setParameter("idParcelaDestino", idParcelaDestino);
            Util.imprimeSQL(sql, q);
            q.executeUpdate();
        } catch (Exception ex) {
            logger.error("Erro ao altera a parcela de origem da dívida ativa: {}", ex.getMessage());
        }
    }

    public List<CertidaoDividaAtiva> buscarCertidoesDividaAtivaDaParcela(Long idParcela) {
        return certidaoDividaAtivaFacade.buscarCertidoesDividaAtivaDaParcela(idParcela);
    }

    public void corrigirParcelasNaInscricaoDeDividaAtiva(ProcessoRevisaoDividaAtiva processoRevisaoDividaAtiva, List<ResultadoParcela> parcelasEmAberto) {
        for (ItemProcessoRevisaoDividaAtiva itemProcessoRevisaoDividaAtiva : processoRevisaoDividaAtiva.getItens()) {
            if (TipoParcelaRevisaoDA.DIVIDA_ATIVA_EM_REVISAO.equals(itemProcessoRevisaoDividaAtiva.getTipoParcelaRevisaoDA())) {
                logger.error("Parcela: " + itemProcessoRevisaoDividaAtiva.getParcela().getValorDivida().getExercicio() + " - " +
                    itemProcessoRevisaoDividaAtiva.getParcela().getValorDivida().getDivida().getDescricao() + " - " +
                    itemProcessoRevisaoDividaAtiva.getParcela().getSequenciaParcela() + " - " +
                    itemProcessoRevisaoDividaAtiva.getParcela().getOpcaoPagamento().getDescricao());

                List<InscricaoDividaParcela> inscricoes = buscarInscricaoDividaParcelaDaDividaAtiva(itemProcessoRevisaoDividaAtiva.getParcela());
                for (InscricaoDividaParcela inscricao : inscricoes) {
                    Exercicio exercicio = buscarExercicioDaInscricao(inscricao.getItemInscricaoDividaAtiva());
                    if (exercicio != null) {
                        for (ResultadoParcela parcela : parcelasEmAberto) {
                            if (Integer.parseInt(inscricao.getParcelaValorDivida().getSequenciaParcela()) == parcela.getSequenciaParcela()
                                && inscricao.getParcelaValorDivida().getOpcaoPagamento().getPromocional().equals(parcela.getCotaUnica())
                                && exercicio.getAno().equals(parcela.getExercicio())) {

                                alterarIdParcelaEmInscricaoDividaParcelaPeloId(inscricao.getId(), parcela.getIdParcela());
                            }
                        }
                    }
                }
            }
        }
    }

    private Exercicio buscarExercicioDaInscricao(ItemInscricaoDividaAtiva itemInscricaoDividaAtiva) {
        String sql = "select ex.* from ItemInscricaoDividaAtiva item\n" +
            "inner join ValorDivida vd on vd.calculo_id = item.id\n" +
            "inner join Exercicio ex on ex.id = vd.exercicio_id\n" +
            "where item.id = :idItemInscricaoDividaAtiva";
        Query q = em.createNativeQuery(sql, Exercicio.class);
        q.setParameter("idItemInscricaoDividaAtiva", itemInscricaoDividaAtiva.getId());
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return (Exercicio) resultList.get(0);
        }
        return null;
    }

    private List<InscricaoDividaParcela> buscarInscricaoDividaParcelaDaDividaAtiva(ParcelaValorDivida parcela) {
        String sql = "select idp.* from InscricaoDividaParcela idp\n" +
            "inner join ItemInscricaoDividaAtiva item on item.id = idp.itemInscricaoDividaAtiva_id\n" +
            "inner join ValorDivida vd on vd.calculo_id = item.id\n" +
            "inner join ParcelaValorDivida pvd on pvd.valorDivida_id = vd.id\n" +
            "where pvd.id = :idParcela";
        Query q = em.createNativeQuery(sql, InscricaoDividaParcela.class);
        q.setParameter("idParcela", parcela.getId());
        return q.getResultList();
    }

    public boolean hasDividaAtivaPagoPorParcelamentoEmAberto(Exercicio exercicio, Cadastro cadastro, Divida divida) {
        return inscricaoDividaAtivaFacade.hasDividaAtivaPagoPorParcelamentoEmAberto(exercicio, cadastro, divida);
    }
}
