package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfigContaDespesaTipoDocumento;
import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.TipoDocumentoFiscal;
import br.com.webpublico.enums.TipoContaDespesa;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfigContaDespesaTipoDocumentoFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-config-conta-despesa-tipo-documento", pattern = "/configuracao-contadespesa-tipodocumento/novo/", viewId = "/faces/financeiro/orcamentario/configuracaocontabil/config-contadespesa-tipodocumento/edita.xhtml"),
    @URLMapping(id = "editar-config-conta-despesa-tipo-documento", pattern = "/configuracao-contadespesa-tipodocumento/editar/#{configContaDespesaTipoDocumentoControlador.id}/", viewId = "/faces/financeiro/orcamentario/configuracaocontabil/config-contadespesa-tipodocumento/edita.xhtml"),
    @URLMapping(id = "ver-config-conta-despesa-tipo-documento", pattern = "/configuracao-contadespesa-tipodocumento/ver/#{configContaDespesaTipoDocumentoControlador.id}/", viewId = "/faces/financeiro/orcamentario/configuracaocontabil/config-contadespesa-tipodocumento/visualizar.xhtml"),
    @URLMapping(id = "listar-config-conta-despesa-tipo-documento", pattern = "/configuracao-contadespesa-tipodocumento/listar/", viewId = "/faces/financeiro/orcamentario/configuracaocontabil/config-contadespesa-tipodocumento/lista.xhtml")
})
public class ConfigContaDespesaTipoDocumentoControlador extends PrettyControlador<ConfigContaDespesaTipoDocumento> implements Serializable, CRUD {

    @EJB
    private ConfigContaDespesaTipoDocumentoFacade facade;

    public ConfigContaDespesaTipoDocumentoControlador() {
        super(ConfigContaDespesaTipoDocumento.class);
    }

    @Override
    @URLAction(mappingId = "nova-config-conta-despesa-tipo-documento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "ver-config-conta-despesa-tipo-documento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "editar-config-conta-despesa-tipo-documento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    public List<Conta> completarContasDeDespesa(String filtro) {
        return facade.buscarContasDeDespesa(filtro);
    }

    public List<TipoDocumentoFiscal> completarTiposDeDocumentosFiscais(String filtro) {
        return facade.buscarTiposDeDocumentosFiscais(filtro);
    }

    public boolean canEditar() {
        return selecionado.getFinalVigencia() == null || Util.getDataHoraMinutoSegundoZerado(selecionado.getFinalVigencia()).compareTo(Util.getDataHoraMinutoSegundoZerado(UtilRH.getDataOperacao())) >= 0;
    }

    public List<TipoContaDespesa> getTiposDeConta() {
        List<TipoContaDespesa> toReturn = Lists.newArrayList();
        if (selecionado != null && selecionado.getContaDespesa() != null) {
            TipoContaDespesa tipo = selecionado.getContaDespesa().getTipoContaDespesa();
            if (!TipoContaDespesa.NAO_APLICAVEL.equals(tipo) && tipo != null) {
                selecionado.setTipoContaDespesa(tipo);
                toReturn.add(tipo);
            } else {
                List<TipoContaDespesa> busca = facade.buscarTiposDeContasDespesaNosFilhosDaConta(selecionado.getContaDespesa());
                if (!busca.isEmpty()) {
                    for (TipoContaDespesa tp : busca) {
                        if (!TipoContaDespesa.NAO_APLICAVEL.equals(tp)) {
                            toReturn.add(tp);
                        }
                    }
                }
            }
        }
        return toReturn;
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            if (isOperacaoNovo()) {
                facade.salvarNovo(selecionado);
            } else {
                facade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    private void validarCampos() {
        selecionado.realizarValidacoes();
        ValidacaoException ve = new ValidacaoException();
        if (facade.hasConfiguracaoCadastrada(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe uma configuração cadastrada com a Conta de Despesa: " + selecionado.getContaDespesa().getCodigo() +
                ", Tipo de Despesa: " + selecionado.getTipoContaDespesa().getDescricao() + " e Tipo de Documento: " + selecionado.getTipoDocumentoFiscal().getDescricao() + ".");
        }
        ve.lancarException();
    }

    public void encerrarVigencia() {
        try {
            facade.encerrarVigencia(selecionado);
            FacesUtil.addOperacaoRealizada("Vigência encerrada com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-contadespesa-tipodocumento/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
