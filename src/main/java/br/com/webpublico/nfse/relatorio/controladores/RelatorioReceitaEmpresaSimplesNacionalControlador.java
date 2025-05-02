package br.com.webpublico.nfse.relatorio.controladores;

import br.com.webpublico.enums.SituacaoCadastralCadastroEconomico;
import br.com.webpublico.enums.TipoIssqn;
import br.com.webpublico.enums.TipoRelatorioApresentacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.nfse.domain.dtos.FiltroReceitaEmpresaSimplesNacional;
import br.com.webpublico.nfse.enums.SituacaoNota;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorioReceitaEmpresaSimplesNacional",
        pattern = "/nfse/relatorio/receita-empresa-simples-nacional/",
        viewId = "/faces/tributario/nfse/relatorio/receita-empresa-simples-nacional.xhtml")
})
public class RelatorioReceitaEmpresaSimplesNacionalControlador {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioReceitaEmpresaSimplesNacionalControlador.class);

    @EJB
    private SistemaFacade sistemaFacade;

    private FiltroReceitaEmpresaSimplesNacional filtro;

    public FiltroReceitaEmpresaSimplesNacional getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroReceitaEmpresaSimplesNacional filtro) {
        this.filtro = filtro;
    }

    @URLAction(mappingId = "relatorioReceitaEmpresaSimplesNacional", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtro = new FiltroReceitaEmpresaSimplesNacional();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            filtro.validarFiltros();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            if (TipoRelatorioDTO.XLS.equals(dto.getTipoRelatorio())) {
                dto.adicionarParametro("FILTROS_EXCEL", filtro.montarMapFiltrosExcel());
                dto.setApi("tributario/nfse/receita-empresa-simples-nacional/excel/");
            } else {
                dto.setApi("tributario/nfse/receita-empresa-simples-nacional/");
            }
            dto.setNomeRelatorio("Relatório de Receita de Empresas Optantes pelo Simples Nacional");
            dto.adicionarParametro("MUNICIPIO", "Municipio de Rio Branco");
            dto.adicionarParametro("SECRETARIA", "Secretaria Municipal de Finanças");
            dto.adicionarParametro("TITULO", "RELATÓRIO DE RECEITA DE EMPRESAS OPTANTES PELO SIMPLES NACIONAL");
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome());
            filtro.adicionarFiltros(dto);
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException op) {
            FacesUtil.printAllFacesMessages(op);
        } catch (Exception ex) {
            logger.error("Erro ao gerar o relatorio de receita de empresas optantes pelo simples nacional. {}", ex.getMessage());
            logger.debug("Detalhes do erro ao gerar o relatorio de receita de empresas optantes pelo simples nacional. ", ex);
            FacesUtil.addErrorPadrao(ex);
        }
    }

    public List<SelectItem> getSituacoes() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, ""));
        if (!filtro.hasSituacao(SituacaoNota.EMITIDA)) {
            retorno.add(new SelectItem(SituacaoNota.EMITIDA.name(), SituacaoNota.EMITIDA.getDescricao()));
        }
        if (!filtro.hasSituacao(SituacaoNota.PAGA)) {
            retorno.add(new SelectItem(SituacaoNota.PAGA.name(), SituacaoNota.PAGA.getDescricao()));
        }
        if (!filtro.hasSituacao(SituacaoNota.CANCELADA)) {
            retorno.add(new SelectItem(SituacaoNota.CANCELADA.name(), SituacaoNota.CANCELADA.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getTiposIssqn() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, ""));
        if (!filtro.getTiposIssqn().contains(TipoIssqn.SIMPLES_NACIONAL)) {
            retorno.add(new SelectItem(TipoIssqn.SIMPLES_NACIONAL.name(), TipoIssqn.SIMPLES_NACIONAL.getDescricao()));
        }
        if (!filtro.getTiposIssqn().contains(TipoIssqn.SUBLIMITE_ULTRAPASSADO)) {
            retorno.add(new SelectItem(TipoIssqn.SUBLIMITE_ULTRAPASSADO.name(), TipoIssqn.SUBLIMITE_ULTRAPASSADO.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getSituacoesCadastral() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, ""));
        if (!filtro.getSituacoesCadastral().contains(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR)) {
            retorno.add(new SelectItem(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR.name(), SituacaoCadastralCadastroEconomico.ATIVA_REGULAR.getDescricao()));
        }
        if (!filtro.getSituacoesCadastral().contains(SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR)) {
            retorno.add(new SelectItem(SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR.name(), SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR.getDescricao()));
        }
        if (!filtro.getSituacoesCadastral().contains(SituacaoCadastralCadastroEconomico.INATIVO)) {
            retorno.add(new SelectItem(SituacaoCadastralCadastroEconomico.INATIVO.name(), SituacaoCadastralCadastroEconomico.INATIVO.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getTiposApresentacao() {
        return Util.getListSelectItemSemCampoVazio(TipoRelatorioApresentacao.values());
    }
}
