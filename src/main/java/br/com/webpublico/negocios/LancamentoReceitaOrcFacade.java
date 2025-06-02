/*
 * Codigo gerado automaticamente em Tue Dec 20 17:59:34 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.portaltransparencia.PortalTransparenciaNovoFacade;
import br.com.webpublico.controle.portaltransparencia.entidades.LancamentoReceitaOrcPortal;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.ReprocessamentoLancamentoContabilSingleton;
import br.com.webpublico.negocios.contabil.reprocessamento.SuperFacadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.ConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.FiltroConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.singleton.SingletonConcorrenciaContabil;
import br.com.webpublico.singletons.SingletonGeradorCodigoContabil;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
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
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class LancamentoReceitaOrcFacade extends SuperFacadeContabil<LancamentoReceitaOrc> {

    @EJB
    private SaldoSubContaFacade saldoSubContaFacade;
    @EJB
    private SubContaFacade subContaFacade;
    @EJB
    private DividaPublicaFacade dividaPublicaFacade;
    @EJB
    private ConvenioReceitaFacade convenioReceitaFacade;
    @EJB
    private ReceitaLOAFacade receitaLOAFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ContaBancariaEntidadeFacade bancariaEntidadeFacade;
    @EJB
    private HistoricoContabilFacade historicoContabilFacade;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    @EJB
    private SaldoCreditoReceberFacade saldoCreditoReceberFacade;
    @EJB
    private SaldoDividaAtivaContabilFacade saldoDividaAtivaContabilFacade;
    @EJB
    private SaldoReceitaORCFacade saldoReceitaORCFacade;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;
    @EJB
    private ConfigReceitaRealizadaFacade configReceitaRealizadaFacade;
    @EJB
    private ClasseCredorFacade classeCredorFacade;
    @EJB
    private IntegracaoTributarioContabilFacade integracaoTributarioContabilFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ReprocessamentoLancamentoContabilSingleton reprocessamentoLancamentoContabilSingleton;
    @EJB
    private SingletonGeradorCodigoContabil singletonGeradorCodigoContabil;
    @EJB
    private ConfiguracaoContabilFacade configuracaoContabilFacade;
    @EJB
    private LoteBaixaFacade loteBaixaFacade;
    @EJB
    private FaseFacade faseFacade;
    @EJB
    private SingletonConcorrenciaContabil singletonConcorrenciaContabil;
    @EJB
    private PortalTransparenciaNovoFacade portalTransparenciaNovoFacade;
    @EJB
    private ConfigAlienacaoAtivosFacade configAlienacaoAtivosFacade;
    @EJB
    private ConfigReceitaRealizadaGrupoPatrimonialFacade configReceitaRealizadaGrupoPatrimonialFacade;
    @EJB
    private SaldoGrupoBemMovelFacade saldoGrupoBemMovelFacade;
    @EJB
    private SaldoDividaPublicaFacade saldoDividaPublicaFacade;
    @EJB
    private CodigoCOFacade codigoCOFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public LancamentoReceitaOrcFacade() {
        super(LancamentoReceitaOrc.class);
    }


    public void gerarSaldoDividaAtivaAndCreditoReceber(LancamentoReceitaOrc entity) throws ExcecaoNegocioGenerica {
        ContaReceita contaReceita = (ContaReceita) entity.getReceitaLOA().getContaDeReceita();
        if ((contaReceita).getTiposCredito() != null) {
            gerarSaldoDividaAtiva(entity);

            gerarSaldoCreditoReceber(entity);
        } else {
            throw new ExcecaoNegocioGenerica(" O cadastro da conta de receita informada não possui um Tipo de Crédito");
        }
    }

    private void gerarSaldoCreditoReceber(LancamentoReceitaOrc entity) {
        if (OperacaoReceita.RECEITA_CREDITOS_RECEBER_BRUTA.equals(entity.getOperacaoReceitaRealizada())) {
            baixarSaldoCreditoReceber(entity);
        }
    }

    private void gerarSaldoDividaAtiva(LancamentoReceitaOrc entity) {
        if (OperacaoReceita.RECEITA_DIVIDA_ATIVA_BRUTA.equals(entity.getOperacaoReceitaRealizada())) {
            baixarSaldoDividaAtiva(entity);
        }
    }

    private void baixarSaldoDividaAtiva(LancamentoReceitaOrc entity) throws ExcecaoNegocioGenerica {
        for (LancReceitaFonte lrf : entity.getLancReceitaFonte()) {
            DividaAtivaContabil dac = new DividaAtivaContabil();
            dac.setDataDivida(entity.getDataLancamento());
            dac.setOperacaoDividaAtiva(OperacaoDividaAtiva.RECEBIMENTO);
            dac.setUnidadeOrganizacional(entity.getUnidadeOrganizacional());
            dac.setValor(lrf.getValor());
            dac.setTipoLancamento(TipoLancamento.NORMAL);
            dac.setContaDeDestinacao(contaFacade.recuperarContaDestinacaoPorFonte(lrf.getReceitaLoaFonte().getDestinacaoDeRecursos().getFonteDeRecursos()));
            dac.setNaturezaDividaAtiva(NaturezaDividaAtivaCreditoReceber.ORIGINAL);
            dac.setIntervalo(Intervalo.CURTO_PRAZO);
            saldoDividaAtivaContabilFacade.gerarSaldoDividaAtiva(dac, (ContaReceita) entity.getReceitaLOA().getContaDeReceita());
        }
    }

    private void baixarSaldoCreditoReceber(LancamentoReceitaOrc entity) throws ExcecaoNegocioGenerica {
        for (LancReceitaFonte lrf : entity.getLancReceitaFonte()) {
            CreditoReceber cr = new CreditoReceber();
            cr.setUnidadeOrganizacional(entity.getUnidadeOrganizacional());
            cr.setDataCredito(entity.getDataLancamento());
            cr.setValor(lrf.getValor());
            cr.setReceitaLOA(entity.getReceitaLOA());
            cr.setTipoLancamento(TipoLancamento.NORMAL);
            cr.setOperacaoCreditoReceber(OperacaoCreditoReceber.RECEBIMENTO);
            cr.setContaDeDestinacao(contaFacade.recuperarContaDestinacaoPorFonte(lrf.getReceitaLoaFonte().getDestinacaoDeRecursos().getFonteDeRecursos()));
            cr.setNaturezaCreditoReceber(NaturezaDividaAtivaCreditoReceber.ORIGINAL);
            cr.setIntervalo(Intervalo.CURTO_PRAZO);
            saldoCreditoReceberFacade.gerarSaldoCreditoReceber(cr, true);
        }
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public String getUltimoNumero() {
        String sql = "select MAX(TO_NUMBER(LANC.NUMERO)+1) from LancamentoReceitaOrc lanc";
        Query q = em.createNativeQuery(sql);

        if (q.getSingleResult() == null) {
            return "1";
        } else {
            return q.getSingleResult().toString();
        }
    }

    public List<LancamentoReceitaOrc> completaLancamentoReceitaORC(String parte, Exercicio exercicio, UnidadeOrganizacional unidadeOrganizacional) {
        String sql = "SELECT L.* FROM LANCAMENTORECEITAORC L "
            + " INNER JOIN PESSOA P ON L.PESSOA_ID = P.ID "
            + " INNER JOIN RECEITALOA RL ON L.RECEITALOA_ID = RL.ID"
            + " INNER JOIN CONTA C ON RL.CONTADERECEITA_ID = C.ID"
            + " LEFT JOIN PESSOAFISICA PF ON P.ID = PF.ID "
            + " LEFT JOIN PESSOAJURIDICA PJ ON P.ID = PJ.ID "
            + " WHERE L.SALDO > 0 AND l.RECEITADEINTEGRACAO = 0 AND l.INTEGRACAOTRIBCONT_ID is null  "
            + " AND L.EXERCICIO_ID = :idExercicio "
            + " AND L.unidadeOrganizacional_id = :idUnidade "
            + " AND (L.NUMERO LIKE :PARAM " +
            "       OR (LOWER(PF.CPF) LIKE :PARAM)" +
            "       OR (LOWER(PF.NOME) LIKE :PARAM) "
            + "     OR (LOWER(PJ.CNPJ) LIKE :PARAM) " +
            "       OR (LOWER(PJ.NOMEFANTASIA) LIKE :PARAM)" +
            "       OR (LOWER(PJ.NOMEREDUZIDO) LIKE :PARAM)" +
            "       OR (LOWER(PJ.RAZAOSOCIAL) LIKE :PARAM) " +
            "       OR (replace(C.CODIGO,'.','') LIKE :SEMPONTO)" +
            "       OR (C.CODIGO) LIKE :CONTA) "
            + " ORDER BY L.DATALANCAMENTO DESC ";
        Query q = getEntityManager().createNativeQuery(sql, LancamentoReceitaOrc.class);
        q.setParameter("PARAM", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("CONTA", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("idExercicio", exercicio.getId());
        q.setParameter("idUnidade", unidadeOrganizacional.getId());
        q.setParameter("SEMPONTO", "%" + parte.replace(".", "").toLowerCase().trim() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }


    public List<LancamentoReceitaOrc> recuperarReceitaPorPeriodo(Exercicio exercicio, HierarquiaOrganizacional hierarquia, Date dataInicial, Date dataFinal, SubConta subConta, Conta contaReceita) {
        String sql = "SELECT L.* FROM LANCAMENTORECEITAORC L            "
            + " INNER JOIN PESSOA P ON L.PESSOA_ID = P.ID           "
            + " INNER JOIN RECEITALOA RL ON L.RECEITALOA_ID = RL.ID "
            + " INNER JOIN CONTA C ON RL.CONTADERECEITA_ID = C.ID   "
            + " LEFT JOIN PESSOAFISICA PF ON P.ID = PF.ID           "
            + " LEFT JOIN PESSOAJURIDICA PJ ON P.ID = PJ.ID         "
            + " WHERE L.SALDO > 0                                   "
            + " AND L.EXERCICIO_ID = :idExercicio                   "
            + " AND C.ID = :idContaReceita                              "
            + " AND l.dataLancamento BETWEEN to_date(:dataInicial, 'dd/MM/yyyy') AND to_date(:dataFinal, 'dd/MM/yyyy') ";
        if (subConta != null) {
            if (subConta.getId() != null) {
                sql += " AND L.subConta_id = :idSubConta                ";
            }
        }
        if (hierarquia != null) {
            if (hierarquia.getId() != null) {
                sql += " AND l.unidadeOrganizacional_id = :idUnidade    ";
            }
        }
        sql += " ORDER BY L.NUMERO, L.DATALANCAMENTO DESC               ";
        Query q = getEntityManager().createNativeQuery(sql, LancamentoReceitaOrc.class);
        q.setParameter("idContaReceita", contaReceita.getId());
        q.setParameter("idExercicio", exercicio.getId());
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        if (subConta != null) {
            if (subConta.getId() != null) {
                q.setParameter("idSubConta", subConta.getId());
            }
        }
        if (hierarquia != null) {
            if (hierarquia.getId() != null) {
                q.setParameter("idUnidade", hierarquia.getSubordinada().getId());
            }
        }
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public List<LancamentoReceitaOrc> listaReceitaRealizadaPorExercicio(String parte, Exercicio exercicio) {
        String sql = "SELECT L.* FROM LANCAMENTORECEITAORC L "
            + " INNER JOIN PESSOA P ON L.PESSOA_ID = P.ID "
            + " INNER JOIN RECEITALOA RL ON L.RECEITALOA_ID = RL.ID"
            + " INNER JOIN CONTA C ON RL.CONTADERECEITA_ID = C.ID"
            + " LEFT JOIN PESSOAFISICA PF ON P.ID = PF.ID "
            + " LEFT JOIN PESSOAJURIDICA PJ ON P.ID = PJ.ID "
            + " WHERE L.EXERCICIO_ID = :idExercicio "
            + " AND (L.NUMERO LIKE :PARAM " +
            "       OR (LOWER(PF.CPF) LIKE :PARAM)" +
            "       OR (LOWER(PF.NOME) LIKE :PARAM) "
            + "     OR (LOWER(PJ.CNPJ) LIKE :PARAM) " +
            "       OR (LOWER(PJ.NOMEFANTASIA) LIKE :PARAM)" +
            "       OR (LOWER(PJ.NOMEREDUZIDO) LIKE :PARAM)" +
            "       OR (LOWER(PJ.RAZAOSOCIAL) LIKE :PARAM) " +
            "       OR (replace(C.CODIGO,'.','') LIKE :SEMPONTO)" +
            "       OR (C.CODIGO) LIKE :CONTA) "
            + " ORDER BY L.NUMERO, L.DATALANCAMENTO DESC ";
        Query q = getEntityManager().createNativeQuery(sql, LancamentoReceitaOrc.class);
        q.setParameter("PARAM", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("CONTA", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("idExercicio", exercicio.getId());
        q.setParameter("SEMPONTO", "%" + parte.replace(".", "").toLowerCase().trim() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<LancamentoReceitaOrc> buscarTodasReceitaRealizadaPorExercicioUnidades(Exercicio exercicio, List<HierarquiaOrganizacional> listaUnidades, Date dataInicial, Date dataFinal) {
        List<Long> unidades = Lists.newArrayList();
        for (HierarquiaOrganizacional lista : listaUnidades) {
            unidades.add(lista.getSubordinada().getId());
        }

        String sql = "SELECT L.* FROM LANCAMENTORECEITAORC L "
            + " WHERE L.EXERCICIO_ID = :idExercicio "
            + " and L.UNIDADEORGANIZACIONAL_ID in (:unidades)"
            + " AND trunc(l.dataLancamento) BETWEEN to_date(:dataInicial, 'dd/MM/yyyy') AND to_date(:dataFinal, 'dd/MM/yyyy') "
            + " ORDER BY L.NUMERO, L.DATALANCAMENTO DESC ";
        Query q = getEntityManager().createNativeQuery(sql, LancamentoReceitaOrc.class);
        q.setParameter("idExercicio", exercicio.getId());
        q.setParameter("unidades", unidades);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        return q.getResultList();
    }

    @Override
    public LancamentoReceitaOrc recuperar(Object id) {
        LancamentoReceitaOrc cd = getEntityManager().find(LancamentoReceitaOrc.class, id);
        cd.getReceitaLOA().getReceitaLoaFontes().size();
        cd.getSubConta().getUnidadesOrganizacionais().size();
        cd.getSubConta().getSubContaFonteRecs().size();
        cd.getLancReceitaFonte().size();
        return cd;
    }

    public boolean validaFontesNaReceitaEConta(LancamentoReceitaOrc lancamento, Long conjuntoFontes) throws ExcecaoNegocioGenerica {

        List<FonteDeRecursos> listaFonteReceita = recuperarFontesDaReceitaLoa(lancamento, conjuntoFontes);
        List<FonteDeRecursos> listaFonteSubConta = recuperarFontesDaSubConta(lancamento);

        for (FonteDeRecursos fonteDeRecursos : listaFonteReceita) {
            if (listaFonteSubConta.contains(fonteDeRecursos)) {
                return true;
            }
        }
        throw new ExcecaoNegocioGenerica(" As fontes de recursos da receita: " + lancamento.getReceitaLOA().getContaDeReceita() + ". Não conferem com as fontes de recursos da conta financeira: " + lancamento.getSubConta().getCodigo() + ".");
    }


    public List<FonteDeRecursos> recuperarFontesDaSubConta(LancamentoReceitaOrc lancamento) {
        List<FonteDeRecursos> listaFonteConta = new ArrayList<FonteDeRecursos>();
        SubConta subConta = subContaFacade.recuperar(lancamento.getSubConta().getId());
        for (SubContaFonteRec sfr : subConta.getSubContaFonteRecs()) {
            if (lancamento.getExercicio().equals(sfr.getFonteDeRecursos().getExercicio())) {
                listaFonteConta.add(sfr.getFonteDeRecursos());
            }
        }
        return listaFonteConta;
    }

    public List<ContaDeDestinacao> buscarContasDeDestinacao(LancamentoReceitaOrc lancamento) {
        List<ContaDeDestinacao> contas = Lists.newArrayList();
        SubConta subConta = subContaFacade.recuperar(lancamento.getSubConta().getId());
        for (SubContaFonteRec sfr : subConta.getSubContaFonteRecs()) {
            if (lancamento.getExercicio().equals(sfr.getContaDeDestinacao().getExercicio())) {
                contas.add(sfr.getContaDeDestinacao());
            }
        }
        return contas;
    }

    public List<FonteDeRecursos> recuperarFontesDaReceitaLoa(LancamentoReceitaOrc lancamento, Long conjuntoFontes) {

        List<FonteDeRecursos> listaFonteReceita = new ArrayList<>();

        ReceitaLOA receitaLOA = receitaLOAFacade.recuperar(lancamento.getReceitaLOA().getId());

        for (ReceitaLOAFonte rlf : receitaLOA.getReceitaLoaFontes()) {
            if (conjuntoFontes.equals(rlf.getItem())) {
                if (lancamento.getExercicio().equals(rlf.getDestinacaoDeRecursos().getFonteDeRecursos().getExercicio())) {
                    if (rlf.getPercentual().compareTo(BigDecimal.ZERO) != 0) {
                        listaFonteReceita.add(rlf.getDestinacaoDeRecursos().getFonteDeRecursos());
                    }
                }
            }
        }
        return listaFonteReceita;
    }


    public ConfigReceitaRealizada getConfiguracaoReceitaRealizada(LancamentoReceitaOrc entity) {
        ConfigReceitaRealizada configuracao = new ConfigReceitaRealizada();
        return configuracao;
    }

    public void contabilizarLancamentoReceitaOrcFonte(LancamentoReceitaOrc entity) throws ExcecaoNegocioGenerica {
        contabilizarLancamentoReceitaOrcFonte(entity, false);
    }

    public void contabilizarLancamentoReceitaOrcFonte(LancamentoReceitaOrc entity, boolean simulacao) throws ExcecaoNegocioGenerica {
        for (LancReceitaFonte lancReceitaFonte : entity.getLancReceitaFonte()) {
            contabilizarLancamentoReceitaOrcFonte(lancReceitaFonte, simulacao);
        }
    }

    public void definirEventoReceitaRealizada(LancamentoReceitaOrc entity) {
        try {
            entity.setEventoContabil(null);
            if (entity.getReceitaLOA().getContaDeReceita() != null
                && entity.getOperacaoReceitaRealizada() != null) {
                ConfigReceitaRealizada configuracao = configReceitaRealizadaFacade.buscarEventoPorContaReceita(
                    entity.getReceitaLOA().getContaDeReceita(),
                    TipoLancamento.NORMAL,
                    entity.getDataLancamento(),
                    entity.getOperacaoReceitaRealizada());
                if (configuracao != null) {
                    entity.setEventoContabil(configuracao.getEventoContabil());
                }
            }
        } catch (ExcecaoNegocioGenerica e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }

    public void contabilizarLancamentoReceitaOrcFonte(LancReceitaFonte entity) throws ExcecaoNegocioGenerica {
        contabilizarLancamentoReceitaOrcFonte(entity, false);
    }

    public void contabilizarLancamentoReceitaOrcFonte(LancReceitaFonte entity, boolean simulacao) throws ExcecaoNegocioGenerica {
        ConfigReceitaRealizada configuracao = configReceitaRealizadaFacade.buscarEventoPorContaReceita(
            entity.getLancReceitaOrc().getReceitaLOA().getContaDeReceita(),
            TipoLancamento.NORMAL,
            entity.getLancReceitaOrc().getDataLancamento(),
            entity.getLancReceitaOrc().getOperacaoReceitaRealizada());

        if (configuracao != null && configuracao.getEventoContabil() != null) {
            entity.setEventoContabil(configuracao.getEventoContabil());
        } else {
            throw new ExcecaoNegocioGenerica(" Evento Contábil não encontrado para a operação selecionada na contabilização de Receita Realizada.");
        }
        if (configuracao != null && configuracao.getId() != null) {
            entity.gerarHistoricos();

            ParametroEvento parametroEvento = new ParametroEvento();
            parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
            parametroEvento.setDataEvento(entity.getLancReceitaOrc().getDataLancamento());
            parametroEvento.setUnidadeOrganizacional(entity.getLancReceitaOrc().getReceitaLOA().getEntidade());
            parametroEvento.setEventoContabil(configuracao.getEventoContabil());
            parametroEvento.setExercicio(entity.getLancReceitaOrc().getExercicio());
            parametroEvento.setIdOrigem(entity.getId().toString());
            parametroEvento.setClasseOrigem(entity.getClass().getSimpleName());

            ItemParametroEvento item = new ItemParametroEvento();
            item.setValor(entity.getValor());
            item.setParametroEvento(parametroEvento);
            item.setTagValor(TagValor.LANCAMENTO);
            item.setOperacaoClasseCredor(classeCredorFacade.recuperaOperacaoAndVigenciaClasseCredor(entity.getLancReceitaOrc().getPessoa(), entity.getLancReceitaOrc().getClasseCredor(), entity.getLancReceitaOrc().getDataLancamento()));

            List<ObjetoParametro> objetos = Lists.newArrayList();
            if (!simulacao) {
                objetos.add(new ObjetoParametro(entity, item));
            }
            objetos.add(new ObjetoParametro(entity.getLancReceitaOrc().getReceitaLOA().getContaDeReceita(), item));
            objetos.add(new ObjetoParametro(entity.getLancReceitaOrc().getSubConta(), item));
            objetos.add(new ObjetoParametro(entity.getLancReceitaOrc().getClasseCredor(), item));
            objetos.add(new ObjetoParametro(((ContaDeDestinacao) entity.getReceitaLoaFonte().getDestinacaoDeRecursos()).getFonteDeRecursos(), item));

            if (entity.getLancReceitaOrc().getDividaPublica() != null) {
                objetos.add(new ObjetoParametro(entity.getLancReceitaOrc().getDividaPublica(), item));
                objetos.add(new ObjetoParametro(entity.getLancReceitaOrc().getDividaPublica().getCategoriaDividaPublica(), item));
            }

            configAlienacaoAtivosFacade.adicionarObjetoParametro(entity.getEventoContabil(), entity.getLancReceitaOrc().getDataLancamento(), objetos, item, ObjetoParametro.TipoObjetoParametro.AMBOS);

            item.setObjetoParametros(objetos);
            parametroEvento.getItensParametrosEvento().add(item);
            contabilizacaoFacade.contabilizar(parametroEvento, simulacao);
        } else {
            throw new ExcecaoNegocioGenerica(" Não existe configuração de Receita Realizada");
        }
    }

    public void gerarSaldoSubConta(LancamentoReceitaOrc entity) throws ExcecaoNegocioGenerica {

        for (LancReceitaFonte lrf : entity.getLancReceitaFonte()) {
            if (OperacaoReceita.retornarOperacoesReceitaDeducao().contains(entity.getOperacaoReceitaRealizada())) {
                saldoSubContaFacade.gerarSaldoFinanceiro(entity.getUnidadeOrganizacional(),
                    entity.getSubConta(),
                    lrf.getReceitaLoaFonte().getDestinacaoDeRecursos(),
                    lrf.getValor(),
                    TipoOperacao.DEBITO,
                    entity.getDataLancamento(),
                    entity.getEventoContabil(),
                    lrf.getHistoricoRazao(),
                    MovimentacaoFinanceira.RECEITA_REALIZADA,
                    entity.getUuid(),
                    true);
            } else {
                saldoSubContaFacade.gerarSaldoFinanceiro(entity.getUnidadeOrganizacional(),
                    entity.getSubConta(),
                    lrf.getReceitaLoaFonte().getDestinacaoDeRecursos(),
                    lrf.getValor(),
                    TipoOperacao.CREDITO,
                    entity.getDataLancamento(),
                    entity.getEventoContabil(),
                    lrf.getHistoricoRazao(),
                    MovimentacaoFinanceira.RECEITA_REALIZADA,
                    entity.getUuid(),
                    true);
            }
        }
    }

    private void gerarSaldoDividaPublica(LancamentoReceitaOrc entity) throws Exception {
        ConfiguracaoContabil configuracaoContabil = configuracaoContabilFacade.configuracaoContabilVigente();
        if (configuracaoContabil.getContasReceita().isEmpty()) {
            throw new ExcecaoNegocioGenerica("É necessário informar as Contas de Receita para geração de Saldo da Dívida Pública na Configuração Contábil.");
        }
        if (entity.getDividaPublica() != null &&
            hasContaDeReceitaConfiguradaParaGerarSaldoDividaPublica(entity, configuracaoContabil)) {
            saldoDividaPublicaFacade.gerarMovimento(
                entity.getDataLancamento(),
                entity.getValor(),
                entity.getUnidadeOrganizacional(),
                entity.getDividaPublica(),
                OperacaoMovimentoDividaPublica.RECEITA_OPERACAO_CREDITO,
                false,
                OperacaoDiarioDividaPublica.RECEITA_REALIZADA,
                Intervalo.LONGO_PRAZO,
                entity.getContaDeDestinacao(),
                true);
        }
    }

    private boolean hasContaDeReceitaConfiguradaParaGerarSaldoDividaPublica(LancamentoReceitaOrc entity, ConfiguracaoContabil configuracaoContabil) {
        for (ConfiguracaoContabilContaReceita configuracaoContabilContaReceita : configuracaoContabil.getContasReceita()) {
            if (entity.getReceitaLOA().getContaDeReceita().getCodigo().startsWith(configuracaoContabilContaReceita.getContaReceita().getCodigoSemZerosAoFinal())) {
                return true;
            }
        }
        return false;
    }

    public void geraSaldoReceitaRealizada(LancamentoReceitaOrc entity) throws ExcecaoNegocioGenerica {
        for (LancReceitaFonte lrf : entity.getLancReceitaFonte()) {
            saldoReceitaORCFacade.geraSaldo(
                TipoSaldoReceitaORC.LANCAMENTORECEITAORC,
                entity.getDataLancamento(),
                entity.getUnidadeOrganizacional(),
                entity.getReceitaLOA().getContaDeReceita(),
                lrf.getReceitaLoaFonte().getDestinacaoDeRecursos().getFonteDeRecursos(),
                lrf.getValor());
        }
    }

    public void geraHistoricos(LancamentoReceitaOrc entity) throws ExcecaoNegocioGenerica {
        for (LancReceitaFonte lrf : entity.getLancReceitaFonte()) {
            lrf.gerarHistoricos();
        }
    }

    private void validaDiferenca(LancamentoReceitaOrc entity) {
        BigDecimal diferenca = calculaDiferenca(entity);
        if (diferenca.compareTo(BigDecimal.ZERO) != 0) {
            throw new ExcecaoNegocioGenerica("Existe diferença de " + Util.formataValor(diferenca) + " do que esta Lançando com o que foi previsto. Verifique as porcentagens lançadas no Orçamento.");
        }
    }

    public void salvarNovaReceita(LancamentoReceitaOrc entity, Long conjuntoFontes) {
        try {
            if (getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
                throw new ExcecaoNegocioGenerica(getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
            } else {
                hierarquiaOrganizacionalFacade.validaVigenciaHIerarquiaAdministrativaOrcamentaria(entity.getUnidadeOrganizacionalAdm(), entity.getUnidadeOrganizacional(), entity.getDataLancamento());
                if (!entity.getReceitaDeIntegracao()) {
                    validaFontesNaReceitaEConta(entity, conjuntoFontes);
                }
                validaCalculoDiferencaDaFonte(entity);
                validaDiferenca(entity);

                entity = salvarReceitaRealizada(entity);

                gerarSaldosParaMovimentos(entity);

                contabilizarLancamentoReceitaOrcFonte(entity);
                portalTransparenciaNovoFacade.salvarPortal(new LancamentoReceitaOrcPortal(entity));
            }
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    private LancamentoReceitaOrc salvarReceitaRealizada(LancamentoReceitaOrc lancamentoReceitaOrc) {
        if (lancamentoReceitaOrc.getId() == null) {
            lancamentoReceitaOrc.setNumero(singletonGeradorCodigoContabil.getNumeroReceitaRealizada(lancamentoReceitaOrc.getExercicio(), lancamentoReceitaOrc.getDataLancamento()));
            lancamentoReceitaOrc.setSaldo(lancamentoReceitaOrc.getValor());
            lancamentoReceitaOrc.setTipoOperacao(TipoOperacaoORC.NORMAL);
            geraHistoricos(lancamentoReceitaOrc);
            em.persist(lancamentoReceitaOrc);
        } else {
            geraHistoricos(lancamentoReceitaOrc);
            lancamentoReceitaOrc = em.merge(lancamentoReceitaOrc);
        }
        return lancamentoReceitaOrc;
    }

    private void gerarSaldosParaMovimentos(LancamentoReceitaOrc entity) throws Exception {
        liberaValorParaConvenioReceita(entity);
        geraSaldoReceitaRealizada(entity);
        gerarSaldoSubConta(entity);
        gerarSaldoDividaAtivaAndCreditoReceber(entity);
        gerarSaldoDividaPublica(entity);
        gerarSaldoGrupoPatrimonial(entity);
    }

    private void gerarSaldoGrupoPatrimonial(LancamentoReceitaOrc entity) {
        ConfigReceitaRealizadaBensMoveis configReceitaRealizadaBensMoveis = configReceitaRealizadaGrupoPatrimonialFacade.buscarConfiguracaoPorContaReceita(entity.getReceitaLOA().getContaDeReceita(), entity.getDataLancamento());
        if (configReceitaRealizadaBensMoveis != null) {
            saldoGrupoBemMovelFacade.geraSaldoGrupoBemMoveis(entity.getUnidadeOrganizacional(),
                configReceitaRealizadaBensMoveis.getGrupoBem(),
                entity.getValor(),
                configReceitaRealizadaBensMoveis.getTipoGrupo(),
                entity.getDataLancamento(),
                TipoOperacaoBensMoveis.ALIENACAO_BENS_MOVEIS,
                TipoLancamento.NORMAL,
                TipoOperacao.CREDITO,
                true);
        }
    }

    private void liberaValorParaConvenioReceita(LancamentoReceitaOrc lancamentoReceitaOrc) {
        if (lancamentoReceitaOrc.getConvenioReceita() != null) {
            convenioReceitaFacade.adicionaLiberacao(lancamentoReceitaOrc);
        }
    }

    private boolean validaCalculoDiferencaDaFonte(LancamentoReceitaOrc lancamentoReceitaOrc) {
        if (calculaDiferenca(lancamentoReceitaOrc).compareTo(BigDecimal.ZERO) == 0) {
            return true;
        } else {
            throw new ExcecaoNegocioGenerica(" Por favor corrigir a diferença entre o total dos lançamentos e o valor da receita realizada.");
        }
    }

    public BigDecimal somaLancamentos(LancamentoReceitaOrc lancamento) {
        BigDecimal soma = BigDecimal.ZERO;
        if (!lancamento.getLancReceitaFonte().isEmpty()) {
            for (LancReceitaFonte lrf : lancamento.getLancReceitaFonte()) {
                soma = soma.add(lrf.getValor()).setScale(2, RoundingMode.HALF_UP);
            }
        }
        return soma;
    }

    public BigDecimal calculaDiferenca(LancamentoReceitaOrc lancamento) {
        BigDecimal diferenca = BigDecimal.ZERO;
        diferenca = somaLancamentos(lancamento).subtract(lancamento.getValor().setScale(2, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP);
        return diferenca;
    }

    public void geraLancamentos(LancamentoReceitaOrc lancamento, Long conjuntoFontes) throws ExcecaoNegocioGenerica {
        ReceitaLOA rl = recuperarReceitaLoa(lancamento);
        validarLancamentoReceita(lancamento, rl, conjuntoFontes);
        gerarReceitaRealizada(lancamento, rl, conjuntoFontes);
    }


    public void gerarLancamentoIntegracao(LancamentoReceitaOrc lancamento) throws ExcecaoNegocioGenerica {
        if (lancamento.getLancReceitaFonte() != null && !lancamento.getLancReceitaFonte().isEmpty()) {
            return;
        }

        ReceitaLOA rl = recuperarReceitaLoa(lancamento);
        try {
            validarLancamentoReceita(lancamento, rl, null);
            List<FonteDeRecursos> fontesDeRecursosIguais = new ArrayList<>();
            List<FonteDeRecursos> fontesSubContaRecuperada = recuperarFontesDaSubConta(lancamento);

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
                gerarReceitaRealizadaIntegracao(lancamento, rl);
            } else {
                throw new ExcecaoNegocioGenerica("As fontes de recursos da receita: " + rl.getContaDeReceita() + ". Não conferem com as fontes de recursos da conta financeira: " + lancamento.getSubConta().getCodigo() + ".");
            }
        } catch (ExcecaoNegocioGenerica e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }

    private ReceitaLOA recuperarReceitaLoa(LancamentoReceitaOrc lancamento) {
        ReceitaLOA rl = lancamento.getReceitaLOA();
        if (!Hibernate.isInitialized(lancamento.getReceitaLOA().getReceitaLoaFontes())) {
            rl = receitaLOAFacade.recarregar(lancamento.getReceitaLOA());
        }
        return rl;
    }

    private Boolean validarLancamentoReceita(LancamentoReceitaOrc lancamento, ReceitaLOA rl, Long conjuntoFontes) {

        if (lancamento.getReceitaDeIntegracao()) {
            if (rl == null) {
                throw new ExcecaoNegocioGenerica(" Conta de Receita não encontrada para gerar lançamentos para a fonte. ");
            }
            if (lancamento.getSubConta() == null) {
                throw new ExcecaoNegocioGenerica(" Conta Financeira não encontrada para gerar lançamentos para a fonte. ");
            }
            if (lancamento.getValor().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ExcecaoNegocioGenerica(" O valor para a Receita está zerado, portando não é possível gerar lançamentos para a fonte.");
            }
        } else {
            if (rl == null) {
                throw new ExcecaoNegocioGenerica(" O campo Conta de Receita deve ser informado. ");
            }
            if (lancamento.getSubConta() == null) {
                throw new ExcecaoNegocioGenerica(" O campo Conta Financeira deve ser informado. ");
            }
            if (lancamento.getValor().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ExcecaoNegocioGenerica(" O campo Valor deve ser maior que zero(0).");
            }
            if (!lancamento.getLancReceitaFonte().isEmpty()) {
                throw new ExcecaoNegocioGenerica(" Para efeturar um novo cálculo, é necessário remover o(s) lançamento(s) adicionado(s).");
            }
            if (validaFontesNaReceitaEConta(lancamento, conjuntoFontes)) {
                return true;
            }
        }

        return true;
    }

    private void gerarReceitaRealizadaIntegracao(LancamentoReceitaOrc lancamento, ReceitaLOA rl) {
        BigDecimal soma = BigDecimal.ZERO;
        BigDecimal somaPercentual = BigDecimal.ZERO;
        BigDecimal diferenca = BigDecimal.ZERO;

        for (ReceitaLOAFonte rlf : rl.getReceitaLoaFontePorConjunto(rl.getConjuntoFonte())) {
            LancReceitaFonte lancReceita = new LancReceitaFonte();
            lancReceita.setReceitaLoaFonte(rlf);
            lancReceita.setItem(rlf.getItem());
            lancReceita.setValor((lancamento.getValor().multiply(rlf.getPercentual())).divide(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP));
            lancReceita.setLancReceitaOrc(lancamento);
            lancamento.getLancReceitaFonte().add(lancReceita);

            soma = soma.add(lancReceita.getValor());
            somaPercentual = somaPercentual.add(rlf.getPercentual());
            diferenca = lancamento.getValor().subtract(soma);
        }
        if (somaPercentual.compareTo(BigDecimal.valueOf(100l)) != 0) {
            throw new ExcecaoNegocioGenerica("O Percentual Previsto no orçamento está maior que 100%. Entre em contato com o orçamento para correção.");
        }
        adicionarDiferencaValorParaFonte(lancamento, diferenca);
    }


    private void gerarReceitaRealizada(LancamentoReceitaOrc lancamento, ReceitaLOA rl, Long conjuntoFontes) {
        BigDecimal soma = BigDecimal.ZERO;
        BigDecimal somaPercentual = BigDecimal.ZERO;
        BigDecimal diferenca = BigDecimal.ZERO;

        try {
            for (ReceitaLOAFonte rlf : rl.getReceitaLoaFontes()) {
                if (rlf.getItem().equals(conjuntoFontes)) {
                    LancReceitaFonte lancReceita = new LancReceitaFonte();
                    lancReceita.setReceitaLoaFonte(rlf);
                    lancReceita.setItem(rlf.getItem());
                    lancReceita.setValor((lancamento.getValor().multiply(rlf.getPercentual())).divide(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP));
                    lancReceita.setLancReceitaOrc(lancamento);
                    lancamento.getLancReceitaFonte().add(lancReceita);
                    soma = soma.add(lancReceita.getValor());
                    somaPercentual = somaPercentual.add(rlf.getPercentual());
                }
            }
            if (somaPercentual.compareTo(new BigDecimal(100)) != 0) {
                throw new ExcecaoNegocioGenerica("O Percentual Previsto no orçamento está maior que 100%. Entre em contato com o Orçamento para correção.");
            }
            diferenca = lancamento.getValor().subtract(soma);
            adicionarDiferencaValorParaFonte(lancamento, diferenca);
        } catch (ExcecaoNegocioGenerica ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }


    private void adicionarDiferencaValorParaFonte(LancamentoReceitaOrc lancamento, BigDecimal diferenca) {
        if (diferenca.compareTo(BigDecimal.ZERO) != 0) {
            for (LancReceitaFonte lrlf : lancamento.getLancReceitaFonte()) {
                if (lrlf.getReceitaLoaFonte().getRounding().equals(true)) {
                    lrlf.setValor(lrlf.getValor().add(diferenca).setScale(2, RoundingMode.HALF_UP));
                }
            }
        }
    }

    @Override
    public void salvar(LancamentoReceitaOrc entity) {
        geraHistoricos(entity);
        em.merge(entity);
        portalTransparenciaNovoFacade.salvarPortal(new LancamentoReceitaOrcPortal(entity));
    }

    public void salvarReceitaNoEstorno(LancamentoReceitaOrc lancamentoReceitaOrc) {
        em.merge(lancamentoReceitaOrc);
    }

    public ClasseCredorFacade getClasseCredorFacade() {
        return classeCredorFacade;
    }

    public ReprocessamentoLancamentoContabilSingleton getReprocessamentoLancamentoContabilSingleton() {
        return reprocessamentoLancamentoContabilSingleton;
    }

    public ConfiguracaoContabilFacade getConfiguracaoContabilFacade() {
        return configuracaoContabilFacade;
    }

    @Override
    public void contabilizarReprocessamento(EntidadeContabil entidadeContabil) {
        contabilizarLancamentoReceitaOrcFonte((LancReceitaFonte) entidadeContabil);
    }

    public void estornarConciliacao(LancamentoReceitaOrc lancamentoReceitaOrc) {
        try {
            lancamentoReceitaOrc.setDataConciliacao(null);
            getEntityManager().merge(lancamentoReceitaOrc);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Erro ao estornar a conciliação. Consulte o suporte!");
        }
    }

    public void baixar(LancamentoReceitaOrc lancamentoReceitaOrc) {
        try {
            lancamentoReceitaOrc.setSaldo(lancamentoReceitaOrc.getSaldo().subtract(lancamentoReceitaOrc.getValor()));
            em.merge(lancamentoReceitaOrc);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Erro ao baixar." + e.getMessage() + ". Consulte o suporte");
        }
    }

    public void estornarBaixa(LancamentoReceitaOrc lancamentoReceitaOrc) {
        try {
            if (listaEstornosReceitaRealizada(lancamentoReceitaOrc).isEmpty()) {
                lancamentoReceitaOrc.setSaldo(lancamentoReceitaOrc.getSaldo().add(lancamentoReceitaOrc.getValor()));
            } else {
                BigDecimal valor = lancamentoReceitaOrc.getValor().subtract(getSomaEstornoReceitaRealizada(lancamentoReceitaOrc));
                lancamentoReceitaOrc.setSaldo(lancamentoReceitaOrc.getSaldo().add(valor));
            }
            em.merge(lancamentoReceitaOrc);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Erro ao estornar a baixa. Consulte o suporte!");
        }
    }

    public List<ReceitaORCEstorno> listaEstornosReceitaRealizada(LancamentoReceitaOrc lanc) {
        String sql = " select est.* from receitaorcestorno est " +
            " inner join lancamentoreceitaorc lanc on lanc.id = est.lancamentoreceitaorc_id " +
            " where lanc.id = :param ";
        Query q = em.createNativeQuery(sql, ReceitaORCEstorno.class);
        q.setParameter("param", lanc.getId());
        return q.getResultList();
    }


    public List<LancamentoReceitaOrc> recuperarReceitaRealizadaDaPessoa(Pessoa pessoa) {
        String sql = " select r.* from lancamentoreceitaorc r " +
            "       where r.pessoa_id = :pessoa           ";
        Query q = em.createNativeQuery(sql, LancamentoReceitaOrc.class);
        q.setParameter("pessoa", pessoa.getId());
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        }
        return q.getResultList();
    }

    public BigDecimal getSomaEstornoReceitaRealizada(LancamentoReceitaOrc lanc) {
        BigDecimal estornos = new BigDecimal(BigInteger.ZERO);
        for (ReceitaORCEstorno est : listaEstornosReceitaRealizada(lanc)) {
            estornos = estornos.add(est.getValor());
        }
        return estornos;
    }

    public ContaBancariaEntidadeFacade getBancariaEntidadeFacade() {
        return bancariaEntidadeFacade;
    }

    public ConvenioReceitaFacade getConvenioReceitaFacade() {
        return convenioReceitaFacade;
    }

    public DividaPublicaFacade getDividaPublicaFacade() {
        return dividaPublicaFacade;
    }

    public FonteDeRecursosFacade getFonteDeRecursosFacade() {
        return fonteDeRecursosFacade;
    }

    public HistoricoContabilFacade getHistoricoContabilFacade() {
        return historicoContabilFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public ReceitaLOAFacade getReceitaLOAFacade() {
        return receitaLOAFacade;
    }

    public SaldoSubContaFacade getSaldoSubContaFacade() {
        return saldoSubContaFacade;
    }

    public SubContaFacade getSubContaFacade() {
        return subContaFacade;
    }

    public SaldoCreditoReceberFacade getSaldoCreditoReceberFacade() {
        return saldoCreditoReceberFacade;
    }

    public SaldoDividaAtivaContabilFacade getSaldoDividaAtivaContabilFacade() {
        return saldoDividaAtivaContabilFacade;
    }

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    public ConfigReceitaRealizadaFacade getConfigReceitaRealizadaFacade() {
        return configReceitaRealizadaFacade;
    }

    public LoteBaixaFacade getLoteBaixaFacade() {
        return loteBaixaFacade;
    }

    public IntegracaoTributarioContabilFacade getIntegracaoTributarioContabilFacade() {
        return integracaoTributarioContabilFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public void setHierarquiaOrganizacionalFacade(HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade) {
        this.hierarquiaOrganizacionalFacade = hierarquiaOrganizacionalFacade;
    }

    public FaseFacade getFaseFacade() {
        return faseFacade;
    }

    public SingletonConcorrenciaContabil getSingletonConcorrenciaContabil() {
        return singletonConcorrenciaContabil;
    }

    public SaldoDividaPublicaFacade getSaldoDividaPublicaFacade() {
        return saldoDividaPublicaFacade;
    }

    public CodigoCOFacade getCodigoCOFacade() {
        return codigoCOFacade;
    }

    @Override
    public List<ConsultaMovimentoContabil> criarConsulta(List<FiltroConsultaMovimentoContabil> filtros) {
        ConsultaMovimentoContabil consulta = new ConsultaMovimentoContabil(LancReceitaFonte.class, filtros);
        consulta.incluirJoinsComplementar(" inner join LancamentoReceitaOrc lanc on obj.lancReceitaOrc_id = lanc.id ");
        consulta.incluirJoinsOrcamentoReceita("lanc.receitaloa_id", "obj.receitaloafonte_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_INICIAL, "lanc.dataLancamento");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_FINAL, "lanc.dataLancamento");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.UNIDADE, "lanc.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.LISTA_UNIDADE, "lanc.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.PESSOA, "lanc.fornecedor_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CLASSE, "lanc.classecredor_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.EVENTO_CONTABIL, "obj.eventocontabil_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.NUMERO, "lanc.numero");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.HISTORICO, "lanc.historicoRazao");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.VALOR, "lanc.valor");
        return Arrays.asList(consulta);
    }

    public List<Object[]> buscarReceitas(Exercicio exercicio) {
        String sql = " select ano, mes, dia, contaCodigo, fonteCodigo, valor from ( " +
            " select extract(day from lanc.datalancamento) as dia, " +
            "        extract(month from lanc.datalancamento) as mes, " +
            "        extract(year from lanc.datalancamento) as ano, " +
            "        c.codigo as contaCodigo, " +
            "        fr.codigo as fonteCodigo, " +
            " case when lanc.operacaoReceitarealizada in " +
            OperacaoReceita.montarClausulaIn(OperacaoReceita.retornarOperacoesReceitaDeducao()) +
            " then COALESCE(lrf.VALOR,0) * -1 else COALESCE(lrf.VALOR,0) end as valor " +
            "   from lancamentoreceitaorc lanc " +
            " inner join receitaloa rec on lanc.receitaloa_id = rec.id " +
            " inner join lancreceitafonte lrf on lrf.lancreceitaorc_id = lanc.id " +
            " INNER JOIN ReceitaLOAFonte rlf ON lrf.receitaloafonte_id = RLF.id " +
            " INNER JOIN ContaDeDestinacao cd ON cd.ID = RLF.DESTINACAODERECURSOS_ID " +
            " INNER JOIN FONTEDERECURSOS FR ON cd.fonteDeRecursos_id = fr.id " +
            " inner join conta c on c.id = rec.CONTADERECEITA_ID " +
            " where lanc.EXERCICIO_ID = :exercicio " +
            " union all " +
            " select extract(day from est.DATAESTORNO) as dia, " +
            "        extract(month from est.DATAESTORNO) as mes, " +
            "        extract(year from est.DATAESTORNO) as ano, " +
            "        c.codigo as contaCodigo, " +
            "        fr.codigo as fonteCodigo, " +
            " case when est.operacaoReceitarealizada in " +
            OperacaoReceita.montarClausulaIn(OperacaoReceita.retornarOperacoesReceitaDeducao()) +
            " then COALESCE(RFE.VALOR,0) else COALESCE(RFE.VALOR,0) *-1 end as valor " +
            "   from receitaorcestorno est " +
            " INNER JOIN RECEITALOA RL ON est.RECEITALOA_ID = RL.ID " +
            " INNER JOIN CONTARECEITA CR ON CR.ID = RL.CONTADERECEITA_ID " +
            " INNER JOIN CONTA C ON CR.ID = C.ID " +
            " INNER JOIN ReceitaORCFonteEstorno RFE on est.id = RFE.RECEITAORCESTORNO_ID " +
            " INNER JOIN RECEITALOAFONTE RLF      ON RFE.RECEITALOAFONTE_ID = RLF.ID " +
            " INNER JOIN CONTADEDESTINACAO CD ON CD.ID = RLF.DESTINACAODERECURSOS_ID " +
            " INNER JOIN FONTEDERECURSOS fr ON CD.FONTEDERECURSOS_ID = fr.ID " +
            " where est.EXERCICIO_ID = :exercicio " +
            " ) " +
            " order by ano, mes, dia ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("exercicio", exercicio.getId());
        return q.getResultList();
    }
}
