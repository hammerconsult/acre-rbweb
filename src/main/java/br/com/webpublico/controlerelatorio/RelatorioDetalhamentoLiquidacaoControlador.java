package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.ApresentacaoRelatorio;
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
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: usuario
 * Date: 13/05/14
 * Time: 13:43
 * To change this template use File | Settings | File Templates.
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-detalhamento-liquidacao", pattern = "/relatorio/detalhamento-liquidacao", viewId = "/faces/financeiro/relatorio/relatoriodetalhamentoliquidacao.xhtml")
})
@ManagedBean
public class RelatorioDetalhamentoLiquidacaoControlador extends RelatorioContabilSuperControlador implements Serializable {

    private Conta contaInicial;
    private Conta contaFinal;

    public RelatorioDetalhamentoLiquidacaoControlador() {
    }

    public List<SelectItem> getApresentacoes() {
        return Util.getListSelectItemSemCampoVazio(ApresentacaoRelatorio.values(), false);
    }

    public List<Conta> completarContas(String parte) {
        return relatorioContabilSuperFacade.getContaFacade().listaFiltrandoContaDespesa(parte, getSistemaFacade().getExercicioCorrente());
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarDatasSemVerificarExercicioLogado();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setApi("contabil/detalhamento-liquidacao/");
            dto.setNomeRelatorio("Relatório de Detalhamento de Liquidação por Elemento de Despesa");
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC ");
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("APRESENTACAO", apresentacao.name());
            dto.adicionarParametro("apresentacao", apresentacao.getToDto());
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getUsername(), Boolean.FALSE);
            dto.adicionarParametro("FILTRO", filtros);
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório: ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        parametros.add(new ParametrosRelatorios(" trunc(liq.dataliquidacao) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(dataInicial), DataUtil.getDataFormatada(dataFinal), 2, true));
        parametros.add(new ParametrosRelatorios(" trunc(EST.DATAESTORNO) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(dataInicial), DataUtil.getDataFormatada(dataFinal), 3, true));
        parametros.add(new ParametrosRelatorios(null, ":EXERCICIO_ID", null, null, getSistemaFacade().getExercicioCorrente().getId(), null, 0, false));

        filtros = " Período: " + DataUtil.getDataFormatada(dataInicial) + " a " + DataUtil.getDataFormatada(dataFinal) + " -";
        List<Long> idsUnidades = Lists.newArrayList();
        if (!listaUnidades.isEmpty()) {
            String unidades = "";
            for (HierarquiaOrganizacional hierarquiaOrganizacional : listaUnidades) {
                idsUnidades.add(hierarquiaOrganizacional.getSubordinada().getId());
                unidades += " " + hierarquiaOrganizacional.getCodigo().substring(3, 10) + " -";
            }
            parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
            filtros += " Unidade(s): " + unidades;
        } else if (this.unidadeGestora == null) {
            List<HierarquiaOrganizacional> unidadesDoUsuario = relatorioContabilSuperFacade.getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorNivel("", getSistemaFacade().getUsuarioCorrente(), getSistemaFacade().getExercicioCorrente(), getSistemaFacade().getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            for (HierarquiaOrganizacional hierarquiaOrganizacional : unidadesDoUsuario) {
                idsUnidades.add(hierarquiaOrganizacional.getSubordinada().getId());
            }
            if (!idsUnidades.isEmpty()) {
                parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
            }
        } else {
            parametros.add(new ParametrosRelatorios(" ug.id ", ":ugId", null, OperacaoRelatorio.IGUAL, this.unidadeGestora.getId(), null, 1, false));
            filtros += " Unidade Gestora: " + unidadeGestora.getCodigo() + " -";
        }

        if (contaInicial != null && contaFinal != null) {
            parametros.add(new ParametrosRelatorios(" c.codigo ", ":codigoInicial", ":codigoFinal", OperacaoRelatorio.BETWEEN, contaInicial.getCodigo(), contaFinal.getCodigo(), 1, false));
            filtros += " Conta de Despesa: " + contaInicial.getCodigo() + " a " + contaFinal.getCodigo() + " -";
        }

        if (fonteDeRecursos != null) {
            parametros.add(new ParametrosRelatorios(" fonte.codigo ", ":codigoFonte", null, OperacaoRelatorio.IGUAL, fonteDeRecursos.getCodigo(), null, 1, false));
            filtros += " Fonte de Recursos: " + fonteDeRecursos.getCodigo() + " -";
        }
        atualizaFiltrosGerais();
        return parametros;
    }

    @URLAction(mappingId = "relatorio-detalhamento-liquidacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        super.limparCamposGeral();
    }

    public Conta getContaInicial() {
        return contaInicial;
    }

    public void setContaInicial(Conta contaInicial) {
        this.contaInicial = contaInicial;
    }

    public Conta getContaFinal() {
        return contaFinal;
    }

    public void setContaFinal(Conta contaFinal) {
        this.contaFinal = contaFinal;
    }

    @Override
    public String getNomeRelatorio() {
        return null;
    }
}
