package br.com.webpublico.controle;

import br.com.webpublico.entidades.CNAE;
import br.com.webpublico.entidades.LotacaoVistoriadora;
import br.com.webpublico.entidades.TipoVistoria;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CNAEFacade;
import br.com.webpublico.negocios.LotacaoVistoriadoraFacade;
import br.com.webpublico.negocios.TipoVistoriaFacade;
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
import java.math.BigDecimal;
import java.util.List;

/**
 * @author daniel
 */
@ManagedBean(name = "tipoVistoriaControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoTipoVistoria", pattern = "/tipo-de-vistoria/novo/", viewId = "/faces/tributario/cadastromunicipal/tipovistoria/edita.xhtml"),
        @URLMapping(id = "editarTipoVistoria", pattern = "/tipo-de-vistoria/editar/#{tipoVistoriaControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/tipovistoria/edita.xhtml"),
        @URLMapping(id = "listarTipoVistoria", pattern = "/tipo-de-vistoria/listar/", viewId = "/faces/tributario/cadastromunicipal/tipovistoria/lista.xhtml"),
        @URLMapping(id = "verTipoVistoria", pattern = "/tipo-de-vistoria/ver/#{tipoVistoriaControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/tipovistoria/visualizar.xhtml")
})
public class TipoVistoriaControlador extends PrettyControlador<TipoVistoria> implements Serializable, CRUD {

    @EJB
    private TipoVistoriaFacade tipoVistoriaFacade;
    @EJB
    private CNAEFacade cnaeFacade;
    @EJB
    private LotacaoVistoriadoraFacade lotacaoFacade;
    private ConverterAutoComplete converterCNAE;
    private ConverterAutoComplete converterLotacao;


    public TipoVistoriaControlador() {
        super(TipoVistoria.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoVistoriaFacade;
    }

    public List<TipoVistoria> getVistorias() {
        return tipoVistoriaFacade.lista();
    }

    public Converter getConverterCNAE() {
        if (converterCNAE == null) {
            converterCNAE = new ConverterAutoComplete(CNAE.class, cnaeFacade);
        }
        return converterCNAE;
    }

    public Converter getConverterLotacao() {
        if (converterLotacao == null) {
            converterLotacao = new ConverterAutoComplete(LotacaoVistoriadora.class, lotacaoFacade);
        }
        return converterLotacao;
    }

    public List<CNAE> completaCNAE(String parte) {
        return cnaeFacade.listaFiltrando(parte.trim(), "codigoCnae", "descricaoDetalhada");
    }

    public List<LotacaoVistoriadora> completaLotacao(String parte) {
        return lotacaoFacade.listaFiltrando(parte.trim(), "descricao");
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tipo-de-vistoria/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novoTipoVistoria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "editarTipoVistoria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    @URLAction(mappingId = "verTipoVistoria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();

    }

    @Override
    public void excluir() {

            super.excluir();

    }

    @Override
    public void salvar() {
        if (validaCampos()) {

            super.salvar();
        }
    }


    private boolean validaCampos() {
        boolean retorno = true;
        if (selecionado.getCodigo() == null || selecionado.getCodigo().intValue() <= 0) {
            retorno = false;
            FacesUtil.addError("Atenção!", "O Código deve ser Maior que Zero");
        }

        if (tipoVistoriaFacade.jaExisteCodigo(selecionado)) {
            retorno = false;
            FacesUtil.addError("Atenção!", "Código já Existente");
        }

        if (tipoVistoriaFacade.jaExisteDescricao(selecionado)) {
            retorno = false;
            FacesUtil.addError("Atenção!", "Descrição já Existente");
        }

        if (selecionado.getValorUFMRB() != null) {
            if (selecionado.getValorUFMRB().compareTo(BigDecimal.ZERO) < 0) {
                retorno = false;
                FacesUtil.addError("Atenção!", "O Campo valor deve ser Maior que Zero");
            }
        }
        return retorno;
    }

}
