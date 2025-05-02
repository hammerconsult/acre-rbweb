/*
 * Codigo gerado automaticamente em Tue May 24 13:43:55 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;


import br.com.webpublico.entidades.LoteMaterial;
import br.com.webpublico.entidades.Material;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.entidadesauxiliares.FormularioLoteMaterial;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class LoteMaterialFacade extends AbstractFacade<LoteMaterial> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LoteMaterialFacade() {
        super(LoteMaterial.class);
    }

    @Override
    public void salvar(LoteMaterial entity) {
        Material m = em.find(Material.class, entity.getMaterial().getId());
        m.setControleDeLote(Boolean.TRUE);
        super.salvar(entity);
    }

    public List<LoteMaterial> buscarFiltrandoPorMaterial(String parte, Material m) {
        String hql = "  from LoteMaterial " +
            "      WHERE material = :material" +
            "          AND (identificacao LIKE :parte" +
            "               OR validade LIKE :parte)";

        Query q = em.createQuery(hql);
        q.setParameter("material", m);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");

        return q.getResultList();
    }

    public List<FormularioLoteMaterial> buscarLoteMaterialPorMaterialAndLocalEstoqueAndOrcamentariaAndFiltro(Long id_localEstoque, Long id_material, Long idUnidadeOrc, String parte) {
        String sql = "  SELECT DISTINCT       " +
            "           LE.ID AS ID_LOCALESTOQUE,    " +
            "           LM.ID AS ID_LOTE," +
            "           LM.identificacao,  " +
            "           LM.validade,       " +
            "           SUM(ELM.quantidade),       " +
            "           0 AS DESEJADA ,   " +
            "           UM.MASCARAQUANTIDADE AS MASCARA_QTDE,    " +
            "           UM.MASCARAVALORUNITARIO AS MASCARA_VALOR " +
            "         FROM LOTEMATERIAL LM " +
            "   INNER JOIN ESTOQUELOTEMATERIAL ELM ON ELM.LOTEMATERIAL_ID = LM.ID " +
            "   inner join ESTOQUE E on E.id = ELM.ESTOQUE_ID  " +
            "   inner join LOCALESTOQUEORCAMENTARIO LTO on LTO.id = E.LOCALESTOQUEORCAMENTARIO_ID " +
            "   INNER JOIN LOCALESTOQUE LE ON LE.ID = LTO.LOCALESTOQUE_ID " +
            "   INNER JOIN MATERIAL MAT ON MAT.ID = E.MATERIAL_ID " +
            "   INNER JOIN UNIDADEMEDIDA UM ON MAT.UNIDADEMEDIDA_ID = UM.ID " +
            "        WHERE E.MATERIAL_ID = :material_id " +
            "          AND LE.ID = :local_id" +
            "          AND (LOWER(LM.IDENTIFICACAO) LIKE :parte " +
            "               OR LM.VALIDADE LIKE :parte)" +
            "          AND e.dataEstoque = (SELECT MAX(e2.dataestoque)  " +
            "                              FROM estoque e2  " +
            "                              WHERE E2.LOCALESTOQUEORCAMENTARIO_ID = E.LOCALESTOQUEORCAMENTARIO_ID  " +
            "                              AND E2.MATERIAL_ID = E.MATERIAL_ID  " +
            "                              AND TRUNC(E2.DATAESTOQUE) <= :dataAtual)";
        if (idUnidadeOrc != null) {
            sql += " AND LTO.UNIDADEORCAMENTARIA_ID = :orc ";
        }
        sql += "  group by LM.identificacao, LM.validade, 4, LE.ID, LM.ID, UM.MASCARAQUANTIDADE, UM.MASCARAVALORUNITARIO ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("material_id", id_material);
        q.setParameter("local_id", id_localEstoque);
        if (idUnidadeOrc != null) {
            q.setParameter("orc", idUnidadeOrc);
        }
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("dataAtual", DataUtil.dataSemHorario(sistemaFacade.getDataOperacao()));
        return FormularioLoteMaterial.preencherFormularioLoteMaterialApartirDeArrayObject(q.getResultList());
    }

    public List<LoteMaterial> listaFiltrandoPorMaterialEUnidade(String parte, Material material, UnidadeOrganizacional uo) {
        String hql = "   select distinct lm "
            + "        from LoteMaterial lm, Estoque e, LocalEstoque le, EstoqueLoteMaterial elm "
            + "       where le.unidadeOrganizacional = :unidade "
            + "         and e.localEstoqueOrcamentario.localEstoque = le "
            + "         and e.material = :material "
            + "         and elm.estoque = e "
            + "         and lm.material = :material "
            + "         and lower(lm.identificacao) like :parte "
            + "         and e.dataEstoque = (select max(e.dataEstoque) "
            + "                                from Estoque e "
            + "                               where e.material = :material)"
            + "    order by lm.validade";

        Query q = em.createQuery(hql);
        q.setParameter("material", material);
        q.setParameter("unidade", uo);
        q.setParameter("parte", "%" + parte + "%");

        return q.getResultList();
    }

    public List<LoteMaterial> recuperaLotesPorMaterialEUnidadeMesmoQueNaoExistaEstoque(String parte, Material material, UnidadeOrganizacional uo) {
        String hql = "   select distinct lm "
            + "        from LoteMaterial lm "
            + "       where lm.material = :material "
            + "         and lower(lm.identificacao) like :parte "
            + "    order by lm.validade";

        Query q = em.createQuery(hql);
        q.setParameter("material", material);
        q.setParameter("parte", "%" + parte + "%");

        return q.getResultList();
    }
}
