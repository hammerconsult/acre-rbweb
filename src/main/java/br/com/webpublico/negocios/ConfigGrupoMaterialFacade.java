package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfigGrupoMaterial;
import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.ContaDespesa;
import br.com.webpublico.entidades.GrupoMaterial;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: usuario
 * Date: 27/05/14
 * Time: 11:07
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class ConfigGrupoMaterialFacade extends AbstractFacade<ConfigGrupoMaterial> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private GrupoMaterialFacade grupoMaterialFacade;

    public ConfigGrupoMaterialFacade() {
        super(ConfigGrupoMaterial.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfigGrupoMaterial verificaConfiguracaoExistente(ConfigGrupoMaterial config, Date dataVigencia) {
        String sql = " SELECT CONFIG.*, GM.*  "
            + " FROM CONFIGGRUPOMATERIAL CONFIG  "
            + " INNER JOIN GRUPOMATERIAL GM ON CONFIG.GRUPOMATERIAL_ID = GM.ID  "
            + " INNER JOIN CONTADESPESA CD ON CONFIG.CONTADESPESA_ID = CD.ID "
            + " WHERE GM.ID = :gruboID "
            + " AND CD.ID = :contaID "
            + " and config.TIPOESTOQUE like '" + config.getTipoEstoque().name() + "'"
            + " AND to_date(:dataVigencia, 'dd/mm/yyyy') between trunc(CONFIG.INICIOVIGENCIA) AND coalesce(trunc(CONFIG.FIMVIGENCIA), to_date(:dataVigencia, 'dd/mm/yyyy')) ";
        if (config.getId() != null) {
            sql += " AND CONFIG.ID <> :configID ";
        }
        Query q = em.createNativeQuery(sql, ConfigGrupoMaterial.class);
        q.setParameter("gruboID", config.getGrupoMaterial().getId());
        q.setParameter("contaID", config.getContaDespesa().getId());
        q.setParameter("dataVigencia", DataUtil.getDataFormatada(dataVigencia));
        if (config.getId() != null) {
            q.setParameter("configID", config.getId());
        }
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return (ConfigGrupoMaterial) q.getSingleResult();
        }
        return null;
    }

    public Boolean verificarContaDespesaPorGrupo(ConfigGrupoMaterial config, Date dataVigencia) {
        String sql = " SELECT CONFIG.* "
            + " FROM CONFIGGRUPOMATERIAL CONFIG  "
            + " INNER JOIN GRUPOMATERIAL GM ON CONFIG.GRUPOMATERIAL_ID = GM.ID  "
            + " INNER JOIN CONTADESPESA CD ON CONFIG.CONTADESPESA_ID = CD.ID "
            + " WHERE CD.ID = :contaID "
            + " AND to_date(:dataVigencia, 'dd/mm/yyyy') between trunc(CONFIG.INICIOVIGENCIA) AND coalesce(trunc(CONFIG.FIMVIGENCIA), to_date(:dataVigencia, 'dd/mm/yyyy')) ";
        if (config.getId() != null) {
            sql += " AND CONFIG.ID <> :configID ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("contaID", config.getContaDespesa().getId());
        q.setParameter("dataVigencia", DataUtil.getDataFormatada(dataVigencia));
        if (config.getId() != null) {
            q.setParameter("configID", config.getId());
        }
        if (!q.getResultList().isEmpty()) {
            return false;
        }
        return true;
    }

    public List<Conta> buscarContasDespesaPorGrupoMaterial(GrupoMaterial grupoMaterial, Date dataOperacao, Conta conta) {
        String sql = " SELECT conta.*, cd.migracao, cd.tipoContaDespesa, cd.codigoReduzido "
            + " FROM CONFIGGRUPOMATERIAL CONFIG  "
            + " INNER JOIN GRUPOMATERIAL GM ON CONFIG.GRUPOMATERIAL_ID = GM.ID  "
            + " INNER JOIN CONTA ON CONFIG.CONTADESPESA_ID = CONTA.ID "
            + " INNER JOIN CONTADESPESA CD ON cd.id = CONTA.ID "
            + " WHERE GM.ID = :idGrupo "
            + " AND to_date(:dataOperacao, 'dd/mm/yyyy') between trunc(CONFIG.INICIOVIGENCIA) AND coalesce(trunc(CONFIG.FIMVIGENCIA), to_date(:dataOperacao, 'dd/mm/yyyy')) ";
        sql += conta != null ? " and conta.codigo like :codigoConta" : "";
        Query q = em.createNativeQuery(sql, ContaDespesa.class);
        q.setParameter("idGrupo", grupoMaterial.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        if (conta != null) {
            q.setParameter("codigoConta", "%" + conta.getCodigoSemZerosAoFinal() + "%");
        }
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return (List<Conta>) resultList;
        }
        return null;
    }

    public GrupoMaterial buscarGrupoPorConta(Conta conta) {
        String sql = " SELECT GM.* "
            + " FROM CONFIGGRUPOMATERIAL CONFIG  "
            + " INNER JOIN GRUPOMATERIAL GM ON CONFIG.GRUPOMATERIAL_ID = GM.ID  "
            + " INNER JOIN CONTA ON CONFIG.CONTADESPESA_ID = CONTA.ID "
            + " INNER JOIN CONTADESPESA CD ON cd.id = CONTA.ID "
            + " WHERE conta.CODIGO = :codigoConta "
            + " AND to_date(:dataOperacao, 'dd/mm/yyyy') between trunc(CONFIG.INICIOVIGENCIA) AND coalesce(trunc(CONFIG.FIMVIGENCIA), to_date(:dataOperacao, 'dd/mm/yyyy')) ";
        Query q = em.createNativeQuery(sql, GrupoMaterial.class);
        q.setParameter("codigoConta", conta.getCodigo());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(new Date()));
        try {
            return (GrupoMaterial) q.getResultList().get(0);
        } catch (NonUniqueResultException e) {
            throw new ExcecaoNegocioGenerica("A conta de despesa: <b> " + conta + "</b> recuperou mais de um grupo material.");
        } catch (NoResultException | IndexOutOfBoundsException ex) {
            throw new ExcecaoNegocioGenerica("Nenhum grupo material encontrado.");
        }
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    public GrupoMaterialFacade getGrupoMaterialFacade() {
        return grupoMaterialFacade;
    }
}
