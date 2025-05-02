/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-movimentacao-financeira", pattern = "/relatorios/extrato-movimentacao-financeira/", viewId = "/faces/financeiro/relatorio/relatorioextratomovfinanceira.xhtml")
})

@ManagedBean
public class RelatorioExtratoMovFinanceiraControlador {

    @EJB
    private ContaBancariaEntidadeFacade contaBancariaEntidadeFacade;
    @EJB
    private LoteBaixaFacade loteFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private ConfiguracaoDeRelatorioFacade configuracaoDeRelatorioFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    private ContaBancariaEntidade contaBancariaEntidade;
    private SubConta subConta;
    private FonteDeRecursos fonteDeRecursos;
    private Date dataInicial;
    private Date dataFinal;
    private Boolean data;
    private Boolean dataSaldoAnterior;
    private Boolean diferenteZero;
    private String filtros;
    private List<HierarquiaOrganizacional> listaUnidades;
    private UnidadeGestora unidadeGestora;
    private LoteBaixa lote;
    private ContaDeDestinacao contaDeDestinacao;
    private Boolean exibirContaDeDestinacao;

    public RelatorioExtratoMovFinanceiraControlador() {
    }

    @URLAction(mappingId = "relatorio-movimentacao-financeira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        dataInicial = sistemaFacade.getDataOperacao();
        dataFinal = sistemaFacade.getDataOperacao();
        contaBancariaEntidade = null;
        subConta = null;
        fonteDeRecursos = null;
        data = null;
        listaUnidades = new ArrayList<>();
        dataSaldoAnterior = false;
        lote = null;
        diferenteZero = false;
        exibirContaDeDestinacao = Boolean.TRUE;
    }

    public void geraRelatorio() {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            montarDtoSemApi(dto);
            dto.setApi("contabil/extrato-movimentacao-financeira/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public StreamedContent gerarExcel() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            montarDtoSemApi(dto);
            dto.setApi("contabil/extrato-movimentacao-financeira/excel/");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<RelatorioDTO> request = new HttpEntity<>(dto, headers);
            ConfiguracaoDeRelatorio configuracao = configuracaoDeRelatorioFacade.getConfiguracaoPorChave();
            ResponseEntity<byte[]> responseEntity = new RestTemplate().exchange(configuracao.getUrl() + dto.getApi() + "gerar", HttpMethod.POST, request, byte[].class);
            byte[] bytes = responseEntity.getBody();
            InputStream stream = new ByteArrayInputStream(bytes);
            return new DefaultStreamedContent(stream, "application/xls", "Extrato de Movimentação Financeira" + ".xls");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
        return null;
    }

    private void montarDtoSemApi(RelatorioDTO dto) {
        dto.adicionarParametro("USER", sistemaFacade.getUsuarioCorrente().getNome(), false);
        dto.setNomeParametroBrasao("BRASAO");
        dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
        dto.adicionarParametro("ORGAO", getOrgao());
        dto.adicionarParametro("UNIDADE", getUnidade());
        dto.adicionarParametro("TOTAL_DIARIO", data);
        dto.adicionarParametro("TOTAL_DIARIO_ANTERIOR", dataSaldoAnterior);
        dto.adicionarParametro("EXIBIR_CONTA_DEST", exibirContaDeDestinacao);
        //Verifica se o dia inicial é dia 01, e se o dia final é final de mês, caso sim para ambos, irá exibir o Total Mensal.
        dto.adicionarParametro("TOTAL_MENSAL", DataUtil.getDataFormatada(dataInicial).substring(0, 2).equals("01") && DataUtil.ultimoDiaDoMes(Integer.parseInt(DataUtil.getDataFormatada(dataFinal).substring(3, 5))) == Integer.parseInt(DataUtil.getDataFormatada(dataFinal).substring(0, 2)));
        dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametrosEFiltros()));
        dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
        dto.adicionarParametro("dataSaldoAnterior", DataUtil.getDataFormatada(dataInicial).startsWith("01/01") ? " where trunc(datasaldo) <= TO_DATE('" + DataUtil.getDataFormatada(dataInicial) + "', 'dd/MM/yyyy') " : " where trunc(datasaldo) < TO_DATE('" + DataUtil.getDataFormatada(dataInicial) + "', 'dd/MM/yyyy') ");
        dto.adicionarParametro("FILTROS", filtros);
        dto.setNomeRelatorio(getNomeRelatorio());
    }

    private String getNomeRelatorio() {
        return "EXTRATO-MOVIMENTACAO-FINANCEIRA " + DataUtil.getDataFormatada(dataInicial, "ddMMyyyy") + "-" + DataUtil.getDataFormatada(dataFinal, "ddMMyyyy");
    }

    private String getUnidade() {
        if (subConta != null) {
            subConta = contaBancariaEntidadeFacade.getSubContaFacade().recuperar(subConta.getId());
            List<SubContaUniOrg> unidadesOrganizacionais = subConta.getUnidadesOrganizacionais();
            for (SubContaUniOrg unidadesOrganizacional : unidadesOrganizacionais) {
                if (unidadesOrganizacional.getExercicio().equals(getExercicioDaDataInicial())) {
                    return contaBancariaEntidadeFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(dataFinal, unidadesOrganizacional.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ORCAMENTARIA).getSubordinada().getDescricao();
                }
            }
        }
        return contaBancariaEntidadeFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(dataFinal, sistemaFacade.getUnidadeOrganizacionalOrcamentoCorrente(), TipoHierarquiaOrganizacional.ORCAMENTARIA).getSubordinada().getDescricao();
    }

    private String getOrgao() {
        if (subConta != null) {
            subConta = contaBancariaEntidadeFacade.getSubContaFacade().recuperar(subConta.getId());
            List<SubContaUniOrg> unidadesOrganizacionais = subConta.getUnidadesOrganizacionais();
            for (SubContaUniOrg unidadesOrganizacional : unidadesOrganizacionais) {
                if (unidadesOrganizacional.getExercicio().equals(getExercicioDaDataInicial())) {
                    return contaBancariaEntidadeFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(dataFinal, unidadesOrganizacional.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ORCAMENTARIA).getSuperior().getDescricao();
                }
            }
        }
        return contaBancariaEntidadeFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(dataFinal, sistemaFacade.getUnidadeOrganizacionalOrcamentoCorrente(), TipoHierarquiaOrganizacional.ORCAMENTARIA).getSuperior().getDescricao();
    }

    private List<ParametrosRelatorios> montarParametrosEFiltros() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        parametros.add(new ParametrosRelatorios(null, ":DATA_INICIAL", null, OperacaoRelatorio.IGUAL, DataUtil.getDataFormatada(dataInicial), null, 5, true));
        parametros.add(new ParametrosRelatorios(null, ":DATA_FINAL", null, OperacaoRelatorio.IGUAL, DataUtil.getDataFormatada(dataFinal), null, 6, true));
        parametros.add(new ParametrosRelatorios(null, ":CBE_ID", null, OperacaoRelatorio.IGUAL, contaBancariaEntidade.getId(), null, 0, false));
        parametros.add(new ParametrosRelatorios(null, ":EXERCICIO_ID", null, OperacaoRelatorio.IGUAL, getExercicioDaDataInicial().getId(), null, 0, false));

        filtros = " Período: " + DataUtil.getDataFormatada(dataInicial) + " a " + DataUtil.getDataFormatada(dataFinal) + " -";
        filtros += " Conta Bancária: " + contaBancariaEntidade.getNumeroConta() + " - " + contaBancariaEntidade.getDigitoVerificador() + " -";

        if (this.subConta != null) {
            parametros.add(new ParametrosRelatorios(" SUB.ID ", ":SUB_ID", null, OperacaoRelatorio.IGUAL, subConta.getId(), null, 1, false));
            filtros += " Conta Financeira: " + subConta.getCodigo() + " -";
        }

        if (this.fonteDeRecursos != null) {
            parametros.add(new ParametrosRelatorios(" FONT.ID ", ":FONT_ID", null, OperacaoRelatorio.IGUAL, fonteDeRecursos.getId(), null, 1, false));
            filtros += " Fonte de Recursos: " + fonteDeRecursos.getCodigo() + " -";
        }

        if (contaDeDestinacao != null) {
            parametros.add(new ParametrosRelatorios(" cd.id ", ":idContaDeDestinacao", null, OperacaoRelatorio.IGUAL, contaDeDestinacao.getId(), null, 1, false));
            filtros += " Conta de Destinação de Recurso: " + contaDeDestinacao.getCodigo() + " -";
        }

        List<Long> listaIdsUnidades = new ArrayList<>();
        if (this.listaUnidades.size() > 0) {
            String unidades = "";
            for (HierarquiaOrganizacional lista : listaUnidades) {
                listaIdsUnidades.add(lista.getSubordinada().getId());
                unidades += " " + lista.getCodigo().substring(3, 10) + " -";
            }
            parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, listaIdsUnidades, null, 1, false));
            filtros += " Unidade(s): " + unidades;

        } else if (this.unidadeGestora == null) {
            List<HierarquiaOrganizacional> listaUndsUsuarios = new ArrayList<>();
            listaUndsUsuarios = contaBancariaEntidadeFacade.getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorNivel("", sistemaFacade.getUsuarioCorrente(), sistemaFacade.getExercicioCorrente(), sistemaFacade.getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            for (HierarquiaOrganizacional lista : listaUndsUsuarios) {
                listaIdsUnidades.add(lista.getSubordinada().getId());
            }
            if (listaIdsUnidades.size() != 0) {
                parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, listaIdsUnidades, null, 1, false));
            }
        } else {
            parametros.add(new ParametrosRelatorios(" ug.id ", ":ugId", null, OperacaoRelatorio.IGUAL, this.unidadeGestora.getId(), null, 1, false));
            filtros += " Unidade Gestora: " + unidadeGestora.getCodigo() + " -";
        }
        if (lote != null) {
            parametros.add(new ParametrosRelatorios(" rr.lote ", ":lote", null, OperacaoRelatorio.IGUAL, this.lote.getCodigoLote(), null, 2, false));
        }
        if (!diferenteZero) {
            parametros.add(new ParametrosRelatorios(" soma ", ":soma", null, OperacaoRelatorio.DIFERENTE, "0", null, 3, false));
            parametros.add(new ParametrosRelatorios(" DEBITO - CREDITO ", ":soma", null, OperacaoRelatorio.DIFERENTE, "0", null, 4, false));
        }
        filtros = filtros.substring(0, filtros.length() - 1);
        return parametros;
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

    public Exercicio getExercicioDaDataInicial() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        return exercicioFacade.getExercicioPorAno(Integer.valueOf(format.format(dataInicial)));
    }

    public List<ContaDeDestinacao> completarContasdeDestinacao(String parte) {
        if (subConta != null) {
            return contaFacade.buscarContasDeDestinacaoPorCodigoOrDescricaoESubConta(parte.trim(), sistemaFacade.getExercicioCorrente(), subConta.getId());
        }
        return contaFacade.buscarContasDeDestinacaoPorCodigoOrDescricao(parte.trim(), sistemaFacade.getExercicioCorrente());
    }

    public List<LoteBaixa> completarLotes(String parte) {
        try {
            return loteFacade.listaFiltrando(parte.trim(), "codigoLote");
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public List<FonteDeRecursos> completarFontesDeRecursos(String parte) {
        if (subConta != null) {
            return contaBancariaEntidadeFacade.getFonteDeRecursosFacade().listaFiltrandoPorContaFinanceiraExercicio(parte.trim(), subConta, getExercicioDaDataInicial());
        }
        return Lists.newArrayList();
    }

    public void recuperaContaBancariaApartirDaContaFinanceira() {
        fonteDeRecursos = null;
        buscarContaBancariaPelaSubConta();
    }

    public void buscarContaBancariaPelaSubConta() {
        try {
            contaBancariaEntidade = subConta.getContaBancariaEntidade();
            FacesUtil.executaJavaScript("setaFoco('Formulario:fonte_input')");
        } catch (Exception e) {
        }
    }

    public void setaIdNullContaBancaria() {
        contaBancariaEntidade = null;
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
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Boolean getData() {
        return data;
    }

    public void setData(Boolean data) {
        this.data = data;
    }

    public List<HierarquiaOrganizacional> getListaUnidades() {
        return listaUnidades;
    }

    public void setListaUnidades(List<HierarquiaOrganizacional> listaUnidades) {
        this.listaUnidades = listaUnidades;
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

    public Boolean getExibirContaDeDestinacao() {
        return exibirContaDeDestinacao;
    }

    public void setExibirContaDeDestinacao(Boolean exibirContaDeDestinacao) {
        this.exibirContaDeDestinacao = exibirContaDeDestinacao;
    }
}
