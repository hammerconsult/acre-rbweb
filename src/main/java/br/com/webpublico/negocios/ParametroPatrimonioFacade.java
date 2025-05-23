package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.administrativo.patrimonio.SecretariaControleSetorial;
import br.com.webpublico.entidades.administrativo.patrimonio.UnidadeControleSetorial;
import br.com.webpublico.entidadesauxiliares.HistoricoResponsavelUnidade;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 23/05/14
 * Time: 14:11
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class ParametroPatrimonioFacade extends AbstractFacade<ParametroPatrimonio> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;

    public ParametroPatrimonioFacade() {
        super(ParametroPatrimonio.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ParametroPatrimonio recuperar(Object id) {
        ParametroPatrimonio recuperado = super.recuperar(id);
        Date dataOperacao = sistemaFacade.getDataOperacao();
        recarregarParametroPatrimonio(recuperado, dataOperacao);
        return recuperado;
    }

    private void recarregarParametroPatrimonio(ParametroPatrimonio recuperado, Date dataOperacao) {
        recuperado.getListaResponsavelPatrimonio().size();
        recuperado.getEntidadeGeradoras().size();
        recuperado.getSecretarias().size();
        for (SecretariaControleSetorial secretariaControleSetorial : recuperado.getSecretarias()) {
            String tipo = TipoHierarquiaOrganizacional.ADMINISTRATIVA.name();
            secretariaControleSetorial.setHierarquiaOrganizacional(hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(tipo, secretariaControleSetorial.getUnidadeOrganizacional(), dataOperacao));
            secretariaControleSetorial.getUnidades().size();
            for (UnidadeControleSetorial unidadeControleSetorial : secretariaControleSetorial.getUnidades()) {
                unidadeControleSetorial.setHierarquiaOrganizacional(hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(tipo, unidadeControleSetorial.getUnidadeOrganizacional(), dataOperacao));
            }
        }
        for (EntidadeGeradoraIdentificacaoPatrimonio entidadeGeradoraIdentificacaoPatrimonio : recuperado.getEntidadeGeradoras()) {
            if (entidadeGeradoraIdentificacaoPatrimonio.ehSequenciaPropria()) {
                ((EntidadeSequenciaPropria) entidadeGeradoraIdentificacaoPatrimonio).getAgregadas().size();
            }
        }
    }

    private ParametroPatrimonio recuperarComDependenciasEntidadesGeradoraCodigoPatrimonio(Object id) {
        ParametroPatrimonio parametro = super.recuperar(id);
        Hibernate.initialize(parametro.getEntidadeGeradoras());
        for (EntidadeGeradoraIdentificacaoPatrimonio entidadeGeradoraIdentificacaoPatrimonio : parametro.getEntidadeGeradoras()) {
            if (entidadeGeradoraIdentificacaoPatrimonio.ehSequenciaPropria()) {
                Hibernate.initialize(((EntidadeSequenciaPropria) entidadeGeradoraIdentificacaoPatrimonio).getAgregadas());
            }
        }
        return parametro;
    }

    public void removerResponsavel(ResponsavelPatrimonio responsavelPatrimonio) {
        em.remove(em.find(ResponsavelPatrimonio.class, responsavelPatrimonio.getId()));
    }

    public void salvarResponsavel(ResponsavelPatrimonio responsavelPatrimonio) {
        em.merge(responsavelPatrimonio);
    }

    @Override
    public void salvar(ParametroPatrimonio entity) {
        for (EntidadeGeradoraIdentificacaoPatrimonio entidadeGeradoraIdentificacaoPatrimonio : entity.getEntidadeGeradorasImoveis()) {
            if (entidadeGeradoraIdentificacaoPatrimonio.getId() == null) {
                em.persist(entidadeGeradoraIdentificacaoPatrimonio);
            }
        }
        super.salvar(entity);
    }

    public boolean naoExisteParametro() {
        return em.createNativeQuery("select p.id from parametropatrimonio p").getResultList().isEmpty();
    }

    public ResponsavelPatrimonio recuperarResponsavelVigente(UnidadeOrganizacional unidade, Date dataOperacao) {
        String sql = "select rp.* from responsavelpatrimonio rp" +
            "          where rp.unidadeorganizacional_id = :UNIDADE_ID" +
            "          and to_date(:DATA_OPERACAO, 'dd/MM/yyyy') between trunc(rp.iniciovigencia) and coalesce(trunc(rp.fimvigencia), to_date(:DATA_OPERACAO, 'dd/MM/yyyy'))";
        Query q = em.createNativeQuery(sql, ResponsavelPatrimonio.class);
        q.setParameter("UNIDADE_ID", unidade.getId());
        q.setParameter("DATA_OPERACAO", DataUtil.getDataFormatada(dataOperacao));
        try {
            return (ResponsavelPatrimonio) q.getSingleResult();
        } catch (NoResultException e) {
            String hierarquiaDaUnidade = hierarquiaOrganizacionalFacade.buscarCodigoDescricaoHierarquia(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), unidade, dataOperacao);
            throw new ExcecaoNegocioGenerica("Não foi encontrado nenhum responsável do patrimônio para a unidade " + hierarquiaDaUnidade + " na data " + DataUtil.getDataFormatada(dataOperacao) + ".");
        } catch (NonUniqueResultException e) {
            String hierarquiaDaUnidade = hierarquiaOrganizacionalFacade.buscarCodigoDescricaoHierarquia(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), unidade, dataOperacao);
            throw new ExcecaoNegocioGenerica("Foi encontrado mais de um responsável do patrimônio para a unidade " + hierarquiaDaUnidade + " na data " + DataUtil.getDataFormatada(dataOperacao) + ".");
        }
    }

    public boolean verificarSeExisteResponsavelUnidade(ResponsavelPatrimonio responsavel) {
        String sql = "select rp.* from responsavelpatrimonio rp" +
            "          where rp.unidadeorganizacional_id = :UNIDADE_ID" +
            "          and to_date(:DATA_OPERACAO, 'dd/MM/yyyy') between trunc(rp.iniciovigencia) and coalesce(trunc(rp.fimvigencia), to_date(:DATA_OPERACAO, 'dd/MM/yyyy'))";
        sql += responsavel.getId() != null ? " and rp.id <> :idResponsavel " : "";
        Query q = em.createNativeQuery(sql, ResponsavelPatrimonio.class);
        q.setParameter("UNIDADE_ID", responsavel.getUnidadeOrganizacional().getId());
        q.setParameter("DATA_OPERACAO", DataUtil.getDataFormatada(responsavel.getInicioVigencia()));
        if (responsavel.getId() != null) {
            q.setParameter("idResponsavel", responsavel.getId());
        }
        return !q.getResultList().isEmpty();
    }

    public boolean verificarSeUsuarioEhResponsavelDaUnidade(UnidadeOrganizacional unidade, Date dataOperacao, UsuarioSistema usuario) {
        String sql = "select rp.*" +
            "       from responsavelpatrimonio rp" +
            "      where rp.unidadeorganizacional_id = :UNIDADE_ID" +
            "        and rp.responsavel_id = :usuario" +
            "        and (:DATA_OPERACAO between rp.iniciovigencia and coalesce(rp.fimvigencia, :DATA_OPERACAO))";
        Query q = em.createNativeQuery(sql, ResponsavelPatrimonio.class);
        q.setParameter("UNIDADE_ID", unidade.getId());
        q.setParameter("DATA_OPERACAO", dataOperacao, TemporalType.DATE);
        q.setParameter("usuario", usuario.getPessoaFisica().getId());
        return q.getResultList().isEmpty();
    }

    public List<ResponsavelPatrimonio> completarResponsavelPatrimonioVigente(String parte, Date dataOperacao) {
        String sql = "select rp.* " +
            "       from responsavelpatrimonio rp " +
            "      inner join pessoafisica pf on pf.id = rp.responsavel_id " +
            "      where (:DATA_OPERACAO between rp.iniciovigencia and coalesce(rp.fimvigencia, :DATA_OPERACAO)) " +
            "        and (lower(pf.nome) like :PARTE)";

        Query q = em.createNativeQuery(sql, ResponsavelPatrimonio.class);
        q.setParameter("DATA_OPERACAO", dataOperacao, TemporalType.DATE);
        q.setParameter("PARTE", "%" + parte.trim().toLowerCase() + "%");

        return q.getResultList();
    }

    public ParametroPatrimonio recuperarParametroComDependenciasResponsaveis() {
        try {
            ParametroPatrimonio parametro = (ParametroPatrimonio) em.createQuery(" from ParametroPatrimonio ").getSingleResult();
            parametro = super.recuperar(parametro.getId());
            Hibernate.initialize(parametro.getListaResponsavelPatrimonio());
            return parametro;
        } catch (NoResultException ex) {
            return null;
        }
    }

    public ParametroPatrimonio recuperarParametro() {
        try {
            return recuperar(getParametroPatrimonio().getId());
        } catch (NoResultException ex) {
            return null;
        }
    }

    public ParametroPatrimonio recuperarParametroPatrimonio() {
        try {
            return recuperarComDependenciasEntidadesGeradoraCodigoPatrimonio(getParametroPatrimonio().getId());
        } catch (NoResultException ex) {
            return null;
        }
    }

    public ParametroPatrimonio getParametroPatrimonio() {
        String sql = " select p.* from parametropatrimonio p ";
        Query query = em.createNativeQuery(sql, ParametroPatrimonio.class);
        return (ParametroPatrimonio) query.getSingleResult();
    }

    public EntidadeGeradoraIdentificacaoPatrimonio recuperarEntidadeGeradoraPorEntidade(Entidade entidade, TipoBem tipoBem) {
        try {
            Query q = em.createQuery("select EG from EntidadeGeradoraIdentificacaoPatrimonio EG " +
                "WHERE EG.entidade = :entidade and eg.tipoBem = :tipoBem");
            q.setParameter("entidade", entidade);
            q.setParameter("tipoBem", tipoBem);
            return (EntidadeGeradoraIdentificacaoPatrimonio) q.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public EntidadeSequenciaPropria recuperarSequenciaPropria(Entidade entidade) {
        String sql = "SELECT EG.*, " +
            "            ET.*  " +
            " FROM ENTIDADESEQUENCIAPROPRIA ET " +
            " INNER JOIN ENTIDADEGERADORA EG ON EG.ID = ET.ID" +
            " WHERE ET.ID =" +
            " (SELECT  " +
            "    CASE   " +
            "      WHEN ESA.ID IS NOT NULL THEN ESA.ENTIDADESEQUENCIAPROPRIA_ID  \n" +
            "      WHEN ESP.ID IS NOT NULL THEN ESP.ID  " +
            "    END AS ENTIDADEGERADORA_ID  " +
            "  FROM ENTIDADEGERADORA EG  " +
            "  LEFT JOIN ENTIDADESEQUENCIAAGREGADA ESA ON ESA.ID  = EG.ID " +
            "  LEFT JOIN ENTIDADESEQUENCIAPROPRIA  ESP ON ESP.ID  = EG.ID " +
            "  WHERE EG.ENTIDADE_ID = :entidade)";
        Query q = em.createNativeQuery(sql, EntidadeSequenciaPropria.class);
        q.setParameter("entidade", entidade.getId());
        try {
            return (EntidadeSequenciaPropria) q.getSingleResult();
        } catch (Exception no) {
            return null;
        }
    }

    public EntidadeSequenciaPropria recuperarSequenciaPropriaPorTipoGeracao(Entidade entidade, TipoBem tipoBem) {
        String sql = "SELECT EG.*, " +
            "            ET.*  " +
            " FROM ENTIDADESEQUENCIAPROPRIA ET " +
            " INNER JOIN ENTIDADEGERADORA EG ON EG.ID = ET.ID" +
            " WHERE ET.ID =" +
            " (SELECT  " +
            "    CASE   " +
            "      WHEN ESA.ID IS NOT NULL THEN ESA.ENTIDADESEQUENCIAPROPRIA_ID " +
            "      WHEN ESP.ID IS NOT NULL THEN ESP.ID  " +
            "    END AS ENTIDADEGERADORA_ID  " +
            "  FROM ENTIDADEGERADORA EG  " +
            "  LEFT JOIN ENTIDADESEQUENCIAAGREGADA ESA ON ESA.ID  = EG.ID " +
            "  LEFT JOIN ENTIDADESEQUENCIAPROPRIA  ESP ON ESP.ID  = EG.ID " +
            "  WHERE EG.ENTIDADE_ID = :entidade " +
            "  and eg.tipoBem = :tipoGeracao) " +
            "  and eg.tipoBem = :tipoGeracao ";
        Query q = em.createNativeQuery(sql, EntidadeSequenciaPropria.class);
        q.setParameter("entidade", entidade.getId());
        q.setParameter("tipoGeracao", tipoBem.name());
        try {
            return (EntidadeSequenciaPropria) q.getSingleResult();
        } catch (Exception no) {
            return null;
        }
    }

    public Long retornaUltimoCodigoInservivelParaEfetivacaoDoLevantamento(Entidade entidade) {
        String sql =
            " WITH ENTIDADEGERADORA_QUERY (ENTIDADEGERADORA_ID) AS  " +
                "  (SELECT  " +
                "    CASE   " +
                "      WHEN ESA.ID IS NOT NULL THEN ESA.ENTIDADESEQUENCIAPROPRIA_ID  " +
                "      WHEN ESP.ID IS NOT NULL THEN ESP.ID  " +
                "    END AS ENTIDADEGERADORA_ID  " +
                "   FROM ENTIDADEGERADORA EG      " +
                "     LEFT JOIN ENTIDADESEQUENCIAAGREGADA ESA ON ESA.ID  = EG.ID  " +
                "     LEFT JOIN ENTIDADESEQUENCIAPROPRIA  ESP ON ESP.ID  = EG.ID  " +
                "   WHERE EG.ENTIDADE_ID = :entidade),                          " +
                "  ENTIDADES_DA_SEQUENCIA (ENTIDADE_ID) AS " +
                "  (SELECT EG.ENTIDADE_ID  " +
                "     FROM ENTIDADEGERADORA EG " +
                "   INNER JOIN ENTIDADEGERADORA_QUERY EGQ ON EGQ.ENTIDADEGERADORA_ID = EG.ID " +
                "     UNION  " +
                "   SELECT EG.ENTIDADE_ID " +
                "     FROM ENTIDADEGERADORA EG " +
                "   INNER JOIN ENTIDADESEQUENCIAAGREGADA ESA ON EG.ID = ESA.ID " +
                "   INNER JOIN ENTIDADEGERADORA_QUERY EGQ ON EGQ.ENTIDADEGERADORA_ID = ESA.ENTIDADESEQUENCIAPROPRIA_ID) " +
                "   SELECT " +
                "       MAX(TO_NUMBER(regexp_replace(bem.IDENTIFICACAO, '[^0-9]'))) " +
                "     FROM EVENTOBEM EV " +
                "   INNER JOIN bem on ev.bem_id = bem.id " +
                "   INNER JOIN EFETIVACAOLEVANTAMENTOBEM EF ON EF.ID = EV.ID" +
                "   INNER JOIN ESTADOBEM ESTADO ON ESTADO.ID = EV.ESTADORESULTANTE_ID " +
                "   INNER JOIN VWHIERARQUIAADMINISTRATIVA VW ON VW.SUBORDINADA_ID = ESTADO.DETENTORAADMINISTRATIVA_ID " +
                "   INNER JOIN VWHIERARQUIAORCAMENTARIA   VWORC ON VWORC.SUBORDINADA_ID = ESTADO.DETENTORAORCAMENTARIA_ID " +
                "   INNER JOIN ENTIDADES_DA_SEQUENCIA EDS ON EDS.ENTIDADE_ID = VW.ENTIDADE_ID  " +
                "    WHERE " +
                "          SYSDATE BETWEEN VW.INICIOVIGENCIA    AND COALESCE(VW.FIMVIGENCIA, SYSDATE)  " +
                "    AND   SYSDATE BETWEEN VWORC.INICIOVIGENCIA AND COALESCE(VWORC.FIMVIGENCIA, SYSDATE) " +
                "    AND   EV.ESTADOINICIAL_ID IS NULL" +
                "    AND   ESTADO.ESTADOBEM = 'INSERVIVEL'";

        Query q = em.createNativeQuery(sql);
        q.setParameter("entidade", entidade.getId());
        try {
            return ((BigDecimal) q.getSingleResult()).longValue();
        } catch (NoResultException | NullPointerException nu) {
            return recuperarParametro().getFaixaInicialParaInsevivel() - 1;
        }
    }

    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public List<HistoricoResponsavelUnidade> buscarHistoricoResponsavelPorUnidade(HierarquiaOrganizacional hierarquiaOrganizacional, Date dataReferencia) {

        StringBuilder query = new StringBuilder();
        query.append(" ")
            .append(" select pf.nome as responsavel, ")
            .append("  resp.iniciovigencia, ")
            .append("  case ")
            .append("    when resp.fimvigencia > to_date(:dataReferencia, 'dd/MM/yyyy') ")
            .append("    then null ")
            .append("    else resp.fimvigencia ")
            .append("  end as fimvigencia ")
            .append("from parametropatrimonio param ")
            .append("inner join responsavelpatrimonio resp on resp.parametropatrimonio_id = param.id ")
            .append("inner join pessoafisica pf on pf.id = resp.responsavel_id  ")
            .append("inner join unidadeorganizacional unid on unid.id = resp.unidadeorganizacional_id ")
            .append("where to_date(:dataReferencia, 'dd/MM/yyyy') >= resp.iniciovigencia ")
            .append("and unid.id = :idUnidade")
            .append(" order by resp.iniciovigencia asc ");

        Query q = em.createNativeQuery(query.toString());
        q.setParameter("dataReferencia", DataUtil.getDataFormatada(dataReferencia));
        q.setParameter("idUnidade", hierarquiaOrganizacional.getSubordinada().getId());
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            List<HistoricoResponsavelUnidade> retorno = new ArrayList<>();
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                HistoricoResponsavelUnidade item = new HistoricoResponsavelUnidade();
                item.setNome((String) obj[0]);
                item.setInicioVigencia((Date) obj[1]);
                item.setFimVigencia((Date) obj[2]);
                retorno.add(item);
            }
            return retorno;
        }
    }

    public ResponsavelPatrimonio recuperarResonsavelPorUnidade(Long id) {
        String sql = "select * from responsavelpatrimonio where id = :id ";
        Query q = em.createNativeQuery(sql, ResponsavelPatrimonio.class);
        q.setParameter("id", id);
        q.setMaxResults(1);
        return (ResponsavelPatrimonio) q.getSingleResult();
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public List<Long> buscarUnidadeControleSetorial(HierarquiaOrganizacional hierarquiaOrganizacional) {
        String sql = " select unidade.UNIDADEORGANIZACIONAL_ID as id" +
            " from SECRETARIACONTROLESETORIAL sec " +
            "  inner join UNIDADECONTROLESETORIAL unidade on sec.ID = unidade.SECRETARIACONTROLESETORIAL_ID " +
            " where sec.UNIDADEORGANIZACIONAL_ID = :secretaria ";
        try {
            Query q = em.createNativeQuery(sql);
            q.setParameter("secretaria", hierarquiaOrganizacional.getSubordinada().getId());
            return q.getResultList();
        } catch (NoResultException e) {
            return Lists.newArrayList();
        }
    }

    public boolean hasUnidadesControleSetorial(HierarquiaOrganizacional hierarquiaOrganizacional) {
        String sql = " select unidade.UNIDADEORGANIZACIONAL_ID as id" +
            " from SECRETARIACONTROLESETORIAL sec " +
            "  inner join UNIDADECONTROLESETORIAL unidade on sec.ID = unidade.SECRETARIACONTROLESETORIAL_ID " +
            " where sec.UNIDADEORGANIZACIONAL_ID = :secretaria ";
        try {
            Query q = em.createNativeQuery(sql);
            q.setParameter("secretaria", hierarquiaOrganizacional.getSubordinada().getId());
            return !q.getResultList().isEmpty();
        } catch (NoResultException e) {
            return false;
        }
    }
}
