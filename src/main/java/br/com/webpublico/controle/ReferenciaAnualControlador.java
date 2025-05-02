/*
 * Codigo gerado automaticamente em Mon Apr 09 15:09:47 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ReferenciaAnualFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.MoneyConverter;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "referenciaAnualControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-referencia-anual", pattern = "/referencia-anual/novo/", viewId = "/faces/financeiro/orcamentario/referenciaanual/edita.xhtml"),
        @URLMapping(id = "editar-referencia-anual", pattern = "/referencia-anual/editar/#{referenciaAnualControlador.id}/", viewId = "/faces/financeiro/orcamentario/referenciaanual/edita.xhtml"),
        @URLMapping(id = "ver-referencia-anual", pattern = "/referencia-anual/ver/#{referenciaAnualControlador.id}/", viewId = "/faces/financeiro/orcamentario/referenciaanual/visualizar.xhtml"),
        @URLMapping(id = "listar-referencia-anual", pattern = "/referencia-anual/listar/", viewId = "/faces/financeiro/orcamentario/referenciaanual/lista.xhtml")
})
public class ReferenciaAnualControlador extends PrettyControlador<ReferenciaAnual> implements Serializable, CRUD {

    @EJB
    private ReferenciaAnualFacade referenciaAnualFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private MoneyConverter moneyConverter;

    public ReferenciaAnualControlador() {
        super(ReferenciaAnual.class);
    }

    public ReferenciaAnualFacade getFacade() {
        return referenciaAnualFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return referenciaAnualFacade;
    }

    @URLAction(mappingId = "novo-referencia-anual", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setExercicio(sistemaControlador.getExercicioCorrente());
        selecionado.setDataLDO(sistemaControlador.getDataOperacao());
        selecionado.setDataLOA(sistemaControlador.getDataOperacao());
        selecionado.setDataPPA(sistemaControlador.getDataOperacao());
    }

    @URLAction(mappingId = "ver-referencia-anual", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-referencia-anual", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/referencia-anual/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void salvar() {
        try {
            if (validaDuplicarExercicio()) {
                if (operacao.equals(Operacoes.NOVO)) {
                    referenciaAnualFacade.salvarNovo(selecionado);
                } else {
                    referenciaAnualFacade.salvar(selecionado);
                }
                FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), " Registro salvo com sucesso.");
                redireciona();

            }
        } catch (Exception ex) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), ex.getMessage());
        }
    }

    private boolean validaDuplicarExercicio() {
        if (referenciaAnualFacade.validaReferenciaAnualMesmoExercicio(selecionado)) {
            return true;
        }
        FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " Já existe uma Referência Anual para o exercício de " + selecionado.getExercicio().getAno() + ".");
        return false;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}
