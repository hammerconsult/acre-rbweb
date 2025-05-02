package br.com.webpublico.controle;

import br.com.webpublico.entidades.Feira;
import br.com.webpublico.entidades.Feirante;
import br.com.webpublico.entidades.FeiranteFeira;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.enums.TipoFeirante;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.FeiranteFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-feirante", pattern = "/feirante/novo/", viewId = "/faces/administrativo/feiras/feirante/edita.xhtml"),
    @URLMapping(id = "editar-feirante", pattern = "/feirante/editar/#{feiranteControlador.id}/", viewId = "/faces/administrativo/feiras/feirante/edita.xhtml"),
    @URLMapping(id = "listar-feirante", pattern = "/feirante/listar/", viewId = "/faces/administrativo/feiras/feirante/lista.xhtml"),
    @URLMapping(id = "ver-feirante", pattern = "/feirante/ver/#{feiranteControlador.id}/", viewId = "/faces/administrativo/feiras/feirante/visualizar.xhtml"),
})
public class FeiranteControlador extends PrettyControlador<Feirante> implements Serializable, CRUD {

    @EJB
    private FeiranteFacade facade;
    private FeiranteFeira feiranteFeira;

    public FeiranteControlador() {
        super(Feirante.class);
    }

    @Override
    @URLAction(mappingId = "novo-feirante", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        novaFeira();
    }

    @Override
    @URLAction(mappingId = "ver-feirante", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "editar-feirante", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        if (isOperacaoEditar()) {
            novaFeira();
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/feirante/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public void limparDadosFeira() {
        feiranteFeira.setFeira(null);
    }

    public void limparDadosFeirante() {
        selecionado.setPessoaFisica(null);
    }

    public List<PessoaFisica> completarFeirante(String parte) {
        return facade.getPessoaFisicaFacade().listaFiltrandoTodasPessoasByNomeAndCpf(parte.trim());
    }

    private void novaFeira() {
        feiranteFeira = new FeiranteFeira();
    }

    public void removerFeira(FeiranteFeira feiranteFeira) {
        selecionado.getFeiras().remove(feiranteFeira);
    }

    public void adicionarFeira() {
        try {
            validarFeira();
            feiranteFeira.setFeirante(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getFeiras(), feiranteFeira);
            novaFeira();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void validarFeira() {
        Util.validarCampos(feiranteFeira);

        ValidacaoException ve = new ValidacaoException();
        selecionado.getFeiras().stream()
            .filter(f -> f.getFeira().equals(feiranteFeira.getFeira()))
            .filter(f -> !f.equals(feiranteFeira))
            .map(f -> "").forEach(ve::adicionarMensagemDeOperacaoNaoPermitida);
    }

    public List<Feira> completarFeiras(String parte) {
        return facade.getFeiraFacade().buscarFeira(parte.trim());
    }

    public List<SelectItem> getTipos() {
        return Util.getListSelectItemSemCampoVazio(TipoFeirante.values(), false);
    }

    public void redirecionarPessoa() {
        FacesUtil.redirecionamentoInterno("/tributario/configuracoes/pessoa/verpessoafisica/" + selecionado.getPessoaFisica().getId() + "/");
    }

    public void redirecionarFeira() {
        FacesUtil.redirecionamentoInterno("/feira/ver/" + feiranteFeira.getFeira().getId() + "/");
    }

    public FeiranteFeira getFeiranteFeira() {
        return feiranteFeira;
    }

    public void setFeiranteFeira(FeiranteFeira feiranteFeira) {
        this.feiranteFeira = feiranteFeira;
    }
}
