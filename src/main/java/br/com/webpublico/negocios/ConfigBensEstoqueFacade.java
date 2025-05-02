package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfigBensEstoque;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoOperacaoBensEstoque;
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
public class ConfigBensEstoqueFacade extends AbstractFacade<ConfigBensEstoque> {

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

    public ConfigBensEstoqueFacade() {
        super(ConfigBensEstoque.class);
    }

    public void verificaAlteracoesEvento(ConfigBensEstoque configNaoAlterado, ConfigBensEstoque config) {

        if (config.getId() == null) {
            return;
        }
        boolean alterou = false;
        if (!configNaoAlterado.getTipoLancamento().equals(config.getTipoLancamento())) {
            alterou = true;
        }
        if (!configNaoAlterado.getOperacaoBensEstoque().equals(config.getOperacaoBensEstoque())) {
            alterou = true;
        }
        if (!configNaoAlterado.getInicioVigencia().equals(config.getInicioVigencia())) {
            alterou = true;
        }
        if (!configNaoAlterado.getEventoContabil().equals(config.getEventoContabil())) {
            eventoContabilFacade.geraEventosReprocessar(config.getEventoContabil(), config.getId(), config.getClass().getSimpleName());
            eventoContabilFacade.geraEventosReprocessar(configNaoAlterado.getEventoContabil(), configNaoAlterado.getId(), configNaoAlterado.getClass().getSimpleName());
        }
        if (alterou) {
            eventoContabilFacade.geraEventosReprocessar(config.getEventoContabil(), config.getId(), config.getClass().getSimpleName());
        }
    }

    public void meuSalvar(ConfigBensEstoque configsNaoAlterado, ConfigBensEstoque configuracaoEncontrada) {
        verificaAlteracoesEvento(configsNaoAlterado, configuracaoEncontrada);
        if (configuracaoEncontrada.getId() == null) {
            salvarNovo(configuracaoEncontrada);
        } else {
            salvar(configuracaoEncontrada);
        }
    }


    public ConfigBensEstoque verificaConfiguracaoExistente(ConfigBensEstoque config, Date dataVigencia) {
        String sql = " SELECT CE.*, CBE.* ";
        sql += " FROM CONFIGBENSESTOQUE CBE ";
        sql += " INNER JOIN CONFIGURACAOEVENTO CE ON CBE.ID = CE.ID ";
        sql += " WHERE CE.TIPOLANCAMENTO = :tipoLancamento ";
        sql += " AND CBE.OPERACAOBENSESTOQUE = :tipoOperacao ";
        sql += " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) ";
        if (config.getId() != null) {
            sql += "and cbe.id <> :idConfig";
        }
        Query q = em.createNativeQuery(sql, ConfigBensEstoque.class);
        q.setParameter("tipoLancamento", config.getTipoLancamento().name());
        q.setParameter("tipoOperacao", config.getOperacaoBensEstoque().name());
        q.setParameter("data", DataUtil.getDataFormatada(dataVigencia));
        if (config.getId() != null) {
            q.setParameter("idConfig", config.getId());
        }
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (ConfigBensEstoque) q.getSingleResult();
        }
        return null;
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }

    public ConfigBensEstoque recuperaEventoPorTipoOperacao(TipoOperacaoBensEstoque operacao, TipoLancamento tipoLancamento, Date data) throws ExcecaoNegocioGenerica, NonUniqueResultException {
        String msgErro;
        Preconditions.checkNotNull(tipoLancamento, "Tipo de Lançamento não informado.");
        Preconditions.checkNotNull(data, " A data do Lançamento esta vazia.");
        Preconditions.checkNotNull(operacao.getDescricao(), " A operação está vazia.");
        try {
            return recuperaConfiguracaoBensMoveis(operacao, tipoLancamento, data);

        } catch (NoResultException nr) {
            msgErro = "Não foi encontrado nenhum Evento com a Operação: " + operacao.getDescricao() + "; Tipo de Lançamento " + tipoLancamento.getDescricao() + " na Data : " + new SimpleDateFormat("dd/MM/yyyy").format(data);
            throw new ExcecaoNegocioGenerica(msgErro);
        } catch (Exception e) {
            msgErro = e.getMessage();
            throw new ExcecaoNegocioGenerica(msgErro);
        }
    }

    private ConfigBensEstoque recuperaConfiguracaoBensMoveis(TipoOperacaoBensEstoque operacao, TipoLancamento tipoLancamento, Date data) throws Exception {
        String sql = "SELECT CE.*, CBE.* "
                + " FROM CONFIGBENSESTOQUE CBE"
                + " INNER JOIN CONFIGURACAOEVENTO CE ON CBE.ID = CE.ID "
                + " INNER JOIN EVENTOCONTABIL EC ON CE.EVENTOCONTABIL_ID = EC.ID "
                + " WHERE CBE.OPERACAOBENSESTOQUE = :operacao"
                + " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) "
                + " AND CE.TIPOLANCAMENTO = :tipoLancamento";

        Query q = em.createNativeQuery(sql, ConfigBensEstoque.class);

        q.setParameter("operacao", operacao.name());
        q.setParameter("data", DataUtil.getDataFormatada(data));
        q.setParameter("tipoLancamento", tipoLancamento.name());

        return (ConfigBensEstoque) q.getSingleResult();

    }

    public void encerrarVigencia(ConfigBensEstoque entity) {
        utilFacade.validarEncerramentoVigencia(entity.getInicioVigencia(), entity.getFimVigencia(), entity.getEventoContabil());
        super.salvar(entity);
    }
}
