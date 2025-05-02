/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.PortalTransparencia;
import br.com.webpublico.entidades.comum.relatorio.ConfiguracaoDeRelatorio;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoFiltros;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.BimestreAnexosLei;
import br.com.webpublico.enums.PortalTransparenciaTipo;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
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
 * @author juggernaut
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-rreo-anexo4-2020", pattern = "/relatorio/rreo/anexo4/2020/", viewId = "/faces/financeiro/relatorioslrf/relatoriorreoanexo04-2020.xhtml")
})
@Deprecated
public class RelatorioRREOAnexo04Controlador2020 extends AbstractReport implements Serializable {

    @EJB
    private RelatoriosItemDemonstFacade relatoriosItemDemonstFacade;
    private BimestreAnexosLei bimestre;
    private List<ItemDemonstrativoComponente> itens;
    private ItemDemonstrativoFiltros itemDemonstrativoFiltros;
    private ItemDemonstrativoFiltros itemDemonstrativoFiltrosExercicioAnterior;

    public RelatorioRREOAnexo04Controlador2020() {
        itens = Lists.newArrayList();
    }

    public List<SelectItem> getBimestres() {
        return Util.getListSelectItemSemCampoVazio(BimestreAnexosLei.values(), false);
    }

    @URLAction(mappingId = "relatorio-rreo-anexo4-2020", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        bimestre = BimestreAnexosLei.PRIMEIRO_BIMESTRE;
    }

    public void instanciarItemDemonstrativoFiltros() {
        itemDemonstrativoFiltros = new ItemDemonstrativoFiltros();
        itemDemonstrativoFiltros.setDataInicial("01/01/" + getSistemaFacade().getExercicioCorrente().getAno());
        itemDemonstrativoFiltros.setDataFinal(Util.getDiasMes(bimestre.getMesFinal().getNumeroMes(), getSistemaFacade().getExercicioCorrente().getAno()) + "/" + bimestre.getMesFinal().getNumeroMesString() + "/" + getSistemaFacade().getExercicioCorrente().getAno());
        itemDemonstrativoFiltros.setDataReferencia("01/" + bimestre.getMesInicial().getNumeroMesString() + "/" + getSistemaFacade().getExercicioCorrente().getAno());
        itemDemonstrativoFiltros.setRelatorio(relatoriosItemDemonstFacade.recuperaRelatorioPorTipoEDescricao("Anexo 4", getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO));
        itemDemonstrativoFiltros.setExercicio(getSistemaFacade().getExercicioCorrente());
    }

    public void instanciarItemDemonstrativoFiltrosExercicioAnterior() {
        itemDemonstrativoFiltrosExercicioAnterior = new ItemDemonstrativoFiltros();
        itemDemonstrativoFiltrosExercicioAnterior.setDataInicial("01/01/" + (getSistemaFacade().getExercicioCorrente().getAno() - 1));
        itemDemonstrativoFiltrosExercicioAnterior.setDataFinal(Util.getDiasMes(bimestre.getMesFinal().getNumeroMes(), (getSistemaFacade().getExercicioCorrente().getAno() - 1)) + "/" + bimestre.getMesFinal().getNumeroMesString() + "/" + (getSistemaFacade().getExercicioCorrente().getAno() - 1));
        itemDemonstrativoFiltrosExercicioAnterior.setDataReferencia("01/" + bimestre.getMesInicial().getNumeroMesString() + "/" + (getSistemaFacade().getExercicioCorrente().getAno() - 1));
        itemDemonstrativoFiltrosExercicioAnterior.setRelatorio(relatoriosItemDemonstFacade.recuperaRelatorioPorTipoEDescricao("Anexo 4", getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO));
        itemDemonstrativoFiltrosExercicioAnterior.setExercicio(buscarExercicioAnterior(getExercicioCorrente()));
    }

    public void gerarRelatorio() {
        try {
            instanciarItemDemonstrativoFiltros();
            instanciarItemDemonstrativoFiltrosExercicioAnterior();
            RelatorioDTO dto = new RelatorioDTO();
            montarDtoSemApi(dto);
            dto.setApi("contabil/rreo-anexo4-2020/");
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
            instanciarItemDemonstrativoFiltros();
            instanciarItemDemonstrativoFiltrosExercicioAnterior();
            RestTemplate restTemplate = new RestTemplate();
            RelatorioDTO dto = new RelatorioDTO();
            montarDtoSemApi(dto);
            dto.setApi("contabil/rreo-anexo4-2020/salvar/");
            ConfiguracaoDeRelatorio configuracao = getSistemaFacade().buscarConfiguracaoDeRelatorioPorChave();
            String urlWebreport = configuracao.getUrl() + dto.getApi();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<RelatorioDTO> request = new HttpEntity<>(dto, headers);
            ResponseEntity<byte[]> exchange = restTemplate.exchange(urlWebreport + "gerar", HttpMethod.POST, request, byte[].class);
            byte[] array = exchange.getBody();
            PortalTransparencia anexo = new PortalTransparencia();
            anexo.setExercicio(getExercicioCorrente());
            anexo.setNome("Anexo 4 - Demonstrativo das Receitas e Despesas Previdenciárias do Regime Próprio de Previdência dos Servidores");
            anexo.setMes(bimestre.getMesFinal());
            anexo.setTipo(PortalTransparenciaTipo.RREO);
            salvarArquivoPortalTransparencia(dto, "RelatorioRREOAnexo04", anexo, array);
            FacesUtil.addOperacaoRealizada(" O Relatório " + anexo.toString() + " foi salvo com sucesso.");
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void montarDtoSemApi(RelatorioDTO dto) {
        dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
        dto.adicionarParametro("DATAINICIAL", bimestre.getMesInicial().getDescricao().toUpperCase());
        dto.adicionarParametro("DATAFINAL", bimestre.getMesFinal().getDescricao().toUpperCase());
        dto.adicionarParametro("BIMESTRE", bimestre.getToDto());
        dto.adicionarParametro("ANO_EXERCICIO", getSistemaFacade().getExercicioCorrente().getAno().toString());
        dto.adicionarParametro("itens", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itens));
        adicionarItemDemonstrativoFiltrosCampoACampo(dto);
        dto.setNomeRelatorio("RELATÓRIO-RREO-ANEXO-04");
    }

    private void adicionarItemDemonstrativoFiltrosCampoACampo(RelatorioDTO dto) {
        dto.adicionarParametro("itemFiltrosRelatorioTemId", itemDemonstrativoFiltros.getRelatorio().getId() != null);
        dto.adicionarParametro("itemFiltrosRelatorioId", itemDemonstrativoFiltros.getRelatorio().getId());
        dto.adicionarParametro("itemFiltrosRelatorioUsaConta", itemDemonstrativoFiltros.getRelatorio().getUsaConta());
        dto.adicionarParametro("itemFiltrosRelatorioUsaPrograma", itemDemonstrativoFiltros.getRelatorio().getUsaPrograma());
        dto.adicionarParametro("itemFiltrosRelatorioUsaAcao", itemDemonstrativoFiltros.getRelatorio().getUsaAcao());
        dto.adicionarParametro("itemFiltrosRelatorioUsaSubAcao", itemDemonstrativoFiltros.getRelatorio().getUsaSubAcao());
        dto.adicionarParametro("itemFiltrosRelatorioUsaFuncao", itemDemonstrativoFiltros.getRelatorio().getUsaFuncao());
        dto.adicionarParametro("itemFiltrosRelatorioUsaSubFuncao", itemDemonstrativoFiltros.getRelatorio().getUsaSubFuncao());
        dto.adicionarParametro("itemFiltrosRelatorioUsaUnidadeOrg", itemDemonstrativoFiltros.getRelatorio().getUsaUnidadeOrganizacional());
        dto.adicionarParametro("itemFiltrosRelatorioUsaFonteRecurso", itemDemonstrativoFiltros.getRelatorio().getUsaFonteRecurso());
        dto.adicionarParametro("itemFiltrosRelatorioUsaTipoDespesa", itemDemonstrativoFiltros.getRelatorio().getUsaTipoDespesa());
        dto.adicionarParametro("itemFiltrosRelatorioUsaContaFinanceira", itemDemonstrativoFiltros.getRelatorio().getUsaContaFinanceira());
        dto.adicionarParametro("itemFiltrosDataFinal", itemDemonstrativoFiltros.getDataFinal());
        dto.adicionarParametro("itemFiltrosDataInicial", itemDemonstrativoFiltros.getDataInicial());
        dto.adicionarParametro("itemFiltrosDataReferencia", itemDemonstrativoFiltros.getDataReferencia());
        dto.adicionarParametro("itemFiltrosIdsUnidades", itemDemonstrativoFiltros.getIdsUnidades());
        dto.adicionarParametro("itemFiltrosTemApresentacao", itemDemonstrativoFiltros.getApresentacaoRelatorio() != null);
        if (itemDemonstrativoFiltros.getApresentacaoRelatorio() != null) {
            dto.adicionarParametro("itemFiltrosApresentacaoRelatorioDTO", itemDemonstrativoFiltros.getApresentacaoRelatorio().getToDto());
        }
        dto.adicionarParametro("itemFiltrosTemExercicio", itemDemonstrativoFiltros.getExercicio() != null);
        if (itemDemonstrativoFiltros.getExercicio() != null) {
            dto.adicionarParametro("itemFiltrosExercicioId", itemDemonstrativoFiltros.getExercicio().getId());
            dto.adicionarParametro("itemFiltrosExercicioAno", itemDemonstrativoFiltros.getExercicio().getAno());
        }
        dto.adicionarParametro("itemFiltrosPesquisouUg", itemDemonstrativoFiltros.getUnidadeGestora() != null);
        if (itemDemonstrativoFiltros.getUnidadeGestora() != null) {
            dto.adicionarParametro("itemFiltrosUnidadeGestoraId", itemDemonstrativoFiltros.getUnidadeGestora().getId());
        }
        dto.adicionarParametro("itemFiltrosTemParametrosRelatorio", (itemDemonstrativoFiltros.getParametros() != null && !itemDemonstrativoFiltros.getParametros().isEmpty()));
        if (itemDemonstrativoFiltros.getParametros() != null && !itemDemonstrativoFiltros.getParametros().isEmpty()) {
            dto.adicionarParametro("itemFiltrosParametrosRelatorios", ParametrosRelatorios.parametrosToDto(itemDemonstrativoFiltros.getParametros()));
        }
        if (itemDemonstrativoFiltrosExercicioAnterior.getExercicio() != null) {
            dto.adicionarParametro("itemFiltrosExercicioAnteriorId", itemDemonstrativoFiltrosExercicioAnterior.getExercicio().getId());
            dto.adicionarParametro("itemFiltrosDataFinalAnterior", itemDemonstrativoFiltrosExercicioAnterior.getDataFinal());
            dto.adicionarParametro("itemFiltrosDataInicialAnterior", itemDemonstrativoFiltrosExercicioAnterior.getDataInicial());
        }
    }

    private Exercicio buscarExercicioAnterior(Exercicio exercicioCorrente) {
        return getExercicioFacade().getExercicioPorAno(exercicioCorrente.getAno() - 1);
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
}
