package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.NotaExecucaoOrcamentaria;
import br.com.webpublico.entidadesauxiliares.NotaExecucaoOrcamentariaDetalhamento;
import br.com.webpublico.entidadesauxiliares.NotaExecucaoOrcamentariaDocumentoComprobatorio;
import br.com.webpublico.enums.*;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.ReprocessamentoLancamentoContabilSingleton;
import br.com.webpublico.negocios.contabil.reprocessamento.SuperFacadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.ConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.FiltroConsultaMovimentoContabil;
import br.com.webpublico.singletons.SingletonGeradorCodigoContabil;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Persistencia;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by mga on 23/06/2017.
 */
@Stateless
public class ObrigacaoAPagarFacade extends SuperFacadeContabil<ObrigacaoAPagar> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConfiguracaoObrigacaoAPagarFacade configuracaoObrigacaoAPagarFacade;
    @EJB
    private UFFacade ufFacade;
    @EJB
    private EmpenhoFacade empenhoFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private HistoricoContabilFacade historicoContabilFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private TipoDocumentoFiscalFacade tipoDocumentoFiscalFacade;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SingletonGeradorCodigoContabil singletonGeradorCodigoContabil;
    @EJB
    private ReprocessamentoLancamentoContabilSingleton reprocessamentoLancamentoContabilSingleton;
    @EJB
    private FonteDespesaORCFacade fonteDespesaORCFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ClasseCredorFacade classeCredorFacade;
    @EJB
    private LiquidacaoFacade liquidacaoFacade;
    @EJB
    private LiquidacaoEstornoFacade liquidacaoEstornoFacade;
    @EJB
    private PagamentoFacade pagamentoFacade;
    @EJB
    private PagamentoEstornoFacade pagamentoEstornoFacade;
    @EJB
    private EmpenhoEstornoFacade empenhoEstornoFacade;
    @EJB
    private ConfigContaDespesaTipoDocumentoFacade configContaDespesaTipoDocumentoFacade;
    @EJB
    private ConfiguracaoTipoContaDespesaClasseCredorFacade configuracaoTipoContaDespesaClasseCredorFacade;
    @EJB
    private ConfiguracaoContabilFacade configuracaoContabilFacade;
    @EJB
    private NotaOrcamentariaFacade notaOrcamentariaFacade;

    public ObrigacaoAPagarFacade() {
        super(ObrigacaoAPagar.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ObrigacaoAPagar recuperar(Object id) {
        ObrigacaoAPagar entity = em.find(ObrigacaoAPagar.class, id);
        entity.getDesdobramentos().size();
        entity.getDocumentosFiscais().size();
        return entity;
    }

    @Override
    public void salvar(ObrigacaoAPagar entity) {
        entity.gerarHistoricos();
        em.merge(entity);
    }

    public ObrigacaoAPagar salvarObrigacao(ObrigacaoAPagar entity) {
        try {
            if (getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
                throw new ExcecaoNegocioGenerica(getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
            } else {
                if (Persistencia.getId(entity) == null) {
                    entity.setNumero(gerarNumero(entity));
                    entity.setSaldo(entity.getValor());
                    if (entity.isObrigacaoPagarAntesEmpenho()) {
                        entity.setSaldoEmpenho(entity.getValor());
                    }
                    entity.gerarHistoricos();
                    em.persist(entity);
                } else {
                    entity.gerarHistoricos();
                    entity = em.merge(entity);
                }
                movimentarSaldoEmpenho(entity);
                contabilizar(entity);
            }
            return entity;
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    private String gerarNumero(ObrigacaoAPagar entity) {
        return singletonGeradorCodigoContabil.getNumeroObrigacaoAPagar(entity.getExercicio(), entity.getUnidadeOrganizacional(), entity.getDataLancamento());
    }

    public void salvarNovoParaFechamentoExercicio(TransporteObrigacaoAPagar transporteObrigacaoAPagar) throws ExcecaoNegocioGenerica {
        try {
            ObrigacaoAPagar entity = transporteObrigacaoAPagar.getObrigacaoAPagar();
            if (getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
                throw new ExcecaoNegocioGenerica(getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
            } else {
                ObrigacaoAPagar obrigacaoAPagar = entity.getObrigacaoAPagar();
                obrigacaoAPagar.setSaldo(BigDecimal.ZERO);
                em.merge(obrigacaoAPagar);

                if (entity.getId() == null) {
                    entity.gerarHistoricos();
                    em.persist(entity);
                } else {
                    entity.gerarHistoricos();
                    em.merge(entity);
                }
            }
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }

    private void movimentarSaldoEmpenho(ObrigacaoAPagar entity) {
        if (entity.isObrigacaoPagarDepoisEmpenho()) {
            Empenho empenho = empenhoFacade.recuperarSimples(entity.getEmpenho().getId());
            empenho.setSaldoObrigacaoPagarDepoisEmp(empenho.getSaldoObrigacaoPagarDepoisEmp().add(entity.getValor()));
            em.merge(empenho);
        }
    }

    private void contabilizar(ObrigacaoAPagar entity) {
        for (DesdobramentoObrigacaoPagar desdobramento : entity.getDesdobramentos()) {
            contabilizarDesdobramento(desdobramento);
        }
    }

    private void contabilizarDesdobramento(DesdobramentoObrigacaoPagar desdobramento) {
        if (!Hibernate.isInitialized(desdobramento.getObrigacaoAPagar().getDesdobramentos())) {
            desdobramento.setObrigacaoAPagar(recuperar(desdobramento.getObrigacaoAPagar().getId()));
        }
        EventoContabil eventoContabil = buscarEventoContabil(desdobramento);
        Preconditions.checkNotNull(eventoContabil, "Evento contábil não encontrado.");
        desdobramento.setEventoContabil(eventoContabil);
        desdobramento.getObrigacaoAPagar().gerarHistoricos();
        ParametroEvento parametroEvento = criarParametroEvento(desdobramento);
        contabilizacaoFacade.contabilizar(parametroEvento);
    }

    public ParametroEvento criarParametroEvento(DesdobramentoObrigacaoPagar desdobramento) {
        ParametroEvento parametroEvento = getParametroEvento(desdobramento);
        ItemParametroEvento item = getItemParametroEvento(desdobramento.getValor(), parametroEvento);
        List<ObjetoParametro> listaObj = getObjetoParametros(desdobramento, item);
        item.setObjetoParametros(listaObj);
        parametroEvento.getItensParametrosEvento().add(item);
        return parametroEvento;
    }

    private List<ObjetoParametro> getObjetoParametros(DesdobramentoObrigacaoPagar desdobramento, ItemParametroEvento item) {
        Preconditions.checkNotNull(desdobramento.getObrigacaoAPagar().getClasseCredor(), "A classe credor não foi preenchida.");
        Preconditions.checkNotNull(desdobramento.getConta(), "A conta de despesa não foi preenchida.");
        Preconditions.checkNotNull(desdobramento.getObrigacaoAPagar().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos(), "A fonte de recurso não foi preenchida.");

        List<ObjetoParametro> objetos = Lists.newArrayList();
        if (desdobramento.getObrigacaoAPagar().getId() != null) {
            objetos.add(new ObjetoParametro(desdobramento.getObrigacaoAPagar().getId().toString(), ObrigacaoAPagar.class.getSimpleName(), item));
        }
        objetos.add(new ObjetoParametro(desdobramento.getConta().getId().toString(), ContaDespesa.class.getSimpleName(), item));
        objetos.add(new ObjetoParametro(getFonteRecurso(desdobramento.getObrigacaoAPagar()).getId().toString(), FonteDeRecursos.class.getSimpleName(), item));
        objetos.add(new ObjetoParametro(desdobramento.getObrigacaoAPagar().getDespesaORC().getProvisaoPPADespesa().getId().toString(), ProvisaoPPADespesa.class.getSimpleName(), item));
        objetos.add(new ObjetoParametro(desdobramento.getObrigacaoAPagar().getClasseCredor().getId().toString(), ClasseCredor.class.getSimpleName(), item));
        objetos.add(new ObjetoParametro(desdobramento.getObrigacaoAPagar().getPessoa().getId().toString(), Pessoa.class.getSimpleName(), item));
        return objetos;
    }

    private ItemParametroEvento getItemParametroEvento(BigDecimal valor, ParametroEvento parametroEvento) {
        ItemParametroEvento item = new ItemParametroEvento();
        item.setValor(valor);
        item.setParametroEvento(parametroEvento);
        item.setTagValor(TagValor.LANCAMENTO);
        return item;
    }

    private ParametroEvento getParametroEvento(DesdobramentoObrigacaoPagar desdobramento) {
        ParametroEvento parametroEvento = new ParametroEvento();
        parametroEvento.setComplementoHistorico(desdobramento.getObrigacaoAPagar().getHistorico());
        parametroEvento.setDataEvento(desdobramento.getObrigacaoAPagar().getDataLancamento());
        parametroEvento.setUnidadeOrganizacional(desdobramento.getObrigacaoAPagar().getUnidadeOrganizacional());
        parametroEvento.setEventoContabil(desdobramento.getEventoContabil());
        parametroEvento.setExercicio(desdobramento.getConta().getExercicio());
        parametroEvento.setClasseOrigem(DesdobramentoObrigacaoPagar.class.getSimpleName());
        parametroEvento.setIdOrigem(desdobramento.getId() == null ? null : desdobramento.getId().toString());
        return parametroEvento;
    }

    private FonteDeRecursos getFonteRecurso(ObrigacaoAPagar entity) {
        Preconditions.checkNotNull(entity.getContaDeDestinacao(), "A destinação de recurso não foi preenchida.");
        Preconditions.checkNotNull(entity.getContaDeDestinacao().getFonteDeRecursos(), "A fonte de recurso não foi preenchida.");
        return entity.getContaDeDestinacao().getFonteDeRecursos();
    }

    @Override
    public void contabilizarReprocessamento(EntidadeContabil entidadeContabil) {
        DesdobramentoObrigacaoPagar desdobramento = (DesdobramentoObrigacaoPagar) entidadeContabil;
        contabilizarDesdobramento(desdobramento);
    }

    public EventoContabil buscarEventoContabil(DesdobramentoObrigacaoPagar desdobramento) {
        EventoContabil eventoContabil = null;
        ConfigObrigacaoAPagar configuracao = configuracaoObrigacaoAPagarFacade.buscarCDEObrigacaoPagar(
            desdobramento.getConta(),
            TipoLancamento.NORMAL,
            desdobramento.getObrigacaoAPagar().getDataLancamento(),
            desdobramento.getObrigacaoAPagar().getTipoContaDespesa(),
            desdobramento.getObrigacaoAPagar().getSubTipoDespesa(),
            desdobramento.getObrigacaoAPagar().getTipoReconhecimento(),
            desdobramento.getObrigacaoAPagar().getEmpenho() != null ? desdobramento.getObrigacaoAPagar().getEmpenho().getCategoriaOrcamentaria() : CategoriaOrcamentaria.NORMAL);
        if (configuracao != null) {
            eventoContabil = configuracao.getEventoContabil();
        }
        return eventoContabil;
    }

    public List<ObrigacaoAPagar> buscarObrigacaoPagar(String parte,
                                                      UnidadeOrganizacional unidade,
                                                      DespesaORC despesaORC,
                                                      FonteDespesaORC fonteDespesaORC,
                                                      Pessoa pessoa,
                                                      ClasseCredor classeCredor) {
        String sql = " select distinct obrigacao.* " +
            "   from usuariosistema usu    " +
            "     inner join usuariounidadeorganizacorc usuund on usuund.usuariosistema_id = usu.id    " +
            "     inner join unidadeorganizacional unid on usuund.unidadeorganizacional_id = unid.id   " +
            "     inner join obrigacaoapagar obrigacao on obrigacao.unidadeorganizacional_id = unid.id  " +
            "     inner join pessoa p on obrigacao.pessoa_id = p.id    " +
            "     left join pessoafisica pf on p.id = pf.id    " +
            "     left join pessoajuridica pj on p.id = pj.id    " +
            "   where obrigacao.saldoEmpenho > 0    " +
            "    and obrigacao.exercicio_id = :idExercicio  " +
            "    and usu.id = :idUsuario  " +
            "    and obrigacao.CONTADESPESA_ID = :idContaDespesa " +
            "    and obrigacao.FONTEDERECURSOS_ID = :idFonte  " +
            "    and obrigacao.pessoa_id = :idPessoa  " +
            "    and obrigacao.classecredor_id = :idClasse  " +
            "    and obrigacao.empenho_id is null  " +
            "    and trunc(obrigacao.datalancamento) <= to_date(:dataOperacao, 'dd/MM/yyyy')  " +
            "    and obrigacao.unidadeorganizacional_id = :idUnidade  " +
            "    and ((lower(pf.nome) like :parte) or (pf.cpf like :parte) or (obrigacao.numero like :parte)    " +
            "       or (lower(pj.nomefantasia) like :parte) or (lower(pj.nomereduzido) like :parte) or (pj.cnpj like :parte)    " +
            "       or (obrigacao.numero like :parte ))    " +
            "   order by obrigacao.datalancamento, obrigacao.numero desc ";
        Query q = em.createNativeQuery(sql, ObrigacaoAPagar.class);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("idUsuario", sistemaFacade.getUsuarioCorrente().getId());
        q.setParameter("idExercicio", sistemaFacade.getExercicioCorrente().getId());
        q.setParameter("idContaDespesa", despesaORC.getProvisaoPPADespesa().getContaDeDespesa().getId());
        q.setParameter("idFonte", fonteDespesaORC.getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao().getFonteDeRecursos().getId());
        q.setParameter("idPessoa", pessoa.getId());
        q.setParameter("idClasse", classeCredor.getId());
        q.setParameter("idUnidade", unidade.getId());
        q.setMaxResults(50);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public List<ObrigacaoAPagar> buscarObrigacaoPagarPorUnidadeUsuarioVinculado(String parte, Date dataOperacao,
                                                                                UnidadeOrganizacional unidade,
                                                                                Exercicio exercicio,
                                                                                UsuarioSistema usuario) {
        String sql = " select distinct obrigacao.* " +
            "   from usuariosistema usu    " +
            "     inner join usuariounidadeorganizacorc usuund on usuund.usuariosistema_id = usu.id    " +
            "     inner join unidadeorganizacional unid on usuund.unidadeorganizacional_id = unid.id   " +
            "     inner join obrigacaoapagar obrigacao on obrigacao.unidadeorganizacional_id = unid.id  " +
            "     inner join pessoa p on obrigacao.pessoa_id = p.id    " +
            "     left join pessoafisica pf on p.id = pf.id    " +
            "     left join pessoajuridica pj on p.id = pj.id    " +
            "   where (case when obrigacao.empenho_id is not null then obrigacao.saldo else obrigacao.saldoempenho  end) > 0 " +
            "    and obrigacao.exercicio_id = :idExercicio  " +
            "    and usu.id = :idUsuario  " +
            "    and trunc(obrigacao.datalancamento) <= to_date(:dataOperacao, 'dd/MM/yyyy')  " +
            "    and obrigacao.unidadeorganizacional_id = :idUnidade  " +
            "    and ((lower(pf.nome) like :parte) or (pf.cpf like :parte) or (obrigacao.numero like :parte)    " +
            "       or (lower(pj.nomefantasia) like :parte) or (lower(pj.nomereduzido) like :parte) or (pj.cnpj like :parte)    " +
            "       or (obrigacao.numero like :parte ))    " +
            "   order by obrigacao.datalancamento desc, obrigacao.numero desc ";
        Query q = em.createNativeQuery(sql, ObrigacaoAPagar.class);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("idUsuario", usuario.getId());
        q.setParameter("idExercicio", exercicio.getId());
        q.setParameter("idUnidade", unidade.getId());
        q.setMaxResults(30);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public List<ObrigacaoAPagar> buscarObrigacaoPagarPorEmpenho(String parte, Empenho empenho) {
        String sql = " " +
            "   select op.* " +
            "    from empenho emp " +
            "    inner join empenhoobrigacaopagar eop on eop.empenho_id = emp.id " +
            "    inner join obrigacaoapagar op on op.id = eop.obrigacaoapagar_id " +
            "     inner join pessoa p on op.pessoa_id = p.id    " +
            "     left join pessoafisica pf on p.id = pf.id    " +
            "     left join pessoajuridica pj on p.id = pj.id    " +
            "   where emp.id = :idEmpenho " +
            "   and op.saldo > 0 " +
            "    and ((lower(pf.nome) like :parte) or (pf.cpf like :parte) or (op.numero like :parte) " +
            "      or (lower(pj.nomefantasia) like :parte) or (lower(pj.nomereduzido) like :parte) or (pj.cnpj like :parte))" +
            "   union " +
            "   select op.* from obrigacaoapagar op " +
            "    inner join empenho emp on emp.id = op.empenho_id " +
            "     inner join pessoa p on op.pessoa_id = p.id    " +
            "     left join pessoafisica pf on p.id = pf.id    " +
            "     left join pessoajuridica pj on p.id = pj.id    " +
            "   where emp.id = :idEmpenho " +
            "   and op.saldo > 0 " +
            "    and ((lower(pf.nome) like :parte) or (pf.cpf like :parte) or (op.numero like :parte) " +
            "      or (lower(pj.nomefantasia) like :parte) or (lower(pj.nomereduzido) like :parte) or (pj.cnpj like :parte)) ";
        Query q = em.createNativeQuery(sql, ObrigacaoAPagar.class);
        q.setParameter("idEmpenho", empenho.getId());
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setMaxResults(50);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public List<Conta> buscarContaDesdobradaPorObrigacaoPagar(String parte, ObrigacaoAPagar obrigacaoAPagar) {
        String sql = " " +
            "   select c.*, cd.* " +
            "    from desdobramentoobrigacaopaga desd " +
            "    inner join conta c on c.id = desd.conta_id " +
            "    inner join contadespesa cd on cd.id = c.id " +
            "    where desd.obrigacaoapagar_id = :idObrigacao " +
            "    AND (LOWER(C.DESCRICAO) LIKE :parte OR (replace(C.CODIGO,'.','') LIKE :parte or C.CODIGO LIKE :parte))" +
            "    ORDER BY C.CODIGO ";
        Query q = em.createNativeQuery(sql, ContaDespesa.class);
        q.setParameter("idObrigacao", obrigacaoAPagar.getId());
        q.setParameter("parte", "%" + parte + "%");
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public ObrigacaoAPagar buscarObrigacaoPagarPorEmpenho(Empenho empenho) {
        String sql = "select * from obrigacaoapagar where empenho_id = :idEmpenho ";
        Query q = em.createNativeQuery(sql, ObrigacaoAPagar.class);
        q.setParameter("idEmpenho", empenho.getId());
        q.setMaxResults(1);
        try {
            return (ObrigacaoAPagar) q.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public ObrigacaoAPagar buscarObrigacaoPagarNovaPorObrigacaoPagar(ObrigacaoAPagar obrigacaoAPagar) {
        String sql = "select * from obrigacaoapagar where obrigacaoAPagar_id = :idObrigacao ";
        Query q = em.createNativeQuery(sql, ObrigacaoAPagar.class);
        q.setParameter("idObrigacao", obrigacaoAPagar.getId());
        q.setMaxResults(1);
        try {
            return (ObrigacaoAPagar) q.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public DesdobramentoObrigacaoPagar buscarDesdobramentobrigacaoPagarNovaPorDesdobramentoObrigacaoPagar(DesdobramentoObrigacaoPagar desdobramento) {
        String sql = "select * from DESDOBRAMENTOOBRIGACAOPAGA where DesdobramentoObrigacaoPagar_id = :idDesdobramento ";
        Query q = em.createNativeQuery(sql, DesdobramentoObrigacaoPagar.class);
        q.setParameter("idDesdobramento", desdobramento.getId());
        q.setMaxResults(1);
        try {
            return (DesdobramentoObrigacaoPagar) q.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public EmpenhoFacade getEmpenhoFacade() {
        return empenhoFacade;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    public HistoricoContabilFacade getHistoricoContabilFacade() {
        return historicoContabilFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }


    public TipoDocumentoFiscalFacade getTipoDocumentoFiscalFacade() {
        return tipoDocumentoFiscalFacade;
    }

    public SingletonGeradorCodigoContabil getSingletonGeradorCodigoContabil() {
        return singletonGeradorCodigoContabil;
    }

    public ReprocessamentoLancamentoContabilSingleton getReprocessamentoLancamentoContabilSingleton() {
        return reprocessamentoLancamentoContabilSingleton;
    }

    public UFFacade getUfFacade() {
        return ufFacade;
    }

    public FonteDespesaORCFacade getFonteDespesaORCFacade() {
        return fonteDespesaORCFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public ClasseCredorFacade getClasseCredorFacade() {
        return classeCredorFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public LiquidacaoFacade getLiquidacaoFacade() {
        return liquidacaoFacade;
    }

    public LiquidacaoEstornoFacade getLiquidacaoEstornoFacade() {
        return liquidacaoEstornoFacade;
    }

    public PagamentoFacade getPagamentoFacade() {
        return pagamentoFacade;
    }

    public PagamentoEstornoFacade getPagamentoEstornoFacade() {
        return pagamentoEstornoFacade;
    }

    public EmpenhoEstornoFacade getEmpenhoEstornoFacade() {
        return empenhoEstornoFacade;
    }

    public ConfiguracaoTipoContaDespesaClasseCredorFacade getConfiguracaoTipoContaDespesaClasseCredorFacade() {
        return configuracaoTipoContaDespesaClasseCredorFacade;
    }

    @Override
    public List<ConsultaMovimentoContabil> criarConsulta(List<FiltroConsultaMovimentoContabil> filtros) {
        ConsultaMovimentoContabil consulta = new ConsultaMovimentoContabil(DesdobramentoObrigacaoPagar.class, filtros);
        consulta.incluirJoinsComplementar(" inner join obrigacaoAPagar obrig on obj.obrigacaoAPagar_id = obrig.id ");
        consulta.incluirJoinsOrcamentoDespesa(" obrig.fontedespesaorc_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_INICIAL, "trunc(obrig.dataLancamento)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_FINAL, "trunc(obrig.dataLancamento)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.UNIDADE, "obrig.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.LISTA_UNIDADE, "obrig.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.PESSOA, "obrig.pessoa_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CLASSE, "obrig.classecredor_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.EVENTO_CONTABIL, "obj.eventocontabil_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.NUMERO, "obrig.numero");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.HISTORICO, "obj.historicoRazao");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.VALOR, "obj.valor");
        return Arrays.asList(consulta);
    }

    public List<TipoDocumentoFiscal> buscarTiposDeDocumentosPorContaDeDespesa(ContaDespesa contaDespesa, TipoContaDespesa tipoContaDespesa, String filtro) {
        return configContaDespesaTipoDocumentoFacade.buscarTiposDeDocumentosPorContaDeDespesa(contaDespesa, tipoContaDespesa, filtro);
    }

    public ConfiguracaoContabilFacade getConfiguracaoContabilFacade() {
        return configuracaoContabilFacade;
    }

    public NotaOrcamentariaFacade getNotaOrcamentariaFacade() {
        return notaOrcamentariaFacade;
    }

    public List<NotaExecucaoOrcamentaria> buscarNotasDeObrigacoesAPagar(String condicao) {
        String sql = " SELECT " +
            "    nota.numero||'/'||exerc.ano as numero, " +
            "    nota.DATALANCAMENTO as data_nota, " +
            "    coalesce(nota.HISTORICONOTA, 'Histórico não cadastrado') as historico_obrigacao, " +
            "    emp.tipoempenho as tipo, " +
            "    TRIM(vworg.CODIGO) AS CD_ORGAO, " +
            "    TRIM(vworg.DESCRICAO) AS DESC_ORGAO, " +
            "    TRIM(vw.CODIGO) AS CD_UNIDADE, " +
            "    TRIM(vw.DESCRICAO) AS DESC_UNIDADE, " +
            "    substr(despesa.codigo, 8, 25) AS CD_PROG_TRABALHO, " +
            "    ct_desp.codigo as elemento, " +
            "    ct_desp.descricao as especificao_despesa, " +
            "    coalesce(pf.nome, pj.razaosocial) as nome_pessoa, " +
            "    formatacpfcnpj(coalesce(pf.cpf, pj.cnpj)) as cpf_cnpj, " +
            "    fonte_r.codigo || ' - ' || fonte_r.descricao as destinacao, " +
            "    NOTA.valor as valor, " +
            "    frt_extenso_monetario(NOTA.valor)||'  ***********************' as valor_extenso, " +
            "    coalesce(endereco.logradouro, 'Pessoa não possui um endereço cadastadro') as logradouro, " +
            "    coalesce(endereco.localidade, 'Pessoa não possui uma cidade cadastrada') as localidade, " +
            "    A.DESCRICAO AS DESC_ACAO, " +
            "    lic.numerolicitacao || ' / ' || to_char(lic.emitidaem,'dd/MM/YYYY') as modalidade, " +
            "    emp.MODALIDADELICITACAO as MODALIDADELICITACAO, " +
            "    classe.codigo || ' - ' || classe.descricao as desc_classepessoa, " +
            "    COALESCE(NOTA.SALDO,0) AS SALDO_LIQUIDAR, " +
            "    COALESCE(EMP.VALOR,0) - coalesce((SELECT SUM(EST.VALOR) FROM EMPENHOESTORNO EST WHERE EMPENHO_ID = NOTA.EMPENHO_ID), 0) AS VALOR_EMPENHADO, " +
            "    COALESCE(endereco.bairro,'Pessoa não possui bairro cadastrado') as bairro, " +
            "    COALESCE(ENDERECO.UF,'sem UF ') AS UF, " +
            "    COALESCE(SUBSTR(ENDERECO.CEP,1,5)||'-'||SUBSTR(ENDERECO.CEP,6,3),'sem CEP') AS CEP, " +
            "    vw.subordinada_id as idUnidadeOrg, " +
            "    nota.id as id " +
            " FROM OBRIGACAOAPAGAR NOTA " +
            "   left JOIN EMPENHO EMP ON NOTA.EMPENHO_ID = EMP.ID " +
            "   INNER JOIN fontedespesaorc FONTE ON NOTA.fontedespesaorc_id = fonte.id " +
            "   INNER JOIN despesaorc DESPESA ON fonte.despesaorc_id = despesa.id " +
            "   INNER JOIN provisaoppadespesa PROVISAO ON despesa.provisaoppadespesa_id= provisao.id " +
            "   inner join provisaoppafonte ppf on ppf.id = fonte.PROVISAOPPAFONTE_ID " +
            "   INNER JOIN SUBACAOPPA SUB ON SUB.ID = provisao.subacaoppa_id " +
            "   INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id " +
            "   INNER JOIN TIPOACAOPPA TPA ON TPA.ID = A.TIPOACAOPPA_ID " +
            "   INNER JOIN programappa P ON P.ID = A.programa_id " +
            "   INNER JOIN FUNCAO F ON F.ID = A.funcao_id " +
            "   INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id " +
            "   INNER JOIN CONTA CT_DESP ON provisao.contadedespesa_id = CT_DESP.ID " +
            "   inner join exercicio exerc on nota.exercicio_id = exerc.id " +
            "   inner join vwhierarquiaorcamentaria vw on NOTA.unidadeorganizacional_id  = vw.subordinada_id " +
            "   inner join vwhierarquiaorcamentaria vworg on vw.superior_id  = vworg.subordinada_id " +
            "   INNER JOIN PESSOA Pes ON NOTA.PESSOA_ID = Pes.id " +
            "   left join pessoafisica pf on pes.id = pf.id " +
            "   left join pessoajuridica pj on pes.id = pj.id " +
            "   left join enderecocorreio endereco on pes.enderecoprincipal_id = endereco.id " +
            "   left join contadedestinacao cd on cd.id = ppf.destinacaoderecursos_id " +
            "   left join fontederecursos fonte_r on fonte_r.id = cd.fontederecursos_id and fonte_r.exercicio_id = exerc.id " +
            "   left join contrato contrato on emp.contrato_id = contrato.id " +
            "   left join conlicitacao conlic on contrato.id = conlic.CONTRATO_ID " +
            "   left join licitacao lic on conlic.licitacao_id = lic.id " +
            "   left join classecredor classe on NOTA.classecredor_id = classe.id " +
            " where cast(NOTA.DATALANCAMENTO as Date) between vw.iniciovigencia and coalesce(vw.fimvigencia, cast(nota.DATALANCAMENTO as Date)) " +
            "   AND cast(nota.DATALANCAMENTO as Date) BETWEEN VWORG.INICIOVIGENCIA AND COALESCE(VWORG.FIMVIGENCIA, cast(nota.DATALANCAMENTO as Date)) " +
            condicao;
        Query q = em.createNativeQuery(sql);
        List<Object[]> resultado = q.getResultList();
        List<NotaExecucaoOrcamentaria> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                NotaExecucaoOrcamentaria nota = new NotaExecucaoOrcamentaria();
                nota.setNomeDaNota("NOTA DE OBRIGAÇÃO A PAGAR");
                nota.setNumeroDocumento((String) obj[0]);
                nota.setData(DataUtil.getDataFormatada((Date) obj[1]));
                nota.setHistorico((String) obj[2]);
                nota.setTipoEmpenho(obj[3] != null ? TipoEmpenho.valueOf((String) obj[3]).getDescricao() : "");
                nota.setCodigoOrgao((String) obj[4]);
                nota.setDescricaoOrgao((String) obj[5]);
                nota.setCodigoUnidade((String) obj[6]);
                nota.setDescricaoUnidade((String) obj[7]);
                nota.setCodigoProgramaTrabalho((String) obj[8]);
                nota.setNaturezaDespesa((String) obj[9]);
                nota.setEspecificacaoDespesa((String) obj[10]);
                nota.setNomePessoa((String) obj[11]);
                nota.setCpfCnpj((String) obj[12]);
                nota.setDescricaoDestinacao((String) obj[13]);
                nota.setValor((BigDecimal) obj[14]);
                nota.setValorPorExtenso((String) obj[15]);
                nota.setLogradouro((String) obj[16]);
                nota.setLocalidade((String) obj[17]);
                nota.setCidade((String) obj[17]);
                nota.setDescricaoProjetoAtividade((String) obj[18]);
                nota.setModalidadeLicitacao(obj[20] != null ? ModalidadeLicitacaoEmpenho.valueOf((String) obj[20]).getDescricao() + " " + (String) obj[19] : "");
                nota.setClassePessoa((String) obj[21]);
                nota.setSaldoAtual((BigDecimal) obj[22]);
                nota.setValorEmpenhado((BigDecimal) obj[23]);
                nota.setBairro((String) obj[24]);
                nota.setUf((String) obj[25]);
                nota.setCep((String) obj[26]);
                nota.setIdUnidadeOrcamentaria((BigDecimal) obj[27]);
                nota.setId(((BigDecimal) obj[28]).longValue());
                nota.setDocumentosComprobatorios(buscarDocumentos(nota.getId()));
                nota.setDetalhamentos(buscarDetalhamentos(nota.getId()));
                retorno.add(nota);
            }
        }
        return retorno;
    }

    private List<NotaExecucaoOrcamentariaDocumentoComprobatorio> buscarDocumentos(Long idObrigacaoAPagar) {
        String sql = " select " +
            "  coalesce(doc.numero, '') as numero, " +
            "  coalesce(doc.serie, '') as serie, " +
            "  doc.datadocto as data, " +
            "  coalesce(tipo.descricao, '') as tipo, " +
            "  coalesce(uf.nome, '') as uf, " +
            "  coalesce(obr.valor, 0) as valor " +
            " from ObrigacaoAPagarDoctoFiscal obr " +
            "   inner join DoctoFiscalLiquidacao doc on obr.DOCUMENTOFISCAL_ID = doc.id " +
            "   left join tipodocumentofiscal tipo on doc.tipodocumentofiscal_id = tipo.id " +
            "   left join uf uf on doc.uf_id = uf.id " +
            " where obr.OBRIGACAOAPAGAR_ID = :idObrigacaoAPagar ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idObrigacaoAPagar", idObrigacaoAPagar);
        List<Object[]> resultado = q.getResultList();
        List<NotaExecucaoOrcamentariaDocumentoComprobatorio> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                NotaExecucaoOrcamentariaDocumentoComprobatorio dc = new NotaExecucaoOrcamentariaDocumentoComprobatorio();
                dc.setNumero((String) obj[0]);
                dc.setSerie((String) obj[1]);
                dc.setData((Date) obj[2]);
                dc.setTipo((String) obj[3]);
                dc.setUf((String) obj[4]);
                dc.setValor((BigDecimal) obj[5]);
                retorno.add(dc);
            }
        }
        return retorno;
    }

    private List<NotaExecucaoOrcamentariaDetalhamento> buscarDetalhamentos(Long idObrigacaoAPagar) {
        String sql = " select " +
            "  c.codigo as conta, " +
            "  c.descricao, " +
            "  eve.codigo as evento, " +
            "  desd.valor " +
            " from desdobramentoobrigacaopaga desd " +
            "   inner join conta c on desd.conta_id = c.id " +
            "   left join eventocontabil eve on desd.eventocontabil_id = eve.id " +
            " where desd.OBRIGACAOAPAGAR_ID = :idObrigacaoAPagar ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idObrigacaoAPagar", idObrigacaoAPagar);
        List<Object[]> resultado = q.getResultList();
        List<NotaExecucaoOrcamentariaDetalhamento> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                NotaExecucaoOrcamentariaDetalhamento d = new NotaExecucaoOrcamentariaDetalhamento();
                d.setConta((String) obj[0]);
                d.setDescricao((String) obj[1]);
                d.setEvento((String) obj[2]);
                d.setValor((BigDecimal) obj[3]);
                retorno.add(d);
            }
        }
        return retorno;
    }
}
