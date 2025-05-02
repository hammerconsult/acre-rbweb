/*
 * Codigo gerado automaticamente em Fri Mar 04 09:44:14 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.negocios.AprovacaoMaterialFacade;
import br.com.webpublico.negocios.EntradaMaterialFacade;
import br.com.webpublico.negocios.RequisicaoMaterialFacade;
import br.com.webpublico.negocios.SaidaMaterialFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "consultaRequisicaoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novaConsultaRequisicao", pattern = "/consulta-requisicao/novo/", viewId = "/faces/administrativo/materiais/consulta/requisicaomaterial/consulta.xhtml")
})
public class ConsultaRequisicaoControlador {

    @EJB
    private RequisicaoMaterialFacade requisicaoMaterialFacade;
    @EJB
    private AprovacaoMaterialFacade aprovacaoMaterialFacade;
    @EJB
    private SaidaMaterialFacade saidaMaterialFacade;
    @EJB
    private EntradaMaterialFacade entradaMaterialFacade;
    private RequisicaoMaterial requisicaoMaterial;
    private AprovacaoMaterial  aprovacaoMaterial;
    private List<SaidaRequisicaoMaterial> listaSaidaDaRequisicao;
    private List<EntradaTransferenciaMaterial> listaEntradaTransferencia;
    private List<ItemRequisicaoMaterial> itensRequisitados;

    public ConsultaRequisicaoControlador() {
        aprovacaoMaterial = new AprovacaoMaterial();
    }

    @URLAction(mappingId = "novaConsultaRequisicao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaConsulta() {
    }

    public void recuperaItensDaRequisicao() {
        inicializar();
        if (requisicaoMaterial == null){
            FacesUtil.addOperacaoNaoPermitida("O campo requisição material deve ser informado.");
            return;
        }

        requisicaoMaterial = requisicaoMaterialFacade.recuperar(requisicaoMaterial.getId());
        aprovacaoMaterial = aprovacaoMaterialFacade.recuperarAprovacaoRequisicaoAndItens(requisicaoMaterial);

        if (aprovacaoMaterial != null) {
            listaSaidaDaRequisicao = saidaMaterialFacade.recuperaSaidasDaRequisicao(requisicaoMaterial);
            if (!listaSaidaDaRequisicao.isEmpty()) {
                List<EntradaTransferenciaMaterial> entrada = entradaMaterialFacade.recuperaEntradasPorSaidas(listaSaidaDaRequisicao);
                Collections.sort(entrada);
                listaEntradaTransferencia = entrada;
            }
        }
    }

    private void inicializar() {
        aprovacaoMaterial = new AprovacaoMaterial();
        listaEntradaTransferencia = new ArrayList<>();
        listaSaidaDaRequisicao = new ArrayList<>();
        itensRequisitados = new ArrayList<>();
    }

    public List<RequisicaoMaterial> completaRequisicaoMaterial(String parte) {
        return requisicaoMaterialFacade.buscarRequisicaoPorPeriodo(parte, null, null);
    }

    public RequisicaoMaterial getRequisicaoMaterial() {
        return requisicaoMaterial;
    }

    public void setRequisicaoMaterial(RequisicaoMaterial requisicaoMaterial) {
        this.requisicaoMaterial = requisicaoMaterial;
    }

    public AprovacaoMaterial getAprovacaoMaterial() {
        return aprovacaoMaterial;
    }

    public void setAprovacaoMaterial(AprovacaoMaterial aprovacaoMaterial) {
        this.aprovacaoMaterial = aprovacaoMaterial;
    }

    public List<ItemRequisicaoMaterial> getItensRequisitados() {
        return itensRequisitados;
    }

    public void setItensRequisitados(List<ItemRequisicaoMaterial> itensRequisitados) {
        this.itensRequisitados = itensRequisitados;
    }

    public List<SaidaRequisicaoMaterial> getListaSaidaDaRequisicao() {
        return listaSaidaDaRequisicao;
    }

    public void setListaSaidaDaRequisicao(List<SaidaRequisicaoMaterial> listaSaidaDaRequisicao) {
        this.listaSaidaDaRequisicao = listaSaidaDaRequisicao;
    }

    public List<EntradaTransferenciaMaterial> getListaEntradaTransferencia() {
        return listaEntradaTransferencia;
    }

    public void setListaEntradaTransferencia(List<EntradaTransferenciaMaterial> listaEntradaTransferencia) {
        this.listaEntradaTransferencia = listaEntradaTransferencia;
    }

    public String formatarDateDDMMYYYY(Date data) {
        if (data != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            return sdf.format(data);
        }

        return null;
    }
}
