package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfigAtoPotencial;
import br.com.webpublico.entidades.ConfiguracaoEvento;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoOperacaoAtoPotencial;
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

@Stateless
public class ConfigAtoPotencialFacade extends AbstractFacade<ConfigAtoPotencial> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private UtilConfiguracaoEventoContabilFacade utilFacade;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public ConfigAtoPotencialFacade() {
        super(ConfigAtoPotencial.class);
    }

    public ConfigAtoPotencial buscarCDEAtoPotencial(TipoLancamento tipoLancamento,
                                                    Date data,
                                                    TipoOperacaoAtoPotencial tipoOperacaoAtoPotencial) {
        String msgErro;
        try {
            Preconditions.checkNotNull(tipoLancamento, "Tipo de Lançamento não informado para recuperar o evento.");
            Preconditions.checkNotNull(tipoOperacaoAtoPotencial, " Operação não informado para recuperar o evento.");
            return buscarConfiguracaoAtoPotencial(tipoLancamento, data, tipoOperacaoAtoPotencial);
        } catch (NoResultException nr) {
            msgErro = "Configuração de Evento não encontrada para a operação: " + tipoOperacaoAtoPotencial.getDescricao()
                + ", lançamento: " + tipoLancamento.getDescricao()
                + "  na data: " + DataUtil.getDataFormatada(data) + ".";
            throw new ExcecaoNegocioGenerica(msgErro);
        } catch (Exception e) {
            msgErro = e.getMessage();
            throw new ExcecaoNegocioGenerica(msgErro);
        }
    }

    private ConfigAtoPotencial buscarConfiguracaoAtoPotencial(TipoLancamento tipoLancamento,
                                                              Date dataOperacao,
                                                              TipoOperacaoAtoPotencial tipoOperacaoAtoPotencial) {

        String sql = getSqlBuscarConfiguracao();
        Query q = em.createNativeQuery(sql, ConfigAtoPotencial.class);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        q.setParameter("tipoLancamento", tipoLancamento.name());
        q.setParameter("operacao", tipoOperacaoAtoPotencial.name());
        return (ConfigAtoPotencial) q.getSingleResult();
    }

    private String getSqlBuscarConfiguracao() {
        return " SELECT CE.*, CONFIG.* "
            + " FROM ConfigAtoPotencial CONFIG "
            + " INNER JOIN CONFIGURACAOEVENTO CE ON CONFIG.ID = CE.ID "
            + " INNER JOIN EVENTOCONTABIL EC ON CE.EVENTOCONTABIL_ID = EC.ID"
            + " WHERE CONFIG.tipoOperacaoAtoPotencial = :operacao "
            + " AND to_date(:dataOperacao,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:dataOperacao,'dd/MM/yyyy')) "
            + " AND CE.TIPOLANCAMENTO = :tipoLancamento";
    }

    private boolean hasConfiguracaoExistente(ConfiguracaoEvento configEvento, Date dataOperacao) {
        try {
            ConfigAtoPotencial config = (ConfigAtoPotencial) configEvento;
            String sql = getSqlBuscarConfiguracao();

            if (configEvento.getId() != null) {
                sql += " AND CE.ID <> :idConfiguracao";
            }
            Query q = em.createNativeQuery(sql, ConfigAtoPotencial.class);
            q.setParameter("operacao", config.getTipoOperacaoAtoPotencial().name());
            q.setParameter("tipoLancamento", config.getTipoLancamento().name());
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
    public void salvarNovo(ConfigAtoPotencial entity) {
        validarCampos(entity);
        super.salvarNovo(entity);
    }

    public void salvar(ConfigAtoPotencial selecionado) {
        validarCampos(selecionado);
        utilFacade.validarVigenciaEncerrada(selecionado.getInicioVigencia(), selecionado.getFimVigencia());
        super.salvar(selecionado);
    }

    public void validarCampos(ConfigAtoPotencial selecionado) {
        ValidacaoException ve = new ValidacaoException();
        selecionado.realizarValidacoes();
        ve.lancarException();
        validarConfiguracaoExistente(selecionado);
    }

    private void validarConfiguracaoExistente(ConfigAtoPotencial selecionado) {
        ValidacaoException ve = new ValidacaoException();
        if (hasConfiguracaoExistente(selecionado, sistemaFacade.getDataOperacao())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(
                "Existe uma configuração vigente para os parâmetros: "
                + " operação: " + selecionado.getTipoOperacaoAtoPotencial().getDescricao()
                + ", tipo lançamento: " + selecionado.getTipoLancamento().getDescricao());
        }
        ve.lancarException();
    }

    public void encerrarVigencia(ConfigAtoPotencial entity) {
        utilFacade.validarEncerramentoVigencia(entity.getInicioVigencia(), entity.getFimVigencia(), entity.getEventoContabil());
        super.salvar(entity);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }
}
