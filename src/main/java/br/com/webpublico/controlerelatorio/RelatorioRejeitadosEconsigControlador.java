package br.com.webpublico.controlerelatorio;

import br.com.webpublico.DateUtils;
import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.negocios.*;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.*;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.DateTime;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.*;

/**
 * Created by peixe on 15/09/2015.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "rejeitadosEconsig", pattern = "/relatorio/rejeitados-econsig/",
        viewId = "/faces/rh/relatorios/relatorioRejeitadosEconsig.xhtml")
})

public class RelatorioRejeitadosEconsigControlador implements Serializable, ValidadorEntidade {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private Mes mesInicial;
    private Exercicio exercicioInicial;
    private Mes mesFinal;
    private Exercicio exercicioFinal;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private VinculoFP vinculoFP;
    private ConverterAutoComplete converterHierarquia;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    private ConverterAutoComplete converterContratoFP;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private TipoFiltro tipoFiltro;

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public TipoFiltro getTipoFiltro() {
        return tipoFiltro;
    }

    public void setTipoFiltro(TipoFiltro tipoFiltro) {
        this.tipoFiltro = tipoFiltro;
    }

    public void limparComponentes() {
        vinculoFP = null;
        hierarquiaOrganizacional = null;
    }


    public Converter getConverterHierarquia() {
        if (converterHierarquia == null) {
            converterHierarquia = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterHierarquia;
    }

    public List<HierarquiaOrganizacional> completarHierarquia(String parte) {
        List<HierarquiaOrganizacional> hos = new ArrayList<>();
        //hos.add(hierarquiaOrganizacionalFacade.getRaizHierarquia(UtilRH.getDataOperacao()));
        hos.addAll(hierarquiaOrganizacionalFacade.filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao()));
        return hos;
    }

    public List<VinculoFP> completaContrato(String s) {
        return contratoFPFacade.buscaContratoFiltrandoAtributosVinculoMatriculaFP(s.trim());
    }

    public Converter getConverterContratoFP() {
        if (converterContratoFP == null) {
            converterContratoFP = new ConverterAutoComplete(VinculoFP.class, vinculoFPFacade);
        }
        return converterContratoFP;
    }

    public Mes getMesInicial() {
        return mesInicial;
    }

    public void setMesInicial(Mes mesInicial) {
        this.mesInicial = mesInicial;
    }

    public Exercicio getExercicioInicial() {
        return exercicioInicial;
    }

    public void setExercicioInicial(Exercicio exercicioInicial) {
        this.exercicioInicial = exercicioInicial;
    }

    public Mes getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(Mes mesFinal) {
        this.mesFinal = mesFinal;
    }

    public Exercicio getExercicioFinal() {
        return exercicioFinal;
    }

    public void setExercicioFinal(Exercicio exercicioFinal) {
        this.exercicioFinal = exercicioFinal;
    }

    public List<SelectItem> getMeses() {
        return Util.getListSelectItem(Mes.values(), false);
    }

    @URLAction(mappingId = "rejeitadosEconsig", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void init() {
        DateTime hoje = new DateTime(new Date());
        mesInicial = Mes.getMesToInt(hoje.getMonthOfYear());
        exercicioInicial = sistemaControlador.getExercicioCorrente();
        mesFinal = Mes.getMesToInt(hoje.getMonthOfYear());
        exercicioFinal = sistemaControlador.getExercicioCorrente();
        tipoFiltro = TipoFiltro.VINCULO;
    }

    public void gerarRelatorioRejeitados() {
        try {
            validarConfirmacao();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setApi("rh/relatorio-rejeitados-econsig/");
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

    private RelatorioDTO montarRelatorioDTO() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.setNomeParametroBrasao("BRASAO");
        dto.setNomeRelatorio("RELATÓRIO DE REJEITADOS DO E-CONSIG");
        dto.adicionarParametro("USUARIO", sistemaControlador.getUsuarioCorrente().getNome());
        dto.adicionarParametro("MODULO", "Recursos Humanos");
        dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE GESTÃO E ADMINISTRAÇÃO DE PESSOAS");
        dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DE REJEITADOS DO E-CONSIG");
        dto.adicionarParametro("PREFEITURA", "PREFEITURA DE RIO BRANCO");
        dto.adicionarParametro("PERIODO", "Período: " + mesInicial.getDescricao() + "/" + exercicioInicial.getAno() + " até " + mesFinal.getDescricao() + "/" + exercicioFinal.getAno());
        dto.adicionarParametro("DATAINICIAL", DateUtils.getDataFormatada(Util.criaDataPrimeiroDiaMesJoda(mesInicial.getNumeroMes(), exercicioInicial.getAno()).toDate()));
        dto.adicionarParametro("DATAFINAL", DateUtils.getDataFormatada(Util.criaDataPrimeiroDiaMesJoda(mesFinal.getNumeroMes(), exercicioFinal.getAno()).toDate()));
        if (vinculoFP != null) {
            dto.adicionarParametro("VINCULOFP", vinculoFP.getId());
        }
        if (hierarquiaOrganizacional != null) {
            dto.adicionarParametro("HIERARQUIAORGANIZACIONAL", hierarquiaOrganizacional.getCodigoSemZerosFinais() + "%");
        }
        return dto;
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
        ValidacaoException validacaoException = new ValidacaoException();
        if (mesInicial == null) {
            validacaoException.adicionarMensagemDeCampoObrigatorio("Preencha o Mês Inicial corretamente.");
        }
        if (exercicioInicial == null) {
            validacaoException.adicionarMensagemDeCampoObrigatorio("Preencha o Ano Inicial corretamente.");
        }
        if (mesFinal == null) {
            validacaoException.adicionarMensagemDeCampoObrigatorio("Preencha o Mês Final corretamente.");
        }
        if (exercicioFinal == null) {
            validacaoException.adicionarMensagemDeCampoObrigatorio("Preencha o Ano Final corretamente.");
        }
        validacaoException.lancarException();
        if (exercicioInicial.getAno() > exercicioFinal.getAno()) {
            validacaoException.adicionarMensagemDeOperacaoNaoPermitida("O Ano Inicial deve ser anterior ao Ano Final");
        }
        if (mesInicial.getNumeroMes() > mesFinal.getNumeroMes() && exercicioInicial.getAno().equals(exercicioFinal.getAno())) {
            validacaoException.adicionarMensagemDeOperacaoNaoPermitida("O Mês Inicial deve ser anterior ao Mês Final");
        }
        validacaoException.lancarException();
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public enum TipoFiltro {
        VINCULO, ORGAO;
    }
}

