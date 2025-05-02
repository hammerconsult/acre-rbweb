package br.com.webpublico.controle.administrativo;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.controle.Web;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.SolicitacaoEmpenhoEstornoItemVo;
import br.com.webpublico.entidadesauxiliares.SolicitacaoEmpenhoEstornoLoquidacaoVo;
import br.com.webpublico.entidadesauxiliares.SolicitacaoEmpenhoEstornoVo;
import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.enums.OrigemSolicitacaoEmpenho;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.administrativo.ExecucaoProcessoEstornoFacade;
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
    @URLMapping(id = "novo-execucao-processo-estorno",
        pattern = "/execucao-sem-contrato-estorno/novo/",
        viewId = "/faces/administrativo/licitacao/execucao-sem-contrato-estorno/edita.xhtml"),

    @URLMapping(id = "editar-execucao-processo-estorno",
        pattern = "/execucao-sem-contrato-estorno/editar/#{execucaoProcessoEstornoControlador.id}/",
        viewId = "/faces/administrativo/licitacao/execucao-sem-contrato-estorno/edita.xhtml"),

    @URLMapping(id = "ver-execucao-processo-estorno",
        pattern = "/execucao-sem-contrato-estorno/ver/#{execucaoProcessoEstornoControlador.id}/",
        viewId = "/faces/administrativo/licitacao/execucao-sem-contrato-estorno/visualizar.xhtml"),

    @URLMapping(id = "listar-execucao-processo-estorno",
        pattern = "/execucao-sem-contrato-estorno/listar/",
        viewId = "/faces/administrativo/licitacao/execucao-sem-contrato-estorno/lista.xhtml")
})
public class ExecucaoProcessoEstornoControlador extends PrettyControlador<ExecucaoProcessoEstorno> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(ExecucaoProcessoEstornoControlador.class);
    @EJB
    private ExecucaoProcessoEstornoFacade facade;
    private List<ExecucaoProcessoItem> itensExecucao;
    private List<SolicitacaoEmpenhoEstornoVo> solicitacoesEmpenhoVo;
    private SolicitacaoEmpenhoEstornoVo solicitacaoEmpenhoEstornoVo;
    private ExecucaoProcessoEmpenhoEstorno execucaoProcessoEstornoSelecionado;

    public ExecucaoProcessoEstornoControlador() {
        super(ExecucaoProcessoEstorno.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novo-execucao-processo-estorno", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataLancamento(DataUtil.getDataComHoraAtual(facade.getSistemaFacade().getDataOperacao()));
        instanciarListas();
        preencherEstornoComDadosDaSessao();
    }

    @URLAction(mappingId = "ver-execucao-processo-estorno", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        ExecucaoProcesso execucaoProcesso = facade.getExecucaoProcessoFacade().recuperarComDependenciasItens(selecionado.getExecucaoProcesso().getId());
        itensExecucao = execucaoProcesso.getItens();
        selecionado.setExecucaoProcesso(execucaoProcesso);
    }

    @URLAction(mappingId = "editar-execucao-processo-estorno", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/execucao-sem-contrato-estorno/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    private void preencherEstornoComDadosDaSessao() {
        ExecucaoProcessoEmpenho execucao = (ExecucaoProcessoEmpenho) Web.pegaDaSessao("EXECUCAO_PROCESSO");
        if (execucao != null) {
            selecionado.setExecucaoProcesso(execucao.getExecucaoProcesso());
            recuperarDadosExecucao();
            try {
                SolicitacaoEmpenhoEstornoVo sol = solicitacoesEmpenhoVo.get(0);
                if (sol.getSolicitacaoEmpenho().equals(execucao.getSolicitacaoEmpenho())) {
                    sol.setSolicitacaoEmpenho(execucao.getSolicitacaoEmpenho());
                    sol.setEmpenho(execucao.getEmpenho());
                    selecionarSolicitacaoEmpenhoVo(sol);
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
                logger.error("Erro ao selecionar execução de processo da sessão: {}", ex);
                FacesUtil.addOperacaoNaoRealizada("Não foi possível  selecionar execução de processo da sessão: " + ex.getMessage());
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
            validarExclusao();
            facade.remover(selecionado);
            redireciona();
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoExcluir());
        } catch (ValidacaoException ex) {
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

    private void validarExclusao() {
        ValidacaoException ve = new ValidacaoException();
        ExecucaoProcesso execucao = facade.getExecucaoProcessoFacade().recuperarUltimaExecucao(selecionado.getExecucaoProcesso().getIdProcesso());
        if (execucao != null && !execucao.equals(selecionado.getExecucaoProcesso())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Exclusão não permitida, pois existe um lançamneto posterior a execução que está sendo excluída.");
        }
        if (selecionado.hasSolicitacaoEstornoEmpenhoEstornada()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Exclusão não permitida, pois a(s) solicitação(ões) de estorno de empenho já teve seu estorno realizado.");
        }
        ve.lancarException();
    }

    public void limparDadosExecucao() {
        selecionado.setExecucaoProcesso(null);
        instanciarListas();
    }

    public boolean hasExecucaoSelecionada() {
        return selecionado.getExecucaoProcesso() != null;
    }

    public void selecionarSolicitacaoEmpenhoVo(SolicitacaoEmpenhoEstornoVo sol) {
        solicitacoesEmpenhoVo.stream().filter(solList -> !sol.equals(solList)).forEach(solList -> solList.setBloqueado(true));
        sol.setSelecionado(Boolean.TRUE);
        setSolicitacaoEmpenhoEstornoVo(sol);
        facade.criarItensEstorno(selecionado, solicitacaoEmpenhoEstornoVo, itensExecucao);
    }

    public void desselecionarSolicitacaoEmpenhoVo(SolicitacaoEmpenhoEstornoVo sol) {
        solicitacoesEmpenhoVo.forEach(solList -> solList.setBloqueado(false));
        sol.setSelecionado(Boolean.FALSE);
        setSolicitacaoEmpenhoEstornoVo(null);
    }

    public List<ExecucaoProcesso> completarExecucao(String parte) {
        return facade.getExecucaoProcessoFacade().buscarExecucoesProcesso(parte.trim());
    }

    public void instanciarListas() {
        solicitacoesEmpenhoVo = Lists.newArrayList();
    }

    public void recuperarDadosExecucao() {
        try {
            instanciarListas();
            ExecucaoProcesso execucao = facade.getExecucaoProcessoFacade().recuperarComDependencias(selecionado.getExecucaoProcesso().getId());
            verificarSeExisteSolicitacaoEstornoPendentes(execucao);
            selecionado.setExecucaoProcesso(execucao);
            itensExecucao = selecionado.getExecucaoProcesso().getItens();
            criarSolicitacaoEstornoEmpenhoVo();
        } catch (ValidacaoException ve) {
            instanciarListas();
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void verificarSeExisteSolicitacaoEstornoPendentes(ExecucaoProcesso execucaoProcesso) {
        ValidacaoException ve = new ValidacaoException();
        List<SolicitacaoEmpenhoEstorno> solicitacoes = facade.buscarSolicitacoesEmpenhoEstorno(execucaoProcesso);
        if (!solicitacoes.isEmpty()) {
            for (SolicitacaoEmpenhoEstorno sol : solicitacoes) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Para continuar é necessário realizar o estorno da solicitação " + sol + ".");
            }
        }
        ve.lancarException();
    }

    public boolean renderizaBotaoExcluir() {
        return !selecionado.hasSolicitacaoEstornoEmpenhoEstornada();
    }

    public boolean hasEstornosLancados() {
        if (selecionado != null) {
            return selecionado.getEstornosEmpenho() != null
                && !selecionado.getEstornosEmpenho().isEmpty();
        }
        return false;
    }

    public void criarSolicitacaoEstornoEmpenhoVo() {
        instanciarListas();
        setSolicitacaoEmpenhoEstornoVo(null);
        for (ExecucaoProcessoEmpenho exEmp : selecionado.getExecucaoProcesso().getEmpenhos()) {
            if (exEmp.getEmpenho() != null) {
                List<Empenho> empenhos = Lists.newArrayList(exEmp.getEmpenho());
                List<Empenho> empenhosRestos = facade.getEmpenhoFacade().buscarEmpenhoRestoPagarExercicio(exEmp.getEmpenho(), facade.getSistemaFacade().getExercicioCorrente());
                if (!Util.isListNullOrEmpty(empenhosRestos)) {
                    empenhos = empenhosRestos;
                }
                for (Empenho empenho : empenhos) {
                    solicitacoesEmpenhoVo.add(facade.criarSolicitacaoEmpenhoEstornoVo(selecionado, empenho, exEmp.getSolicitacaoEmpenho()));
                }
            } else {
                SolicitacaoEmpenhoEstornoVo solEmpVo = new SolicitacaoEmpenhoEstornoVo(SolicitacaoEmpenhoEstornoVo.TipoEstornoExecucao.SOLICITACAO_EMPENHO);
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

    public BigDecimal getValorEmpenhoExecutado(Empenho empenho) {
        BigDecimal valorSolEmpSalvo = facade.getValorSolicitacaoEstornoEmpenhoAguardandoEstornoEmpenho(empenho, selecionado.getExecucaoProcesso());
        BigDecimal valor = BigDecimal.ZERO;
        for (ExecucaoProcessoEmpenhoEstorno execucao : selecionado.getEstornosEmpenho()) {
            if (empenho.equals(execucao.getSolicitacaoEmpenhoEstorno().getEmpenho())) {
                valor = valor.add(execucao.getSolicitacaoEmpenhoEstorno().getValor());
            }
        }
        return valorSolEmpSalvo.add(valor);
    }

    public BigDecimal getValorSolicitacaoEmpenhoExecutado(SolicitacaoEmpenho solEmp) {
        BigDecimal valorSolEmpSalvo = facade.getValorSolicitacaoEstornoEmpenho(solEmp, selecionado.getExecucaoProcesso());
        BigDecimal valor = BigDecimal.ZERO;
        for (ExecucaoProcessoEmpenhoEstorno execucao : selecionado.getEstornosEmpenho()) {
            if (solEmp.equals(execucao.getSolicitacaoEmpenhoEstorno().getSolicitacaoEmpenho())) {
                valor = valor.add(execucao.getSolicitacaoEmpenhoEstorno().getValor());
            }
        }
        return valorSolEmpSalvo.add(valor);
    }

    public void selecionarEstornoExecucao(ExecucaoProcessoEmpenhoEstorno execProcEmpEst) {
        this.execucaoProcessoEstornoSelecionado = execProcEmpEst;
    }

    public void confirmarEmpenho() {
        try {
            validarConfirmarEmpenho();
            validarItensVo();
            ExecucaoProcessoEmpenhoEstorno execucaoContEmpEst = new ExecucaoProcessoEmpenhoEstorno();
            execucaoContEmpEst.setExecucaoProcessoEstorno(selecionado);
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

    private void validarConfirmarEmpenho() {
        validarEmpenhoObrigatorio();
        ValidacaoException ve = new ValidacaoException();
        if (solicitacaoEmpenhoEstornoVo.getSaldo().compareTo(BigDecimal.ZERO) == 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O empenho selecionado não possui saldo para ser estornado.");
            setSolicitacaoEmpenhoEstornoVo(null);
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

    private BigDecimal getValorAdicionadoEstorno(ExecucaoProcessoItem itemExecucao) {
        BigDecimal total = BigDecimal.ZERO;
        if (hasEstornosLancados()) {
            for (ExecucaoProcessoEmpenhoEstorno exEmpEst : selecionado.getEstornosEmpenho()) {
                for (ExecucaoProcessoEmpenhoEstornoItem itemEstorno : exEmpEst.getItens()) {
                    if (itemEstorno.getExecucaoProcessoItem().equals(itemExecucao)
                        && exEmpEst.getSolicitacaoEmpenhoEstorno().getSolicitacaoEmpenho().getFonteDespesaORC().equals(solicitacaoEmpenhoEstornoVo.getSolicitacaoEmpenho().getFonteDespesaORC())) {
                        total = total.add(itemEstorno.getValorTotal());
                    }
                }
            }
        }
        return total;
    }

    private void criarItensExecucaoEmpenhoEstorno(ExecucaoProcessoEmpenhoEstorno execProcEmpEst) {
        for (SolicitacaoEmpenhoEstornoItemVo itemVo : solicitacaoEmpenhoEstornoVo.getItens()) {
            if (itemVo.getSelecionado()) {
                ExecucaoProcessoEmpenhoEstornoItem novoItem = new ExecucaoProcessoEmpenhoEstornoItem();
                novoItem.setExecucaoProcessoEmpenhoEstorno(execProcEmpEst);
                novoItem.setExecucaoProcessoItem(itemVo.getExecucaoProcessoItem());
                novoItem.setQuantidade(itemVo.getQuantidade());
                novoItem.setValorUnitario(itemVo.getValorUnitario());
                novoItem.setValorTotal(itemVo.getValorTotal());
                Util.adicionarObjetoEmLista(execProcEmpEst.getItens(), novoItem);
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
            .map(item -> "A quantidade do item " + item.getExecucaoProcessoItem().getItemProcessoCompra().getNumero() + " deve ser maior que zero(0).")
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

    private void validarValorTotalItem(SolicitacaoEmpenhoEstornoItemVo itemVo) {
        ValidacaoException ve = new ValidacaoException();
        ItemProcessoDeCompra itemProc = itemVo.getExecucaoProcessoItem().getItemProcessoCompra();
        if (itemVo.getValorTotal() == null || itemVo.getValorTotal().compareTo(BigDecimal.ZERO) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O 'Valor Total' do item " + itemProc.getNumero() + " deve ser positivo.");
        }
        ve.lancarException();
        if (itemVo.getValorTotal().compareTo(itemVo.getSaldoDisponivel()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O 'Valor Total' do item " + itemProc.getNumero() + " ultrapassa o saldo disponível de " + Util.formataValor(itemVo.getSaldoDisponivel()) + ".");
        }
        ve.lancarException();
    }

    private void validarQuantidadeItem(SolicitacaoEmpenhoEstornoItemVo itemVo) {
        ExecucaoProcessoItem itemExec = itemVo.getExecucaoProcessoItem();
        ValidacaoException ve = new ValidacaoException();
        if (itemVo.isTipoControleQuantidade() && itemVo.getQuantidade() == null || itemVo.getQuantidade().compareTo(BigDecimal.ZERO) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A quantidade do item " + itemExec.getItemProcessoCompra().getNumero() + " deve ser um positivo.");
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

    public void removerExecucaoProcessoEmpenhoEstorno(ExecucaoProcessoEmpenhoEstorno estorno) {
        selecionado.getEstornosEmpenho().remove(estorno);
        criarSolicitacaoEstornoEmpenhoVo();
        selecionado.calcularValorTotal();
    }

    public void removerItem(SolicitacaoEmpenhoEstornoItemVo item) {
        solicitacaoEmpenhoEstornoVo.getItens().remove(item);
    }

    private void criarSolicitacaoEmpenhoEstorno(ExecucaoProcessoEmpenhoEstorno execProcEmpEst) {
        Empenho empenho = solicitacaoEmpenhoEstornoVo.getEmpenho();
        OrigemSolicitacaoEmpenho origemSolEmp = selecionado.getExecucaoProcesso().isExecucaoAta() ? OrigemSolicitacaoEmpenho.ATA_REGISTRO_PRECO : OrigemSolicitacaoEmpenho.DISPENSA_LICITACAO_INEXIGIBILIDADE;
        SolicitacaoEmpenhoEstorno novaSolEst = new SolicitacaoEmpenhoEstorno(solicitacaoEmpenhoEstornoVo.getSolicitacaoEmpenho(), empenho, origemSolEmp);
        novaSolEst.setDataSolicitacao(facade.getSistemaFacade().getDataOperacao());
        novaSolEst.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        novaSolEst.setValor(execProcEmpEst.getValorTotalItens());
        novaSolEst.setHistorico(facade.getSolicitacaoEmpenhoEstornoFacade().getHistoricoSolicitacaoEmpenhoExecucaoProcessoEstorno(solicitacaoEmpenhoEstornoVo.getSolicitacaoEmpenho(), selecionado.getExecucaoProcesso()));
        execProcEmpEst.setSolicitacaoEmpenhoEstorno(novaSolEst);
    }

    public List<ExecucaoProcessoItem> getItensExecucao() {
        return itensExecucao;
    }

    public void setItensExecucao(List<ExecucaoProcessoItem> itensExecucao) {
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

    public ExecucaoProcessoEmpenhoEstorno getExecucaoProcessoEstornoSelecionado() {
        return execucaoProcessoEstornoSelecionado;
    }

    public void setExecucaoProcessoEstornoSelecionado(ExecucaoProcessoEmpenhoEstorno execucaoProcessoEstornoSelecionado) {
        this.execucaoProcessoEstornoSelecionado = execucaoProcessoEstornoSelecionado;
    }
}
