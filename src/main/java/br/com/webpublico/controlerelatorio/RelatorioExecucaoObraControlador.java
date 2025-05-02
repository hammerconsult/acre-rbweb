package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Obra;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ObraFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * Created by HardRock on 19/05/2017.
 */

@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-relatorio-execucao-obra", pattern = "/obra/relatorio/execucao-obras/", viewId = "/faces/administrativo/obras/relatorio/execucao-obra.xhtml")
})
@ManagedBean
public class RelatorioExecucaoObraControlador extends AbstractReport implements Serializable {

    @EJB
    private ObraFacade obraFacade;
    private Obra obra;
    private String filtros;

    @URLAction(mappingId = "novo-relatorio-execucao-obra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        obra = null;
    }

    public void gerarRelatorio() {
        try {
            validarRelatorio();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", getUsuario(), false);
            dto.adicionarParametro("MUNICIPIO", "PREFEITURA MUNICIPAL DE RIO BRANCO");
            dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE OBRAS PÚBLICAS - SEOP");
            dto.adicionarParametro("NOMERELATORIO", "Relatório de Execução de Obra");
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("OBRA", "Obra: " + obra.getNome());
            dto.adicionarParametro("CONTRATO", "Contrato: " + obra.getContrato().getNumeroTermo());
            dto.adicionarParametro("FILTRO", filtros);
            dto.adicionarParametro("condicao", montarFiltroSql().toString());
            dto.adicionarParametro("MODULO", "Obras");
            dto.setNomeRelatorio(getNomeRelatorioPDF());
            dto.setApi("administrativo/execucao-obra/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    private StringBuilder montarFiltroSql() {
        StringBuilder condicao = new StringBuilder();
        if (obra != null) {
            condicao.append(" and obra.id = ").append(obra.getId());
            filtros += "Obra: " + obra;
        }
        return condicao;
    }

    private void validarRelatorio() {
        ValidacaoException ve = new ValidacaoException();
        if (obra == null) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo obra deve ser informado.");
        }
        ve.lancarException();
    }

    private String getNomeRelatorioPDF() {
        return "RELATORIO EXECUCAO OBRA ";
    }

    private String getUsuario() {
        return getSistemaFacade().getUsuarioCorrente().getNome();
    }

    public List<Obra> completarObras(String parte) {
        return obraFacade.buscarObrasPorUsuarioSistema(parte.trim(), getSistemaFacade().getUsuarioCorrente());
    }

    public Obra getObra() {
        return obra;
    }

    public void setObra(Obra obra) {
        this.obra = obra;
    }
}
