/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.ProcessoCalculoIPTU;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ProcessoCalculoIPTUFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import java.io.Serializable;
import java.util.List;

/**
 * @author Gustavo/Heinz
 */

//
//

@ManagedBean(name = "relatorioDeCalculosMaisDeUmaConstrucao")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioDeCalculosMaisDeUmaConstrucao",
        pattern = "/relatorios/iptu-mais-de-uma-construcao/",
        viewId = "/faces/tributario/iptu/relatorio/calculosmaisdeumaconstrucao.xhtml"),
})
public class RelatorioDeCalculosMaisDeUmaConstrucao extends AbstractReport implements Serializable {

    private ProcessoCalculoIPTU processoCalculoIPTU;
    private Converter converterProcessoCalculo;
    @EJB
    private ProcessoCalculoIPTUFacade processoCalculoIPTUFacade;
    private String cadastroInicial, cadastroFinal;

    @URLAction(mappingId = "novoRelatorioDeCalculosMaisDeUmaConstrucao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        cadastroInicial = "1";
        cadastroFinal = "999999999999999";
        processoCalculoIPTU = null;
    }

    public Converter getConverterProcessoCalculo() {
        if (converterProcessoCalculo == null) {
            converterProcessoCalculo = new ConverterAutoComplete(ProcessoCalculoIPTU.class, processoCalculoIPTUFacade);
        }
        return converterProcessoCalculo;
    }

    public ProcessoCalculoIPTU getProcessoCalculoIPTU() {
        return processoCalculoIPTU;
    }

    public void setProcessoCalculoIPTU(ProcessoCalculoIPTU processoCalculoIPTU) {
        this.processoCalculoIPTU = processoCalculoIPTU;
    }

    public List<ProcessoCalculoIPTU> completaProcessoCalculo(String parte) {
        return processoCalculoIPTUFacade.listaProcessosPorDescricao(parte.trim());
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (processoCalculoIPTU == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Processo de Cálculo deve ser informado.");
        }
        if (cadastroInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Cadastro Inicial deve ser informado.");
        }
        if (cadastroFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Cadastro Final deve ser informado.");
        }
        ve.lancarException();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("PROCESSO_ID", getProcessoCalculoIPTU().getId());
            dto.adicionarParametro("CADASTROINICIAL", cadastroInicial);
            dto.adicionarParametro("CADASTROFINAL", cadastroFinal);
            dto.setNomeRelatorio("RELATÓRIO-IPTU-MAIS-DE-UMA-CONSTRUÇÃO");
            dto.setApi("tributario/iptu-mais-de-uma-construcao/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public String getCadastroInicial() {
        return cadastroInicial;
    }

    public void setCadastroInicial(String cadastroInicial) {
        this.cadastroInicial = cadastroInicial;
    }

    public String getCadastroFinal() {
        return cadastroFinal;
    }

    public void setCadastroFinal(String cadastroFinal) {
        this.cadastroFinal = cadastroFinal;
    }
}
