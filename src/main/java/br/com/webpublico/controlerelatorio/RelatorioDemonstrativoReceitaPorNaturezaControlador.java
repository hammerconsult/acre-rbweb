/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltrosRelatorioDemonstrativoReceitaPorNatureza;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.comum.ConfiguracaoDeRelatorioFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author juggernaut
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "demonstrativo-receita-por-natureza", pattern = "/relatorio/demonstrativo-receita-por-natureza/", viewId = "/faces/financeiro/relatorio/relatoriodemonstreceitanatureza.xhtml")
})
public class RelatorioDemonstrativoReceitaPorNaturezaControlador extends RelatorioContabilSuperControlador implements Serializable {

    @EJB
    private ConfiguracaoDeRelatorioFacade configuracaoDeRelatorioFacade;
    private String mesFinal;
    private Boolean exibirContaDeDestinacao;
    private FiltrosRelatorioDemonstrativoReceitaPorNatureza filtroRelatorio;
    private Boolean exibirCodigoCo;

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarDatasSemVerificarExercicioLogado();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    @URLAction(mappingId = "demonstrativo-receita-por-natureza", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        super.limparCamposGeral();
        exibirContaDeDestinacao = false;
        filtroRelatorio = new FiltrosRelatorioDemonstrativoReceitaPorNatureza();
        exibirCodigoCo = false;
    }

    public List<FonteDeRecursos> completarFontesDeRecursos(String parte) {
        return relatorioContabilSuperFacade.getFonteDeRecursosFacade().listaFiltrandoPorExercicio(parte.trim(), buscarExercicioPelaDataFinal());
    }

    public List<Conta> completarContasDeDestinacao(String filtro) {
        return relatorioContabilSuperFacade.getContaFacade().buscarContasDeDestinacaoDeRecursos(filtro, buscarExercicioPelaDataFinal());
    }

    public void limparContasAndUnidades() {
        fonteDeRecursos = null;
        listaUnidades = Lists.newArrayList();
        unidadeGestora = null;
        setContasDestinacoes(Lists.newArrayList());
    }

    private RelatorioDTO montarRelatorioDTO() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.setNomeParametroBrasao("IMAGEM");
        dto.setNomeRelatorio("DEMONSTRATIVO-RECEITA-POR-NATUREZA");
        dto.setNomeParametroSubreportDir("SUBREPORT_DIR");
        dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
        dto.adicionarParametro("ANO_EXERCICIO", getSistemaFacade().getExercicioCorrente().getAno().toString());
        dto.adicionarParametro("MODULO", "Contábil");
        dto.adicionarParametro("DATAINICIAL", DataUtil.getDataFormatada(dataInicial));
        dto.adicionarParametro("DATAFINAL", DataUtil.getDataFormatada(dataFinal));
        dto.adicionarParametro("FONTES", exibirContaDeDestinacao);
        dto.adicionarParametro("exibirCodigoCo", exibirCodigoCo);
        dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
        dto.adicionarParametro("FILTRO_RELATORIO", buscarPeriodo());
        dto.adicionarParametro("APRESENTACAO", apresentacao.name());
        dto.adicionarParametro("apresentacaoRelatorio", apresentacao.getToDto());
        dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametrosRelatorio()));
        dto.adicionarParametro("FILTRO", filtros);
        if (unidadeGestora != null) {
            dto.adicionarParametro("unidadeGestoraId", unidadeGestora.getId());
        }
        dto.adicionarParametro("NOME_ARQUIVO", getNomeArquivo());
        dto.setNomeRelatorio(getNomeRelatorio());
        dto.setApi("contabil/demonstrativo-receita-natureza/");
        return dto;
    }

    @Override
    public String getNomeArquivo() {
        return "DEMONSTRATIVO-RECEITA-POR-NATUREZA-" + DataUtil.getDataFormatada(dataInicial, "ddMMyyyy") + "A" + DataUtil.getDataFormatada(dataFinal, "ddMMyyyy");
    }

    private String buscarPeriodo() {
        if (dataInicial != null
            && dataFinal != null) {
            return DataUtil.getDataFormatada(dataInicial) + " até " + DataUtil.getDataFormatada(dataFinal);
        }
        return "";
    }

    private List<ParametrosRelatorios> montarParametrosRelatorio() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        filtros = "";
        List<Long> idsUnidades = Lists.newArrayList();
        if (!listaUnidades.isEmpty()) {
            String unidades = "";
            for (HierarquiaOrganizacional hierarquia : listaUnidades) {
                idsUnidades.add(hierarquia.getSubordinada().getId());
                unidades += " " + hierarquia.getCodigo().substring(3, 10) + " -";
            }
            filtros += " Unidade(s): " + unidades;
            parametros.add(new ParametrosRelatorios(" vw.subordinada_id", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
        } else if (unidadeGestora == null) {
            List<HierarquiaOrganizacional> unidadesDoUsuario = relatorioContabilSuperFacade.getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorNivel("", getSistemaFacade().getUsuarioCorrente(), buscarExercicioPelaDataFinal(), dataFinal, TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            for (HierarquiaOrganizacional hierarquia : unidadesDoUsuario) {
                idsUnidades.add(hierarquia.getSubordinada().getId());
            }
            if (!idsUnidades.isEmpty()) {
                parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
            }
        }
        adicionarFontes(parametros, " fr.codigo ", ":codigosFontes", 1);
        adicionarContasDestinacoes(parametros, " cd.codigo ", ":codigosContasDestinacoes", 1);
        if (unidadeGestora != null) {
            parametros.add(new ParametrosRelatorios(" ug.id", ":ugId", null, OperacaoRelatorio.IGUAL, unidadeGestora.getId(), null, 1, false));
            filtros += " Unidade Gestora: " + unidadeGestora.getDescricao() + " - " + unidadeGestora.getCodigo() + " -";
        }
        filtros = !filtros.trim().isEmpty() ? filtros.substring(0, filtros.length() - 1) : "";
        SimpleDateFormat formatoAno = new SimpleDateFormat("yyyy");
        Exercicio exercicio = getExercicioFacade().recuperarExercicioPeloAno(new Integer(formatoAno.format(dataInicial)));
        parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, exercicio.getId(), null, 0, false));
        parametros.add(new ParametrosRelatorios(null, ":DATAINICIAL", null, null, DataUtil.getDataFormatada(dataInicial), null, 0, false));
        parametros.add(new ParametrosRelatorios(null, ":DATAFINAL", null, null, DataUtil.getDataFormatada(dataFinal), null, 0, false));
        parametros.add(new ParametrosRelatorios(null, ":DTINICIAL_ATEMES", null, null, "01/01/" + exercicio.getAno(), null, 0, false));
        parametros.add(new ParametrosRelatorios(null, ":DTFINAL_ATEMES", null, null, DataUtil.getDataFormatada(dataFinal), null, 0, false));
        return parametros;
    }

    @Override
    public String getNomeRelatorio() {
        return "DEMONSTRATIVO-RECEITA-POR-NATUREZA";
    }

    public String getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(String mesFinal) {
        this.mesFinal = mesFinal;
    }

    public Boolean getExibirContaDeDestinacao() {
        return exibirContaDeDestinacao;
    }

    public void setExibirContaDeDestinacao(Boolean exibirContaDeDestinacao) {
        this.exibirContaDeDestinacao = exibirContaDeDestinacao;
    }

    public FiltrosRelatorioDemonstrativoReceitaPorNatureza getFiltroRelatorio() {
        return filtroRelatorio;
    }

    public void setFiltroRelatorio(FiltrosRelatorioDemonstrativoReceitaPorNatureza filtroRelatorio) {
        this.filtroRelatorio = filtroRelatorio;
    }

    public Boolean getExibirCodigoCo() {
        return exibirCodigoCo == null ? Boolean.FALSE : exibirCodigoCo;
    }

    public void setExibirCodigoCo(Boolean exibirCodigoCo) {
        this.exibirCodigoCo = exibirCodigoCo;
    }
}
