package br.com.webpublico.controle;

import br.com.webpublico.entidades.EnquadramentoTributoExerc;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EnquadramentoTributoExercicioFacade;
import br.com.webpublico.util.ConverterExercicio;
import br.com.webpublico.util.FacesUtil;
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
import java.util.List;

/**
 * @author daniel
 */
@ManagedBean(name = "enquadramentoTributoExercicioControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoEnquadramentoExercicio", pattern = "/enquadamento-de-exercicio-de-referencia-de-tributos-com-conta-de-receita/novo/", viewId = "/faces/tributario/arrecadacao/enquadramento/edita.xhtml"),
        @URLMapping(id = "editarEnquadramentoExercicio", pattern = "/enquadamento-de-exercicio-de-referencia-de-tributos-com-conta-de-receita/editar/#{enquadramentoTributoExercicioControlador.id}/", viewId = "/faces/tributario/arrecadacao/enquadramento/edita.xhtml"),
        @URLMapping(id = "listarEnquadramentoExercicio", pattern = "/enquadamento-de-exercicio-de-referencia-de-tributos-com-conta-de-receita/listar/", viewId = "/faces/tributario/arrecadacao/enquadramento/lista.xhtml"),
        @URLMapping(id = "verEnquadramentoExercicio", pattern = "/enquadamento-de-exercicio-de-referencia-de-tributos-com-conta-de-receita/ver/#{enquadramentoTributoExercicioControlador.id}/", viewId = "/faces/tributario/arrecadacao/enquadramento/visualizar.xhtml")
})
public class EnquadramentoTributoExercicioControlador extends PrettyControlador<EnquadramentoTributoExerc> implements Serializable, CRUD {

    private ConverterExercicio converterExercicio;
    @EJB
    private EnquadramentoTributoExercicioFacade enquadramentoTributoExercicioFacade;

    public EnquadramentoTributoExercicioControlador() {
        super(EnquadramentoTributoExerc.class);
    }

    public ConverterExercicio getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterExercicio(enquadramentoTributoExercicioFacade.getExercicioFacade());
        }
        return converterExercicio;
    }

    public List<SelectItem> getExercicios() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        retorno.add(new SelectItem(null, ""));
        for (Exercicio ex : enquadramentoTributoExercicioFacade.getExercicioFacade().lista()) {
            retorno.add(new SelectItem(ex, ex.getAno().toString()));
        }
        return retorno;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/enquadamento-de-exercicio-de-referencia-de-tributos-com-conta-de-receita/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return enquadramentoTributoExercicioFacade;
    }

    @URLAction(mappingId = "novoEnquadramentoExercicio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editarEnquadramentoExercicio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verEnquadramentoExercicio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            super.salvar();
        }
    }

    public boolean validaCampos() {
        boolean retorno = true;
        if (selecionado.getCodigo() == null) {
            retorno = false;
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Informe o Código do Enquadramento.");
        } else if (selecionado.getCodigo().intValue() <= 0) {
            retorno = false;
            FacesUtil.addError(SummaryMessages.ATENCAO.getDescricao(), "O Código do Enquadramento deve ser Maior que Zero.");
        }
        if (selecionado.getExercicioVigente() == null || selecionado.getExercicioVigente().getId() == null) {
            retorno = false;
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Informe o Exercício Vigente do Enquadramento.");
        }
        if (selecionado.getDescricao() == null || "".equals(selecionado.getDescricao().trim())) {
            retorno = false;
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Informe a Descrição do Enquadramento.");
        }
        if (selecionado.getExercicioDividaInicial() == null || selecionado.getExercicioDividaInicial().getId() == null) {
            retorno = false;
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Informe o Exercício Inicial da Dívida.");
        }
        if (selecionado.getExercicioDividaFinal() == null || selecionado.getExercicioDividaFinal().getId() == null) {
            retorno = false;
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Informe o Exercício Final da Dívida.");
        }
        if (selecionado.getExercicioDividaFinal() != null && selecionado.getExercicioDividaFinal().getId() != null && selecionado.getExercicioDividaFinal().getAno().intValue() > selecionado.getExercicioDividaInicial().getAno().intValue()) {
            retorno = false;
            FacesUtil.addError(SummaryMessages.ATENCAO.getDescricao(), "O Exercício Final da Dívida deve ser Menor que o Exercício Inicial.");
        }
        if (!enquadramentoTributoExercicioFacade.isDataInicialValida(selecionado)) {
            retorno = false;
            FacesUtil.addError(SummaryMessages.ATENCAO.getDescricao(), "O Exercício Inicial da Dívida não pode pertencer ao período de outro Enquadramento para o mesmo Exercício Vigente.");
        }
        if (!enquadramentoTributoExercicioFacade.isDataFinalValida(selecionado)) {
            retorno = false;
            FacesUtil.addError(SummaryMessages.ATENCAO.getDescricao(), "O Exercício Final da Dívida não pode pertencer ao período de outro Enquadramento para o mesmo Exercício Vigente.");
        }
        return retorno;
    }
}
