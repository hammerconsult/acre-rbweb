package br.com.webpublico.controle.administrativo.patrimonio;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.GrupoBem;
import br.com.webpublico.entidadesauxiliares.administrativo.relatorio.FiltroPatrimonio;
import br.com.webpublico.enums.TipoAquisicaoBem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Created by Desenvolvimento on 13/03/2017.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoRelatorioLevantamentoImovelPorExercicio",
                pattern = "/relatorio-levantamento-bens-imoveis-por-exercicio/",
                viewId = "/faces/administrativo/patrimonio/relatorios/imovel/relatorioLevantamentoBemImovelPorExercicio.xhtml")})
public class RelatorioLevantamentoImovelPorExercicioControlador extends RelatorioPatrimonioSuperControlador {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioLevantamentoImovelPorExercicioControlador.class);
    private Exercicio exercicioInicial;
    private Exercicio exercicioFinal;
    private GrupoBem grupoBem;
    private TipoAquisicaoBem tipoAquisicaoBem;
    private String numeroNotaFiscal;
    private String numeroEmpenho;
    private Integer anoEmpenho;
    private String observacao;
    private String localizacao;
    private Boolean detalhar;
    private Integer exercicio;
    private Integer tipoUnidade;

    @URLAction(mappingId = "novoRelatorioLevantamentoImovelPorExercicio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoRelatorioLevantamentoImovelPorExercicio() {
        limparCampos();
    }

    public void limparCampos() {
        filtro = new FiltroPatrimonio();
        detalhar = Boolean.FALSE;
        exercicioInicial = null;
        exercicioFinal = null;
        grupoBem = null;
        tipoAquisicaoBem = null;
        numeroNotaFiscal = null;
        numeroEmpenho = null;
        anoEmpenho = null;
        observacao = null;
        localizacao = null;
        exercicio = 0;
        tipoUnidade = 0;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarFiltrosRelatorioLevantamentoImovelPorExercicio();
            StringBuffer filtros = new StringBuffer();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE " + getSistemaFacade().getMunicipio().toUpperCase());
            dto.adicionarParametro("MODULO", "PATRIMÔNIO");
            dto.adicionarParametro("DATAOPERACAO", DataUtil.getDataFormatada(getSistemaFacade().getDataOperacao()));
            dto.adicionarParametro("SECRETARIA", montaNomeSecretaria());
            dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DE LEVANTAMENTO DE BENS IMÓVEIS POR EXERCÌCIO");
            dto.adicionarParametro("detalhado", detalhar);
            dto.adicionarParametro("CONDICAO", montarCondicaoRelatorioLevantamentoImovelPorExercicio(filtros));
            dto.adicionarParametro("FILTROS", filtros.toString());
            dto.setNomeRelatorio("RelatorioDeLevantamentodeBensImoveisPorExercicio");
            dto.setApi("administrativo/levantamento-imovel-por-exercicio/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um erro ao gerar o relatório: " + ex.getMessage());
            logger.error(ex.getMessage());
        }
    }

    private String montarCondicaoRelatorioLevantamentoImovelPorExercicio(StringBuffer filtros) {
        StringBuffer sql = new StringBuffer();

        if (filtro.getHierarquiaAdm() != null && filtro.getHierarquiaAdm().getCodigo() != null) {
            sql.append(" and VW.CODIGO LIKE '").append(filtro.getHierarquiaAdm().getCodigoSemZerosFinais()).append("%' ");
            filtros.append("Unidade Administrativa: ").append(filtro.getHierarquiaAdm().toString()).append(". ");
        }

        if (filtro.getHierarquiaOrc() != null && filtro.getHierarquiaOrc().getCodigo() != null) {
            sql.append(" and lev.UNIDADEORCAMENTARIA_ID = ").append(filtro.getHierarquiaOrc().getSubordinada().getId());
            filtros.append("Unidade Orçametária: ").append(filtro.getHierarquiaOrc().toString()).append(". ");
        }

        if (getGrupoBem() != null) {
            sql.append(" and lev.grupobem_id = ").append(getGrupoBem().getId());
            filtros.append("Grupo Patrimonial: ").append(getGrupoBem().toString()).append(". ");
        }

        if (getExercicioInicial() != null) {
            sql.append("  AND EXTRACT(YEAR FROM to_date(lev.dataaquisicao)) >= ").append(getExercicioInicial().getAno());
            filtros.append("Exercício Inicial: " + getExercicioInicial().getAno()).append(". ");
        }

        if (getExercicioFinal() != null) {
            sql.append("  AND EXTRACT(YEAR FROM to_date(lev.dataaquisicao)) <= ").append(getExercicioFinal().getAno());
            filtros.append("Exercício Final: " + getExercicioFinal().getAno()).append(". ");
        }

        if (getTipoAquisicaoBem() != null) {
            sql.append(" and lev.tipoAquisicaoBem =  '").append(getTipoAquisicaoBem().name()).append("' ");
            filtros.append("Tipo de Aquisição: ").append(getTipoAquisicaoBem().getDescricao()).append(". ");
        }

        if (StringUtils.isNotEmpty(getNumeroNotaFiscal()) && !getNumeroNotaFiscal().trim().isEmpty()) {
            sql.append(" and exists(select 1 from DOCTOCOMPROBLEVBEMIMOVEL doc  ")
                    .append("    where doc.LEVANTAMENTOBEMIMOVEL_ID = lev.id AND doc.NUMERO like ")
                    .append("'%")
                    .append(getNumeroNotaFiscal().trim())
                    .append("%'")
                    .append(" ) ");
        }

        if ((StringUtils.isNotEmpty(getNumeroEmpenho())) && !getNumeroEmpenho().trim().isEmpty() && getAnoEmpenho() != null) {

            sql.append(" AND exists(SELECT 1 ")
                    .append(" FROM DOCTOCOMPROBLEVBEMIMOVEL doc ")
                    .append(" INNER JOIN EMPENHOLEVANTAMENTOIMOVEL empenho ")
                    .append(" ON doc.ID = empenho.DOCUMENTOCOMPROBATORIO_ID ")
                    .append(" WHERE ")
                    .append(" doc.LEVANTAMENTOBEMIMOVEL_ID = lev.id ")
                    .append(" AND empenho.NUMEROEMPENHO LIKE ")
                    .append("'%").append(getNumeroEmpenho().trim()).append("%'")
                    .append(" AND ")
                    .append(" extract(YEAR FROM empenho.DATAEMPENHO) = ")
                    .append(getAnoEmpenho())
                    .append(" ) ");
        }

        if ((getObservacao() != null) && !getObservacao().trim().isEmpty())
            sql.append(" and lev.observacao like '%").append(getObservacao()).append("%'");

        if ((getLocalizacao() != null) && !getLocalizacao().trim().isEmpty())
            sql.append(" and lev.localizacao like '%").append(getLocalizacao()).append("'%");

        filtros.append(" Detalhar: ").append(Util.converterBooleanSimOuNao(getDetalhar())).append(". ");

        return sql.toString();
    }

    private void validarFiltrosRelatorioLevantamentoImovelPorExercicio() {
        ValidacaoException ex = new ValidacaoException();
        if (getTipoUnidade() == 0) {
            ex.adicionarMensagemDeCampoObrigatorio("É necessário informar um Tipo de Unidade.");
        } else {
            if (getTipoUnidade() == 1 && filtro.getHierarquiaAdm() == null) {
                ex.adicionarMensagemDeCampoObrigatorio("O campo unidade administrativa é obrigatório.");

            }
            if (getTipoUnidade() == 2 && filtro.getHierarquiaOrc() == null) {
                ex.adicionarMensagemDeCampoObrigatorio("O campo Unidade Orçamentaria é obrigatório.");

            }
            if (getTipoUnidade() == 3) {
                if (filtro.getHierarquiaAdm() == null) {
                    ex.adicionarMensagemDeCampoObrigatorio("O campo unidade administrativa é obrigatório.");

                }
                if (filtro.getHierarquiaOrc() == null) {
                    ex.adicionarMensagemDeCampoObrigatorio("O campo Unidade Orçamentaria é obrigatório.");

                }
            }
        }
        if (getExercicio() == 0) {
            ex.adicionarMensagemDeCampoObrigatorio("É necessário informar um Filtro por Exercício.");

        } else {
            if (getExercicio() == 1 && getExercicioInicial() == null) {
                ex.adicionarMensagemDeOperacaoNaoPermitida("O campo Exercício Inicial é obrigatório.");

            }
            if (getExercicio() == 2 && getExercicioFinal() == null) {
                ex.adicionarMensagemDeOperacaoNaoPermitida("O campo Exercício Final é obrigatório.");

            }
            if (getExercicio() == 3) {
                if (getExercicioInicial() == null) {
                    ex.adicionarMensagemDeOperacaoNaoPermitida("O campo Exercício Inicial é obrigatório.");

                }
                if (getExercicioFinal() == null) {
                    ex.adicionarMensagemDeOperacaoNaoPermitida("O campo Exercício Final é obrigatório.");

                }
            }
        }
        if (getExercicioInicial() != null && getExercicioFinal() != null) {
            if (getExercicioInicial().getAno() > getExercicioFinal().getAno()) {
                ex.adicionarMensagemDeOperacaoNaoPermitida("O campo exercício inicial deve ser maior que execício final.");
            }
        }
        ex.lancarException();
    }

    public void atualizarCamposExercicio() {
        exercicioInicial = null;
        exercicioFinal = null;
    }

    public void atualizarCamposUnidade() {
        filtro.setHierarquiaAdm(null);
        filtro.setHierarquiaOrc(null);
    }

    public Exercicio getExercicioInicial() {
        return exercicioInicial;
    }

    public void setExercicioInicial(Exercicio exercicioInicial) {
        this.exercicioInicial = exercicioInicial;
    }

    public Exercicio getExercicioFinal() {
        return exercicioFinal;
    }

    public void setExercicioFinal(Exercicio exercicioFinal) {
        this.exercicioFinal = exercicioFinal;
    }

    public GrupoBem getGrupoBem() {
        return grupoBem;
    }

    public void setGrupoBem(GrupoBem grupoBem) {
        this.grupoBem = grupoBem;
    }

    public TipoAquisicaoBem getTipoAquisicaoBem() {
        return tipoAquisicaoBem;
    }

    public void setTipoAquisicaoBem(TipoAquisicaoBem tipoAquisicaoBem) {
        this.tipoAquisicaoBem = tipoAquisicaoBem;
    }

    public String getNumeroNotaFiscal() {
        return numeroNotaFiscal;
    }

    public void setNumeroNotaFiscal(String numeroNotaFiscal) {
        this.numeroNotaFiscal = numeroNotaFiscal;
    }

    public String getNumeroEmpenho() {
        return numeroEmpenho;
    }

    public void setNumeroEmpenho(String numeroEmpenho) {
        this.numeroEmpenho = numeroEmpenho;
    }

    public Integer getAnoEmpenho() {
        return anoEmpenho;
    }

    public void setAnoEmpenho(Integer anoEmpenho) {
        this.anoEmpenho = anoEmpenho;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public Boolean getDetalhar() {
        return detalhar;
    }

    public void setDetalhar(Boolean detalhar) {
        this.detalhar = detalhar;
    }

    public Integer getExercicio() {
        return exercicio;
    }

    public void setExercicio(Integer exercicio) {
        this.exercicio = exercicio;
    }

    public Integer getTipoUnidade() {
        return tipoUnidade;
    }

    public void setTipoUnidade(Integer tipoUnidade) {
        this.tipoUnidade = tipoUnidade;
    }
}
