package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.UnidadeGestora;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.TransparenciaFacade;
import br.com.webpublico.negocios.UnidadeGestoraFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mateus on 11/12/2014.
 */
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "relatorio-compras", pattern = "/relatorio/compras", viewId = "/faces/financeiro/relatorio/relatoriocompras.xhtml")
})
@ManagedBean
public class RelatorioComprasControlador extends AbstractReport implements Serializable {

    public static final String RELATORIO_COMPRAS_JASPER = "RelatorioCompras.jasper";
    @EJB
    private UnidadeGestoraFacade unidadeGestoraFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private TransparenciaFacade transparenciaFacade;
    private ConverterAutoComplete converterExercicio;
    private ConverterAutoComplete converterUnidadeGestora;
    private Exercicio exercicio;
    private UnidadeGestora unidadeGestora;
    private Mes mes;

    public RelatorioComprasControlador() {
    }

    public List<UnidadeGestora> completaUnidadesGestora(String parte) {
        if (exercicio != null) {
            return unidadeGestoraFacade.listaFiltrandoPorExercicio(exercicio, parte);
        }
        return null;
    }

    public List<SelectItem> getListaMeses() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (Mes m : Mes.values()) {
            toReturn.add(new SelectItem(m, m.getDescricao()));
        }
        return toReturn;
    }

    private Boolean validaFiltros() {
        if (this.exercicio == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, " Campo Obrigatório!", "O campo Exercício é obrigatório!"));
            return false;
        }
        return true;
    }

    @URLAction(mappingId = "relatorio-compras", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        exercicio = null;
        unidadeGestora = null;
    }

    public void gerarRelatorio() {
        try {
            if (validaFiltros()) {
                ByteArrayOutputStream byteArrayOutputStream = transparenciaFacade.gerarRelatorioCompras(mes, exercicio, unidadeGestora, RELATORIO_COMPRAS_JASPER);
                escreveNoResponse(RELATORIO_COMPRAS_JASPER, byteArrayOutputStream.toByteArray());
            }
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public ConverterAutoComplete getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterAutoComplete(Exercicio.class, exercicioFacade);
        }
        return converterExercicio;
    }

    public ConverterAutoComplete getConverterUnidadeGestora() {
        if (converterUnidadeGestora == null) {
            converterUnidadeGestora = new ConverterAutoComplete(UnidadeGestora.class, unidadeGestoraFacade);
        }
        return converterUnidadeGestora;
    }

    public List<Exercicio> completaExercicio(String parte) {
        return exercicioFacade.listaFiltrandoEspecial(parte.trim());
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public UnidadeGestora getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(UnidadeGestora unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }
}
