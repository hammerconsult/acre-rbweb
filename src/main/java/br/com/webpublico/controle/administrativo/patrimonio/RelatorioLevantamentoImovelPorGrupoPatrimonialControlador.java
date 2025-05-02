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
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Date;

/**
 * Created by Desenvolvimento on 13/03/2017.
 */

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoRelatorioLevantamentoImovelPorGrupoPatrimonial",
                pattern = "/relatorio-levantamento-bens-imoveis-por-grupo-patrimonial/",
                viewId = "/faces/administrativo/patrimonio/relatorios/imovel/relatorioLevantamentoBemImovelPorGrupoPatrimonial.xhtml")})
public class RelatorioLevantamentoImovelPorGrupoPatrimonialControlador extends RelatorioPatrimonioSuperControlador {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioLevantamentoImovelPorGrupoPatrimonialControlador.class);
    private GrupoBem grupoBem;
    private Date dtinicial;
    private Date dtFinal;
    private TipoAquisicaoBem tipoAquisicaoBem;
    private String localizacao;
    private String observacao;
    private String numeroNota;
    private String numeroEmpenho;
    private Exercicio exercicioEmpenho;

    @URLAction(mappingId = "novoRelatorioLevantamentoImovelPorGrupoPatrimonial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoRelatorioLevantamentoImovelPorGrupoPatrimonial() {
        limparCampos();
    }

    public void limparCampos() {
        filtro = new FiltroPatrimonio();
        grupoBem = null;
        dtinicial = null;
        dtFinal = null;
        tipoAquisicaoBem = null;
        localizacao = null;
        observacao = null;
        numeroNota = null;
        numeroEmpenho = null;
        exercicioEmpenho = null;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarFiltrosRelatorioLevantamentoImovelPorGrupoPatrimonial();
            StringBuffer filtros = new StringBuffer();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE " + getSistemaFacade().getMunicipio().toUpperCase());
            dto.adicionarParametro("MODULO", "PATRIMÔNIO");
            dto.adicionarParametro("DATAOPERACAO", DataUtil.getDataFormatada(getSistemaFacade().getDataOperacao()));
            dto.adicionarParametro("SECRETARIA", montaNomeSecretaria());
            dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DE LEVANTAMENTO DE BENS IMÓVEIS POR GRUPO PATRIMONIAL");
            dto.adicionarParametro("CONDICAO", montarCondicaoRelatorioLevantamentoImovelPorGrupoPatrimonial(filtros));
            dto.adicionarParametro("FILTROS", filtros.toString());
            dto.setNomeRelatorio("RelatorioDeLevantamentoDeBensImoveisPorGrupoPatrimonial");
            dto.setApi("administrativo/levantamento-imovel-grupo-patrimonial/");
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

    private String montarCondicaoRelatorioLevantamentoImovelPorGrupoPatrimonial(StringBuffer filtros) {
        StringBuffer sql = new StringBuffer();

        if (filtro.getHierarquiaAdm() != null) {
            sql.append(" and VW.CODIGO LIKE '").append(filtro.getHierarquiaAdm().getCodigoSemZerosFinais()).append("%' ");
            filtros.append("Unidade Administrativa: ").append(filtro.getHierarquiaAdm().toString()).append(". ");
        }

        if (filtro.getHierarquiaOrc() != null) {
            sql.append(" and lev.UNIDADEORCAMENTARIA_ID = ").append(filtro.getHierarquiaOrc().getSubordinada().getId());
            filtros.append("Unidade Orçametária: ").append(filtro.getHierarquiaOrc().toString()).append(". ");
        }

        if (getGrupoBem() != null) {
            sql.append(" and GRUPOBEM.id = ").append(getGrupoBem().getId());
            filtros.append("Grupo Patrimonial: ").append(getGrupoBem().toString()).append(". ");
        }

        if (getDtinicial() != null) {
            sql.append(" and lev.DATAAQUISICAO >= to_date('").append(DataUtil.getDataFormatada(getDtinicial())).append("','dd/MM/yyyy') ");
            filtros.append("Data de Aquisição Inicial: ").append(DataUtil.getDataFormatada(getDtinicial())).append(". ");
        }

        if (getDtFinal() != null) {
            sql.append(" and lev.DATAAQUISICAO <= to_date('").append(DataUtil.getDataFormatada(getDtFinal())).append("','dd/MM/yyyy') ");
            filtros.append("Data de Aquisição Final: ").append(DataUtil.getDataFormatada(getDtFinal())).append(". ");
        }

        if (getTipoAquisicaoBem() != null) {
            sql.append(" and lev.tipoAquisicaoBem =  '").append(getTipoAquisicaoBem().name()).append("' ");
            filtros.append("Tipo de Aquisição: ").append(getTipoAquisicaoBem().getDescricao()).append(". ");
        }

        if (getLocalizacao() != null && !getLocalizacao().trim().isEmpty()) {
            sql.append(" and lower(lev.localizacao) like ('%").append(getLocalizacao().toLowerCase()).append("%')");
            filtros.append(" - Localização: ").append(getLocalizacao());
        }

        if (getObservacao() != null && !getObservacao().trim().isEmpty()) {
            sql.append(" and lower(lev.observacao) like ('%").append(getObservacao().toLowerCase()).append("%')");
            filtros.append(" - Observação: ").append(getObservacao());
        }

        if (getNumeroNota() != null && !getNumeroNota().trim().isEmpty()) {
            sql.append(" and exists(SELECT " +
                "                    1 " +
                "                  FROM levantamentobemimovel levantamento " +
                "                  inner JOIN DOCTOCOMPROBLEVBEMIMOVEL DOC ON DOC.LEVANTAMENTOBEMIMOVEL_ID = levantamento.ID " +
                "                  inner JOIN EMPENHOLEVANTAMENTOIMOVEL EMPENHO ON EMPENHO.DOCUMENTOCOMPROBATORIO_ID = DOC.ID " +
                "                  where levantamento.id = lev.id" +
                "                    and doc.numero = '" + getNumeroNota() + "'" +
                " ) ");
            filtros.append(" - Número da Nota: ").append(getNumeroNota());
        }

        if (getNumeroEmpenho() != null && !getNumeroEmpenho().trim().isEmpty()) {
            sql.append(" and exists(SELECT " +
                "                    1 " +
                "                  FROM DOCTOCOMPROBLEVBEMIMOVEL DOC " +
                "                  inner JOIN EMPENHOLEVANTAMENTOIMOVEL EMPENHO ON EMPENHO.DOCUMENTOCOMPROBATORIO_ID = DOC.ID " +
                "                  where DOC.LEVANTAMENTOBEMIMOVEL_ID = lev.id " +
                "                    and empenho.numeroempenho = '" + getNumeroEmpenho() + "'" +
                " ) ");
            filtros.append(" - Número do Empenho: ").append(getNumeroEmpenho());
        }

        if (getExercicioEmpenho() != null) {
            sql.append(" and exists(SELECT " +
                "                    1 " +
                "                  FROM DOCTOCOMPROBLEVBEMIMOVEL DOC " +
                "                  inner JOIN EMPENHOLEVANTAMENTOIMOVEL EMPENHO ON EMPENHO.DOCUMENTOCOMPROBATORIO_ID = DOC.ID " +
                "                  where DOC.LEVANTAMENTOBEMIMOVEL_ID = lev.id " +
                "                    and extract(year from empenho.dataempenho) = " + getExercicioEmpenho().getAno() +
                "         ) ");
            filtros.append(" - Exercício Empenho: ").append(getExercicioEmpenho().getAno()).append(". ");
        }
        return sql.toString();
    }

    private void validarFiltrosRelatorioLevantamentoImovelPorGrupoPatrimonial() {
        ValidacaoException ve = new ValidacaoException();
        if (filtro.getHierarquiaOrc() == null && filtro.getHierarquiaAdm() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione ao menos uma unidade administrativa ou unidade orçamentária.");
        }
        if (getDtinicial() != null && getDtFinal() != null) {
            if (getDtinicial().after(getDtFinal())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Data inicial deve ser anterior a data final.");
            }
        }
        ve.lancarException();
    }

    public GrupoBem getGrupoBem() {
        return grupoBem;
    }

    public void setGrupoBem(GrupoBem grupoBem) {
        this.grupoBem = grupoBem;
    }

    public Date getDtinicial() {
        return dtinicial;
    }

    public void setDtinicial(Date dtinicial) {
        this.dtinicial = dtinicial;
    }

    public Date getDtFinal() {
        return dtFinal;
    }

    public void setDtFinal(Date dtFinal) {
        this.dtFinal = dtFinal;
    }

    public TipoAquisicaoBem getTipoAquisicaoBem() {
        return tipoAquisicaoBem;
    }

    public void setTipoAquisicaoBem(TipoAquisicaoBem tipoAquisicaoBem) {
        this.tipoAquisicaoBem = tipoAquisicaoBem;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
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

    public String getNumeroEmpenho() {
        return numeroEmpenho;
    }

    public void setNumeroEmpenho(String numeroEmpenho) {
        this.numeroEmpenho = numeroEmpenho;
    }

    public Exercicio getExercicioEmpenho() {
        return exercicioEmpenho;
    }

    public void setExercicioEmpenho(Exercicio exercicioEmpenho) {
        this.exercicioEmpenho = exercicioEmpenho;
    }
}
