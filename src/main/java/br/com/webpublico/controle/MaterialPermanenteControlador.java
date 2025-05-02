package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "materialPermanenteControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoMaterialPermanente", pattern = "/material-permanente/novo/", viewId = "/faces/administrativo/patrimonio/materialpermanente/edita.xhtml"),
        @URLMapping(id = "editarMaterialPermanente", pattern = "/material-permanente/editar/#{materialPermanenteControlador.id}/", viewId = "/faces/administrativo/patrimonio/materialpermanente/edita.xhtml"),
        @URLMapping(id = "listarMaterialPermanente", pattern = "/material-permanente/listar/", viewId = "/faces/administrativo/patrimonio/materialpermanente/lista.xhtml"),
        @URLMapping(id = "verMaterialPermanente", pattern = "/material-permanente/ver/#{materialPermanenteControlador.id}/", viewId = "/faces/administrativo/patrimonio/materialpermanente/visualizar.xhtml")
})
public class MaterialPermanenteControlador extends PrettyControlador<MaterialPermanente> implements Serializable, CRUD {

    @EJB
    private MaterialPermanenteFacade materialPermanenteFacade;
    @EJB
    private ModeloFacade modeloFacade;
    @EJB
    private ObjetoCompraFacade objetoCompraFacade;
    @EJB
    private UnidadeMedidaFacade unidadeMedidaFacade;
    @EJB
    private MarcaFacade marcaFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private GrupoObjetoCompraGrupoBemFacade grupoObjetoCompraGrupoBemFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private GrupoObjetoCompraFacade grupoObjetoCompraFacade;
    private GrupoObjetoCompraGrupoBem grupoObjetoCompraGrupoBem;

    public MaterialPermanenteControlador() {
        metadata = new EntidadeMetaData(MaterialPermanente.class);
    }

    @Override
    public void excluir() {
        super.excluir();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/material-permanente/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novoMaterialPermanente", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        selecionado = (MaterialPermanente) Web.pegaDaSessao(MaterialPermanente.class);
        if (selecionado == null) {
            super.novo();
            selecionado.setCodigo(singletonGeradorCodigo.getProximoCodigo(MaterialPermanente.class, "codigo"));
        }
    }

    @Override
    @URLAction(mappingId = "editarMaterialPermanente", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        grupoObjetoCompraGrupoBem = selecionado.getAssociacaoDeGrupos();
    }

    @Override
    @URLAction(mappingId = "verMaterialPermanente", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    public AbstractFacade getFacede() {
        return materialPermanenteFacade;
    }

    public List<Modelo> completaModelo(String parte) {
        if (selecionado.getMarca() != null) {
            return modeloFacade.listaPorMarca(parte.trim(), selecionado.getMarca());
        } else {
            FacesUtil.addError(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), "Selecione uma marca.");
            return new ArrayList<>();
        }
    }

    public void setaMarca(SelectEvent evt) {
        selecionado.setMarca(((Marca) evt.getObject()));
    }

    public List<MaterialPermanente> completarMaterialPorDescricaoECodigo(String parte) {
        return materialPermanenteFacade.listaFiltrando(parte.trim(), "descricao", "codigo");
    }

    public void recuperarAssociacaoComGrupoBem() {
        grupoObjetoCompraGrupoBem = grupoObjetoCompraGrupoBemFacade.recuperarAssociacaoDoGrupoObjetoCompra(selecionado.getObjetoCompra().getGrupoObjetoCompra(), sistemaFacade.getDataOperacao());
        if (validarMaterialPermanente()) {
            selecionado.setAssociacaoDeGrupos(grupoObjetoCompraGrupoBem);
        }
    }

    @Override
    public boolean validaRegrasEspecificas() {
        return validarMaterialPermanente();
    }

    public boolean validarMaterialPermanente() {
        boolean retorno = true;
        if (grupoObjetoCompraGrupoBem == null) {
            retorno = false;
            FacesUtil.addOperacaoNaoRealizada("Não foi encontrada nenhuma associação do grupo objeto de compra " + selecionado.getObjetoCompra().getGrupoObjetoCompra() + " com Grupo Patrimonial.");
        } else {
            GrupoBem grupoBem = grupoObjetoCompraGrupoBem.getGrupoBem();
            if (grupoBem != null && grupoBem.getTipoBem() == null) {
                retorno = false;
                FacesUtil.addOperacaoNaoPermitida("O campo tipo do bem do grupo patrimonial " + grupoBem + " não está definido!");
            }
        }
        return retorno;
    }

    public GrupoObjetoCompraGrupoBem getGrupoObjetoCompraGrupoBem() {
        return grupoObjetoCompraGrupoBem;
    }

    public void setGrupoObjetoCompraGrupoBem(GrupoObjetoCompraGrupoBem grupoObjetoCompraGrupoBem) {
        this.grupoObjetoCompraGrupoBem = grupoObjetoCompraGrupoBem;
    }
}
