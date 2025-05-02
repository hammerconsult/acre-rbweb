package br.com.webpublico.controle;

import br.com.webpublico.entidades.DerivacaoObjetoCompra;
import br.com.webpublico.entidades.DerivacaoObjetoCompraComponente;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.DerivacaoObjetoCompraFacade;
import br.com.webpublico.util.FacesUtil;
import com.google.common.base.Strings;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "derivacaoObjetoCompraControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaDerivacao", pattern = "/derivacao-objeto-compra/novo/", viewId = "/faces/administrativo/licitacao/derivacao-objeto-compra/edita.xhtml"),
    @URLMapping(id = "editarDerivacao", pattern = "/derivacao-objeto-compra/editar/#{derivacaoObjetoCompraControlador.id}/", viewId = "/faces/administrativo/licitacao/derivacao-objeto-compra/edita.xhtml"),
    @URLMapping(id = "listarDerivacao", pattern = "/derivacao-objeto-compra/listar/", viewId = "/faces/administrativo/licitacao/derivacao-objeto-compra/lista.xhtml"),
    @URLMapping(id = "verDerivacao", pattern = "/derivacao-objeto-compra/ver/#{derivacaoObjetoCompraControlador.id}/", viewId = "/faces/administrativo/licitacao/derivacao-objeto-compra/visualizar.xhtml")
})
public class DerivacaoObjetoCompraControlador extends PrettyControlador<DerivacaoObjetoCompra> implements Serializable, CRUD {

    @EJB
    private DerivacaoObjetoCompraFacade facade;
    private DerivacaoObjetoCompraComponente derivacaoComponente;


    public DerivacaoObjetoCompraControlador() { super(DerivacaoObjetoCompra.class); }

    @Override
    public String getCaminhoPadrao() {
        return "/derivacao-objeto-compra/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    @URLAction(mappingId = "novaDerivacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        derivacaoComponente = new DerivacaoObjetoCompraComponente();
    }

    @Override
    @URLAction(mappingId = "editarDerivacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        derivacaoComponente = new DerivacaoObjetoCompraComponente();
    }

    @Override
    @URLAction(mappingId = "verDerivacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
    }

    @Override
    public void salvar() {
        try {
            selecionado.realizarValidacoes();
            validarListaComponenteVazia();
            selecionado = facade.salvarNovaDerivacao(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redirecionarParaVer();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void validarListaComponenteVazia() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDerivacaoComponentes().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Adicione um componente à lista.");
        }
        ve.lancarException();
    }

    public void addDerivacaoComponente() {
        try {
            validarComponenteJaAdicionado();
            derivacaoComponente.setDerivacaoObjetoCompra(selecionado);
            selecionado.getDerivacaoComponentes().add(derivacaoComponente);
            derivacaoComponente = new DerivacaoObjetoCompraComponente();
            FacesUtil.executaJavaScript("setaFoco('Formulario:input-componente')");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void validarComponenteJaAdicionado() {
        ValidacaoException ve = new ValidacaoException();
        if (Strings.isNullOrEmpty(derivacaoComponente.getDescricao())){
            ve.adicionarMensagemDeCampoObrigatorio("O campo componente deve ser informado.");
        }
        ve.lancarException();
        for (DerivacaoObjetoCompraComponente componente : selecionado.getDerivacaoComponentes()) {
            if (derivacaoComponente.getDescricao().equalsIgnoreCase(componente.getDescricao())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O componente informado já foi adicionado na lista.");
            }
        }
        ve.lancarException();
    }

    public void removerComponente(DerivacaoObjetoCompraComponente componente) {
        selecionado.getDerivacaoComponentes().remove(componente);
    }

    public DerivacaoObjetoCompraComponente getDerivacaoComponente() {
        return derivacaoComponente;
    }

    public void setDerivacaoComponente(DerivacaoObjetoCompraComponente derivacaoComponente) {
        this.derivacaoComponente = derivacaoComponente;
    }

    public void redirecionarParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + getUrlKeyValue() + "/");
    }
}


