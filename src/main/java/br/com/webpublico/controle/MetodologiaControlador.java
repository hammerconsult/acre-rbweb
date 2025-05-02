package br.com.webpublico.controle;

import br.com.webpublico.entidades.Metodologia;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.MetodologiaFacade;
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
 * Created by carlos on 27/05/15.
 */
@ManagedBean(name = "metodologiaControlador")
@ViewScoped
@URLMappings(
        mappings = {
                @URLMapping(id = "listarMetodologia", pattern = "/metodologia/listar/", viewId = "/faces/rh/administracaodepagamento/capacitacaoservidor/metodologia/lista.xhtml"),
                @URLMapping(id = "criarMetodologia", pattern = "/metodologia/novo/", viewId = "/faces/rh/administracaodepagamento/capacitacaoservidor/metodologia/edita.xhtml"),
                @URLMapping(id = "editarMetodologia", pattern = "/metodologia/editar/#{metodologiaControlador.id}/", viewId = "/faces/rh/administracaodepagamento/capacitacaoservidor/metodologia/edita.xhtml"),
                @URLMapping(id = "verMetodologia", pattern = "/metodologia/ver/#{metodologiaControlador.id}/", viewId = "/faces/rh/administracaodepagamento/capacitacaoservidor/metodologia/visualizar.xhtml")
        }
)
public class MetodologiaControlador extends PrettyControlador<Metodologia> implements CRUD {
    @EJB
    private MetodologiaFacade metodologiaFacade;

    public MetodologiaControlador() {
        super(Metodologia.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/metodologia/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return metodologiaFacade;
    }

    @Override
    @URLAction(mappingId = "criarMetodologia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setCodigo(metodologiaFacade.getSingletonGeradorCodigoRH().gerarCodigoMetodologia().toString());
    }

    @Override
    @URLAction(mappingId = "verMetodologia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "editarMetodologia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
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
                    codigoSalvo = metodologiaFacade.getSingletonGeradorCodigoRH().gerarCodigoMetodologia().toString();
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
        if (selecionado.getCodigo().trim().isEmpty()) {
            FacesUtil.addCampoObrigatorio("O Código é um campo obrigatório!");
            valida = false;
        }
        if (selecionado.getNome().trim().isEmpty()) {
            FacesUtil.addCampoObrigatorio("O Nome é um campo obrigatório!");
            valida = false;
        }
        if (selecionado.getDescricao() == null || selecionado.getDescricao().trim().equals("")) {
            FacesUtil.addCampoObrigatorio("A Descrição é um campo obrigatório!");
            valida = false;
        }
        return valida;
    }
}
