package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.contabil.conciliacaocontabil.AnaliseMovimentoContabil;
import br.com.webpublico.entidades.contabil.conciliacaocontabil.MovimentoHashContabil;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.entidadesauxiliares.contabil.ExtratoMovimentoDespesaORC;
import br.com.webpublico.entidadesauxiliares.contabil.ReprocessamentoSaldoExtraOrcamentario;
import br.com.webpublico.entidadesauxiliares.contabil.analise.*;
import br.com.webpublico.entidadesauxiliares.contabil.apiservicecontabil.SaldoFonteDespesaORCVO;
import br.com.webpublico.enums.Intervalo;
import br.com.webpublico.enums.OperacaoMovimentoDividaPublica;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoReprocessamentoHistorico;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.contabil.SituacaoMovimentoContabil;
import br.com.webpublico.negocios.contabil.execucao.ExtratoMovimentoDespesaOrcFacade;
import br.com.webpublico.negocios.contabil.reprocessamento.ReprocessamentoSaldoExtraOrcamentarioFacade;
import br.com.webpublico.negocios.contabil.reprocessamento.daos.JdbcMovimentoHashContabil;
import br.com.webpublico.seguranca.service.QueryReprocessamentoService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.springframework.web.context.ContextLoader;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Stateless
public class AnaliseContabilFacade {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private PlanoDeContasFacade planoDeContasFacade;
    @EJB
    private OrigemOCCFacade origemOCCFacade;
    @EJB
    private CLPFacade clpFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private TipoContaFacade tipoContaFacade;
    @EJB
    private DespesaORCFacade despesaORCFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private ExtratoMovimentoDespesaOrcFacade extratoMovimentoDespesaOrcFacade;
    @EJB
    private ReprocessamentoSaldoSubContaFacade reprocessamentoSaldoSubContaFacade;
    @EJB
    private SubContaFacade subContaFacade;
    @EJB
    private SaldoSubContaFacade saldoSubContaFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private SaldoContaContabilFacade saldoContaContabilFacade;
    @EJB
    private ReprocessamentoSaldoExtraOrcamentarioFacade reprocessamentoSaldoExtraOrcamentarioFacade;
    @EJB
    private SaldoExtraorcamentarioFacade saldoExtraorcamentarioFacade;
    @EJB
    private ReprocessamentoSaldoDividaPublicaFacade reprocessamentoSaldoDividaPublicaFacade;
    @EJB
    private ReprocessamentoSaldoBensFacade reprocessamentoSaldoGrupoBemFacade;
    @EJB
    private SaldoDividaPublicaFacade saldoDividaPublicaFacade;
    @EJB
    private SaldoGrupoBemMovelFacade saldoGrupoBemMovelFacade;
    @EJB
    private SaldoGrupoBemImovelFacade saldoGrupoBemImovelFacade;
    @EJB
    private SaldoGrupoMaterialFacade saldoGrupoMaterialFacade;
    @EJB
    private SaldoFonteDespesaORCFacade saldoFonteDespesaORCFacade;
    private JdbcMovimentoHashContabil jdbcMovimentoHashContabil;

    @PostConstruct
    private void init() {
        jdbcMovimentoHashContabil = (JdbcMovimentoHashContabil) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcMovimentoHashContabil");
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public AssistenteAnaliseContabil gerarSaldo(AssistenteAnaliseContabil assistente, TipoAssistenteAnaliseContabil tipo) {
        gerarSaldoOrcamentario(assistente, tipo);
        return assistente;
    }

    private void gerarSaldoOrcamentario(AssistenteAnaliseContabil assistente, TipoAssistenteAnaliseContabil tipo) {
        if (!TipoAnaliseContabil.ORCAMENTARIO.equals(tipo.getTipoAnaliseContabil())) return;

        tipo.setMensagemBarraProcesso("Gerando saldo orçamentários ");

        tipo.setMensagemBarraProcesso("Total de despesas orcamentárias: " + assistente.getGerarSaldoOrcamentarios().size());
        tipo.zerarContadoresProcesso();
        tipo.setTotal(assistente.getGerarSaldoOrcamentarios().size());
        for (AnaliseGerarSaldoOrcamentario analiseGerarSaldoOrcamentario : assistente.getGerarSaldoOrcamentarios()) {
            for (AnaliseGerarSaldoOrcamentarioFonte analiseFonte : analiseGerarSaldoOrcamentario.getFontes()) {
                tipo.setMensagemBarraProcesso("Gerando dados da despesa : " + analiseFonte.getFonte().toString());
                try {
                    for (AnaliseGerarSaldoOrcamentario gerarSaldoOrcamentario : assistente.getGerarSaldoOrcamentarios()) {
                        Date data = gerarSaldoOrcamentario.getData();
                        if (data == null) {
                            LocalDate startDate = DataUtil.dateToLocalDate(assistente.getDataInicial());
                            long start = startDate.toEpochDay();

                            LocalDate endDate = DataUtil.dateToLocalDate(assistente.getDataFinal());
                            long end = endDate.toEpochDay();

                            long randomEpochDay = ThreadLocalRandom.current().longs(start, end).findAny().getAsLong();
                            data = DataUtil.localDateToDate(LocalDate.ofEpochDay(randomEpochDay));
                        }

                        String indice = assistente.getGerarSaldoOrcamentarios().indexOf(gerarSaldoOrcamentario) + "";
                        SaldoFonteDespesaORCVO vo = new SaldoFonteDespesaORCVO(
                            analiseFonte.getFonte(),
                            gerarSaldoOrcamentario.getOperacaoOrc(),
                            gerarSaldoOrcamentario.getTipoCredito(),
                            gerarSaldoOrcamentario.getValor(),
                            data,
                            gerarSaldoOrcamentario.getUnidade(),
                            indice,
                            gerarSaldoOrcamentario.getClass().getSimpleName(),
                            indice,
                            ""
                        );
                        saldoFonteDespesaORCFacade.gerarSaldoOrcamentario(vo);
                        analiseFonte.getSaldos().add(vo);
                    }
                } catch (ExcecaoNegocioGenerica ex) {
                    tipo.setMensagemBarraProcesso("Erro ao gerar saldo orcamentaros na fonte de despesa: Detalhes: " + ex.getMessage().toString());
                } catch (Exception ex) {
                    tipo.setMensagemBarraProcesso("Erro ao gerar saldo orcamentaros na fonte de despesa: Detalhes:  " + ex.getMessage());
                }
            }
            tipo.contar(1);
        }
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public AssistenteAnaliseContabil analiseContabil(AssistenteAnaliseContabil assistente, TipoAssistenteAnaliseContabil tipo) {
        AnaliseMovimentoContabil analise = assistente.getAnalise();
        if (analise != null) {
            analise.setSituacao(SituacaoMovimentoContabil.PROCESSAMENTO);
            analise = salvarAnaliseContabil(analise);
        }
        validarSaldoOrcamentario(assistente, tipo);
        validarSaldoFinanceiro(assistente, tipo);
        validarSaldoContabil(assistente, tipo);
        validarSaldoContabilSiconfi(assistente, tipo);
        validarSaldoExtraOrcamentario(assistente, tipo);
        validarSaldoDividaPublica(assistente, tipo);
        validarSaldoGrupoBemMoveis(assistente, tipo);
        validarSaldoGrupoBemImoveis(assistente, tipo);
        validarSaldoGrupoBemEstoque(assistente, tipo);
        return assistente;
    }

    private void validarSaldoGrupoBemEstoque(AssistenteAnaliseContabil assistente, TipoAssistenteAnaliseContabil tipo) {
        if (!TipoAnaliseContabil.BEM_ESTOQUE.equals(tipo.getTipoAnaliseContabil())) return;
        assistente.setGrupoBemEstoque(Lists.newArrayList());


        String filtros = "";
        List<ReprocessamentoSaldoGrupoBens> reprocessamentos = reprocessamentoSaldoGrupoBemFacade.getBensEstoque(assistente.getDataInicial(), assistente.getDataFinal(), filtros);

        List<AnaliseGrupoBemEstoque> itens = Lists.newArrayList();
        for (ReprocessamentoSaldoGrupoBens reprocessamento : reprocessamentos) {
            if (assistente.getHierarquiaOrganizacional() != null) {
                if (!assistente.getHierarquiaOrganizacional().getSubordinada().getId().equals(reprocessamento.getUnidadeOrganizacional().getId())) {
                    continue;
                }
            }
            Optional<AnaliseGrupoBemEstoque> first = itens.stream().filter(f ->
                f.getUnidadeOrganizacional().equals(reprocessamento.getUnidadeOrganizacional()) &&
                    f.getGrupoMaterial().equals(reprocessamento.getGrupoMaterial())
            ).findFirst();

            if (first.isPresent()) {
                if (reprocessamento.getTipoOperacaoBensEstoque().equals(TipoOperacaoBensEstoque.AQUISICAO_BENS_ESTOQUE)) {
                    first.get().setTotalMovimentoDebito(first.get().getTotalMovimentoDebito().add(reprocessamento.getValor()));
                } else {
                    first.get().setTotalMovimentoCredito(first.get().getTotalMovimentoCredito().add(reprocessamento.getValor()));
                }
                first.get().getMovimentos().add(reprocessamento);
            } else {
                itens.add(new AnaliseGrupoBemEstoque(reprocessamento));
            }
        }

        Integer total = itens.size();
        tipo.setMensagemBarraProcesso("Total de lancamentos : " + total);
        tipo.setTotal(total);
        for (AnaliseGrupoBemEstoque item : itens) {
            try {
                GrupoMaterial grupoMaterial = item.getGrupoMaterial();
                TipoEstoque tipoGrupo = item.getTipoEstoque();
                UnidadeOrganizacional unidadeOrganizacional = item.getUnidadeOrganizacional();
                TipoOperacaoBensEstoque tipoOperacaoBensEstoque = item.getTipoOperacaoBensEstoque();
                String descricao = grupoMaterial.toString()
                    + " unidade " + unidadeOrganizacional
                    + " tipo " + tipoOperacaoBensEstoque.getDescricao();
                tipo.setMensagemBarraProcesso("Recuperando dados do grupo : " + descricao);

                Date dataInicial = assistente.getDataInicial();
                Date dataFinal = assistente.getDataFinal();


                TipoOperacao tipoOperacao = TipoOperacao.DEBITO;
                if (!item.getTipoOperacaoBensEstoque().equals(TipoOperacaoBensEstoque.AQUISICAO_BENS_ESTOQUE)) {
                    tipoOperacao = TipoOperacao.CREDITO;
                }
                NaturezaTipoGrupoMaterial naturezaTipoGrupoBem = saldoGrupoMaterialFacade.recuperarNaturezaGrupoMaterial(tipoOperacaoBensEstoque, tipoOperacao);
                SaldoGrupoMaterial saldoAnterior = saldoGrupoMaterialFacade.buscarUltimoSaldoPorData(grupoMaterial, unidadeOrganizacional, tipoGrupo, naturezaTipoGrupoBem, DataUtil.removerDias(dataInicial, 1));

                if (saldoAnterior != null) {
                    item.setSaldoAnterior(saldoAnterior.getCredito().subtract(saldoAnterior.getDebito()));
                } else {
                    item.setSaldoAnterior(BigDecimal.ZERO);
                }


                SaldoGrupoMaterial saldoAtual = saldoGrupoMaterialFacade.buscarUltimoSaldoPorData(grupoMaterial, unidadeOrganizacional, tipoGrupo, naturezaTipoGrupoBem, dataFinal);
                if (saldoAtual != null) {
                    item.setSaldoUltimo(saldoAtual.getCredito().subtract(saldoAtual.getDebito()));
                } else {
                    item.setSaldoUltimo(BigDecimal.ZERO);
                }

                if (assistente.getMostrarSomenteInsconsistencia()
                    && item.getDiferenca().doubleValue() != 0) {
                    assistente.getGrupoBemEstoque().add(item);
                }

                if (!assistente.getMostrarSomenteInsconsistencia()) {
                    assistente.getGrupoBemEstoque().add(item);
                }
            } catch (Exception ex) {
                adicionarLog(tipo, "Erro ao recuperar dados grupo bens estoque: " + ex.getMessage());
            }
            tipo.contar(1);
        }

        Collections.sort(assistente.getGrupoBemEstoque(), new Comparator<AnaliseGrupoBemEstoque>() {
            @Override
            public int compare(AnaliseGrupoBemEstoque o1, AnaliseGrupoBemEstoque o2) {
                return o1.getGrupoMaterial().getCodigo().compareTo(o2.getGrupoMaterial().getCodigo());
            }
        });
    }


    private TipoOperacao getTipoOperacao(TipoLancamento tipoLancamento, String descricao) {
        TipoOperacao tipoOperacao = tipoLancamento != null ? TipoLancamento.NORMAL.equals(tipoLancamento) ? TipoOperacao.CREDITO : TipoOperacao.DEBITO : null;
        if (tipoOperacao == null) {
            throw new ExcecaoNegocioGenerica("Tipo de operação vazia para " + descricao);
        }
        return tipoOperacao;
    }

    private void validarSaldoGrupoBemImoveis(AssistenteAnaliseContabil assistente, TipoAssistenteAnaliseContabil tipo) {
        if (!TipoAnaliseContabil.BEM_IMOVEL.equals(tipo.getTipoAnaliseContabil())) return;
        assistente.setGrupoBemImovel(Lists.newArrayList());


        String filtros = "";
        List<ReprocessamentoSaldoGrupoBens> reprocessamentos = reprocessamentoSaldoGrupoBemFacade.getBensImoveis(assistente.getDataInicial(), assistente.getDataFinal(), filtros);

        List<AnaliseGrupoBemImovel> itens = Lists.newArrayList();
        for (ReprocessamentoSaldoGrupoBens reprocessamento : reprocessamentos) {
            if (assistente.getHierarquiaOrganizacional() != null) {
                if (!assistente.getHierarquiaOrganizacional().getSubordinada().getId().equals(reprocessamento.getUnidadeOrganizacional().getId())) {
                    continue;
                }
            }
            Optional<AnaliseGrupoBemImovel> first = itens.stream().filter(f ->
                f.getUnidadeOrganizacional().equals(reprocessamento.getUnidadeOrganizacional()) &&
                    f.getGrupoBem().equals(reprocessamento.getGrupoBem()) &&
                    f.getTipoOperacaoBensImoveis().equals(reprocessamento.getTipoOperacaoBensImoveis())
            ).findFirst();

            if (first.isPresent()) {
                if (reprocessamento.getTipoOperacaoBensImoveis().equals(TipoOperacaoBensImoveis.AQUISICAO_BENS_IMOVEIS)) {
                    first.get().setTotalMovimentoDebito(first.get().getTotalMovimentoDebito().add(reprocessamento.getValor()));
                } else {
                    first.get().setTotalMovimentoCredito(first.get().getTotalMovimentoCredito().add(reprocessamento.getValor()));
                }
                first.get().getMovimentos().add(reprocessamento);
            } else {
                itens.add(new AnaliseGrupoBemImovel(reprocessamento));
            }
        }

        Integer total = itens.size();
        tipo.setMensagemBarraProcesso("Total de lancamentos : " + total);
        tipo.setTotal(total);
        for (AnaliseGrupoBemImovel item : itens) {
            try {
                GrupoBem grupoBem = item.getGrupoBem();
                TipoGrupo tipoGrupo = item.getTipoGrupo();
                UnidadeOrganizacional unidadeOrganizacional = item.getUnidadeOrganizacional();
                TipoOperacaoBensImoveis tipoOperacaoBensImoveis = item.getTipoOperacaoBensImoveis();
                String descricao = grupoBem.toString()
                    + " unidade " + unidadeOrganizacional
                    + " tipo " + tipoOperacaoBensImoveis.getDescricao();
                tipo.setMensagemBarraProcesso("Recuperando dados do grupo : " + descricao);

                Date dataInicial = assistente.getDataInicial();
                Date dataFinal = assistente.getDataFinal();


                TipoOperacao tipoOperacao = getTipoOperacao(item.getTipoLancamento(), descricao);
                if (!item.getTipoOperacaoBensImoveis().equals(TipoOperacaoBensImoveis.AQUISICAO_BENS_IMOVEIS)) {
                    tipoOperacao = TipoOperacao.CREDITO;
                }
                NaturezaTipoGrupoBem naturezaTipoGrupoBem = saldoGrupoBemImovelFacade.recuperarNaturezaGrupoBem(tipoOperacaoBensImoveis, tipoOperacao);
                try {
                    SaldoGrupoBemImoveis saldoAnterior = saldoGrupoBemImovelFacade.buscarUltimoSaldoPorData(grupoBem, unidadeOrganizacional, tipoGrupo, naturezaTipoGrupoBem, DataUtil.removerDias(dataInicial, 1));

                    if (saldoAnterior != null) {
                        item.setSaldoAnterior(saldoAnterior.getCredito().subtract(saldoAnterior.getDebito()));
                    } else {
                        item.setSaldoAnterior(BigDecimal.ZERO);
                    }
                } catch (ExcecaoNegocioGenerica ex) {
                    item.setSaldoAnterior(BigDecimal.ZERO);
                }


                try {
                    SaldoGrupoBemImoveis saldoAtual = saldoGrupoBemImovelFacade.buscarUltimoSaldoPorData(grupoBem, unidadeOrganizacional, tipoGrupo, naturezaTipoGrupoBem, dataFinal);
                    if (saldoAtual != null) {
                        item.setSaldoUltimo(saldoAtual.getCredito().subtract(saldoAtual.getDebito()));
                    } else {
                        item.setSaldoUltimo(BigDecimal.ZERO);
                    }
                } catch (ExcecaoNegocioGenerica ex) {
                    item.setSaldoUltimo(BigDecimal.ZERO);
                }


                if (assistente.getMostrarSomenteInsconsistencia()
                    && item.getDiferenca().doubleValue() != 0) {
                    assistente.getGrupoBemImovel().add(item);
                }

                if (!assistente.getMostrarSomenteInsconsistencia()) {
                    assistente.getGrupoBemImovel().add(item);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                adicionarLog(tipo, "Erro ao recuperar dados grupo bens imóveis: " + ex.getMessage());
            }
            tipo.contar(1);
        }

        Collections.sort(assistente.getGrupoBemImovel(), new Comparator<AnaliseGrupoBemImovel>() {
            @Override
            public int compare(AnaliseGrupoBemImovel o1, AnaliseGrupoBemImovel o2) {
                return o1.getGrupoBem().getCodigo().compareTo(o2.getGrupoBem().getCodigo());
            }
        });
    }

    private void validarSaldoGrupoBemMoveis(AssistenteAnaliseContabil assistente, TipoAssistenteAnaliseContabil tipo) {
        if (!TipoAnaliseContabil.BEM_MOVEL.equals(tipo.getTipoAnaliseContabil())) return;
        assistente.setGrupoBemMovel(Lists.newArrayList());


        String filtros = "";
        List<ReprocessamentoSaldoGrupoBens> reprocessamentos = reprocessamentoSaldoGrupoBemFacade.getBensMoveis(assistente.getDataInicial(), assistente.getDataFinal(), filtros);

        List<AnaliseGrupoBemMovel> itens = Lists.newArrayList();
        for (ReprocessamentoSaldoGrupoBens reprocessamento : reprocessamentos) {
            if (assistente.getHierarquiaOrganizacional() != null) {
                if (!assistente.getHierarquiaOrganizacional().getSubordinada().getId().equals(reprocessamento.getUnidadeOrganizacional().getId())) {
                    continue;
                }
            }

            TipoOperacao tipoOperacao = null;
            if (!TipoOperacaoBensMoveis.AQUISICAO_BENS_MOVEIS.equals(reprocessamento.getTipoOperacaoBensMoveis())) {
                if (TipoOperacaoBensMoveis.getOperacoesParaUnicoSaldoDebito().contains(reprocessamento.getTipoOperacaoBensMoveis())) {
                    tipoOperacao = TipoOperacao.DEBITO;
                } else if (TipoOperacaoBensMoveis.getOperacoesParaUnicoSaldoCredito().contains(reprocessamento.getTipoOperacaoBensMoveis())) {
                    tipoOperacao = TipoOperacao.CREDITO;
                } else {
                    tipoOperacao = TipoOperacao.CREDITO;
                    if (TipoOperacaoBensMoveis.ALIENACAO_BENS_MOVEIS.equals(reprocessamento.getTipoOperacaoBensMoveis())) {
                        if (!reprocessamento.isReceitaRealizada()) {
                            tipoOperacao = TipoOperacao.DEBITO;
                        }
                    } else {
                        tipoOperacao = TipoOperacao.DEBITO;
                    }
                }
            } else if (TipoOperacaoBensMoveis.getOperacoesBensOriginalDebito().contains(reprocessamento.getTipoOperacaoBensMoveis())) {
                tipoOperacao = TipoOperacao.DEBITO;
            }
            NaturezaTipoGrupoBem naturezaTipoGrupoBem = saldoGrupoBemMovelFacade.recuperarNaturezaGrupoBem(reprocessamento.getTipoOperacaoBensMoveis(), tipoOperacao);

            Optional<AnaliseGrupoBemMovel> first = itens.stream().filter(f ->
                f.getUnidadeOrganizacional().equals(reprocessamento.getUnidadeOrganizacional()) &&
                    f.getGrupoBem().equals(reprocessamento.getGrupoBem())&&
                    f.getNatureza().equals(naturezaTipoGrupoBem)

            ).findFirst();

            if (first.isPresent()) {
                if (reprocessamento.getTipoOperacaoBensMoveis().equals(TipoOperacaoBensMoveis.AQUISICAO_BENS_MOVEIS)) {
                    first.get().setTotalMovimentoDebito(first.get().getTotalMovimentoDebito().add(reprocessamento.getValor()));
                } else {
                    first.get().setTotalMovimentoCredito(first.get().getTotalMovimentoCredito().add(reprocessamento.getValor()));
                }
                first.get().getMovimentos().add(reprocessamento);
            } else {
                AnaliseGrupoBemMovel analiseGrupoBemMovel = new AnaliseGrupoBemMovel(reprocessamento);
                analiseGrupoBemMovel.setNatureza(naturezaTipoGrupoBem);
                itens.add(analiseGrupoBemMovel);
            }
        }

        Integer total = itens.size();
        tipo.setMensagemBarraProcesso("Total de lancamentos : " + total);
        tipo.setTotal(total);
        for (AnaliseGrupoBemMovel item : itens) {
            try {
                GrupoBem grupoBem = item.getGrupoBem();
                TipoGrupo tipoGrupo = item.getTipoGrupo();
                UnidadeOrganizacional unidadeOrganizacional = item.getUnidadeOrganizacional();
                TipoOperacaoBensMoveis tipoOperacaoBensMoveis = item.getTipoOperacaoBensMoveis();
                String descricao = grupoBem.toString()
                    + " unidade " + unidadeOrganizacional
                    + " tipo " + tipoOperacaoBensMoveis.getDescricao();
                tipo.setMensagemBarraProcesso("Recuperando dados do grupo : " + descricao);

                Date dataInicial = assistente.getDataInicial();
                Date dataFinal = assistente.getDataFinal();



                SaldoGrupoBem saldoAnterior = saldoGrupoBemMovelFacade.recuperaUltimoSaldoPorData(grupoBem, unidadeOrganizacional, tipoGrupo, item.getNatureza(), DataUtil.removerDias(dataInicial, 1));

                if (saldoAnterior != null) {
                    item.setSaldoAnterior(saldoAnterior.getCredito().subtract(saldoAnterior.getDebito()));
                } else {
                    item.setSaldoAnterior(BigDecimal.ZERO);
                }


                SaldoGrupoBem saldoAtual = saldoGrupoBemMovelFacade.recuperaUltimoSaldoPorData(grupoBem, unidadeOrganizacional, tipoGrupo, item.getNatureza(), dataFinal);
                if (saldoAtual != null) {
                    item.setSaldoUltimo(saldoAtual.getCredito().subtract(saldoAtual.getDebito()));
                } else {
                    item.setSaldoUltimo(BigDecimal.ZERO);
                }

                if (assistente.getMostrarSomenteInsconsistencia()
                    && item.getDiferenca().doubleValue() != 0) {
                    assistente.getGrupoBemMovel().add(item);
                }

                if (!assistente.getMostrarSomenteInsconsistencia()) {
                    assistente.getGrupoBemMovel().add(item);
                }
            } catch (Exception ex) {
                adicionarLog(tipo, "Erro ao recuperar dados grupo bens móveis: " + ex.getMessage());
            }
            tipo.contar(1);
        }

        Collections.sort(assistente.getGrupoBemMovel(), new Comparator<AnaliseGrupoBemMovel>() {
            @Override
            public int compare(AnaliseGrupoBemMovel o1, AnaliseGrupoBemMovel o2) {
                return o1.getGrupoBem().getCodigo().compareTo(o2.getGrupoBem().getCodigo());
            }
        });
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public AssistenteAnaliseContabil salvarHashMovimento(AssistenteAnaliseContabil
                                                             assistente, TipoAssistenteAnaliseContabil tipo) {
        tipo.setMensagemBarraProcesso("Salvando hash movimentos contábeis do saldo " + tipo.getTipoAnaliseContabil().getDescricao());
        tipo.zerarContadoresProcesso();
        if (TipoAnaliseContabil.ORCAMENTARIO.equals(tipo.getTipoAnaliseContabil())) {
            tipo.setTotal(assistente.getOrcamentario().size());
            for (ExtratoMovimentoDespesaORC extratoMovimentoDespesaORC : assistente.getOrcamentario()) {
                MovimentoHashContabil movimento = new MovimentoHashContabil().toOrcamentario(extratoMovimentoDespesaORC.getFonteDespesaORC(), assistente.getDataFinal(), extratoMovimentoDespesaORC.getSaldoAtual());
                try {
                    movimento = jdbcMovimentoHashContabil.salvar(movimento);
                    assistente.getMovimentos().add(movimento);
                } catch (Exception ex) {
                    adicionarLog(tipo, "Erro ao salvar hash movimentos contábeis do tipo " + tipo.getTipoAnaliseContabil().getDescricao() + ": " + ex.getMessage());
                }
                tipo.contar(1);
            }
        }
        if (TipoAnaliseContabil.FINANCEIRO.equals(tipo.getTipoAnaliseContabil())) {
            tipo.setTotal(assistente.getFinanceiro().size());
            for (AnaliseContaFinanceira analise : assistente.getFinanceiro()) {
                HierarquiaOrganizacional hierarquiaDaUnidade = hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), analise.getUnidadeOrganizacional(), assistente.getDataFinal());
                MovimentoHashContabil movimento = new MovimentoHashContabil().toFinanceiro(hierarquiaDaUnidade, analise.getSubConta(), analise.getContaDeDestinacao().getFonteDeRecursos(), assistente.getDataFinal(), analise.getTotalMovimentoAtual());
                try {
                    movimento = jdbcMovimentoHashContabil.salvar(movimento);
                    assistente.getMovimentos().add(movimento);
                } catch (Exception ex) {
                    adicionarLog(tipo, "Erro ao salvar hash movimentos contábeis do " + tipo.getTipoAnaliseContabil().getDescricao() + ": " + ex.getMessage());
                }
                tipo.contar(1);
            }
        }
        return assistente;
    }

    private void validarSaldoDividaPublica(AssistenteAnaliseContabil assistente, TipoAssistenteAnaliseContabil tipo) {
        if (!TipoAnaliseContabil.DIVIDAPUBLICA.equals(tipo.getTipoAnaliseContabil())) return;
        assistente.setDividaPublica(Lists.newArrayList());

        AssistenteReprocessamento selecionado = new AssistenteReprocessamento();
        selecionado.setQueryReprocessamento(getQueryReprocessamentoService().getReprocessamentoFinanceiro());
        selecionado.setDataInicial(assistente.getDataInicial());
        selecionado.setDataFinal(assistente.getDataFinal());
        selecionado.setExercicio(tipo.getExercicio());
        selecionado.getAssistenteBarraProgresso().inicializa();
        selecionado.inicializarHistorico(TipoReprocessamentoHistorico.SALDO_DIVIDA_PUBLICA, tipo.getUsuarioSistema());
        selecionado.setQueryReprocessamento(QueryReprocessamentoService.getService().getReprocessamentoDividaPublica());
        selecionado.getAssistenteBarraProgresso().inicializa();
        if (!selecionado.getQueryReprocessamento().isEmpty()) {
            List<SaldoDividaPublicaReprocessamento> reprocessamentos = reprocessamentoSaldoDividaPublicaFacade.recuperarItens(selecionado);

            List<AnaliseDividaPublica> itens = Lists.newArrayList();
            for (SaldoDividaPublicaReprocessamento reprocessamento : reprocessamentos) {
                if (OperacaoMovimentoDividaPublica.EMPENHO.equals(reprocessamento.getOperacaoMovimentoDividaPublica())) {
                    continue;
                }
                Optional<AnaliseDividaPublica> first = itens.stream().filter(f ->
                    f.getUnidadeOrganizacional().equals(reprocessamento.getUnidadeOrganizacional()) &&
                        f.getDividapublica().equals(reprocessamento.getDividaPublica()) &&
                        f.getContaDeDestinacao().equals(reprocessamento.getContaDeDestinacao())
                ).findFirst();

                if (first.isPresent()) {
                    if (OperacaoMovimentoDividaPublica.getTiposOperacaoSomar().contains(reprocessamento.getOperacaoMovimentoDividaPublica())) {
                        first.get().setTotalMovimentoCredito(first.get().getTotalMovimentoCredito().add(reprocessamento.getValor()));
                    } else {
                        first.get().setTotalMovimentoDebito(first.get().getTotalMovimentoDebito().add(reprocessamento.getValor()));
                    }
                    first.get().getMovimentos().add(reprocessamento);
                } else {
                    itens.add(new AnaliseDividaPublica(reprocessamento));
                }
            }

            Integer total = itens.size();
            tipo.setMensagemBarraProcesso("Total de lancamentos : " + total);
            tipo.setTotal(total);
            for (AnaliseDividaPublica item : itens) {
                try {
                    DividaPublica dividapublica = item.getDividapublica();
                    UnidadeOrganizacional unidadeOrganizacional = item.getUnidadeOrganizacional();
                    ContaDeDestinacao contaDeDestinacao = item.getContaDeDestinacao();
                    tipo.setMensagemBarraProcesso("Recuperando dados da divida : " + dividapublica.toString()
                        + " unidade " + unidadeOrganizacional
                        + " fonte " + contaDeDestinacao.getFonteDeRecursos().toString());

                    Date dataInicial = assistente.getDataInicial();
                    Date dataFinal = assistente.getDataFinal();

                    item.setSaldoAnterior(BigDecimal.ZERO);
                    SaldoDividaPublica saldoAnteriorCurto = saldoDividaPublicaFacade.recuperarSaldoPorDividaPublicaAndDataAndUnidadeOrganizacionalAndIntervaloAndContaDeDestinacao(DataUtil.removerDias(dataInicial, 1), item.getUnidadeOrganizacional(), item.getDividapublica(), Intervalo.CURTO_PRAZO, item.getContaDeDestinacao());
                    SaldoDividaPublica saldoAnteriorLongo = saldoDividaPublicaFacade.recuperarSaldoPorDividaPublicaAndDataAndUnidadeOrganizacionalAndIntervaloAndContaDeDestinacao(DataUtil.removerDias(dataInicial, 1), item.getUnidadeOrganizacional(), item.getDividapublica(), Intervalo.LONGO_PRAZO, item.getContaDeDestinacao());
                    if (saldoAnteriorCurto != null) {
                        item.setSaldoAnterior(item.getSaldoAnterior().add(saldoAnteriorCurto.getSaldoReal()));
                    }
                    if (saldoAnteriorLongo != null) {
                        item.setSaldoAnterior(item.getSaldoAnterior().add(saldoAnteriorLongo.getSaldoReal()));
                    }

                    item.setSaldoUltimo(BigDecimal.ZERO);
                    SaldoDividaPublica saldoAtualCurto = saldoDividaPublicaFacade.recuperarSaldoPorDividaPublicaAndDataAndUnidadeOrganizacionalAndIntervaloAndContaDeDestinacao(dataFinal, item.getUnidadeOrganizacional(), item.getDividapublica(), Intervalo.CURTO_PRAZO, item.getContaDeDestinacao());
                    SaldoDividaPublica saldoAtualLongo = saldoDividaPublicaFacade.recuperarSaldoPorDividaPublicaAndDataAndUnidadeOrganizacionalAndIntervaloAndContaDeDestinacao(dataFinal, item.getUnidadeOrganizacional(), item.getDividapublica(), Intervalo.LONGO_PRAZO, item.getContaDeDestinacao());
                    if (saldoAtualCurto != null) {
                        item.setSaldoUltimo(item.getSaldoUltimo().add(saldoAtualCurto.getSaldoReal()));
                    }
                    if (saldoAtualLongo != null) {
                        item.setSaldoUltimo(item.getSaldoUltimo().add(saldoAtualLongo.getSaldoReal()));
                    }

                    if (assistente.getMostrarSomenteInsconsistencia()
                        && item.getDiferenca().doubleValue() != 0) {
                        assistente.getDividaPublica().add(item);
                    }

                    if (!assistente.getMostrarSomenteInsconsistencia()) {
                        assistente.getDividaPublica().add(item);
                    }
                } catch (Exception ex) {
                    adicionarLog(tipo, "Erro ao recuperar dados divida publica: " + ex.getMessage());
                }
                tipo.contar(1);
            }

            Collections.sort(assistente.getDividaPublica(), new Comparator<AnaliseDividaPublica>() {
                @Override
                public int compare(AnaliseDividaPublica o1, AnaliseDividaPublica o2) {
                    try {
                        return Integer.valueOf(o1.getDividapublica().getNumero()).compareTo(Integer.valueOf(o2.getDividapublica().getNumero()));
                    } catch (Exception e) {
                        return o1.getDividapublica().toString().compareTo(o2.getDividapublica().toString());
                    }
                }
            });
        }
    }

    private void validarSaldoExtraOrcamentario(AssistenteAnaliseContabil assistente, TipoAssistenteAnaliseContabil
        tipo) {
        if (!TipoAnaliseContabil.EXTRAORCAMENTARIO.equals(tipo.getTipoAnaliseContabil())) return;

        assistente.setExtraOrcamentario(Lists.newArrayList());

        AssistenteReprocessamento selecionado = new AssistenteReprocessamento();
        selecionado.setQueryReprocessamento(getQueryReprocessamentoService().getReprocessamentoFinanceiro());
        selecionado.setDataInicial(assistente.getDataInicial());
        selecionado.setDataFinal(assistente.getDataFinal());
        selecionado.setExercicio(tipo.getExercicio());
        selecionado.getAssistenteBarraProgresso().inicializa();
        selecionado.setReprocessamentoSaldoExtraOrcamentario(new ReprocessamentoSaldoExtraOrcamentario());
        selecionado.inicializarHistorico(TipoReprocessamentoHistorico.SALDO_EXTRAORCAMENTARIO, tipo.getUsuarioSistema());

        if (!selecionado.getReprocessamentoSaldoExtraOrcamentario().getQueryPagamentoEstorno().isEmpty() &&
            !selecionado.getReprocessamentoSaldoExtraOrcamentario().getQueryPagamento().isEmpty() &&
            !selecionado.getReprocessamentoSaldoExtraOrcamentario().getQueryReceitaEstorno().isEmpty() &&
            !selecionado.getReprocessamentoSaldoExtraOrcamentario().getQueryReceita().isEmpty() &&
            !selecionado.getReprocessamentoSaldoExtraOrcamentario().getQueryAjuste().isEmpty() &&
            !selecionado.getReprocessamentoSaldoExtraOrcamentario().getQueryAjusteEstorno().isEmpty()) {
        }

        tipo.setMensagemBarraProcesso("Buscando dados extra orçamentários ");
        tipo.zerarContadoresProcesso();
        reprocessamentoSaldoExtraOrcamentarioFacade.recuperarMovimentos(selecionado);


        tipo.setMensagemBarraProcesso("Organizando movimentos recuperados ");
        List<AnaliseContaExtra> itens = Lists.newArrayList();
        selecionado.getReprocessamentoSaldoExtraOrcamentario().getReceitas().forEach(receita -> {
            BigDecimal valorCredito = receita.getValor();
            BigDecimal valorDebito = BigDecimal.ZERO;
            Conta contaExtraorcamentaria = receita.getContaExtraorcamentaria();
            UnidadeOrganizacional unidadeOrganizacional = receita.getUnidadeOrganizacional();
            ContaDeDestinacao contaDeDestinacao = receita.getContaDeDestinacao();
            adicionarAnaliseContaExtra(itens, contaExtraorcamentaria, unidadeOrganizacional, contaDeDestinacao, valorCredito, valorDebito);
        });
        selecionado.getReprocessamentoSaldoExtraOrcamentario().getReceitasEstornos().forEach(receita -> {
            BigDecimal valorCredito = BigDecimal.ZERO;
            BigDecimal valorDebito = receita.getValor();
            Conta contaExtraorcamentaria = receita.getReceitaExtra().getContaExtraorcamentaria();
            UnidadeOrganizacional unidadeOrganizacional = receita.getUnidadeOrganizacional();
            ContaDeDestinacao contaDeDestinacao = receita.getReceitaExtra().getContaDeDestinacao();
            adicionarAnaliseContaExtra(itens, contaExtraorcamentaria, unidadeOrganizacional, contaDeDestinacao, valorCredito, valorDebito);
        });
        selecionado.getReprocessamentoSaldoExtraOrcamentario().getDespesa().forEach(receita -> {
            BigDecimal valorCredito = BigDecimal.ZERO;
            BigDecimal valorDebito = receita.getValor();
            Conta contaExtraorcamentaria = receita.getContaExtraorcamentaria();
            UnidadeOrganizacional unidadeOrganizacional = receita.getUnidadeOrganizacional();
            ContaDeDestinacao contaDeDestinacao = receita.getContaDeDestinacao();
            adicionarAnaliseContaExtra(itens, contaExtraorcamentaria, unidadeOrganizacional, contaDeDestinacao, valorCredito, valorDebito);
        });
        selecionado.getReprocessamentoSaldoExtraOrcamentario().getDespesaEstorno().forEach(receita -> {
            BigDecimal valorCredito = receita.getValor();
            BigDecimal valorDebito = BigDecimal.ZERO;
            Conta contaExtraorcamentaria = receita.getPagamentoExtra().getContaExtraorcamentaria();
            UnidadeOrganizacional unidadeOrganizacional = receita.getUnidadeOrganizacional();
            ContaDeDestinacao contaDeDestinacao = receita.getPagamentoExtra().getContaDeDestinacao();
            adicionarAnaliseContaExtra(itens, contaExtraorcamentaria, unidadeOrganizacional, contaDeDestinacao, valorCredito, valorDebito);
        });

        selecionado.getReprocessamentoSaldoExtraOrcamentario().getAjusteDeposito().forEach(receita -> {
            BigDecimal valorCredito = receita.getValor();
            BigDecimal valorDebito = BigDecimal.ZERO;
            Conta contaExtraorcamentaria = receita.getContaExtraorcamentaria();
            UnidadeOrganizacional unidadeOrganizacional = receita.getUnidadeOrganizacional();
            ContaDeDestinacao contaDeDestinacao = receita.getContaDeDestinacao();
            adicionarAnaliseContaExtra(itens, contaExtraorcamentaria, unidadeOrganizacional, contaDeDestinacao, valorCredito, valorDebito);
        });
        selecionado.getReprocessamentoSaldoExtraOrcamentario().getAjusteDepositoEstorno().forEach(receita -> {
            BigDecimal valorCredito = BigDecimal.ZERO;
            BigDecimal valorDebito = receita.getValor();
            Conta contaExtraorcamentaria = receita.getAjusteDeposito().getContaExtraorcamentaria();
            UnidadeOrganizacional unidadeOrganizacional = receita.getUnidadeOrganizacional();
            ContaDeDestinacao contaDeDestinacao = receita.getAjusteDeposito().getContaDeDestinacao();
            adicionarAnaliseContaExtra(itens, contaExtraorcamentaria, unidadeOrganizacional, contaDeDestinacao, valorCredito, valorDebito);
        });

        Integer total = itens.size();
        tipo.setMensagemBarraProcesso("Total de lancamentos : " + total);
        tipo.setTotal(total);
        for (AnaliseContaExtra item : itens) {

            try {

                Conta contaExtraorcamentaria = item.getContaExtraorcamentaria();
                UnidadeOrganizacional unidadeOrganizacional = item.getUnidadeOrganizacional();
                ContaDeDestinacao contaDeDestinacao = item.getContaDeDestinacao();
                tipo.setMensagemBarraProcesso("Recuperando dados da conta : " + contaExtraorcamentaria.toString()
                    + " unidade " + unidadeOrganizacional
                    + " fonte " + contaDeDestinacao.getFonteDeRecursos().toString());

                Date dataInicial = assistente.getDataInicial();
                Date dataFinal = assistente.getDataFinal();

                SaldoExtraorcamentario saldoAnterior = saldoExtraorcamentarioFacade.recuperaUltimoSaldoPorData(DataUtil.removerDias(dataInicial, 1), item.getContaExtraorcamentaria(), item.getContaDeDestinacao(), item.getUnidadeOrganizacional());
                if (saldoAnterior != null) {
                    item.setSaldoAnterior(saldoAnterior);
                }

                SaldoExtraorcamentario saldoAtual = saldoExtraorcamentarioFacade.recuperaUltimoSaldoPorData(dataFinal, item.getContaExtraorcamentaria(), item.getContaDeDestinacao(), item.getUnidadeOrganizacional());
                if (saldoAtual != null) {
                    item.setSaldoUltimo(saldoAtual);
                }

                if (assistente.getMostrarSomenteInsconsistencia()
                    && item.getDiferenca().doubleValue() != 0) {
                    assistente.getExtraOrcamentario().add(item);
                }

                if (!assistente.getMostrarSomenteInsconsistencia()) {
                    assistente.getExtraOrcamentario().add(item);
                }
            } catch (Exception ex) {
                adicionarLog(tipo, "Erro ao recuperar dados extra: " + ex.getMessage());
            }
            tipo.contar(1);
        }
    }

    private static void adicionarAnaliseContaExtra(List<AnaliseContaExtra> itens, Conta
        contaExtraorcamentaria, UnidadeOrganizacional unidadeOrganizacional, ContaDeDestinacao
                                                       contaDeDestinacao, BigDecimal valorCredito, BigDecimal valorDebito) {
        Optional<AnaliseContaExtra> first = itens.stream().filter(
            f -> f.getContaExtraorcamentaria().equals(contaExtraorcamentaria) &&
                f.getUnidadeOrganizacional().equals(unidadeOrganizacional) &&
                f.getContaDeDestinacao().equals(contaDeDestinacao)
        ).findFirst();
        if (!first.isPresent()) {
            itens.add(new AnaliseContaExtra(unidadeOrganizacional, contaExtraorcamentaria, contaDeDestinacao, valorCredito, valorDebito));
        } else {
            first.get().setTotalMovimentoCredito(first.get().getTotalMovimentoCredito().add(valorCredito));
            first.get().setTotalMovimentoDebito(first.get().getTotalMovimentoDebito().add(valorDebito));
        }
    }

    private void validarSaldoContabil(AssistenteAnaliseContabil assistente, TipoAssistenteAnaliseContabil tipo) {
        if (!TipoAnaliseContabil.CONTABIL.equals(tipo.getTipoAnaliseContabil())) return;
        assistente.setContabil(Lists.newArrayList());

        tipo.setMensagemBarraProcesso("Buscando dados do balancete contábil ");
        tipo.zerarContadoresProcesso();
        List<LancamentoContabil> lancamentoContabils = saldoContaContabilFacade.recuperLancamentosContabeis(assistente);
        tipo.setMensagemBarraProcesso("Total de lancamentos : " + lancamentoContabils.size());
        tipo.setTotal(lancamentoContabils.size());

        List<AnaliseBalanceteContabil> itens = Lists.newArrayList();
        for (LancamentoContabil lancamento : lancamentoContabils) {
            Conta contaCredito = lancamento.getContaCredito();
            Conta contaDebito = lancamento.getContaDebito();
            UnidadeOrganizacional unidadeOrganizacional = lancamento.getUnidadeOrganizacional();


            adicionarAnaliseBalanceteContabil(lancamento, itens, contaCredito, unidadeOrganizacional, PesquisaLCP.DebitoCredito.CREDITO);
            adicionarAnaliseBalanceteContabil(lancamento, itens, contaDebito, unidadeOrganizacional, PesquisaLCP.DebitoCredito.DEBITO);
        }

        Comparator<AnaliseBalanceteContabil> comparator = Comparator.comparing(AnaliseBalanceteContabil::getConta);
        itens = itens.stream().sorted(comparator).collect(Collectors.toList());

        for (AnaliseBalanceteContabil analiseBalanceteContabil : itens) {
            try {
                String descricao = analiseBalanceteContabil.getConta().toString() + " - " + analiseBalanceteContabil.getUnidadeOrganizacional().getDescricao();
                tipo.setMensagemBarraProcesso("Recuperando saldo da conta : " + descricao);
                UnidadeOrganizacional unidadeOrganizacional = analiseBalanceteContabil.getUnidadeOrganizacional();
                Conta conta = analiseBalanceteContabil.getConta();
                Date dataInicial = assistente.getDataInicial();
                Date dataFinal = assistente.getDataFinal();
                BigDecimal saldoAtual = saldoContaContabilFacade.buscarSaldoAtual(dataFinal, tipo.getExercicio(), conta, unidadeOrganizacional);
                analiseBalanceteContabil.setSaldoAtual(saldoAtual);

                BigDecimal saldoAnterior = saldoContaContabilFacade.buscarSaldoAtual(DataUtil.removerDias(dataInicial, 1), tipo.getExercicio(), conta, unidadeOrganizacional);
                analiseBalanceteContabil.setSaldoAnterior(saldoAnterior);


                if (assistente.getMostrarSomenteInsconsistencia()
                    && analiseBalanceteContabil.getDiferenca().doubleValue() != 0) {
                    assistente.getContabil().add(analiseBalanceteContabil);
                }

                if (!assistente.getMostrarSomenteInsconsistencia()) {
                    assistente.getContabil().add(analiseBalanceteContabil);
                }
            } catch (Exception ex) {
                adicionarLog(tipo, "Erro ao recuperar dados financeiro: " + ex.getMessage());
            }
            tipo.contar(1);
        }
    }

    private void adicionarAnaliseBalanceteContabilSiconfi(LancamentoContabil
                                                              lancamento, List<AnaliseBalanceteContabilSiconfi> itens, Conta contaCredito, UnidadeOrganizacional
                                                              unidadeOrganizacional, PesquisaLCP.DebitoCredito debitoCredito) {
        Optional<AnaliseBalanceteContabilSiconfi> first = itens.stream().filter(
            f -> f.getConta().getId().equals(contaCredito.getId()) &&
                f.getUnidadeOrganizacional().getId().equals(unidadeOrganizacional.getId())
        ).findFirst();
        if (first.isPresent()) {
            first.get().getLancamentos().add(lancamento);
            if (PesquisaLCP.DebitoCredito.CREDITO.equals(debitoCredito)) {
                first.get().setTotalMovimentoCredito(first.get().getTotalMovimentoCredito().add(lancamento.getValor()));
            }
            if (PesquisaLCP.DebitoCredito.DEBITO.equals(debitoCredito)) {
                first.get().setTotalMovimentoDebito(first.get().getTotalMovimentoDebito().add(lancamento.getValor()));
            }
        } else {
            itens.add(new AnaliseBalanceteContabilSiconfi(lancamento, contaCredito, debitoCredito));
        }
    }

    private void adicionarAnaliseBalanceteContabil(LancamentoContabil
                                                       lancamento, List<AnaliseBalanceteContabil> itens, Conta contaCredito, UnidadeOrganizacional
                                                       unidadeOrganizacional, PesquisaLCP.DebitoCredito debitoCredito) {
        Optional<AnaliseBalanceteContabil> first = itens.stream().filter(
            f -> f.getConta().getId().equals(contaCredito.getId()) &&
                f.getUnidadeOrganizacional().getId().equals(unidadeOrganizacional.getId())
        ).findFirst();
        if (first.isPresent()) {
            first.get().getLancamentos().add(lancamento);
            if (PesquisaLCP.DebitoCredito.CREDITO.equals(debitoCredito)) {
                first.get().setTotalMovimentoCredito(first.get().getTotalMovimentoCredito().add(lancamento.getValor()));
            }
            if (PesquisaLCP.DebitoCredito.DEBITO.equals(debitoCredito)) {
                first.get().setTotalMovimentoDebito(first.get().getTotalMovimentoDebito().add(lancamento.getValor()));
            }
        } else {
            itens.add(new AnaliseBalanceteContabil(lancamento, contaCredito, debitoCredito));
        }
    }

    private void validarSaldoContabilSiconfi(AssistenteAnaliseContabil assistente, TipoAssistenteAnaliseContabil
        tipo) {
        if (!TipoAnaliseContabil.CONTABIL_SICONFI.equals(tipo.getTipoAnaliseContabil())) return;

        assistente.setSiconfi(Lists.newArrayList());

        tipo.setMensagemBarraProcesso("Buscando dados do balancete contábil siconfi");
        tipo.zerarContadoresProcesso();
        List<LancamentoContabil> lancamentoContabils = saldoContaContabilFacade.recuperLancamentosContabeis(assistente);
        tipo.setMensagemBarraProcesso("Total de lancamentos : " + lancamentoContabils.size());
        tipo.setTotal(lancamentoContabils.size());

        List<AnaliseBalanceteContabilSiconfi> itens = Lists.newArrayList();
        for (LancamentoContabil lancamento : lancamentoContabils) {
            Conta contaCredito = lancamento.getContaCredito();
            Conta contaDebito = lancamento.getContaDebito();
            UnidadeOrganizacional unidadeOrganizacional = lancamento.getUnidadeOrganizacional();


            adicionarAnaliseBalanceteContabilSiconfi(lancamento, itens, contaCredito, unidadeOrganizacional, PesquisaLCP.DebitoCredito.CREDITO);
            adicionarAnaliseBalanceteContabilSiconfi(lancamento, itens, contaDebito, unidadeOrganizacional, PesquisaLCP.DebitoCredito.DEBITO);
        }

        Comparator<AnaliseBalanceteContabilSiconfi> comparator = Comparator.comparing(AnaliseBalanceteContabilSiconfi::getConta);
        itens = itens.stream().sorted(comparator).collect(Collectors.toList());

        for (AnaliseBalanceteContabilSiconfi analiseBalanceteContabil : itens) {
            try {
                String descricao = analiseBalanceteContabil.getConta().toString() + " - " + analiseBalanceteContabil.getUnidadeOrganizacional().getDescricao();
                tipo.setMensagemBarraProcesso("Recuperando saldo da conta : " + descricao);
                UnidadeOrganizacional unidadeOrganizacional = analiseBalanceteContabil.getUnidadeOrganizacional();
                Conta conta = analiseBalanceteContabil.getConta();
                Date dataInicial = assistente.getDataInicial();
                Date dataFinal = assistente.getDataFinal();
                BigDecimal saldoAtual = saldoContaContabilFacade.buscarSaldoSiconfiAtual(dataFinal, tipo.getExercicio(), conta, unidadeOrganizacional);
                analiseBalanceteContabil.setSaldoAtual(saldoAtual);

                BigDecimal saldoAtualContabil = saldoContaContabilFacade.buscarSaldoAtual(dataFinal, tipo.getExercicio(), conta, unidadeOrganizacional);
                analiseBalanceteContabil.setSaldoContabilAtual(saldoAtualContabil);

                BigDecimal saldoAnterior = saldoContaContabilFacade.buscarSaldoSiconfiAtual(DataUtil.removerDias(dataInicial, 1), tipo.getExercicio(), conta, unidadeOrganizacional);
                analiseBalanceteContabil.setSaldoAnterior(saldoAnterior);


                if (assistente.getMostrarSomenteInsconsistencia()
                    && analiseBalanceteContabil.getDiferenca().doubleValue() != 0) {
                    assistente.getSiconfi().add(analiseBalanceteContabil);
                }

                if (!assistente.getMostrarSomenteInsconsistencia()) {
                    assistente.getSiconfi().add(analiseBalanceteContabil);
                }
            } catch (Exception ex) {
                adicionarLog(tipo, "Erro ao recuperar dados contabil: " + ex.getMessage());
            }
            tipo.contar(1);
        }
    }

    private void validarSaldoFinanceiro(AssistenteAnaliseContabil assistente, TipoAssistenteAnaliseContabil tipo) {
        if (!TipoAnaliseContabil.FINANCEIRO.equals(tipo.getTipoAnaliseContabil())) return;
        assistente.setFinanceiro(Lists.newArrayList());

        List<AnaliseContaFinanceira> itens = Lists.newArrayList();
        try {
            tipo.setMensagemBarraProcesso("Buscando dados financeiro ");
            tipo.zerarContadoresProcesso();

            UnidadeOrganizacional subordinada = assistente.getHierarquiaOrganizacional() != null ? assistente.getHierarquiaOrganizacional().getSubordinada() : null;
            List<SubConta> subContas = subContaFacade.buscarTodasContaFinanceiraPorExericicioUnidadeComMovimentacao(tipo.getExercicio(), subordinada, assistente.getDataInicial(), assistente.getDataFinal(), assistente.getBuscarSomenteContasComMovimentacao());
            tipo.setMensagemBarraProcesso("Total de conta financeiras com movimentação: " + subContas.size());
            tipo.setTotal(subContas.size());

            for (SubConta subConta : subContas) {
                tipo.setMensagemBarraProcesso("Buscando movimentos da conta financeira: " + subConta.getCodigo());

                subConta = subContaFacade.recuperar(subConta.getId());
                for (SubContaFonteRec subContaFonteRec : subConta.getSubContaFonteRecs()) {
                    if (subContaFonteRec.getFonteDeRecursos().getExercicio().equals(tipo.getExercicio())) {
                        for (SubContaUniOrg subUo : subConta.getUnidadesOrganizacionais()) {
                            if (subUo.getExercicio().equals(tipo.getExercicio())) {
                                itens.add(new AnaliseContaFinanceira(subConta, subContaFonteRec.getContaDeDestinacao(), subUo.getUnidadeOrganizacional()));
                            }
                        }
                    }
                }
                for (AnaliseContaFinanceira analiseContaFinanceira : itens) {
                    if (analiseContaFinanceira.getSubConta().equals(subConta)) {
                        HierarquiaOrganizacional hierarquiaDaUnidade = hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), analiseContaFinanceira.getUnidadeOrganizacional(), assistente.getDataFinal());

                        MovimentoHashContabil movimento = jdbcMovimentoHashContabil.buscarMovimentos(new MovimentoHashContabil().toFinanceiro(hierarquiaDaUnidade, subConta, analiseContaFinanceira.getContaDeDestinacao().getFonteDeRecursos(), assistente.getDataFinal(), null));
                        if (movimento == null) {

                            AssistenteReprocessamento selecionado = new AssistenteReprocessamento();
                            selecionado.setQueryReprocessamento(getQueryReprocessamentoService().getReprocessamentoFinanceiro());
                            selecionado.setDataInicial(assistente.getDataInicial());
                            selecionado.setDataFinal(assistente.getDataFinal());
                            selecionado.setExercicio(tipo.getExercicio());
                            selecionado.setSubConta(subConta);
                            selecionado.setContaBancariaEntidade(subConta.getContaBancariaEntidade());
                            selecionado.setContaDeDestinacao(analiseContaFinanceira.getContaDeDestinacao());
                            StringBuilder stb = new StringBuilder();
                            String concat = " where ";
                            if (selecionado.getSubConta() != null) {
                                stb.append(concat).append(" subconta = ").append(selecionado.getSubConta().getId());
                                concat = " and ";
                            }
                            if (selecionado.getContaDeDestinacao() != null) {
                                stb.append(concat).append(" contadedestinacao = ").append(selecionado.getContaDeDestinacao().getId());
                            }
                            selecionado.setFiltro(stb.toString());
                            List<ReprocessamentoSubConta> reprocessamentoSubContas = reprocessamentoSaldoSubContaFacade.buscarMovimentos(selecionado);

                            for (ReprocessamentoSubConta reprocessamentoSubConta : reprocessamentoSubContas) {
                                UnidadeOrganizacional unidadeOrganizacional = reprocessamentoSubConta.getUnidadeOrganizacional();
                                ContaDeDestinacao contaDeDestinacao = reprocessamentoSubConta.getContaDeDestinacao();

                                SubConta finalSubConta = subConta;
                                Optional<AnaliseContaFinanceira> first = itens.stream().filter(
                                    f -> f.getSubConta().getId().equals(finalSubConta.getId()) &&
                                        f.getUnidadeOrganizacional().getId().equals(unidadeOrganizacional.getId()) &&
                                        f.getContaDeDestinacao().getId().equals(contaDeDestinacao.getId())
                                ).findFirst();
                                if (first.isPresent()) {
                                    first.get().getReprocessamentoSubContas().add(reprocessamentoSubConta);
                                    first.get().setTotalMovimentoCredito(first.get().getTotalMovimentoCredito().add(reprocessamentoSubConta.getValorCredito()));
                                    first.get().setTotalMovimentoDebito(first.get().getTotalMovimentoDebito().add(reprocessamentoSubConta.getValorDebito()));
                                } else {
                                    itens.add(new AnaliseContaFinanceira(reprocessamentoSubConta));
                                }
                            }
                        } else {
                            analiseContaFinanceira.setTotalMovimentoCredito(analiseContaFinanceira.getTotalMovimentoCredito().add(movimento.getValor()));
                        }
                    }
                }
                tipo.contar(1);
            }
        } catch (Exception e) {
            adicionarLog(tipo, "Erro ao recuperar dados financeiros: " + e.getMessage());
        }

        for (AnaliseContaFinanceira analiseContaFinanceira : itens) {
            try {
                String descricao = analiseContaFinanceira.getSubConta().toString() + " - " + analiseContaFinanceira.getUnidadeOrganizacional().getDescricao();
                tipo.setMensagemBarraProcesso("Recuperando saldo da conta : " + descricao);
                UnidadeOrganizacional unidadeOrganizacional = analiseContaFinanceira.getUnidadeOrganizacional();
                SubConta subConta = analiseContaFinanceira.getSubConta();
                ContaDeDestinacao contaDeDestinacao = analiseContaFinanceira.getContaDeDestinacao();
                Date dataInicial = assistente.getDataInicial();
                Date dataFinal = assistente.getDataFinal();
                SaldoSubConta saldoSubConta = saldoSubContaFacade.recuperaUltimoSaldoSubContaPorData(unidadeOrganizacional, subConta, contaDeDestinacao, dataFinal);
                analiseContaFinanceira.setSaldoSubConta(saldoSubConta);

                SaldoSubConta saldoAnterior = saldoSubContaFacade.recuperaUltimoSaldoSubContaPorData(unidadeOrganizacional, subConta, contaDeDestinacao, DataUtil.removerDias(dataInicial, 1));
                analiseContaFinanceira.setSaldoAnterior(saldoAnterior);


                if (assistente.getMostrarSomenteInsconsistencia()
                    && (analiseContaFinanceira.getDiferenca().doubleValue() != 0 || analiseContaFinanceira.getSaldoAtual().doubleValue() < 0)) {
                    assistente.getFinanceiro().add(analiseContaFinanceira);
                }

                if (!assistente.getMostrarSomenteInsconsistencia()) {
                    assistente.getFinanceiro().add(analiseContaFinanceira);
                }
            } catch (Exception ex) {
                adicionarLog(tipo, "Erro ao recuperar dados financeiro: " + ex.getMessage());
            }
        }

    }

    private void validarSaldoOrcamentario(AssistenteAnaliseContabil assistente, TipoAssistenteAnaliseContabil tipo) {
        if (!TipoAnaliseContabil.ORCAMENTARIO.equals(tipo.getTipoAnaliseContabil())) return;
        assistente.setOrcamentario(Lists.newArrayList());

        tipo.setMensagemBarraProcesso("Buscando dados orçamentarios ");
        List<DespesaORC> despesaORCS = despesaORCFacade.buscarTodasDespesasOrcPorExercicioAndUnidadeComMovimentacao(tipo.getExercicio(), assistente.getHierarquiaOrganizacional(), assistente.getDataInicial(), assistente.getDataFinal(), assistente.getBuscarSomenteContasComMovimentacao());
        tipo.setMensagemBarraProcesso("Total de despesas orcamentárias: " + despesaORCS.size());
        tipo.zerarContadoresProcesso();
        tipo.setTotal(despesaORCS.size());
        for (DespesaORC despesaORC : despesaORCS) {
            for (FonteDespesaORC fonteDespesaORC : despesaORC.getFonteDespesaORCs()) {
                tipo.setMensagemBarraProcesso("Recuperando dados da despesa : " + fonteDespesaORC.toString());

                try {
                    ExtratoMovimentoDespesaORC selecionado = new ExtratoMovimentoDespesaORC();
                    selecionado.setExercicio(tipo.getExercicio());
                    LocalDate localDate = DataUtil.criarDataPrimeiroDiaMes(1, tipo.getExercicio().getAno());

                    selecionado.setDataInicial(DataUtil.localDateToDate(localDate));
                    selecionado.setDataFinal(assistente.getDataFinal());

                    UnidadeOrganizacional unidadeOrganizacional = despesaORC.getProvisaoPPADespesa().getUnidadeOrganizacional();
                    HierarquiaOrganizacional hierarquiaDaUnidade = hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), unidadeOrganizacional, assistente.getDataFinal());

                    selecionado.setHierarquiaOrganizacional(hierarquiaDaUnidade);
                    selecionado.setFonteDespesaORC(fonteDespesaORC);
                    selecionado.setDespesaORC(despesaORC);

                    extratoMovimentoDespesaOrcFacade.buscarUltimoMovimentoDespesaOrc(selecionado);

                    MovimentoHashContabil movimento = jdbcMovimentoHashContabil.buscarMovimentos(new MovimentoHashContabil().toOrcamentario(fonteDespesaORC, assistente.getDataFinal(), null));
                    if (movimento == null) {
                        extratoMovimentoDespesaOrcFacade.buscarValoresFatosGerados(selecionado);
                    } else {
                        selecionado.setSaldoAtual(movimento.getValor());
                    }

                    if (selecionado.getUltimoSaldo() != null) {
                        if (assistente.getMostrarSomenteInsconsistencia()
                            && (selecionado.getUltimoSaldo().getSaldoAtual().compareTo(selecionado.getSaldoAtual()) != 0 || selecionado.getUltimoSaldo().getSaldoAtual().doubleValue() < 0)) {
                            assistente.getOrcamentario().add(selecionado);
                        }

                        if (!assistente.getMostrarSomenteInsconsistencia()) {
                            assistente.getOrcamentario().add(selecionado);
                        }
                    } else {
                        assistente.getOrcamentario().add(selecionado);
                    }
                } catch (Exception ex) {
                    adicionarLog(tipo, "Erro ao recuperar dados da fonte de despesa: " + ex.getMessage());
                }
            }
            tipo.contar(1);
        }
    }

    private static void adicionarLog(TipoAssistenteAnaliseContabil tipo, String ex) {
        tipo.getMensagens().add(ex);
    }

    public QueryReprocessamentoService getQueryReprocessamentoService() {
        return (QueryReprocessamentoService) Util.getSpringBeanPeloNome("queryReprocessamentoService");
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public DocumentoOficialFacade getDocumentoOficialFacade() {
        return documentoOficialFacade;
    }

    public List<HierarquiaOrganizacional> completarUnidadeOrganizacional(String parte) {
        return hierarquiaOrganizacionalFacade.listaHierarquiaUsuarioCorrentePorTipo(parte.trim(), sistemaFacade.getUsuarioCorrente(), sistemaFacade.getExercicioCorrente(), sistemaFacade.getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name());
    }

    public DespesaORCFacade getDespesaORCFacade() {
        return despesaORCFacade;
    }

    public AnaliseMovimentoContabil salvarAnaliseContabil(AnaliseMovimentoContabil analiseMovimentoContabil) {
        analiseMovimentoContabil = em.merge(analiseMovimentoContabil);
        return analiseMovimentoContabil;
    }

    public void finalizarAnalise(AnaliseMovimentoContabil analise) {
        analise.setSituacao(SituacaoMovimentoContabil.FINALIZADO);
        salvarAnaliseContabil(analise);
    }
}
