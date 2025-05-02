/*
 * Codigo gerado automaticamente em Thu May 05 14:21:20 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.OperacaoORC;
import br.com.webpublico.exception.ValidaLDOExeption;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Stateless
public class ProvisaoPPADespesaFacade extends AbstractFacade<ProvisaoPPADespesa> {

    private static final Logger logger = LoggerFactory.getLogger(ProvisaoPPADespesaFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ParticipanteAcaoPPAFacade participanteAcaoPPAFacade;
    @EJB
    private ProvisaoPPAFacade provisaoPPAFacade;
    @EJB
    private LOAFacade loaFacade;


    public ProvisaoPPADespesaFacade() {
        super(ProvisaoPPADespesa.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public BigDecimal retornaValorDespesas(Exercicio ex, ProvisaoPPA pp, UnidadeOrganizacional uni) {
        BigDecimal valor = BigDecimal.ZERO;
        String sql = "SELECT coalesce (sum(ppd.valor), 0) FROM provisaoppadespesa ppd "
            + "INNER JOIN provisaoppa pp ON ppd.provisaoppa_id = pp.id "
            + "INNER JOIN provisaoppaldo pl ON pp.id = pl.provisaoppa_id "
            + "INNER JOIN ldo ldo ON pl.ldo_id = ldo.id AND ldo.exercicio_id = :ex "
            + "INNER JOIN unidadeorganizacional uni ON uni.id = ppd.unidadeorganizacional_id "
            + "WHERE ppd.unidadeorganizacional_id = :uni "
            + "AND pp.id <> :prov";
        Query q = em.createNativeQuery(sql);
        q.setParameter("ex", ex.getId());
        q.setParameter("prov", pp.getId());
        q.setParameter("uni", uni.getId());
        valor = (BigDecimal) q.getSingleResult();
        return valor;
    }

    public List<ProvisaoPPADespesa> listaProvisaoDispesaPPA(SubAcaoPPA subAcaoPPA) {
        String hql = "select p from ProvisaoPPADespesa p where p.subAcaoPPA= :parametro ";
        Query q = em.createQuery(hql);
        q.setParameter("parametro", subAcaoPPA);
        return q.getResultList();
    }

    public List<ProvisaoPPADespesa> recuperarProvisaoPPADespesaPorSubAcao(SubAcaoPPA subAcaoPPA) {
        String hql = "select p from ProvisaoPPADespesa p where p.subAcaoPPA= :parametro ";
        Query q = em.createQuery(hql);
        q.setParameter("parametro", subAcaoPPA);
        List<ProvisaoPPADespesa> retorno = q.getResultList();
        for (ProvisaoPPADespesa prov : retorno) {
            prov.getProvisaoPPAFontes().size();
        }
        return retorno;
    }

    public List<ProvisaoPPADespesa> listaProvisaoDispesaPPARecuperandoFontes(SubAcaoPPA subAcaoPPA) {
        String hql = "select p from ProvisaoPPADespesa p " +
            " inner join fetch p.provisaoPPAFontes fonte" +
            " where p.subAcaoPPA= :parametro ";
        Query q = em.createQuery(hql);
        q.setParameter("parametro", subAcaoPPA);
        List<ProvisaoPPADespesa> retorno = q.getResultList();
        return retorno;
    }

    public List<ProvisaoPPA> listaProvisao() {
        String hql = "from ProvisaoPPA";
        Query q = em.createQuery(hql);
        List<ProvisaoPPA> lista = q.getResultList();
        if (lista.isEmpty()) {
            lista = new ArrayList<ProvisaoPPA>();
        }
        return lista;
    }

    public List<ProvisaoPPA> listaProvisaoPorPpa(Long ppa) {
        String hql = "select pp from ProvisaoPPA pp "
            + "inner join pp.produtoPPA ps "
            + "inner join ps.acaoPrincipal sa "
            + "inner join sa.programa ap "
            + "inner join ap.ppa z "
            + "where z.id =:param ";
        Query q = em.createQuery(hql);
        q.setParameter("param", ppa);
        if (q.getResultList().isEmpty()) {
            return new ArrayList<ProvisaoPPA>();
        } else {
            return q.getResultList();
        }
    }

    public ProvisaoPPADespesa salvarRetornando(ProvisaoPPADespesa p) {
        try {
            ArrayList<ProvisaoPPAFonte> provisaoPPAFontes = new ArrayList<ProvisaoPPAFonte>(new HashSet(p.getProvisaoPPAFontes()));
            p.setProvisaoPPAFontes(provisaoPPAFontes);
            return em.merge(p);
        } catch (Exception ex) {
            logger.debug("Problema ao alterar", ex);
            return null;
        }
    }

    public void salvarDespesaOrcamentaria(ProvisaoPPADespesa provisaoPPADespesa, List<ProvisaoPPAFonte> listaProvisaoPPAFonteLoaEfetiva, GrupoOrcamentario grupoOrcamentario) throws ExcecaoNegocioGenerica, ValidaLDOExeption {
        List<ProvisaoPPADespesa> provisaoPPADespesas = listaProvisaoDispesaPPA(provisaoPPADespesa.getSubAcaoPPA());
        validarSeContaJaExisteNoOrcamento(provisaoPPADespesa, provisaoPPADespesas);

        boolean novoProv = provisaoPPADespesa.getId() == null;
        List<ProvisaoPPAFonte> fontes = Lists.newArrayList();
        fontes.addAll(listaProvisaoPPAFonteLoaEfetiva);
        fontes.addAll(provisaoPPADespesa.getProvisaoPPAFontes());

        loaFacade.salvarProvisaoPPA(provisaoPPADespesa, fontes);
        em.flush();

        LOA loa = loaFacade.listaUltimaLoaPorExercicio(provisaoPPADespesa.getSubAcaoPPA().getExercicio());
        if (!provisaoPPAFacade.validaEfetivacaoLoa(provisaoPPADespesa.getSubAcaoPPA().getExercicio()).isEmpty()) {
            List<ProvisaoPPADespesa> provisoes = new ArrayList<>();
            provisoes.add(provisaoPPADespesa);
            if (novoProv) {
                loaFacade.efetuaDespesa(loa, provisoes, OperacaoORC.DOTACAO, grupoOrcamentario);
            } else {
                loaFacade.efetuaMovimentoDespesa(loa, OperacaoORC.DOTACAO, new ArrayList<>(new HashSet<>(fontes)), provisaoPPADespesa, grupoOrcamentario);
            }
        }
    }

    private void validarSeContaJaExisteNoOrcamento(ProvisaoPPADespesa provisaoPPADespesa, List<ProvisaoPPADespesa> provisaoPPADespesas) {
        for (ProvisaoPPADespesa ppaDespesa : provisaoPPADespesas) {
            if (provisaoPPADespesa.getId() == null) {
                if (provisaoPPADespesa.getContaDeDespesa().getId().equals(ppaDespesa.getContaDeDespesa().getId())) {
                    throw new ExcecaoNegocioGenerica(" A Conta " + ppaDespesa.getContaDeDespesa().toString() + " já foi utilizada.");
                }
            } else {
                if (provisaoPPADespesa.getContaDeDespesa().getId().equals(ppaDespesa.getContaDeDespesa().getId())
                    && !ppaDespesa.getId().equals(provisaoPPADespesa.getId())) {
                    throw new ExcecaoNegocioGenerica(" A Conta " + ppaDespesa.getContaDeDespesa().toString() + " já foi utilizada.");
                }
            }
        }
    }

    @Override
    public void remover(ProvisaoPPADespesa entity) {
        ProvisaoPPADespesa p = em.find(ProvisaoPPADespesa.class, entity.getId());
        em.remove(p);

    }

    public String getCodigo(Exercicio e, SubAcaoPPA subAcaoPPA) {
        String sql = " select max(cast(p.codigo as number)) from ProvisaoPPADespesa p " +
            " inner join subacaoppa sub on p.subacaoppa_id = sub.id" +
            " inner join exercicio e on sub.exercicio_id = e.id " +
            " where sub.id = :subacao " +
            " and e.id = :exercicio";
        Query q = em.createNativeQuery(sql);
        q.setParameter("exercicio", e.getId());
        q.setParameter("subacao", subAcaoPPA.getId());
        Long cod = 0l;
        BigDecimal valor = (BigDecimal) q.getSingleResult();

        if (valor != null) {
            cod = valor.longValue();
        }
        cod++;
        return String.valueOf(cod);
    }

    @Override
    public List<ProvisaoPPADespesa> lista() {
        List<ProvisaoPPADespesa> listaP = new ArrayList<ProvisaoPPADespesa>();
        String hql = "from ProvisaoPPADespesa";
        Query q = em.createQuery(hql);
        listaP = q.getResultList();
        return listaP;
    }

    @Override
    public ProvisaoPPADespesa recuperar(Object id) {
        if (id instanceof ProvisaoPPADespesa) {
            ProvisaoPPADespesa p = (ProvisaoPPADespesa) id;
            p = em.find(ProvisaoPPADespesa.class, p.getId());
            p.getProvisaoPPAFontes().size();
            return p;
        } else {
            ProvisaoPPADespesa p = em.find(ProvisaoPPADespesa.class, id);
            p.getProvisaoPPAFontes().size();
            return p;
        }
    }

    public ProvisaoPPADespesa recuperarComFontes(Long id) {
        ProvisaoPPADespesa p = em.find(ProvisaoPPADespesa.class, id);
        p.getProvisaoPPAFontes().size();
        return p;
    }

    public List<ProvisaoPPA> listaPorLdo(LDO ldo) {
        String sql = "SELECT pa.* FROM provisaoppa pa "
            + "INNER JOIN provisaoppaldo pl ON pa.id = pl.provisaoppa_id "
            + "WHERE pl.ldo_id = :param";
        Query q = em.createNativeQuery(sql, ProvisaoPPA.class);
        q.setParameter("param", ldo.getId());
        return q.getResultList();
    }

    public List<ProvisaoPPADespesa> provisaoPPADespesasPorExercicio(Exercicio exerc) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select despesa.* FROM SUBACAOPPA sub  ");
        sql.append(" INNER JOIN PROVISAOPPADESPESA despesa on despesa.subacaoppa_id = sub.id ");
        sql.append(" where sub.EXERCICIO_ID =:param ");

        Query q = getEntityManager().createNativeQuery(sql.toString(), ProvisaoPPADespesa.class);
        q.setParameter("param", exerc.getId());
        return q.getResultList();
    }

    public List<ProvisaoPPADespesa> provisaoPPADespesasPorExercicioUnidades(Exercicio exerc, List<HierarquiaOrganizacional> listaUnidades) {
        List<UnidadeOrganizacional> unidades = Lists.newArrayList();
        for (HierarquiaOrganizacional lista : listaUnidades) {
            unidades.add(lista.getSubordinada());
        }
        StringBuilder sql = new StringBuilder();
        sql.append(" select despesa from SubAcaoPPA sub  ");
        sql.append(" INNER JOIN sub.provisaoPPADespesas despesa ");
        sql.append(" where sub.exercicio.id =:exercicio ");
        sql.append(" and despesa.unidadeOrganizacional in (:unidades) ");

        Query q = getEntityManager().createQuery(sql.toString(), ProvisaoPPADespesa.class);
        q.setParameter("exercicio", exerc.getId());
        q.setParameter("unidades", unidades);
        return q.getResultList();
    }

    public List<ProvisaoPPADespesa> getListaProvisaoPPADespesaPorUnidade(LDO ldo, Exercicio ex) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select ho.codigo, uo.descricao, pppad.* from provisaoppadespesa pppad ");
        sql.append(" inner join provisaoppa pppa on pppa.id = pppad.provisaoppa_id ");
        sql.append(" inner join provisaoppaldo pl on pppa.id = pl.provisaoppa_id and pl.ldo_id = :param");
        sql.append(" inner join subacaoppa sa on sa.id = pppa.subacao_id");
        sql.append(" inner join acaoppa ac on ac.id = sa.acaoppa_id");
        sql.append(" inner join unidadeorganizacional uo on uo.id = pppad.UNIDADEORGANIZACIONAL_ID");
        sql.append(" inner join hierarquiaorganizacional ho on ho.subordinada_id = uo.id and ho.tipohierarquiaorganizacional = 'ORCAMENTARIA' and ho.exercicio_id = :ex");
        sql.append(" order by ho.codigo, pppad.codigo");
        Query q = getEntityManager().createNativeQuery(sql.toString(), ProvisaoPPADespesa.class);
        q.setParameter("param", ldo);
        q.setParameter("ex", ex);
        return q.getResultList();
    }

    public Boolean verificaEfetivacaoLOA() {
        Boolean controle = true;
        String sql = "SELECT * FROM provisaoppa p"
            + " INNER JOIN provisaoppaldo pl ON p.id = pl.provisaoppa_id"
            + " INNER JOIN ldo d ON d.id = pl.ldo_id"
            + " INNER JOIN loa o ON o.ldo_id = d.id"
            + " WHERE o.efetivada = 1";
        Query q = em.createNativeQuery(sql, ProvisaoPPA.class);
        if (q.getResultList() == null) {
            controle = false;
        }
        return controle;
    }

    public List<ProvisaoPPADespesa> validaCodigoReduzido(Exercicio ex, String codigo) {
        String sql = "SELECT pppa.codigo FROM provisaoppadespesa pppa"
            + " INNER JOIN provisaoppa ppa ON ppa.id = pppa.provisaoppa_id"
            + " INNER JOIN provisaoppaldo ppl ON ppl.provisaoppa_id = ppa.id"
            + " INNER JOIN ldo ldo ON ldo.id = ppl.ldo_id"
            + " WHERE ldo.exercicio_id = :exercicio AND pppa.codigo = :codigo";
        Query q = em.createNativeQuery(sql);
        q.setParameter("exercicio", ex.getId());
        q.setParameter("codigo", codigo);
        return q.getResultList();
    }

    public void recalculaCodigoReduzido(LDO ldo, Exercicio exercicioCorrente, Integer intervalo) throws ExcecaoNegocioGenerica {
        Integer i = intervalo;
        try {
            for (ProvisaoPPADespesa pa : getListaProvisaoPPADespesaPorUnidade(ldo, exercicioCorrente)) {
                pa.setCodigo(String.valueOf(i));
                em.merge(pa);
                i = i + intervalo;
            }
        } catch (Exception ex) {
            String msg = "Não foi possível realizar essa operação!";
            throw new ExcecaoNegocioGenerica(msg);
        }
    }

    public BigDecimal retornaValorDasPrevisoesPorContaUnidadeExerc(Exercicio e, Conta c, UnidadeOrganizacional u) {
        String sql = "SELECT PHO.VALOR FROM PREVISAOHOCONTADESTINACAO PHO"
            + " WHERE PHO.EXERCICIO_ID = :ex"
            + " AND PHO.CONTA_ID = :conta"
            + " AND PHO.UNIDADEORGANIZACIONAL_ID = :unidade";
        Query q = em.createNativeQuery(sql);
        q.setParameter("ex", e.getId());
        q.setParameter("conta", c.getId());
        q.setParameter("unidade", u.getId());
        if (q.getResultList().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return (BigDecimal) q.getSingleResult();
    }

    public BigDecimal retornaValorFontesPorExercicioUnidadeConta(Exercicio e, Conta c, UnidadeOrganizacional u) {
        String sql = "SELECT coalesce(SUM(PPF.VALOR),0) FROM PROVISAOPPAFONTE PPF"
            + " INNER JOIN PROVISAOPPADESPESA PPD ON PPF.PROVISAOPPADESPESA_ID = PPD.ID"
            + " INNER JOIN SUBACAOPPA SUB ON SUB.ID = PPD.SUBACAOPPA_ID"
            + " INNER JOIN PROVISAOPPA PPA ON SUB.PROVISAOPPA_ID = PPA.ID"
            + " WHERE PPA.EXERCICIO_ID = :ex"
            + " AND PPD.UNIDADEORGANIZACIONAL_ID = :unidade"
            + " AND PPF.DESTINACAODERECURSOS_ID = :conta";
        Query q = em.createNativeQuery(sql);
        q.setParameter("ex", e.getId());
        q.setParameter("conta", c.getId());
        q.setParameter("unidade", u.getId());
        if (q.getResultList().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return (BigDecimal) q.getSingleResult();
    }

    public Boolean verificarSubAcaoPPATemProvisao(SubAcaoPPA subAcaoPPA) {
        String sql = " select * from PROVISAOPPADESPESA prov where prov.SUBACAOPPA_ID = :idSubAcao ";
        Query q = em.createNativeQuery(sql, ProvisaoPPADespesa.class);
        q.setParameter("idSubAcao", subAcaoPPA.getId());
        return !q.getResultList().isEmpty();
    }

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public ParticipanteAcaoPPAFacade getParticipanteAcaoPPAFacade() {
        return participanteAcaoPPAFacade;
    }

    public ProvisaoPPAFacade getProvisaoPPAFacade() {
        return provisaoPPAFacade;
    }

    public BigDecimal buscarValorPorFonteDespesaOrc(FonteDespesaORC fonteDespesaORC) {
        String sql = "select  coalesce(sum(provfonte.valor), 0) as dotacaoInicial " +
            " from fontedespesaorc fontdesp " +
            " inner join despesaorc desp on fontdesp.DESPESAORC_ID = desp.id " +
            " inner join provisaoppafonte provfonte on fontdesp.provisaoppafonte_id = provfonte.id" +
            " where fontdesp.id = :fontedespesaorc";
        Query q = em.createNativeQuery(sql);
        q.setParameter("fontedespesaorc", fonteDespesaORC.getId());
        if (q.getResultList().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return (BigDecimal) q.getSingleResult();
    }
}
