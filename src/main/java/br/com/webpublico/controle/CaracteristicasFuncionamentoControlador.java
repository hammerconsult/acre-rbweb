package br.com.webpublico.controle;

import br.com.webpublico.entidades.CaracFuncionamento;
import br.com.webpublico.entidades.Tributo;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoCalculoCaracteristicaFuncionamento;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CaracteristicasFuncionamentoFacede;
import br.com.webpublico.negocios.TributoFacade;
import br.com.webpublico.tributario.enumeration.TipoCaracteristicaFuncionamentoDTO;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "caracteristicasFuncionamentoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoCaracteristicafuncionamento", pattern = "/tributario/caracteristicafuncionamento/novo/", viewId = "/faces/tributario/cadastromunicipal/caracteristicafuncionamento/edita.xhtml"),
        @URLMapping(id = "editarCaracteristicafuncionamento", pattern = "/tributario/caracteristicafuncionamento/editar/#{caracteristicasFuncionamentoControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/caracteristicafuncionamento/edita.xhtml"),
        @URLMapping(id = "listarCaracteristicafuncionamento", pattern = "/tributario/caracteristicafuncionamento/listar/", viewId = "/faces/tributario/cadastromunicipal/caracteristicafuncionamento/lista.xhtml"),
        @URLMapping(id = "verCaracteristicafuncionamento", pattern = "/tributario/caracteristicafuncionamento/ver/#{caracteristicasFuncionamentoControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/caracteristicafuncionamento/visualizar.xhtml")
})
public class CaracteristicasFuncionamentoControlador extends PrettyControlador<CaracFuncionamento> implements Serializable, CRUD {

    @EJB
    private CaracteristicasFuncionamentoFacede caracteristicasFuncionamentoFacede;
    @EJB
    private TributoFacade tributoFacade;
    private ConverterAutoComplete converterTributo;

    public CaracteristicasFuncionamentoControlador() {
        super(CaracFuncionamento.class);
    }

    @URLAction(mappingId = "novoCaracteristicafuncionamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        if (selecionado.getCodigo() == null) {
            selecionado.setCodigo(caracteristicasFuncionamentoFacede.sugereCodigo());
        }
    }

    @URLAction(mappingId = "editarCaracteristicafuncionamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verCaracteristicafuncionamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
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

    public Converter getConverterTributo() {
        if (converterTributo == null) {
            converterTributo = new ConverterAutoComplete(Tributo.class, tributoFacade);
        }
        return converterTributo;
    }

    public List<Tributo> completaTributo(String parte) {
        return tributoFacade.listaFiltrando(parte.trim().toLowerCase(), "descricao");
    }

    public List<CaracFuncionamento> getLista() {
        return caracteristicasFuncionamentoFacede.lista();
    }

    private boolean validaCampos() {
        boolean retorno = true;
        if (selecionado.getCodigo() == null) {
            retorno = false;
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "O Campo Código deve ser Preenchido.");
        } else if (selecionado.getCodigo() != null && selecionado.getCodigo().intValue() <= 0) {
            FacesUtil.addError(SummaryMessages.ATENCAO.getDescricao(), "O Valor informado do Campo Código deve ser Maior ou Igual a Zero.");
            retorno = false;
        } else if (selecionado.getCodigo() != null && selecionado.getId() == null) {
            if (caracteristicasFuncionamentoFacede.existeCodigoCategoria(selecionado.getCodigo())) {
                selecionado.setCodigo(caracteristicasFuncionamentoFacede.sugereCodigo());
                FacesUtil.addError(SummaryMessages.ATENCAO.getDescricao(), "O Valor informado do Campo Código já existe. Foi Calculado um Novo Código.");
                retorno = false;
            }
        }
        if (selecionado.getDescricaoCurta() == null || selecionado.getDescricaoCurta().trim().isEmpty()) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "O Campo Descrição Curta deve ser Preenchido.");
            retorno = false;

        } else if (caracteristicasFuncionamentoFacede.isDescricaoCurtaEmUso(selecionado)) {
            FacesUtil.addError(SummaryMessages.ATENCAO.getDescricao(), "O Valor informado do Campo Descrição Curta já existe.");
            retorno = false;
        }
        if (selecionado.getDescricao() == null || selecionado.getDescricao().trim().isEmpty()) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "O Campo Descrição Completa deve ser Preenchido.");
            retorno = false;

        } else if (caracteristicasFuncionamentoFacede.isDescricaoEmUso(selecionado)) {
            FacesUtil.addError(SummaryMessages.ATENCAO.getDescricao(), "O Valor informado do Campo Descrição Completa já existe.");
            retorno = false;
        }
        if (selecionado.getTributo() == null || selecionado.getTributo().getId() == null) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "O Campo Tributo deve ser Preenchido.");
            retorno = false;
        }
        if (selecionado.getTipo() == null) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "O Campo Tipo deve ser Preenchido.");
            retorno = false;
        }
        if (selecionado.getTipoCalculo() == null) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "O Campo Tipo de Cálculo deve ser Preenchido.");
            retorno = false;
        }
        return retorno;
    }

    public List<SelectItem> getTipos() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        retorno.add(new SelectItem(null, " "));
        for (TipoCaracteristicaFuncionamentoDTO tipo : TipoCaracteristicaFuncionamentoDTO.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getTiposCalculo() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        retorno.add(new SelectItem(null, " "));
        for (TipoCalculoCaracteristicaFuncionamento tipo : TipoCalculoCaracteristicaFuncionamento.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }

    @Override
    public AbstractFacade getFacede() {
        return caracteristicasFuncionamentoFacede;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/caracteristicafuncionamento/";
    }

    @Override
    public void redireciona() {
        FacesUtil.navegaEmbora(selecionado, getCaminhoPadrao());
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
