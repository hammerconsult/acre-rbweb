package br.com.webpublico.controlerelatorio.rh;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoPCS;
import br.com.webpublico.enums.rh.relatorios.TipoRelatorioControleVagas;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.rh.RelatorioControleQuadroFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.base.Strings;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by zaca on 05/04/17.
 */
@ViewScoped
@ManagedBean
@URLMapping(id = "relatorioControleQuadro", pattern = "/rh/relatorio/controle-quadro/", viewId = "/faces/rh/relatorios/relatorio-controle-quadro.xhtml")
public class RelatorioControleQuadroControlador {

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private RelatorioControleQuadroFacade relatorioControleVagasFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    protected static final Logger logger = LoggerFactory.getLogger(RelatorioControleQuadroControlador.class);
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private Boolean isTodosOrgaos;
    private Boolean isTodosCargos;
    private Boolean detalhar;
    private TipoRelatorioControleVagas tipoRelatorio;
    private TipoPCS tipoCargo;
    private List<HierarquiaOrganizacional> unidades;
    private List<HierarquiaOrganizacional> unidadesSelecionadas;

    public RelatorioControleQuadroControlador() {
    }

    @URLAction(mappingId = "relatorioControleQuadro", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        limparCampos();
    }

    public void limparCampos() {
        this.isTodosOrgaos = Boolean.TRUE;
        this.isTodosCargos = Boolean.TRUE;
        this.detalhar = Boolean.FALSE;
        this.unidades = new ArrayList<>();
        this.tipoCargo = null;
        this.tipoRelatorio = null;
        this.unidadesSelecionadas = new ArrayList<>();
        this.hierarquiaOrganizacional = null;
    }

    private String buscarIdsDasUnidades() {
        String idUnidades = "";

        for (HierarquiaOrganizacional unidade : unidadesSelecionadas) {
            idUnidades += unidade.getSubordinada().getId() + ",";
        }

        return !Strings.isNullOrEmpty(idUnidades) ? idUnidades.substring(0, idUnidades.length() - 1) : "";
    }

    public void gerarRelatorio(String tipoRelatorio) {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorio));
            dto.setApi("rh/relatorio-controle-quadro/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
            logger.error("Erro ao tentar gerar relatório. ", e);
        }
    }

    private RelatorioDTO montarRelatorioDTO() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.setNomeParametroBrasao("BRASAO");
        dto.setNomeRelatorio("RELATÓRIO CONTROLE DE QUADRO");
        dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getLogin(), false);
        dto.adicionarParametro("MODULO", "Recursos Humanos");
        dto.adicionarParametro("FILTROS", montarFiltros());
        dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO CONTROLE DE QUADRO");
        dto.adicionarParametro("PREFEITURA", "PREFEITURA MUNICIPAL DE RIO BRANCO");
        dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO E GESTÃO DE PESSOAS");
        dto.adicionarParametro("HIERARQUIA_ORGANIZACIONAL_CODIGO", hierarquiaOrganizacional != null ? hierarquiaOrganizacional.getCodigoSemZerosFinais() : null);
        dto.adicionarParametro("DATA_OPERACAO", sistemaFacade.getDataOperacao());
        dto.adicionarParametro("TIPO_CARGO", tipoCargo != null ? tipoCargo.getToDto() : null);
        dto.adicionarParametro("ORGAO_DETALHADO", isOrgaoNaoDetalhado());
        dto.adicionarParametro("DETALHADO", detalhar);
        dto.adicionarParametro("ID_UNIDADES", buscarIdsDasUnidades());
        dto.adicionarParametro("TIPO_RELATORIO", tipoRelatorio != null ? tipoRelatorio.getToDto() : null);
        return dto;
    }

    public String montarFiltros() {
        StringBuilder filtro = new StringBuilder();
        if (detalhar) {
            filtro.append(" Detalhado: ").append(Util.converterBooleanSimOuNao(detalhar)).append(", ");
        }
        if (tipoCargo != null) {
            filtro.append(" Tipo de Cargo: ").append(tipoCargo.getDescricao()).append(", ");
        }
        if (hierarquiaOrganizacional != null) {
            filtro.append(" Órgão: ").append(hierarquiaOrganizacional.toString()).append(", ");
        }
        if (tipoRelatorio != null) {
            filtro.append(" Tipo de Relatório: ").append(tipoRelatorio.toString());
        } else {
            filtro.append(" Tipo de Relatório: Geral ");
        }
        return filtro.toString();
    }

    public boolean isOrgaoNaoDetalhado(){
        return TipoRelatorioControleVagas.ORGAO_ENTIDADE.equals(tipoRelatorio) && !detalhar;
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();

        if (!isTodosOrgaos && getHierarquiaOrganizacional() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Por favor informe o Órgão.");
        }
        if (tipoCargo == null && !isTodosCargos) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Cargo é obrigatório.");
        }
        if (tipoRelatorio == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Relatório é obrigatório.");
        }
        if (TipoRelatorioControleVagas.UNIDADEADM.equals(tipoRelatorio) && unidadesSelecionadas.isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Por favor selecione ao menos uma Unidade Administrativa.");
        }
        ve.lancarException();
    }

    public void habilitarTodosOrgaos() {
        hierarquiaOrganizacional = null;
        recarregarUnidades();
    }

    public void habilitarTodosOsCargos() {
        tipoCargo = null;
    }

    public Boolean containsTodasAsUnidades() {
        for (HierarquiaOrganizacional unidade : unidades) {
            if (!unidadesSelecionadas.contains(unidade)) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    public void removerTodasAsUnidades() {
        unidadesSelecionadas.clear();
    }

    public void adicionarTodasAsUnidades() {
        unidadesSelecionadas.clear();
        for (HierarquiaOrganizacional unidade : unidades) {
            unidadesSelecionadas.add(unidade);
        }
    }

    public Boolean containsUnidade(HierarquiaOrganizacional unidade) {
        return unidadesSelecionadas.contains(unidade);
    }

    public void removerUnidade(HierarquiaOrganizacional unidade) {
        unidadesSelecionadas.remove(unidade);
    }

    public void adicionarUnidade(HierarquiaOrganizacional unidade) {
        if (!unidadesSelecionadas.contains(unidade)) {
            unidadesSelecionadas.add(unidade);
        }
    }

    public Boolean getTodosOrgaos() {
        return isTodosOrgaos;
    }

    public void setTodosOrgaos(Boolean todosOrgaos) {
        isTodosOrgaos = todosOrgaos;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public List<SelectItem> getTiposRelatorio() {
        return Util.getListSelectItem(TipoRelatorioControleVagas.values(), false);
    }

    public List<SelectItem> getTiposDeCagos() {
        return Util.getListSelectItem(TipoPCS.values(), false);
    }

    public TipoRelatorioControleVagas getTipoRelatorio() {
        return tipoRelatorio;
    }

    public TipoPCS getTipoCargo() {
        return tipoCargo;
    }

    public void setTipoCargo(TipoPCS tipoCargo) {
        this.tipoCargo = tipoCargo;
    }

    public void setTipoRelatorio(TipoRelatorioControleVagas tipoRelatorio) {
        this.tipoRelatorio = tipoRelatorio;
    }

    public Boolean getDetalhar() {
        return detalhar;
    }

    public void setDetalhar(Boolean detalhar) {
        this.detalhar = detalhar;
    }

    public List<HierarquiaOrganizacional> getUnidades() {
        return unidades;
    }

    public void setUnidades(List<HierarquiaOrganizacional> unidades) {
        this.unidades = unidades;
    }

    public List<HierarquiaOrganizacional> getUnidadesSelecionadas() {
        return unidadesSelecionadas;
    }

    public void setUnidadesSelecionadas(List<HierarquiaOrganizacional> unidadesSelecionadas) {
        this.unidadesSelecionadas = unidadesSelecionadas;
    }



    public List<HierarquiaOrganizacional> completarOrgao(String parte) {
        return hierarquiaOrganizacionalFacade.filtraPorNivel(parte.trim(), "2", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public Boolean isTipoRelatorioAdministrativo() {
        return TipoRelatorioControleVagas.UNIDADEADM.equals(tipoRelatorio);
    }

    public void recarregarUnidades() {
        if (isTipoRelatorioAdministrativo()) {
            unidadesSelecionadas.clear();
            if (hierarquiaOrganizacional != null) {
                unidades = relatorioControleVagasFacade.filtrandoHierarquiaOrganizacionalAdministrativa(hierarquiaOrganizacional.getCodigoSemZerosFinais());
                if (unidades != null) {
                    Collections.sort(unidades);
                }
            } else {
                unidades = hierarquiaOrganizacionalFacade.getListaHierarquiasAdministrativasVigentes(Util.getSistemaControlador().getDataOperacao());
                Collections.sort(unidades);
            }
        }
        else {
            unidadesSelecionadas = hierarquiaOrganizacionalFacade.getListaHierarquiasAdministrativasVigentes(Util.getSistemaControlador().getDataOperacao());
        }
    }


    public Boolean getTodosCargos() {
        return isTodosCargos;
    }

    public void setTodosCargos(Boolean todosCargos) {
        isTodosCargos = todosCargos;
    }
}
