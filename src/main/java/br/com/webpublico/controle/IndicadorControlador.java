/*
 * Codigo gerado automaticamente em Thu Mar 31 17:13:44 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.Indicador;
import br.com.webpublico.entidades.UnidadeMedida;
import br.com.webpublico.entidades.ValorIndicador;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.IndicadorFacade;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

@ManagedBean(name = "indicadorControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-indicador", pattern = "/indicador/novo/", viewId = "/faces/financeiro/ppa/indicador/edita.xhtml"),
    @URLMapping(id = "editar-indicador", pattern = "/indicador/editar/#{indicadorControlador.id}/", viewId = "/faces/financeiro/ppa/indicador/edita.xhtml"),
    @URLMapping(id = "ver-indicador", pattern = "/indicador/ver/#{indicadorControlador.id}/", viewId = "/faces/financeiro/ppa/indicador/visualizar.xhtml"),
    @URLMapping(id = "listar-indicador", pattern = "/indicador/listar/", viewId = "/faces/financeiro/ppa/indicador/lista.xhtml")
})
public class IndicadorControlador extends PrettyControlador<Indicador> implements Serializable, CRUD {

    @EJB
    private IndicadorFacade indicadorFacade;
    private ConverterGenerico converterUnidadeMedida;
    private ValorIndicador valorIndicador;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;

    @Override
    public AbstractFacade getFacede() {
        return indicadorFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/indicador/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-indicador", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        valorIndicador = new ValorIndicador();
        selecionado.setValorIndicadores(new ArrayList<ValorIndicador>());
    }

    @URLAction(mappingId = "ver-indicador", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarEditaVer();
    }

    @URLAction(mappingId = "editar-indicador", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarEditaVer();
    }

    @Override
    public void salvar() {
        try {
            selecionado.realizarValidacoes();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErrorPadrao(ex);
        }
    }

    public ConverterGenerico getConverterUnidadeMedida() {
        if (converterUnidadeMedida == null) {
            converterUnidadeMedida = new ConverterGenerico(UnidadeMedida.class, indicadorFacade.getUnidadeMedidaFacade());
        }
        return converterUnidadeMedida;
    }


    public List<SelectItem> getUnidadeMedidas() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (UnidadeMedida object : indicadorFacade.getUnidadeMedidaFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public void remover(ActionEvent evento) {
        ValorIndicador vi = (ValorIndicador) evento.getComponent().getAttributes().get("removeIndicador");
        selecionado.getValorIndicadores().remove(vi);

    }

    public void alterar(ValorIndicador vi) {
        valorIndicador = (ValorIndicador) Util.clonarObjeto(vi);
    }

    public void adicionarValorIndicador() {
        try {
            validarValorIndicador();
            valorIndicador.setIndicador(selecionado);
            valorIndicador.setUsuarioSistema(sistemaControlador.getUsuarioCorrente());
            selecionado.setValorIndicadores(Util.adicionarObjetoEmLista(selecionado.getValorIndicadores(), valorIndicador));
            valorIndicador = new ValorIndicador();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarValorIndicador() {
        ValidacaoException ve = new ValidacaoException();
        valorIndicador.realizarValidacoes();
        if (valorIndicador.getValor().compareTo(BigDecimal.ZERO) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo valor deve ser maior ou igual a zero(0).");
        }
        if (valorIndicador.getDataRegistro() != null){
            for (ValorIndicador vi : selecionado.getValorIndicadores()) {
                if(DataUtil.getDataFormatada(vi.getApurado()).equals(DataUtil.getDataFormatada(valorIndicador.getApurado())) && !vi.equals(valorIndicador)){
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Ja existe um valor apurado na data selecionada.");
                    break;
                }
            }
        }
        ve.lancarException();
    }

    public List<ValorIndicador> valoresIndicadoresOrdenados() {
        if(!selecionado.getValorIndicadores().isEmpty()) {
            Collections.sort(selecionado.getValorIndicadores(), new Comparator<ValorIndicador>() {
                @Override
                public int compare(ValorIndicador o1, ValorIndicador o2) {
                    Date i1 = o1.getApurado();
                    Date i2 = o2.getApurado();
                    return i2.compareTo(i1);
                }
            });
            return selecionado.getValorIndicadores();
        }
        return new ArrayList<>();
    }

    public void recuperarEditaVer() {
        valorIndicador = new ValorIndicador();
    }

    public ValorIndicador getValorIndicador() {
        return valorIndicador;
    }

    public void setValorIndicador(ValorIndicador valorIndicador) {
        this.valorIndicador = valorIndicador;
    }

    public IndicadorControlador() {
        super(Indicador.class);
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}
