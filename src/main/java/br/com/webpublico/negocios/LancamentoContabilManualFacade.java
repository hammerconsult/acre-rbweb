package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.SuperFacadeContabil;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Stateless
public class LancamentoContabilManualFacade extends SuperFacadeContabil<LancamentoContabilManual> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;
    @EJB
    private ConfiguracaoContabilFacade configuracaoContabilFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private CLPFacade clpFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private SubFuncaoFacade subFuncaoFacade;
    @EJB
    private ProgramaPPAFacade programaPPAFacade;
    @EJB
    private ExtensaoFonteRecursoFacade extensaoFonteRecursoFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private SubContaFacade subContaFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LancamentoContabilManualFacade() {
        super(LancamentoContabilManual.class);
    }

    @Override
    public LancamentoContabilManual recuperar(Object id) {
        LancamentoContabilManual entity = super.recuperar(id);
        Hibernate.initialize(entity.getContas());
        Hibernate.initialize(entity.getTiposDePrestacoesDeContas());
        return entity;
    }

    public Date getDataOperacao() {
        return sistemaFacade.getDataOperacao();
    }

    public Exercicio getExercicioCorrente() {
        return sistemaFacade.getExercicioCorrente();
    }

    public UnidadeOrganizacional getUnidadeOrganizacionalOrcamentoCorrente() {
        return sistemaFacade.getUnidadeOrganizacionalOrcamentoCorrente();
    }

    public ConfiguracaoContabil buscarConfiguracaoContabilVigente() {
        return configuracaoContabilFacade.configuracaoContabilVigente();
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalPorUnidade(UnidadeOrganizacional unidadeOrganizacional, Date dataOperacao) {
        return hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(dataOperacao, unidadeOrganizacional, TipoHierarquiaOrganizacional.ORCAMENTARIA);
    }

    public List<HierarquiaOrganizacional> buscarHierarquiaOrganizacional(String parte, Date data) {
        return hierarquiaOrganizacionalFacade.listaHierarquiaUsuarioCorrentePorNivel(parte.trim(), sistemaFacade.getUsuarioCorrente(),
            sistemaFacade.getExercicioCorrente(), data, TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
    }

    public List<ContaDeDestinacao> buscarContasDeDestinacao(String parte) {
        return contaFacade.buscarContasDeDestinacaoPorCodigoOrDescricao(parte, sistemaFacade.getExercicioCorrente());
    }

    public List<ContaContabil> buscarContasContabeis(String parte, Conta contaSintetica) {
        return contaFacade.buscarContasContabeis(parte, sistemaFacade.getExercicioCorrente(), contaSintetica);
    }

    public List<EventoContabil> buscarEventoContabil(String filtro, TipoLancamento tipoLancamento) {
        return eventoContabilFacade.buscarFiltrandoPorTipoEventoAndTipoLancamento(filtro, TipoEventoContabil.AJUSTE_CONTABIL_MANUAL, tipoLancamento, null);
    }

    public List<CLP> buscarCLPComContasPorEventoContabil(EventoContabil eventoContabil, Date data) {
        List<CLP> retorno = clpFacade.buscarCLPComContasPorEventoContabil(eventoContabil, data);
        retorno.forEach(clp -> {
            Hibernate.initialize(clp.getLcps());
        });
        return retorno;
    }

    public List<Conta> buscarContaReceita(String filtro) {
        return contaFacade.listaFiltrandoReceitaAnalitica(filtro.trim(), sistemaFacade.getExercicioCorrente());
    }

    public List<Conta> buscarContasDespesa(String filtro) {
        return contaFacade.listaFiltrandoContaDespesa(filtro.trim(), sistemaFacade.getExercicioCorrente());
    }

    public List<Funcao> buscarFuncoes(String parte) {
        return subFuncaoFacade.getFuncaoFacade().listaFiltrandoFuncao(parte);
    }

    public List<SubFuncao> buscarSubFuncoes(String parte) {
        return subFuncaoFacade.listaFiltrandoSubFuncao(parte);
    }

    public List<ProgramaPPA> buscarProgramas(String parte, Exercicio exercicio) {
        return programaPPAFacade.buscarProgramasPorExercicio(parte, exercicio);
    }

    public List<AcaoPPA> buscarAcoesPPAs(String parte, Exercicio exercicio, ProgramaPPA programaPPA) {
        if (programaPPA != null) {
            return programaPPAFacade.getProjetoAtividadeFacade().buscarProjetoAtividadePorPrograma(programaPPA, parte);
        }
        return programaPPAFacade.getProjetoAtividadeFacade().buscarAcoesPPAPorExercicio(parte, exercicio);
    }

    public List<ExtensaoFonteRecurso> buscarExtensoes(String parte) {
        return extensaoFonteRecursoFacade.listaFiltrando(parte.trim(), "codigo", "descricao");
    }

    public List<Pessoa> buscarCredores(String parte) {
        return pessoaFacade.listaTodasPessoasAtivas(parte.trim());
    }

    public List<SubConta> buscarContasFinanceiras(String parte, UnidadeOrganizacional unidadeOrganizacional, Exercicio exercicio) {
        return subContaFacade.buscarContasFinanceirasPorBancariaEntidadeOuTodosPorUnidadeAndContaDeDestinacao(parte.trim(), null, unidadeOrganizacional, exercicio, null, null, null, true);
    }

    public String buscarUltimoNumero() {
        String sql = "SELECT MAX(TO_NUMBER(LANC.NUMERO)) +1 AS ULTIMONUMERO FROM LANCAMENTOCONTABILMANUAL LANC";
        Query q = em.createNativeQuery(sql);
        q.setMaxResults(1);
        if (q.getSingleResult() == null) {
            return "1";
        }
        return q.getSingleResult().toString();
    }

    @Override
    public void salvarNovo(LancamentoContabilManual entity) {
        if (entity.getId() == null) {
            entity.setNumero(buscarUltimoNumero());
        }
        entity = salvarRetornando(entity);
        contabilizarReprocessamento(entity);
    }

    @Override
    public void contabilizarReprocessamento(EntidadeContabil entidadeContabil) {
        LancamentoContabilManual lancamentoContabilManual = (LancamentoContabilManual) entidadeContabil;
        if (lancamentoContabilManual != null &&
            lancamentoContabilManual.getId() != null) {
            lancamentoContabilManual = recuperar(lancamentoContabilManual.getId());
            if (lancamentoContabilManual.hasTipoCadastrado(Lists.newArrayList(TipoPrestacaoDeContas.SICONFI, TipoPrestacaoDeContas.SISTEMA))) {
                contabilizacaoFacade.contabilizar(getParametroEvento(lancamentoContabilManual));
            }
        }
    }

    public ParametroEvento getParametroEvento(LancamentoContabilManual entity) {
        try {
            ParametroEvento parametroEvento = criarParametroEvento(entity);
            ItemParametroEvento item = criarItemParametroEvento(entity.getValor(), parametroEvento);
            List<ObjetoParametro> objetos = criarObjetosParametros(entity, item);
            item.setObjetoParametros(objetos);
            parametroEvento.getItensParametrosEvento().add(item);
            return parametroEvento;
        } catch (Exception e) {
            return null;
        }
    }

    private ParametroEvento criarParametroEvento(LancamentoContabilManual entity) {
        ParametroEvento parametroEvento = new ParametroEvento();
        parametroEvento.setComplementoHistorico(entity.getComplementoHistorico());
        parametroEvento.setDataEvento(entity.getData());
        parametroEvento.setUnidadeOrganizacional(entity.getUnidadeOrganizacional());
        parametroEvento.setEventoContabil(entity.getEventoContabil());
        parametroEvento.setClasseOrigem(LancamentoContabilManual.class.getSimpleName());
        parametroEvento.setIdOrigem(entity.getId() == null ? null : entity.getId().toString());
        parametroEvento.setExercicio(entity.getExercicio());
        parametroEvento.setTipoBalancete(entity.getEventoContabil().getTipoBalancete());
        return parametroEvento;
    }

    private ItemParametroEvento criarItemParametroEvento(BigDecimal valor, ParametroEvento parametroEvento) {
        ItemParametroEvento item = new ItemParametroEvento();
        item.setValor(valor);
        item.setParametroEvento(parametroEvento);
        item.setTagValor(TagValor.LANCAMENTO);
        return item;
    }

    private List<ObjetoParametro> criarObjetosParametros(LancamentoContabilManual lancamentoContabilManual, ItemParametroEvento item) {
        List<ObjetoParametro> objetos = Lists.newArrayList();
        for (ContaLancamentoManual conta : lancamentoContabilManual.getContas()) {

            if (TipoLancamentoContabil.CREDITO.equals(conta.getTipo())) {
                objetos.add(new ObjetoParametro(conta.getId().toString(), ContaLancamentoManual.class.getSimpleName(), item, ObjetoParametro.TipoObjetoParametro.CREDITO));
                objetos.add(new ObjetoParametro(conta.getContaContabil().getId().toString(), ContaContabil.class.getSimpleName(), item, ObjetoParametro.TipoObjetoParametro.CREDITO));
                objetos.add(new ObjetoParametro(conta.getContaDeDestinacao().getFonteDeRecursos().getId().toString(), FonteDeRecursos.class.getSimpleName(), item , ObjetoParametro.TipoObjetoParametro.CREDITO));
            }
            if (TipoLancamentoContabil.DEBITO.equals(conta.getTipo())) {
                objetos.add(new ObjetoParametro(conta.getId().toString(), ContaLancamentoManual.class.getSimpleName(), item, ObjetoParametro.TipoObjetoParametro.DEBITO));
                objetos.add(new ObjetoParametro(conta.getContaContabil().getId().toString(), ContaContabil.class.getSimpleName(), item, ObjetoParametro.TipoObjetoParametro.DEBITO));
                objetos.add(new ObjetoParametro(conta.getContaDeDestinacao().getFonteDeRecursos().getId().toString(), FonteDeRecursos.class.getSimpleName(), item, ObjetoParametro.TipoObjetoParametro.DEBITO));
            }
        }
        return objetos;
    }

    public List<LancamentoContabilManual> buscarLancamentosContabeisManuais(Mes mes, Exercicio exercicio, List<TipoPrestacaoDeContas> tipos, String unidades) {
        String sql = "select distinct lcm.* " +
            " from lancamentocontabilmanual lcm " +
            "         inner join tipolancamentomanual tlm on tlm.lancamento_id = lcm.id " +
            " where lcm.exercicio_id = :idExercicio " +
            "   and extract(month from lcm.data) = :mes " +
            "   and tlm.tipoprestacaodecontas in " + TipoPrestacaoDeContas.montarClausulaIn(tipos) +
            (unidades != null ? " and lcm.unidadeorganizacional_id in " + unidades : "") +
            " order by to_number(lcm.numero)";
        Query q = em.createNativeQuery(sql, LancamentoContabilManual.class);
        q.setParameter("idExercicio", exercicio.getId());
        q.setParameter("mes", mes.getNumeroMes());
        List<LancamentoContabilManual> lancamentos = q.getResultList();
        lancamentos.forEach(lancamento -> {
            Hibernate.initialize(lancamento.getContas());
            Hibernate.initialize(lancamento.getTiposDePrestacoesDeContas());
        });
        return q.getResultList();
    }
}
