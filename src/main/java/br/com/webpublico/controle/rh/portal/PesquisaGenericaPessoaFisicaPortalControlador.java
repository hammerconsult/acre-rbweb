package br.com.webpublico.controle.rh.portal;

import br.com.webpublico.controle.ComponentePesquisaGenerico;
import br.com.webpublico.entidades.MatriculaFP;
import br.com.webpublico.entidadesauxiliares.DataTablePesquisaGenerico;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;
import br.com.webpublico.enums.rh.SituacaoPessoaPortal;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by william on 06/11/17.
 */
@ManagedBean
@ViewScoped
public class PesquisaGenericaPessoaFisicaPortalControlador extends ComponentePesquisaGenerico implements Serializable {

    @Override
    public void getCampos() {
        super.getCampos();
        itens.add(new ItemPesquisaGenerica("matricula.matricula", "Matrícula", MatriculaFP.class, false, true));

        itensOrdenacao.add(new ItemPesquisaGenerica("matricula.matricula", "Matrícula", MatriculaFP.class, false, true));

        ItemPesquisaGenerica ipg = new ItemPesquisaGenerica("status", "Situação", SituacaoPessoaPortal.class, true, false);
        itens.add(ipg);
        DataTablePesquisaGenerico item = new DataTablePesquisaGenerico();
        item.setItemPesquisaGenerica(ipg);
        item.setValuePesquisa(SituacaoPessoaPortal.AGUARDANDO_LIBERACAO.name());
        camposPesquisa.add(item);
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
        if (classe!= null && classe.equals(SituacaoPessoaPortal.class)) {
            retorno.add(new SelectItem(SituacaoPessoaPortal.AGUARDANDO_LIBERACAO, SituacaoPessoaPortal.AGUARDANDO_LIBERACAO.getDescricao()));
            retorno.add(new SelectItem(SituacaoPessoaPortal.LIBERADO, SituacaoPessoaPortal.LIBERADO.getDescricao()));
            return retorno;
        } else {
            return super.recuperaValuesEnum(item);
        }
    }

    @Override
    public String getHqlConsulta() {
        return "select distinct obj from " + classe.getSimpleName() + " obj, MatriculaFP matricula ";
    }

    @Override
    public String getHqlContador() {
        return "select count(distinct obj.id) from " + classe.getSimpleName() + " obj, MatriculaFP matricula ";
    }

    @Override
    public String montaCondicao() {
        return super.montaCondicao() + " and matricula.pessoa = obj.pessoaFisica";
    }

}
