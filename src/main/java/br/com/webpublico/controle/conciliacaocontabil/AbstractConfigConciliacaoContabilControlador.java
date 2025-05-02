package br.com.webpublico.controle.conciliacaocontabil;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.entidades.contabil.conciliacaocontabil.ConfigConciliacaoContabil;
import br.com.webpublico.entidades.contabil.conciliacaocontabil.ConfigConciliacaoContabilConta;
import br.com.webpublico.enums.conciliacaocontabil.TipoConfigConciliacaoContabil;
import br.com.webpublico.enums.conciliacaocontabil.TipoMovimentoSaldo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.contabil.conciliacaocontabil.AbstractConfigConciliacaoContabilFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;

import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

public abstract class AbstractConfigConciliacaoContabilControlador extends PrettyControlador<ConfigConciliacaoContabil> implements Serializable, CRUD {

    private ConfigConciliacaoContabilConta configConciliacaoContabilContaContabil;

    public AbstractConfigConciliacaoContabilControlador() {
        super(ConfigConciliacaoContabil.class);
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            if (isOperacaoNovo()) {
                getFacede().salvarNovo(selecionado);
            } else {
                getFacede().salvar(selecionado);
            }
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
            logger.error("Erro ao salvar: {} ", ex);
        }
    }

    private void validarCampos() {
        selecionado.realizarValidacoes();
        ValidacaoException ve = new ValidacaoException();
        if (getFacade().hasConfigParaOrdemQuadroAndTipo(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe uma configuração com a Ordem <b>" + selecionado.getOrdem() + "</b>, Quadro <b>" + selecionado.getQuadro() + " </b> e Totalizador <b>" + selecionado.getTotalizador().getDescricao() + "</b>.");
            ve.lancarException();
        }
        if (selecionado.getContasContabeis().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É necessário informar pelo menos uma(1) conta contábil na configuração.");
        }
        realizarValidacoes(ve);
        ve.lancarException();
    }

    public abstract void realizarValidacoes(ValidacaoException ve);

    @Override
    public AbstractFacade getFacede() {
        return getFacade();
    }

    public abstract AbstractConfigConciliacaoContabilFacade getFacade();

    public void finalizarVigencia() {
        try {
            validarVigencia();
            getFacede().salvar(selecionado);
            redireciona();
            FacesUtil.addOperacaoRealizada("Vigência finalizada com sucesso.");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
            logger.error("Erro ao finalizar a vigência: {} ", ex);
        }
    }

    private void validarVigencia() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDataFinal() != null && selecionado.getDataFinal().before(selecionado.getDataInicial())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Data Final deve ser igual ou posterior à Data Inicial.");
        }
        ve.lancarException();
    }

    public void adicionarContaContabil() {
        try {
            validarContaContabil();
            Util.adicionarObjetoEmLista(selecionado.getContas(), configConciliacaoContabilContaContabil);
            cancelarContaContabil();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    public void cancelarContaContabil() {
        configConciliacaoContabilContaContabil = null;
    }

    public void novoContaContabil() {
        configConciliacaoContabilContaContabil = new ConfigConciliacaoContabilConta();
        configConciliacaoContabilContaContabil.setConfigConciliacaoContabil(selecionado);
    }

    public void removerContaContabil(ConfigConciliacaoContabilConta config) {
        selecionado.getContasContabeis().remove(config);
    }

    public void editarContaContabil(ConfigConciliacaoContabilConta config) {
        configConciliacaoContabilContaContabil = (ConfigConciliacaoContabilConta) Util.clonarObjeto(config);
    }

    private void validarContaContabil() {
        ValidacaoException ve = new ValidacaoException();
        if (configConciliacaoContabilContaContabil.getConta() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Conta Contábil deve ser informado.");
        }
        ve.lancarException();
        for (ConfigConciliacaoContabilConta configConta : selecionado.getContasContabeis()) {
            if (!configConciliacaoContabilContaContabil.equals(configConta) && configConta.getConta().equals(configConciliacaoContabilContaContabil.getConta())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Conta Contábil selecionada já está adicionada.");
            }
        }
        ve.lancarException();
    }

    public List<Conta> completarContasContabeis(String filtro) {
        return getFacade().getContaFacade().listaContasContabeis(filtro, getFacade().getSistemaFacade().getExercicioCorrente());
    }

    public List<SelectItem> getTiposConfigConciliacaoContabil() {
        return Util.getListSelectItemSemCampoVazio(TipoConfigConciliacaoContabil.values());
    }

    public List<SelectItem> getTiposMovimentoSaldo() {
        return Util.getListSelectItemSemCampoVazio(TipoMovimentoSaldo.values());
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public ConfigConciliacaoContabilConta getConfigConciliacaoContabilContaContabil() {
        return configConciliacaoContabilContaContabil;
    }

    public void setConfigConciliacaoContabilContaContabil(ConfigConciliacaoContabilConta configConciliacaoContabilContaContabil) {
        this.configConciliacaoContabilContaContabil = configConciliacaoContabilContaContabil;
    }

    public UsuarioSistema getUsuarioCorrente() {
        return getFacade().getSistemaFacade().getUsuarioCorrente();
    }
}
