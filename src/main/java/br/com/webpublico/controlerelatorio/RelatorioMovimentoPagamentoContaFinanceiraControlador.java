package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.SituacaoMovimentoPagamento;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoMovimentoPagamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.relatoriofacade.RelatorioContabilSuperFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Criado por Mateus
 * Data: 11/04/2017.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "movimentos-pagamento-conta-financeira", pattern = "/relatorio/movimentos-pagamentos-por-conta-financeira/", viewId = "/faces/financeiro/relatorio/relatorio-movimento-pagamentos-conta-financeira.xhtml")}
)
public class RelatorioMovimentoPagamentoContaFinanceiraControlador extends RelatorioContabilSuperControlador {

    @EJB
    private RelatorioContabilSuperFacade relatorioFacade;
    private SituacaoMovimentoPagamento[] situacoes;
    private TipoMovimentoPagamento[] tiposMovimento;
    private String numeroMovimento;
    private String ordemBancaria;

    @URLAction(mappingId = "movimentos-pagamento-conta-financeira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        super.limparCamposGeral();
        situacoes = null;
        tiposMovimento = null;
        numero = null;
        ordemBancaria = null;
    }

    public List<ContaBancariaEntidade> completarContaBancariaEntidade(String parte) {
        try {
            return relatorioFacade.getContaBancariaEntidadeFacade().listaFiltrandoPorUnidadeDoUsuario(parte.trim(),
                getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente(),
                getSistemaControlador().getExercicioCorrente(),
                getSistemaControlador().getUsuarioCorrente(),
                getSistemaControlador().getDataOperacao());
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public List<SubConta> completarContasFinanceiras(String parte) {
        try {
            return relatorioFacade.getContaBancariaEntidadeFacade().getSubContaFacade().listaPorContaBancariaEntidadeDoUsuarioLogadoOuTodas(parte.trim(),
                contaBancariaEntidade,
                getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente(),
                getSistemaControlador().getExercicioCorrente(),
                getSistemaControlador().getUsuarioCorrente(),
                getSistemaControlador().getDataOperacao());
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public List<FonteDeRecursos> completarFontesDeRecursos(String parte) {
        return relatorioFacade.getContaBancariaEntidadeFacade().getFonteDeRecursosFacade().listaFiltrandoPorContaFinanceiraExercicio(parte.trim(),
            contaFinanceira,
            getSistemaControlador().getExercicioCorrente());
    }

    public List<ContaDeDestinacao> completarContasDeDestinacao(String parte) {
        return relatorioFacade.getContaFacade().buscarContasDeDestinacaoPorContaFinanceirAndExercicio(parte.trim(),
            contaFinanceira,
            getSistemaControlador().getExercicioCorrente());
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarDatas();
            validarContaBancaria();
            validarHierarquiaContaFinanceira();
            filtros = "";
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaControlador().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("EXERCICIO", getSistemaControlador().getExercicioCorrente().getId());
            dto.adicionarParametro("ORGAO", getHierarquiaPelaContaFinanceira().getSuperior().getDescricao());
            dto.adicionarParametro("UNIDADE", getHierarquiaPelaContaFinanceira().getSubordinada().getDescricao());
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametrosEFiltros()));
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("situacoes", SituacaoMovimentoPagamento.situacoesMovimentosPagamentosToDto(Arrays.asList(situacoes)));
            dto.adicionarParametro("tiposMovimentos", TipoMovimentoPagamento.tiposMovimentosToDto(Arrays.asList(tiposMovimento)));
            dto.adicionarParametro("FILTRO", filtros);
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/movimentos-pagamento-conta-financeira/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório: " + ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void validarHierarquiaContaFinanceira() {
        ValidacaoException ve = new ValidacaoException();
        if (getHierarquiaPelaContaFinanceira() == null ) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido utilizar o(a) " + getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente().getDescricao() + " pois não é uma unidade orçamentária. ");
        }

        if (getHierarquiaPelaContaFinanceira().getSuperior() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi possível encontrar uma unidade superior para esta orçamentaria");
        }

        if (getHierarquiaPelaContaFinanceira().getSubordinada() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi possível encontrar uma unidade subordinada para esta orçamentaria");
        }
        ve.lancarException();
    }

    private void validarContaBancaria() {
        ValidacaoException ve = new ValidacaoException();
        if (contaBancariaEntidade == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Conta Bancária deve ser informado.");
        }
        ve.lancarException();
    }

    private List<ParametrosRelatorios> montarParametrosEFiltros() {
        filtros += " Conta Bancária: " + contaBancariaEntidade.getNumeroConta() + "-" + contaBancariaEntidade.getDigitoVerificador() + " -";
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        parametros.add(new ParametrosRelatorios(null, ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.MENOR_IGUAL, DataUtil.getDataFormatada(dataInicial), DataUtil.getDataFormatada(dataFinal), 0, true));
        parametros.add(new ParametrosRelatorios(null, ":CBE_ID", null, OperacaoRelatorio.IGUAL, contaBancariaEntidade.getId(), null, 0, false));
        adicionarExercicio(parametros);
        if (contaFinanceira != null) {
            parametros.add(new ParametrosRelatorios(" SUB.ID ", ":SUB_ID", null, OperacaoRelatorio.IGUAL, contaFinanceira.getId(), null, 1, false));
            filtros += " Conta Financeira: " + contaFinanceira.getCodigo() + " -";
        }
        if (fonteDeRecursos != null) {
            parametros.add(new ParametrosRelatorios(" FONT.ID ", ":FONT_ID", null, OperacaoRelatorio.IGUAL, fonteDeRecursos.getId(), null, 1, false));
            filtros += " Fonte de Recursos: " + fonteDeRecursos.getCodigo() + " -";
        }
        if (conta != null) {
            parametros.add(new ParametrosRelatorios(" cdt.ID ", ":cdt_ID", null, OperacaoRelatorio.IGUAL, conta.getId(), null, 1, false));
            filtros += " Conta de Destinação de Recurso: " + conta.getCodigo() + " -";
        }
        if (ordemBancaria != null && !ordemBancaria.isEmpty()) {
            parametros.add(new ParametrosRelatorios(" bord.sequenciaArquivo ", ":ordemBancaria", null, OperacaoRelatorio.LIKE, ordemBancaria, null, 1, false));
            filtros += " Ordem Bancária: " + ordemBancaria + " -";
        }
        if (pessoa != null) {
            parametros.add(new ParametrosRelatorios(" p.id ", ":pessoa", null, OperacaoRelatorio.IGUAL, pessoa.getId(), null, 1, false));
            filtros += " Pessoa: " + pessoa.getNome() + " -";
        }
        if (numeroMovimento != null && !numeroMovimento.isEmpty()) {
            parametros.add(new ParametrosRelatorios(" numero ", ":numero", null, OperacaoRelatorio.IGUAL, numeroMovimento, null, 2, false));
            filtros += " Número: " + numeroMovimento + " -";
        }
        if (situacoes != null && situacoes.length > 0) {
            filtros += " Situações: ";
            for (SituacaoMovimentoPagamento situacao : situacoes) {
                filtros += " " + situacao.getDescricao() + ",";
            }
            atualizaFiltrosGerais();
            filtros += " -";
        }
        if (tiposMovimento != null && tiposMovimento.length > 0) {
            filtros += " Tipos de Movimento: ";
            for (TipoMovimentoPagamento tipoMovimento : tiposMovimento) {
                filtros += " " + tipoMovimento.getDescricao() + ",";
            }
            atualizaFiltrosGerais();
            filtros += " -";
        }
        filtrosParametrosUnidade(parametros);
        filtros = getFiltrosPeriodo() + filtros;
        filtros = atualizaFiltrosGerais();
        return parametros;
    }

    private HierarquiaOrganizacional getHierarquiaPelaContaFinanceira() {
        if (contaFinanceira != null) {
            contaFinanceira = relatorioFacade.getContaBancariaEntidadeFacade().getSubContaFacade().recuperar(contaFinanceira.getId());
            List<SubContaUniOrg> unidadesOrganizacionais = contaFinanceira.getUnidadesOrganizacionais();
            for (SubContaUniOrg unidadesOrganizacional : unidadesOrganizacionais) {
                if (unidadesOrganizacional.getExercicio().equals(getSistemaControlador().getExercicioCorrente())) {
                    return relatorioFacade.getContaBancariaEntidadeFacade().getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(dataInicial, unidadesOrganizacional.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
                }
            }
        }
        return relatorioFacade.getContaBancariaEntidadeFacade().getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(dataInicial, getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
    }

    public List<SituacaoMovimentoPagamento> getTodasSituacoes() {
        return Arrays.asList(SituacaoMovimentoPagamento.values());
    }

    public List<TipoMovimentoPagamento> getTiposMovimentos() {
        return Arrays.asList(TipoMovimentoPagamento.values());
    }

    public void atribuirNullContaBancariaEntidade() {
        contaBancariaEntidade = null;
    }

    public void atribuirNullContaFinanceira() {
        contaFinanceira = null;
        fonteDeRecursos = null;
    }

    public void recuperarContaBancariaApartirDaContaFinanceira() {
        fonteDeRecursos = null;
        contaBancariaEntidade = contaFinanceira.getContaBancariaEntidade();
    }

    @Override
    public String getNomeRelatorio() {
        return "MOVIMENTO-PAGAMENTOS-POR-CONTA-FINANCEIRA";
    }

    public String getNumeroMovimento() {
        return numeroMovimento;
    }

    public void setNumeroMovimento(String numeroMovimento) {
        this.numeroMovimento = numeroMovimento;
    }

    public String getOrdemBancaria() {
        return ordemBancaria;
    }

    public void setOrdemBancaria(String ordemBancaria) {
        this.ordemBancaria = ordemBancaria;
    }

    public SituacaoMovimentoPagamento[] getSituacoes() {
        return situacoes;
    }

    public void setSituacoes(SituacaoMovimentoPagamento[] situacoes) {
        this.situacoes = situacoes;
    }

    public TipoMovimentoPagamento[] getTiposMovimento() {
        return tiposMovimento;
    }

    public void setTiposMovimento(TipoMovimentoPagamento[] tiposMovimento) {
        this.tiposMovimento = tiposMovimento;
    }
}
