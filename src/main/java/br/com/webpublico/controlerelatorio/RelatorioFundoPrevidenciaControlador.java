/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controlerelatorio.rh.RelatorioPagamentoRH;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.TipoPrevidenciaFP;
import br.com.webpublico.entidades.comum.relatorio.ConfiguracaoDeRelatorio;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.TipoPrevidenciaFPFacade;
import br.com.webpublico.negocios.comum.ConfiguracaoDeRelatorioFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author peixe
 */
@ViewScoped
@ManagedBean(name = "relatorioFundoPrevidenciaControlador")
@URLMappings(mappings = {
    @URLMapping(id = "gerarRelatorioFundoPrevidenciaContribuicao", pattern = "/relatorio/fundo-previdencia-contribuicao-patronal/", viewId = "/faces/rh/relatorios/relatoriocontribuicaopatronal.xhtml")
})
public class RelatorioFundoPrevidenciaControlador extends RelatorioPagamentoRH implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioFundoPrevidenciaControlador.class);
    @EJB
    private ConfiguracaoDeRelatorioFacade configuracaoDeRelatorioFacade;

    private HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada;
    private ConverterAutoComplete converterTipoPrevidencia;
    private TipoPrevidenciaFP tipoPrevidenciaFP;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private TipoPrevidenciaFPFacade tipoPrevidenciaFPFacade;
    private Boolean detalhado;

    public RelatorioFundoPrevidenciaControlador() {
    }

    public Boolean getDetalhado() {
        return detalhado == null ? false : detalhado;
    }

    public void setDetalhado(Boolean detalhado) {
        this.detalhado = detalhado;
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
            validarCampos();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DE FUNDO PREVIDENCIÁRIO - CONTRIBUIÇÃO PATRONAL");
            dto.setApi("rh/fundo-previdencia/excel/");
            ResponseEntity<byte[]> responseEntity = retornarByte(dto);
            byte[] bytes = responseEntity.getBody();
            InputStream stream = new ByteArrayInputStream(bytes);
            return new DefaultStreamedContent(stream, "application/xls", "RELATORIO-DE-FUNDO-PREVIDENCIARIO_CONTRIBUICAO-PATRONAL.xls");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
        return null;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setApi("rh/fundo-previdencia/");
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
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
        RelatorioDTO dto = new RelatorioDTO();
        dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.setNomeParametroBrasao("BRASAO");
        dto.adicionarParametro("PREVIDENCIA", "CONTRIBUIÇÃO: " + tipoPrevidenciaFP.getDescricao());
        dto.adicionarParametro("tipoPrevidenciaFPCodigo", tipoPrevidenciaFP.getCodigo());
        dto.adicionarParametro("MES", (getMes() - 1));
        dto.adicionarParametro("versao", montarCampoVersao());
        dto.adicionarParametro("detalhado", detalhado);
        dto.adicionarParametro("ANO", getAno());
        dto.adicionarParametro("codigo", hierarquiaOrganizacionalSelecionada.getCodigoSemZerosFinais() + "%");
        dto.adicionarParametro("tipoFolhaDePagamentoDTO", getTipoFolhaDePagamento().getToDto());
        dto.adicionarParametro("MODULO", "Recursos Humanos");
        dto.adicionarParametro("SECRETARIA", "DEPARTAMENTO DE RECURSOS HUMANOS");
        dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DE FUNDO PREVIDENCIÁRIO - CONTRIBUIÇÃO PATRONAL");
        dto.adicionarParametro("DATAOPERACAO", UtilRH.getDataOperacao());
        dto.setNomeRelatorio("RELATORIO-DE-FUNDO-PREVIDENCIARIO_CONTRIBUICAO-PATRONAL");
        return dto;
    }

    @Override
    public String montarCampoVersao() {
        if (getVersao() != null) {
            return " and ficha.versao = " + getVersao() + " ";
        }
        return " ";
    }

    public void validarCampos() {
        ValidacaoException val = new ValidacaoException();
        if (getMes() == null) {
            val.adicionarMensagemDeCampoObrigatorio("O campo Mês é obrigatório");

        }
        if (getAno() == null || (getAno() != null && getAno() == 0)) {
            val.adicionarMensagemDeCampoObrigatorio("O campo Ano é obrigatório");
        }
        if (getTipoFolhaDePagamento() == null) {
            val.adicionarMensagemDeCampoObrigatorio("Selecione o Tipo de Folha Corretamente");
        }

        if (getMes() != null && (getMes() < 1 || getMes() > 12)) {
            val.adicionarMensagemDeCampoObrigatorio("Favor informar um mês entre 01 (Janeiro) e 12 (Dezembro).");
        }

        if (hierarquiaOrganizacionalSelecionada == null) {
            val.adicionarMensagemDeCampoObrigatorio("O campo Órgão é obrigatório");
        }

        if (tipoPrevidenciaFP == null) {
            val.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Previdência é obrigatório");
        }
        val.lancarException();
    }


    public HierarquiaOrganizacional getHierarquiaOrganizacionalSelecionada() {
        return hierarquiaOrganizacionalSelecionada;
    }

    public void setHierarquiaOrganizacionalSelecionada(HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada) {
        this.hierarquiaOrganizacionalSelecionada = hierarquiaOrganizacionalSelecionada;
    }


    public List<HierarquiaOrganizacional> completarHierarquias(String parte) {
        return hierarquiaOrganizacionalFacade.filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    @URLAction(mappingId = "gerarRelatorioFundoPrevidenciaContribuicao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        hierarquiaOrganizacionalSelecionada = null;
        setMes(null);
        setAno(null);
        setTipoFolhaDePagamento(null);
        setTipoPrevidenciaFP(null);
        detalhado = false;
    }

    @Override
    public List<SelectItem> getTiposFolha() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (TipoFolhaDePagamento tipo : TipoFolhaDePagamento.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getTipoPrevidencia() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (TipoPrevidenciaFP previdenciaFP : tipoPrevidenciaFPFacade.lista()) {
            retorno.add(new SelectItem(previdenciaFP, previdenciaFP.toString()));
        }
        return retorno;
    }

    public ConverterAutoComplete getConverterTipoPrevidencia() {
        if (converterTipoPrevidencia == null) {
            converterTipoPrevidencia = new ConverterAutoComplete(TipoPrevidenciaFP.class, tipoPrevidenciaFPFacade);
        }
        return converterTipoPrevidencia;
    }

    public TipoPrevidenciaFP getTipoPrevidenciaFP() {
        return tipoPrevidenciaFP;
    }

    public void setTipoPrevidenciaFP(TipoPrevidenciaFP tipoPrevidenciaFP) {
        this.tipoPrevidenciaFP = tipoPrevidenciaFP;
    }
}
