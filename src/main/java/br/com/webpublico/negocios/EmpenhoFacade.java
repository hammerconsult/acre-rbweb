/*
 * Codigo gerado automaticamente em Thu Jun 09 17:00:48 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.portaltransparencia.PortalTransparenciaNovoFacade;
import br.com.webpublico.controle.portaltransparencia.entidades.EmpenhoPortal;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.NotaExecucaoOrcamentaria;
import br.com.webpublico.entidadesauxiliares.contabil.apiservicecontabil.SaldoFonteDespesaORCVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.ReprocessamentoLancamentoContabilSingleton;
import br.com.webpublico.negocios.contabil.reprocessamento.SuperFacadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.ConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.FiltroConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.singleton.SingletonConcorrenciaContabil;
import br.com.webpublico.singletons.SingletonGeradorCodigoContabil;
import br.com.webpublico.util.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.hibernate.Hibernate;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Stateless
public class EmpenhoFacade extends SuperFacadeContabil<Empenho> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SaldoFonteDespesaORCFacade saldoFonteDespesaORCFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private CotaOrcamentariaFacade cotaOrcamentariaFacade;
    @EJB
    private PagamentoFacade pagamentoFacade;
    @EJB
    private LiquidacaoFacade liquidacaoFacade;
    @EJB
    private SolicitacaoEmpenhoFacade solicitacaoEmpenhoFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private HistoricoContabilFacade historicoContabilFacade;
    @EJB
    private DespesaORCFacade despesaORCFacade;
    @EJB
    private FonteDespesaORCFacade fonteDespesaORCFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ConfiguracaoContabilFacade configuracaoContabilFacade;
    @EJB
    private LiquidacaoEstornoFacade liquidacaoEstornoFacade;
    @EJB
    private PagamentoEstornoFacade pagamentoEstornoFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private DividaPublicaFacade dividaPublicaFacade;
    @EJB
    private ConvenioDespesaFacade convenioDespesaFacade;
    @EJB
    private ConvenioReceitaFacade convenioReceitaFacade;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;
    @EJB
    private ClasseCredorFacade classeCredorFacade;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private PropostaConcessaoDiariaFacade propostaConcessaoDiariaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ContratoFacade contratoFacade;
    @EJB
    private ConfigEmpenhoFacade configEmpenhoFacade;
    @EJB
    private ConfigEmpenhoRestoFacade configEmpenhoRestoFacade;
    @EJB
    private ReprocessamentoLancamentoContabilSingleton reprocessamentoLancamentoContabilSingleton;
    @EJB
    private SingletonGeradorCodigoContabil singletonGeradorCodigoContabil;
    @EJB
    private ConfiguracaoTipoContaDespesaClasseCredorFacade configuracaoTipoContaDespesaClasseCredorFacade;
    @EJB
    private SingletonConcorrenciaContabil singletonConcorrenciaContabil;
    @EJB
    private ConfigContaDespTipoPessoaFacade configContaDespTipoPessoaFacade;
    @EJB
    private PortalTransparenciaNovoFacade portalTransparenciaNovoFacade;
    @EJB
    private DotacaoSolicitacaoMaterialFacade dotacaoSolicitacaoMaterialFacade;
    @EJB
    private ObrigacaoAPagarFacade obrigacaoAPagarFacade;
    @EJB
    private DesdobramentoObrigacaoAPagarFacade desdobramentoObrigacaoAPagarFacade;
    @EJB
    private SaldoDividaPublicaFacade saldoDividaPublicaFacade;
    @EJB
    private ExtensaoFonteRecursoFacade extensaoFonteRecursoFacade;
    @EJB
    private ExecucaoContratoFacade execucaoContratoFacade;
    @EJB
    private ConfiguracaoExcecaoDespesaContratoFacade configuracaoExcecaoDespesaContratoFacade;
    @EJB
    private ExecucaoProcessoFacade execucaoProcessoFacade;
    @EJB
    private NotaOrcamentariaFacade notaOrcamentariaFacade;
    @EJB
    private EmendaFacade emendaFacade;
    @EJB
    private CodigoCOFacade codigoCOFacade;
    @EJB
    private ReconhecimentoDividaFacade reconhecimentoDividaFacade;

    public EmpenhoFacade() {
        super(Empenho.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }


    @Override
    public Empenho recuperar(Object id) {
        String hql = "from Empenho e where e.id=:param";
        Query q = em.createQuery(hql);
        q.setParameter("param", id);
        Empenho emp = (Empenho) q.getSingleResult();
        emp.getEmpenhoEstornos().size();
        emp.getLiquidacoes().size();
        emp.getDesdobramentos().size();
        emp.getObrigacoesPagar().size();
        emp.getEmendas().size();
        recuperarContratos(emp);
        return emp;
    }

    public void recuperarContratos(Empenho emp) {
        if (emp.getContrato() != null) {
            emp.getContrato().getExecucoes().size();
        }
    }

    public Empenho recuperarComFind(Long id) {
        Empenho emp = (Empenho) em.find(Empenho.class, id);
        emp.getEmpenhoEstornos().size();
        emp.getLiquidacoes().size();
        emp.getDesdobramentos().size();
        emp.getObrigacoesPagar().size();
        emp.getEmendas().size();
        recuperarContratos(emp);
        return emp;
    }

    public Empenho recuperarDetalhamento(Long id) {
        Empenho emp = em.find(Empenho.class, id);
        emp.getDesdobramentos().size();
        return emp;
    }

    public Empenho recuperarSimples(Long id) {
        Empenho emp = em.find(Empenho.class, id);
        return emp;
    }


    @Override
    public Empenho recarregar(Empenho entity) {
        if (entity == null) {
            return null;
        }
        Object chave = Persistencia.getId(entity);
        if (chave == null) {
            return entity;
        }
        String hql = "from Empenho e where e = :param";
        Query q = em.createQuery(hql);
        q.setParameter("param", entity);
        Empenho emp = (Empenho) q.getSingleResult();
        emp.getEmpenhoEstornos().size();
        emp.getLiquidacoes().size();
        emp.getDesdobramentos().size();
        emp.getObrigacoesPagar().size();
        emp.getEmendas().size();
        return emp;
    }

    public List<Empenho> buscarEmpenhoAnterioresAoExercicio(String parte, Integer ano, UnidadeOrganizacional unidadeOrganizacional) {
        String sql = "SELECT e.* FROM empenho e" + " INNER JOIN exercicio ex ON ex.id = e.exercicio_id AND ex.ano < :ano" + " left join pessoafisica pf ON pf.id = e.fornecedor_id" + " and ((lower(pf.nome) LIKE :parte) OR (pf.cpf LIKE :parte) OR (e.numero LIKE :parte))" + " left join pessoajuridica pj ON pj.id = e.fornecedor_id" + " and ((lower(pj.nomefantasia) LIKE :parte) OR (lower(pj.nomereduzido) LIKE :parte) OR (pj.cnpj LIKE :parte))" + " WHERE e.saldo > 0 AND e.importado = 0 " + " and e.unidadeOrganizacional_id = :uni" + " UNION" + " SELECT DISTINCT e. * FROM empenho e" + " INNER JOIN exercicio ex ON ex.id = e.exercicio_id" + " left join pessoafisica pf ON pf.id = e.fornecedor_id" + " and ((lower(pf.nome) LIKE :parte) OR (pf.cpf LIKE :parte) OR (e.numero LIKE :parte))" + " left join pessoajuridica pj ON pj.id = e.fornecedor_id" + " and ((lower(pj.nomefantasia) LIKE :parte) OR (lower(pj.nomereduzido) LIKE :parte) OR (pj.cnpj LIKE :parte))" + " AND ex.ano< :ano" + " RIGHT JOIN liquidacao l ON l.empenho_id = e.id" + " AND l.saldo > 0" + " WHERE e.id IS NOT NULL AND e.importado = 0" + " and e.unidadeOrganizacional_id = :uni" + " UNION" + " SELECT DISTINCT e. * FROM empenho e" + " INNER JOIN exercicio ex ON ex.id = e.exercicio_id" + " left join pessoafisica pf ON pf.id = e.fornecedor_id" + " and ((lower(pf.nome) LIKE :parte) OR (pf.cpf LIKE :parte) OR (e.numero LIKE :parte))" + " left join pessoajuridica pj ON pj.id = e.fornecedor_id" + " and ((lower(pj.nomefantasia) LIKE :parte) OR (lower(pj.nomereduzido) LIKE :parte) OR (pj.cnpj LIKE :parte))" + " AND ex.ano< :ano" + " RIGHT JOIN liquidacao l ON l.empenho_id = e.id" + " RIGHT JOIN pagamento p ON p.liquidacao_id = l.id" + " AND p.status != 'PAGO'" + " AND p.saldo > 0" + " WHERE e.id IS NOT NULL AND e.importado = 0" + " and e.unidadeOrganizacional_id = :uni";

        Query q = em.createNativeQuery(sql, Empenho.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("ano", ano.toString());
        q.setParameter("uni", unidadeOrganizacional.getId());
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<Empenho> buscarEmpenhoPorUnidadesAndPessoaAndDataInicialEFinal(String parte, List<HierarquiaOrganizacional> listaUnidades, Pessoa p, Date dataInicial, Date dataFinal) {
        String sql = " select emp.* from empenho emp" + " INNER JOIN PESSOA P ON EMP.FORNECEDOR_ID = P.ID  " + " LEFT JOIN PESSOAFISICA PF ON P.ID = PF.ID  " + " LEFT JOIN PESSOAJURIDICA PJ ON P.ID = PJ.ID  " + " where p.id = :pessoa and ((LOWER(PF.NOME) LIKE :parte) OR (PF.CPF LIKE :parte) OR (EMP.NUMERO LIKE :parte) " + " OR (LOWER(PJ.NOMEFANTASIA) LIKE :parte) OR (LOWER(PJ.NOMEREDUZIDO) LIKE :parte) OR (PJ.CNPJ LIKE :parte) " + " OR (EMP.NUMERO LIKE :parte )) " + " and emp.unidadeorganizacional_id in ( :unidades )" + " and trunc(emp.dataempenho) between to_date(:DATAINICIAL, 'dd/mm/yyyy') and to_date(:DATAFINAL, 'dd/mm/yyyy') ";
        Query q = em.createNativeQuery(sql, Empenho.class);
        q.setParameter("parte", "%" + parte.trim() + "%");
        q.setParameter("pessoa", p.getId());
        q.setParameter("DATAINICIAL", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("DATAFINAL", DataUtil.getDataFormatada(dataFinal));
        StringBuilder idUnidades = new StringBuilder();
        String concat = "";
        for (HierarquiaOrganizacional lista : listaUnidades) {
            idUnidades.append(concat).append(lista.getSubordinada().getId());
            concat = ",";
        }
        q.setParameter("unidades", idUnidades.toString());
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            return q.getResultList();
        }
    }

    public List<Empenho> buscarEmpenhoPorUnidadeGestoraEPessoaEExercicioEDataInicialEFinal(String parte, UnidadeGestora unidadeGestora, Exercicio exercicio, Pessoa p, Date dataInicial, Date dataFinal) {
        String sql = " select emp.* from empenho emp" + " INNER JOIN PESSOA P ON EMP.FORNECEDOR_ID = P.ID  " + " LEFT JOIN PESSOAFISICA PF ON P.ID = PF.ID  " + " LEFT JOIN PESSOAJURIDICA PJ ON P.ID = PJ.ID  " + " inner join vwhierarquiaorcamentaria vw on emp.unidadeorganizacional_id = vw.subordinada_id " + " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id " + " inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id " + " where p.id = :pessoa and ((LOWER(PF.NOME) LIKE :parte) OR (PF.CPF LIKE :parte) OR (EMP.NUMERO LIKE :parte) " + " OR (LOWER(PJ.NOMEFANTASIA) LIKE :parte) OR (LOWER(PJ.NOMEREDUZIDO) LIKE :parte) OR (PJ.CNPJ LIKE :parte) " + " OR (EMP.NUMERO LIKE :parte )) " + " and ug.exercicio_id = :exercicio " + " and ug.id = :ugId " + " and trunc(emp.dataempenho) between to_date(:DATAINICIAL, 'dd/mm/yyyy') and to_date(:DATAFINAL, 'dd/mm/yyyy') " + " and trunc(emp.dataempenho) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(emp.dataempenho)) ";
        Query q = em.createNativeQuery(sql, Empenho.class);
        q.setParameter("parte", "%" + parte.trim() + "%");
        q.setParameter("pessoa", p.getId());
        q.setParameter("DATAINICIAL", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("DATAFINAL", DataUtil.getDataFormatada(dataFinal));
        q.setParameter("ugId", unidadeGestora.getId());
        q.setParameter("exercicio", exercicio.getId());
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            return q.getResultList();
        }
    }

    public List<Empenho> buscarEmpenhoPorUnidades(String parte, List<HierarquiaOrganizacional> listaUnidades) {
        String sql = " select emp.* from empenho emp" + " INNER JOIN PESSOA P ON EMP.FORNECEDOR_ID = P.ID  " + " LEFT JOIN PESSOAFISICA PF ON P.ID = PF.ID  " + " LEFT JOIN PESSOAJURIDICA PJ ON P.ID = PJ.ID  " + " where ((LOWER(PF.NOME) LIKE :parte) OR (PF.CPF LIKE :parte) OR (EMP.NUMERO LIKE :parte) " + " OR (LOWER(PJ.NOMEFANTASIA) LIKE :parte) OR (LOWER(PJ.NOMEREDUZIDO) LIKE :parte) OR (PJ.CNPJ LIKE :parte) " + " OR (EMP.NUMERO LIKE :parte )) " + " and emp.unidadeorganizacional_id in ( :unidades )";
        Query q = em.createNativeQuery(sql, Empenho.class);
        q.setParameter("parte", "%" + parte.trim() + "%");
        StringBuilder idUnidades = new StringBuilder();
        String concat = "";
        for (HierarquiaOrganizacional lista : listaUnidades) {
            idUnidades.append(concat).append(lista.getSubordinada().getId());
            concat = ",";
        }
        q.setParameter("unidades", idUnidades.toString());
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            return q.getResultList();
        }
    }

    public List<Empenho> listaEmpenhoPorUnidadesExercicio(Exercicio ano, List<HierarquiaOrganizacional> listaUnidades, CategoriaOrcamentaria categoria) {
        List<UnidadeOrganizacional> unidades = Lists.newArrayList();
        for (HierarquiaOrganizacional lista : listaUnidades) {
            unidades.add(lista.getSubordinada());
        }

        String hql = " select emp from Empenho emp " + " where emp.unidadeOrganizacional in (:unidades)" + " and emp.exercicio.id = :ano" + " and emp.categoriaOrcamentaria = :categoria";
        Query consulta = em.createQuery(hql, Empenho.class);
        consulta.setParameter("unidades", unidades);
        consulta.setParameter("ano", ano.getId());
        consulta.setParameter("categoria", categoria);
        List resultList = consulta.getResultList();
        if (resultList.isEmpty()) {
            return new ArrayList<>();
        } else {
            return resultList;
        }
    }

    public List<Empenho> buscarEmpenhoRestoPagarPorExercicio(String parte, Exercicio exercicio, UnidadeOrganizacional unidadeOrganizacional) {
        String sql = "SELECT e.* FROM empenho e " + "INNER JOIN exercicio ex ON ex.id = e.exercicio_id AND ex.ano = :exercicio " + "left join pessoafisica pf ON pf.id = e.fornecedor_id " + "and ((lower(pf.nome) LIKE :parte) OR (pf.cpf LIKE :parte) OR (e.numero LIKE :parte)) " + "left join pessoajuridica pj ON pj.id = e.fornecedor_id " + "and ((lower(pj.nomefantasia) LIKE :parte) OR (lower(pj.nomereduzido) LIKE :parte) OR (pj.cnpj LIKE :parte)) " + "WHERE e.saldo > 0 AND e.importado = 0 " + "and e.categoriaorcamentaria = '" + CategoriaOrcamentaria.RESTO + "' " + "and e.unidadeOrganizacional_id = :uni " + "UNION " + "SELECT DISTINCT e.* FROM empenho e " + "INNER JOIN exercicio ex ON ex.id = e.exercicio_id AND ex.ano = :exercicio " + "left join pessoafisica pf ON pf.id = e.fornecedor_id " + "and ((lower(pf.nome) LIKE :parte) OR (pf.cpf LIKE :parte) OR (e.numero LIKE :parte)) " + "left join pessoajuridica pj ON pj.id = e.fornecedor_id " + "and ((lower(pj.nomefantasia) LIKE :parte) OR (lower(pj.nomereduzido) LIKE :parte) OR (pj.cnpj LIKE :parte)) " + "inner JOIN liquidacao l ON l.empenho_id = e.id " + "AND l.saldo > 0 " + "WHERE e.id IS NOT NULL AND e.importado = 0 " + "and e.categoriaorcamentaria = '" + CategoriaOrcamentaria.RESTO + "' " + "and e.unidadeOrganizacional_id = :uni " + "UNION " + "SELECT DISTINCT e.* FROM empenho e " + "INNER JOIN exercicio ex ON ex.id = e.exercicio_id AND ex.ano = :exercicio " + "left join pessoafisica pf ON pf.id = e.fornecedor_id " + "and ((lower(pf.nome) LIKE :parte) OR (pf.cpf LIKE :parte) OR (e.numero LIKE :parte)) " + "left join pessoajuridica pj ON pj.id = e.fornecedor_id " + "and ((lower(pj.nomefantasia) LIKE :parte) OR (lower(pj.nomereduzido) LIKE :parte) OR (pj.cnpj LIKE :parte)) " + "inner JOIN liquidacao l ON l.empenho_id = e.id " + "inner JOIN pagamento p ON p.liquidacao_id = l.id " + "AND p.status !=  '" + StatusPagamento.PAGO + "' " + "AND p.saldo > 0 " + "WHERE e.id IS NOT NULL AND e.importado = 0 " + "and e.categoriaorcamentaria = '" + CategoriaOrcamentaria.RESTO + "' " + "and e.unidadeOrganizacional_id = :uni ";

        Query q = em.createNativeQuery(sql, Empenho.class);
        q.setParameter("exercicio", exercicio.getAno());
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("uni", unidadeOrganizacional.getId());
        q.setMaxResults(10);
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            return q.getResultList();
        }
    }

    public List<Empenho> buscarPorNumeroEPessoa(String parte, Integer ano) {
        String sql = "SELECT e.* FROM empenho e " + "INNER JOIN pessoafisica pf ON pf.id = e.fornecedor_id " + "WHERE e.saldo > 0 AND extract(year from e.dataEmpenho) = :ano " + "AND e.categoriaorcamentaria = 'NORMAL' " + " and ((lower(pf.nome) LIKE :parte) OR (pf.cpf LIKE :parte) OR (e.numero LIKE :parte))" + "UNION ALL " + "SELECT e.* FROM empenho e " + "INNER JOIN pessoajuridica pj ON pj.id = e.fornecedor_id " + "WHERE e.saldo > 0 AND extract(year from e.dataEmpenho) = :ano " + " AND e.categoriaorcamentaria = 'NORMAL'" + " AND ((lower(pj.nomefantasia) LIKE :parte) " + " OR (lower(pj.nomereduzido) LIKE :parte) OR (pj.cnpj LIKE :parte) OR (e.numero LIKE :parte)) ";
        Query q = em.createNativeQuery(sql, Empenho.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("ano", ano.toString());
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<Empenho> buscarEmpenhoPorNumeroEPessoaCategoriaNormal(String parte, Integer ano, UnidadeOrganizacional uo) {
        String sql = "" + "  SELECT * FROM ( " + " SELECT e.* FROM empenho e " + "  INNER JOIN pessoa p ON p.id = e.fornecedor_id " + "  inner join pessoafisica pf on pf.id = p.id " + "  WHERE (e.saldo > 0 or e.saldoobrigacaopagarantesemp > 0 )" + "  AND extract(year from e.dataEmpenho) = :ano " + "  AND e.categoriaorcamentaria = 'NORMAL' " + "  AND E.UNIDADEORGANIZACIONAL_ID = :unidade " + "  AND ((lower(pf.nome) LIKE :parte) OR (pf.cpf LIKE :parte) OR (e.numero LIKE :parte))" + " UNION ALL " + " SELECT e.* FROM empenho e " + "  INNER JOIN pessoa p ON p.id = e.fornecedor_id " + "  INNER JOIN pessoajuridica pj ON pj.id = p.id " + "  WHERE (e.saldo > 0 or e.saldoobrigacaopagarantesemp > 0 )" + "  AND extract(year from e.dataEmpenho) = :ano " + "  AND e.categoriaorcamentaria = 'NORMAL'" + "  AND E.UNIDADEORGANIZACIONAL_ID = :unidade " + "  AND ((lower(pj.nomefantasia) LIKE :parte) " + "  OR (lower(pj.nomereduzido) LIKE :parte) " + "  OR (pj.cnpj LIKE :parte) " + "  OR (e.numero LIKE :parte)) " + ") X " + " ORDER BY X.NUMERO DESC ";
        Query q = em.createNativeQuery(sql, Empenho.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("ano", ano.toString());
        q.setParameter("unidade", uo.getId());
        q.setMaxResults(30);
        return q.getResultList();
    }

    public List<Empenho> buscarEmpenhoPorNumeroPessoaAndCategoriaResto(String parte, Integer ano, UnidadeOrganizacional uo) {
        return buscarEmpenhoPorNumeroPessoaAndCategoriaResto(parte, ano, uo, 30);
    }

    public List<Empenho> buscarEmpenhoPorNumeroPessoaAndCategoriaResto(String parte, Integer ano, UnidadeOrganizacional uo, Integer maximoDeResultados) {
        String sql = " SELECT * FROM ( "
            + "SELECT e.* FROM empenho e "
            + " INNER JOIN pessoafisica pf ON pf.id = e.fornecedor_id "
            + "WHERE e.saldo > 0 AND extract(year from e.dataEmpenho) = :ano "
            + " AND e.categoriaorcamentaria = 'RESTO' "
            + " AND E.UNIDADEORGANIZACIONAL_ID = :unidade "
            + " AND ((lower(pf.nome) LIKE :parte) OR (pf.cpf LIKE :parte) OR (e.numero LIKE :parte or e.numerooriginal like :parte))"
            + "UNION ALL "
            + " SELECT e.* FROM empenho e "
            + "  INNER JOIN pessoajuridica pj ON pj.id = e.fornecedor_id "
            + " WHERE e.saldo > 0 AND extract(year from e.dataEmpenho) = :ano "
            + "  AND e.categoriaorcamentaria = 'RESTO'"
            + "  AND E.UNIDADEORGANIZACIONAL_ID = :unidade "
            + "  AND ((lower(pj.nomefantasia) LIKE :parte) "
            + "  OR (lower(pj.nomereduzido) LIKE :parte) "
            + "  OR (pj.cnpj LIKE :parte) "
            + "  OR (e.numero LIKE :parte  or e.numerooriginal like :parte)) "
            + ") X "
            + "ORDER BY X.NUMERO DESC ";
        Query q = em.createNativeQuery(sql, Empenho.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("ano", ano.toString());
        q.setParameter("unidade", uo.getId());
        if (maximoDeResultados != null) {
            q.setMaxResults(maximoDeResultados);
        }
        return q.getResultList();
    }

    public List<Empenho> buscarPorNumeroAndPessoaRelatorio(String parte, Integer ano) {
        return buscarPorNumeroAndPessoaRelatorioPorCategoria(parte, ano, Lists.<CategoriaOrcamentaria>newArrayList(CategoriaOrcamentaria.NORMAL));
    }

    public List<Empenho> buscarPorNumeroAndPessoaRelatorioPorCategoria(String parte, Integer ano, List<CategoriaOrcamentaria> categorias) {
        String sql = "SELECT e.* FROM empenho e " + " inner join pessoa p on e.fornecedor_id = p.id " + " left join  pessoafisica pf ON pf.id = p.id " + " left join pessoajuridica pj on pj.id = p.id " + " where ((lower(pf.nome) LIKE :parte) " + " OR (pf.cpf LIKE :parte) " + " or (lower(pj.nomefantasia) LIKE :parte) " + " OR (lower(pj.nomereduzido) LIKE :parte) " + " OR (pj.cnpj LIKE :parte) " + " OR (e.numero LIKE :parte)) " + " AND e.categoriaorcamentaria in :categorias";
        if (ano != null) {
            sql += " and extract(year from e.dataEmpenho) = :ano ";
        }
        Query q = em.createNativeQuery(sql, Empenho.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("categorias", getCategoriasOrcamentaria(categorias));
        if (ano != null) {
            q.setParameter("ano", ano.toString());
        }
        q.setMaxResults(10);
        return q.getResultList();
    }

    private List<String> getCategoriasOrcamentaria(List<CategoriaOrcamentaria> categorias) {
        List<String> categoria = Lists.newArrayList();
        for (CategoriaOrcamentaria cat : categorias) {
            categoria.add(cat.name());
        }
        return categoria;
    }

    public List<Empenho> buscarEmpenhosPorUnidadeEUsuarioVinculados(String parte, UsuarioSistema usu, Exercicio exercicio, UnidadeOrganizacional unidadeOrganizacional) {
        String sql = "SELECT DISTINCT EMP.* " + "  FROM USUARIOSISTEMA USU " + " INNER JOIN USUARIOUNIDADEORGANIZACORC USUUND ON USUUND.USUARIOSISTEMA_ID = USU.ID " + " INNER JOIN UNIDADEORGANIZACIONAL UO_ORC ON USUUND.UNIDADEORGANIZACIONAL_ID = UO_ORC.ID " + " INNER JOIN EMPENHO EMP ON UO_ORC.ID = EMP.UNIDADEORGANIZACIONAL_ID " + " INNER JOIN PESSOA P ON EMP.FORNECEDOR_ID = P.ID " + "  LEFT JOIN PESSOAFISICA PF ON P.ID = PF.ID " + "  LEFT JOIN PESSOAJURIDICA PJ ON P.ID = PJ.ID " + " WHERE EMP.SALDO > 0  " + "   AND EMP.EXERCICIO_ID = :EXERCICIO" + "   AND USU.ID = :USUARIO " + "   AND EMP.UNIDADEORGANIZACIONAL_ID = :UNIDADE " + "   AND EMP.CATEGORIAORCAMENTARIA = 'NORMAL' " + "   AND ((LOWER(PF.NOME) LIKE :parte) OR (PF.CPF LIKE :parte) OR (EMP.NUMERO LIKE :parte) " + "       OR (LOWER(PJ.NOMEFANTASIA) LIKE :parte) OR (LOWER(PJ.NOMEREDUZIDO) LIKE :parte) OR (PJ.CNPJ LIKE :parte) " + "       OR (EMP.NUMERO LIKE :parte ))" + " ORDER BY EMP.DATAEMPENHO desc, emp.numero DESC";
        Query q = em.createNativeQuery(sql, Empenho.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("USUARIO", usu.getId());
        q.setParameter("EXERCICIO", exercicio.getId());
        q.setParameter("UNIDADE", unidadeOrganizacional.getId());
        q.setMaxResults(30);

        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            return q.getResultList();
        }
    }

    public List<Empenho> buscarEmpenhoObrigacaoPagar(String parte, UsuarioSistema usu, Exercicio exercicio, UnidadeOrganizacional unidadeOrganizacional) {
        String sql = " " + " select distinct emp.* " + "   from usuariosistema usu " + "   inner join usuariounidadeorganizacorc usuund on usuund.usuariosistema_id = usu.id " + "   inner join unidadeorganizacional unid on usuund.unidadeorganizacional_id = unid.id  " + "   inner join empenho emp on unid.id = emp.unidadeorganizacional_id " + "   inner join pessoa p on emp.fornecedor_id = p.id " + "   left join pessoafisica pf on p.id = pf.id " + "   left join pessoajuridica pj on p.id = pj.id " + " where emp.saldo > 0 " + "  and (select e.valor - e.saldoobrigacaopagardepoisemp from empenho e where e.id = emp.id) > 0 " + "  and emp.exercicio_id = :idExercicio " + "  and usu.id = :idUsuario " + "  and emp.unidadeorganizacional_id = :idUnidade " + "  and (emp.tipocontadespesa = :variacaoPatrimonial or emp.tipocontadespesa = :servicoTerceiro or emp.tipocontadespesa = :pessoalEncargos)" + "  and ((lower(pf.nome) like :parte) or (pf.cpf like :parte) or (emp.numero like :parte) " + "     or (lower(pj.nomefantasia) like :parte) or (lower(pj.nomereduzido) like :parte) or (pj.cnpj like :parte) " + "     or (emp.numero like :parte )) " + " order by emp.numero desc ";
        Query q = em.createNativeQuery(sql, Empenho.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("idUsuario", usu.getId());
        q.setParameter("idExercicio", exercicio.getId());
        q.setParameter("idUnidade", unidadeOrganizacional.getId());
        q.setParameter("variacaoPatrimonial", TipoContaDespesa.VARIACAO_PATRIMONIAL_DIMINUTIVA.name());
        q.setParameter("servicoTerceiro", TipoContaDespesa.SERVICO_DE_TERCEIRO.name());
        q.setParameter("pessoalEncargos", TipoContaDespesa.PESSOAL_ENCARGOS.name());
        q.setMaxResults(30);
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            return q.getResultList();
        }
    }

    public List<Empenho> buscarEmpenhoPorContratoEUnidadeUsuarioVinculados(String parte, Contrato contrato, UsuarioSistema usu, Date data, Exercicio exercicio, UnidadeOrganizacional unidadeOrganizacional) {
        String sql = "SELECT DISTINCT EMP.* " + "  FROM USUARIOSISTEMA USU " + " INNER JOIN USUARIOUNIDADEORGANIZACIO USUUND ON USUUND.USUARIOSISTEMA_ID = USU.ID " + " INNER JOIN UNIDADEORGANIZACIONAL UO_ADM ON USUUND.UNIDADEORGANIZACIONAL_ID = UO_ADM.ID " + " INNER JOIN HIERARQUIAORGANIZACIONAL HO_ADM ON HO_ADM.SUBORDINADA_ID = UO_ADM.ID " + "       AND HO_ADM.TIPOHIERARQUIAORGANIZACIONAL = 'ADMINISTRATIVA' " + "       AND (TO_DATE(:DATA, 'dd/MM/yyyy') BETWEEN trunc(HO_ADM.INICIOVIGENCIA) AND COALESCE(trunc(HO_ADM.FIMVIGENCIA),TO_DATE(:DATA, 'dd/MM/yyyy'))) " + " INNER JOIN HIERARQUIAORGANIZACIONAL HO_ORC ON HO_ORC.ID = HO_ADM.HIERARQUIAORC_ID " + "       AND HO_ORC.TIPOHIERARQUIAORGANIZACIONAL = 'ORCAMENTARIA' " + "       AND (TO_DATE(:DATA, 'dd/MM/yyyy') BETWEEN trunc(HO_ORC.INICIOVIGENCIA) AND COALESCE(trunc(HO_ORC.FIMVIGENCIA),TO_DATE(:DATA, 'dd/MM/yyyy'))) " + " INNER JOIN UNIDADEORGANIZACIONAL UO_ORC ON HO_ORC.SUBORDINADA_ID= UO_ORC.ID " + " INNER JOIN EMPENHO EMP ON UO_ORC.ID = EMP.UNIDADEORGANIZACIONAL_ID " + "       AND EMP.EXERCICIO_ID = :EXERCICIO" + "       AND USU.ID = :USUARIO " + " INNER JOIN PESSOA P ON EMP.FORNECEDOR_ID = P.ID " + " LEFT JOIN PESSOAFISICA PF ON P.ID = PF.ID " + " LEFT JOIN PESSOAJURIDICA PJ ON P.ID = PJ.ID " + " WHERE EMP.SALDO > 0  " + " AND EMP.CONTRATO_ID = :CONTRATO" + " AND EMP.UNIDADEORGANIZACIONAL_ID = :UNIDADE " + " AND EMP.CATEGORIAORCAMENTARIA = 'NORMAL' " + " AND ((LOWER(PF.NOME) LIKE :parte) OR (PF.CPF LIKE :parte) OR (EMP.NUMERO LIKE :parte) " + "       OR (LOWER(PJ.NOMEFANTASIA) LIKE :parte) OR (LOWER(PJ.NOMEREDUZIDO) LIKE :parte) OR (PJ.CNPJ LIKE :parte) " + "       OR (EMP.NUMERO LIKE :parte ))" + " AND EMP.TIPOCONTADESPESA IN('BEM_MOVEL','BEM_ESTOQUE')" + " ORDER BY EMP.DATAEMPENHO DESC";
        Query q = em.createNativeQuery(sql, Empenho.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("CONTRATO", contrato.getId());
        q.setParameter("USUARIO", usu.getId());
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        q.setParameter("DATA", formato.format(data));
        q.setParameter("EXERCICIO", exercicio.getId());
        q.setParameter("UNIDADE", unidadeOrganizacional.getId());
        q.setMaxResults(50);

        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            return q.getResultList();
        }
    }

    public String retornaUltimoNumeroEmpenho(UnidadeOrganizacional uni) {
        String hql = "from Empenho e " + "where e.categoriaOrcamentaria = :categoria " + "and e.unidadeOrganizacional = :uni " + "order by to_number(e.numero) desc";
        Query q = em.createQuery(hql);
        q.setParameter("uni", uni);
        q.setParameter("categoria", CategoriaOrcamentaria.NORMAL);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return ((Empenho) q.getSingleResult()).getNumero();
        } else {
            return "0";
        }
    }

    private ItemParametroEvento criaItemParametroEventoSemParametroEvento(Empenho empenho) {
        //Criando os items dos paramentros, para depois ser adicionado na lista do parametroevento
        ItemParametroEvento item = new ItemParametroEvento();
        item.setTagValor(TagValor.LANCAMENTO);
        item.setValor(empenho.getValor());
        //Criando os objetos para depois ser adicionado na lista de cada item.
        List<ObjetoParametro> listaObj = new ArrayList<>();
        listaObj.add(new ObjetoParametro(empenho.getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa(), item));
        listaObj.add(new ObjetoParametro(empenho.getClasseCredor(), item));
        item.setObjetoParametros(listaObj);
        return item;
    }

    private ParametroEvento buscaParametroEventoEmpenho(Empenho empenho) throws ExcecaoNegocioGenerica {

        EventoContabil eventoContabilDeEmpenhos = contabilizacaoFacade.recuperaEvento("EMPENHO");

        ParametroEvento parametroEvento = new ParametroEvento();
        parametroEvento.setComplementoHistorico(empenho.getComplementoContabil());
        parametroEvento.setDataEvento(empenho.getDataEmpenho());
        parametroEvento.setUnidadeOrganizacional(empenho.getUnidadeOrganizacional());
        parametroEvento.setEventoContabil(eventoContabilDeEmpenhos);
        parametroEvento.setExercicio(empenho.getExercicio());
        //finalizando a criação dos items dos paramentros, para depois ser adicionado na lista do parametroevento
        ItemParametroEvento item = criaItemParametroEventoSemParametroEvento(empenho);
        item.setParametroEvento(parametroEvento);
        parametroEvento.getItensParametrosEvento().add(item);
        return parametroEvento;
    }

    public Boolean validaCampos(Empenho empenho) throws ExcecaoNegocioGenerica {
        Boolean retorno = true;
        EntidadeMetaData metadata = new EntidadeMetaData(Empenho.class);
        String camposObrigatorios = "";
        for (Iterator<AtributoMetadata> it = metadata.getAtributos().iterator(); it.hasNext(); ) {
            AtributoMetadata object = it.next();
            if (object.getAtributo().isAnnotationPresent(br.com.webpublico.util.anotacoes.Obrigatorio.class)) {
                if (object.getString(empenho).equals("") || ((AtributoMetadata) object).getString(empenho) == null) {
                    camposObrigatorios += camposObrigatorios + " " + object.getEtiqueta();
                    retorno = false;
                }
            }
        }

        if (!retorno) {
            throw new ExcecaoNegocioGenerica("Não informados os campos " + camposObrigatorios + " que são obrigatórios!");
        }

        return retorno;
    }

    private Boolean validaSaldoFonteDespesa(Empenho empenho) throws ExcecaoNegocioGenerica {
        Boolean retorno = true;
        BigDecimal saldo = getFonteDespesaORCFacade().saldoRealPorFonte(empenho.getFonteDespesaORC(), empenho.getDataEmpenho(), empenho.getUnidadeOrganizacional());
        if (saldo == null) {
            saldo = BigDecimal.ZERO;
        }
        BigDecimal valor = empenho.getValor();
        if (empenho.getFonteDespesaORC() != null) {
            if (valor.compareTo(saldo) > 0) {
                throw new ExcecaoNegocioGenerica("Saldo da Fonte Indisponivel: " + empenho.getFonteDespesaORC());
            }
        }
        return retorno;
    }

    public Boolean validarEmpenho(Empenho emp) {
        try {
            if (!validaSaldoFonteDespesa(emp) && validaCampos(emp)) {
                verificarSaldoGrupoOrcamentario(emp.getDataEmpenho(), emp.getValor(), emp.getFonteDespesaORC());
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }

    @Override
    public void salvar(Empenho entity) {
        entity.gerarHistoricos();
        mergiarEmpenho(entity);
        atribuirSolicitacaoEmpenhoNoEmpenho(entity);
        salvarPortal(entity);
    }

    public void mergiarEmpenho(Empenho entity) {
        em.merge(entity);
    }

    private void validaSaldoPosterior(Empenho selecionado) {
        if (selecionado.getSolicitacaoEmpenho() != null) {
            if (!selecionado.getSolicitacaoEmpenho().getGerarReserva()) {
                BigDecimal valor = getSaldoFonteDespesaORC(selecionado).subtract(selecionado.getValor());

                if (valor.compareTo(BigDecimal.ZERO) < 0) {
                    throw new ExcecaoNegocioGenerica("O Saldo Atual deve ser maior que 0 (ZERO). Saldo Atual: " + Util.formataValor(valor));
                }
            }
        }
    }

    public BigDecimal adicionarValorReservadoNoSaldoAnterior(Empenho selecionado) {
        if (selecionado.getSolicitacaoEmpenho() != null) {
            if (selecionado.getSolicitacaoEmpenho().getGerarReserva() || selecionado.getContrato() != null) {
                return selecionado.getValor();
            }
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getSaldoFonteDespesaORC(Empenho selecionado) {
        try {
            if (selecionado.getFonteDespesaORC() != null) {
                return saldoFonteDespesaORCFacade.saldoRealPorFonte(selecionado.getFonteDespesaORC(), selecionado.getDataEmpenho(), selecionado.getUnidadeOrganizacional()).add(adicionarValorReservadoNoSaldoAnterior(selecionado));
            }
            return BigDecimal.ZERO;
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(" Erro ao recuperar o saldo " + ex.getMessage());
        }
    }

    public void geraNumeroEmpenho(Empenho entity) {
        entity.setNumero(singletonGeradorCodigoContabil.getNumeroEmpenho(entity.getExercicio(), entity.getUnidadeOrganizacional(), entity.getDataEmpenho()));
    }

    private void definirValoresNoEmpenho(Empenho selecionado) {
        selecionado.setSaldoDisponivel(getSaldoFonteDespesaORC(selecionado));
    }

    public Empenho salvarEmpenho(Empenho entity) throws ExcecaoNegocioGenerica {
        if (getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
            throw new ExcecaoNegocioGenerica(getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
        } else {
            hierarquiaOrganizacionalFacade.validaVigenciaHIerarquiaAdministrativaOrcamentaria(entity.getUnidadeOrganizacionalAdm(), entity.getUnidadeOrganizacional(), entity.getDataEmpenho());
            definirValoresNoEmpenho(entity);
            validaSaldoPosterior(entity);
            validarSaldoGrupoOrcamentario(entity.getDataEmpenho(), entity.getValor(), entity.getFonteDespesaORC());
            entity = salvarNovoEmpenhoGerandoHistorico(entity);
            removerReservaDotacaoParaIntegracaoDeDiaria(entity);
            removerReservaDotacaoParaIntegracaoReconhecimento(entity);
            movimentarEmpenho(entity);
            gerarMovimentoDespesaORC(entity);
            gerarSaldoDividaPublica(entity);
            movimentarObrigacaoPagar(entity);
            atribuirSolicitacaoEmpenhoNoEmpenho(entity);
            contabilizarEmpenho(entity);
            salvarPortal(entity);
        }
        return entity;
    }

    private void gerarSaldoDividaPublica(Empenho entity) {
        if (entity.getDividaPublica() != null && SubTipoDespesa.VALOR_PRINCIPAL.equals(entity.getSubTipoDespesa()) && (TipoContaDespesa.PRECATORIO.equals(entity.getTipoContaDespesa()) || TipoContaDespesa.DIVIDA_PUBLICA.equals(entity.getTipoContaDespesa()))) {
            ConfiguracaoContabil configuracaoContabil = configuracaoContabilFacade.configuracaoContabilVigente();
            try {
                if (hasContaDeDespesaConfiguradaParaGerarSaldoDividaPublica(entity, configuracaoContabil)) {
                    saldoDividaPublicaFacade.gerarMovimento(entity.getDataEmpenho(), entity.getValor(), entity.getUnidadeOrganizacional(), entity.getDividaPublica(), OperacaoMovimentoDividaPublica.EMPENHO, false, OperacaoDiarioDividaPublica.EMPENHO, Intervalo.CURTO_PRAZO, entity.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao(), true);
                }
            } catch (Exception e) {
                throw new ExcecaoNegocioGenerica(e.getMessage());
            }
        }
    }

    private boolean hasContaDeDespesaConfiguradaParaGerarSaldoDividaPublica(Empenho entity, ConfiguracaoContabil configuracaoContabil) {
        for (ConfiguracaoContabilContaDespesa configuracaoContabilContaDespesa : configuracaoContabil.getContasDespesa()) {
            if (entity.getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getCodigo().startsWith(configuracaoContabilContaDespesa.getContaDespesa().getCodigoSemZerosAoFinal())) {
                return true;
            }
        }
        return false;
    }

    private Empenho salvarNovoEmpenhoGerandoHistorico(Empenho entity) {
        if (entity.getId() == null) {
            geraNumeroEmpenho(entity);
            entity.setSaldo(entity.getValor());
            entity.gerarHistoricos();
            em.persist(entity);
        } else {
            entity.gerarHistoricos();
            entity = em.merge(entity);
        }
        return entity;
    }

    private void salvarPortal(Empenho entity) {
        portalTransparenciaNovoFacade.salvarPortal(new EmpenhoPortal(entity));
    }

    public Empenho salvarNovoParaFechamentoExercicio(InscricaoEmpenho inscricaoEmpenho) throws ExcecaoNegocioGenerica {
        try {
            Empenho entity = inscricaoEmpenho.getEmpenhoInscrito();
            if (getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
                throw new ExcecaoNegocioGenerica(getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
            } else {

                List<Liquidacao> liquidacoes = new ArrayList<>(entity.getLiquidacoes());
                Empenho empenho = entity.getEmpenho();
                empenho.setSaldo(BigDecimal.ZERO);
                mergiarEmpenho(empenho);

                if (entity.getId() == null) {
                    geraNumeroEmpenho(entity);
                    entity.gerarHistoricos();
                    em.persist(entity);
                } else {
                    entity.gerarHistoricos();
                    entity = em.merge(entity);
                }
                contabilizarEmpenhoParaFechamentoDeExercicio(entity, inscricaoEmpenho);
                liquidacaoFacade.salvarEncerramento(liquidacoes, entity);
            }
            return entity;
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }

    private void gerarMovimentoDespesaORC(Empenho entity) {
        if (entity.getCategoriaOrcamentaria().equals(CategoriaOrcamentaria.RESTO)) {
            if (entity.getTipoRestosProcessados().equals(TipoRestosProcessado.PROCESSADOS)) {
                MovimentoDespesaORC mdorc = gerarMovimentoDespesaOrcPorOperacao(entity, OperacaoORC.EMPENHORESTO_PROCESSADO, TipoOperacaoORC.NORMAL);
                entity.setMovimentoDespesaORC(mdorc);
            } else {
                MovimentoDespesaORC mdorc = gerarMovimentoDespesaOrcPorOperacao(entity, OperacaoORC.EMPENHORESTO_NAO_PROCESSADO, TipoOperacaoORC.NORMAL);
                entity.setMovimentoDespesaORC(mdorc);
            }
        } else {
            MovimentoDespesaORC mdorc = gerarMovimentoDespesaOrcPorOperacao(entity, OperacaoORC.EMPENHO, TipoOperacaoORC.NORMAL);
            entity.setMovimentoDespesaORC(mdorc);
        }
    }

    private MovimentoDespesaORC gerarMovimentoDespesaOrcPorOperacao(Empenho entity, OperacaoORC operacaoORC, TipoOperacaoORC tipoOperacaoORC) {
        SaldoFonteDespesaORCVO vo = new SaldoFonteDespesaORCVO(entity.getFonteDespesaORC(), operacaoORC, tipoOperacaoORC, entity.getValor(), DataUtil.getDataComHoraAtual(entity.getDataEmpenho()), entity.getUnidadeOrganizacional(), entity.getId().toString(), entity.getClass().getSimpleName(), entity.getNumero(), entity.getHistoricoRazao());
        return saldoFonteDespesaORCFacade.gerarSaldoOrcamentario(vo);
    }

    private void movimentarEmpenho(Empenho entity) {
        validarCotaOrcamentaria(entity);
        removerSaldoOrcamentarioReservadoPorLicitacao(entity);
        adicionarLiberacaoDeRecursoConvenioReceita(entity);
        atribuirParcelaDividaPublicaNoEmpenho(entity);
    }

    private void movimentarObrigacaoPagar(Empenho entity) {
        if (entity.isEmpenhoDeObrigacaoPagar()) {
            for (EmpenhoObrigacaoPagar empenhoObrigacaoPagar : entity.getObrigacoesPagar()) {
                ObrigacaoAPagar obrigacaoAPagar = obrigacaoAPagarFacade.recuperar(empenhoObrigacaoPagar.getObrigacaoAPagar().getId());

                for (DesdobramentoEmpenho desdEmpenho : entity.getDesdobramentos()) {
                    for (DesdobramentoObrigacaoPagar desdObrigacao : obrigacaoAPagar.getDesdobramentos()) {
                        if (desdEmpenho.getConta().equals(desdObrigacao.getConta()) && desdEmpenho.getDesdobramentoObrigacaoPagar().getObrigacaoAPagar().equals(obrigacaoAPagar)) {
                            desdObrigacao.setSaldo(desdObrigacao.getSaldo().subtract(desdEmpenho.getValor()));
                            obrigacaoAPagar.setSaldoEmpenho(obrigacaoAPagar.getSaldoEmpenho().subtract(desdEmpenho.getValor()));
                        }
                    }
                }
                if (obrigacaoAPagar.isObrigacaoPagarAntesEmpenho()) {
                    entity.setSaldoObrigacaoPagarAntesEmp(entity.getValorTotalDetalhamento());
                }
                em.merge(obrigacaoAPagar);
            }
        }
    }

    private void adicionarLiberacaoDeRecursoConvenioReceita(Empenho entity) {
        if (entity.getConvenioDespesa() != null) {
            convenioDespesaFacade.adicionaLiberacaoDoConvenio(entity);
        }
    }

    private void validarCotaOrcamentaria(Empenho entity) {
        Calendar data = Calendar.getInstance();
        data.setTime(entity.getDataEmpenho());
        int mes = (data.get(Calendar.MONTH)) + 1;
        cotaOrcamentariaFacade.aumentarValorUtilizadoCotaOrcamentaria(entity.getFonteDespesaORC(), mes, entity.getValor());
    }

    public void atribuirSolicitacaoEmpenhoNoEmpenho(Empenho entity) {
        if (entity.getSolicitacaoEmpenho() != null) {
            SolicitacaoEmpenho se = solicitacaoEmpenhoFacade.recarregar(entity.getSolicitacaoEmpenho());
            se.setEmpenho(entity);
            se.setTipoEmpenho(entity.getTipoEmpenho());
            em.merge(se);

            atribuirEmpenhoNaExecucaoContrato(entity, se);
            atribuirEmpenhoNaExecucaoProcesso(entity, se);
        }
    }

    private void atribuirEmpenhoNaExecucaoContrato(Empenho entity, SolicitacaoEmpenho se) {
        ExecucaoContratoEmpenho execucaoContratoEmpenho = execucaoContratoFacade.buscarExecucaoContratoEmpenhoPorSolicitacaoEmpenho(se);
        if (execucaoContratoEmpenho != null) {
            execucaoContratoEmpenho.setEmpenho(entity);
            em.merge(execucaoContratoEmpenho);
        }
    }

    private void atribuirEmpenhoNaExecucaoProcesso(Empenho entity, SolicitacaoEmpenho se) {
        if (se.getOrigemSolicitacaoEmpenho().isOrigemExecucaoProcesso()) {
            ExecucaoProcessoEmpenho execProcEmp = execucaoProcessoFacade.buscarExecucaoProcessoEmpenhoPorSolicitacaoEmpenho(se);
            if (execProcEmp != null) {
                execProcEmp.setEmpenho(entity);
                em.merge(execProcEmp);
            }
        }
    }

    private void atribuirParcelaDividaPublicaNoEmpenho(Empenho emp) {
        if (emp.getSolicitacaoEmpenho() != null) {
            if (emp.getTipoContaDespesa().isDividaPublica() || emp.getTipoContaDespesa().isPrecatorio()) {
                DividaPublicaSolicitacaoEmpenho solicitacaoDividaPublica = solicitacaoEmpenhoFacade.recuperarDividaPublicaSolicitacaoEmpenhoApartirSolicitacaoEmpenho(emp.getSolicitacaoEmpenho());
                if (solicitacaoDividaPublica != null) {
                    emp.setSolicitacaoEmpenho(solicitacaoDividaPublica.getSolicitacaoEmpenho());
                    emp.setDividaPublica(solicitacaoDividaPublica.getDividaPublica());
                    emp.setParcelaDividaPublica(solicitacaoDividaPublica.getParcelaDividaPublica());
                    emp.setSubTipoDespesa(solicitacaoDividaPublica.getSubTipoDespesa());
                }
            }
        }
    }

    /**
     * Método que persiste um novo Empenho.
     *
     * @param valor                  *obrigatório
     * @param unidade                *obrigatório
     * @param tipoEmpenho            *obrigatório
     * @param historicoContabil
     * @param fornecedor             *obrigatório - Pessoa que receberá o pagamento deste
     *                               empenho
     * @param fonteDespesaORC        *obrigatório
     * @param despesaORC             *obrigatório
     * @param data                   *obrigatório
     * @param contaDespesaDesdobrada *obrigatório
     * @param complementoHistorico
     * @param solicitacao
     * @return Empenho
     * @throws ExcecaoNegocioGenerica
     */

    public Empenho geraEmpenho(BigDecimal valor, UnidadeOrganizacional unidade, TipoEmpenho tipoEmpenho, HistoricoContabil historicoContabil, Pessoa fornecedor, FonteDespesaORC fonteDespesaORC, DespesaORC despesaORC, Date data, Conta contaDespesaDesdobrada, String complementoHistorico, SolicitacaoEmpenho solicitacao) throws ExcecaoNegocioGenerica {

        try {
            verificarSaldoGrupoOrcamentario(data, valor, fonteDespesaORC);
            Empenho emp = new Empenho();
            emp.setComplementoHistorico(complementoHistorico);
            emp.setDataEmpenho(new Date());
            emp.setDespesaORC(despesaORC);
            emp.setFonteDespesaORC(fonteDespesaORC);
            emp.setFornecedor(fornecedor);
            emp.setHistoricoContabil(historicoContabil);
            emp.setNumero(new BigDecimal(retornaUltimoNumeroEmpenho(unidade)).add(BigDecimal.ONE).toString());
            emp.setTipoEmpenho(tipoEmpenho);
            emp.setUnidadeOrganizacional(unidade);
            emp.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
            emp.setValor(valor);
            emp.setSolicitacaoEmpenho(solicitacao);
            emp.setCategoriaOrcamentaria(CategoriaOrcamentaria.NORMAL);
            /*
             * TODO Verificar parâmetro como null
             */
            salvarEmpenho(emp);
            return emp;
        } catch (Exception e) {
            String msg = "Não foi possível gerar o Empenho!";
            throw new ExcecaoNegocioGenerica(msg);
        }
    }

    private void validarSaldoGrupoOrcamentario(Date dataEmpenho, BigDecimal valor, FonteDespesaORC fonte) {
        try {
            Calendar data = Calendar.getInstance();
            data.setTime(dataEmpenho);
            int mes = (data.get(Calendar.MONTH)) + 1;
            if (!cotaOrcamentariaFacade.validarValorUtilizadoCotaOrcamentaria(fonte, mes, valor)) {
                throw new ExcecaoNegocioGenerica("Saldo indisponível para o Grupo Orçamentário.");
            }
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }

    private void verificarSaldoGrupoOrcamentario(Date dataEmpenho, BigDecimal valor, FonteDespesaORC fonte) {
        try {
            Calendar data = Calendar.getInstance();
            data.setTime(dataEmpenho);
            int mes = (data.get(Calendar.MONTH)) + 1;
            cotaOrcamentariaFacade.validarValorUtilizadoCotaOrcamentaria(fonte, mes, valor);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }

    public void empenharSolicitacao(SolicitacaoEmpenho se) throws ExcecaoNegocioGenerica {
        try {
            Empenho emp = geraEmpenho(se.getValor(), se.getUnidadeOrganizacional(), se.getTipoEmpenho(), se.getHistoricoContabil(), se.getFornecedor(), se.getFonteDespesaORC(), se.getDespesaORC(), se.getDataSolicitacao(), se.getContaDespesaDesdobrada(), se.getComplementoHistorico(), se);
            se.setEmpenho(emp);
            em.merge(se);
        } catch (ExcecaoNegocioGenerica ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    public void removerReservaDotacaoParaIntegracaoDeDiaria(Empenho entity) {
        if (entity.getPropostaConcessaoDiaria() != null && entity.getSolicitacaoEmpenho() != null) {
            if (entity.isEmpenhoNormal()
                && entity.getDespesaORC() != null
                && entity.getSolicitacaoEmpenho().getGerarReserva()
                && TipoContaDespesa.retornaTipoContaConcessaoDiaria().contains(entity.getTipoContaDespesa())) {
                SaldoFonteDespesaORCVO vo = new SaldoFonteDespesaORCVO(
                    entity.getFonteDespesaORC(),
                    OperacaoORC.RESERVADO,
                    TipoOperacaoORC.ESTORNO,
                    entity.getValor(),
                    entity.getDataEmpenho(),
                    entity.getUnidadeOrganizacional(),
                    entity.getId().toString(),
                    entity.getClass().getSimpleName(),
                    entity.getNumero(),
                    entity.getHistoricoRazao());
                saldoFonteDespesaORCFacade.gerarSaldoOrcamentario(vo);
            }
            entity.getSolicitacaoEmpenho().setTipoEmpenho(entity.getTipoEmpenho());
            entity.getSolicitacaoEmpenho().setEmpenho(entity);
        }
    }

    public void removerSaldoOrcamentarioReservadoPorLicitacao(Empenho entity) {
        if (entity.getCategoriaOrcamentaria().isNormal() && entity.getSolicitacaoEmpenho() != null) {
            Exercicio exSolEmp = exercicioFacade.getExercicioPorAno(DataUtil.getAno(entity.getSolicitacaoEmpenho().getDataSolicitacao()));
            if (sistemaFacade.getExercicioCorrente().equals(exSolEmp)) {
                if (entity.isEmpenhoIntegracaoContrato() && entity.getSolicitacaoEmpenho().getContrato() != null) {
                    gerarMovimentoDespesaOrcPorOperacao(entity, OperacaoORC.RESERVADO_POR_LICITACAO, TipoOperacaoORC.ESTORNO);
                }
                if (entity.isEmpenhoIntegracaoExecucaoProcesso()) {
                    gerarMovimentoDespesaOrcPorOperacao(entity, OperacaoORC.RESERVADO_POR_LICITACAO, TipoOperacaoORC.ESTORNO);
                }
            }
        }
    }

    private void removerReservaDotacaoParaIntegracaoReconhecimento(Empenho entity) {
        if (entity.getReconhecimentoDivida() != null && entity.getSolicitacaoEmpenho() != null) {
            if (CategoriaOrcamentaria.NORMAL.equals(entity.getCategoriaOrcamentaria()) && entity.getDespesaORC() != null && entity.getSolicitacaoEmpenho().getGerarReserva()) {
                SaldoFonteDespesaORCVO vo = new SaldoFonteDespesaORCVO(entity.getFonteDespesaORC(), OperacaoORC.RESERVADO, TipoOperacaoORC.ESTORNO, entity.getValor(), entity.getDataEmpenho(), entity.getUnidadeOrganizacional(), entity.getId().toString(), entity.getClass().getSimpleName(), entity.getNumero(), entity.getHistoricoRazao());
                saldoFonteDespesaORCFacade.gerarSaldoOrcamentario(vo);
                entity.getSolicitacaoEmpenho().setTipoEmpenho(entity.getTipoEmpenho());
                entity.getSolicitacaoEmpenho().setEmpenho(entity);
            }
        }
    }

    /*
     * Gera Empenho passando usuário como parâmetro
     */
    private Empenho geraEmpenho(UsuarioSistema usuario, BigDecimal valor, UnidadeOrganizacional unidade, TipoEmpenho tipoEmpenho, HistoricoContabil historicoContabil, Pessoa fornecedor, FonteDespesaORC fonteDespesaORC, DespesaORC despesaORC, Date data, Conta contaDespesaDesdobrada, String complementoHistorico, SolicitacaoEmpenho solicitacao) throws ExcecaoNegocioGenerica {

        try {
            verificarSaldoGrupoOrcamentario(data, valor, fonteDespesaORC);
            Empenho emp = new Empenho();
            emp.setComplementoHistorico(complementoHistorico);
            emp.setDataEmpenho(new Date());
            emp.setDespesaORC(despesaORC);
            emp.setFonteDespesaORC(fonteDespesaORC);
            emp.setFornecedor(fornecedor);
            emp.setHistoricoContabil(historicoContabil);
            emp.setNumero(new BigDecimal(retornaUltimoNumeroEmpenho(unidade)).add(BigDecimal.ONE).toString());
            emp.setTipoEmpenho(tipoEmpenho);
            emp.setUnidadeOrganizacional(unidade);
            emp.setUsuarioSistema(usuario);
            emp.setValor(valor);
            emp.setSolicitacaoEmpenho(solicitacao);
            emp.setCategoriaOrcamentaria(CategoriaOrcamentaria.NORMAL);
            salvarNovo(emp);
            return emp;
        } catch (Exception e) {
            String msg = "Não foi possível gerar o Empenho!";
            throw new ExcecaoNegocioGenerica(msg);
        }
    }

    public CotaOrcamentariaFacade getCotaOrcamentariaFacade() {
        return cotaOrcamentariaFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public LiquidacaoFacade getLiquidacaoFacade() {
        return liquidacaoFacade;
    }

    public PagamentoFacade getPagamentoFacade() {
        return pagamentoFacade;
    }

    public SaldoFonteDespesaORCFacade getSaldoFonteDespesaORCFacade() {
        return saldoFonteDespesaORCFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public SolicitacaoEmpenhoFacade getSolicitacaoEmpenhoFacade() {
        return solicitacaoEmpenhoFacade;
    }

    public HistoricoContabilFacade getHistoricoContabilFacade() {
        return historicoContabilFacade;
    }

    public ConfiguracaoContabilFacade getConfiguracaoContabilFacade() {
        return configuracaoContabilFacade;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    public DespesaORCFacade getDespesaORCFacade() {
        return despesaORCFacade;
    }


    public FonteDespesaORCFacade getFonteDespesaORCFacade() {
        return fonteDespesaORCFacade;
    }

    public LiquidacaoEstornoFacade getLiquidacaoEstornoFacade() {
        return liquidacaoEstornoFacade;
    }

    public PagamentoEstornoFacade getPagamentoEstornoFacade() {
        return pagamentoEstornoFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public DividaPublicaFacade getDividaPublicaFacade() {
        return dividaPublicaFacade;
    }

    public ConvenioDespesaFacade getConvenioDespesaFacade() {
        return convenioDespesaFacade;
    }

    public ConvenioReceitaFacade getConvenioReceitaFacade() {
        return convenioReceitaFacade;
    }

    public ClasseCredorFacade getClasseCredorFacade() {
        return classeCredorFacade;
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }

    public PropostaConcessaoDiariaFacade getPropostaConcessaoDiariaFacade() {
        return propostaConcessaoDiariaFacade;
    }

    public CodigoCOFacade getCodigoCOFacade() {
        return codigoCOFacade;
    }

    public Empenho getEmpenhoPorSolicitacaoEmpenho(SolicitacaoEmpenho se) {
        String hql = " select new Empenho(e.id) from SolicitacaoEmpenho se " + " inner join se.empenho e" + " where se = :solicitacao";

        Query q = em.createQuery(hql);
        q.setParameter("solicitacao", se);
        q.setMaxResults(1);
        try {
            return (Empenho) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public Empenho recuperaEmpenhoConstrutorSimples(Long id) {
        String sql = "select obj from Empenho obj where obj.id = :id";

        Query q = em.createQuery(sql);
        q.setParameter("id", id);
        try {
            return (Empenho) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public Empenho recuperarEstornosDoEmpenho(Empenho e) {
        Query consulta = em.createQuery("select e from Empenho e inner join fetch e.empenhoEstornos where e.id = :empenho");
        consulta.setParameter("empenho", e.getId());
        try {
            Empenho emp = (Empenho) consulta.getSingleResult();
            return emp;
        } catch (Exception ex) {
            e.setEmpenhoEstornos(new ArrayList<EmpenhoEstorno>());
            return e;
        }
    }

    public ContratoFacade getContratoFacade() {
        return contratoFacade;
    }

    public ConfigEmpenhoFacade getConfigEmpenhoFacade() {
        return configEmpenhoFacade;
    }

    private void contabilizarEmpenho(Empenho entity) {
        if (entity.isEmpenhoNormal()) {
            if (!Hibernate.isInitialized(entity.getDesdobramentos())) {
                entity = recuperar(entity.getId());
            }
            if (!entity.getDesdobramentos().isEmpty()) {
                for (DesdobramentoEmpenho desd : entity.getDesdobramentos()) {
                    contabilizarPorDesdobramento(desd);
                }
            } else {
                contabilizarEmpenhoNormal(entity);
            }
        } else {
            contabilizarEmpenhoResto(entity);
        }
    }

    public void contabilizarEmpenhoParaFechamentoDeExercicio(Empenho entity, InscricaoEmpenho inscricaoEmpenho) {
        if (entity.getEventoContabil().getId() != null) {
            entity.gerarHistoricos();
            ParametroEvento parametroEvento = new ParametroEvento();
            parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
            parametroEvento.setDataEvento(inscricaoEmpenho.getAberturaFechamentoExercicio().getDataGeracao());
            parametroEvento.setUnidadeOrganizacional(entity.getUnidadeOrganizacional());
            parametroEvento.setEventoContabil(inscricaoEmpenho.getEventoContabil());
            parametroEvento.setClasseOrigem(InscricaoEmpenho.class.getSimpleName());
            parametroEvento.setIdOrigem(inscricaoEmpenho.getId().toString());
            parametroEvento.setExercicio(inscricaoEmpenho.getEmpenho().getExercicio());
            parametroEvento.setTipoBalancete(TipoBalancete.APURACAO);

            ItemParametroEvento item = new ItemParametroEvento();
            item.setValor(inscricaoEmpenho.getValor());
            item.setParametroEvento(parametroEvento);
            item.setTagValor(TagValor.LANCAMENTO);

            List<ObjetoParametro> objetos = criarObjetosParametros(inscricaoEmpenho.getEmpenho(), null, item);
            item.setObjetoParametros(objetos);
            parametroEvento.getItensParametrosEvento().add(item);

            contabilizacaoFacade.contabilizar(parametroEvento);

        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada na contabilização de Resto a Pagar.");
        }
    }

    private Conta getContaDespesaEmpenho(Empenho entity) {
        Conta contaDeDespesa = entity.getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa();
        Preconditions.checkNotNull(contaDeDespesa, "A conta de despesa do empenho não foi preenchida.");
        return contaDeDespesa;
    }

    private void contabilizarEmpenhoResto(Empenho entity) {
        EventoContabil eventoContabil = buscarEventoContabilRestoPagar(entity, getContaDespesaEmpenho(entity));
        if (eventoContabil != null) {
            entity.setEventoContabil(eventoContabil);
            entity.gerarHistoricos();
            contabilizacaoFacade.contabilizar(getParametroEvento(entity));
        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada na contabilização de Resto a Pagar.");
        }
    }

    private void contabilizarPorDesdobramento(DesdobramentoEmpenho desdobramento) {
        desdobramento.getEmpenho().gerarHistoricos();
        contabilizacaoFacade.contabilizar(getParametroEventoDesdobramento(desdobramento));
    }

    private void contabilizarEmpenhoNormal(Empenho entity) {
        try {
            if (!configuracaoContabilFacade.configuracaoContabilVigente().getBuscarEventoEmpDiferenteObrig()) {
                atualizarEventoEContabilizar(entity);
            } else if (entity.getValorLiquidoEmpenho().compareTo(BigDecimal.ZERO) > 0) {
                atualizarEventoEContabilizar(entity);
            }
        } catch (ExcecaoNegocioGenerica ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    private void atualizarEventoEContabilizar(Empenho entity) {
        Preconditions.checkNotNull(entity.getDespesaORC(), "Não foi encontrado Despesa Orçamentária.");
        Preconditions.checkNotNull(entity.getDespesaORC().getContaDeDespesa(), "Não foi encontrado a conta de despesa.");
        EventoContabil eventoContabil = buscarEventoContabilEmpenho(entity, entity.getDespesaORC().getContaDeDespesa());

        if (eventoContabil != null && eventoContabil.getId() != null) {
            entity.setEventoContabil(eventoContabil);
            entity.gerarHistoricos();
        }
        contabilizacaoFacade.contabilizar(getParametroEvento(entity));
    }

    private ParametroEvento criarParametroEvento(Empenho entity) {
        ParametroEvento parametroEvento = new ParametroEvento();
        parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
        parametroEvento.setDataEvento(entity.getDataEmpenho());
        parametroEvento.setUnidadeOrganizacional(entity.getUnidadeOrganizacional());
        parametroEvento.setEventoContabil(entity.getEventoContabil());
        parametroEvento.setClasseOrigem(Empenho.class.getSimpleName());
        parametroEvento.setIdOrigem(entity.getId() == null ? null : entity.getId().toString());
        parametroEvento.setExercicio(entity.getExercicio());
        return parametroEvento;
    }

    private ItemParametroEvento criarItemParametroEvento(BigDecimal valor, ParametroEvento parametroEvento) {
        ItemParametroEvento item = new ItemParametroEvento();
        item.setValor(valor);
        item.setParametroEvento(parametroEvento);
        item.setTagValor(TagValor.LANCAMENTO);
        return item;
    }

    private List<ObjetoParametro> criarObjetosParametros(Empenho entity, DesdobramentoEmpenho desdobramentoEmpenho, ItemParametroEvento item) {
        List<ObjetoParametro> objetos = Lists.newArrayList();
        if (desdobramentoEmpenho == null) {
            objetos.add(new ObjetoParametro(entity, item));
            Conta contaDespesaEmpenho = getContaDespesaEmpenho(entity);
            objetos.add(new ObjetoParametro(contaDespesaEmpenho, item));
        } else {
            objetos.add(new ObjetoParametro(desdobramentoEmpenho, item));
            objetos.add(new ObjetoParametro(desdobramentoEmpenho, item));
        }

        Preconditions.checkNotNull(entity.getClasseCredor(), "A Classe credor do empenho não foi preenchida.");
        objetos.add(new ObjetoParametro(entity.getClasseCredor(), item));
        objetos.add(new ObjetoParametro(entity.getFornecedor(), item));
        if (entity.getId() != null) {
            objetos.add(new ObjetoParametro(entity, item));
        }
        return objetos;
    }

    private ParametroEvento criarParametroEventoDesdobramento(DesdobramentoEmpenho desdobramento) {
        ParametroEvento parametroEvento = new ParametroEvento();
        parametroEvento.setComplementoHistorico(desdobramento.getEmpenho().getHistoricoRazao());
        parametroEvento.setDataEvento(desdobramento.getEmpenho().getDataEmpenho());
        parametroEvento.setUnidadeOrganizacional(desdobramento.getEmpenho().getUnidadeOrganizacional());
        parametroEvento.setEventoContabil(desdobramento.getEmpenho().getEventoContabil());
        parametroEvento.setClasseOrigem(DesdobramentoEmpenho.class.getSimpleName());
        parametroEvento.setIdOrigem(desdobramento.getId() == null ? null : desdobramento.getId().toString());
        parametroEvento.setExercicio(desdobramento.getConta().getExercicio());
        return parametroEvento;
    }

    public EventoContabil buscarEventoContabil(Empenho entity) {
        Conta contaDespesa = entity.getDespesaORC().getContaDeDespesa();
        Preconditions.checkNotNull(contaDespesa, " Conta de despesa não recuperada da despesaOrc.");
        if (!entity.isEmpenhoRestoPagar()) {
            return buscarEventoContabilEmpenho(entity, contaDespesa);
        } else {
            return buscarEventoContabilRestoPagar(entity, contaDespesa);
        }
    }

    private EventoContabil buscarEventoContabilEmpenho(Empenho entity, Conta contaDespesa) {
        EventoContabil eventoContabil = null;
        ConfigEmpenho configuracao = null;
        if (!configuracaoContabilFacade.configuracaoContabilVigente().getBuscarEventoEmpDiferenteObrig()) {
            configuracao = configEmpenhoFacade.buscarCDEEmpenho(contaDespesa, TipoLancamento.NORMAL, entity.getDataEmpenho(), entity.getTipoReconhecimento(), entity.getTipoContaDespesa(), entity.getSubTipoDespesa());
        } else {
            configuracao = configEmpenhoFacade.buscarCDEEmpenho(contaDespesa, TipoLancamento.NORMAL, entity.getDataEmpenho(), TipoReconhecimentoObrigacaoPagar.SEM_RECONHECIMENTO_OBRIGACAO_ANTES_EMPENHO_DESPESA, entity.getTipoContaDespesa(), entity.getSubTipoDespesa());
        }
        if (configuracao != null) {
            eventoContabil = configuracao.getEventoContabil();
        }
        return eventoContabil;
    }

    public EventoContabil buscarEventoContabilPorDesdobramento(DesdobramentoEmpenho desdobramento) {
        EventoContabil eventoContabil = null;
        ConfigEmpenho configuracao = configEmpenhoFacade.buscarCDEEmpenho(desdobramento.getConta(), TipoLancamento.NORMAL, desdobramento.getEmpenho().getDataEmpenho(), desdobramento.getEmpenho().getTipoReconhecimento(), desdobramento.getEmpenho().getTipoContaDespesa(), desdobramento.getEmpenho().getSubTipoDespesa());
        if (configuracao != null) {
            eventoContabil = configuracao.getEventoContabil();
        }
        return eventoContabil;
    }

    private EventoContabil buscarEventoContabilRestoPagar(Empenho entity, Conta contaDespesa) {
        EventoContabil eventoContabil = null;
        ConfigEmpenhoRestoPagar configuracao = configEmpenhoRestoFacade.recuperarEventoPorContaDespesa(contaDespesa, TipoLancamento.NORMAL, entity.getDataEmpenho(), entity.getTipoRestosProcessados(), !entity.getObrigacoesPagar().isEmpty());
        if (configuracao != null) {
            eventoContabil = configuracao.getEventoContabil();
        }
        return eventoContabil;
    }

    public ReprocessamentoLancamentoContabilSingleton getReprocessamentoLancamentoContabilSingleton() {
        return reprocessamentoLancamentoContabilSingleton;
    }

    public ConfiguracaoTipoContaDespesaClasseCredorFacade getConfiguracaoTipoContaDespesaClasseCredorFacade() {
        return configuracaoTipoContaDespesaClasseCredorFacade;
    }

    public ExecucaoContratoFacade getExecucaoContratoFacade() {
        return execucaoContratoFacade;
    }

    public SingletonConcorrenciaContabil getSingletonConcorrenciaContabil() {
        return singletonConcorrenciaContabil;
    }

    public EmendaFacade getEmendaFacade() {
        return emendaFacade;
    }

    @Override
    public void contabilizarReprocessamento(EntidadeContabil entidadeContabil) {
        contabilizarEmpenho((Empenho) entidadeContabil);
    }

    private ParametroEvento getParametroEventoDesdobramento(EntidadeContabil entidadeContabil) {
        try {
            DesdobramentoEmpenho desdobramento = (DesdobramentoEmpenho) entidadeContabil;
            ParametroEvento parametroEvento = criarParametroEventoDesdobramento(desdobramento);
            ItemParametroEvento item = criarItemParametroEvento(desdobramento.getValor(), parametroEvento);
            List<ObjetoParametro> objetos = criarObjetosParametros(desdobramento.getEmpenho(), desdobramento, item);

            item.setObjetoParametros(objetos);
            parametroEvento.getItensParametrosEvento().add(item);
            return parametroEvento;
        } catch (Exception e) {
            return null;
        }
    }

    public ParametroEvento getParametroEvento(EntidadeContabil entidadeContabil) {
        try {
            Empenho entity = (Empenho) entidadeContabil;
            ParametroEvento parametroEvento = criarParametroEvento(entity);
            ItemParametroEvento item;
            if (!configuracaoContabilFacade.configuracaoContabilVigente().getBuscarEventoEmpDiferenteObrig()) {
                item = criarItemParametroEvento(entity.getValor(), parametroEvento);
            } else {
                item = criarItemParametroEvento(entity.getValorLiquidoEmpenho(), parametroEvento);
            }

            List<ObjetoParametro> objetos = criarObjetosParametros(entity, null, item);

            item.setObjetoParametros(objetos);
            parametroEvento.getItensParametrosEvento().add(item);
            return parametroEvento;
        } catch (Exception e) {
            return null;
        }
    }

    public ConfigContaDespTipoPessoaFacade getConfigContaDespTipoPessoaFacade() {
        return configContaDespTipoPessoaFacade;
    }

    public List<Liquidacao> buscarLiquidacoesPorEmpenho(Empenho e) {
        String sql = " SELECT LIQ.* FROM EMPENHO EMP " + " INNER JOIN LIQUIDACAO LIQ ON LIQ.EMPENHO_ID = EMP.ID " + " WHERE EMP.ID = :param ";
        Query q = getEntityManager().createNativeQuery(sql.toString(), Liquidacao.class);
        q.setParameter("param", e.getId());
        if (q.getResultList() != null) {
            return q.getResultList();
        } else {
            return new ArrayList<>();
        }
    }

    public List<Empenho> recuperarEmpenhosDaPessoa(Pessoa pessoa) {
        String sql = " select e.* from empenho e " + " where e.fornecedor_id = :pessoa ";
        Query q = em.createNativeQuery(sql, Empenho.class);
        q.setParameter("pessoa", pessoa.getId());
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        }
        return q.getResultList();
    }

    public List<Empenho> buscarEmpenhosPorDoctoFiscalLiquidacao(DoctoFiscalLiquidacao doctoFiscalLiquidacao) {
        String hql = " select distinct liq.empenho from Liquidacao liq " + " join liq.doctoFiscais docto " + " where docto.doctoFiscalLiquidacao.id = :idDoctoFiscalLiquidacao ";
        Query q = em.createQuery(hql);
        q.setParameter("idDoctoFiscalLiquidacao", doctoFiscalLiquidacao.getId());
        try {
            return q.getResultList();
        } catch (NoResultException nre) {
            return Lists.newArrayList();
        }
    }

    public PortalTransparenciaNovoFacade getPortalTransparenciaNovoFacade() {
        return portalTransparenciaNovoFacade;
    }

    public List<Empenho> buscarEmpenhoSemVinculoComContratoPorNumeroOrPessoa(String parte, Pessoa fornecedor) {
        String sql = "SELECT E.* FROM EMPENHO E " + " INNER JOIN PESSOA P ON E.FORNECEDOR_ID = P.ID" + " LEFT JOIN PESSOAFISICA PF ON P.ID = PF.ID " + " LEFT JOIN PESSOAJURIDICA PJ ON P.ID = PJ.ID " + " WHERE (E.NUMERO like :parte " + "    or LOWER(PF.NOME) LIKE :parte or replace(replace(pf.cpf,'.',''),'-','') LIKE :cpfCnpj " + "    or LOWER(PJ.RAZAOSOCIAL) LIKE :parte or replace(replace(pj.cnpj,'.',''),'-','') LIKE :cpfCnpj) " + "  AND E.UNIDADEORGANIZACIONAL_ID = :unidadeAtual " + "  AND E.categoriaOrcamentaria =  :categoria " + "  and e.CONTRATO_ID is null " + "    and E.FORNECEDOR_ID = :idFornecedor " + "    ORDER BY E.ID DESC ";
        Query q = em.createNativeQuery(sql, Empenho.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("categoria", CategoriaOrcamentaria.NORMAL.name());
        q.setParameter("idFornecedor", fornecedor.getId());
        q.setParameter("cpfCnpj", "%" + parte.toLowerCase().trim().replace(".", "").replace("-", "") + "%");
        q.setParameter("unidadeAtual", sistemaFacade.getUnidadeOrganizacionalOrcamentoCorrente().getId());
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public ObrigacaoAPagarFacade getObrigacaoAPagarFacade() {
        return obrigacaoAPagarFacade;
    }

    public List<Empenho> buscarEmpenhoPorObrigacaoPagar(ObrigacaoAPagar obrigacaoAPagar) {
        String sql = " select e.* from empenho e " + "inner join empenhoobrigacaopagar eop on eop.empenho_id = e.id " + " where eop.obrigacaoAPagar_id = :idObrigacao ";
        Query q = em.createNativeQuery(sql, Empenho.class);
        q.setParameter("idObrigacao", obrigacaoAPagar.getId());
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        }
        return q.getResultList();
    }

    public Boolean verificarSeEmpenhoPossuiObrigacaoPagar(Empenho empenho) {
        String sql = " " + "   select op.* from empenho emp " + "   inner join empenhoobrigacaopagar eop on eop.empenho_id = emp.id " + "    inner join obrigacaoapagar op on op.id =  eop.obrigacaoapagar_id " + "   where emp.id = :idEmpenho " + "   and op.saldo > 0 " + "   union " + "   select op.* from obrigacaoapagar op " + "    inner join empenho emp on emp.id = op.empenho_id " + "   where emp.id = :idEmpenho " + "   and op.saldo > 0 ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idEmpenho", empenho.getId());
        return !q.getResultList().isEmpty();
    }


    public List<Conta> buscarContaDesdobradaPorEmpenho(String parte, Empenho empenho) {
        String sql = " " + "   select c.*, cd.* " + "    from desdobramentoempenho desd " + "    inner join conta c on c.id = desd.conta_id " + "    inner join contadespesa cd on cd.id = c.id " + "    where desd.empenho_id = :idEmpenho " + "    AND (LOWER(C.DESCRICAO) LIKE :parte OR (replace(C.CODIGO,'.','') LIKE :parte or C.CODIGO LIKE :parte))" + "    ORDER BY C.CODIGO ";
        Query q = em.createNativeQuery(sql, ContaDespesa.class);
        q.setParameter("idEmpenho", empenho.getId());
        q.setParameter("parte", "%" + parte + "%");
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public DesdobramentoEmpenho buscarDesdobramento(Empenho empenho) {
        String sql = " " + "   select desd.* from desdobramentoempenho desd " + "    where desd.empenho_id = :idEmpenho ";
        Query q = em.createNativeQuery(sql, DesdobramentoEmpenho.class);
        q.setParameter("idEmpenho", empenho.getId());
        q.setMaxResults(1);
        try {
            return (DesdobramentoEmpenho) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public DesdobramentoObrigacaoAPagarFacade getDesdobramentoObrigacaoAPagarFacade() {
        return desdobramentoObrigacaoAPagarFacade;
    }

    public ExecucaoProcessoFacade getExecucaoProcessoFacade() {
        return execucaoProcessoFacade;
    }

    public ExtensaoFonteRecursoFacade getExtensaoFonteRecursoFacade() {
        return extensaoFonteRecursoFacade;
    }

    public List<Empenho> buscarEmpenhosSemSolicitacaoPorUnidade(UnidadeOrganizacional unidadeOrganizacional, String filtro, Exercicio exercicio) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select emp.* from empenho emp").append(" INNER JOIN PESSOA P ON EMP.FORNECEDOR_ID = P.ID  ").append(" LEFT JOIN PESSOAFISICA PF ON P.ID = PF.ID  ").append(" LEFT JOIN PESSOAJURIDICA PJ ON P.ID = PJ.ID  ").append(" where ((LOWER(PF.NOME) LIKE :parte) OR (PF.CPF LIKE :parte) OR (EMP.NUMERO LIKE :parte) ").append(" OR (LOWER(PJ.NOMEFANTASIA) LIKE :parte) OR (LOWER(PJ.NOMEREDUZIDO) LIKE :parte) OR (PJ.CNPJ LIKE :parte) ").append(" OR (EMP.NUMERO LIKE :parte)) ").append(" and emp.unidadeorganizacional_id = :unidade ").append(" and emp.exercicio_id = :exercicio ").append(" and emp.id not in (select sol.empenho_id from solicitacaoempenho sol where sol.unidadeorganizacional_id = :unidade and sol.empenho_id is not null) ").append(" order by emp.numero desc ");
        Query q = em.createNativeQuery(sql.toString(), Empenho.class);
        q.setParameter("unidade", unidadeOrganizacional.getId());
        q.setParameter("exercicio", exercicio.getId());
        q.setParameter("parte", "%" + filtro.trim().toLowerCase() + "%");
        return q.getResultList();
    }


    @Override
    public List<ConsultaMovimentoContabil> criarConsulta(List<FiltroConsultaMovimentoContabil> filtros) {
        ConsultaMovimentoContabil consulta = new ConsultaMovimentoContabil(Empenho.class, filtros);
        consulta.incluirJoinsOrcamentoDespesa(" obj.fontedespesaorc_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_INICIAL, "trunc(obj.dataEmpenho)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_FINAL, "trunc(obj.dataEmpenho)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.UNIDADE, "obj.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.LISTA_UNIDADE, "obj.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CATEGORIA_ORCAMENTARIA, "obj.categoriaorcamentaria");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.PESSOA, "obj.fornecedor_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CLASSE, "obj.classecredor_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.EVENTO_CONTABIL, "obj.eventocontabil_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.NUMERO, "obj.numero");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.HISTORICO, "obj.historicoRazao");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.VALOR, "obj.valor");
        return Arrays.asList(consulta);
    }

    public BigDecimal buscarValorPorFonteDespesaOrc(FonteDespesaORC fonteDespesaORC, Date dataInicial, Date dataFinal) {
        String sql = "select sum(valor) from ( " + "select coalesce(sum(emp.valor), 0) as valor " + "  FROM empenho emp  " + " INNER JOIN FONTEDESPESAORC fontdesp ON emp.FONTEDESPESAORC_ID = fontdesp.ID  " + " where emp.categoriaorcamentaria = :categoria  " + " and fontdesp.id = :fontedespesaorc" + " and trunc(emp.dataempenho) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') " + "  " + " union all " + "  " + " select coalesce(sum(est.valor), 0) *-1 as valor " + "  FROM empenhoestorno est  " + " inner join empenho emp on est.empenho_id = emp.id  " + " INNER JOIN FONTEDESPESAORC fontdesp ON emp.FONTEDESPESAORC_ID = fontdesp.ID  " + " where emp.categoriaorcamentaria = :categoria  " + " and fontdesp.id = :fontedespesaorc" + " and trunc(est.dataestorno) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') " + " ) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("fontedespesaorc", fonteDespesaORC.getId());
        q.setParameter("categoria", CategoriaOrcamentaria.NORMAL.name());
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        if (q.getResultList().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return (BigDecimal) q.getSingleResult();
    }

    public Date buscarMaiorDataEmpenhosPorRequisicaoDeCompra(RequisicaoDeCompra requisicaoDeCompra) {
        try {
            String sql = "select max(trunc(emp.dataempenho)) from requisicaodecompra req " +
                "           left join requisicaocompraexecucao reqEx on reqEx.requisicaocompra_id = req.id " +
                "           left join execucaocontratoempenho exEmp on  exEmp.id = reqEx.execucaocontratoempenho_id " +
                "           left join execucaoprocessoempenho exEmpProc on exEmpProc.id = reqEx.execucaoprocessoempenho_id " +
                "           left join solicitacaoempenhorecdiv solrd on solrd.id = reqEx.execucaoreconhecimentodiv_id " +
                "           left join solicitacaoempenho sol on sol.id = solrd.solicitacaoempenho_id " +
                "           inner join empenho emp on emp.id = coalesce(exEmp.empenho_id, exEmpProc.empenho_id, sol.empenho_id) " +
                "         where req.id = :requisicao " +
                "         order by emp.dataempenho desc ";
            Query q = em.createNativeQuery(sql);
            q.setParameter("requisicao", requisicaoDeCompra.getId());
            q.setMaxResults(1);
            return (Date) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Date buscarMenorDataEmpenhosPorRequisicaoDeCompra(List<Long> idsEmpenho) {
        try {
            String sql = "select min(cast(emp.dataempenho as date)) from empenho emp " + "         where emp.id in (:idsEmpenhos) " + "         order by emp.dataempenho ";
            Query q = em.createNativeQuery(sql);
            q.setParameter("idsEmpenhos", idsEmpenho);
            q.setMaxResults(1);
            return (Date) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<Object[]> buscarEmpenhos(Exercicio exercicio, Date dataOperacao) {
        String sql = " select ano,  " + "       mes, " + "       dia, " + "       contaCodigo, " + "       orgao, " + "       funcaoCodigo, " + "       subFuncaoCodigo, " + "       programaCodigo, " + "       tipoAcaoCodigo, " + "       acaoCodigo, " + "       fonteCodigo, " + "       subFonteCodigo, " + "       fornecedor, " + "        coalesce(numerotermo, 0) as NUMEROCONTRATO, " + "       valor " + "  from ( " + "select extract(year from emp.dataEmpenho) as ano, " + "       extract(month from emp.dataEmpenho) as mes, " + "       extract(day from emp.dataEmpenho) as dia, " + "       substr(c.codigo, 0, 12) as contaCodigo,  " + "       VW.codigo as orgao,  " + "       f.codigo as funcaoCodigo,  " + "       sf.codigo as subFuncaoCodigo,  " + "       prog.codigo as programaCodigo,  " + "       tpa.codigo as tipoAcaoCodigo,  " + "       a.codigo as acaoCodigo,  " + "       fr.codigo as fonteCodigo,  " + "       case when coalesce((select '1' from CALAMIDADEPUBLICACONTRATO cpc " + "                 where cpc.CONTRATO_ID = cont.id), '0') = '1' then '1' " + "            when tpa.codigo || a.codigo || '.' || sub.codigo in ('1396.0000', '1397.0000', '1398.0000', '1399.0000', '1400.0000', '1400.0001', '1401.0000') " + "                 then '1' " + "            when fr.codigo = '126' then '1' " + "            else '0' " + "       end as subFonteCodigo, " + "       emp.FORNECEDOR_ID as fornecedor, " + "       to_number(cont.numerotermo) as numerotermo, " + "       emp.valor as valor " + "  from empenho emp " + "  left join contrato cont on emp.contrato_id = cont.id " + " inner join fontedespesaorc fontOrc on emp.FONTEDESPESAORC_ID = fontOrc.id " + " inner join provisaoppafonte provFonte on fontOrc.PROVISAOPPAFONTE_ID = provFonte.id " + " inner join CONTADEDESTINACAO cd on provFonte.DESTINACAODERECURSOS_ID= cd.id " + " inner join FONTEDERECURSOS fr on cd.FONTEDERECURSOS_ID = fr.id " + " inner join despesaorc desp on emp.DESPESAORC_ID = desp.id " + " inner join provisaoppadespesa provDesp on desp.PROVISAOPPADESPESA_ID = provDesp.id " + " inner join conta c on provDesp.CONTADEDESPESA_ID = c.id " + " inner join VWHIERARQUIAORCAMENTARIA vw on emp.UNIDADEORGANIZACIONAL_ID = vw.SUBORDINADA_ID " + " INNER JOIN SUBACAOPPA SUB ON SUB.ID = provdesp.subacaoppa_id " + " INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id " + " INNER JOIN TIPOACAOPPA TPA ON TPA.ID = A.TIPOACAOPPA_ID " + " INNER JOIN programappa PROG ON PROG.ID = A.programa_id " + " INNER JOIN FUNCAO F ON F.ID = A.funcao_id " + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id " + " where TO_DATE(:dataOperacao, 'dd/mm/yyyy') between vw.INICIOVIGENCIA and coalesce(vw.FIMVIGENCIA, TO_DATE(:dataOperacao, 'dd/mm/yyyy')) " + "   and emp.EXERCICIO_ID = :exercicio " + "   and emp.CATEGORIAORCAMENTARIA = :categoria " + " union all " + "select extract(year from est.dataEstorno) as ano, " + "       extract(month from est.dataEstorno) as mes, " + "       extract(day from est.dataEstorno) as dia, " + "       substr(c.codigo, 0, 12) as contaCodigo,  " + "       VW.codigo as orgao,  " + "       f.codigo as funcaoCodigo,  " + "       sf.codigo as subFuncaoCodigo,  " + "       prog.codigo as programaCodigo,  " + "       tpa.codigo as tipoAcaoCodigo,  " + "       a.codigo as acaoCodigo,  " + "       fr.codigo as fonteCodigo,  " + "       case when coalesce((select '1' from CALAMIDADEPUBLICACONTRATO cpc " + "                 where cpc.CONTRATO_ID = cont.id), '0') = '1' then '1' " + "            when tpa.codigo || a.codigo || '.' || sub.codigo in ('1396.0000', '1397.0000', '1398.0000', '1399.0000', '1400.0000', '1400.0001', '1401.0000') " + "                 then '1' " + "            when fr.codigo = '126' then '1' " + "            else '0' " + "       end as subFonteCodigo, " + "       emp.FORNECEDOR_ID as fornecedor, " + "       to_number(cont.numerotermo) as numerotermo, " + "       est.valor * -1 as valor " + "  from empenhoestorno est " + " inner join empenho emp on est.empenho_id = emp.id" + "  left join contrato cont on emp.contrato_id = cont.id " + " inner join fontedespesaorc fontOrc on emp.FONTEDESPESAORC_ID = fontOrc.id " + " inner join provisaoppafonte provFonte on fontOrc.PROVISAOPPAFONTE_ID = provFonte.id " + " inner join CONTADEDESTINACAO cd on provFonte.DESTINACAODERECURSOS_ID= cd.id " + " inner join FONTEDERECURSOS fr on cd.FONTEDERECURSOS_ID = fr.id " + " inner join despesaorc desp on emp.DESPESAORC_ID = desp.id " + " inner join provisaoppadespesa provDesp on desp.PROVISAOPPADESPESA_ID = provDesp.id " + " inner join conta c on provDesp.CONTADEDESPESA_ID = c.id " + " inner join VWHIERARQUIAORCAMENTARIA vw on est.UNIDADEORGANIZACIONAL_ID = vw.SUBORDINADA_ID " + " INNER JOIN SUBACAOPPA SUB ON SUB.ID = provdesp.subacaoppa_id " + " INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id " + " INNER JOIN TIPOACAOPPA TPA ON TPA.ID = A.TIPOACAOPPA_ID " + " INNER JOIN programappa PROG ON PROG.ID = A.programa_id " + " INNER JOIN FUNCAO F ON F.ID = A.funcao_id " + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id " + " where TO_DATE(:dataOperacao, 'dd/mm/yyyy') between vw.INICIOVIGENCIA and coalesce(vw.FIMVIGENCIA, TO_DATE(:dataOperacao, 'dd/mm/yyyy')) " + "   and est.EXERCICIO_ID = :exercicio " + "   and est.CATEGORIAORCAMENTARIA = :categoria) " + " order by dia, mes, ano ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("exercicio", exercicio.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        q.setParameter("categoria", CategoriaOrcamentaria.NORMAL.name());
        return q.getResultList();
    }

    public List<Empenho> buscarEmpenhosPorRequisicaoCompra(Long idRequisicao) {
        String sql = " select distinct emp.* from requisicaodecompra req  " +
            "           inner join requisicaocompraexecucao reqEx on reqEx.requisicaocompra_id = req.id " +
            "           left join execucaocontratoempenho exEmp on  exEmp.id = reqEx.execucaocontratoempenho_id " +
            "           left join execucaoprocessoempenho exEmpProc on exEmpProc.id = reqEx.execucaoprocessoempenho_id " +
            "           left join solicitacaoempenhorecdiv solrd on solrd.id = reqEx.execucaoreconhecimentodiv_id " +
            "           left join solicitacaoempenho sol on sol.id = solrd.solicitacaoempenho_id " +
            "           inner join empenho emp on emp.id = coalesce(exEmp.empenho_id, exEmpProc.empenho_id, sol.empenho_id) " +
            "          where req.id = :idRequisicao " +
            "          order by emp.numero ";
        Query q = em.createNativeQuery(sql, Empenho.class);
        q.setParameter("idRequisicao", idRequisicao);
        return q.getResultList();
    }

    public Empenho buscarEmpenhoNovoPorEmpenho(Empenho empenho) {
        String sql = "select * from Empenho where empenho_id = :idEmpenho ";
        Query q = em.createNativeQuery(sql, Empenho.class);
        q.setParameter("idEmpenho", empenho.getId());
        q.setMaxResults(1);
        try {
            return (Empenho) q.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public List<Empenho> buscarEmpenhosNormaisAndRestos(UnidadeOrganizacional unidadeOrganizacional, String filtro, Exercicio exercicio) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select emp.* from empenho emp")
            .append(" INNER JOIN PESSOA P ON EMP.FORNECEDOR_ID = P.ID  ")
            .append(" LEFT JOIN PESSOAFISICA PF ON P.ID = PF.ID  ")
            .append(" LEFT JOIN PESSOAJURIDICA PJ ON P.ID = PJ.ID  ")
            .append(" where ((LOWER(PF.NOME) LIKE :parte) OR (PF.CPF LIKE :parte) OR (EMP.NUMERO LIKE :parte) ")
            .append(" OR (LOWER(PJ.NOMEFANTASIA) LIKE :parte) OR (LOWER(PJ.NOMEREDUZIDO) LIKE :parte) OR (PJ.CNPJ LIKE :parte) ")
            .append(" OR (EMP.NUMERO LIKE :parte)) ")
            .append(" and emp.unidadeorganizacional_id = :unidade ")
            .append(" and emp.exercicio_id = :exercicio ")
            .append(" order by emp.numero desc ");
        Query q = em.createNativeQuery(sql.toString(), Empenho.class);
        q.setParameter("unidade", unidadeOrganizacional.getId());
        q.setParameter("exercicio", exercicio.getId());
        q.setParameter("parte", "%" + filtro.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public Empenho recuperarEmpenhoPorGrupoBemAndProcesso(GrupoBem grupoBem, Long idProcesso) {
        String sql = "" + " select emp.* from empenho emp " + "   inner join desdobramentoempenho desd on desd.EMPENHO_ID = emp.id " + "   inner join ConfigDespesaBens config on config.CONTADESPESA_ID = desd.CONTA_ID " + " where config.GRUPOBEM_ID = :idGrupo " + "   and to_date(:dataOperacao, 'dd/mm/yyyy') between trunc(config.iniciovigencia) and coalesce(trunc(config.fimvigencia), to_date(:dataOperacao, 'dd/mm/yyyy')) " + "   and coalesce(emp.contrato_id, emp.execucaoprocesso_id) = :idProcesso ";
        Query q = em.createNativeQuery(sql, Empenho.class);
        q.setParameter("idGrupo", grupoBem.getId());
        q.setParameter("idProcesso", idProcesso);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setMaxResults(1);
        try {
            return (Empenho) q.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public Empenho recuperarEmpenhoPorGrupoMaterialAndContrato(GrupoMaterial grupoMaterial, Long idProcesso) {
        String sql = "" + " select emp.* from empenho emp " + "   inner join desdobramentoempenho desd on desd.EMPENHO_ID = emp.id " + "   inner join CONFIGGRUPOMATERIAL config on config.CONTADESPESA_ID = desd.CONTA_ID " + " where config.GRUPOMATERIAL_ID = :idGrupo " + "   and to_date(:dataOperacao, 'dd/mm/yyyy') between trunc(config.iniciovigencia) and coalesce(trunc(config.fimvigencia), to_date(:dataOperacao, 'dd/mm/yyyy')) " + "   and coalesce(emp.contrato_id, emp.execucaoprocesso_id) = :idProcesso ";
        Query q = em.createNativeQuery(sql, Empenho.class);
        q.setParameter("idGrupo", grupoMaterial.getId());
        q.setParameter("idProcesso", idProcesso);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setMaxResults(1);
        try {
            return (Empenho) q.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public List<Empenho> buscarEmpenhoRestoPagarExercicio(Empenho empenhoOriginal, Exercicio exercicio) {
        String sql = " select resto.* from empenho resto where resto.empenho_id = :idEmpenho and resto.exercicio_id = :idExercicio ";
        Query q = em.createNativeQuery(sql, Empenho.class);
        q.setParameter("idEmpenho", empenhoOriginal.getId());
        q.setParameter("idExercicio", exercicio.getId());
        List resultList = q.getResultList();
        if (resultList == null || resultList.isEmpty()) {
            return Lists.newArrayList();
        }
        return resultList;
    }

    public Object[] recuperarIdENumeroExecucaoContrato(SolicitacaoEmpenho solicitacaoEmpenho) {
        return execucaoContratoFacade.recuperarIdENumeroExecucaoContrato(solicitacaoEmpenho);
    }

    public Object[] recuperarIdENumeroExecucaoProcesso(SolicitacaoEmpenho solicitacaoEmpenho) {
        return execucaoProcessoFacade.recuperarIdENumeroExecucaoProcesso(solicitacaoEmpenho);
    }

    public Object[] recuperarIdENumeroDividaPublica(SolicitacaoEmpenho solicitacaoEmpenho) {
        return dividaPublicaFacade.recuperarIdENumeroDividaPublica(solicitacaoEmpenho);
    }

    public Object[] recuperarIdENumeroPropostaConcessaoDiaria(SolicitacaoEmpenho solicitacaoEmpenho, boolean isSuprimentoFundo) {
        return propostaConcessaoDiariaFacade.recuperarIdENumeroPropostaConcessaoDiaria(solicitacaoEmpenho, isSuprimentoFundo);
    }

    public Object[] recuperarIdENumeroConvenioDespesa(SolicitacaoEmpenho solicitacaoEmpenho) {
        return convenioDespesaFacade.recuperarIdENumeroConvenioDespesa(solicitacaoEmpenho);
    }

    public boolean hasConfiguracaoExcecaoVigente(Date dataOperacao, Conta contaDespesa) {
        return configuracaoExcecaoDespesaContratoFacade.hasConfiguracaoVigente(dataOperacao, contaDespesa);
    }

    public NotaOrcamentariaFacade getNotaOrcamentariaFacade() {
        return notaOrcamentariaFacade;
    }

    public List<NotaExecucaoOrcamentaria> buscarNotaEmpenho(String condicao, CategoriaOrcamentaria categoriaOrcamentaria, String nomeDaNota) {
        String sql = " SELECT " + " EMP.numero||'/'||exerc.ano as numero, " + " emp.dataempenho as data_EMPENHO, " + " EMP.COMPLEMENTOHISTORICO as historico_emp, " + " emp.tipoempenho as tipo, " + " TRIM(HO_ORGAO.CODIGO) AS CD_ORGAO, " + " TRIM(ho_orgao.DESCRICAO) AS DESC_ORAGAO, " + " TRIM(HO_UNIDADE.CODIGO) AS CD_UNIDADE, " + " TRIM(HO_UNIDADE.DESCRICAO) AS DESC_UNIDADE, " + " f.codigo || '.' || sf.codigo || '.' || p.codigo || '.' || tpa.codigo || a.codigo || '.' || sub.codigo as CD_PROG_TRABALHO, " + " ct_desp.codigo as elemento, " + " ct_desp.descricao as especificao_despesa, " + " coalesce(pf.nome, pj.razaosocial) as nome_pessoa, " + " formatacpfcnpj(coalesce(pf.cpf, pj.cnpj)) as cpf_cnpj, " + " fonte_r.codigo || ' - ' || trim(fonte_r.descricao)  || ' (' || cdest.codigo || ')' as desc_destinacao, " + " EMP.valor as valor, " + " RPAD(FRT_EXTENSO_MONETARIO(EMP.VALOR)||' ',600,'* ') AS VALOR_EXTENSO, " + " COALESCE(ENDERECO.LOGRADOURO,'Pessoa não possui Logradouro') AS LOGRADOURO, " + " coalesce(endereco.localidade,'Pessoa não possui Localidade') as localidade, " + " A.DESCRICAO AS DESC_ACAO, " + " coalesce(lic.numero, disp.numeroDaDispensa, regext.numeromodalidade)  || ' - Processo Licitatório ' || coalesce(lic.numerolicitacao, disp.numerodadispensa, regext.numeroregistro) || ' - ' || to_char(coalesce(lic.emitidaem, disp.datadadispensa, regext.dataregistrocarona),'dd/MM/YYYY') as modalidade, " + " emp.MODALIDADELICITACAO as MODALIDADELICITACAO, " + " classe.codigo || ' - ' || classe.descricao as desc_classepessoa, " + " COALESCE(EMP.SALDODISPONIVEL,0) AS SALDO_ANTERIOR, " + " COALESCE(EMP.SALDODISPONIVEL,0)- EMP.VALOR AS SALDO_POSTERIOR, " + " COALESCE(endereco.bairro,'Pessoa não possui bairro cadastrado') as bairro, " + " COALESCE(ENDERECO.uf,'sem UF ') AS UF, " + " COALESCE(SUBSTR(ENDERECO.CEP,1,5)||'-'||SUBSTR(ENDERECO.CEP,6,3),'sem CEP') AS CEP," + " HO_UNIDADE.subordinada_id, " + " emp.id as idNota " + " FROM empenho EMP " + (categoriaOrcamentaria.isResto() ? " inner join empenho emp_original on emp.empenho_id = emp_original.id " : "") + " INNER JOIN fontedespesaorc FONTE ON EMP.fontedespesaorc_id = fonte.id " + " INNER JOIN despesaorc DESPESA ON fonte.despesaorc_id = despesa.id " + " INNER JOIN provisaoppadespesa PROVISAO ON despesa.provisaoppadespesa_id= provisao.id " + " INNER JOIN SUBACAOPPA SUB ON SUB.ID = provisao.subacaoppa_id " + " inner join provisaoppafonte ppf on ppf.id = fonte.PROVISAOPPAFONTE_ID " + " INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id " + " INNER JOIN TIPOACAOPPA TPA ON TPA.ID = A.TIPOACAOPPA_ID " + " INNER JOIN programappa P ON P.ID = A.programa_id " + " INNER JOIN FUNCAO F ON F.ID = A.funcao_id " + " INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id " + " INNER JOIN CONTA CT_DESP ON provisao.contadedespesa_id = CT_DESP.ID " + " inner join exercicio exerc on emp.exercicio_id = exerc.id " + " INNER JOIN vwhierarquiaorcamentaria HO_UNIDADE ON emp.unidadeorganizacional_id = ho_unidade.subordinada_id " + " INNER JOIN vwhierarquiaorcamentaria HO_ORGAO ON ho_unidade.superior_id = ho_orgao.subordinada_id " + " INNER JOIN PESSOA Pes ON emp.fornecedor_id = Pes.id " + " left join pessoafisica pf on pes.id = pf.id " + " left join pessoajuridica pj on pes.id = pj.id " + " left join enderecocorreio endereco on pes.enderecoprincipal_id = endereco.id " + " left join contadedestinacao cd on cd.id = ppf.destinacaoderecursos_id " + " left join conta cdest on cdest.id = cd.id " + " left join fontederecursos fonte_r on fonte_r.id = cd.fontederecursos_id " + (categoriaOrcamentaria.isResto() ? " and fonte_r.exercicio_id = emp_original.exercicio_id " : " and fonte_r.exercicio_id = exerc.id ") + " left join contrato contrato on emp.contrato_id = contrato.id " + " left join conlicitacao conlic on conlic.contrato_id = contrato.id " + " left join licitacao lic on lic.id = conlic.licitacao_id " + " left join condispensalicitacao conDisp on condisp.contrato_id = contrato.id " + " left join dispensadelicitacao disp on disp.id = condisp.dispensadelicitacao_id " + " left join CONREGPRECOEXTERNO conReg on conreg.contrato_id = contrato.id " + " left join REGISTROSOLMATEXT regExt on regExt.id = conreg.regsolmatext_id " + " left join classecredor classe on emp.classecredor_id = classe.id " + " where trunc(emp.dataempenho) between ho_unidade.iniciovigencia and coalesce(ho_unidade.fimvigencia, trunc(emp.dataempenho)) " + "   and trunc(emp.dataempenho) between ho_orgao.iniciovigencia and coalesce(ho_orgao.fimvigencia, trunc(emp.dataempenho)) " + "   and emp.categoriaOrcamentaria = :categoriaOrcamentaria " + condicao;
        Query q = em.createNativeQuery(sql);
        q.setParameter("categoriaOrcamentaria", categoriaOrcamentaria.name());
        List<Object[]> resultado = q.getResultList();
        List<NotaExecucaoOrcamentaria> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                NotaExecucaoOrcamentaria nota = new NotaExecucaoOrcamentaria();
                nota.setNomeDaNota(nomeDaNota);
                nota.setNumeroEmpenho((String) obj[0]);
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
                nota.setSaldoAnterior((BigDecimal) obj[22]);
                nota.setSaldoAtual((BigDecimal) obj[23]);
                nota.setBairro((String) obj[24]);
                nota.setUf((String) obj[25]);
                nota.setCep((String) obj[26]);
                nota.setIdUnidadeOrcamentaria((BigDecimal) obj[27]);
                nota.setId(((BigDecimal) obj[28]).longValue());
                retorno.add(nota);
            }
        }
        return retorno;
    }

    public BigDecimal buscarValorEmpenhadoPorTipoObjetoCompra(Contrato contrato, TipoObjetoCompra tipoObjetoCompra, CategoriaOrcamentaria categoria) {
        TipoContaDespesa tipoConta = TipoContaDespesa.getContaDespesaPorTipoObjetoCompra(tipoObjetoCompra);
        if (tipoConta == null) {
            throw new ExcecaoNegocioGenerica("Tipo conta de despesa não encontratda para o tipo de objeto de compra: " + tipoObjetoCompra.getDescricao());
        }
        String sql = " select coalesce(sum(emp.valor),0) from empenho emp " + "           where emp.contrato_id = :idContrato " + "           and emp.tipocontadespesa = :tipoContaDespesa " + "           and emp.categoriaorcamentaria = :categoriaNormal ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idContrato", contrato.getId());
        q.setParameter("categoriaNormal", categoria.name());
        q.setParameter("tipoContaDespesa", tipoConta.name());
        return q.getResultList().isEmpty() ? BigDecimal.ZERO : (BigDecimal) q.getSingleResult();
    }

    public List<TipoContaDespesa> buscarTiposContaDespesaExecucaoEmpenho(Long idExecucao) {
        String sql = " select distinct emp.tipocontadespesa from empenho emp " +
            "           left join execucaoprocessoempenho exproc on exproc.empenho_id = emp.id " +
            "           left join execucaocontratoempenho excont on excont.empenho_id = emp.id " +
            "           left join reconhecimentodivida rd on rd.id = emp.reconhecimentodivida_id " +
            "          where coalesce(exproc.execucaoprocesso_id, excont.execucaocontrato_id, rd.id) = :idExecucao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idExecucao", idExecucao);
        List<String> resultado = q.getResultList();
        if (Util.isListNullOrEmpty(q.getResultList())) {
            return Lists.newArrayList();
        }
        Set<TipoContaDespesa> toReturn = Sets.newHashSet();
        Arrays.stream(TipoContaDespesa.values()).forEach(tipoContaDespesa -> {
            resultado.stream()
                .filter(lista -> tipoContaDespesa.name().equals(lista))
                .map(lista -> tipoContaDespesa)
                .forEach(toReturn::add);
        });
        return Lists.newArrayList(toReturn);
    }

    public boolean isEmpenhoTotalmenteEstornado(Empenho empenho) {
        String sql = " select 1 from empenho emp " +
            " left join empenhoestorno est on emp.id = est.empenho_id " +
            " where emp.id = :idEmpenho " +
            " and emp.valor = (select coalesce(sum(est.valor),0) from empenhoestorno est where est.empenho_id = emp.id) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idEmpenho", empenho.getId());
        return !q.getResultList().isEmpty();
    }

    public ReconhecimentoDividaFacade getReconhecimentoDividaFacade() {
        return reconhecimentoDividaFacade;
    }
}

