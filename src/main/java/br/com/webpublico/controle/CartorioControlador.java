/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Cartorio;
import br.com.webpublico.entidades.PessoaJuridica;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CartorioFacade;
import br.com.webpublico.negocios.PessoaJuridicaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "cartorioControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoCartorio", pattern = "/cartorio/novo/", viewId = "/faces/tributario/cadastromunicipal/cartorio/edita.xhtml"),
        @URLMapping(id = "editarCartorio", pattern = "/cartorio/editar/#{cartorioControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/cartorio/edita.xhtml"),
        @URLMapping(id = "listarCartorio", pattern = "/cartorio/listar/", viewId = "/faces/tributario/cadastromunicipal/cartorio/lista.xhtml"),
        @URLMapping(id = "verCartorio", pattern = "/cartorio/ver/#{cartorioControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/cartorio/visualizar.xhtml")
})
public class CartorioControlador extends PrettyControlador<Cartorio> implements Serializable, CRUD {

    @EJB
    private CartorioFacade cartoriofacade;
    @EJB
    private PessoaJuridicaFacade pessoaJuridicaFacade;
    private ConverterAutoComplete converterPessoaJuridica;

    public CartorioControlador() {
        super(Cartorio.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/cartorio/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoCartorio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editarCartorio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verCartorio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public boolean validaRegrasEspecificas() {
        Boolean valida = true;
        if (cartoriofacade.existeCartorio(selecionado)) {
            FacesUtil.addOperacaoNaoPermitida("O Cartório selecionado já está cadastrado.");
            valida = false;
        }
        return valida;
    }

    @Override
    public AbstractFacade getFacede() {
        return cartoriofacade;
    }

    public List<PessoaJuridica> completaPessoaJuridica(String parte) {
        return pessoaJuridicaFacade.listaFiltrandoRazaoSocialCNPJ(parte.trim());
    }

    public ConverterAutoComplete getConverterPessoaJuridica() {
        if (converterPessoaJuridica == null) {
            converterPessoaJuridica = new ConverterAutoComplete(PessoaJuridica.class, pessoaJuridicaFacade);
        }
        return converterPessoaJuridica;
    }
}
