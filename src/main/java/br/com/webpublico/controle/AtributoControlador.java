/*
 * Codigo gerado automaticamente em Wed Mar 23 09:29:06 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Atributo;
import br.com.webpublico.entidades.ValorPossivel;
import br.com.webpublico.enums.ClasseDoAtributo;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoAtributo;
import br.com.webpublico.enums.TipoComponenteVisual;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AtributoFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "atributoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoAtributo", pattern = "/atributo/novo/", viewId = "/faces/tributario/cadastromunicipal/atributo/edita.xhtml"),
    @URLMapping(id = "editarAtributo", pattern = "/atributo/editar/#{atributoControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/atributo/edita.xhtml"),
    @URLMapping(id = "listarAtributo", pattern = "/atributo/listar/", viewId = "/faces/tributario/cadastromunicipal/atributo/lista.xhtml"),
    @URLMapping(id = "verAtributo", pattern = "/atributo/ver/#{atributoControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/atributo/visualizar.xhtml")
})
public class AtributoControlador extends PrettyControlador<Atributo> implements Serializable, CRUD {

    @EJB
    private AtributoFacade atributoFacade;
    private ValorPossivel valorPossivel;

    public AtributoControlador() {
        super(Atributo.class);

    }

    @Override
    public String getCaminhoPadrao() {
        return "/atributo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoAtributo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setValoresPossiveis(new ArrayList<ValorPossivel>());
        valorPossivel = new ValorPossivel();
        selecionado.setAtivo(Boolean.TRUE);
    }

    public ValorPossivel getValorPossivel() {
        return valorPossivel;
    }

    public void setValorPossivel(ValorPossivel valorPossivel) {
        this.valorPossivel = valorPossivel;
    }

    public AtributoFacade getFacade() {
        return atributoFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return atributoFacade;
    }

    public List<SelectItem> getClasseDoAtributo() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, " "));
        for (ClasseDoAtributo object : ClasseDoAtributo.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoAtributo() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, " "));
        for (TipoAtributo object : TipoAtributo.values()) {
            toReturn.add(new SelectItem(object, object.name()));
        }
        return toReturn;
    }

    public void validarUnicoValorPadrao(ValorPossivel valorPossivel) {
        int index = 0;
        for (ValorPossivel vl : selecionado.getValoresPossiveis()) {
            if (!vl.equals(valorPossivel)) {
                if (vl.isValorPadrao()) {
                    selecionado.getValoresPossiveis().get(index).setValorPadrao(false);
                }
            }
            index++;
        }

    }

    public List<SelectItem> getTipoComponenteVisual() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, " "));
        for (TipoComponenteVisual object : TipoComponenteVisual.values()) {
            toReturn.add(new SelectItem(object, object.name()));
        }
        return toReturn;
    }

    public List<ValorPossivel> getValorPossivels() {
        return selecionado.getValoresPossiveis();
    }

    public void adicionarValorPossivel() {
        try {
            validarValorPossivel();
            valorPossivel.setAtributo(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getValoresPossiveis(), valorPossivel);
            valorPossivel = new ValorPossivel();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    private void validarValorPossivel() {
        valorPossivel.realizarValidacoes();
        if (selecionado.getValoresPossiveis().stream().anyMatch(vp -> !valorPossivel.equals(vp) &&
            vp.getCodigo().equals(valorPossivel.getCodigo()))) {
            throw new ValidacaoException("O código informado já está registrado.");
        }
    }

    public void alterarValorPossivel(ValorPossivel vp) {
        valorPossivel = vp;
    }

    public void removerValorPossivel(ValorPossivel valorPossivel) {
        selecionado.getValoresPossiveis().remove(valorPossivel);
    }

    @URLAction(mappingId = "editarAtributo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        valorPossivel = new ValorPossivel();
    }

    @URLAction(mappingId = "verAtributo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public void validaIdentificador(FacesContext context, UIComponent component, Object value) {
        FacesMessage message = new FacesMessage();
        String identificador = value.toString();
        if (identificador.length() > 0) {
            if (!Character.isLetter(identificador.charAt(0))) {
                message.setDetail("A palavra deve ser iniciada com uma letra");
                message.setSummary("A palavra deve ser iniciada com uma letra");
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(message);
            }
            if (identificador.contains(" ")) {
                message.setDetail("A palavra não pode conter espaços em branco");
                message.setSummary("A palavra não pode conter espaços em branco");
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(message);
            }
        }
    }

    public Boolean validaCampos() {
        boolean validacao = Util.validaCampos(selecionado);
        if (selecionado.getSomenteLeitura() && selecionado.getObrigatorio()) {
            validacao = false;
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "O Atributo não pode ser obrigatório e somente leitura, escolha apenas um");
        }
        if (!selecionado.getValoresPossiveis().isEmpty()) {
            boolean validarPadrao = false;
            for (ValorPossivel vl : selecionado.getValoresPossiveis()) {
                if (vl.isValorPadrao()) {
                    validarPadrao = true;
                }
            }
            if (!validarPadrao) {
                validacao = false;
                FacesUtil.addCampoObrigatorio("Selecione um valor como padrão");
            }
        }
        return validacao;
    }


    @Override
    public void salvar() {
        if (validaCampos()) {
            if (atributoFacade.concultaIdentificacao(selecionado, operacao)) {
                super.salvar();
            } else {
                FacesUtil.addError(SummaryMessages.ATENCAO.getDescricao(), "O valor do campo identificação já existe, insira outro");
            }
        }
    }

    public List<Atributo> completarAtributoConstrucao(String parte) {
        return atributoFacade.buscarAtributoPorClasse(ClasseDoAtributo.CONSTRUCAO, parte);
    }

    public List<Atributo> completarAtributos(String parte) {
        return atributoFacade.buscarAtributosAtivos(parte);
    }

    public void alterouTipoAtributo() {
        selecionado.setComponenteVisual(null);
        selecionado.setValoresPossiveis(new ArrayList<>());
        if (TipoAtributo.DISCRETO.equals(selecionado.getTipoAtributo())) {
            selecionado.setComponenteVisual(TipoComponenteVisual.COMBO);
        }
    }
}
