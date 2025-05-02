package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controlerelatorio.administrativo.RelatorioMaterialSuperControlador;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidadesauxiliares.administrativo.relatorio.demonstrativocompras.DemonstrativoMovimentacoesAlmoxarifado;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.MovimentoEstoqueFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Desenvolvimento on 22/02/2017.
 */

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoDemonstrativoAlmoxarifado", pattern = "/demonstrativo-da-movimentacao-do-almoxarifado/", viewId = "/faces/administrativo/materiais/relatorios/demonstrativo-da-movimentacao-do-almoxarifado.xhtml")})
public class DemonstrativoMovimentacaoAlmoxarifado extends RelatorioMaterialSuperControlador {

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacionalAdministrativa(String parte) {
        if (getDataFinal() != null) {
            return getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalTipo(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), getDataFinal());
        }
        return getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalTipo(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), getSistemaFacade().getDataOperacao());
    }
    public void gerarDemonstrativo() {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome());
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "Materiais");
            dto.adicionarParametro("MUNICIPIO", montarNomeDoMunicipio());
            dto.adicionarParametro("NOMERELATORIO", getNomeRelatorio());
            dto.adicionarParametro("APRESENTACAO", getApresentacaoRelatorio().name());
            dto.adicionarParametro("dataInicial", DataUtil.getDataFormatada(getDataInicial()));
            dto.adicionarParametro("dataFinal", DataUtil.getDataFormatada(getDataFinal()));
            dto.adicionarParametro("condicao", montarCondicoes());
            dto.adicionarParametro("FILTROS", getFiltros().toString());
            dto.adicionarParametro("apresentacaoRelatorioDTO", getApresentacaoRelatorio().getToDto());
            dto.adicionarParametro("tipoHierarquiaOrganizacionalDTO", getTipoHierarquiaOrganizacional().getToDto());
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("administrativo/movimentacao-almoxarifado/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio("Ocorreu um erro ao gerar o relatório: " + ex.getMessage());
        }
    }

    private String getNomeRelatorio() {
        return "Demonstrativo da Movimentação do Almoxarifado";
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();

        if (getDataInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Inicial deve ser informado.");
        }

        if (getDataFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Final deve ser informado.");
        }

        if(getDataInicial() != null && getDataFinal() != null){
            if(getDataInicial().after(getDataFinal())){
                ve.adicionarMensagemDeCampoObrigatorio("A data final deve ser posterior a data inicial.");
            }
        }

        ve.lancarException();
    }

    private String montarCondicoes() {
        limparCamposFiltros();
        getFiltros().append("Critérios Utilizados: ").append("\n");
        getFiltros().append("Apresentação: ").append(getApresentacaoRelatorio().getDescricao()).append("\n");
        if (getHierarquiaAdministrativa() != null) {
            getCondicaoWhere().append(" AND ADM.SUBORDINADA_ID = ").append(getHierarquiaAdministrativa().getSubordinada().getId());
            getFiltros().append("Unidade Administrativa: ").append(getHierarquiaAdministrativa().getSubordinada().getDescricao()).append("\n");
        }
        if (getHierarquiaOrcamentaria() != null) {
            getCondicaoWhere().append(" AND ORC.SUBORDINADA_ID = ").append(getHierarquiaOrcamentaria().getSubordinada().getId());
            getFiltros().append("Unidade Orçamentária: ").append(getHierarquiaOrcamentaria().getSubordinada().getDescricao()).append("\n");
        }

        if (getLocalEstoque() != null) {
            getCondicaoWhere().append(" AND MOVIMENTOS.LOCAL_CODIGO LIKE '").append(getLocalEstoque().getCodigoSemZerosFinais()).append("%' ");
            getFiltros().append("Local de Estoque: ").append(getLocalEstoque().getCodigo()).append(" - ").append(getLocalEstoque().getDescricao()).append("\n");
            if (getTipoEstoque() == null) {
                getFiltros().append("Tipo de Estoque: ").append(getLocalEstoque().getTipoEstoque().getDescricao()).append("\n");
            }
        }

        if (getTipoEstoque() != null) {
            getCondicaoWhere().append(" AND MOVIMENTOS.TIPOESTOQUE =  ").append("'" + getTipoEstoque().name() + "'");
            getFiltros().append("Tipo de Estoque: ").append(getTipoEstoque().getDescricao()).append("\n");
        }

        if (getMaterial() != null) {
            getCondicaoWhere().append(" AND MOVIMENTOS.MATERIAL_ID = ").append(getMaterial().getId());
            getFiltros().append("Nº de Registro: ").append(getMaterial().getCodigo()).append("\n");
            getFiltros().append("Especificação: ").append(getMaterial().getDescricao()).append("\n");
            getFiltros().append("Grupo Objeto de Compra: ").append(getMaterial().getObjetoCompra().getGrupoObjetoCompra()).append("\n");
            if (getGrupoMaterial() == null) {
                getFiltros().append("Grupo Material: ").append(getMaterial().getGrupo()).append("\n");
            }
        }

        if (getGrupoMaterial() != null) {
            getCondicaoWhere().append(" AND MOVIMENTOS.GRUPO_ID = ").append(getGrupoMaterial().getId());
            getFiltros().append("Grupo de Material: ").append(getGrupoMaterial()).append("\n");
        }
        getFiltros().append("Período: De: ").append(DataUtil.getDataFormatada(getDataInicial())).append(" Até: ").append(DataUtil.getDataFormatada(getDataFinal())).append("\n");
        return getCondicaoWhere().toString();
    }
}
