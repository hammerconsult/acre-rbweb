package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfigInvestimento;
import br.com.webpublico.entidades.ConfiguracaoEvento;
import br.com.webpublico.enums.OperacaoInvestimento;
import br.com.webpublico.enums.TipoLancamento;
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
 * Created by mateus on 19/10/17.
 */
@Stateless
public class ConfigInvestimentoFacade extends AbstractFacade<ConfigInvestimento> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private UtilConfiguracaoEventoContabilFacade utilFacade;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public ConfigInvestimentoFacade() {
        super(ConfigInvestimento.class);
    }

    public ConfigInvestimento buscarCDEInvestimento(TipoLancamento tipoLancamento,
                                                    Date data, OperacaoInvestimento operacaoInvestimento) {
        String msgErro;
        try {
            Preconditions.checkNotNull(tipoLancamento, "Tipo de Lançamento não informado para recuperar o evento.");
            Preconditions.checkNotNull(operacaoInvestimento, "Operação não informada para recuperar o evento.");
            return buscarConfiguracaoInvestimento(tipoLancamento, data, operacaoInvestimento);
        } catch (NoResultException nr) {
            msgErro = "Configuração de Evento não encontrada para o lançamento: " + tipoLancamento.getDescricao()
                + ", operação: " + operacaoInvestimento.getDescricao()
                + "  na data: " + DataUtil.getDataFormatada(data) + ".";
            throw new ExcecaoNegocioGenerica(msgErro);
        } catch (Exception e) {
            msgErro = e.getMessage();
            throw new ExcecaoNegocioGenerica(msgErro);
        }
    }

    private ConfigInvestimento buscarConfiguracaoInvestimento(TipoLancamento tipoLancamento,
                                                              Date dataOperacao,
                                                              OperacaoInvestimento operacaoInvestimento) {

        String sql = getSqlBuscarConfiguracao();
        Query q = em.createNativeQuery(sql, ConfigInvestimento.class);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        q.setParameter("tipoLancamento", tipoLancamento.name());
        q.setParameter("operacaoInvestimento", operacaoInvestimento.name());
        return (ConfigInvestimento) q.getSingleResult();
    }

    private String getSqlBuscarConfiguracao() {
        return " SELECT CE.*, CONFIG.* "
            + " FROM ConfigInvestimento CONFIG "
            + " INNER JOIN CONFIGURACAOEVENTO CE ON CONFIG.ID = CE.ID "
            + " INNER JOIN EVENTOCONTABIL EC ON CE.EVENTOCONTABIL_ID = EC.ID"
            + " WHERE to_date(:dataOperacao, 'dd/MM/yyyy') BETWEEN trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA),to_date(:dataOperacao, 'dd/MM/yyyy')) "
            + " AND CE.TIPOLANCAMENTO = :tipoLancamento"
            + " AND CONFIG.operacaoInvestimento = :operacaoInvestimento";
    }

    public Boolean verificarConfiguracaoExistente(ConfiguracaoEvento configEvento, Date dataOperacao) {
        try {
            ConfigInvestimento config = (ConfigInvestimento) configEvento;
            String sql = getSqlBuscarConfiguracao();

            if (configEvento.getId() != null) {
                sql += " AND CE.ID <> :idConfiguracao";
            }
            Query q = em.createNativeQuery(sql, ConfigInvestimento.class);
            q.setParameter("tipoLancamento", config.getTipoLancamento().name());
            q.setParameter("operacaoInvestimento", config.getOperacaoInvestimento().name());
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
    public void salvarNovo(ConfigInvestimento entity) {
        validarCampos(entity);
        super.salvarNovo(entity);
    }

    public void salvar(ConfigInvestimento configuracaoNaoAlterado, ConfigInvestimento selecionado) {
        validarCampos(selecionado);
        verifcarAlteracoesEvento(configuracaoNaoAlterado, selecionado);
        utilFacade.validarVigenciaEncerrada(selecionado.getInicioVigencia(), selecionado.getFimVigencia());
        super.salvar(selecionado);
    }

    private void verifcarAlteracoesEvento(ConfigInvestimento configOriginal, ConfigInvestimento selecionado) {
        if (selecionado.getId() != null) {
            boolean configAlterada = false;
            if (!configOriginal.getTipoLancamento().equals(selecionado.getTipoLancamento())) {
                configAlterada = true;
            }
            if (!configOriginal.getOperacaoInvestimento().equals(selecionado.getOperacaoInvestimento())) {
                configAlterada = true;
            }
            utilFacade.verifcarAlteracoesEvento(configOriginal, selecionado, configAlterada);
        }
    }

    public void validarCampos(ConfigInvestimento selecionado) {
        ValidacaoException ve = new ValidacaoException();
        selecionado.realizarValidacoes();
        if (selecionado.getTipoLancamento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo tipo de lançamento deve ser informado.");
        }
        if (selecionado.getOperacaoInvestimento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo operação deve ser informado.");
        }
        ve.lancarException();
        validarConfiguracaoExistente(selecionado);
    }

    private void validarConfiguracaoExistente(ConfigInvestimento selecionado) {
        ValidacaoException ve = new ValidacaoException();
        if (verificarConfiguracaoExistente(selecionado, sistemaFacade.getDataOperacao())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("" +
                "Existe uma configuração vigente para os parâmetros: tipo de lançamento: " + selecionado.getTipoLancamento().getDescricao()
                + " e operação: " + selecionado.getOperacaoInvestimento().getDescricao());
        }
        ve.lancarException();
    }

    public void encerrarVigencia(ConfigInvestimento entity) {
        utilFacade.validarEncerramentoVigencia(entity.getInicioVigencia(), entity.getFimVigencia(), entity.getEventoContabil());
        super.salvar(entity);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UtilConfiguracaoEventoContabilFacade getUtilFacade() {
        return utilFacade;
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }
}
