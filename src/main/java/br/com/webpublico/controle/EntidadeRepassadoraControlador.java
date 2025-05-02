/*
 * Codigo gerado automaticamente em Wed Apr 04 15:31:02 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EntidadeRepassadoraFacade;
import br.com.webpublico.util.*;
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

@ManagedBean(name = "entidadeRepassadoraControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-entidade-concedente", pattern = "/entidade-concedente/novo/", viewId = "/faces/financeiro/convenios/receita/entidaderepassadora/edita.xhtml"),
        @URLMapping(id = "editar-entidade-concedente", pattern = "/entidade-concedente/editar/#{entidadeRepassadoraControlador.id}/", viewId = "/faces/financeiro/convenios/receita/entidaderepassadora/edita.xhtml"),
        @URLMapping(id = "ver-entidade-concedente", pattern = "/entidade-concedente/ver/#{entidadeRepassadoraControlador.id}/", viewId = "/faces/financeiro/convenios/receita/entidaderepassadora/visualizar.xhtml"),
        @URLMapping(id = "listar-entidade-concedente", pattern = "/entidade-concedente/listar/", viewId = "/faces/financeiro/convenios/receita/entidaderepassadora/lista.xhtml")
})
public class EntidadeRepassadoraControlador extends PrettyControlador<EntidadeRepassadora> implements Serializable, CRUD {

    @EJB
    private EntidadeRepassadoraFacade entidadeRepassadoraFacade;
    private ConverterAutoComplete converterPessoaJuridica;
    private ConverterGenerico converterEsferaGoverno;

    public EntidadeRepassadoraControlador() {
        super(EntidadeRepassadora.class);
    }

    @URLAction(mappingId = "novo-entidade-concedente", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "ver-entidade-concedente", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-entidade-concedente", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/entidade-concedente/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return entidadeRepassadoraFacade;
    }


    public void salvar() {
        try {
            if (Util.validaCampos(selecionado)) {
                if (selecionado.getPessoaJuridica() != null) {
                    if (entidadeRepassadoraFacade.validaMesmaPessoaParaEntidadeRepassadora(selecionado)) {
                        super.salvar();
                    } else {
                        FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " Já existe uma Entidade Concedente cadastrada para a Pessoa: " + selecionado.getPessoaJuridica());
                    }
                }
            }
        } catch (Exception ex) {
            FacesUtil.addError("Operação não Realizada! ", " Erro: " + ex.getMessage());
        }
    }

    public List<Pessoa> completaPessoaJuridica(String parte) {
        return entidadeRepassadoraFacade.getPessoaJuridicaFacade().listaPessoasJuridicas(parte.trim());
    }

    public ConverterAutoComplete getConverterPessoaJuridica() {
        if (converterPessoaJuridica == null) {
            converterPessoaJuridica = new ConverterAutoComplete(PessoaJuridica.class, entidadeRepassadoraFacade.getPessoaJuridicaFacade());
        }
        return converterPessoaJuridica;
    }


    public ConverterGenerico getConverterEsferaGoverno() {
        if (converterEsferaGoverno == null) {
            converterEsferaGoverno = new ConverterGenerico(EsferaGoverno.class, entidadeRepassadoraFacade.getEsferaGovernoFacade());
        }
        return converterEsferaGoverno;
    }

    public List<SelectItem> getEsferaGoverno() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (EsferaGoverno object : entidadeRepassadoraFacade.getEsferaGovernoFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getNome()));
        }
        return toReturn;
    }


    public EntidadeRepassadoraFacade getFacade() {
        return entidadeRepassadoraFacade;
    }


}
