package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfigEmpenhoRestoContaDesp;
import br.com.webpublico.entidades.ConfigEmpenhoRestoPagar;
import br.com.webpublico.entidades.ConfiguracaoEvento;
import br.com.webpublico.entidades.Conta;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoRestosProcessado;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Preconditions;
import org.hibernate.NonUniqueResultException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 27/09/13
 * Time: 09:27
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class ConfigEmpenhoRestoFacade extends AbstractFacade<ConfigEmpenhoRestoPagar> {

    @PersistenceContext(name = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private EmpenhoFacade empenhoFacade;
    @EJB
    private UtilConfiguracaoEventoContabilFacade utilFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public void meuSalvar(ConfigEmpenhoRestoPagar configEmpenhoNaoAlterado, ConfigEmpenhoRestoPagar selecionado) {
        verifcaAlteracoesEvento(configEmpenhoNaoAlterado, selecionado);
        if (selecionado.getId() == null) {
            salvarNovo(selecionado);
        } else {
            salvar(selecionado);
        }
    }

    public void verifcaAlteracoesEvento(ConfigEmpenhoRestoPagar configEmpenhoNaoAlterado, ConfigEmpenhoRestoPagar selecionado) {
        if (selecionado.getId() == null) {
            return;
        }
        boolean alterou = false;
        if (!configEmpenhoNaoAlterado.getTipoLancamento().equals(selecionado.getTipoLancamento())) {
            alterou = true;
        }
        for (ConfigEmpenhoRestoContaDesp configEmpenhoRestoContaDesp : selecionado.getConfigEmpenhoRestoContaDespesas()) {
            if (!configEmpenhoNaoAlterado.getConfigEmpenhoRestoContaDespesas().contains(configEmpenhoRestoContaDesp)) {
                alterou = true;
            }
        }
        if (!configEmpenhoNaoAlterado.getInicioVigencia().equals(selecionado.getInicioVigencia())) {
            alterou = true;
        }
        if (!configEmpenhoNaoAlterado.getEventoContabil().equals(selecionado.getEventoContabil())) {
            eventoContabilFacade.geraEventosReprocessar(selecionado.getEventoContabil(), selecionado.getId(), selecionado.getClass().getSimpleName());
            eventoContabilFacade.geraEventosReprocessar(configEmpenhoNaoAlterado.getEventoContabil(), configEmpenhoNaoAlterado.getId(), configEmpenhoNaoAlterado.getClass().getSimpleName());
        }
        if (alterou) {
            eventoContabilFacade.geraEventosReprocessar(selecionado.getEventoContabil(), selecionado.getId(), selecionado.getClass().getSimpleName());
        }
    }

    public boolean hasConfiguracaoExistente(Conta conta, TipoLancamento tipoLancamento, ConfiguracaoEvento cr, Date dataVigencia, TipoRestosProcessado tipoResto, Boolean emLiquidacao) {
        String sql = "SELECT CE.*,CEMP.* "
            + " FROM CONFIGEMPENHORESTOPAGAR CEMP "
            + " INNER JOIN CONFIGURACAOEVENTO CE ON CEMP.ID = CE.ID"
            + " INNER JOIN CONFIGEMPRESTOCONTADESP C ON C.CONFIGEMPENHORESTO_ID = CEMP.ID "
            + " WHERE C.CONTADESPESA_ID = :conta "
            + " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) "
            + " AND CE.TIPOLANCAMENTO = :tipoLancamento "
            + " AND CEMP.EMLIQUIDACAO = :emLiquidacao "
            + " AND CEMP.TIPORESTOSPROCESSADOS = :tipoRestoEmpenho";
        if (cr.getId() != null) {
            sql += " AND CE.ID <> :config";
        }
        Query q = em.createNativeQuery(sql, ConfigEmpenhoRestoPagar.class);
        q.setParameter("conta", conta.getId());
        q.setParameter("tipoLancamento", tipoLancamento.name());
        q.setParameter("tipoRestoEmpenho", tipoResto.name());
        q.setParameter("emLiquidacao", emLiquidacao);
        q.setParameter("data", DataUtil.getDataFormatada(dataVigencia));
        if (cr.getId() != null) {
            q.setParameter("config", cr.getId());
        }
        return q.getResultList() != null && !q.getResultList().isEmpty();
    }

    @Override
    public ConfigEmpenhoRestoPagar recuperar(Object id) {
        ConfigEmpenhoRestoPagar conf = em.find(ConfigEmpenhoRestoPagar.class, id);
        conf.getConfigEmpenhoRestoContaDespesas().size();
        return conf;
    }

    public ConfigEmpenhoRestoFacade() {
        super(ConfigEmpenhoRestoPagar.class);
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    public EmpenhoFacade getEmpenhoFacade() {
        return empenhoFacade;
    }

    public ConfigEmpenhoRestoPagar recuperarEventoPorContaDespesa(Conta c, TipoLancamento tipoLancamento, Date data, TipoRestosProcessado tipoRestoEmp, Boolean emLiquidacao) throws ExcecaoNegocioGenerica, NonUniqueResultException {
        String msgErro;
        Preconditions.checkNotNull(tipoLancamento, "Tipo de Lançamento não informado");
        Preconditions.checkNotNull(data, " A data do Lançamento esta vazia");
        Preconditions.checkNotNull(tipoRestoEmp, " Tipo de Resto não informado.");
        Preconditions.checkNotNull(c.getId(), " A conta de Receita esta vazia ");
        try {
            return recuperarConfiguracaoEmpenho(c, tipoLancamento, data, tipoRestoEmp, emLiquidacao);
        } catch (NoResultException nr) {
            msgErro = "Evento não encontrado para a Conta: " + c + ". Tipo de Lançamento: " + tipoLancamento.getDescricao() + ". Tipo de Resto: " + tipoRestoEmp.getDescricao() + ". Em Liquidação: " + Util.converterBooleanSimOuNao(emLiquidacao) + " na data " + new SimpleDateFormat("dd/MM/yyyy").format(data);
            throw new ExcecaoNegocioGenerica(msgErro);
        } catch (Exception e) {
            msgErro = e.getMessage();
            throw new ExcecaoNegocioGenerica(msgErro);
        }
    }

    private ConfigEmpenhoRestoPagar recuperarConfiguracaoEmpenho(Conta c, TipoLancamento tipoLancamento, Date data, TipoRestosProcessado tipoRestoEmp, Boolean emLiquidacao) {
        String sql = "SELECT CE.*, CEMP.* "
            + " FROM CONFIGEMPENHORESTOPAGAR CEMP"
            + " INNER JOIN CONFIGURACAOEVENTO CE ON CEMP.ID = CE.ID"
            + " INNER JOIN EVENTOCONTABIL EC ON CE.EVENTOCONTABIL_ID = EC.ID"
            + " INNER JOIN ConfigEmpRestoContaDesp CC ON CC.configEmpenhoResto_ID = CEMP.ID"
            + " INNER JOIN CONTA C ON CC.CONTADESPESA_ID = C.ID"
            + " WHERE C.CODIGO = :conta "
            + " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) "
            + " AND CE.TIPOLANCAMENTO = :tipoLancamento "
            + " AND CEMP.EMLIQUIDACAO = :emLiquidacao "
            + " AND CEMP.TIPORESTOSPROCESSADOS = :tipoRestoEmpenho ";

        Query q = em.createNativeQuery(sql, ConfigEmpenhoRestoPagar.class);
        q.setParameter("conta", c.getCodigo());
        q.setParameter("data", DataUtil.getDataFormatada(data));
        q.setParameter("tipoLancamento", tipoLancamento.name());
        q.setParameter("tipoRestoEmpenho", tipoRestoEmp.name());
        q.setParameter("emLiquidacao", emLiquidacao);
        ConfigEmpenhoRestoPagar ce = (ConfigEmpenhoRestoPagar) q.getSingleResult();
        return ce;
    }

    public void encerrarVigencia(ConfigEmpenhoRestoPagar entity) {
        utilFacade.validarEncerramentoVigencia(entity.getInicioVigencia(), entity.getFimVigencia(), entity.getEventoContabil());
        super.salvar(entity);
    }

}
