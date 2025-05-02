/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.PPA;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.PPAFacade;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Usuario
 */
@ManagedBean
@SessionScoped
public class RelatorioRenunciaReceitaControle extends AbstractReport implements Serializable {

    @EJB
    ExercicioFacade exercicioFacade;
    Exercicio exercicioSelecionado;
    @EJB
    PPAFacade ppaFacade;
    PPA ppaSelecionado;

    public PPA getPpaSelecionado() {
        return ppaSelecionado;
    }

    public void setPpaSelecionado(PPA ppaSelecionado) {
        this.ppaSelecionado = ppaSelecionado;
    }

    public Exercicio getExercicioSelecionado() {
        return exercicioSelecionado;
    }

    public void setExercicioSelecionado(Exercicio exercicioSelecionado) {
        this.exercicioSelecionado = exercicioSelecionado;
    }

    public List<SelectItem> getListaPPAs() {
        List<SelectItem> toreturn = new ArrayList<SelectItem>();
        toreturn.add(new SelectItem(null, ""));
        if (exercicioSelecionado != null) {
            //System.out.println(exercicioSelecionado);
            for (PPA object : ppaFacade.listaTodosPpaExericicioCombo(exercicioSelecionado)) {
                toreturn.add(new SelectItem(object, object.getDescricao() + " --- " + object.getVersao() + " --- " + object.getAprovacao()));
            }
        } else {
            toreturn = null;
        }
        return toreturn;
    }

    public void limpaCampos() {
        ppaSelecionado = null;
        exercicioSelecionado = null;
    }

//    public void geraRelatorioRenuncia() throws JRException, IOException {
//
//        String arquivoJasper = "RelatorioRenunciaReceita.jasper";
//
//
//        HashMap parameters = new HashMap();
//        //parameters.put("EXERCICIO", exercicioSelecionado.getId());
//
//
//        FacesContext facesContext = FacesContext.getCurrentInstance();
//        facesContext.responseComplete();
//        ServletContext scontext = (ServletContext) facesContext.getExternalContext().getContext();
//
//        String subReport = scontext.getRealPath("/WEB-INF/report/Cabecalho_subreport2.jasper");
//
//
//        //System.out.println("PPA SELECIONADO ---- " + ppaSelecionado.getId());
//        //System.out.println("Usuario Logado" + usuarioLogado().getPessoaFisica().getNome());
//        //System.out.println("Unidade Organizacional" + getUnidadeOrganizacionalRaiz().getId());
//        //System.out.println("URL LOGO --- " + subReport);
//
//        parameters.put("URL_SUB", subReport);
//        parameters.put("URL_LOGO", retornaUrl(getUnidadeOrganizacionalRaiz().getEntidade().getArquivo().getId()));
//        parameters.put("PPA_ID", ppaSelecionado.getId());
//        parameters.put("USUARIO", usuarioLogado().getPessoaFisica().getNome());
//        parameters.put("UNIDADE_ORG", getUnidadeOrganizacionalRaiz().getId());
//
//
//        gerarRelatorio(arquivoJasper, parameters);
//
//
//
//    }
}
