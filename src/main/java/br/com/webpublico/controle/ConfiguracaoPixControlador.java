package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfiguracaoDAM;
import br.com.webpublico.entidades.ConfiguracaoPix;
import br.com.webpublico.entidades.ConfiguracaoPixDam;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.ConfiguracaoPixFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.lang.StringUtils;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaConfiguracaoPix", pattern = "/configuracaopix/novo/", viewId = "/faces/tributario/configuracaopix/edita.xhtml"),
    @URLMapping(id = "editarConfiguracaoPix", pattern = "/configuracaopix/editar/#{configuracaoPixControlador.id}/", viewId = "/faces/tributario/configuracaopix/edita.xhtml"),
    @URLMapping(id = "listarConfiguracaoPix", pattern = "/configuracaopix/listar/", viewId = "/faces/tributario/configuracaopix/lista.xhtml"),
    @URLMapping(id = "verConfiguracaoPix", pattern = "/configuracaopix/ver/#{configuracaoPixControlador.id}/", viewId = "/faces/tributario/configuracaopix/visualizar.xhtml")
})
public class ConfiguracaoPixControlador extends PrettyControlador<ConfiguracaoPix> implements CRUD {
    private final Integer INPUT_SIZE = 20;

    @EJB
    private ConfiguracaoPixFacade configuracaoPixFacade;

    private ConfiguracaoPixDam configuracaoPixDam;

    public ConfiguracaoPixControlador() {
        super(ConfiguracaoPix.class);
    }

    @URLAction(mappingId = "novaConfiguracaoPix", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        this.configuracaoPixDam = new ConfiguracaoPixDam();
    }

    @URLAction(mappingId = "editarConfiguracaoPix", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        this.configuracaoPixDam = new ConfiguracaoPixDam();
    }

    @URLAction(mappingId = "verConfiguracaoPix", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        try {
            validarConfiguracao();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao salvar Configuração Pix. " + e.getMessage());
            logger.error("Erro ao salvar configuracao pix. ", e);
        }
    }

    private void validarConfiguracao() {
        ValidacaoException ve = new ValidacaoException();
        if (StringUtils.isBlank(selecionado.getUrlIntegrador())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo URL Integrador deve ser informado.");
        }
        if (StringUtils.isBlank(selecionado.getBase())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Base deve ser informado.");
        }
        if (StringUtils.isBlank(selecionado.getAppKey())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo App Key deve ser informado.");
        }
        if (StringUtils.isBlank(selecionado.getUrlToken())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo URL Token deve ser informado.");
        }
        if (StringUtils.isBlank(selecionado.getUrlQrCode())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo URL QrCode deve ser informado.");
        }
        if (selecionado.getConfiguracoesPorDam().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("É necessário adicionar uma configuração para cada configuração DAM.");
        }
        if (!StringUtils.isBlank(selecionado.getBase()) && getFacede().hasConfiguracaoBase(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe uma configuração pra base " + selecionado.getBase() + " salva.");
        }

        ve.lancarException();
    }

    public String buscarValorObjetoReduzido(String valor) {
        if (valor != null && !StringUtils.isBlank(valor)) {
            return valor.length() >= INPUT_SIZE ? valor.substring(0, INPUT_SIZE) : valor;
        }
        return "";
    }

    public boolean isLenghtMaiorPermitido(String valor) {
        if (valor != null && !StringUtils.isBlank(valor)) {
            return valor.length() > INPUT_SIZE;
        }
        return false;
    }

    public void adicionarConfiguracao() {
        try {
            validarConfiguracaoPixDam();
            Util.adicionarObjetoEmLista(selecionado.getConfiguracoesPorDam(), configuracaoPixDam);
            this.configuracaoPixDam = new ConfiguracaoPixDam();
            FacesUtil.addOperacaoRealizada("Configuração adicionada com sucesso!");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao adicionar Configuração Pix-Dam. " + e.getMessage());
            logger.error("Erro ao adicionar configuracao pix. ", e);
        } finally {
            FacesUtil.executaJavaScript("aguarde.hide()");
        }
    }

    private void validarConfiguracaoPixDam() {
        ValidacaoException ve = new ValidacaoException();
        if (configuracaoPixDam != null) {
            if (configuracaoPixDam.getConfiguracaoDAM() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Configuração DAM deve ser informado.");
            }
            if (StringUtils.isBlank(configuracaoPixDam.getNumeroConvenio())) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Número do Convênio deve ser informado.");
            }
            if (StringUtils.isBlank(configuracaoPixDam.getChavePix())) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Chave PIX deve ser informado.");
            }
            if (StringUtils.isBlank(configuracaoPixDam.getChaveAcesso())) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Chave de Acesso deve ser informado.");
            }
            if (StringUtils.isBlank(configuracaoPixDam.getAuthorization())) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Authorization deve ser informado.");
            }
            if (configuracaoPixDam.getConfiguracaoDAM() != null && !StringUtils.isBlank(configuracaoPixDam.getNumeroConvenio())) {
                for (ConfiguracaoPixDam config : selecionado.getConfiguracoesPorDam()) {
                    if (!config.equals(configuracaoPixDam)) {
                        if ((config.getConfiguracaoDAM() != null && config.getConfiguracaoDAM().equals(configuracaoPixDam.getConfiguracaoDAM())) &&
                            (!StringUtils.isBlank(config.getNumeroConvenio()) && config.getNumeroConvenio().equals(configuracaoPixDam.getNumeroConvenio()))) {

                            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe uma configuração adicionada para Configuração DAM: " + config.getConfiguracaoDAM() +
                                " e Número de Convênio: " + config.getNumeroConvenio());
                        }
                    }
                }
            }
        }
        ve.lancarException();
    }

    public void editarConfiguracao(ConfiguracaoPixDam configuracaoPixDam) {
        this.configuracaoPixDam = (ConfiguracaoPixDam) Util.clonarObjeto(configuracaoPixDam);
    }

    public void removerConfiguracao(ConfiguracaoPixDam configuracaoPixDam) {
        selecionado.getConfiguracoesPorDam().remove(configuracaoPixDam);
    }

    @Override
    public ConfiguracaoPixFacade getFacede() {
        return configuracaoPixFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracaopix/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public ConfiguracaoPixDam getConfiguracaoPixDam() {
        return configuracaoPixDam;
    }

    public void setConfiguracaoPixDam(ConfiguracaoPixDam configuracaoPixDam) {
        this.configuracaoPixDam = configuracaoPixDam;
    }

    public List<ConfiguracaoDAM> buscarConfiguracoesDam(String parte) {
        return configuracaoPixFacade.buscarConfiguracoesDam(parte);
    }
}
