package br.com.webpublico.controlerelatorio;


import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.FonteDeRecursos;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.UnidadeGestora;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.ApresentacaoRelatorio;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.FonteDeRecursosFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "demonstrativo-programa-cat-economica", pattern = "/demonstrativo-programa-categoria-economica/", viewId = "/faces/financeiro/relatorio/demonstrativo-programa-cat-economica.xhtml")
})
@ManagedBean
public class DemonstrativoProgramaCategoriaEconomicaControlador {

    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private ApresentacaoRelatorio apresentacao;
    private Date dataReferencia;
    private FonteDeRecursos fonteDeRecursos;
    private List<HierarquiaOrganizacional> unidades;
    private UnidadeGestora unidadeGestora;
    private Boolean mostrarRodape;
    private String filtros;

    public DemonstrativoProgramaCategoriaEconomicaControlador() {
    }

    @URLAction(mappingId = "demonstrativo-programa-cat-economica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        dataReferencia = new Date();
        apresentacao = ApresentacaoRelatorio.CONSOLIDADO;
        fonteDeRecursos = null;
        unidades = Lists.newArrayList();
        mostrarRodape = Boolean.TRUE;
        unidadeGestora = null;
        filtros = "";
    }

    public List<SelectItem> getApresentacoes() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(ApresentacaoRelatorio.CONSOLIDADO, ApresentacaoRelatorio.CONSOLIDADO.getDescricao()));
        toReturn.add(new SelectItem(ApresentacaoRelatorio.ORGAO, ApresentacaoRelatorio.ORGAO.getDescricao()));
        toReturn.add(new SelectItem(ApresentacaoRelatorio.UNIDADE, ApresentacaoRelatorio.UNIDADE.getDescricao()));
        return toReturn;
    }

    public List<FonteDeRecursos> completarFontesDeRecursos(String filtro) {
        return fonteDeRecursosFacade.listaFiltrandoPorExercicio(filtro.trim(), sistemaFacade.getExercicioCorrente());
    }

    public void gerarRelatorio() {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.setNomeParametroSubreportDir("SUBREPORT_DIR");
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(buscarParametros()));
            dto.adicionarParametro("EXIBIR_RODAPE", mostrarRodape);
            dto.adicionarParametro("DATA_REFERENCIA", DataUtil.getDataFormatada(dataReferencia));
            dto.adicionarParametro("EXERCICIO", getExercicioDaDataReferencia().getAno().toString());
            dto.adicionarParametro("FILTROS", filtros);
            dto.adicionarParametro("apresentacaoRelatorio", apresentacao.getToDto());
            dto.adicionarParametro("APRESENTACAO", apresentacao.name());
            dto.adicionarParametro("MODULO", "Planejamento Público");
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
            dto.setNomeRelatorio("DEMONSTRATIVO-POR-PROGRAMA-E-CATEGORIA-ECONÔMICA");
            dto.setApi("contabil/demonstrativo-programa-categoria-economica/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (dataReferencia == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Referência deve ser informado.");
        }
        ve.lancarException();
    }

    private List<ParametrosRelatorios> buscarParametros() {
        filtros = "";
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        filtrosParametrosUnidade(parametros);

        parametros.add(new ParametrosRelatorios(null, ":DATAREFERENCIA", null, OperacaoRelatorio.IGUAL, DataUtil.getDataFormatada(dataReferencia), null, 0, true));
        parametros.add(new ParametrosRelatorios(null, ":ID_EXERCICIO", null, OperacaoRelatorio.IGUAL, getExercicioDaDataReferencia().getId(), null, 0, true));
        if (fonteDeRecursos != null) {
            parametros.add(new ParametrosRelatorios(" fte.id ", ":idFonte", null, OperacaoRelatorio.IGUAL, fonteDeRecursos.getId(), null, 1, false));
            filtros += " Fonte de Recurso: " + fonteDeRecursos.toString() + "; ";
        }
        return parametros;
    }

    public void filtrosParametrosUnidade(List<ParametrosRelatorios> parametros) {
        List<Long> idsUnidades = Lists.newArrayList();
        if (!unidades.isEmpty()) {
            String codigoUnidades = "";
            for (HierarquiaOrganizacional hierarquiaOrganizacional : unidades) {
                idsUnidades.add(hierarquiaOrganizacional.getSubordinada().getId());
                codigoUnidades += " " + hierarquiaOrganizacional.getCodigo().substring(3, 10) + " -";
            }
            parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
            filtros += " Unidade(s): " + codigoUnidades;

        } else if (unidadeGestora == null) {
            List<HierarquiaOrganizacional> unidadesDoUsuario = hierarquiaOrganizacionalFacade.listaHierarquiaUsuarioCorrentePorNivel("", sistemaFacade.getUsuarioCorrente(), sistemaFacade.getExercicioCorrente(), sistemaFacade.getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            for (HierarquiaOrganizacional hierarquiaOrganizacional : unidadesDoUsuario) {
                idsUnidades.add(hierarquiaOrganizacional.getSubordinada().getId());
            }
            if (!idsUnidades.isEmpty()) {
                parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
            }
        } else {
            parametros.add(new ParametrosRelatorios(" ug.codigo ", ":ugCodigo", null, OperacaoRelatorio.IGUAL, this.unidadeGestora.getCodigo(), null, 1, false));
        }
    }

    private Exercicio getExercicioDaDataReferencia() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        return exercicioFacade.getExercicioPorAno(Integer.valueOf(format.format(dataReferencia)));
    }

    public ApresentacaoRelatorio getApresentacao() {
        return apresentacao;
    }

    public void setApresentacao(ApresentacaoRelatorio apresentacao) {
        this.apresentacao = apresentacao;
    }

    public Date getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(Date dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public List<HierarquiaOrganizacional> getUnidades() {
        return unidades;
    }

    public void setUnidades(List<HierarquiaOrganizacional> unidades) {
        this.unidades = unidades;
    }

    public Boolean getMostrarRodape() {
        return mostrarRodape;
    }

    public void setMostrarRodape(Boolean mostrarRodape) {
        this.mostrarRodape = mostrarRodape;
    }

    public UnidadeGestora getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(UnidadeGestora unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }
}
