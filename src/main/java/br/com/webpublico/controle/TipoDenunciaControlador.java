package br.com.webpublico.controle;

import br.com.webpublico.entidades.SecretariaFiscalizacao;
import br.com.webpublico.entidades.TipoDenuncia;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TipoDenunciaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "tipoDenunciaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoTipoDenuncia", pattern = "/tipo-de-denuncia/novo/", viewId = "/faces/tributario/denuncia/tipodenuncia/edita.xhtml"),
    @URLMapping(id = "editarTipoDenuncia", pattern = "/tipo-de-denuncia/editar/#{tipoDenunciaControlador.id}/", viewId = "/faces/tributario/denuncia/tipodenuncia/edita.xhtml"),
    @URLMapping(id = "listarTipoDenuncia", pattern = "/tipo-de-denuncia/listar/", viewId = "/faces/tributario/denuncia/tipodenuncia/lista.xhtml"),
    @URLMapping(id = "verTipoDenuncia", pattern = "/tipo-de-denuncia/ver/#{tipoDenunciaControlador.id}/", viewId = "/faces/tributario/denuncia/tipodenuncia/visualizar.xhtml")
})
public class TipoDenunciaControlador extends PrettyControlador<TipoDenuncia> implements Serializable, CRUD {

    @EJB
    private TipoDenunciaFacade tipoDenunciaFacade;
    private ConverterAutoComplete converterSecretariaFiscalizacao;
    private ConverterAutoComplete converterInfracao;

    public TipoDenunciaControlador() {
        super(TipoDenuncia.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tipo-de-denuncia/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoDenunciaFacade;
    }

    @URLAction(mappingId = "novoTipoDenuncia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editarTipoDenuncia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verTipoDenuncia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public ConverterAutoComplete getConverterSecretaria() {
        if (this.converterSecretariaFiscalizacao == null) {
            this.converterSecretariaFiscalizacao = new ConverterAutoComplete(SecretariaFiscalizacao.class, tipoDenunciaFacade.getSecretariaFiscalizacaoFacade());
        }
        return this.converterSecretariaFiscalizacao;
    }


    public List<SecretariaFiscalizacao> completaSecretaria(String parte) {
        return tipoDenunciaFacade.getSecretariaFiscalizacaoFacade().completarSecretariaFiscalizacao(parte.trim());
    }

    public Boolean validaCampos() {
        boolean retorno = true;
        if (selecionado.getSecretariaFiscalizacao() == null) {
            FacesUtil.addError("Campo Obrigatório!", " O campo Secretaria é obrigatório!");
            retorno = false;
        }
        if ("".equals(selecionado.getDescricao())) {
            FacesUtil.addError("Campo Obrigatório!", " O campo Descrição Secretaria é obrigatório!");
            retorno = false;
        }

        if ("".equals(selecionado.getDescricaoDetalhada())) {
            FacesUtil.addError("Campo Obrigatório!", " O campo Descrição Detalhada é obrigatório!");
            retorno = false;
        }
        return retorno;
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            selecionado.setCodigo(tipoDenunciaFacade.getSingletonGeradorCodigo().getProximoCodigo(TipoDenuncia.class, "codigo"));
            super.salvar();
        }
    }
}
