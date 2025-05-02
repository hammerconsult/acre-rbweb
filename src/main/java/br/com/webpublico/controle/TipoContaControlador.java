/*
 * Codigo gerado automaticamente em Fri Apr 15 09:13:56 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.TipoConta;
import br.com.webpublico.enums.ClasseDaConta;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.TipoContaFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "tipoContaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-tipo-conta", pattern = "/tipo-conta/novo/", viewId = "/faces/financeiro/planodecontas/tipoconta/edita.xhtml"),
    @URLMapping(id = "editar-tipo-conta", pattern = "/tipo-conta/editar/#{tipoContaControlador.id}/", viewId = "/faces/financeiro/planodecontas/tipoconta/edita.xhtml"),
    @URLMapping(id = "ver-tipo-conta", pattern = "/tipo-conta/ver/#{tipoContaControlador.id}/", viewId = "/faces/financeiro/planodecontas/tipoconta/visualizar.xhtml"),
    @URLMapping(id = "listar-tipo-conta", pattern = "/tipo-conta/listar/", viewId = "/faces/financeiro/planodecontas/tipoconta/lista.xhtml")
})
public class TipoContaControlador extends PrettyControlador<TipoConta> implements Serializable, CRUD {

    @EJB
    private TipoContaFacade tipoContaFacade;

    public TipoContaControlador() {
        super(TipoConta.class);
    }

    public TipoContaFacade getFacade() {
        return tipoContaFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoContaFacade;
    }

    public List<SelectItem> getClasseContas() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (ClasseDaConta object : ClasseDaConta.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    @Override
    public void excluir() {
        if (tipoContaFacade.validaRelacionamentoComPlanoDeContas(((TipoConta) selecionado))) {
            super.excluir();
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao tentar remover!", "Não é possível remover este Tipo de Conta, pois está associado com Plano de Contas."));
        }
    }

    @Override
    public void salvar() {
        try {
            Util.validarCampos(selecionado);
            validarMascara();
            tipoContaFacade.verificarExistente(selecionado);

            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), e.getMessage());
        }
    }

    private void validarMascara() {
        String nove = "9";
        String ponto = ".";
        for (char c : selecionado.getMascara().toCharArray()) {
            if (c != nove.charAt(0)
                && c != ponto.charAt(0)) {
                throw new ExcecaoNegocioGenerica("O campo Máscara deve utilizar os caracteres <b> 9 </b> ou '<b>.</b>' ");
            }
        }
    }


    @URLAction(mappingId = "novo-tipo-conta", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setExercicio(Util.getSistemaControlador().getExercicioCorrente());
    }

    @URLAction(mappingId = "ver-tipo-conta", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-tipo-conta", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tipo-conta/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<Exercicio> completarExercicio(String parte) {
        return tipoContaFacade.getExercicioFacade().listaFiltrandoEspecial(parte.trim());
    }
}

