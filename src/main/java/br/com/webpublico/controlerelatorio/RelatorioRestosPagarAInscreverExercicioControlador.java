/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.ContaDeDestinacao;
import br.com.webpublico.entidades.FonteDeRecursos;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoExibicao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
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
 * @author Edi
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-resto-pagar-a-inscrever", pattern = "/relatorio/restos-a-pagar-a-inscrever-exercicio/", viewId = "/faces/financeiro/relatorio/relatorio-resto-pagar-a-inscrever-exercicio.xhtml"),
})

@ManagedBean
public class RelatorioRestosPagarAInscreverExercicioControlador extends RelatorioRestoPagarSuperControlador implements Serializable {

    private TipoExibicao tipoExibicao;

    @URLAction(mappingId = "relatorio-resto-pagar-a-inscrever", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparCamposGeral();
        FacesUtil.executaJavaScript("setaFoco('Formulario:dataInicial_input')");
        tipoExibicao = TipoExibicao.CONTA_DE_DESTINACAO;
    }

    public List<Conta> completarContasDeDestinacao(String parte) {
        return facade.getContaFacade().buscarContasDeDestinacaoDeRecursos(parte.trim(), buscarExercicioPelaDataFinal());
    }

    public List<FonteDeRecursos> completarFontesDeRecursos(String parte) {
        return facade.getFonteDeRecursosFacade().listaFiltrandoPorExercicio(parte, buscarExercicioPelaDataFinal());
    }

    public List<SelectItem> getTiposDeExibicao() {
        return Util.getListSelectItemSemCampoVazio(TipoExibicao.values(), false);
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            dataReferencia = dataFinal;
            validarDataIniciaAndFinal(true);
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaControlador().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("FILTRO_DATAREFERENCIA", "Referente a " + getDataReferenciaFormatada());
            if (unidadeGestora != null) {
                dto.adicionarParametro("FILTRO_UG", unidadeGestora.getCodigo() + " - " + unidadeGestora.getDescricao());
            }
            dto.adicionarParametro("FILTRO_PERIODO", getFiltroPeriodo());
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("FILTRO_GERAL", filtros.trim());
            dto.adicionarParametro("TIPOEXIBICAO", tipoExibicao.getToDto());
            dto.adicionarParametro("DESCRICAO_FONTE", tipoExibicao.getDescricao());
            dto.adicionarParametro("ABREVIACAO_FONTE", TipoExibicao.FONTE_DE_RECURSO.equals(tipoExibicao) ? "Fonte" : "CDR");
            dto.setNomeRelatorio(getNomeArquivo());
            dto.setApi("contabil/resto-pagar-a-inscrever/");
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

    public String getCaminhoRelatorio() {
        return "/relatorio/restos-a-pagar-a-inscrever-exercicio/";
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        filtros = "";
        filtrosParametrosDatas(parametros);
        filtrosParametrosUnidade(parametros);
        filtrosParametrosGerais(parametros);
        return parametros;
    }

    public List<ParametrosRelatorios> filtrosParametrosGerais(List<ParametrosRelatorios> parametros) {
        if (tipoRestosProcessado != null) {
            parametros.add(new ParametrosRelatorios(" EMP.TIPORESTOSPROCESSADOS", ":tipoResto", null, OperacaoRelatorio.IGUAL, tipoRestosProcessado.name(), null, 1, false));
            filtros += " Tipo de Resto: " + tipoRestosProcessado.getDescricao() + " -";
        }
        if (programaPPA != null) {
            parametros.add(new ParametrosRelatorios(" PROG.ID", ":idPrograma", null, OperacaoRelatorio.IGUAL, programaPPA.getId(), null, 1, false));
            filtros += " Programa: " + programaPPA + " -";
        }
        if (projetoAtividade != null) {
            parametros.add(new ParametrosRelatorios(" PROJETOATIVIDADE.ID", ":idProjetoAtividade", null, OperacaoRelatorio.IGUAL, projetoAtividade.getId(), null, 1, false));
            filtros += " Projeto/Atividade: " + projetoAtividade + " -";
        }
        if (funcao != null) {
            parametros.add(new ParametrosRelatorios(" FUNCAO.ID", ":idFuncao", null, OperacaoRelatorio.IGUAL, funcao.getId(), null, 1, false));
            filtros += " Função: " + funcao + " -";
        }
        if (subFuncao != null) {
            parametros.add(new ParametrosRelatorios(" SUBFUNCAO.ID", ":idSubFuncao", null, OperacaoRelatorio.IGUAL, subFuncao.getId(), null, 1, false));
            filtros += " SubFunção: " + subFuncao + " -";
        }
        if (contaDespesa != null) {
            parametros.add(new ParametrosRelatorios(" C.CODIGO", ":codigoContaDesp", null, OperacaoRelatorio.LIKE, contaDespesa.getCodigoSemZerosAoFinal() + "%", null, 1, false));
            filtros += " Conta de Despesa: " + contaDespesa.getCodigo() + " -";
        }
        if (fonteDeRecursos != null) {
            parametros.add(new ParametrosRelatorios(" font.codigo ", ":fonteCodigo", null, OperacaoRelatorio.LIKE, this.fonteDeRecursos.getCodigo(), null, 1, false));
            filtros += " Fonte de Recurso: " + fonteDeRecursos.getCodigo() + " -";
        }
        if (contaDestinacao != null) {
            parametros.add(new ParametrosRelatorios(" cdest.ID ", ":cdestId", null, OperacaoRelatorio.IGUAL, contaDestinacao.getId(), null, 1, false));
            filtros += " Conta de Destinação de Recurso: " + contaDestinacao.getCodigo() + " -";
        }
        if (pessoa != null) {
            parametros.add(new ParametrosRelatorios(" EMP.FORNECEDOR_ID", ":idPessoa", null, OperacaoRelatorio.IGUAL, pessoa.getId(), null, 1, false));
            filtros += " Pessoa: " + pessoa.getNome() + " -";
        }
        if (classeCredor != null) {
            parametros.add(new ParametrosRelatorios(" EMP.CLASSECREDOR_ID ", ":idClasse", null, OperacaoRelatorio.IGUAL, classeCredor.getId(), null, 1, false));
            filtros += " Classe Credor: " + classeCredor.getCodigo() + " -";
        }
        filtros = atualizaFiltrosGerais();
        return parametros;
    }

    private void filtrosParametrosDatas(List<ParametrosRelatorios> parametros) {
        parametros.add(new ParametrosRelatorios(" trunc(EMP.DATAEMPENHO)", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, getDataInicialFormatada(), getDataFinalFormatada(), 2, true));
        parametros.add(new ParametrosRelatorios(" trunc(LIQ.DATALIQUIDACAO)", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, getDataInicialFormatada(), getDataFinalFormatada(), 4, true));
        parametros.add(new ParametrosRelatorios(" trunc(PAG.DATAPAGAMENTO)", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, getDataInicialFormatada(), getDataFinalFormatada(), 5, true));
        parametros.add(new ParametrosRelatorios(" trunc(EST.DATAESTORNO)", ":DATAINICIAL", ":DATAREFERENCIA", OperacaoRelatorio.BETWEEN, getDataInicialFormatada(), getDataReferenciaFormatada(), 3, true));
        parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, getIdExercicioCorrente(), null, 0, false));
    }

    @Override
    public String getNumeroRelatorio() {
        return "";
    }

    @Override
    public String getNomeArquivo() {
        return "RESTOS-A-PAGAR-INSCREVER-NO-EXERCICIO-PERIODO" + getDataPorPeriodoParaImprimir();
    }

    public TipoExibicao getTipoExibicao() {
        return tipoExibicao;
    }

    public void setTipoExibicao(TipoExibicao tipoExibicao) {
        this.tipoExibicao = tipoExibicao;
    }
}
