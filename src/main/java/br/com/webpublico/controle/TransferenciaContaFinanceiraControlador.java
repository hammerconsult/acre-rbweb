/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.interfaces.IConciliar;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.TransferenciaContaFinanceiraFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
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
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Claudio
 */
@ManagedBean(name = "transferenciaContaFinanceiraControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-transferencia-financeira", pattern = "/transferencia-financeira/novo/", viewId = "/faces/financeiro/orcamentario/transferenciacotafinanceira/edita.xhtml"),
    @URLMapping(id = "editar-transferencia-financeira", pattern = "/transferencia-financeira/editar/#{transferenciaContaFinanceiraControlador.id}/", viewId = "/faces/financeiro/orcamentario/transferenciacotafinanceira/edita.xhtml"),
    @URLMapping(id = "ver-transferencia-financeira", pattern = "/transferencia-financeira/ver/#{transferenciaContaFinanceiraControlador.id}/", viewId = "/faces/financeiro/orcamentario/transferenciacotafinanceira/visualizar.xhtml"),
    @URLMapping(id = "listar-transferencia-financeira", pattern = "/transferencia-financeira/listar/", viewId = "/faces/financeiro/orcamentario/transferenciacotafinanceira/lista.xhtml")
})
public class TransferenciaContaFinanceiraControlador extends PrettyControlador<TransferenciaContaFinanceira> implements Serializable, CRUD, IConciliar {

    @EJB
    private TransferenciaContaFinanceiraFacade transferenciaContaFinanceiraFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConverterAutoComplete converterFinalidadePagamento;
    private ConverterAutoComplete converterContaFinanceira;
    private ConverterAutoComplete converterContaBancaria;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private BigDecimal saldoContaFinanceiraConcedida;
    private ContaBancariaEntidade contaBancariaEntidadeConcedida;
    private ContaBancariaEntidade contaBancariaEntidadeRecebida;

    public TransferenciaContaFinanceiraControlador() {
        super(TransferenciaContaFinanceira.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return transferenciaContaFinanceiraFacade;
    }

    public void setSelecionado(TransferenciaContaFinanceira selecionado) {
        this.selecionado = selecionado;
    }

    public void recuperarEventoRetirada() {
        TransferenciaContaFinanceira ad = ((TransferenciaContaFinanceira) selecionado);
        ConfiguracaoTranferenciaFinanceira configuracao;
        ad.setEventoContabilRetirada(null);
        try {
            if (ad.getResultanteIndependente() != null
                && ad.getTipoTransferenciaFinanceira() != null) {
                configuracao = transferenciaContaFinanceiraFacade.getConfiguracaoTranferenciaFinanceiraFacade().recuperaEvento(TipoLancamento.NORMAL, ad.getTipoTransferenciaFinanceira(), OrigemTipoTransferencia.CONCEDIDO, ad.getResultanteIndependente(), ad.getDataTransferencia());
                ad.setEventoContabilRetirada(configuracao.getEventoContabil());
            }
        } catch (ExcecaoNegocioGenerica e) {

        }
    }

    public void recuperarEventoDeposito() {
        TransferenciaContaFinanceira ad = ((TransferenciaContaFinanceira) selecionado);
        ConfiguracaoTranferenciaFinanceira configuracao;
        ad.setEventoContabil(null);
        try {
            if (ad.getResultanteIndependente() != null
                && ad.getTipoTransferenciaFinanceira() != null) {
                configuracao = transferenciaContaFinanceiraFacade.getConfiguracaoTranferenciaFinanceiraFacade().recuperaEvento(TipoLancamento.NORMAL, ad.getTipoTransferenciaFinanceira(), OrigemTipoTransferencia.RECEBIDO, ad.getResultanteIndependente(), ad.getDataTransferencia());
                ad.setEventoContabil(configuracao.getEventoContabil());
            }
        } catch (ExcecaoNegocioGenerica e) {

        }
    }

    public void excluirSelecionado() {
        try {
            transferenciaContaFinanceiraFacade.remover(selecionado);
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Operação não Permitida! ", " O Registro não pode ser excluido, o mesmo possui dependencias."));
        }
    }

    public List<TransferenciaContaFinanceira> getLista() {
        return transferenciaContaFinanceiraFacade.listaDecrescente();
    }

    public List<SelectItem> getValoresResultanteIndependente() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        lista.add(new SelectItem(ResultanteIndependente.RESULTANTE_EXECUCAO_ORCAMENTARIA, ResultanteIndependente.RESULTANTE_EXECUCAO_ORCAMENTARIA.getDescricao()));
        lista.add(new SelectItem(ResultanteIndependente.INDEPENDENTE_EXECUCAO_ORCAMENTARIA, ResultanteIndependente.INDEPENDENTE_EXECUCAO_ORCAMENTARIA.getDescricao()));
        return lista;
    }

    public List<SelectItem> getTipoDocumento() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoDocPagto object : TipoDocPagto.values()) {
            if (!TipoDocPagto.DEPJU.equals(object)) {
                toReturn.add(new SelectItem(object, object.getDescricao()));
            }
        }
        return Util.ordenaSelectItem(toReturn);
    }

    public List<SelectItem> getTipoOperacaoPagamento() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoOperacaoPagto tipoOperacaoPagto : TipoOperacaoPagto.values()) {
            toReturn.add(new SelectItem(tipoOperacaoPagto, tipoOperacaoPagto.getDescricao()));
        }
        return Util.ordenaSelectItem(toReturn);
    }

    public List<SelectItem> getTiposTransferencia() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        lista.add(new SelectItem(null, ""));
        for (TipoTransferenciaFinanceira tipo : TipoTransferenciaFinanceira.values()) {
            lista.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return Util.ordenaSelectItem(lista);
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacional(String parte) {
        boolean temUnidade = false;
        List<HierarquiaOrganizacional> lista = transferenciaContaFinanceiraFacade.getHierarquiaOrganizacionalFacade().filtraPorNivel(parte, "3", TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), sistemaControlador.getDataOperacao());
        if (hierarquiaOrganizacional != null) {
            for (HierarquiaOrganizacional h : lista) {
                if (h.getSubordinada().equals(hierarquiaOrganizacional.getSubordinada())) {
                    temUnidade = true;
                }
            }
            if (temUnidade == true) {
                return lista;
            } else {
                if (hierarquiaOrganizacional.getSubordinada() != null) {
                    String str = hierarquiaOrganizacional.getSubordinada().getDescricao() + hierarquiaOrganizacional.getCodigo();
                    if (str.toLowerCase().contains(parte.toLowerCase())) {
                        lista.add(hierarquiaOrganizacional);
                    }
                }
                return lista;
            }
        } else {
            return lista;
        }
    }

    public Boolean verificaEdicao() {
        if (operacao.equals(Operacoes.EDITAR)) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean getHabilitaEditar() {
        if (selecionado.getStatusPagamento() == null) {
            return true;
        }
        if (StatusPagamento.INDEFERIDO.equals(selecionado.getStatusPagamento())
            || StatusPagamento.DEFERIDO.equals(selecionado.getStatusPagamento())
            || StatusPagamento.ABERTO.equals(selecionado.getStatusPagamento())) {
            return false;
        }
        return true;
    }

    public Boolean getHabilitaBotaoEditar() {
        TransferenciaContaFinanceira transf = ((TransferenciaContaFinanceira) selecionado);
        if (transf.getStatusPagamento() == null) {
            return false;
        }

        if (transf.getStatusPagamento().equals(StatusPagamento.INDEFERIDO)
            || transf.getStatusPagamento().equals(StatusPagamento.DEFERIDO)
            || transf.getStatusPagamento().equals(StatusPagamento.ABERTO)) {
            return true;
        }
        return false;
    }

    public Boolean getHabilitaExcluir() {
        TransferenciaContaFinanceira transf = ((TransferenciaContaFinanceira) selecionado);
        Boolean controle = false;
        if (transf.getStatusPagamento() == null) {
            return controle;
        }
        if (transf.getStatusPagamento().equals(StatusPagamento.ABERTO)) {
            controle = true;
        }
        return controle;
    }

    public Boolean habilitaBotaoEditarNoLista(TransferenciaContaFinanceira transf) {
        if (transf.getStatusPagamento() == null) {
            return false;
        }
        if (transf.getStatusPagamento().equals(StatusPagamento.INDEFERIDO)
            || transf.getStatusPagamento().equals(StatusPagamento.DEFERIDO)
            || transf.getStatusPagamento().equals(StatusPagamento.ABERTO)) {
            return true;
        }
        return false;
    }

    public List<FinalidadePagamento> completaFinalidadePagamento(String parte) {
        return transferenciaContaFinanceiraFacade.getFinalidadePagamentoFacade().listaFiltrando(parte.trim(), "codigo", "codigoOB", "descricao");
    }

    public ConverterAutoComplete getConverterFinalidadePagamento() {
        if (converterFinalidadePagamento == null) {
            converterFinalidadePagamento = new ConverterAutoComplete(FinalidadePagamento.class, transferenciaContaFinanceiraFacade.getFinalidadePagamentoFacade());
        }
        return converterFinalidadePagamento;
    }

    public ConverterAutoComplete getConverterContaFinanceira() {
        if (converterContaFinanceira == null) {
            converterContaFinanceira = new ConverterAutoComplete(SubConta.class, transferenciaContaFinanceiraFacade.getSubContaFacade());
        }
        return converterContaFinanceira;
    }

    public ConverterAutoComplete getConverterContaBancaria() {
        if (converterContaBancaria == null) {
            converterContaBancaria = new ConverterAutoComplete(ContaBancariaEntidade.class, transferenciaContaFinanceiraFacade.getContaBancariaEntidadeFacade());
        }
        return converterContaBancaria;
    }

    public void setaNullContaFinanceiraConcedida() {
        selecionado.setSubContaRetirada(null);
    }

    public List<SelectItem> getContasDeDestinacaoRecebida() {
        List<SelectItem> dados = Lists.newArrayList();
        if (selecionado.getSubContaDeposito() != null && selecionado.getSubContaDeposito().getId() != null) {
            selecionado.setSubContaDeposito(transferenciaContaFinanceiraFacade.getSubContaFacade().recuperar(selecionado.getSubContaDeposito().getId()));
            for (SubContaFonteRec subContaFonteRec : selecionado.getSubContaDeposito().getSubContaFonteRecs()) {
                if (subContaFonteRec.getContaDeDestinacao().getExercicio().equals(sistemaControlador.getExercicioCorrente())) {
                    dados.add(new SelectItem(subContaFonteRec.getContaDeDestinacao(), subContaFonteRec.getContaDeDestinacao().toString()));
                }
            }
        }
        return Util.ordenaSelectItem(dados);
    }

    public void atualizarFonteRecebida() {
        if (selecionado.getContaDeDestinacaoDeposito() != null) {
            selecionado.setFonteDeRecursosDeposito(selecionado.getContaDeDestinacaoDeposito().getFonteDeRecursos());
        }
    }

    public List<SelectItem> getContasDeDestinacaoConcedida() {
        List<SelectItem> dados = Lists.newArrayList();
        if (selecionado.getSubContaRetirada() != null &&  selecionado.getSubContaRetirada().getId() != null) {
            selecionado.setSubContaRetirada(transferenciaContaFinanceiraFacade.getSubContaFacade().recuperar(selecionado.getSubContaRetirada().getId()));
            for (SubContaFonteRec subContaFonteRec : selecionado.getSubContaRetirada().getSubContaFonteRecs()) {
                if (subContaFonteRec.getContaDeDestinacao().getExercicio().equals(sistemaControlador.getExercicioCorrente())) {
                    dados.add(new SelectItem(subContaFonteRec.getContaDeDestinacao(), subContaFonteRec.getContaDeDestinacao().toString()));
                }
            }
        }
        return Util.ordenaSelectItem(dados);
    }

    public void atualizarFonteConcedida() {
        if (selecionado.getContaDeDestinacaoRetirada() != null) {
            selecionado.setFonteDeRecursosRetirada(selecionado.getContaDeDestinacaoRetirada().getFonteDeRecursos());
        }
    }

    public void atribuirContaBancariaConcedida() {
        try {
            contaBancariaEntidadeConcedida = selecionado.getSubContaRetirada().getContaBancariaEntidade();
            List<SelectItem> contasDeDestinacaoConcedida = getContasDeDestinacaoConcedida();
            if (!contasDeDestinacaoConcedida.isEmpty()) {
                selecionado.setContaDeDestinacaoRetirada((ContaDeDestinacao) contasDeDestinacaoConcedida.get(0).getValue());
                selecionado.setFonteDeRecursosRetirada(selecionado.getContaDeDestinacaoRetirada().getFonteDeRecursos());
            }
        } catch (Exception e) {
            logger.error("Erro ao atribuir a conta bancaria concedida: {}", e);
        }
    }

    public void atribuirContaBancariaRecebida() {
        try {
            contaBancariaEntidadeRecebida = selecionado.getSubContaDeposito().getContaBancariaEntidade();
            List<SelectItem> contasDeDestinacaoRecebida = getContasDeDestinacaoRecebida();
            if (!contasDeDestinacaoRecebida.isEmpty()) {
                selecionado.setContaDeDestinacaoDeposito((ContaDeDestinacao) contasDeDestinacaoRecebida.get(0).getValue());
                selecionado.setFonteDeRecursosDeposito(selecionado.getContaDeDestinacaoDeposito().getFonteDeRecursos());
            }
        } catch (Exception e) {
            logger.error("Erro ao atribuir a conta bancaria recebida: {}", e);
        }
    }

    private void validaContaFinanceiraEContasDeDestinacaoIguais() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getValor() == null || selecionado.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Valor deve ser maior que zero." + "<b> " + Util.formataValor(selecionado.getValor()) + "</b>");
        }
        if (selecionado.getContaDeDestinacaoRetirada().equals(selecionado.getContaDeDestinacaoDeposito())) {
            if (selecionado.getSubContaRetirada().equals(selecionado.getSubContaDeposito())) {
                if (selecionado.getUnidadeOrganizacional().equals(selecionado.getUnidadeOrgConcedida())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida(" Não é possivel efetuar uma transferência para a mesma Conta Financeira e mesma Fonte de Recurso.");
                }
            }
        }
        ve.lancarException();
    }

    public Boolean isMesmaContaBancaria() {
        if (selecionado.getSubContaDeposito() != null && selecionado.getSubContaRetirada() != null) {
            if (selecionado.getSubContaDeposito().getContaBancariaEntidade().equals(selecionado.getSubContaRetirada().getContaBancariaEntidade())) {
                return true;
            }
        }
        return false;
    }

    public void salvar() {
        try {
            selecionado.realizarValidacoes();
            validarDatasAoConciliar();
            validaContaFinanceiraEContasDeDestinacaoIguais();
            validarIntegracaoBancariaParaMesmaContaBancaria();
            if (operacao.equals(Operacoes.NOVO)) {
                transferenciaContaFinanceiraFacade.salvarNovo(selecionado);
            } else {
                transferenciaContaFinanceiraFacade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada("Registro salvo com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            transferenciaContaFinanceiraFacade.getSingletonConcorrenciaContabil().tratarExceptionConcorrenciaSaldos(ex);
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void validarDatasAoConciliar() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDataConciliacao() != null) {
            if (Util.getDataHoraMinutoSegundoZerado(selecionado.getDataConciliacao()).before(Util.getDataHoraMinutoSegundoZerado(selecionado.getDataTransferencia()))) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" Data de conciliação concedida deve ser superior a data da transferência.");

            }
        }
        if (selecionado.getRecebida() != null) {
            if (Util.getDataHoraMinutoSegundoZerado(selecionado.getRecebida()).before(Util.getDataHoraMinutoSegundoZerado(selecionado.getDataTransferencia()))) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" Data de conciliação recebida deve ser superior a data da transferência.");
            }
        }
        ve.lancarException();
        validarDataConciliacao();
    }

    private void validarIntegracaoBancariaParaMesmaContaBancaria() {
        if (!isMesmaContaBancaria()) {
            if (selecionado.getTipoDocumento() == null) {
                throw new ExcecaoNegocioGenerica(" O campo Tipo de Documento deve ser informado!");
            }
            if (selecionado.getTipoOperacaoPagto() == null) {
                throw new ExcecaoNegocioGenerica(" O campo Tipo de Operação deve ser informado!");
            }
            if (selecionado.getFinalidadePagamento() == null && !selecionado.getTipoOperacaoPagto().equals(TipoOperacaoPagto.NAO_APLICAVEL)) {
                throw new ExcecaoNegocioGenerica(" O campo Finalidade do Pagamento deve ser informado!");
            }
        }
    }

    public BigDecimal recuperaSaldoContaFinanceiraConcedida() {
        BigDecimal saldo = BigDecimal.ZERO;
        if (selecionado.getContaDeDestinacaoRetirada() != null && selecionado.getUnidadeOrgConcedida() != null && selecionado.getSubContaRetirada() != null && selecionado.getDataTransferencia() != null) {
            SaldoSubConta saldoSubConta = transferenciaContaFinanceiraFacade.getSaldoSubContaFacade().recuperaUltimoSaldoSubContaPorData(selecionado.getUnidadeOrgConcedida(), selecionado.getSubContaRetirada(), selecionado.getContaDeDestinacaoRetirada(), selecionado.getDataTransferencia());
            if (saldoSubConta != null && saldoSubConta.getId() != null) {
                saldo = saldoSubConta.getTotalCredito().subtract(saldoSubConta.getTotalDebito());
            }
        }
        return saldoContaFinanceiraConcedida = saldo;
    }

    public void adicionaUnidadeHierarquiaSelecionada() {
        if (hierarquiaOrganizacional.getSubordinada() != null) {
            selecionado.setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
        }
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

    @URLAction(mappingId = "novo-transferencia-financeira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setUnidadeOrgConcedida(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
        selecionado.setUnidadeOrganizacionalAdm(sistemaControlador.getUnidadeOrganizacionalAdministrativaCorrente());
        selecionado.setExercicio(sistemaControlador.getExercicioCorrente());
        selecionado.setDataTransferencia(sistemaControlador.getDataOperacao());
        selecionado.setTipoDocumento(TipoDocPagto.ORDEMPAGAMENTO);
        selecionado.setResultanteIndependente(ResultanteIndependente.RESULTANTE_EXECUCAO_ORCAMENTARIA);
        hierarquiaOrganizacional = new HierarquiaOrganizacional();
        saldoContaFinanceiraConcedida = new BigDecimal(BigInteger.ZERO);

        if (transferenciaContaFinanceiraFacade.getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
            FacesUtil.addWarn("Aviso! ", transferenciaContaFinanceiraFacade.getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
        }
    }

    @URLAction(mappingId = "ver-transferencia-financeira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditaVer();
    }

    @URLAction(mappingId = "editar-transferencia-financeira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditaVer();
        List<SubContaUniOrg> unidadesOrganizacionais = selecionado.getSubContaDeposito().getUnidadesOrganizacionais();
        UnidadeOrganizacional organizacional = null;
        Exercicio exercicioDaData = getExercicioDaData();
        for (SubContaUniOrg subContaUniOrg : unidadesOrganizacionais) {
            if (subContaUniOrg.getExercicio().equals(exercicioDaData)) {
                organizacional = subContaUniOrg.getUnidadeOrganizacional();
            }
        }
        if (organizacional != null) {
            hierarquiaOrganizacional = transferenciaContaFinanceiraFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(selecionado.getDataTransferencia(), organizacional, TipoHierarquiaOrganizacional.ORCAMENTARIA);
        }
    }

    public void recuperaEditaVer() {
        selecionado = transferenciaContaFinanceiraFacade.recuperar(selecionado.getId());
        contaBancariaEntidadeConcedida = selecionado.getSubContaRetirada().getContaBancariaEntidade();
        contaBancariaEntidadeRecebida = selecionado.getSubContaDeposito().getContaBancariaEntidade();
        selecionado.setTipoDocumento(TipoDocPagto.ORDEMPAGAMENTO);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/transferencia-financeira/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<ContaBancariaEntidade> completaContaBancariaEntidade(String parte) {
        try {
            return transferenciaContaFinanceiraFacade.getContaBancariaEntidadeFacade().listaFiltrandoAtivaPorUnidade(parte.trim(), sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente(), getSistemaControlador().getExercicioCorrente(), selecionado.getFonteDeRecursosRetirada(), null, null);
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public List<SubConta> completaContaFinanceira(String parte) {
        try {
            return transferenciaContaFinanceiraFacade.getSubContaFacade().buscarContasFinanceirasPorBancariaEntidadeOuTodosPorUnidade(parte.trim(), contaBancariaEntidadeConcedida, sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente(), getSistemaControlador().getExercicioCorrente(), selecionado.getFonteDeRecursosRetirada(), null, null, true);
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public Boolean mostrarBotaoBaixar() {
        if (selecionado.getId() != null
            && selecionado.getStatusPagamento().equals(StatusPagamento.DEFERIDO)) {
            return true;
        }
        return false;
    }

    public Boolean mostrarBotaoEstornarBaixar() {
        if (selecionado.getId() != null
            && selecionado.getStatusPagamento().equals(StatusPagamento.PAGO)) {
            return true;
        }
        return false;
    }

    @Override
    public void baixar() {
        try {
            selecionado.realizarValidacoes();
            validarIntegracaoBancariaParaMesmaContaBancaria();
            validarDataConciliacao();
            transferenciaContaFinanceiraFacade.baixar(selecionado);
            FacesUtil.addOperacaoRealizada(" A Transferência Financeira N° <b>" + selecionado.getNumero() + "</b> foi baixada com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoRealizada(ex.getMessage());
        }
    }

    private void validarDataConciliacao() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDataConciliacao() != null && DataUtil.getAno(selecionado.getDataTransferencia()).compareTo(DataUtil.getAno(selecionado.getDataConciliacao())) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de conciliação está com o exercício diferente da data do registro.");
        }
        if (selecionado.getRecebida() != null && DataUtil.getAno(selecionado.getDataTransferencia()).compareTo(DataUtil.getAno(selecionado.getRecebida())) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de conciliação está com o exercício diferente da data do registro.");
        }
        ve.lancarException();
    }

    @Override
    public void estornarBaixa() {
        try {
            selecionado.realizarValidacoes();
            validarIntegracaoBancariaParaMesmaContaBancaria();
            transferenciaContaFinanceiraFacade.estornarBaixa(selecionado);
            FacesUtil.addOperacaoRealizada(" A Transferência Financeira N° <b>" + selecionado.getNumero() + "</b> foi estornada a baixa com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoRealizada(ex.getMessage());
        }
    }

    public void redirecionaParaLista() {
        FacesUtil.redirecionamentoInterno("/transferencia-financeira/listar/");
    }

    private Exercicio getExercicioDaData() {
        Calendar c = Calendar.getInstance();
        c.setTime(selecionado.getDataTransferencia());
        return transferenciaContaFinanceiraFacade.getExercicioFacade().getExercicioPorAno(c.get(Calendar.YEAR));
    }

    public BigDecimal getSaldoContaFinanceiraConcedida() {
        return saldoContaFinanceiraConcedida;
    }

    public void setSaldoContaFinanceiraConcedida(BigDecimal saldoContaFinanceiraConcedida) {
        this.saldoContaFinanceiraConcedida = saldoContaFinanceiraConcedida;
    }

    public ContaBancariaEntidade getContaBancariaEntidadeConcedida() {
        return contaBancariaEntidadeConcedida;
    }

    public void setContaBancariaEntidadeConcedida(ContaBancariaEntidade contaBancariaEntidadeConcedida) {
        this.contaBancariaEntidadeConcedida = contaBancariaEntidadeConcedida;
    }

    public ContaBancariaEntidade getContaBancariaEntidadeRecebida() {
        return contaBancariaEntidadeRecebida;
    }

    public void setContaBancariaEntidadeRecebida(ContaBancariaEntidade contaBancariaEntidadeRecebida) {
        this.contaBancariaEntidadeRecebida = contaBancariaEntidadeRecebida;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public void setarFinalidade(ActionEvent evento) {
        selecionado.setFinalidadePagamento((FinalidadePagamento) evento.getComponent().getAttributes().get("objeto"));
    }


    public ParametroEvento getParametroEventoConcedido() {
        return transferenciaContaFinanceiraFacade.criarParametroEventoConcedido(selecionado);
    }

    public ParametroEvento getParametroEventoRecebido() {
        return transferenciaContaFinanceiraFacade.criarParametroEventoRecebido(selecionado);
    }

    public void recuperarEventos() {
        recuperarEventoRetirada();
        recuperarEventoDeposito();
    }
}
