package br.com.webpublico.controle;

import br.com.webpublico.entidades.Habilidade;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.HabilidadeFacade;
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
 * Created by AndreGustavo on 26/09/2014.
 */
@ManagedBean(name = "habilidadeControlador")
@ViewScoped
@URLMappings(
        mappings = {
                @URLMapping(id = "listarHabilidade", pattern = "/habilidade/listar/", viewId = "/faces/rh/administracaodepagamento/capacitacaoservidor/habilidade/lista.xhtml"),
                @URLMapping(id = "criarHabilidade", pattern = "/habilidade/novo/", viewId = "/faces/rh/administracaodepagamento/capacitacaoservidor/habilidade/edita.xhtml"),
                @URLMapping(id = "editarHabilidade", pattern = "/habilidade/editar/#{habilidadeControlador.id}/", viewId = "/faces/rh/administracaodepagamento/capacitacaoservidor/habilidade/edita.xhtml"),
                @URLMapping(id = "verHabilidade", pattern = "/habilidade/ver/#{habilidadeControlador.id}/", viewId = "/faces/rh/administracaodepagamento/capacitacaoservidor/habilidade/visualizar.xhtml")
        }
)
public class HabilidadeControlador extends PrettyControlador<Habilidade> implements CRUD {
    @EJB
    private HabilidadeFacade habilidadeFacade;

    @Override
    public String getCaminhoPadrao() {
        return "/habilidade/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "criarHabilidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setCodigo(habilidadeFacade.getSingletonGeradorCodigoRH().gerarCodigoHabilidade().toString());
    }

    @Override
    @URLAction(mappingId = "verHabilidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "editarHabilidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    public HabilidadeControlador() {
        super(Habilidade.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return habilidadeFacade;
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
                    codigoSalvo = habilidadeFacade.getSingletonGeradorCodigoRH().gerarCodigoHabilidade().toString();
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
        if (selecionado.getCodigo() == null || selecionado.getCodigo().trim().equals("")) {
            FacesUtil.addCampoObrigatorio("O código é um campo obrigatório.");
            valida = false;
        }
        if (selecionado.getDescricao() == null || selecionado.getDescricao().trim().equals("")) {
            FacesUtil.addCampoObrigatorio("A descrição é um campo obrigatório.");
            valida = false;
        }
        return valida;
    }
}
