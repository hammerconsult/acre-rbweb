/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.PPA;
import br.com.webpublico.entidadesauxiliares.DataTablePesquisaGenerico;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.LOAFacade;
import br.com.webpublico.negocios.ProjetoAtividadeFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ComponentSystemEvent;
import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * @author Usuario
 */
@ManagedBean
@ViewScoped
public class ProvisaoPPADespesaPesquisaGenerico extends ComponentePesquisaGenerico implements Serializable {

    @EJB
    private ProjetoAtividadeFacade projetoAtividadeFacade;
    @EJB
    private LOAFacade loaFacade;

    @Override
    public void getCampos() {
        super.getCampos();

        SistemaControlador sistemaControlador = (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");

        ItemPesquisaGenerica itemPpa = new ItemPesquisaGenerica("obj.exercicio.ano", "Exercício", Integer.class, false);
        DataTablePesquisaGenerico dataTablePesquisaGenerico = new DataTablePesquisaGenerico();
        dataTablePesquisaGenerico.setItemPesquisaGenerica(itemPpa);
        dataTablePesquisaGenerico.setValuePesquisa(loaFacade.getExercicioCorrenteLoa().toString());


        super.getItens().add(itemPpa);
        super.getCamposPesquisa().add(dataTablePesquisaGenerico);

        ItemPesquisaGenerica codigo = new ItemPesquisaGenerica();
        codigo.setLabel("Código");
        codigo.setCondicao("obj.codigo");
        codigo.setTipoOrdenacao("asc");
        super.getCamposOrdenacao().add(codigo);

        ItemPesquisaGenerica codigoProjetoAtividade = new ItemPesquisaGenerica();
        codigoProjetoAtividade.setLabel("Projeto/Atividade.Código");
        codigoProjetoAtividade.setCondicao("obj.acaoPPA.codigo");
        codigoProjetoAtividade.setTipoOrdenacao("asc");
        super.getCamposOrdenacao().add(codigoProjetoAtividade);
    }


    @Override
    public String getComplementoQuery() {
        try {
            UnidadeOrganizacional unidadeOrganizacionalOrcamentoCorrente = super.getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente();
            String retorno = "";
            retorno += " inner join obj.acaoPPA.acaoPrincipal ap ";
            retorno += " inner join obj.acaoPPA.programa.ppa ppa ";
            retorno += " where obj.acaoPPA.responsavel.id = " + unidadeOrganizacionalOrcamentoCorrente.getId();
            retorno += " and " + montaCondicao() + montaOrdenacao();
            return retorno;
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), e.getMessage());
            return "";
        }
    }

    public PPA getPpaAtivo() {
        try {
            PPA ppa = projetoAtividadeFacade.getPpaFacade().ultimoPpaDoExercicio(super.getSistemaControlador().getExercicioCorrente());
            return ppa;
        } catch (Exception ex) {
            return null;
        }
    }
}
