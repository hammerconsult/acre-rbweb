package br.com.webpublico.controle;

import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;
import br.com.webpublico.enums.Mes;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Claudio
 * Date: 06/12/13
 * Time: 14:15
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@SessionScoped
public class CompetenciaFPPesquisaGenericaControlador extends ComponentePesquisaGenerico {

    public CompetenciaFPPesquisaGenericaControlador() {
    }


    @Override
    public void getCampos() {
        super.getCampos();
        ItemPesquisaGenerica item = new ItemPesquisaGenerica("obj.mes", "Mês", Mes.class, Boolean.TRUE, Boolean.TRUE);

        super.getItens().add(item);
        super.getItensOrdenacao().add(item);
    }

    @Override
    protected String ordenacaoPadrao() {
        return " order by obj.exercicio.ano desc, obj.mes desc";
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
        if (item.getCondicao().equals("obj.mes")) {
            Mes[] meses = Mes.values();
            for (Mes mes : meses) {
                retorno.add(new SelectItem(mes.getNumeroMes()-1, mes.getDescricao()));
            }
            return retorno;
        } else {
            return super.recuperaValuesEnum(item);
        }
    }
}
