/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.comum.relatorio.ConfiguracaoDeRelatorio;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.comum.ConfiguracaoDeRelatorioFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-movimentacao-financeira-por-conta-bancaria", pattern = "/relatorios/extrato-movimentacao-financeira-por-conta-bancaria/", viewId = "/faces/financeiro/relatorio/relatorioextratomovfinanceiracbe.xhtml")
})

@ManagedBean
public class RelatorioExtratoMovFinanceiraCbeControlador implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private LoteBaixaFacade loteFacade;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private ConfiguracaoDeRelatorioFacade configuracaoDeRelatorioFacade;
    private ContaBancariaEntidade contaBancariaEntidade;
    private ConverterAutoComplete converterLote;
    private SubConta subConta;
    private FonteDeRecursos fonteDeRecursos;
    private List<Exercicio> exerciciosAnteriores;
    private Date dataInicial;
    private Date dataFinal;
    private Boolean mostrarSaldoDiario;
    private Boolean dataSaldoAnterior;
    private Boolean diferenteZero;
    private String filtros;
    private List<HierarquiaOrganizacional> hierarquiasOrganizacionais;
    private UnidadeGestora unidadeGestora;
    private LoteBaixa lote;
    private ContaDeDestinacao contaDeDestinacao;

    public RelatorioExtratoMovFinanceiraCbeControlador() {
    }

    public ConverterAutoComplete getConverterLote() {
        if (converterLote == null) {
            converterLote = new ConverterAutoComplete(LoteBaixa.class, loteFacade);
        }
        return converterLote;
    }

    @URLAction(mappingId = "relatorio-movimentacao-financeira-por-conta-bancaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        dataInicial = sistemaFacade.getDataOperacao();
        dataFinal = sistemaFacade.getDataOperacao();
        contaBancariaEntidade = null;
        subConta = null;
        fonteDeRecursos = null;
        unidadeGestora = null;
        mostrarSaldoDiario = Boolean.FALSE;
        dataSaldoAnterior = false;
        hierarquiasOrganizacionais = Lists.newArrayList();
        exerciciosAnteriores = Lists.newArrayList();
        lote = null;
        diferenteZero = false;
    }

    public void atualizarCampos() {
        if (exerciciosAnteriores.size() > 1) {
            if (!exerciciosAnteriores.get(exerciciosAnteriores.size() - 2).equals(getExercicioDaDataInicial())) {
                contaBancariaEntidade = null;
                subConta = null;
                fonteDeRecursos = null;
                mostrarSaldoDiario = Boolean.FALSE;
                dataSaldoAnterior = false;
                hierarquiasOrganizacionais = Lists.newArrayList();
                unidadeGestora = null;
            }
        }
    }

    private String getNomeRelatorio() {
        return "EXTRATO-MOVIMENTACAO-FINANCEIRA-POR-CONTA-BANCARIA " + DataUtil.getDataFormatada(dataInicial, "ddMMyyyy") + "-" + DataUtil.getDataFormatada(dataFinal, "ddMMyyyy");
    }

    public void gerarRelatorio() {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            definirParametros(dto);
            dto.setApi("contabil/extrato-movimentacao-financeira-conta-bancaria/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void definirParametros(RelatorioDTO dto) {
        dto.adicionarParametro("USER", sistemaFacade.getUsuarioCorrente().getNome(), false);
        dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO");
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("TOTAL_DIARIO", mostrarSaldoDiario);
        dto.adicionarParametro("MODULO", "Financeiro");
        dto.adicionarParametro("TOTAL_DIARIO_ANTERIOR", dataSaldoAnterior);
        dto.adicionarParametro("TOTAL_MENSAL", "01".equals(DataUtil.getDataFormatada(dataInicial).substring(0, 2)) && DataUtil.ultimoDiaDoMes(Integer.parseInt(DataUtil.getDataFormatada(dataFinal).substring(3, 5))) == Integer.parseInt(DataUtil.getDataFormatada(dataFinal).substring(0, 2)));
        dto.adicionarParametro("dataSaldoAnterior", DataUtil.getDataFormatada(dataInicial).startsWith("01/01") ? " where trunc(datasaldo) <= TO_DATE('" + DataUtil.getDataFormatada(dataInicial) + "', 'dd/MM/yyyy') " : " where trunc(datasaldo) < TO_DATE('" + DataUtil.getDataFormatada(dataInicial) + "', 'dd/MM/yyyy') ");
        dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
        dto.adicionarParametro("FILTROS", filtros);
        dto.setNomeRelatorio(getNomeRelatorio());
    }

    public StreamedContent fileDownload() {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            definirParametros(dto);
            dto.setApi("contabil/extrato-movimentacao-financeira-conta-bancaria/excel/");
            ResponseEntity<byte[]> responseEntity = retornarByte(dto);
            byte[] bytes = responseEntity.getBody();
            InputStream stream = new ByteArrayInputStream(bytes);
            return new DefaultStreamedContent(stream, "application/xls", getNomeRelatorio() + ".xls");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
        return null;
    }

    private ResponseEntity<byte[]> retornarByte(RelatorioDTO dto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RelatorioDTO> request = new HttpEntity<>(dto, headers);
        ConfiguracaoDeRelatorio configuracao = configuracaoDeRelatorioFacade.getConfiguracaoPorChave();
        return new RestTemplate().exchange(configuracao.getUrl() + dto.getApi() + "gerar", HttpMethod.POST, request, byte[].class);
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        parametros.add(new ParametrosRelatorios(null, ":DATA_INICIAL", null, OperacaoRelatorio.IGUAL, DataUtil.getDataFormatada(dataInicial), null, 5, true));
        parametros.add(new ParametrosRelatorios(null, ":DATA_FINAL", null, OperacaoRelatorio.IGUAL, DataUtil.getDataFormatada(dataFinal), null, 6, true));
        parametros.add(new ParametrosRelatorios(null, ":CBE_ID", null, OperacaoRelatorio.IGUAL, contaBancariaEntidade.getId(), null, 9, false));
        parametros.add(new ParametrosRelatorios(null, ":EXERCICIO_ID", null, OperacaoRelatorio.IGUAL, getExercicioDaDataInicial().getId(), null, 0, false));

        filtros = " Período: " + DataUtil.getDataFormatada(dataInicial) + " a " + DataUtil.getDataFormatada(dataFinal) + " -";
        filtros += " Conta Bancária: " + contaBancariaEntidade.getNumeroConta() + "-" + contaBancariaEntidade.getDigitoVerificador() + " -";

        if (subConta != null) {
            parametros.add(new ParametrosRelatorios(" SUB.ID ", ":SUB_ID", null, OperacaoRelatorio.IGUAL, subConta.getId(), null, 1, false));
            filtros += " Conta Financeira: " + subConta.getCodigo() + " -";
        }

        if (fonteDeRecursos != null) {
            parametros.add(new ParametrosRelatorios(" FONT.ID ", ":FONT_ID", null, OperacaoRelatorio.IGUAL, fonteDeRecursos.getId(), null, 1, false));
            filtros += " Fonte de Recursos: " + fonteDeRecursos.getCodigo() + " -";
        }

        if (contaDeDestinacao != null) {
            parametros.add(new ParametrosRelatorios(" contad.ID ", ":idContaDeDestinacao", null, OperacaoRelatorio.IGUAL, contaDeDestinacao.getId(), null, 1, false));
            filtros += " Conta de Destinação de Recurso: " + contaDeDestinacao.getCodigo() + " -";
        }

        List<Long> idsUnidades = Lists.newArrayList();
        if (!hierarquiasOrganizacionais.isEmpty()) {
            String unidades = "";
            for (HierarquiaOrganizacional hierarquia : hierarquiasOrganizacionais) {
                idsUnidades.add(hierarquia.getSubordinada().getId());
                unidades += " " + hierarquia.getCodigo().substring(3, 10) + " -";
            }
            parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
            filtros += " Unidade(s): " + unidades;

        } else if (unidadeGestora == null) {
            List<HierarquiaOrganizacional> unidadesDoUsuario = hierarquiaOrganizacionalFacade.listaHierarquiaUsuarioCorrentePorNivel("", sistemaFacade.getUsuarioCorrente(), getExercicioDaDataInicial(), sistemaFacade.getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            for (HierarquiaOrganizacional hierarquia : unidadesDoUsuario) {
                idsUnidades.add(hierarquia.getSubordinada().getId());
            }
            if (!idsUnidades.isEmpty()) {
                parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
            }
        } else {
            parametros.add(new ParametrosRelatorios(" ug.id ", ":ugCodigo", null, OperacaoRelatorio.IGUAL, unidadeGestora.getId(), null, 1, false));
            filtros += " Unidade Gestora: " + unidadeGestora.getCodigo() + " -";
        }
        if (lote != null) {
            parametros.add(new ParametrosRelatorios(" rr.lote ", ":lote", null, OperacaoRelatorio.IGUAL, this.lote.getCodigoLote(), null, 2, false));
        }
        if (!diferenteZero) {
            parametros.add(new ParametrosRelatorios(" ssb.totaldebito - ssb.totalcredito ", ":soma", null, OperacaoRelatorio.DIFERENTE, "0", null, 3, false));
            parametros.add(new ParametrosRelatorios(" DEBITO - CREDITO ", ":soma", null, OperacaoRelatorio.DIFERENTE, "0", null, 4, false));
        }
        filtros = filtros.substring(0, filtros.length() - 1);
        return parametros;
    }

    public Exercicio getExercicioDaDataInicial() {
        return exercicioFacade.getExercicioPorAno(DataUtil.getAno(dataInicial));
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (dataInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Inicial deve ser informado.");
        }
        if (dataFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Data Final deve ser informado.");
        }
        if (contaBancariaEntidade == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Conta Bancária deve ser informado.");
        }
        ve.lancarException();
        if (dataFinal.compareTo(dataInicial) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Informe um Intervalo de datas válido.");
        }
        SimpleDateFormat formato = new SimpleDateFormat("yyyy");
        if (formato.format(dataInicial).compareTo(formato.format(dataFinal)) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("As datas estão com exercícios diferentes.");
        }
        ve.lancarException();
    }

    public List<LoteBaixa> completarLotes(String parte) {
        return loteFacade.listaFiltrando(parte.trim(), "codigoLote");
    }

    public List<FonteDeRecursos> completarFontesDeRecursos(String parte) {
        return fonteDeRecursosFacade.listaFiltrandoPorContaFinanceiraExercicio(parte.trim(), subConta, getExercicioDaDataInicial());
    }

    public void atualizarContaBancaria() {
        if (subConta != null) {
            contaBancariaEntidade = subConta.getContaBancariaEntidade();
            FacesUtil.executaJavaScript("setaFoco('Formulario:fonte_input')");
        }
    }

    public List<ContaDeDestinacao> completarContasDeDestinacao(String parte) {
        if (subConta != null) {
            return contaFacade.buscarContasDeDestinacaoPorCodigoOrDescricaoESubConta(parte.trim(), sistemaFacade.getExercicioCorrente(), subConta.getId());
        }
        return contaFacade.buscarContasDeDestinacaoPorCodigoOrDescricao(parte.trim(), sistemaFacade.getExercicioCorrente());
    }

    public ContaBancariaEntidade getContaBancariaEntidade() {
        return contaBancariaEntidade;
    }

    public void setContaBancariaEntidade(ContaBancariaEntidade contaBancariaEntidade) {
        this.contaBancariaEntidade = contaBancariaEntidade;
    }

    public SubConta getSubConta() {
        return subConta;
    }

    public void setSubConta(SubConta subConta) {
        this.subConta = subConta;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public void limparContaFonte() {
        this.subConta = null;
        this.fonteDeRecursos = null;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
        exerciciosAnteriores.add(getExercicioDaDataInicial());
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Boolean getMostrarSaldoDiario() {
        return mostrarSaldoDiario;
    }

    public void setMostrarSaldoDiario(Boolean mostrarSaldoDiario) {
        this.mostrarSaldoDiario = mostrarSaldoDiario;
    }

    public List<HierarquiaOrganizacional> getHierarquiasOrganizacionais() {
        return hierarquiasOrganizacionais;
    }

    public void setHierarquiasOrganizacionais(List<HierarquiaOrganizacional> hierarquiasOrganizacionais) {
        this.hierarquiasOrganizacionais = hierarquiasOrganizacionais;
    }

    public String getFiltros() {
        return filtros;
    }

    public void setFiltros(String filtros) {
        this.filtros = filtros;
    }

    public UnidadeGestora getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(UnidadeGestora unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public Boolean getDiferenteZero() {
        return diferenteZero;
    }

    public void setDiferenteZero(Boolean diferenteZero) {
        this.diferenteZero = diferenteZero;
    }

    public LoteBaixa getLote() {
        return lote;
    }

    public void setLote(LoteBaixa lote) {
        this.lote = lote;
    }

    public Boolean getDataSaldoAnterior() {
        return dataSaldoAnterior;
    }

    public void setDataSaldoAnterior(Boolean dataSaldoAnterior) {
        this.dataSaldoAnterior = dataSaldoAnterior;
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }
}
