/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.ProcessoCalculoIPTU;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.NovoCalculoIPTUFacade;
import br.com.webpublico.negocios.ProcessoCalculoIPTUFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * @author Gustavo/Heinz
 */

//
//

@ManagedBean(name = "relatorioDeCalculosIsentos")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioDeCalculosIsentos",
        pattern = "/relatorios/iptu-isentos/",
        viewId = "/faces/tributario/iptu/relatorio/calculosisentos.xhtml"),
})
public class RelatorioDeCalculosIsentos extends AbstractReport implements Serializable {

    private ProcessoCalculoIPTU processoCalculoIPTU;
    private Converter converterProcessoCalculo;
    @EJB
    private ProcessoCalculoIPTUFacade processoCalculoIPTUFacade;
    @EJB
    private NovoCalculoIPTUFacade novoCalculoIPTUFacade;
    private String cadastroInicial, cadastroFinal;
    private ByteArrayOutputStream dadosByte;
    private String subReport, brasao, login, report;

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

    public void imprime() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.responseComplete();
        byte[] bytes = dadosByte.toByteArray();
        if (bytes != null && bytes.length > 0) {
            try {
                HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
                response.setContentType("application/pdf");
                response.setHeader("Content-disposition", "inline; filename=\"IPTU.pdf\"");
                response.setContentLength(bytes.length);
                ServletOutputStream outputStream = response.getOutputStream();
                outputStream.write(bytes, 0, bytes.length);
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                FacesUtil.addError("Operação não realizada", "Ocorreu um problema de comunicação com o servidor");
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) throws JRException, IOException {
        try {
            if (valida()) {
                RelatorioDTO dto = new RelatorioDTO();
                dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
                dto.setNomeRelatorio("Calculo de IPTU Isentos");
                dto.setNomeParametroBrasao("BRASAO");
                dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome());
                dto.adicionarParametro("WHERE", montaCondicao());
                dto.setApi("tributario/calculo-iptu-isento/");
                ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
                FacesUtil.addMensagemRelatorioSegundoPlano();
            }
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException op) {
            FacesUtil.printAllFacesMessages(op);
        } catch (Exception ex) {
            logger.error("Erro ao gerar o relatorio de encerramento mensal de servico. Erro {}", ex);
            FacesUtil.addErrorPadrao(ex);
        }
    }

    String montaCondicao() {
        return " where " +
            " processo.id = " + getProcessoCalculoIPTU().getId() +
            " and itemiptu.isento = 1 " +
            " and ci.inscricaocadastral >= " +
            cadastroInicial +
            " and ci.inscricaocadastral <= " +
            cadastroFinal +
            " and (ci.ativo = null or ci.ativo = 1) " +
            " and prop.id = (select max(id) from propriedade where imovel_id = ci.id) ";
    }

    @URLAction(mappingId = "novoRelatorioDeCalculosIsentos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        cadastroInicial = "1";
        cadastroFinal = "999999999999999";
        processoCalculoIPTU = null;
    }

    public ByteArrayOutputStream getDadosByte() {
        return dadosByte;
    }

    public void setDadosByte(ByteArrayOutputStream dadosByte) {
        this.dadosByte = dadosByte;
    }

    public String getSubReport() {
        return subReport;
    }

    public String getLogin() {
        return login;
    }

    public String getBrasao() {
        return brasao;
    }

    public String getReport() {
        return report;
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

    public boolean valida() {
        boolean valida = true;
        if (processoCalculoIPTU == null) {
            FacesUtil.addError("Campo Obrigatório", "Informe o processo de cálculo");
            valida = false;
        }
        if (cadastroInicial == null || cadastroInicial.isEmpty()) {
            FacesUtil.addError("Campo Obrigatório", "Informe o cadastro inicial");
            valida = false;
        }
        if (cadastroFinal == null || cadastroFinal.isEmpty()) {
            FacesUtil.addError("Campo Obrigatório", "Informe o cadastro final");
            valida = false;
        }
        return valida;
    }
}
