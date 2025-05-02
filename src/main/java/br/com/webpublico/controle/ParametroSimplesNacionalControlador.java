package br.com.webpublico.controle;

import br.com.webpublico.entidades.Divida;
import br.com.webpublico.entidades.ParamSimplesNacionalDivida;
import br.com.webpublico.entidades.ParametroSimplesNacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ParametroSimplesNacionalFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "parametroSimplesNacionalNovo", pattern = "/parametro-simples-nacional/novo/", viewId = "/faces/tributario/simples-nacional/parametro/edita.xhtml"),
    @URLMapping(id = "parametroSimplesNacionalEditar", pattern = "/parametro-simples-nacional/editar/#{parametroSimplesNacionalControlador.id}/", viewId = "/faces/tributario/simples-nacional/parametro/edita.xhtml"),
    @URLMapping(id = "parametroSimplesNacionalVer", pattern = "/parametro-simples-nacional/ver/#{parametroSimplesNacionalControlador.id}/", viewId = "/faces/tributario/simples-nacional/parametro/visualizar.xhtml"),
    @URLMapping(id = "parametroSimplesNacionalListar", pattern = "/parametro-simples-nacional/listar/", viewId = "/faces/tributario/simples-nacional/parametro/lista.xhtml")
})
public class ParametroSimplesNacionalControlador extends PrettyControlador<ParametroSimplesNacional> implements CRUD {

    @EJB
    private ParametroSimplesNacionalFacade parametroSimplesNacionalFacade;

    private Divida divida;

    public ParametroSimplesNacionalControlador() {
        super(ParametroSimplesNacional.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return parametroSimplesNacionalFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/parametro-simples-nacional/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "parametroSimplesNacionalNovo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "parametroSimplesNacionalEditar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    @URLAction(mappingId = "parametroSimplesNacionalVer", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        try {
            validarParametro();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarParametro() {
        ValidacaoException ve = new ValidacaoException();
        if(selecionado.getExercicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício deve ser informado.");
        }
        if(selecionado.getValorUFMRB() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Valor Mínimo em UFMRB deve ser informado.");
        }
        if(selecionado.getDividas().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Adicione pelo menos uma dívida.");
        }
        if (selecionado.getExercicio() != null && parametroSimplesNacionalFacade.hasParametroNoExercicio(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um parâmetro cadastrado para o exercício informado.");
        }
        ve.lancarException();
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public List<Divida> buscarDividas(String parte) {
        return parametroSimplesNacionalFacade.buscarDividas(parte);
    }

    public void adicionarDivida() {
        try {
            validarDividas();

            ParamSimplesNacionalDivida parametroDivida = new ParamSimplesNacionalDivida();
            parametroDivida.setParametroSimplesNacional(selecionado);
            parametroDivida.setDivida(divida);

            selecionado.getDividas().add(parametroDivida);

        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } finally {
            divida = null;
        }
    }

    public void removerDivida(ParamSimplesNacionalDivida divida) {
        selecionado.getDividas().remove(divida);
    }

    private void validarDividas() {
        ValidacaoException ve = new ValidacaoException();

        if (divida == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo dívida deve ser informado.");
        }

        if (selecionado.getDividas() != null && !selecionado.getDividas().isEmpty()) {
            for (ParamSimplesNacionalDivida paramDivida : selecionado.getDividas()) {
                if (paramDivida.getDivida().equals(divida)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A Dívida selecionada já está na lista.");
                    break;
                }
            }
        }
        ve.lancarException();
    }
}
