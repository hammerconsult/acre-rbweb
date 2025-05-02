package br.com.webpublico.controle;

import br.com.webpublico.entidades.AreaFormacao;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AreaFormacaoFacade;
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
 * Created by AndreGustavo on 29/09/2014.
 */
@ManagedBean(name = "areaFormacaoControlador")
@ViewScoped
@URLMappings(
    mappings = {
        @URLMapping(id = "listarAreaFormacao", pattern = "/area-de-formacao/listar/", viewId = "/faces/rh/administracaodepagamento/capacitacaoservidor/areaformacao/lista.xhtml"),
        @URLMapping(id = "criarAreaFormacao", pattern = "/area-de-formacao/novo/", viewId = "/faces/rh/administracaodepagamento/capacitacaoservidor/areaformacao/edita.xhtml"),
        @URLMapping(id = "editarAreaFormacao", pattern = "/area-de-formacao/editar/#{areaFormacaoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/capacitacaoservidor/areaformacao/edita.xhtml"),
        @URLMapping(id = "verAreaFormacao", pattern = "/area-de-formacao/ver/#{areaFormacaoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/capacitacaoservidor/areaformacao/visualizar.xhtml")
    }
)
public class AreaFormacaoControlador extends PrettyControlador<AreaFormacao> implements CRUD {
    @EJB
    private AreaFormacaoFacade areaFormacaoFacade;

    public AreaFormacaoControlador() {
        super(AreaFormacao.class);
    }


    @Override
    public String getCaminhoPadrao() {
        return "/area-de-formacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return areaFormacaoFacade;
    }

    @Override
    @URLAction(mappingId = "criarAreaFormacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setCodigo(areaFormacaoFacade.getSingletonGeradorCodigoRH().gerarCodigoAreaFormacao().toString());
    }

    @Override
    @URLAction(mappingId = "verAreaFormacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "editarAreaFormacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
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
                    codigoSalvo = areaFormacaoFacade.getSingletonGeradorCodigoRH().gerarCodigoAreaFormacao().toString();
                    if (!codigoSalvo.equals(codigoProposto)) {
                        selecionado.setCodigo(codigoSalvo);
                        getFacede().salvar(selecionado);
                        msg = "O Código " + codigoProposto + " já está sendo usado e foi gerado um novo código " + codigoSalvo + "!";
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

    @Override
    public void excluir() {
        if (!areaFormacaoFacade.buscarCadastroEmFormacao(selecionado)) {
            getFacede().remover(selecionado);
            redireciona();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoExcluir()));
        } else {
            FacesUtil.addOperacaoNaoPermitida("Existem cadastros em Formação que utilizam a Área de Formação " + selecionado.getDescricao());
        }
    }

    public boolean validarCampos() {
        boolean valida = true;
        if (selecionado.getDescricao() == null || selecionado.getDescricao().trim().equals("")) {
            FacesUtil.addCampoObrigatorio("A descrição é um campo obrigatório.");
            valida = false;
        }
        return valida;
    }
}
