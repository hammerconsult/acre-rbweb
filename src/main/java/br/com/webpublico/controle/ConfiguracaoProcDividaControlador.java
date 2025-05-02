/*
 * Codigo gerado automaticamente em Tue Apr 17 16:07:17 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Cargo;
import br.com.webpublico.entidades.ConfiguracaoProcDivida;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfiguracaoProcDividaFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.DateSelectEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "configuracaoProcDividaControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-config-procurador", pattern = "/divida-publica/configuracao-procurador/novo/", viewId = "/faces/financeiro/orcamentario/dividapublica/configuracaoprocdivida/edita.xhtml"),
        @URLMapping(id = "editar-config-procurador", pattern = "/divida-publica/configuracao-procurador/editar/#{configuracaoProcDividaControlador.id}/", viewId = "/faces/financeiro/orcamentario/dividapublica/configuracaoprocdivida/edita.xhtml"),
        @URLMapping(id = "ver-config-procurador", pattern = "/divida-publica/configuracao-procurador/ver/#{configuracaoProcDividaControlador.id}/", viewId = "/faces/financeiro/orcamentario/dividapublica/configuracaoprocdivida/visualizar.xhtml"),
        @URLMapping(id = "listar-config-procurador", pattern = "/divida-publica/configuracao-procurador/listar/", viewId = "/faces/financeiro/orcamentario/dividapublica/configuracaoprocdivida/lista.xhtml")
})
public class ConfiguracaoProcDividaControlador extends PrettyControlador<ConfiguracaoProcDivida> implements Serializable, CRUD {

    @EJB
    private ConfiguracaoProcDividaFacade configuracaoProcDividaFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConverterAutoComplete converterCargo;
    private Date desde;

    public ConfiguracaoProcDividaControlador() {
        super(ConfiguracaoProcDivida.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/divida-publica/configuracao-procurador/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return configuracaoProcDividaFacade;
    }

    @URLAction(mappingId = "novo-config-procurador", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        desde = selecionado.getInicioVigencia();
        selecionado.setInicioVigencia(sistemaControlador.getDataOperacao());
    }

    @URLAction(mappingId = "ver-config-procurador", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-config-procurador", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        try {
            if (Util.validaCampos(selecionado)) {
                if (configuracaoProcDividaFacade.recuperaUltima() != null) {
                    ConfiguracaoProcDivida c = configuracaoProcDividaFacade.recuperaUltima();
                    c.setFimVigencia(sistemaControlador.getDataOperacao());
                    configuracaoProcDividaFacade.salvar(c);
                    FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), " Registro salvo com sucesso.");
                } else {
                    configuracaoProcDividaFacade.salvar(selecionado);
                    FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), " Registro salvo com sucesso.");
                }
                redireciona();
            }
        } catch (Exception ex) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), ex.getMessage());
        }
    }

    public List<Cargo> completaCargo(String parte) {
        return configuracaoProcDividaFacade.getCargoFacade().listaFiltrando(parte.trim(), "codigoDoCargo", "descricao");
    }

    public ConverterAutoComplete getConverterCargo() {
        if (converterCargo == null) {
            converterCargo = new ConverterAutoComplete(Cargo.class, configuracaoProcDividaFacade.getCargoFacade());
        }
        return converterCargo;
    }

    public Date getDesde() {
        return desde;
    }

    public void setDesde(Date desde) {
        this.desde = desde;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}
