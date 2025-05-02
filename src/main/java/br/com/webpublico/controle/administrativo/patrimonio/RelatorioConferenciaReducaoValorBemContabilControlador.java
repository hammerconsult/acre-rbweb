package br.com.webpublico.controle.administrativo.patrimonio;


import br.com.webpublico.entidades.LoteReducaoValorBem;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoReducaoValorBem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ReducaoValorBemFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioReducaoValorBemContabil",
        pattern = "/conferencia-reducao-valor-bem-contabil/",
        viewId = "/faces/administrativo/patrimonio/relatorios/conferencia-reducao-valor-bem-contabil.xhtml")})

public class RelatorioConferenciaReducaoValorBemContabilControlador extends RelatorioPatrimonioSuperControlador {

    @EJB
    private ReducaoValorBemFacade reducaoValorBemFacade;
    private LoteReducaoValorBem reducaoValorBem;

    @URLAction(mappingId = "novoRelatorioReducaoValorBemContabil", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoRelatorio() {
        novoFiltroRelatorio();
        getFiltro().setDataReferencia(getSistemaFacade().getDataOperacao());
        getFiltro().setTipoReducaoValorBem(TipoReducaoValorBem.DEPRECIACAO);
    }

    public void gerarRelatorio(String tipoRelatorioExtensao, LoteReducaoValorBem reducaoValorBem) {
        try {
            novoFiltroRelatorio();
            getFiltro().setTipoReducaoValorBem(reducaoValorBem.getTipoReducao());
            this.reducaoValorBem = reducaoValorBem;
            validarFiltros();
            RelatorioDTO dto = montarParametros();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void gerarRelatorio() {
        try {
            validarFiltros();
            RelatorioDTO dto = montarParametros();
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public RelatorioDTO montarParametros() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.adicionarParametro("MUNICIPIO", montarNomeMunicipio());
        dto.adicionarParametro("IMAGEM",FacesUtil.geraUrlImagemDir() + "img/Brasao_de_Rio_Branco.gif");
        dto.setNomeParametroSubreportDir("SUBREPORT_DIR");
        dto.setNomeRelatorio("RELATÓRIO DE CONFERÊNCIA DA " + getFiltro().getTipoReducaoValorBem().getDescricao().toUpperCase() + " ADMINISTRATIVO/CONTÁBIL");
        dto.adicionarParametro("NOMERELATORIO", dto.getNomeRelatorio());
        dto.adicionarParametro("MODULO", "ADMINISTRATIVO");
        dto.adicionarParametro("TIPO_REDUCAO", getFiltro().getTipoReducaoValorBem().getDescricao());
        montarClausulaWhere(dto);
        dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.setApi("administrativo/conferencia-reducao-valor-bem-contabil/");
        return dto;
    }

    private void montarClausulaWhere(RelatorioDTO dto) {
        StringBuilder where = new StringBuilder();

        where.append(" where grupo.tipobem  = '").append(TipoBem.MOVEIS.name()).append("' ");

        if (reducaoValorBem != null) {
            where.append(" and red.id = ").append(reducaoValorBem.getId());
        }
        if (getFiltro().getGrupoBem() != null) {
            where.append(" and grupo.id = ").append(getFiltro().getGrupoBem().getId());
        }
        atribuirDataReferencia();
        dto.adicionarParametro("dataReferencia",getFiltro().getDataReferencia());
        dto.adicionarParametro("complementoQuery", where.toString());
        dto.adicionarParametro("situacao", reducaoValorBem.getSituacao().name());

    }

    public List<LoteReducaoValorBem> completarReducaoValorBem(String parte) {
        return reducaoValorBemFacade.buscarLoteReducaoValorBem(parte.trim());
    }

    private void validarFiltros() {
        ValidacaoException ve = new ValidacaoException();
        if (reducaoValorBem == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo " + getFiltro().getTipoReducaoValorBem().getDescricao() + " deve ser informado.");
        }
        ve.lancarException();
    }

    public void atribuirDataReferencia() {
        if (reducaoValorBem != null) {
            getFiltro().setDataReferencia(reducaoValorBem.getData());
        }
    }

    public LoteReducaoValorBem getReducaoValorBem() {
        return reducaoValorBem;
    }

    public void setReducaoValorBem(LoteReducaoValorBem reducaoValorBem) {
        this.reducaoValorBem = reducaoValorBem;
    }
}
