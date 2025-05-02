/*
 * Codigo gerado automaticamente em Mon Sep 26 15:49:23 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class ReceitaLOAFacade extends AbstractFacade<ReceitaLOA> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private LOAFacade loaFacade;
    @EJB
    private ContaFacade contaDeReceitaFacade;
    @EJB
    private HierarquiaOrganizacionalFacadeOLD hierarquiaOrganizacionalFacade;
    @EJB
    private ConfiguracaoPlanejamentoPublicoFacade configuracaoPlanejamentoPublicoFacade;
    @EJB
    private SaldoReceitaORCFacade saldoReceitaORCFacade;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrgResponsavelFacade;
    @EJB
    private SubContaFacade subContaFacade;
    @EJB
    private ConfigReceitaRealizadaFacade configReceitaRealizadaFacade;

    public ReceitaLOAFacade() {
        super(ReceitaLOA.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FonteDeRecursosFacade getFonteDeRecursosFacade() {
        return fonteDeRecursosFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrgResponsavelFacade() {
        return hierarquiaOrgResponsavelFacade;
    }

    public List<LOA> listaLoas() {
        Query q = em.createQuery("from LOA");
        return q.getResultList();
    }

    public List<ReceitaLOA> recuperarReceitas(LOA loa) {
        Query q = em.createQuery("from ReceitaLOA r where r.loa = :l");
        q.setParameter("l", loa);
        return q.getResultList();
    }

    /*
     *
     * Meto que filtra ReceitaLOA para auto compelte através dos campos
     * Descrição e Código da Conta de Receita 10/11/2010
     *
     */
    public List<ReceitaLOA> filtraReceitaLoaPorDescricaoCodExercDaConta(String parte, Exercicio ex, UnidadeOrganizacional unidadeOrganizacional) {
        StringBuilder sql = new StringBuilder();

        sql.append(" select rl.*  from ReceitaLOA rl  ");
        sql.append(" inner join Conta ct on rl.CONTADERECEITA_ID = ct.ID  ");
        sql.append(" inner join LOA loa on rl.LOA_ID = loa.ID  ");
        sql.append(" inner join LDO ldo on loa.LDO_ID = ldo.id and ldo.EXERCICIO_ID = :exercParam  ");
        sql.append(" where ((upper(ct.descricao) like :param) or (upper(replace(ct.codigo,'.','')) like :param))  ");
        sql.append(" and rl.entidade_id = :idUnid ");
        Query q = getEntityManager().createNativeQuery(sql.toString(), ReceitaLOA.class);
        q.setParameter("param", "%" + parte.toUpperCase().trim() + "%");
        q.setParameter("exercParam", ex.getId());
        q.setParameter("idUnid", unidadeOrganizacional.getId());
        return q.getResultList();
    }

    public List<ReceitaLOA> listaReceitaLoaPorExercicioUnidadesUsuarioLogado(String parte, Exercicio ex, UsuarioSistema usuario, Date dataOperacao) {
        String sql = " SELECT RL.*  FROM RECEITALOA RL  " +
            " INNER JOIN CONTA CT ON RL.CONTADERECEITA_ID = CT.ID  " +
            " INNER JOIN LOA LOA ON RL.LOA_ID = LOA.ID " +
            " INNER JOIN LDO LDO ON LOA.LDO_ID = LDO.ID " +
            "      AND LDO.EXERCICIO_ID = :idExercicio" +
            " INNER JOIN UNIDADEORGANIZACIONAL UNID ON UNID.ID = RL.ENTIDADE_ID " +
            " INNER JOIN VWHIERARQUIAORCAMENTARIA VWORG ON VWORG.SUBORDINADA_ID = UNID.ID  " +
            "      AND TO_DATE(:dataOperacao, 'dd/MM/yyyy') BETWEEN VWORG.INICIOVIGENCIA AND COALESCE(VWORG.FIMVIGENCIA, TO_DATE(:dataOperacao, 'dd/MM/yyyy')) " +
            " INNER JOIN USUARIOUNIDADEORGANIZACORC UU ON UU.UNIDADEORGANIZACIONAL_ID = VWORG.SUBORDINADA_ID " +
            " INNER JOIN USUARIOSISTEMA USU ON USU.ID = UU.USUARIOSISTEMA_ID " +
            " WHERE ((UPPER(CT.DESCRICAO) LIKE :paramDesc) OR (UPPER(REPLACE(CT.CODIGO, '.','')) LIKE :paramCod)) " +
            " AND USU.ID = :idUsuario ";

        Query q = getEntityManager().createNativeQuery(sql, ReceitaLOA.class);
        q.setParameter("paramDesc", "%" + parte.toUpperCase().trim() + "%");
        q.setParameter("paramCod", parte.trim() + "%");
        q.setParameter("idExercicio", ex.getId());
        q.setParameter("idUsuario", usuario.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        return q.getResultList();
    }

    @Override
    public ReceitaLOA recuperar(Object id) {
        ReceitaLOA rl = em.find(ReceitaLOA.class, id);
        rl.getReceitaLoaFontes().size();
        rl.getPrevisaoReceitaOrc().size();
        return rl;
    }

    @Override
    public ReceitaLOA recarregar(ReceitaLOA entity) {
        if (entity != null) {
            entity = em.find(ReceitaLOA.class, entity.getId());
            entity.getPrevisaoReceitaOrc().size();
            entity.getReceitaLoaFontes().size();
        }
        return entity;
    }

    public void geraPrevisoesReceitaOrc(ReceitaLOA rl) {
        if (rl.getPrevisaoReceitaOrc().isEmpty()) {
            Integer num = 1;
            for (Mes m : Mes.values()) {
                PrevisaoReceitaOrc prev = new PrevisaoReceitaOrc();
                prev.setMes(m);
                prev.setIndice(num);
                prev.setReceitaLOA(rl);
                rl.getPrevisaoReceitaOrc().add(prev);
                num = num + 1;
            }
        }
    }

    public List<ReceitaLOA> listaContaDeReceitaExercLOA(String parte, Exercicio ex) {
        StringBuilder sql = new StringBuilder();
        List<ReceitaLOA> retorno = new ArrayList<>();
        sql.append(" selectrec.*  ");
        sql.append("    from receitaloa rec ");
        sql.append("   inner join ldo ld on ld.exercicio_id = :exercParam ");
        sql.append("   inner join loa lo on lo.id = rec.loa_id and lo.ldo_id = ld.id  ");
        sql.append("   inner join conta con on con.id = rec.contadereceita_id  ");
        sql.append(" where (upper(con.descricao) like :param ) or (con.codigo like :param) or (rec.codigoreduzido like :param)");
        parte = "%" + parte.toUpperCase().trim() + "%";
        Query q = getEntityManager().createNativeQuery(sql.toString(), ReceitaLOA.class);
        q.setParameter("param", parte);
        q.setParameter("exercParam", ex.getId());
        if (q.getResultList() != null && q.getResultList().size() > 0) {
            retorno = q.getResultList();
        }
        return retorno;

    }

    public List<ReceitaLOA> buscarContaReceitaPorUnidadeExercicioAndOperacaoReceita(String parte, Exercicio ex, UnidadeOrganizacional und, OperacaoReceita operacaoReceita) {
        StringBuilder sql = new StringBuilder();
        List<ReceitaLOA> retorno = new ArrayList<>();
        sql.append("SELECT RL.* ");
        sql.append("FROM RECEITALOA RL ");
        sql.append("INNER JOIN LOA ON RL.LOA_ID = LOA.ID ");
        sql.append("INNER JOIN LDO ON LOA.LDO_ID = LDO.ID AND LDO.EXERCICIO_ID = :ex ");
        sql.append("INNER JOIN CONTA C ON RL.CONTADERECEITA_ID = C.ID ");
        sql.append("INNER JOIN CONTARECEITA CR ON C.ID = CR.ID AND C.DTYPE = 'ContaReceita' ");
        sql.append("INNER JOIN PLANODECONTAS PC ON C.PLANODECONTAS_ID = PC.ID ");
        sql.append("INNER JOIN PLANODECONTASEXERCICIO PCE ON PCE.PLANODERECEITAS_ID = PC.ID AND PCE.EXERCICIO_ID = :ex ");
        sql.append("INNER JOIN UNIDADEORGANIZACIONAL UND ON RL.ENTIDADE_ID = UND.ID ");
        sql.append("WHERE (REPLACE(C.CODIGO, '.', '') LIKE :parte ");
        sql.append("         OR C.CODIGO LIKE :parte ");
        sql.append("         OR LOWER(C.DESCRICAO) LIKE :parte ");
        sql.append("         OR CR.CODIGOREDUZIDO LIKE :parte) ");
        sql.append(" and UND.ID = :und ");
        sql.append(" and rl.operacaoReceita = :operacaoReceita ");
        sql.append(" ORDER BY C.CODIGO ");
        Query q = getEntityManager().createNativeQuery(sql.toString(), ReceitaLOA.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("ex", ex.getId());
        q.setParameter("und", und.getId());
        q.setParameter("operacaoReceita", operacaoReceita.name());
        if (q.getResultList() != null && q.getResultList().size() > 0) {
            retorno = q.getResultList();
        }
        return retorno;
    }

    public List<ReceitaLOA> listaReceitaLOAPorExercicioUnidades(Exercicio ex, List<HierarquiaOrganizacional> listaUnidades) {
        StringBuilder sql = new StringBuilder();
        List<ReceitaLOA> retorno = new ArrayList<>();
        sql.append("SELECT RL.* ");
        sql.append("FROM RECEITALOA RL ");
        sql.append("INNER JOIN LOA ON RL.LOA_ID = LOA.ID ");
        sql.append("INNER JOIN LDO ON LOA.LDO_ID = LDO.ID AND LDO.EXERCICIO_ID = :ex ");
        sql.append("INNER JOIN CONTA C ON RL.CONTADERECEITA_ID = C.ID ");
        sql.append("INNER JOIN CONTARECEITA CR ON C.ID = CR.ID AND C.DTYPE = 'ContaReceita' ");
        sql.append("INNER JOIN PLANODECONTAS PC ON C.PLANODECONTAS_ID = PC.ID ");
        sql.append("INNER JOIN PLANODECONTASEXERCICIO PCE ON PCE.PLANODERECEITAS_ID = PC.ID AND PCE.EXERCICIO_ID = :ex ");
        sql.append("INNER JOIN UNIDADEORGANIZACIONAL UND ON RL.ENTIDADE_ID = UND.ID ");
        sql.append("WHERE UND.ID in ( :unidades ) ");
        sql.append(" ORDER BY C.CODIGO ");
        Query q = getEntityManager().createNativeQuery(sql.toString(), ReceitaLOA.class);
        q.setParameter("ex", ex.getId());

        List<Long> unidades = Lists.newArrayList();
        for (HierarquiaOrganizacional lista : listaUnidades) {
            unidades.add(lista.getSubordinada().getId());
        }
        q.setParameter("unidades", unidades);
        if (q.getResultList() != null && q.getResultList().size() > 0) {
            retorno = q.getResultList();
        }
        return retorno;
    }

    public Boolean verificaCodigoReceita(ReceitaLOA receitaLOA) {

        String sql = "SELECT rl.* FROM receitaloa rl "
            + "INNER JOIN loa loa ON rl.loa_id = loa.id "
            + "INNER JOIN ldo ldo ON loa.ldo_id = ldo.id " +
            "  INNER JOIN CONTA CONTA ON rl.contadereceita_id = conta.id"
            + " WHERE ldo.exercicio_id = :exer " +
            "   AND rl.entidade_id = :uo" +
            "   AND conta.codigo = :cod" +
            "   AND rl.operacaoReceita = :operacaoReceita";
        if (receitaLOA.getId() != null) {
            sql += " and rl.id <> :id";
        }
        Query q = getEntityManager().createNativeQuery(sql, ReceitaLOA.class);
        q.setParameter("cod", receitaLOA.getContaDeReceita().getCodigo());
        q.setParameter("exer", receitaLOA.getLoa().getLdo().getExercicio().getId());
        q.setParameter("uo", receitaLOA.getEntidade().getId());
        q.setParameter("operacaoReceita", receitaLOA.getOperacaoReceita().name());
        if (receitaLOA.getId() != null) {
            q.setParameter("id", receitaLOA.getId());
        }
        if (q.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public List<ReceitaLOA> listaContaDeReceitaExercLOA() {
        StringBuilder sql = new StringBuilder();

        sql.append(" select rec.*  from RECEITALOA rec INNER JOIN LDO ld on ld.EXERCICIO_ID = :exercParam ");
        sql.append(" INNER JOIN LOA lo on lo.ID = rec.LOA_ID and lo.LDO_ID = ld.ID  ");
        Query q = getEntityManager().createNativeQuery(sql.toString(), ReceitaLOA.class);
        q.setParameter("exercParam", sistemaFacade.getExercicioCorrente().getId());

        return q.getResultList();
    }

    public List<PrevisaoReceitaOrc> listaPrevisoesReceitaOrc(ReceitaLOA rl) {
        String hql = "from PrevisaoReceitaOrc where receitaLOA = :param";
        Query q = em.createQuery(hql);
        q.setParameter("param", rl);
        if (q.getResultList().isEmpty()) {
            return new ArrayList<PrevisaoReceitaOrc>();
        } else {
            return q.getResultList();
        }
    }

    public List<LOA> listaPorExercicio(Exercicio ex) {
        String sql = "SELECT l.* FROM loa l INNER JOIN ldo ld ON l.ldo_id = ld.id INNER JOIN exercicio exe ON ld.exercicio_id = exe.id WHERE exe.ano = :ano";
        Query q = em.createNativeQuery(sql, LOA.class);
        q.setParameter("ano", ex.getAno());
        if (q.getResultList().isEmpty()) {
            return new ArrayList<LOA>();
        } else {
            return q.getResultList();
        }
    }

    public List<ReceitaLOA> listaPorCodigoReduzidoCodigoDescricao(String parte, TiposCredito tipoCredito) {
        StringBuilder sql = new StringBuilder();
        sql.append("select distinct rl.* from receitaloa rl ");
        sql.append(" inner join conta conta on conta.id = rl.contadereceita_id ");
        sql.append(" inner join contareceita cr on cr.id = conta.id ");
        sql.append(" and conta.dtype = 'ContaReceita' ");
        sql.append(" and (conta.codigo like :parte ");
        sql.append(" or conta.descricao like :parte ");
        sql.append(" or rl.codigoReduzido like :parte ) ");

        if (tipoCredito != null) {
            sql.append(" and cr.tiposcredito = :tipoCredito ");
        }

        Query q = em.createNativeQuery(sql.toString(), ReceitaLOA.class);
        q.setParameter("parte", "%" + parte.trim() + "%");
        if (tipoCredito != null) {
            q.setParameter("tipoCredito", tipoCredito.name());
        }
        if (q.getResultList() != null) {
            return q.getResultList();
        }
        return new ArrayList<ReceitaLOA>();
    }

    public List<ReceitaLOA> listaPorCodigoReduzidoCodigoDescricaoDivida(String parte, Exercicio exercicio, UnidadeOrganizacional unidade) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select rl.* from receitaloa rl ");
        sql.append(" inner join loa loa on rl.loa_id = loa.id ");
        sql.append(" inner join ldo ldo on loa.ldo_id = ldo.id ");
        sql.append(" inner join conta conta on conta.id = rl.contadereceita_id ");
        sql.append(" inner join contareceita cr on cr.id = conta.id ");
        sql.append(" and conta.dtype = 'ContaReceita' ");
        sql.append(" and ldo.exercicio_id = :ex ");
        sql.append(" and rl.entidade_id = :unidade ");
        sql.append(" and (replace(conta.codigo, '.', '') like :parteCodigo ");
        sql.append(" or lower(conta.descricao) like :parte ");
        sql.append(" or rl.codigoReduzido like :parte ) ");
        sql.append(" and cr.tiposcredito in ('DIVIDA_ATIVA_TRIBUTARIA','DIVIDA_ATIVA_NAO_TRIBUTARIA_CLIENTES','DIVIDA_ATIVA_NAO_TRIBUTARIA_DEMAIS') ");

        Query q = em.createNativeQuery(sql.toString(), ReceitaLOA.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("parteCodigo", parte.trim().toLowerCase().replace(".", "") + "%");
        q.setParameter("ex", exercicio.getId());
        q.setParameter("unidade", unidade.getId());
        if (q.getResultList() != null) {
            return q.getResultList();
        }
        return new ArrayList<ReceitaLOA>();
    }

    public List<ReceitaLOA> buscarReceitasLoaPorCodigoReduzidoCodigoDescricaoCredito(String parte, Exercicio ex, UnidadeOrganizacional unidade, OperacaoReceita operacaoReceita) {
        StringBuilder sql = new StringBuilder();
        sql.append("select distinct rl.* from receitaloa rl ");
        sql.append(" inner join loa loa on rl.loa_id = loa.id ");
        sql.append(" inner join ldo ldo on loa.ldo_id = ldo.id ");
        sql.append(" inner join conta conta on conta.id = rl.contadereceita_id ");
        sql.append(" inner join contareceita cr on cr.id = conta.id ");
        sql.append(" where conta.dtype = 'ContaReceita' ");
        sql.append(" and ldo.exercicio_id = :ex ");
        sql.append(" and rl.entidade_id = :unidade ");
        sql.append(" and rl.operacaoReceita = :operacao ");
        sql.append(" and (replace(conta.codigo, '.', '') like :parteCodigo ");
        sql.append(" or lower(conta.descricao) like :parte ");
        sql.append(" or rl.codigoReduzido like :parte  ");
        sql.append(" or cr.codigoReduzido like :parte  ");
        sql.append(" or cr.descricaoReduzida like :parte ) ");
        sql.append(" and cr.tiposcredito in ('CREDITOS_CLIENTES_A_RECEBER','CREDITOS_TRIBUTARIOS_A_RECEBER','CREDITOS_TRANSFERENCIAS_A_RECEBER','CREDITOS_NAO_TRIBUTARIOS_A_RECEBER','CREDITOS_DIVERSOS_A_RECEBER') ");

        Query q = em.createNativeQuery(sql.toString(), ReceitaLOA.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("parteCodigo", parte.trim().toLowerCase().replace(".", "") + "%");
        q.setParameter("ex", ex.getId());
        q.setParameter("unidade", unidade.getId());
        q.setParameter("operacao", operacaoReceita.name());
        q.setMaxResults(MAX_RESULTADOS_NO_AUTOCOMPLATE);
        if (q.getResultList() != null) {
            return q.getResultList();
        }
        return new ArrayList<ReceitaLOA>();
    }

    public SaldoReceitaORCFacade getSaldoReceitaORCFacade() {
        return saldoReceitaORCFacade;
    }

    public List<ReceitaLOAFonte> listaReceitaLoaFontes(ReceitaLOA receitaLOA) {
        String sql = "SELECT f.* FROM ReceitaLOAFonte f WHERE f.receitaloa_id = :id ";
        Query q = em.createNativeQuery(sql, ReceitaLOAFonte.class);
        q.setParameter("id", receitaLOA.getId());
        if (q.getResultList() != null) {
            return q.getResultList();
        }
        return new ArrayList<ReceitaLOAFonte>();
    }

    public void salvarReceitaLoa(ReceitaLOA r) throws ExcecaoNegocioGenerica {
        try {
            em.merge(r);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Erro " + e.getMessage());
        }

    }

    public ReceitaLOA receitaPorContaUnidadeExercicio(ContaReceita conta, UnidadeOrganizacional unidade, Exercicio exercicio, SubConta contaFinanceira, OperacaoReceita operacaoReceitaRealizada, FonteDeRecursos fonteDeRecursos) {

        try {
            String sql = " select rl.* from receitaloa rl "
                + " inner join loa loa on loa.id = rl.loa_id "
                + " inner join ldo ldo on ldo.id = loa.ldo_id "
                + " where rl.contadereceita_id = :conta "
                + " and rl.entidade_id = :unidade "
                + " and ldo.exercicio_id = :exercicio "
                + " and rl.operacaoReceita = :operacao ";

            Query q = em.createNativeQuery(sql, ReceitaLOA.class)
                .setParameter("unidade", unidade.getId())
                .setParameter("conta", conta.getId())
                .setParameter("exercicio", exercicio.getId())
                .setParameter("operacao", operacaoReceitaRealizada.name());

            if (!q.getResultList().isEmpty()) {
                return (ReceitaLOA) q.getResultList().get(0);

            } else {
                contaFinanceira = subContaFacade.recuperar(contaFinanceira.getId());
                List<FonteDeRecursos> fontesContaFinanceira = recuperarFontesDaSubContaIntegracao(contaFinanceira, exercicio);

                if (contaFinanceira == null) {
                    throw new ExcecaoNegocioGenerica("Não foi encontrado nenhuma Receita com a conta " + conta + " para a Unidade " + unidade + " no Orçamento de " + exercicio + ".");
                } else {
                    if (fontesContaFinanceira.isEmpty()) {
                        throw new ExcecaoNegocioGenerica("Não foi possível encontrar nenhuma Fonte de Recurso para "
                            + exercicio.getAno() + " na Conta Financeira " + contaFinanceira.toStringAutoCompleteContaFinanceira()
                            + " ao tentar criar a Receita Orçamentária para a Unidade " + unidade.getDescricao() + " no orçamento de " + exercicio + ".");
                    }
                    if (fontesContaFinanceira.contains(fonteDeRecursos)) {
                        return criarReceitaLOA(conta, unidade, exercicio, operacaoReceitaRealizada, fontesContaFinanceira.get(0));
                    } else {
                        throw new ExcecaoNegocioGenerica("Existe mais de uma Fonte de Recurso para o " + exercicio + " na conta financeira " + contaFinanceira.toStringAutoCompleteContaFinanceira() + "."
                            + " Nesta situação, é preciso que seja criado a Receita Loa (Conta de Receita) no Orçamento de " + exercicio.getAno() + " na Unidade " + unidade.toString() + ".");
                    }
                }
            }
        } catch (NonUniqueResultException ex) {
            throw new ExcecaoNegocioGenerica("Existe mais de uma receita loa com a conta : " + conta + " para a unidade " + unidade + " no orçamento de " + exercicio + ".");
        }
    }

    private ReceitaLOA criarReceitaLOA(ContaReceita conta, UnidadeOrganizacional unidade, Exercicio exercicio, OperacaoReceita operacaoReceita, FonteDeRecursos fonteDeRecursos) {

        LOA loa = loaFacade.listaUltimaLoaPorExercicio(exercicio);
        if (!loa.getLdo().getExercicio().equals(exercicio)) {
            throw new ExcecaoNegocioGenerica(" O Exercicio da LOA " + loa.toString() + " é diferente de " + exercicio.getAno() + ".");
        }
        ReceitaLOA receitaLOA = new ReceitaLOA();
        receitaLOA.setContaDeReceita(conta);
        receitaLOA.setValor(BigDecimal.ZERO);
        receitaLOA.setDataRegistro(new Date());
        receitaLOA.setEntidade(unidade);
        receitaLOA.setLoa(loa);
        receitaLOA.setCodigoReduzido("");
        receitaLOA.setSaldo(BigDecimal.ZERO);
        receitaLOA.setReceitaLoaFontes(new ArrayList<ReceitaLOAFonte>());
        receitaLOA.setOperacaoReceita(operacaoReceita);

        ReceitaLOAFonte receitaLOAFonte = new ReceitaLOAFonte();
        receitaLOAFonte.setDataRegistro(new Date());
        receitaLOAFonte.setValor(BigDecimal.ZERO);
        receitaLOAFonte.setReceitaLOA(receitaLOA);
        receitaLOAFonte.setDestinacaoDeRecursos(contaDeReceitaFacade.recuperarContaDestinacaoPorFonte(fonteDeRecursos));
        receitaLOAFonte.setEsferaOrcamentaria(EsferaOrcamentaria.ORCAMENTOGERAL);
        receitaLOAFonte.setPercentual(new BigDecimal(100));
        receitaLOAFonte.setRounding(true);
        receitaLOA.getReceitaLoaFontes().add(receitaLOAFonte);


        salvarNoticacaoNovaReceitaLoa(unidade, conta, receitaLOA.getOperacaoReceita());
        return em.merge(receitaLOA);

    }

    public void salvarNoticacaoNovaReceitaLoa(UnidadeOrganizacional unidadeOrganizacional, Conta conta, OperacaoReceita operacaoReceita) {

        String url = "";
        ConfigReceitaRealizada configReceitaRealizada = configReceitaRealizadaFacade.buscarEventoPorContaReceita(conta,
            TipoLancamento.NORMAL, sistemaFacade.getDataOperacao(), operacaoReceita);
        if (configReceitaRealizada != null) {
            url = "/configuracao-receita-realizada/novo/";
        } else {
            url = "";
        }
        Notificacao notificacao = new Notificacao();
        notificacao.setTitulo("Criado Receita Loa no Orçamento");
        notificacao.setDescricao("Foi criado automaticamente a receita " + conta.getCodigo() + " no orçamento da unidade: " + unidadeOrganizacional + ". Crie/verifique as configurações contábeis (Configuração de Evento Contábil e Origem Conta Contábil) necessárias para integração. ");
        notificacao.setGravidade(Notificacao.Gravidade.INFORMACAO);
        notificacao.setTipoNotificacao(TipoNotificacao.CONFIGURACAO_CONTABIL);
        notificacao.setLink(url);
        NotificacaoService.getService().notificar(notificacao);
    }

    public List<FonteDeRecursos> recuperarFonteDeRecursoPorExercicio(Exercicio exercicio, SubConta contaFinanceira) {
        List<FonteDeRecursos> fontes = new ArrayList<FonteDeRecursos>();
        contaFinanceira = em.find(SubConta.class, contaFinanceira.getId());
        for (SubContaFonteRec subContaFonteRec : contaFinanceira.getSubContaFonteRecs()) {
            if (subContaFonteRec.getFonteDeRecursos().getExercicio().equals(exercicio)) {
                fontes.add(subContaFonteRec.getFonteDeRecursos());
            }
        }
        return fontes;
    }

    public ReceitaLOAFonte recuperarReceitaLoaFontePorExercicio(ReceitaORCEstorno l) {
        ReceitaLOA rec = em.find(ReceitaLOA.class, l.getReceitaLOA().getId());
        for (ReceitaLOAFonte fontes : rec.getReceitaLoaFontes()) {
            if (fontes.getDestinacaoDeRecursos().getFonteDeRecursos().equals(recuperarFonteDeRecursoPorExercicio(l.getExercicio(), l.getContaFinanceira()).get(0))) {
                return fontes;
            }
        }
        return null;
    }

    public ReceitaLOAFonte recuperarReceitaLoaFontePorExercicio(LancamentoReceitaOrc l) {
        ReceitaLOA rec = em.find(ReceitaLOA.class, l.getReceitaLOA().getId());
        for (ReceitaLOAFonte fontes : rec.getReceitaLoaFontes()) {
            if (fontes.getDestinacaoDeRecursos().getFonteDeRecursos().equals(recuperarFonteDeRecursoPorExercicio(l.getExercicio(), l.getSubConta()).get(0))) {
                return fontes;
            }
        }
        return null;
    }

    public List<FonteDeRecursos> recuperarFonteDeRecursoPorConjuntoDeFonte(ReceitaLOA receitaLOA, ReceitaLOAFonte receitaLOAFonte) {

        ReceitaLOA rl = recuperar(receitaLOA.getId());

        List<FonteDeRecursos> fontes = new ArrayList<FonteDeRecursos>();
        for (ReceitaLOAFonte loaFonte : rl.getReceitaLoaFontes()) {
            if (loaFonte.getItem().equals(receitaLOAFonte.getItem())) {
                fontes.add(loaFonte.getDestinacaoDeRecursos().getFonteDeRecursos());
            }
        }
        return fontes;
    }

    public List<BigDecimal> recuperarConjuntoDeFontePorReceitaLoa(ReceitaLOA receitaLOA, Exercicio exercicio) {
        try {
            String sql = "select rlf.item " +
                " from receitaloafonte rlf " +
                " inner join receitaloa rec on rec.id = rlf.receitaloa_id " +
                " inner join loa l on l.id = rec.loa_id " +
                " inner join ldo ldo on ldo.id = l.ldo_id " +
                " inner join conta c on c.id = rec.contadereceita_id " +
                " where c.codigo like :codigoConta " +
                "  and ldo.exercicio_id = :idExercicio " +
                "  and rec.entidade_id = :idUnidade " +
                " group by rlf.item ";
            Query q = em.createNativeQuery(sql);
            q.setParameter("codigoConta", receitaLOA.getContaDeReceita().getCodigo());
            q.setParameter("idExercicio", exercicio.getId());
            q.setParameter("idUnidade", receitaLOA.getEntidade().getId());
            if (q.getResultList() != null) {
                return (List<BigDecimal>) q.getResultList();
            }
            return new ArrayList<>();
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    private List<FonteDeRecursos> recuperarFontesDaSubContaIntegracao(SubConta contaFinanceira, Exercicio exercicio) {
        List<FonteDeRecursos> listaFonteConta = new ArrayList<FonteDeRecursos>();
        for (SubContaFonteRec sfr : contaFinanceira.getSubContaFonteRecs()) {
            if (exercicio.equals(sfr.getFonteDeRecursos().getExercicio())) {
                listaFonteConta.add(sfr.getFonteDeRecursos());
            }
        }
        return listaFonteConta;
    }

    public String getUltimoNumero(ReceitaLOA receitaLOA) {
        String sql = " select max(to_number(rlf.item)) + 1 as ultimonumero " +
            " from receitaloafonte rlf " +
            " inner join receitaloa rl on rlf.receitaloa_id = rl.id ";
        if (receitaLOA != null) {
            if (receitaLOA.getId() != null) {
                sql += "where rl.id = :idReceitaLoa ";
            }
        }
        Query q = em.createNativeQuery(sql);
        if (receitaLOA != null) {
            if (receitaLOA.getId() != null) {
                q.setParameter("idReceitaLoa", receitaLOA.getId());
            }
        }
        if (q.getSingleResult() == null) {
            return "1";
        }
        return q.getSingleResult().toString();
    }

    public ConfiguracaoPlanejamentoPublicoFacade getConfiguracaoPlanejamentoPublicoFacade() {
        return configuracaoPlanejamentoPublicoFacade;
    }

    public ContaFacade getContaDeReceitaFacade() {
        return contaDeReceitaFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public HierarquiaOrganizacionalFacadeOLD getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public LOAFacade getLoaFacade() {
        return loaFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public List<ReceitaLOAFonte> buscarReceitasLoasFontesPorExercicioEContaDeReceita(Integer anoExercicio, Conta contaDeReceita) {
        String sql = "select fonte.* " +
            " from receitaloafonte fonte " +
            "    inner join receitaloa recloa on recloa.id = fonte.receitaloa_id " +
            "    inner join conta c on c.id = recloa.contadereceita_id " +
            "    inner join loa on loa.id = recloa.loa_id " +
            "    inner join ldo on ldo.id = loa.ldo_id " +
            "    inner join exercicio ex on ex.id = ldo.exercicio_id " +
            " where ex.ano = :anoExercicio  " +
            "   and trim(c.codigo) = :codigoContaDeReceita ";
        Query q = em.createNativeQuery(sql, ReceitaLOAFonte.class);
        q.setParameter("anoExercicio", anoExercicio);
        q.setParameter("codigoContaDeReceita", contaDeReceita.getCodigo().trim());
        return q.getResultList();
    }
}
