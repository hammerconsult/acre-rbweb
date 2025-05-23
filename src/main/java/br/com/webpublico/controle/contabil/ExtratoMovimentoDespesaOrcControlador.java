package br.com.webpublico.controle.contabil;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.contabil.ExtratoMovimentoDespesaORC;
import br.com.webpublico.entidadesauxiliares.contabil.MovimentoDespesaORCVO;
import br.com.webpublico.entidadesauxiliares.contabil.SaldoFonteDespesaOcamentariaVO;
import br.com.webpublico.enums.OperacaoORC;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ProjetoAtividadeFacade;
import br.com.webpublico.negocios.contabil.execucao.ExtratoMovimentoDespesaOrcFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.seguranca.service.SistemaService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "extratoMovimentoDespesaOrcControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-extrato-movimento-despesaorc", pattern = "/extrato-movimento-despesaorc/", viewId = "/faces/financeiro/orcamentario/extrato-movimento-despesaorc/edita.xhtml")
})
public class ExtratoMovimentoDespesaOrcControlador implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(ExtratoMovimentoDespesaOrcControlador.class);
    @EJB
    private ExtratoMovimentoDespesaOrcFacade facade;
    @EJB
    private ProjetoAtividadeFacade projetoAtividadeFacade;
    private ConverterAutoComplete converterFonteDespesaORC;
    private ConverterAutoComplete converterDespesaORC;
    private ExtratoMovimentoDespesaORC selecionado;
    private SistemaService sistemaService;

    @PostConstruct
    public void init() {
        sistemaService = (SistemaService) Util.getSpringBeanPeloNome("sistemaService");
    }

    public ExtratoMovimentoDespesaOrcControlador() {
    }

    @URLAction(mappingId = "novo-extrato-movimento-despesaorc", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        selecionado = new ExtratoMovimentoDespesaORC();
        selecionado.setExercicio(sistemaService.getExercicioCorrente());
        selecionado.setDataInicial(DataUtil.getPrimeiroDiaAno(selecionado.getExercicio().getAno()));
        selecionado.setDataFinal(sistemaService.getDataOperacao());
        selecionado.setHierarquiaOrganizacional(sistemaService.getHierarquiOrcamentariaCorrente());
    }

    public void atribuirNullAcaoDespesaEFonte() {
        selecionado.setDespesaORC(null);
        atribuirNullAcaoPPA();
        atribuirNullFonte();
    }

    public void atribuirNullDespesaEFonte() {
        selecionado.setDespesaORC(null);
        atribuirNullFonte();
    }

    public void atribuirNullFonte() {
        selecionado.setFonteDespesaORC(null);
    }

    public void atribuirNullAcaoPPA() {
        selecionado.setAcaoPPA(null);
    }

    public void abrirDialogProgressBar() {
        FacesUtil.executaJavaScript("dialogProgressBar.show()");
    }

    public void executarPoll() {
        FacesUtil.executaJavaScript("poll.start()");
    }

    public void recuperarMovimento() {
        try {
            validarFiltros();
            abrirDialogProgressBar();
            executarPoll();
            facade.recuparMovimento(selecionado);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErrorGenerico(ex);
        }
    }

    private void validarFiltros() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDataInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Campo Data inicial é obrigatório.");
        }
        if (selecionado.getDataInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Campo Data final é obrigatório.");
        }
        if (selecionado.getHierarquiaOrganizacional() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Campo Unidade organizacional é obrigatório.");
        }
        if (selecionado.getDespesaORC() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Campo Despesa orçamentária é obrigatório.");
        }
        if (selecionado.getDespesaORC() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Campo Fonte de recursos é obrigatório.");
        }
        ve.lancarException();
    }

    public void finalizarBarraProgressao() {
        if (!selecionado.getBarraProgressoItens().getCalculando()) {
            FacesUtil.executaJavaScript("poll.stop()");
            FacesUtil.executaJavaScript("dialogProgressBar.hide()");
            FacesUtil.addOperacaoRealizada("Busca dos movimentos realizado com sucesso.");
            FacesUtil.atualizarComponente("Formulario");
        }
    }

    public List<HierarquiaOrganizacional> completarUnidadeOrganizacional(String parte) {
        return facade.getUnidadeOrganizacionalFacade().listaHierarquiasVigentes(sistemaService.getDataOperacao(), parte.trim());
    }

    public List<AcaoPPA> buscarProjetoAtividade(String parte) {
        if (selecionado.getHierarquiaOrganizacional() != null) {
            return projetoAtividadeFacade.buscarAcoesPPAPorExercicioUnidade(parte.trim(), selecionado.getExercicio(), selecionado.getHierarquiaOrganizacional().getSubordinada(), null);
        } else {
            return projetoAtividadeFacade.buscarAcoesPPAPorExercicio(parte.trim(), selecionado.getExercicio());
        }
    }

    public List<DespesaORC> completarDespesaORC(String parte) {
        return facade.getDespesaORCFacade().buscarDespesasOrcPorExercicioAndUnidade(parte.trim(), selecionado.getExercicio(), selecionado.getHierarquiaOrganizacional().getSubordinada(), selecionado.getAcaoPPA());
    }

    public List<SelectItem> getFontesDespesaORC() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        if (selecionado.getDespesaORC() != null) {
            for (FonteDespesaORC fonteDespesaORC : facade.getFonteDespesaORCFacade().completaFonteDespesaORC("", selecionado.getDespesaORC())) {
                toReturn.add(new SelectItem(fonteDespesaORC, "" + fonteDespesaORC.getProvisaoPPAFonte().getDestinacaoDeRecursos()));
            }
        }
        return toReturn;
    }


    public ConverterAutoComplete getConverterFonteDespesaORC() {
        if (converterFonteDespesaORC == null) {
            converterFonteDespesaORC = new ConverterAutoComplete(FonteDespesaORC.class, facade.getFonteDespesaORCFacade());
        }
        return converterFonteDespesaORC;
    }

    public ConverterAutoComplete getConverterDespesaORC() {
        if (converterDespesaORC == null) {
            converterDespesaORC = new ConverterAutoComplete(DespesaORC.class, facade.getDespesaORCFacade());
        }
        return converterDespesaORC;
    }

    public ExtratoMovimentoDespesaORC getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(ExtratoMovimentoDespesaORC selecionado) {
        this.selecionado = selecionado;
    }

    public List<SelectItem> getOperacoesOrc() {
        return Util.getListSelectItem(OperacaoORC.values(), false);
    }

    public void adicionarOperacao() {
        if (selecionado.getOperacaoORCSelecionada() != null) {
            selecionado.getOperacoesSelecionadas().add(selecionado.getOperacaoORCSelecionada());
            selecionado.setOperacaoORCSelecionada(null);
            selecionado.atualizarMovimentosFiltrados();
        }
    }

    public void removerOperacao(OperacaoORC operacaoORC) {
        selecionado.getOperacoesSelecionadas().remove(operacaoORC);
        selecionado.atualizarMovimentosFiltrados();
    }

    public List<SelectItem> getClassesOrigens() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, ""));
        retorno.add(new SelectItem(Empenho.class.getSimpleName(), "Empenho"));
        retorno.add(new SelectItem(EmpenhoEstorno.class.getSimpleName(), "Estorno de Empenho"));
        retorno.add(new SelectItem(Liquidacao.class.getSimpleName(), "Liquidação"));
        retorno.add(new SelectItem(LiquidacaoEstorno.class.getSimpleName(), "Estorno de Liquidação"));
        retorno.add(new SelectItem(Pagamento.class.getSimpleName(), "Pagamento"));
        retorno.add(new SelectItem(PagamentoEstorno.class.getSimpleName(), "Estorno de Pagamento"));
        retorno.add(new SelectItem(SolicitacaoEmpenho.class.getSimpleName(), "Solicitação de Empenho"));
        retorno.add(new SelectItem(PropostaConcessaoDiaria.class.getSimpleName(), "Diária/Suprimento de Fundo"));
        retorno.add(new SelectItem(Contrato.class.getSimpleName(), "Contrato"));
        retorno.add(new SelectItem(DotacaoSolicitacaoMaterialItemFonte.class.getSimpleName(), "Dotação Solicitação de Compra"));
        retorno.add(new SelectItem(ExecucaoContratoTipoFonte.class.getSimpleName(), "Dotação Execução Contrato"));
        retorno.add(new SelectItem(ExecucaoProcessoReserva.class.getSimpleName(), "Dotação Execução Processo"));
        retorno.add(new SelectItem(CancelamentoReservaDotacao.class.getSimpleName(), "Cancelamento de Reserva de Dotação"));
        retorno.add(new SelectItem(ExecucaoContratoEmpenhoEstorno.class.getSimpleName(), "Dotação Execução Contrato Estorno"));
        retorno.add(new SelectItem(ExecucaoProcessoEmpenhoEstorno.class.getSimpleName(), "Dotação Execução Processo Estorno"));
        retorno.add(new SelectItem(ProvisaoPPAFonte.class.getSimpleName(), "Provisão PPA Fonte"));
        retorno.add(new SelectItem(AnulacaoORC.class.getSimpleName(), "Anulação Orçamentária"));
        retorno.add(new SelectItem(SuplementacaoORC.class.getSimpleName(), "Suplementação Orçamentária"));
        retorno.add(new SelectItem(ReservaFonteDespesaOrc.class.getSimpleName(), "Reserva Fonte de Despesa Orçamentária"));
        return retorno;
    }

    public void adicionarClasseOrigem() {
        if (selecionado.getClasseOrigemSelecionada() != null) {
            selecionado.getClassesSelecionadas().add(selecionado.getClasseOrigemSelecionada());
            selecionado.setClasseOrigemSelecionada(null);
            selecionado.atualizarMovimentosFiltrados();
        }
    }

    public void removerClasseOrigem(String classeOrigem) {
        selecionado.getClassesSelecionadas().remove(classeOrigem);
        selecionado.atualizarMovimentosFiltrados();
    }

    public String getDescricaoClasseOrigem(String classeOrigem) {
        return MovimentoDespesaORCVO.getDescricaoClasseOrigem(classeOrigem);
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarGerarRelatorio();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USER", sistemaService.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("SECRETARIA", "MUNICÍPIO DE RIO BRANCO");
            dto.adicionarParametro("NOMERELATORIO", "Extrato de Movimentação de Despesa Orçamentária");
            dto.adicionarParametro("saldos", SaldoFonteDespesaOcamentariaVO.saldosToDto(selecionado.getSaldos(), selecionado.getMostrarMovimentos(), selecionado.hasMovimentosFiltrados()));
            dto.adicionarParametro("mostrarHistorico", selecionado.getMostrarHistoricoTabela());
            dto.adicionarParametro("FILTRO", getFiltrosUtilizados());
            dto.adicionarParametro("DOTACAO", Util.formataValorSemCifrao(selecionado.getDotacao()));
            dto.adicionarParametro("ALTERACAO", Util.formataValorSemCifrao(selecionado.getAlteracao()));
            dto.adicionarParametro("DOTACAO_ATUALIZADA", Util.formataValorSemCifrao(selecionado.getSaldoDotacaoAtual()));
            dto.adicionarParametro("EMPENHADO", Util.formataValorSemCifrao(selecionado.getEmpenhado()));
            dto.adicionarParametro("RESERVADO", Util.formataValorSemCifrao(selecionado.getReservado()));
            dto.adicionarParametro("RESERVADO_POR_LICITACAO", Util.formataValorSemCifrao(selecionado.getReservadoPorLicitacao()));
            dto.adicionarParametro("LIQUIDADO", Util.formataValorSemCifrao(selecionado.getLiquidado()));
            dto.adicionarParametro("PAGO", Util.formataValorSemCifrao(selecionado.getPago()));
            dto.adicionarParametro("SALDO", Util.formataValorSemCifrao(selecionado.getSaldoAtual()));
            dto.setNomeRelatorio("EXTRATO-MOVIMENTACAO-DESPESA-ORCAMENTARIA");
            dto.setApi("contabil/extrato-movimento-despesaorc/");
            ReportService.getInstance().gerarRelatorio(sistemaService.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatrio: ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void validarGerarRelatorio() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getSaldos().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado saldos para serem gerados.");
        }
        ve.lancarException();
    }

    private String getFiltrosUtilizados() {
        String retorno = "Data Inicial: " + DataUtil.getDataFormatada(selecionado.getDataInicial()) +
            "; Data Final: " + DataUtil.getDataFormatada(selecionado.getDataFinal()) +
            "; Unidade Organizacional: " + selecionado.getHierarquiaOrganizacional().toString() +
            "; Despesa Orçamentária: " + selecionado.getDespesaORC().toString() +
            "; Fonte de Recursos: " + selecionado.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos().toString() +
            "; Visualizar Movimentos? " + (selecionado.getMostrarMovimentos() ? "Sim" : "Não") +
            "; Histórico Tabelavel? " + (selecionado.getMostrarHistoricoTabela() ? "Sim" : "Não");

        if (!selecionado.getOperacoesSelecionadas().isEmpty()) {
            retorno += "; Operações: ";
            for (OperacaoORC operacao : selecionado.getOperacoesSelecionadas()) {
                retorno += operacao.getDescricao() + ", ";
            }
            retorno = retorno.substring(0, retorno.length() - 2);
        }

        if (!selecionado.getClassesSelecionadas().isEmpty()) {
            retorno += "; Classes de Movimentos: ";
            for (String classe : selecionado.getClassesSelecionadas()) {
                retorno += getDescricaoClasseOrigem(classe) + ", ";
            }
            retorno = retorno.substring(0, retorno.length() - 2);
        }
        return retorno;
    }
}
