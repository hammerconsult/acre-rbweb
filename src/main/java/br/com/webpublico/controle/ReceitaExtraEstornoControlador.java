/*
 * Codigo gerado automaticamente em Mon Dec 26 14:45:38 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.NotaExecucaoOrcamentaria;
import br.com.webpublico.enums.ModuloTipoDoctoOficial;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.StatusPagamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ReceitaExtraEstornoFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import com.google.common.base.Preconditions;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "receitaExtraEstornoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-receita-extra-estorno", pattern = "/receita-extra-estorno/novo/", viewId = "/faces/financeiro/extraorcamentario/receitaextraestorno/edita.xhtml"),
    @URLMapping(id = "editar-receita-extra-estorno", pattern = "/receita-extra-estorno/editar/#{receitaExtraEstornoControlador.id}/", viewId = "/faces/financeiro/extraorcamentario/receitaextraestorno/edita.xhtml"),
    @URLMapping(id = "ver-receita-extra-estorno", pattern = "/receita-extra-estorno/ver/#{receitaExtraEstornoControlador.id}/", viewId = "/faces/financeiro/extraorcamentario/receitaextraestorno/visualizar.xhtml"),
    @URLMapping(id = "listar-receita-extra-estorno", pattern = "/receita-extra-estorno/listar/", viewId = "/faces/financeiro/extraorcamentario/receitaextraestorno/lista.xhtml")
})
public class ReceitaExtraEstornoControlador extends PrettyControlador<ReceitaExtraEstorno> implements Serializable, CRUD {

    @EJB
    private ReceitaExtraEstornoFacade receitaExtraEstornoFacade;
    private BigDecimal saldoReceitaExtraRecuperado;
    private BigDecimal saldoContaFinanceiraRecuperado;
    private BigDecimal saldoContaExtraorcamentariaRecuperado;

    public ReceitaExtraEstornoControlador() {
        super(ReceitaExtraEstorno.class);
    }

    public String actionSelecionar() {
        return "edita";
    }

    @Override
    public String getCaminhoPadrao() {
        return "/receita-extra-estorno/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "ver-receita-extra-estorno", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditarVer();
    }

    @URLAction(mappingId = "editar-receita-extra-estorno", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditarVer();
    }

    @URLAction(mappingId = "novo-receita-extra-estorno", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        ReceitaExtraEstorno ree = ((ReceitaExtraEstorno) selecionado);
        ree.setUsuarioSistema(getUsuarioSistema());
        ree.setDataEstorno(receitaExtraEstornoFacade.getSistemaFacade().getDataOperacao());
        ree.setUnidadeOrganizacional(receitaExtraEstornoFacade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente());
        ree.setUnidadeOrganizacionalAdm(receitaExtraEstornoFacade.getSistemaFacade().getUnidadeOrganizacionalAdministrativaCorrente());
        ree.setExercicio(receitaExtraEstornoFacade.getSistemaFacade().getExercicioCorrente());
        ree.setStatusPagamento(StatusPagamento.EFETUADO);
        saldoReceitaExtraRecuperado = BigDecimal.ZERO;
        saldoContaFinanceiraRecuperado = BigDecimal.ZERO;
        saldoContaExtraorcamentariaRecuperado = BigDecimal.ZERO;

        if (receitaExtraEstornoFacade.getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
            FacesUtil.addWarn("Aviso! ", receitaExtraEstornoFacade.getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
        }
    }

    public void recuperaEditarVer() {
        selecionado.setUnidadeOrganizacional(receitaExtraEstornoFacade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente());
        selecionado.setUnidadeOrganizacionalAdm(receitaExtraEstornoFacade.getSistemaFacade().getUnidadeOrganizacionalAdministrativaCorrente());
        recuperaConfiguracaoEventoContabil();
        recuperaSaldos();
    }

    @Override
    public void salvar() {
        try {
            validarEstornoReceitaExtra();
            if (operacao.equals(Operacoes.NOVO)) {
                selecionado = receitaExtraEstornoFacade.salvarNovoEstorno(selecionado);
                FacesUtil.addOperacaoRealizada("Registro " + selecionado + " salvo com sucesso.");
                redirecionarParaVerOrEditar(selecionado.getId(), "editar");
            } else {
                receitaExtraEstornoFacade.salvar(selecionado);
                FacesUtil.addOperacaoRealizada("Registro " + selecionado + " alterado com sucesso.");
                redireciona();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            receitaExtraEstornoFacade.getSingletonConcorrenciaContabil().desbloquear(selecionado.getReceitaExtra());
            receitaExtraEstornoFacade.getSingletonConcorrenciaContabil().tratarExceptionConcorrenciaSaldos(ex);
        } catch (Exception e) {
            receitaExtraEstornoFacade.getSingletonConcorrenciaContabil().desbloquear(selecionado.getReceitaExtra());
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void validarEstornoReceitaExtra() {
        selecionado.realizarValidacoes();
        validaCampos();
    }

    public void validaCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getValor().compareTo(new BigDecimal(BigInteger.ZERO)) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O valor do estorno deve ser maior que zero(0).");
        }
        if (isOperacaoNovo() && selecionado.getValor().compareTo(selecionado.getReceitaExtra().getSaldo()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O valor a ser estornado é maior que o saldo disponível na receita extraorçamentária.");
        }
        if (selecionado.getDataEstorno().compareTo(selecionado.getReceitaExtra().getDataReceita()) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" A data do Estorno da Receita Extra deve ser maior ou igual a data da Receita Extra selecionada. Data da Receita Extra: <b>" + DataUtil.getDataFormatada(selecionado.getReceitaExtra().getDataReceita()) + "</b>.");
        }
        if (selecionado.getReceitaExtra() != null) {
            receitaExtraEstornoFacade.getReceitaExtraFacade().getHierarquiaOrganizacionalFacade()
                .validarUnidadesIguais(selecionado.getReceitaExtra().getUnidadeOrganizacional(), selecionado.getUnidadeOrganizacional()
                    , ve
                    , "A Unidade Organizacional do estorno de receita extra deve ser a mesma da receita extra.");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    @Override
    public AbstractFacade getFacede() {
        return receitaExtraEstornoFacade;
    }

    public List<ReceitaExtra> completaReceitaExtra(String parte) {
        return receitaExtraEstornoFacade.getReceitaExtraFacade().listaFiltrandoReceitasAbertasNaoConsignadas(parte.trim(), receitaExtraEstornoFacade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente());
    }

    public BigDecimal getSaldoReceitaExtra() {
        ReceitaExtraEstorno ree = ((ReceitaExtraEstorno) selecionado);
        BigDecimal saldo = BigDecimal.ZERO;
        if (ree.getReceitaExtra() != null) {
            saldo = ree.getReceitaExtra().getSaldo();
        }
        return saldo;
    }

    public BigDecimal getSaldoContaFinanceira() {
        ReceitaExtraEstorno ree = ((ReceitaExtraEstorno) selecionado);
        BigDecimal saldo = BigDecimal.ZERO;
        if (selecionado.getReceitaExtra().getFonteDeRecursos() != null) {
            SaldoSubConta saldoSubConta = receitaExtraEstornoFacade.getSaldoSubContaFacade().recuperaUltimoSaldoSubContaPorData(ree.getUnidadeOrganizacional(), ree.getReceitaExtra().getSubConta(), ree.getReceitaExtra().getContaDeDestinacao(), ree.getDataEstorno());
            if (saldoSubConta != null && saldoSubConta.getId() != null) {
                saldo = saldoSubConta.getTotalCredito().subtract(saldoSubConta.getTotalDebito());
            }
        }
        return saldo;
    }

    public BigDecimal getSaldoContaExtraorcamentaria() {
        BigDecimal saldo = BigDecimal.ZERO;
        SaldoExtraorcamentario saldoExtraorcamentario = receitaExtraEstornoFacade.getSaldoExtraorcamentarioFacade().recuperaUltimoSaldoPorData(selecionado.getDataEstorno(), selecionado.getReceitaExtra().getContaExtraorcamentaria(), selecionado.getReceitaExtra().getContaDeDestinacao(), selecionado.getUnidadeOrganizacional());
        if (saldoExtraorcamentario != null && saldoExtraorcamentario.getId() != null) {
            saldo = saldoExtraorcamentario.getValor();
            return saldo;
        }
        return saldo;
    }

    public void validaDataReceitaExtraEstorno(FacesContext context, UIComponent component, Object value) {
        FacesMessage message = new FacesMessage();
        Date data = (Date) value;
        ReceitaExtraEstorno ree = ((ReceitaExtraEstorno) selecionado);
        Calendar dataRecEst = Calendar.getInstance();
        dataRecEst.setTime(data);
        Integer ano = receitaExtraEstornoFacade.getSistemaFacade().getExercicioCorrente().getAno();
        if (ree.getReceitaExtra() != null) {
            if (data.before(ree.getReceitaExtra().getDataReceita())) {
                message.setDetail("Data não pode ser menor que da Receita Extra!");
                message.setSummary("Data não pode ser menor que da Receita Extra!");
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(message);
            }
            if (dataRecEst.get(Calendar.YEAR) != ano) {
                message.setDetail("Ano diferente do exercício corrente!");
                message.setSummary("Ano diferente do exercício corrente!");
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(message);
            }
        }
    }

    public Boolean getVerificaEdicao() {
        if (operacao.equals(Operacoes.EDITAR)) {
            return true;
        } else {
            return false;
        }
    }

    public void selecionouReceitaExtra(SelectEvent evento) {
        ((ReceitaExtraEstorno) selecionado).setReceitaExtra(((ReceitaExtra) evento.getObject()));
        recuperaSaldos();
        recuperaConfiguracaoEventoContabil();
        selecionado.setValor(selecionado.getReceitaExtra().getSaldo());
        selecionado.setComplementoHistorico(selecionado.getReceitaExtra().getComplementoHistorico());
    }

    public void recuperaSaldos() {
        if (((ReceitaExtraEstorno) selecionado).getDataEstorno() != null && ((ReceitaExtraEstorno) selecionado).getReceitaExtra() != null) {
            saldoReceitaExtraRecuperado = getSaldoReceitaExtra();
            saldoContaFinanceiraRecuperado = getSaldoContaFinanceira();
            saldoContaExtraorcamentariaRecuperado = getSaldoContaExtraorcamentaria();
            selecionado.setComplementoHistorico(selecionado.getReceitaExtra().getComplementoHistorico());

        }
    }

    private void recuperaConfiguracaoEventoContabil() {
        ReceitaExtraEstorno rec = ((ReceitaExtraEstorno) selecionado);
        try {
            ConfigReceitaExtra configReceitaExtra = receitaExtraEstornoFacade.getReceitaExtraFacade().getConfigReceitaExtraFacade().recuperaConfiguracaoEventoContabilEstor(rec.getReceitaExtra());
            Preconditions.checkNotNull(configReceitaExtra, "Configuração contábil não encontrada para os parametros informados.");
            rec.setEventoContabil(configReceitaExtra.getEventoContabil());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida("Erro ao tentar recuperar o Evento Contábil: " + e.getMessage());
        }
    }

    public void selecionarReceita(ActionEvent evento) {
        ((ReceitaExtraEstorno) selecionado).setReceitaExtra((ReceitaExtra) evento.getComponent().getAttributes().get("objeto"));
        recuperaConfiguracaoEventoContabil();
        recuperaSaldos();
        selecionado.setValor(selecionado.getReceitaExtra().getSaldo());
    }

    public ReceitaExtraEstornoFacade getFacade() {
        return receitaExtraEstornoFacade;
    }

    public UsuarioSistema getUsuarioSistema() {
        return receitaExtraEstornoFacade.getSistemaFacade().getUsuarioCorrente();
    }

    public BigDecimal getSaldoContaExtraorcamentariaRecuperado() {
        return saldoContaExtraorcamentariaRecuperado;
    }

    public BigDecimal getSaldoContaFinanceiraRecuperado() {
        return saldoContaFinanceiraRecuperado;
    }

    public BigDecimal getSaldoReceitaExtraRecuperado() {
        return saldoReceitaExtraRecuperado;
    }

    public void gerarNotaOrcamentaria(boolean isDownload) {
        try {
            NotaExecucaoOrcamentaria notaExecucaoOrcamentaria = receitaExtraEstornoFacade.buscarNotaReceitaExtraEstorno(selecionado);
            if (notaExecucaoOrcamentaria != null) {
                receitaExtraEstornoFacade.getNotaOrcamentariaFacade().imprimirDocumentoOficial(notaExecucaoOrcamentaria, ModuloTipoDoctoOficial.NOTA_RECEITA_EXTRA_ESTORNO, selecionado, isDownload);
            }
        } catch (Exception ex) {
            logger.error("Erro ao gerar nota de estorno de receita extra: ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }
}
