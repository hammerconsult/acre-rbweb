package br.com.webpublico.controlerelatorio.rh;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.comum.relatorio.ConfiguracaoDeRelatorio;
import br.com.webpublico.entidadesauxiliares.FiltroRelatorioServidoresAtivosPorSecretaria;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.comum.ConfiguracaoDeRelatorioFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
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
import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * @author octavio
 */

@ManagedBean(name = "relatorioQuantitativoServidoresPorCargoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "quantitativo-servidores-por-cargo", pattern = "/relatorio/quantitativo-servidores-por-cargo/", viewId = "/faces/rh/relatorios/relatorioquantitativoservidorporcargo.xhtml")
})
public class RelatorioQuantitativoServidoresPorCargoControlador implements Serializable {


    private static final String RELATORIO_QUANTITATIVO_SERVIDORES_CARGO = "Relatório com Quantitativo de Servidores por Cargo e Secretaria";

    private Date dataReferencia;

    @EJB
    private ConfiguracaoDeRelatorioFacade configuracaoDeRelatorioFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private FiltroRelatorioServidoresAtivosPorSecretaria filtroRelatorio;

    public RelatorioQuantitativoServidoresPorCargoControlador() {
        limparCampos();
    }

    public FiltroRelatorioServidoresAtivosPorSecretaria getFiltroRelatorio() {
        return filtroRelatorio;
    }

    public List<SelectItem> getMeses() {
        return Util.getListSelectItem(Mes.values(), false);
    }

    public List<HierarquiaOrganizacional> completarHierarquia(String parte) {
        return hierarquiaOrganizacionalFacade.filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
            sistemaFacade.getDataOperacao());
    }

    public Date getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(Date dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    public void gerarRelatorio() {
        try {
            validarDados();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setApi("rh/quantitativo-servidores-por-cargo/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    private RelatorioDTO montarRelatorioDTO() {
        String referencia = DataUtil.getDataFormatada(dataReferencia, "MM/yyyy");
        RelatorioDTO dto = new RelatorioDTO();
        dto.setNomeParametroBrasao("BRASAO");
        dto.setNomeRelatorio("RELATÓRIO COM QUANTITATIVO DE SERVIDORES POR CARGO E SECRETARIA");
        dto.adicionarParametro("MODULO", "Recursos Humanos");
        dto.adicionarParametro("SECRETARIA", "DEPARTAMENTO DE RECURSOS HUMANOS");
        dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO COM QUANTITATIVO DE SERVIDORES POR CARGO E SECRETARIA");
        dto.adicionarParametro("MUNICIPIO", "PREFEITURA MUNICIPAL DE RIO BRANCO");
        dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome());
        dto.adicionarParametro("DATAOPERACAO", DataUtil.getDataFormatada(UtilRH.getDataOperacao()));
        dto.adicionarParametro("dataReferencia", DataUtil.getDataFormatada(this.dataReferencia));
        dto.adicionarParametro("referencia", referencia);
        return dto;
    }


    public void limparCampos() {
        filtroRelatorio = new FiltroRelatorioServidoresAtivosPorSecretaria();
    }

    private ResponseEntity<byte[]> retornarByte(RelatorioDTO dto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RelatorioDTO> request = new HttpEntity<>(dto, headers);
        ConfiguracaoDeRelatorio configuracao = configuracaoDeRelatorioFacade.getConfiguracaoPorChave();
        return new RestTemplate().exchange(configuracao.getUrl() + dto.getApi() + "gerar", HttpMethod.POST, request, byte[].class);
    }

    public StreamedContent gerarExcel() {
        try {
            validarDados();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setApi("rh/quantitativo-servidores-por-cargo/excel/");
            dto.adicionarParametro("NOME_ARQUIVO", RELATORIO_QUANTITATIVO_SERVIDORES_CARGO);
            ResponseEntity<byte[]> responseEntity = retornarByte(dto);
            byte[] bytes = responseEntity.getBody();
            InputStream stream = new ByteArrayInputStream(bytes);
            return new DefaultStreamedContent(stream, "application/xls", RELATORIO_QUANTITATIVO_SERVIDORES_CARGO + ".xls");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
        return null;
    }

    private void validarDados() {
        ValidacaoException ve = new ValidacaoException();
        if (dataReferencia == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Data de Referência.");
        }
        ve.lancarException();
    }
}
