/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.DateUtils;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;

/**
 * @author java
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoMovimentacaoCadastral", pattern = "/tributario/relatorio-alteracao-cadastro-pessoa/",
        viewId = "/faces/tributario/cadastromunicipal/relatorio/relatorioAlteracoesCadastroPessoa.xhtml"),
})
public class MovimentacaoCadastralControlador implements Serializable {

    private Date dataInicial;
    private Date dataFinal;
    private UsuarioSistema usuario;
    @EJB
    public SistemaFacade sistemaFacade;

    @URLAction(mappingId = "novoMovimentacaoCadastral", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        limparCampos();
    }

    public void limparCampos() {
        dataInicial = null;
        dataFinal = null;
        usuario = null;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("SECRETARIA", "Secretaria de Finanças");
            dto.adicionarParametro("NOMERELATORIO", "ALTERAÇÕES NO CADASTRO DE PESSOAS");
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.adicionarParametro("DATA_INICIAL", DateUtils.getDataFormatada(dataInicial));
            dto.adicionarParametro("DATA_FINAL", DateUtils.getDataFormatada(dataFinal));
            dto.adicionarParametro("LOGIN_USUARIO", usuario != null ? usuario.getLogin() : "");
            dto.adicionarParametro("MODULO", "TRIBUTÁRIO");
            dto.setNomeRelatorio("ALTERAÇÕES NO CADASTRO DE PESSOAS");
            dto.setApi("tributario/relatorio-movimentacao-cadastro-pessoa/");
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

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public UsuarioSistema getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioSistema us) {
        this.usuario = us;
    }

}
