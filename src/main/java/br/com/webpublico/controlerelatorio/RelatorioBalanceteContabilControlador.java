/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.UnidadeGestora;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoAdministracao;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ContaFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import br.com.webpublico.webreportdto.dto.contabil.ApresentacaoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author wiplash
 */
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "relatorio-balancete-contabil", pattern = "/relatorio/balancete-contabil/", viewId = "/faces/financeiro/relatorio/relatoriobalancetecontabil.xhtml")
})
@ManagedBean
public class RelatorioBalanceteContabilControlador implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hoFacade;
    private Date dataInicial;
    private Date dataFinal;
    private ConverterAutoComplete converterConta;
    private List<HierarquiaOrganizacional> listaUnidades;
    private TipoAdministracao tipoAdministracao;
    private Conta conta;
    private String filtro;
    private ApresentacaoRelatorioDTO apresentacao;
    private UnidadeGestora unidadeGestora;
    private Boolean listarContaAuxiliar;

    @URLAction(mappingId = "relatorio-balancete-contabil", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        this.conta = null;
        this.tipoAdministracao = null;
        this.listaUnidades = new ArrayList<>();
        this.dataInicial = sistemaFacade.getDataOperacao();
        this.dataFinal = sistemaFacade.getDataOperacao();
        this.filtro = "";
        this.listarContaAuxiliar = false;
        this.apresentacao = ApresentacaoRelatorioDTO.CONSOLIDADO;
    }

    public List<SelectItem> getApresentacoes() {
        return Util.getListSelectItemSemCampoVazio(ApresentacaoRelatorioDTO.values(), false);
    }

    public RelatorioBalanceteContabilControlador() {
    }

    private void validarDatas() {
        ValidacaoException ve = new ValidacaoException();
        if (this.dataInicial == null || this.dataFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar um intervalo de datas.");
        }
        ve.lancarException();
        if (this.dataInicial.after(this.dataFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Data Inicial não pode ser maior que a Data Final.");
        }
        if (DataUtil.getAno(dataInicial).compareTo(DataUtil.getAno(dataFinal)) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("As datas devem estar no mesmo exercício.");
        }
        ve.lancarException();
    }

    public void gerarRelatorio() {
        try {
            validarDatas();
            RelatorioDTO dto = montarRelatorioDto();
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

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarDatas();
            RelatorioDTO dto = montarRelatorioDto();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
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

    private RelatorioDTO montarRelatorioDto() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.adicionarParametro("USER", sistemaFacade.getUsuarioCorrente().getNome(), false);
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
        dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
        dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
        dto.adicionarParametro("listarContaAuxiliar", listarContaAuxiliar);
        dto.adicionarParametro("DATAINICIAL", DataUtil.getDataFormatada(dataInicial));
        dto.adicionarParametro("DATAFINAL", DataUtil.getDataFormatada(dataFinal));
        dto.adicionarParametro("EXERCICIO_ID", sistemaFacade.getExercicioCorrente().getId());
        dto.adicionarParametro("APRESENTACAO", apresentacao.name());
        dto.adicionarParametro("FILTRO", filtro);
        dto.setNomeRelatorio("Balancete Contábil");
        dto.setApi("contabil/balancete-contabil/");
        return dto;
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        ParametrosRelatorios parametro = new ParametrosRelatorios(null, ":DATAINICIAL", null, OperacaoRelatorio.MENOR_IGUAL, DataUtil.getDataFormatada(dataInicial), null, 0, true);
        parametros.add(parametro);
        parametro = new ParametrosRelatorios(null, ":DATAFINAL", null, OperacaoRelatorio.MENOR_IGUAL, DataUtil.getDataFormatada(dataFinal), null, 0, true);
        parametros.add(parametro);
        filtro = " Período: " + DataUtil.getDataFormatada(dataInicial) + " a " + DataUtil.getDataFormatada(dataFinal) + " -";

        if (this.listaUnidades.size() > 0) {
            List<Long> listaIdsUnidades = new ArrayList<>();
            String unidades = "";
            for (HierarquiaOrganizacional lista : listaUnidades) {
                listaIdsUnidades.add(lista.getSubordinada().getId());
                unidades += " " + lista.getCodigo().substring(3, 10) + " -";
            }
            filtro += " Unidade(s): " + unidades;
            parametro = new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, listaIdsUnidades, null, 1, false);
            parametros.add(parametro);

        } else if (this.unidadeGestora == null) {
            List<HierarquiaOrganizacional> listaUndsUsuarios = new ArrayList<>();
            List<Long> listaIdsUnidades = new ArrayList<>();
            listaUndsUsuarios = hoFacade.listaHierarquiaUsuarioCorrentePorNivel("", sistemaFacade.getUsuarioCorrente(), sistemaFacade.getExercicioCorrente(), sistemaFacade.getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            for (HierarquiaOrganizacional lista : listaUndsUsuarios) {
                listaIdsUnidades.add(lista.getSubordinada().getId());
            }
            if (listaIdsUnidades.size() != 0) {
                parametro = new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, listaIdsUnidades, null, 1, false);
                parametros.add(parametro);
            }
        }

        if (tipoAdministracao != null) {
            parametro = new ParametrosRelatorios(" vw.TIPOADMINISTRACAO  ", ":tipoAdm", null, OperacaoRelatorio.IGUAL, tipoAdministracao.name(), null, 1, false);
            parametros.add(parametro);
            filtro += " Tipo Adminstração: " + tipoAdministracao.getDescricao() + " -";
        }
        if (conta != null) {
            parametro = new ParametrosRelatorios(" C.CODIGO  ", ":contaCod", null, OperacaoRelatorio.LIKE, conta.getCodigoSemZerosAoFinal() + "%", null, 1, false);
            parametros.add(parametro);
            filtro += " Conta Contábil: " + conta.toString() + " -";
        }
        if (unidadeGestora != null) {
            parametro = new ParametrosRelatorios(" ug.id ", ":ugId", null, OperacaoRelatorio.IGUAL, this.unidadeGestora.getId(), null, 1, false);
            parametros.add(parametro);
            filtro += " Unidade Gestora: " + unidadeGestora.getCodigo() + " -";
        }

        parametro = new ParametrosRelatorios(null, ":exercicio", null, null, sistemaFacade.getExercicioCorrente().getId(), null, 0, false);
        parametros.add(parametro);

        filtro = filtro.substring(0, filtro.length() - 1);
        return parametros;
    }

    public List<SelectItem> getTiposDeAdministracao() {
        return Util.getListSelectItem(TipoAdministracao.values(), false);
    }

    public List<Conta> completarContas(String parte) {
        return contaFacade.listaContasContabeis(parte.trim(), sistemaFacade.getExercicioCorrente());
    }

    public ConverterAutoComplete getConverterConta() {
        if (converterConta == null) {
            converterConta = new ConverterAutoComplete(Conta.class, contaFacade);
        }
        return converterConta;
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

    public List<HierarquiaOrganizacional> getListaUnidades() {
        return listaUnidades;
    }

    public void setListaUnidades(List<HierarquiaOrganizacional> listaUnidades) {
        this.listaUnidades = listaUnidades;
    }

    public TipoAdministracao getTipoAdministracao() {
        return tipoAdministracao;
    }

    public void setTipoAdministracao(TipoAdministracao tipoAdministracao) {
        this.tipoAdministracao = tipoAdministracao;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public ApresentacaoRelatorioDTO getApresentacao() {
        return apresentacao;
    }

    public void setApresentacao(ApresentacaoRelatorioDTO apresentacao) {
        this.apresentacao = apresentacao;
    }

    public UnidadeGestora getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(UnidadeGestora unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public Boolean getListarContaAuxiliar() {
        return listarContaAuxiliar;
    }

    public void setListarContaAuxiliar(Boolean listarContaAuxiliar) {
        this.listarContaAuxiliar = listarContaAuxiliar;
    }
}
