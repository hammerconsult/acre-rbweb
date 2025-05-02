package br.com.webpublico.controle.rh.previdencia;

import br.com.webpublico.entidades.MatriculaFP;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoRH;
import br.com.webpublico.entidadesauxiliares.FiltroPrevidenciaContribuicao;
import br.com.webpublico.entidadesauxiliares.rh.relatorio.AssistentePrevidenciaContribuicao;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.MatriculaFPFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.VinculoFPFacade;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoRHFacade;
import br.com.webpublico.negocios.rh.relatorio.PrevidenciaContribuicaoIndividualizadaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author peixe
 * on 17/08/2015.
 */

@ManagedBean(name = "previdenciaContribuicaoIndividualizadaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "contribuicaoIndividualizada", pattern = "/relatorio/registro-individualizado-previdencia/",
        viewId = "/faces/rh/relatorios/previdencia/contribuicaoindividualizada.xhtml")
})
public class PrevidenciaContribuicaoIndividualizadaControlador implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(PrevidenciaContribuicaoIndividualizadaControlador.class);
    private static final String ARQUIVO_JASPER = "RegistroIndividualizadoContribuicao.jasper";
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private ConfiguracaoRHFacade configuracaoRHFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private PrevidenciaContribuicaoIndividualizadaFacade previdenciaContribuicaoIndividualizadaFacade;
    @EJB
    private MatriculaFPFacade matriculaFPFacade;
    private ConfiguracaoRH configuracaoRH;
    public FiltroPrevidenciaContribuicao filtro;

    @URLAction(mappingId = "contribuicaoIndividualizada", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        limparFiltros();
    }

    public void sugerirInicioVigencia() {
        if (filtro.getVinculoFP() != null) {
            filtro.setInicio(filtro.getVinculoFP().getInicioVigencia());
            if (filtro.getVinculoFP().getFinalVigencia() != null) {
                filtro.setTermino(filtro.getVinculoFP().getFinalVigencia());
            }
        }
    }

    public void sugerirVigencia(boolean apagarContrato) {
        if (apagarContrato) {
            filtro.setContrato(null);
        }
        if (filtro.getMatriculaFP() != null) {
            if (filtro.getContrato() != null) {
                VinculoFP vinculo = vinculoFPFacade.recuperarVinculoPorMatriculaETipoOrNumero(filtro.getMatriculaFP().getMatricula(), filtro.getContrato());
                filtro.setInicio(vinculo.getInicioVigencia());
                filtro.setTermino(vinculo.getFinalVigencia() != null ? vinculo.getFinalVigencia() : null);
            } else {
                List<VinculoFP> vinculos = vinculoFPFacade.buscarVinculosParaMatricula(filtro.getMatriculaFP().getMatricula());
                filtro.setInicio(vinculos.get(0).getInicioVigencia());
                VinculoFP ultimoVinculo = vinculos.get(vinculos.size() - 1);
                filtro.setTermino(ultimoVinculo.getFinalVigencia() != null ? ultimoVinculo.getFinalVigencia() : null);
            }
        }
    }

    private AssistentePrevidenciaContribuicao preencherAssistente() {
        AssistentePrevidenciaContribuicao assistente = new AssistentePrevidenciaContribuicao();
        assistente.setVinculoFP(filtro.getVinculoFP());
        assistente.setInicio(filtro.getInicio());
        assistente.setTermino(filtro.getTermino());
        assistente.setMatriculaFP(filtro.getMatriculaFP());
        assistente.setContrato(filtro.getContrato());
        return assistente;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarFiltros();
            RelatorioDTO dto = previdenciaContribuicaoIndividualizadaFacade.montarRelatorioDTO(preencherAssistente());
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setApi("rh/previdencia-contribuicao-individualizada/");
            ReportService.getInstance().gerarRelatorio(Util.getSistemaControlador().getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    private void validarFiltros() {
        ValidacaoException vl = new ValidacaoException();
        if (filtro.getMatriculaFP() == null) {
            vl.adicionarMensagemDeCampoObrigatorio("O campo 'Matrícula' deve ser preenchido.");
        }
        if (filtro.getInicio() == null) {
            vl.adicionarMensagemDeCampoObrigatorio("O campo 'Data Inicial' deve ser preenchido.");
        } else {
            if (DataUtil.dataSemHorario(getMinimaDataInicial()).compareTo(DataUtil.dataSemHorario(filtro.getInicio())) > 0) {
                vl.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "A 'Data Inicial' deve ser igual ou superior a " + DataUtil.getDataFormatada(getMinimaDataInicial()));
            }
        }
        if (filtro.getInicio() != null && filtro.getTermino() != null) {
            if (filtro.getInicio().after(filtro.getTermino())) {
                vl.adicionarMensagemDeCampoObrigatorio("A 'Data Inicial' deve ser menor que a 'Data Final'.");
            }
        }
        if (vl.temMensagens()) {
            throw vl;
        }
    }

    public Date getMinimaDataInicial() {
        if (getConfiguracaoRH() != null && getConfiguracaoRH().getConfiguracaoPrevidencia() != null &&
            getConfiguracaoRH().getConfiguracaoPrevidencia().getInicioRegistroIndividualizado() != null) {
            return getConfiguracaoRH().getConfiguracaoPrevidencia().getInicioRegistroIndividualizado();
        }
        return null;
    }

    public ConfiguracaoRH getConfiguracaoRH() {
        if (configuracaoRH == null) {
            configuracaoRH = configuracaoRHFacade.recuperarConfiguracaoRHVigente(sistemaFacade.getDataOperacao());
        }
        return configuracaoRH;
    }

    public void irParaConfiguracaoRH() {
        String redirecionarPara = getConfiguracaoRH() == null ? "/configuracao/rh/listar/" : "/configuracao/rh/editar/" + getConfiguracaoRH().getId() + "/";
        FacesUtil.redirecionamentoInterno(redirecionarPara);
    }

    public void limparFiltros() {
        filtro = new FiltroPrevidenciaContribuicao();
        filtro.setVinculoFP(null);
        filtro.setInicio(null);
        filtro.setTermino(null);
    }

    public List<VinculoFP> completaVinculo(String s) {
        return vinculoFPFacade.listaTodosVinculos1(s.trim());
    }

    public List<MatriculaFP> completarMatriculas(String s) {
        return matriculaFPFacade.buscarMatriculasPorPessoaOrMatricula(s.trim());
    }

    public List<SelectItem> getContratos() {
        List<SelectItem> retorno = Lists.newArrayList();
        if (filtro.getMatriculaFP() != null) {
            retorno.add(new SelectItem(null, "Todos"));
            for (String contrato : vinculoFPFacade.buscarContratosParaMatricula(filtro.getMatriculaFP())) {
                retorno.add(new SelectItem(contrato));
            }
        }
        return retorno;
    }

    public FiltroPrevidenciaContribuicao getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroPrevidenciaContribuicao filtro) {
        this.filtro = filtro;
    }
}
