/*
 * Codigo gerado automaticamente em Wed Mar 07 16:44:51 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfiguracaoFaltasInjustificadas;
import br.com.webpublico.entidades.ReferenciaFP;
import br.com.webpublico.entidades.TipoRegime;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfiguracaoFaltasInjustificadasFacade;
import br.com.webpublico.negocios.ReferenciaFPFacade;
import br.com.webpublico.negocios.TipoRegimeFacade;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "configuracaoFaltasInjustificadasControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoConfiguracaoFaltasInjustificadas", pattern = "/configuracaofaltasinjustificadas/novo/", viewId = "/faces/rh/administracaodepagamento/configuracaofaltasinjustificadas/edita.xhtml"),
        @URLMapping(id = "editarConfiguracaoFaltasInjustificadas", pattern = "/configuracaofaltasinjustificadas/editar/#{configuracaoFaltasInjustificadasControlador.id}/", viewId = "/faces/rh/administracaodepagamento/configuracaofaltasinjustificadas/edita.xhtml"),
        @URLMapping(id = "listarConfiguracaoFaltasInjustificadas", pattern = "/configuracaofaltasinjustificadas/listar/", viewId = "/faces/rh/administracaodepagamento/configuracaofaltasinjustificadas/lista.xhtml"),
        @URLMapping(id = "verConfiguracaoFaltasInjustificadas", pattern = "/configuracaofaltasinjustificadas/ver/#{configuracaoFaltasInjustificadasControlador.id}/", viewId = "/faces/rh/administracaodepagamento/configuracaofaltasinjustificadas/visualizar.xhtml")
})
public class ConfiguracaoFaltasInjustificadasControlador extends PrettyControlador<ConfiguracaoFaltasInjustificadas> implements Serializable, CRUD {

    @EJB
    private ConfiguracaoFaltasInjustificadasFacade configuracaoFaltasInjustificadasFacade;
    @EJB
    private TipoRegimeFacade tipoRegimeFacade;
    private ConverterGenerico converterTipoRegime;
    @EJB
    private ReferenciaFPFacade referenciaFPFacade;
    private ConverterGenerico converterReferenciaFP;

    public ConfiguracaoFaltasInjustificadasControlador() {
        super(ConfiguracaoFaltasInjustificadas.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return configuracaoFaltasInjustificadasFacade;
    }

    public List<SelectItem> getTipoRegime() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoRegime object : tipoRegimeFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterTipoRegime() {
        if (converterTipoRegime == null) {
            converterTipoRegime = new ConverterGenerico(TipoRegime.class, tipoRegimeFacade);
        }
        return converterTipoRegime;
    }

    public List<SelectItem> getReferenciaFP() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (ReferenciaFP object : referenciaFPFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao() + " " + object.getCodigo()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterReferenciaFP() {
        if (converterReferenciaFP == null) {
            converterReferenciaFP = new ConverterGenerico(ReferenciaFP.class, referenciaFPFacade);
        }
        return converterReferenciaFP;
    }

    private ConfiguracaoFaltasInjustificadas getConfiguracaoFaltasInjustificadas() {
        return (ConfiguracaoFaltasInjustificadas) selecionado;
    }

    @URLAction(mappingId = "novoConfiguracaoFaltasInjustificadas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo(); //To change body of generated methods, choose Tools | Templates.
    }

    @URLAction(mappingId = "verConfiguracaoFaltasInjustificadas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver(); //To change body of generated methods, choose Tools | Templates.
    }

    @URLAction(mappingId = "editarConfiguracaoFaltasInjustificadas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            try {
                super.salvar();
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Exceção do sistema!", e.getMessage().toString()));
            }
        }
    }

    public Boolean validaCampos() {
        boolean retorno = true;
        retorno = Util.validaCampos(selecionado);
        if (retorno) {
            if (getConfiguracaoFaltasInjustificadas().getFinalVigencia() != null) {
                if (getConfiguracaoFaltasInjustificadas().getFinalVigencia().before(getConfiguracaoFaltasInjustificadas().getInicioVigencia())) {
                    FacesUtil.addMessageWarn("Atenção", "Data Final menor que a Inicial");
                    retorno = false;
                }
            }
        }
        return retorno;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracaofaltasinjustificadas/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
