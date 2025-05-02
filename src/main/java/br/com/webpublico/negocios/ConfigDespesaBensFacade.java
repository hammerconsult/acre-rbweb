package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 18/02/14
 * Time: 18:07
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class ConfigDespesaBensFacade extends AbstractFacade<ConfigDespesaBens> {

    @EJB
    private ContaFacade contaFacade;
    @EJB
    private GrupoBemFacade grupoBemFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;


    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfigDespesaBensFacade() {
        super(ConfigDespesaBens.class);
    }

    public ConfigDespesaBens verificaConfiguracaoExistente(ConfigDespesaBens config, Date dataVigencia) {
        String sql = " SELECT CONFIG.*, GB.*  "
            + " FROM CONFIGDESPESABENS CONFIG "
            + " INNER JOIN GRUPOBEM GB ON CONFIG.GRUPOBEM_ID = GB.ID "
            + " INNER JOIN CONTADESPESA CD ON CONFIG.CONTADESPESA_ID = CD.ID "
            + " WHERE GB.ID = :grupoID "
            + " AND CONFIG.TIPOGRUPOBEM = :tipoGrupo"
            + " AND CD.ID = :idConta"
            + " AND to_date(:dataVigencia,'dd/MM/yyyy') between trunc(Config.INICIOVIGENCIA) AND coalesce(trunc(config.FIMVIGENCIA), to_date(:dataVigencia,'dd/MM/yyyy')) ";
        if (config != null) {
            if (config.getId() != null) {
                sql += " AND CONFIG.ID <> :configID ";
            }
        }
        Query q = em.createNativeQuery(sql, ConfigDespesaBens.class);
        q.setParameter("grupoID", config.getGrupoBem().getId());
        q.setParameter("tipoGrupo", config.getTipoGrupoBem().name());
        q.setParameter("idConta", config.getContaDespesa().getId());
        q.setParameter("dataVigencia", DataUtil.getDataFormatada(dataVigencia));
        if (config != null) {
            if (config.getId() != null) {
                q.setParameter("configID", config.getId());
            }
        }
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (ConfigDespesaBens) q.getSingleResult();
        }
        return null;
    }


    public Boolean verificarContaDespesaPorGrupo(ConfigDespesaBens config, Date dataVigencia) {
        String sql = " SELECT CONFIG.*  "
            + " FROM CONFIGDESPESABENS CONFIG "
            + " INNER JOIN GRUPOBEM GB ON CONFIG.GRUPOBEM_ID = GB.ID "
            + " INNER JOIN CONTADESPESA CD ON CONFIG.CONTADESPESA_ID = CD.ID "
            + " WHERE CD.ID = :idConta "
            + " AND to_date(:dataVigencia,'dd/MM/yyyy') between trunc(CONFIG.INICIOVIGENCIA) AND coalesce(trunc(CONFIG.FIMVIGENCIA), to_date(:dataVigencia,'dd/MM/yyyy')) ";
        if (config != null) {
            if (config.getId() != null) {
                sql += " AND CONFIG.ID <> :configID ";
            }
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("idConta", config.getContaDespesa().getId());
        q.setParameter("dataVigencia", DataUtil.getDataFormatada(dataVigencia));
        if (config != null) {
            if (config.getId() != null) {
                q.setParameter("configID", config.getId());
            }
        }
        if (!q.getResultList().isEmpty()) {
            return false;
        }
        return true;
    }

    public List<Conta> buscarContasPorGrupoBem(GrupoBem grupoBem, Date dataOperacao, Conta conta) {
        String sql = " SELECT conta.*, cd.migracao, cd.tipoContaDespesa, cd.codigoReduzido " +
            " FROM CONFIGDESPESABENS CONFIG " +
            " INNER JOIN GRUPOBEM GB ON CONFIG.GRUPOBEM_ID = GB.ID " +
            " INNER JOIN CONTA ON CONFIG.CONTADESPESA_ID = CONTA.ID " +
            " INNER JOIN CONTADESPESA CD ON cd.id = CONTA.ID " +
            "   WHERE GB.ID = :idGrupoBem " +
            " AND to_date(:dataOperacao,'dd/MM/yyyy') between trunc(CONFIG.INICIOVIGENCIA) AND coalesce(trunc(CONFIG.FIMVIGENCIA), to_date(:dataOperacao,'dd/MM/yyyy')) ";
        sql += conta != null ? "  and conta.codigo like :codigoConta" : "";
        Query q = em.createNativeQuery(sql, ContaDespesa.class);
        q.setParameter("idGrupoBem", grupoBem.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        if (conta != null) {
            q.setParameter("codigoConta", "%" + conta.getCodigoSemZerosAoFinal() + "%");
        }
        List<Conta> listaContas = q.getResultList();
        try {
            return listaContas;
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Nenhuma configuração vigente encontrada para grupo bem: <b> " + grupoBem + "</b>.");
        }
    }

    public GrupoBem buscarGrupoBemPorContaDespesa(Date dataOperacao, Conta conta) {
        String sql = " SELECT GB.* " +
            " FROM CONFIGDESPESABENS CONFIG " +
            " INNER JOIN GRUPOBEM GB ON CONFIG.GRUPOBEM_ID = GB.ID " +
            " INNER JOIN CONTA ON CONFIG.CONTADESPESA_ID = CONTA.ID " +
            " INNER JOIN CONTADESPESA CD ON cd.id = CONTA.ID " +
            "  where conta.codigo = :codigoConta " +
            " AND to_date(:dataOperacao,'dd/MM/yyyy') between trunc(CONFIG.INICIOVIGENCIA) AND coalesce(trunc(CONFIG.FIMVIGENCIA), to_date(:dataOperacao,'dd/MM/yyyy')) ";
        Query q = em.createNativeQuery(sql, GrupoBem.class);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        q.setParameter("codigoConta", conta.getCodigo());
        try {
            return (GrupoBem) q.getResultList().get(0);
        } catch (NonUniqueResultException e) {
            throw new ExcecaoNegocioGenerica("A conta de despesa: <b> " + conta + "</b> recuperou mais de um grupo patrimonial.");
        }
    }

    public List<GrupoBem> buscarGruposPorContaNivelElemento(Date dataOperacao, Conta conta) {
        String sql = " SELECT GB.* " +
            " FROM CONFIGDESPESABENS CONFIG " +
            " INNER JOIN GRUPOBEM GB ON CONFIG.GRUPOBEM_ID = GB.ID " +
            " INNER JOIN CONTA ON CONFIG.CONTADESPESA_ID = CONTA.ID " +
            "  where conta.codigo like :codigoConta" +
            " AND to_date(:dataOperacao,'dd/MM/yyyy') between trunc(CONFIG.INICIOVIGENCIA) AND coalesce(trunc(CONFIG.FIMVIGENCIA), to_date(:dataOperacao,'dd/MM/yyyy')) ";
        Query q = em.createNativeQuery(sql, GrupoBem.class);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        q.setParameter("codigoConta", conta.getCodigo().substring(0, 10) + "%");
        try {
            return q.getResultList();
        } catch (NonUniqueResultException e) {
            throw new ExcecaoNegocioGenerica("A conta de despesa: <b> " + conta + "</b> recuperou mais de um grupo patrimonial.");
        }
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    public GrupoBemFacade getGrupoBemFacade() {
        return grupoBemFacade;
    }
}
