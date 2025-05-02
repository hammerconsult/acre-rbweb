package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfigObrigacaoAPagar;
import br.com.webpublico.entidades.ConfiguracaoEvento;
import br.com.webpublico.entidades.Conta;
import br.com.webpublico.enums.*;
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

/**
 * Created by mga on 22/06/2017.
 */
@Stateless
public class ConfiguracaoObrigacaoAPagarFacade extends AbstractFacade<ConfigObrigacaoAPagar> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private UtilConfiguracaoEventoContabilFacade utilFacade;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public ConfiguracaoObrigacaoAPagarFacade() {
        super(ConfigObrigacaoAPagar.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfigObrigacaoAPagar buscarCDEObrigacaoPagar(Conta conta, TipoLancamento tipoLancamento,
                                                         Date data, TipoContaDespesa tipoContaDespesa,
                                                         SubTipoDespesa subTipoDespesa,
                                                         TipoReconhecimentoObrigacaoPagar tipoReconhecimento,
                                                         CategoriaOrcamentaria categoriaOrcamentaria) {
        String msgErro;
        try {
            Preconditions.checkNotNull(conta.getId(), " A conta de despesa não informada para recuperar o evento.");
            Preconditions.checkNotNull(tipoLancamento, "Tipo de Lançamento não informado para recuperar o evento.");
            Preconditions.checkNotNull(tipoContaDespesa, " Tipo de Despesa não informado para recuperar o evento.");
            Preconditions.checkNotNull(subTipoDespesa, " Sub-Tipo de Despesa não informado para recuperar o evento.");
            Preconditions.checkNotNull(tipoReconhecimento, " Tipo de reconhecimento não informado para recuperar o evento.");
            Preconditions.checkNotNull(categoriaOrcamentaria, " Categoria Orçamentária não informado para recuperar o evento.");
            try {
                return buscarConfiguracaoObrigacaoPagar(conta, tipoLancamento, data, tipoContaDespesa, subTipoDespesa, tipoReconhecimento, categoriaOrcamentaria);
            } catch (NoResultException nr) {
                return buscarConfiguracaoObrigacaoPagar(conta, tipoLancamento, data, TipoContaDespesa.NAO_APLICAVEL, SubTipoDespesa.NAO_APLICAVEL, tipoReconhecimento, categoriaOrcamentaria);
            }
        } catch (NoResultException nr) {
            msgErro = "Configuração de Evento não encontrada para a conta: " + conta
                + ", lançamento: " + tipoLancamento.getDescricao()
                + ", tipo de Despesa: " + tipoContaDespesa.getDescricao()
                + ", subtipo de despesa: " + subTipoDespesa.getDescricao()
                + ", tipo de reconhecimento: " + tipoReconhecimento.getDescricao()
                + ", categoria orçamentária: " + categoriaOrcamentaria.getDescricao()
                + "  na data: " + DataUtil.getDataFormatada(data) + ".";
            throw new ExcecaoNegocioGenerica(msgErro);
        } catch (Exception e) {
            msgErro = e.getMessage();
            throw new ExcecaoNegocioGenerica(msgErro);
        }
    }

    private ConfigObrigacaoAPagar buscarConfiguracaoObrigacaoPagar(Conta conta,
                                                                   TipoLancamento tipoLancamento,
                                                                   Date dataOperacao,
                                                                   TipoContaDespesa tipoContaDespesa,
                                                                   SubTipoDespesa subTipoDespesa,
                                                                   TipoReconhecimentoObrigacaoPagar tipoReconhecimento,
                                                                   CategoriaOrcamentaria categoriaOrcamentaria) {

        String sql = getSqlBuscarConfiguracao();
        Query q = em.createNativeQuery(sql, ConfigObrigacaoAPagar.class);
        q.setParameter("conta", conta.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        q.setParameter("tipoLancamento", tipoLancamento.name());
        q.setParameter("tipoReconhecimento", tipoReconhecimento.name());
        q.setParameter("tipoConta", tipoContaDespesa.name());
        q.setParameter("categoriaOrcamentaria", categoriaOrcamentaria.name());
        q.setParameter("subTipoDespesa", subTipoDespesa.name());
        return (ConfigObrigacaoAPagar) q.getSingleResult();
    }

    private String getSqlBuscarConfiguracao() {
        return " SELECT CE.*, CONFIG.* "
            + " FROM CONFIGOBRIGACAOAPAGAR CONFIG "
            + " INNER JOIN CONFIGURACAOEVENTO CE ON CONFIG.ID = CE.ID "
            + " INNER JOIN EVENTOCONTABIL EC ON CE.EVENTOCONTABIL_ID = EC.ID"
            + " WHERE CONFIG.CONTA_ID = :conta "
            + " AND to_date(:dataOperacao,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:dataOperacao,'dd/MM/yyyy')) "
            + " AND CE.TIPOLANCAMENTO = :tipoLancamento"
            + " AND CONFIG.SUBTIPODESPESA = :subTipoDespesa"
            + " AND CONFIG.tipoContaDespesa = :tipoConta"
            + " AND CONFIG.categoriaOrcamentaria = :categoriaOrcamentaria "
            + " AND CONFIG.TIPORECONHECIMENTO = :tipoReconhecimento ";
    }

    public Boolean verificarConfiguracaoExistente(ConfiguracaoEvento configEvento, Date dataOperacao) {
        try {
            ConfigObrigacaoAPagar config = (ConfigObrigacaoAPagar) configEvento;
            String sql = getSqlBuscarConfiguracao();

            if (configEvento.getId() != null) {
                sql += " AND CE.ID <> :idConfiguracao";
            }
            Query q = em.createNativeQuery(sql, ConfigObrigacaoAPagar.class);
            q.setParameter("conta", config.getConta().getId());
            q.setParameter("subTipoDespesa", config.getSubTipoDespesa().name());
            q.setParameter("tipoLancamento", config.getTipoLancamento().name());
            q.setParameter("tipoConta", config.getTipoContaDespesa().name());
            q.setParameter("tipoReconhecimento", config.getTipoReconhecimento().name());
            q.setParameter("categoriaOrcamentaria", config.getCategoriaOrcamentaria().name());
            q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
            if (configEvento.getId() != null) {
                q.setParameter("idConfiguracao", configEvento.getId());
            }
            return q.getResultList() != null && !q.getResultList().isEmpty();
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    @Override
    public void salvarNovo(ConfigObrigacaoAPagar entity) {
        validarCampos(entity);
        super.salvarNovo(entity);
    }

    public void salvar(ConfigObrigacaoAPagar configuracaoNaoAlterado, ConfigObrigacaoAPagar selecionado) {
        validarCampos(selecionado);
        utilFacade.validarVigenciaEncerrada(selecionado.getInicioVigencia(), selecionado.getFimVigencia());
        super.salvar(selecionado);
    }

    public void validarCampos(ConfigObrigacaoAPagar selecionado) {
        ValidacaoException ve = new ValidacaoException();
        selecionado.realizarValidacoes();
        if (selecionado.getTipoReconhecimento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo tipo de reconhecimento deve ser informado.");
        }
        if (selecionado.getCategoriaOrcamentaria() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Categoria Orçamentária deve ser informado.");
        }
        if (selecionado.getSubTipoDespesa() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo tipo de despesa deve ser informado.");
        }
        if (selecionado.getSubTipoDespesa() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo subtipo de despesa deve ser informado.");
        }
        if (selecionado.getConta() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo conta de despesa deve ser informado.");
        }
        ve.lancarException();
        validarConfiguracaoExistente(selecionado);
    }

    private void validarConfiguracaoExistente(ConfigObrigacaoAPagar selecionado) {
        ValidacaoException ve = new ValidacaoException();
        if (verificarConfiguracaoExistente(selecionado, sistemaFacade.getDataOperacao())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("" +
                "Existe uma configuração vigente para os parâmetros: "
                + ", tipo lançamento: " + selecionado.getTipoLancamento().getDescricao()
                + ", tipo de reconhecimento: " + selecionado.getTipoReconhecimento().getDescricao()
                + ", subtipo despesa: " + selecionado.getSubTipoDespesa().getDescricao()
                + ", tipo de despesa: " + selecionado.getTipoContaDespesa().getDescricao()
                + ", categoria orçamentária: " + selecionado.getCategoriaOrcamentaria().getDescricao()
                + " e conta de despesa: " + selecionado.getConta());
        }
        ve.lancarException();
    }

    public void encerrarVigencia(ConfigObrigacaoAPagar entity) {
        utilFacade.validarEncerramentoVigencia(entity.getInicioVigencia(), entity.getFimVigencia(), entity.getEventoContabil());
        super.salvar(entity);
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }
}
