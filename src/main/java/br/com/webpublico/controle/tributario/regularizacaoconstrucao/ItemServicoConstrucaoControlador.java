package br.com.webpublico.controle.tributario.regularizacaoconstrucao;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.FaixaDeValoresSCL;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.ItemServicoConstrucao;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.ServicoConstrucao;
import br.com.webpublico.enums.tributario.regularizacaoconstrucao.TipoMedida;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.tributario.regularizacaoconstrucao.ItemServicoConstrucaoFacade;
import br.com.webpublico.negocios.tributario.regularizacaoconstrucao.ServicoConstrucaoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@ManagedBean(name = "itemServicoConstrucaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoItemServicoConstrucao", pattern = "/regularizacao-construcao/item-servico-construcao/novo/", viewId = "/faces/tributario/regularizacaoconstrucao/itemservicoconstrucao/edita.xhtml"),
    @URLMapping(id = "editarItemServicoConstrucao", pattern = "/regularizacao-construcao/item-servico-construcao/editar/#{itemServicoConstrucaoControlador.id}/", viewId = "/faces/tributario/regularizacaoconstrucao/itemservicoconstrucao/edita.xhtml"),
    @URLMapping(id = "listarItemServicoConstrucao", pattern = "/regularizacao-construcao/item-servico-construcao/listar/", viewId = "/faces/tributario/regularizacaoconstrucao/itemservicoconstrucao/lista.xhtml"),
    @URLMapping(id = "verItemServicoConstrucao", pattern = "/regularizacao-construcao/item-servico-construcao/ver/#{itemServicoConstrucaoControlador.id}/", viewId = "/faces/tributario/regularizacaoconstrucao/itemservicoconstrucao/visualizar.xhtml")
})

public class ItemServicoConstrucaoControlador extends PrettyControlador<ItemServicoConstrucao> implements Serializable, CRUD {

    @EJB
    private ItemServicoConstrucaoFacade itemServicoConstrucaoFacade;
    @EJB
    private ServicoConstrucaoFacade servicoConstrucaoFacade;
    private ConverterAutoComplete converterServicoConstrucao;
    private FaixaDeValoresSCL faixaDeValoresSCL;

    public ItemServicoConstrucaoControlador() {
        super(ItemServicoConstrucao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return itemServicoConstrucaoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/regularizacao-construcao/item-servico-construcao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoItemServicoConstrucao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setListaFaixaDeValoresSCL(new ArrayList<FaixaDeValoresSCL>());
        faixaDeValoresSCL = new FaixaDeValoresSCL();
    }

    @URLAction(mappingId = "verItemServicoConstrucao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarItemServicoConstrucao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        faixaDeValoresSCL = new FaixaDeValoresSCL();
    }

    public FaixaDeValoresSCL getFaixaDeValoresSCL() {
        return faixaDeValoresSCL;
    }

    public void setFaixaDeValoresSCL(FaixaDeValoresSCL faixaDeValoresSCL) {
        this.faixaDeValoresSCL = faixaDeValoresSCL;
    }

    public List<ServicoConstrucao> completarServicoConstrucao(String parte) {
        try {
            return servicoConstrucaoFacade.listarFiltrando(parte.trim());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            return new ArrayList<>();
        }
    }

    public Converter getConverterServicoConstrucao() {
        if (converterServicoConstrucao == null) {
            converterServicoConstrucao = new ConverterAutoComplete(ServicoConstrucao.class, servicoConstrucaoFacade);
        }
        return converterServicoConstrucao;
    }

    public List<SelectItem> getTiposMedidas() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, " "));
        for (TipoMedida tipo : TipoMedida.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }

    private void validarFaixaDeValoresSCL() {
        ValidacaoException ve = new ValidacaoException();
        if (faixaDeValoresSCL.getValorUFM() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Valor em UFM deve ser preenchido!");
        }
        if (faixaDeValoresSCL.getTipoMedida() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Medida deve ser preenchido!");
        }
        if (faixaDeValoresSCL.getExercicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício deve ser preenchido!");
        }
        ve.lancarException();
    }

    public void adicionarFaixaDeValoresSCL() {
        try {
            validarFaixaDeValoresSCL();
            faixaDeValoresSCL.setItemServicoConstrucao(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getListaFaixaDeValoresSCL(), faixaDeValoresSCL);
            faixaDeValoresSCL = new FaixaDeValoresSCL();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void limparFaixaDeValoresSCL() {
        faixaDeValoresSCL = new FaixaDeValoresSCL();
    }

    public void editarFaixaDeValoresSCL(FaixaDeValoresSCL fvscl) {
        faixaDeValoresSCL = (FaixaDeValoresSCL) Util.clonarObjeto(fvscl);
    }

    public void removerFaixaDeValoresSCL(FaixaDeValoresSCL fvscl) {
        selecionado.getListaFaixaDeValoresSCL().remove(fvscl);
    }
}
