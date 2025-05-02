/*
 * Codigo gerado automaticamente em Mon Mar 19 14:45:35 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.contabil.apiservicecontabil.SaldoFonteDespesaORCVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.singletons.SingletonGeradorCodigoContabil;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Persistencia;
import br.com.webpublico.util.UtilRH;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class PropostaConcessaoDiariaFacade extends AbstractFacade<PropostaConcessaoDiaria> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private DespesaORCFacade despesaORCFacade;
    @EJB
    private ConfiguracaoDiariaFacade configuracaoDiariaFacade;
    @EJB
    private SolicitacaoEmpenhoFacade solicitacaoEmpenhoFacade;
    @EJB
    private FonteDespesaORCFacade fonteDespesaORCFacade;
    @EJB
    private SaldoFonteDespesaORCFacade saldoFonteDespesaORCFacade;
    @EJB
    private ClasseCredorFacade classeCredorFacade;
    @EJB
    private ContaCorrenteBancPessoaFacade contaCorrenteBancPessoaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private GrupoDiariaFacade grupoDiariaFacade;
    @EJB
    private SingletonGeradorCodigoContabil singletonGeradorCodigoContabil;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private CidadeFacade cidadeFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private TermoColaboradorEventualFacade termoColaboradorEventualFacade;
    @EJB
    private IndicadorEconomicoFacade indicadorEconomicoFacade;
    @EJB
    private ConfiguracaoContabilFacade configuracaoContabilFacade;
    @EJB
    private FeriadoFacade feriadoFacade;

    public PropostaConcessaoDiariaFacade() {
        super(PropostaConcessaoDiaria.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public int getCodigoSequencial(Exercicio exerc, UnidadeOrganizacional unidade, TipoProposta tipoProposta) {
        String sql = "select coalesce(max(to_number(p.codigo))+1,1) from PropostaConcessaoDiaria p ";
        sql += " where p.tipoProposta = :tipo "
            + "and p.exercicio_id =:exerc "
            + "and p.unidadeOrganizacional_id = :unidade "
            + "and p.codigo > 0 ";
        Query q = getEntityManager().createNativeQuery(sql);
        q.setParameter("tipo", tipoProposta.name());
        q.setParameter("exerc", exerc.getId());
        q.setParameter("unidade", unidade.getId());
        BigDecimal toReturn = (BigDecimal) q.getSingleResult();
        return toReturn.intValue();
    }

    public String getCodigoSequencialCampo(Exercicio exerc) {
        String sql = "select max(to_number(p.codigo))+1 from PropostaConcessaoDiaria p ";
        sql += " where p.tipoProposta = :tipo and p.exercicio_id =:exerc ";
        Query q = getEntityManager().createNativeQuery(sql);
        q.setParameter("tipo", TipoProposta.CONCESSAO_DIARIACAMPO.name());
        q.setParameter("exerc", exerc.getId());
        if (q.getSingleResult() == null) {
            return "1";
        }
        return q.getSingleResult().toString();
    }

    public int getCodigoSequencialSuprimentoFundo(Exercicio exerc) {

        String sql = "select coalesce(max(to_number(p.codigo))+1,1) from PropostaConcessaoDiaria p ";
        sql += " where p.tipoProposta = :tipo and p.exercicio_id =:exerc ";
        Query q = getEntityManager().createNativeQuery(sql);
        q.setParameter("tipo", TipoProposta.SUPRIMENTO_FUNDO.toString());
        q.setParameter("exerc", exerc.getId());
        BigDecimal toReturn = (BigDecimal) q.getSingleResult();
        return toReturn.intValue();
    }

    @Override
    public PropostaConcessaoDiaria recarregar(PropostaConcessaoDiaria entity) {
        if (entity == null) {
            return null;
        }
        Object chave = Persistencia.getId(entity);
        if (chave == null) {
            return entity;
        }
        Query q = getEntityManager().createQuery(" From PropostaConcessaoDiaria p left join fetch p.diariaPrestacaoContas where p=:param");
        q.setParameter("param", entity);
        entity = (PropostaConcessaoDiaria) q.getSingleResult();
        return entity;
    }

    @Override
    public PropostaConcessaoDiaria recuperar(Object id) {
        PropostaConcessaoDiaria p = em.find(PropostaConcessaoDiaria.class, id);
        p.getDiariaPrestacaoContas().size();
        p.getArquivosCusteadosTeceiros().size();
        p.getViagens().size();
        p.getArquivos().size();
        p.getDesdobramentos().size();
        return p;
    }

    public List<PropostaConcessaoDiaria> listaPorTipoProposta(TipoProposta tipo) {
        String hql = "from PropostaConcessaoDiaria p where p.tipoProposta = :tipo";
        Query q = em.createQuery(hql);
        q.setParameter("tipo", tipo);
        return q.getResultList();
    }

    public List<PropostaConcessaoDiaria> listaPropostaConcessaoDiariaExercicio(Exercicio exerc) {
        String hql = "from PropostaConcessaoDiaria p where p.tipoProposta != :tipo and p.exercicio=:exerc";
        Query q = em.createQuery(hql);
        q.setParameter("tipo", TipoProposta.SUPRIMENTO_FUNDO);
        q.setParameter("exerc", exerc);

        return q.getResultList();
    }

    public List<PropostaConcessaoDiaria> listaPropostaConcessaoDiariaExercicioUnidadesPagas(Exercicio exerc, List<HierarquiaOrganizacional> listaUnidades) {
        List<UnidadeOrganizacional> unidades = Lists.newArrayList();
        for (HierarquiaOrganizacional lista : listaUnidades) {
            unidades.add(lista.getSubordinada());
        }
        String sql = "select diaria.*  from propostaconcessaodiaria diaria " +
            " inner join empenho e on diaria.id = e.propostaconcessaodiaria_id " +
            " inner join liquidacao l on e.id = l.empenho_id " +
            " inner join pagamento p on l.id = p.liquidacao_id " +
            " inner join exercicio ex on p.EXERCICIO_ID = ex.id " +
            " where p.CATEGORIAORCAMENTARIA = '" + CategoriaOrcamentaria.NORMAL + "' " +
            " and ex.ano = :ano and p.STATUS <> '" + StatusPagamento.ABERTO + "' " +
            " and diaria.tipoProposta <> '" + TipoProposta.SUPRIMENTO_FUNDO + "' " +
            " and p.unidadeOrganizacional_id in (:unidades) " +
            " order by diaria.datalancamento";
        Query q = em.createNativeQuery(sql, PropostaConcessaoDiaria.class);
        q.setParameter("ano", exerc.getAno());
        q.setParameter("unidades", unidades);
        return q.getResultList();
    }


    public List<PropostaConcessaoDiaria> listaFundoPorExercicio(Exercicio exerc) {
        String hql = "from PropostaConcessaoDiaria p where p.tipoProposta = :tipo and p.exercicio=:exerc";
        Query q = em.createQuery(hql);
        q.setParameter("tipo", TipoProposta.SUPRIMENTO_FUNDO);
        q.setParameter("exerc", exerc);
        return q.getResultList();
    }

    public void geraSolicitacaoEmpenho(PropostaConcessaoDiaria pcd, FonteDespesaORC fonte, Boolean geraReserva) throws Exception {
        String solicitacao = getHistoricoDiariaNaSolicitacaoEmpenho(pcd);
        DiariaCivilSolicitacaoEmpenho dcse = new DiariaCivilSolicitacaoEmpenho();
        dcse.setSolicitacaoEmpenho(solicitacaoEmpenhoFacade.geraSolicitacaoEmpenho(pcd.getValor(), pcd.getUnidadeOrganizacional(), null, null,
            fonte, pcd.getDespesaORC(), UtilRH.getDataOperacao(), null, solicitacao, pcd.getPessoaFisica(), geraReserva,
            OrigemReservaFonte.DIARIA_CIVIL, pcd.getClasseCredor(), OrigemSolicitacaoEmpenho.DIARIA, pcd.getCodigo()));
        dcse.setPropostaConcessaoDiaria(pcd);
        pcd.setAprovado(true);
        pcd.setEmpenhado(true);
        em.merge(pcd);
        em.persist(dcse);
    }

    private String getHistoricoDiariaNaSolicitacaoEmpenho(PropostaConcessaoDiaria pcd) {
        return ("Solicitação gerada através da " + pcd.getTipoProposta().getDescricao()
            + " Nº " + pcd.getCodigo() + "/" + pcd.getExercicio() + ", "
            + " para " + pcd.getPessoaFisica() + " - "
            + pcd.getObjetivo()).length() > 3000 ? ("Solicitação gerada através da " + pcd.getTipoProposta().getDescricao()
            + " Nº " + pcd.getCodigo() + "/" + pcd.getExercicio() + ", "
            + " para " + pcd.getPessoaFisica() + " - "
            + pcd.getObjetivo()).substring(0, 3000) :
            ("Solicitação gerada através da " + pcd.getTipoProposta().getDescricao()
                + " Nº " + pcd.getCodigo() + "/" + pcd.getExercicio() + ", "
                + " para " + pcd.getPessoaFisica() + " - "
                + pcd.getObjetivo());
    }


    public void removerReservaFonteDespesaORC(PropostaConcessaoDiaria entity) {
        SaldoFonteDespesaORCVO vo = new SaldoFonteDespesaORCVO(
            entity.getFonteDespesaORC(),
            OperacaoORC.RESERVADO,
            TipoOperacaoORC.ESTORNO,
            entity.getValor(),
            entity.getDataLancamento(),
            entity.getUnidadeOrganizacional(),
            entity.getId().toString(),
            entity.getClass().getSimpleName(),
            entity.getCodigo() + "/" + entity.getExercicio(),
            entity.getObjetivo());
        saldoFonteDespesaORCFacade.gerarSaldoOrcamentario(vo);
    }

    public BigDecimal retornaCotacaoDolarDia(Date dataLancamento) {

        String sql = "SELECT COALESCE(VI.VALOR, 0) as valor"
            + " FROM INDICADOR I"
            + " INNER JOIN VALORINDICADOR VI ON VI.INDICADOR_ID = I.ID"
            + " WHERE trunc(VI.DATAREGISTRO) = "
            + " (SELECT trunc(MAX(VI.DATAREGISTRO)) FROM VALORINDICADOR VI WHERE trunc(VI.DATAREGISTRO) <= TO_DATE(:dataLanc,'DD/MM/YYYY'))";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataLanc", DataUtil.getDataFormatada(dataLancamento));

        if (q.getResultList().size() != 0) {
            return (BigDecimal) q.getSingleResult();
        } else {
            throw new ExcecaoNegocioGenerica("erro");
        }
    }

    public List<PropostaConcessaoDiaria> listaPropostaConcessaoDiariaPorPessoaOuCodigo(Exercicio exerc, String codigo) {
        String hql = " SELECT PROP.* FROM PROPOSTACONCESSAODIARIA PROP "
            + " INNER JOIN PESSOAFISICA PES ON PROP.PESSOAFISICA_ID = PES.ID "
            + " where (PES.NOME LIKE :PARAM or PROP.codigo like :PARAM) AND PROP.EXERCICIO_ID = :EXERCICIO";
        Query q = em.createNativeQuery(hql, PropostaConcessaoDiaria.class);
        q.setParameter("PARAM", "%" + codigo.trim() + "%");
        q.setParameter("EXERCICIO", exerc.getId());
        if (q.getResultList() != null) {
            return (ArrayList<PropostaConcessaoDiaria>) q.getResultList();
        } else {
            return new ArrayList<>();
        }
    }

    public List<PropostaConcessaoDiaria> listaFiltrandoDiariasPorUnidadePessoaTipoESituacao(String parte, UnidadeOrganizacional uo, TipoProposta tp, SituacaoDiaria situacao) {

        String sql = " SELECT PCD.* "
            + " FROM PROPOSTACONCESSAODIARIA PCD "
            + " INNER JOIN PESSOAFISICA PF ON PCD.PESSOAFISICA_ID = PF.ID "
            + " WHERE UNIDADEORGANIZACIONAL_ID = :uo "
            + " AND (LOWER (PF.NOME) LIKE :parte OR LOWER (PF.CPF) LIKE :parte  OR PCD.CODIGO LIKE :parte) "
            + " AND PCD.TIPOPROPOSTA = :tp "
            + " AND PCD.situacao = :situacao "
            + " AND PCD.ID NOT IN (select prop.id from empenho emp inner join propostaconcessaodiaria prop on EMP.PROPOSTACONCESSAODIARIA_ID = prop.id) ";
        Query q = em.createNativeQuery(sql, PropostaConcessaoDiaria.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("uo", uo.getId());
        q.setParameter("tp", tp.name());
        q.setParameter("situacao", situacao.name());
        return q.getResultList();
    }


    public Boolean verificarVinculoDiariaComEmpenho(PropostaConcessaoDiaria diaria) {

        String sql = " select pcd.* " +
            "      from propostaconcessaodiaria pcd " +
            "     inner join empenho emp on emp.propostaconcessaodiaria_id = pcd.id " +
            "     where pcd.unidadeorganizacional_id = :uo " +
            "       and pcd.exercicio_id = :exercicio " +
            "       and pcd.tipoproposta <> :diariacampo " +
            "       and pcd.id = :idDiaria " +
            "       and not exists (select 1 from empenhoestorno est where emp.id = est.empenho_id having sum(est.valor) = emp.valor) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("uo", diaria.getUnidadeOrganizacional().getId());
        q.setParameter("exercicio", diaria.getExercicio().getId());
        q.setParameter("idDiaria", diaria.getId());
        q.setParameter("diariacampo", TipoProposta.CONCESSAO_DIARIACAMPO.name());
        return !q.getResultList().isEmpty();
    }


    public List<PropostaConcessaoDiaria> listaFiltrandoDiariasPorUnidadePessoaETipo(String parte, UnidadeOrganizacional uo, TipoProposta tp) {

        String sql = " SELECT PCD.* "
            + " FROM PROPOSTACONCES-SAODIARIA PCD "
            + " INNER JOIN PESSOAFISICA PF ON PCD.PESSOAFISICA_ID = PF.ID "
            + " WHERE UNIDADEORGANIZACIONAL_ID = :uo "
            + " AND (LOWER (PF.NOME) LIKE :parte OR LOWER (PF.CPF) LIKE :parte  OR PCD.CODIGO LIKE :parte) "
            + " AND PCD.TIPOPROPOSTA = :tp AND PCD.ID NOT IN (select prop.id from empenho emp inner join propostaconcessaodiaria prop on EMP.PROPOSTACONCESSAODIARIA_ID = prop.id) ";
        Query q = em.createNativeQuery(sql, PropostaConcessaoDiaria.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("uo", uo.getId());
        q.setParameter("tp", tp.name());
        return q.getResultList();
    }

    public List<PropostaConcessaoDiaria> buscarDiariasPorUnidadeOrganizacionalETipoProposta(String parte, TipoProposta tipoProposta, UnidadeOrganizacional unidade, Boolean semFiltro) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT distinct prop.* FROM PROPOSTACONCESSAODIARIA prop "
            + " inner join pessoa p on prop.pessoafisica_id = p.id "
            + " inner join pessoafisica pf on p.id = pf.id "
            + " where (PROP.TIPOPROPOSTA = :tipoProposta) "
            + (semFiltro ? "" : " and prop.unidadeorganizacional_id = :uni "));
        sql.append(" and (prop.codigo like :str or pf.nome like :str or pf.cpf like :str or replace(replace(replace(pf.cpf, '.', ''), '-', ''), '/', '') like :str or to_char(prop.datalancamento, 'dd/MM/yyyy') like :str) ");
        sql.append(" ORDER BY PROP.CODIGO, prop.datalancamento desc ");


        Query q = getEntityManager().createNativeQuery(sql.toString(), PropostaConcessaoDiaria.class);
        q.setParameter("tipoProposta", tipoProposta.name());
        q.setParameter("str", "%" + parte.trim().toUpperCase() + "%");
        if (!semFiltro) {
            q.setParameter("uni", unidade.getId());
        }
        return q.getResultList();
    }

    public List<PropostaConcessaoDiaria> recuperarDiariasDaPessoa(Pessoa pessoa) {
        String sql = " select p.* from propostaconcessaodiaria p " +
            "       where p.pessoafisica_id = :pessoa        ";
        Query q = em.createNativeQuery(sql, PropostaConcessaoDiaria.class);
        q.setParameter("pessoa", pessoa.getId());
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        }
        return q.getResultList();
    }

    public List<Empenho> listaEmpenhoPorProposta(PropostaConcessaoDiaria p) {
        String sql = " SELECT EMP.* FROM EMPENHO EMP "
            + " INNER JOIN PROPOSTACONCESSAODIARIA PROP ON EMP.PROPOSTACONCESSAODIARIA_ID = PROP.ID "
            + " WHERE PROP.ID = :param ";
        Query q = getEntityManager().createNativeQuery(sql.toString(), Empenho.class);
        q.setParameter("param", p.getId());
        if (q.getResultList() != null) {
            return q.getResultList();
        } else {
            return new ArrayList<>();
        }
    }

    public List<EmpenhoEstorno> listaEmpenhoEstornoPorProposta(PropostaConcessaoDiaria p) {
        String sql = " SELECT EMPEST.* FROM EMPENHO EMP "
            + " INNER JOIN EMPENHOESTORNO EMPEST ON EMPEST.EMPENHO_ID = EMP.ID "
            + " INNER JOIN PROPOSTACONCESSAODIARIA PROP ON EMP.PROPOSTACONCESSAODIARIA_ID = PROP.ID "
            + " WHERE PROP.ID = :param ";
        Query q = getEntityManager().createNativeQuery(sql.toString(), EmpenhoEstorno.class);
        q.setParameter("param", p.getId());
        if (q.getResultList() != null) {
            return q.getResultList();
        } else {
            return new ArrayList<>();
        }
    }

    public List<Liquidacao> listaLiquidacaoPorProposta(PropostaConcessaoDiaria p) {
        String sql = " SELECT LIQ.* FROM EMPENHO EMP "
            + " INNER JOIN LIQUIDACAO LIQ ON LIQ.EMPENHO_ID = EMP.ID "
            + " INNER JOIN PROPOSTACONCESSAODIARIA PROP ON EMP.PROPOSTACONCESSAODIARIA_ID = PROP.ID "
            + " WHERE PROP.ID = :param ";
        Query q = getEntityManager().createNativeQuery(sql.toString(), Liquidacao.class);
        q.setParameter("param", p.getId());
        if (q.getResultList() != null) {
            return q.getResultList();
        } else {
            return new ArrayList<>();
        }
    }

    public List<LiquidacaoEstorno> listaLiquidacaoEstornoPorProposta(PropostaConcessaoDiaria p) {
        String sql = " SELECT LIQEST.* FROM EMPENHO EMP "
            + " INNER JOIN LIQUIDACAO LIQ ON LIQ.EMPENHO_ID = EMP.ID "
            + " INNER JOIN LIQUIDACAOESTORNO LIQEST ON LIQEST.LIQUIDACAO_ID = LIQ.ID "
            + " INNER JOIN PROPOSTACONCESSAODIARIA PROP ON EMP.PROPOSTACONCESSAODIARIA_ID = PROP.ID "
            + " WHERE PROP.ID = :param ";
        Query q = getEntityManager().createNativeQuery(sql.toString(), LiquidacaoEstorno.class);
        q.setParameter("param", p.getId());
        if (q.getResultList() != null) {
            return q.getResultList();
        } else {
            return new ArrayList<>();
        }
    }

    public List<Pagamento> listaPagamentoPorProposta(PropostaConcessaoDiaria p) {
        String sql = " SELECT PAG.* FROM EMPENHO EMP "
            + " INNER JOIN LIQUIDACAO LIQ ON LIQ.EMPENHO_ID = EMP.ID "
            + " INNER JOIN PAGAMENTO PAG ON PAG.LIQUIDACAO_ID = LIQ.ID "
            + " INNER JOIN PROPOSTACONCESSAODIARIA PROP ON EMP.PROPOSTACONCESSAODIARIA_ID = PROP.ID "
            + " WHERE PROP.ID = :param ";
        Query q = getEntityManager().createNativeQuery(sql.toString(), Pagamento.class);
        q.setParameter("param", p.getId());
        if (q.getResultList() != null) {
            return q.getResultList();
        } else {
            return new ArrayList<>();
        }
    }

    public List<PagamentoEstorno> listaPagamentoEstornoPorProposta(PropostaConcessaoDiaria p) {
        String sql = " SELECT PAGEST.* FROM EMPENHO EMP "
            + " INNER JOIN LIQUIDACAO LIQ ON LIQ.EMPENHO_ID = EMP.ID "
            + " INNER JOIN PAGAMENTO PAG ON PAG.LIQUIDACAO_ID = LIQ.ID "
            + " INNER JOIN PAGAMENTOESTORNO PAGEST ON PAGEST.PAGAMENTO_ID = PAG.ID "
            + " INNER JOIN PROPOSTACONCESSAODIARIA PROP ON EMP.PROPOSTACONCESSAODIARIA_ID = PROP.ID "
            + " WHERE PROP.ID = :param ";
        Query q = getEntityManager().createNativeQuery(sql.toString(), PagamentoEstorno.class);
        q.setParameter("param", p.getId());
        if (q.getResultList() != null) {
            return q.getResultList();
        } else {
            return new ArrayList<>();
        }
    }

    public List<DiariaContabilizacao> listaDiariaContabilizacaoPorPropostaTipoEOperacao(OperacaoDiariaContabilizacao odc, TipoLancamento tl, PropostaConcessaoDiaria p) {
        String sql = " SELECT DC.* FROM PROPOSTACONCESSAODIARIA PROP "
            + " INNER JOIN DIARIACONTABILIZACAO DC ON DC.PROPOSTACONCESSAODIARIA_ID = PROP.ID "
            + " WHERE DC.OPERACAODIARIACONTABILIZACAO = :odc "
            + " AND TIPOLANCAMENTO = :tl "
            + " AND PROP.ID = :param ";
        Query q = getEntityManager().createNativeQuery(sql.toString(), DiariaContabilizacao.class);
        q.setParameter("odc", odc.name());
        q.setParameter("tl", tl.name());
        q.setParameter("param", p.getId());
        if (q.getResultList() != null) {
            return q.getResultList();
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void salvarNovo(PropostaConcessaoDiaria entity) {
        try {
            if (entity.getTipoProposta().equals(TipoProposta.CONCESSAO_DIARIA)) {
                entity.setCodigo(singletonGeradorCodigoContabil.getNumeroDiariaCivil(entity.getExercicio(), entity.getUnidadeOrganizacional(), entity.getDataLancamento()));
            }
            if (entity.getTipoProposta().equals(TipoProposta.CONCESSAO_DIARIACAMPO)) {
                entity.setCodigo(singletonGeradorCodigoContabil.getNumeroDiariaDeCampo(entity.getExercicio(), entity.getUnidadeOrganizacional(), entity.getDataLancamento()));
            }
            if (entity.getTipoProposta().equals(TipoProposta.SUPRIMENTO_FUNDO)) {
                entity.setCodigo(singletonGeradorCodigoContabil.getNumeroSuprimentoDeFundo(entity.getExercicio(), entity.getUnidadeOrganizacional(), entity.getDataLancamento()));
            }
            if (entity.getTipoProposta().equals(TipoProposta.COLABORADOR_EVENTUAL)) {
                entity.setCodigo(singletonGeradorCodigoContabil.getNumeroDiariaColaboradorEventual(entity.getExercicio(), entity.getUnidadeOrganizacional(), entity.getDataLancamento()));
            }

            if (entity.getDeclaracao() != null) {
                Arquivo declaracao = arquivoFacade.retornaArquivoSalvo(entity.getDeclaracao(), entity.getDeclaracao().getInputStream());
                entity.setDeclaracao(declaracao);
            }

            if (entity.getQualificacaoColaborador() != null) {
                Arquivo qualificacao = arquivoFacade.retornaArquivoSalvo(entity.getQualificacaoColaborador(), entity.getQualificacaoColaborador().getInputStream());
                entity.setQualificacaoColaborador(qualificacao);
            }


            if (entity.getId() == null) {
                em.persist(entity);
            } else {
                entity = em.merge(entity);
            }

        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }


    public void indeferirDiaria(PropostaConcessaoDiaria entity) {
        try {
            entity.setSituacao(SituacaoDiaria.INDEFERIDO);
            SolicitacaoEmpenho solicitacaoEmpenho = solicitacaoEmpenhoFacade.getSolicitacaoEmpenhoPorDiaria(entity);
            if (solicitacaoEmpenho != null) {
                if (solicitacaoEmpenho.getGerarReserva()) {
                    removerReservaFonteDespesaORC(entity);
                }
                solicitacaoEmpenhoFacade.removerSolicitacaoEmpenho(solicitacaoEmpenho);
                em.merge(solicitacaoEmpenho);
            }
            meuSalvar(entity);
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    public boolean hasDiariasAComprovarPorPessoa(PropostaConcessaoDiaria propostaConcessaoDiaria, Integer quantidadeDiasCorridos, Integer quantidadeDiasUteis) {
        String sql = " select trunc(viagem.dataFinal) from PropostaConcessaoDiaria prop " +
            " inner join ( select max(trunc(viagem.dataFinalRetorno)) as dataFinal, viagem.PROPOSTACONCESSAODIARIA_ID from VIAGEMDIARIA viagem " +
            "               group by viagem.PROPOSTACONCESSAODIARIA_ID) viagem on viagem.propostaconcessaodiaria_id = prop.id " +
            " where prop.pessoafisica_id = :pessoa " +
            "   and prop.situacaoDiaria = :situacao " +
            "   and prop.situacao <> :indeferido ";

        if (propostaConcessaoDiaria.getId() != null) {
            sql += " and prop.id <> :id ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("situacao", SituacaoPropostaConcessaoDiaria.A_COMPROVAR.name());
        q.setParameter("indeferido", SituacaoDiaria.INDEFERIDO.name());
        q.setParameter("pessoa", propostaConcessaoDiaria.getPessoaFisica().getId());
        if (propostaConcessaoDiaria.getId() != null) {
            q.setParameter("id", propostaConcessaoDiaria.getId());
        }
        q.setMaxResults(1);
        try {
            Date data = (Date) q.getSingleResult();
            data = DataUtil.adicionaDias(data, 1);
            data = DataUtil.adicionaDias(data, quantidadeDiasCorridos);

            for (int diasUteis = 1; diasUteis <= quantidadeDiasUteis; diasUteis++) {
                data = DataUtil.adicionaDias(data, 1);
                data = DataUtil.ajustarDataUtil(data, feriadoFacade);
            }
            return propostaConcessaoDiaria.getDataLancamento().compareTo(data) >= 0;
        } catch (NoResultException nre) {
            return false;
        }
    }

    public boolean hasDiariasAComprovarPorPessoa(PessoaFisica pessoa) {
        String sql = " select 1 from PropostaConcessaoDiaria prop " +
            " where prop.pessoafisica_id = :pessoa " +
            "   and prop.situacaoDiaria = :situacao " +
            "   and prop.situacao <> :indeferido ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("situacao", SituacaoPropostaConcessaoDiaria.A_COMPROVAR.name());
        q.setParameter("indeferido", SituacaoDiaria.INDEFERIDO.name());
        q.setParameter("pessoa", pessoa.getId());
        return !q.getResultList().isEmpty();
    }

    private List<PropostaConcessaoDiaria> buscarDiariasAComprovar(Integer diasParametro) {
        String sql = " select prop.* from PropostaConcessaoDiaria prop " +
            " inner join ( select max(trunc(viagem.dataFinalRetorno)) as dataFinal, viagem.PROPOSTACONCESSAODIARIA_ID from VIAGEMDIARIA viagem " +
            "               group by viagem.PROPOSTACONCESSAODIARIA_ID) viagem on viagem.propostaconcessaodiaria_id = prop.id " +
            " where to_date(:data, 'dd/MM/yyyy') - trunc(viagem.dataFinal) = :parametro " +
            "   and prop.situacaoDiaria = :situacao " +
            " order by DATALANCAMENTO desc ";
        Query q = em.createNativeQuery(sql, PropostaConcessaoDiaria.class);
        q.setParameter("situacao", SituacaoPropostaConcessaoDiaria.A_COMPROVAR.name());
        q.setParameter("parametro", diasParametro);
        q.setParameter("data", DataUtil.getDataFormatada(new Date()));
        return (List<PropostaConcessaoDiaria>) q.getResultList();
    }

    public void notificarParaControladoria(ConfiguracaoContabil configuracaoContabil) {
        List<PropostaConcessaoDiaria> diariasPrimeiraNotificacao = buscarDiariasAComprovar(configuracaoContabil.getDiasPrimeiraNotificacao());
        for (PropostaConcessaoDiaria diaria : diariasPrimeiraNotificacao) {
            for (ConfiguracaoContabilUsuario usuario : configuracaoContabil.getUsuariosGestoresControladoria()) {
                criarNotificacaoControladoriaOrContabilidade(diaria, usuario.getUsuarioSistema(), TipoNotificacao.AVISO_DIARIA_PENDENTE_PRESTACAO_CONTAS_PRIMEIRA_NOTIFICACAO);
            }
        }
        List<PropostaConcessaoDiaria> diariasSegundaNotificacao = buscarDiariasAComprovar(configuracaoContabil.getDiasSegundaNotificacao());
        for (PropostaConcessaoDiaria diaria : diariasSegundaNotificacao) {
            for (ConfiguracaoContabilUsuario usuario : configuracaoContabil.getUsuariosGestores()) {
                criarNotificacaoControladoriaOrContabilidade(diaria, usuario.getUsuarioSistema(), TipoNotificacao.AVISO_DIARIA_PENDENTE_PRESTACAO_CONTAS_SEGUNDA_NOTIFICACAO);
            }
        }
    }

    private void criarNotificacaoControladoriaOrContabilidade(PropostaConcessaoDiaria entity, UsuarioSistema usuarioSistema, TipoNotificacao tipoNotificacao) {
        Notificacao notificacao = new Notificacao();
        notificacao.setDescricao("Diária: " + entity.toString());
        notificacao.setGravidade(Notificacao.Gravidade.ATENCAO);
        notificacao.setTitulo("Diária sem prestação de contas");
        notificacao.setTipoNotificacao(tipoNotificacao);
        notificacao.setUnidadeOrganizacional(entity.getUnidadeOrganizacional());
        notificacao.setUsuarioSistema(usuarioSistema);
        notificacao.setLink("/diaria/ver/" + entity.getId() + "/");
        NotificacaoService.getService().notificar(notificacao);
    }

    public Integer buscarQuantidadeDiariasMaiorQueParametro(PropostaConcessaoDiaria entity) {
        String sql = " select count(prop.id) from PropostaConcessaoDiaria prop " +
            " where prop.TIPOPROPOSTA = :tipoProposta " +
            " and prop.PESSOAFISICA_ID = :pessoaFisica " +
            " and extract(month from prop.DATALANCAMENTO) = extract(month from  to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            " and extract(year from prop.DATALANCAMENTO) = extract(year from to_date(:dataOperacao, 'dd/MM/yyyy')) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("tipoProposta", TipoProposta.CONCESSAO_DIARIACAMPO.name());
        q.setParameter("pessoaFisica", entity.getPessoaFisica().getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(entity.getDataLancamento()));
        return q.getResultList().size();
    }

    public Object[] recuperarIdENumeroPropostaConcessaoDiaria(SolicitacaoEmpenho solicitacaoEmpenho, boolean isSuprimentoFundo) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select pcd.id, pcd.codigo ")
            .append(" from PropostaConcessaoDiaria pcd ")
            .append("  inner join DIARIACIVILSOLICEMP dcse on dcse.propostaconcessaodiaria_id = pcd.id ")
            .append(" where dcse.solicitacaoempenho_id = :solicitacaoEmpenhoId ")
            .append("   and pcd.tipoProposta ").append(isSuprimentoFundo ? " = " : " <> ").append(" :suprimentoFundo ");
        Query q = em.createNativeQuery(sb.toString());
        try {
            q.setParameter("solicitacaoEmpenhoId", solicitacaoEmpenho.getId());
            q.setParameter("suprimentoFundo", TipoProposta.SUPRIMENTO_FUNDO.name());
            return (Object[]) q.getSingleResult();
        } catch (NoResultException | NullPointerException e) {
            return null;
        }
    }

    public BigDecimal getValorEmpenhado(PropostaConcessaoDiaria p) {
        String sql = " select sum(valor) from ( " +
            "    SELECT coalesce(emp.valor, 0) as valor " +
            "    FROM EMPENHO emp " +
            "    WHERE emp.PROPOSTACONCESSAODIARIA_ID = :idPropCD " +
            "    union all " +
            "    SELECT coalesce(estEmp.valor, 0) * -1 as valor " +
            "    FROM EMPENHO emp " +
            "       INNER JOIN EMPENHOESTORNO estEmp on estEmp.empenho_id = emp.id " +
            "    WHERE emp.PROPOSTACONCESSAODIARIA_ID = :idPropCD " +
            "    ) ";
        Query q = getEntityManager().createNativeQuery(sql);
        q.setParameter("idPropCD", p.getId());
       return (BigDecimal) q.getSingleResult();
    }

    public ConfiguracaoDiariaFacade getConfiguracaoDiariaFacade() {
        return configuracaoDiariaFacade;
    }

    public DespesaORCFacade getDespesaORCFacade() {
        return despesaORCFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public SolicitacaoEmpenhoFacade getSolicitacaoEmpenhoFacade() {
        return solicitacaoEmpenhoFacade;
    }

    public FonteDespesaORCFacade getFonteDespesaORCFacade() {
        return fonteDespesaORCFacade;
    }

    public ClasseCredorFacade getClasseCredorFacade() {
        return classeCredorFacade;
    }

    public ContaCorrenteBancPessoaFacade getContaCorrenteBancPessoaFacade() {
        return contaCorrenteBancPessoaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public GrupoDiariaFacade getGrupoDiariaFacade() {
        return grupoDiariaFacade;
    }

    public PropostaConcessaoDiaria meuSalvar(PropostaConcessaoDiaria p) {
        return em.merge(p);
    }

    public SingletonGeradorCodigoContabil getSingletonGeradorCodigoContabil() {
        return singletonGeradorCodigoContabil;
    }

    public AtoLegalFacade getAtoLegalFacade() {
        return atoLegalFacade;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public TermoColaboradorEventualFacade getTermoColaboradorEventualFacade() {
        return termoColaboradorEventualFacade;
    }

    public CidadeFacade getCidadeFacade() {
        return cidadeFacade;
    }

    public IndicadorEconomicoFacade getIndicadorEconomicoFacade() {
        return indicadorEconomicoFacade;
    }

    public ConfiguracaoContabilFacade getConfiguracaoContabilFacade() {
        return configuracaoContabilFacade;
    }
}
