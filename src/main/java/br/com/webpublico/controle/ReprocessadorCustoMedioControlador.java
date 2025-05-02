/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.ItemSaidaMaterial;
import br.com.webpublico.entidades.MovimentoEstoque;
import br.com.webpublico.entidades.SaidaMaterial;
import br.com.webpublico.negocios.MovimentoEstoqueFacade;
import br.com.webpublico.negocios.ReprocessadorCustoMedioFacade;
import br.com.webpublico.negocios.SaidaMaterialFacade;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.DateSelectEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author renato
 */
@ManagedBean(name = "reprocessadorCustoMedioControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoReprocessadorCM", pattern = "/reprocessador-custo-medio/novo/", viewId = "/faces/administrativo/materiais/reprocessadorcustomedio/edita.xhtml")
})
public class ReprocessadorCustoMedioControlador implements Serializable {

    @EJB
    private ReprocessadorCustoMedioFacade reprocessadorCustoMedioFacade;
    @EJB
    private SaidaMaterialFacade saidaMaterialFacade;
    @EJB
    private MovimentoEstoqueFacade movimentoEstoqueFacade;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private Date dataDeInicio;
    private Date dataRecuperada;

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public Date getDataDeInicio() {
        return dataDeInicio;
    }

    public void setDataDeInicio(Date data) {
        this.dataDeInicio = data;
    }

    public Date getDataRecuperada() {
        return dataRecuperada;
    }

    public void setDataRecuperada(Date novaData) {
        this.dataRecuperada = novaData;
    }

    public void novoReprocessadorCustoMedio() {
        dataDeInicio = null;
        hierarquiaOrganizacional = null;
    }

    public void processarCustoMedio() {
        if (!validaDataDeInicio()) {
            return;
        }

        List<SaidaMaterial> listaDeSaidasRetroativas = recuperarSaidasDaUnidadeSelecionada();

        for (SaidaMaterial sm : listaDeSaidasRetroativas) {
            sm.setListaDeItemSaidaMaterial(saidaMaterialFacade.buscarItensSaidaMaterial(sm));

            for (ItemSaidaMaterial ism : sm.getListaDeItemSaidaMaterial()) {
                ism.setValorUnitarioReajustado(reprocessadorCustoMedioFacade.recalcularCustoMedioDoItemSaidaSemQuery(ism, hierarquiaOrganizacional.getSubordinada()));

                MovimentoEstoque movimentoEstoque = ism.getMovimentoEstoque();

                movimentoEstoque.setFinanceiroReajustado(ism.getQuantidade().multiply(ism.getValorUnitarioReajustado()));
                reprocessadorCustoMedioFacade.setaProcessadoNasSaidasEEntradasPosteriorADataSelecionada(dataDeInicio, hierarquiaOrganizacional.getSubordinada());

                movimentoEstoqueFacade.salvar(movimentoEstoque);
            }

            saidaMaterialFacade.salvar(sm);
        }

        exibirMensagemRecalculoConcluidoComSucesso();
    }

    private List<SaidaMaterial> recuperarSaidasDaUnidadeSelecionada() {
        List<SaidaMaterial> saidas = saidaMaterialFacade.recuperarSaidasNaoProcessadasAPartirDaData(dataDeInicio, hierarquiaOrganizacional.getSubordinada());

        return saidas;
    }

    public String toStringDaData() {
        if (dataDeInicio == null) {
            return new String();
        }
        return new SimpleDateFormat("dd/MM/yyyy").format(dataDeInicio);
    }

    public void setaData(DateSelectEvent event) {
        dataDeInicio = (Date) event.getDate();
    }

    private boolean validaDataDeInicio() {
        if (dataRecuperada.before(dataDeInicio)) {
            exibirMensagemDataPosteriorSelecionadaInvalida();
            dataDeInicio = null;
            return false;
        }
        return true;
    }

    public boolean desabilitaCalendar() {
        return dataDeInicio == null;
    }

    public void recuperarData() {
        if (validaRecuperarData()) {
            dataDeInicio = reprocessadorCustoMedioFacade.recuperarDataDaPrimeiraMovimentacaoRetroativa(hierarquiaOrganizacional.getSubordinada());
            dataRecuperada = dataDeInicio;
        }

        if (dataDeInicio == null) {
            exibeMensagemUnidadeOrganizacionalSemMovimentacaoRetroativa();
        }
    }

    private boolean validaRecuperarData() {
        if (hierarquiaOrganizacional == null) {
            exibeMensagemUnidadeOrganizacionalNaoSelecionada();
            return false;
        }

        return true;
    }

    private void exibeMensagemUnidadeOrganizacionalNaoSelecionada() {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Unidade Organizacional não selecionada!", "Selecione uma unidade organizacional antes de recuperar a data."));
    }

    private void exibirMensagemDataPosteriorSelecionadaInvalida() {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Data inválida!", "A data selecionada não pode ser posterior a data recuperada. Data recuperada: " + Util.dateToString(dataRecuperada)));
    }

    private void exibirMensagemRecalculoConcluidoComSucesso() {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Recálculo concluído!", "O recálculo do custo médio foi concluído com sucesso."));
    }

    private void exibeMensagemUnidadeOrganizacionalSemMovimentacaoRetroativa() {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                "Nenhuma data recuperada!", "A unidade organizacional selecionada não possui movimentações retroativas."));
    }
}
