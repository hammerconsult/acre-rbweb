/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.PPA;
import br.com.webpublico.entidadesauxiliares.DataTablePesquisaGenerico;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.PPAFacade;
import br.com.webpublico.util.Persistencia;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Pesquisavel;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author reidocrime
 */
@ManagedBean
@ViewScoped
public class ProgramaPpaPesquisaGenerico extends ComponentePesquisaGenerico {

    @EJB
    private PPAFacade ppaFacade;

    @Override
    public void getCampos() {

        SistemaControlador sistemaControlador = (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");

        itens.add(new ItemPesquisaGenerica("", "", null));
        itensOrdenacao.add(new ItemPesquisaGenerica("", "", null));

        ItemPesquisaGenerica itemPpa = new ItemPesquisaGenerica("ppa", "PPA", PPA.class, true);
        DataTablePesquisaGenerico dataTablePesquisaGenerico = new DataTablePesquisaGenerico();
        dataTablePesquisaGenerico.setItemPesquisaGenerica(itemPpa);

        ItemPesquisaGenerica itemProgramaExercicio = new ItemPesquisaGenerica("obj.exercicio.ano", "Exercício", Integer.class);
        DataTablePesquisaGenerico dataTablePesquisaG = new DataTablePesquisaGenerico();
        dataTablePesquisaG.setItemPesquisaGenerica(itemProgramaExercicio);
        dataTablePesquisaG.setPodeRemover(false);

        try {
            PPA ppa = ppaFacade.ultimoPpaDoExercicio(sistemaControlador.getExercicioCorrente());
            Exercicio exercicio = sistemaControlador.getExercicioCorrente();
            if (exercicio != null){
                dataTablePesquisaG.setValuePesquisa(exercicio.getAno().toString());
            }
            if (ppa != null) {
                dataTablePesquisaGenerico.setValuePesquisa(ppa.getId().toString());
            }
        } catch (ExcecaoNegocioGenerica e) {

        }

        ItemPesquisaGenerica acaoComSemPPA = new ItemPesquisaGenerica("PPA", "Possui PPA?", Boolean.class, false, false, "Não", "Sim");
        DataTablePesquisaGenerico dataTablePesquisaGenerico2 = new DataTablePesquisaGenerico();
        dataTablePesquisaGenerico2.setItemPesquisaGenerica(acaoComSemPPA);
        dataTablePesquisaGenerico2.setValuePesquisa("true");


        super.getItens().add(itemPpa);
        super.getItens().add(itemProgramaExercicio);
        super.getItens().add(acaoComSemPPA);

        super.getCamposPesquisa().add(dataTablePesquisaGenerico);
        super.getCamposPesquisa().add(dataTablePesquisaG);


        if (classe != null) {
            for (Field field : Persistencia.getAtributos(classe)) {
                if (field.isAnnotationPresent(Pesquisavel.class)) {
                    super.criarItemPesquisaGenerico(field);
                }
            }
        }
    }

    @Override
    public String montaCondicao() {
        String condicao = super.montaCondicao();
        if (condicao.contains("obj.ppa = true")) {
            condicao = condicao.replace("obj.ppa = true", "obj.ppa is not null");
        }
        if (condicao.contains("obj.ppa = false")) {
            condicao = condicao.replace("obj.ppa = false", "obj.ppa is null");
        }
        return condicao;
    }

    private void ordenarPPA(List<PPA> ppaS) {
        Collections.sort(ppaS, new Comparator<PPA>() {
            @Override
            public int compare(PPA o1, PPA o2) {
                Date i1 = o1.getDataRegistro();
                Date i2 = o2.getDataRegistro();
                return i1.compareTo(i2);
            }
        });
    }

    @Override
    public List<SelectItem> recuperaValuesEnum(ItemPesquisaGenerica item) {
        String nomeDaClasse = item.getTipo().toString();
        nomeDaClasse = nomeDaClasse.replace("class ", "");
        Class<?> classe = null;
        try {
            classe = Class.forName(nomeDaClasse);
        } catch (ClassNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro !", "Classe não encontrada : " + nomeDaClasse));
        }
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        if (classe.equals(PPA.class)) {
            List<PPA> ppas = ppaFacade.lista();
            ordenarPPA(ppas);
            for (PPA ppa : ppas) {
                retorno.add(new SelectItem(ppa.getId(), ppa.getDescricao() + " - " + ppa.getVersao() +
                    (ppa.getAtoLegal() != null ? " - " + ppa.getAtoLegal().getTipoNumeroAnoPPA() : " - PLC Nº XX/XXXX")));
            }
            return retorno;
        } else {
            return super.recuperaValuesEnum(item);
        }
    }
}
