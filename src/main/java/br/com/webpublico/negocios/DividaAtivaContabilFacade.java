/*
 * Codigo gerado automaticamente em Fri Aug 31 14:22:19 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.OperacaoDividaAtiva;
import br.com.webpublico.enums.TagValor;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.ReprocessamentoLancamentoContabilSingleton;
import br.com.webpublico.negocios.contabil.reprocessamento.SuperFacadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.ConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.FiltroConsultaMovimentoContabil;
import br.com.webpublico.singletons.SingletonGeradorCodigoContabil;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Stateless
public class DividaAtivaContabilFacade extends SuperFacadeContabil<DividaAtivaContabil> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ClasseCredorFacade classeCredorFacade;
    @EJB
    private ReceitaLOAFacade receitaLOAFacade;
    @EJB
    private SaldoDividaAtivaContabilFacade saldoDividaAtivaContabilFacade;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;
    @EJB
    private ConfigDividaAtivaContabilFacade configDividaAtivaContabilFacade;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private IntegracaoTributarioContabilFacade integracaoTributarioContabilFacade;
    @EJB
    private ReprocessamentoLancamentoContabilSingleton reprocessamentoLancamentoContabilSingleton;
    @EJB
    private SingletonGeradorCodigoContabil singletonGeradorCodigoContabil;
    @EJB
    private ConfiguracaoContabilFacade configuracaoContabilFacade;
    @EJB
    private FaseFacade faseFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
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

    public IntegracaoTributarioContabilFacade getIntegracaoTributarioContabilFacade() {
        return integracaoTributarioContabilFacade;
    }

    public DividaAtivaContabilFacade() {
        super(DividaAtivaContabil.class);
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public ClasseCredorFacade getClasseCredorFacade() {
        return classeCredorFacade;
    }

    public ReceitaLOAFacade getReceitaLOAFacade() {
        return receitaLOAFacade;
    }

    public ConfigDividaAtivaContabilFacade getConfigDividaAtivaContabilFacade() {
        return configDividaAtivaContabilFacade;
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }

    public void setEventoContabilFacade(EventoContabilFacade eventoContabilFacade) {
        this.eventoContabilFacade = eventoContabilFacade;
    }

    public String getUltimoNumero() {
        String sql = "SELECT to_number(max(dac.numero)+1) FROM DividaAtivaContabil dac";
        Query q = em.createNativeQuery(sql);
        if (q.getSingleResult() == null) {
            return "1";
        }
        return q.getSingleResult().toString();
    }

    @Override
    public void salvarNovo(DividaAtivaContabil entity) {
        try {
            if (reprocessamentoLancamentoContabilSingleton.isCalculando()) {
                throw new ExcecaoNegocioGenerica(getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
            } else {
                hierarquiaOrganizacionalFacade.validaVigenciaHIerarquiaAdministrativaOrcamentaria(entity.getUnidadeOrganizacionalAdm(), entity.getUnidadeOrganizacional(), entity.getDataDivida());
                validarCampos(entity);
                definirContaReceitaNaDividaAtiva(entity);
                if (entity.getId() == null) {
                    entity.setNumero(singletonGeradorCodigoContabil.getNumeroDividaAtivaContabil(entity.getExercicio(), entity.getDataDivida()));
                    entity.gerarHistoricos();
                    em.persist(entity);
                } else {
                    entity.gerarHistoricos();
                    entity = em.merge(entity);
                }
                gerarSaldoDividaAtiva(entity);
                contabilizarDividaAtiva(entity);
            }
        } catch (ExcecaoNegocioGenerica ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    public void gerarSaldoDividaAtiva(DividaAtivaContabil entity) {
        saldoDividaAtivaContabilFacade.gerarSaldoDividaAtiva(entity, null);
    }

    public Boolean validarCampos(DividaAtivaContabil selecionado) {
        if (selecionado.getValor() != null
            && selecionado.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ExcecaoNegocioGenerica("O valor  deve ser maior que zero.");
        }
        if (selecionado.getDataReferencia().compareTo(selecionado.getDataDivida()) > 0) {
            throw new ExcecaoNegocioGenerica("A Data de Referência deve ser menor ou igual a Data de Lançamento.");
        }
        return true;
    }

    public void definirContaReceitaNaDividaAtiva(DividaAtivaContabil selecionado) {
        if (selecionado.getOperacaoDividaAtiva().equals(OperacaoDividaAtiva.INSCRICAO)) {
            selecionado.setContaReceita(((ContaReceita) selecionado.getReceitaLOA().getContaDeReceita()).getCorrespondente());
        } else {
            selecionado.setContaReceita((ContaReceita) selecionado.getReceitaLOA().getContaDeReceita());
        }
    }

    @Override
    public void salvar(DividaAtivaContabil entity) {
        entity.gerarHistoricos();
        em.merge(entity);
    }

    public DividaAtivaContabil salvarRetornando(DividaAtivaContabil entity) {
        entity.gerarHistoricos();
        return em.merge(entity);
    }

    public void contabilizarDividaAtiva(DividaAtivaContabil entity) throws ExcecaoNegocioGenerica {
        contabilizarDividaAtiva(entity, false);
    }

    public void contabilizarDividaAtiva(DividaAtivaContabil entity, boolean simulacao) throws ExcecaoNegocioGenerica {

        if (((ContaReceita) entity.getReceitaLOA().getContaDeReceita()).getTiposCredito() == null) {

            throw new ExcecaoNegocioGenerica("A Conta de Receita selecionada está com o identificador 'Tipo de Crédito' nulo, contate o suporte para resolver o problema");
        } else {

            ConfigDividaAtivaContabil configuracao = configDividaAtivaContabilFacade.recuperaEvento(entity);
            if (configuracao != null && configuracao.getEventoContabil() != null) {
                entity.setEventoContabil(configuracao.getEventoContabil());
            } else {
                throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada na contabilização de Dívida Ativa.");
            }
            entity.gerarHistoricos();
            ParametroEvento parametroEvento = new ParametroEvento();
            parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
            parametroEvento.setDataEvento(entity.getDataDivida());
            parametroEvento.setUnidadeOrganizacional(entity.getUnidadeOrganizacional());
            parametroEvento.setExercicio(entity.getExercicio());
            parametroEvento.setEventoContabil(entity.getEventoContabil());
            parametroEvento.setClasseOrigem(entity.getClass().getSimpleName());
            parametroEvento.setIdOrigem(entity.getId() != null ? entity.getId().toString() : "");

            ItemParametroEvento item = new ItemParametroEvento();
            item.setValor(entity.getValor());
            item.setParametroEvento(parametroEvento);
            item.setTagValor(TagValor.LANCAMENTO);
            item.setOperacaoClasseCredor(classeCredorFacade.recuperaOperacaoAndVigenciaClasseCredor(entity.getPessoa(), entity.getClasseCredorPessoa(), entity.getDataDivida()));

            List<ObjetoParametro> objetos = Lists.newArrayList();
            if (entity.getOperacaoDividaAtiva().equals(OperacaoDividaAtiva.INSCRICAO)) {
                objetos.add(new ObjetoParametro(((ContaReceita) entity.getReceitaLOA().getContaDeReceita()).getCorrespondente(), item, ObjetoParametro.TipoObjetoParametro.DEBITO));
                objetos.add(new ObjetoParametro(entity.getReceitaLOA().getContaDeReceita(), item, ObjetoParametro.TipoObjetoParametro.CREDITO));
            } else {
                objetos.add(new ObjetoParametro(entity.getReceitaLOA().getContaDeReceita(), item));
            }
            objetos.add(new ObjetoParametro(entity.getClasseCredorPessoa(), item));
            if (!simulacao) {
                objetos.add(new ObjetoParametro(entity, item));
            }
            item.setObjetoParametros(objetos);

            parametroEvento.getItensParametrosEvento().add(item);

            contabilizacaoFacade.contabilizar(parametroEvento, simulacao);

        }
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public ReprocessamentoLancamentoContabilSingleton getReprocessamentoLancamentoContabilSingleton() {
        return reprocessamentoLancamentoContabilSingleton;
    }

    public SingletonGeradorCodigoContabil getSingletonGeradorCodigoContabil() {
        return singletonGeradorCodigoContabil;
    }

    public ConfiguracaoContabilFacade getConfiguracaoContabilFacade() {
        return configuracaoContabilFacade;
    }

    public FaseFacade getFaseFacade() {
        return faseFacade;
    }

    @Override
    public void contabilizarReprocessamento(EntidadeContabil entidadeContabil) {
        contabilizarDividaAtiva((DividaAtivaContabil) entidadeContabil);
    }

    public List<DividaAtivaContabil> recuperarDividaAtivaDaPessoa(Pessoa pessoa) {
        String sql = " select d.* from dividaativacontabil d     " +
            "       where d.pessoa_id = :pessoa        ";
        Query q = em.createNativeQuery(sql, DividaAtivaContabil.class);
        q.setParameter("pessoa", pessoa.getId());
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        }
        return q.getResultList();
    }

    @Override
    public List<ConsultaMovimentoContabil> criarConsulta(List<FiltroConsultaMovimentoContabil> filtros) {
        ConsultaMovimentoContabil consulta = new ConsultaMovimentoContabil(DividaAtivaContabil.class, filtros);
        consulta.incluirJoinsComplementar(" inner join receitaLoaFonte lanfonte on lanfonte.receitaloa_id = obj.receitaLOA_id ");
        consulta.incluirJoinsOrcamentoReceita("obj.receitaloa_id", "lanfonte.id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_INICIAL, "trunc(obj.dataDivida)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_FINAL, "trunc(obj.dataDivida)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.UNIDADE, "obj.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.LISTA_UNIDADE, "obj.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.PESSOA, "obj.pessoa_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CLASSE, "obj.classecredor_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.EVENTO_CONTABIL, "obj.eventocontabil_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.NUMERO, "obj.numero");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.VALOR, "obj.valor");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.HISTORICO, "obj.historicoRazao");
        return Arrays.asList(consulta);
    }

    public void definirEventoDividaAtiva(DividaAtivaContabil da) {
        try {
            da.setEventoContabil(null);
            if (da.getReceitaLOA().getContaDeReceita() != null && da.getOperacaoDividaAtiva() != null) {
                ConfigDividaAtivaContabil configuracao = configDividaAtivaContabilFacade.recuperaEvento(da);
                if (configuracao != null) {
                    da.setEventoContabil(configuracao.getEventoContabil());
                }
            }
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }
}
