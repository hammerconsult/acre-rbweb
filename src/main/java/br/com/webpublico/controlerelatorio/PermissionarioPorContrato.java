package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.ConfiguracaoTributario;
import br.com.webpublico.entidades.ContratoRendasPatrimoniais;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.Localizacao;
import br.com.webpublico.enums.SituacaoContratoRendasPatrimoniais;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ContratoRendasPatrimoniaisFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "permissionarioPorContrato")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relacaoPermissionarioPorContrato", pattern = "/relacao-de-permissionarios-por-contrato/", viewId = "/faces/tributario/rendaspatrimoniais/relatorio/permissionarios.xhtml")
})
public class PermissionarioPorContrato extends AbstractReport {


    private static final Logger logger = LoggerFactory.getLogger(PermissionarioPorContrato.class);

    @EJB
    public ContratoRendasPatrimoniaisFacade contratoRendasPatrimoniaisFacade;
    public SistemaControlador sistemaControlador;
    public ContratoRendasPatrimoniais contratoRendasPatrimoniaisInicio;
    public ContratoRendasPatrimoniais contratoRendasPatrimoniaisFim;
    public Converter converterExercicio;
    private ContratoRendasPatrimoniais selecionado;
    private ConfiguracaoTributario configuracaoTributario;
    private ConverterAutoComplete converterLocalizacao;
    private Localizacao localizacao;
    private Exercicio exercicioInicial;
    private Exercicio exercicioFinal;
    private String inscricaoCadastralInicial;
    private String inscricaoCadastralFinal;
    private SituacaoContratoRendasPatrimoniais situacaoContrato;


    public ContratoRendasPatrimoniais getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(ContratoRendasPatrimoniais selecionado) {
        this.selecionado = selecionado;
    }

    public String getInscricaoCadastralInicial() {
        return inscricaoCadastralInicial;
    }

    public void setInscricaoCadastralInicial(String inscricaoCadastralInicial) {
        this.inscricaoCadastralInicial = inscricaoCadastralInicial;
    }

    public SituacaoContratoRendasPatrimoniais getSituacaoContrato() {
        return situacaoContrato;
    }

    public void setSituacaoContrato(SituacaoContratoRendasPatrimoniais situacaoContrato) {
        this.situacaoContrato = situacaoContrato;
    }

    public String getInscricaoCadastralFinal() {
        return inscricaoCadastralFinal;
    }

    public void setInscricaoCadastralFinal(String inscricaoCadastralFinal) {
        this.inscricaoCadastralFinal = inscricaoCadastralFinal;
    }

    public PermissionarioPorContrato() {
        sistemaControlador = (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
        exercicioInicial = null;
        exercicioFinal = null;
        contratoRendasPatrimoniaisInicio = null;
        contratoRendasPatrimoniaisFim = null;
        inscricaoCadastralInicial = null;
        inscricaoCadastralFinal = null;
    }

    public Converter getConverterLocalizacao() {
        if (converterLocalizacao == null) {
            converterLocalizacao = new ConverterAutoComplete(Localizacao.class, contratoRendasPatrimoniaisFacade.getLocalizacaoFacade());
        }
        return converterLocalizacao;
    }

    public Exercicio getExercicioFinal() {
        return exercicioFinal;
    }

    public void setExercicioFinal(Exercicio exercicioFinal) {
        this.exercicioFinal = exercicioFinal;
    }

    public Exercicio getExercicioInicial() {
        return exercicioInicial;
    }

    public void setExercicioInicial(Exercicio exercicioInicial) {
        this.exercicioInicial = exercicioInicial;
    }

    public Localizacao getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(Localizacao localizacao) {
        this.localizacao = localizacao;
    }


    @URLAction(mappingId = "relacaoPermissionarioPorContrato", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        selecionado = new ContratoRendasPatrimoniais();
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (Util.isNull(localizacao)) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe ao menos uma localização.");
        }
        ve.lancarException();

    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setApi("tributario/permissionario-por-contrato/");
            ReportService.getInstance().gerarRelatorio(sistemaControlador.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
            logger.error("Erro ao gerar relatório de Permissao por Contrato : {} ", e);
        }
    }


    private RelatorioDTO montarRelatorioDTO() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.setNomeParametroBrasao("BRASAO");
        dto.adicionarParametro("NOME_RELATORIO", "Relatório de Permissionário por Contrato");
        dto.setNomeRelatorio("Relatório de Permissionário por Contrato");
        dto.adicionarParametro("MODULO", "TRIBUTÁRIO");
        dto.adicionarParametro("USUARIO", sistemaControlador.getUsuarioCorrente().getNome(), false);
        dto.adicionarParametro("FILTROS", montarDescricaoFiltros());
        dto.adicionarParametro("INSCRICAO_CADASTRAL_INICIAL", inscricaoCadastralInicial);
        dto.adicionarParametro("INSCRICAO_CADASTRAL_FINAL", inscricaoCadastralFinal);
        dto.adicionarParametro("EXERCICIO_INICIAL", exercicioInicial != null ? exercicioInicial.getAno() : null);
        dto.adicionarParametro("EXERCICIO_FINAL", exercicioFinal != null ? exercicioFinal.getAno() : null);
        dto.adicionarParametro("SITUACAO_CONTRATO", situacaoContrato != null ? situacaoContrato.name() : null);
        dto.adicionarParametro("LOCALIZACAO", localizacao.getId());
        dto.adicionarParametro("SECRETARIA", "Secretaria Municipal de Finanças");
        return dto;
    }

    private String montarDescricaoFiltros() {
        StringBuilder retorno = new StringBuilder();
        retorno.append("Localização: ").append(localizacao.getDescricao().trim());
        if (exercicioInicial != null) {
            retorno.append(", Exercício Inicial: ").append(exercicioInicial.getAno());
        }
        if (exercicioFinal != null) {
            retorno.append(", Exercício Final: ").append(exercicioFinal.getAno());
        }
        if (inscricaoCadastralInicial != null && !inscricaoCadastralInicial.isEmpty()) {
            retorno.append(", Inscrição Inicial: ").append(inscricaoCadastralInicial);
        }
        if (inscricaoCadastralFinal != null && !inscricaoCadastralFinal.isEmpty()) {
            retorno.append(", Inscrição Final: ").append(inscricaoCadastralFinal);
        }
        retorno.append(", Situação do Contrato: ");
        if (situacaoContrato != null) {
            retorno.append(situacaoContrato.getDescricao());
        } else {
            retorno.append("Todos");
        }
        retorno.append(".");

        return retorno.toString();
    }


    public List<Localizacao> completaLocalizacao(String parte) {

        if (Util.isNotNull(configuracaoTributario) && Util.isNotNull(selecionado.getLotacaoVistoriadora())) {
            return contratoRendasPatrimoniaisFacade.getLocalizacaoFacade().listaFiltrandoPorLotacao(selecionado.getLotacaoVistoriadora(), parte.trim(), "descricao");
        } else {
            FacesUtil.addOperacaoNaoPermitida("Não foi encontrado configuração tributária, verifique!");
            return new ArrayList<>();
        }

    }

    public List<SelectItem> getSituacoesContrato() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, "Todos"));
        for (SituacaoContratoRendasPatrimoniais situacao : SituacaoContratoRendasPatrimoniais.values()) {
            retorno.add(new SelectItem(situacao, situacao.getDescricao()));
        }
        return retorno;
    }

    public void limparCampos() {
        FacesUtil.redirecionamentoInterno("/relacao-de-permissionarios-por-contrato/");

    }
}
