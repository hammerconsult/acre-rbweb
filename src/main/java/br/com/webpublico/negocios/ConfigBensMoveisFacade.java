package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfigBensMoveis;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoOperacaoBensMoveis;
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
public class ConfigBensMoveisFacade extends AbstractFacade<ConfigBensMoveis> {

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

    public ConfigBensMoveisFacade() {
        super(ConfigBensMoveis.class);
    }

    public void verificaAlteracoesEvento(ConfigBensMoveis configBensMoveisNaoAlterado, ConfigBensMoveis config) {

        if (config.getId() == null) {
            return;
        }
        boolean alterou = false;
        if (!configBensMoveisNaoAlterado.getTipoLancamento().equals(config.getTipoLancamento())) {
            alterou = true;
        }
        if (!configBensMoveisNaoAlterado.getOperacaoBensMoveis().equals(config.getOperacaoBensMoveis())) {
            alterou = true;
        }
        if (!configBensMoveisNaoAlterado.getInicioVigencia().equals(config.getInicioVigencia())) {
            alterou = true;
        }
        if (!configBensMoveisNaoAlterado.getEventoContabil().equals(config.getEventoContabil())) {
            eventoContabilFacade.geraEventosReprocessar(config.getEventoContabil(), config.getId(), config.getClass().getSimpleName());
            eventoContabilFacade.geraEventosReprocessar(configBensMoveisNaoAlterado.getEventoContabil(), configBensMoveisNaoAlterado.getId(), configBensMoveisNaoAlterado.getClass().getSimpleName());
        }
        if (alterou) {
            eventoContabilFacade.geraEventosReprocessar(config.getEventoContabil(), config.getId(), config.getClass().getSimpleName());
        }
    }

    public void meuSalvar(ConfigBensMoveis configBensMoveisNaoAlterado, ConfigBensMoveis configuracaoEncontrada) {
        verificaAlteracoesEvento(configBensMoveisNaoAlterado, configuracaoEncontrada);
        if (configuracaoEncontrada.getId() == null) {
            salvarNovo(configuracaoEncontrada);
        } else {
            salvar(configuracaoEncontrada);
        }
    }


    public ConfigBensMoveis verificaConfiguracaoExistente(ConfigBensMoveis config, Date dataVigencia) {
        String sql = " SELECT CE.*, CBM.* ";
        sql += " FROM CONFIGBENSMOVEIS CBM ";
        sql += " INNER JOIN CONFIGURACAOEVENTO CE ON CBM.ID = CE.ID ";
        sql += " WHERE CE.TIPOLANCAMENTO = :tipoLancamento ";
        sql += " AND CBM.OPERACAOBENSMOVEIS = :tipoOperacao ";
        sql += " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) ";
        if (config.getId() != null) {
            sql += "and cbm.id <> :idConfig";
        }
        Query q = em.createNativeQuery(sql, ConfigBensMoveis.class);
        q.setParameter("tipoLancamento", config.getTipoLancamento().name());
        q.setParameter("tipoOperacao", config.getOperacaoBensMoveis().name());
        q.setParameter("data", DataUtil.getDataFormatada(dataVigencia));
        if (config.getId() != null) {
            q.setParameter("idConfig", config.getId());
        }
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (ConfigBensMoveis) q.getSingleResult();
        }
        return null;
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }

    public ConfigBensMoveis buscarEventoContabilPorOperacaoLancamentoAndDataMov(TipoOperacaoBensMoveis operacao, TipoLancamento tipoLancamento, Date data) throws ExcecaoNegocioGenerica, NonUniqueResultException {
        String msgErro;
        Preconditions.checkNotNull(tipoLancamento, "Tipo de Lançamento está vazio.");
        Preconditions.checkNotNull(data, "A data do Lançamento esta vazia.");
        Preconditions.checkNotNull(operacao.getDescricao(), "A operação está vazia.");
        try {
            return buscarConfigBensMoveisPorOperacaoLancamentoAndDataMov(operacao, tipoLancamento, data);

        } catch (NoResultException nr) {
            msgErro = "Evento contábil não encontrado para a operação: " + operacao.getDescricao() + "; tipo de lançamento: " + tipoLancamento.getDescricao() + " na data : " + new SimpleDateFormat("dd/MM/yyyy").format(data) + ".";
            throw new ExcecaoNegocioGenerica(msgErro);
        } catch (Exception e) {
            msgErro = e.getMessage();
            throw new ExcecaoNegocioGenerica(msgErro);
        }
    }

    private ConfigBensMoveis buscarConfigBensMoveisPorOperacaoLancamentoAndDataMov(TipoOperacaoBensMoveis operacao, TipoLancamento tipoLancamento, Date data) throws Exception {
        String sql = "SELECT CE.*, CBM.* "
            + " FROM CONFIGBENSMOVEIS CBM"
            + " INNER JOIN CONFIGURACAOEVENTO CE ON CBM.ID = CE.ID "
            + " INNER JOIN EVENTOCONTABIL EC ON CE.EVENTOCONTABIL_ID = EC.ID "
            + " WHERE CBM.OPERACAOBENSMOVEIS = :operacao"
            + " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) "
            + " AND CE.TIPOLANCAMENTO = :tipoLancamento";

        Query q = em.createNativeQuery(sql, ConfigBensMoveis.class);

        q.setParameter("operacao", operacao.name());
        q.setParameter("data", DataUtil.getDataFormatada(data));
        q.setParameter("tipoLancamento", tipoLancamento.name());

        return (ConfigBensMoveis) q.getSingleResult();
    }

    public void encerrarVigencia(ConfigBensMoveis entity) {
        utilFacade.validarEncerramentoVigencia(entity.getInicioVigencia(), entity.getFimVigencia(), entity.getEventoContabil());
        super.salvar(entity);
    }
}
