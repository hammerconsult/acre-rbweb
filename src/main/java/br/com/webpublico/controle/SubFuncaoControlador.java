/*
 * Codigo gerado automaticamente em Thu Mar 31 11:54:49 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Funcao;
import br.com.webpublico.entidades.SubFuncao;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.FuncaoFacade;
import br.com.webpublico.negocios.SubFuncaoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "subFuncaoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-sub-funcao", pattern = "/sub-funcao-ppa/novo/", viewId = "/faces/financeiro/ppa/subfuncao/edita.xhtml"),
        @URLMapping(id = "editar-sub-funcao", pattern = "/sub-funcao-ppa/editar/#{subFuncaoControlador.id}/", viewId = "/faces/financeiro/ppa/subfuncao/edita.xhtml"),
        @URLMapping(id = "ver-sub-funcao", pattern = "/sub-funcao-ppa/ver/#{subFuncaoControlador.id}/", viewId = "/faces/financeiro/ppa/subfuncao/visualizar.xhtml"),
        @URLMapping(id = "listar-sub-funcao", pattern = "/sub-funcao-ppa/listar/", viewId = "/faces/financeiro/ppa/subfuncao/lista.xhtml")
})
public class SubFuncaoControlador extends PrettyControlador<SubFuncao> implements Serializable, CRUD {

    @EJB
    private SubFuncaoFacade subFuncaoFacade;
    protected ConverterAutoComplete converterFuncao;

    public SubFuncaoControlador() {
        super(SubFuncao.class);
    }

    public SubFuncaoFacade getFacade() {
        return subFuncaoFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return subFuncaoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/sub-funcao-ppa/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<SelectItem> getFuncao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (Funcao object : subFuncaoFacade.getFuncaoFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getCodigo() + " - " + object.getDescricao()));
        }
        return toReturn;
    }

    public ConverterAutoComplete getConverterFuncao() {
        if (converterFuncao == null) {
            converterFuncao = new ConverterAutoComplete(Funcao.class, subFuncaoFacade.getFuncaoFacade());
        }
        return converterFuncao;
    }

    public List<Funcao> completaFuncao(String parte) {
        try {
            return subFuncaoFacade.getFuncaoFacade().listaFiltrandoFuncao(parte.trim());
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    @URLAction(mappingId = "novo-sub-funcao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "ver-sub-funcao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-sub-funcao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        if (!subFuncaoFacade.validaCodigoIgual(selecionado)) {
            FacesUtil.addOperacaoNaoPermitida("Existe outra Sub-Função cadastrada com o código " + selecionado.getCodigo() + ".");
            return;
        }
        super.salvar();
    }

    public List<SubFuncao> completarSubFuncoes(String filtro) {
        return subFuncaoFacade.listaFiltrandoSubFuncao(filtro);
    }
}
