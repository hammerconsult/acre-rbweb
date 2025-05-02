/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.Processo;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.relatoriofacade.RelatorioProtocoloFacade;
import br.com.webpublico.report.ReportService;
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

/**
 * @author Usuario
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioProcesso", pattern = "/protocolo/relatorio-processo/", viewId = "/faces/tributario/cadastromunicipal/relatorio/processorelatorio.xhtml")
})
public class RelatorioProtocoloControle implements Serializable {

    @EJB
    private RelatorioProtocoloFacade facade;
    private Processo[] processosPessoa;
    private List<Processo> processos;
    private Integer numeroProcesso;
    private Integer anoProcesso;
    private Pessoa pessoa;

    public RelatorioProtocoloControle() {
        this.anoProcesso = null;
        this.numeroProcesso = null;
    }

    @URLAction(mappingId = "novoRelatorioProcesso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoRelatorioProcessos() {
        this.anoProcesso = null;
        this.numeroProcesso = null;
        pessoa = null;
        processos = Lists.newArrayList();
    }

    public void buscarProcessos() {
        processos = facade.getProcessoFacade().buscarProcessoPessoa(pessoa);
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarFiltros();
            UsuarioSistema usuarioCorrente = facade.getSistemaFacade().getUsuarioCorrente();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setNomeParametroSubreportDir("SUBREPORT_DIR");
            dto.setNomeRelatorio("CONSULTA DE PROCESSO");
            dto.adicionarParametro("MUNICIPIO", "PREFEITURA MUNICIPAL DE RIO BRANCO");
            dto.adicionarParametro("MODULO", "Protocolo");
            dto.adicionarParametro("USUARIO", usuarioCorrente.getNome(), false);
            dto.adicionarParametro("PROCESSO", " where PROCESSO.ID IN (" + concatenarProcessos() + ") ");
            dto.setApi("administrativo/processo/");
            ReportService.getInstance().gerarRelatorio(usuarioCorrente, dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void validarFiltros() {
        ValidacaoException ve = new ValidacaoException();
        if (numeroProcesso == null && anoProcesso == null && pessoa == null && processosPessoa.length == 0) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe um filtro para gerar o relatório!");
        }
        if ((numeroProcesso != null && anoProcesso == null) || (numeroProcesso == null && anoProcesso != null)) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o número do processo e o ano para gerar o relatório!");
        }
        if (pessoa != null && processosPessoa.length == 0) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione o(s) processo(s) para gerar o relatório!");
        }
        if (numeroProcesso != null && anoProcesso != null && pessoa != null && processosPessoa.length > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Somente um filtro deve ser selecionado!");
        }
        ve.lancarException();
    }

    public String concatenarProcessos() {
        String ids = "";
        if (numeroProcesso != null && anoProcesso != null && pessoa == null) {
            Processo processo = facade.getProcessoFacade().recuperarProcessoPorNumeroAndAno(numeroProcesso, anoProcesso);
            if (processo != null) {
                ids = processo.getId().toString();
            }
        } else if (numeroProcesso == null && anoProcesso == null && pessoa != null) {
            if (processosPessoa.length > 0) {
                StringBuilder sb = new StringBuilder();
                for (Processo proc : this.getProcessosPessoa()) {
                    sb.append(proc.getId()).append(",");
                }
                ids += sb.substring(0, sb.length() - 1);
            }
        }
        return ids;
    }

    public Processo[] getProcessosPessoa() {
        return processosPessoa;
    }

    public void setProcessosPessoa(Processo[] processosPessoa) {
        this.processosPessoa = processosPessoa;
    }

    public Integer getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(Integer numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public Integer getAnoProcesso() {
        return anoProcesso;
    }

    public void setAnoProcesso(Integer anoProcesso) {
        this.anoProcesso = anoProcesso;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public List<Processo> getProcessos() {
        return processos;
    }

    public void setProcessos(List<Processo> processos) {
        this.processos = processos;
    }
}
