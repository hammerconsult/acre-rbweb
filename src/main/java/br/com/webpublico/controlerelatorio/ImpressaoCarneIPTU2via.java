/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.CadastroImobiliario;
import br.com.webpublico.entidades.Divida;
import br.com.webpublico.negocios.CadastroImobiliarioFacade;
import br.com.webpublico.negocios.DividaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Limpavel;
import net.sf.jasperreports.engine.JRException;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * @author Heinz
 */
@ManagedBean
@SessionScoped
public class ImpressaoCarneIPTU2via extends AbstractReport {
    @Limpavel(Limpavel.NULO)
    private Divida divida;
    @EJB
    private DividaFacade dividaFacade;
    private Converter converterDivida;
    @Limpavel(Limpavel.NULO)
    private Long exercicio;
    //    @EJB
//    private ExercicioFacade exercicioFacade;
//    private Converter converterExercicio;
    @Limpavel(Limpavel.NULO)
    private CadastroImobiliario cadastroImobiliario;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    private Converter converterCadastroImobiliario;

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public Long getExercicio() {
        return exercicio;
    }

    public void setExercicio(Long exercicio) {
        this.exercicio = exercicio;
    }

    public CadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
    }

    public Converter getConverterDivida() {
        if (converterDivida == null) {
            converterDivida = new ConverterAutoComplete(Divida.class, dividaFacade);
        }
        return converterDivida;
    }

//    public Converter getConverterExercicio() {
//        if (converterExercicio == null) {
//            converterExercicio = new ConverterAutoComplete(Exercicio.class, exercicioFacade);
//        }
//        return converterExercicio;
//    }

    public Converter getConverterCadastroImobiliario() {
        if (converterCadastroImobiliario == null) {
            converterCadastroImobiliario = new ConverterAutoComplete(CadastroImobiliario.class, cadastroImobiliarioFacade);
        }
        return converterCadastroImobiliario;
    }

    public List<Divida> completaDivida(String parte) {
        return dividaFacade.listaFiltrando(parte.trim(), "descricao");
    }

//    public List<Exercicio> completaExercicio(String parte) {
//        return exercicioFacade.listaFiltrandoEspecial(parte.trim());
//    }

    public List<CadastroImobiliario> completaCadastroImobiliario(String parte) {
        List<CadastroImobiliario> lista = cadastroImobiliarioFacade.listarNaoDevedores(parte.trim());
        CadastroImobiliario erro = new CadastroImobiliario();
        if (lista.isEmpty() && parte.trim().equals("")) {
            erro.setCodigo("Não existem cadastros com débitos");
        } else if (lista.isEmpty()) {
            erro.setCodigo("Não foi encontrado o cadastro imobiliário desejado");
        } else {
            return lista;
        }
        lista.add(erro);
        return lista;
    }

    public void imprimeCarne() throws IOException, JRException {
        String semDados = "Nenhum carnê foi encontrado";
        if (validaCampos()) {
            String subReport = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/WEB-INF/report") + "/";
            String arquivoJasper = "Carne_IPTU2via.jasper";
            HashMap parametros = new HashMap();
            parametros.put("DIVIDA_ID", divida.getId());
            parametros.put("EXERCICIO", exercicio);
            parametros.put("CADASTROIMOBILIARIO_ID", cadastroImobiliario.getId().longValue());
            parametros.put("SEMDADOS", semDados);
            parametros.put("SUBREPORT_DIR", subReport);
            gerarRelatorio(arquivoJasper, parametros);
        }
    }

    private boolean validaCampos() {
        if (divida == null || divida.getId() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "impossível continuar", "Dívida não informada"));
            return false;
        }
        if (exercicio == null || exercicio.toString().length() != 4) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "impossível continuar", "Exercício não informado"));
            return false;
        }

        if (cadastroImobiliario == null || cadastroImobiliario.getId() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Impossível continuar", "Cadastro imobiliário não informado"));
            return false;
        }
        return true;

    }

    public void limparCampos() {
        Util.limparCampos(this);
    }
}
