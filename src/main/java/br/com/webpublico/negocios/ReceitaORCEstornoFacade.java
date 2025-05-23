/*
 * Codigo gerado automaticamente em Thu Dec 22 14:32:20 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.portaltransparencia.PortalTransparenciaNovoFacade;
import br.com.webpublico.controle.portaltransparencia.entidades.ReceitaOrcEstornoPortal;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.BloqueioMovimentoContabilException;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.ReprocessamentoLancamentoContabilSingleton;
import br.com.webpublico.negocios.contabil.reprocessamento.SuperFacadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.ConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.FiltroConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.singleton.SingletonConcorrenciaContabil;
import br.com.webpublico.singletons.SingletonGeradorCodigoContabil;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Stateless
public class ReceitaORCEstornoFacade extends SuperFacadeContabil<ReceitaORCEstorno> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConfigReceitaRealizadaFacade configReceitaRealizadaFacade;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private ReceitaLOAFacade receitaLOAFacade;
    @EJB
    private LancamentoReceitaOrcFacade lancamentoReceitaOrcFacade;
    @EJB
    private SaldoReceitaORCFacade saldoReceitaORCFacade;
    @EJB
    private ReprocessamentoLancamentoContabilSingleton reprocessamentoLancamentoContabilSingleton;
    @EJB
    private SingletonGeradorCodigoContabil singletonGeradorCodigoContabil;
    @EJB
    private SaldoSubContaFacade saldoSubContaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SaldoDividaAtivaContabilFacade saldoDividaAtivaContabilFacade;
    @EJB
    private SaldoCreditoReceberFacade saldoCreditoReceberFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private SubContaFacade subContaFacade;
    @EJB
    private ContaBancariaEntidadeFacade contaBancariaEntidadeFacade;
    @EJB
    private ClasseCredorFacade classeCredorFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private SingletonConcorrenciaContabil singletonConcorrenciaContabil;
    @EJB
    private PortalTransparenciaNovoFacade portalTransparenciaNovoFacade;
    @EJB
    private SaldoDividaPublicaFacade saldoDividaPublicaFacade;
    @EJB
    private ConfiguracaoContabilFacade configuracaoContabilFacade;
    @EJB
    private SaldoGrupoBemMovelFacade saldoGrupoBemMovelFacade;
    @EJB
    private ConfigReceitaRealizadaGrupoPatrimonialFacade configReceitaRealizadaGrupoPatrimonialFacade;

    public ReceitaORCEstornoFacade() {
        super(ReceitaORCEstorno.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public int getCodigo() {
        String sql = "SELECT coalesce(max(re.numero + 1),1) FROM ReceitaORCEstorno re";
        Query q = getEntityManager().createNativeQuery(sql);
        BigDecimal retorno;
        retorno = (BigDecimal) q.getSingleResult();
        return retorno.intValue();
    }

    public void salvarNovoEstorno(ReceitaORCEstorno entity, Long conjuntoFonte) throws ExcecaoNegocioGenerica, BloqueioMovimentoContabilException {
        try {
            atualizarCodigoCoReceitaORCFonteEstorno(entity);
            singletonConcorrenciaContabil.bloquearMovimentoContabil(entity.getLancamentoReceitaOrc());

            getHierarquiaOrganizacionalFacade().validaVigenciaHIerarquiaAdministrativaOrcamentaria(entity.getUnidadeOrganizacionalAdm(), entity.getUnidadeOrganizacionalOrc(), entity.getDataEstorno());

            if (getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
                throw new ExcecaoNegocioGenerica(getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
            } else {
                if (!entity.getIntegracao()) {
                    validaFontesNaReceitaEConta(entity, conjuntoFonte);
                    validaCalculoDiferencaDaFonte(entity);
                }
                entity = salvaEstornoReceitaRealizada(entity);
                geraSaldosReceitarOrcEstorno(entity);
                contabilizarReceitaOrcEstorno(entity);
                portalTransparenciaNovoFacade.salvarPortal(new ReceitaOrcEstornoPortal(entity));
            }
            singletonConcorrenciaContabil.desbloquearMovimentoContabil(entity.getLancamentoReceitaOrc());
        } catch (BloqueioMovimentoContabilException bmce) {
            throw bmce;
        } catch (Exception ex) {
            singletonConcorrenciaContabil.desbloquearMovimentoContabil(entity.getLancamentoReceitaOrc());
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    public void atualizarCodigoCoReceitaORCFonteEstorno(ReceitaORCEstorno entity) {
        entity.getReceitaORCFonteEstorno().forEach(roef -> {
            roef.setCodigoCO(buscarCodigoCoDaLancReceitaFonte(roef));
        });
    }

    private CodigoCO buscarCodigoCoDaLancReceitaFonte(ReceitaORCFonteEstorno roef) {
        if (roef.getReceitaORCEstorno().getLancamentoReceitaOrc() == null) {
            return null;
        }
        String sql = "select cco.* " +
            " from codigoco cco " +
            "    inner join LancReceitaFonte lrf on lrf.codigoco_id = cco.id " +
            " where lrf.lancReceitaOrc_id = :idReceitaRealizada " +
            "   and lrf.receitaLoaFonte_id = :idReceitaLoaFonte ";
        Query q = em.createNativeQuery(sql, CodigoCO.class);
        q.setParameter("idReceitaRealizada", roef.getReceitaORCEstorno().getLancamentoReceitaOrc().getId());
        q.setParameter("idReceitaLoaFonte", roef.getReceitaLoaFonte().getId());
        List<CodigoCO> resultado = q.getResultList();
        return !resultado.isEmpty() ? resultado.get(0) : null;
    }

    public ReceitaORCEstorno salvaEstornoReceitaRealizada(ReceitaORCEstorno entity) {
        if (entity.getId() == null) {
            lancamentoReceitaOrc(entity);
            entity.setNumero(singletonGeradorCodigoContabil.getNumeroReceitaRealizada(entity.getExercicio(), entity.getDataEstorno()));
            setaDividaPublicaEConvenioReceita(entity);
            geraHistoricos(entity);
            em.persist(entity);
        } else {
            geraHistoricos(entity);
            entity = em.merge(entity);
        }
        return entity;
    }

    private void setaDividaPublicaEConvenioReceita(ReceitaORCEstorno entity) {
        if (entity.getLancamentoReceitaOrc() != null) {
            if (entity.getLancamentoReceitaOrc().getDividaPublica() != null) {
                entity.setDividaPublica(entity.getLancamentoReceitaOrc().getDividaPublica());
            }
            if (entity.getLancamentoReceitaOrc().getConvenioReceita() != null) {
                entity.setConvenioReceita(entity.getLancamentoReceitaOrc().getConvenioReceita());
            }
        }
    }

    public BigDecimal somaLancamentos(ReceitaORCEstorno lancamento) {
        BigDecimal soma = BigDecimal.ZERO;
        if (!lancamento.getReceitaORCFonteEstorno().isEmpty()) {
            for (ReceitaORCFonteEstorno rfe : lancamento.getReceitaORCFonteEstorno()) {
                soma = soma.add(rfe.getValor()).setScale(2, RoundingMode.HALF_UP);
            }
        }
        return soma;
    }

    public void salvarEdicao(ReceitaORCEstorno entity) throws BloqueioMovimentoContabilException {
        try {
            atualizarCodigoCoReceitaORCFonteEstorno(entity);
            singletonConcorrenciaContabil.bloquearMovimentoContabil(entity.getLancamentoReceitaOrc());

            geraHistoricos(entity);
            em.merge(entity);
            portalTransparenciaNovoFacade.salvarPortal(new ReceitaOrcEstornoPortal(entity));

            singletonConcorrenciaContabil.desbloquearMovimentoContabil(entity.getLancamentoReceitaOrc());
        } catch (BloqueioMovimentoContabilException bmce) {
            throw bmce;
        } catch (Exception ex) {
            singletonConcorrenciaContabil.desbloquearMovimentoContabil(entity.getLancamentoReceitaOrc());
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    public void lancamentoReceitaOrc(ReceitaORCEstorno estornoReceita) {
        try {
            if (estornoReceita.getLancamentoReceitaOrc() != null) {
                LancamentoReceitaOrc receita = lancamentoReceitaOrcFacade.recuperar(estornoReceita.getLancamentoReceitaOrc().getId());
                receita.setSaldo(receita.getSaldo().subtract(estornoReceita.getValor()));
                estornoReceita.setContaFinanceira(receita.getSubConta());
                estornoReceita.setClasseCredor(receita.getClasseCredor());
                estornoReceita.setPessoa(receita.getPessoa());
                if (receita.getSaldo().compareTo(BigDecimal.ZERO) == 0) {
                    estornoReceita.setDataConciliacao(sistemaFacade.getDataOperacao());
                    if (receita.getDataConciliacao() == null) {
                        receita.setDataConciliacao(sistemaFacade.getDataOperacao());
                    }
                }
                lancamentoReceitaOrcFacade.salvarReceitaNoEstorno(receita);
            }
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Erro: " + ex.getMessage());
        }
    }

    public void geraSaldoReceita(ReceitaORCEstorno entity) throws ExcecaoNegocioGenerica {
        try {
            List<ReceitaLOAFonte> listaReceitaLoaFontes = receitaLOAFacade.listaReceitaLoaFontes(entity.getReceitaLOA());
            BigDecimal somaValorCalc = new BigDecimal(BigInteger.ZERO);
            BigDecimal diferenca = new BigDecimal(BigInteger.ZERO);
            for (ReceitaLOAFonte rf : listaReceitaLoaFontes) {
                BigDecimal valorCalculado = new BigDecimal(BigInteger.ZERO);
                valorCalculado = entity.getValor().multiply(rf.getPercentual().divide(new BigDecimal(100)));
                somaValorCalc = somaValorCalc.add(valorCalculado);
                saldoReceitaORCFacade.geraSaldo(TipoSaldoReceitaORC.RECEITAORCESTORNO, entity.getDataEstorno(), entity.getUnidadeOrganizacionalOrc(), entity.getReceitaLOA().getContaDeReceita(), rf.getDestinacaoDeRecursos().getFonteDeRecursos(), valorCalculado);
            }

            if (entity.getValor() != null && entity.getValor().compareTo(BigDecimal.ZERO) <= 0) {
                if (!somaValorCalc.equals(entity.getValor())) {
                    for (ReceitaLOAFonte rf : listaReceitaLoaFontes) {
                        if (rf.getRounding()) {
                            diferenca = entity.getValor().subtract(somaValorCalc);
                            saldoReceitaORCFacade.geraSaldo(TipoSaldoReceitaORC.RECEITAORCESTORNO, entity.getDataEstorno(), entity.getUnidadeOrganizacionalOrc(), entity.getReceitaLOA().getContaDeReceita(), rf.getDestinacaoDeRecursos().getFonteDeRecursos(), diferenca);
                        }
                    }
                }
                throw new ExcecaoNegocioGenerica(" O campo valor dever ser maior que zero(0).");
            }

        } catch (ExcecaoNegocioGenerica ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    public void geraSaldoContaFinanceira(ReceitaORCEstorno entity) {
        for (ReceitaORCFonteEstorno roe : entity.getReceitaORCFonteEstorno()) {
            if (OperacaoReceita.retornarOperacoesReceitaDeducao().contains(entity.getOperacaoReceitaRealizada())) {
                saldoSubContaFacade.gerarSaldoFinanceiro(entity.getUnidadeOrganizacionalOrc(),
                    entity.getContaFinanceira(),
                    roe.getReceitaLoaFonte().getDestinacaoDeRecursos(),
                    roe.getValor(),
                    TipoOperacao.CREDITO,
                    entity.getDataEstorno(),
                    entity.getEventoContabil(),
                    roe.getHistoricoRazao(),
                    MovimentacaoFinanceira.ESTORNO_DE_RECEITA_REALIZADA,
                    entity.getUuid(),
                    true);
            } else {
                saldoSubContaFacade.gerarSaldoFinanceiro(entity.getUnidadeOrganizacionalOrc(),
                    entity.getContaFinanceira(),
                    roe.getReceitaLoaFonte().getDestinacaoDeRecursos(),
                    roe.getValor(),
                    TipoOperacao.DEBITO,
                    entity.getDataEstorno(),
                    entity.getEventoContabil(),
                    roe.getHistoricoRazao(),
                    MovimentacaoFinanceira.ESTORNO_DE_RECEITA_REALIZADA,
                    entity.getUuid(),
                    true);
            }
        }
    }

    private void gerarSaldoDividaPublica(ReceitaORCEstorno entity) throws Exception {
        ConfiguracaoContabil configuracaoContabil = configuracaoContabilFacade.configuracaoContabilVigente();
        if (configuracaoContabil.getContasReceita().isEmpty()) {
            throw new ExcecaoNegocioGenerica("É necessário informar as Contas de Receita para geração de Saldo da Dívida Pública na Configuração Contábil.");
        }
        if (entity.getLancamentoReceitaOrc() != null &&
            entity.getLancamentoReceitaOrc().getDividaPublica() != null &&
            hasContaDeReceitaConfiguradaParaGerarSaldoDividaPublica(entity, configuracaoContabil)) {
            saldoDividaPublicaFacade.gerarMovimento(
                entity.getDataEstorno(),
                entity.getValor(),
                entity.getUnidadeOrganizacionalOrc(),
                entity.getDividaPublica(),
                OperacaoMovimentoDividaPublica.RECEITA_OPERACAO_CREDITO,
                true,
                OperacaoDiarioDividaPublica.RECEITA_REALIZADA,
                Intervalo.LONGO_PRAZO,
                entity.getLancamentoReceitaOrc().getContaDeDestinacao(),
                true);
        }
    }

    private boolean hasContaDeReceitaConfiguradaParaGerarSaldoDividaPublica(ReceitaORCEstorno entity, ConfiguracaoContabil configuracaoContabil) {
        for (ConfiguracaoContabilContaReceita configuracaoContabilContaReceita : configuracaoContabil.getContasReceita()) {
            if (entity.getReceitaLOA().getContaDeReceita().getCodigo().startsWith(configuracaoContabilContaReceita.getContaReceita().getCodigoSemZerosAoFinal())) {
                return true;
            }
        }
        return false;
    }

    public void geraSaldosReceitarOrcEstorno(ReceitaORCEstorno entity) throws Exception {
        geraSaldoReceita(entity);
        geraSaldoContaFinanceira(entity);
        gerarSaldoDividaAtivaAndCreditoReceber(entity);
        gerarSaldoDividaPublica(entity);
        gerarSaldoGrupoPatrimonial(entity);
    }

    private void gerarSaldoGrupoPatrimonial(ReceitaORCEstorno entity) {
        ConfigReceitaRealizadaBensMoveis configReceitaRealizadaBensMoveis = configReceitaRealizadaGrupoPatrimonialFacade.buscarConfiguracaoPorContaReceita(entity.getReceitaLOA().getContaDeReceita(), entity.getDataEstorno());
        if (configReceitaRealizadaBensMoveis != null) {
            saldoGrupoBemMovelFacade.geraSaldoGrupoBemMoveis(entity.getLancamentoReceitaOrc().getUnidadeOrganizacional(),
                configReceitaRealizadaBensMoveis.getGrupoBem(),
                entity.getValor(),
                configReceitaRealizadaBensMoveis.getTipoGrupo(),
                entity.getDataEstorno(),
                TipoOperacaoBensMoveis.ALIENACAO_BENS_MOVEIS,
                TipoLancamento.ESTORNO,
                TipoOperacao.CREDITO,
                true);
        }
    }

    public void gerarSaldoDividaAtivaAndCreditoReceber(ReceitaORCEstorno entity) throws ExcecaoNegocioGenerica {
        ContaReceita contaReceita = (ContaReceita) entity.getReceitaLOA().getContaDeReceita();
        if ((contaReceita).getTiposCredito() != null) {
            gerarSaldoDividaAtiva(entity);

            gerarSaldoCreditoReceber(entity);
        } else {
            throw new ExcecaoNegocioGenerica(" O cadastro da conta de receita informada não possui um Tipo de Crédito");
        }
    }

    private void gerarSaldoCreditoReceber(ReceitaORCEstorno entity) {
        if (OperacaoReceita.RECEITA_CREDITOS_RECEBER_BRUTA.equals(entity.getOperacaoReceitaRealizada())) {
            baixarSaldoCreditoReceber(entity);
        }
    }

    private void gerarSaldoDividaAtiva(ReceitaORCEstorno entity) {
        if (OperacaoReceita.RECEITA_DIVIDA_ATIVA_BRUTA.equals(entity.getOperacaoReceitaRealizada())) {
            baixarSaldoDividaAtiva(entity);
        }
    }

    private void baixarSaldoDividaAtiva(ReceitaORCEstorno entity) throws ExcecaoNegocioGenerica {
        for (ReceitaORCFonteEstorno rofe : entity.getReceitaORCFonteEstorno()) {
            DividaAtivaContabil dac = new DividaAtivaContabil();
            dac.setDataDivida(entity.getDataEstorno());
            dac.setOperacaoDividaAtiva(OperacaoDividaAtiva.RECEBIMENTO);
            dac.setUnidadeOrganizacional(entity.getUnidadeOrganizacionalOrc());
            dac.setValor(rofe.getValor());
            dac.setTipoLancamento(TipoLancamento.ESTORNO);
            dac.setContaDeDestinacao(contaFacade.recuperarContaDestinacaoPorFonte(rofe.getReceitaLoaFonte().getDestinacaoDeRecursos().getFonteDeRecursos()));
            dac.setNaturezaDividaAtiva(NaturezaDividaAtivaCreditoReceber.ORIGINAL);
            dac.setIntervalo(Intervalo.CURTO_PRAZO);
            saldoDividaAtivaContabilFacade.gerarSaldoDividaAtiva(dac, (ContaReceita) entity.getReceitaLOA().getContaDeReceita());
        }
    }

    private void baixarSaldoCreditoReceber(ReceitaORCEstorno entity) throws ExcecaoNegocioGenerica {
        for (ReceitaORCFonteEstorno rofe : entity.getReceitaORCFonteEstorno()) {
            CreditoReceber cr = new CreditoReceber();
            cr.setUnidadeOrganizacional(entity.getUnidadeOrganizacionalOrc());
            cr.setDataCredito(entity.getDataEstorno());
            cr.setValor(rofe.getValor());
            cr.setReceitaLOA(entity.getReceitaLOA());
            cr.setTipoLancamento(TipoLancamento.ESTORNO);
            cr.setOperacaoCreditoReceber(OperacaoCreditoReceber.RECEBIMENTO);
            cr.setContaDeDestinacao(contaFacade.recuperarContaDestinacaoPorFonte(rofe.getReceitaLoaFonte().getDestinacaoDeRecursos().getFonteDeRecursos()));
            cr.setNaturezaCreditoReceber(NaturezaDividaAtivaCreditoReceber.ORIGINAL);
            cr.setIntervalo(Intervalo.CURTO_PRAZO);
            saldoCreditoReceberFacade.gerarSaldoCreditoReceber(cr, true);
        }
    }

    public void contabilizarReceitaOrcEstorno(ReceitaORCEstorno entity) {
        contabilizarReceitaOrcEstorno(entity, false);
    }

    public void contabilizarReceitaOrcEstorno(ReceitaORCEstorno entity, boolean simulacao) {
        for (ReceitaORCFonteEstorno receitaORCEstorno : entity.getReceitaORCFonteEstorno()) {
            contabilizarReceitaOrcEstornoFonte(receitaORCEstorno, simulacao);
        }
    }

    public void geraHistoricos(ReceitaORCEstorno entity) {
        for (ReceitaORCFonteEstorno receitaORCEstorno : entity.getReceitaORCFonteEstorno()) {
            receitaORCEstorno.gerarHistoricos();
        }
    }

    public void contabilizarReceitaOrcEstornoFonte(ReceitaORCFonteEstorno entity) {
        contabilizarReceitaOrcEstornoFonte(entity, false);
    }

    public void contabilizarReceitaOrcEstornoFonte(ReceitaORCFonteEstorno entity, boolean simulacao) {
        ConfigReceitaRealizada configuracao = configReceitaRealizadaFacade.buscarEventoPorContaReceita(
            entity.getReceitaORCEstorno().getReceitaLOA().getContaDeReceita(),
            TipoLancamento.ESTORNO,
            entity.getReceitaORCEstorno().getDataEstorno(),
            entity.getReceitaORCEstorno().getOperacaoReceitaRealizada());

        if (configuracao != null && configuracao.getEventoContabil() != null) {
            entity.setEventoContabil(configuracao.getEventoContabil());
        } else {
            throw new ExcecaoNegocioGenerica(" Evento Contábil não encontrado para a operação selecionada na contabilização de receita realizada.");
        }
        if (configuracao != null && configuracao.getId() != null) {
            entity.gerarHistoricos();

            ParametroEvento parametroEvento = new ParametroEvento();
            parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
            parametroEvento.setDataEvento(entity.getReceitaORCEstorno().getDataEstorno());
            parametroEvento.setUnidadeOrganizacional(entity.getReceitaORCEstorno().getReceitaLOA().getEntidade());
            parametroEvento.setEventoContabil(configuracao.getEventoContabil());
            parametroEvento.setExercicio(entity.getReceitaORCEstorno().getExercicio());
            parametroEvento.setIdOrigem(entity.getId().toString());
            parametroEvento.setClasseOrigem(entity.getClass().getSimpleName());

            ItemParametroEvento item = new ItemParametroEvento();
            item.setValor(entity.getValor());
            item.setParametroEvento(parametroEvento);
            item.setTagValor(TagValor.LANCAMENTO);
            item.setOperacaoClasseCredor(classeCredorFacade.recuperaOperacaoAndVigenciaClasseCredor(entity.getReceitaORCEstorno().getPessoa(), entity.getReceitaORCEstorno().getClasseCredor(), entity.getReceitaORCEstorno().getDataEstorno()));

            List<ObjetoParametro> objetos = Lists.newArrayList();
            if (!simulacao) {
                objetos.add(new ObjetoParametro(entity.getId().toString(), ReceitaORCFonteEstorno.class.getSimpleName(), item));
            }
            objetos.add(new ObjetoParametro(entity.getReceitaORCEstorno().getReceitaLOA().getContaDeReceita().getId().toString(), ContaReceita.class.getSimpleName(), item));
            objetos.add(new ObjetoParametro(entity.getReceitaORCEstorno().getContaFinanceira().getId().toString(), SubConta.class.getSimpleName(), item));
            objetos.add(new ObjetoParametro(((ContaDeDestinacao) entity.getReceitaLoaFonte().getDestinacaoDeRecursos()).getFonteDeRecursos().getId().toString(), FonteDeRecursos.class.getSimpleName(), item));

            if (entity.getReceitaORCEstorno().getDividaPublica() != null) {
                objetos.add(new ObjetoParametro(entity.getReceitaORCEstorno().getDividaPublica().getCategoriaDividaPublica().getId().toString(), CategoriaDividaPublica.class.getSimpleName(), item));
                objetos.add(new ObjetoParametro(entity.getReceitaORCEstorno().getDividaPublica().getId().toString(), DividaPublica.class.getSimpleName(), item));
            }
            item.setObjetoParametros(objetos);
            parametroEvento.getItensParametrosEvento().add(item);
            contabilizacaoFacade.contabilizar(parametroEvento, simulacao);
        } else {
            throw new ExcecaoNegocioGenerica(" Não existe configuração de receita realizada");
        }
    }

    public ConfigReceitaRealizadaFacade getConfigReceitaRealizadaFacade() {
        return configReceitaRealizadaFacade;
    }

    public BigDecimal somaReceitaEstornos(ReceitaORCEstorno receita) {
        BigDecimal soma = BigDecimal.ZERO;
        for (ReceitaORCFonteEstorno rfe : receita.getReceitaORCFonteEstorno()) {
            soma = soma.add(rfe.getValor()).setScale(2, RoundingMode.HALF_EVEN);
        }
        return soma;
    }

    public BigDecimal calculaDiferenca(ReceitaORCEstorno receita) {
        try {
            BigDecimal diferenca = new BigDecimal(0).setScale(2, RoundingMode.HALF_UP);
            diferenca = somaReceitaEstornos(receita)
                .subtract(receita.getValor().setScale(2, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP);
            return diferenca;
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }

    private boolean validaCalculoDiferencaDaFonte(ReceitaORCEstorno entity) {
        if (entity.getLancamentoReceitaOrc() == null) {
            if (calculaDiferenca(entity).compareTo(BigDecimal.ZERO) == 0) {
                return true;
            } else {
                throw new ExcecaoNegocioGenerica(" Por favor corrigir a diferença entre o total dos lançamentos e o valor da receita realizada.");
            }
        }
        return false;
    }

    public void definirEventoContabil(ReceitaORCEstorno selecionado) {
        try {
            Conta contaDeReceita = selecionado.getReceitaLOA().getContaDeReceita();
            Preconditions.checkArgument(contaDeReceita != null, "A Conta de Receita esta vazia.");

            ConfigReceitaRealizada configuracao = configReceitaRealizadaFacade.buscarEventoPorContaReceita(
                contaDeReceita,
                TipoLancamento.ESTORNO,
                selecionado.getDataEstorno(),
                selecionado.getOperacaoReceitaRealizada());
            Preconditions.checkArgument(configuracao != null, " Não foi encontrada configuração de evento para os parametros selecionados em data e receita.");
            selecionado.setEventoContabil(configuracao.getEventoContabil());
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, ex.getMessage(), ex.getMessage()));
            selecionado.setEventoContabil(null);
        }
    }

    @Override
    public ReceitaORCEstorno recuperar(Object id) {
        ReceitaORCEstorno re = getEntityManager().find(ReceitaORCEstorno.class, id);
        re.getReceitaORCFonteEstorno().size();
        return re;
    }

    public void geraLancamentos(ReceitaORCEstorno receitaORCEstorno, Long conjuntoFonte) throws ExcecaoNegocioGenerica {

        ReceitaLOA rl = recuperarReceitaLoa(receitaORCEstorno);
        try {
            validarLancamentoReceita(receitaORCEstorno, rl, conjuntoFonte);
            gerarLancamentoParaFonte(receitaORCEstorno, rl, conjuntoFonte);
        } catch (ExcecaoNegocioGenerica e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }

    private ReceitaLOA recuperarReceitaLoa(ReceitaORCEstorno receitaORCEstorno) {
        return receitaLOAFacade.recarregar(receitaORCEstorno.getReceitaLOA());
    }


    public void gerarLancamentoIntegracao(ReceitaORCEstorno receitaORCEstorno) throws ExcecaoNegocioGenerica {
        if (receitaORCEstorno.getReceitaORCFonteEstorno() != null && !receitaORCEstorno.getReceitaORCFonteEstorno().isEmpty()) {
            return;
        }

        ReceitaLOA rl = recuperarReceitaLoa(receitaORCEstorno);
        try {
            validarLancamentoReceita(receitaORCEstorno, rl, null);
            List<FonteDeRecursos> fontesDeRecursosIguais = new ArrayList<>();
            List<FonteDeRecursos> fontesSubContaRecuperada = recuperarFontesDaSubConta(receitaORCEstorno);

            for (Long conjunto : rl.getConjuntos()) {
                List<ReceitaLOAFonte> receitaLoaFontePorConjunto = rl.getReceitaLoaFontePorConjunto(conjunto);

                for (ReceitaLOAFonte receitaLOAFonte : receitaLoaFontePorConjunto) {
                    if (fontesSubContaRecuperada.contains(receitaLOAFonte.getFonteRecurso())) {
                        fontesDeRecursosIguais.add(receitaLOAFonte.getFonteRecurso());
                        rl.setConjuntoFonte(conjunto);
                        break;
                    }
                }
            }
            boolean validouFonte = false;
            for (FonteDeRecursos fonteDeRecursos : fontesDeRecursosIguais) {
                if (fontesSubContaRecuperada.contains(fonteDeRecursos)) {
                    validouFonte = true;
                }
            }
            if (validouFonte) {
                gerarLancamentoIntegracaoParaFonte(receitaORCEstorno, rl);
            } else {
                throw new ExcecaoNegocioGenerica("As fontes de recursos da receita: " + rl.getContaDeReceita() + ". Não conferem com as fontes de recursos da conta financeira: " + receitaORCEstorno.getContaFinanceira().getCodigo() + ".");
            }
        } catch (ExcecaoNegocioGenerica e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }

    private void gerarLancamentoIntegracaoParaFonte(ReceitaORCEstorno receitaORCEstorno, ReceitaLOA rl) {

        BigDecimal soma = BigDecimal.ZERO;
        BigDecimal somaPercentual = BigDecimal.ZERO;
        BigDecimal diferenca = BigDecimal.ZERO;
        try {
            for (ReceitaLOAFonte rlf : rl.getReceitaLoaFontePorConjunto(rl.getConjuntoFonte())) {
                ReceitaORCFonteEstorno receitaFonteEstorno = new ReceitaORCFonteEstorno();
                receitaFonteEstorno.setReceitaLoaFonte(rlf);
                receitaFonteEstorno.setItem(rlf.getItem());
                receitaFonteEstorno.setValor((receitaORCEstorno.getValor().multiply(rlf.getPercentual())).divide(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP));
                receitaFonteEstorno.setReceitaORCEstorno(receitaORCEstorno);
                receitaORCEstorno.getReceitaORCFonteEstorno().add(receitaFonteEstorno);
                soma = soma.add(receitaFonteEstorno.getValor());
                somaPercentual = somaPercentual.add(rlf.getPercentual());
                diferenca = receitaORCEstorno.getValor().subtract(soma);
            }
            adicionarDiferencaValorParaFonte(receitaORCEstorno, diferenca);
        } catch (ExcecaoNegocioGenerica ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    private void gerarLancamentoParaFonte(ReceitaORCEstorno receitaORCEstorno, ReceitaLOA rl, Long conjuntoFonte) {

        BigDecimal soma = BigDecimal.ZERO;
        BigDecimal somaPercentual = BigDecimal.ZERO;
        BigDecimal diferenca = BigDecimal.ZERO;

        try {
            if (receitaORCEstorno.getLancamentoReceitaOrc() != null) {
                LancamentoReceitaOrc lancamento = lancamentoReceitaOrcFacade.recuperar(receitaORCEstorno.getLancamentoReceitaOrc().getId());
                for (LancReceitaFonte rlf : lancamento.getLancReceitaFonte()) {
                    if (conjuntoFonte.equals(rlf.getItem())) {
                        ReceitaORCFonteEstorno receitaFonteEstorno = new ReceitaORCFonteEstorno();
                        receitaFonteEstorno.setReceitaLoaFonte(rlf.getReceitaLoaFonte());
                        receitaFonteEstorno.setValor((receitaORCEstorno.getValor().multiply(rlf.getReceitaLoaFonte().getPercentual())).divide(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP));
                        receitaFonteEstorno.setReceitaORCEstorno(receitaORCEstorno);
                        receitaORCEstorno.getReceitaORCFonteEstorno().add(receitaFonteEstorno);
                        soma = soma.add(receitaFonteEstorno.getValor());
                        somaPercentual = somaPercentual.add(rlf.getReceitaLoaFonte().getPercentual());
                        diferenca = receitaORCEstorno.getValor().subtract(soma);
                    }
                }
            } else {
                for (ReceitaLOAFonte rlf : rl.getReceitaLoaFontes()) {
                    if (conjuntoFonte.equals(rlf.getItem())) {
                        ReceitaORCFonteEstorno receitaFonteEstorno = new ReceitaORCFonteEstorno();
                        receitaFonteEstorno.setReceitaLoaFonte(rlf);
                        receitaFonteEstorno.setValor((receitaORCEstorno.getValor().multiply(rlf.getPercentual())).divide(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP));
                        receitaFonteEstorno.setReceitaORCEstorno(receitaORCEstorno);
                        receitaORCEstorno.getReceitaORCFonteEstorno().add(receitaFonteEstorno);
                        soma = soma.add(receitaFonteEstorno.getValor());
                        somaPercentual = somaPercentual.add(rlf.getPercentual());
                        diferenca = receitaORCEstorno.getValor().subtract(soma);
                    }
                }
            }
            adicionarDiferencaValorParaFonte(receitaORCEstorno, diferenca);
        } catch (ExcecaoNegocioGenerica ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    private Boolean validarLancamentoReceita(ReceitaORCEstorno receitaORCEstorno, ReceitaLOA rl, Long conjuntoFonte) {

        if (receitaORCEstorno.getIntegracao()) {
            if (rl == null) {
                throw new ExcecaoNegocioGenerica(" Conta de Receita não encontrada para gerar lançamentos para a fonte. ");
            }
            if (receitaORCEstorno.getValor().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ExcecaoNegocioGenerica(" O valor para o Estorno da Receita está zerado, portando não é possível gerar lançamentos para a fonte.");
            }
        } else {
            if (receitaORCEstorno.getValor().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ExcecaoNegocioGenerica(" O campo valor deve ser maior que zero(0).");
            }
            if (!receitaORCEstorno.getReceitaORCFonteEstorno().isEmpty()) {
                throw new ExcecaoNegocioGenerica(" Para efeturar um novo cálculo, é necessário remover o(s) lançamento(s) adicionado(s).");
            }
            if (receitaORCEstorno.getLancamentoReceitaOrc() != null) {
                if (receitaORCEstorno.getValor().compareTo(receitaORCEstorno.getLancamentoReceitaOrc().getSaldo()) > 0) {
                    throw new ExcecaoNegocioGenerica(" O valor para calcular o lançamento deve ser menor ou igual ao saldo de <b> " + Util.formataValor(receitaORCEstorno.getLancamentoReceitaOrc().getSaldo()) + "</b>, disponível para essa receita.");
                }
            }
            if (validaFontesNaReceitaEConta(receitaORCEstorno, conjuntoFonte)) {
                return true;
            }
        }
        return true;
    }

    private void adicionarDiferencaValorParaFonte(ReceitaORCEstorno receitaORCEstorno, BigDecimal diferenca) {
        if (diferenca.compareTo(BigDecimal.ZERO) != 0) {
            for (ReceitaORCFonteEstorno rofe : receitaORCEstorno.getReceitaORCFonteEstorno()) {
                if (rofe.getReceitaLoaFonte().getRounding().equals(true)) {
                    rofe.setValor(rofe.getValor().add(diferenca).setScale(2, RoundingMode.HALF_UP));
                }
            }
        }
    }

    public boolean validaFontesNaReceitaEConta(ReceitaORCEstorno lancamento, Long conjuntoFonte) throws ExcecaoNegocioGenerica {

        List<FonteDeRecursos> listaFonteReceita = recuperarFontesDaReceitaLoa(lancamento, conjuntoFonte);
        List<FonteDeRecursos> listaFonteSubConta = recuperarFontesDaSubConta(lancamento);

        for (FonteDeRecursos fonteDeRecursos : listaFonteSubConta) {
            if (listaFonteReceita.contains(fonteDeRecursos)) {
                return true;
            }
        }
        throw new ExcecaoNegocioGenerica(" As fontes de recursos da receita: " + lancamento.getReceitaLOA().getContaDeReceita() + ". Não conferem com as fontes de recursos da conta financeira: " + lancamento.getContaFinanceira().getCodigo() + ".");
    }

    public List<FonteDeRecursos> recuperarFontesDaSubConta(ReceitaORCEstorno lancamento) {
        List<FonteDeRecursos> listaFonteConta = new ArrayList<FonteDeRecursos>();
        SubConta subConta = subContaFacade.recuperar(lancamento.getContaFinanceira().getId());
        for (SubContaFonteRec sfr : subConta.getSubContaFonteRecs()) {
            if (lancamento.getExercicio().equals(sfr.getFonteDeRecursos().getExercicio()))
                listaFonteConta.add(sfr.getFonteDeRecursos());
        }
        return listaFonteConta;
    }

    public List<FonteDeRecursos> recuperarFontesDaReceitaLoa(ReceitaORCEstorno lancamento, Long conjuntoFonte) {
        List<FonteDeRecursos> listaFonteReceita = new ArrayList<>();
//        if (!Hibernate.isInitialized(lancamento.getReceitaLOA().getReceitaLoaFontes())) {
        ReceitaLOA receitaLOA = receitaLOAFacade.recuperar(lancamento.getReceitaLOA().getId());
        for (ReceitaLOAFonte rlf : receitaLOA.getReceitaLoaFontes()) {
            if (rlf.getItem().equals(conjuntoFonte)) {
                if (lancamento.getExercicio().equals(rlf.getDestinacaoDeRecursos().getFonteDeRecursos().getExercicio())) {
                    if (rlf.getPercentual().compareTo(BigDecimal.ZERO) != 0) {
                        listaFonteReceita.add(rlf.getDestinacaoDeRecursos().getFonteDeRecursos());
                    }
                }
            }
//            }
        }
        return listaFonteReceita;
    }

    public void estornarConciliacao(ReceitaORCEstorno receitaORCEstorno) {
        try {
            receitaORCEstorno.setDataConciliacao(null);
            em.merge(receitaORCEstorno);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Erro ao estornar a conciliação. Consulte o suporte!");
        }
    }

    public ReprocessamentoLancamentoContabilSingleton getReprocessamentoLancamentoContabilSingleton() {
        return reprocessamentoLancamentoContabilSingleton;
    }

    public ReceitaLOAFacade getReceitaLOAFacade() {
        return receitaLOAFacade;
    }

    public LancamentoReceitaOrcFacade getLancamentoReceitaOrcFacade() {
        return lancamentoReceitaOrcFacade;
    }

    public SaldoReceitaORCFacade getSaldoReceitaORCFacade() {
        return saldoReceitaORCFacade;
    }

    public SaldoSubContaFacade getSaldoSubContaFacade() {
        return saldoSubContaFacade;
    }

    public SaldoDividaAtivaContabilFacade getSaldoDividaAtivaContabilFacade() {
        return saldoDividaAtivaContabilFacade;
    }

    public SaldoCreditoReceberFacade getSaldoCreditoReceberFacade() {
        return saldoCreditoReceberFacade;
    }

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    public SubContaFacade getSubContaFacade() {
        return subContaFacade;
    }

    public ContaBancariaEntidadeFacade getContaBancariaEntidadeFacade() {
        return contaBancariaEntidadeFacade;
    }

    public ClasseCredorFacade getClasseCredorFacade() {
        return classeCredorFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    @Override
    public void contabilizarReprocessamento(EntidadeContabil entidadeContabil) {
        contabilizarReceitaOrcEstornoFonte((ReceitaORCFonteEstorno) entidadeContabil);
    }

    public List<ReceitaORCEstorno> recuperarEstornoReceitaRealizadaDaPessoa(Pessoa pessoa) {
        String sql = " select r.* from receitaorcestorno r " +
            "       where r.pessoa_id = :pessoa        ";
        Query q = em.createNativeQuery(sql, ReceitaORCEstorno.class);
        q.setParameter("pessoa", pessoa.getId());
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        }
        return q.getResultList();
    }

    public List<ReceitaORCEstorno> buscarTodasReceitaORCEstornoPorExercicioUnidades(Exercicio exercicio, List<HierarquiaOrganizacional> listaUnidades, Date dataInicial, Date dataFinal) {
        List<Long> unidades = Lists.newArrayList();
        for (HierarquiaOrganizacional lista : listaUnidades) {
            unidades.add(lista.getSubordinada().getId());
        }

        String sql = "SELECT L.* FROM ReceitaORCEstorno L "
            + " WHERE L.EXERCICIO_ID = :idExercicio "
            + " and L.unidadeOrganizacionalOrc in (:unidades)"
            + " AND trunc(l.dataEstorno) BETWEEN to_date(:dataInicial, 'dd/MM/yyyy') AND to_date(:dataFinal, 'dd/MM/yyyy') "
            + " ORDER BY L.NUMERO, L.dataEstorno DESC ";
        Query q = getEntityManager().createNativeQuery(sql, ReceitaORCEstorno.class);
        q.setParameter("idExercicio", exercicio.getId());
        q.setParameter("unidades", unidades);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        return q.getResultList();
    }

    public SingletonConcorrenciaContabil getSingletonConcorrenciaContabil() {
        return singletonConcorrenciaContabil;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    @Override
    public List<ConsultaMovimentoContabil> criarConsulta(List<FiltroConsultaMovimentoContabil> filtros) {
        ConsultaMovimentoContabil consulta = new ConsultaMovimentoContabil(ReceitaORCFonteEstorno.class, filtros);
        consulta.incluirJoinsComplementar(" inner join receitaORCEstorno lanc on obj.receitaORCEstorno_id = lanc.id ");
        consulta.incluirJoinsOrcamentoReceita("lanc.receitaloa_id", "obj.receitaloafonte_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_INICIAL, "lanc.dataEstorno");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_FINAL, "lanc.dataEstorno");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.UNIDADE, "lanc.unidadeOrganizacionalOrc");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.LISTA_UNIDADE, "lanc.unidadeOrganizacionalOrc");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.PESSOA, "lanc.fornecedor_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CLASSE, "lanc.classecredor_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.EVENTO_CONTABIL, "obj.eventocontabil_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.NUMERO, "lanc.numero");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.HISTORICO, "lanc.historicorazao");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.VALOR, "lanc.valor");
        return Arrays.asList(consulta);
    }
}
