package br.com.webpublico.controle;

import br.com.webpublico.entidades.LotacaoVistoriadora;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.LotacaoVistoriadoraFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "lotacaoVistoriadoraControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novaLotacaoVistoriadora", pattern = "/lotacao-vistoriadora/novo/", viewId = "/faces/tributario/alvara/lotacao/edita.xhtml"),
        @URLMapping(id = "editarLotacaoVistoriadora", pattern = "/lotacao-vistoriadora/editar/#{lotacaoVistoriadoraControlador.id}/", viewId = "/faces/tributario/alvara/lotacao/edita.xhtml"),
        @URLMapping(id = "listarLotacaoVistoriadora", pattern = "/lotacao-vistoriadora/listar/", viewId = "/faces/tributario/alvara/lotacao/lista.xhtml"),
        @URLMapping(id = "verLotacaoVistoriadora", pattern = "/lotacao-vistoriadora/ver/#{lotacaoVistoriadoraControlador.id}/", viewId = "/faces/tributario/alvara/lotacao/visualizar.xhtml")
})
public class LotacaoVistoriadoraControlador extends PrettyControlador<LotacaoVistoriadora> implements Serializable, CRUD {

    @EJB
    private LotacaoVistoriadoraFacade lotacaoVistoriadoraFacade;
    private ConverterAutoComplete converterUsuarioSistema;


    public LotacaoVistoriadoraControlador() {
        super(LotacaoVistoriadora.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/lotacao-vistoriadora/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return lotacaoVistoriadoraFacade;
    }

    @Override
    @URLAction(mappingId = "novaLotacaoVistoriadora", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setCodigo(lotacaoVistoriadoraFacade.sugereCodigo());
    }

    @Override
    @URLAction(mappingId = "verLotacaoVistoriadora", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "editarLotacaoVistoriadora", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            super.salvar();
        }
    }


    private boolean validaCampos() {
        boolean resultado = true;
        if (selecionado.getCodigo() == null || selecionado.getCodigo().intValue() <= 0) {
            resultado = false;
            FacesUtil.addError("Atenção!", "O Código deve ser informado e Maior que Zero");
        } else if (lotacaoVistoriadoraFacade.codigoJaExiste(selecionado)) {
            selecionado.setCodigo(lotacaoVistoriadoraFacade.sugereCodigo());
            resultado = false;
            FacesUtil.addError("Atenção!", "O Código informado já está em uso em outro Registro. Foi Calculado um Novo Código");
        }
        if (selecionado.getDescricao() == null || selecionado.getDescricao().trim().isEmpty()) {
            resultado = false;
            FacesUtil.addError("Campo Obrigatório!", "Informe a descrição");
        } else if (lotacaoVistoriadoraFacade.descricaoJaExiste(selecionado)) {
            resultado = false;
            FacesUtil.addError("Atenção!", "A Descrição informada já está em uso em outro Registro");
        }
        return resultado;
    }

    public List<UsuarioSistema> completaUsuarioSistema(String parte) {
        return lotacaoVistoriadoraFacade.getUsuarioSistemaFacade().listaFiltrando(parte.trim(), "login");
    }

    public Converter getConverterUsuarioSistema() {
        if (this.converterUsuarioSistema == null) {
            this.converterUsuarioSistema = new ConverterAutoComplete(UsuarioSistema.class, lotacaoVistoriadoraFacade.getUsuarioSistemaFacade());
        }
        return this.converterUsuarioSistema;
    }
}
