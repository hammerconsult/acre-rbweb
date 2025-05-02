package br.com.webpublico.controle.tributario;

import br.com.webpublico.controle.RelatorioLevantamentoBemPorGrupoBemComparativoControlador;
import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.GrupoBem;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.EstadoConservacaoBem;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-levantamento-bem-por-grupo-bem", pattern = "/relatorios/levantamento-bem-por-grupo-bem/", viewId = "/faces/administrativo/relatorios/relatoriolevantamentobemporgrupobem.xhtml"),
})
@ManagedBean
public class RelatorioLevantamentoBemPorGrupoBemControlador extends AbstractReport implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(RelatorioLevantamentoBemPorGrupoBemComparativoControlador.class);
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private GrupoBem grupoBem;
    private Date dtInicial;
    private Date dtFinal;
    private HierarquiaOrganizacional hoADM;
    private HierarquiaOrganizacional hoORC;
    private String filtro;
    private Date dtReferencia;
    private EstadoConservacaoBem estadoConservacaoBem;
    private Boolean consolidar;
    private Boolean efetivado = Boolean.FALSE;
    private String observacao;
    private String numeroNota;
    private Exercicio exercicioEmpenho;
    private String localizacao;
    private String numeroEmpenho;

    @URLAction(mappingId = "relatorio-levantamento-bem-por-grupo-bem", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void relatorioGrupoBem() {
        limparCampos();
    }

    public void limparCampos() {
        this.grupoBem = null;
        this.dtReferencia = getSistemaFacade().getDataOperacao();
        this.consolidar = Boolean.FALSE;
        dtInicial = null;
        dtFinal = null;
        hoADM = null;
        hoORC = null;
        filtro = null;
        estadoConservacaoBem = null;
        observacao = null;
        numeroNota = null;
        exercicioEmpenho = null;
        localizacao = null;
        numeroEmpenho = null;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("MODULO", "ADMINISTRATIVO");
            dto.adicionarParametro("MUNICIPIO", montarNomeDoMunicipio());
            dto.adicionarParametro("SECRETARIA", buscarNomeSecretaria());
            String nomeRelatorio = "RELATÓRIO DE LEVANTAMENTO DE BENS MÓVEIS POR GRUPO PATRIMONIAL";
            dto.adicionarParametro("NOMERELATORIO", nomeRelatorio);

            dto.setNomeRelatorio(nomeRelatorio);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("dataOperacao", getSistemaFacade().getDataOperacao());
            dto.adicionarParametro("condicao", montarCondicao(dto));
            dto.adicionarParametro("consolidar", consolidar);
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setApi("administrativo/levantamento-bem-por-grupo-bem/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
            logger.error(ex.getMessage());
        }
    }

    @Override
    public String montarNomeDoMunicipio() {
        return "MUNICÍPIO DE " + getSistemaFacade().getMunicipio().toUpperCase();
    }

    private String buscarNomeSecretaria() {
        if (hoADM != null) {
            HierarquiaOrganizacional secretaria = hierarquiaOrganizacionalFacade.recuperarSecretariaAdministrativaVigente(this.hoADM, getSistemaFacade().getDataOperacao());
            if (secretaria != null) {
                return secretaria.getDescricao().toUpperCase();
            }
        }
        if (hoORC != null) {
            HierarquiaOrganizacional hierarquiaAdministrativa = hierarquiaOrganizacionalFacade.recuperarAdministrativaDaOrcamentariaVigente(this.hoORC, getSistemaFacade().getDataOperacao());
            HierarquiaOrganizacional secretaria = hierarquiaOrganizacionalFacade.recuperarSecretariaAdministrativaVigente(hierarquiaAdministrativa, getSistemaFacade().getDataOperacao());
            if (secretaria != null) {
                return secretaria.getDescricao().toUpperCase();
            }
        }
        return "";
    }


    private List<ParametrosRelatorios> montarParametrosEFiltrosRelatorioAcompanhamento(RelatorioDTO dto) {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        StringBuilder filtros = new StringBuilder();
        parametros.add(new ParametrosRelatorios(null, ":DataReferencia", null, OperacaoRelatorio.IGUAL, DataUtil.getDataFormatada(dtReferencia), null, 0, true));
        parametros.add(new ParametrosRelatorios(" gb.tipobem ", ":tipoBem", null, OperacaoRelatorio.IGUAL, TipoBem.MOVEIS.name(), null, 1, false));
        if (hoORC != null && hoORC.getCodigo() != null) {
            filtros.append(" Unidade Orçamentária: ").append(hoORC.toString()).append(". ");

            parametros.add(new ParametrosRelatorios(" vworc.codigo ", ":codigoOrc", null, OperacaoRelatorio.LIKE, hoORC.getCodigo(), null, 3, false));
            parametros.add(new ParametrosRelatorios(" SALDO.UNIDADEORGANIZACIONAL_ID ", ":orcamentaria", null, OperacaoRelatorio.IGUAL, null, null, 2, false));
            parametros.add(new ParametrosRelatorios(" EST.DETENTORAORCAMENTARIA_ID ", ":orcamentaria", null, OperacaoRelatorio.IGUAL, hoORC.getSubordinada().getId(), null, 1, false));
        }



        if (dtInicial != null) {
            filtros.append(" Data de Aquisição Inicial: ").append(DataUtil.getDataFormatada(dtInicial)).append(". ");
            parametros.add(new ParametrosRelatorios(null, ":DataInicial", null, OperacaoRelatorio.MENOR_IGUAL, DataUtil.getDataFormatada(dtInicial), null, 0, true));

            parametros.add(new ParametrosRelatorios(" trunc(lev.dataaquisicao) ", ":DataInicial", null, OperacaoRelatorio.MAIOR_IGUAL, null, null, 3, true));
            parametros.add(new ParametrosRelatorios(" trunc(bem.dataaquisicao) ", ":DataInicial", null, OperacaoRelatorio.MAIOR_IGUAL, null, null, 1, true));
        }

        if (dtFinal != null) {
            filtros.append(" Data de Aquisição Final: ").append(DataUtil.getDataFormatada(dtFinal)).append(". ");
            parametros.add(new ParametrosRelatorios(null, ":DataFinal", null, OperacaoRelatorio.MENOR_IGUAL, DataUtil.getDataFormatada(dtFinal), null, 0, true));
            parametros.add(new ParametrosRelatorios(" trunc(lev.dataaquisicao) ", ":DataFinal", null, OperacaoRelatorio.MENOR_IGUAL, null, null, 3, true));
            parametros.add(new ParametrosRelatorios(" trunc(bem.dataaquisicao) ", ":DataFinal", null, OperacaoRelatorio.MENOR_IGUAL, null, null, 1, true));
        }

        if (grupoBem != null) {
            filtros.append(" Grupo Patrimonial ").append(grupoBem.toString()).append(". ");
            parametros.add(new ParametrosRelatorios(" SALDO.GRUPOBEM_ID ", ":grupoBem_id", null, OperacaoRelatorio.IGUAL, null, null, 2, false));
            parametros.add(new ParametrosRelatorios(" EST.GRUPOBEM_ID  ", ":grupoBem_id", null, OperacaoRelatorio.IGUAL, grupoBem.getId(), null, 1, false));
            parametros.add(new ParametrosRelatorios(" GRUPOBEM.ID  ", ":grupoBem_id", null, OperacaoRelatorio.IGUAL, null, null, 4, false));
        }
        if (dtReferencia != null) {
            filtros.append(" Data de Referência: ").append(DataUtil.getDataFormatada(dtReferencia));
        }
        dto.adicionarParametro("FILTROS", filtros.toString());
        return parametros;
    }

    public String montarCondicao(RelatorioDTO dto) {
        StringBuilder sql = new StringBuilder();
        StringBuilder filtros = new StringBuilder();

        if (!consolidar) {
            if (hoADM != null && hoADM.getCodigo() != null) {
                sql.append(" and VWADM.CODIGO LIKE '").append(hoADM.getCodigoSemZerosFinais()).append("%'");
                filtros.append(" Unidade Organizacional: ").append(hoADM.toString()).append(". ");
            }

            if (hoORC != null && hoORC.getCodigo() != null) {
                sql.append(" and VWORC.CODIGO LIKE '").append(hoORC.getCodigo()).append("' ");
                filtros.append(" Unidade Orçamentária: ").append(hoORC.toString()).append(". ");
            }

            filtros.append(" Consolidado: Não").append(". ");
        } else {

            if (hoADM != null && hoADM.getCodigo() != null) {
                dto.adicionarParametro("codigoLikeHierarquia", hoADM.getCodigoSemZerosFinais() + "%");
                dto.adicionarParametro("codigoHierarquia", hoADM.getCodigo());
                filtros.append(" Unidade Organizacional: ").append(hoADM.toString()).append(". ");
            }

            if (hoORC != null && hoORC.getCodigo() != null) {
                sql.append(" and VWORC.CODIGO LIKE '").append(hoORC.getCodigo()).append("' ");
                filtros.append(" Unidade Orçamentária: ").append(hoORC.toString()).append(". ");
            }

            filtros.append(" Consolidado: Sim").append(". ");
        }

        if (dtInicial != null) {
            sql.append(" and lev.dataaquisicao >= to_date('").append(DataUtil.getDataFormatada(dtInicial)).append("') ");
            filtros.append(" Data de Aquisição Inicial: ").append(DataUtil.getDataFormatada(dtInicial)).append(". ");
        }

        if (dtFinal != null) {
            sql.append(" and lev.DATAAQUISICAO <= to_date('").append(DataUtil.getDataFormatada(dtFinal)).append("','dd/MM/yyyy') ");
            filtros.append("Data de Aquisição Final: ").append(DataUtil.getDataFormatada(dtFinal)).append(". ");
        }

        if (grupoBem != null) {
            sql.append(" and grupobem.id = ").append(grupoBem.getId());
            filtros.append(" Grupo Patrimonial ").append(grupoBem.toString()).append(". ");
        }

        if (estadoConservacaoBem != null) {
            sql.append(" and lev.estadoconservacaobem = '").append(estadoConservacaoBem.name()).append("' ");
            filtros.append(" Estado de Conservação: ").append(estadoConservacaoBem.getDescricao()).append(". ");
        }

        if (localizacao != null && !localizacao.trim().isEmpty()) {
            sql.append(" and lower(lev.localizacao) like ('%").append(localizacao.toLowerCase()).append("%')");
            filtros.append(" - Localização: ").append(localizacao);
        }

        if (observacao != null && !observacao.trim().isEmpty()) {
            sql.append(" and lower(lev.observacao) like ('%").append(observacao.toLowerCase()).append("%')");
            filtros.append(" - Observação: ").append(observacao);
        }

        if (numeroNota != null && !numeroNota.trim().isEmpty()) {
            sql.append(" and lev.notafiscal = '").append(numeroNota).append("'");
            filtros.append(" - Número da Nota: ").append(numeroNota);
        }

        if (numeroEmpenho != null && !numeroEmpenho.trim().isEmpty()) {
            sql.append(" and lev.notaempenho = '").append(numeroEmpenho).append("'");
            filtros.append(" - Número do Empenho: ").append(numeroEmpenho);
        }

        if (exercicioEmpenho != null) {
            sql.append(" and extract(year from lev.datanotaempenho) = ").append(exercicioEmpenho.getAno());
            filtros.append(" - Exercício Empenho: ").append(exercicioEmpenho.getAno()).append(". ");
        }

        dto.adicionarParametro("FILTROS", filtros.toString());
        return sql.toString();
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        verificarCampoNull(ve, hoADM, "O campo Unidade Organizacional deve ser informado.");
        ve.lancarException();
    }

    private void verificarCampoNull(ValidacaoException ve, Object campo, String mensagemValidacao) {
        if (campo == null) {
            ve.adicionarMensagemDeCampoObrigatorio(mensagemValidacao);
        }
    }

    public GrupoBem getGrupoBem() {
        return grupoBem;
    }

    public void setGrupoBem(GrupoBem grupoBem) {
        this.grupoBem = grupoBem;
    }

    public Date getDtReferencia() {
        return dtReferencia;
    }

    public void setDtReferencia(Date dtReferencia) {
        this.dtReferencia = dtReferencia;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public Date getDtInicial() {
        return dtInicial;
    }

    public void setDtInicial(Date dtInicial) {
        this.dtInicial = dtInicial;
    }

    public Date getDtFinal() {
        return dtFinal;
    }

    public void setDtFinal(Date dtFinal) {
        this.dtFinal = dtFinal;
    }

    public HierarquiaOrganizacional getHoADM() {
        return hoADM;
    }

    public void setHoADM(HierarquiaOrganizacional hoADM) {
        this.hoADM = hoADM;
    }

    public HierarquiaOrganizacional getHoORC() {
        return hoORC;
    }

    public void setHoORC(HierarquiaOrganizacional hoORC) {
        this.hoORC = hoORC;
    }

    public EstadoConservacaoBem getEstadoConservacaoBem() {
        return estadoConservacaoBem;
    }

    public void setEstadoConservacaoBem(EstadoConservacaoBem estadoConservacaoBem) {
        this.estadoConservacaoBem = estadoConservacaoBem;
    }

    public Boolean getEfetivado() {
        return efetivado;
    }

    public void setEfetivado(Boolean efetivado) {
        this.efetivado = efetivado;
    }

    public Boolean getConsolidar() {
        return consolidar;
    }

    public void setConsolidar(Boolean consolidar) {
        this.consolidar = consolidar;
    }

    public List<SelectItem> buscarHierarquiasOrcamentarias() {
        List<SelectItem> toReturn = new ArrayList<>();
        if (hoADM != null && hoADM.getSubordinada() != null) {
            toReturn.add(new SelectItem(null, ""));
            for (HierarquiaOrganizacional obj : hierarquiaOrganizacionalFacade.retornaHierarquiaOrcamentariaPorUnidadeAdministrativa(hoADM.getSubordinada(), getSistemaFacade().getDataOperacao())) {
                toReturn.add(new SelectItem(obj, obj.toString()));
            }
            return toReturn;
        }

        toReturn.add(new SelectItem(null, ""));
        for (HierarquiaOrganizacional obj : hierarquiaOrganizacionalFacade.retornaHierarquiaOrganizacionalOrcamentariaOndeUsuarioEhGestorPatrimonio(getSistemaFacade().getUsuarioCorrente(), getSistemaFacade().getDataOperacao())) {
            toReturn.add(new SelectItem(obj, obj.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getListaSelectItemEstadosDeConservacao() {
        return Util.getListSelectItem(Arrays.asList(EstadoConservacaoBem.getValoresPossiveisParaLevantamentoDeBemPatrimonial()));
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getNumeroNota() {
        return numeroNota;
    }

    public void setNumeroNota(String numeroNota) {
        this.numeroNota = numeroNota;
    }

    public Exercicio getExercicioEmpenho() {
        return exercicioEmpenho;
    }

    public void setExercicioEmpenho(Exercicio exercicioEmpenho) {
        this.exercicioEmpenho = exercicioEmpenho;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public String getNumeroEmpenho() {
        return numeroEmpenho;
    }

    public void setNumeroEmpenho(String numeroEmpenho) {
        this.numeroEmpenho = numeroEmpenho;
    }

}
