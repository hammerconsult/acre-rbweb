package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Fase;
import br.com.webpublico.entidades.UnidadeGestora;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.enums.SituacaoPeriodoFase;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 22/10/14
 * Time: 14:04
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMapping(id = "relatorio-de-fases", pattern = "/relatorio/fases/", viewId = "/faces/financeiro/relatorio/relatorio-de-fases.xhtml")
public class RelatorioDeFasesControlador extends RelatorioContabilSuperControlador implements Serializable {

    private Fase fase;
    private UsuarioSistema usuarioSistema;
    private SituacaoPeriodoFase situacaoPeriodoFase;

    @URLAction(mappingId = "relatorio-de-fases", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        limparCamposGeral();
        fase = null;
        usuarioSistema = null;
        situacaoPeriodoFase = null;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarDataDeReferencia();
            montaFiltroSql();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("USER", getNomeUsuarioLogado(), false);
            dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaControlador().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("complemento", sql);
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("FILTROS", filtros);
            dto.adicionarParametro("FILTRO_UG", filtroUG);
            dto.adicionarParametro("FILTRO_DATA", "Referente à: " + DataUtil.getDataFormatada(dataReferencia));
            dto.setNomeRelatorio("RELATORIO-FASES");
            dto.setApi("contabil/fases/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar o relatório: {}", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    @Override
    public String getNomeRelatorio() {
        return "FASES";
    }

    private void montaFiltroSql() {
        filtros = "";
        filtroUG = "";
        sql = " and pf.exercicio_id = " + getSistemaControlador().getExercicioCorrente().getId();
        sql += " and TO_DATE('" + DataUtil.getDataFormatada(dataReferencia) + "', 'dd/MM/yyyy') BETWEEN trunc(PFU.INICIOVIGENCIA) and coalesce(trunc(PFU.FIMVIGENCIA), TO_DATE('" + DataUtil.getDataFormatada(dataReferencia) + "','dd/MM/yyyy'))";

        if (fase != null) {
            filtros += " Fase: " + fase + " -";
            sql += " and pf.fase_id = " + fase.getId();
        }
        if (usuarioSistema != null) {
            filtros += " Usuário: " + usuarioSistema + " -";
            sql += " and pfusu.usuariosistema_id = " + usuarioSistema.getId();
        }
        if (situacaoPeriodoFase != null) {
            filtros += " Situação: " + situacaoPeriodoFase.getDescricao() + " -";
            sql += " and pfu.situacaoperiodofase like '" + situacaoPeriodoFase.name() + "'";
        }
        sqlUnidades();
        atualizaFiltrosGerais();
    }

    public List<SelectItem> getSituacoesPeriodoFase() {
        return Util.getListSelectItem(SituacaoPeriodoFase.values());
    }

    public List<UsuarioSistema> completarUsuarioSistemaPeloNomePessoaFisica(String filtro) {
        return relatorioContabilSuperFacade.getUsuarioSistemaFacade().completarUsuarioSistemaPeloNomePessoaFisica(filtro);
    }

    public Fase getFase() {
        return fase;
    }

    public void setFase(Fase fase) {
        this.fase = fase;
    }

    public UnidadeGestora getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(UnidadeGestora unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public Date getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(Date dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    public SituacaoPeriodoFase getSituacaoPeriodoFase() {
        return situacaoPeriodoFase;
    }

    public void setSituacaoPeriodoFase(SituacaoPeriodoFase situacaoPeriodoFase) {
        this.situacaoPeriodoFase = situacaoPeriodoFase;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }
}

