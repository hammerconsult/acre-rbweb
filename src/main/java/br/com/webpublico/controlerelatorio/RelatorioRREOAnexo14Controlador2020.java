package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.comum.relatorio.ConfiguracaoDeRelatorio;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoFiltros;
import br.com.webpublico.enums.BimestreAnexosLei;
import br.com.webpublico.enums.PortalTransparenciaTipo;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ItemDemonstrativoFacade;
import br.com.webpublico.negocios.ReferenciaAnualFacade;
import br.com.webpublico.negocios.RelatoriosItemDemonstFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Mateus on 12/09/2014.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-rreo-anexo14-2020", pattern = "/relatorio/rreo/anexo14/2020/", viewId = "/faces/financeiro/relatorioslrf/relatoriorreoanexo14-2020.xhtml")
})
@Deprecated
public class RelatorioRREOAnexo14Controlador2020 extends AbstractReport implements Serializable {
    @EJB
    private ReferenciaAnualFacade referenciaAnualFacade;
    @EJB
    private RelatoriosItemDemonstFacade relatoriosItemDemonstFacade;
    @EJB
    private ItemDemonstrativoFacade itemDemonstrativoFacade;
    private List<ItemDemonstrativoComponente> itens;
    private BimestreAnexosLei bimestre;
    private ItemDemonstrativoFiltros itemDemonstrativoFiltros;

    public RelatorioRREOAnexo14Controlador2020() {
        itens = Lists.newArrayList();
    }

    public List<SelectItem> getBimestres() {
        return Util.getListSelectItemSemCampoVazio(BimestreAnexosLei.values(), false);
    }

    public void instanciarItemDemonstrativoFiltros() {
        itemDemonstrativoFiltros = new ItemDemonstrativoFiltros();
        itemDemonstrativoFiltros.setDataInicial("01/01/" + getSistemaFacade().getExercicioCorrente().getAno());
        itemDemonstrativoFiltros.setDataFinal(Util.getDiasMes(bimestre.getMesFinal().getNumeroMes(), getSistemaFacade().getExercicioCorrente().getAno()) + "/" + bimestre.getMesFinal().getNumeroMesString() + "/" + getSistemaFacade().getExercicioCorrente().getAno());
        itemDemonstrativoFiltros.setDataReferencia("01/" + bimestre.getMesInicial().getNumeroMesString() + "/" + getSistemaFacade().getExercicioCorrente().getAno());
        relatoriosItemDemonstFacade.recuperaRelatorioPorTipoEDescricao("Anexo 14", getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
        itemDemonstrativoFiltros.setExercicio(getSistemaFacade().getExercicioCorrente());
    }

    public void gerarRelatorio() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            montarDtoSemApi(dto);
            dto.setApi("contabil/rreo-anexo14-2020/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório: ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void salvarRelatorio() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            RelatorioDTO dto = new RelatorioDTO();
            montarDtoSemApi(dto);
            dto.setApi("contabil/rreo-anexo14-2020/salvar/");
            ConfiguracaoDeRelatorio configuracao = getSistemaFacade().buscarConfiguracaoDeRelatorioPorChave();
            String urlWebreport = configuracao.getUrl() + dto.getApi();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<RelatorioDTO> request = new HttpEntity<>(dto, headers);
            ResponseEntity<byte[]> exchange = restTemplate.exchange(urlWebreport + "gerar", HttpMethod.POST, request, byte[].class);
            byte[] array = exchange.getBody();
            PortalTransparencia anexo = new PortalTransparencia();
            anexo.setExercicio(getExercicioCorrente());
            anexo.setNome("Anexo 14 - Demonstrativo Simplificado do Relatório Resumido da Execução Orçamentária");
            anexo.setMes(bimestre.getMesFinal());
            anexo.setTipo(PortalTransparenciaTipo.RREO);
            salvarArquivoPortalTransparencia(dto, "RelatorioRREOAnexo14", anexo, array);
            FacesUtil.addOperacaoRealizada(" O Relatório " + anexo.toString() + " foi salvo com sucesso.");
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void montarDtoSemApi(RelatorioDTO dto) {
        Exercicio exAnterior = getExercicioFacade().getExercicioPorAno(getExercicioCorrente().getAno() - 1);
        ReferenciaAnual referenciaAnual = referenciaAnualFacade.recuperaReferenciaPorExercicio(getExercicioCorrente());
        List<ItemDemonstrativoComponente> itensAnexo5e6 = popularComponentes(itemDemonstrativoFacade.buscarItensPorExercicioAndRelatorio(getSistemaFacade().getExercicioCorrente(), "", "Anexo 6 - Novo", TipoRelatorioItemDemonstrativo.RREO));
        List<ItemDemonstrativoComponente> itensAnexo08 = popularComponentes(itemDemonstrativoFacade.buscarItensPorExercicioAndRelatorio(getSistemaFacade().getExercicioCorrente(), "", "Anexo 8", TipoRelatorioItemDemonstrativo.RREO));
        List<ItemDemonstrativoComponente> itensAnexo12 = popularComponentes(itemDemonstrativoFacade.buscarItensPorExercicioAndRelatorio(getSistemaFacade().getExercicioCorrente(), "", "Anexo 12", TipoRelatorioItemDemonstrativo.RREO));
        RelatoriosItemDemonst relatorioAnexo01 = relatoriosItemDemonstFacade.recuperaRelatorioPorTipoEDescricao("Anexo 1", getSistemaFacade().getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
        RelatoriosItemDemonst relatorioAnexo02 = relatoriosItemDemonstFacade.recuperaRelatorioPorTipoEDescricao("Anexo 2", getSistemaFacade().getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
        RelatoriosItemDemonst relatorioAnexo03 = relatoriosItemDemonstFacade.recuperaRelatorioPorTipoEDescricao("Anexo 3", getSistemaFacade().getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
        RelatoriosItemDemonst relatorioAnexo04 = relatoriosItemDemonstFacade.recuperaRelatorioPorTipoEDescricao("Anexo 4", getSistemaFacade().getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
        RelatoriosItemDemonst relatorioAnexo06Novo = relatoriosItemDemonstFacade.recuperaRelatorioPorTipoEDescricao("Anexo 6 - Novo", getSistemaFacade().getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
        RelatoriosItemDemonst relatorioAnexo08 = relatoriosItemDemonstFacade.recuperaRelatorioPorTipoEDescricao("Anexo 8", getSistemaFacade().getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
        RelatoriosItemDemonst relatorioAnexo09 = relatoriosItemDemonstFacade.recuperaRelatorioPorTipoEDescricao("Anexo 9", getSistemaFacade().getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
        RelatoriosItemDemonst relatorioAnexo11 = relatoriosItemDemonstFacade.recuperaRelatorioPorTipoEDescricao("Anexo 11", getSistemaFacade().getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
        RelatoriosItemDemonst relatorioAnexo12 = relatoriosItemDemonstFacade.recuperaRelatorioPorTipoEDescricao("Anexo 12", getSistemaFacade().getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
        dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
        dto.adicionarParametro("DATAINICIAL", bimestre.getMesInicial().getDescricao().toUpperCase());
        dto.adicionarParametro("DATAFINAL", bimestre.getMesFinal().getDescricao().toUpperCase());
        dto.adicionarParametro("ID_EXERCICIO", getExercicioCorrente().getId());
        dto.adicionarParametro("ANO_EXERCICIO", getExercicioCorrente().getAno().toString());
        dto.adicionarParametro("ID_EXERCICIO_ANTERIOR", exAnterior.getId());
        dto.adicionarParametro("ANO_EXERCICIO_ANTERIOR", exAnterior.getAno());
        dto.adicionarParametro("mesInicial", bimestre.getMesInicial().getNumeroMesString());
        dto.adicionarParametro("mesFinal", bimestre.getMesFinal().getNumeroMesString());
        dto.adicionarParametro("metaResultadoPrimario", referenciaAnual.getMetaResultadoPrimario());
        dto.adicionarParametro("metaResultadoNominal", referenciaAnual.getMetaResultadoNominal());
        dto.adicionarParametro("isUltimoBimestre", bimestre.isUltimoBimestre());
        dto.adicionarParametro("itens", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itens));
        dto.adicionarParametro("itensAnexo5e6", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itensAnexo5e6));
        dto.adicionarParametro("itensAnexo08", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itensAnexo08));
        dto.adicionarParametro("itensAnexo12", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itensAnexo12));
        dto.adicionarParametro("relatorioAnexo01", relatorioAnexo01.toDto());
        dto.adicionarParametro("relatorioAnexo02", relatorioAnexo02.toDto());
        dto.adicionarParametro("relatorioAnexo03", relatorioAnexo03.toDto());
        dto.adicionarParametro("relatorioAnexo04", relatorioAnexo04.toDto());
        dto.adicionarParametro("relatorioAnexo06Novo", relatorioAnexo06Novo.toDto());
        dto.adicionarParametro("relatorioAnexo08", relatorioAnexo08.toDto());
        dto.adicionarParametro("relatorioAnexo09", relatorioAnexo09.toDto());
        dto.adicionarParametro("relatorioAnexo11", relatorioAnexo11.toDto());
        dto.adicionarParametro("relatorioAnexo12", relatorioAnexo12.toDto());
        dto.setNomeRelatorio("RelatorioRREOAnexo14");
    }

    private List<ItemDemonstrativoComponente> popularComponentes(List<ItemDemonstrativo> itensDemonstrativos) {
        List<ItemDemonstrativoComponente> retorno = Lists.newArrayList();
        if (itensDemonstrativos != null && !itensDemonstrativos.isEmpty()) {
            for (ItemDemonstrativo itemDemonstrativo : itensDemonstrativos) {
                retorno.add(new ItemDemonstrativoComponente(itemDemonstrativo));
            }
        }
        return retorno;
    }

    @URLAction(mappingId = "relatorio-rreo-anexo14-2020", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        bimestre = BimestreAnexosLei.PRIMEIRO_BIMESTRE;
    }

    public List<ItemDemonstrativoComponente> getItens() {
        return itens;
    }

    public void setItens(List<ItemDemonstrativoComponente> itens) {
        this.itens = itens;
    }

    public BimestreAnexosLei getBimestre() {
        return bimestre;
    }

    public void setBimestre(BimestreAnexosLei bimestre) {
        this.bimestre = bimestre;
    }

    public ItemDemonstrativoFiltros getItemDemonstrativoFiltros() {
        return itemDemonstrativoFiltros;
    }

    public void setItemDemonstrativoFiltros(ItemDemonstrativoFiltros itemDemonstrativoFiltros) {
        this.itemDemonstrativoFiltros = itemDemonstrativoFiltros;
    }
}
