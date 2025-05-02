package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.EstadoConservacaoBem;
import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoAquisicaoBem;
import br.com.webpublico.exception.ValidacaoException;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Suporte Contabil
 * Date: 23/04/14
 * Time: 17:53
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class AprovacaoLevantamentoBemFacade extends AbstractFacade<AprovacaoLevantamentoBem> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AprovacaoLevantamentoBemFacade() {
        super(AprovacaoLevantamentoBem.class);
    }

    public List<LevantamentoBemPatrimonial> recuperarLevantamentosNaoAprovados(HierarquiaOrganizacional orcamentaria, TipoAquisicaoBem[] tipos, Boolean excluirInconsistencias) throws ExcecaoNegocioGenerica {
        if (orcamentaria == null) {
            throw new ExcecaoNegocioGenerica("Impossível pesquisar sem unidade organizacional.");
        }

        if (tipos == null) {
            throw new ExcecaoNegocioGenerica("Impossível pesquisar sem tipos de aquisição de bem.");
        }

        String sql = " SELECT LEV.*   " +
            "      FROM LEVANTAMENTOBEMPATRIMONIAL LEV   " +
            " LEFT JOIN ITEMAPROVACAOLEVANTAMENTO ITEM ON ITEM.LEVANTAMENTOBEMPATRIMONIAL_ID = LEV.ID   " +
            " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON VW.SUBORDINADA_ID = LEV.UNIDADEORCAMENTARIA_ID   " +
            "     WHERE VW.CODIGO LIKE :unidade  " +
            "       AND SYSDATE BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, SYSDATE)  " +
            "       AND (ITEM.ID IS NULL  " +
            "            OR (ITEM.ID = (SELECT MAX(T.ID)  " +
            "                             FROM ITEMAPROVACAOLEVANTAMENTO T   " +
            "                           WHERE T.LEVANTAMENTOBEMPATRIMONIAL_ID = LEV.ID) AND ITEM.SITUACAO = :estornado )) ";

        if (excluirInconsistencias) {
            sql += " AND TRIM(LEV.CODIGOPATRIMONIO)" +
                "                         IN(SELECT TRIM(LEV_AUX.CODIGOPATRIMONIO) " +
                "                          FROM LEVANTAMENTOBEMPATRIMONIAL LEV_AUX " +
                "                       WHERE NOT REGEXP_LIKE(LEV_AUX.CODIGOPATRIMONIO, '[A-Z a-z]' ,'i')" +
                "                             AND NOT REGEXP_LIKE(LEV_AUX.CODIGOPATRIMONIO, '[.,!?:;\\/~'']' ,'i')" +
                "                             AND NOT REGEXP_LIKE(LEV_AUX.CODIGOPATRIMONIO, '-' ,'i'))";
        }

        sql += " AND LEV.TIPOAQUISICAOBEM IN (";

        for (TipoAquisicaoBem tipo : tipos) {
            sql += "'" + tipo.name() + "', ";
        }

        sql = sql.substring(0, sql.length() - 2) + ") order by lev.codigopatrimonio";

        Query q = em.createNativeQuery(sql, LevantamentoBemPatrimonial.class);
        q.setParameter("unidade", orcamentaria.getCodigoSemZerosFinais() + "%");
        q.setParameter("estornado", SituacaoEventoBem.ESTORNADO.name());
        return q.getResultList();
    }

    public List<Object> recuperarLevantamentosConsulta(UnidadeOrganizacional unidadeOrganizacional, TipoAquisicaoBem[] tipos, TipoFiltroConsultaLevantamento tipofiltro) throws ExcecaoNegocioGenerica {
        ValidacaoException ve = new ValidacaoException();
        if (unidadeOrganizacional == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Organizacional é obrigatório.");
        }

        if (tipos == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Os tipos de aquisição de bem devem ser informados.");
        }
        if (ve.temMensagens()) {
            throw ve;
        }

        String condicao = "";

        if (TipoFiltroConsultaLevantamento.APROVADO.equals(tipofiltro)) {
            condicao = " AND ITEM.ID IS NOT NULL ";
        }
        if (TipoFiltroConsultaLevantamento.NAO_APROVADO.equals(tipofiltro)) {
            condicao = " AND ITEM.ID IS NULL ";
        }
        if (TipoFiltroConsultaLevantamento.INCONSISTENTE.equals(tipofiltro)) {
            condicao = " AND TRIM(LEV.CODIGOPATRIMONIO) NOT IN(SELECT " +
                "                                             TRIM(LEV_AUX.CODIGOPATRIMONIO)       " +
                "                                      FROM LEVANTAMENTOBEMPATRIMONIAL LEV_AUX\n   " +
                "                                    WHERE NOT REGEXP_LIKE(LEV_AUX.CODIGOPATRIMONIO, '[A-Z a-z]' ,'i')     " +
                "                                      AND NOT REGEXP_LIKE(LEV_AUX.CODIGOPATRIMONIO, '[.,!?:;\\/~'']' ,'i')" +
                "                                      AND NOT REGEXP_LIKE(LEV_AUX.CODIGOPATRIMONIO, '-' ,'i')) ";
        }

        String sql = " SELECT LEV.CODIGOPATRIMONIO ," +
            "             OBJ.CODIGO || ' ' || OBJ.DESCRICAO AS OBJ_COD_DESCRICAO," +
            "             LEV.MARCA," +
            "             LEV.MODELO," +
            "      CASE " +
            "          WHEN LEV.ESTADOCONSERVACAOBEM = '" + EstadoConservacaoBem.OPERACIONAL.name() + "'              THEN '" + EstadoConservacaoBem.OPERACIONAL.getDescricao() + "' " +
            "          WHEN LEV.ESTADOCONSERVACAOBEM = 'INSERVIVEL'               THEN 'Inservível' " +
            "          WHEN LEV.ESTADOCONSERVACAOBEM = 'BAIXADO'                  THEN 'Baixado' " +
            "          WHEN LEV.ESTADOCONSERVACAOBEM = 'AGUARDANDO_TRANSFERENCIA' THEN 'Aguardando Transferência' " +
            "       ELSE 'Não Encontrado' " +
            "      END AS ESTADOCONSERVACAOBEM, " +
            "      CASE " +
            "          WHEN LEV.SITUACAOCONSERVACAOBEM = 'USO_NORMAL'    THEN 'Uso Normal' " +
            "          WHEN LEV.SITUACAOCONSERVACAOBEM = 'OCIOSO'        THEN 'Ocioso' " +
            "          WHEN LEV.SITUACAOCONSERVACAOBEM = 'ANTIECONOMICO' THEN 'Antieconômico' " +
            "          WHEN LEV.SITUACAOCONSERVACAOBEM = 'IRRECUPERAVEL' THEN 'Irrecuperável' " +
            "          WHEN LEV.SITUACAOCONSERVACAOBEM = 'OBSOLETO'      THEN 'Obsoleto' " +
            "       ELSE 'Não Encontrada' " +
            "      END AS SITUACAOCONSERVACAOBEM, " +
            "      CASE " +
            "          WHEN LEV.TIPOAQUISICAOBEM = 'ADJUDICACAO'    THEN 'Adjudicação' " +
            "          WHEN LEV.TIPOAQUISICAOBEM = 'APREENSAO'      THEN 'Apreensão' " +
            "          WHEN LEV.TIPOAQUISICAOBEM = 'COMPRA'         THEN 'Compra' " +
            "          WHEN LEV.TIPOAQUISICAOBEM = 'CONVENIO'       THEN 'Convênio' " +
            "          WHEN LEV.TIPOAQUISICAOBEM = 'CONTRATO'       THEN 'Contrato' " +
            "          WHEN LEV.TIPOAQUISICAOBEM = 'DACAO'          THEN 'Dação em Pagamento' " +
            "          WHEN LEV.TIPOAQUISICAOBEM = 'DOACAO'         THEN 'Doação' " +
            "          WHEN LEV.TIPOAQUISICAOBEM = 'PERMUTA'        THEN 'Permuta' " +
            "          WHEN LEV.TIPOAQUISICAOBEM = 'REDISTRIBUICAO' THEN 'Redistribuição' " +
            "          WHEN LEV.TIPOAQUISICAOBEM = 'COMODATO'       THEN 'Comodato' " +
            "          WHEN LEV.TIPOAQUISICAOBEM = 'AVALIACAO'      THEN 'Avaliação' " +
            "       ELSE 'Não Encontrado' " +
            "      END AS TIPOAQUISICAOBEM, " +
            "             LEV.DATAAQUISICAO," +
            "             COALESCE(FORNECEDOR_PF.CPF, FORNECEDOR_PJ.CNPJ) AS FORNECEDOR_CPF_CNPJ," +
            "             COALESCE(FORNECEDOR_PF.NOME,FORNECEDOR_PJ.RAZAOSOCIAL) AS FORNECEDOR," +
            "             LEV.VALORBEM, " +
            "      CASE" +
            "          WHEN ITEM.ID IS NOT NULL     THEN 'Aprovado' " +
            "          WHEN ITEM.ID IS NULL         THEN 'Não Aprovado' " +
            "       ELSE 'Não Encontrada' " +
            "      END AS SITUACAO ," +
            "             LEV.ID " +
            "        FROM LEVANTAMENTOBEMPATRIMONIAL LEV " +
            "   LEFT JOIN ITEMAPROVACAOLEVANTAMENTO item on ITEM.LEVANTAMENTOBEMPATRIMONIAL_ID = lev.id" +
            "        JOIN OBJETOCOMPRA OBJ ON OBJ.ID = LEV.ITEM_ID " +
            "   LEFT JOIN PESSOAFISICA   FORNECEDOR_PF ON FORNECEDOR_PF.ID = LEV.FORNECEDOR_ID " +
            "   LEFT JOIN PESSOAJURIDICA FORNECEDOR_PJ ON FORNECEDOR_PJ.ID = LEV.FORNECEDOR_ID " +
            "       WHERE LEV.UNIDADEADMINISTRATIVA_ID = :unidade_id " +
            condicao;

        if (!TipoFiltroConsultaLevantamento.INCONSISTENTE.equals(tipofiltro)) {
            sql += "         AND LEV.TIPOAQUISICAOBEM IN (";
            for (TipoAquisicaoBem tipo : tipos) {
                sql += "'" + tipo.name() + "', ";
            }
            sql = sql.substring(0, sql.length() - 2) + ")";
        }

        sql += " order by lev.codigopatrimonio";
        Query q = em.createNativeQuery(sql);
        q.setParameter("unidade_id", unidadeOrganizacional.getId());
        return q.getResultList();

    }

    @Override
    public AprovacaoLevantamentoBem recuperar(Object id) {
        AprovacaoLevantamentoBem aprovacao = super.recuperar(id);
        aprovacao.getListaItemAprovacaoLevantamentos().size();
        return aprovacao;
    }

    public Boolean podeEstornar(ItemAprovacaoLevantamento item) {
        String sql = "select  ef.* from EfetivacaoLevantamentoBem  ef where ef.levantamento_id = :id";

        Query q = em.createNativeQuery(sql);
        q.setParameter("id", item.getLevantamentoBemPatrimonial().getId());

        return q.getResultList().isEmpty();
    }

    public Boolean podeEstornarTodos(List<ItemAprovacaoLevantamento> itens) {
        String sql = "select  ef.* " +
            "   from EfetivacaoLevantamentoBem ef" +
            "   inner join eventobem ev on ev.id = ef.id " +
            "   join LevantamentoBemPatrimonial lev on ef.levantamento_id = lev.id " +
            " where  (ef.id = (select max(eft.id) " +
            "                  from EfetivacaoLevantamentoBem eft" +
            "                 where eft.levantamento_id = lev.id) and ev.situacaoeventobem <> :estornado)";

        String juncao = " and lev.id in (";
        Integer count = 0;
        StringBuffer toReturn = new StringBuffer();
        for (ItemAprovacaoLevantamento item : itens) {
            toReturn.append(juncao);
            toReturn.append(item.getLevantamentoBemPatrimonial().getId());
            juncao = ", ";
            count++;
            if (count == 1000) {
                juncao = ") or lev.id in (";
                count = 0;
            }
        }

        if (toReturn.length() > 0) {
            toReturn.append(") ");
        }

        Query q = em.createNativeQuery(sql + toReturn.toString());
        q.setParameter("estornado", SituacaoEventoBem.ESTORNADO.name());
        return q.getResultList().isEmpty();
    }

    public ItemAprovacaoLevantamento recuperarItemAprovacaoDoLevantamento(LevantamentoBemPatrimonial levantamento) {
        Query q = em.createNativeQuery(
            " select item.* " +
                " from ItemAprovacaoLevantamento  item " +
                " where item.levantamentoBemPatrimonial_id = :levantamento" +
                "       and item.situacao <> :estornado " +
                "       and item.id = ( select max(it.id) " +
                "                 from ItemAprovacaoLevantamento it" +
                "               where it.levantamentoBemPatrimonial_id = item.levantamentoBemPatrimonial_id)",
            ItemAprovacaoLevantamento.class)
            .setParameter("levantamento", levantamento.getId())
            .setParameter("estornado", SituacaoEventoBem.ESTORNADO.name());

        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }

        return (ItemAprovacaoLevantamento) resultList.get(0);
    }

    public enum TipoFiltroConsultaLevantamento {

        APROVADO("Aprovado"),
        NAO_APROVADO("Não Aprovado"),
        INCONSISTENTE("Inconsistente");
        private String descricao;

        private TipoFiltroConsultaLevantamento(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Object[] filtarComContadorDeRegistros(String sql, String sqlCount, int inicio, int max) throws Exception {
        Query consulta = em.createNativeQuery(sql);
        Query consultaCount = em.createNativeQuery(sqlCount);
        Long count = 0L;
        List<AprovacaoLevantamentoBem> list = new ArrayList<>();
        try {
            count = ((BigDecimal) consultaCount.getSingleResult()).longValue();
            if (max != 0) {
                consulta.setMaxResults(max);
                consulta.setFirstResult(inicio);
            }
            List<Object[]> lista = consulta.getResultList();
            for (Object[] object : lista) {
                AprovacaoLevantamentoBem aprovacao = new AprovacaoLevantamentoBem();
                aprovacao.setId(((BigDecimal) object[0]).longValue());
                aprovacao.setDataAprovacao(object[1] != null ? (Date) object[1] : null);
                aprovacao.setDescricaoAprovador(object[2] != null ? (String) object[2] : null);
                aprovacao.setDescricaoUnidade(object[3] != null ? (String) object[3] : null);
                aprovacao.setSituacao(object[4] != null ? SituacaoEventoBem.valueOf((String) object[4]) : null);
                list.add(aprovacao);
            }
        } catch (NoResultException nre) {
            logger.error("Erro ao montar lista de aprovação de levantamento de bens móveis{} ", nre);
        }
        Object[] retorno = new Object[2];
        retorno[0] = list;
        retorno[1] = count;
        return retorno;
    }
}
