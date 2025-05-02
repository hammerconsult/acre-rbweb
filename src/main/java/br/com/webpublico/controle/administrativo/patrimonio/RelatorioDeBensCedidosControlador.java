package br.com.webpublico.controle.administrativo.patrimonio;

/**
 * Created by Desenvolvimento on 13/03/2017.
 */

import br.com.webpublico.controle.RelatorioPatrimonioControlador;
import br.com.webpublico.enums.TipoCessao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoRelatorioBensCedidos",
                pattern = "/relatorio-bens-cedidos/novo/",
                viewId = "/faces/administrativo/patrimonio/relatorios/relatoriobenscedidos.xhtml")})
public class RelatorioDeBensCedidosControlador extends RelatorioPatrimonioControlador {

    private TipoCessao tipoCessao;

    @URLAction(mappingId = "novoRelatorioBensCedidos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoRelatorioBensCedidos() {
        limparCampos();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarFiltrosRelatorioBensCedidos();
            String filtros = "Unidade de origem: " + getHierarquiaOrganizacional().toString() + (tipoCessao!= null ? " - Tipo de Cessão: " + tipoCessao.getDescricao() : "");
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MUNICIPIO", montarNomeDoMunicipio());
            dto.adicionarParametro("SECRETARIA", montaNomeSecretaria());
            dto.adicionarParametro("MODULO", "Patrimônio");
            dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DE BENS CEDIDOS");
            dto.adicionarParametro("CONDICAO", tipoCessao != null ?  " AND LOTECESSAO.TIPOCESSAO = '"+tipoCessao.name()+"' " : " ");
            dto.adicionarParametro("ID", getHierarquiaOrganizacional().getSubordinada().getId());
            dto.adicionarParametro("FILTROS", filtros);
            dto.setNomeRelatorio("RELATÓRIO-DE-BENS-CEDIDOS");
            dto.setApi("administrativo/bens-cedidos/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e){
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio("Ocorreu um erro ao gerar o relatório: " + ex.getMessage());
        }
    }

    private void validarFiltrosRelatorioBensCedidos() {
        ValidacaoException ve = new ValidacaoException();
        if (this.getHierarquiaOrganizacional() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Organizacional deve ser informado.");
        }
        ve.lancarException();
    }

    public TipoCessao getTipoCessao() {
        return tipoCessao;
    }

    public void setTipoCessao(TipoCessao tipoCessao) {
        this.tipoCessao = tipoCessao;
    }
}
