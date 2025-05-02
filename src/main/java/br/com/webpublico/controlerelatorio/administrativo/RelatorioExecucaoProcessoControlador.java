package br.com.webpublico.controlerelatorio.administrativo;

import br.com.webpublico.entidades.AtaRegistroPreco;
import br.com.webpublico.entidades.DispensaDeLicitacao;
import br.com.webpublico.entidades.ExecucaoProcesso;
import br.com.webpublico.enums.TipoExecucaoProcesso;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.AtaRegistroPrecoFacade;
import br.com.webpublico.negocios.DispensaDeLicitacaoFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-execucao-processo", pattern = "/relatorios/execucao-processo/", viewId = "/faces/administrativo/relatorios/execucao-processo.xhtml"),
})
public class RelatorioExecucaoProcessoControlador {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private AtaRegistroPrecoFacade ataRegistroPrecoFacade;
    @EJB
    private DispensaDeLicitacaoFacade dispensaDeLicitacaoFacade;
    private String filtrosUtilizados;
    private AtaRegistroPreco ataRegistroPreco;
    private DispensaDeLicitacao dispensaLicitacao;
    private TipoExecucaoProcesso tipoExecucaoProcesso;
    private ExecucaoProcesso execucaoProcesso;

    @URLAction(mappingId = "relatorio-execucao-processo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtrosUtilizados = "";
        ataRegistroPreco = null;
        execucaoProcesso = null;
    }

    public void gerarRelatorio(ExecucaoProcesso execucaoProcesso) {
        this.execucaoProcesso = execucaoProcesso;
        this.tipoExecucaoProcesso = execucaoProcesso.getTipoExecucao();
        if (this.execucaoProcesso.isExecucaoAta()) {
            this.ataRegistroPreco = this.execucaoProcesso.getAtaRegistroPreco();
        } else {
            this.dispensaLicitacao = this.execucaoProcesso.getDispensaLicitacao();
        }

        gerarRelatorio();
    }

    public void gerarRelatorioExecucaoAta(AtaRegistroPreco ataRegistroPreco) {
        this.tipoExecucaoProcesso = TipoExecucaoProcesso.ATA_REGISTRO_PRECO;
        this.ataRegistroPreco = ataRegistroPreco;
        gerarRelatorio();
    }

    public void gerarRelatorioExecucaoDispensa(DispensaDeLicitacao dispensaLicitacao) {
        this.tipoExecucaoProcesso = TipoExecucaoProcesso.DISPENSA_LICITACAO_INEXIGIBILIDADE;
        this.dispensaLicitacao = dispensaLicitacao;
        gerarRelatorio();
    }

    public void gerarRelatorio() {
        try {
            validarFiltrosObrigatorios();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.adicionarParametro("MODULO", "Compras e Licitações");
            dto.adicionarParametro("NOMERELATORIO", getNomeRelatorio().toUpperCase());
            dto.adicionarParametro("DESCRICAOORIGEMEXECUCAO", getDescricaoPrcocesso());
            dto.adicionarParametro("condicao", montarCondicaoEFiltrosUtilizados());
            dto.adicionarParametro("ISEXECUCAOCONTRATO", Boolean.FALSE);
            dto.adicionarParametro("FORMAENTREGA", execucaoProcesso.getFormaEntrega().getDescricao());
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("administrativo/execucao-processo/");
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

    private String getDescricaoPrcocesso() {
        if (tipoExecucaoProcesso.isAta()) {
            return "Ata nº: " + ataRegistroPreco.getNumero() + ", Licitação nº : " + ataRegistroPreco.getLicitacao().getNumeroLicitacao() + "/" + ataRegistroPreco.getLicitacao().getExercicio().getAno() + ", Modalidade nº : " + ataRegistroPreco.getLicitacao().getNumero() + " - " + execucaoProcesso.getFornecedor().getNomeCpfCnpj();
        }
        return dispensaLicitacao.getModalidade().getDescricao() + " nº: " + dispensaLicitacao.getNumeroDaDispensa() + "/" + dispensaLicitacao.getExercicioDaDispensa().getAno() + " - " + execucaoProcesso.getFornecedor().getNomeCpfCnpj();
    }

    private void validarFiltrosObrigatorios() {
        ValidacaoException ve = new ValidacaoException();
        if (tipoExecucaoProcesso == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo tipo de execução deve ser informado.");
        } else if (tipoExecucaoProcesso.isAta() && ataRegistroPreco == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ata Registro de Preço deve ser informado.");
        } else if (tipoExecucaoProcesso.isDispensa() && dispensaLicitacao == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo dispensa/inexigibilidade deve ser informado.");
        }
        ve.lancarException();
    }

    private String getNomeRelatorio() {
        return "Relatório de Execução da " + tipoExecucaoProcesso.getDescricao();
    }

    private String montarCondicaoEFiltrosUtilizados() {
        String condicao;
        if (tipoExecucaoProcesso.isAta()) {
            condicao = " and exAta.ataRegistroPreco_id = " + ataRegistroPreco.getId();
        } else {
            condicao = " and exDisp.dispensalicitacao_id = " + dispensaLicitacao.getId();
        }
        filtrosUtilizados = getDescricaoPrcocesso();
        if (execucaoProcesso != null) {
            condicao += " and exProc.id = " + execucaoProcesso.getId();
            filtrosUtilizados += " - Execução " + tipoExecucaoProcesso.getDescricao() + ": " + execucaoProcesso.toString();
        }
        return condicao;
    }

    public List<AtaRegistroPreco> completarAtas(String parte) {
        return ataRegistroPrecoFacade.buscarAtaRegistroPreco(parte);
    }

    public List<DispensaDeLicitacao> completarDispensa(String parte) {
        return dispensaDeLicitacaoFacade.buscarDispensaDeLicitacaoPorNumeroOrExercicioOrResumo(parte.trim());
    }

    public List<ExecucaoProcesso> completarExecucoes(String parte) {
        if (ataRegistroPreco != null) {
            return ataRegistroPrecoFacade.getExecucaoProcessoFacade().buscarExecucoesPorAtaRegistroPreco(ataRegistroPreco);
        } else if (dispensaLicitacao != null) {
            return ataRegistroPrecoFacade.getExecucaoProcessoFacade().buscarExecucoesPorDispensaLicitacao(dispensaLicitacao);
        }
        return ataRegistroPrecoFacade.getExecucaoProcessoFacade().buscarExecucoesProcesso(parte);
    }

    public List<SelectItem> getTiposExecucao() {
        return Util.getListSelectItem(TipoExecucaoProcesso.values());
    }

    public void limparExecucaoProcesso() {
        execucaoProcesso = null;
    }

    public void atualizarProcessoComExecucao() {
        if (execucaoProcesso != null && execucaoProcesso.getAtaRegistroPreco() != null) {
            ataRegistroPreco = execucaoProcesso.getAtaRegistroPreco();
        }
        if (execucaoProcesso != null && execucaoProcesso.getDispensaLicitacao() != null) {
            dispensaLicitacao = execucaoProcesso.getDispensaLicitacao();
        }
    }

    public boolean isTipoRelatorioAta() {
        return tipoExecucaoProcesso != null && tipoExecucaoProcesso.isAta();
    }

    public boolean isTipoRelatorioDispensa() {
        return tipoExecucaoProcesso != null && tipoExecucaoProcesso.isDispensa();
    }

    public AtaRegistroPreco getAtaRegistroPreco() {
        return ataRegistroPreco;
    }

    public void setAtaRegistroPreco(AtaRegistroPreco ataRegistroPreco) {
        this.ataRegistroPreco = ataRegistroPreco;
    }

    public ExecucaoProcesso getExecucaoProcesso() {
        return execucaoProcesso;
    }

    public void setExecucaoProcesso(ExecucaoProcesso execucaoProcesso) {
        this.execucaoProcesso = execucaoProcesso;
    }

    public DispensaDeLicitacao getDispensaLicitacao() {
        return dispensaLicitacao;
    }

    public void setDispensaLicitacao(DispensaDeLicitacao dispensaLicitacao) {
        this.dispensaLicitacao = dispensaLicitacao;
    }

    public TipoExecucaoProcesso getTipoExecucaoProcesso() {
        return tipoExecucaoProcesso;
    }

    public void setTipoExecucaoProcesso(TipoExecucaoProcesso tipoExecucaoProcesso) {
        this.tipoExecucaoProcesso = tipoExecucaoProcesso;
    }
}
