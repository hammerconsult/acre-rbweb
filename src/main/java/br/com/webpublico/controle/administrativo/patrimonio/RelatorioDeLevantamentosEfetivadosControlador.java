package br.com.webpublico.controle.administrativo.patrimonio;

import br.com.webpublico.entidades.GrupoBem;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.LoteEfetivacaoLevantamentoBem;
import br.com.webpublico.entidadesauxiliares.administrativo.relatorio.FiltroPatrimonio;
import br.com.webpublico.enums.EstadoConservacaoBem;
import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
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
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Desenvolvimento on 13/03/2017.
 */
@ManagedBean
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioLevEfetivados",
        pattern = "/relatorio-levantamentos-efetivados/novo/",
        viewId = "/faces/administrativo/patrimonio/relatorios/relatoriobenslevantamentoefetivados.xhtml")})
public class RelatorioDeLevantamentosEfetivadosControlador extends RelatorioPatrimonioSuperControlador {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioDeLevantamentosEfetivadosControlador.class);
    private EstadoConservacaoBem estadoConservacaoBem;
    private GrupoBem grupoBem;

    @URLAction(mappingId = "novoRelatorioLevEfetivados", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        setFiltro(new FiltroPatrimonio());
        estadoConservacaoBem = null;
        grupoBem = null;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarFiltrosRelatorioLevantamentosEfetivados();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            StringBuffer filtros = new StringBuffer();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "PATRIMÔNIO");
            dto.adicionarParametro("SECRETARIA", montaNomeSecretaria());
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO");
            dto.adicionarParametro("NOMERELATORIO", getNomeRelatorio());
            dto.adicionarParametro("DATA_OPERACAO", DataUtil.getDataFormatada(getSistemaFacade().getDataOperacao()));
            dto.adicionarParametro("CONDICAO", montarCondicaoRelatorioLevantamentosEfetivados(filtros));
            dto.adicionarParametro("FILTROS", filtros.toString());
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("administrativo/levantamento-bem-movel-efetivado/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            FacesUtil.addErroAoGerarRelatorio("Ocorreu um erro ao gerar o relatório: " + ex.getMessage());
        }
    }

    public void gerarRelatorioLevantamentosEfetivados(LoteEfetivacaoLevantamentoBem loteEfetivacaoLevantamentoBem, String tipoRelatorioExtensao) {
        try {
            filtro = new FiltroPatrimonio();

            RelatorioDTO dto = new RelatorioDTO();
            StringBuffer filtros = new StringBuffer();
            filtros.append("Unidade Orçametária: ").append(super.getHierarquiaOrganizacionalFacade().getDescricaoHierarquia(TipoHierarquiaOrganizacional.ORCAMENTARIA.name(),
                loteEfetivacaoLevantamentoBem.getUnidadeOrcamentaria(),
                loteEfetivacaoLevantamentoBem.getDataEfetivacao())).append(". ").toString();

            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "PATRIMÔNIO");
            dto.adicionarParametro("SECRETARIA", montaNomeSecretaria());
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO");
            dto.adicionarParametro("NOMERELATORIO", "EFETIVAÇÃO DE LEVANTAMENTOS DE BENS MÓVEIS N°:" + loteEfetivacaoLevantamentoBem.getCodigo());
            dto.adicionarParametro("DATA_OPERACAO", DataUtil.getDataFormatada(getSistemaFacade().getDataOperacao()));
            dto.adicionarParametro("CONDICAO", " AND EF.LOTE_ID = " + loteEfetivacaoLevantamentoBem.getId());
            dto.adicionarParametro("FILTROS", filtros);
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("administrativo/levantamento-bem-movel-efetivado/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório", ex);
            FacesUtil.addErroAoGerarRelatorio("Ocorreu um erro ao gerar o relatório: " + ex.getMessage());
        }
    }

    private String getNomeRelatorio() {
        return "RELATÓRIO DE LEVANTAMENTOS DE BENS MÓVEIS EFETIVADOS";
    }

    private String montarCondicaoRelatorioLevantamentosEfetivados(StringBuffer filtros) {
        StringBuffer sql = new StringBuffer();

        if (getFiltro().getHierarquiaAdm() != null && getFiltro().getHierarquiaAdm() != null) {
            sql.append(" and VWADM.CODIGO LIKE '").append(getFiltro().getHierarquiaAdm().getCodigoSemZerosFinais()).append("%' ");
            filtros.append("Unidade Organizacional: ").append(getFiltro().getHierarquiaAdm().toString()).append(". ");
        }
        if (getFiltro().getHierarquiaOrc() != null && getFiltro().getHierarquiaOrc().getCodigo() != null) {
            sql.append(" and VWORC.CODIGO = '").append(getFiltro().getHierarquiaOrc().getCodigo()).append("' ");
            filtros.append("Unidade Orçametária: ").append(getFiltro().getHierarquiaOrc().toString()).append(". ");
        }
        if (estadoConservacaoBem != null) {
            sql.append(" and lev.estadoconservacaobem = '").append(estadoConservacaoBem.name()).append("' ");
            filtros.append("Estado de Conservação: ").append(estadoConservacaoBem.getDescricao()).append(". ");
        }
        if (grupoBem != null) {
            sql.append(" and ESTADO.GRUPOBEM_ID = ").append(grupoBem.getId()).append(" ");
            filtros.append("Grupo Patrimonial: ").append(grupoBem.toString()).append(". ");
        }
        sql.append("      AND (EF.ID = (SELECT MAX(EFT.ID) " +
            "                  FROM EFETIVACAOLEVANTAMENTOBEM EFT " +
            "               WHERE EFT.LEVANTAMENTO_ID = LEV.ID)) " +
            "      AND EV.SITUACAOEVENTOBEM = '").append(SituacaoEventoBem.FINALIZADO.name()).append("' ");

        filtros.append(" agrupado por unidade organizacional e ordenado por patrimônio.");
        return sql.toString();
    }

    private void validarFiltrosRelatorioLevantamentosEfetivados() {
        ValidacaoException ve = new ValidacaoException();
        if (getFiltro().getHierarquiaAdm() == null && getFiltro().getHierarquiaOrc() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Administrativa ou Unidade Orçamentária deve ser informado.");
        }
        ve.lancarException();
    }

    public List<SelectItem> retornarHierarquiasOrcamentarias() {
        List<SelectItem> toReturn = new ArrayList<>();
        if (getFiltro().getHierarquiaAdm() != null && getFiltro().getHierarquiaAdm().getSubordinada() != null) {
            toReturn.add(new SelectItem(null, ""));
            for (HierarquiaOrganizacional obj : getHierarquiaOrganizacionalFacade().retornaHierarquiaOrcamentariaPorUnidadeAdministrativa(getFiltro().getHierarquiaAdm().getSubordinada(), getSistemaFacade().getDataOperacao())) {
                toReturn.add(new SelectItem(obj, obj.toString()));
            }
            return toReturn;
        }

        toReturn.add(new SelectItem(null, ""));
        for (HierarquiaOrganizacional obj : getHierarquiaOrganizacionalFacade().retornaHierarquiaOrganizacionalOrcamentariaOndeUsuarioEhGestorPatrimonio(getSistemaFacade().getUsuarioCorrente(), getSistemaFacade().getDataOperacao())) {
            toReturn.add(new SelectItem(obj, obj.toString()));
        }
        return toReturn;
    }

    public EstadoConservacaoBem getEstadoConservacaoBem() {
        return estadoConservacaoBem;
    }

    public void setEstadoConservacaoBem(EstadoConservacaoBem estadoConservacaoBem) {
        this.estadoConservacaoBem = estadoConservacaoBem;
    }

    public GrupoBem getGrupoBem() {
        return grupoBem;
    }

    public void setGrupoBem(GrupoBem grupoBem) {
        this.grupoBem = grupoBem;
    }
}
