package br.com.webpublico.nfse.relatorio.controladores;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.nfse.domain.dtos.FiltroNotaFiscalNaoEscriturada;
import br.com.webpublico.nfse.enums.Exigibilidade;
import br.com.webpublico.nfse.enums.SituacaoNota;
import br.com.webpublico.nfse.enums.TipoDocumentoNfse;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.beust.jcommander.internal.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-relatorio-notas-nao-escrituradas",
        pattern = "/nfse/relatorio/notas-nao-escrituradas/",
        viewId = "/faces/tributario/nfse/relatorio/notas-nao-escrituradas.xhtml")
})
public class RelatorioNotasNaoEscrituradasControlador extends AbstractReport {

    private FiltroNotaFiscalNaoEscriturada filtro;

    @URLAction(mappingId = "novo-relatorio-notas-nao-escrituradas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtro = new FiltroNotaFiscalNaoEscriturada();
    }

    public void gerarRelatorio(TipoRelatorioDTO tipoRelatorio) {
        try {
            filtro.validarCampos();
            RelatorioDTO dto = getRelatorioDTO(tipoRelatorio);
            dto.setApi("tributario/nfse/notas-fiscais-nao-escrituradas/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        } catch (Exception ex) {
            logger.error("Erro ao gerar o relatorio {}", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void gerarRelatorioPDF() {
        gerarRelatorio(TipoRelatorioDTO.PDF);
    }

    public void gerarRelatorioExcel() {
        gerarRelatorio(TipoRelatorioDTO.XLS);
    }

    private RelatorioDTO getRelatorioDTO(TipoRelatorioDTO tipoRelatorio) {
        RelatorioDTO dto = new RelatorioDTO();
        dto.setTipoRelatorio(tipoRelatorio);
        dto.setNomeRelatorio("RELATORIO-RECEITA-CONTRIBUINTE" + tipoRelatorio.getExtension());
        dto.setNomeParametroBrasao("BRASAO");
        dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.adicionarParametro("MODULO", "Tributário");
        dto.adicionarParametro("MUNICIPIO", "Municipio de Rio Branco");
        dto.adicionarParametro("SECRETARIA", "Secretaria Municipal de Finanças");
        dto.adicionarParametro("SISTEMA", "Nota Fiscal de Serviços Eletrônica - NFS-e");
        dto.adicionarParametro("TITULO", "Relatório de Notas Fiscais não escrituradas");
        dto.adicionarParametro("FILTROS", filtro.getFiltros());
        dto.adicionarParametro("WHERE", filtro.getWhere());
        dto.adicionarParametro("ORDER_BY", filtro.getOrderBy());
        return dto;
    }

    public List<SelectItem> getTiposDocumentos() {
        return Util.getListSelectItem(TipoDocumentoNfse.values());
    }

    public List<SelectItem> getSituacoes() {
        return Util.getListSelectItem(SituacaoNota.values());
    }

    public FiltroNotaFiscalNaoEscriturada getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroNotaFiscalNaoEscriturada filtro) {
        this.filtro = filtro;
    }

    public void atribuirTomador(ActionEvent evento) {
        filtro.setCpfCnpjTomadorInicial("");
        filtro.setCpfCnpjTomadorFinal("");
        Pessoa pessoa = (Pessoa) evento.getComponent().getAttributes().get("objeto");
        if (pessoa != null) {
            filtro.setCpfCnpjTomadorInicial(pessoa.getCpf_Cnpj());
            filtro.setCpfCnpjTomadorFinal(pessoa.getCpf_Cnpj());
        }
    }

    public List<SelectItem> getExigibilidades() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (Exigibilidade value : Exigibilidade.values()) {
            if (!filtro.hasExigibilidade(value))
                toReturn.add(new SelectItem(value, value.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposAgrupamento() {
        return Util.getListSelectItemSemCampoVazio(FiltroNotaFiscalNaoEscriturada.TipoAgrupamento.values());
    }
}
