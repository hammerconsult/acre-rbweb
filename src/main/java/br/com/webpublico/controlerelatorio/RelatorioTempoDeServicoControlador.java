package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.AverbacaoTempoServico;
import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.MatriculaFP;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.relatoriofacade.rh.RelatorioTempoDeServicoFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.*;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.*;

/**
 * @author boy
 */
@ManagedBean(name = "relatorioTempoDeServicoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "antigoRelatorioTempoEfetivoExercicio", pattern = "/relatorio/tempo-efetivo-exercicio/antigo/", viewId = "/faces/rh/relatorios/relatoriotempodeservicoAntigo.xhtml"),
    @URLMapping(id = "novoRelatorioTempoEfetivoExercicio", pattern = "/relatorio/tempo-efetivo-exercicio/novo/", viewId = "/faces/rh/relatorios/relatoriotempodeservico.xhtml"),
    @URLMapping(id = "gerarRelatorioTempoEfetivoExercicio", pattern = "/relatorio/tempo-efetivo-exercicio/gerar/", viewId = "/faces/rh/relatorios/relatoriofichafinanceira.xhtml")
})
public class RelatorioTempoDeServicoControlador implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioTempoDeServicoControlador.class);

    @EJB
    private RelatorioTempoDeServicoFacade relatorioTempoDeServicoFacade;
    private MatriculaFP matriculaSelecionada;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ContratoFP contratoFP;
    private boolean detalhado;
    private Boolean averbacaoDataContratoTemporario;
    private String textoAverbacaoDataContratoTemporario;

    public RelatorioTempoDeServicoControlador() {

    }

    public List<ContratoFP> completarContratoFP(String parte) {
        return relatorioTempoDeServicoFacade.getContratoFPFacade().recuperaContratoMatricula(parte.trim());
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public boolean isDetalhado() {
        return detalhado;
    }

    public void setDetalhado(boolean detalhado) {
        this.detalhado = detalhado;
    }

    public void validarCampos() {
        ValidacaoException val = new ValidacaoException();
        if (contratoFP == null) {
            val.adicionarMensagemDeCampoObrigatorio("Informe o Contrato.");
        }
        val.lancarException();
    }

    @URLAction(mappingId = "antigoRelatorioTempoEfetivoExercicio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCamposAntigo() {
        matriculaSelecionada = null;
    }

    @URLAction(mappingId = "novoRelatorioTempoEfetivoExercicio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        contratoFP = null;
        detalhado = false;
    }

    public void gerarRelatorio() {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDTO();
            ReportService.getInstance().gerarRelatorio(sistemaControlador.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
            logger.error("Erro ao gerar relatório. ", e);
        }
    }

    public void gerarRelatorio(String tipoRelatorio) {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorio));
            ReportService.getInstance().gerarRelatorio(sistemaControlador.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
            logger.error("Erro ao gerar relatório. ", e);
        }
    }

    private RelatorioDTO montarRelatorioDTO() {
        RelatorioDTO dto = new RelatorioDTO();

        buscarDadosComplementares(contratoFP);

        dto.setNomeParametroBrasao("BRASAO");
        dto.setNomeRelatorio("RELATÓRIO DE TEMPO DE SERVIÇO");
        dto.adicionarParametro("USUARIO", sistemaControlador.getUsuarioCorrente().getLogin(), false);
        dto.adicionarParametro("MODULO", "Recursos Humanos");
        dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO");
        dto.adicionarParametro("NOMERELATORIO", "DEPARTAMENTO DE RECURSOS HUMANOS");

        dto.adicionarParametro("LOCAL_E_DATA", "RIO BRANCO, " + Util.dateToString(new Date()));
        dto.adicionarParametro("CONTRATOFP_ID", contratoFP.getId());
        dto.adicionarParametro("DETALHADO", detalhado);
        dto.adicionarParametro("AVERBACAO_DATA_CONTRATO_TEMPORARIO", averbacaoDataContratoTemporario);
        dto.adicionarParametro("TEXTO_AVERBACAO_TEMPORARIO", textoAverbacaoDataContratoTemporario);
        dto.adicionarParametro("DATA_OPERACAO", sistemaControlador.getDataOperacao().getTime());
        VinculoFP vinculo = relatorioTempoDeServicoFacade.getVinculoFPFacade().recuperarSimples(contratoFP.getId());
        dto.adicionarParametro("DATA_FINAL_VINCULOFP", vinculo.getFinalVigencia() == null ? null : vinculo.getFinalVigencia().getTime());
        dto.setApi("rh/relatorio-tempo-de-servico/");
        return dto;

    }

    public void buscarDadosComplementares(ContratoFP contrato) {
        averbacaoDataContratoTemporario = false;
        textoAverbacaoDataContratoTemporario = "";
        if (contrato.getAlteracaoVinculo() != null) {
            List<AverbacaoTempoServico> averbacoes = relatorioTempoDeServicoFacade.buscarDataAverbacao(contrato.getId());
            if (contrato.getAlteracaoVinculo() != null && (averbacoes != null && !averbacoes.isEmpty())) {
                for (AverbacaoTempoServico averbacao : averbacoes) {
                    if (averbacao.getInicioVigencia() != null) {
                        if (DataUtil.dataSemHorario(contrato.getDataAdmissao()).compareTo(DataUtil.dataSemHorario(averbacao.getInicioVigencia())) == 0) {
                            averbacaoDataContratoTemporario = true;
                            textoAverbacaoDataContratoTemporario = "A averbação correspondente a data " + DataUtil.getDataFormatada(averbacao.getInicioVigencia()) +
                                " é referente ao contrato temporário exercido na Prefeitura Municipal de Rio Branco.";
                            break;
                        }
                    }
                }
            }
        }
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}
