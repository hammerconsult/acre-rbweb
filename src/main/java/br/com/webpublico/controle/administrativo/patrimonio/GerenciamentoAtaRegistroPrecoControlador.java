package br.com.webpublico.controle.administrativo.patrimonio;

import br.com.webpublico.entidades.AtaRegistroPreco;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.SolicitacaoMaterialExterno;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.AtaRegistroPrecoFacade;
import br.com.webpublico.negocios.LicitacaoFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.SolicitacaoMaterialExternoFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoGerenciamentoAtaRegistroPreco",
        pattern = "/relatorio-de-gerenciamento-ata-registro-preco/",
        viewId = "/faces/administrativo/patrimonio/relatorios/gerenciamento-ata-registro-preco.xhtml")})
public class GerenciamentoAtaRegistroPrecoControlador implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private AtaRegistroPrecoFacade ataRegistroPrecoFacade;
    @EJB
    private SolicitacaoMaterialExternoFacade solicitacaoMaterialExternoFacade;
    @EJB
    private LicitacaoFacade licitacaoFacade;
    private AtaRegistroPreco ataRegistroPreco;
    private SolicitacaoMaterialExterno solicitacaoMaterialExterno;
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    @URLAction(mappingId = "novoGerenciamentoAtaRegistroPreco", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoRelatorio() {
        setAtaRegistroPreco(null);
        setSolicitacaoMaterialExterno(null);
        setHierarquiaOrganizacional(null);
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarFiltros();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE " + sistemaFacade.getMunicipio().toUpperCase());
            dto.setNomeParametroBrasao("BRASAO");
            dto.setNomeParametroSubreportDir("SUBREPORT_DIR");
            dto.setNomeRelatorio("RELATÓRIO DE GERENCIAMENTO DA ATA REGISTRO DE PREÇO");
            dto.adicionarParametro("NOME_RELATORIO", dto.getNomeRelatorio());
            dto.adicionarParametro("MODULO", "ADMINISTRATIVO");
            dto.adicionarParametro("UNIDADE_GERENCIADORA", getUnidadeGerenciadoraAta());
            montarClausulaWhere(dto);
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setApi("administrativo/gerenciamento-ata-registro-preco/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public String getUnidadeGerenciadoraAta() {
        HierarquiaOrganizacional hierarquia = licitacaoFacade.recuperarUnidadeVigenteLicitacao(ataRegistroPreco.getLicitacao());
        if (hierarquia != null) {
            return hierarquia.getCodigo() + " - " + hierarquia.getDescricao();
        }
        return " ";
    }

    private void montarClausulaWhere(RelatorioDTO dto) {
        StringBuilder condicaoAta = new StringBuilder();
        StringBuilder filtros = new StringBuilder();

        condicaoAta.append(" where ata.id = ").append(ataRegistroPreco.getId());
        filtros.append("Ata Registro de Preço: ").append(ataRegistroPreco.toString()).append(", ");

        if (solicitacaoMaterialExterno != null) {
            condicaoAta.append(" and sol.id = ").append(solicitacaoMaterialExterno.getId());
            filtros.append("Solicitação a Ata: ").append(solicitacaoMaterialExterno.toString()).append(", ");
        }
        StringBuilder condicaoUnidadeSolicitacao = new StringBuilder(condicaoAta);
        if (hierarquiaOrganizacional != null) {
            condicaoUnidadeSolicitacao.append(" and vw.subordinada_id = ").append(hierarquiaOrganizacional.getSubordinada().getId());
            filtros.append("Unidade: ").append(hierarquiaOrganizacional.toString());
        }
        dto.adicionarParametro("dataReferencia", DataUtil.dataSemHorario(sistemaFacade.getDataOperacao()));
        dto.adicionarParametro("condicaoAta", condicaoAta.toString());
        dto.adicionarParametro("condicaoSolicitacaoAta", condicaoUnidadeSolicitacao.toString());
        dto.adicionarParametro("FILTROS", filtros.substring(0, filtros.length() - 2));
    }

    private void validarFiltros() {
        ValidacaoException ve = new ValidacaoException();
        if (ataRegistroPreco == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo ata registro de preço deve ser informado.");
        }
        ve.lancarException();
    }

    public List<AtaRegistroPreco> completarAtaRegistroPreco(String parte) {
        return ataRegistroPrecoFacade.buscarAtaRegistroPreco(parte.trim());
    }

    public List<SolicitacaoMaterialExterno> completarSolicitacao(String parte) {
        if (ataRegistroPreco != null) {
            return solicitacaoMaterialExternoFacade.buscarSolicitacaoInternaPorAtaRegistroPreco(parte.trim(), ataRegistroPreco);
        }
        FacesUtil.addAtencao("As solicitações são filtradas por ata registro de preço.");
        return Lists.newArrayList();
    }

    public AtaRegistroPreco getAtaRegistroPreco() {
        return ataRegistroPreco;
    }

    public void setAtaRegistroPreco(AtaRegistroPreco ataRegistroPreco) {
        this.ataRegistroPreco = ataRegistroPreco;
    }


    public SolicitacaoMaterialExterno getSolicitacaoMaterialExterno() {
        return solicitacaoMaterialExterno;
    }

    public void setSolicitacaoMaterialExterno(SolicitacaoMaterialExterno solicitacaoMaterialExterno) {
        this.solicitacaoMaterialExterno = solicitacaoMaterialExterno;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }
}
