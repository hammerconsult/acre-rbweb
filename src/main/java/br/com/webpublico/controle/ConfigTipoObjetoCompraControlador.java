package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfigTipoObjetoCompra;
import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.TipoObjetoCompraContaDespesa;
import br.com.webpublico.enums.SubTipoObjetoCompra;
import br.com.webpublico.enums.TipoObjetoCompra;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfigTipoObjetoCompraFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.List;

/**
 * Created by mga on 19/02/2018.
 */
@ManagedBean(name = "configTipoObjetoCompraControlador")
@ViewScoped

@URLMappings(mappings = {
    @URLMapping(id = "nova-config-tipoObjetoCompra", pattern = "/configuracao-tipo-objeto-compra/novo/", viewId = "/faces/administrativo/configuracao/tipoobjetocompra/edita.xhtml"),
    @URLMapping(id = "editar-config-tipoObjetoCompra", pattern = "/configuracao-tipo-objeto-compra/editar/#{configTipoObjetoCompraControlador.id}/", viewId = "/faces/administrativo/configuracao/tipoobjetocompra/edita.xhtml"),
    @URLMapping(id = "ver-config-tipoObjetoCompra", pattern = "/configuracao-tipo-objeto-compra/ver/#{configTipoObjetoCompraControlador.id}/", viewId = "/faces/administrativo/configuracao/tipoobjetocompra/visualizar.xhtml"),
    @URLMapping(id = "listar-config-tipoObjetoCompra", pattern = "/configuracao-tipo-objeto-compra/listar/", viewId = "/faces/administrativo/configuracao/tipoobjetocompra/lista.xhtml")
})
public class ConfigTipoObjetoCompraControlador extends PrettyControlador<ConfigTipoObjetoCompra> implements CRUD {

    @EJB
    private ConfigTipoObjetoCompraFacade facade;
    private TipoObjetoCompraContaDespesa tipoObjetoCompraContaDespesa;

    public ConfigTipoObjetoCompraControlador() {
        super(ConfigTipoObjetoCompra.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-tipo-objeto-compra/";
    }

    @Override
    @URLAction(mappingId = "nova-config-tipoObjetoCompra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setInicioVigencia(facade.getSistemaFacade().getDataOperacao());
        novaContaDespesa();
    }

    @Override
    @URLAction(mappingId = "ver-config-tipoObjetoCompra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "editar-config-tipoObjetoCompra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        novaContaDespesa();
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public List<SelectItem> getTiposObjetoCompra() {
        return Util.getListSelectItem(TipoObjetoCompra.values());
    }

    public List<SelectItem> getSubtiposObjetoCompra() {
        List<SelectItem> toReturn = Lists.newArrayList();
        if (selecionado.getTipoObjetoCompra() != null) {
            for (SubTipoObjetoCompra sub : SubTipoObjetoCompra.values()) {
                if (isObra()) {
                    if (!SubTipoObjetoCompra.NAO_APLICAVEL.equals(sub)) {
                        toReturn.add(new SelectItem(sub, sub.getDescricao()));
                    }
                } else if (isObraOrServico()) {
                    toReturn.add(new SelectItem(sub, sub.getDescricao()));
                } else {
                    toReturn.add(new SelectItem(SubTipoObjetoCompra.NAO_APLICAVEL, SubTipoObjetoCompra.NAO_APLICAVEL.getDescricao()));
                }
            }
        }
        return toReturn;
    }

    public void definirSubTipoObjetoCompra() {
        if (isObra()) {
            selecionado.setSubtipoObjetoCompra(null);
        } else {
            selecionado.setSubtipoObjetoCompra(SubTipoObjetoCompra.NAO_APLICAVEL);
        }
    }

    public boolean isObraOrServico() {
        return isObra() || isServico();
    }

    public boolean isObra() {
        return TipoObjetoCompra.PERMANENTE_IMOVEL.equals(selecionado.getTipoObjetoCompra());
    }

    public boolean isServico() {
        return TipoObjetoCompra.SERVICO.equals(selecionado.getTipoObjetoCompra());
    }

    private void novaContaDespesa() {
        tipoObjetoCompraContaDespesa = new TipoObjetoCompraContaDespesa();
    }

    public void adicionarConta() {
        try {
            Util.validarCampos(tipoObjetoCompraContaDespesa);
            selecionado.validarObjetoEmLista(tipoObjetoCompraContaDespesa.getContaDespesa());
            tipoObjetoCompraContaDespesa.setConfigTipoObjetoCompra(selecionado);
            selecionado.setContasDespesa(Util.adicionarObjetoEmLista(selecionado.getContasDespesa(), tipoObjetoCompraContaDespesa));
            novaContaDespesa();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    @Override
    public void salvar() {
        try {
            validarSalvar();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarSalvar() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCampos(selecionado);
        if (selecionado.getContasDespesa() == null || selecionado.getContasDespesa().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório adicionar uma conta de despesa para continuar .");
        }
        if (facade.verificarConfiguracaoVigente(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" Já existe uma configuração vigente para o tipo de objeto de compra: <b>"
                + selecionado.getTipoObjetoCompra().getDescricao() + " e </b> subtipo de objeto de compra <b>"
                + selecionado.getSubtipoObjetoCompra().getDescricao() + "</b>.");

        }
        ve.lancarException();
    }

    public void removerConta(TipoObjetoCompraContaDespesa obj) {
        selecionado.getContasDespesa().remove(obj);
    }

    public List<Conta> completarContaDespesa(String parte) {
        return facade.getContaDespesaFacade().listaFiltrandoContaDespesa(parte.trim(), facade.getSistemaFacade().getExercicioCorrente());
    }

    public TipoObjetoCompraContaDespesa getTipoObjetoCompraContaDespesa() {
        return tipoObjetoCompraContaDespesa;
    }

    public void setTipoObjetoCompraContaDespesa(TipoObjetoCompraContaDespesa tipoObjetoCompraContaDespesa) {
        this.tipoObjetoCompraContaDespesa = tipoObjetoCompraContaDespesa;
    }
}
