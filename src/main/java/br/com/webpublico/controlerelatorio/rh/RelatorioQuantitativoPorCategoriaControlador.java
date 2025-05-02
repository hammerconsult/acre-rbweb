package br.com.webpublico.controlerelatorio.rh;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidadesauxiliares.rh.FiltroRelatorioRh;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.FolhaDePagamentoFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.ModalidadeContratoFPFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.List;

/**
 * Created by zaca on 05/04/17.
 */
@ViewScoped
@ManagedBean(name = "relatorioQuantitativoPorCategoriaControlador")
@URLMapping(id = "relatorioQuantitativoCategoria", pattern = "/relatorio/quantitativo-por-categoria/", viewId = "/faces/rh/relatorios/relatorioquantitativoporcategoria.xhtml")
public class RelatorioQuantitativoPorCategoriaControlador {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioQuantitativoPorCategoriaControlador.class);
    @EJB
    private ModalidadeContratoFPFacade modalidadeContratoFPFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private FiltroRelatorioRh filtroRelatorioRh;

    @URLAction(mappingId = "relatorioQuantitativoCategoria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtroRelatorioRh = new FiltroRelatorioRh();
        filtroRelatorioRh.inicializarAtributos();
        filtroRelatorioRh.setMes(buscarMesCorrente());
        filtroRelatorioRh.setAno(sistemaFacade.getExercicioCorrente().getAno());
    }

    private Integer buscarMesCorrente() {
        return DataUtil.getMes(sistemaFacade.getDataOperacao());
    }

    public List<SelectItem> getModalidades() {
        return Util.getListSelectItem(modalidadeContratoFPFacade.modalidadesAtivas(), false);
    }

    public List<SelectItem> getVersoes() {
        List<SelectItem> retorno = Lists.newArrayList();
        if (filtroRelatorioRh.getMes() != null && filtroRelatorioRh.getAno() != null && filtroRelatorioRh.getTipoFolhaDePagamento() != null) {
            retorno.add(new SelectItem(null, ""));
            for (Integer versao : folhaDePagamentoFacade.recuperarVersao(
                Mes.getMesToInt(filtroRelatorioRh.getMes()),
                filtroRelatorioRh.getAno(),
                filtroRelatorioRh.getTipoFolhaDePagamento())) {
                retorno.add(new SelectItem(versao, String.valueOf(versao)));
            }
        }
        return retorno;
    }

    public List<SelectItem> getTiposFolha() {
        return Util.getListSelectItem(TipoFolhaDePagamento.values(), false);
    }

    public List<HierarquiaOrganizacional> buscarHierarquiaOrganizacionalSegundoNivelAdministrativa(String filter) {
        return hierarquiaOrganizacionalFacade.filtraNivelDoisEComRaiz(filter, TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public void gerarRelatorio(String tipoRelatorio) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorio));
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getLogin(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MES", filtroRelatorioRh.getMes());
            dto.adicionarParametro("ANO", filtroRelatorioRh.getAno());
            dto.adicionarParametro("NOMERELATORIO", "Relatório Quantitativo por Categoria");
            dto.adicionarParametro("PREFEITURA", "PREFEITURA MUNICIPAL DE RIO BRANCO");
            dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO E GESTÃO DE PESSOAS");
            dto.adicionarParametro("MODULO", "Recursos Humanos");
            dto.adicionarParametro("modalidadeContratoCodigo", (filtroRelatorioRh.getModalidadeContratoFP() != null ? filtroRelatorioRh.getModalidadeContratoFP().getCodigo().toString() : ""));
            dto.adicionarParametro("mesIniciadoEmZero", Mes.getMesToInt(filtroRelatorioRh.getMes()).getNumeroMesIniciandoEmZero());
            dto.adicionarParametro("agruparPorOrgao", filtroRelatorioRh.getAgruparPorOrgao());
            dto.adicionarParametro("todosOrgao", filtroRelatorioRh.getTodosOrgao());
            dto.adicionarParametro("tipoFolha", filtroRelatorioRh.getTipoFolhaDePagamento().getToDto());
            dto.adicionarParametro("versao", filtroRelatorioRh.getVersao());
            if (getTodosOrgaos()) {
                filtroRelatorioRh.setHierarquiaOrganizacional(hierarquiaOrganizacionalFacade.getRaizHierarquia(sistemaFacade.getDataOperacao()));
                dto.adicionarParametro("hierarquiaCodigo", filtroRelatorioRh.getHierarquiaOrganizacional().getCodigo());
                dto.adicionarParametro("hierarquiaDescricao", filtroRelatorioRh.getHierarquiaOrganizacional().getDescricao());
            }
            dto.setNomeRelatorio("RELATÓRIO-QUANTITATIVO-POR-CATEGORIA");
            dto.setApi("rh/quantitativo-por-categoria/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório de Quantitativo por Categoria: {}", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();

        if (!isTodosOrgaos() && filtroRelatorioRh.getHierarquiaOrganizacional() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Órgão deve ser informado.");
        }
        if (filtroRelatorioRh.getMes() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês deve ser informado.");
        }
        if (filtroRelatorioRh.getAno() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano deve ser informado.");
        }
        if (filtroRelatorioRh.getTipoFolhaDePagamento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Folha deve ser informado.");
        }
        ve.lancarException();
    }

    public void habilitarTodosOrgaos() {
        filtroRelatorioRh.setHierarquiaOrganizacional(null);
        filtroRelatorioRh.setAgruparPorOrgao(Boolean.FALSE);
    }

    public List<SelectItem> getMeses() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (Mes obj : Mes.values()) {
            toReturn.add(new SelectItem(obj.getNumeroMes(), obj.toString()));
        }
        return toReturn;
    }

    public Boolean getTodosOrgaos() {
        return filtroRelatorioRh.getTodosOrgao() != null ? filtroRelatorioRh.getTodosOrgao() : false;
    }

    public Boolean isTodosOrgaos() {
        return filtroRelatorioRh.getTodosOrgao();
    }

    public FiltroRelatorioRh getFiltroRelatorioRh() {
        return filtroRelatorioRh;
    }

    public void setFiltroRelatorioRh(FiltroRelatorioRh filtroRelatorioRh) {
        this.filtroRelatorioRh = filtroRelatorioRh;
    }
}
