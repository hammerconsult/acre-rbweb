package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.SolicitacaoEmpenhoEstornoItemVo;
import br.com.webpublico.entidadesauxiliares.SolicitacaoEmpenhoEstornoLoquidacaoVo;
import br.com.webpublico.entidadesauxiliares.SolicitacaoEmpenhoEstornoVo;
import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.enums.OrigemSolicitacaoEmpenho;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ExecucaoContratoEstornoFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-execucao-contrato-estorno",
        pattern = "/execucao-contrato-estorno/novo/",
        viewId = "/faces/administrativo/contrato/execucao-contrato-estorno/edita.xhtml"),

    @URLMapping(id = "editar-execucao-contrato-estorno",
        pattern = "/execucao-contrato-estorno/editar/#{execucaoContratoEstornoControlador.id}/",
        viewId = "/faces/administrativo/contrato/execucao-contrato-estorno/edita.xhtml"),

    @URLMapping(id = "ver-execucao-contrato-estorno",
        pattern = "/execucao-contrato-estorno/ver/#{execucaoContratoEstornoControlador.id}/",
        viewId = "/faces/administrativo/contrato/execucao-contrato-estorno/visualizar.xhtml"),

    @URLMapping(id = "listar-execucao-contrato-estorno",
        pattern = "/execucao-contrato-estorno/listar/",
        viewId = "/faces/administrativo/contrato/execucao-contrato-estorno/lista.xhtml")
})
public class ExecucaoContratoEstornoControlador extends PrettyControlador<ExecucaoContratoEstorno> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(ExecucaoContratoEstornoControlador.class);

    @EJB
    private ExecucaoContratoEstornoFacade facade;
    private List<ExecucaoContratoItem> itensExecucao;
    private List<SolicitacaoEmpenhoEstornoVo> solicitacoesEmpenhoVo;
    private SolicitacaoEmpenhoEstornoVo solicitacaoEmpenhoEstornoVo;
    private SolicitacaoEmpenhoEstornoLoquidacaoVo solicitacaoLiquidacaoVo;
    private ExecucaoContratoEmpenhoEstorno execucaoContratoEstornoSelecionado;

    public ExecucaoContratoEstornoControlador() {
        super(ExecucaoContratoEstorno.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novo-execucao-contrato-estorno", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataLancamento(DataUtil.getDataComHoraAtual(facade.getSistemaFacade().getDataOperacao()));
        instanciarListas();
        preencherEstornoComDadosDaSessao();
    }

    @URLAction(mappingId = "ver-execucao-contrato-estorno", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        ExecucaoContrato execucaoContrato = facade.getExecucaoContratoFacade().recuperarComDependenciasItens(selecionado.getExecucaoContrato().getId());
        itensExecucao = execucaoContrato.getItens();
        selecionado.setExecucaoContrato(execucaoContrato);
    }

    @URLAction(mappingId = "editar-execucao-contrato-estorno", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/execucao-contrato-estorno/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    private void preencherEstornoComDadosDaSessao() {
        ExecucaoContratoEmpenho execucao = (ExecucaoContratoEmpenho) Web.pegaDaSessao("EXECUCAO_CONTRATO");
        if (execucao != null) {
            selecionado.setExecucaoContrato(execucao.getExecucaoContrato());
            recuperarDadosExecucao();
            try {
                SolicitacaoEmpenhoEstornoVo sol = solicitacoesEmpenhoVo.get(0);
                if (sol.getSolicitacaoEmpenho().equals(execucao.getSolicitacaoEmpenho())) {
                    sol.setSolicitacaoEmpenho(execucao.getSolicitacaoEmpenho());
                    sol.setEmpenho(execucao.getEmpenho());
                    selecionarSolicitacaoEmpenhoVo(sol);
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
                logger.error("Erro ao selecionar execução de contrato da sessão: {}", ex);
                FacesUtil.addOperacaoNaoRealizada("Não foi possível  selecionar execução de contrato da sessão: " + ex.getMessage());
            }
            FacesUtil.atualizarComponente("Formulario");
        }
    }

    @Override
    public void salvar() {
        try {
            validarSalvar();
            selecionado = facade.salvarRetornando(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + getUrlKeyValue());
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    @Override
    public void excluir() {
        try {
            Contrato contrato = selecionado.getExecucaoContrato().getContrato();
            facade.remover(selecionado);
            facade.getReprocessamentoSaldoContratoFacade().reprocessarUnicoContrato(contrato);
            redireciona();
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoExcluir());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    private void validarSalvar() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCampos(selecionado);
        if (selecionado.getEstornosEmpenho() == null || selecionado.getEstornosEmpenho().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhum empenho adicionado para ser estornado.");
        }
        ve.lancarException();
    }

    public void limparDadosExecucao() {
        selecionado.setExecucaoContrato(null);
        instanciarListas();
    }

    public boolean hasSolicitacaoEmpenhoVoSelecionada() {
        return solicitacaoEmpenhoEstornoVo != null;
    }

    public boolean hasExecucaoContratoSelecionada() {
        return selecionado.getExecucaoContrato() != null;
    }

    public void selecionarSolicitacaoEmpenhoVo(SolicitacaoEmpenhoEstornoVo sol) {
        try {
            solicitacoesEmpenhoVo.stream().filter(solList -> !sol.equals(solList)).forEach(solList -> solList.setBloqueado(true));
            sol.setSelecionado(Boolean.TRUE);
            setSolicitacaoEmpenhoEstornoVo(sol);
            criarItensEstorno();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            FacesUtil.addError("Erro:", e.getMessage());
        }
    }

    public void desselecionarSolicitacaoEmpenhoVo(SolicitacaoEmpenhoEstornoVo sol) {
        solicitacoesEmpenhoVo.forEach(solList -> solList.setBloqueado(false));
        sol.setSelecionado(Boolean.FALSE);
        setSolicitacaoEmpenhoEstornoVo(null);
    }

    public List<ExecucaoContrato> completarExecucao(String parte) {
        return facade.getExecucaoContratoFacade().buscarExecucaoContratoComSaldoAEstornar(parte.trim());
    }

    public void instanciarListas() {
        solicitacoesEmpenhoVo = Lists.newArrayList();
    }

    public void recuperarDadosExecucao() {
        try {
            instanciarListas();
            ExecucaoContrato execucaoContrato = facade.getExecucaoContratoFacade().recuperarComDependencias(selecionado.getExecucaoContrato().getId());
            facade.verificarSeExisteSolicitacaoEstornoExecucaoPendentes(execucaoContrato);
            selecionado.setExecucaoContrato(execucaoContrato);
            itensExecucao = selecionado.getExecucaoContrato().getItens();
            criarSolicitacaoEstornoEmpenhoVo();
        } catch (ValidacaoException ve) {
            instanciarListas();
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public boolean renderizaBotaoExcluir() {
        return !selecionado.hasSolicitacaoEstornoEmpenhoEstornada();
    }

    public boolean hasEstornosLancados() {
        return facade.hasEstornosLancados(selecionado);
    }

    public void selecionarVoLiquidacao(SolicitacaoEmpenhoEstornoLoquidacaoVo voLiquidacao) {
        this.solicitacaoLiquidacaoVo = voLiquidacao;
    }

    public void criarSolicitacaoEstornoEmpenhoVo() {
        instanciarListas();
        setSolicitacaoEmpenhoEstornoVo(null);
        for (ExecucaoContratoEmpenho exEmp : selecionado.getExecucaoContrato().getEmpenhos()) {
            if (exEmp.getEmpenho() != null) {
                List<Empenho> empenhos = Lists.newArrayList(exEmp.getEmpenho());
                List<Empenho> empenhosRestos = facade.getEmpenhoFacade().buscarEmpenhoRestoPagarExercicio(exEmp.getEmpenho(), facade.getSistemaFacade().getExercicioCorrente());
                if (!Util.isListNullOrEmpty(empenhosRestos)) {
                    empenhos = empenhosRestos;
                }
                for (Empenho empenho : empenhos) {
                    solicitacoesEmpenhoVo.add(facade.criarSolicitacaoEmpenhoEstornoVoPorEmpenho(selecionado, empenho, exEmp.getSolicitacaoEmpenho()));
                }
            } else {
                SolicitacaoEmpenhoEstornoVo solEmpVo = new SolicitacaoEmpenhoEstornoVo(SolicitacaoEmpenhoEstornoVo.TipoEstornoExecucao.SOLICITACAO_EMPENHO);
                solEmpVo.setLiquidacoes(Lists.<SolicitacaoEmpenhoEstornoLoquidacaoVo>newArrayList());
                solEmpVo.setSolicitacaoEmpenho(exEmp.getSolicitacaoEmpenho());
                solEmpVo.setCategoria(CategoriaOrcamentaria.NORMAL);

                BigDecimal valorSolicitacaoEmpenhoExecutado = getValorSolicitacaoEmpenhoExecutado(exEmp.getSolicitacaoEmpenho());
                solEmpVo.setSaldo(exEmp.getSolicitacaoEmpenho().getValor().subtract(valorSolicitacaoEmpenhoExecutado));
                solicitacoesEmpenhoVo.add(solEmpVo);
            }
            ordenarVoEmpenho();
        }
    }

    public void ordenarVoEmpenho() {
        for (SolicitacaoEmpenhoEstornoVo voEmpenho : solicitacoesEmpenhoVo) {
            Collections.sort(voEmpenho.getLiquidacoes());
        }
        Collections.sort(solicitacoesEmpenhoVo);
    }

    public BigDecimal getValorSolicitacaoEmpenhoExecutado(SolicitacaoEmpenho solEmp) {
        BigDecimal valorSolEmpSalvo = facade.recuperarValorSolicitacaoEstornoEmpenho(solEmp, selecionado.getExecucaoContrato());
        BigDecimal valor = BigDecimal.ZERO;
        for (ExecucaoContratoEmpenhoEstorno execucao : selecionado.getEstornosEmpenho()) {
            if (solEmp.equals(execucao.getSolicitacaoEmpenhoEstorno().getSolicitacaoEmpenho())) {
                valor = valor.add(execucao.getSolicitacaoEmpenhoEstorno().getValor());
            }
        }
        return valorSolEmpSalvo.add(valor);
    }

    public void selecionarEstornoExecucao(ExecucaoContratoEmpenhoEstorno execucaoContratoEstorno) {
        this.execucaoContratoEstornoSelecionado = execucaoContratoEstorno;
    }

    public void confirmarEmpenho() {
        try {
            atribuirEmpenhoSelecionadoPorLiquidacao();
            validarConfirmarEmpenho();
            validarItensVo();
            ExecucaoContratoEmpenhoEstorno execucaoContEmpEst = new ExecucaoContratoEmpenhoEstorno();
            execucaoContEmpEst.setExecucaoContratoEstorno(selecionado);
            criarItensExecucaoEmpenhoEstorno(execucaoContEmpEst);
            criarSolicitacaoEmpenhoEstorno(execucaoContEmpEst);

            execucaoContEmpEst.setValor(execucaoContEmpEst.getValorTotalItens());
            Util.adicionarObjetoEmLista(selecionado.getEstornosEmpenho(), execucaoContEmpEst);
            selecionado.calcularValorTotal();

            criarSolicitacaoEstornoEmpenhoVo();
            FacesUtil.atualizarComponente("Formulario:panel-solicitacao");
            FacesUtil.atualizarComponente("Formulario:panel-estornos");
            FacesUtil.atualizarComponente("Formulario:gridGeral");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void atribuirEmpenhoSelecionadoPorLiquidacao() {
        if (solicitacaoEmpenhoEstornoVo == null) {
            for (SolicitacaoEmpenhoEstornoVo empenho : solicitacoesEmpenhoVo) {
                for (SolicitacaoEmpenhoEstornoLoquidacaoVo liquidacao : empenho.getLiquidacoes()) {
                    if (liquidacao.getSelecionado()) {
                        solicitacaoEmpenhoEstornoVo = liquidacao.getVoExecucaoContratoEstorno();
                        solicitacaoEmpenhoEstornoVo.setSaldo(solicitacaoEmpenhoEstornoVo.getValorTotalLiquidacao());
                        break;
                    }
                }
            }
        } else {
            if (solicitacaoEmpenhoEstornoVo.hasLiquidacoes()) {
                solicitacaoEmpenhoEstornoVo.setSaldo(solicitacaoEmpenhoEstornoVo.getSaldoTotalComLiquidacoes());
            }
        }
    }

    private void validarConfirmarEmpenho() {
        validarEmpenhoObrigatorio();
        ValidacaoException ve = new ValidacaoException();
        if (solicitacaoEmpenhoEstornoVo.getSaldo().compareTo(BigDecimal.ZERO) == 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O empenho selecionado não possui saldo para ser estornado.");
            setSolicitacaoEmpenhoEstornoVo(null);
        }

        for (ExecucaoContratoEmpenhoEstorno execucao : selecionado.getEstornosEmpenho()) {
            if (solicitacaoEmpenhoEstornoVo.getEmpenho().equals(execucao.getSolicitacaoEmpenhoEstorno().getEmpenho())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido mais de uma solicitação de estorno para o mesmo empenho.");
                setSolicitacaoEmpenhoEstornoVo(null);
            }
        }
        ve.lancarException();
    }

    public void validarEmpenhoObrigatorio() {
        ValidacaoException ve = new ValidacaoException();
        if (solicitacaoEmpenhoEstornoVo == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório selecionar uma solicitação de empenho para continuar.");
        }
        ve.lancarException();
    }

    private void criarItensEstorno() {
        solicitacaoEmpenhoEstornoVo.setItens(facade.criarItensEstorno(selecionado, solicitacaoEmpenhoEstornoVo, itensExecucao));
    }

    private void criarItensExecucaoEmpenhoEstorno(ExecucaoContratoEmpenhoEstorno execucaoContratoEmpenhoEstorno) {
        for (SolicitacaoEmpenhoEstornoItemVo itemVo : solicitacaoEmpenhoEstornoVo.getItens()) {
            if (itemVo.getSelecionado()) {
                ExecucaoContratoEmpenhoEstornoItem novoItem = new ExecucaoContratoEmpenhoEstornoItem();
                novoItem.setExecucaoContratoEmpenhoEst(execucaoContratoEmpenhoEstorno);
                novoItem.setExecucaoContratoItem(itemVo.getExecucaoContratoItem());
                novoItem.setQuantidade(itemVo.getQuantidade());
                novoItem.setValorUnitario(itemVo.getValorUnitario());
                novoItem.setValorTotal(itemVo.getValorTotal());
                Util.adicionarObjetoEmLista(execucaoContratoEmpenhoEstorno.getItens(), novoItem);
            }
        }
    }

    public void validarItensVo() {
        ValidacaoException ve = new ValidacaoException();
        boolean itemSelecionado = solicitacaoEmpenhoEstornoVo.getItens().stream().anyMatch(SolicitacaoEmpenhoEstornoItemVo::getSelecionado);
        if (!itemSelecionado) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A solicitação de estorno não possui itens com valor maior que zero(0).");
        }
        ve.lancarException();
        solicitacaoEmpenhoEstornoVo.getItens().stream()
            .filter(item -> item.getSelecionado() && item.getQuantidade().compareTo(BigDecimal.ZERO) == 0)
            .map(item -> "A quantidade do item " + item.getExecucaoContratoItem().getItemContrato().getNumero() + " deve ser maior que zero(0).")
            .forEach(ve::adicionarMensagemDeOperacaoNaoPermitida);

        if (getValorTotalItensSelecionado().compareTo(solicitacaoEmpenhoEstornoVo.getSaldo()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O valor total dos itens deve ser menor ou igual ao saldo do empenho. Saldo do Empenho "
                + Util.formataValor(solicitacaoEmpenhoEstornoVo.getSaldo()));
        }
        for (SolicitacaoEmpenhoEstornoLoquidacaoVo liquidacao : solicitacaoEmpenhoEstornoVo.getLiquidacoes()) {
            if (liquidacao.getSelecionado() && getValorTotalItensSelecionado().compareTo(liquidacao.getSaldo()) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor total dos itens não atingiu o valor necessário para a solicitação de estorno de liquidação "
                    + Util.formataValor(liquidacao.getSaldo()));
            }
        }
        ve.lancarException();
    }

    public BigDecimal getValorTotalItensSelecionado() {
        BigDecimal total = BigDecimal.ZERO;
        for (SolicitacaoEmpenhoEstornoItemVo item : solicitacaoEmpenhoEstornoVo.getItens()) {
            if (item.getSelecionado()) {
                total = total.add(item.getValorTotal());
            }
        }
        return total;
    }

    public void processarQuantidadeItem(SolicitacaoEmpenhoEstornoItemVo item) {
        try {
            validarQuantidadeItem(item);
            item.calcularValorTotal();
            selecionado.calcularValorTotal();
        } catch (ValidacaoException ve) {
            item.setValorTotal(BigDecimal.ZERO);
            item.setQuantidade(BigDecimal.ZERO);
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void marcarLiquidacao(SolicitacaoEmpenhoEstornoLoquidacaoVo liquidacao, SolicitacaoEmpenhoEstornoVo voEmpenho) {
        liquidacao.setSelecionado(true);
        if (hasSolicitacaoEmpenhoVoSelecionada() && !voEmpenho.equals(solicitacaoEmpenhoEstornoVo)) {
            solicitacaoEmpenhoEstornoVo = null;
        }
        if (!hasSolicitacaoEmpenhoVoSelecionada() && voEmpenho.getSaldo().compareTo(BigDecimal.ZERO) == 0) {
            solicitacaoEmpenhoEstornoVo = voEmpenho;
        }

        boolean itemSelecionado = isTodasLiquidacoesSelecionadas(voEmpenho);
        if (itemSelecionado) {
            for (SolicitacaoEmpenhoEstornoVo empenho : solicitacoesEmpenhoVo) {
                if (!empenho.equals(voEmpenho)) {
                    empenho.setBloqueado(true);
                }
            }
        }
    }

    public boolean isTodasLiquidacoesSelecionadas(SolicitacaoEmpenhoEstornoVo voEmpenho) {
        boolean itemSelecionado = false;
        for (SolicitacaoEmpenhoEstornoLoquidacaoVo item : voEmpenho.getLiquidacoes()) {
            if (item.getSelecionado()) {
                itemSelecionado = true;
            }
        }
        return itemSelecionado;
    }

    public void desmarcarLiquidacao(SolicitacaoEmpenhoEstornoLoquidacaoVo liquidacao, SolicitacaoEmpenhoEstornoVo voEmpenho) {
        liquidacao.setSelecionado(false);

        if (hasSolicitacaoEmpenhoVoSelecionada() && voEmpenho.getSaldo().compareTo(BigDecimal.ZERO) == 0) {
            solicitacaoEmpenhoEstornoVo = null;
        }

        boolean itemSelecionado = isTodasLiquidacoesSelecionadas(voEmpenho);
        if (!itemSelecionado) {
            for (SolicitacaoEmpenhoEstornoVo empenho : solicitacoesEmpenhoVo) {
                if (!empenho.equals(voEmpenho)) {
                    empenho.setBloqueado(false);
                }
            }
        }
    }

    private void validarValorTotalItem(SolicitacaoEmpenhoEstornoItemVo itemVo) {
        ValidacaoException ve = new ValidacaoException();
        ItemContrato itemContrato = itemVo.getExecucaoContratoItem().getItemContrato();
        if (itemVo.getValorTotal() == null || itemVo.getValorTotal().compareTo(BigDecimal.ZERO) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O 'Valor Total' do item " + itemContrato.getNumero() + " deve ser positivo.");
        }
        ve.lancarException();
        if (itemVo.getValorTotal().compareTo(itemVo.getSaldoDisponivel()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O 'Valor Total' do item " + itemContrato.getNumero() + " ultrapassa o saldo disponível de " + Util.formataValor(itemVo.getSaldoDisponivel()) + ".");
        }
        ve.lancarException();
    }

    private void validarQuantidadeItem(SolicitacaoEmpenhoEstornoItemVo itemVo) {
        ExecucaoContratoItem itemExec = itemVo.getExecucaoContratoItem();
        ValidacaoException ve = new ValidacaoException();
        if (itemVo.isTipoControleQuantidade() && itemVo.getQuantidade() == null || itemVo.getQuantidade().compareTo(BigDecimal.ZERO) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A quantidade do item " + itemExec.getItemContrato().getNumero() + " deve ser um positivo.");
        }
        ve.lancarException();
        itemVo.calcularValorTotal();
        validarValorTotalItem(itemVo);
    }

    public void processarValorTotalItem(SolicitacaoEmpenhoEstornoItemVo item) {
        try {
            validarValorTotalItem(item);
            item.setQuantidade(BigDecimal.ONE);
            selecionado.calcularValorTotal();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            item.setValorTotal(BigDecimal.ZERO);
        }
    }

    public void removerExecucaoContratoEmpenhoEstorno(ExecucaoContratoEmpenhoEstorno estorno) {
        selecionado.getEstornosEmpenho().remove(estorno);
        criarSolicitacaoEstornoEmpenhoVo();
        selecionado.calcularValorTotal();
    }

    public void removerItem(SolicitacaoEmpenhoEstornoItemVo item) {
        solicitacaoEmpenhoEstornoVo.getItens().remove(item);
    }

    private void criarSolicitacaoEmpenhoEstorno(ExecucaoContratoEmpenhoEstorno execucaoContratoEmpenhoEstorno) {
        Empenho empenho = solicitacaoEmpenhoEstornoVo.getEmpenho();
        SolicitacaoEmpenhoEstorno novaSolEst = new SolicitacaoEmpenhoEstorno(solicitacaoEmpenhoEstornoVo.getSolicitacaoEmpenho(), empenho, OrigemSolicitacaoEmpenho.CONTRATO);
        novaSolEst.setDataSolicitacao(facade.getSistemaFacade().getDataOperacao());
        novaSolEst.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        novaSolEst.setValor(execucaoContratoEmpenhoEstorno.getValorTotalItens());
        novaSolEst.setHistorico(facade.getSolicitacaoEmpenhoEstornoFacade().getHistoricoSolicitacaoEmpenho(novaSolEst.getClasseCredor(), novaSolEst.getFonteDespesaORC(), selecionado.getExecucaoContrato()));
        execucaoContratoEmpenhoEstorno.setSolicitacaoEmpenhoEstorno(novaSolEst);

        if (solicitacaoEmpenhoEstornoVo.hasLiquidacoes()) {
            for (SolicitacaoEmpenhoEstornoLoquidacaoVo execucaoLiquidacao : solicitacaoEmpenhoEstornoVo.getLiquidacoes()) {
                Liquidacao liquidacao = execucaoLiquidacao.getLiquidacao();
                if (liquidacao.getSaldo().compareTo(BigDecimal.ZERO) > 0 && execucaoLiquidacao.getSelecionado()) {
                    SolicitacaoLiquidacaoEstorno novaSolLiqEst = new SolicitacaoLiquidacaoEstorno();
                    novaSolLiqEst.setLiquidacao(liquidacao);
                    novaSolLiqEst.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
                    novaSolLiqEst.setValor(liquidacao.getSaldo());
                    novaSolLiqEst.setHistorico(novaSolEst.getHistorico());

                    ExecucaoContratoLiquidacaoEstorno novaExecLiqEst = new ExecucaoContratoLiquidacaoEstorno();
                    novaExecLiqEst.setExecucaoContratoEmpenhoEst(execucaoContratoEmpenhoEstorno);
                    novaExecLiqEst.setSolicitacaoLiquidacaoEst(novaSolLiqEst);

                    Util.adicionarObjetoEmLista(execucaoContratoEmpenhoEstorno.getSolicitacoesLiquidacaoEstorno(), novaExecLiqEst);
                }
            }
        }
    }

    public List<ExecucaoContratoItem> getItensExecucao() {
        return itensExecucao;
    }

    public void setItensExecucao(List<ExecucaoContratoItem> itensExecucao) {
        this.itensExecucao = itensExecucao;
    }

    public List<SolicitacaoEmpenhoEstornoVo> getSolicitacoesEmpenhoVo() {
        return solicitacoesEmpenhoVo;
    }

    public void setSolicitacoesEmpenhoVo(List<SolicitacaoEmpenhoEstornoVo> solicitacoesEmpenhoVo) {
        this.solicitacoesEmpenhoVo = solicitacoesEmpenhoVo;
    }

    public SolicitacaoEmpenhoEstornoVo getSolicitacaoEmpenhoEstornoVo() {
        return solicitacaoEmpenhoEstornoVo;
    }

    public void setSolicitacaoEmpenhoEstornoVo(SolicitacaoEmpenhoEstornoVo solicitacaoEmpenhoEstornoVo) {
        this.solicitacaoEmpenhoEstornoVo = solicitacaoEmpenhoEstornoVo;
    }

    public ExecucaoContratoEmpenhoEstorno getExecucaoContratoEstornoSelecionado() {
        return execucaoContratoEstornoSelecionado;
    }

    public void setExecucaoContratoEstornoSelecionado(ExecucaoContratoEmpenhoEstorno execucaoContratoEstornoSelecionado) {
        this.execucaoContratoEstornoSelecionado = execucaoContratoEstornoSelecionado;
    }

    public SolicitacaoEmpenhoEstornoLoquidacaoVo getSolicitacaoLiquidacaoVo() {
        return solicitacaoLiquidacaoVo;
    }

    public void setSolicitacaoLiquidacaoVo(SolicitacaoEmpenhoEstornoLoquidacaoVo solicitacaoLiquidacaoVo) {
        this.solicitacaoLiquidacaoVo = solicitacaoLiquidacaoVo;
    }
}
