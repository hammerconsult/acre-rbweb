package br.com.webpublico.nfse.relatorio.controladores;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.comum.relatorio.ConfiguracaoDeRelatorio;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.RegimeTributario;
import br.com.webpublico.enums.TipoIssqn;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.comum.ConfiguracaoDeRelatorioFacade;
import br.com.webpublico.nfse.domain.dtos.FiltroReceitaContribuinte;
import br.com.webpublico.nfse.enums.Exigibilidade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-relatorio-receita-contribuinte",
        pattern = "/nfse/relatorio/receita-receita-contribuinte/",
        viewId = "/faces/tributario/nfse/relatorio/relatorio-receita-contribuinte.xhtml")
})
public class RelatorioReceitaContribuinteControlador extends AbstractReport {

    @EJB
    private ConfiguracaoDeRelatorioFacade configuracaoDeRelatorioFacade;
    private FiltroReceitaContribuinte filtro;
    private StreamedContent streamedContent;

    @URLAction(mappingId = "novo-relatorio-receita-contribuinte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtro = new FiltroReceitaContribuinte();
        filtro.setExercicioInicial(getSistemaFacade().getExercicioCorrente());
        filtro.setMesInicial(Mes.getMesToInt(DataUtil.getMes(getSistemaFacade().getDataOperacao())));
        filtro.setExercicioFinal(getSistemaFacade().getExercicioCorrente());
        filtro.setMesFinal(Mes.getMesToInt(DataUtil.getMes(getSistemaFacade().getDataOperacao())));
    }

    private RelatorioDTO getRelatorioDTO() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.setNomeParametroBrasao("BRASAO");
        dto.setNomeRelatorio("RELATORIO-RECEITA-CONTRIBUINTE");
        dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.adicionarParametro("MODULO", "Tributário");
        dto.adicionarParametro("MUNICIPIO", "Prefeitura Municipal de Rio Branco AC");
        dto.adicionarParametro("SECRETARIA", "Secretaria de Desenvolvimento Econômico e Finanças");
        dto.adicionarParametro("SISTEMA", "Nota Fiscal de Serviços Eletrônica - NFS-e");
        dto.adicionarParametro("TITULO", "Relatório de Receita por Contribuinte no Período");
        dto.adicionarParametro("WHERE", filtro.getWhere());
        dto.adicionarParametro("HAVING", filtro.getHaving());
        dto.adicionarParametro("FILTROS", filtro.getFiltros());
        return dto;
    }

    public void gerarRelatorio() {
        try {
            filtro.validarCampos();
            RelatorioDTO dto = getRelatorioDTO();
            dto.setApi("tributario/nfse/receita-contribuinte/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void gerarExcel() {
        try {
            streamedContent = null;
            filtro.validarCampos();
            RelatorioDTO dto = getRelatorioDTO();
            dto.adicionarParametro("NOMEARQUIVO", "RELATORIO-RECEITA-CONTRIBUINTE");
            dto.setApi("tributario/nfse/receita-contribuinte/excel/");
            ResponseEntity<byte[]> responseEntity = retornarByte(dto);
            byte[] bytes = responseEntity.getBody();
            InputStream stream = new ByteArrayInputStream(bytes);
            streamedContent = new DefaultStreamedContent(stream, "application/xls", "RELATORIO-RECEITA-CONTRIBUINTE.xls");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private ResponseEntity<byte[]> retornarByte(RelatorioDTO dto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RelatorioDTO> request = new HttpEntity<>(dto, headers);
        ConfiguracaoDeRelatorio configuracao = configuracaoDeRelatorioFacade.getConfiguracaoPorChave();
        return new RestTemplate().exchange(configuracao.getUrl() + dto.getApi() + "gerar", HttpMethod.POST, request, byte[].class);
    }

    public List<SelectItem> getRegimesTributario() {
        return Util.getListSelectItem(RegimeTributario.values());
    }

    public List<SelectItem> getTiposIssqn() {
        return Util.getListSelectItem(TipoIssqn.values());
    }

    public List<SelectItem> getExigibilidades() {
        return Util.getListSelectItem(Exigibilidade.values());
    }

    public FiltroReceitaContribuinte getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroReceitaContribuinte filtro) {
        this.filtro = filtro;
    }

    public List<SelectItem> getMeses() {
        List<SelectItem> selectItems = Lists.newArrayList();
        for (Mes mes : Mes.values()) {
            selectItems.add(new SelectItem(mes, mes.getDescricao()));
        }
        return selectItems;
    }

    public StreamedContent getStreamedContent() {
        return streamedContent;
    }

    public void setStreamedContent(StreamedContent streamedContent) {
        this.streamedContent = streamedContent;
    }
}
