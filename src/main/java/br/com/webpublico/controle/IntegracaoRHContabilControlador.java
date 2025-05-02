package br.com.webpublico.controle;


import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteIntegracaoRHContabil;
import br.com.webpublico.entidadesauxiliares.ContaDespesaTipoContaDespesaIntegracaoRHContabil;
import br.com.webpublico.entidadesauxiliares.FonteItemIntegracaoRHContabil;
import br.com.webpublico.entidadesauxiliares.PessoaClasseIntegracaoRHContabil;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.rh.integracao.TipoContabilizacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.IntegracaoRHContabilFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FlowEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-integracao-rh-contabil", pattern = "/integracao-rh-contabil/novo/", viewId = "/faces/financeiro/integracao-rh-contabil/edita.xhtml"),
    @URLMapping(id = "ver-integracao-rh-contabil", pattern = "/integracao-rh-contabil/ver/#{integracaoRHContabilControlador.id}/", viewId = "/faces/financeiro/integracao-rh-contabil/visualizar.xhtml"),
    @URLMapping(id = "editar-integracao-rh-contabil", pattern = "/integracao-rh-contabil/editar/#{integracaoRHContabilControlador.id}/", viewId = "/faces/financeiro/integracao-rh-contabil/edita.xhtml"),
    @URLMapping(id = "listar-integracao-rh-contabil", pattern = "/integracao-rh-contabil/listar/", viewId = "/faces/financeiro/integracao-rh-contabil/lista.xhtml")
})
public class IntegracaoRHContabilControlador extends PrettyControlador<IntegracaoRHContabil> implements Serializable, CRUD {

    @EJB
    private IntegracaoRHContabilFacade facade;
    private AssistenteIntegracaoRHContabil assistenteIntegracaoRHContabil;
    private Future<IntegracaoRHContabil> future;
    private List<FolhaDePagamento> folhaDePagamentos;
    private TipoIntegradorRHContabil[] tipos;
    private RetencaoIntegracaoRHContabil[] retencaoSelecionadas;
    private List<ItemIntegracaoRHContabil> itensRetencao;
    private Boolean simulacao;

    public IntegracaoRHContabilControlador() {
        super(IntegracaoRHContabil.class);
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/integracao-rh-contabil/";
    }

    @URLAction(mappingId = "novo-integracao-rh-contabil", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        definirParametros();
        simulacao = false;
    }

    private void definirParametros() {
        selecionado.setExercicio(facade.getExercicioCorrente());
        selecionado.setDataRegistro(facade.getDataOperacao());
        selecionado.setUsuarioSistema(facade.getUsuarioCorrente());
        selecionado.setMes(Mes.getMesToInt(DataUtil.getMes(selecionado.getDataRegistro())));

        selecionado.setParametro(new ParametroRHContabil(selecionado));
        selecionado.getParametro().setDataEmpenho(facade.getDataOperacao());
        selecionado.getParametro().setDataLiquidacao(facade.getDataOperacao());
        selecionado.getParametro().setDataPrevisaoPagamento(facade.getDataOperacao());
        selecionado.getParametro().setDataObrigacao(facade.getDataOperacao());

        folhaDePagamentos = Lists.newArrayList();
        tipos = TipoIntegradorRHContabil.values();
    }

    @URLAction(mappingId = "ver-integracao-rh-contabil", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        definirParametrosEditar();
    }

    @URLAction(mappingId = "editar-integracao-rh-contabil", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        definirParametrosEditar();
    }

    private void definirParametrosEditar() {
        facade.criarListasAuxiliares(selecionado);
        folhaDePagamentos = Lists.newArrayList();
        tipos = new TipoIntegradorRHContabil[selecionado.getTipos().size()];
        int posicao = 0;
        for (TipoIntegracaoRHContabil tipo : selecionado.getTipos()) {
            tipos[posicao] = tipo.getTipo();
            posicao++;
        }
    }


    public List<SelectItem> getTipoDocumento() {
        return Util.getListSelectItem(TipoDocPagto.values());
    }

    public List<SelectItem> getTipoOperacao() {
        return Util.getListSelectItem(TipoOperacaoPagto.values());
    }

    public List<SelectItem> getMeses() {
        return Util.getListSelectItem(Mes.values(), false);
    }

    public List<SelectItem> getExercicios() {
        return Util.getListSelectItem(facade.getExercicioFacade().listaDecrescente());
    }

    public List<SelectItem> getTiposFolha() {
        return Util.getListSelectItem(TipoFolhaDePagamento.values(), false);
    }

    public List<TipoIntegradorRHContabil> getTiposIntegracao() {
        return Arrays.asList(TipoIntegradorRHContabil.values());
    }

    private void adicionarFolhasAoSelecionado() {
        selecionado.getFolhasDePagamento().clear();
        if (folhaDePagamentos != null && !folhaDePagamentos.isEmpty()) {
            for (FolhaDePagamento folhaDePagamento : folhaDePagamentos) {
                selecionado.getFolhasDePagamento().add(new IntegracaoRHContabilFolha(folhaDePagamento, selecionado));
            }
        }
    }

    private void adicionarTiposAoSelecionado() {
        selecionado.getTipos().clear();
        if (tipos != null) {
            for (TipoIntegradorRHContabil tipo : tipos) {
                selecionado.getTipos().add(new TipoIntegracaoRHContabil(selecionado, tipo));
            }
        }
    }

    public void removerRetencaoDoItem(ItemIntegracaoRHContabil item, RetencaoIntegracaoRHContabil retencaoIntegracaoRHContabil) {
        retencaoIntegracaoRHContabil.setItemIntegracaoRHContabil(null);
        item.getRetencoes().remove(retencaoIntegracaoRHContabil);
        selecionado.getRetencoesSemPagamento().add(retencaoIntegracaoRHContabil);
    }

    public void confirmarVinculoRentecaoPagamento(ItemIntegracaoRHContabil item) {
        if (retencaoSelecionadas != null) {
            for (RetencaoIntegracaoRHContabil retencao : retencaoSelecionadas) {
                BigDecimal valor = retencao.getValor().add(item.getValorTotalRetencoes());
                BigDecimal liquido = item.getValor().subtract(item.getValorTotalRetencoes());
                if (valor.compareTo(liquido) > 0) {
                    FacesUtil.addOperacaoNaoPermitida("O Saldo da dotação não pode ficar negativo. Saldo : <b> " + Util.formataValor(liquido) + " </b>");
                    return;
                }

                RetencaoIntegracaoRHContabil retencaoJaAdicionada = getRetencaoJaAdicionada(item, retencao);
                if (retencaoJaAdicionada != null) {
                    retencaoJaAdicionada.setValor(retencaoJaAdicionada.getValor().add(retencao.getValor()));
                    atualizarServidoresDaRetencaoJaAdicionada(retencaoJaAdicionada, retencao);
                    Util.adicionarObjetoEmLista(item.getRetencoes(), retencaoJaAdicionada);
                } else {
                    retencao.setSubConta(item.getSubConta());
                    retencao.setItemIntegracaoRHContabil(item);
                    item.getRetencoes().add(retencao);
                }
                selecionado.getRetencoesSemPagamento().remove(retencao);
            }
        }
    }

    private RetencaoIntegracaoRHContabil getRetencaoJaAdicionada(ItemIntegracaoRHContabil item, RetencaoIntegracaoRHContabil retencaoSelecionada) {
        for (RetencaoIntegracaoRHContabil retencao : item.getRetencoes()) {
            if (retencao.getContaExtraorcamentaria().equals(retencaoSelecionada.getContaExtraorcamentaria()) &&
                retencao.getPessoa().equals(retencaoSelecionada.getPessoa())) {
                return retencao;
            }
        }
        return null;
    }

    private void atualizarServidoresDaRetencaoJaAdicionada(RetencaoIntegracaoRHContabil retencaoJaAdicionada, RetencaoIntegracaoRHContabil retencaoSelecionada) {
        List<ServidorRetencaoRHContabil> novosServidores = Lists.newArrayList();
        novosServidores.addAll(retencaoSelecionada.getServidores());
        retencaoJaAdicionada.getServidores().forEach(servidorAdicionado -> {
            retencaoSelecionada.getServidores().forEach(servidorSelecionado -> {
                if (servidorAdicionado.getVinculoFP().equals(servidorSelecionado.getVinculoFP())) {
                    servidorAdicionado.setValor(servidorAdicionado.getValor().add(servidorSelecionado.getValor()));
                    novosServidores.remove(servidorSelecionado);
                }
            });
        });
        retencaoJaAdicionada.getServidores().addAll(novosServidores);
    }

    public List<FonteDeRecursos> completarFonteDeRecursos(String filtro) {
        return facade.getFonteDeRecursosFacade().listaFiltrandoPorExercicioETipo(filtro, facade.getExercicioCorrente(), TipoFonteRecurso.RETENCAO);
    }

    public List<SubConta> completarSubContaPorFonteRecurso(String filtro) {
        return facade.getSubContaFacade().listaPorExercicio(filtro, facade.getExercicioCorrente());
    }


    public List<SelectItem> buscarClassesCredor(Pessoa pessoa) {
        List<SelectItem> retorno = Lists.newArrayList();
        if (pessoa == null) {
            return retorno;
        }
        List<ClasseCredor> classesDoFornecedor = facade.getClasseCredorFacade().buscarClassesPorPessoa(pessoa, selecionado.getDataRegistro());
        if (classesDoFornecedor == null || classesDoFornecedor.isEmpty()) {
            retorno.add(new SelectItem(null, ""));
        }
        for (ClasseCredor classeCredor : classesDoFornecedor) {
            retorno.add(new SelectItem(classeCredor));
        }
        return retorno;
    }

    public List<SelectItem> buscarTipoContas(Conta conta) {
        List<TipoContaDespesa> toReturn = Lists.newArrayList();
        if (conta != null) {
            TipoContaDespesa tipo = ((ContaDespesa) conta).getTipoContaDespesa();
            if (!TipoContaDespesa.NAO_APLICAVEL.equals(tipo) && tipo != null) {
                toReturn.add(tipo);
            } else {
                List<TipoContaDespesa> busca = facade.getContaFacade().buscarTiposContasDespesaNosFilhosDaConta((ContaDespesa) conta);
                if (!busca.isEmpty()) {
                    for (TipoContaDespesa tp : busca) {
                        if (!TipoContaDespesa.NAO_APLICAVEL.equals(tp)) {
                            toReturn.add(tp);
                        }
                    }
                }
            }
        }
        return Util.getListSelectItemSemCampoVazio(toReturn.toArray(), false);
    }

    public void confirmarContaDespesa() {
        if (selecionado.getId() == null) {
            for (ContaDespesaTipoContaDespesaIntegracaoRHContabil conta : selecionado.getContasTipos()) {
                if (conta.getConta() == null || conta.getTipoContaDespesa() == null) {
                    throw new ExcecaoNegocioGenerica("Existe(m) registros sem informar a conta ou o tipo.");
                }
            }
            for (ContaDespesaTipoContaDespesaIntegracaoRHContabil conta : selecionado.getContasTipos()) {
                for (ItemIntegracaoRHContabil itemIntegracaoRHContabil : selecionado.getItens()) {
                    if (itemIntegracaoRHContabil.getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().equals(conta.getConta())) {
                        itemIntegracaoRHContabil.setTipoContaDespesa(conta.getTipoContaDespesa());
                    }
                }
            }
        }
    }

    public void confirmarPessoaClasse() throws ExcecaoNegocioGenerica {
        if (selecionado.getId() == null) {
            for (PessoaClasseIntegracaoRHContabil pessoasClasse : selecionado.getPessoasClasses()) {
                if (pessoasClasse.getPessoa() == null || pessoasClasse.getClasseCredor() == null) {
                    throw new ExcecaoNegocioGenerica("Existe(m) registros sem informar a pessoa ou classe.");
                }
            }
            for (PessoaClasseIntegracaoRHContabil pessoasClasse : selecionado.getPessoasClasses()) {
                for (ItemIntegracaoRHContabil itemIntegracaoRHContabil : selecionado.getItens()) {
                    if (itemIntegracaoRHContabil.getFornecedor().equals(pessoasClasse.getPessoa())) {
                        itemIntegracaoRHContabil.setClasseCredor(pessoasClasse.getClasseCredor());
                    }
                }
            }
            for (RetencaoIntegracaoRHContabil retencao : selecionado.getRetencoes()) {
                for (ItemIntegracaoRHContabil item : selecionado.getItens()) {
                    if (item.getFornecedor() != null && retencao.getPessoa() != null) {
                        if (item.getFornecedor().getId().equals(retencao.getPessoa().getId())) {
                            retencao.setClasseCredor(item.getClasseCredor());
                        }
                    }
                }
            }
        }
    }


    public String onFlowProcess(FlowEvent event) {
        try {
            String newStep = event.getNewStep().toLowerCase();
            if (newStep.equalsIgnoreCase("fornecedor")) {
                validarSaldoOrcamentario(Boolean.TRUE);
            }
            if (newStep.equalsIgnoreCase("contas")) {
                confirmarPessoaClasse();
            }
            if (newStep.equalsIgnoreCase("empenho")) {
                confirmarContaDespesa();
            }
            if (newStep.equalsIgnoreCase("retencao")) {
                vincularClasseCredorQuandoVazia();
            }
            if (newStep.equalsIgnoreCase("resumo")) {
                validarRetencaoSemPagamento();
                vincularClasseRetencao();
            }
            if (newStep.equalsIgnoreCase("final")) {
                validarValorLiquido();
            }
            FacesUtil.executaJavaScript("Formulario");
            return newStep;
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
            return event.getOldStep();
        }
    }

    private void validarValorLiquido() {
        for (ItemIntegracaoRHContabil itemIntegracaoRHContabil : selecionado.getItens()) {
            if (itemIntegracaoRHContabil.getValorTotalRetencoes().compareTo(itemIntegracaoRHContabil.getValor()) > 0) {
                throw new ExcecaoNegocioGenerica("O Item " + itemIntegracaoRHContabil.getDespesaORC().getCodigo()
                    + " - " + itemIntegracaoRHContabil.getDesdobramento().getCodigo() + " possui retenção maior que seu valor. " +
                    " Valor <b> " + Util.formataValor(itemIntegracaoRHContabil.getValor()) + " </b> e " +
                    " Total de Retenção <b> " + Util.formataValor(itemIntegracaoRHContabil.getValorTotalRetencoes()) + " </b> " +
                    " .");
            }
        }
    }

    private void vincularClasseRetencao() {
        for (RetencaoIntegracaoRHContabil item : selecionado.getRetencoes()) {
            if (item.getClasseCredor() == null && item.getPessoa() != null) {
                List<ClasseCredor> classesDoFornecedor = facade.getClasseCredorFacade().buscarClassesPorPessoa(item.getPessoa(), selecionado.getDataRegistro());
                if (classesDoFornecedor != null && !classesDoFornecedor.isEmpty()) {
                    item.setClasseCredor(classesDoFornecedor.get(0));
                }
            }
        }
    }

    private void validarRetencaoSemPagamento() {
        if (!selecionado.getRetencoesSemPagamento().isEmpty()) {
            throw new ExcecaoNegocioGenerica("Existem retenções sem vincular a um pagamento.");
        }
    }

    private void vincularClasseCredorQuandoVazia() {
        if (selecionado.getId() == null) {
            for (ItemIntegracaoRHContabil item : selecionado.getItens()) {
                if (item.getClasseCredor() == null && item.getFornecedor() != null) {
                    List<ClasseCredor> classesDoFornecedor = facade.getClasseCredorFacade().buscarClassesPorPessoa(item.getFornecedor(), selecionado.getDataRegistro());
                    if (classesDoFornecedor != null && !classesDoFornecedor.isEmpty()) {
                        item.setClasseCredor(classesDoFornecedor.get(0));
                    }
                }
            }
        }
    }

    private void validarSaldoOrcamentario(Boolean somenteAlertar) {
        for (FonteItemIntegracaoRHContabil fonte : selecionado.getFontes()) {
            if (!fonte.isSaldoDisponivel()) {
                String message = "Saldo indispónivel na Dotação <b> " + fonte.getDespesaORC().getCodigo()
                    + " - " + fonte.getDespesaORC().getContaDeDespesa().getCodigo()
                    + " - " + fonte.getFonteDeRecursos().getCodigo() + " </b> ";
                if (somenteAlertar) {
                    FacesUtil.addAtencao(message);
                } else {
                    throw new ExcecaoNegocioGenerica(message);
                }
            }
        }
    }

    public void imprimirRelatorio() {
        try {
            String conteudoRelatorio = facade.montarRelatorio(selecionado);
            Util.geraPDF("Relatório de Integração RH/Contábil", conteudoRelatorio, FacesContext.getCurrentInstance());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    public void imprimirRelatorioPorServidor() {
        try {
            String conteudoRelatorio = facade.montarRelatorioPorServidor(selecionado);
            Util.geraPDF("Relatório de Integração RH/Contábil", conteudoRelatorio, FacesContext.getCurrentInstance());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    public void simularIntegracao() {
        try {
            Util.validarCampos(selecionado);
            adicionarFolhasAoSelecionado();
            adicionarTiposAoSelecionado();
            realizarValidacaoSimularIntegracao();
            inicializarAssistente();
            abrirDialogProgressBar();
            executarPoll();
            simulacao = true;
            future = facade.inicializarSimulacao(assistenteIntegracaoRHContabil);
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e);
        } catch (Exception e) {
            logger.error("error ", e);
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void realizarValidacaoSimularIntegracao() {
        if (selecionado.getTipos().isEmpty()) {
            throw new ValidacaoException("É necessário selecionar pelo menos um tipo.");
        }
        if (selecionado.getFolhasDePagamento().isEmpty()) {
            throw new ValidacaoException("É necessário selecionar pelo menos uma folha de pagamento.");
        }
    }

    public void realizarValidacoes() {
        try {
            Util.validarCampos(selecionado);
            Util.validarCampos(selecionado.getParametro());
            ValidacaoException ve = new ValidacaoException();
            realizarValidacoesExtra(ve);
            for (ItemIntegracaoRHContabil itemIntegracaoRHContabil : selecionado.getItens()) {
                Util.validarCampos(itemIntegracaoRHContabil);
                validarItemIntegracaoRHContabil(ve, itemIntegracaoRHContabil);
            }
            ve.lancarException();
            for (RetencaoIntegracaoRHContabil retencaoIntegracaoRHContabil : selecionado.getRetencoes()) {
                validarClasseCredorRentencao(retencaoIntegracaoRHContabil);
                Util.validarCampos(retencaoIntegracaoRHContabil);
            }
            validarSaldoOrcamentario(Boolean.FALSE);
            selecionado.setSituacao(SituacaoIntegracaoRHContabil.VALIDADO);
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e);
        } catch (Exception e) {
            logger.error("error ", e);
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void validarItemIntegracaoRHContabil(ValidacaoException ve, ItemIntegracaoRHContabil item) {
        if (TipoContabilizacao.EMPENHO.equals(item.getTipoContabilizacao()) && item.getSubConta() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Conta Financeira do empenho " + item.getDespesaORC().getFuncionalProgramatica() + " deve ser informado.");
        }
    }

    private void validarClasseCredorRentencao(RetencaoIntegracaoRHContabil retencaoIntegracaoRHContabil) {
        if (retencaoIntegracaoRHContabil.getClasseCredor() == null) {
            throw new ExcecaoNegocioGenerica("Nenhuma classe de credor foi encontrada para a retenção <b> "
                + retencaoIntegracaoRHContabil.getContaExtraorcamentaria().getCodigo()
                + " </b> e <b>" + retencaoIntegracaoRHContabil.getPessoa()
                + "</b> e valor de <b>" + Util.formataValor(retencaoIntegracaoRHContabil.getValor()) + ". </b>");
        }
    }

    public void finalizarIntegracao() {
        try {
            realizarValidacoes();
            Util.validarCampos(selecionado.getParametro());
            inicializarAssistente();
            abrirDialogProgressBar();
            executarPoll();
            simulacao = false;
            future = facade.finalizarIntegracao(assistenteIntegracaoRHContabil);
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e);
        } catch (Exception e) {
            logger.error("error ", e);
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void inicializarAssistente() {
        assistenteIntegracaoRHContabil = new AssistenteIntegracaoRHContabil();
        assistenteIntegracaoRHContabil.setMensagens(Lists.<String>newArrayList());
        assistenteIntegracaoRHContabil.setIntegracaoRHContabil(selecionado);
    }

    private void realizarValidacoesExtra(ValidacaoException ve) throws ValidacaoException {
        if (folhaDePagamentos != null && folhaDePagamentos.isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Seleciona uma folha de pagamento para prosseguir.");
        }
        if (selecionado.isTipoSelecionado(TipoIntegradorRHContabil.OBRIGACAO_A_PAGAR) && !selecionado.getObrigacoes().isEmpty()) {
            if (selecionado.getParametro().getDataObrigacao() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Data da Obrigação deve ser informado.");
            }
            if (selecionado.getParametro().getTipoDocumentoObrigacao() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo (Obrigação a Pagar) Tipo de Documento deve ser informado.");
            }
            if (selecionado.getParametro().getNumeroDocumentoObrigacao() == null || selecionado.getParametro().getNumeroDocumentoObrigacao().isEmpty()) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo (Obrigação a Pagar) Número deve ser informado.");
            }
            if (selecionado.getParametro().getHistoricoObrigacao() == null || selecionado.getParametro().getHistoricoObrigacao().isEmpty()) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo (Obrigação a Pagar) Histórico deve ser informado.");
            }
        }
        if (selecionado.isTipoSelecionado(TipoIntegradorRHContabil.EMPENHO)) {
            if (selecionado.getParametro().getDataEmpenho() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Data do Empenho deve ser informado.");
            }
            if (selecionado.getParametro().getHistorico() == null || selecionado.getParametro().getHistorico().isEmpty()) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo (Empenho) Histórico deve ser informado.");
            }
        }
        if (selecionado.isTipoSelecionado(TipoIntegradorRHContabil.LIQUIDACAO)) {
            if (selecionado.getParametro().getDataLiquidacao() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Data da Liquidação deve ser informado.");
            }
            if (selecionado.getParametro().getTipoDocumento() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo (Liquidação) Tipo de Documento deve ser informado.");
            }
            if (selecionado.getParametro().getNumeroDocumentoLiquidacao() == null || selecionado.getParametro().getNumeroDocumentoLiquidacao().isEmpty()) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo (Liquidação) Número deve ser informado.");
            }
        }
        if (selecionado.isTipoSelecionado(TipoIntegradorRHContabil.PAGAMENTO)) {
            if (selecionado.getParametro().getDataPrevisaoPagamento() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Previsão de Pagamento deve ser informado.");
            }
            if (selecionado.getParametro().getFinalidadePagamento() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Finalidade do Pagamento deve ser informado.");
            }
            if (selecionado.getParametro().getTipoOperacaoPagto() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Operação deve ser informado.");
            }
            if (selecionado.getParametro().getTipoDocPagto() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo (Pagamento) Tipo de Documento deve ser informado.");
            }
            if (selecionado.getParametro().getDataDocumento() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Data do Documento deve ser informado.");
            }
            if (selecionado.getParametro().getNumeroDocumentoPagamento() == null || selecionado.getParametro().getNumeroDocumentoPagamento().isEmpty()) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Número do Documento deve ser informado.");
            }
        }
        ve.lancarException();
    }

    public void abrirDialogProgressBar() {
        FacesUtil.executaJavaScript("dialogProgressBar.show()");
    }

    public void executarPoll() {
        FacesUtil.executaJavaScript("poll.start()");
    }

    public void finalizarBarraProgressao() {
        if (!assistenteIntegracaoRHContabil.getBarraProgressoItens().getCalculando()) {
            FacesUtil.executaJavaScript("dialogProgressBar.hide()");
            FacesUtil.executaJavaScript("poll.stop()");
            verificarMensagens();
            if (!simulacao) {
                redireciona();
            }
        }
    }

    public void abortar() {
        if (future != null) {
            future.cancel(true);
            assistenteIntegracaoRHContabil.getBarraProgressoItens().finaliza();
            future = null;
        }
    }

    @Override
    public void salvar() {
        try {
            if (isOperacaoNovo()) {
                Util.validarCampos(selecionado);
                selecionado = facade.salvarIntegracao(selecionado);
                redirecionarParaEdita();
                Web.limpaNavegacao();
            } else {
                facade.salvar(selecionado);
                redireciona();
            }
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            logger.error("error ", e);
            descobrirETratarException(e);
        }
    }

    public void redirecionarParaEdita() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "editar/" + selecionado.getId() + "/");
    }

    public void verificarMensagens() {
        if (future != null && future.isDone()) {
            if (assistenteIntegracaoRHContabil.getMensagens().isEmpty()) {
                if (assistenteIntegracaoRHContabil.getIntegracaoRHContabil().getId() == null) {
                    FacesUtil.addOperacaoRealizada("Busca realizada com sucesso.");
                    FacesUtil.atualizarComponente("Formulario");
                } else {
                    FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
                    redireciona();
                }
            } else {
                String mensagemCompleta = "Ocorreram os seguintes erros durante o processo: ";
                for (String mensagem : assistenteIntegracaoRHContabil.getMensagens()) {
                    mensagemCompleta += mensagem;
                }
                FacesUtil.addOperacaoNaoRealizada(mensagemCompleta);
            }
            future = null;
        }
    }

    public List<SelectItem> getFolhasDePagamento() {
        if (selecionado.getMes() != null && selecionado.getExercicio() != null && selecionado.getTipoFolhaDePagamento() != null) {
            List<FolhaDePagamento> values = facade.getFolhaDePagamentoFacade().buscarFolhaPorTipoMesAndExercicio(selecionado.getTipoFolhaDePagamento(), selecionado.getMes(), selecionado.getExercicio());
            folhaDePagamentos.clear();
            folhaDePagamentos.addAll(values);
            return Util.getListSelectItemSemCampoVazio(values.toArray(), false);
        }
        return null;
    }

    public AssistenteIntegracaoRHContabil getAssistenteIntegracaoRHContabil() {
        return assistenteIntegracaoRHContabil;
    }

    public void setAssistenteIntegracaoRHContabil(AssistenteIntegracaoRHContabil assistenteIntegracaoRHContabil) {
        this.assistenteIntegracaoRHContabil = assistenteIntegracaoRHContabil;
    }

    public List<FolhaDePagamento> getFolhaDePagamentos() {
        return folhaDePagamentos;
    }

    public void setFolhaDePagamentos(List<FolhaDePagamento> folhaDePagamentos) {
        this.folhaDePagamentos = folhaDePagamentos;
    }

    public TipoIntegradorRHContabil[] getTipos() {
        return tipos;
    }

    public void setTipos(TipoIntegradorRHContabil[] tipos) {
        this.tipos = tipos;
    }

    public RetencaoIntegracaoRHContabil[] getRetencaoSelecionadas() {
        return retencaoSelecionadas;
    }

    public void setRetencaoSelecionadas(RetencaoIntegracaoRHContabil[] retencaoSelecionadas) {
        this.retencaoSelecionadas = retencaoSelecionadas;
    }

    public List<ItemIntegracaoRHContabil> getItensRetencao() {
        return itensRetencao;
    }

    public void setItensRetencao(List<ItemIntegracaoRHContabil> itensRetencao) {
        this.itensRetencao = itensRetencao;
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public boolean isEmpenhoSelecionado() {
        return selecionado.getTipos() != null && selecionado.isTipoSelecionado(TipoIntegradorRHContabil.EMPENHO);
    }

    public boolean isLiquidacaoSelecionado() {
        return selecionado.getTipos() != null && selecionado.isTipoSelecionado(TipoIntegradorRHContabil.LIQUIDACAO);
    }

    public boolean isPagamentoSelecionado() {
        return selecionado.getTipos() != null && selecionado.isTipoSelecionado(TipoIntegradorRHContabil.PAGAMENTO);
    }

    public void prepararRetencao() {
        this.itensRetencao = Lists.newArrayList();
        this.itensRetencao.addAll(selecionado.getItens());
    }
}
