/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfigPagamento;
import br.com.webpublico.entidades.ConfigPagamentoContaDesp;
import br.com.webpublico.entidades.ConfiguracaoEvento;
import br.com.webpublico.entidades.Conta;
import br.com.webpublico.enums.TipoContaDespesa;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.util.DataUtil;
import com.google.common.base.Preconditions;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Stateless
public class ConfigPagamentoFacade extends AbstractFacade<ConfigPagamento> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private PagamentoFacade pagamentoFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private UtilConfiguracaoEventoContabilFacade utilFacade;

    public ConfigPagamentoFacade() {
        super(ConfigPagamento.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    @Override
    public Object recuperar(Class entidade, Object id) {
        ConfigPagamento configPagamento = (ConfigPagamento) super.recuperar(ConfigPagamento.class, id);
        configPagamento.getConfigPagamentoContaDespesas().size();
        return configPagamento;
    }

    @Override
    public ConfigPagamento recuperar(Object id) {
        ConfigPagamento configPagamento = (ConfigPagamento) super.recuperar(ConfigPagamento.class, id);
        configPagamento.getConfigPagamentoContaDespesas().size();
        return configPagamento;
    }

    public Boolean verificaContaExistente(Conta conta, TipoLancamento tipoLancamento, ConfiguracaoEvento cr, Date dataVigencia) {
        ConfigPagamento cp = (ConfigPagamento) cr;
        String sql = "SELECT CE.*,CPAG.* "
                + " FROM CONFIGPAGAMENTO CPAG "
                + " INNER JOIN CONFIGURACAOEVENTO CE ON CPAG.ID = CE.ID"
                + " INNER JOIN CONFIGPAGAMENTOCONTADESP C ON C.CONFIGPAGAMENTO_ID = CPAG.ID "
                + " WHERE C.CONTADESPESA_ID = :conta "
                + " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) "
                + " AND CE.TIPOLANCAMENTO = :tipoLancamento"
                + " AND CPAG.tipocontadespesa = :tipoDesp";
        if (cr.getId() != null) {
            sql += " AND CE.ID <> :config";
        }

        Query q = em.createNativeQuery(sql, ConfigPagamento.class);
        q.setParameter("conta", conta.getId());
        q.setParameter("tipoLancamento", tipoLancamento.name());
        q.setParameter("data", new SimpleDateFormat("dd/MM/yyyy").format(dataVigencia));
        q.setParameter("tipoDesp", cp.getTipoContaDespesa().name());
        if (cr.getId() != null) {
            q.setParameter("config", cr.getId());
        }
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public ConfigPagamento recuperaEventoPorContaDespesa(Conta c, TipoLancamento tipoLancamento, TipoContaDespesa tipoContaDespesa, Date data) throws ExcecaoNegocioGenerica, NonUniqueResultException {
        String msgErro;
        Preconditions.checkNotNull(tipoLancamento, "Tipo de Lançamento não informado");
        Preconditions.checkNotNull(data, " A data do Lançamento esta vazia");
        Preconditions.checkNotNull(c.getId(), " A conta de Receita esta vazia ");
        Preconditions.checkNotNull(tipoContaDespesa, "O Tipo da Conta de Despesa do Empenho referente a esse pagamento esta vazia! ");
        try {
            return recuperaConfiguracaoPagamento(c, tipoLancamento, tipoContaDespesa, data);

        } catch (NoResultException nr) {
            msgErro = "Não foi encontrado nenhum Evento com a Conta " + c + "; Tipo Conta de Despesa: " + tipoContaDespesa.getDescricao() + " e Lançamento " + tipoLancamento.getDescricao() + " na data: " + new SimpleDateFormat("dd/MM/yyyy").format(data);
            throw new ExcecaoNegocioGenerica(msgErro);
        } catch (NonUniqueResultException e) {
            msgErro = "Foi encontrado mais de um Evento com a Conta " + c + "; Tipo Conta de Despesa: " + tipoContaDespesa.getDescricao() + " e Lançamento " + tipoLancamento.getDescricao() + " na data: " + new SimpleDateFormat("dd/MM/yyyy").format(data) + "! Conta no exercicio " + c.getExercicio().getAno() + ".";
            throw new ExcecaoNegocioGenerica(msgErro);
        } catch (Exception e) {
            msgErro = e.getMessage();
            throw new ExcecaoNegocioGenerica(msgErro);
        }
    }

    private ConfigPagamento recuperaConfiguracaoPagamento(Conta c, TipoLancamento tipoLancamento, TipoContaDespesa tipoContaDespesa, Date data) throws Exception {
        String sql = "SELECT CE.*, CEOPG.* "
                + " FROM CONFIGPAGAMENTO CEOPG"
                + " INNER JOIN CONFIGURACAOEVENTO CE ON CEOPG.ID = CE.ID"
                + " INNER JOIN EVENTOCONTABIL EC ON CE.EVENTOCONTABIL_ID = EC.ID"
                + " INNER JOIN CONFIGPAGAMENTOCONTADESP CC ON CC.CONFIGPAGAMENTO_ID = CEOPG.ID"
                + " WHERE CC.CONTADESPESA_ID = :conta "
                + " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) "
                + " AND CE.TIPOLANCAMENTO = :tipoLancamento"
                + " AND ceopg.tipocontadespesa =:tipocontadesp";

        Query q = em.createNativeQuery(sql, ConfigPagamento.class);

        q.setParameter("conta", c.getId());
        q.setParameter("data", DataUtil.getDataFormatada(data));
        q.setParameter("tipoLancamento", tipoLancamento.name());
        q.setParameter("tipocontadesp", tipoContaDespesa.name());

        return (ConfigPagamento) q.getSingleResult();
    }

    public void meuSalvar(ConfigPagamento configPagamentoNaoAlterado, ConfigPagamento selecionado) {
        verifcaAlteracoesEvento(configPagamentoNaoAlterado, selecionado);
        if (selecionado.getId() == null) {
            salvarNovo(selecionado);
        } else {
            salvar(selecionado);
        }
    }

    public void verifcaAlteracoesEvento(ConfigPagamento configPagamentoNaoAlterado, ConfigPagamento selecionado) {
        //System.out.println("Verifica alterações no Evento");

        if (selecionado.getId() == null) {
            return;
        }
        boolean alterou = false;
        if (!configPagamentoNaoAlterado.getTipoLancamento().equals(selecionado.getTipoLancamento())) {
            alterou = true;
        }
        for (ConfigPagamentoContaDesp configPagamentoContaDesp : selecionado.getConfigPagamentoContaDespesas()) {
            if (!configPagamentoNaoAlterado.getConfigPagamentoContaDespesas().contains(configPagamentoContaDesp)) {
                alterou = true;
                //System.out.println("alterou a conta de despesa");
            }
        }
        if (!configPagamentoNaoAlterado.getInicioVigencia().equals(selecionado.getInicioVigencia())) {
            alterou = true;
        }
        if (!configPagamentoNaoAlterado.getEventoContabil().equals(selecionado.getEventoContabil())) {
            eventoContabilFacade.geraEventosReprocessar(selecionado.getEventoContabil(), selecionado.getId(), selecionado.getClass().getSimpleName());
            eventoContabilFacade.geraEventosReprocessar(configPagamentoNaoAlterado.getEventoContabil(), configPagamentoNaoAlterado.getId(), configPagamentoNaoAlterado.getClass().getSimpleName());
        }
        if (alterou) {
            //System.out.println("alterou alguma coisa vai gerar reprocessamento");
            eventoContabilFacade.geraEventosReprocessar(selecionado.getEventoContabil(), selecionado.getId(), selecionado.getClass().getSimpleName());
        }

    }

    public void encerrarVigencia(ConfigPagamento entity) {
        utilFacade.validarEncerramentoVigencia(entity.getInicioVigencia(), entity.getFimVigencia(), entity.getEventoContabil());
        super.salvar(entity);
    }
}
