package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

/**
 * @author juggernaut
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-contabil-credito-receber", pattern = "/relatorio/contabil/creditos-a-receber/", viewId = "/faces/financeiro/relatorio/relatoriocreditosreceber.xhtml"),
})

@ManagedBean
public class RelatorioCreditosReceberControlador extends RelatorioContabilSuperControlador implements Serializable {

    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ClasseCredorFacade classeCredorFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private TiposCredito tiposCredito;
    private OperacaoCreditoReceber operacaoCreditoReceber;
    private String numeroInicial;
    private String numeroFinal;
    private Conta contaReceita;
    private ContaDeDestinacao contaDeDestinacao;

    public List<SelectItem> getApresentacoes() {
        return Util.getListSelectItemSemCampoVazio(ApresentacaoRelatorio.values(), false);
    }

    public List<SelectItem> getTiposLancamentos() {
        return Util.getListSelectItem(TipoLancamento.values());
    }

    @URLAction(mappingId = "relatorio-contabil-credito-receber", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparCamposGeral();
        tiposCredito = null;
        operacaoCreditoReceber = null;
        numeroInicial = null;
        numeroFinal = null;
        contaReceita = null;
        contaDeDestinacao = null;
    }

    public void gerarRelatorioCreditosReceber() {
        try {
            validarDatasSemVerificarExercicioLogado();
            RelatorioDTO dto = montarRelatorioDto();
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

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarDatasSemVerificarExercicioLogado();
            RelatorioDTO dto = montarRelatorioDto();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
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

    private RelatorioDTO montarRelatorioDto() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
        dto.adicionarParametro("APRESENTACAO", apresentacao.name());
        dto.adicionarParametro("MODULO", "Contábil");
        dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
        dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametrosEFiltros()));
        dto.adicionarParametro("FILTRO", filtros);
        dto.setNomeRelatorio(getNomeRelatorio());
        dto.setApi("contabil/credito-receber/");
        return dto;
    }

    @Override
    public String getNomeRelatorio() {
        return "RELATORIO-CREDITOS-RECEBER " + DataUtil.getDataFormatada(dataInicial, "MM/yyyy" + "-" + DataUtil.getDataFormatada(dataFinal, "MM/yyyy"));
    }


    public List<SelectItem> getTiposDeCredito() {
        return Util.getListSelectItem(TiposCredito.values());
    }

    public List<SelectItem> getOperacoesCreditoReceber() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, " "));
        for (OperacaoCreditoReceber ocr : OperacaoCreditoReceber.values()) {
            if (!OperacaoCreditoReceber.RECEBIMENTO.equals(ocr)) {
                toReturn.add(new SelectItem(ocr, ocr.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<ClasseCredor> completarClassesCredores(String parte) {
        return classeCredorFacade.listaFiltrando(parte, "descricao");
    }

    public List<EventoContabil> completarEventosContabeis(String parte) {
        return eventoContabilFacade.listaFiltrando(parte, "descricao");
    }

    public List<Conta> completarContasDeReceita(String parte) {
        return contaFacade.listaFiltrandoReceita(parte, getSistemaFacade().getExercicioCorrente());
    }

    public List<ContaDeDestinacao> completarContasDeDestinacao(String parte) {
        return contaFacade.buscarContasDeDestinacaoPorCodigoOrDescricao(parte, getSistemaFacade().getExercicioCorrente());
    }

    public List<Pessoa> completarPessoas(String parte) {
        return pessoaFacade.listaTodasPessoasPorId(parte);
    }

    private List<ParametrosRelatorios> montarParametrosEFiltros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        parametros.add(new ParametrosRelatorios(" trunc(CR.DATACREDITO) ", ":DataInicial", ":DataFinal", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(dataInicial), DataUtil.getDataFormatada(dataFinal), 1, true));
        filtros = " Período: " + DataUtil.getDataFormatada(dataInicial) + " a " + DataUtil.getDataFormatada(dataFinal) + " -";

        if (!listaUnidades.isEmpty()) {
            List<Long> idsUnidades = Lists.newArrayList();
            StringBuilder unidades = new StringBuilder();
            for (HierarquiaOrganizacional hierarquia : listaUnidades) {
                idsUnidades.add(hierarquia.getSubordinada().getId());
                unidades.append(" ").append(hierarquia.getCodigo().substring(3, 10)).append(" -");
            }
            filtros += " Unidade(s): " + unidades.toString();
            parametros.add(new ParametrosRelatorios(" vw.subordinada_id", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
        } else if (unidadeGestora == null) {
            List<Long> idsUnidades = Lists.newArrayList();
            List<HierarquiaOrganizacional> hierarquiasDoUsuario = hierarquiaOrganizacionalFacade.listaHierarquiaUsuarioCorrentePorNivel("", getSistemaFacade().getUsuarioCorrente(), getSistemaFacade().getExercicioCorrente(), getSistemaFacade().getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            for (HierarquiaOrganizacional hierarquia : hierarquiasDoUsuario) {
                idsUnidades.add(hierarquia.getSubordinada().getId());
            }
            if (!idsUnidades.isEmpty()) {
                parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
            }
        }

        if (unidadeGestora != null) {
            parametros.add(new ParametrosRelatorios(" ug.id ", ":ugId", null, OperacaoRelatorio.IGUAL, this.unidadeGestora.getId(), null, 1, false));
            filtros += " Unidade Gestora: " + unidadeGestora.getCodigo() + " -";
        }

        if (unidadeGestora != null || ApresentacaoRelatorio.UNIDADE_GESTORA.equals(apresentacao)) {
            parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, getSistemaFacade().getExercicioCorrente().getId(), null, 0, false));
        }

        if (!Strings.isNullOrEmpty(numeroInicial) && !Strings.isNullOrEmpty(numeroFinal)) {
            parametros.add(new ParametrosRelatorios(" CR.NUMERO ", ":numInicial", ":numFinal", OperacaoRelatorio.BETWEEN, numeroInicial, numeroFinal, 1, false));
            filtros += " Número inicial: " + numeroInicial + " Número final: " + numeroFinal + " -";
        }

        if (contaReceita != null) {
            parametros.add(new ParametrosRelatorios(" c.codigo ", ":codigoConta", null, OperacaoRelatorio.IGUAL, contaReceita.getCodigo(), null, 1, false));
            filtros += " Conta de Receita: " + contaReceita.getCodigo() + " -";
        }

        if (contaDeDestinacao != null) {
            parametros.add(new ParametrosRelatorios(" cDest.codigo ", ":cDestinacao", null, OperacaoRelatorio.IGUAL, contaDeDestinacao.getCodigo(), null, 1, false));
            filtros += " Conta de Destinação de Recurso: " + contaDeDestinacao.getCodigo() + " -";
        }

        if (tiposCredito != null) {
            parametros.add(new ParametrosRelatorios(" CONTAREC.TIPOSCREDITO ", ":tipoCredito", null, OperacaoRelatorio.IGUAL, tiposCredito.name(), null, 1, false));
            filtros += " Tipo de Conta: " + tiposCredito.getDescricao() + " -";
        }

        if (tipoLancamento != null) {
            parametros.add(new ParametrosRelatorios(" CR.TIPOLANCAMENTO ", ":tipoLancamento", null, OperacaoRelatorio.IGUAL, tipoLancamento.name(), null, 1, false));
            filtros += " Tipo de Lançamento: " + tipoLancamento.getDescricao() + " -";
        }

        if (operacaoCreditoReceber != null) {
            parametros.add(new ParametrosRelatorios(" CR.OPERACAOCREDITORECEBER ", ":operacaoCredito", null, OperacaoRelatorio.IGUAL, operacaoCreditoReceber.name(), null, 1, false));
            filtros += " Operação: " + operacaoCreditoReceber.getDescricao() + " -";
        }

        if (eventoContabil != null) {
            parametros.add(new ParametrosRelatorios(" cr.EVENTOCONTABIL_ID ", ":eventoId", null, OperacaoRelatorio.IGUAL, eventoContabil.getId(), null, 1, false));
            filtros += " Evento Contabil: " + eventoContabil.getCodigo() + " - " + eventoContabil.getDescricao() + " -";
        }

        if (pessoa != null) {
            parametros.add(new ParametrosRelatorios(" CR.PESSOA_ID ", ":pessoaId", null, OperacaoRelatorio.IGUAL, pessoa.getId(), null, 1, false));
            filtros += " Pessoa: " + pessoa.getNome() + " -";
        }

        if (classeCredor != null) {
            parametros.add(new ParametrosRelatorios(" CR.classecredor_id ", ":classeCredorID", null, OperacaoRelatorio.IGUAL, classeCredor.getId(), null, 1, false));
            filtros += " Classe Credor: " + classeCredor.getDescricao() + " -";
        }
        atualizaFiltrosGerais();
        return parametros;
    }

    public String getNumeroInicial() {
        return numeroInicial;
    }

    public void setNumeroInicial(String numeroInicial) {
        this.numeroInicial = numeroInicial;
    }

    public OperacaoCreditoReceber getOperacaoCreditoReceber() {
        return operacaoCreditoReceber;
    }

    public void setOperacaoCreditoReceber(OperacaoCreditoReceber operacaoCreditoReceber) {
        this.operacaoCreditoReceber = operacaoCreditoReceber;
    }

    public TiposCredito getTiposCredito() {
        return tiposCredito;
    }

    public void setTiposCredito(TiposCredito tiposCredito) {
        this.tiposCredito = tiposCredito;
    }

    public Conta getContaReceita() {
        return contaReceita;
    }

    public void setContaReceita(Conta contaReceita) {
        this.contaReceita = contaReceita;
    }

    public String getNumeroFinal() {
        return numeroFinal;
    }

    public void setNumeroFinal(String numeroFinal) {
        this.numeroFinal = numeroFinal;
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }
}
