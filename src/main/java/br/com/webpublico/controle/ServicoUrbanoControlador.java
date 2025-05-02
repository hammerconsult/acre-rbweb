/*
 * Codigo gerado automaticamente em Mon Apr 04 16:43:56 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ServicoUrbano;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ServicoUrbanoFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;

@ManagedBean(name = "servicoUrbanoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoServicoUrbano", pattern = "/servico-urbano/novo/", viewId = "/faces/tributario/cadastromunicipal/servicourbano/edita.xhtml"),
        @URLMapping(id = "editarServicoUrbano", pattern = "/servico-urbano/editar/#{servicoUrbanoControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/servicourbano/edita.xhtml"),
        @URLMapping(id = "listarServicoUrbano", pattern = "/servico-urbano/listar/", viewId = "/faces/tributario/cadastromunicipal/servicourbano/lista.xhtml"),
        @URLMapping(id = "verServicoUrbano", pattern = "/servico-urbano/ver/#{servicoUrbanoControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/servicourbano/visualizar.xhtml")
})
public class ServicoUrbanoControlador extends PrettyControlador<ServicoUrbano> implements Serializable, CRUD {

    @EJB
    private ServicoUrbanoFacade servicoUrbanoFacade;

    public ServicoUrbanoControlador() {
        super(ServicoUrbano.class);
    }

    public ServicoUrbanoFacade getFacade() {
        return servicoUrbanoFacade;
    }

    @URLAction(mappingId = "novoServicoUrbano", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verServicoUrbano", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarServicoUrbano", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/servico-urbano/";
    }

    @Override
    public AbstractFacade getFacede() {
        return servicoUrbanoFacade;
    }

    @Override
    public void redireciona() {
        FacesUtil.navegaEmbora(selecionado, getCaminhoPadrao());
    }

    public void validaIdentificador() {
        String identificador = ((ServicoUrbano) getSelecionado()).getIdentificacao();
        if (identificador.length() > 0) {
            if (!Character.isLetter(identificador.charAt(0))) {
                FacesContext.getCurrentInstance().addMessage("Formulario:identificador", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "A Identificação deve começar com uma letra"));
            }
            if (identificador.contains(" ")) {
                FacesContext.getCurrentInstance().addMessage("Formulario:identificador", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "A Identificação não pode conter espaços em branco"));
            }
        }
    }

    @Override
    public void salvar() {
        if (validaRegistro()) {
            super.salvar();
        }
    }

    public boolean validaRegistro() {
        boolean resultado = true;
        ServicoUrbano servicoUrbano = ((ServicoUrbano) selecionado);
        if (servicoUrbano.getNome() == null || servicoUrbano.getNome().trim().isEmpty()) {
            resultado = false;
            FacesUtil.addWarn("Atenção!", "O Nome é um campo obrigatório.");
        } else {
            if (servicoUrbanoFacade.jaExisteNome(servicoUrbano)) {
                resultado = false;
                FacesUtil.addWarn("Atenção!", "Já existe um Serviço Urbano cadastrado com o nome informado.");
            }
        }
        if (servicoUrbano.getIdentificacao() == null || servicoUrbano.getIdentificacao().trim().isEmpty()) {
            resultado = false;
            FacesUtil.addWarn("Atenção!", "O Identificador é um campo obrigatório.");
        } else {
            if (!Character.isLetter(servicoUrbano.getIdentificacao().charAt(0))) {
                resultado = false;
                FacesUtil.addWarn("Atenção!", "O Identificador deve começar com uma letra.");
            }
            if (servicoUrbano.getIdentificacao().contains(" ")) {
                resultado = false;
                FacesUtil.addWarn("Atenção!", "O Identificador não pode conter espaços.");
            }
            if (servicoUrbanoFacade.jaExisteIdentificacao(servicoUrbano)) {
                resultado = false;
              FacesUtil.addWarn("Atenção!", "O Identificador já utilizada. Informe outra.");
            }
        }
        return resultado;
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
