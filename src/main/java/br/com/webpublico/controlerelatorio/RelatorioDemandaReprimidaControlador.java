package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.GrupoMaterial;
import br.com.webpublico.entidades.LocalEstoque;
import br.com.webpublico.entidades.RequisicaoMaterial;
import br.com.webpublico.entidadesauxiliares.FiltroRelatorioMateriais;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;


@ViewScoped
@ManagedBean
@URLMappings(mappings = {
    @URLMapping(id = "novoDemandaReprimida", pattern = "/relatorio-demanda-reprimida/novo/", viewId = "/faces/administrativo/materiais/relatorios/relatoriodemandareprimida.xhtml")
})
public class RelatorioDemandaReprimidaControlador implements Serializable {

    @EJB
    private GrupoMaterialFacade grupoMaterialFacade;
    @EJB
    private MaterialFacade materialFacade;
    @EJB
    private RequisicaoMaterialFacade requisicaoMaterialFacade;
    @EJB
    private LocalEstoqueFacade localEstoqueFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private FiltroRelatorioMateriais filtro;

    @URLAction(mappingId = "novoDemandaReprimida", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        filtro = new FiltroRelatorioMateriais();
    }

    public void gerarRelatorio() {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "Materiais");
            dto.adicionarParametro("NOMERELATORIO", getNomeRelatorio());
            dto.adicionarParametro("dataReferencia", DataUtil.getDataFormatada(filtro.getDataFinal()));
            dto.adicionarParametro("condicao", montarCondicaoSql());
            dto.adicionarParametro("FILTROS", filtro.getFiltros());
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("administrativo/demanda-reprimida/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public List<GrupoMaterial> completarGrupoMaterial(String parte) {
        return grupoMaterialFacade.listaFiltrando(parte.trim());
    }

    public List<RequisicaoMaterial> completaRequisicaoMaterial(String parte) {
        if (filtro.getDataFinal() != null && filtro.getDataInicial() != null) {
            return requisicaoMaterialFacade.buscarRequisicaoPorPeriodo(parte.trim(), filtro.getDataInicial(), filtro.getDataFinal());
        }
        return Lists.newArrayList();
    }

    public List<LocalEstoque> completaLocalEstoque(String parte) {
        return localEstoqueFacade.buscarPorCodigoOrDescricao(parte.trim());
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (filtro.getHierarquiaAdministrativa() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Hierarquia Organizacional deve ser informado.");
        }
        if (filtro.getDataInicial() == null || filtro.getDataFinal() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo período (Datas) deve ser informado.");
        }

        ve.lancarException();
        if (filtro.getDataFinal().before(filtro.getDataInicial())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O  campo data final deve ser posterior ao campo da inicial.");
        }
        ve.lancarException();
    }

    public String montarCondicaoSql() {
        StringBuilder condicao = new StringBuilder();
        StringBuilder filtros = new  StringBuilder();
        if (filtro.getMaterial() != null) {
            condicao.append(" and m.id = ").append(filtro.getMaterial().getId());
            filtros.append(" Material: ").append(filtro.getMaterial().toString());
        }

        if (filtro.getRequisicaoMaterial() != null) {
            condicao.append(" and rm.id = ").append(filtro.getRequisicaoMaterial().getId());
            filtros.append(" Requisição de Material: ").append(filtro.getRequisicaoMaterial().toString());
        }

        if (filtro.getLocalEstoque() != null) {
            condicao.append(" and LOCALESTOQUE.id = ").append(filtro.getLocalEstoque().getId());
            filtros.append(" Local de Estoque: ").append(filtro.getLocalEstoque().toString());
        }
        condicao.append(" and TO_CHAR(RM.DATAREQUISICAO, 'dd/MM/yyyy') >= '").append(DataUtil.getDataFormatada(filtro.getDataInicial())).append("'")
            .append(" and TO_CHAR(RM.DATAREQUISICAO, 'dd/MM/yyyy') <= '").append(DataUtil.getDataFormatada(filtro.getDataFinal())).append("' ");
        filtros.append(" Período de ").append(DataUtil.getDataFormatada(filtro.getDataInicial())).append(" até ").append(DataUtil.getDataFormatada(filtro.getDataFinal()));

        if (filtro.getHierarquiaAdministrativa() != null) {
            condicao.append(" and vw.codigo LIKE '").append(filtro.getHierarquiaAdministrativa().getCodigoSemZerosFinais()).append("%'");
            filtros.append(" Unidade Organizacional: ").append(filtro.getHierarquiaAdministrativa().toString());
        }
        if(filtro.getGrupoMaterial() != null){
            condicao.append(" and gm.id = ").append(filtro.getGrupoMaterial().getId());
            filtros.append(" Grupo Material: ").append(filtro.getGrupoMaterial().toString());
        }
        filtro.setFiltros(filtros.toString());
        return condicao.toString();
    }

    private String getNomeRelatorio() {
        return "RELATÓRIO DE DEMANDA REPRIMIDA";
    }

    public void cancelarLimparCampos() {
        Util.limparCampos(this);
    }

    public FiltroRelatorioMateriais getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroRelatorioMateriais filtro) {
        this.filtro = filtro;
    }
}
