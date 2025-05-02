/*
 * Codigo gerado automaticamente em Tue Feb 07 08:22:09 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.UnidadeGestora;
import br.com.webpublico.enums.CategoriaDeclaracaoPrestacaoContas;
import br.com.webpublico.entidades.DeclaracaoPrestacaoContas;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoDeclaracaoPrestacaoContas;
import br.com.webpublico.enums.TipoUnidadeGestora;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.DeclaracaoPrestacaoContasFacade;
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

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-declaracao-prestacao-contas", pattern = "/declaracao-prestacao-contas/novo/", viewId = "/faces/rh/administracaodepagamento/declaracaoprestacaocontas/edita.xhtml"),
    @URLMapping(id = "editar-declaracao-prestacao-contas", pattern = "/declaracao-prestacao-contas/editar/#{declaracaoPrestacaoContasControlador.id}/", viewId = "/faces/rh/administracaodepagamento/declaracaoprestacaocontas/edita.xhtml"),
    @URLMapping(id = "ver-declaracao-prestacao-contas", pattern = "/declaracao-prestacao-contas/ver/#{declaracaoPrestacaoContasControlador.id}/", viewId = "/faces/rh/administracaodepagamento/declaracaoprestacaocontas/visualizar.xhtml"),
    @URLMapping(id = "listar-declaracao-prestacao-contas", pattern = "/declaracao-prestacao-contas/listar/", viewId = "/faces/rh/administracaodepagamento/declaracaoprestacaocontas/lista.xhtml")
})
public class DeclaracaoPrestacaoContasControlador extends PrettyControlador<DeclaracaoPrestacaoContas> implements Serializable, CRUD {

    @EJB
    private DeclaracaoPrestacaoContasFacade declaracaoPrestacaoContasFacade;

    public DeclaracaoPrestacaoContasControlador() {
        super(DeclaracaoPrestacaoContas.class);
    }

    public DeclaracaoPrestacaoContasFacade getFacade() {
        return declaracaoPrestacaoContasFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return declaracaoPrestacaoContasFacade;
    }

    public List<SelectItem> getTiposDeclaracaoPrestacaoContas() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoDeclaracaoPrestacaoContas object : TipoDeclaracaoPrestacaoContas.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposCategoriasPrestacaoContas() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (CategoriaDeclaracaoPrestacaoContas obj : CategoriaDeclaracaoPrestacaoContas.values()) {
            toReturn.add(new SelectItem(obj, obj.getDescricao()));
        }
        return toReturn;
    }

    @Override
    public void salvar() {
        if (declaracaoPrestacaoContasFacade.existeCodigo(selecionado)) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "O Código informado já está cadastrado em outra Declaração/Prestação de Contas!");
            return;
        }
        super.salvar();
    }

    @URLAction(mappingId = "novo-declaracao-prestacao-contas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editar-declaracao-prestacao-contas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "ver-declaracao-prestacao-contas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/declaracao-prestacao-contas/";
    }

    public List<UnidadeGestora> completarUnidadesGestoras(String parte) {

        return declaracaoPrestacaoContasFacade.getUnidadeGestoraFacade().buscarUnidadeGestoraPorTipoAndExercicio(parte.trim(), declaracaoPrestacaoContasFacade.getSistemaFacade().getExercicioCorrente(),  TipoUnidadeGestora.PRESTACAO_CONTAS);
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void novaUnidadeGestora() {
        Web.navegacao(getUrlAtual(), "/unidade-gestora/novo/", getSelecionado());
    }
}
