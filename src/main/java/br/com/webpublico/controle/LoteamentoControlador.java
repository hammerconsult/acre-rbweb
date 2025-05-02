package br.com.webpublico.controle;

import br.com.webpublico.entidades.Cidade;
import br.com.webpublico.entidades.Loteamento;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CidadeFacade;
import br.com.webpublico.negocios.LoteamentoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "loteamentoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoLoteamento", pattern = "/tributario/loteamento/novo/",
                viewId = "/faces/tributario/cadastromunicipal/loteamento/edita.xhtml"),
        @URLMapping(id = "editarLoteamento", pattern = "/tributario/loteamento/editar/#{loteamentoControlador.id}/",
                viewId = "/faces/tributario/cadastromunicipal/loteamento/edita.xhtml"),
        @URLMapping(id = "listarLoteamento", pattern = "/tributario/loteamento/listar/",
                viewId = "/faces/tributario/cadastromunicipal/loteamento/lista.xhtml"),
        @URLMapping(id = "verLoteamento", pattern = "/tributario/loteamento/ver/#{loteamentoControlador.id}/",
                viewId = "/faces/tributario/cadastromunicipal/loteamento/visualizar.xhtml")
})
public class LoteamentoControlador extends PrettyControlador<Loteamento> implements Serializable, CRUD {

    @EJB
    private LoteamentoFacade facade;
    @EJB
    private CidadeFacade cidadeFacade;
    protected ConverterAutoComplete converterCidade;

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public LoteamentoControlador() {
        super(Loteamento.class);
    }

    public List<SelectItem> getCidade() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (Cidade object : cidadeFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getNome()));
        }
        return toReturn;
    }

    @URLAction(mappingId = "novoLoteamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verLoteamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarLoteamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    public Converter getConverterCidade() {
        if (converterCidade == null) {
            converterCidade = new ConverterAutoComplete(Cidade.class, cidadeFacade);
        }
        return converterCidade;
    }

    public List<Cidade> completaLoteamento(String parte) {
        return cidadeFacade.listaFiltrando(parte.trim(), "nome", "cep");
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/loteamento/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void salvar() {
        if (validaCadastro()) {
            try {
                if (operacao == Operacoes.NOVO) {
                    getFacede().salvarNovo(selecionado);
                } else {
                    getFacede().salvar(selecionado);
                }
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoSalvar()));
            } catch (ValidacaoException ex) {
                FacesUtil.printAllFacesMessages(ex.getMensagens());
                return;
            } catch (Exception e) {
                descobrirETratarException(e);

            }
            redireciona();
        }
    }

    public boolean validaCadastro() {
        boolean valida = true;
        if (selecionado.getCodigo() == null) {
            FacesUtil.addWarn("Atenção!", "O Código é um campo obrigatório.");
            valida = false;
        } else if (facade.existeLoteamentoPorCodigo(selecionado.getId(), selecionado.getCodigo())) {
            FacesUtil.addWarn("Atenção!", "O Código já existe.");
            valida = false;
        }

        if(selecionado.getNome() == null || selecionado.getNome().trim().isEmpty()){
            FacesUtil.addWarn("Atenção!", "O Nome é um campo obrigatório.");
            valida = false;
        }
        return valida;
    }
}
