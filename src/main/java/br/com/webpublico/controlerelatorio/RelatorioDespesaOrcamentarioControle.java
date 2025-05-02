/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacadeOLD;
import br.com.webpublico.negocios.UnidadeOrganizacionalFacade;
import br.com.webpublico.relatoriofacade.RelatorioReceitaOrcamentarioFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterGenerico;
import net.sf.jasperreports.engine.JRException;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author andregustavo
 */
@ManagedBean
public class RelatorioDespesaOrcamentarioControle extends AbstractReport implements Serializable {

    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private HierarquiaOrganizacionalFacadeOLD hierarquiaOrganizacionalFacade;
    @EJB
    private RelatorioReceitaOrcamentarioFacade relatorioReceitaOrcamentarioFacade;
    private Exercicio exercicioSelecionado;
    private UnidadeOrganizacional unidadeOrganizacionalSelecionada;
    private Converter converterExercicio;
    private Converter converterHierarquiaOrganizacional;
    private HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;

    public void limpaCampos() {
        exercicioSelecionado = null;
        unidadeOrganizacionalSelecionada = null;
        hierarquiaOrganizacionalSelecionada = null;
    }

    public Converter getConverterHierarquiaOrganizacional() {
        if (converterHierarquiaOrganizacional == null) {
            converterHierarquiaOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterHierarquiaOrganizacional;
    }

    public List<HierarquiaOrganizacional> completaHierarquia(String parte) {
        return hierarquiaOrganizacionalFacade.filtrandoHierarquiaHorganizacional(parte, sistemaControlador.getExercicioCorrente());
    }

    public Converter getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterGenerico(Exercicio.class, exercicioFacade);
        }
        return converterExercicio;
    }

    public List<SelectItem> getListaExercicios() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (Exercicio obj : exercicioFacade.lista()) {
            toReturn.add(new SelectItem(obj, obj.getAno().toString()));
        }
        return toReturn;
    }

    public Exercicio getExercicioSelecionado() {
        return exercicioSelecionado;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalSelecionada() {
        return hierarquiaOrganizacionalSelecionada;
    }

    public void setHierarquiaOrganizacionalSelecionada(HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada) {
        this.hierarquiaOrganizacionalSelecionada = hierarquiaOrganizacionalSelecionada;
    }

    public void setExercicioSelecionado(Exercicio exercicioSelecionado) {
        this.exercicioSelecionado = exercicioSelecionado;
    }

    public UnidadeOrganizacional getUnidadeOrganizacionalSelecionada() {
        return unidadeOrganizacionalSelecionada;
    }

    public void setUnidadeOrganizacionalSelecionada(UnidadeOrganizacional unidadeOrganizacionalSelecionada) {
        this.unidadeOrganizacionalSelecionada = unidadeOrganizacionalSelecionada;
    }

    public Boolean validaExercicio() {
        if (exercicioSelecionado != null) {
            return true;
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Exercicio não pode ser nulo", ""));
            return false;
        }
    }

    public Boolean validaHierarquia() {
        if (hierarquiaOrganizacionalSelecionada != null) {
            return true;
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "A Hierarquia deve ser selecionada", ""));
            return false;
        }
    }

    public void validaCampos() throws JRException, IOException {
        if (validaExercicio() && validaHierarquia()) {
            gerarRelatorioOrcamentario();
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "todos os campos devem ter valores válidos", ""));
        }
    }

    public void gerarRelatorioOrcamentario() throws JRException, IOException {
        //System.out.println("Exercicio--------- " + exercicioSelecionado.getAno());


        if (hierarquiaOrganizacionalSelecionada.getSubordinada() != null) {
            //System.out.println("Unidade Org------- " + hierarquiaOrganizacionalSelecionada.getSubordinada().getId());
            String arquivoJasper = "RelatorioDespesaOrcamentario.jasper";
            FacesContext facesContext = FacesContext.getCurrentInstance();
            facesContext.responseComplete();
            ServletContext scontext = (ServletContext) facesContext.getExternalContext().getContext();
            String subReport = scontext.getRealPath("/WEB-INF");
            subReport += "/report/";
            HashMap parameters = new HashMap();
            //System.out.println("USUARIO LOGADO ----- " + usuarioLogado().getPessoaFisica().getNome());
            //System.out.println("UNIDADE_NOME-------- " + hierarquiaOrganizacionalSelecionada.getSubordinada().getDescricao());
            //System.out.println("Sun_RELATORIO----- " + subReport);

            parameters.put("EXERCICIO", exercicioSelecionado.getAno());
            parameters.put("HI_SELECIONADA", hierarquiaOrganizacionalSelecionada.getSubordinada().getId());
            parameters.put("SUB", subReport);
            parameters.put("UNIDADE_NOME", hierarquiaOrganizacionalSelecionada.getSubordinada().getDescricao());
            parameters.put("IMAGEM", getCaminhoImagem());
            parameters.put("MUNICIPIO", "Prefeitura Municipal de Rio Branco - AC");
            parameters.put("USER", usuarioLogado().getPessoaFisica().getNome());
            //parameters.put("UNIDADE_NOME", hierarquiaOrganizacionalSelecionada.getSubordinada().getDescricao());
            // if (relatorioReceitaOrcamentarioFacade.consultaFiltros(hierarquiaOrganizacionalSelecionada.getSubordinada().getId(), exercicioSelecionado.getAno())) {
            gerarRelatorio(arquivoJasper, parameters);
            // } else {
            //    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "A Consulta não retornou um resultado", ""));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "A hierarquia selecionada não possui unidade subordinada", ""));
        }
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}
