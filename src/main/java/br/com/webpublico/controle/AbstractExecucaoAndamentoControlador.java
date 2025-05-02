package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroResumoExecucaoVO;
import br.com.webpublico.enums.FormaEntregaExecucao;
import br.com.webpublico.enums.TipoControleLicitacao;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoResumoExecucao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ExecucaoContratoFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class AbstractExecucaoAndamentoControlador extends PrettyControlador<ExecucaoContrato> implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(AbstractExecucaoAndamentoControlador.class);
    @EJB
    private ExecucaoContratoFacade facade;
    private ExecucaoContrato execucaoContrato;
    private ExecucaoContrato execucaoContratoSelecionada;
    private List<ExecucaoContrato> execucoesContrato;
    private Contrato contrato;
    private Empenho empenho;
    private Boolean permissaoEspecial;
    private FiltroResumoExecucaoVO filtroResumoExecucaoVO;

    public void novaExecucaoContrato() {
        execucaoContrato = null;
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public void recuperarDadosContrato(String idComponente) {
        try {
            if (contrato != null) {
                contrato = facade.getContratoFacade().recuperarContratoComDependenciasExecucao(contrato.getId());
                execucaoContrato = facade.criarExecucaoContrato(contrato, null);
                execucoesContrato = contrato.getExecucoes();
                atribuirDadosExecucao();
                FacesUtil.atualizarComponente(idComponente + ":Formulario");
                filtroResumoExecucaoVO = new FiltroResumoExecucaoVO(TipoResumoExecucao.CONTRATO);
                filtroResumoExecucaoVO.setContrato(contrato);
                filtroResumoExecucaoVO.setIdProcesso(contrato.getId());
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (IndexOutOfBoundsException a) {
            FacesUtil.addOperacaoNaoPermitida("Não existe saldo suficiente para gerar os itens da execução do contrato.");
            setContrato(null);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
            logger.error("recuperarDadosContrato {}", e);
        }
    }

    public void atribuirDadosExecucao() {
        execucaoContrato.setFormaEntrega(FormaEntregaExecucao.UNICA);
        execucaoContrato.setExecucaoAndamento(true);
        execucaoContrato.setDataLancamento(new Date());
        execucaoContrato.setUnidadeOrcamentaria(facade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente());
        execucaoContrato.setHierarquiaOrcamentaria(facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), execucaoContrato.getUnidadeOrcamentaria(), execucaoContrato.getDataLancamento()));
        execucaoContrato.adicionarItensListaOriginal();
    }

    public Boolean hasItensExecucaoQuantidade() {
        return execucaoContrato != null && execucaoContrato.hasItensExecucaoQuantidade();
    }

    public Boolean hasItensExecucaovalor() {
        return execucaoContrato != null && execucaoContrato.hasItensExecucaovalor();
    }

    public List<SelectItem> getTiposControle() {
        return Util.getListSelectItem(TipoControleLicitacao.values());
    }

    public void removerExecucaoContratoItem(ExecucaoContratoItem execucaoContratoItem) {
        execucaoContrato.getItens().remove(execucaoContratoItem);
        if (execucaoContratoItem.isExecucaoPorQuantidade()) {
            execucaoContrato.getItensQuantidade().remove(execucaoContratoItem);
        } else {
            execucaoContrato.getItensValor().remove(execucaoContratoItem);
        }
        execucaoContrato.setValor(execucaoContrato.getValorEmpenhado());
    }

    public void processarValorTotalExecucaoContratoItem(ExecucaoContratoItem execucaoContratoItem) {
        try {
            validarValorTotalExecucaoContratoItem(execucaoContratoItem);
            if (execucaoContratoItem.isExecucaoPorQuantidade()) {
                execucaoContratoItem.calcularValorTotal();
            }
            if (execucaoContrato.hasEmpenhos()) {
                criarEstruturaReservaExecucao();
            }
            execucaoContrato.setValor(execucaoContrato.getValorEmpenhado());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            execucaoContratoItem.setValorTotal(BigDecimal.ZERO);
        }
    }

    private void validarValorTotalExecucaoContratoItem(ExecucaoContratoItem item) {
        ValidacaoException ve = new ValidacaoException();
        if (contrato.getSaldoAtualContrato().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O contrato não possui saldo atual para realizar esta execução.");
        }
        ve.lancarException();
        if (item.isExecucaoPorValor()) {
            if (!item.hasValorTotal()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor deve ser informado para o item: " + item + ", ou remova o mesmo desta execução.");
            }
        }
        if (item.isExecucaoPorQuantidade()) {
            if (!item.hasQuantidade()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A quantidade deve ser informada para o item: " + item + ", ou remova o mesmo desta execução.");
            }
            ve.lancarException();
            item.calcularValorTotal();
        }
        if (item.getValorTotal() != null && item.getValorTotal().compareTo(contrato.getSaldoAtualContrato()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível realizar execução maior que o saldo atual do contrato. Saldo atual: "
                + Util.formataValor(contrato.getSaldoAtualContrato()) + ".");
        }
        ve.lancarException();
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrcamentaria(String parte) {
        if (contrato != null) {
            return facade.getHierarquiaOrganizacionalFacade().buscarFiltrandoHierarquiaOrcamentariaPorUnidadeAdministrativa(parte.trim(), contrato.getUnidadeAdministrativa().getSubordinada(), execucaoContrato.getDataLancamento());
        }
        return Lists.newArrayList();
    }

    public List<Contrato> completarContrato(String parte) {
        return facade.getContratoFacade().buscarContratoEmAndamento(parte.trim(), facade.getSistemaFacade().getDataOperacao());
    }

    public void definirExecucaoContratoComoNull() {
        contrato = null;
        execucaoContrato = null;
    }

    public void salvarExecucaoContrato() {
        try {
            validarSalvar();
            execucaoContrato.adicionarItensListaOriginal();
            ExecucaoContrato execucaoSalva = facade.salvarExecucaoContratoAndamento(execucaoContrato);
            FacesUtil.redirecionamentoInterno("/execucao-contrato-adm/ver/" + execucaoSalva.getId() + "/");
            FacesUtil.addOperacaoRealizada("Execução de contrato salva com sucesso.");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            logger.error("salvarExecucaoContratoAndamento {}", e);
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void cancelar() {
        redicionarParaListar();
    }

    private void redicionarParaListar() {
        FacesUtil.redirecionamentoInterno("/execucao-contrato-adm/listar/");
    }

    private void validarSalvar() {
        ValidacaoException ve = new ValidacaoException();
        if (contrato == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo contrato deve ser informado.");
        }
        ve.lancarException();
        if (execucaoContrato.getItensQuantidade().isEmpty() && execucaoContrato.getItensValor().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A execução não possui itens vinculados.");
        }
        if (execucaoContrato.getEmpenhos().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A execução não possui empenhos vinculados.");
        }
        ve.lancarException();
        if (!permissaoEspecial) {
            if (execucaoContrato.getValor().compareTo(contrato.getValorAtualContrato()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor da execução deve ser menor ou igual ao total do contrato.");
            }
            if (execucaoContrato.getValorExecucaoLiquido().compareTo(contrato.getValorAtualContrato()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor líquido da execução deve ser menor ou igual ao saldo atual do contrato.");
            }
            if (execucaoContrato.getValorEmpenhado().compareTo(contrato.getValorAtualContrato()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor total do(s) empenho(s) deve ser menor ou igual ao total do contrato.");
            }
            if (execucaoContrato.getValorExecucaoLiquido().compareTo(contrato.getSaldoAtualContrato()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor total líquido do(s) empenho(s) deve ser me ou igual ao saldo atual do contrato.");
            }
            if (execucaoContrato.getValorEmpenhado().compareTo(execucaoContrato.getValorTotalItens()) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" Os valores dos empenhos não atingiu o total dos itens.");
            }
            if (execucaoContrato.hasEstornoExecucao()) {
                ExecucaoContratoEstorno estornoExecucao = execucaoContrato.getEstornosExecucao().get(0);

                for (ExecucaoContratoEmpenhoEstorno estEmpenho : estornoExecucao.getEstornosEmpenho()) {
                    if (estEmpenho.getValor().compareTo(estEmpenho.getValorTotalItens()) != 0) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("O valor total dos itens do estorno deve ser igual ao valor do estorno do empenho " + estEmpenho.getSolicitacaoEmpenhoEstorno().getEmpenho().getNumero() + "." +
                            " Isso deve acontecer para que o saldo do contrato fique correto para os itens.");
                    }
                }
            }
        }
        ve.lancarException();
    }

    public void visualizarExecucao(ExecucaoContrato execucaoContrato) {
        this.execucaoContratoSelecionada = execucaoContrato;
    }

    public List<Empenho> completarEmpenhos(String parte) {
        return facade.getEmpenhoFacade().buscarEmpenhoSemVinculoComContratoPorNumeroOrPessoa(parte.trim(), contrato.getContratado());
    }

    public void adicionarEmpenhoExecucao() {
        try {
            validarEmpenhoExecucao();
            if (execucaoContrato.getEmpenhos() == null) {
                execucaoContrato.setEmpenhos(Lists.<ExecucaoContratoEmpenho>newArrayList());
            }
            ExecucaoContratoEmpenho execucaoContratoEmpenho = new ExecucaoContratoEmpenho();
            execucaoContratoEmpenho.setExecucaoContrato(execucaoContrato);
            execucaoContratoEmpenho.setEstornosEmpenho(Lists.<EmpenhoEstorno>newArrayList());
            execucaoContratoEmpenho.setEmpenho(facade.getEmpenhoFacade().recuperarComFind(empenho.getId()));
            execucaoContrato.getEmpenhos().add(execucaoContratoEmpenho);
            execucaoContrato.setValor(execucaoContrato.getValorEmpenhado());
            facade.recuperarEmpenhosExecucaoContrato(execucaoContrato);
            empenho = null;
            criarEstruturaReservaExecucao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void criarEstruturaReservaExecucao() {
        execucaoContrato = facade.criarEstruturaReservaExecucaoAndamento(execucaoContrato);
    }

    public void removerEmpenhoExecucao(ExecucaoContratoEmpenho execucaoContratoEmpenho) {
        execucaoContrato.getEmpenhos().remove(execucaoContratoEmpenho);
        criarEstruturaReservaExecucao();
        execucaoContrato.setValor(execucaoContrato.getValorEmpenhado());
    }

    private void validarEmpenhoExecucao() {
        ValidacaoException ve = new ValidacaoException();
        if (empenho == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Empenho deve ser informado.");
        }
        ve.lancarException();

        for (ExecucaoContratoEmpenho execucaoContratoEmpenho : execucaoContrato.getEmpenhos()) {
            if (execucaoContratoEmpenho.getEmpenho() != null && execucaoContratoEmpenho.getEmpenho().equals(empenho)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Empenho selecionado já está adicionado a esta execução.");
                break;
            }
        }
        ve.lancarException();
    }

    public ExecucaoContrato getExecucaoContrato() {
        return execucaoContrato;
    }

    public void setExecucaoContrato(ExecucaoContrato execucaoContrato) {
        this.execucaoContrato = execucaoContrato;
    }

    public ExecucaoContrato getExecucaoContratoSelecionada() {
        return execucaoContratoSelecionada;
    }

    public void setExecucaoContratoSelecionada(ExecucaoContrato execucaoContratoSelecionada) {
        this.execucaoContratoSelecionada = execucaoContratoSelecionada;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public Empenho getEmpenho() {
        return empenho;
    }

    public void setEmpenho(Empenho empenho) {
        this.empenho = empenho;
    }

    public List<ExecucaoContrato> getExecucoesContrato() {
        return execucoesContrato;
    }

    public void setExecucoesContrato(List<ExecucaoContrato> execucoesContrato) {
        this.execucoesContrato = execucoesContrato;
    }

    public Boolean getPermissaoEspecial() {
        return permissaoEspecial;
    }

    public void setPermissaoEspecial(Boolean permissaoEspecial) {
        this.permissaoEspecial = permissaoEspecial;
    }

    public FiltroResumoExecucaoVO getFiltroResumoExecucaoContrato() {
        return filtroResumoExecucaoVO;
    }

    public void setFiltroResumoExecucaoContrato(FiltroResumoExecucaoVO filtroResumoExecucaoVO) {
        this.filtroResumoExecucaoVO = filtroResumoExecucaoVO;
    }
}
