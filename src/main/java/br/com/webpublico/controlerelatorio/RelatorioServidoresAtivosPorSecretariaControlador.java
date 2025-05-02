package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidadesauxiliares.FiltroRelatorioServidoresAtivosPorSecretaria;
import br.com.webpublico.entidadesauxiliares.rh.RelatorioServidoresAtivosPorSecretariaDetalhamentoVO;
import br.com.webpublico.entidadesauxiliares.rh.RelatorioServidoresAtivosPorSecretariaVO;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.comum.ConfiguracaoDeRelatorioFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;


/**
 * @author octavio
 */

@ManagedBean(name = "relatorioServidoresAtivosPorSecretariaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-servidores-ativos-por-secretaria", pattern = "/relatorio/servidores-ativos-por-secretaria/", viewId = "/faces/rh/relatorios/relatorioservidoresativosporsecretaria.xhtml")
})
public class RelatorioServidoresAtivosPorSecretariaControlador implements Serializable {

    @EJB
    private ConfiguracaoDeRelatorioFacade configuracaoDeRelatorioFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private FiltroRelatorioServidoresAtivosPorSecretaria filtroRelatorio;
    private StringBuilder filtroUtilizado;
    private List<RelatorioServidoresAtivosPorSecretariaDetalhamentoVO> servidoresDetalhamento;
    private List<RelatorioServidoresAtivosPorSecretariaVO> servidoresAtivosPorSecretaria;

    public RelatorioServidoresAtivosPorSecretariaControlador() {
        limparCampos();
    }

    public FiltroRelatorioServidoresAtivosPorSecretaria getFiltroRelatorio() {
        return filtroRelatorio;
    }

    public List<SelectItem> getTiposFolhaPagamento() {
        return Util.getListSelectItem(TipoFolhaDePagamento.getFolhasPorPrioridadeDeUso(), false);
    }

    public List<SelectItem> getMeses() {
        return Util.getListSelectItem(Mes.values(), false);
    }

    public List<HierarquiaOrganizacional> completarHierarquia(String parte) {
        return hierarquiaOrganizacionalFacade.filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
            sistemaFacade.getDataOperacao());
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (filtroRelatorio.getHierarquiaOrganizacional() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione o órgão.");
        }
        if (filtroRelatorio.getTipoFolha() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione o tipo de folha de pagamento.");
        }
        if (filtroRelatorio.getMesInicial() == null || filtroRelatorio.getAnoInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione o mês e ano inicial.");
        }
        if (filtroRelatorio.getMesFinal() == null || filtroRelatorio.getAnoFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione o mês e ano final.");
        }
        if ((filtroRelatorio.getAnoInicial() != null && filtroRelatorio.getAnoFinal() != null)
            && filtroRelatorio.getAnoInicial() > filtroRelatorio.getAnoFinal()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O ano inicial deve ser menor ou igual ao ano final.");
        }
        if ((filtroRelatorio.getMesInicial() != null && filtroRelatorio.getMesFinal() != null)
            && (filtroRelatorio.getMesInicial().getNumeroMesIniciandoEmZero() > filtroRelatorio.getMesFinal().getNumeroMesIniciandoEmZero())
            && (filtroRelatorio.getAnoInicial() != null && filtroRelatorio.getAnoFinal() != null) &&
            (filtroRelatorio.getAnoInicial().equals(filtroRelatorio.getAnoFinal()))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O mês inicial deve ser menor ou igual ao mês final.");
        }
        ve.lancarException();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            instanciarListas();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "Recursos Humanos");
            dto.adicionarParametro("SECRETARIA", "DEPARTAMENTO DE RECURSOS HUMANOS");
            dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO SERVIDORES ATIVOS POR SECRETARIA");
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome());
            dto.adicionarParametro("DATAOPERACAO", DataUtil.getDataFormatada(UtilRH.getDataOperacao()));
            dto.adicionarParametro("CODIGOHO", filtroRelatorio.getHierarquiaOrganizacional().getCodigoSemZerosFinais() + "%");
            dto.adicionarParametro("FILTRORELATORIO", FiltroRelatorioServidoresAtivosPorSecretaria.servidoresAtivosToDto(filtroRelatorio));
            dto.adicionarParametro("FILTROS", filtroUtilizado().toString());
            String dataPorExtenso = DataUtil.recuperarDataPorExtenso(sistemaFacade.getDataOperacao());
            dto.adicionarParametro("USUARIORESPONSAVEL", sistemaFacade.getUsuarioCorrente().toString());
            dto.adicionarParametro("DATAEXTENSO", dataPorExtenso);
            dto.adicionarParametro("NOME_ARQUIVO", "RELATORIO_SERVIDOR_POR_SECRETARIA-" + dataPorExtenso.substring(0, dataPorExtenso.length() - 1));
            dto.adicionarParametro("ORGAO", filtroRelatorio.getHierarquiaOrganizacional().toString());
            dto.setNomeRelatorio("RELATÓRIO SERVIDORES ATIVOS POR SECRETARIA");
            dto.setApi("rh/servidores-ativos-por-secretaria/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    private StringBuilder filtroUtilizado(){
        filtroUtilizado = new StringBuilder();
        String sepearador = "; ";
        filtroUtilizado.append("Órgão: ").append(filtroRelatorio.getHierarquiaOrganizacional().toString()).append(sepearador);
        filtroUtilizado.append("Tipo de Folha de Pagamento: ").append(filtroRelatorio.getTipoFolha().getDescricao()).append(sepearador);
        filtroUtilizado.append("Mês/Ano Inicial: ").append(filtroRelatorio.getMesInicial().getDescricao()).append("/").append(filtroRelatorio.getAnoInicial()).append(sepearador);
        filtroUtilizado.append("Mês/Ano Final: ").append(filtroRelatorio.getMesFinal().getDescricao()).append("/").append(filtroRelatorio.getAnoFinal()).append(sepearador);
        String filtroFinal = filtroUtilizado.toString().substring(0, this.filtroUtilizado.length() - 2);
        filtroUtilizado = new StringBuilder();
        return filtroUtilizado.append(filtroFinal);
    }

    public void limparCampos() {
        filtroRelatorio = new FiltroRelatorioServidoresAtivosPorSecretaria();
    }

    private void instanciarListas() {
        servidoresAtivosPorSecretaria = Lists.newArrayList();
        servidoresDetalhamento = Lists.newArrayList();
    }
}
