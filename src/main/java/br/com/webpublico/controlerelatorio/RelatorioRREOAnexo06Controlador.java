/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.PortalTransparencia;
import br.com.webpublico.entidades.ReferenciaAnual;
import br.com.webpublico.entidades.RelatoriosItemDemonst;
import br.com.webpublico.entidades.comum.relatorio.ConfiguracaoDeRelatorio;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.PortalTransparenciaTipo;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ReferenciaAnualFacade;
import br.com.webpublico.negocios.RelatoriosItemDemonstFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.MesDTO;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author juggernaut
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-rreo-anexo6", pattern = "/relatorio/rreo/anexo6/", viewId = "/faces/financeiro/relatorioslrf/relatoriorreoanexo06.xhtml")})
public class RelatorioRREOAnexo06Controlador extends AbstractReport implements Serializable {

    @EJB
    private ReferenciaAnualFacade referenciaAnualFacade;
    @EJB
    private RelatoriosItemDemonstFacade relatoriosItemDemonstFacade;
    private List<ItemDemonstrativoComponente> itens;
    private RelatoriosItemDemonst relatoriosItemDemonst;
    private String mesInicial;
    private String mesFinal;
    private BigDecimal previsaoAtualizada;
    private BigDecimal receitaRealizadaAteBimestre;
    private BigDecimal receitaRealizadaAteBimestreExAnterior;

    public RelatorioRREOAnexo06Controlador() {
        itens = new ArrayList<>();
    }

    @URLAction(mappingId = "relatorio-rreo-anexo6", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        previsaoAtualizada = BigDecimal.ZERO;
        receitaRealizadaAteBimestre = BigDecimal.ZERO;
        receitaRealizadaAteBimestreExAnterior = BigDecimal.ZERO;
    }

    public void gerarRelatorio() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            montarDtoSemApi(dto);
            dto.setApi("contabil/rreo-anexo6/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void salvarRelatorio() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            RelatorioDTO dto = new RelatorioDTO();
            montarDtoSemApi(dto);
            dto.setApi("contabil/rreo-anexo6/salvar/");
            ConfiguracaoDeRelatorio configuracao = getSistemaFacade().buscarConfiguracaoDeRelatorioPorChave();
            String urlWebreport = configuracao.getUrl() + dto.getApi();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<RelatorioDTO> request = new HttpEntity<>(dto, headers);
            ResponseEntity<byte[]> exchange = restTemplate.exchange(urlWebreport + "gerar", HttpMethod.POST, request, byte[].class);
            byte[] array = exchange.getBody();
            PortalTransparencia anexo = new PortalTransparencia();
            anexo.setExercicio(getSistemaFacade().getExercicioCorrente());
            anexo.setNome("Anexo 6 - Demonstrativo do Resultado Primário - Estados, Distrito Federal e Municípios");
            anexo.setMes(Mes.getMesToInt(Integer.valueOf(mesFinal)));
            anexo.setTipo(PortalTransparenciaTipo.RREO);
            salvarArquivoPortalTransparencia(dto, "RelatorioRREOAnexo06", anexo, array);
            FacesUtil.addOperacaoRealizada(" O Relatório " + anexo.toString() + " foi salvo com sucesso.");
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void montarDtoSemApi(RelatorioDTO dto) {
        relatoriosItemDemonst = relatoriosItemDemonstFacade.recuperaRelatorioPorTipoEDescricao("Anexo 6", getSistemaFacade().getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
        dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
        dto.adicionarParametro("DATAINICIAL", MesDTO.getMesToInt(Integer.parseInt(mesInicial)).getDescricao().toUpperCase());
        dto.adicionarParametro("BIMESTRE_FINAL", mesFinal.equals("12"));
        dto.adicionarParametro("DATAFINAL", MesDTO.getMesToInt(Integer.parseInt(mesFinal)).getDescricao().toUpperCase());
        dto.adicionarParametro("ID_EXERCICIO", getSistemaFacade().getExercicioCorrente().getId());
        dto.adicionarParametro("ANO_EXERCICIO", getSistemaFacade().getExercicioCorrente().getAno().toString());
        Exercicio exAnterior = getExercicioFacade().getExercicioPorAno(getSistemaFacade().getExercicioCorrente().getAno() - 1);
        dto.adicionarParametro("ID_EXERCICIO_ANTERIOR", exAnterior.getId());
        dto.adicionarParametro("ANO_EXERCICIO_ANTERIOR", exAnterior.getAno());
        dto.adicionarParametro("itens", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itens));
        dto.adicionarParametro("mesFinal", mesFinal);
        ReferenciaAnual referenciaAnual = referenciaAnualFacade.recuperaReferenciaPorExercicio(getSistemaFacade().getExercicioCorrente());
        dto.adicionarParametro("metResultadoPrimarioReferenciaAnual", referenciaAnual.getMetaResultadoPrimario() != null ? referenciaAnual.getMetaResultadoPrimario() : BigDecimal.ZERO);
        dto.adicionarParametro("relatoriosItemDemonst", relatoriosItemDemonst.toDto());
        dto.setNomeRelatorio("RelatorioRREOAnexo06");
    }

    public String getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(String mesFinal) {
        this.mesFinal = mesFinal;
        Integer mes = Integer.parseInt(this.mesFinal);
        if (mes == 12) {
            this.mesInicial = "11";
        } else {
            this.mesInicial = "0" + (mes - 1);
        }
    }

    public String getMesInicial() {
        return mesInicial;
    }

    public void setMesInicial(String mesInicial) {
        this.mesInicial = mesInicial;
    }

    public List<ItemDemonstrativoComponente> getItens() {
        return itens;
    }

    public void setItens(List<ItemDemonstrativoComponente> itens) {
        this.itens = itens;
    }

    public RelatoriosItemDemonst getRelatoriosItemDemonst() {
        return relatoriosItemDemonst;
    }

    public void setRelatoriosItemDemonst(RelatoriosItemDemonst relatoriosItemDemonst) {
        this.relatoriosItemDemonst = relatoriosItemDemonst;
    }

    public BigDecimal getPrevisaoAtualizada() {
        return previsaoAtualizada;
    }

    public void setPrevisaoAtualizada(BigDecimal previsaoAtualizada) {
        this.previsaoAtualizada = previsaoAtualizada;
    }

    public BigDecimal getReceitaRealizadaAteBimestre() {
        return receitaRealizadaAteBimestre;
    }

    public void setReceitaRealizadaAteBimestre(BigDecimal receitaRealizadaAteBimestre) {
        this.receitaRealizadaAteBimestre = receitaRealizadaAteBimestre;
    }

    public BigDecimal getReceitaRealizadaAteBimestreExAnterior() {
        return receitaRealizadaAteBimestreExAnterior;
    }

    public void setReceitaRealizadaAteBimestreExAnterior(BigDecimal receitaRealizadaAteBimestreExAnterior) {
        this.receitaRealizadaAteBimestreExAnterior = receitaRealizadaAteBimestreExAnterior;
    }
}
