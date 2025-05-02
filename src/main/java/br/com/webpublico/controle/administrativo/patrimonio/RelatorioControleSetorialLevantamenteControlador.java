package br.com.webpublico.controle.administrativo.patrimonio;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
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
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Desenvolvimento on 13/03/2017.
 */

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioControleSetorialLevantamente",
        pattern = "/relatorio-controle-setorial-patrimonio-levantamento/",
        viewId = "/faces/administrativo/patrimonio/relatorios/relatoriocontrolesetoriallevantamento.xhtml")})
public class RelatorioControleSetorialLevantamenteControlador extends RelatorioPatrimonioSuperControlador {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioDeBensAdquiridosControlador.class);
    private Date dtReferencia;
    private HierarquiaOrganizacional hierarquiaOrganizacionalOrcamentaria;

    @URLAction(mappingId = "novoRelatorioControleSetorialLevantamente", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoRelatorioControleSetorialLevantamente() {
        limparCampos();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarRelatorioControleSetorialLevantamento();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            StringBuffer filtros = new StringBuffer();
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "PATRIMÔNIO");
            dto.adicionarParametro("NOMERELATORIO", getNomeRelatorio());
            dto.adicionarParametro("CONDICAO", montarCondicaoRelatorioControleSetorialPatrimonioLevantamento(filtros));
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE " + getSistemaFacade().getMunicipio().toUpperCase());
            dto.adicionarParametro("DATAREFERENCIA", DataUtil.getDataFormatada(getDtReferencia()));
            dto.adicionarParametro("DATASISTEMA", DataUtil.getDataFormatada(getSistemaFacade().getDataOperacao()));
            dto.adicionarParametro("FILTROS", filtros.toString());
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("administrativo/controle-setorial-levantamento/");
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

    private String getNomeRelatorio() {
        return "RELATÓRIO DE LEVANTAMENTO DE BENS MÓVEIS PARA CONTROLE SETORIAL DE PATRIMÔNIO";
    }

    public List<SelectItem> getHierarquiasOrcamentarias() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (HierarquiaOrganizacional obj : getHierarquiaOrganizacionalFacade().retornaHierarquiaOrganizacionalOrcamentariaOndeUsuarioEhGestorPatrimonio(getSistemaFacade().getUsuarioCorrente(), getSistemaFacade().getDataOperacao())) {
            toReturn.add(new SelectItem(obj, obj.toString()));
        }
        return toReturn;
    }

    private void validarRelatorioControleSetorialLevantamento() {
        ValidacaoException ve = new ValidacaoException();
        if (dtReferencia == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Referência deve ser informado.");
        }
        ve.lancarException();
    }

    private String montarCondicaoRelatorioControleSetorialPatrimonioLevantamento(StringBuffer filtros) {
        StringBuilder sql = new StringBuilder();
        filtros.append("Data de referência: ").append(DataUtil.getDataFormatada(dtReferencia)).append(". ");
        if (hierarquiaOrganizacionalOrcamentaria != null && hierarquiaOrganizacionalOrcamentaria.getCodigo() != null) {
            sql.append(" and vw.codigo = '").append(hierarquiaOrganizacionalOrcamentaria.getCodigo()).append("' ");
            filtros.append(" Unidade Orçamentária: ").append(hierarquiaOrganizacionalOrcamentaria.toString()).append(". ");
        }
        return sql.toString();
    }

    public void limparCampos() {
        dtReferencia = getSistemaFacade().getDataOperacao();
        hierarquiaOrganizacionalOrcamentaria = null;
    }

    public Date getDtReferencia() {
        return dtReferencia;
    }

    public void setDtReferencia(Date dtReferencia) {
        this.dtReferencia = dtReferencia;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalOrcamentaria() {
        return hierarquiaOrganizacionalOrcamentaria;
    }

    public void setHierarquiaOrganizacionalOrcamentaria(HierarquiaOrganizacional hierarquiaOrganizacionalOrcamentaria) {
        this.hierarquiaOrganizacionalOrcamentaria = hierarquiaOrganizacionalOrcamentaria;
    }
}
