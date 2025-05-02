/*
 * Codigo gerado automaticamente em Mon May 23 15:54:24 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoEstoque;
import br.com.webpublico.exception.OperacaoEstoqueException;
import br.com.webpublico.util.DataUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import javax.ejb.Stateless;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Stateless
public class EstoqueFacade extends AbstractFacade<Estoque> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public EstoqueFacade() {
        super(Estoque.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Estoque validaLocalDeEstoquePossuiEstoque(LocalEstoque localEstoque) {
//        throw new RuntimeException("Este método deve contemplar as alterações referentes a contabilização por unidade orçamentária.");

        String hql = "select estoque " +
            "       from Estoque estoque" +
            "     inner join estoque.localEstoqueOrcamentario orcamentario"
            + "    where orcamentario.localEstoque.id = :local";

        Query consulta = em.createQuery(hql);
        consulta.setParameter("local", localEstoque.getId());
        consulta.setMaxResults(1);

        try {
            Estoque estoque = (Estoque) consulta.getSingleResult();
            if (estoque != null) {
                if (estoque.getLocalEstoque().equals(localEstoque)) {
                    return estoque;
                }
            }
        } catch (Exception e) {
            return null;
        }

        return null;
    }

    public ItemEntradaMaterial validaVinculoDoLocalEstoqueComEntrada(LocalEstoque localEstoque) {
//        throw new RuntimeException("Este método deve contemplar as alterações referentes a contabilização por unidade orçamentária.");

        String hql = "select item " +
            "       from ItemEntradaMaterial item" +
            "   inner join item.localEstoqueOrcamentario.localEstoque local"
            + "     where local.id = :local";

        Query consulta = em.createQuery(hql);
        consulta.setParameter("local", localEstoque.getId());
        consulta.setMaxResults(1);

        try {
            ItemEntradaMaterial item = (ItemEntradaMaterial) consulta.getSingleResult();
            if (item != null) {
                if (item.getLocalEstoque().equals(localEstoque)) {
                    return item;
                }
            }
        } catch (Exception e) {
            return null;
        }

        return null;
    }

    public ItemSaidaMaterial validaVinculoDoLocalEstoqueComSaida(LocalEstoque localEstoque) {
//        throw new RuntimeException("Este método deve contemplar as alterações referentes a contabilização por unidade orçamentária.");

        String hql = "select item " +
            "       from ItemSaidaMaterial item" +
            "     inner join item.localEstoqueOrcamentario.localEstoque local"
            + "   where local.id = :local";
        Query consulta = em.createQuery(hql);
        consulta.setParameter("local", localEstoque.getId());
        consulta.setMaxResults(1);
        try {
            ItemSaidaMaterial item = (ItemSaidaMaterial) consulta.getSingleResult();
            if (item != null) {
                if (item.getLocalEstoque().equals(localEstoque)) {
                    return item;
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public Estoque recuperarEstoque(LocalEstoqueOrcamentario local, Material material, Date data) throws OperacaoEstoqueException {
        String sql = "SELECT * " +
            "       FROM (SELECT E.* " +
            "               FROM ESTOQUE E " +
            "              WHERE E.LOCALESTOQUEORCAMENTARIO_ID = :local_id " +
            "                AND E.MATERIAL_ID     = :material_id " +
            "           ORDER BY E.DATAESTOQUE DESC) " +
            "      WHERE trunc(DATAESTOQUE) <= trunc(:data) " +
            "        AND ROWNUM = 1";

        Query q = em.createNativeQuery(sql, Estoque.class);

        q.setParameter("material_id", material.getId());
        q.setParameter("local_id", local.getId());
        q.setParameter("data", DataUtil.dataSemHorario(data));

        try {
            Estoque e = (Estoque) q.getSingleResult();
            e.getLotesMaterial().size();
            return e;
        } catch (NoResultException e) {
            throw new OperacaoEstoqueException("Não existe estoque do material " + material + ", no local de estoque " + local.getLocalEstoque() + ", contabilizado na unidade orçamentária " + local.getUnidadeOrcamentaria() + ".");
        }
    }

    public Estoque recuperarEstoque(LocalEstoque local, UnidadeOrganizacional unidadeOrcamentaria, Material material, Date data) {
        String sql = "SELECT E.* " +
            "               FROM ESTOQUE E " +
            "         INNER JOIN LOCALESTOQUEORCAMENTARIO LEO ON LEO.ID = E.LOCALESTOQUEORCAMENTARIO_ID" +
            "              WHERE E.MATERIAL_ID     = :material_id " +
            "                AND LEO.LOCALESTOQUE_ID = :local_id" +
            "                AND LEO.UNIDADEORCAMENTARIA_ID = :unidade_id" +
            "                AND trunc(E.DATAESTOQUE) <= to_date(:data, 'dd/MM/yyyy')" +
            "      order by E.id desc ";
        Query q = em.createNativeQuery(sql, Estoque.class);
        try {
            q.setParameter("material_id", material.getId());
            q.setParameter("local_id", local.getId());
            q.setParameter("unidade_id", unidadeOrcamentaria.getId());
            q.setParameter("data", DataUtil.getDataFormatada(data));
            q.setMaxResults(1);
            return (Estoque) q.getSingleResult();
        } catch (NullPointerException | NoResultException e) {
            return null;
        }
    }

    public Estoque recuperaEstoqueDaUltimaEntradaDaUnidadeOrgAteAData(Date data, UnidadeOrganizacional uo, Material m) {
//        throw new RuntimeException("Este método deve contemplar as alterações referentes a contabilização por unidade orçamentária.");

        String hql = "          select e "
            + "               from Estoque e, MovimentoEstoque me, ItemEntradaMaterial iem, EntradaMaterial em, LocalEstoque le "
            + "              where iem.entradaMaterial = em "
            + "                and iem.movimentoEstoque = me "
            + "                and me.estoque = e "
            + "                and e.localEstoqueOrcamentario.localEstoque = le "
            + "                and le.unidadeOrganizacional = :uo "
            + "                and me.material = :mat"
            + "                and em.dataEntrada <= :data"
            + "           order by em.dataEntrada desc";

        Query q = em.createQuery(hql);
        q.setParameter("data", data, TemporalType.TIMESTAMP);
        q.setParameter("uo", uo);
        q.setParameter("mat", m);
        q.setMaxResults(1);

        return (Estoque) q.getSingleResult();
    }

    public boolean estoqueBloqueado(Material material, LocalEstoqueOrcamentario local, TipoEstoque tipoEstoque, Date data) {
        try {
            return recuperarEstoque(local, material, data).getBloqueado();
        } catch (OperacaoEstoqueException eng) {
            return false;
        }
    }

    public boolean estoqueBloqueado(Material material, LocalEstoque local, UnidadeOrganizacional unidadeOrcamentaria, Date data, TipoEstoque tipoEstoque) {
        Estoque estoque = recuperarEstoque(local, unidadeOrcamentaria, material, data);

        if (estoque != null) {
            return estoque.getBloqueado();
        }

        return false;
    }

    /*A ordenação deve ser mantida*/
    public List<Estoque> recuperarEstoquesDaHierarquiaDoLocalDeEstoque(LocalEstoque pai, Material m, Date dataOperacao, Boolean estoqueBloqueado) {

        Preconditions.checkNotNull(pai, "Local de estoque pai não foi preenchido.");
        Preconditions.checkNotNull(m, "O material não foi preenchido.");
        Preconditions.checkNotNull(dataOperacao, "A data de operação não foi preenchida.");

        String sql = "    WITH PAI (ID, CODIGO, DESCRICAO, FECHADOEM, SUPERIOR_ID, UNIDADEORGANIZACIONAL_ID) AS     " +
            "      (SELECT LE_PAI.ID, LE_PAI.CODIGO, LE_PAI.DESCRICAO, LE_PAI.FECHADOEM, LE_PAI.SUPERIOR_ID, LE_PAI.UNIDADEORGANIZACIONAL_ID    " +
            "         FROM LOCALESTOQUE LE_PAI     " +
            "        WHERE LE_PAI.ID = :local_pai_id " +
            "    UNION ALL     " +
            "       SELECT LE_FILHO.ID, LE_FILHO.CODIGO, LE_FILHO.DESCRICAO, LE_FILHO.FECHADOEM, LE_FILHO.SUPERIOR_ID, LE_FILHO.UNIDADEORGANIZACIONAL_ID     " +
            "         FROM LOCALESTOQUE LE_FILHO     " +
            "   INNER JOIN PAI P ON P.ID = LE_FILHO.SUPERIOR_ID) " +
            "       SELECT e.* " +
            "         FROM estoque e " +
            "   INNER JOIN localestoqueorcamentario leo ON leo.id = e.localestoqueorcamentario_id " +
            "   INNER JOIN localestoque le on le.id = leo.localestoque_id " +
            "   INNER JOIN pai p on p.id = le.id " +
            "        WHERE le.fechadoem is null " +
            "          AND e.material_id = :material_id " +
            "          AND e.bloqueado = :estoqueBloqueado " +
            "          AND e.dataestoque = (SELECT MAX(e2.dataestoque) " +
            "                                 FROM estoque e2 " +
            "                                WHERE e2.localestoqueorcamentario_id = e.localestoqueorcamentario_id " +
            "                                  AND e2.material_id = e.material_id" +
            "                                  AND trunc(e2.dataestoque) <= trunc(:data_operacao)) " +
            "     ORDER BY le.codigo";

        Query q = em.createNativeQuery(sql, Estoque.class);
        q.setParameter("local_pai_id", pai.getId());
        q.setParameter("material_id", m.getId());
        q.setParameter("estoqueBloqueado", estoqueBloqueado);
        q.setParameter("data_operacao", DataUtil.dataSemHorario(dataOperacao));
        return q.getResultList();
    }

    public BigDecimal buscarUltimoSaldoEstoquePorData(GrupoMaterial grupo, UnidadeOrganizacional unidade, TipoEstoque tipoEstoque, Date dataReferencia) throws ExcecaoNegocioGenerica {
        try {
            String sql = " select coalesce(sum(trunc(est.financeiro,4)),0)  from estoque est " +
                " inner join localestoqueorcamentario leo on leo.id = est.localestoqueorcamentario_id " +
                " inner join material mat on mat.id = est.material_id " +
                " where trunc(EST.DATAESTOQUE) = (SELECT MAX(TRUNC(E2.DATAESTOQUE)) " +
                "                        FROM ESTOQUE E2 " +
                "                      WHERE E2.LOCALESTOQUEORCAMENTARIO_ID = est.LOCALESTOQUEORCAMENTARIO_ID " +
                "                        AND E2.MATERIAL_ID = est.MATERIAL_ID" +
                "                        AND E2.tipoestoque = est.tipoestoque " +
                "                        AND trunc(E2.DATAESTOQUE) <= trunc(:dataReferencia)) " +
                " and mat.grupo_id = :grupoMaterial " +
                " and leo.unidadeorcamentaria_id = :unidade  " +
                " and est.tipoestoque = :tipoEstoque ";
            Query q = em.createNativeQuery(sql);
            q.setParameter("grupoMaterial", grupo.getId());
            q.setParameter("unidade", unidade.getId());
            q.setParameter("tipoEstoque", tipoEstoque.name());
            q.setParameter("dataReferencia", DataUtil.dataSemHorario(dataReferencia));
            q.setMaxResults(1);
            return (BigDecimal) q.getSingleResult();
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }

    public Date recuperarMenorDataEstoque() {
        String sql = " select MIN(e.dataestoque) from estoque e ";
        Query q = em.createNativeQuery(sql);
        try {
            return (Date) q.getSingleResult();
        } catch (NullPointerException | NoResultException e) {
            return new Date();
        }
    }

    public List<Estoque> recuperarEstoquesDaHierarquiaDoLocalDeEstoque(LocalEstoque localEstoquePai, Material material, Date dataOperacao) {
        List<Estoque> retorno = Lists.newArrayList();
        retorno.addAll(recuperarEstoquesDaHierarquiaDoLocalDeEstoque(localEstoquePai, material, dataOperacao, true));
        retorno.addAll(recuperarEstoquesDaHierarquiaDoLocalDeEstoque(localEstoquePai, material, dataOperacao, false));
        return retorno;
    }
}
