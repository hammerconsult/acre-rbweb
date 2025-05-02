package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfigPatrimonioLiquido;
import br.com.webpublico.entidades.ConfiguracaoEvento;
import br.com.webpublico.enums.OperacaoPatrimonioLiquido;
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
 * Created by mga on 18/10/2017.
 */
@Stateless
public class ConfiguracaoPatrimonioLiquidoFacade extends AbstractFacade<ConfigPatrimonioLiquido> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private UtilConfiguracaoEventoContabilFacade utilFacade;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public ConfiguracaoPatrimonioLiquidoFacade() {
        super(ConfigPatrimonioLiquido.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfigPatrimonioLiquido buscarCDEPatrimonioLiquido(TipoLancamento tipoLancamento, OperacaoPatrimonioLiquido operacao, Date dataOperacao) {
        String msgErro;
        try {
            Preconditions.checkNotNull(tipoLancamento, "Tipo de Lançamento não informado para recuperar o evento.");
            Preconditions.checkNotNull(operacao, "Operação não informado para recuperar o evento.");

            return buscarConfiguracaoPatrimonioLiquido(tipoLancamento, operacao, dataOperacao);
        } catch (NoResultException nr) {
            msgErro = "Configuração de Evento não encontrada para o tipo de lançamento: " + tipoLancamento.getDescricao()
                + ", operação: " + operacao.getDescricao()
                + "  na data: " + DataUtil.getDataFormatada(dataOperacao) + ".";
            throw new ExcecaoNegocioGenerica(msgErro);
        } catch (Exception e) {
            msgErro = e.getMessage();
            throw new ExcecaoNegocioGenerica(msgErro);
        }
    }

    private ConfigPatrimonioLiquido buscarConfiguracaoPatrimonioLiquido(TipoLancamento tipoLancamento, OperacaoPatrimonioLiquido operacao, Date dataOperacao) {

        String sql = getSqlBuscarConfiguracao();
        Query q = em.createNativeQuery(sql, ConfigPatrimonioLiquido.class);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        q.setParameter("tipoLancamento", tipoLancamento.name());
        q.setParameter("operacao", operacao.name());
        return (ConfigPatrimonioLiquido) q.getSingleResult();
    }

    private String getSqlBuscarConfiguracao() {
        return " SELECT CE.*, CONFIG.* "
            + " FROM CONFIGPATRIMONIOLIQUIDO CONFIG "
            + " INNER JOIN CONFIGURACAOEVENTO CE ON CONFIG.ID = CE.ID "
            + " INNER JOIN EVENTOCONTABIL EC ON CE.EVENTOCONTABIL_ID = EC.ID"
            + " WHERE to_date(:dataOperacao, 'dd/MM/yyyy') BETWEEN trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:dataOperacao, 'dd/MM/yyyy')) "
            + " AND CE.TIPOLANCAMENTO = :tipoLancamento"
            + " AND CONFIG.OPERACAOPATRIMONIOLIQUIDO = :operacao";
    }

    public Boolean verificarConfiguracaoExistente(ConfiguracaoEvento configEvento, Date dataOperacao) {
        try {
            ConfigPatrimonioLiquido config = (ConfigPatrimonioLiquido) configEvento;
            String sql = getSqlBuscarConfiguracao();

            if (configEvento.getId() != null) {
                sql += " AND CE.ID <> :idConfiguracao";
            }
            Query q = em.createNativeQuery(sql, ConfigPatrimonioLiquido.class);
            q.setParameter("tipoLancamento", config.getTipoLancamento().name());
            q.setParameter("operacao", config.getOperacaoPatrimonioLiquido().name());
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
    public void salvarNovo(ConfigPatrimonioLiquido entity) {
        validarCampos(entity);
        super.salvarNovo(entity);
    }

    public void salvar(ConfigPatrimonioLiquido configuracaoNaoAlterado, ConfigPatrimonioLiquido selecionado) {
        validarCampos(selecionado);
        verifcarAlteracoesEvento(configuracaoNaoAlterado, selecionado);
        utilFacade.validarVigenciaEncerrada(selecionado.getInicioVigencia(), selecionado.getFimVigencia());
        super.salvar(selecionado);
    }

    private void verifcarAlteracoesEvento(ConfigPatrimonioLiquido configOriginal, ConfigPatrimonioLiquido selecionado) {
        if (selecionado.getId() != null) {
            boolean configAlterada = false;
            if (!configOriginal.getOperacaoPatrimonioLiquido().equals(selecionado.getOperacaoPatrimonioLiquido())) {
                configAlterada = true;
            }
            utilFacade.verifcarAlteracoesEvento(configOriginal, selecionado, configAlterada);
        }
    }

    public void validarCampos(ConfigPatrimonioLiquido selecionado) {
        ValidacaoException ve = new ValidacaoException();
        selecionado.realizarValidacoes();
        if (selecionado.getOperacaoPatrimonioLiquido() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo operação deve ser informado.");
        }
        ve.lancarException();
        validarConfiguracaoExistente(selecionado);
    }

    private void validarConfiguracaoExistente(ConfigPatrimonioLiquido selecionado) {
        ValidacaoException ve = new ValidacaoException();
        if (verificarConfiguracaoExistente(selecionado, sistemaFacade.getDataOperacao())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("" +
                "Existe uma configuração vigente para os parâmetros: "
                + ", tipo lançamento: " + selecionado.getTipoLancamento().getDescricao()
                + " e operação: " + selecionado.getOperacaoPatrimonioLiquido().getDescricao());
        }
        ve.lancarException();
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }

    public void encerrarVigencia(ConfigPatrimonioLiquido entity) {
        utilFacade.validarEncerramentoVigencia(entity.getInicioVigencia(), entity.getFimVigencia(), entity.getEventoContabil());
        super.salvar(entity);
    }
}
