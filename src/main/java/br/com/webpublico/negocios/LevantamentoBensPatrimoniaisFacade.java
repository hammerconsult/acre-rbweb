package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.EstadoConservacaoBem;
import br.com.webpublico.enums.TipoAquisicaoBem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author Arthur Zavadski
 * @author Lucas Cheles
 */
@Stateless
public class LevantamentoBensPatrimoniaisFacade extends AbstractFacade<LevantamentoBemPatrimonial> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private GrupoObjetoCompraFacade grupoObjetoCompraFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    @EJB
    private FaseFacade faseFacade;
    @EJB
    private ObjetoCompraFacade objetoCompraFacade;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private AprovacaoLevantamentoBemFacade aprovacaoLevantamentoBemFacade;
    @EJB
    private ParametroPatrimonioFacade parametroPatrimonioFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private LoteEfetivacaoLevantamentoBemFacade loteEfetivacaoLevantamentoBemFacade;
    @EJB
    private GrupoObjetoCompraGrupoBemFacade grupoObjetoCompraGrupoBemFacade;


    public LevantamentoBensPatrimoniaisFacade() {
        super(LevantamentoBemPatrimonial.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    @Override
    public void salvarNovo(LevantamentoBemPatrimonial entity) {
        validarCodigoRepetido(entity);
        super.salvarNovo(entity);
    }

    @Override
    public void salvar(LevantamentoBemPatrimonial entity) {
        validarCodigoRepetido(entity);
        super.salvar(entity);
    }

    private void validarCodigoRepetido(LevantamentoBemPatrimonial entity) {
        ValidacaoException ve = new ValidacaoException();
        if (bemFacade.bemMovelTemCodigoRepetido(entity)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O código " + entity.getCodigoPatrimonio() + " já existe.");
        }
        ve.lancarException();
    }

    @Override
    public LevantamentoBemPatrimonial recuperar(Object id) {
        LevantamentoBemPatrimonial recuperarado = super.recuperar(id);
        recuperarado.getListaDeOriemRecursoBem().size();
        return recuperarado;
    }

    public List<LevantamentoBemPatrimonial> recuperarLevantamentos(HierarquiaOrganizacional hierarquiaOrganizacional, HierarquiaOrganizacional orcamentaria, ObjetoCompra objetoCompra, GrupoObjetoCompra grupoObjetoCompra, List<GrupoBem> grupoBem, Boolean excluirInconsistencia) throws ExcecaoNegocioGenerica {
        String sql = "select distinct  lev.* ,vw.codigo" +
            "    from levantamentobempatrimonial lev \n" +
            " left join efetivacaolevantamentobem ef on ef.levantamento_id = lev.id " +
            " left join ESTORNOEFETIVACAOLEVMOVEL estorno on estorno.efetivacaoLevantamentoBem_id = ef.id " +
            " left join eventobem ev on ev.id = ef.id" +
            " inner join vwhierarquiaadministrativa vw on vw.subordinada_id = lev.unidadeadministrativa_id" +
            " inner join vwhierarquiaorcamentaria vworc on vworc.subordinada_id = lev.unidadeorcamentaria_id ";

        if (grupoBem != null && !grupoBem.isEmpty()) {
            sql += " inner join objetocompra obj on obj.id = lev.item_id " +
                " left join GRUPOOBJCOMPRAGRUPOBEM associacao on associacao.grupoobjetocompra_id = obj.grupoObjetoCompra_id " +
                " left join grupobem gp on gp.id = associacao.grupobem_id";
        }

        sql += " where SYSDATE BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, SYSDATE)";
        if (grupoBem != null && !grupoBem.isEmpty()) {
            sql += " and SYSDATE BETWEEN associacao.INICIOVIGENCIA AND COALESCE(associacao.FIMVIGENCIA, SYSDATE) " +
                " and gp.id in (:grupobem) ";
        }

        sql += " and SYSDATE BETWEEN VWORC.INICIOVIGENCIA AND COALESCE(VWORC.FIMVIGENCIA, SYSDATE)" +
            "    and  ( ef.id IS NULL" +
            "     OR  ( estorno.id  = " +
            "      (SELECT max(evt.id) " +
            "                  FROM eventobem evt " +
            "               WHERE evt.bem_id = lev.bem_ID)) " +
            "    )";

        if (hierarquiaOrganizacional != null) {
            sql += " and vw.codigo like :unidade ";
        }

        if (excluirInconsistencia) {
            sql += " AND LEV.ID IN(SELECT LEV_AUX.ID " +
                "                FROM LEVANTAMENTOBEMPATRIMONIAL LEV_AUX " +
                "               WHERE NOT REGEXP_LIKE(LEV_AUX.CODIGOPATRIMONIO, '[A-Z a-z]' ,'i')" +
                "                     AND NOT REGEXP_LIKE(LEV_AUX.CODIGOPATRIMONIO, '[.,!?:;\\/~'']' ,'i')" +
                "                     AND NOT REGEXP_LIKE(LEV_AUX.CODIGOPATRIMONIO, '-' ,'i'))";
        }

        if (orcamentaria != null) {
            sql += " and vworc.codigo like :orcamentaria ";
        }

        sql += " order by vw.codigo ";

        Query q = em.createNativeQuery(sql, LevantamentoBemPatrimonial.class);

        if (hierarquiaOrganizacional != null) {
            q.setParameter("unidade", hierarquiaOrganizacional.getCodigoSemZerosFinais() + "%");
        }

        if (grupoBem != null && !grupoBem.isEmpty()) {
            q.setParameter("grupobem", grupoBem);
        }

        if (orcamentaria != null) {
            q.setParameter("orcamentaria", orcamentaria.getCodigoSemZerosFinais() + "%");
        }

        return q.getResultList();
    }


    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<List<LevantamentoBemPatrimonial>> recuperarLevantamentosBem(HierarquiaOrganizacional orcamentaria, Boolean excluirInconsistencia, Date dataOperacao) throws ExcecaoNegocioGenerica {
        String sql = "select distinct lev.id, rel_go_gb.grupobem_id " +
            "   from levantamentobempatrimonial lev " +
            "  left join efetivacaolevantamentobem ef on ef.levantamento_id = lev.id " +
            "  left join estornoefetivacaolevmovel estorno on estorno.efetivacaolevantamentobem_id = ef.id " +
            "  left join eventobem ev on ev.id = ef.id " +
            "  left join objetocompra oc on oc.id = lev.item_id " +
            "  left join grupoobjcompragrupobem rel_go_gb on rel_go_gb.grupoobjetocompra_id = oc.grupoobjetocompra_id " +
            "  inner join vwhierarquiaorcamentaria vworc on vworc.subordinada_id = lev.unidadeorcamentaria_id " +
            " where to_date(:dataOperacao, 'dd/MM/yyyy') BETWEEN trunc(VWORC.INICIOVIGENCIA) AND COALESCE(trunc(VWORC.FIMVIGENCIA), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "    and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(rel_go_gb.iniciovigencia) and coalesce(trunc(rel_go_gb.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "    and  ( ef.id IS NULL" +
            "     OR  ( estorno.id  = " +
            "      (SELECT max(evt.id) " +
            "                  FROM eventobem evt " +
            "               WHERE evt.bem_id = lev.bem_ID)) " +
            "    )";

        if (excluirInconsistencia) {
            sql += " AND LEV.ID IN(SELECT LEV_AUX.ID " +
                "                FROM LEVANTAMENTOBEMPATRIMONIAL LEV_AUX " +
                "               WHERE NOT REGEXP_LIKE(LEV_AUX.CODIGOPATRIMONIO, '[A-Z a-z]' ,'i')" +
                "                     AND NOT REGEXP_LIKE(LEV_AUX.CODIGOPATRIMONIO, '[.,!?:;\\/~'']' ,'i')" +
                "                     AND NOT REGEXP_LIKE(LEV_AUX.CODIGOPATRIMONIO, '-' ,'i'))";
        }

        if (orcamentaria != null) {
            sql += " and vworc.codigo like :orcamentaria ";
        }
        Query q = em.createNativeQuery(sql);
        if (orcamentaria != null) {
            q.setParameter("orcamentaria", orcamentaria.getCodigoSemZerosFinais() + "%");
        }
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        List<LevantamentoBemPatrimonial> toReturn = Lists.newArrayList();
        Map<Long, GrupoBem> grupos = Maps.newHashMap();
        for (Object[] o : (List<Object[]>) q.getResultList()) {
            LevantamentoBemPatrimonial levantamentoBemPatrimonial = em.find(LevantamentoBemPatrimonial.class, ((BigDecimal) o[0]).longValue());
            if (o[1] != null) {
                Long idGrupo = ((BigDecimal) o[1]).longValue();
                if (grupos.get(idGrupo) == null) {
                    grupos.put(idGrupo, em.find(GrupoBem.class, idGrupo));
                }
                levantamentoBemPatrimonial.setGrupoBem(grupos.get(idGrupo));
            }
            toReturn.add(levantamentoBemPatrimonial);
        }
        return new AsyncResult<>(toReturn);
    }

    public void verificarLevantamentosQuePrecisamDeAprovacao(List<LevantamentoBemPatrimonial> levantamentos) {
        ValidacaoException ve = new ValidacaoException();
        for (LevantamentoBemPatrimonial levantamento : levantamentos) {
            if (levantamento.requerAprovacao() && aprovacaoLevantamentoBemFacade.recuperarItemAprovacaoDoLevantamento(levantamento) == null) {
                String erro = "O registro patrimonial " + levantamento.getCodigoPatrimonio() + " do objeto de compra " + levantamento.getItem() + " deve ser aprovado antes de continuar.";
                ve.adicionarMensagemDeOperacaoNaoPermitida(erro);
                if (levantamento.getErro() != null && !levantamento.getErro().isEmpty()) {
                    levantamento.setErro(levantamento.getErro() + ", " + erro);
                } else {
                    levantamento.setErro(erro);
                }
            }
        }

        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public List<HierarquiaOrganizacional> getHierarquiaOrganizacionalAdministrativaOndeUsuarioEhGestorPatrimonioPorEntidade(String parte, String nivelEstrutura, Date data, UsuarioSistema usuarioCorrente, Entidade entidade) {
        String sql = "  SELECT distinct h.* " +
            "       FROM vwhierarquiaadministrativa vw " +
            " INNER JOIN hierarquiaorganizacional h ON h.id = vw.id " +
            " INNER JOIN unidadeorganizacional u on u.id = h.subordinada_id " +
            " INNER JOIN usuariounidadeorganizacio uu on uu.unidadeorganizacional_id = u.id " +
            "      WHERE :data BETWEEN vw.iniciovigencia AND COALESCE(vw.fimvigencia, :data)" +
            "        and :data BETWEEN h.iniciovigencia AND COALESCE(h.fimvigencia, :data)" +
            "        AND (upper(vw.descricao) LIKE :str " +
            "         OR vw.codigo LIKE :str) " +
            "        AND uu.gestorpatrimonio = 1 " +
            "        AND uu.usuariosistema_id = :usuario_id" +
            (nivelEstrutura == null ? "" : " AND h.indicedono = :nivel_estrutura") +
            (entidade == null ? "" : " and vw.entidade_id = :entidade") +
            "   ORDER BY h.codigo";

        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);

        q.setParameter("str", "%" + parte.toUpperCase() + "%");
        q.setParameter("data", data, TemporalType.DATE);
        q.setParameter("usuario_id", usuarioCorrente.getId());

        if (nivelEstrutura != null) {
            q.setParameter("nivel_estrutura", nivelEstrutura);
        }

        if (entidade != null) {
            q.setParameter("entidade", entidade.getId());
        }

        return q.getResultList();
    }

    public LevantamentoBemPatrimonial buscarLevantamentoPorCodigoPatrimonio(String codigo) {
        String sql = "select lev.*, vw.codigo " +
            "    from levantamentobempatrimonial lev  " +
            " inner join vwhierarquiaadministrativa vw on vw.subordinada_id = lev.unidadeadministrativa_id" +
            " where lev.codigopatrimonio = :codigo " +
            " and to_date(:dataOperacao, 'dd/mm/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, to_date(:dataOperacao, 'dd/mm/yyyy')) ";
        Query q = em.createNativeQuery(sql, LevantamentoBemPatrimonial.class);
        q.setParameter("codigo", codigo);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (LevantamentoBemPatrimonial) q.getSingleResult();
        }
        return null;
    }

    public List<LevantamentoBemPatrimonial> buscarLevantamentosPorCodigoPatrimonio(String codigo) {
        String sql = "select lev.*, vw.codigo " +
            "    from levantamentobempatrimonial lev  " +
            " inner join vwhierarquiaadministrativa vw on vw.subordinada_id = lev.unidadeadministrativa_id" +
            " where lev.codigopatrimonio like :codigo " +
            " and to_date(:dataOperacao, 'dd/mm/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, to_date(:dataOperacao, 'dd/mm/yyyy')) ";
        Query q = em.createNativeQuery(sql, LevantamentoBemPatrimonial.class);
        q.setParameter("codigo", "%" + codigo + "%");
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setMaxResults(10);
        if (!q.getResultList().isEmpty()) {
            return (List<LevantamentoBemPatrimonial>) q.getResultList();
        }
        return null;
    }

    public LevantamentoBemPatrimonial salvarLevantamento(LevantamentoBemPatrimonial entity) {
        return em.merge(entity);
    }

    public LoteEfetivacaoLevantamentoBem salvarLoteEfetivacaoLevantamentoBem(LoteEfetivacaoLevantamentoBem entity) {
        return em.merge(entity);
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public GrupoObjetoCompraFacade getGrupoObjetoCompraFacade() {
        return grupoObjetoCompraFacade;
    }

    public EntidadeFacade getEntidadeFacade() {
        return entidadeFacade;
    }

    public FaseFacade getFaseFacade() {
        return faseFacade;
    }

    public ObjetoCompraFacade getObjetoCompraFacade() {
        return objetoCompraFacade;
    }

    public BemFacade getBemFacade() {
        return bemFacade;
    }

    public ParametroPatrimonioFacade getParametroPatrimonioFacade() {
        return parametroPatrimonioFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public SingletonGeradorCodigo getSingletonGeradorCodigo() {
        return singletonGeradorCodigo;
    }

    public LoteEfetivacaoLevantamentoBemFacade getLoteEfetivacaoLevantamentoBemFacade() {
        return loteEfetivacaoLevantamentoBemFacade;
    }

    public GrupoObjetoCompraGrupoBemFacade getGrupoObjetoCompraGrupoBemFacade() {
        return grupoObjetoCompraGrupoBemFacade;
    }
}
