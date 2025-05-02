/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ContratoFPFacade;
import br.com.webpublico.negocios.PessoaFacade;
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
 * @author leonardo
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioDependente", pattern = "/relatorio/dependente/novo/", viewId = "/faces/rh/relatorios/relatoriodependentes.xhtml")
})
public class RelatorioDependenteControlador extends AbstractReport implements Serializable {

    private PessoaFisica servidor;
    private Converter converterServidor;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;

    public RelatorioDependenteControlador() {
    }

    public PessoaFisica getServidor() {
        return servidor;
    }

    public void setServidor(PessoaFisica servidor) {
        this.servidor = servidor;
    }

    public Converter getConverterServidor() {
        if (converterServidor == null) {
            converterServidor = new ConverterAutoComplete(PessoaFisica.class, pessoaFacade);
        }
        return converterServidor;
    }

    public List<PessoaFisica> completarServidores(String parte) {
        return contratoFPFacade.listaPessoasComContratos(parte.trim());
    }

    public void gerarRelatorio() {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDTO();
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar o relatório de Dependentes: {}", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void gerarRelatorio(String tipoRelatorioDependente) {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioDependente));
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar o relatório de Dependentes: {}", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private RelatorioDTO montarRelatorioDTO() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.setNomeParametroBrasao("BRASAO");
        dto.adicionarParametro("MODULO", "Recursos Humanos");
        dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO E GESTÃO");
        dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DE DEPENDENTES");
        dto.adicionarParametro("PESSOA_ID", servidor.getId());
        dto.setNomeRelatorio("RELATÓRIO-DE-DEPENDENTES");
        dto.setApi("rh/dependente/");
        return dto;
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (servidor == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Servidor deve ser informado.");
        }
        ve.lancarException();
    }

    @URLAction(mappingId = "novoRelatorioDependente", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        this.servidor = null;
    }
}
