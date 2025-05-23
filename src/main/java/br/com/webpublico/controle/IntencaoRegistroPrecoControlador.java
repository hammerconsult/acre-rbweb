package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroHistoricoProcessoLicitatorio;
import br.com.webpublico.entidadesauxiliares.ItemFormularioCompraVO;
import br.com.webpublico.entidadesauxiliares.LoteFormularioCompraVO;
import br.com.webpublico.entidadesauxiliares.ResumoItemIRPParticipanteVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.IntencaoRegistroPrecoFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.TabChangeEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Desenvolvimento on 12/01/2016.
 */

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoIntencaoRegistroDePreco", pattern = "/intencao-de-registro-de-preco/novo/", viewId = "/faces/administrativo/licitacao/intencaoderegistrodepreco/edita.xhtml"),
    @URLMapping(id = "editarIntencaoRegistroDePreco", pattern = "/intencao-de-registro-de-preco/editar/#{intencaoRegistroPrecoControlador.id}/", viewId = "/faces/administrativo/licitacao/intencaoderegistrodepreco/edita.xhtml"),
    @URLMapping(id = "listarIntencaoRegistroDePreco", pattern = "/intencao-de-registro-de-preco/listar/", viewId = "/faces/administrativo/licitacao/intencaoderegistrodepreco/lista.xhtml"),
    @URLMapping(id = "verIntencaoRegistroDePreco", pattern = "/intencao-de-registro-de-preco/ver/#{intencaoRegistroPrecoControlador.id}/", viewId = "/faces/administrativo/licitacao/intencaoderegistrodepreco/visualizar.xhtml")
})
public class IntencaoRegistroPrecoControlador extends PrettyControlador<IntencaoRegistroPreco> implements Serializable, CRUD {

    @EJB
    private IntencaoRegistroPrecoFacade facade;
    private ParticipanteIRP participanteIrp;
    private List<ResumoItemIRPParticipanteVO> resumoItensParticipantes;
    private FormularioCotacao formularioCotacao;
    private List<LoteFormularioCompraVO> lotesVO;
    private FiltroHistoricoProcessoLicitatorio filtroHistoricoProcesso;

    public IntencaoRegistroPrecoControlador() {
        super(IntencaoRegistroPreco.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/intencao-de-registro-de-preco/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novoIntencaoRegistroDePreco", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setResponsavel(facade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setDataLancamento(facade.getSistemaFacade().getDataOperacao());
        lotesVO = Lists.newArrayList();
    }

    @URLAction(mappingId = "editarIntencaoRegistroDePreco", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        popularFormularioCompraVO();
        buscarFormularioCotacao();
        if (hasIrpFormularioCotacao()) {
            redirecionarParaVer();
            FacesUtil.addOperacaoNaoPermitida("A irp encontra-se no formulário de cotação: " + formularioCotacao + ", e não pederá sofrer alterações.");
        }
        montarResumoItemPorParticipantes();
        Collections.sort(selecionado.getParticipantes());
    }

    private void buscarFormularioCotacao() {
        formularioCotacao = facade.getFormularioCotacaoFacade().recuperarFormularioCotacaoPorIrp(selecionado);
    }

    @URLAction(mappingId = "verIntencaoRegistroDePreco", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        try {
            operacao = Operacoes.VER;
            recuperarObjeto();
            buscarFormularioCotacao();
            montarResumoItemPorParticipantes();
            Collections.sort(selecionado.getParticipantes());
            popularFormularioCompraVO();
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
            redireciona();
        }
    }

    @Override
    public void salvar() {
        try {
            validarSalvar();
            validarSeQuantidadeOrValorItemIrpAtendeOsParticipantes();
            selecionado = facade.salvarIrp(selecionado);
            selecionado.setUnidadeGerenciadora(facade.buscarUnidadeGerenciadoraVigente(selecionado));
            if (selecionado.getSituacaoIRP().isEmElaboracao()) {
                FacesUtil.executaJavaScript("dlgFinalizarIrp.show()");
                FacesUtil.atualizarComponente("formDlgFinalizarIrp");
            } else {
                FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
                redirecionarParaVer();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.debug("Salvar intenção de registro de preço {}", ex);
            descobrirETratarException(ex);
        }
    }

    public void preConcluir() {
        try {
            validarConcluir();
            FacesUtil.executaJavaScript("dlgConcluirIrp.show()");
            FacesUtil.atualizarComponente("formDlgConcluirIrp");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Concluir intenção de registro de preço {}", ex);
            descobrirETratarException(ex);
        }
    }

    public void concluirIRP() {
        try {
            selecionado.setSituacaoIRP(SituacaoIRP.CONCLUIDA);
            selecionado = facade.salvarRetornando(selecionado);
            FacesUtil.addOperacaoRealizada("A IRP foi concluída com sucesso!");
            redirecionarParaVer();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Concluir intenção de registro de preço {}", ex);
            descobrirETratarException(ex);
        }
    }

    public void reabrirIRP() {
        try {
            selecionado.setSituacaoIRP(SituacaoIRP.EM_ELABORACAO);
            selecionado = facade.salvarRetornando(selecionado);
            FacesUtil.addOperacaoRealizada("A IRP foi reaberta com sucesso!");
            redirecionarParaVerOrEditar(selecionado.getId(), "editar");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Concluir intenção de registro de preço {}", ex);
            descobrirETratarException(ex);
        }
    }

    public void montarResumoItemPorParticipantes() {
        try {
            resumoItensParticipantes = facade.montarResumoItemPorParticipantes(selecionado);
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    public TipoMovimentoProcessoLicitatorio getTipoProcesso() {
        return TipoMovimentoProcessoLicitatorio.IRP;
    }

    public boolean hasLotes() {
        return !Util.isListNullOrEmpty(lotesVO);
    }

    public boolean hasLotesNovos() {
        return hasLotes() && lotesVO.stream().anyMatch(loteVO -> loteVO.getLoteIRP().getId() == null);
    }

    public boolean hasItensNovos() {
        return hasLotes() && lotesVO.stream().flatMap(loteVO -> loteVO.getItens().stream()).anyMatch(itemVO -> itemVO.getItemIRP().getId() == null);
    }

    private void popularFormularioCompraVO() {
        setLotesVO(LoteFormularioCompraVO.popularFormularioCompraVOFromIRP(selecionado.getLotes()));
        Collections.sort(lotesVO);
        lotesVO.forEach(loteVO ->Collections.sort(loteVO.getItens()));
    }

    public void redirecionarParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + getUrlKeyValue() + "/");
    }

    public void redirecionarParaNovoParticipanteIRP() {
        Web.poeNaSessao("IRP", selecionado);
        Web.setCaminhoOrigem("/intencao-de-registro-de-preco/listar/");
        FacesUtil.redirecionamentoInterno("/participante-intencao-registro-de-preco/novo/");
        Web.getSessionMap().put("esperaRetorno", true);
    }


    public void aprovarTodasUnidadeParticipamente() {
        try {
            validarMovimentacaoParticipantes();
            for (ParticipanteIRP participante : selecionado.getParticipantes()) {
                if (!participante.getGerenciador() && !participante.isParticipanteAprovado()) {
                    movimentarItensIrpVo(participante);
                    participante.setSituacao(SituacaoIRP.APROVADA);
                }
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void validarMovimentacaoParticipantes() {
        ValidacaoException ve = new ValidacaoException();
        if (hasLotesNovos() || hasItensNovos()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido movimentar participantes quando a irp possui lotes ou itens novos, é necessário salvar a intenção de registro de preço");
        }
        ve.lancarException();
    }

    public void atribuirNullQuantidadeParticipantes() {
        selecionado.setQuantidadeParticipante(null);
    }

    public void reinicarUnidadeParticipante(ParticipanteIRP participante) {
        try {
            validarMovimentacaoParticipantes();
            if (participante.isParticipanteAprovado()) {
                movimentarItensIrpVo(participante);
            }
            participante.setSituacao(SituacaoIRP.EM_ELABORACAO);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void movimentarItensIrpVo(ParticipanteIRP participante) {
        for (ItemParticipanteIRP itemPart : participante.getItens()) {
            for (ResumoItemIRPParticipanteVO itemVo : resumoItensParticipantes) {
                ItemIntencaoRegistroPreco itemIrp = itemPart.getItemIntencaoRegistroPreco();
                if (itemIrp.equals(itemVo.getItemIrp())) {
                    if (!participante.isParticipanteAprovado()) {
                        itemVo.getItensParticipantes().add(itemPart);
                    } else {
                        itemVo.getItensParticipantes().remove(itemPart);
                    }
                    if (itemIrp.isTipoControlePorValor()) {
                        itemIrp.setQuantidade(BigDecimal.ONE);
                        itemIrp.setValor(itemVo.getValorOrQuantidadeTotalParticipantes());
                    } else {
                        itemIrp.setQuantidade(itemVo.getValorOrQuantidadeTotalParticipantes());
                    }
                }
            }
        }

        for (LoteFormularioCompraVO lote : lotesVO) {
            for (ItemFormularioCompraVO itemVO : lote.getItens()) {
                ItemIntencaoRegistroPreco itemIrp = itemVO.getItemIRP();
                for (ResumoItemIRPParticipanteVO res : resumoItensParticipantes) {
                    if (itemIrp.equals(res.getItemIrp())) {
                        BigDecimal qtde = itemIrp.isTipoControlePorQuantidade() ? res.getValorOrQuantidadeTotalParticipantes() : BigDecimal.ONE;
                        itemIrp.setQuantidade(qtde);
                        itemVO.setQuantidade(qtde);

                        if (itemIrp.isTipoControlePorValor()) {
                            itemIrp.setValor(res.getValorOrQuantidadeTotalParticipantes());
                            itemVO.setValorTotal(res.getValorOrQuantidadeTotalParticipantes());
                        }
                    }
                }
            }
            lote.calcularValorTotal();
        }
    }

    public void aprovarUnidadeParticipante(ParticipanteIRP participante) {
        try {
            validarMovimentacaoParticipantes();
            if (!participante.isParticipanteAprovado()) {
                movimentarItensIrpVo(participante);
            }
            participante.setSituacao(SituacaoIRP.APROVADA);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void recusarUnidadeParticipante(ParticipanteIRP participante) {
        try {
            validarMovimentacaoParticipantes();
            if (participante.isParticipanteAprovado()) {
                movimentarItensIrpVo(participante);
            }
            participante.setSituacao(SituacaoIRP.RECUSADA);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void selecionarParticipante(ParticipanteIRP participante) {
        participanteIrp = participante;
        Collections.sort(participante.getItens());
    }

    public void movimentarQuantidadeItemParticipamente(ParticipanteIRP participante) {
        participanteIrp = participante;
        Collections.sort(participanteIrp.getItens());
        FacesUtil.executaJavaScript("dlgItensPartcipantes.show()");
    }

    public List<SelectItem> getTiposObjetoCompra() {
        return Util.getListSelectItem(TipoObjetoCompra.values());
    }

    private void validarConcluir() {
        ValidacaoException ve = new ValidacaoException();
        if (!selecionado.hasParticipantes()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O irp não possui unidade participante.");
        }
        for (ParticipanteIRP participante : selecionado.getParticipantes()) {
            if (participante.getSituacao().isEmElaboracao()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O participante " + participante + " não foi movimentado e encontra-se com situação de 'Em Elaboração'.");
            }
        }
        ve.lancarException();
    }

    private void validarSalvar() {
        Util.validarCampos(selecionado);
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getHabilitarNumeroParticipante() && selecionado.getQuantidadeParticipante() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo quantidade de participantes deve ser informado.");
        }
        if (!hasLotes()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Intenção não possui lotes. Para continuar insira os lotes e itens para este irp.");
        } else {
            lotesVO.stream()
                .filter(lote -> !lote.hasItens())
                .map(lote -> "O lote nº " + lote.getNumero() + " não possui itens. Para continuar insira os itens para este lote.")
                .forEach(ve::adicionarMensagemDeOperacaoNaoPermitida);

            lotesVO.stream()
                .flatMap(lote -> lote.getItens().stream())
                .forEach(item -> {
                    if (item.getQuantidade().compareTo(BigDecimal.ZERO) == 0) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("Quando o controle for por valor a quantidade o item deve ser igual um(1).");
                    }
                });
        }
        if (selecionado.getHabilitarNumeroParticipante() && selecionado.getQuantidadeParticipantesCadastrados() > selecionado.getQuantidadeParticipante()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A intenção de registro de preço ultrapassou a quantidade de participantes definida em seu cadastro.");
        }
        ve.lancarException();
    }

    private Date getDataOperacao() {
        return facade.getSistemaFacade().getDataOperacao();
    }

    public ParticipanteIRP getParticipanteIrp() {
        return participanteIrp;
    }

    public void setParticipanteIrp(ParticipanteIRP participanteIrp) {
        this.participanteIrp = participanteIrp;
    }

    public List<SelectItem> getTiposApuracao() {
        return Util.getListSelectItem(TipoApuracaoLicitacao.values());
    }

    public List<SelectItem> getTiposObjeto() {
        return Util.getListSelectItem(TipoSolicitacao.values());
    }

    private void validarSeQuantidadeOrValorItemIrpAtendeOsParticipantes() {
        ValidacaoException ex = new ValidacaoException();
        if (hasItensResumo()) {
            lotesVO.stream()
                .flatMap(loteVO -> loteVO.getItens().stream())
                .forEach(itemVO -> resumoItensParticipantes.forEach(resumoItemIRPParticipanteVO -> {
                    BigDecimal valorTotalParticipantes = resumoItemIRPParticipanteVO.getValorOrQuantidadeTotalParticipantes();
                    ItemIntencaoRegistroPreco itemIrp = itemVO.getItemIRP();
                    BigDecimal valorTotaItem = itemIrp.getValorColuna();
                    if (itemIrp.equals(resumoItemIRPParticipanteVO.getItemIrp()) && valorTotaItem.compareTo(valorTotalParticipantes) < 0) {
                        ex.adicionarMensagemDeOperacaoNaoPermitida("O campo " + itemIrp.getTipoControle().getDescricao() + " deve maior ou igual ao total dos participantes " + Util.formataValorSemCifrao(valorTotalParticipantes) + ".");
                    }
                }));
        }
        ex.lancarException();
    }

    public void gerarRelatorio(IntencaoRegistroPreco irp) {
        if (irp != null) {
            selecionado = irp;
            gerarRelatorio("PDF");
        }
    }

    public void gerarRelatorio() {
        try {
            RelatorioDTO dto = montarRelatorioDto();
            ReportService.getInstance().gerarRelatorio(facade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório: ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            RelatorioDTO dto = montarRelatorioDto();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            ReportService.getInstance().gerarRelatorio(facade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório: ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private RelatorioDTO montarRelatorioDto() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.adicionarParametro("USER", facade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
        dto.adicionarParametro("condicao", " and irp.id = " + selecionado.getId());
        dto.adicionarParametro("dataOperacao", getDataOperacao());
        dto.setNomeRelatorio("Relatório de Intenção de Registro de Preço - IRP");
        dto.setApi("administrativo/irp/");
        return dto;
    }

    public boolean hasIrpFormularioCotacao() {
        return formularioCotacao != null;
    }

    public boolean hasMaisDeUmParticipante() {
        return selecionado.hasMaisDeUmParticipante();
    }

    public boolean hasItensResumo() {
        return !Util.isListNullOrEmpty(resumoItensParticipantes);
    }


    public List<ResumoItemIRPParticipanteVO> getResumoItensParticipantes() {
        return resumoItensParticipantes;
    }

    public void setResumoItensParticipantes(List<ResumoItemIRPParticipanteVO> resumoItensParticipantes) {
        this.resumoItensParticipantes = resumoItensParticipantes;
    }

    private void novoFiltroHistoricoProcesso() {
        filtroHistoricoProcesso = new FiltroHistoricoProcessoLicitatorio(selecionado.getId(), TipoMovimentoProcessoLicitatorio.IRP);
    }

    public void onTabChange(TabChangeEvent event) {
        String tab = event.getTab().getId();
        if ("tab-historico".equals(tab)) {
            novoFiltroHistoricoProcesso();
        }
    }

    public FiltroHistoricoProcessoLicitatorio getFiltroHistoricoProcesso() {
        return filtroHistoricoProcesso;
    }

    public void setFiltroHistoricoProcesso(FiltroHistoricoProcessoLicitatorio filtroHistoricoProcesso) {
        this.filtroHistoricoProcesso = filtroHistoricoProcesso;
    }

    public FormularioCotacao getFormularioCotacao() {
        return formularioCotacao;
    }

    public void setFormularioCotacao(FormularioCotacao formularioCotacao) {
        this.formularioCotacao = formularioCotacao;
    }

    public List<LoteFormularioCompraVO> getLotesVO() {
        return lotesVO;
    }

    public void setLotesVO(List<LoteFormularioCompraVO> lotesVO) {
        this.lotesVO = lotesVO;
    }
}
