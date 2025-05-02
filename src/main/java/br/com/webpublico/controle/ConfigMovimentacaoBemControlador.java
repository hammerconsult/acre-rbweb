package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfigMovimentacaoBem;
import br.com.webpublico.entidades.ConfigMovimentacaoBemBloqueio;
import br.com.webpublico.entidades.ConfigMovimentacaoBemPesquisa;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfigMovimentacaoBemFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "configMovimentacaoBemControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-config-movimentacao-bem", pattern = "/configuracao-movimentacao-bem/novo/", viewId = "/faces/administrativo/configuracao/config-movimentacao-bem/edita.xhtml"),
    @URLMapping(id = "edita-config-movimentacao-bem", pattern = "/configuracao-movimentacao-bem/editar/#{configMovimentacaoBemControlador.id}/", viewId = "/faces/administrativo/configuracao/config-movimentacao-bem/edita.xhtml"),
    @URLMapping(id = "listar-config-movimentacao-bem", pattern = "/configuracao-movimentacao-bem/listar/", viewId = "/faces/administrativo/configuracao/config-movimentacao-bem/lista.xhtml"),
    @URLMapping(id = "ver-config-movimentacao-bem", pattern = "/configuracao-movimentacao-bem/ver/#{configMovimentacaoBemControlador.id}/", viewId = "/faces/administrativo/configuracao/config-movimentacao-bem/visualizar.xhtml")
})

public class ConfigMovimentacaoBemControlador extends PrettyControlador<ConfigMovimentacaoBem> implements Serializable, CRUD {

    @EJB
    private ConfigMovimentacaoBemFacade facade;

    public ConfigMovimentacaoBemControlador() {
        super(ConfigMovimentacaoBem.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-movimentacao-bem/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "nova-config-movimentacao-bem", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setPesquisa(new ConfigMovimentacaoBemPesquisa());
        selecionado.setBloqueio(new ConfigMovimentacaoBemBloqueio());
        selecionado.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setInicioVigencia(facade.getSistemaFacade().getDataOperacao());
    }

    @URLAction(mappingId = "edita-config-movimentacao-bem", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        if (selecionado.getPesquisa() == null) {
            selecionado.setPesquisa(new ConfigMovimentacaoBemPesquisa());
        }
        if (selecionado.getBloqueio() == null) {
            selecionado.setBloqueio(new ConfigMovimentacaoBemBloqueio());
        }
    }

    @URLAction(mappingId = "ver-config-movimentacao-bem", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public List<SelectItem> getOperacoes() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (OperacaoMovimentacaoBem obj : OperacaoMovimentacaoBem.values()) {
            if (!OperacaoMovimentacaoBem.NAO_APLICAVEL.equals(obj)){
                toReturn.add(new SelectItem(obj, obj.toString()));
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
                validarVigenciaEncerrada();
                facade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void validarConfiguracaoExistente() {
        ValidacaoException ve = new ValidacaoException();
        if (facade.verificarConfiguracaoExistente(selecionado, selecionado.getInicioVigencia(), selecionado.getOperacaoMovimentacaoBem())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("" +
                "Existe uma configuração vigente para os parâmetros: " +
                " Operação: " + selecionado.getOperacaoMovimentacaoBem().getDescricao() + ", " +
                " Vigência:  " + DataUtil.getDataFormatada(selecionado.getInicioVigencia()) + ".");
        }
        ve.lancarException();
    }


    public void validarCampos() {
        selecionado.realizarValidacoes();
        validarConfiguracaoExistente();
    }

    public void validarEncerramentoVigencia() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getFimVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo fim de vigência deve ser informado.");
        }
        ve.lancarException();
        if (selecionado.getFimVigencia().before(selecionado.getInicioVigencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O fim de vigência deve ser superior ao inicio de vigência.");
        }
        ve.lancarException();
    }

    public void validarVigenciaEncerrada() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getFimVigencia() != null && selecionado.getInicioVigencia().after(selecionado.getFimVigencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" Vigência já encerrada na data: " + DataUtil.getDataFormatada(selecionado.getFimVigencia())
                + ".  Para editar a configuração, o início de vigência deve ser inferior ao fim de vigência.");
        }
        ve.lancarException();
    }

    public void encerrarVigencia() {
        try {
            validarEncerramentoVigencia();
            facade.encerrarVigencia(selecionado);
            FacesUtil.addOperacaoRealizada("Configuração encerrada com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void cancelarEncerramentoVigencia() {
        selecionado.setFimVigencia(null);
    }

    public boolean isVigenciaEncerrada() {
        return selecionado.getFimVigencia() == null || Util.getDataHoraMinutoSegundoZerado(selecionado.getFimVigencia()).compareTo(Util.getDataHoraMinutoSegundoZerado(facade.getSistemaFacade().getDataOperacao())) >= 0;
    }
}
