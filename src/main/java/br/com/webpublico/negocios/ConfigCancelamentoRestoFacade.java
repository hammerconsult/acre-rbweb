package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.PatrimonioLiquido;
import br.com.webpublico.enums.TipoEmpenhoEstorno;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoRestosProcessado;
import br.com.webpublico.util.DataUtil;
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
public class ConfigCancelamentoRestoFacade extends AbstractFacade<ConfigCancelamentoResto> {

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

    public ConfigCancelamentoRestoFacade() {
        super(ConfigCancelamentoResto.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ConfigCancelamentoResto recuperar(Object id) {
        ConfigCancelamentoResto conf = em.find(ConfigCancelamentoResto.class, id);
        conf.getConfigCancelamentoRestoContaDespesas().size();
        return conf;
    }

    public void meuSalvar(ConfigCancelamentoResto configNaoAlterada, ConfigCancelamentoResto selecionado) {
        verifcaAlteracoesEvento(configNaoAlterada, selecionado);
        if (selecionado.getId() == null) {
            salvarNovo(selecionado);
        } else {
            salvar(selecionado);
        }
    }

    public void verifcaAlteracoesEvento(ConfigCancelamentoResto configNaoAlterado, ConfigCancelamentoResto selecionado) {
        if (selecionado.getId() == null) {
            return;
        }
        boolean alterou = false;
        if (!configNaoAlterado.getTipoLancamento().equals(selecionado.getTipoLancamento())) {
            alterou = true;
        }
        for (ConfigCancRestoContaDesp configCancRestoContaDesp : selecionado.getConfigCancelamentoRestoContaDespesas()) {
            if (!configNaoAlterado.getConfigCancelamentoRestoContaDespesas().contains(configCancRestoContaDesp)) {
                alterou = true;
            }
        }
        if (!configNaoAlterado.getInicioVigencia().equals(selecionado.getInicioVigencia())) {
            alterou = true;
        }
        if (!configNaoAlterado.getEventoContabil().equals(selecionado.getEventoContabil())) {
            eventoContabilFacade.geraEventosReprocessar(selecionado.getEventoContabil(), selecionado.getId(), selecionado.getClass().getSimpleName());
            eventoContabilFacade.geraEventosReprocessar(configNaoAlterado.getEventoContabil(), configNaoAlterado.getId(), configNaoAlterado.getClass().getSimpleName());
        }
        if (alterou) {
            eventoContabilFacade.geraEventosReprocessar(selecionado.getEventoContabil(), selecionado.getId(), selecionado.getClass().getSimpleName());
        }
    }

    public boolean hasConfiguracaoExistente(Conta conta, TipoLancamento tipoLancamento, ConfiguracaoEvento cr, Date dataVigencia, TipoRestosProcessado tipoResto, TipoEmpenhoEstorno tipoEmpenhoEstorno, PatrimonioLiquido patrimonioLiquido) {
        String sql = "SELECT CE.*,CCR.* "
            + " FROM CONFIGCANCELAMENTORESTO CCR "
            + " INNER JOIN CONFIGURACAOEVENTO CE ON CCR.ID = CE.ID"
            + " INNER JOIN CONFIGCANCRESTOCONTADESP C ON C.CONFIGCANCELAMENTORESTO_ID = CCR.ID "
            + " WHERE C.CONTADESPESA_ID = :conta "
            + " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) "
            + " AND CE.TIPOLANCAMENTO = :tipoLancamento "
            + " AND CCR.TIPORESTOSPROCESSADOS = :tipoRestoEmpenho"
            + " and ccr.patrimonioLiquido = :patrimonioLiquido "
            + " and ccr.cancelamentoPrescricao = :cancelamentoPrescricao ";
        if (cr.getId() != null) {
            sql += " AND CE.ID <> :config";
        }
        Query q = em.createNativeQuery(sql, ConfigEmpenhoRestoPagar.class);
        q.setParameter("conta", conta.getId());
        q.setParameter("tipoLancamento", tipoLancamento.name());
        q.setParameter("tipoRestoEmpenho", tipoResto.name());
        q.setParameter("data", new SimpleDateFormat("dd/MM/yyyy").format(dataVigencia));
        q.setParameter("cancelamentoPrescricao", tipoEmpenhoEstorno.name());
        q.setParameter("patrimonioLiquido", patrimonioLiquido.name());
        if (cr.getId() != null) {
            q.setParameter("config", cr.getId());
        }
        return q.getResultList() != null && !q.getResultList().isEmpty();
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

    public ConfigCancelamentoResto recuperaEventoPorContaDespesa(Conta c, TipoLancamento tipoLancamento, Date data, TipoRestosProcessado tipoRestoEmp) throws ExcecaoNegocioGenerica, NonUniqueResultException {
        String msgErro;
        Preconditions.checkNotNull(tipoLancamento, "Tipo de Lançamento não informado");
        Preconditions.checkNotNull(data, " A data do Lançamento esta vazia");
        Preconditions.checkNotNull(tipoRestoEmp, " Tipo de Resto não informado.");
        Preconditions.checkNotNull(c.getId(), " A conta de Despesa esta vazia ");
        try {
            return recuperaConfiguracaoCancelamentoResto(c, tipoLancamento, data, tipoRestoEmp);

        } catch (NoResultException nr) {
            msgErro = "Evento não encontrado para a Conta: " + c + ". Tipo de Lançamento: " + tipoLancamento.getDescricao() + ". Tipo de Resto: " + tipoRestoEmp.getDescricao() + " na data " + new SimpleDateFormat("dd/MM/yyyy").format(data);
            throw new ExcecaoNegocioGenerica(msgErro);
        } catch (Exception e) {
            msgErro = e.getMessage();
            throw new ExcecaoNegocioGenerica(msgErro);
        }
    }

    private ConfigCancelamentoResto recuperaConfiguracaoCancelamentoResto(Conta c, TipoLancamento tipoLancamento, Date data, TipoRestosProcessado tipoRestoEmp) throws Exception {
        String sql = "SELECT CE.*, CCR.* "
            + " FROM CONFIGCANCELAMENTORESTO CCR "
            + " INNER JOIN CONFIGURACAOEVENTO CE ON CCR.ID = CE.ID "
            + " INNER JOIN EVENTOCONTABIL EC ON CE.EVENTOCONTABIL_ID = EC.ID "
            + " INNER JOIN CONFIGCANCRESTOCONTADESP CONFIG ON CONFIG.CONFIGCANCELAMENTORESTO_ID = CCR.ID "
            + " INNER JOIN CONTA C ON CONFIG.CONTADESPESA_ID = C.ID "
            + " WHERE C.CODIGO = :conta "
            + " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) "
            + " AND CE.TIPOLANCAMENTO = :tipoLancamento "
            + " AND CCR.TIPORESTOSPROCESSADOS = :tipoRestoEmpenho ";

        Query q = em.createNativeQuery(sql, ConfigCancelamentoResto.class);
        q.setParameter("conta", c.getCodigo());
        q.setParameter("data", DataUtil.getDataFormatada(data));
        q.setParameter("tipoLancamento", tipoLancamento.name());
        q.setParameter("tipoRestoEmpenho", tipoRestoEmp.name());
        ConfigCancelamentoResto ce = (ConfigCancelamentoResto) q.getSingleResult();
        return ce;
    }

    public ConfigCancelamentoResto recuperarEventoPorContaDespesaAndTipoEmpenhoEstorno(Conta c, TipoLancamento tipoLancamento, Date data, TipoRestosProcessado tipoRestoEmp, TipoEmpenhoEstorno tipoEmpenhoEstorno, br.com.webpublico.enums.PatrimonioLiquido patrimonioLiquido) throws ExcecaoNegocioGenerica, NonUniqueResultException {
        String msgErro;
        Preconditions.checkNotNull(tipoLancamento, "Tipo de Lançamento não informado");
        Preconditions.checkNotNull(data, " A data do Lançamento esta vazia");
        Preconditions.checkNotNull(tipoRestoEmp, " Tipo de Resto não informado.");
        Preconditions.checkNotNull(c.getId(), " A conta de Despesa esta vazia ");
        Preconditions.checkNotNull(tipoEmpenhoEstorno, " O tipo de empenho estorno esta vazio ");
        Preconditions.checkNotNull(patrimonioLiquido, " O patrimonio líquido esta vazio ");
        try {
            return recuperarConfiguracaoCancelamentoRestoPorTipoEmpenhoEstorno(c, tipoLancamento, data, tipoRestoEmp, tipoEmpenhoEstorno, patrimonioLiquido);

        } catch (NoResultException nr) {
            msgErro = "Evento não encontrado para a Conta: " + c + ". Tipo de Lançamento: " + tipoLancamento.getDescricao() + ". Tipo de Resto: " + tipoRestoEmp.getDescricao() + ", Cancelamento/Prescrição: " + tipoEmpenhoEstorno.getDescricao() + ", Patrimonio Líquido: " + patrimonioLiquido.getDescricao() + " na data " + new SimpleDateFormat("dd/MM/yyyy").format(data);
            throw new ExcecaoNegocioGenerica(msgErro);
        } catch (Exception e) {
            msgErro = e.getMessage();
            throw new ExcecaoNegocioGenerica(msgErro);
        }
    }

    private ConfigCancelamentoResto recuperarConfiguracaoCancelamentoRestoPorTipoEmpenhoEstorno(Conta c, TipoLancamento tipoLancamento, Date data, TipoRestosProcessado tipoRestoEmp, TipoEmpenhoEstorno tipoEmpenhoEstorno, PatrimonioLiquido patrimonioLiquido) throws Exception {
        String sql = "SELECT CE.*, CCR.* "
            + " FROM CONFIGCANCELAMENTORESTO CCR "
            + " INNER JOIN CONFIGURACAOEVENTO CE ON CCR.ID = CE.ID "
            + " INNER JOIN EVENTOCONTABIL EC ON CE.EVENTOCONTABIL_ID = EC.ID "
            + " INNER JOIN CONFIGCANCRESTOCONTADESP CONFIG ON CONFIG.CONFIGCANCELAMENTORESTO_ID = CCR.ID "
            + " INNER JOIN CONTA C ON CONFIG.CONTADESPESA_ID = C.ID "
            + " WHERE C.CODIGO = :conta "
            + " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) "
            + " AND CE.TIPOLANCAMENTO = :tipoLancamento "
            + " AND CCR.TIPORESTOSPROCESSADOS = :tipoRestoEmpenho "
            + " and ccr.patrimonioLiquido = :patrimonioLiquido "
            + " and ccr.cancelamentoPrescricao = :cancelamentoPrescricao  ";

        Query q = em.createNativeQuery(sql, ConfigCancelamentoResto.class);
        q.setParameter("conta", c.getCodigo());
        q.setParameter("data", DataUtil.getDataFormatada(data));
        q.setParameter("tipoLancamento", tipoLancamento.name());
        q.setParameter("tipoRestoEmpenho", tipoRestoEmp.name());
        q.setParameter("cancelamentoPrescricao", tipoEmpenhoEstorno.name());
        q.setParameter("patrimonioLiquido", patrimonioLiquido.name());
        return (ConfigCancelamentoResto) q.getSingleResult();
    }

    public void encerrarVigencia(ConfigCancelamentoResto entity) {
        utilFacade.validarEncerramentoVigencia(entity.getInicioVigencia(), entity.getFimVigencia(), entity.getEventoContabil());
        super.salvar(entity);
    }
}
