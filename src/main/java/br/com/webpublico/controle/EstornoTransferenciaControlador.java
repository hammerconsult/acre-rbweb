/*
 * Codigo gerado automaticamente em Mon Sep 17 16:20:52 GMT-03:00 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfiguracaoTranferenciaFinanceira;
import br.com.webpublico.entidades.ContaBancariaEntidade;
import br.com.webpublico.entidades.EstornoTransferencia;
import br.com.webpublico.entidades.TransferenciaContaFinanceira;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.OrigemTipoTransferencia;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EstornoTransferenciaFacade;
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
@Etiqueta("Estorno Transferência Financeira")
@URLMappings(mappings = {
    @URLMapping(id = "novo-estorno-transferencia-financeira", pattern = "/estorno-transferencia-financeira/novo/", viewId = "/faces/financeiro/orcamentario/estornotransferencia/edita.xhtml"),
    @URLMapping(id = "editar-estorno-transferencia-financeira", pattern = "/estorno-transferencia-financeira/editar/#{estornoTransferenciaControlador.id}/", viewId = "/faces/financeiro/orcamentario/estornotransferencia/edita.xhtml"),
    @URLMapping(id = "ver-estorno-transferencia-financeira", pattern = "/estorno-transferencia-financeira/ver/#{estornoTransferenciaControlador.id}/", viewId = "/faces/financeiro/orcamentario/estornotransferencia/visualizar.xhtml"),
    @URLMapping(id = "listar-estorno-transferencia-financeira", pattern = "/estorno-transferencia-financeira/listar/", viewId = "/faces/financeiro/orcamentario/estornotransferencia/lista.xhtml")
})
public class EstornoTransferenciaControlador extends PrettyControlador<EstornoTransferencia> implements Serializable, CRUD {

    @EJB
    private EstornoTransferenciaFacade estornoTransferenciaFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConverterAutoComplete converterTransferencia;
    private TransferenciaContaFinanceira transferenciaFinanceira;
    private MoneyConverter moneyConverter;
    private ContaBancariaEntidade contaBancariaConcedida;
    private ContaBancariaEntidade contaBancariaRecebida;

    public EstornoTransferenciaControlador() {
        metadata = new EntidadeMetaData(EstornoTransferencia.class);
    }

    public EstornoTransferenciaFacade getFacade() {
        return estornoTransferenciaFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return estornoTransferenciaFacade;
    }

    @URLAction(mappingId = "novo-estorno-transferencia-financeira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataEstorno(sistemaControlador.getDataOperacao());
        selecionado.setUnidadeOrganizacionalAdm(sistemaControlador.getUnidadeOrganizacionalAdministrativaCorrente());
        selecionado.setUnidadeOrganizacional(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
        selecionado.setExercicio(sistemaControlador.getExercicioCorrente());

        if (estornoTransferenciaFacade.getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
            FacesUtil.addWarn(SummaryMessages.ATENCAO.getDescricao(), estornoTransferenciaFacade.getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
        }
    }

    @URLAction(mappingId = "editar-estorno-transferencia-financeira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditaVer();
    }

    @URLAction(mappingId = "ver-estorno-transferencia-financeira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditaVer();
    }

    public void recuperaEditaVer() {
        operacao = Operacoes.EDITAR;
        selecionado = estornoTransferenciaFacade.recuperar(selecionado.getId());
        transferenciaFinanceira = selecionado.getTransferencia();
        setaContaBancariaRecebida();
        setaContaBancariaConcedida();
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O  campo valor deve ser maior que zero(0).");
        }
        if (DataUtil.dataSemHorario(selecionado.getDataEstorno()).before(DataUtil.dataSemHorario(selecionado.getTransferencia().getDataTransferencia()))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" A data do estorno " + DataUtil.getDataFormatada(selecionado.getDataEstorno()) + " deve ser superior ou igual a data da transferência " + DataUtil.getDataFormatada(selecionado.getTransferencia().getDataTransferencia()) + ".");
        }
        if (isOperacaoNovo() && selecionado.getValor().compareTo(selecionado.getTransferencia().getSaldo()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O Valor a ser estornado de " + "<b>" + Util.formataValor(selecionado.getValor()) + "</b>" + ", não pode ser maior que o saldo de " + "<b>" + Util.formataValor(selecionado.getTransferencia().getSaldo()) + "</b>" + " disponível na transferência.");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public Boolean verificaEdicao() {
        if (operacao.equals(Operacoes.EDITAR)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void salvar() {
        try {
            selecionado.realizarValidacoes();
            validarCampos();
            if (operacao.equals(Operacoes.NOVO)) {
                estornoTransferenciaFacade.salvarNovo(selecionado);
            } else {
                estornoTransferenciaFacade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada("Registro salvo com sucesso.");
            redireciona();

        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            estornoTransferenciaFacade.getSingletonConcorrenciaContabil().tratarExceptionConcorrenciaSaldos(ex);
            estornoTransferenciaFacade.getSingletonConcorrenciaContabil().desbloquear(selecionado.getTransferencia());
        } catch (Exception e) {
            estornoTransferenciaFacade.getSingletonConcorrenciaContabil().desbloquear(selecionado.getTransferencia());
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public String setaEventoRetirada() {
        EstornoTransferencia ad = ((EstornoTransferencia) selecionado);
        ConfiguracaoTranferenciaFinanceira configuracao;
        try {
            if (transferenciaFinanceira != null) {
                configuracao = estornoTransferenciaFacade.getConfiguracaoTranferenciaFinanceiraFacade().recuperaEvento(TipoLancamento.ESTORNO, transferenciaFinanceira.getTipoTransferenciaFinanceira(), OrigemTipoTransferencia.CONCEDIDO, transferenciaFinanceira.getResultanteIndependente(), ad.getDataEstorno());
                ad.setEventoContabilRetirada(configuracao.getEventoContabil());
                return ad.getEventoContabilRetirada().toString();
            } else {
                return "Nenhum evento encontrado";
            }
        } catch (ExcecaoNegocioGenerica e) {
            return e.getMessage();
        }
    }

    public String setaEventoDeposito() {
        EstornoTransferencia ad = ((EstornoTransferencia) selecionado);
        ConfiguracaoTranferenciaFinanceira configuracao;
        try {
            if (transferenciaFinanceira != null) {
                configuracao = estornoTransferenciaFacade.getConfiguracaoTranferenciaFinanceiraFacade().recuperaEvento(TipoLancamento.ESTORNO, transferenciaFinanceira.getTipoTransferenciaFinanceira(), OrigemTipoTransferencia.RECEBIDO, transferenciaFinanceira.getResultanteIndependente(), ad.getDataEstorno());
                ad.setEventoContabil(configuracao.getEventoContabil());
                return ad.getEventoContabil().toString();
            } else {
                return "Nenhum evento encontrado";
            }
        } catch (ExcecaoNegocioGenerica e) {
            return e.getMessage();
        }
    }

    public List<EstornoTransferencia> getListaTodos() {
        return estornoTransferenciaFacade.listaPorUnidade(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
    }

    public List<TransferenciaContaFinanceira> completaTransferencia(String parte) {
        return estornoTransferenciaFacade.getTransferenciaContaFinanceiraFacade().listaFiltrandoPorUnidadeESaldo(parte.trim(), sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente(), sistemaControlador.getExercicioCorrente());
    }

    public ConverterAutoComplete getConverterTransferencia() {
        if (converterTransferencia == null) {
            converterTransferencia = new ConverterAutoComplete(TransferenciaContaFinanceira.class, estornoTransferenciaFacade.getTransferenciaContaFinanceiraFacade());
        }
        return converterTransferencia;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public void selecionarTransferencia(ActionEvent evento) {
        transferenciaFinanceira = (TransferenciaContaFinanceira) evento.getComponent().getAttributes().get("objeto");
        definirValores();
    }

    public String actionSelecionar() {
        return "edita";
    }

    @Override
    public String getCaminhoPadrao() {
        return "/estorno-transferencia-financeira/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void setaContaBancariaConcedida() {
        try {
            contaBancariaConcedida = transferenciaFinanceira.getSubContaRetirada().getContaBancariaEntidade();
        } catch (Exception ex) {

        }
    }

    public void setaContaBancariaRecebida() {
        try {
            contaBancariaRecebida = transferenciaFinanceira.getSubContaDeposito().getContaBancariaEntidade();
        } catch (Exception ex) {
            logger.debug(ex.getMessage());
        }
    }

    public void definirValores() {
        if (transferenciaFinanceira != null) {
            setaContaBancariaConcedida();
            setaContaBancariaRecebida();
            selecionado.setValor(transferenciaFinanceira.getSaldo());
            selecionado.setExercicio(transferenciaFinanceira.getExercicio());
            selecionado.setHistorico(transferenciaFinanceira.getHistorico());
            selecionado.setUnidadeOrganizacional(transferenciaFinanceira.getUnidadeOrgConcedida());
            selecionado.setUnidadeOrganizacionalAdm(transferenciaFinanceira.getUnidadeOrganizacionalAdm());
            selecionado.setTransferencia(transferenciaFinanceira);
        }
    }

    public void redirecionaParaLista() {
        FacesUtil.redirecionamentoInterno("/estorno-transferencia-financeira/listar/");
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

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public TransferenciaContaFinanceira getTransferenciaFinanceira() {
        return transferenciaFinanceira;
    }

    public void setTransferenciaFinanceira(TransferenciaContaFinanceira transferenciaFinanceira) {
        this.transferenciaFinanceira = transferenciaFinanceira;
    }
}
