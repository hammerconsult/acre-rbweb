package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hudson on 09/12/15.
 */

@Stateless
public class ConfiguracaoLicitacaoFacade extends AbstractFacade<ConfiguracaoLicitacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SolicitacaoMaterialFacade solicitacaoMaterialFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private TipoDocumentoAnexoFacade tipoDocumentoAnexoFacade;


    public ConfiguracaoLicitacaoFacade() {
        super(ConfiguracaoLicitacao.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    @Override
    public ConfiguracaoLicitacao recuperar(Object id) {
        ConfiguracaoLicitacao config = super.recuperar(id);
        Hibernate.initialize(config.getConfigReservasDotacoes());
        Hibernate.initialize(config.getConfigSubstituicoesObjetoCompra());
        for (ConfiguracaoProcessoCompra configProcessoCompraUnidade : config.getConfigProcessoCompraUnidades()) {
            HierarquiaOrganizacional hierarquiaOrganizacional = hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), configProcessoCompraUnidade.getUnidadeOrganizacional(), sistemaFacade.getDataOperacao());
            configProcessoCompraUnidade.setHierarquiaOrganizacional(hierarquiaOrganizacional);
        }

        Hibernate.initialize(config.getUnidadesTercerializadasRh());
        for (UnidadeTercerializadaRh unid : config.getUnidadesTercerializadasRh()) {
            HierarquiaOrganizacional hierarquia = hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), unid.getUnidadeOrganizacional(), sistemaFacade.getDataOperacao());
            unid.setHierarquiaOrganizacional(hierarquia);
        }

        Hibernate.initialize(config.getUnidadesGrupoObjetoCompra());
        for (UnidadeGrupoObjetoCompra unid : config.getUnidadesGrupoObjetoCompra()) {
            HierarquiaOrganizacional hierarquia = hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), unid.getUnidadeOrganizacional(), sistemaFacade.getDataOperacao());
            unid.setHierarquiaOrganizacional(hierarquia);
        }

        Hibernate.initialize(config.getConfigAnexosLicitacoes());
        for (ConfiguracaoAnexoLicitacao configAnexo : config.getConfigAnexosLicitacoes()) {
            Hibernate.initialize(configAnexo.getTiposDocumentos());
        }
        return config;
    }

    public List<ConfiguracaoLicitacao> buscarConfiguracaoLicitacao(ConfiguracaoLicitacao config) {
        String hql = "select config from ConfiguracaoLicitacao config ";
        if (config.getId() != null) {
            hql += " where config = :config ";
        }
        Query q = em.createQuery(hql);
        if (config.getId() != null) {
            q.setParameter("config", config);
        }
        return q.getResultList();
    }

    public ConfiguracaoReservaDotacao buscarConfiguracaoReservaDotacao(ModalidadeLicitacao modalidade, TipoNaturezaDoProcedimentoLicitacao naturezaProc) {
        String sql = "select crd.* from configuracaoreservadotacao crd " +
            "           inner join configuracaolicitacao cl on crd.configuracaolicitacao_id = cl.id " +
            "         where to_date(:dataReferencia, 'dd/MM/yyyy') BETWEEN cl.iniciovigencia and coalesce(cl.finalvigencia, to_date(:dataReferencia, 'dd/MM/yyyy'))" +
            "           and crd.modalidadelicitacao = :modalidade ";
        sql += naturezaProc != null ? " and crd.naturezaprocedimento = :natureza " : "";
        Query q = em.createNativeQuery(sql, ConfiguracaoReservaDotacao.class);
        q.setParameter("modalidade", modalidade.name());
        q.setParameter("dataReferencia", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        if (naturezaProc != null) {
            q.setParameter("natureza", naturezaProc.name());
        }

        try {
            return (ConfiguracaoReservaDotacao) q.getResultList().get(0);
        } catch (Exception e) {
            throw new ValidacaoException("Não existe configuração de reserva de dotação para a " + modalidade.getDescricao() + "/" + naturezaProc.getDescricao()
                + ". Entre em contato com o suporte.");
        }
    }

    public ConfiguracaoSubstituicaoObjetoCompra buscarConfiguracaoSubstituicaoObjetoCompra(UsuarioSistema usuarioSistema) {
        String sql = " select config.* from configsubstituicaoobjcomp config " +
            "           inner join configuracaolicitacao cl on config.configuracaolicitacao_id = cl.id " +
            "         where to_date(:dataReferencia, 'dd/MM/yyyy') BETWEEN cl.iniciovigencia and coalesce(cl.finalvigencia, to_date(:dataReferencia, 'dd/MM/yyyy'))" +
            "           and config.usuariosistema_id = :idUsuario ";
        Query q = em.createNativeQuery(sql, ConfiguracaoSubstituicaoObjetoCompra.class);
        q.setParameter("idUsuario", usuarioSistema.getId());
        q.setParameter("dataReferencia", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        try {
            return (ConfiguracaoSubstituicaoObjetoCompra) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<ConfiguracaoAnexoLicitacaoTipoDocumento> buscarConfiguracaoAnexoLicitacaoTipoDocumento(TipoMovimentoProcessoLicitatorio tipoMovimento) {
        String sql = "select doc.* from ConfiguracaoAnexoLicitacao cal " +
            "           inner join configuracaolicitacao cl on cal.configuracaolicitacao_id = cl.id " +
            "           inner join CONFIGURACAOANEXOLICDOC doc on cal.id = doc.configuracaoanexolicitacao_id" +
            "         where to_date(:dataReferencia, 'dd/MM/yyyy') BETWEEN cl.iniciovigencia and coalesce(cl.finalvigencia, to_date(:dataReferencia, 'dd/MM/yyyy'))" +
            "           and cal.recursotela = :tipoMovimento ";
        Query q = em.createNativeQuery(sql, ConfiguracaoAnexoLicitacaoTipoDocumento.class);
        q.setParameter("tipoMovimento", tipoMovimento.name());
        q.setParameter("dataReferencia", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        try {
            return q.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<TipoDocumentoAnexo> buscarTipoDocumentoAnexo(TipoMovimentoProcessoLicitatorio tipoMovimento) {
        String sql = "select tipo.* from ConfiguracaoAnexoLicitacao cal " +
            "           inner join configuracaolicitacao cl on cal.configuracaolicitacao_id = cl.id " +
            "           inner join CONFIGURACAOANEXOLICDOC doc on cal.id = doc.configuracaoanexolicitacao_id " +
            "           inner join tipodocumentoanexo tipo on doc.tipodocumentoanexo_id = tipo.id " +
            "         where to_date(:dataReferencia, 'dd/MM/yyyy') BETWEEN cl.iniciovigencia and coalesce(cl.finalvigencia, to_date(:dataReferencia, 'dd/MM/yyyy'))" +
            "           and cal.recursotela = :tipoMovimento ";
        Query q = em.createNativeQuery(sql, TipoDocumentoAnexo.class);
        q.setParameter("tipoMovimento", tipoMovimento.name());
        q.setParameter("dataReferencia", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        try {
            return q.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public void validarAnexoObrigatorio(DetentorDocumentoLicitacao detentorDocumentoLicitacao, TipoMovimentoProcessoLicitatorio tipoMovimento) {
        ValidacaoException ve = new ValidacaoException();
        if (detentorDocumentoLicitacao != null) {
            boolean isTiposObrigatoriosNaLista = true;

            List<ConfiguracaoAnexoLicitacaoTipoDocumento> configAnexoTipoMovimento = buscarConfiguracaoAnexoLicitacaoTipoDocumento(tipoMovimento);
            for (ConfiguracaoAnexoLicitacaoTipoDocumento tipo : configAnexoTipoMovimento) {
                if (tipo.getObrigatorio()) {
                    boolean encontrouTipoObrigatorio = false;

                    if (!Util.isListNullOrEmpty(detentorDocumentoLicitacao.getDocumentoLicitacaoList())) {
                        for (DocumentoLicitacao doc : detentorDocumentoLicitacao.getDocumentoLicitacaoList()) {
                            if (tipo.getTipoDocumentoAnexo().getId().equals(doc.getTipoDocumentoAnexo().getId())) {
                                encontrouTipoObrigatorio = true;
                                break;
                            }
                        }
                    }

                    if (!encontrouTipoObrigatorio) {
                        isTiposObrigatoriosNaLista = false;
                        ve.adicionarMensagemDeCampoObrigatorio("É necessário incluir o anexo assinado do tipo " + tipo.getTipoDocumentoAnexo().getDescricao() + ".");
                    }
                }
            }

            if (!isTiposObrigatoriosNaLista) {
                ve.lancarException();
            }
        }
    }

    public ConfiguracaoProcessoCompra buscarConfiguracaoProcessoCompraVigente(String data, UnidadeOrganizacional unidade) {
        String sql = "select cpc.* from CONFIGPROCESSOCOMPRA cpc " +
            " inner join CONFIGURACAOLICITACAO cl on cpc.configuracaoLicitacao_id = cl.ID " +
            "where to_date(:data, 'dd/MM/yyyy') BETWEEN cl.INICIOVIGENCIA and coalesce(cl.FINALVIGENCIA, to_date(:data, 'dd/MM/yyyy'))" +
            "and cpc.UNIDADEORGANIZACIONAL_ID = :unidade";
        Query q = em.createNativeQuery(sql, ConfiguracaoProcessoCompra.class);
        q.setParameter("data", data);
        q.setParameter("unidade", unidade.getId());
        try {
            return (ConfiguracaoProcessoCompra) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public ConfiguracaoLicitacao buscarConfiguracao() {
        String sql = "select cl.* from CONFIGURACAOLICITACAO cl " +
            "          where to_date(:data, 'dd/MM/yyyy') BETWEEN cl.INICIOVIGENCIA and coalesce(cl.FINALVIGENCIA, to_date(:data, 'dd/MM/yyyy'))";
        Query q = em.createNativeQuery(sql, ConfiguracaoLicitacao.class);
        q.setParameter("data", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        try {
            return (ConfiguracaoLicitacao) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Date getDataReferenciaReservaDotacao() {
        String sql = "select cl.datareferenciareservadotacao from CONFIGURACAOLICITACAO cl " +
            "          where to_date(:data, 'dd/MM/yyyy') BETWEEN cl.INICIOVIGENCIA and coalesce(cl.FINALVIGENCIA, to_date(:data, 'dd/MM/yyyy'))";
        Query q = em.createNativeQuery(sql);
        q.setParameter("data", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        try {
            return (Date) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Boolean verificarUnidadeTercerializadaRh(UnidadeOrganizacional unidade) {
        String sql = " select unid.* from unidadetercerializadarh unid " +
            "           inner join configuracaolicitacao cl on unid.configuracaolicitacao_id = cl.id " +
            "          where to_date(:dataOperacao, 'dd/MM/yyyy') BETWEEN trunc(cl.iniciovigencia) and coalesce(trunc(cl.finalvigencia), to_date(:dataOperacao, 'dd/MM/yyyy'))" +
            "           and unid.unidadeorganizacional_id = :unidade";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("unidade", unidade.getId());
        q.setMaxResults(1);
        try {
            return q.getSingleResult() != null;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean verificarUnidadeGrupoObjetoCompra(UnidadeOrganizacional unidade) {
        String sql = " select unid.* from UNIDADEGRUPOOBJCOMP unid " +
            "           inner join configuracaolicitacao cl on unid.configuracaolicitacao_id = cl.id " +
            "          where to_date(:dataOperacao, 'dd/MM/yyyy') BETWEEN trunc(cl.iniciovigencia) and coalesce(trunc(cl.finalvigencia), to_date(:dataOperacao, 'dd/MM/yyyy'))" +
            "           and unid.unidadeorganizacional_id = :unidade";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("unidade", unidade.getId());
        q.setMaxResults(1);
        try {
            return q.getSingleResult() != null;
        } catch (Exception e) {
            return false;
        }
    }

    public SolicitacaoMaterialFacade getSolicitacaoMaterialFacade() {
        return solicitacaoMaterialFacade;
    }

    public TipoDocumentoAnexoFacade getTipoDocumentoAnexoFacade() {
        return tipoDocumentoAnexoFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public List<ConfiguracaoAnexoLicitacaoTipoDocumento> buscarConfiguracaoAnexoLicitacaoPorTipoDocumentoAnexo(TipoDocumentoAnexoPNCP tipoDocumentoAnexoPNCP){
        String sql = "select doc.* from ConfiguracaoAnexoLicitacao cal" +
            "             inner join configuracaolicitacao cl on cal.configuracaolicitacao_id = cl.id" +
            "             inner join CONFIGURACAOANEXOLICDOC doc on cal.id = doc.configuracaoanexolicitacao_id" +
            "             inner join tipodocumentoanexo tipo on doc.tipodocumentoanexo_id = tipo.id" +
            "         where lower(tipo.DESCRICAO) = lower(:tipoDocumentoAnexoPNCP) ";
        Query q = em.createNativeQuery(sql, ConfiguracaoAnexoLicitacaoTipoDocumento.class);
        q.setParameter("tipoDocumentoAnexoPNCP", tipoDocumentoAnexoPNCP.getDescricao());

        List retorno = q.getResultList();

        if (retorno == null) {
            retorno = Lists.newArrayList();
        }

        return retorno;
    }
}
