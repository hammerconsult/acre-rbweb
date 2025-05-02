package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfigContaDespesaTipoDocumento;
import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.ContaDespesa;
import br.com.webpublico.entidades.TipoDocumentoFiscal;
import br.com.webpublico.enums.TipoContaDespesa;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ConfigContaDespesaTipoDocumentoFacade extends AbstractFacade<ConfigContaDespesaTipoDocumento> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private TipoDocumentoFiscalFacade tipoDocumentoFiscalFacade;
    @EJB
    private UtilConfiguracaoEventoContabilFacade utilFacade;

    public ConfigContaDespesaTipoDocumentoFacade() {
        super(ConfigContaDespesaTipoDocumento.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<TipoDocumentoFiscal> buscarTiposDeDocumentosPorContaDeDespesa(ContaDespesa contaDespesa, TipoContaDespesa tipoContaDespesa, String filtro) {
        String sql = " select tdf.* from CONFIGCONTADESPESATIPODOC cfg " +
            " inner join tipoDocumentoFiscal tdf on cfg.tipoDocumentoFiscal_id = tdf.id " +
            " where cfg.tipoContaDespesa = :tipoConta " +
            "   and cfg.contaDespesa_id = :contaDespesa " +
            "   and (upper(tdf.codigo) like :parte or upper(tdf.descricao) like :parte) "+
            "   and to_date(:dataOperacao, 'dd/mm/yyyy') between cfg.inicioVigencia and coalesce(cfg.finalVigencia, to_date(:dataOperacao, 'dd/mm/yyyy')) ";
        Query q = em.createNativeQuery(sql, TipoDocumentoFiscal.class);
        q.setParameter("contaDespesa", contaDespesa.getId());
        q.setParameter("tipoConta", tipoContaDespesa.name());
        q.setParameter("parte", "%" + filtro.trim().toUpperCase() + "%");
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        return q.getResultList();
    }

    public List<TipoDocumentoFiscal> buscarTiposDeDocumentosPorContaDeDespesa(List<Long> contasDespesas, List<String> tiposContasDespesas, String filtro) {
        String sql = " select tdf.* from CONFIGCONTADESPESATIPODOC cfg " +
            " inner join tipoDocumentoFiscal tdf on cfg.tipoDocumentoFiscal_id = tdf.id " +
            " where cfg.tipoContaDespesa in :tiposContasDespesas " +
            "   and cfg.contaDespesa_id in :contasDespesas " +
            "   and (upper(tdf.codigo) like :parte or upper(tdf.descricao) like :parte) "+
            "   and to_date(:dataOperacao, 'dd/mm/yyyy') between cfg.inicioVigencia and coalesce(cfg.finalVigencia, to_date(:dataOperacao, 'dd/mm/yyyy')) ";
        Query q = em.createNativeQuery(sql, TipoDocumentoFiscal.class);
        q.setParameter("contasDespesas", contasDespesas);
        q.setParameter("tiposContasDespesas", tiposContasDespesas);
        q.setParameter("parte", "%" + filtro.trim().toUpperCase() + "%");
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        return q.getResultList();
    }

    public boolean hasConfiguracaoCadastrada(ConfigContaDespesaTipoDocumento configContaDespesaTipoDocumento) {
        String sql = " select * from CONFIGCONTADESPESATIPODOC cfg " +
            " where cfg.tipoContaDespesa = :tipoConta " +
            "   and cfg.contaDespesa_id = :contaDespesa " +
            "   and cfg.tipoDocumentoFiscal_id = :tipoDocumentoFiscal " +
            "   and to_date(:dataOperacao, 'dd/mm/yyyy') between cfg.inicioVigencia and coalesce(cfg.finalVigencia, to_date(:dataOperacao, 'dd/mm/yyyy')) ";
        if (configContaDespesaTipoDocumento.getId() != null) {
            sql += " and cfg.id <> :id ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("contaDespesa", configContaDespesaTipoDocumento.getContaDespesa().getId());
        q.setParameter("tipoConta", configContaDespesaTipoDocumento.getTipoContaDespesa().name());
        q.setParameter("tipoDocumentoFiscal", configContaDespesaTipoDocumento.getTipoDocumentoFiscal().getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(configContaDespesaTipoDocumento.getInicioVigencia()));
        if (configContaDespesaTipoDocumento.getId() != null) {
            q.setParameter("id", configContaDespesaTipoDocumento.getId());
        }
        return !q.getResultList().isEmpty();
    }

    public List<Conta> buscarContasDeDespesa(String filtro) {
        return contaFacade.listaFiltrandoContaDespesa(filtro, sistemaFacade.getExercicioCorrente());
    }

    public List<TipoDocumentoFiscal> buscarTiposDeDocumentosFiscais(String filtro) {
        return tipoDocumentoFiscalFacade.buscarTiposDeDocumentosAtivos(filtro.trim());
    }

    public List<TipoContaDespesa> buscarTiposDeContasDespesaNosFilhosDaConta(ContaDespesa contaDespesa) {
        return contaFacade.buscarTiposContasDespesaNosFilhosDaConta(contaDespesa);
    }

    public void encerrarVigencia(ConfigContaDespesaTipoDocumento entity) {
        utilFacade.validarEncerramentoVigencia(entity.getInicioVigencia(), entity.getFinalVigencia(), null);
        super.salvar(entity);
    }
}
