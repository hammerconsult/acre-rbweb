package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfigBensImoveis;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoOperacaoBensImoveis;
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
public class ConfigBensImoveisFacade extends AbstractFacade<ConfigBensImoveis> {

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

    public ConfigBensImoveisFacade() {
        super(ConfigBensImoveis.class);
    }

    public void verificaAlteracoesEvento(ConfigBensImoveis configBensImoveisNaoAlterado, ConfigBensImoveis config) {

        if (config.getId() == null) {
            return;
        }
        boolean alterou = false;
        if (!configBensImoveisNaoAlterado.getTipoLancamento().equals(config.getTipoLancamento())) {
            alterou = true;
        }
        if (!configBensImoveisNaoAlterado.getOperacaoBensImoveis().equals(config.getOperacaoBensImoveis())) {
            alterou = true;
        }
        if (!configBensImoveisNaoAlterado.getInicioVigencia().equals(config.getInicioVigencia())) {
            alterou = true;
        }
        if (!configBensImoveisNaoAlterado.getEventoContabil().equals(config.getEventoContabil())) {
            eventoContabilFacade.geraEventosReprocessar(config.getEventoContabil(), config.getId(), config.getClass().getSimpleName());
            eventoContabilFacade.geraEventosReprocessar(configBensImoveisNaoAlterado.getEventoContabil(), configBensImoveisNaoAlterado.getId(), configBensImoveisNaoAlterado.getClass().getSimpleName());
        }
        if (alterou) {
            eventoContabilFacade.geraEventosReprocessar(config.getEventoContabil(), config.getId(), config.getClass().getSimpleName());
        }
    }

    public void meuSalvar(ConfigBensImoveis configBensImoveisNaoAlterado, ConfigBensImoveis configuracaoEncontrada) {
        verificaAlteracoesEvento(configBensImoveisNaoAlterado, configuracaoEncontrada);
        if (configuracaoEncontrada.getId() == null) {
            salvarNovo(configuracaoEncontrada);
        } else {
            salvar(configuracaoEncontrada);
        }
    }


    public ConfigBensImoveis verificaConfiguracaoExistente(ConfigBensImoveis config, Date dataVigencia) {
        String sql = " SELECT CE.*, CBI.* ";
        sql += " FROM CONFIGBENSIMOVEIS CBI ";
        sql += " INNER JOIN CONFIGURACAOEVENTO CE ON CBI.ID = CE.ID ";
        sql += " WHERE CE.TIPOLANCAMENTO = :tipoLancamento ";
        sql += " AND CBI.OPERACAOBENSIMOVEIS = :tipoOperacao ";
        sql += " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) ";
        if (config.getId() != null) {
            sql += "and cbi.id <> :idConfig";
        }
        Query q = em.createNativeQuery(sql, ConfigBensImoveis.class);
        q.setParameter("tipoLancamento", config.getTipoLancamento().name());
        q.setParameter("tipoOperacao", config.getOperacaoBensImoveis().name());
        q.setParameter("data", DataUtil.getDataFormatada(dataVigencia));
        if (config.getId() != null) {
            q.setParameter("idConfig", config.getId());
        }
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (ConfigBensImoveis) q.getSingleResult();
        }
        return null;
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }


    public ConfigBensImoveis recuperaEventoPorTipoOperacao(TipoOperacaoBensImoveis operacao, TipoLancamento tipoLancamento, Date data) throws ExcecaoNegocioGenerica, NonUniqueResultException {
        String msgErro;
        Preconditions.checkNotNull(tipoLancamento, "Tipo de Lançamento não informado.");
        Preconditions.checkNotNull(data, " A data do Lançamento esta vazia.");
        Preconditions.checkNotNull(operacao.getDescricao(), " A operação está vazia.");
        try {
            return recuperaConfiguracaoBensImoveis(operacao, tipoLancamento, data);

        } catch (NoResultException nr) {
            msgErro = "Não foi encontrado nenhum Evento com a Operação: " + operacao.getDescricao() + "; Tipo de Lançamento " + tipoLancamento.getDescricao() + " na Data : " + new SimpleDateFormat("dd/MM/yyyy").format(data);
            throw new ExcecaoNegocioGenerica(msgErro);
        } catch (Exception e) {
            msgErro = e.getMessage();
            throw new ExcecaoNegocioGenerica(msgErro);
        }
    }

    private ConfigBensImoveis recuperaConfiguracaoBensImoveis(TipoOperacaoBensImoveis operacao, TipoLancamento tipoLancamento, Date data) throws Exception {
        String sql = "SELECT CE.*, CBI.* "
                + " FROM CONFIGBENSIMOVEIS CBI"
                + " INNER JOIN CONFIGURACAOEVENTO CE ON CBI.ID = CE.ID "
                + " INNER JOIN EVENTOCONTABIL EC ON CE.EVENTOCONTABIL_ID = EC.ID "
                + " WHERE CBI.OPERACAOBENSIMOVEIS = :operacao"
                + " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) "
                + " AND CE.TIPOLANCAMENTO = :tipoLancamento";

        Query q = em.createNativeQuery(sql, ConfigBensImoveis.class);

        q.setParameter("operacao", operacao.name());
        q.setParameter("data", DataUtil.getDataFormatada(data));
        q.setParameter("tipoLancamento", tipoLancamento.name());

        return (ConfigBensImoveis) q.getSingleResult();

    }

    public void encerrarVigencia(ConfigBensImoveis entity) {
        utilFacade.validarEncerramentoVigencia(entity.getInicioVigencia(), entity.getFimVigencia(), entity.getEventoContabil());
        super.salvar(entity);
    }
}
