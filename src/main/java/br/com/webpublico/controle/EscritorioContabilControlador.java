/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.EscritorioContabil;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EscritorioContabilFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "escritorioContabilControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "cadastrarEscritorioContabilTributario",pattern = "/tributario/escritoriocontabil/novo/",viewId = "/faces/tributario/cadastromunicipal/escritoriocontabil/edita.xhtml"),
    @URLMapping(id = "alteraEscritorioContabilTributario",pattern = "/tributario/escritoriocontabil/editar/#{escritorioContabilControlador.id}/",viewId = "/faces/tributario/cadastromunicipal/escritoriocontabil/edita.xhtml"),
    @URLMapping(id = "listarEscritorioContabil",pattern = "/tributario/escritoriocontabil/listar/",viewId = "/faces/tributario/cadastromunicipal/escritoriocontabil/lista.xhtml"),
    @URLMapping(id = "visualizarEscritorioContabilTributario",pattern = "/tributario/escritoriocontabil/ver/#{escritorioContabilControlador.id}/",viewId = "/faces/tributario/cadastromunicipal/escritoriocontabil/visualizar.xhtml")
})
public class EscritorioContabilControlador extends PrettyControlador<EscritorioContabil> implements Serializable, CRUD {

    @EJB
    private EscritorioContabilFacade escritorioContabilFacade;

    public EscritorioContabilControlador() {
        super(EscritorioContabil.class);
    }

    @Override
    @URLAction(mappingId = "cadastrarEscritorioContabilTributario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setCodigo(escritorioContabilFacade.retornaUltimoCodigoLong());
    }

    @Override
    @URLAction(mappingId = "visualizarEscritorioContabilTributario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();

    }

    @Override
    @URLAction(mappingId = "alteraEscritorioContabilTributario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    public void excluir() {
        if (escritorioContabilFacade.usadoEmBCE(selecionado)) {
            FacesUtil.addError("Impossível Excluir", "O Registro Selecionado está vinculado a um ou mais BCEs");
        } else {
            super.excluir();
        }
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            super.salvar();
        }
    }

    private boolean validaCampos() {
        boolean retorno = true;
        if (selecionado.getCodigo() == null) {
            retorno = false;
            FacesUtil.addError("Impossível Continuar", "O Campo Código deve ser Preenchido");
        } else if (selecionado.getCodigo().intValue() <= 0) {
            retorno = false;
            FacesUtil.addError("Impossível Continuar", "O Código deve ser Maior que Zero");
        } else if (escritorioContabilFacade.jaExisteCodigo(selecionado)) {
            retorno = false;
            FacesUtil.addError("Impossível Continuar", "Código já Existente");
        }
        if (selecionado.getPessoa() == null && selecionado.getResponsavel() == null) {
            retorno = false;
            FacesUtil.addError("Impossível Continuar", "Por favor, informe Escritório Contábil e/ou o Contador.");
        }
        return retorno;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/escritoriocontabil/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return escritorioContabilFacade;
    }

    public void atualizaForm(){
        FacesUtil.atualizarComponente("Formulario");
    }
}
