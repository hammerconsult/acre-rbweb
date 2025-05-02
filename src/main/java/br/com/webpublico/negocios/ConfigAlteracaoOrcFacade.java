package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfigAlteracaoOrc;
import br.com.webpublico.entidades.ReceitaAlteracaoORC;
import br.com.webpublico.enums.*;
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
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 05/02/14
 * Time: 17:41
 * To change this template use File | Settings | File Templates.
 */


@Stateless
public class ConfigAlteracaoOrcFacade extends AbstractFacade<ConfigAlteracaoOrc> {

    @EJB
    private EventoContabilFacade eventoContabilFacade;

    @EJB
    private UtilConfiguracaoEventoContabilFacade utilFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfigAlteracaoOrcFacade() {
        super(ConfigAlteracaoOrc.class);
    }

    public void verificaAlteracoesEvento(ConfigAlteracaoOrc configNaoAlterada, ConfigAlteracaoOrc config) {

        if (config.getId() == null) {
            return;
        }
        boolean alterou = false;
        if (config.getTipoConfigAlteracaoOrc().equals(TipoConfigAlteracaoOrc.SUPLEMENTAR)) {
            if (!configNaoAlterada.getTipoDespesaORC().equals(config.getTipoDespesaORC())) {
                alterou = true;
            }
            if (!configNaoAlterada.getOrigemSuplementacaoORC().equals(config.getOrigemSuplementacaoORC())) {
                alterou = true;
            }
        }
        if (config.getTipoConfigAlteracaoOrc().equals(TipoConfigAlteracaoOrc.ANULACAO)) {
            if (!configNaoAlterada.getTipoDespesaORC().equals(config.getTipoDespesaORC())) {
                alterou = true;
            }
        }
        if (config.getTipoConfigAlteracaoOrc().equals(TipoConfigAlteracaoOrc.RECEITA)) {
            if (!configNaoAlterada.getTipoAlteracaoORC().equals(config.getTipoAlteracaoORC())) {
                alterou = true;
            }
        }
        if (!configNaoAlterada.getTipoLancamento().equals(config.getTipoLancamento())) {
            alterou = true;
        }
        if (!configNaoAlterada.getInicioVigencia().equals(config.getInicioVigencia())) {
            alterou = true;
        }
        if (!configNaoAlterada.getEventoContabil().equals(config.getEventoContabil())) {
            eventoContabilFacade.geraEventosReprocessar(config.getEventoContabil(), config.getId(), config.getClass().getSimpleName());
            eventoContabilFacade.geraEventosReprocessar(configNaoAlterada.getEventoContabil(), configNaoAlterada.getId(), configNaoAlterada.getClass().getSimpleName());
        }
        if (alterou) {
            eventoContabilFacade.geraEventosReprocessar(config.getEventoContabil(), config.getId(), config.getClass().getSimpleName());
        }
    }

    public void meuSalvar(ConfigAlteracaoOrc configuracaoNaoAlterada, ConfigAlteracaoOrc configuracaoEncontrada) {
        verificaAlteracoesEvento(configuracaoNaoAlterada, configuracaoEncontrada);
        if (configuracaoEncontrada.getId() == null) {
            salvarNovo(configuracaoEncontrada);
        } else {
            salvar(configuracaoEncontrada);
        }
    }

    public ConfigAlteracaoOrc verificaConfiguracaoExistente(ConfigAlteracaoOrc configAlteracaoOrc, Date dataVigencia) {
        ConfigAlteracaoOrc configuracao;
        if (configAlteracaoOrc.getTipoConfigAlteracaoOrc().equals(TipoConfigAlteracaoOrc.SUPLEMENTAR)) {
            configuracao = verificaConfiguracaoExistenteSuplementar(configAlteracaoOrc, dataVigencia);
        } else if (configAlteracaoOrc.getTipoConfigAlteracaoOrc().equals(TipoConfigAlteracaoOrc.ANULACAO)) {
            configuracao = verificaConfiguracaoExistenteAnulacao(configAlteracaoOrc, dataVigencia);
        } else {
            configuracao = verificaConfiguracaoExistenteReceita(configAlteracaoOrc, dataVigencia);
        }
        return configuracao;
    }


    public ConfigAlteracaoOrc verificaConfiguracaoExistenteSuplementar(ConfigAlteracaoOrc config, Date dataVigencia) {
        String sql = " SELECT CE.*, CONFIG.* ";
        sql += " FROM CONFIGALTERACAOORC CONFIG ";
        sql += " INNER JOIN CONFIGURACAOEVENTO CE ON CONFIG.ID = CE.ID ";
        sql += " WHERE CE.TIPOLANCAMENTO = :tipoLancamento ";
        sql += " AND CONFIG.TIPODESPESAORC = :tipoCredito ";
        sql += " AND CONFIG.ORIGEMSUPLEMENTACAOORC = :origemRecurso ";
        sql += " AND CONFIG.TIPOCONFIGALTERACAOORC = 'SUPLEMENTAR'";
        if (config.getId() != null) {
            sql += "and CONFIG.id <> :idConfig";
        }
        sql += " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) ";

        Query q = em.createNativeQuery(sql, ConfigAlteracaoOrc.class);
        q.setParameter("tipoLancamento", config.getTipoLancamento().name());
        q.setParameter("data", DataUtil.getDataFormatada(dataVigencia));
        q.setParameter("tipoCredito", config.getTipoDespesaORC().name());
        q.setParameter("origemRecurso", config.getOrigemSuplementacaoORC().name());
        if (config.getId() != null) {
            q.setParameter("idConfig", config.getId());
        }
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (ConfigAlteracaoOrc) q.getSingleResult();
        }
        return null;
    }

    public ConfigAlteracaoOrc verificaConfiguracaoExistenteAnulacao(ConfigAlteracaoOrc config, Date dataVigencia) {
        String sql = " SELECT CE.*, CONFIG.* ";
        sql += " FROM CONFIGALTERACAOORC CONFIG ";
        sql += " INNER JOIN CONFIGURACAOEVENTO CE ON CONFIG.ID = CE.ID ";
        sql += " WHERE CE.TIPOLANCAMENTO = :tipoLancamento ";
        sql += " AND CONFIG.TIPODESPESAORC = :tipoCredito ";
        sql += " AND CONFIG.TIPOCONFIGALTERACAOORC = 'ANULACAO'";
        if (config.getId() != null) {
            sql += "and CONFIG.id <> :idConfig";
        }
        sql += " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) ";

        Query q = em.createNativeQuery(sql, ConfigAlteracaoOrc.class);
        q.setParameter("tipoLancamento", config.getTipoLancamento().name());
        q.setParameter("data", DataUtil.getDataFormatada(dataVigencia));
        q.setParameter("tipoCredito", config.getTipoDespesaORC().name());
        if (config.getId() != null) {
            q.setParameter("idConfig", config.getId());
        }
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (ConfigAlteracaoOrc) q.getSingleResult();
        }
        return null;
    }

    public ConfigAlteracaoOrc verificaConfiguracaoExistenteReceita(ConfigAlteracaoOrc config, Date dataVigencia) {
        String sql = " SELECT CE.*, CONFIG.* ";
        sql += " FROM CONFIGALTERACAOORC CONFIG ";
        sql += " INNER JOIN CONFIGURACAOEVENTO CE ON CONFIG.ID = CE.ID ";
        sql += " WHERE CE.TIPOLANCAMENTO = :tipoLancamento ";
        sql += " AND CONFIG.TIPOALTERACAOORC = :tipoAlteracao ";
        sql += " AND CONFIG.NUMEROINICIALCONTARECEITA = :inicioConta";
        sql += " AND CONFIG.TIPOCONFIGALTERACAOORC = 'RECEITA'";
        if (config.getId() != null) {
            sql += "and CONFIG.id <> :idConfig";
        }
        sql += " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) ";

        Query q = em.createNativeQuery(sql, ConfigAlteracaoOrc.class);
        q.setParameter("tipoLancamento", config.getTipoLancamento().name());
        q.setParameter("data", DataUtil.getDataFormatada(dataVigencia));
        q.setParameter("tipoAlteracao", config.getTipoAlteracaoORC().name());
        q.setParameter("inicioConta", config.getNumeroInicialContaReceita().name());
        if (config.getId() != null) {
            q.setParameter("idConfig", config.getId());
        }
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (ConfigAlteracaoOrc) q.getSingleResult();
        }
        return null;
    }

    public ConfigAlteracaoOrc recuperarConfigEventoCreditoSuplementar(TipoDespesaORC tipoDespesaORC, OrigemSuplementacaoORC origemSuplementacaoORC, TipoLancamento tipoLancamento, Date data) throws Exception {
        String msgErro;
        Preconditions.checkNotNull(tipoLancamento, "Tipo de Lançamento está vazio.");
        Preconditions.checkNotNull(data, " A data do Lançamento esta vazia.");
        Preconditions.checkNotNull(tipoDespesaORC.getDescricao(), " O tipo de crédito está vazia.");
        Preconditions.checkNotNull(origemSuplementacaoORC.getDescricao(), " A Origem do Recurso está vazia.");

        try {
            String sql = "SELECT CE.*, CONFIG.* "
                    + " FROM CONFIGALTERACAOORC CONFIG "
                    + " INNER JOIN CONFIGURACAOEVENTO CE ON CONFIG.ID = CE.ID "
                    + " INNER JOIN EVENTOCONTABIL EC ON CE.EVENTOCONTABIL_ID = EC.ID "
                    + " WHERE CONFIG.TIPODESPESAORC = :tipoCredito "
                    + "   AND CONFIG.ORIGEMSUPLEMENTACAOORC = :origemRecurso "
                    + "   AND CONFIG.TIPOCONFIGALTERACAOORC = '" + TipoConfigAlteracaoOrc.SUPLEMENTAR.name() + "'"
                    + "   AND CE.TIPOLANCAMENTO = :tipoLancamento "
                    + "   AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) ";

            Query q = em.createNativeQuery(sql, ConfigAlteracaoOrc.class);
            q.setParameter("tipoCredito", tipoDespesaORC.name());
            q.setParameter("origemRecurso", origemSuplementacaoORC.name());
            q.setParameter("data", DataUtil.getDataFormatada(data));
            q.setParameter("tipoLancamento", tipoLancamento.name());

            return (ConfigAlteracaoOrc) q.getSingleResult();
        } catch (NoResultException nr) {
            msgErro = "Evento Contábil (Suplementação) não encontrado para o tipo de crédito: " + tipoDespesaORC.getDescricao() + ", lançamento " + tipoLancamento.getDescricao() + " e origem do recurso: " + origemSuplementacaoORC.getDescricao() + " na data: " + DataUtil.getDataFormatada(data) + ".";
            throw new ExcecaoNegocioGenerica(msgErro);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }

    public ConfigAlteracaoOrc recuperarConfigEventoAnulacaoCredito(TipoDespesaORC tipoDespesaORC, TipoLancamento tipoLancamento, Date data) throws Exception {
        String msgErro;
        Preconditions.checkNotNull(tipoLancamento, "Tipo de Lançamento está vazio.");
        Preconditions.checkNotNull(data, " A data do Lançamento esta vazia.");
        Preconditions.checkNotNull(tipoDespesaORC.getDescricao(), " O tipo de crédito está vazia.");

        try {
            String sql = "SELECT CE.*, CONFIG.* "
                    + " FROM CONFIGALTERACAOORC CONFIG "
                    + " INNER JOIN CONFIGURACAOEVENTO CE ON CONFIG.ID = CE.ID "
                    + " INNER JOIN EVENTOCONTABIL EC ON CE.EVENTOCONTABIL_ID = EC.ID "
                    + " WHERE CONFIG.TIPODESPESAORC = :tipoCredito "
                    + "   AND CONFIG.TIPOCONFIGALTERACAOORC = '" + TipoConfigAlteracaoOrc.ANULACAO.name() + "'"
                    + "   AND CE.TIPOLANCAMENTO = :tipoLancamento "
                    + "   AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) ";

            Query q = em.createNativeQuery(sql, ConfigAlteracaoOrc.class);
            q.setParameter("tipoCredito", tipoDespesaORC.name());
            q.setParameter("data", DataUtil.getDataFormatada(data));
            q.setParameter("tipoLancamento", tipoLancamento.name());

            return (ConfigAlteracaoOrc) q.getSingleResult();
        } catch (NoResultException nr) {
            msgErro = "Evento Contábil (Anulação) não encontrado para o tipo de crédito: " + tipoDespesaORC.getDescricao() + ", lançamento " + tipoLancamento.getDescricao() + " na data: " + DataUtil.getDataFormatada(data) + ".";
            throw new ExcecaoNegocioGenerica(msgErro);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }

    public ConfigAlteracaoOrc buscarConfigEventoReceita(TipoLancamento tipoLancamento, ReceitaAlteracaoORC receitaAlteracaoORC) throws Exception {
        String msgErro;
        Preconditions.checkNotNull(tipoLancamento, "Tipo de Lançamento está vazio.");
        Preconditions.checkNotNull(receitaAlteracaoORC.getAlteracaoORC().getDataEfetivacao(), " A data do Lançamento esta vazia.");
        Preconditions.checkNotNull(receitaAlteracaoORC.getAlteracaoORC().getDescricao(), " O Tipo de Alteração da Receita está vazia.");

        try {
            String sql = "SELECT CE.*, CONFIG.* ";
            sql += " FROM CONFIGALTERACAOORC CONFIG ";
            sql += " INNER JOIN CONFIGURACAOEVENTO CE ON CONFIG.ID = CE.ID ";
            sql += " INNER JOIN EVENTOCONTABIL EC ON CE.EVENTOCONTABIL_ID = EC.ID ";
            sql += " WHERE CONFIG.TIPOALTERACAOORC = :tipoAlteracao ";
            sql += "   AND CE.TIPOLANCAMENTO = :tipoLancamento ";
            sql += "   AND CONFIG.TIPOCONFIGALTERACAOORC = '" + TipoConfigAlteracaoOrc.RECEITA.name() + "'";
            sql += "   AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) ";
            if (OperacaoReceita.retornarOperacoesReceitaDeducao().contains(receitaAlteracaoORC.getOperacaoReceita())) {
                sql += " AND CONFIG.NUMEROINICIALCONTARECEITA = '" + NumeroInicialContaReceita.CONTA_INICIADA_COM_NOVE.name() + "'";
            } else {
                sql += " AND CONFIG.NUMEROINICIALCONTARECEITA = '" + NumeroInicialContaReceita.CONTA_NAO_INICIADA_COM_NOVE.name() + "'";
            }
            Query q = em.createNativeQuery(sql, ConfigAlteracaoOrc.class);
            q.setParameter("tipoAlteracao", receitaAlteracaoORC.getTipoAlteracaoORC().name());
            q.setParameter("data", DataUtil.getDataFormatada(receitaAlteracaoORC.getAlteracaoORC().getDataEfetivacao()));
            q.setParameter("tipoLancamento", tipoLancamento.name());

            return (ConfigAlteracaoOrc) q.getSingleResult();
        } catch (NoResultException nr) {
            msgErro = "Evento Contábil (Receita) não encontrado para o tipo de alteração: " + receitaAlteracaoORC.getTipoAlteracaoORC().getDescricao() + ", lançamento " + tipoLancamento.getDescricao() + " na data: " + DataUtil.getDataFormatada(receitaAlteracaoORC.getAlteracaoORC().getDataEfetivacao()) + ".";
            throw new ExcecaoNegocioGenerica(msgErro);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }

    public void encerrarVigencia(ConfigAlteracaoOrc entity) {
        utilFacade.validarEncerramentoVigencia(entity.getInicioVigencia(), entity.getFimVigencia(), entity.getEventoContabil());
        super.salvar(entity);
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }
}
