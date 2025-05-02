/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.MotoristaSaud;
import br.com.webpublico.entidades.VeiculoSaud;
import br.com.webpublico.entidades.VistoriaVeiculoSaud;
import br.com.webpublico.enums.SituacaoCadastralPessoa;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.VeiculoSaudFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

/**
 * @author Usuario
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listarVeiculoSaud",
        pattern = "/tributario/saud/veiculo-saud/listar/",
        viewId = "/faces/tributario/saud/veiculosaud/lista.xhtml"),
    @URLMapping(id = "novoVeiculoSaud",
        pattern = "/tributario/saud/veiculo-saud/novo/",
        viewId = "/faces/tributario/saud/veiculosaud/edita.xhtml"),
    @URLMapping(id = "editarVeiculoSaud",
        pattern = "/tributario/saud/veiculo-saud/editar/#{veiculoSaudControlador.id}/",
        viewId = "/faces/tributario/saud/veiculosaud/edita.xhtml"),
    @URLMapping(id = "verVeiculoSaud",
        pattern = "/tributario/saud/veiculo-saud/ver/#{veiculoSaudControlador.id}/",
        viewId = "/faces/tributario/saud/veiculosaud/visualizar.xhtml")
})
public class VeiculoSaudControlador extends PrettyControlador<VeiculoSaud> implements Serializable, CRUD {

    @EJB
    private VeiculoSaudFacade facade;
    private ConverterAutoComplete converterMotorista;
    private VistoriaVeiculoSaud vistoriaVeiculoSaud;

    public VeiculoSaudControlador() {
        super(VeiculoSaud.class);
    }

    @Override
    public VeiculoSaudFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/saud/veiculo-saud/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoVeiculoSaud", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setAtivo(Boolean.TRUE);
    }

    @URLAction(mappingId = "editarVeiculoSaud", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verVeiculoSaud", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        recuperarObjeto();
    }

    public ConverterAutoComplete getConverterMotorista() {
        if (converterMotorista == null) {
            converterMotorista = new ConverterAutoComplete(MotoristaSaud.class, getFacede().getMotoristaSaudFacade());
        }

        return converterMotorista;
    }

    public VistoriaVeiculoSaud getVistoriaVeiculoSaud() {
        return vistoriaVeiculoSaud;
    }

    public void setVistoriaVeiculoSaud(VistoriaVeiculoSaud vistoriaVeiculoSaud) {
        this.vistoriaVeiculoSaud = vistoriaVeiculoSaud;
    }

    public List<SelectItem> getTipoVeiculo() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (VeiculoSaud.TipoVeiculo tipoVeiculo : VeiculoSaud.TipoVeiculo.values()) {
            toReturn.add(new SelectItem(tipoVeiculo, tipoVeiculo.getDescricao()));
        }
        return toReturn;
    }

    public List<MotoristaSaud> completarMotoristas(String parte) {
        return facade.buscarFiltrandoMotoristasSaud(parte);
    }

    public void novaVistoria() {
        vistoriaVeiculoSaud = new VistoriaVeiculoSaud();
        vistoriaVeiculoSaud.setVeiculoSaud(selecionado);
    }

    public void adicionarVistoria() {
        try {
            validaVistoria();
            selecionado.getVistorias().add(vistoriaVeiculoSaud);
            setVistoriaVeiculoSaud(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Não foi possível adicionar a vistoria", ex);
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public void removerVistoria(VistoriaVeiculoSaud vistoria) {
        selecionado.getVistorias().remove(vistoria);
    }

    private void validaVistoria() {
        ValidacaoException ve = new ValidacaoException();
        if (vistoriaVeiculoSaud.getVistoriador() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Infome o vistoriador.");
        }
        if (vistoriaVeiculoSaud.getDataVistoria() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Infome a data da vistoria.");
        }
        if (vistoriaJaNaLista()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível modificar uma vistoria já feita.");
        }
        ve.lancarException();
    }

    public List<SituacaoCadastralPessoa> getSituacoesPesquisaPessoa() {
        return Lists.newArrayList(SituacaoCadastralPessoa.ATIVO);
    }

    public boolean vistoriaJaNaLista() {
        if (vistoriaVeiculoSaud != null) {
            return selecionado.getVistorias().contains(vistoriaVeiculoSaud);
        }
        return false;
    }

    public void uploadCrlv(FileUploadEvent event) {
        try {
            Arquivo arquivo = facade.getArquivoFacade().criarArquivo(event.getFile());
            selecionado.setCrlv(arquivo);
            FacesUtil.executaJavaScript("dlgUploadCrlv.hide()");
            FacesUtil.atualizarComponente("form:gridCrlv");
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }
}
