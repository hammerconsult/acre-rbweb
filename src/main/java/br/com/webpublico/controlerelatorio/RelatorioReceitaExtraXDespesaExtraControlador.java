package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoContaExtraorcamentaria;
import br.com.webpublico.enums.TipoExibicao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
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
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mateus on 14/08/2015.
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-receita-extra-x-despesa-extra", pattern = "/relatorio/receita-extra-x-despesa-extra/", viewId = "/faces/financeiro/relatorio/relatorioreceitaextraxdespesaextra.xhtml")
})
@ManagedBean
public class RelatorioReceitaExtraXDespesaExtraControlador extends RelatorioContabilSuperControlador implements Serializable {

    private ReceitaExtra receitaExtra;
    private TipoContaExtraorcamentaria tipoContaExtraorcamentaria;
    private Conta contaDeDestinacao;
    private TipoExibicao tipoExibicao;

    @URLAction(mappingId = "relatorio-receita-extra-x-despesa-extra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparCamposGeral();
        receitaExtra = null;
        tipoContaExtraorcamentaria = null;
        tipoExibicao = TipoExibicao.CONTA_DE_DESTINACAO;
    }


    public List<SelectItem> getTiposDeExibicao() {
        return Util.getListSelectItemSemCampoVazio(TipoExibicao.values(), false);
    }

    public List<SelectItem> tiposContasExtras() {
        return Util.getListSelectItem(TipoContaExtraorcamentaria.values());
    }

    public List<Conta> buscarContas(String parte) {
        return relatorioContabilSuperFacade.getContaFacade().listaFiltrandoExtraorcamentario(parte.trim(), buscarExercicioPelaDataFinal());
    }

    @Override
    public String getNomeRelatorio() {
        return "RECEITA-EXTRA-X-DESPESA-EXTRA";
    }

    public List<Pessoa> buscarFornecedor(String parte) {
        return relatorioContabilSuperFacade.getPessoaFacade().listaTodasPessoasPorId(parte.trim());
    }

    public List<FonteDeRecursos> buscarFonteDeRecursos(String parte) {
        return relatorioContabilSuperFacade.getFonteDeRecursosFacade().listaFiltrandoPorExercicio(parte, buscarExercicioPelaDataFinal());
    }

    public List<Conta> completarContasDeDestinacao(String parte) {
        return relatorioContabilSuperFacade.getContaFacade().buscarContasDeDestinacaoDeRecursos(parte.trim(), buscarExercicioPelaDataFinal());
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarDatas();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaControlador().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("EXERCICIO", getSistemaControlador().getExercicioCorrente().getId());
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("FILTRO", getFiltrosAtualizados());
            dto.adicionarParametro("TIPOEXIBICAO", tipoExibicao.getToDto());
            dto.adicionarParametro("DESCRICAO_FONTE", tipoExibicao.getDescricao());
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/receita-extra-x-despesa-extra/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
            filtros = "";
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String getFiltrosAtualizados() {
        filtros = getFiltrosPeriodo() + filtros;
        filtros = filtros.length() == 0 ? " " : filtros.substring(0, filtros.length() - 1);
        return filtros;
    }

    @Override
    public void validarDatas() {
        ValidacaoException ve = new ValidacaoException();
        verificarCampoNull(ve, dataInicial, "O campo Data Inicial deve ser informado.");
        verificarCampoNull(ve, dataFinal, "O campo Data Final deve ser informado.");
        verificarCampoNull(ve, dataReferencia, "O campo Data de Referência deve ser informado.");
        ve.lancarException();
        if (dataFinal.before(dataInicial)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Data Final deve ser maior ou igual a Data Inicial.");
        }
        if (dataReferencia.before(dataFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Data de Referência deve ser maior ou igual a Data Final.");
        }
        ve.lancarException();
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");

        filtros = " Data de Referência: " + DataUtil.getDataFormatada(dataReferencia) + " -";

        parametros.add(new ParametrosRelatorios(null, ":DataInicioExercicio", null, OperacaoRelatorio.BETWEEN, "01/01/" + buscarExercicioPelaDataFinal().getAno(), null, 4, true));
        parametros.add(new ParametrosRelatorios(null, ":DataInicial", ":DataFinal", OperacaoRelatorio.BETWEEN, formato.format(dataInicial), formato.format(dataFinal), 2, true));
        parametros.add(new ParametrosRelatorios(null, ":DataInicial", ":DataReferencia", OperacaoRelatorio.BETWEEN, formato.format(dataInicial), formato.format(dataReferencia), 3, true));
        filtros += " Exibir: " + tipoExibicao.getDescricao() + " -";
        if (fonteDeRecursos != null) {
            parametros.add(new ParametrosRelatorios(" fr.codigo ", ":fonteCodigo", null, OperacaoRelatorio.LIKE, this.fonteDeRecursos.getCodigo(), null, 1, false));
            filtros += " Fonte de Recurso: " + fonteDeRecursos.getCodigo() + " -";
        }
        if (contaDeDestinacao != null) {
            parametros.add(new ParametrosRelatorios(" cdest.ID ", ":cdestId", null, OperacaoRelatorio.IGUAL, contaDeDestinacao.getId(), null, 1, false));
            filtros += " Conta de Destinação de Recurso: " + contaDeDestinacao.getCodigo() + " -";
        }
        if (pessoa != null) {
            parametros.add(new ParametrosRelatorios(" p.id ", ":fornecedorId", null, OperacaoRelatorio.IGUAL, this.pessoa.getId(), null, 1, false));
            filtros += " Pessoa: " + pessoa.getCpf_Cnpj() + " - " + pessoa.getNome() + " -";
        }

        if (conta != null) {
            parametros.add(new ParametrosRelatorios(" c.codigo ", ":cCodigo", null, OperacaoRelatorio.IGUAL, conta.getCodigo(), null, 1, false));
            filtros += " Conta Extraorçamentária: " + conta.getCodigo() + " -";
        }

        if (receitaExtra != null) {
            parametros.add(new ParametrosRelatorios(" rec.id ", ":recId", null, OperacaoRelatorio.LIKE, receitaExtra.getId(), null, 5, false));
            filtros += " Receita Extra: " + receitaExtra.getNumero() + " -";
        }

        if (tipoContaExtraorcamentaria != null) {
            parametros.add(new ParametrosRelatorios(" ce.tipocontaextraorcamentaria ", ":tipoConta", null, OperacaoRelatorio.LIKE, tipoContaExtraorcamentaria.name(), null, 1, false));
            filtros += " Tipo de Conta Extraorçamentária: " + tipoContaExtraorcamentaria.getDescricao() + " -";
        }

        parametros = filtrosParametrosUnidade(parametros);
        filtros = filtros.substring(0, filtros.length() - 1);
        return parametros;
    }

    @Override
    public void adicionarExercicio(List<ParametrosRelatorios> parametros) {
        parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, buscarExercicioPelaDataFinal().getId(), null, 0, false));
    }

    public Conta getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(Conta contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public List<ReceitaExtra> completarReceitas(String filtro) {
        return relatorioContabilSuperFacade.getReceitaExtraFacade().buscarReceitasExtrasNoExercicio(filtro, buscarExercicioPelaDataFinal());
    }

    public ReceitaExtra getReceitaExtra() {
        return receitaExtra;
    }

    public void setReceitaExtra(ReceitaExtra receitaExtra) {
        this.receitaExtra = receitaExtra;
    }

    public TipoContaExtraorcamentaria getTipoContaExtraorcamentaria() {
        return tipoContaExtraorcamentaria;
    }

    public void setTipoContaExtraorcamentaria(TipoContaExtraorcamentaria tipoContaExtraorcamentaria) {
        this.tipoContaExtraorcamentaria = tipoContaExtraorcamentaria;
    }

    public TipoExibicao getTipoExibicao() {
        return tipoExibicao;
    }

    public void setTipoExibicao(TipoExibicao tipoExibicao) {
        this.tipoExibicao = tipoExibicao;
    }
}
