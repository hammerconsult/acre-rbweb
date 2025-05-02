package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfigBensIntangiveis;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoOperacaoBensIntangiveis;
import br.com.webpublico.util.DataUtil;
import com.google.common.base.Preconditions;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 05/02/14
 * Time: 17:41
 * To change this template use File | Settings | File Templates.
 */


@Stateless
public class ConfigBensIntangiveisFacade extends AbstractFacade<ConfigBensIntangiveis> {

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

    public ConfigBensIntangiveisFacade() {
        super(ConfigBensIntangiveis.class);
    }

    public void verificaAlteracoesEvento(ConfigBensIntangiveis configBensIntangiveisNaoAlterado, ConfigBensIntangiveis config ) {

        if (config.getId() == null) {
            return;
        }
        boolean alterou = false;
        if (!configBensIntangiveisNaoAlterado.getTipoLancamento().equals(config.getTipoLancamento())) {
            alterou = true;
        }
        if (!configBensIntangiveisNaoAlterado.getOperacaoBensIntangiveis().equals(config.getOperacaoBensIntangiveis())) {
            alterou = true;
        }
        if (!configBensIntangiveisNaoAlterado.getInicioVigencia().equals(config.getInicioVigencia())) {
            alterou = true;
        }
        if (!configBensIntangiveisNaoAlterado.getEventoContabil().equals(config.getEventoContabil())) {
            eventoContabilFacade.geraEventosReprocessar(config.getEventoContabil(), config.getId(), config.getClass().getSimpleName());
            eventoContabilFacade.geraEventosReprocessar(configBensIntangiveisNaoAlterado.getEventoContabil(), configBensIntangiveisNaoAlterado.getId(), configBensIntangiveisNaoAlterado.getClass().getSimpleName());
        }
        if (alterou) {
            eventoContabilFacade.geraEventosReprocessar(config.getEventoContabil(), config.getId(), config.getClass().getSimpleName());
        }
    }

    public void meuSalvar(ConfigBensIntangiveis configBensIntangiveisNaoAlterado, ConfigBensIntangiveis configuracaoEncontrada) {
        verificaAlteracoesEvento(configBensIntangiveisNaoAlterado, configuracaoEncontrada);
        if (configuracaoEncontrada.getId() == null) {
            salvarNovo(configuracaoEncontrada);
        } else {
            salvar(configuracaoEncontrada);
        }
    }


    public ConfigBensIntangiveis verificaConfiguracaoExistente(ConfigBensIntangiveis config, Date dataVigencia) {
        String sql = " SELECT CE.*, CBI.* ";
        sql += " FROM CONFIGBENSINTANGIVEIS CBI ";
        sql += " INNER JOIN CONFIGURACAOEVENTO CE ON CBI.ID = CE.ID ";
        sql += " WHERE CE.TIPOLANCAMENTO = :tipoLancamento ";
        sql += " AND CBI.OPERACAOBENSINTANGIVEIS = :tipoOperacao ";
        sql += " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) ";
        if (config.getId() != null) {
            sql += "and cbi.id <> :idConfig";
        }
        Query q = em.createNativeQuery(sql, ConfigBensIntangiveis.class);
        q.setParameter("tipoLancamento", config.getTipoLancamento().name());
        q.setParameter("tipoOperacao", config.getOperacaoBensIntangiveis().name());
        q.setParameter("data", DataUtil.getDataFormatada(dataVigencia));
        if (config.getId() != null) {
            q.setParameter("idConfig", config.getId());
        }
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (ConfigBensIntangiveis) q.getSingleResult();
        }
        return null;
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }

    public ConfigBensIntangiveis recuperaEventoPorTipoOperacao(TipoOperacaoBensIntangiveis operacao, TipoLancamento tipoLancamento, Date data) throws ExcecaoNegocioGenerica, NonUniqueResultException {
        String msgErro;
        Preconditions.checkNotNull(tipoLancamento, "Tipo de Lançamento não informado.");
        Preconditions.checkNotNull(data, " A data do Lançamento esta vazia.");
        Preconditions.checkNotNull(operacao.getDescricao(), " A operação está vazia.");
        try {
            return recuperaConfiguracaoBensIntangiveis(operacao, tipoLancamento, data);

        } catch (NoResultException nr) {
            msgErro = "Não foi encontrado nenhum Evento com a Operação: " + operacao.getDescricao() + "; Tipo de Lançamento " + tipoLancamento.getDescricao() + " na Data : " + new SimpleDateFormat("dd/MM/yyyy").format(data);
            throw new ExcecaoNegocioGenerica(msgErro);
        } catch (Exception e) {
            msgErro = e.getMessage();
            throw new ExcecaoNegocioGenerica(msgErro);
        }
    }

    private ConfigBensIntangiveis recuperaConfiguracaoBensIntangiveis(TipoOperacaoBensIntangiveis operacao, TipoLancamento tipoLancamento, Date data) throws Exception {
        String sql = "SELECT CE.*, CBI.* "
                + " FROM CONFIGBENSINTANGIVEIS CBI"
                + " INNER JOIN CONFIGURACAOEVENTO CE ON CBI.ID = CE.ID "
                + " INNER JOIN EVENTOCONTABIL EC ON CE.EVENTOCONTABIL_ID = EC.ID "
                + " WHERE CBI.OPERACAOBENSINTANGIVEIS = :operacao"
                + " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) "
                + " AND CE.TIPOLANCAMENTO = :tipoLancamento";

        Query q = em.createNativeQuery(sql, ConfigBensIntangiveis.class);

        q.setParameter("operacao", operacao.name());
        q.setParameter("data", DataUtil.getDataFormatada(data));
        q.setParameter("tipoLancamento", tipoLancamento.name());

        return (ConfigBensIntangiveis) q.getSingleResult();

    }

    public void encerrarVigencia(ConfigBensIntangiveis entity) {
        utilFacade.validarEncerramentoVigencia(entity.getInicioVigencia(), entity.getFimVigencia(), entity.getEventoContabil());
        super.salvar(entity);
    }
}
