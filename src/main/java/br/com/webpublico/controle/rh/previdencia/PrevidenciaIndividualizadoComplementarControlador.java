package br.com.webpublico.controle.rh.previdencia;

import br.com.webpublico.entidades.MatriculaFP;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidadesauxiliares.FiltroPrevidenciaContribuicao;
import br.com.webpublico.entidadesauxiliares.rh.PrevidenciaContribuicaoIndividualizada;
import br.com.webpublico.entidadesauxiliares.rh.relatorio.AssistentePrevidenciaContribuicao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.MatriculaFPFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.VinculoFPFacade;
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
import java.util.List;



@ManagedBean(name = "previdenciaIndividualizadoComplementarControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "contribuicaoIndividualizadoComplementar", pattern = "/relatorio/relatorio-individualizado-previdencia-complementar/",
        viewId = "/faces/rh/relatorios/previdencia/contribuicao-individualizado-complementar.xhtml")
})
public class PrevidenciaIndividualizadoComplementarControlador implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(PrevidenciaContribuicaoIndividualizadaControlador.class);
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private MatriculaFPFacade matriculaFPFacade;

    public FiltroPrevidenciaContribuicao filtro;

    @URLAction(mappingId = "contribuicaoIndividualizadoComplementar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        limparFiltros();
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
            RelatorioDTO dto = montarRelatorioDTO(preencherAssistente());
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setApi("rh/previdencia-contribuicao-individualizada-complementar/");
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

    public void limparFiltros() {
        filtro = new FiltroPrevidenciaContribuicao();
        filtro.setVinculoFP(null);
        filtro.setInicio(null);
        filtro.setTermino(null);
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


    public RelatorioDTO montarRelatorioDTO(AssistentePrevidenciaContribuicao assistente) {
        PrevidenciaContribuicaoIndividualizada prev = new PrevidenciaContribuicaoIndividualizada();
        assistente.setContribuicaoIndividualizada(definirCabecalho(prev, assistente));


        RelatorioDTO dto = new RelatorioDTO();
        dto.setNomeParametroBrasao("BRASAO");
        dto.adicionarParametro("USUARIO", sistemaFacade.getLogin(), false);
        dto.setNomeRelatorio("Relatório Individualizado de Previdência Complementar");
        dto.adicionarParametro("PREFEITURA", "MUNICÍPIO DE RIO BRANCO");
        dto.adicionarParametro("DATAOPERACAO", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
        dto.setNomeParametroSubreportDir("SUBREPORT_DIR");
        dto.adicionarParametro("NOMERELATORIO", "Relatório Individualizado de Previdência Complementar");
        dto.adicionarParametro("INICIO", Util.parseMonthYear(assistente.getInicio()));
        dto.adicionarParametro("TERMINO", Util.parseMonthYear(assistente.getTermino() != null
            ? assistente.getTermino() : sistemaFacade.getDataOperacao()));
        dto.adicionarParametro("MODULO", "Recursos Humanos");
        dto.adicionarParametro("NUMERO_VINCULO", assistente.getContrato());
        dto.adicionarParametro("MATRICULA_VINCULO", assistente.getMatriculaFP().getMatricula());
        dto.adicionarParametro("PESSOA_CPF_VINCULO", assistente.getMatriculaFP().getPessoa().getCpf());
        dto.adicionarParametro("PESSOA_NOME_MATRICULA", assistente.getMatriculaFP().getPessoa().getNome());
        dto.adicionarParametro("DATA_INICIO", assistente.getInicio().getTime());
        dto.adicionarParametro("DATA_TERMINO", assistente.getTermino() != null ? assistente.getTermino().getTime() : sistemaFacade.getDataOperacao().getTime());

        return dto;
    }

    public PrevidenciaContribuicaoIndividualizada definirCabecalho(PrevidenciaContribuicaoIndividualizada prev, AssistentePrevidenciaContribuicao assistente) {
        prev.setMatricula(assistente.getMatriculaFP().getMatricula());
        prev.setCpf(assistente.getMatriculaFP().getPessoa().getCpf());
        prev.setNome(assistente.getMatriculaFP().getPessoa().getNome());
        return prev;
    }

    public FiltroPrevidenciaContribuicao getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroPrevidenciaContribuicao filtro) {
        this.filtro = filtro;
    }
}
