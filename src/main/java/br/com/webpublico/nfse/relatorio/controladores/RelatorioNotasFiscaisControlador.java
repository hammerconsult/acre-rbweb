package br.com.webpublico.nfse.relatorio.controladores;

import br.com.webpublico.DateUtils;
import br.com.webpublico.entidades.EnderecoCorreio;
import br.com.webpublico.entidades.Telefone;
import br.com.webpublico.enums.TipoPessoa;
import br.com.webpublico.enums.TipoRelatorioApresentacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.nfse.domain.dtos.FiltroNotaFiscal;
import br.com.webpublico.nfse.enums.Exigibilidade;
import br.com.webpublico.nfse.enums.SituacaoNota;
import br.com.webpublico.nfse.facades.NotaFiscalFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-relatorio-notas-fiscais",
        pattern = "/nfse/relatorio/notas-fiscais/",
        viewId = "/faces/tributario/nfse/relatorio/notas-fiscais.xhtml")
})
public class RelatorioNotasFiscaisControlador {

    private static Logger logger = LoggerFactory.getLogger(RelatorioNotasFiscaisControlador.class);

    @EJB
    private NotaFiscalFacade notaFiscalService;
    private FiltroNotaFiscal filtro;
    private SituacaoNota situacaoNota;

    @URLAction(mappingId = "novo-relatorio-notas-fiscais", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtro = new FiltroNotaFiscal();
        situacaoNota = null;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            filtro.validarCampos(tipoRelatorioExtensao);
            RelatorioDTO dto = new RelatorioDTO();
            if (TipoRelatorioDTO.XLS.name().equals(tipoRelatorioExtensao)) {
                dto.setTipoRelatorio(TipoRelatorioDTO.XLS);
                dto.setApi("tributario/nfse/nota-fiscal/excel/");
                dto.adicionarParametro("FILTROS_EXCEL", filtro.montarMapFiltrosExcel());
                dto.adicionarParametro("PERIODO_INICIAL", DateUtils.getDataFormatada(filtro.getPeriodoInicial().getTime()));
                dto.adicionarParametro("PERIODO_FINAL", DateUtils.getDataFormatada(filtro.getPeriodoFinal().getTime()));
            } else {
                dto.setApi("tributario/nfse/nota-fiscal/");
            }
            dto.setNomeRelatorio("relatorio-nota-fiscal");
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("MUNICIPIO", "Municipio de Rio Branco");
            dto.adicionarParametro("SECRETARIA", "Secretaria Municipal de Finanças");
            dto.adicionarParametro("TITULO", "RELATÓRIO DE NOTAS FISCAIS - " + filtro.getTipoRelatorioApresentacao().name());
            dto.adicionarParametro("TIPO_APRESENTACAO", filtro.getTipoRelatorioApresentacao().name());
            dto.adicionarParametro("TIPO_AGRUPAMENTO", filtro.getTipoAgrupamento().name());
            dto.adicionarParametro("USUARIO", notaFiscalService.getSistemaFacade().getUsuarioCorrente().getNome());
            dto.adicionarParametro("SOMENTE_TOTALIZADOR", filtro.getSomenteTotalizador());
            filtro.adicionarFiltros(dto);
            ReportService.getInstance().gerarRelatorio(notaFiscalService.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException op) {
            FacesUtil.printAllFacesMessages(op);
        } catch (Exception ex) {
            logger.error("Erro ao gerar o relatorio de encerramento mensal de servico. Erro {}", ex);
            FacesUtil.addErrorPadrao(ex);
        }
    }

    public FiltroNotaFiscal getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroNotaFiscal filtro) {
        this.filtro = filtro;
    }

    public SituacaoNota getSituacaoNota() {
        return situacaoNota;
    }

    public void setSituacaoNota(SituacaoNota situacaoNota) {
        this.situacaoNota = situacaoNota;
    }

    public List<SelectItem> getExigibilidades() {
        return Util.getListSelectItem(Exigibilidade.values());
    }

    public List<SelectItem> getTiposApresentacao() {
        return Util.getListSelectItemSemCampoVazio(TipoRelatorioApresentacao.values());
    }

    public List<SelectItem> getTiposAgrupamento() {
        return Util.getListSelectItemSemCampoVazio(FiltroNotaFiscal.TipoAgrupamento.values());
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

    public void adicionarSituacao() {
        if (filtro.getSituacoes() == null) {
            filtro.setSituacoes(new ArrayList());
        }
        filtro.getSituacoes().add(situacaoNota);
        situacaoNota = null;
    }

    public void removerSituacao(SituacaoNota situacaoNota) {
        filtro.getSituacoes().remove(situacaoNota);
    }

    public List<SelectItem> getTiposTomador() {
        return Util.getListSelectItem(TipoPessoa.values(), false);
    }

    public void selecionouTipoTomador() {
        filtro.setContribuinte(null);
    }
}
