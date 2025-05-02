package br.com.webpublico.nfse.relatorio.controladores;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.enums.ClassificacaoAtividade;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoPorte;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.nfse.domain.dtos.FiltroNotaFiscal;
import br.com.webpublico.nfse.domain.dtos.RelatorioNotasFiscaisDTO;
import br.com.webpublico.nfse.enums.SituacaoNota;
import br.com.webpublico.nfse.facades.NotaFiscalFacade;
import br.com.webpublico.nfse.util.ImprimeRelatorioNfse;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.List;
import java.util.concurrent.Future;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-resumo-notas-fiscais-por-atividade",
        pattern = "/nfse/relatorio/resumo-notas-fiscais-por-atividade/",
        viewId = "/faces/tributario/nfse/relatorio/notas-fiscais-por-atividade.xhtml")
})
public class RelatorioResumoNotasFiscaisPorAtividadeControlador extends AbstractReport {

    @EJB
    private NotaFiscalFacade notaFiscalFacade;
    private FiltroNotaFiscal filtro;
    private Future<List<RelatorioNotasFiscaisDTO>> futureNotasFicais;
    private Boolean prontoParaImpressao;
    private AssistenteBarraProgresso assistenteBarraProgresso;

    @URLAction(mappingId = "novo-resumo-notas-fiscais-por-atividade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtro = new FiltroNotaFiscal();
        filtro.setExercicioInicial(getSistemaFacade().getExercicioCorrente());
        filtro.setMesInicial(Mes.getMesToInt(DataUtil.getMes(getSistemaFacade().getDataOperacao())));
        filtro.setExercicioFinal(getSistemaFacade().getExercicioCorrente());
        filtro.setMesFinal(Mes.getMesToInt(DataUtil.getMes(getSistemaFacade().getDataOperacao())));
    }

    public Boolean getProntoParaImpressao() {
        return prontoParaImpressao;
    }

    public void setProntoParaImpressao(Boolean prontoParaImpressao) {
        this.prontoParaImpressao = prontoParaImpressao;
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    public void gerarRelatorio() {
        try {
            validarFiltros();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setNomeRelatorio("Relatório de Notas Fiscais por Atividade");
            dto.setApi("tributario/nfse/nota-fiscal-por-atividade/");
            dto.adicionarParametro("MUNICIPIO", "Municipio de Rio Branco");
            dto.adicionarParametro("SECRETARIA", "Secretaria Municipal de Finanças");
            dto.adicionarParametro("TITULO", "RESUMO DE NOTAS FISCAIS POR ATIVIDADE - " + filtro.getTipoRelatorioApresentacao().name());
            dto.adicionarParametro("TIPOAPRESENTACAO", filtro.getTipoRelatorioApresentacao().name());
            dto.adicionarParametro("USUARIO", notaFiscalFacade.getSistemaFacade().getUsuarioCorrente().getNome());
            filtro.adicionarFiltros(dto);
            ReportService.getInstance().gerarRelatorio(notaFiscalFacade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException op) {
            FacesUtil.printAllFacesMessages(op);
        } catch (Exception ex) {
            logger.error("Erro ao gerar o relatorio de nota fiscal por atividade. Erro {}", ex);
            FacesUtil.addErrorPadrao(ex);
        }
    }

    private void validarFiltros() {
        filtro.validarCamposPorCompetencia();
    }

    public void acompanharGeracao() {
        if (futureNotasFicais != null) {
            if (futureNotasFicais.isDone()) {
                FacesUtil.executaJavaScript("finalizarGeracaoRelatorio()");
            } else if (futureNotasFicais.isCancelled()) {
                FacesUtil.executaJavaScript("pararGeracaoRelatorio()");
            }
        }
    }

    public void finalizarGeracaoRelatorio() {
        assistenteBarraProgresso.setDescricaoProcesso("Geração concluída");
        prontoParaImpressao = true;
    }

    public void cancelarGeracaoRelatorio() {
        futureNotasFicais.cancel(true);
    }

    public void imprimirRelatorio() {
        try {
            ImprimeRelatorioNfse imprimeRelatorioNfse = new ImprimeRelatorioNfse();
            imprimeRelatorioNfse.adicionarParametro("FILTROS", filtro.montarDescricaoFiltros());
            imprimeRelatorioNfse.adicionarParametro("TIPOAPRESENTACAO", filtro.getTipoRelatorioApresentacao().name());
            imprimeRelatorioNfse.imprimirRelatorio("RelatorioResumoNotasFiscaisPorAtividade.jasper", futureNotasFicais.get());
        } catch (Exception e) {
            logger.error("Erro ao imprimir o relatorio {}", e);
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    public FiltroNotaFiscal getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroNotaFiscal filtro) {
        this.filtro = filtro;
    }

    public List<SelectItem> getClassificacoesAtividades() {
        List<SelectItem> classificacoesAtividades = Lists.newArrayList();
        classificacoesAtividades.add(new SelectItem(null, "     "));
        for (ClassificacaoAtividade classificacaoAtividade : ClassificacaoAtividade.values()) {
            classificacoesAtividades.add(new SelectItem(classificacaoAtividade, classificacaoAtividade.getDescricao()));
        }
        return classificacoesAtividades;
    }

    public List<SelectItem> getMeses() {
        List<SelectItem> selectItems = Lists.newArrayList();
        for (Mes mes : Mes.values()) {
            selectItems.add(new SelectItem(mes, mes.getDescricao()));
        }
        return selectItems;
    }

    public List<SelectItem> getSituacoes() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, ""));
        if (!filtro.hasSituacao(SituacaoNota.EMITIDA)) {
            retorno.add(new SelectItem(SituacaoNota.EMITIDA.name(), SituacaoNota.EMITIDA.getDescricao()));
        }
        if (!filtro.hasSituacao(SituacaoNota.PAGA)) {
            retorno.add(new SelectItem(SituacaoNota.PAGA.name(), SituacaoNota.PAGA.getDescricao()));
        }
        if (!filtro.hasSituacao(SituacaoNota.CANCELADA)) {
            retorno.add(new SelectItem(SituacaoNota.CANCELADA.name(), SituacaoNota.CANCELADA.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getPortes() {
        return Util.getListSelectItem(TipoPorte.values());
    }
}
