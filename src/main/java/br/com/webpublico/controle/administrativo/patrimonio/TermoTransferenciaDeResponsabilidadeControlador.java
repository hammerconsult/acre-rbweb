package br.com.webpublico.controle.administrativo.patrimonio;

import br.com.webpublico.controle.RelatorioPatrimonioControlador;
import br.com.webpublico.enums.EstadoConservacaoBem;
import br.com.webpublico.enums.TipoBem;
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

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Created by Desenvolvimento on 13/03/2017.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoTermoTransferenciaDeResponsabilidade",
        pattern = "/termo-transferencia-responsabilidade-bens/",
        viewId = "/faces/administrativo/patrimonio/inventario/invertariotransferencia/inventariotransferencia.xhtml")})
public class TermoTransferenciaDeResponsabilidadeControlador extends RelatorioPatrimonioControlador {
    private boolean somenteBensComSaldo = Boolean.TRUE;

    @URLAction(mappingId = "novoTermoTransferenciaDeResponsabilidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)

    public void novoTermoTransferenciaDeResponsabilidade() {
        limparCampos();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarFiltrosTermoDeResponsabilidade();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "Patrimônio");
            dto.adicionarParametro("SECRETARIA", montaNomeSecretaria());
            dto.adicionarParametro("NOMERELATORIO", "TERMO DE RESPONSABILIDADE");
            dto.adicionarParametro("CONDICAO", montarCondicaoTermoDeTransferenciaDeResponsabilidade());
            dto.adicionarParametro("DATAOPERACAO", DataUtil.getDataFormatada(getSistemaFacade().getDataOperacao()));
            dto.setNomeRelatorio("TERMO-DE-RESPONSABILIDADE");
            dto.setApi("administrativo/termo-transferencia-responsabilidade/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio("Ocorreu um erro ao gerar o relatório: " + ex.getMessage());
        }
    }

    private void validarFiltrosTermoDeResponsabilidade() {
        ValidacaoException ve = new ValidacaoException();
        if (this.getHierarquiaOrganizacional() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Organizacional deve ser informado.");
        }
        ve.lancarException();
    }

    private String montarCondicaoTermoDeTransferenciaDeResponsabilidade() {
        StringBuffer sql = new StringBuffer();
        sql.append(" where grupo.tipobem = '").append(TipoBem.MOVEIS.name()).append("' ");
        if (getHierarquiaOrganizacional() != null && getHierarquiaOrganizacional().getCodigo() != null) {
            sql.append(" and UND.ID = ").append(getHierarquiaOrganizacional().getSubordinada().getId());
        }
        sql.append(" and estado.estadobem <> '").append(EstadoConservacaoBem.BAIXADO.name()).append("' ");

        if (somenteBensComSaldo) {
            sql.append(" and estado.valororiginal > 0 ");
        }
        return sql.toString();
    }

    public boolean isSomenteBensComSaldo() {
        return somenteBensComSaldo;
    }

    public void setSomenteBensComSaldo(boolean somenteBensComSaldo) {
        this.somenteBensComSaldo = somenteBensComSaldo;
    }
}
