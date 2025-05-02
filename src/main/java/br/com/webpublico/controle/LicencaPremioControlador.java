/*
 * Codigo gerado automaticamente em Tue Apr 10 15:00:41 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.AtoLegal;
import br.com.webpublico.entidades.BasePeriodoAquisitivo;
import br.com.webpublico.entidades.LicencaPremio;
import br.com.webpublico.enums.TipoPeriodoAquisitivo;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AtoLegalFacade;
import br.com.webpublico.negocios.BasePeriodoAquisitivoFacade;
import br.com.webpublico.negocios.LicencaPremioFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "licencaPremioControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaLicencaPremio", pattern = "/licenca-premio/novo/", viewId = "/faces/rh/administracaodepagamento/licencapremio/edita.xhtml"),
    @URLMapping(id = "editarLicencaPremio", pattern = "/licenca-premio/editar/#{licencaPremioControlador.id}/", viewId = "/faces/rh/administracaodepagamento/licencapremio/edita.xhtml"),
    @URLMapping(id = "listarLicencaPremio", pattern = "/licenca-premio/listar/", viewId = "/faces/rh/administracaodepagamento/licencapremio/lista.xhtml"),
    @URLMapping(id = "verLicencaPremio", pattern = "/licenca-premio/ver/#{licencaPremioControlador.id}/", viewId = "/faces/rh/administracaodepagamento/licencapremio/visualizar.xhtml")
})
public class LicencaPremioControlador extends PrettyControlador<LicencaPremio> implements Serializable, CRUD {

    @EJB
    private LicencaPremioFacade licencaPremioFacade;
    @EJB
    private BasePeriodoAquisitivoFacade basePeriodoAquisitivoFacade;
    private ConverterAutoComplete converterBasePeriodoAquisitivo;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    private ConverterAutoComplete converterAtoLegal;

    public LicencaPremioControlador() {
        super(LicencaPremio.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return licencaPremioFacade;
    }

    public List<BasePeriodoAquisitivo> completaBasePeriodoAquisitivo(String parte) {
        return basePeriodoAquisitivoFacade.buscaBasePeriodoAquisitivoTipoDescricao(TipoPeriodoAquisitivo.LICENCA, parte.trim());
    }

    public ConverterAutoComplete getConverterBasePeriodoAquisitivo() {
        if (converterBasePeriodoAquisitivo == null) {
            converterBasePeriodoAquisitivo = new ConverterAutoComplete(BasePeriodoAquisitivo.class, basePeriodoAquisitivoFacade);
        }
        return converterBasePeriodoAquisitivo;
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return atoLegalFacade.listaFiltrando(parte.trim(), "nome");
    }

    public ConverterAutoComplete getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, atoLegalFacade);
        }
        return converterAtoLegal;
    }

    public void calculaFinalVigencia() {
        if (this.selecionado.getBasePeriodoAquisitivo() != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(this.selecionado.getInicioVigencia());
            c.add(Calendar.DAY_OF_MONTH, selecionado.getBasePeriodoAquisitivo().getDiasDeDireito());
            selecionado.setFinalVigencia(c.getTime());
        }
    }

    @URLAction(mappingId = "novaLicencaPremio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verLicencaPremio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarLicencaPremio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            super.salvar();
        }
    }

    public Boolean validaCampos() {
        boolean valida = Util.validaCampos(selecionado);
        if (valida) {
            LicencaPremio licenca = (LicencaPremio) selecionado;
            if (licenca.getFinalVigencia() != null) {
                if (licenca.getFinalVigencia().before(licenca.getInicioVigencia())) {
                    FacesUtil.addMessageWarn("Formulario:inicioVigencia", "Atenção", "O Início da Vigência não pode ser superior ao final da vigência !");
                    valida = false;
                }
            }

            if (licencaPremioFacade.existeLicencaPremioPorBase(licenca)) {
                FacesUtil.addMessageWarn("Formulario:basePeriodoAquisitivo", "Atenção", "Já existe uma licença prêmio vigente com a mesma base do período aquisitivo cadastrado !");
                valida = false;
            }

            if (licencaPremioFacade.existeLicencaPremioPorAtoLegal(licenca)) {
                FacesUtil.addMessageWarn("Formulario:atoLegal", "Atenção", "Já existe uma licença prêmio vigente com este mesmo ato legal cadastrado !");
                valida = false;
            }
        }
        return valida;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/licenca-premio/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
