/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ExecucaoMetaFisicaAcao;
import br.com.webpublico.entidades.LDO;
import br.com.webpublico.entidades.SubAcaoPPA;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExecucaoMetaFisicaAcaoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.util.Util;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

/**
 *
 * @author Edi
 */
@ManagedBean(name = "execucaoMetaFisicaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoExecucaoMetaFisica", pattern = "/execucao/meta/fisica/novo/", viewId = "/faces/financeiro/ppa/execucaometafisica/edita.xhtml"),
    @URLMapping(id = "editarExecucaoMetaFisica", pattern = "/execucao/meta/fisica/editar/#{execucaoMetaFisicaControlador.id}/", viewId = "/faces/financeiro/ppa/execucaometafisica/edita.xhtml"),
    @URLMapping(id = "verExecucaoMetaFisica", pattern = "/execucao/meta/fisica/ver/#{execucaoMetaFisicaControlador.id}/", viewId = "/faces/financeiro/ppa/execucaometafisica/visualiza.xhtml"),
    @URLMapping(id = "listarExecucaoMetaFisica", pattern = "/execucao/meta/fisica/listar/", viewId = "/faces/financeiro/ppa/execucaometafisica/lista.xhtml")
})
public class ExecucaoMetaFisicaControlador extends PrettyControlador<ExecucaoMetaFisicaAcao> implements Serializable, CRUD {

    @EJB
    private ExecucaoMetaFisicaAcaoFacade execucaoMetaFisicaAcaoFacade;
    private ConverterAutoComplete converterLDO;
    private ConverterAutoComplete converterSubacao;

    public ExecucaoMetaFisicaControlador() {
        super(ExecucaoMetaFisicaAcao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return execucaoMetaFisicaAcaoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/execucao/meta/fisica/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoExecucaoMetaFisica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        parametrosIniciais();
    }

    @URLAction(mappingId = "editarExecucaoMetaFisica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verExecucaoMetaFisica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    private void parametrosIniciais() {
        selecionado = new ExecucaoMetaFisicaAcao();
        SistemaControlador sistemaControlador = (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
        selecionado.setDataRegistro(sistemaControlador.getDataOperacao());
    }

    @Override
    public void salvar() {
        try {
            if (Util.validaCampos(selecionado) && validaValorFisico()) {
                if (operacao.equals(Operacoes.NOVO)) {
                    execucaoMetaFisicaAcaoFacade.salvarNovo(selecionado);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Salvo com sucesso ", " A Execução da Meta Física: " + selecionado.getLdo() + " " + selecionado.getSubAcao() + " foi salvo com sucesso"));
                } else {
                    execucaoMetaFisicaAcaoFacade.salvar(selecionado);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Alterado com sucesso ", " A Execução da Meta Física: " + selecionado.getLdo() + " " + selecionado.getSubAcao() + " foi alterada com sucesso"));
                }
                redireciona();
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Exeção do sistema ", e.getMessage()));
        }
    }

    public Boolean validaValorFisico() {
        boolean valida = true;
        if (selecionado.getValorFisico().compareTo(new BigDecimal(BigInteger.ZERO)) <= 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Valor inválido: ", "O Valor Físico não pode ser negativo"));
            valida = false;
        }
        return valida;
    }

    public List<SelectItem> getListaLDO() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        for (LDO l : execucaoMetaFisicaAcaoFacade.getlDOFacade().lista()) {
            if (l.getAtoLegal() != null) {
                toReturn.add(new SelectItem(l, l.getExercicio() + " - " + l.getAtoLegal().getNome()));
            } else {
                toReturn.add(new SelectItem(l, l.getExercicio().toString()));
            }
        }
        return toReturn;
    }

    public List<SubAcaoPPA> completaSubacao(String parte) {
        return execucaoMetaFisicaAcaoFacade.listaFiltrandoSubacao(parte.trim());
    }

    public ConverterAutoComplete getConverterLDO() {
        if (converterLDO == null) {
            converterLDO = new ConverterAutoComplete(LDO.class, execucaoMetaFisicaAcaoFacade.getlDOFacade());
        }
        return converterLDO;
    }

    public ConverterAutoComplete getConverterSubacao() {
        if (converterSubacao == null) {
            converterSubacao = new ConverterAutoComplete(SubAcaoPPA.class, execucaoMetaFisicaAcaoFacade.getSubAcaoPPAFacade());
        }
        return converterSubacao;
    }
}
