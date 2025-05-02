/*
 * Codigo gerado automaticamente em Mon Sep 17 16:20:52 GMT-03:00 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfigLiberacaoFinanceira;
import br.com.webpublico.entidades.ContaBancariaEntidade;
import br.com.webpublico.entidades.EstornoLibCotaFinanceira;
import br.com.webpublico.entidades.LiberacaoCotaFinanceira;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.OrigemTipoTransferencia;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EstornoLibCotaFinanceiraFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.*;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@ManagedBean
@ViewScoped
@Etiqueta("Estorno Liberação Financeira")
@URLMappings(mappings = {
    @URLMapping(id = "novo-estorno-liberacao-financeira", pattern = "/estorno-liberacao-financeira/novo/", viewId = "/faces/financeiro/orcamentario/estornoliberacaocotafinanceira/edita.xhtml"),
    @URLMapping(id = "editar-estorno-liberacao-financeira", pattern = "/estorno-liberacao-financeira/editar/#{estornoLibCotaFinanceiraControlador.id}/", viewId = "/faces/financeiro/orcamentario/estornoliberacaocotafinanceira/edita.xhtml"),
    @URLMapping(id = "ver-estorno-liberacao-financeira", pattern = "/estorno-liberacao-financeira/ver/#{estornoLibCotaFinanceiraControlador.id}/", viewId = "/faces/financeiro/orcamentario/estornoliberacaocotafinanceira/visualizar.xhtml"),
    @URLMapping(id = "listar-estorno-liberacao-financeira", pattern = "/estorno-liberacao-financeira/listar/", viewId = "/faces/financeiro/orcamentario/estornoliberacaocotafinanceira/lista.xhtml")
})
public class EstornoLibCotaFinanceiraControlador extends PrettyControlador<EstornoLibCotaFinanceira> implements Serializable, CRUD {

    @EJB
    private EstornoLibCotaFinanceiraFacade estornoLibCotaFinanceiraFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConverterAutoComplete converterLiberacao;
    private MoneyConverter moneyConverter;
    private ContaBancariaEntidade contaBancariaConcedida;
    private ContaBancariaEntidade contaBancariaRecebida;

    public EstornoLibCotaFinanceiraControlador() {
        metadata = new EntidadeMetaData(EstornoLibCotaFinanceira.class);
    }

    public EstornoLibCotaFinanceiraFacade getFacade() {
        return estornoLibCotaFinanceiraFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return estornoLibCotaFinanceiraFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/estorno-liberacao-financeira/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-estorno-liberacao-financeira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado = new EstornoLibCotaFinanceira();
        ((EstornoLibCotaFinanceira) selecionado).setDataEstorno(sistemaControlador.getDataOperacao());
        selecionado.setUnidadeOrganizacional(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
        selecionado.setUnidadeOrganizacionalAdm(sistemaControlador.getUnidadeOrganizacionalAdministrativaCorrente());

        if (estornoLibCotaFinanceiraFacade.getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
            FacesUtil.addWarn("Aviso! ", estornoLibCotaFinanceiraFacade.getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
        }
    }

    @URLAction(mappingId = "editar-estorno-liberacao-financeira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditaVer();
    }

    @URLAction(mappingId = "ver-estorno-liberacao-financeira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditaVer();
    }

    public String setaEventoDeposito() {
        EstornoLibCotaFinanceira estono = ((EstornoLibCotaFinanceira) selecionado);
        ConfigLiberacaoFinanceira configuracao;
        estono.setEventoContabilDeposito(null);
        try {
            if (estono.getLiberacao() != null) {
                configuracao = estornoLibCotaFinanceiraFacade.getConfigLiberacaoFinanceiraFacade().recuperaEvento(TipoLancamento.ESTORNO, selecionado.getLiberacao().getTipoLiberacaoFinanceira(), OrigemTipoTransferencia.RECEBIDO, estono.getDataEstorno(), estono.getLiberacao().getSolicitacaoCotaFinanceira().getResultanteIndependente());
                estono.setEventoContabilDeposito(configuracao.getEventoContabil());
                return estono.getEventoContabilDeposito().toString();
            } else {
                return "Nenhum Evento encontrado";
            }
        } catch (ExcecaoNegocioGenerica e) {
            return e.getMessage();
        }
    }


    public String setaEventoRetirada() {
        EstornoLibCotaFinanceira estorno = ((EstornoLibCotaFinanceira) selecionado);
        ConfigLiberacaoFinanceira configuracao;
        estorno.setEventoContabilRetirada(null);
        try {
            if (estorno.getLiberacao() != null) {
                configuracao = estornoLibCotaFinanceiraFacade.getConfigLiberacaoFinanceiraFacade().recuperaEvento(TipoLancamento.ESTORNO, selecionado.getLiberacao().getTipoLiberacaoFinanceira(), OrigemTipoTransferencia.CONCEDIDO, estorno.getDataEstorno(), estorno.getLiberacao().getSolicitacaoCotaFinanceira().getResultanteIndependente());
                estorno.setEventoContabilRetirada(configuracao.getEventoContabil());
                return estorno.getEventoContabilRetirada().toString();
            } else {
                return "Nenhum Evento encontrado";
            }
        } catch (ExcecaoNegocioGenerica e) {
            return e.getMessage();
        }
    }


    public void carregaListaLiberacao() {
        estornoLibCotaFinanceiraFacade.getLiberacaoCotaFinanceiraFacade().listaPorUnidadeESaldo(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
    }

    public void selecionarTransferencia(ActionEvent evento) {
        LiberacaoCotaFinanceira lib = (LiberacaoCotaFinanceira) evento.getComponent().getAttributes().get("objeto");
        selecionado.setLiberacao((LiberacaoCotaFinanceira) estornoLibCotaFinanceiraFacade.getLiberacaoCotaFinanceiraFacade().recuperar(lib.getId()));
        setaValores();
    }

    public String actionSelecionar() {
        return "edita.xhtml";
    }

    @Override
    public void salvar() {
        try {
            validarLiberacaoFinanceira();
            if (isOperacaoNovo()) {
                selecionado = estornoLibCotaFinanceiraFacade.salvarRetornando(selecionado);
                estornoLibCotaFinanceiraFacade.desbloquearLiberacao(selecionado);
            } else {
                estornoLibCotaFinanceiraFacade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada("Registro salvo com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            estornoLibCotaFinanceiraFacade.desbloquearLiberacao(selecionado);
            estornoLibCotaFinanceiraFacade.getSingletonConcorrenciaContabil().tratarExceptionConcorrenciaSaldos(ex);
        } catch (Exception ex) {
            estornoLibCotaFinanceiraFacade.desbloquearLiberacao(selecionado);
            descobrirETratarException(ex);
        }
    }

    private void validarLiberacaoFinanceira() {
        selecionado.realizarValidacoes();
        validarCampos();
    }

    public Boolean verificaEditar() {
        if (operacao.equals(Operacoes.EDITAR)) {
            return true;
        } else {
            return false;
        }
    }

    public void setaValores() {
        if (selecionado.getLiberacao() != null) {
            setaContaBancariaRecebida();
            setaContaBancariaConcedida();
            selecionado.setValor(selecionado.getLiberacao().getSaldo());
            selecionado.setHistorico(selecionado.getLiberacao().getObservacoes());
        }
    }

    public void setaContaBancariaConcedida() {
        try {
            contaBancariaConcedida = selecionado.getLiberacao().getContaFinanceiraRetirada().getContaBancariaEntidade();
        } catch (Exception ex) {

        }
    }

    public void setaContaBancariaRecebida() {
        try {
            contaBancariaRecebida = selecionado.getLiberacao().getContaFinanceiraRecebida().getContaBancariaEntidade();
        } catch (Exception ex) {

        }
    }


    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O cmapo valor deve ser maior que zero(0).");
        }
        if (DataUtil.dataSemHorario(selecionado.getDataEstorno()).before(DataUtil.dataSemHorario(selecionado.getLiberacao().getDataLiberacao()))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" A data de estorno da liberação deve ser maior que a data da Liberação. Data da Liberação: <b>" + DataUtil.getDataFormatada(selecionado.getLiberacao().getDataLiberacao()) + "</b>.");
        }
        if (selecionado.getValor().compareTo(selecionado.getLiberacao().getSaldo()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O Valor de " + "<b>" + Util.formataValor(selecionado.getValor()) + "</b>" + " não pode ser maior que o saldo de " + "<b>" + Util.formataValor(selecionado.getLiberacao().getSaldo()) + "</b>" + " disponível para essa liberação.");
        }
        if (selecionado.getLiberacao() != null) {
            estornoLibCotaFinanceiraFacade.getHierarquiaOrganizacionalFacade()
                .validarUnidadesIguais(selecionado.getLiberacao().getUnidadeRetirada(), selecionado.getUnidadeOrganizacional()
                    , ve
                    , "A Unidade Organizacional do estorno de liberação deve ser a mesma da liberação.");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public void recuperaEditaVer() {
        operacao = Operacoes.EDITAR;
        selecionado = estornoLibCotaFinanceiraFacade.recuperar(selecionado.getId());
        setaContaBancariaConcedida();
        setaContaBancariaRecebida();
    }

    public List<EstornoLibCotaFinanceira> getListaTodos() {
        return estornoLibCotaFinanceiraFacade.listaPorUnidade(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
    }

    public List<LiberacaoCotaFinanceira> completaLiberacao(String parte) {
        return estornoLibCotaFinanceiraFacade.getLiberacaoCotaFinanceiraFacade().buscarFiltrandoPorUnidadeESaldo(parte.trim(), sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente(), sistemaControlador.getExercicioCorrente());
    }

    public ConverterAutoComplete getConverterLiberacao() {
        if (converterLiberacao == null) {
            converterLiberacao = new ConverterAutoComplete(LiberacaoCotaFinanceira.class, estornoLibCotaFinanceiraFacade.getLiberacaoCotaFinanceiraFacade());
        }
        return converterLiberacao;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public void redirecionaParaLista() {
        FacesUtil.redirecionamentoInterno("/estorno-liberacao-financeira/listar/");
    }

    public ContaBancariaEntidade getContaBancariaConcedida() {
        return contaBancariaConcedida;
    }

    public void setContaBancariaConcedida(ContaBancariaEntidade contaBancariaConcedida) {
        this.contaBancariaConcedida = contaBancariaConcedida;
    }

    public ContaBancariaEntidade getContaBancariaRecebida() {
        return contaBancariaRecebida;
    }

    public void setContaBancariaRecebida(ContaBancariaEntidade contaBancariaRecebida) {
        this.contaBancariaRecebida = contaBancariaRecebida;
    }
}
