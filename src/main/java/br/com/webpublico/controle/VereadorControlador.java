package br.com.webpublico.controle;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.Vereador;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.VereadorFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
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

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 16/06/15
 * Time: 11:04
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "vereadorControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-vereador", pattern = "/planejamento/vereador/novo/", viewId = "/faces/financeiro/emenda/vereador/edita.xhtml"),
    @URLMapping(id = "editar-vereador", pattern = "/planejamento/vereador/editar/#{vereadorControlador.id}/", viewId = "/faces/financeiro/emenda/vereador/edita.xhtml"),
    @URLMapping(id = "ver-vereador", pattern = "/planejamento/vereador/ver/#{vereadorControlador.id}/", viewId = "/faces/financeiro/emenda/vereador/visualizar.xhtml"),
    @URLMapping(id = "listar-vereador", pattern = "/planejamento/vereador/listar/", viewId = "/faces/financeiro/emenda/vereador/lista.xhtml")
})
public class VereadorControlador extends PrettyControlador<Vereador> implements Serializable, CRUD {

    @EJB
    private VereadorFacade vereadorFacade;
    private ConverterAutoComplete converterExercicio;
    private ConverterAutoComplete converterPessoa;

    public VereadorControlador() {
        super(Vereador.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/planejamento/vereador/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return vereadorFacade;
    }

    @URLAction(mappingId = "novo-vereador", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editar-vereador", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "ver-vereador", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            if (isOperacaoNovo()) {
                vereadorFacade.salvarNovo(selecionado);
            } else {
                vereadorFacade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada("Registro salvo com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    private void validarCampos() {
        Util.validarCampos(selecionado);
        ValidacaoException ve = new ValidacaoException();
        if (vereadorFacade.hasVereadorVigente(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Vereador: " + selecionado.getPessoa().getNome() + " está vigente para o exercício de " + selecionado.getExercicio().getAno() + ".");
        }
        ve.lancarException();
    }

    public ConverterAutoComplete getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterAutoComplete(Exercicio.class, vereadorFacade.getExercicioFacade());
        }
        return converterExercicio;
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, vereadorFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public List<SelectItem> getExercicios() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, "Selecione"));
        for (Exercicio exercicio : vereadorFacade.getExercicioFacade().listaExerciciosAtualFuturos()) {
            retorno.add(new SelectItem(exercicio, exercicio.getAno().toString()));
        }
        return retorno;
    }

    public List<Pessoa> completaPessoa(String parte) {
        try {
            return vereadorFacade.getPessoaFacade().listaTodasPessoasAtivas(parte.trim());
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }
}
