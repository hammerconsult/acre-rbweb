/*
 * Codigo gerado automaticamente em Tue Dec 13 16:19:47 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.NotaExecucaoOrcamentaria;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EmpenhoEstornoFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.ConverterAutoComplete;
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
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@ManagedBean(name = "empenhoEstornoControlador")
@ViewScoped
@URLMappings(mappings = {
//        Mapeamento Empenho Estorno Normal
    @URLMapping(id = "novo-empenho-estorno", pattern = "/estorno-empenho/novo/", viewId = "/faces/financeiro/orcamentario/empenhoestorno/edita.xhtml"),
    @URLMapping(id = "editar-empenho-estorno", pattern = "/estorno-empenho/editar/#{empenhoEstornoControlador.id}/", viewId = "/faces/financeiro/orcamentario/empenhoestorno/edita.xhtml"),
    @URLMapping(id = "ver-empenho-estorno", pattern = "/estorno-empenho/ver/#{empenhoEstornoControlador.id}/", viewId = "/faces/financeiro/orcamentario/empenhoestorno/visualizar.xhtml"),
    @URLMapping(id = "listar-empenho-estorno", pattern = "/estorno-empenho/listar/", viewId = "/faces/financeiro/orcamentario/empenhoestorno/lista.xhtml"),

//        Mapeamento Empenho Estorno Resto a Pagar
    @URLMapping(id = "novo-empenho-estorno-resto", pattern = "/cancelamento-empenho-restos-a-pagar/novo/", viewId = "/faces/financeiro/orcamentario/empenhoestorno/edita.xhtml"),
    @URLMapping(id = "editar-empenho-estorno-resto", pattern = "/cancelamento-empenho-restos-a-pagar/editar/#{empenhoEstornoControlador.id}/", viewId = "/faces/financeiro/orcamentario/empenhoestorno/edita.xhtml"),
    @URLMapping(id = "ver-empenho-estorno-resto", pattern = "/cancelamento-empenho-restos-a-pagar/ver/#{empenhoEstornoControlador.id}/", viewId = "/faces/financeiro/orcamentario/empenhoestorno/visualizar.xhtml"),
    @URLMapping(id = "listar-empenho-estorno-resto", pattern = "/cancelamento-empenho-restos-a-pagar/listar/", viewId = "/faces/financeiro/orcamentario/empenhoestorno/listarestopagar.xhtml")
})

public class EmpenhoEstornoControlador extends PrettyControlador<EmpenhoEstorno> implements Serializable, CRUD {

    @EJB
    private EmpenhoEstornoFacade facade;
    private DesdobramentoEmpenhoEstorno desdobramento;
    private DesdobramentoObrigacaoPagar desdobramentoObrigacaoPagar;
    private DesdobramentoEmpenho desdobramentoEmpenho;
    private ConverterAutoComplete converterDesdobramentoEmpenho;
    private List<DesdobramentoEmpenho> desdobramentosEmpenho;
    private SolicitacaoEmpenhoEstorno solicitacaoEmpenhoEstorno;
    private List<SolicitacaoEmpenhoEstorno> solicitacoesEmpenhoEstorno;
    private Integer indiceAba;

    public EmpenhoEstornoControlador() {
        super(EmpenhoEstorno.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        if (selecionado.isEmpenhoEstornoNormal()) {
            return "/estorno-empenho/";
        } else {
            return "/cancelamento-empenho-restos-a-pagar/";
        }
    }

    private void redirecionarParaLista() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "listar/");
    }

    private void redirecionarParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public String getTituloEstornoEmpenho() {
        if (selecionado.isEmpenhoEstornoNormal()) {
            return "Estorno de Empenho";
        } else {
            return "Cancelamento de Resto a Pagar";
        }
    }

    public String getTituloEmpenho() {
        if (selecionado.isEmpenhoEstornoNormal()) {
            return "Empenho";
        } else {
            return "Restos a Pagar";
        }
    }

    public ConverterAutoComplete getConverterDesdobramentoEmpenho() {
        if (converterDesdobramentoEmpenho == null) {
            converterDesdobramentoEmpenho = new ConverterAutoComplete(DesdobramentoEmpenho.class, facade.getDesdobramentoEmpenhoFacade());
        }
        return converterDesdobramentoEmpenho;
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-empenho-estorno", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setCategoriaOrcamentaria(CategoriaOrcamentaria.NORMAL);
        parametosIniciais();
    }

    @URLAction(mappingId = "ver-empenho-estorno", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarEmpenho();
    }


    @URLAction(mappingId = "editar-empenho-estorno", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarEmpenho();
    }

    private void recuperarEmpenho() {
        if (selecionado.getEmpenho() != null) {
            selecionado.setEmpenho(facade.getEmpenhoFacade().recuperar(selecionado.getEmpenho().getId()));
            desdobramentosEmpenho = selecionado.getEmpenho().getDesdobramentos();
        }
    }

    @URLAction(mappingId = "novo-empenho-estorno-resto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoRestoPagar() {
        super.novo();
        selecionado.setCategoriaOrcamentaria(CategoriaOrcamentaria.RESTO);
        parametosIniciais();
        definirTipoEmpenhoComoCancelamento(selecionado);
    }

    @URLAction(mappingId = "ver-empenho-estorno-resto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verRestoPagar() {
        super.ver();
    }

    @URLAction(mappingId = "editar-empenho-estorno-resto", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarRestoPagar() {
        super.editar();
        recuperarEmpenho();
    }

    private void parametosIniciais() {
        selecionado.setDataEstorno(facade.getSistemaFacade().getDataOperacao());
        selecionado.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
        selecionado.setUnidadeOrganizacional(facade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente());
        selecionado.setUnidadeOrganizacionalAdm(facade.getSistemaFacade().getUnidadeOrganizacionalAdministrativaCorrente());
        if (facade.getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
            FacesUtil.addWarn("Aviso!", facade.getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
        }
        solicitacoesEmpenhoEstorno = facade.getSolicitacaoEmpenhoEstornoFacade().buscarSolicitacaoEmpenhoEstorno(
            facade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente(),
            selecionado.getCategoriaOrcamentaria(),
            null,
            facade.getSistemaFacade().getExercicioCorrente()
        );
        indiceAba = 0;
    }

    public boolean isEmpenhoEstornoNormal() {
        return selecionado.isEmpenhoEstornoNormal();
    }

    public void selecionar(ActionEvent evento) {
        EmpenhoEstorno eev = (EmpenhoEstorno) evento.getComponent().getAttributes().get("objeto");
        selecionado = facade.recuperar(eev.getId());
    }

    @Override
    public void salvar() {
        try {
            validarSelecionado();
            validarValorSolicitacaoEstorno();
            if (isOperacaoNovo()) {
                selecionado.setSaldoDisponivel(facade.getSaldoFonteDespesaORC(selecionado));
                selecionado = facade.salvarNovoEstorno(selecionado, solicitacaoEmpenhoEstorno);
                FacesUtil.addOperacaoRealizada(" Estorno de Empenho " + selecionado.toString() + " foi salvo com sucesso. ");
                redirecionarParaVer();
            } else {
                selecionado = facade.salvarRetornando(selecionado);
                FacesUtil.addOperacaoRealizada(" Estorno de Empenho " + selecionado.toString() + " foi alterado com sucesso. ");
                redirecionarParaLista();
            }
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida("Erro ao validar empenho...: " + ex.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    private void definirTipoEmpenhoComoCancelamento(EmpenhoEstorno entity) {
        if (entity.isEmpenhoEstornoResto()) {
            entity.setTipoEmpenhoEstorno(TipoEmpenhoEstorno.CANCELAMENTO);
        }
    }

    private void validarSelecionado() {
        ValidacaoException ve = new ValidacaoException();
        validarEmpenho(ve);
        selecionado.realizarValidacoes();
        if (isOperacaoNovo()) {
            validarRegrasEspecificas(ve);
            validarSolicitacoesEstornoComEmpenho();
        }
    }

    private void validarValorSolicitacaoEstorno() {
        if (solicitacaoEmpenhoEstorno != null) {
            if (solicitacaoEmpenhoEstorno.getValor().compareTo(selecionado.getValor()) != 0) {
                ValidacaoException ve = new ValidacaoException();
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor do estorno de empenho é diferente do valor da solicitação." +
                    " <b> Valor da Solicitação: " + Util.formataValor(solicitacaoEmpenhoEstorno.getValor()) + ". </b>");
                ve.lancarException();
            }
        }
    }

    private void validarSolicitacoesEstornoComEmpenho() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getEmpenho() != null && solicitacaoEmpenhoEstorno == null) {
            List<SolicitacaoEmpenhoEstorno> solicitacoes = facade.getSolicitacaoEmpenhoEstornoFacade().buscarSolicitacoesPendentesPorUnidadeAndEmpenho(selecionado.getUnidadeOrganizacional(), selecionado.getEmpenho(), selecionado.getCategoriaOrcamentaria());
            if (!Util.isListNullOrEmpty(solicitacoes)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Existe(m) solicitação(ões) de estorno de empenho para o empenho: " + selecionado.getEmpenho().getNumero()
                    + ". Utilize a aba 'Solicitações' para selecioná-la.");
                selecionado.setEmpenho(null);
                indiceAba = 1;
                FacesUtil.atualizarComponente("Formulario:tabGeral");
            }
            ve.lancarException();
            Empenho empenho = selecionado.getEmpenho().isEmpenhoRestoPagar() ? selecionado.getEmpenho().getEmpenho() : selecionado.getEmpenho();
            ExecucaoContratoEmpenho exEmp = facade.getExecucaoContratoFacade().buscarExecucaoContratoEmpenhoPorEmpenho(empenho);
            if (exEmp != null && solicitacaoEmpenhoEstorno == null) {
                String url = FacesUtil.getRequestContextPath() + "/execucao-contrato-estorno/novo/";
                Web.poeNaSessao("EXECUCAO_CONTRATO", exEmp);

                String mensagem = "Este empenho possui integração com o contrato " + empenho.getContrato() + "." +
                    " " + "<b><a href='" + url + "'target='_blank' style='color: blue;'>Clique aqui</a></b>" +
                    " para gerar a solicitação de estorno da execução do contrato para este empenho. ";
                ve.adicionarMensagemDeOperacaoNaoPermitida(mensagem);
                selecionado.setEmpenho(null);
            }
            ExecucaoProcessoEmpenho exProcEmp = facade.getExecucaoProcessoFacade().buscarExecucaoProcessoEmpenhoPorEmpenho(empenho.getId());
            if (exProcEmp != null && solicitacaoEmpenhoEstorno == null) {
                String url = FacesUtil.getRequestContextPath() + "/execucao-sem-contrato-estorno/novo/";
                Web.poeNaSessao("EXECUCAO_PROCESSO", exProcEmp);

                String mensagem = "Este empenho possui integração com a " + exProcEmp.getEmpenho().getExecucaoProcesso().getDescricaoProcesso() + "." +
                    " " + "<b><a href='" + url + "'target='_blank' style='color: blue;'>Clique aqui</a></b>" +
                    " para gerar a solicitação de estorno da execução sem contrato para este empenho. ";
                ve.adicionarMensagemDeOperacaoNaoPermitida(mensagem);
                selecionado.setEmpenho(null);
            }

        }
        ve.lancarException();
    }

    private void validarRegrasEspecificas(ValidacaoException ve) {
        Empenho empenho = facade.getEmpenhoFacade().recuperar(selecionado.getEmpenho().getId());
        BigDecimal saldoFinalEmpenho = empenho.getSaldoDisponivelEmpenhoComObrigacao();
        if (DataUtil.dataSemHorario(selecionado.getDataEstorno()).compareTo(DataUtil.dataSemHorario(selecionado.getEmpenho().getDataEmpenho())) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" A data do estorno do empenho deve ser maior ou igual a data do empenho selecionado. Data do empenho: <b>" + DataUtil.getDataFormatada(selecionado.getEmpenho().getDataEmpenho()) + "</b>.");
        }
        if (!selecionado.getDesdobramentos().isEmpty()) {
            if (selecionado.getValor().compareTo(selecionado.getValorTotalDetalhamento()) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor do estorno deve ser igual ou maior que o valor total de " + Util.formataValor(selecionado.getValorTotalDetalhamento()) + " referente aos desdobramentos adicionados na lista.");
            }
            if (selecionado.getValorTotalDetalhamento().compareTo(empenho.getSaldoTotalDetalhamento()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor do estorno referente aos desdobramentos deve ser menor ou igual ao saldo disponível de " + Util.formataValor(saldoFinalEmpenho) + " aos desdobramentos do empenho.");
            }
            if (selecionado.getValor().compareTo(empenho.getSaldo()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor do estorno deve ser menor ou igual ao saldo disponível de " + Util.formataValor(empenho.getSaldo()) + " referente ao empenho.");
            }
            if (selecionado.getValor().compareTo(saldoFinalEmpenho.add(selecionado.getValorTotalDetalhamento())) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor do estorno é maior que o valor total dos detalhamentos " + Util.formataValor(selecionado.getValorTotalDetalhamento()) + " somado com o saldo sem obrigação a pagar do empenho:  " + Util.formataValor(saldoFinalEmpenho) + ".");
            }
        } else {
            if (selecionado.getValor().compareTo(saldoFinalEmpenho) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor do estorno deve ser menor ou igual ao saldo disponível de " + Util.formataValor(saldoFinalEmpenho) + " referente ao empenho.");
            }
            if (selecionado.getDesdobramentos().isEmpty() && !selecionado.getEmpenho().getDesdobramentos().isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório informar ao menos um(1) detalhamento para efetuar o estorno.");
            }
            if (selecionado.getEmpenho().isEmpenhoDeObrigacaoPagar() && empenho.getValorLiquidoEmpenho().compareTo(BigDecimal.ZERO) == 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O saldo restante pertence aos desdobramentos do empenho referente a obrigação a pagar.");
            }
        }
        ve.lancarException();
    }

    private void validarEmpenho(ValidacaoException validacaoException) {
        if (selecionado.getEmpenho() == null) {
            validacaoException.adicionarMensagemDeCampoObrigatorio("O campo Empenho deve ser informado.");
        } else {
            facade.getEmpenhoFacade().getHierarquiaOrganizacionalFacade()
                .validarUnidadesIguais(selecionado.getEmpenho().getUnidadeOrganizacional(), selecionado.getUnidadeOrganizacional()
                    , validacaoException
                    , "A Unidade Organizacional do estorno de empenho deve ser a mesma do empenho.");
        }
        validacaoException.lancarException();
    }

    public List<Empenho> completarEmpenho(String parte) {
        if (selecionado.isEmpenhoEstornoNormal()) {
            return facade.getEmpenhoFacade().buscarEmpenhoPorNumeroEPessoaCategoriaNormal(
                parte.trim(),
                selecionado.getExercicio().getAno(),
                selecionado.getUnidadeOrganizacional());
        }
        return facade.getEmpenhoFacade().buscarEmpenhoPorNumeroPessoaAndCategoriaResto(
            parte.trim(),
            selecionado.getExercicio().getAno(),
            selecionado.getUnidadeOrganizacional());
    }

    public void definirEmpenho() {
        try {
            validarSolicitacoesEstornoComEmpenho();
            if (selecionado.getEmpenho() != null) {
                selecionado.setEmpenho(facade.getEmpenhoFacade().recuperar(selecionado.getEmpenho().getId()));
                selecionado.setValor(selecionado.getEmpenho().getSaldoDisponivelEmpenhoComObrigacao());
                selecionado.setComplementoHistorico(selecionado.getEmpenho().getComplementoHistorico());
                desdobramentosEmpenho = selecionado.getEmpenho().getDesdobramentos();
                selecionado.getEmpenho().setTipoReconhecimento(TipoReconhecimentoObrigacaoPagar.SEM_RECONHECIMENTO_OBRIGACAO_ANTES_EMPENHO_DESPESA);

                if (selecionado.getEmpenho().isEmpenhoDeObrigacaoPagar()) {
                    selecionado.getEmpenho().setTipoReconhecimento(TipoReconhecimentoObrigacaoPagar.COM_RECONHECIMENTO_OBRIGACAO_ANTES_EMPENHO_DESPESA);
                }
                definirEventoContabil();
                facade.verificarSeEmpenhoPossuiObrigacaoPagarDepoisEmpenho(selecionado);
                if (selecionado.getEventoContabil() == null) {
                    selecionado.setEmpenho(null);
                }
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public Boolean isRegistroEditavel() {
        return isOperacaoEditar();
    }

    public void selecionarEmpenho(ActionEvent evento) {
        selecionado.setEmpenho((Empenho) evento.getComponent().getAttributes().get("objeto"));
        definirEmpenho();
    }


    private void definirEventoContabil() {
        try {
            facade.definirEventoContabil(selecionado);
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    public void gerarNotaOrcamentaria(boolean isDownload) {
        try {
            List<NotaExecucaoOrcamentaria> notas = facade.buscarNotaEstornoEmpenho(" and nota.id = " + selecionado.getId(), selecionado.getCategoriaOrcamentaria(), getFraseDocumento());
            if (notas != null && !notas.isEmpty()) {
                facade.getNotaOrcamentariaFacade().imprimirDocumentoOficial(notas.get(0), selecionado.getCategoriaOrcamentaria().isResto() ? ModuloTipoDoctoOficial.NOTA_RESTO_ESTORNO_EMPENHO : ModuloTipoDoctoOficial.NOTA_ESTORNO_EMPENHO, selecionado, isDownload);
            }
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String getFraseDocumento() {
        String retorno = "NOTA DE ESTORNO DE EMPENHO";
        if (selecionado.getCategoriaOrcamentaria().isResto()) {
            retorno = "NOTA DE CANCELAMENTO DE RESTOS A PAGAR";
            if (selecionado.getEmpenho().getTransportado() != null && selecionado.getEmpenho().getTransportado()) {
                if (TipoRestosProcessado.NAO_PROCESSADOS.equals(selecionado.getEmpenho().getTipoRestosProcessados())) {
                    retorno += " NÃO PROCESSADOS";
                } else {
                    retorno += " PROCESSADOS";
                }
            }
        }
        return retorno;
    }

    public void definirNullParaEmpenho() {
        selecionado.setEmpenho(null);
        selecionado.setValor(BigDecimal.ZERO);
        selecionado.setComplementoHistorico(null);
        selecionado.setEventoContabil(null);
        selecionado.getDesdobramentos().clear();
    }

    public ParametroEvento getParametroEvento() {
        return facade.getParametroEvento(selecionado);
    }

    public ParametroEvento getParametroEventoDesdobramento(DesdobramentoEmpenhoEstorno desdobramento) {
        return facade.getParametroEventoDesdobramento(desdobramento);
    }

    public void novoDesdobramento() {
        try {
            desdobramento = new DesdobramentoEmpenhoEstorno();
            desdobramentoObrigacaoPagar = new DesdobramentoObrigacaoPagar();
            FacesUtil.executaJavaScript("setaFoco('Formulario:contaDesdobramento_input')");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public boolean isEstornoEmpenhoComObrigacaoPagar() {
        return selecionado.getEmpenho() != null
            && selecionado.getEmpenho().getSaldoObrigacaoPagarAntesEmp().compareTo(BigDecimal.ZERO) > 0
            && selecionado.getEmpenho().isEmpenhoDeObrigacaoPagar();
    }

    public boolean isEstornoPossuiDetalhamento() {
        return selecionado.getDesdobramentos() != null
            && !selecionado.getDesdobramentos().isEmpty();
    }

    public void editarDesdobramento(DesdobramentoEmpenhoEstorno desdobramento) {
        this.desdobramento = (DesdobramentoEmpenhoEstorno) Util.clonarObjeto(desdobramento);
        if (selecionado.getEmpenho().isEmpenhoDeObrigacaoPagar()) {
            setDesdobramentoObrigacaoPagar(this.desdobramento.getDesdobramentoObrigacaoPagar());
        } else {
            setDesdobramentoEmpenho(desdobramento.getDesdobramentoEmpenho());
        }
    }

    public void removerDesdobramento(DesdobramentoEmpenhoEstorno desdobramento) {
        selecionado.getDesdobramentos().remove(desdobramento);
        selecionado.setValor(selecionado.getValorTotalDetalhamento());
        FacesUtil.executaJavaScript("setaFoco('Formulario:contaDesdobramento_input')");
    }

    public void definirSaldoDetalhamento() {
        if (selecionado.getEmpenho() != null && !selecionado.getEmpenho().getDesdobramentos().isEmpty()) {
            for (DesdobramentoEmpenho desdEmpenho : selecionado.getEmpenho().getDesdobramentos()) {
                if ((selecionado.getEmpenho().isEmpenhoDeObrigacaoPagar() && desdEmpenho.getDesdobramentoObrigacaoPagar().getObrigacaoAPagar().equals(desdobramentoObrigacaoPagar.getObrigacaoAPagar())
                    && desdobramentoObrigacaoPagar.getConta().equals(desdEmpenho.getConta()))) {
                    desdobramento.setValor(desdEmpenho.getSaldo());
                }
                if (!selecionado.getEmpenho().isEmpenhoDeObrigacaoPagar() && desdobramentoEmpenho.getConta().equals(desdEmpenho.getConta())) {
                    desdobramento.setValor(desdEmpenho.getSaldo());
                }
            }
        }
    }

    public void adicionarDetalhamento() {
        try {
            validarDetalhamento();
            desdobramento.setEmpenhoEstorno(selecionado);
            selecionado.setDesdobramentos(Util.adicionarObjetoEmLista(selecionado.getDesdobramentos(), desdobramento));
            selecionado.setValor(selecionado.getValorTotalDetalhamento());
            cancelarDesdobramento();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
        FacesUtil.executaJavaScript("setaFoco('Formulario:historico_input')");
    }

    private void validarDetalhamento() {
        adicionarContaDetalhamentoObrigacaoPagarAoDesdobramentoEstornoEmpenho();
        ValidacaoException ve = new ValidacaoException();
        if (desdobramento.getConta() == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Conta de Despesa deve ser informado.");
        }
        if (desdobramento.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo valor deve ser maior que zero(0).");
        }
        validarMesmoDetalhamentoEmLista();
        validarSaldoDesdobramento();
        ve.lancarException();
    }

    private void validarSaldoDesdobramento() {
        ValidacaoException ve = new ValidacaoException();
        BigDecimal saldoEmpenho = selecionado.getEmpenho().getSaldo();
        if (saldoEmpenho != null && desdobramento.getValor().compareTo(saldoEmpenho) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O valor a ser estornado de <b>" + Util.formataValor(desdobramento.getValor()) + "</b> é maior que o saldo de <b>" + Util.formataValor(saldoEmpenho) + "</b> disponível no empenho.");
        }
        validarSaldoDisponivelPorDetalhamento();
        ve.lancarException();
    }

    private void validarSaldoDisponivelPorDetalhamento() {
        if (selecionado.getEmpenho() != null && selecionado.getEmpenho().isEmpenhoDeObrigacaoPagar()) {
            selecionado.getEmpenho().validarSaldoDisponivelPorContaDespesa(desdobramento.getValor(), desdobramento.getConta(), desdobramentoObrigacaoPagar.getObrigacaoAPagar());
        }
    }

    private void adicionarContaDetalhamentoObrigacaoPagarAoDesdobramentoEstornoEmpenho() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getEmpenho().isEmpenhoDeObrigacaoPagar() && desdobramentoObrigacaoPagar == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Conta de Despesa deve ser informado.");
        }
        if (!selecionado.getEmpenho().isEmpenhoDeObrigacaoPagar() && desdobramentoEmpenho == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Conta de Despesa deve ser informado.");
        }
        ve.lancarException();
        if (selecionado.getEmpenho().isEmpenhoDeObrigacaoPagar() && desdobramentoObrigacaoPagar.getConta() != null) {
            desdobramento.setDesdobramentoObrigacaoPagar(desdobramentoObrigacaoPagar);
            desdobramento.setConta(desdobramentoObrigacaoPagar.getConta());
        }
        if (!selecionado.getEmpenho().isEmpenhoDeObrigacaoPagar() && desdobramentoEmpenho.getConta() != null) {
            desdobramento.setDesdobramentoEmpenho(desdobramentoEmpenho);
            desdobramento.setConta(desdobramentoEmpenho.getConta());
        }
    }

    public void cancelarDesdobramento() {
        desdobramento = null;
        desdobramentoObrigacaoPagar = null;
        desdobramentoEmpenho = null;
        definirFocoBtnNovoDesdobramento();
    }

    private void definirFocoBtnNovoDesdobramento() {
        FacesUtil.executaJavaScript("setaFoco('Formulario:btnNovoDesdobramento')");
    }

    private void validarMesmoDetalhamentoEmLista() {
        if (selecionado.getEmpenho().isEmpenhoDeObrigacaoPagar()) {
            selecionado.validarMesmoDetalhamentoEmListaPorObrigacaoPagar(desdobramento, desdobramentoObrigacaoPagar.getObrigacaoAPagar());
        } else {
            selecionado.validarMesmoDetalhamentoEmListaPorEmpenho(desdobramento, desdobramentoEmpenho.getEmpenho());
        }
    }

    public List<DesdobramentoObrigacaoPagar> completarDetalhamento(String parte) {
        if (selecionado.getEmpenho() != null && selecionado.getEmpenho().isEmpenhoDeObrigacaoPagar()) {
            return facade.getDesdobramentoObrigacaoAPagarFacade().buscarDesdobramentoObrigacaoPagarPorEmpenho(parte.trim(), selecionado.getEmpenho());
        }
        return Lists.newArrayList();
    }

    public List<DesdobramentoEmpenho> completarDetalhamentosEmpenho(String filtro) {
        if (selecionado.getEmpenho() != null) {
            return facade.getDesdobramentoEmpenhoFacade().buscarDesdobramentoObrigacaoPagarPorEmpenho(filtro.trim(), selecionado.getEmpenho());
        }
        return Lists.newArrayList();
    }

    public void estornarSolicitacao(SolicitacaoEmpenhoEstorno solicitacao) {
        try {
            solicitacaoEmpenhoEstorno = solicitacao;
            validarEstornoLiquidacaoPendenteSolicitacao();
            solicitacaoEmpenhoEstorno.setEmpenhoEstorno(selecionado);
            selecionado.setEmpenho(solicitacaoEmpenhoEstorno.getEmpenho());
            definirEmpenho();
            selecionado.setComplementoHistorico(solicitacaoEmpenhoEstorno.getHistorico());
            selecionado.setValor(solicitacaoEmpenhoEstorno.getValor());
            indiceAba = 0;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    private void validarEstornoLiquidacaoPendenteSolicitacao() {
        ValidacaoException ve = new ValidacaoException();
        List<SolicitacaoLiquidacaoEstorno> solicitacoes = facade.getSolicitacaoLiquidacaoEstornoFacade().buscarPorSolicitacaoEstornoEmpenho(solicitacaoEmpenhoEstorno);
        if (!solicitacoes.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Para continuar com o estorno será necessário estornar a(s) liquidação(ões) relacionada(s) abaixo:");

            for (SolicitacaoLiquidacaoEstorno solicitacao : solicitacoes) {
                ve.adicionarMensagemWarn("Liquidação: ", solicitacao.getLiquidacao().toString());
            }
        }
        ve.lancarException();
    }

    public DesdobramentoEmpenhoEstorno getDesdobramento() {
        return desdobramento;
    }

    public void setDesdobramento(DesdobramentoEmpenhoEstorno desdobramento) {
        this.desdobramento = desdobramento;
    }

    public List<DesdobramentoEmpenho> getDesdobramentosEmpenho() {
        return desdobramentosEmpenho;
    }

    public void setDesdobramentosEmpenho(List<DesdobramentoEmpenho> desdobramentosEmpenho) {
        this.desdobramentosEmpenho = desdobramentosEmpenho;
    }

    public DesdobramentoObrigacaoPagar getDesdobramentoObrigacaoPagar() {
        return desdobramentoObrigacaoPagar;
    }

    public void setDesdobramentoObrigacaoPagar(DesdobramentoObrigacaoPagar desdobramentoObrigacaoPagar) {
        this.desdobramentoObrigacaoPagar = desdobramentoObrigacaoPagar;
    }

    public SolicitacaoEmpenhoEstorno getSolicitacaoEmpenhoEstorno() {
        return solicitacaoEmpenhoEstorno;
    }

    public void setSolicitacaoEmpenhoEstorno(SolicitacaoEmpenhoEstorno solicitacaoEmpenhoEstorno) {
        this.solicitacaoEmpenhoEstorno = solicitacaoEmpenhoEstorno;
    }

    public List<SolicitacaoEmpenhoEstorno> getSolicitacoesEmpenhoEstorno() {
        return solicitacoesEmpenhoEstorno;
    }

    public void setSolicitacoesEmpenhoEstorno(List<SolicitacaoEmpenhoEstorno> solicitacoesEmpenhoEstorno) {
        this.solicitacoesEmpenhoEstorno = solicitacoesEmpenhoEstorno;
    }

    public boolean isEstornoEmpenhoDeContrato() {
        return selecionado.getEmpenho() != null && selecionado.getEmpenho().getContrato() != null;
    }

    public Integer getIndiceAba() {
        return indiceAba;
    }

    public void setIndiceAba(Integer indiceAba) {
        this.indiceAba = indiceAba;
    }

    public DesdobramentoEmpenho getDesdobramentoEmpenho() {
        return desdobramentoEmpenho;
    }

    public void setDesdobramentoEmpenho(DesdobramentoEmpenho desdobramentoEmpenho) {
        this.desdobramentoEmpenho = desdobramentoEmpenho;
    }
}

