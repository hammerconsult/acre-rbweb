package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfiguracaoEmenda;
import br.com.webpublico.entidades.ConfiguracaoEmendaConta;
import br.com.webpublico.entidades.ConfiguracaoEmendaFonte;
import br.com.webpublico.entidades.Conta;
import br.com.webpublico.enums.ModalidadeEmenda;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfiguracaoEmendaFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
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
    @URLMapping(id = "nova-configuracao-emenda", pattern = "/configuracao-emenda/novo/", viewId = "/faces/financeiro/emenda/configuracao-emenda/edita.xhtml"),
    @URLMapping(id = "editar-configuracao-emenda", pattern = "/configuracao-emenda/editar/#{configuracaoEmendaControlador.id}/", viewId = "/faces/financeiro/emenda/configuracao-emenda/edita.xhtml"),
    @URLMapping(id = "listar-configuracao-emenda", pattern = "/configuracao-emenda/listar/", viewId = "/faces/financeiro/emenda/configuracao-emenda/lista.xhtml"),
    @URLMapping(id = "ver-configuracao-emenda", pattern = "/configuracao-emenda/ver/#{configuracaoEmendaControlador.id}/", viewId = "/faces/financeiro/emenda/configuracao-emenda/visualizar.xhtml")
})
public class ConfiguracaoEmendaControlador extends PrettyControlador<ConfiguracaoEmenda> implements Serializable, CRUD {

    @EJB
    private ConfiguracaoEmendaFacade facade;
    private ConfiguracaoEmendaConta configuracaoEmendaContaDireta;
    private ConfiguracaoEmendaConta configuracaoEmendaContaIndireta;
    private ConfiguracaoEmendaFonte configuracaoEmendaFonte;

    public ConfiguracaoEmendaControlador() {
        super(ConfiguracaoEmenda.class);
    }

    @URLAction(mappingId = "nova-configuracao-emenda", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
        selecionado.setCodigoPrograma("0404");
        instanciarConfiguracaoEmendaContaDireta();
        instanciarConfiguracaoEmendaContaIndireta();
        instanciarConfiguracaoEmendaFonte();
    }

    @URLAction(mappingId = "editar-configuracao-emenda", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        instanciarConfiguracaoEmendaContaDireta();
        instanciarConfiguracaoEmendaContaIndireta();
        instanciarConfiguracaoEmendaFonte();
    }

    @URLAction(mappingId = "ver-configuracao-emenda", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public List<Conta> completarContasDeDespesa(String filtro) {
        return facade.getContaFacade().listaFiltrandoContaDespesa(filtro.trim(), facade.getSistemaFacade().getExercicioCorrente());
    }

    public List<Conta> completarDestinacoes(String parte) {
        return facade.getContaFacade().listaFiltrandoDestinacaoRecursos(parte.trim(), facade.getSistemaFacade().getExercicioCorrente());
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
        Util.validarCampos(selecionado);
        ValidacaoException ve = new ValidacaoException();
        if (facade.hasConfiguracaoParaExercicio(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe uma configuração para o exercício " + selecionado.getExercicio().getAno() + ".");
        }
        ve.lancarException();
    }

    public void adicionarContaDespesaIndireta() {
        validarAndAdicionarConta(ModalidadeEmenda.INDIRETA, configuracaoEmendaContaIndireta);
    }

    public void adicionarContaDespesaDireta() {
        validarAndAdicionarConta(ModalidadeEmenda.DIRETA, configuracaoEmendaContaDireta);
    }

    private void validarAndAdicionarConta(ModalidadeEmenda modalidade, ConfiguracaoEmendaConta configuracaoEmendaConta) {
        try {
            configuracaoEmendaConta.setModalidadeEmenda(modalidade);
            validarContaDespesa(configuracaoEmendaConta);
            Util.adicionarObjetoEmLista(selecionado.getContasDeDespesa(), configuracaoEmendaConta);
            if (modalidade.isIndireta()) {
                instanciarConfiguracaoEmendaContaIndireta();
            } else {
                instanciarConfiguracaoEmendaContaDireta();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarContaDespesa(ConfiguracaoEmendaConta configuracaoEmendaConta) {
        ValidacaoException ve = new ValidacaoException();
        if (configuracaoEmendaConta.getContaDespesa() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Conta de Despesa deve ser informado.");
        } else {
            for (ConfiguracaoEmendaConta configEmenda : selecionado.getContasDeDespesa()) {
                if (configEmenda.getContaDespesa().equals(configuracaoEmendaConta.getContaDespesa()) && configEmenda.getModalidadeEmenda().equals(configuracaoEmendaConta.getModalidadeEmenda()) && !configEmenda.equals(configuracaoEmendaConta)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A conta selecionada já está adicionada.");
                }
            }
        }
        ve.lancarException();
    }

    public void adicionarFonte() {
        try {
            validarFonte();
            Util.adicionarObjetoEmLista(selecionado.getFontesDeRecurso(), configuracaoEmendaFonte);
            instanciarConfiguracaoEmendaFonte();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarFonte() {
        ValidacaoException ve = new ValidacaoException();
        if (configuracaoEmendaFonte.getContaDeDestinacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Fonte de Recursos deve ser informado.");
        } else {
            for (ConfiguracaoEmendaFonte configEmenda : selecionado.getFontesDeRecurso()) {
                if (configEmenda.getContaDeDestinacao().equals(configuracaoEmendaFonte.getContaDeDestinacao()) && !configEmenda.equals(configuracaoEmendaFonte)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A Fonte de Recursos selecionada já está adicionada.");
                }
            }
        }
        ve.lancarException();
    }

    private void instanciarConfiguracaoEmendaContaDireta() {
        configuracaoEmendaContaDireta = new ConfiguracaoEmendaConta();
        configuracaoEmendaContaDireta.setConfiguracaoEmenda(selecionado);
    }

    private void instanciarConfiguracaoEmendaContaIndireta() {
        configuracaoEmendaContaIndireta = new ConfiguracaoEmendaConta();
        configuracaoEmendaContaIndireta.setConfiguracaoEmenda(selecionado);
    }

    private void instanciarConfiguracaoEmendaFonte() {
        configuracaoEmendaFonte = new ConfiguracaoEmendaFonte();
        configuracaoEmendaFonte.setConfiguracaoEmenda(selecionado);
    }

    public void removerConfiguracaoEmendaConta(ConfiguracaoEmendaConta configuracaoEmendaConta) {
        selecionado.getContasDeDespesa().remove(configuracaoEmendaConta);
    }

    public void removerConfiguracaoEmendaFonte(ConfiguracaoEmendaFonte configuracaoEmendaFonte) {
        selecionado.getFontesDeRecurso().remove(configuracaoEmendaFonte);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-emenda/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public ConfiguracaoEmendaConta getConfiguracaoEmendaContaDireta() {
        return configuracaoEmendaContaDireta;
    }

    public void setConfiguracaoEmendaContaDireta(ConfiguracaoEmendaConta configuracaoEmendaContaDireta) {
        this.configuracaoEmendaContaDireta = configuracaoEmendaContaDireta;
    }

    public ConfiguracaoEmendaConta getConfiguracaoEmendaContaIndireta() {
        return configuracaoEmendaContaIndireta;
    }

    public void setConfiguracaoEmendaContaIndireta(ConfiguracaoEmendaConta configuracaoEmendaContaIndireta) {
        this.configuracaoEmendaContaIndireta = configuracaoEmendaContaIndireta;
    }

    public ConfiguracaoEmendaFonte getConfiguracaoEmendaFonte() {
        return configuracaoEmendaFonte;
    }

    public void setConfiguracaoEmendaFonte(ConfiguracaoEmendaFonte configuracaoEmendaFonte) {
        this.configuracaoEmendaFonte = configuracaoEmendaFonte;
    }
}
