/*
 * Codigo gerado automaticamente em Wed Mar 02 08:39:54 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.ConverterExercicio;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-exercicio", pattern = "/exercicio/novo/", viewId = "/faces/tributario/cadastromunicipal/exercicio/edita.xhtml"),
        @URLMapping(id = "editar-exercicio", pattern = "/exercicio/editar/#{exercicioControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/exercicio/edita.xhtml"),
        @URLMapping(id = "ver-exercicio", pattern = "/exercicio/ver/#{exercicioControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/exercicio/visualizar.xhtml"),
        @URLMapping(id = "listar-exercicio", pattern = "/exercicio/listar/", viewId = "/faces/tributario/cadastromunicipal/exercicio/lista.xhtml")
})
public class ExercicioControlador extends PrettyControlador<Exercicio> implements Serializable, CRUD {

    @EJB
    private ExercicioFacade exercicioFacade;
    private ConverterExercicio converterExercicio;

    public ExercicioControlador() {
        super(Exercicio.class);
    }

    @Override
    @URLAction(mappingId = "novo-exercicio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setDataRegistro(SistemaFacade.getDataCorrente());
        Integer exercioSugerido = exercicioFacade.sugereProximoExerciocio();
        selecionado.setAno(exercioSugerido);
    }

    @Override
    public void salvar() {
        if (Util.validaCampos(selecionado)) {
            super.salvar();
        } else {
            FacesUtil.addError("Operação não Realizada", "Ocorreu erro ao salvar o exercício!");
        }
    }

    @Override
    @URLAction(mappingId = "editar-exercicio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    @URLAction(mappingId = "ver-exercicio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    public ExercicioFacade getFacade() {
        return exercicioFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return exercicioFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/exercicio/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public ConverterExercicio getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterExercicio(exercicioFacade);
        }
        return converterExercicio;
    }

    public List<SelectItem> getExercicios() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (Exercicio exercicio : exercicioFacade.listaExerciciosAtual()) {
            retorno.add(new SelectItem(exercicio, exercicio.getAno().toString()));
        }
        return retorno;
    }
}
