/*
 * Codigo gerado automaticamente em Mon Oct 01 11:27:54 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfiguracaoTranferenciaMesmaUnidade;
import br.com.webpublico.entidades.ContaBancariaEntidade;
import br.com.webpublico.entidades.EstornoTransfMesmaUnidade;
import br.com.webpublico.entidades.TransferenciaMesmaUnidade;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.OrigemTipoTransferencia;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.interfaces.IConciliar;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EstornoTransfMesmaUnidadeFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.MoneyConverter;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "estornoTransfMesmaUnidadeControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-estorno-transferencia-financeira-mesma-unidade", pattern = "/estorno-transferencia-financeira-mesma-unidade/novo/", viewId = "/faces/financeiro/orcamentario/estornotransferenciamesmaunidade/edita.xhtml"),
    @URLMapping(id = "editar-estorno-transferencia-financeira-mesma-unidade", pattern = "/estorno-transferencia-financeira-mesma-unidade/editar/#{estornoTransfMesmaUnidadeControlador.id}/", viewId = "/faces/financeiro/orcamentario/estornotransferenciamesmaunidade/edita.xhtml"),
    @URLMapping(id = "ver-estorno-transferencia-financeira-mesma-unidade", pattern = "/estorno-transferencia-financeira-mesma-unidade/ver/#{estornoTransfMesmaUnidadeControlador.id}/", viewId = "/faces/financeiro/orcamentario/estornotransferenciamesmaunidade/visualizar.xhtml"),
    @URLMapping(id = "listar-estorno-transferencia-financeira-mesma-unidade", pattern = "/estorno-transferencia-financeira-mesma-unidade/listar/", viewId = "/faces/financeiro/orcamentario/estornotransferenciamesmaunidade/lista.xhtml")
})
public class EstornoTransfMesmaUnidadeControlador extends PrettyControlador<EstornoTransfMesmaUnidade> implements Serializable, CRUD {

    @EJB
    private EstornoTransfMesmaUnidadeFacade estornoTransfMesmaUnidadeFacade;
    private ConverterAutoComplete converterTransferenciaMesmaUnidade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private MoneyConverter moneyConverter;
    private ContaBancariaEntidade contaBancariaConcedida;
    private ContaBancariaEntidade contaBancariaRecebida;

    public EstornoTransfMesmaUnidadeControlador() {
        metadata = new EntidadeMetaData(EstornoTransfMesmaUnidade.class);
    }

    public EstornoTransfMesmaUnidadeFacade getFacade() {
        return estornoTransfMesmaUnidadeFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return estornoTransfMesmaUnidadeFacade;
    }

    @URLAction(mappingId = "novo-estorno-transferencia-financeira-mesma-unidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setUnidadeOrganizacional(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
        selecionado.setUnidadeOrganizacionalAdm(sistemaControlador.getUnidadeOrganizacionalAdministrativaCorrente());
        selecionado.setDataEstorno(sistemaControlador.getDataOperacao());

        if (estornoTransfMesmaUnidadeFacade.getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
            FacesUtil.addWarn(" Aviso! ", estornoTransfMesmaUnidadeFacade.getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
        }
    }

    @URLAction(mappingId = "editar-estorno-transferencia-financeira-mesma-unidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        setaContaBancariaRecebida();
        setaContaBancariaConcedida();
    }

    @URLAction(mappingId = "ver-estorno-transferencia-financeira-mesma-unidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        setaContaBancariaRecebida();
        setaContaBancariaRecebida();
    }

    public String setaEventoRetirada() {
        EstornoTransfMesmaUnidade ad = ((EstornoTransfMesmaUnidade) selecionado);
        ConfiguracaoTranferenciaMesmaUnidade configuracao;
        try {
            if (ad.getTransferenciaMesmaUnidade() != null) {
                configuracao = estornoTransfMesmaUnidadeFacade.getConfiguracaoTranferenciaMesmaUnidadeFacade().recuperaEvento(TipoLancamento.ESTORNO, ad.getTransferenciaMesmaUnidade().getResultanteIndependente(), ad.getTransferenciaMesmaUnidade().getTipoTransferencia(), OrigemTipoTransferencia.CONCEDIDO, ad.getDataEstorno());
                ad.setEventoContabilRetirada(configuracao.getEventoContabil());
                return ad.getEventoContabilRetirada().toString();
            } else {
                ad.setEventoContabilRetirada(null);
                return "Nenhum evento encontrado";
            }
        } catch (ExcecaoNegocioGenerica e) {
            return e.getMessage();
        }
    }

    public String setaEventoDeposito() {
        EstornoTransfMesmaUnidade ad = ((EstornoTransfMesmaUnidade) selecionado);
        ConfiguracaoTranferenciaMesmaUnidade configuracao;
        try {
            if (ad.getTransferenciaMesmaUnidade() != null) {
                configuracao = estornoTransfMesmaUnidadeFacade.getConfiguracaoTranferenciaMesmaUnidadeFacade().recuperaEvento(TipoLancamento.ESTORNO, ad.getTransferenciaMesmaUnidade().getResultanteIndependente(), ad.getTransferenciaMesmaUnidade().getTipoTransferencia(), OrigemTipoTransferencia.RECEBIDO, ad.getDataEstorno());
                ad.setEventoContabil(configuracao.getEventoContabil());
                return ad.getEventoContabil().toString();
            } else {
                ad.setEventoContabilRetirada(null);
                return "Nenhum evento encontrado";
            }
        } catch (ExcecaoNegocioGenerica e) {
            return e.getMessage();
        }
    }

    @Override
    public void salvar() {
        try {
            selecionado.realizarValidacoes();
            validarUnidade();
            if (operacao.equals(Operacoes.NOVO)) {
                estornoTransfMesmaUnidadeFacade.salvarNovo(selecionado);
            } else {
                estornoTransfMesmaUnidadeFacade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada(" Registro salvo com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            estornoTransfMesmaUnidadeFacade.getSingletonConcorrenciaContabil().tratarExceptionConcorrenciaSaldos(ex);
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }


    private void validarUnidade() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getTransferenciaMesmaUnidade() != null) {
            estornoTransfMesmaUnidadeFacade.getHierarquiaOrganizacionalFacade()
                .validarUnidadesIguais(selecionado.getTransferenciaMesmaUnidade().getUnidadeOrganizacional(), selecionado.getUnidadeOrganizacional()
                    , ve
                    , "A Unidade Organizacional do estorno de transferência deve ser a mesma da transferência.");
        }
        ve.lancarException();
    }

    public List<TransferenciaMesmaUnidade> completaTransferencia(String parte) {
        return estornoTransfMesmaUnidadeFacade.getTransferenciaMesmaUnidadeFacade().listaFiltrandoTransferenciaPorUnidadeLogadaESaldoDisponivel(parte.trim(), sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente(), sistemaControlador.getExercicioCorrente());
    }

    public ConverterAutoComplete getConverterTransferenciaMesmaUnidade() {
        if (converterTransferenciaMesmaUnidade == null) {
            converterTransferenciaMesmaUnidade = new ConverterAutoComplete(TransferenciaMesmaUnidade.class, estornoTransfMesmaUnidadeFacade.getTransferenciaMesmaUnidadeFacade());
        }
        return converterTransferenciaMesmaUnidade;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public void validaDataLancamento(FacesContext context, UIComponent component, Object value) {
        Date dataSelecionada = (Date) value;
        FacesMessage message = new FacesMessage();
        if (dataSelecionada.after(new Date())) {
            message.setDetail("Data invalida!");
            message.setSummary("Não é possivel executar lançamento com data Posterior a data de Hoje! ");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }

    public void selecionarTransferencia(ActionEvent evento) {
        ((EstornoTransfMesmaUnidade) selecionado).setTransferenciaMesmaUnidade((TransferenciaMesmaUnidade) evento.getComponent().getAttributes().get("objeto"));
        definirValores();
    }

    public String actionSelecionar() {
        return "edita";
    }

    @Override
    public String getCaminhoPadrao() {
        return "/estorno-transferencia-financeira-mesma-unidade/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void setaContaBancariaConcedida() {
        try {
            contaBancariaConcedida = selecionado.getTransferenciaMesmaUnidade().getSubContaRetirada().getContaBancariaEntidade();
        } catch (Exception ex) {

        }
    }

    public void setaContaBancariaRecebida() {
        try {
            contaBancariaRecebida = selecionado.getTransferenciaMesmaUnidade().getSubContaDeposito().getContaBancariaEntidade();
        } catch (Exception ex) {

        }
    }

    public void definirValores() {
        setaContaBancariaConcedida();
        setaContaBancariaRecebida();
        selecionado.setValor(selecionado.getTransferenciaMesmaUnidade().getSaldo());
        selecionado.setHistorico(selecionado.getTransferenciaMesmaUnidade().getHistorico());
    }

    public void redirecionaParaLista() {
        FacesUtil.redirecionamentoInterno("/estorno-transferencia-financeira-mesma-unidade/listar/");
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
