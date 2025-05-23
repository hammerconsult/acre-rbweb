/*
 * Codigo gerado automaticamente em Fri Aug 31 14:22:19 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Stateless
public class CreditoReceberFacade extends SuperFacadeContabil<CreditoReceber> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ClasseCredorFacade classeCredorFacade;
    @EJB
    private ReceitaLOAFacade receitaLOAFacade;
    @EJB
    private SaldoCreditoReceberFacade saldoCreditoReceberFacade;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;
    @EJB
    private ConfigCreditoReceberFacade configCreditoReceberFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private IntegracaoTributarioContabilFacade integracaoTributarioContabilFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private ContaFacade contaFacade;
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

    public CreditoReceberFacade() {
        super(CreditoReceber.class);
    }

    public SaldoCreditoReceberFacade getSaldoCreditoReceberFacade() {
        return saldoCreditoReceberFacade;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public IntegracaoTributarioContabilFacade getIntegracaoTributarioContabilFacade() {
        return integracaoTributarioContabilFacade;
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

    public ConfigCreditoReceberFacade getConfigCreditoReceberFacade() {
        return configCreditoReceberFacade;
    }

    public String getUltimoNumero() {
        String sql = "SELECT max(to_number(cr.numero))+1 AS ultimoNumero FROM CreditoReceber cr";
        Query q = em.createNativeQuery(sql);
        if (q.getSingleResult() == null) {
            return "1";
        }
        return q.getSingleResult().toString();
    }

    @Override
    public void salvarNovo(CreditoReceber entity) {

        try {
            if (reprocessamentoLancamentoContabilSingleton.isCalculando()) {
                throw new ExcecaoNegocioGenerica(getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
            } else {
                hierarquiaOrganizacionalFacade.validaVigenciaHIerarquiaAdministrativaOrcamentaria(entity.getUnidadeOrganizacionalAdm(), entity.getUnidadeOrganizacional(), entity.getDataCredito());
                if (entity.getId() == null) {
                    entity.setNumero(singletonGeradorCodigoContabil.getNumeroCreditoReceber(entity.getExercicio(), entity.getDataCredito()));
                    em.persist(entity);
                } else {
                    entity.gerarHistoricos();
                    entity = em.merge(entity);
                }
                saldoCreditoReceberFacade.gerarSaldoCreditoReceber(entity, true);
                contabilizarCreditoReceber(entity);
            }
        } catch (ExcecaoNegocioGenerica ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    @Override
    public void salvar(CreditoReceber entity) {
        entity.gerarHistoricos();
        em.merge(entity);
    }

    public CreditoReceber salvarRetornando(CreditoReceber entity) {
        entity.gerarHistoricos();
        return em.merge(entity);
    }

    public void contabilizarCreditoReceber(CreditoReceber entity) {
        contabilizarCreditoReceber(entity, false);
    }

    public void contabilizarCreditoReceber(CreditoReceber entity, boolean simulacao) {
        try {

            ConfigCreditoReceber configCreditoReceber = configCreditoReceberFacade.recuperarEventoContabil(entity);
            if (configCreditoReceber != null && configCreditoReceber.getEventoContabil() != null) {
                entity.setEventoContabil(configCreditoReceber.getEventoContabil());
            } else {
                throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada na contabilização de Crédito a Receber.");
            }
            entity.gerarHistoricos();
            ParametroEvento parametroEvento = new ParametroEvento();
            parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
            parametroEvento.setDataEvento(entity.getDataCredito());
            parametroEvento.setUnidadeOrganizacional(entity.getUnidadeOrganizacional());
            parametroEvento.setEventoContabil(entity.getEventoContabil());
            parametroEvento.setExercicio(entity.getExercicio());
            parametroEvento.setClasseOrigem(entity.getClass().getSimpleName());
            parametroEvento.setIdOrigem(entity.getId() != null ? entity.getId().toString() : "");

            ItemParametroEvento item = new ItemParametroEvento();
            item.setValor(entity.getValor());
            item.setParametroEvento(parametroEvento);
            item.setTagValor(TagValor.LANCAMENTO);
            item.setOperacaoClasseCredor(classeCredorFacade.recuperaOperacaoAndVigenciaClasseCredor(entity.getPessoa(), entity.getClasseCredor(), entity.getDataCredito()));

            List<ObjetoParametro> objetos = Lists.newArrayList();
            objetos.add(new ObjetoParametro(entity.getReceitaLOA().getContaDeReceita().getId().toString(), ContaReceita.class.getSimpleName(), item));
            if (!simulacao) {
                objetos.add(new ObjetoParametro(entity.getId().toString(), CreditoReceber.class.getSimpleName(), item));
            }
            if (entity.getClasseCredor() == null) {
                throw new ExcecaoNegocioGenerica("A Classe Credor está vazia.");
            }
            objetos.add(new ObjetoParametro(entity.getClasseCredor().getId().toString(), ClasseCredor.class.getSimpleName(), item));
            item.setObjetoParametros(objetos);

            parametroEvento.getItensParametrosEvento().add(item);

            contabilizacaoFacade.contabilizar(parametroEvento, simulacao);
        } catch (ExcecaoNegocioGenerica ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        } catch (Exception e) {
            logger.error("Erro não tratado: {}", e);
        }
    }

    private ConfigCreditoReceber getConfigCreditoReceber(CreditoReceber entity) {
        ConfigCreditoReceber config = new ConfigCreditoReceber();
        config.setOperacaoCreditoReceber(entity.getOperacaoCreditoReceber());
        config.setTipoLancamento(entity.getTipoLancamento());
        config.setTiposCredito(((ContaReceita) entity.getReceitaLOA().getContaDeReceita()).getTiposCredito());
        return config;
    }

    public List<CreditoReceber> lista(UnidadeOrganizacional uo) {
        String sql = "SELECT * "
            + " FROM creditoreceber"
            + " WHERE unidadeorganizacional_id = :uo"
            + " ORDER BY id DESC";
        Query q = em.createNativeQuery(sql, CreditoReceber.class);
        q.setParameter("uo", uo.getId());
        return super.lista();
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
    public void contabilizarReprocessamento(EntidadeContabil obj) {
        contabilizarCreditoReceber((CreditoReceber) obj);
    }

    public List<CreditoReceber> recuperarCreditoReceberDaPessoa(Pessoa pessoa) {
        String sql = " select cr.* from creditoreceber cr " +
            "where cr.pessoa_id = :pessoa   ";
        Query q = em.createNativeQuery(sql, CreditoReceber.class);
        q.setParameter("pessoa", pessoa.getId());
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        }
        return q.getResultList();
    }

    @Override
    public List<ConsultaMovimentoContabil> criarConsulta(List<FiltroConsultaMovimentoContabil> filtros) {
        ConsultaMovimentoContabil consulta = new ConsultaMovimentoContabil(CreditoReceber.class, filtros);
        consulta.incluirJoinsComplementar(" inner join receitaLoaFonte lanfonte on lanfonte.receitaloa_id = obj.receitaLOA_id ");
        consulta.incluirJoinsOrcamentoReceita("obj.receitaloa_id", "lanfonte.id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_INICIAL, "trunc(obj.dataCredito)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_FINAL, "trunc(obj.dataCredito)");
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

    public void definirEventoCreditoReceber(CreditoReceber cr) {
        try {
            cr.setEventoContabil(null);
            if (cr.getReceitaLOA().getContaDeReceita() != null && cr.getOperacaoCreditoReceber() != null) {
                ConfigCreditoReceber configuracao = configCreditoReceberFacade.recuperarEventoContabil(cr);
                if (configuracao != null) {
                    cr.setEventoContabil(configuracao.getEventoContabil());
                }
            }
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }
}
