package br.com.webpublico.controle;

import br.com.webpublico.entidades.CompetenciaFP;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.FolhaDePagamento;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.StatusCompetencia;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CompetenciaFPFacade;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.FolhaDePagamentoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.DateTime;
import org.joda.time.YearMonth;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "competenciaFPControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaCompetencia", pattern = "/competenciafp/novo/", viewId = "/faces/rh/administracaodepagamento/competenciafp/edita.xhtml"),
    @URLMapping(id = "editarCompetencia", pattern = "/competenciafp/editar/#{competenciaFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/competenciafp/edita.xhtml"),
    @URLMapping(id = "listarCompetencia", pattern = "/competenciafp/listar/", viewId = "/faces/rh/administracaodepagamento/competenciafp/lista.xhtml"),
    @URLMapping(id = "verCompetencia", pattern = "/competenciafp/ver/#{competenciaFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/competenciafp/visualizar.xhtml")
})
public class CompetenciaFPControlador extends PrettyControlador<CompetenciaFP> implements Serializable, CRUD {

    @EJB
    private CompetenciaFPFacade competenciaFPFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;

    public CompetenciaFPControlador() {
        super(CompetenciaFP.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return competenciaFPFacade;
    }

    public List<SelectItem> getExercicio() {
        YearMonth mesAno = new YearMonth();
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (Exercicio object : exercicioFacade.buscarPorIntervaloDeAno(mesAno.minusYears(50).getYear(), mesAno.plusYears(1).getYear())) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public ConverterAutoComplete getConverterExercicio() {
        return new ConverterAutoComplete(Exercicio.class, exercicioFacade);
    }

    public List<SelectItem> getMes() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (Mes object : Mes.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getStatusCompetencias() {
        List<SelectItem> toReturn = Lists.newArrayList();
        if (isOperacaoNovo()) {
            toReturn.add(new SelectItem(StatusCompetencia.ABERTA, "Gerado automaticamente pelo sistema"));
            return toReturn;
        }
        for (StatusCompetencia object : StatusCompetencia.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoFolhaDePagamentos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoFolhaDePagamento object : TipoFolhaDePagamento.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    @Override
    public void excluir() {
        List<FolhaDePagamento> listaFolhas = new ArrayList<>();
        listaFolhas = folhaDePagamentoFacade.recuperaFolhaPelaCompetencia(selecionado);
        if (listaFolhas.isEmpty()) {
            super.excluir();
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Registro não pode ser excluído. ", "O registro está associado a Folha de Pagamento"));
        }
    }

    @URLAction(mappingId = "novaCompetencia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setStatusCompetencia(StatusCompetencia.ABERTA);
        selecionado.setExercicio(exercicioFacade.getExercicioPorAno(DateTime.now().getYear()));
    }

    @URLAction(mappingId = "editarCompetencia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verCompetencia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }


    private Boolean validarQuandoEstiverNovo() {
        CompetenciaFP competenciaExistente = competenciaFPFacade.recuperarCompetenciaPorTipoMesAno(selecionado.getMes(), selecionado.getExercicio().getAno(), selecionado.getTipoFolhaDePagamento());
        if (competenciaExistente != null && !TipoFolhaDePagamento.RESCISAO.equals(competenciaExistente.getTipoFolhaDePagamento())) {
            FacesUtil.addOperacaoNaoPermitida("Já existe Competência com o Tipo de Folha de Pagamento " + selecionado.getTipoFolhaDePagamento().getDescricao() + " cadastrada para o mês " + selecionado.getMes() + " de " + selecionado.getExercicio().getAno());
            return false;
        }
        List<CompetenciaFP> competenciaFPs = competenciaFPFacade.findCompetenciasAbertasPorTipo(selecionado.getTipoFolhaDePagamento());
        if (competenciaFPs != null && !competenciaFPs.isEmpty() && !TipoFolhaDePagamento.RESCISAO.equals(selecionado.getTipoFolhaDePagamento())) {
            FacesUtil.addOperacaoNaoPermitida("Não é permitida mais de uma competência aberta no sistema.");
            FacesUtil.addOperacaoNaoPermitida("Já existe competência aberta " + competenciaFPs.get(0).toString());
            return false;
        }
        return true;
    }

    private Boolean validarQuandoEstiverEditando() {
        List<CompetenciaFP> competenciaFPs = competenciaFPFacade.findCompetenciasAbertasPorTipo(selecionado.getTipoFolhaDePagamento());
        if (competenciaFPs != null && !competenciaFPs.isEmpty()) {
            CompetenciaFP competenciaBanco = competenciaFPs.get(0);
            if (!competenciaBanco.getId().equals(selecionado.getId()) && competenciaBanco.getStatusCompetencia().equals(selecionado.getStatusCompetencia()) && !TipoFolhaDePagamento.RESCISAO.equals(competenciaBanco.getTipoFolhaDePagamento())) {
                FacesUtil.addError("Atenção!", "Já existe competência aberta " + competenciaFPs.get(0).toString());
                return false;
            }
        }
        return true;
    }

    public Boolean validaCampos() {
        if (isOperacaoNovo()) return validarQuandoEstiverNovo();
        if (isOperacaoEditar()) return validarQuandoEstiverEditando();
        return false;
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            super.salvar();
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/competenciafp/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
