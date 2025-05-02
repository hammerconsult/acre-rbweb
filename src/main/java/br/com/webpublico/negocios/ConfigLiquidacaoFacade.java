package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfigLiquidacao;
import br.com.webpublico.entidades.ConfigLiquidacaoContaDesp;
import br.com.webpublico.entidades.ConfiguracaoEvento;
import br.com.webpublico.entidades.Conta;
import br.com.webpublico.enums.SubTipoDespesa;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoReconhecimentoObrigacaoPagar;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.DataUtil;
import com.google.common.base.Preconditions;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA. User: Romanini Date: 21/06/13 Time: 09:49 To
 * change this template use File | Settings | File Templates.
 */
@Stateless
public class ConfigLiquidacaoFacade extends AbstractFacade<ConfigLiquidacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private UtilConfiguracaoEventoContabilFacade utilFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public ConfigLiquidacaoFacade() {
        super(ConfigLiquidacao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public Object recuperar(Class entidade, Object id) {
        ConfigLiquidacao configLiquidacao = (ConfigLiquidacao) super.recuperar(ConfigLiquidacao.class, id);
        configLiquidacao.getConfigLiquidacaoContaDespesas().size();
        return configLiquidacao;
    }

    @Override
    public ConfigLiquidacao recuperar(Object id) {
        ConfigLiquidacao configLiquidacao = (ConfigLiquidacao) super.recuperar(ConfigLiquidacao.class, id);
        configLiquidacao.getConfigLiquidacaoContaDespesas().size();
        return configLiquidacao;
    }

    public ConfigLiquidacao buscarCDELiquidacao(Conta conta, TipoLancamento tipoLancamento,
                                                Date dataLancamento, SubTipoDespesa subTipoDespesa,
                                                TipoReconhecimentoObrigacaoPagar tipoReconhecimento) throws ExcecaoNegocioGenerica, NonUniqueResultException {
        String msgErro;
        try {
            Preconditions.checkNotNull(tipoLancamento, "Tipo de lançamento não foi informado.");
            Preconditions.checkNotNull(dataLancamento, " A data do lançamento não foi informada.");
            Preconditions.checkNotNull(conta.getId(), " A conta de despesa não foi informada.");
            Preconditions.checkNotNull(subTipoDespesa, " Subtipo de despesa não informado.");
            Preconditions.checkNotNull(tipoReconhecimento, " O tipo de reconhecimento não foi informado.");

            return buscarConfiguracao(conta, tipoLancamento, dataLancamento, subTipoDespesa, tipoReconhecimento);
        } catch (NoResultException nr) {
            msgErro = "Configuração de Evento não encontrada para a conta: " + conta
                + ", lançamento: " + tipoLancamento.getDescricao()
                + ", subtipo de despesa: " + subTipoDespesa.getDescricao()
                + ", tipo de reconhecimento: " + tipoReconhecimento.getDescricao()
                + "  na data: " + DataUtil.getDataFormatada(dataLancamento) + ".";
            throw new ExcecaoNegocioGenerica(msgErro);
        } catch (Exception e) {
            msgErro = e.getMessage();
            throw new ExcecaoNegocioGenerica(msgErro);
        }
    }

    private ConfigLiquidacao buscarConfiguracao(Conta conta, TipoLancamento tipoLancamento,
                                                Date data, SubTipoDespesa subTipoDespesa,
                                                TipoReconhecimentoObrigacaoPagar tipoReconhecimento) throws Exception {
        String sql = getSqlConfiguracao();
        Query q = em.createNativeQuery(sql, ConfigLiquidacao.class);
        q.setParameter("conta", conta.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(data));
        q.setParameter("tipoLancamento", tipoLancamento.name());
        q.setParameter("tipoReconhecimento", tipoReconhecimento.name());
        q.setParameter("subTipoDesp", subTipoDespesa.name());
        return (ConfigLiquidacao) q.getSingleResult();
    }

    private String getSqlConfiguracao() {
        return "SELECT CE.*, CLIQ.* "
            + " FROM CONFIGLIQUIDACAO CLIQ"
            + " INNER JOIN CONFIGURACAOEVENTO CE ON CLIQ.ID = CE.ID"
            + " INNER JOIN EVENTOCONTABIL EC ON CE.EVENTOCONTABIL_ID = EC.ID"
            + " INNER JOIN CONFIGLIQUIDACAOCONTADESP CC ON CC.CONFIGLIQUIDACAO_ID = CLIQ.ID"
            + " WHERE CC.CONTADESPESA_ID = :conta "
            + " AND to_date(:dataOperacao,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:dataOperacao,'dd/MM/yyyy')) "
            + " AND CE.TIPOLANCAMENTO = :tipoLancamento"
            + " AND CLIQ.SUBTIPODESPESA = :subTipoDesp"
            + " AND CLIQ.TIPORECONHECIMENTO = :tipoReconhecimento ";
    }

    public Boolean verificarConfiguracaoExistente(ConfiguracaoEvento configEvento, Date dataOperacao) {
        try {
            ConfigLiquidacao config = (ConfigLiquidacao) configEvento;
            String sql = getSqlConfiguracao();

            if (configEvento.getId() != null) {
                sql += " AND CE.ID <> :idConfiguracao";
            }
            Query q = em.createNativeQuery(sql, ConfigLiquidacao.class);
            q.setParameter("conta", config.getContaDespesa().getId());
            q.setParameter("tipoLancamento", config.getTipoLancamento().name());
            q.setParameter("tipoReconhecimento", config.getTipoReconhecimento().name());
            q.setParameter("subTipoDesp", config.getSubTipoDespesa().name());
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
    public void salvarNovo(ConfigLiquidacao entity) {
        validarCampos(entity);
        adicionarContaDespesa(entity);
        super.salvarNovo(entity);
    }

    private void adicionarContaDespesa(ConfigLiquidacao selecionado) {
        ConfigLiquidacaoContaDesp configContaDespesa = new ConfigLiquidacaoContaDesp();
        configContaDespesa.setConfigLiquidacao(selecionado);
        configContaDespesa.setContaDespesa(selecionado.getContaDespesa());
        selecionado.getConfigLiquidacaoContaDespesas().add(configContaDespesa);
    }

    public void salvar(ConfigLiquidacao selecionado) {
        validarCampos(selecionado);
        utilFacade.validarVigenciaEncerrada(selecionado.getInicioVigencia(), selecionado.getFimVigencia());
        selecionado.getConfigLiquidacaoContaDespesas().removeAll(selecionado.getConfigLiquidacaoContaDespesas());
        adicionarContaDespesa(selecionado);
        super.salvar(selecionado);
    }

    private void atualizarContaDeDespesa(ConfigLiquidacao selecionado) {
        selecionado.getConfigLiquidacaoContaDespesas().get(0).setContaDespesa(selecionado.getContaDespesa());
    }

    public void validarCampos(ConfigLiquidacao selecionado) {
        ValidacaoException ve = new ValidacaoException();
        selecionado.realizarValidacoes();
        if (selecionado.getSubTipoDespesa() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo subtipo de desepsa deve ser informado.");
        }
        if (selecionado.getContaDespesa() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo conta de despesa deve ser informado.");
        }
        if (selecionado.getTipoReconhecimento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo tipo de reconhecimento deve ser informado.");
        }
        ve.lancarException();
        validarConfiguracaoExistente(selecionado);
    }

    private void validarConfiguracaoExistente(ConfigLiquidacao selecionado) {
        ValidacaoException ve = new ValidacaoException();
        if (verificarConfiguracaoExistente(selecionado, sistemaFacade.getDataOperacao())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("" +
                "Existe uma configuração vigente para os parâmetros: "
                + " tipo lançamento: " + selecionado.getTipoLancamento().getDescricao()
                + ", subtipo de despesa: " + selecionado.getSubTipoDespesa().getDescricao()
                + ", tipo de reconhecimento: " + selecionado.getTipoReconhecimento().getDescricao()
                + " e conta de despesa: " + selecionado.getContaDespesa());
        }
        ve.lancarException();
    }

    public void encerrarVigencia(ConfigLiquidacao entity) {
        utilFacade.validarEncerramentoVigencia(entity.getInicioVigencia(), entity.getFimVigencia(), entity.getEventoContabil());
        super.salvar(entity);
    }

    public boolean isVigenciaEncerrada(Date fimVigencia) {
        return utilFacade.isVigenciaEncerrada(fimVigencia);
    }

    private void verifcarAlteracoesEvento(ConfigLiquidacao configuracaoNaoAlterada, ConfigLiquidacao selecionado) {
        if (selecionado.getId() != null) {
            boolean configAlterada = false;
            for (ConfigLiquidacaoContaDesp configContaDespesa : selecionado.getConfigLiquidacaoContaDespesas()) {
                if (!configuracaoNaoAlterada.getConfigLiquidacaoContaDespesas().contains(configContaDespesa)) {
                    configAlterada = true;
                }
            }
            if (!configuracaoNaoAlterada.getSubTipoDespesa().equals(selecionado.getSubTipoDespesa())) {
                configAlterada = true;
            }
            if (!configuracaoNaoAlterada.getTipoReconhecimento().equals(selecionado.getTipoReconhecimento())) {
                configAlterada = true;
            }
            utilFacade.verifcarAlteracoesEvento(configuracaoNaoAlterada, selecionado, configAlterada);
        }
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
