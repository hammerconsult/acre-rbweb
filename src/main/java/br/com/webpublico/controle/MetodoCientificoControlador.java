package br.com.webpublico.controle;

import br.com.webpublico.entidades.MetodoCientifico;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.MetodoCientificoFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

/**
 * Created by carlos on 26/05/15.
 */
@ManagedBean(name = "metodoCientificoControlador")
@ViewScoped
@URLMappings(
    mappings = {
        @URLMapping(id = "listarMetodoCientifico", pattern = "/metodo-cientifico/listar/", viewId = "/faces/rh/administracaodepagamento/capacitacaoservidor/metodocientifico/lista.xhtml"),
        @URLMapping(id = "criarMetodoCientifico", pattern = "/metodo-cientifico/novo/", viewId = "/faces/rh/administracaodepagamento/capacitacaoservidor/metodocientifico/edita.xhtml"),
        @URLMapping(id = "editarMetodoCientifico", pattern = "/metodo-cientifico/editar/#{metodoCientificoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/capacitacaoservidor/metodocientifico/edita.xhtml"),
        @URLMapping(id = "verMetodoCientifico", pattern = "/metodo-cientifico/ver/#{metodoCientificoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/capacitacaoservidor/metodocientifico/visualizar.xhtml")
    }
)
public class MetodoCientificoControlador extends PrettyControlador<MetodoCientifico> implements CRUD {
    @EJB
    private MetodoCientificoFacade metodoCientificoFacade;

    @Override
    public String getCaminhoPadrao() {
        return "/metodo-cientifico/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "criarMetodoCientifico", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setCodigo(metodoCientificoFacade.getSingletonGeradorCodigoRH().gerarCodigoMetodoCientifico().toString());
    }

    @Override
    @URLAction(mappingId = "verMetodoCientifico", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "editarMetodoCientifico", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    public MetodoCientificoControlador() {
        super(MetodoCientifico.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return metodoCientificoFacade;
    }

    @Override
    public void salvar() {
        String msg = "";
        String codigoSalvo = "";
        String codigoProposto = selecionado.getCodigo();
        if (validarCampos()) {
            try {
                msg = getMensagemSucessoAoSalvar();
                if (operacao == Operacoes.NOVO) {
                    codigoSalvo = metodoCientificoFacade.getSingletonGeradorCodigoRH().gerarCodigoMetodoCientifico().toString();
                    if (!codigoSalvo.equals(codigoProposto)) {
                        selecionado.setCodigo(codigoSalvo);
                        getFacede().salvar(selecionado);
                        msg = "O Código " + codigoProposto + " já está sendo usado, com isso foi gerado o código " + codigoSalvo + "!";
                    } else {
                        getFacede().salvar(selecionado);
                    }
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), msg));
                } else {
                    getFacede().salvar(selecionado);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), msg));
                }
            } catch (ValidacaoException ex) {
                FacesUtil.printAllFacesMessages(ex.getMensagens());
                return;
            } catch (Exception e) {
                descobrirETratarException(e);
            }
            redireciona();
        }
    }

    public boolean validarCampos() {
        boolean valida = true;

        if (selecionado.getNome() == null || selecionado.getNome().trim().equals("")) {
            FacesUtil.addCampoObrigatorio("O Método Científico é um campo obrigatório!");
            valida = false;
        }

        if (selecionado.getDescricao() == null || selecionado.getDescricao().trim().equals("")) {
            FacesUtil.addCampoObrigatorio("A Descrição é um campo obrigatório!");
            valida = false;
        }
        return valida;
    }
}
