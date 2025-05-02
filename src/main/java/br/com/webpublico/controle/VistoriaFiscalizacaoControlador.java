/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoVistoria;
import br.com.webpublico.enums.TipoUsuarioTributario;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Heinz
 */
@ManagedBean(name = "vistoriaFiscalizacaoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoVistoriaFiscalizacao", pattern = "/vistoria-de-fiscalizacao/novo/", viewId = "/faces/tributario/cadastromunicipal/vistoriafiscalizacao/edita.xhtml"),
        @URLMapping(id = "editaVistoriaFiscalizacao", pattern = "/vistoria-de-fiscalizacao/editar/#{vistoriaFiscalizacaoControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/vistoriafiscalizacao/edita.xhtml"),
        @URLMapping(id = "listarVistoriaFiscalizacao", pattern = "/vistoria-de-fiscalizacao/listar/", viewId = "/faces/tributario/cadastromunicipal/vistoriafiscalizacao/lista.xhtml"),
        @URLMapping(id = "verVistoriaFiscalizacao", pattern = "/vistoria-de-fiscalizacao/ver/#{vistoriaFiscalizacaoControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/vistoriafiscalizacao/visualizar.xhtml")
})
public class VistoriaFiscalizacaoControlador extends PrettyControlador<VistoriaFiscalizacao> implements Serializable, CRUD {

    @EJB
    private VistoriaFiscalizacaoFacade vistoriaFiscalizacaoFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    private ConverterAutoComplete converterUsuarioSistema;
    @EJB
    private TipoVistoriaFacade tipoVistoriaFacade;
    private ConverterAutoComplete converterTipoVistoria;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    private ConverterAutoComplete converterCadastroEconomico;

    private TipoDocumentoFiscal tipoDocumentoFiscal;
    @EJB
    private TipoDocumentoFiscalFacade tipoDocumentoFiscalFacade;
    private ConverterAutoComplete converterTipoDocumentoFiscal;

    public VistoriaFiscalizacaoControlador() {
        super(VistoriaFiscalizacao.class);
    }


    public TipoDocumentoFiscal getTipoDocumentoFiscal() {
        return tipoDocumentoFiscal;
    }

    public void setTipoDocumentoFiscal(TipoDocumentoFiscal tipoDocumentoFiscal) {
        this.tipoDocumentoFiscal = tipoDocumentoFiscal;
    }


    public Converter getConverterUsuarioSistema() {
        if (converterUsuarioSistema == null) {
            converterUsuarioSistema = new ConverterAutoComplete(UsuarioSistema.class, usuarioSistemaFacade);
        }
        return converterUsuarioSistema;
    }

    public Converter getConverterTipoVistoria() {
        if (converterTipoVistoria == null) {
            converterTipoVistoria = new ConverterAutoComplete(TipoVistoria.class, tipoVistoriaFacade);
        }
        return converterTipoVistoria;
    }

    public Converter getConverterCadastroEconomico() {
        if (converterCadastroEconomico == null) {
            converterCadastroEconomico = new ConverterAutoComplete(CadastroEconomico.class, cadastroEconomicoFacade);
        }
        return converterCadastroEconomico;
    }

    public Converter getConverterTipoDocumentoFiscal() {
        if (converterTipoDocumentoFiscal == null) {
            converterTipoDocumentoFiscal = new ConverterAutoComplete(TipoDocumentoFiscal.class, tipoDocumentoFiscalFacade);
        }
        return converterTipoDocumentoFiscal;
    }

    public List<UsuarioSistema> completaUsuarioSistema(String parte) {
        List<UsuarioSistema> lista = usuarioSistemaFacade.listaFiltrandoUsuarioSistemaPorTipo(parte, TipoUsuarioTributario.FISCAL_TRIBUTARIO, TipoUsuarioTributario.SERVIDOR_CADASTRO, TipoUsuarioTributario.SERVIDOR_TRIBUTARIO);
        PessoaFisica erro = new PessoaFisica();
        UsuarioSistema erroLista = new UsuarioSistema();
        if (lista.isEmpty() && parte.trim().equals("")) {
            erro.setNome("Não existem fiscais cadastrados");
        } else if (lista.isEmpty()) {
            erro.setNome("Não foi encontrado(a) o(a) fiscal desejado(a)");
        } else {
            return lista;
        }
        erroLista.setPessoaFisica(erro);
        lista.add(erroLista);
        return lista;
    }

    public List<TipoVistoria> completaTipoVistoria(String parte) {
        List<TipoVistoria> lista = tipoVistoriaFacade.listaFiltrando(parte.trim(), "descricao");
        TipoVistoria erro = new TipoVistoria();
        if (lista.isEmpty() && parte.trim().equals("")) {
            erro.setDescricao("Não existem tipos de vistorias cadastrados");
        } else if (lista.isEmpty()) {
            erro.setDescricao("Não foi encontrado o tipo de vistoria desejado");
        } else {
            return lista;
        }
        lista.add(erro);
        return lista;
    }

    public List<TipoDocumentoFiscal> completaTipoDocumentoFiscal(String parte) {
        List<TipoDocumentoFiscal> lista = tipoDocumentoFiscalFacade.listaFiltrando(parte, "descricao");
        TipoDocumentoFiscal erro = new TipoDocumentoFiscal();
        if (lista.isEmpty() && parte.trim().equals("")) {
            erro.setDescricao("Não existem tipos de documentos fiscais cadastrados");
        } else if (lista.isEmpty()) {
            erro.setDescricao("Não foi encontrado o tipo de documento desejado");
        } else {
            return lista;
        }
        lista.add(erro);
        return lista;
    }

    public List<CadastroEconomico> completaCadastroEconomico(String parte) {
        return cadastroEconomicoFacade.listaCadastroEconomicoPorPessoa(parte);
    }

    public List<SelectItem> getListaSituacaoVistoria() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (SituacaoVistoria sit : SituacaoVistoria.values()) {
            toReturn.add(new SelectItem(sit, sit.getDescricao()));
        }
        return toReturn;
    }

    public List<VistoriaFiscalizacao> getLista() {
        return vistoriaFiscalizacaoFacade.lista();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/vistoria-de-fiscalizacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return vistoriaFiscalizacaoFacade;
    }

    @Override
    @URLAction(mappingId = "novoVistoriaFiscalizacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "editaVistoriaFiscalizacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    @URLAction(mappingId = "verVistoriaFiscalizacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            super.salvar();
        }
    }

    private boolean validaCampos() {
        boolean retorno = true;
        if (selecionado.getUsuarioSistema() == null) {
            FacesUtil.addError("Atenção!", "O(a) agente deve ser selecionado(a)");
            retorno = false;
        }

        if (selecionado.getParecer() == null) {
            FacesUtil.addError("Atenção!", "O parecer não foi especificado");
            retorno = false;
        }
        return retorno;
    }

    public void adicionarItem() {
        if (tipoDocumentoFiscal != null) {
            ItemValidacaoFiscalizacao itemValidacaoFiscalizacao = new ItemValidacaoFiscalizacao();
            itemValidacaoFiscalizacao.setTipoDocumentoFiscal(tipoDocumentoFiscal);
            itemValidacaoFiscalizacao.setVistoriaFiscalizacao(selecionado);
            selecionado.getItens().add(itemValidacaoFiscalizacao);
            tipoDocumentoFiscal = new TipoDocumentoFiscal();
        } else {
            FacesContext.getCurrentInstance().addMessage("Formulario:tipoDocumentoFiscal", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar. ", "Não foi possível adicionar o tipo documento fiscal na lista"));
        }
    }

    public void removeItem(ActionEvent event) {
        ItemValidacaoFiscalizacao aRemover = (ItemValidacaoFiscalizacao) event.getComponent().getAttributes().get("remove");
        boolean remove = selecionado.getItens().remove(aRemover);
        if (!remove) {
            FacesContext.getCurrentInstance().addMessage("Formulario:tipoDocumentoFiscal", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar. ", "Não foi possível remover o tipo documento fiscal da lista"));
        }
    }

    public void navegaTipoVistoria(){
        String origem = getCaminhoPadrao();
        if (selecionado.getId() != null) {
            origem += "editar/" + getUrlKeyValue() + "/";
        } else {
            origem += "novo/";
        }
        Web.navegacao(origem,"/tipo-de-vistoria/novo/",selecionado);
    }

}
