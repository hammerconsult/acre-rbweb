package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.ContaDeDestinacao;
import br.com.webpublico.entidades.FonteDeRecursos;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.EspecieDemonstrativo;
import br.com.webpublico.enums.OperacaoReceita;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.List;

@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "demonstrativo-receita-por-natureza-e-tipo", pattern = "/relatorio/demonstrativo-receita-por-natureza-e-tipo/", viewId = "/faces/financeiro/relatorio/relatoriodemonstreceitanaturezaetipo.xhtml")
})
@ManagedBean
public class RelatorioDemonstrativoReceitaPorNaturezaTipoControlador extends RelatorioContabilSuperControlador {

    private Boolean exibirContaDeDestinacao;
    private EspecieDemonstrativo especieDemonstrativo;
    private ContaDeDestinacao contaDeDestinacao;
    private OperacaoReceita operacaoReceita;

    @URLAction(mappingId = "demonstrativo-receita-por-natureza-e-tipo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparCamposGeral();
        dataInicial = DataUtil.getPrimeiroDiaAno(relatorioContabilSuperFacade.getSistemaFacade().getExercicioCorrente().getAno());
        exibirContaDeDestinacao = Boolean.FALSE;
        operacaoReceita = null;
    }

    public List<SelectItem> getEspeciesDemonstrativo() {
        return Util.getListSelectItemSemCampoVazio(EspecieDemonstrativo.values(), false);
    }

    public List<SelectItem> getOperacoesReceita() {
        return Util.getListSelectItem(OperacaoReceita.values(), false);
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarDatasSemVerificarExercicioLogado();
            validarFiltros();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setNomeParametroBrasao("IMAGEM");
            dto.setNomeRelatorio("DEMONSTRATIVO-RECEITA-POR-NATUREZA-E-TIPO");
            dto.setNomeParametroSubreportDir("SUBREPORT_DIR");
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
            dto.adicionarParametro("ANO_EXERCICIO", buscarExercicioPelaDataFinal().getAno().toString());
            dto.adicionarParametro("MODULO", "Contábil");
            dto.adicionarParametro("FONTES", exibirContaDeDestinacao);
            dto.adicionarParametro("FILTRO_RELATORIO", DataUtil.getDataFormatada(dataInicial) + " até " + DataUtil.getDataFormatada(dataFinal));
            dto.adicionarParametro("USER", relatorioContabilSuperFacade.getSistemaFacade().getUsuarioCorrente().getNome());
            dto.adicionarParametro("APRESENTACAO", apresentacao.name());
            dto.adicionarParametro("FILTRO", filtros);
            dto.adicionarParametro("ESPECIE", especieDemonstrativo.name());

            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/demonstrativo-receita-natureza-e-tipo/");
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

    private void validarFiltros() {
        ValidacaoException ve = new ValidacaoException();
        if (especieDemonstrativo.isARealizar() && !"01/01".equals(DataUtil.getDataFormatada(dataInicial, "dd/MM"))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data inicial deve ser igual a 01/01 quando a espécie for 'A Realizar'.");
        }
        ve.lancarException();
    }

    public void limparFonteAndUnidade() {
        unidadeGestora = null;
        listaUnidades = Lists.newArrayList();
        fonteDeRecursos = null;
        contaDeDestinacao = null;
    }

    public List<Conta> completarContasDeDestinacao(String filtro) {
        return relatorioContabilSuperFacade.getContaFacade().buscarContasDeDestinacaoDeRecursos(filtro, buscarExercicioPelaDataFinal());
    }

    public List<FonteDeRecursos> completarFontesDeRecurso(String parte) {
        return relatorioContabilSuperFacade.getFonteDeRecursosFacade().listaFiltrandoPorExercicio(parte.trim(), buscarExercicioPelaDataFinal());
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        filtros = " Espécie do Demonstrativo: " + especieDemonstrativo.getDescricao() + " -";

        if (operacaoReceita != null) {
            parametros.add(new ParametrosRelatorios(" RECLOA.operacaoreceita ", ":operacaoReceita", null, OperacaoRelatorio.IGUAL, operacaoReceita.name(), null, 2, false));
            parametros.add(new ParametrosRelatorios(" ALT.operacaoreceita ", ":operacaoReceita", null, OperacaoRelatorio.IGUAL, operacaoReceita.name(), null, 3, false));
            parametros.add(new ParametrosRelatorios(" lanc.operacaoreceitarealizada ", ":operacaoReceita", null, OperacaoRelatorio.IGUAL, operacaoReceita.name(), null, 4, false));
            filtros += " Operação: " + operacaoReceita.getDescricao() + " -";
        }

        if (fonteDeRecursos != null) {
            parametros.add(new ParametrosRelatorios(" fr.id ", ":fonteId", null, OperacaoRelatorio.IGUAL, fonteDeRecursos.getId(), null, 1, false));
            filtros += " Fonte de Recurso: " + fonteDeRecursos.getCodigo() + " -";
        }

        if (contaDeDestinacao != null) {
            parametros.add(new ParametrosRelatorios(" cd.id ", ":contaDestinacaoId", null, OperacaoRelatorio.IGUAL, contaDeDestinacao.getId(), null, 1, false));
            filtros += " Conta de Destinação de Recurso: " + contaDeDestinacao.getCodigo() + " -";
        }


        parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, buscarExercicioPelaDataFinal().getId(), null, 0, false));
        if (especieDemonstrativo.isARealizar() || especieDemonstrativo.isRealizada()) {
            parametros.add(new ParametrosRelatorios(null, ":DATAINICIAL", null, null, DataUtil.getDataFormatada(dataInicial), null, 0, false));
        }
        parametros.add(new ParametrosRelatorios(null, ":DATAFINAL", null, null, DataUtil.getDataFormatada(dataFinal), null, 0, false));

        List<Long> idsUnidades = Lists.newArrayList();
        if (!listaUnidades.isEmpty()) {
            String unidades = "";
            for (HierarquiaOrganizacional hierarquia : listaUnidades) {
                idsUnidades.add(hierarquia.getSubordinada().getId());
                unidades += " " + hierarquia.getCodigo().substring(3, 10) + " -";
            }
            filtros += " Unidade(s): " + unidades;
            parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
        } else if (this.unidadeGestora == null) {
            List<HierarquiaOrganizacional> unidadesDoUsuario = relatorioContabilSuperFacade.getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorNivel("", relatorioContabilSuperFacade.getSistemaFacade().getUsuarioCorrente(), buscarExercicioPelaDataFinal(), dataFinal, TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            for (HierarquiaOrganizacional hierarquia : unidadesDoUsuario) {
                idsUnidades.add(hierarquia.getSubordinada().getId());
            }
            if (!idsUnidades.isEmpty()) {
                parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
            }
        } else {
            parametros.add(new ParametrosRelatorios(" ug.codigo ", ":ugCodigo", null, OperacaoRelatorio.IGUAL, unidadeGestora.getCodigo(), null, 1, false));
            filtros += " Unidade Gestora: " + unidadeGestora.getDescricao() + " - " + unidadeGestora.getCodigo() + " -";
        }

        atualizaFiltrosGerais();
        return parametros;
    }

    @Override
    public String getNomeRelatorio() {
        return "DEMONSTRATIVO-RECEITA-POR-NATUREZA-E-TIPO-" + DataUtil.getDataFormatada(dataInicial, "ddMMyyyy") + "A" + DataUtil.getDataFormatada(dataFinal, "ddMMyyyy");
    }

    public Boolean getExibirContaDeDestinacao() {
        return exibirContaDeDestinacao;
    }

    public void setExibirContaDeDestinacao(Boolean exibirContaDeDestinacao) {
        this.exibirContaDeDestinacao = exibirContaDeDestinacao;
    }

    public EspecieDemonstrativo getEspecieDemonstrativo() {
        return especieDemonstrativo;
    }

    public void setEspecieDemonstrativo(EspecieDemonstrativo especieDemonstrativo) {
        this.especieDemonstrativo = especieDemonstrativo;
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public OperacaoReceita getOperacaoReceita() {
        return operacaoReceita;
    }

    public void setOperacaoReceita(OperacaoReceita operacaoReceita) {
        this.operacaoReceita = operacaoReceita;
    }
}
