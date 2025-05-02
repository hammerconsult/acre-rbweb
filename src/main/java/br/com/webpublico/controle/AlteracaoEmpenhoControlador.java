package br.com.webpublico.controle;


import br.com.webpublico.entidades.Empenho;
import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.enums.SubTipoDespesa;
import br.com.webpublico.enums.TipoContaDespesa;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.EmpenhoFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "alterar-empenho", pattern = "/alterar-empenho/", viewId = "/faces/financeiro/orcamentario/empenho/alterar-empenho.xhtml")
})
public class AlteracaoEmpenhoControlador implements Serializable {

    @EJB
    private EmpenhoFacade empenhoFacade;
    private Empenho selecionado;
    private CategoriaOrcamentaria categoriaOrcamentaria;

    public AlteracaoEmpenhoControlador() {
    }

    @URLAction(mappingId = "alterar-empenho", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        selecionado = null;
        categoriaOrcamentaria = CategoriaOrcamentaria.NORMAL;
    }

    public void salvar() {
        try {
            validarCampos();
            empenhoFacade.mergiarEmpenho(selecionado);
            FacesUtil.addOperacaoRealizada("Empenho " + selecionado.toString() + " foi alterado com sucesso.");
            redirecionar();
        } catch (ValidacaoException ve) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    private void redirecionar() {
        FacesUtil.redirecionamentoInterno("/alterar-empenho/");
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Empenho deve ser informado.");
        }
        if (TipoContaDespesa.retornaTipoContaDividaPublica().contains(selecionado.getTipoContaDespesa()) && selecionado.getSubTipoDespesa() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Subtipo de Despesa deve ser informado.");
        }
        ve.lancarException();
        Util.validarCampos(selecionado);
    }

    public List<Empenho> completarEmpenho(String parte) {
        if (isCategoriaNormal()) {
            return empenhoFacade.buscarEmpenhosPorUnidadeEUsuarioVinculados(
                parte.trim(),
                empenhoFacade.getSistemaFacade().getUsuarioCorrente(),
                empenhoFacade.getSistemaFacade().getExercicioCorrente(),
                empenhoFacade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente());
        } else {
            return empenhoFacade.buscarEmpenhoPorNumeroPessoaAndCategoriaResto(
                parte.trim(),
                empenhoFacade.getSistemaFacade().getExercicioCorrente().getAno(),
                empenhoFacade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente());
        }
    }

    public void cancelar() {
        selecionado = null;
    }

    public String getTituloEmpenho() {
        if (isCategoriaNormal()) {
            return "Empenho";
        } else {
            return "Empenho de Resto a Pagar";
        }
    }

    public boolean isCategoriaNormal() {
        return CategoriaOrcamentaria.NORMAL.equals(categoriaOrcamentaria);
    }

    public Boolean renderizarSubTipoDespesa() {
        return selecionado.getTipoContaDespesa() != null && TipoContaDespesa.retornaTipoContaDividaPublica().contains(selecionado.getTipoContaDespesa());
    }

    public boolean hasEmpenhoSelecionado() {
        return selecionado != null;
    }

    public List<CategoriaOrcamentaria> getCategoriasOrcamentarias() {
        return Arrays.asList(CategoriaOrcamentaria.values());
    }

    public List<TipoContaDespesa> getTiposContaDeDespesa() {
        List<TipoContaDespesa> toReturn = Lists.newArrayList();
        for (TipoContaDespesa tp : TipoContaDespesa.values()) {
            if (!tp.isNaoAplicavel()) {
                toReturn.add(tp);
            }
        }
        return toReturn;
    }


    public void definirSubTipoDespesaPorTipoDespesa() {
        TipoContaDespesa tipo = selecionado.getTipoContaDespesa();
        if (!tipo.isPessoaEncargos() && !tipo.isDividaPublica() && !tipo.isPrecatorio()) {
            selecionado.setSubTipoDespesa(SubTipoDespesa.NAO_APLICAVEL);
        } else {
            selecionado.setSubTipoDespesa(null);
        }
    }

    public List<SubTipoDespesa> getSubTiposDeDespesa() {
        List<SubTipoDespesa> toReturn = Lists.newArrayList();
        if (selecionado != null) {
            TipoContaDespesa tipo = selecionado.getTipoContaDespesa();
            if (tipo.isPessoaEncargos()) {
                toReturn.add(SubTipoDespesa.RGPS);
                toReturn.add(SubTipoDespesa.RPPS);
            } else if (tipo.isDividaPublica() || tipo.isPrecatorio()) {
                toReturn.add(SubTipoDespesa.JUROS);
                toReturn.add(SubTipoDespesa.OUTROS_ENCARGOS);
                toReturn.add(SubTipoDespesa.VALOR_PRINCIPAL);
            } else {
                toReturn.add(SubTipoDespesa.NAO_APLICAVEL);
            }
        }
        return toReturn;
    }

    public Empenho getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Empenho selecionado) {
        this.selecionado = selecionado;
    }

    public CategoriaOrcamentaria getCategoriaOrcamentaria() {
        return categoriaOrcamentaria;
    }

    public void setCategoriaOrcamentaria(CategoriaOrcamentaria categoriaOrcamentaria) {
        this.categoriaOrcamentaria = categoriaOrcamentaria;
    }
}
