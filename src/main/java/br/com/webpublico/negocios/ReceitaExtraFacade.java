/*
 * Codigo gerado automaticamente em Thu Dec 22 17:19:38 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.NotaExecucaoOrcamentaria;
import br.com.webpublico.entidadesauxiliares.NotaExecucaoOrcamentariaPagamento;
import br.com.webpublico.enums.*;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.ReprocessamentoLancamentoContabilSingleton;
import br.com.webpublico.negocios.contabil.reprocessamento.SuperFacadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.ConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.FiltroConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.singleton.SingletonConcorrenciaContabil;
import br.com.webpublico.singletons.SingletonGeradorCodigoContabil;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Stateless
@LocalBean
public class ReceitaExtraFacade extends SuperFacadeContabil<ReceitaExtra> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SaldoExtraorcamentarioFacade saldoExtraorcamentarioFacade;
    @EJB
    private SubContaFacade subContaFacade;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private PlanoDeContasFacade planoDeContasFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ClasseCredorFacade classeCredorFacade;
    @EJB
    private SaldoSubContaFacade saldoSubContaFacade;
    @EJB
    private ConfigReceitaExtraFacade configReceitaExtraFacade;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SingletonGeradorCodigoContabil singletonGeradorCodigoContabil;
    @EJB
    private ReprocessamentoLancamentoContabilSingleton reprocessamentoLancamentoContabilSingleton;
    @EJB
    private LiquidacaoFacade liquidacaoFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private SingletonConcorrenciaContabil singletonConcorrenciaContabil;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private NotaOrcamentariaFacade notaOrcamentariaFacade;


    public ReceitaExtraFacade() {
        super(ReceitaExtra.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfigReceitaExtraFacade getConfigReceitaExtraFacade() {
        return configReceitaExtraFacade;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    @Override
    public ReceitaExtra recarregar(ReceitaExtra entity) {
        Query q;
        q = getEntityManager().createQuery("From ReceitaExtra re left join fetch re.unidadeOrganizacional where re = :param");
        q = getEntityManager().createQuery("From ReceitaExtra re left join fetch re.retencaoPgto rp left join fetch rp.pagamento  where re = :param");
        q.setParameter("param", entity);
        return (ReceitaExtra) q.getSingleResult();
    }


    public void salvarNovo(ReceitaExtra entity) {
        try {
            if (reprocessamentoLancamentoContabilSingleton.isCalculando()) {
                throw new ExcecaoNegocioGenerica(getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
            } else {
                hierarquiaOrganizacionalFacade.validaVigenciaHIerarquiaAdministrativaOrcamentaria(entity.getUnidadeOrganizacionalAdm(), entity.getUnidadeOrganizacional(), entity.getDataReceita());
                if (entity.getId() == null) {
                    try {
                        entity.setNumero(singletonGeradorCodigoContabil.getNumeroReceitaExtra(entity.getExercicio(), entity.getUnidadeOrganizacional(), entity.getDataReceita()));
                    } catch (NonUniqueResultException ex) {
                        throw new ExcecaoNegocioGenerica("O número da receita extraorçamentária " + entity.getNumero() + " já foi utilizado. Inicie o processo novamente. " + ex.getMessage());
                    }
                    em.persist(entity);
                } else {
                    recuperarDesdobramentoParaHistorico(entity);
                    entity.gerarHistoricos();
                    entity = em.merge(entity);
                }
                gerarSaldoExtraorcamentario(entity);
                gerarSaldoFinanceiro(entity);
                contabilizarReceitaExtra(entity);
            }
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    private void gerarSaldoExtraorcamentario(ReceitaExtra entity) {
        saldoExtraorcamentarioFacade.gerarSaldoExtraorcamentario(entity.getDataReceita(),
            TipoOperacao.CREDITO,
            entity.getValor(),
            entity.getContaExtraorcamentaria(),
            entity.getContaDeDestinacao(),
            entity.getUnidadeOrganizacional(),
            entity.getId().toString(), entity.getClass().getSimpleName());
    }

    public void salvarReceitaTransportada(ReceitaExtra entity) {
        try {
            em.persist(entity);
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Erro ao transportar receita extra " + ex.getMessage());
        }
    }

    public ReceitaExtra salvarRetornando(ReceitaExtra entity) {
        try {
            entity = recuperarDesdobramentoParaHistorico(entity);
            entity.gerarHistoricos();
            return em.merge(entity);
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    public void salvarReceitaSemContabilizar(ReceitaExtra entity) {
        try {
            if (reprocessamentoLancamentoContabilSingleton.isCalculando()) {
                throw new ExcecaoNegocioGenerica(getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
            } else {
                if (entity.getId() == null) {
                    try {
                        entity.setNumero(singletonGeradorCodigoContabil.getNumeroReceitaExtra(entity.getExercicio(), entity.getUnidadeOrganizacional(), entity.getDataReceita()));
                    } catch (NonUniqueResultException ex) {
                        throw new ExcecaoNegocioGenerica("O número da receita extraorçamentária " + entity.getNumero() + " já foi utilizado. Inicie o processo novamente. " + ex.getMessage());
                    }
                    entity = recuperarDesdobramentoParaHistorico(entity);
                    entity.gerarHistoricos();
                    entity.setEventoContabil(null);
                    entity.setTransportado(true);
                    entity.setDataConciliacao(null);
                    em.persist(entity);
                } else {
                    entity = recuperarDesdobramentoParaHistorico(entity);
                    entity.gerarHistoricos();
                    entity = em.merge(entity);
                }
            }
        } catch (ExcecaoNegocioGenerica ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    private void gerarSaldoFinanceiro(ReceitaExtra entity) {
        if (entity.getRetencaoPgto() == null) {
            saldoSubContaFacade.gerarSaldoFinanceiro(entity.getUnidadeOrganizacional(),
                entity.getSubConta(),
                entity.getContaDeDestinacao(),
                entity.getValor(),
                TipoOperacao.CREDITO,
                entity.getDataReceita(),
                entity.getEventoContabil(),
                entity.getComplementoHistorico(),
                MovimentacaoFinanceira.RECEITA_EXTRAORCAMENTARIA,
                entity.getUuid(),
                true);
        }
    }

    @Override
    public void salvar(ReceitaExtra entity) {
        em.merge(entity);
    }

    public String retornaUltimoNumeroReceitaExtra() {
        String sql = "SELECT re.numero FROM receitaextra re ORDER BY to_number(re.numero) DESC";
        Query q = em.createNativeQuery(sql);
        q.setMaxResults(1);
        List<String> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            return resultado.get(0);
        } else {
            return "0";
        }
    }

    public List<ReceitaExtra> buscarReceitasExtrasNoExercicio(String filtro, Exercicio exercicio) {
        String sql = " select rex.* from receitaextra rex " +
            "   where rex.numero like :parte " +
            "   and rex.exercicio_id = :exercicio_id " +
            " order by rex.datareceita desc ";
        Query q = em.createNativeQuery(sql, ReceitaExtra.class);
        q.setParameter("parte", "%" + filtro.trim().toLowerCase() + "%");
        q.setParameter("exercicio_id", exercicio.getId());
        q.setMaxResults(10);
        List<ReceitaExtra> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            return resultado;
        }
        return new ArrayList<>();
    }

    public List<ReceitaExtra> listaPorPagamentoExtra(PagamentoExtra pag) {
        String sql = "SELECT RE.* "
            + " FROM PAGAMENTOEXTRA PE"
            + " INNER JOIN PAGAMENTORECEITAEXTRA PRE ON PE.ID = PRE.PAGAMENTOEXTRA_ID and PRE.pagamentoEstornoRecExtra_id is null"
            + " LEFT JOIN RECEITAEXTRA RE ON PRE.RECEITAEXTRA_ID = RE.ID"
            + " WHERE PE.ID = :pag ";
        Query q = em.createNativeQuery(sql, ReceitaExtra.class);
        q.setParameter("pag", pag.getId());
        return q.getResultList();
    }

    public List<ReceitaExtra> listaFiltrandoReceitasAbertasNaoConsignadas(String parte, UnidadeOrganizacional unidade) {

        String hql = "select re from ReceitaExtra re "
            + " where re.unidadeOrganizacional = :unidade "
            + " and re.situacaoReceitaExtra = 'ABERTO' "
            + " and re.saldo > 0 "
            + " and re.retencaoPgto is null "
            + " and re.transportado = 0 "
            + " and (re.numero like :parte "
            + "  or ( re.pessoa in ( select pj from PessoaJuridica pj where pj = re.pessoa and  (lower(pj.razaoSocial) like :parte or pj.cnpj like :parte))"
            + "  or re.pessoa in ( select pf from PessoaFisica pf where  pf = re.pessoa and  (lower(pf.nome) like :parte or pf.cpf like :parte )))) "
            + " order by re.dataReceita desc";
        Query q = em.createQuery(hql);
        q.setParameter("unidade", unidade);
        q.setParameter("parte", "%" + parte.trim() + "%");
        q.setMaxResults(10);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<ReceitaExtra>();
    }

    public List<ReceitaExtra> buscarReceitaExtraAbertaPorContaExtraUnidadeFonteAndContaFinanceira(UnidadeOrganizacional unidade, ContaExtraorcamentaria contaExtra, FonteDeRecursos fonteRecurso, SubConta contaFinanceira) {

        String sql = " " +
            " select rex.* from receitaextra rex " +
            "  inner join fontederecursos fdr on rex.fontederecursos_id = fdr.id  " +
            "  inner join conta conta on rex.contaextraorcamentaria_id = conta.id  " +
            "  inner join unidadeorganizacional uni on uni.id = rex.unidadeorganizacional_id  " +
            "   where rex.situacaoreceitaextra = '" + SituacaoReceitaExtra.ABERTO.name() + "'" +
            "   and rex.saldo > 0 " +
            "   and fdr.id = :idFonteRecurso " +
            "   and conta.id = :idContaExtra " +
            "   and rex.subconta_id = :idContaFinanceira " +
            "   and uni.id = :idUnidade " +
            " order by rex.datareceita, rex.numero ";
        Query q = em.createNativeQuery(sql, ReceitaExtra.class);
        q.setParameter("idUnidade", unidade.getId());
        q.setParameter("idContaExtra", contaExtra.getId());
        q.setParameter("idFonteRecurso", fonteRecurso.getId());
        q.setParameter("idContaFinanceira", contaFinanceira.getId());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public List<ReceitaExtra> listaReceitaPorUnidadeContaExtraSubContaFonte(UnidadeOrganizacional unidadeOrganizacional, Conta contaExtraorcamentaria, SubConta subConta,
                                                                            FonteDeRecursos fonteDeRecursos, Date dataInicial, Date dataFinal) {
        String hql = "SELECT re.* FROM receitaextra re"
            + " INNER JOIN subconta sc ON re.subconta_id = sc.id AND sc.id = :subconta"
            + " INNER JOIN fontederecursos fdr ON re.fontederecursos_id = fdr.id AND fdr.id = :fonte"
            + " INNER JOIN conta conta ON re.contaextraorcamentaria_id = conta.id AND conta.dtype = 'ContaExtraorcamentaria' AND conta.id = :conta"
            + " INNER JOIN UNIDADEORGANIZACIONAL UNI ON UNI.ID = re.UNIDADEORGANIZACIONAL_ID AND uni.id = :unidade"
            + " WHERE re.situacaoReceitaExtra = 'ABERTO'"
            + " AND re.saldo > 0 "
            + " AND re.id not in (select pre.receitaextra_id from pagamentoreceitaextra pre where pre.receitaextra_id = re.id and pre.pagamentoestornorecextra_id is null) "
            + (dataInicial != null ? " and trunc(re.datareceita) >= to_date(:dataInicial, 'dd/MM/yyyy') " : "")
            + (dataFinal != null ? " and trunc(re.datareceita) <= to_date(:dataFinal, 'dd/MM/yyyy') " : "")
            + " order by re.datareceita desc";
        Query q = em.createNativeQuery(hql, ReceitaExtra.class);
        q.setParameter("unidade", unidadeOrganizacional.getId());
        q.setParameter("conta", contaExtraorcamentaria.getId());
        q.setParameter("subconta", subConta.getId());
        q.setParameter("fonte", fonteDeRecursos.getId());
        if (dataInicial != null) {
            q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        }
        if (dataFinal != null) {
            q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        }
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<ReceitaExtra>();
    }

    public void contabilizarReceitaExtra(ReceitaExtra entity) throws ExcecaoNegocioGenerica {
        try {
            recuperarDesdobramentoParaHistorico(entity);
            ConfigReceitaExtra configReceitaExtra = configReceitaExtraFacade.recuperaEventoPorContaReceita(TipoLancamento.NORMAL, entity);
            if (configReceitaExtra != null && configReceitaExtra.getEventoContabil() != null) {
                entity.setEventoContabil(configReceitaExtra.getEventoContabil());
            } else {
                throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil de Receita Extra para a operação selecionada.");
            }
            if (entity.getEventoContabil() != null) {

                entity.gerarHistoricos();
                ParametroEvento parametroEvento = new ParametroEvento();
                parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
                parametroEvento.setDataEvento(entity.getDataReceita());
                parametroEvento.setUnidadeOrganizacional(entity.getUnidadeOrganizacional());
                parametroEvento.setExercicio(entity.getExercicio());
                parametroEvento.setIdOrigem(entity.getId().toString());
                parametroEvento.setClasseOrigem(entity.getClass().getSimpleName());
                parametroEvento.setEventoContabil(entity.getEventoContabil());

                ItemParametroEvento item = new ItemParametroEvento();
                item.setValor(entity.getValor());
                item.setParametroEvento(parametroEvento);
                item.setTagValor(TagValor.LANCAMENTO);
                item.setOperacaoClasseCredor(classeCredorFacade.recuperaOperacaoAndVigenciaClasseCredor(entity.getPessoa(), entity.getClasseCredor(), entity.getDataReceita()));
//            item.setOperacaoClasseCredor(entity.getClasseCredor().getOperacaoClasseCredor());

                List<ObjetoParametro> objetos = Lists.newArrayList();
                objetos.add(new ObjetoParametro(entity, item));
                objetos.add(new ObjetoParametro(entity.getContaExtraorcamentaria(), item));
                objetos.add(new ObjetoParametro(entity.getSubConta(), item));
                if (entity.getRetencaoPgto() != null) {
                    objetos.add(new ObjetoParametro(entity.getRetencaoPgto().getPagamento().getLiquidacao().getEmpenho().getClasseCredor(), item));
                    objetos.add(new ObjetoParametro(entity.getRetencaoPgto().getPagamento().getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa(), item));
                    objetos.add(new ObjetoParametro(entity.getRetencaoPgto().getPagamento().getLiquidacao().getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao().getFonteDeRecursos(), item));
                    if (entity.getRetencaoPgto().getPagamento().getLiquidacao().getEmpenho().getDividaPublica() != null) {
                        objetos.add(new ObjetoParametro(entity.getRetencaoPgto().getPagamento().getLiquidacao().getEmpenho().getDividaPublica().getCategoriaDividaPublica(), item));
                        objetos.add(new ObjetoParametro(entity.getRetencaoPgto().getPagamento().getLiquidacao().getEmpenho().getDividaPublica(), item));
                    }
                } else {
                    objetos.add(new ObjetoParametro(entity.getClasseCredor(), item));
                }
                item.setObjetoParametros(objetos);
                parametroEvento.getItensParametrosEvento().add(item);

                contabilizacaoFacade.contabilizar(parametroEvento);

            } else {
                throw new ExcecaoNegocioGenerica("Não existe configuração para Receita Extra");
            }
        } catch (ExcecaoNegocioGenerica ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }

    }

    private ReceitaExtra recuperarDesdobramentoParaHistorico(ReceitaExtra entity) {
        if (entity.getRetencaoPgto() != null) {
            if (!Hibernate.isInitialized(entity.getRetencaoPgto().getPagamento().getLiquidacao().getDesdobramentos())) {
                entity.getRetencaoPgto().getPagamento().setLiquidacao(liquidacaoFacade.recuperar(entity.getRetencaoPgto().getPagamento().getLiquidacao().getId()));
            }
        }
        return entity;
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

    @Override
    public void contabilizarReprocessamento(EntidadeContabil entidadeContabil) {
        contabilizarReceitaExtra((ReceitaExtra) entidadeContabil);
    }

    public List<ReceitaExtra> listaReceitaExtraParaTransportar(Date dtInicio, Date dtFim, UnidadeOrganizacional unidadeOrganizacional, Conta contaExtra, SubConta contaFinanceira, ContaDeDestinacao contaDeDestinacao, Pessoa pessoa, ClasseCredor classe) {

        String sql = " SELECT REC.* FROM RECEITAEXTRA REC "
            + " INNER JOIN UNIDADEORGANIZACIONAL UNID ON UNID.ID = REC.UNIDADEORGANIZACIONAL_ID "
            + " WHERE REC.SALDO > 0 "
            + " AND rec.situacaoreceitaextra = 'ABERTO'"
            + " AND REC.UNIDADEORGANIZACIONAL_ID = :idUnidade "
            + " AND trunc(REC.DATARECEITA) BETWEEN to_date(:dataInicial, 'dd/MM/yyyy') AND to_date(:dataFinal, 'dd/MM/yyyy') ";
        if (contaExtra != null) {
            if (contaExtra.getId() != null) {
                sql += " AND REC.CONTAEXTRAORCAMENTARIA_ID = :idContaExtra";
            }
        }
        if (contaFinanceira != null) {
            if (contaFinanceira.getId() != null) {
                sql += " AND REC.SUBCONTA_ID = :idContaFinanceira";
            }
        }
        if (contaDeDestinacao != null) {
            if (contaDeDestinacao.getId() != null) {
                sql += " AND REC.CONTADEDESTINACAO_ID = :idContaDest";
            }
        }
        if (pessoa != null) {
            if (pessoa.getId() != null) {
                sql += " AND REC.PESSOA_ID = :idPessoa";
            }
        }
        if (classe != null) {
            if (classe.getId() != null) {
                sql += " AND REC.CLASSECREDOR_ID = :idClasse";
            }
        }
        sql += " order by REC.DATARECEITA desc, REC.NUMERO desc";
        Query q = em.createNativeQuery(sql, ReceitaExtra.class);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dtInicio));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dtFim));
        q.setParameter("idUnidade", unidadeOrganizacional.getId());
        if (contaExtra != null) {
            if (contaExtra.getId() != null) {
                q.setParameter("idContaExtra", contaExtra.getId());
            }
        }
        if (contaFinanceira != null) {
            if (contaFinanceira.getId() != null) {
                q.setParameter("idContaFinanceira", contaFinanceira.getId());
            }
        }
        if (contaDeDestinacao != null) {
            if (contaDeDestinacao.getId() != null) {
                q.setParameter("idContaDest", contaDeDestinacao.getId());
            }
        }
        if (pessoa != null) {
            if (pessoa.getId() != null) {
                q.setParameter("idPessoa", pessoa.getId());
            }
        }
        if (classe != null) {
            if (classe.getId() != null) {
                q.setParameter("idClasse", classe.getId());
            }
        }
        List<ReceitaExtra> receitaExtras = q.getResultList();
        if (!receitaExtras.isEmpty()) {
            return receitaExtras;
        } else {
            return new ArrayList<>();
        }
    }

    public void estornarConciliacao(ReceitaExtra receitaExtra) {
        try {
            receitaExtra.setDataConciliacao(null);
            getEntityManager().merge(receitaExtra);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Erro ao estornar a baixa da despesa extra. Consulte o suporte!");
        }
    }

    public void estornarBaixa(ReceitaExtra receitaExtra) {
        try {
            if (listaEstornoReceitaExtra(receitaExtra).isEmpty()) {
                receitaExtra.setSaldo(receitaExtra.getSaldo().add(receitaExtra.getValor()));
                receitaExtra.setSituacaoReceitaExtra(SituacaoReceitaExtra.ABERTO);
            } else {
                BigDecimal valor;
                valor = receitaExtra.getValor().subtract(getSomaEstornosReceitaExtra(receitaExtra));
                receitaExtra.setSaldo(receitaExtra.getSaldo().add(valor));
                receitaExtra.setSituacaoReceitaExtra(SituacaoReceitaExtra.ABERTO);
            }
            getEntityManager().merge(receitaExtra);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Erro ao estornar a baixa. Consulte o suporte!");
        }
    }

    public void baixar(ReceitaExtra receitaExtra) {
        receitaExtra.setSaldo(receitaExtra.getSaldo().subtract(receitaExtra.getSaldo()));
        receitaExtra.setSituacaoReceitaExtra(SituacaoReceitaExtra.EFETUADO);
        getEntityManager().merge(receitaExtra);
    }

    public BigDecimal getSomaEstornosReceitaExtra(ReceitaExtra receitaExtra) {
        BigDecimal estornos = new BigDecimal(BigInteger.ZERO);
        for (ReceitaExtraEstorno est : listaEstornoReceitaExtra(receitaExtra)) {
            estornos = estornos.add(est.getValor());
        }
        return estornos;
    }

    public List<ReceitaExtraEstorno> listaEstornoReceitaExtra(ReceitaExtra re) {
        String sql = " SELECT EST.* FROM RECEITAEXTRAESTORNO EST "
            + " INNER JOIN RECEITAEXTRA RE ON RE.ID = EST.RECEITAEXTRA_ID "
            + " WHERE RE.ID = :receita ";
        Query q = em.createNativeQuery(sql, ReceitaExtraEstorno.class);
        q.setParameter("receita", re.getId());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public List<ReceitaExtra> recuperarReceitaExtraDaPessoa(Pessoa pessoa) {
        String sql = " select r.* from receitaextra r " +
            "       where r.pessoa_id = :pessoa        ";
        Query q = em.createNativeQuery(sql, ReceitaExtra.class);
        q.setParameter("pessoa", pessoa.getId());
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        }
        return q.getResultList();
    }

    public boolean canEstornarBaixa(ReceitaExtra receitaExtra) {
        String sql = "select 1 from PagamentoExtra pe " +
            "inner join PAGAMENTORECEITAEXTRA pre on pre.PAGAMENTOEXTRA_ID = pe.id " +
            "where pre.RECEITAEXTRA_ID = :receitaId " +
            "and pe.STATUS not in (:estornado, :indeferido) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("receitaId", receitaExtra.getId());
        q.setParameter("estornado", StatusPagamento.ESTORNADO.name());
        q.setParameter("indeferido", StatusPagamento.INDEFERIDO.name());
        return q.getResultList().isEmpty();
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public SaldoExtraorcamentarioFacade getSaldoExtraorcamentarioFacade() {
        return saldoExtraorcamentarioFacade;
    }

    public SubContaFacade getSubContaFacade() {
        return subContaFacade;
    }

    public ClasseCredorFacade getClasseCredorFacade() {
        return classeCredorFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public SaldoSubContaFacade getSaldoSubContaFacade() {
        return saldoSubContaFacade;
    }

    public PlanoDeContasFacade getPlanoDeContasFacade() {
        return planoDeContasFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public FonteDeRecursosFacade getFonteDeRecursosFacade() {
        return fonteDeRecursosFacade;
    }

    public SingletonConcorrenciaContabil getSingletonConcorrenciaContabil() {
        return singletonConcorrenciaContabil;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public NotaOrcamentariaFacade getNotaOrcamentariaFacade() {
        return notaOrcamentariaFacade;
    }

    @Override
    public List<ConsultaMovimentoContabil> criarConsulta(List<FiltroConsultaMovimentoContabil> filtros) {
        ConsultaMovimentoContabil consulta = new ConsultaMovimentoContabil(ReceitaExtra.class, filtros);
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_INICIAL, "trunc(obj.dataReceita)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_FINAL, "trunc(obj.dataReceita)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.UNIDADE, "obj.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.LISTA_UNIDADE, "obj.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.PESSOA, "obj.pessoa_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CLASSE, "obj.classecredor_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.EVENTO_CONTABIL, "obj.eventocontabil_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.NUMERO, "obj.numero");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CONTA_FINANCEIRA, "obj.subConta_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.FONTE_DE_RECURSO, "obj.fonteDeRecursos_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CONTA_EXTRA, "obj.contaExtraorcamentaria_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.HISTORICO, "obj.historicorazao");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.VALOR, "obj.valor");
        return Arrays.asList(consulta);
    }

    public NotaExecucaoOrcamentaria buscarNotaReceitaExtra(ReceitaExtra receitaExtra, UsuarioSistema usuarioCorrente) {
        String sql = " SELECT " +
            "     nota.numero||'/'||exerc.ano as numero, " +
            "     nota.DATARECEITA as data_nota, " +
            "     coalesce(trim(nota.HISTORICONOTA), 'Histórico não cadastrado') as historico_nota, " +
            "     TRIM(vworg.CODIGO) AS CD_ORGAO, " +
            "     TRIM(vworg.DESCRICAO) AS DESC_ORAGAO, " +
            "     TRIM(vw.CODIGO) AS CD_UNIDADE, " +
            "     TRIM(vw.DESCRICAO) AS DESC_UNIDADE, " +
            "     coalesce(pf.nome, pj.razaosocial) as nome_pessoa, " +
            "     formatacpfcnpj(coalesce(pf.cpf, pj.cnpj)) as cpf_cnpj, " +
            "     fonte.codigo || ' - ' || fonte.descricao as descricao_destinacao, " +
            "     NOTA.valor as valor, " +
            "     c.codigo || ' - ' || c.descricao as contaextra, " +
            "     coalesce(nota.valor, 0) as saldo_anterior, " +
            "     coalesce(nota.SALDO, 0) as saldo_atual, " +
            "     frt_extenso_monetario(NOTA.valor)||'  ***********************' as valor_extenso, " +
            "     classe.codigo || ' - ' || classe.descricao as desc_classepessoa, " +
            "     COALESCE(endereco.bairro,'Pessoa não possui bairro cadastrado') as bairro, " +
            "     COALESCE(ENDERECO.uf,'sem UF ') AS UF, " +
            "     COALESCE(SUBSTR(ENDERECO.CEP,1,5)||'-'||SUBSTR(ENDERECO.CEP,6,3),'sem CEP') AS CEP, " +
            "     COALESCE(ENDERECO.LOGRADOURO,'Pessoa não possui Logradouro') AS LOGRADOURO, " +
            "     coalesce(endereco.localidade,'Pessoa não possui Localidade') as localidade, " +
            "     coalesce(ag.nomeagencia,'Pessoa não possui agência cadastrada') as nomeagencia, " +
            "     coalesce(banco.descricao,'Pessoa não possui banco cadastrado') as descricao, " +
            "     coalesce(replace(banco.numerobanco||' / '||ag.numeroagencia||' / '||cc.numeroconta||'-'||cc.digitoverificador,' //- '),'Pessoa sem Dados Bancarios') as banco_gencia_conta, " +
            "     coalesce(bco.numerobanco||' / '||age.numeroagencia || '-' || AGe.DIGITOVERIFICADOR ||' / '||cbe.numeroconta||'-'||cbe.digitoverificador || '/' || sc.codigo || '-' || sc.descricao,' ') as banco_agencia_conta, " +
            "     bco.numerobanco as banco, " +
            "     ag.numeroagencia as agencia," +
            "     vw.subordinada_id as idUnidadeOrc, " +
            "     nota.RETENCAOPGTO_id " +
            " FROM receitaextra NOTA " +
            "   inner join fontederecursos fonte on nota.fontederecursos_id = fonte.id " +
            "   inner join conta c on contaextraorcamentaria_id = c.id " +
            "   INNER JOIN PESSOA Pes ON nota.PESSOA_ID = Pes.id " +
            "   left join pessoafisica pf on pes.id = pf.id " +
            "   LEFT JOIN PESSOAJURIDICA PJ ON PES.ID = PJ.ID " +
            "   LEFT JOIN ENDERECOCORREIO ENDERECO ON PES.ENDERECOPRINCIPAL_ID = ENDERECO.ID " +
            "   left join ContaCorrenteBancPessoa ccP on pes.id = ccp.pessoa_id and ccp.principal = 1 " +
            "   left join ContaCorrenteBancaria cc on ccp.ContaCorrenteBancaria_id = cc.id " +
            "   left join agencia ag on cc.agencia_id =ag.id " +
            "   left join banco banco on ag.banco_id=banco.id " +
            "   inner join vwhierarquiaorcamentaria vw on nota.unidadeorganizacional_id  = vw.subordinada_id " +
            "   inner join vwhierarquiaorcamentaria vworg on vw.superior_id  = vworg.subordinada_id " +
            "   inner join exercicio exerc on nota.exercicio_id = exerc.id " +
            "   inner join subconta sc on nota.subconta_id = sc.id " +
            "   inner join contabancariaentidade cbe on sc.CONTABANCARIAENTIDADE_ID = cbe.id " +
            "   inner join agencia age on cbe.agencia_id = age.id " +
            "   inner join banco bco on age.banco_id = bco.id " +
            "   left join classecredor classe on nota.classecredor_id = classe.id " +
            " where trunc(nota.DATARECEITA) between vw.INICIOVIGENCIA and coalesce(vw.fimvigencia, trunc(nota.DATARECEITA)) " +
            "   AND trunc(NOTA.DATARECEITA) BETWEEN VWORG.INICIOVIGENCIA AND COALESCE(VWORG.FIMVIGENCIA, trunc(NOTA.DATARECEITA)) " +
            "   and NOTA.id = :idReceitaExtra ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idReceitaExtra", receitaExtra.getId());
        List<Object[]> resultado = q.getResultList();
        List<NotaExecucaoOrcamentaria> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                NotaExecucaoOrcamentaria nota = new NotaExecucaoOrcamentaria();
                nota.setNumeroDocumento((String) obj[0]);
                nota.setData(DataUtil.getDataFormatada((Date) obj[1]));
                nota.setHistorico((String) obj[2] + " Usuário Emissor: " + usuarioCorrente.getNome());
                nota.setCodigoOrgao((String) obj[3]);
                nota.setDescricaoOrgao((String) obj[4]);
                nota.setCodigoUnidade((String) obj[5]);
                nota.setDescricaoUnidade((String) obj[6]);
                nota.setNomePessoa((String) obj[7]);
                nota.setCpfCnpj((String) obj[8]);
                nota.setDescricaoDestinacao((String) obj[9]);
                nota.setValor((BigDecimal) obj[10]);
                nota.setContaExtraorcamentaria((String) obj[11]);
                nota.setSaldoAnterior((BigDecimal) obj[12]);
                nota.setSaldoAtual((BigDecimal) obj[13]);
                nota.setValorPorExtenso((String) obj[14]);
                nota.setClassePessoa((String) obj[15]);
                nota.setBairro((String) obj[16]);
                nota.setUf((String) obj[17]);
                nota.setCep((String) obj[18]);
                nota.setLogradouro((String) obj[19]);
                nota.setLocalidade((String) obj[20]);
                nota.setDescricaoAgencia((String) obj[21]);
                nota.setDescricaoBanco((String) obj[22]);
                nota.setBancoAgenciaConta((String) obj[23]);
                nota.setContaFinanceira((String) obj[24]);
                nota.setNumeroBanco((String) obj[25]);
                nota.setNumeroAgencia((String) obj[26]);
                nota.setIdUnidadeOrcamentaria((BigDecimal) obj[27]);
                nota.setHasIdRetencaoPagamento(obj[28] != null);
                nota.setId(receitaExtra.getId());
                if (nota.getHasIdRetencaoPagamento()) {
                    nota.setPagamentos(buscarPagamentos(receitaExtra.getId()));
                }
                retorno.add(nota);
            }
        }
        return retorno.get(0);
    }

    private List<NotaExecucaoOrcamentariaPagamento> buscarPagamentos(Long idReceitaExtra) {
        String sql = " select " +
            "    pag.numeropagamento, " +
            "    cast(pag.datapagamento as Date) as data, " +
            "    coalesce(pag.complementohistorico, ' ') as complementohistorico, " +
            "    pag.valor  " +
            " from pagamento pag " +
            "   inner join RetencaoPgto pagrec on pag.id = pagrec.pagamento_id " +
            "   inner join receitaextra rec on pagrec.id = rec.retencaopgto_id " +
            " where rec.id = :idReceitaExtra ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idReceitaExtra", idReceitaExtra);
        List<Object[]> resultado = q.getResultList();
        List<NotaExecucaoOrcamentariaPagamento> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                NotaExecucaoOrcamentariaPagamento pagamento = new NotaExecucaoOrcamentariaPagamento();
                pagamento.setNumero((String) obj[0]);
                pagamento.setDataPag((Date) obj[1]);
                pagamento.setHistorico((String) obj[2]);
                pagamento.setValor((BigDecimal) obj[3]);
                retorno.add(pagamento);
            }
        }
        return retorno;
    }
}
