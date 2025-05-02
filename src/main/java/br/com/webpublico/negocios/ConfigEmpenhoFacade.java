package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfigEmpenho;
import br.com.webpublico.entidades.ConfigEmpenhoContaDesp;
import br.com.webpublico.entidades.ConfiguracaoEvento;
import br.com.webpublico.entidades.Conta;
import br.com.webpublico.enums.SubTipoDespesa;
import br.com.webpublico.enums.TipoContaDespesa;
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
 * Created with IntelliJ IDEA.
 * User: Romanini
 * Date: 20/06/13
 * Time: 15:12
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class ConfigEmpenhoFacade extends AbstractFacade<ConfigEmpenho> {

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

    public ConfigEmpenhoFacade() {
        super(ConfigEmpenho.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public Object recuperar(Class entidade, Object id) {
        ConfigEmpenho configEmpenho = (ConfigEmpenho) super.recuperar(ConfigEmpenho.class, id);
        configEmpenho.getConfigEmpenhoContaDespesas().size();
        return configEmpenho;
    }

    @Override
    public ConfigEmpenho recuperar(Object id) {
        ConfigEmpenho configEmpenho = (ConfigEmpenho) super.recuperar(ConfigEmpenho.class, id);
        configEmpenho.getConfigEmpenhoContaDespesas().size();
        return configEmpenho;
    }

    public ConfigEmpenho buscarCDEEmpenho(Conta conta, TipoLancamento tipoLancamento, Date data, TipoReconhecimentoObrigacaoPagar tipoReconhecimento, TipoContaDespesa tipoContaDespesa, SubTipoDespesa subTipoDespesa) throws ExcecaoNegocioGenerica, NonUniqueResultException {
        String msgErro;
        try {
            Preconditions.checkNotNull(tipoLancamento, "Tipo de lançamento não foi informado.");
            Preconditions.checkNotNull(data, " A data do lançamento não foi informada.");
            Preconditions.checkNotNull(conta.getId(), " A conta de despesa não foi informada.");
            Preconditions.checkNotNull(tipoReconhecimento, " O tipo de reconhecimento não foi informado.");
            Preconditions.checkNotNull(tipoContaDespesa, " O tipo de despesa não foi informado.");
            Preconditions.checkNotNull(subTipoDespesa, " O subtipo de despesa não foi informado.");
            try {
                return buscarConfiguracaoEmpenho(conta, tipoLancamento, data, tipoReconhecimento, tipoContaDespesa, subTipoDespesa);
            } catch (NoResultException nr) {
                return buscarConfiguracaoEmpenho(conta, tipoLancamento, data, tipoReconhecimento, TipoContaDespesa.NAO_APLICAVEL, SubTipoDespesa.NAO_APLICAVEL);
            }
        } catch (NoResultException nr) {
            msgErro = "Configuração de Evento não encontrada para a conta: " + conta
                + ", lançamento: " + tipoLancamento.getDescricao()
                + ", tipo de reconhecimento: " + tipoReconhecimento.getDescricao()
                + ", tipo de Despesa: " + tipoContaDespesa.getDescricao()
                + ", Subtipo de Despesa: " + subTipoDespesa.getDescricao()
                + "  na data: " + DataUtil.getDataFormatada(data) + ".";
            throw new ExcecaoNegocioGenerica(msgErro);
        } catch (Exception e) {
            msgErro = e.getMessage();
            throw new ExcecaoNegocioGenerica(msgErro);
        }
    }

    private ConfigEmpenho buscarConfiguracaoEmpenho(Conta conta, TipoLancamento tipoLancamento, Date data, TipoReconhecimentoObrigacaoPagar tipoReconhecimento, TipoContaDespesa tipoContaDespesa, SubTipoDespesa subTipoDespesa) throws Exception {
        String sql = getSqlConfiguracao();
        Query q = em.createNativeQuery(sql, ConfigEmpenho.class);
        q.setParameter("conta", conta.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(data));
        q.setParameter("tipoLancamento", tipoLancamento.name());
        q.setParameter("tipoReconhecimento", tipoReconhecimento.name());
        q.setParameter("subTipo", subTipoDespesa.name());
        q.setParameter("tipoConta", tipoContaDespesa.name());
        return (ConfigEmpenho) q.getSingleResult();
    }

    private String getSqlConfiguracao() {
        return "SELECT CE.*, CEMP.* "
            + " FROM CONFIGEMPENHO CEMP"
            + " INNER JOIN CONFIGURACAOEVENTO CE ON CEMP.ID = CE.ID"
            + " INNER JOIN EVENTOCONTABIL EC ON CE.EVENTOCONTABIL_ID = EC.ID"
            + " INNER JOIN CONFIGEMPENHOCONTADESP CC ON CC.CONFIGEMPENHO_ID = CEMP.ID"
            + " WHERE CC.CONTADESPESA_ID = :conta "
            + " and CEMP.TIPOCONTADESPESA = :tipoConta "
            + " and CEMP.SUBTIPODESPESA = :subTipo "
            + " AND to_date(:dataOperacao,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:dataOperacao,'dd/MM/yyyy')) "
            + " AND CE.TIPOLANCAMENTO = :tipoLancamento "
            + " AND CEMP.TIPORECONHECIMENTO = :tipoReconhecimento ";
    }

    public Boolean verificarConfiguracaoExistente(ConfiguracaoEvento configEvento, Date dataOperacao) {
        try {
            ConfigEmpenho config = (ConfigEmpenho) configEvento;
            String sql = getSqlConfiguracao();

            if (configEvento.getId() != null) {
                sql += " AND CE.ID <> :idConfiguracao";
            }
            Query q = em.createNativeQuery(sql, ConfigEmpenho.class);
            q.setParameter("conta", config.getContaDespesa().getId());
            q.setParameter("tipoLancamento", config.getTipoLancamento().name());
            q.setParameter("tipoReconhecimento", config.getTipoReconhecimento().name());
            q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
            q.setParameter("subTipo", config.getSubTipoDespesa().name());
            q.setParameter("tipoConta", config.getTipoContaDespesa().name());
            if (configEvento.getId() != null) {
                q.setParameter("idConfiguracao", configEvento.getId());
            }
            return q.getResultList() != null && !q.getResultList().isEmpty();
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    @Override
    public void salvarNovo(ConfigEmpenho entity) {
        validarCampos(entity);
        adicionarContaDespesa(entity);
        super.salvarNovo(entity);
    }

    private void adicionarContaDespesa(ConfigEmpenho selecionado) {
        ConfigEmpenhoContaDesp configContaDespesa = new ConfigEmpenhoContaDesp();
        configContaDespesa.setConfigEmpenho(selecionado);
        configContaDespesa.setContaDespesa(selecionado.getContaDespesa());
        selecionado.getConfigEmpenhoContaDespesas().add(configContaDespesa);
    }

    public void salvar(ConfigEmpenho configuracaoNaoAlterado, ConfigEmpenho selecionado) {
        validarCampos(selecionado);
        verifcarAlteracoesEvento(configuracaoNaoAlterado, selecionado);
        utilFacade.validarVigenciaEncerrada(selecionado.getInicioVigencia(), selecionado.getFimVigencia());
        selecionado.getConfigEmpenhoContaDespesas().removeAll(selecionado.getConfigEmpenhoContaDespesas());
        adicionarContaDespesa(selecionado);
        super.salvar(selecionado);
    }

    public void validarCampos(ConfigEmpenho selecionado) {
        ValidacaoException ve = new ValidacaoException();
        selecionado.realizarValidacoes();
        if (selecionado.getContaDespesa() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo conta de despesa deve ser informado.");
        }
        if (selecionado.getTipoReconhecimento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo tipo de reconhecimento deve ser informado.");
        }
        ve.lancarException();
        validarConfiguracaoExistente(selecionado);
    }

    private void validarConfiguracaoExistente(ConfigEmpenho selecionado) {
        ValidacaoException ve = new ValidacaoException();
        if (verificarConfiguracaoExistente(selecionado, sistemaFacade.getDataOperacao())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("" +
                "Existe uma configuração vigente para os parâmetros: "
                + " tipo lançamento: " + selecionado.getTipoLancamento().getDescricao()
                + ", tipo de reconhecimento: " + selecionado.getTipoReconhecimento().getDescricao()
                + ", conta de despesa: " + selecionado.getContaDespesa()
                + ", Tipo de Despesa: " + selecionado.getTipoContaDespesa().getDescricao()
                + ", Subtipo de Despesa: " + selecionado.getSubTipoDespesa().getDescricao());
        }
        ve.lancarException();
    }

    public void encerrarVigencia(ConfigEmpenho entity) {
        utilFacade.validarEncerramentoVigencia(entity.getInicioVigencia(), entity.getFimVigencia(), entity.getEventoContabil());
        super.salvar(entity);
    }

    public boolean isVigenciaEncerrada(Date fimVigencia) {
        return utilFacade.isVigenciaEncerrada(fimVigencia);
    }

    private void verifcarAlteracoesEvento(ConfigEmpenho configuracaoNaoAlterada, ConfigEmpenho selecionado) {
        if (selecionado.getId() != null) {
            boolean configAlterada = false;
            for (ConfigEmpenhoContaDesp configEmpenhoContaDesp : selecionado.getConfigEmpenhoContaDespesas()) {
                if (!configuracaoNaoAlterada.getConfigEmpenhoContaDespesas().contains(configEmpenhoContaDesp)) {
                    configAlterada = true;
                }
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

    public UtilConfiguracaoEventoContabilFacade getUtilFacade() {
        return utilFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }
}
