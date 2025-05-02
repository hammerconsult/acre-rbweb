package br.com.webpublico.controle;

import br.com.webpublico.controle.contabil.reprocessamento.AbstractReprocessamentoHistoricoControlador;
import br.com.webpublico.entidades.ContaBancariaEntidade;
import br.com.webpublico.entidades.ContaDeDestinacao;
import br.com.webpublico.entidades.SubConta;
import br.com.webpublico.entidadesauxiliares.AssistenteReprocessamento;
import br.com.webpublico.enums.TipoReprocessamentoHistorico;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ReprocessamentoSaldoSubContaFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mateus on 02/02/2015.
 */
@ManagedBean(name = "reprocessamentoSaldoSubContaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-reprocessamento-financeiro", pattern = "/reprocessamento-financeiro/novo/", viewId = "/faces/financeiro/orcamentario/reprocessamentosaldosubconta/edita.xhtml"),
    @URLMapping(id = "listar-reprocessamento-financeiro", pattern = "/reprocessamento-financeiro/listar/", viewId = "/faces/financeiro/orcamentario/reprocessamentosaldosubconta/lista.xhtml"),
    @URLMapping(id = "ver-reprocessamento-financeiro", pattern = "/reprocessamento-financeiro/ver/#{reprocessamentoSaldoSubContaControlador.id}/", viewId = "/faces/financeiro/orcamentario/reprocessamentosaldosubconta/visualizar.xhtml")
})
public class ReprocessamentoSaldoSubContaControlador extends AbstractReprocessamentoHistoricoControlador implements Serializable {
    @EJB
    private ReprocessamentoSaldoSubContaFacade facade;

    public ReprocessamentoSaldoSubContaControlador() {
    }

    @Override
    public String getCaminhoPadrao() {
        return "/reprocessamento-financeiro/";
    }

    @Override
    protected TipoReprocessamentoHistorico getTipo() {
        return TipoReprocessamentoHistorico.SALDO_SUB_CONTA;
    }

    @URLAction(mappingId = "novo-reprocessamento-financeiro", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        assistente = new AssistenteReprocessamento();
    }

    @URLAction(mappingId = "ver-reprocessamento-financeiro", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.recuperarObjeto();
    }

    public List<ContaBancariaEntidade> completarContaBancaria(String parte) {
        return facade.getContaBancariaEntidadeFacade().listaFiltrandoPorUnidadeDoUsuario(
            parte.trim(),
            facade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente(),
            facade.getSistemaFacade().getExercicioCorrente(),
            facade.getSistemaFacade().getUsuarioCorrente(),
            facade.getSistemaFacade().getDataOperacao());
    }

    public List<ContaDeDestinacao> completarContasDeDestinacao(String parte) {
        if (assistente.getSubConta() != null) {
            return facade.getContaFacade().buscarContasDeDestinacaoPorCodigoOrDescricaoESubConta(
                parte.trim(),
                facade.getSistemaFacade().getExercicioCorrente(),
                assistente.getSubConta().getId());
        }
        return new ArrayList<>();
    }

    public List<SubConta> completarContaFinanceira(String parte) {
        try {
            if (assistente.getContaBancariaEntidade() != null) {
                return facade.getContaBancariaEntidadeFacade().getSubContaFacade().listaPorContaBancariaEntidadeDoUsuarioLogadoOuTodas(
                    parte.trim(),
                    assistente.getContaBancariaEntidade(),
                    facade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente(),
                    facade.getSistemaFacade().getExercicioCorrente(),
                    facade.getSistemaFacade().getUsuarioCorrente(),
                    facade.getSistemaFacade().getDataOperacao());
            } else {
                return facade.getSubContaFacade().listaPorExercicio(parte.trim(), facade.getSistemaFacade().getExercicioCorrente());
            }
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public void reprocessar() {
        try {
            inicializarAssistente();
            future = facade.reprocessar(assistente);
            FacesUtil.executaJavaScript("dialogProgressBar.show()");
            FacesUtil.executaJavaScript("poll.start()");
            FacesUtil.atualizarComponente("formDialogProgressBar");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            FacesUtil.executaJavaScript("aguarde.hide()");
        } catch (Exception ex) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.addErrorGenerico(ex);
        }
    }

    @Override
    public void inicializarAssistente() {
        super.inicializarAssistente();
        assistente.setFiltro(getFiltros());
        assistente.setIdsContaFinanceira(recuperaSubContas());
    }

    private List<Long> recuperaSubContas() {
        List<Long> retorno = new ArrayList<>();
        if (assistente.getSubConta() != null) {
            retorno.add(assistente.getSubConta().getId());
        } else if (assistente.getContaBancariaEntidade() != null) {
            List<SubConta> contas = facade.getSubContaFacade().listaPorContaBancariaEntidade(assistente.getContaBancariaEntidade());
            if (!contas.isEmpty()) {
                for (SubConta sc : contas) {
                    retorno.add(sc.getId());
                }
            }
        }
        return retorno;
    }

    private String getFiltros() {
        StringBuilder stb = new StringBuilder();
        StringBuilder filtrosUtilizados = new StringBuilder();
        String concat = " where ";
        if (assistente.getContaBancariaEntidade() != null) {
            stb.append(concat).append(" contabancaria = ").append(assistente.getContaBancariaEntidade().getId());
            concat = " and ";
            filtrosUtilizados.append("Conta Bancária: ").append(assistente.getContaBancariaEntidade().toString()).append("; ");
        }
        if (assistente.getSubConta() != null) {
            stb.append(concat).append(" subconta = ").append(assistente.getSubConta().getId());
            concat = " and ";
            filtrosUtilizados.append("Conta Financeira: ").append(assistente.getSubConta().toString()).append("; ");
        }
        if (assistente.getContaDeDestinacao() != null) {
            stb.append(concat).append(" contadedestinacao = ").append(assistente.getContaDeDestinacao().getId());
            filtrosUtilizados.append("Conta de Destinação de Recursos: ").append(assistente.getContaDeDestinacao().toString()).append("; ");
        }
        if (assistente.getHistorico() != null) {
            assistente.getHistorico().setFiltrosUtilizados(filtrosUtilizados.toString());
        }
        return stb.toString();
    }
}
