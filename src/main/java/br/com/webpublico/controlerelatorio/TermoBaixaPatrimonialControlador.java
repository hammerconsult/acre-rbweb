package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.RelatorioPatrimonioControlador;
import br.com.webpublico.entidades.EfetivacaoBaixaPatrimonial;
import br.com.webpublico.entidadesauxiliares.FiltroTermoBaixaPatrimonial;
import br.com.webpublico.enums.TipoBaixa;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.Arrays;
import java.util.List;

@ViewScoped
@ManagedBean
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-termo-baixa",
        pattern = "/relatorio-termo-baixa/",
        viewId = "/faces/administrativo/relatorios/relatorio-termo-baixa-patrimonial.xhtml")}
)
public class TermoBaixaPatrimonialControlador extends RelatorioPatrimonioControlador {

    public static final String NOME_RELATORIO = "RELATORIO-TERMO-BAIXA-PATRIMONIAL";
    private FiltroTermoBaixaPatrimonial filtroRelatorio;

    @URLAction(mappingId = "relatorio-termo-baixa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        novoFitltroRelatorio();
    }

    public void novoFitltroRelatorio() {
        filtroRelatorio = new FiltroTermoBaixaPatrimonial();
        filtroRelatorio.setDataOperacao(getSistemaFacade().getDataOperacao());
        filtroRelatorio.setUsuario(getSistemaFacade().getUsuarioCorrente());
    }

    @Override
    protected void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (filtroRelatorio.getDataBaixaInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Baixa Inicial deve ser informado.");
        }
        if (filtroRelatorio.getDataBaixaFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Baixa Final deve ser informado.");
        }
        ve.lancarException();
        if (filtroRelatorio.getDataBaixaInicial().after(filtroRelatorio.getDataBaixaFinal())) {
            ve.adicionarMensagemDeCampoObrigatorio("O Data de Baixa Inicial não pode ser posterior a Data de Baixa Final.");
        }
        ve.lancarException();
    }

    public List<SelectItem> getTiposDeBaixa() {
        return Util.getListSelectItem(Arrays.asList(TipoBaixa.values()));
    }

    public void gerarRelatorioEfetivacao(EfetivacaoBaixaPatrimonial efetivacao) {
        filtroRelatorio = new FiltroTermoBaixaPatrimonial();
        filtroRelatorio.setDataOperacao(getSistemaFacade().getDataOperacao());
        filtroRelatorio.setEfetivacaoBaixa(efetivacao);
        filtroRelatorio.setUsuario(efetivacao.getUsuarioSistema());
        String nomeArquivo = TipoBem.MOVEIS.equals(efetivacao.getTipoBem()) ? "Termo de Baixa de Bem Móvel" : "Termo de Baixa de Bem Imóvel";
        gerarRelatorioTermo(nomeArquivo, "PDF");
    }

    private void gerarRelatorioTermo(String nomeArquivo, String tipoRelatorioExtensao) {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setNomeRelatorio(nomeArquivo);
            dto.adicionarParametro("MODULO", "Patrimônio");
            dto.adicionarParametro("SECRETARIA", "SETOR PATRIMONIAL");
            dto.adicionarParametro("MUNICIPIO", montarNomeDoMunicipio());
            dto.adicionarParametro("NOMERELATORIO", dto.getNomeRelatorio());
            dto.adicionarParametro("DATA_OPERACAO", DataUtil.getDataFormatada(getSistemaFacade().getDataOperacao()));
            dto.adicionarParametro("CONDICAO", filtroRelatorio.getFiltros());
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setApi("administrativo/termo-baixa-patrimonial/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            gerarRelatorioTermo("Termo de Baixa de Bem Móvel", tipoRelatorioExtensao);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public FiltroTermoBaixaPatrimonial getFiltroRelatorio() {
        return filtroRelatorio;
    }

    public void setFiltroRelatorio(FiltroTermoBaixaPatrimonial filtroRelatorio) {
        this.filtroRelatorio = filtroRelatorio;
    }
}
