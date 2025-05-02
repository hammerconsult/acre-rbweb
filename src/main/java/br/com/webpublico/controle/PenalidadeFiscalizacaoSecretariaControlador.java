package br.com.webpublico.controle;

import br.com.webpublico.entidades.PenalidadeFiscalizacaoSecretaria;
import br.com.webpublico.entidades.SecretariaFiscalizacao;
import br.com.webpublico.entidades.Tributo;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.PenalidadeFiscalizacaoSecretariaFacade;
import br.com.webpublico.negocios.SecretariaFiscalizacaoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 04/08/14
 * Time: 15:08
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "penalidadeFiscalizacaoSecretariaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaPenalidadeFiscSecretaria", pattern = "/penalidade-fiscalizacao-secretaria/novo/", viewId = "/faces/tributario/fiscalizacaosecretaria/penalidade/edita.xhtml"),
    @URLMapping(id = "editarPenalidadeFiscSecretaria", pattern = "/penalidade-fiscalizacao-secretaria/editar/#{penalidadeFiscalizacaoSecretariaControlador.id}/", viewId = "/faces/tributario/fiscalizacaosecretaria/penalidade/edita.xhtml"),
    @URLMapping(id = "listarPenalidadeFiscSecretaria", pattern = "/penalidade-fiscalizacao-secretaria/listar/", viewId = "/faces/tributario/fiscalizacaosecretaria/penalidade/lista.xhtml"),
    @URLMapping(id = "verPenalidadeFiscSecretaria", pattern = "/penalidade-fiscalizacao-secretaria/ver/#{penalidadeFiscalizacaoSecretariaControlador.id}/", viewId = "/faces/tributario/fiscalizacaosecretaria/penalidade/visualizar.xhtml")
})
public class PenalidadeFiscalizacaoSecretariaControlador extends PrettyControlador<PenalidadeFiscalizacaoSecretaria> implements Serializable, CRUD {

    @EJB
    private PenalidadeFiscalizacaoSecretariaFacade penalidadeFiscalizacaoSecretariaFacade;
    @EJB
    private SecretariaFiscalizacaoFacade secretariaFiscalizacaoFacade;
    private ConverterAutoComplete converterSecretariaFiscalizacao;
    private ConverterAutoComplete converterTributo;

    public PenalidadeFiscalizacaoSecretariaControlador() {
        super(PenalidadeFiscalizacaoSecretaria.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/penalidade-fiscalizacao-secretaria/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return penalidadeFiscalizacaoSecretariaFacade;
    }

    @URLAction(mappingId = "novaPenalidadeFiscSecretaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verPenalidadeFiscSecretaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarPenalidadeFiscSecretaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    public ConverterAutoComplete getConverterSecretariaFiscalizacao() {
        if (converterSecretariaFiscalizacao == null) {
            converterSecretariaFiscalizacao = new ConverterAutoComplete(SecretariaFiscalizacao.class, secretariaFiscalizacaoFacade);
        }
        return converterSecretariaFiscalizacao;
    }

    public ConverterAutoComplete getConverterTributo() {
        if (converterTributo == null) {
            converterTributo = new ConverterAutoComplete(Tributo.class, secretariaFiscalizacaoFacade);
        }
        return converterTributo;
    }

    public List<SecretariaFiscalizacao> completaSecretaria(String parte) {
        return secretariaFiscalizacaoFacade.completarSecretariaFiscalizacao(parte.trim());
    }

    public List<SelectItem> getGraus() {
        return Util.getListSelectItem(Arrays.asList(PenalidadeFiscalizacaoSecretaria.Grau.values()));
    }

    public List<SelectItem> getTiposCobranca() {
        return Util.getListSelectItem(Arrays.asList(PenalidadeFiscalizacaoSecretaria.TipoCobranca.values()));
    }

    public void processaSelecaoDeTipoDeCobranca() {
        if (selecionado.getTipoCobranca() != null &&
            selecionado.getTipoCobranca().equals(PenalidadeFiscalizacaoSecretaria.TipoCobranca.NAO_APLICADA)) {
            selecionado.setValor(BigDecimal.ZERO);
            selecionado.setTributo(null);
        }
    }
}
