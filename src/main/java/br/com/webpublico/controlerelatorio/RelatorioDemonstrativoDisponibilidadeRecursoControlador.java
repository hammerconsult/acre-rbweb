package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.UnidadeGestora;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.relatoriofacade.RelatorioDemonstrativoDisponibilidadeRecursoFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
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
 * Created with IntelliJ IDEA.
 * User: usuario
 * Date: 15/07/14
 * Time: 16:05
 * To change this template use File | Settings | File Templates.
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "demonstrativo-disponibilidade-destinacao-recurso", pattern = "/relatorio/demonstrativo-disponibilidade-destinacao-recurso", viewId = "/faces/financeiro/relatorio/relatoriodemonstrativodisponibilidaderecurso.xhtml")
})
@ManagedBean(name = "relatorioDemonstrativoDisponibilidadeRecursoControlador")
public class RelatorioDemonstrativoDisponibilidadeRecursoControlador extends AbstractReport implements Serializable {

    @EJB
    private RelatorioDemonstrativoDisponibilidadeRecursoFacade relatorioFacade;
    private String mesFinal;
    private UnidadeGestora unidadeGestora;
    private List<HierarquiaOrganizacional> hierarquias;
    private Exercicio exercicio;
    private String filtro;
    private Mes mes;
    private ApresentacaoRelatorio apresentacaoRelatorio;
    private TipoExibicao tipoExibicao;

    public RelatorioDemonstrativoDisponibilidadeRecursoControlador() {
    }

    @URLAction(mappingId = "demonstrativo-disponibilidade-destinacao-recurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        mes = Mes.JANEIRO;
        filtro = "";
        hierarquias = Lists.newArrayList();
        exercicio = getSistemaFacade().getExercicioCorrente();
        apresentacaoRelatorio = ApresentacaoRelatorio.CONSOLIDADO;
        tipoExibicao = TipoExibicao.CONTA_DE_DESTINACAO;
    }


    public List<SelectItem> getMeses() {
        return Util.getListSelectItemSemCampoVazio(Mes.values(), false);
    }

    public List<SelectItem> getApresentacoes() {
        return Util.getListSelectItemSemCampoVazio(ApresentacaoRelatorio.values(), false);
    }

    public List<SelectItem> getTiposDeExibicao() {
        return Util.getListSelectItemSemCampoVazio(TipoExibicao.values(), false);
    }

    private String getNomeRelatorioDownload() {
        switch (apresentacaoRelatorio) {
            case UNIDADE_GESTORA:
                return "DDR-" + exercicio.getAno() + "-" + getNumeroMes() + "-UNG";
            case UNIDADE:
                return "DDR-" + exercicio.getAno() + "-" + getNumeroMes() + "-UND";
            case ORGAO:
                return "DDR-" + exercicio.getAno() + "-" + getNumeroMes() + "-ORG";
            default:
                return "DDR-" + exercicio.getAno() + "-" + getNumeroMes() + "-CSL";
        }
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.adicionarParametro("apresentacaoRelatorioDTO", apresentacaoRelatorio.getToDto());
            dto.adicionarParametro("MODULO", "Contábil");
            dto.adicionarParametro("TIPOEXIBICAO", tipoExibicao.getToDto());
            dto.adicionarParametro("APRESENTACAO", apresentacaoRelatorio.name());
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(buscarParametros()));
            dto.adicionarParametro("FILTRO", filtro);
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.setNomeRelatorio(getNomeRelatorioDownload());
            dto.setApi("contabil/demonstrativo-disponibilidade-destinacao-recurso/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (exercicio == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício deve ser informado.");
        }
        ve.lancarException();
    }

    private List<ParametrosRelatorios> buscarParametros() {
        filtro = " Período: Janeiro a " + mes.getDescricao() + " de " + exercicio.getAno() + " -";
        filtro += " Exibir: " + tipoExibicao.getDescricao() + " -";
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        parametros.add(new ParametrosRelatorios(" trunc(SLD.DATASALDO) ", ":DATAFINAL", null, OperacaoRelatorio.MENOR_IGUAL, getDataFinal(), null, 1, true));
        parametros.add(new ParametrosRelatorios(" trunc(SLD.DATASALDO) ", ":DATAINICIAL", null, OperacaoRelatorio.MENOR_IGUAL, "01/01/" + exercicio.getAno(), null, 10, true));
        parametros.add(new ParametrosRelatorios(" trunc(SALDO.DATASALDO) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + exercicio.getAno(), getDataFinal(), 3, true));
        parametros.add(new ParametrosRelatorios(" trunc(emp.dataempenho) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + exercicio.getAno(), getDataFinal(), 4, true));
        parametros.add(new ParametrosRelatorios(" trunc(est.dataestorno) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + exercicio.getAno(), getDataFinal(), 5, true));
        parametros.add(new ParametrosRelatorios(" trunc(liq.dataliquidacao) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + exercicio.getAno(), getDataFinal(), 6, true));
        parametros.add(new ParametrosRelatorios(" trunc(pag.datapagamento) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + exercicio.getAno(), getDataFinal(), 7, true));
        parametros.add(new ParametrosRelatorios(" trunc(re.datareceita) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + exercicio.getAno(), getDataFinal(), 8, true));
        parametros.add(new ParametrosRelatorios(" trunc(pg.datapagto) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + exercicio.getAno(), getDataFinal(), 9, true));
        parametros.add(new ParametrosRelatorios(" trunc(lanc.datalancamento) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + exercicio.getAno(), getDataFinal(), 12, true));
        parametros.add(new ParametrosRelatorios(" trunc(transf.DATATRANSFERENCIA) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + exercicio.getAno(), getDataFinal(), 11, true));
        parametros.add(new ParametrosRelatorios(" trunc(ajuste.DATAAJUSTE) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + exercicio.getAno(), getDataFinal(), 13, true));
        parametros.add(new ParametrosRelatorios(" trunc(LIB.DATALIBERACAO) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + exercicio.getAno(), getDataFinal(), 14, true));
        if (!buscarIdsUnidades().isEmpty()) {
            parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, buscarIdsUnidades(), null, 2, false));
        }
        if (unidadeGestora != null) {
            parametros.add(new ParametrosRelatorios(" ug.id ", ":ugId", null, OperacaoRelatorio.IGUAL, unidadeGestora.getId(), null, 2, false));
            filtro += " Unidade Gestora: " + unidadeGestora.getCodigo() + " - " + unidadeGestora.getDescricao() + " -";
        }
        parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, exercicio.getId(), null, 0, false));
        if (filtro.length() > 0) {
            filtro = filtro.substring(0, filtro.length() - 1);
        }
        return parametros;
    }

    private List<Long> buscarIdsUnidades() {
        List<Long> idsUnidades = Lists.newArrayList();
        if (!hierarquias.isEmpty()) {
            String unidades = "";
            for (HierarquiaOrganizacional hierarquia : hierarquias) {
                idsUnidades.add(hierarquia.getSubordinada().getId());
                unidades += " " + hierarquia.getCodigo().substring(3, 10) + " -";
            }
            filtro += " Unidade(s): " + unidades;
        } else if (unidadeGestora == null) {
            List<HierarquiaOrganizacional> unidadesUsuario = relatorioFacade.getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorNivel("", getSistemaFacade().getUsuarioCorrente(), exercicio, getSistemaFacade().getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            for (HierarquiaOrganizacional hierarquia : unidadesUsuario) {
                idsUnidades.add(hierarquia.getSubordinada().getId());
            }
        }
        return idsUnidades;
    }

    public String getNumeroMes() {
        return mes.getNumeroMesString();
    }

    public String getDataFinal() {
        return Util.getDiasMes(mes.getNumeroMes(), exercicio.getAno()) + "/" + mes.getNumeroMesString() + "/" + exercicio.getAno();
    }

    public UnidadeGestora getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(UnidadeGestora unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public List<HierarquiaOrganizacional> getHierarquias() {
        return hierarquias;
    }

    public void setHierarquias(List<HierarquiaOrganizacional> hierarquias) {
        this.hierarquias = hierarquias;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public String getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(String mesFinal) {
        this.mesFinal = mesFinal;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public ApresentacaoRelatorio getApresentacaoRelatorio() {
        return apresentacaoRelatorio;
    }

    public void setApresentacaoRelatorio(ApresentacaoRelatorio apresentacaoRelatorio) {
        this.apresentacaoRelatorio = apresentacaoRelatorio;
    }

    public TipoExibicao getTipoExibicao() {
        return tipoExibicao;
    }

    public void setTipoExibicao(TipoExibicao tipoExibicao) {
        this.tipoExibicao = tipoExibicao;
    }
}
