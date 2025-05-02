package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfigTipoObjetoCompra;
import br.com.webpublico.entidades.Conta;
import br.com.webpublico.enums.SubTipoObjetoCompra;
import br.com.webpublico.enums.TipoObjetoCompra;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.DataUtil;
import com.google.common.base.Preconditions;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

/**
 * Created by mga on 19/02/2018.
 */
@Stateless
public class ConfigTipoObjetoCompraFacade extends AbstractFacade<ConfigTipoObjetoCompra> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ContaFacade contaDespesaFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfigTipoObjetoCompraFacade() {
        super(ConfigTipoObjetoCompra.class);
    }

    @Override
    public ConfigTipoObjetoCompra recuperar(Object id) {
        ConfigTipoObjetoCompra entity = super.recuperar(id);
        entity.getContasDespesa().size();
        return entity;
    }

    public ConfigTipoObjetoCompra recuperarConfiguracaoVigente(TipoObjetoCompra tipoObjetoCompra, SubTipoObjetoCompra subTipoObjetoCompra, Date dataOperacao) {
        Preconditions.checkNotNull(tipoObjetoCompra, "Tipo de objeto de compra está nulo.");
        Preconditions.checkNotNull(subTipoObjetoCompra, "Subtipo de objeto de compra está nulo.");
        String sql = getSql();
        try {
            Query consulta = em.createNativeQuery(sql, ConfigTipoObjetoCompra.class);
            consulta.setParameter("tipoObjeto", tipoObjetoCompra.name());
            consulta.setParameter("subtipoObjeto", subTipoObjetoCompra.name());
            consulta.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));

            ConfigTipoObjetoCompra retorno = (ConfigTipoObjetoCompra) consulta.getSingleResult();
            retorno.getContasDespesa().size();
            return retorno;
        } catch (NoResultException nre) {
            throw new ValidacaoException("Configuração de tipo objeto de compra/conta de despesa não encontrada para os parâmetros. " +
                " Tipo Objeto de Compra: " + tipoObjetoCompra.getDescricao() +
                " ; Subtipo Objeto de Compra: " + subTipoObjetoCompra.getDescricao() +
                " na data: " + DataUtil.getDataFormatada(dataOperacao) + ".");
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    public List<ConfigTipoObjetoCompra> buscarConfiguracoesVigente(Date dataOperacao, TipoObjetoCompra tipoObjetoCompra) {
        try {
            String sql = "" +
                "   select config.* " +
                "   from configtipoobjetocompra config " +
                "    where config.tipoobjetocompra = :tipoObjeto" +
                "     and to_date(:dataOperacao,'dd/MM/yyyy') between trunc(config.iniciovigencia) and coalesce(trunc(config.fimvigencia), to_date(:dataOperacao,'dd/MM/yyyy'))" +
                "";
            Query consulta = em.createNativeQuery(sql, ConfigTipoObjetoCompra.class);
            consulta.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
            consulta.setParameter("tipoObjeto", tipoObjetoCompra.name());
            return consulta.getResultList();
        } catch (NoResultException nre) {
            throw new ExcecaoNegocioGenerica("Configuração de tipo objeto de compra/conta de despesa não encontrada na data: " + DataUtil.getDataFormatada(dataOperacao) + ".");
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    private String getSql() {
        return " " +
            "   select " +
            "     config.* " +
            "   from configtipoobjetocompra config " +
            "   where config.tipoobjetocompra = :tipoObjeto " +
            "     and config.subtipoobjetocompra = :subtipoObjeto" +
            "     and to_date(:dataOperacao,'dd/MM/yyyy') between trunc(config.iniciovigencia) and coalesce(trunc(config.fimvigencia), to_date(:dataOperacao,'dd/MM/yyyy')) ";
    }

    public boolean verificarConfiguracaoVigente(ConfigTipoObjetoCompra config) {
        String sql = getSql();
        if (config.getId() != null) {
            sql += " and config.id <> :id";
        }
        Query consulta = em.createNativeQuery(sql, ConfigTipoObjetoCompra.class);
        consulta.setMaxResults(1);
        consulta.setParameter("tipoObjeto", config.getTipoObjetoCompra().name());
        consulta.setParameter("subtipoObjeto", config.getSubtipoObjetoCompra().name());
        consulta.setParameter("dataOperacao", DataUtil.getDataFormatada(config.getInicioVigencia()));
        if (config.getId() != null) {
            consulta.setParameter("id", config.getId());
        }
        return !consulta.getResultList().isEmpty();
    }

    public TipoObjetoCompra getTipoObjetoCompra(Date dataOperacao, Conta conta) {
        String sql = " " +
            "   select distinct cf.tipoObjetoCompra from ConfigTipoObjetoCompra cf " +
            "     inner join cf.contasDespesa tipo " +
            "     inner join tipo.contaDespesa c" +
            "   where to_date(:dataOperacao, 'dd/mm/yyyy') between trunc(cf.inicioVigencia) AND coalesce(trunc(cf.fimVigencia), to_date(:dataOperacao, 'dd/mm/yyyy')) " +
            "   and c.codigo like :codigoConta ";
        Query q = em.createQuery(sql, TipoObjetoCompra.class);
        q.setParameter("codigoConta", conta.getCodigo().substring(0, 10) + "%");
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        try {
            return (TipoObjetoCompra) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public ContaFacade getContaDespesaFacade() {
        return contaDespesaFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }
}
