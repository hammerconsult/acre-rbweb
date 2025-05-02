package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ContaFacade;
import br.com.webpublico.negocios.PPAFacade;
import br.com.webpublico.negocios.ProgramaPPAFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Edi on 22/04/2016.
 */


@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "demonstrativo-ppa-categoria-economica", pattern = "/demonstrativo-ppa-categoria-economica/", viewId = "/faces/financeiro/relatorio/demonstrativo-ppa-categoria-economica.xhtml")
})
@ManagedBean
public class DemonstrativoPpaPorCategoriaEconomicaControlador extends AbstractReport implements Serializable {

    @EJB
    private ProgramaPPAFacade programaPPAFacade;
    @EJB
    private PPAFacade ppaFacade;
    @EJB
    private ContaFacade contaFacade;
    private List<HierarquiaOrganizacional> hierarquiasOrganizacionais;
    private Date dataReferencia;
    private ProgramaPPA programaPPAInicial;
    private ProgramaPPA programaPPAFinal;
    private Exercicio exercicio;
    private PPA ppa;
    private Conta contaDespesa;

    @URLAction(mappingId = "demonstrativo-ppa-categoria-economica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        hierarquiasOrganizacionais = Lists.newArrayList();
        dataReferencia = getSistemaFacade().getDataOperacao();
        programaPPAInicial = null;
        programaPPAFinal = null;
        exercicio = getSistemaFacade().getExercicioCorrente();
        contaDespesa = null;
        recuperarUltimoPpaDoExercicio();
    }

    public void gerarRelatorio() {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("MODULO", "Planejamento Público");
            dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
            dto.adicionarParametro("DATAREFERENCIA", DataUtil.getDataFormatada(dataReferencia));
            dto.adicionarParametro("EXERCICIO", exercicio.getAno().toString());
            dto.adicionarParametro("PPA", ppa.toString());
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/demonstrativo-ppa-por-categoria-economica/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao gerar o relatório: {}", e);
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    private String getNomeRelatorio() {
        return "DEMONSTRATIVO-PPA-CATEGORIA-ECONOMICA" + DataUtil.getDataFormatada(dataReferencia, "ddMMyyyy");
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        parametrosFiltroGeral(parametros);
        parametrosFitroUnidade(parametros);
        return parametros;
    }

    private void parametrosFiltroGeral(List<ParametrosRelatorios> parametros) {
        parametros.add(new ParametrosRelatorios(null, ":DATAREFERENCIA", null, OperacaoRelatorio.IGUAL, DataUtil.getDataFormatada(dataReferencia), null, 0, true));
        parametros.add(new ParametrosRelatorios(" ac.exercicio_id ", ":ID_EXERCICIO", null, OperacaoRelatorio.IGUAL, exercicio.getId(), null, 2, false));

        if (programaPPAInicial != null && programaPPAFinal != null) {
            parametros.add(new ParametrosRelatorios(" prog.codigo ", ":codigoProgInicial", ":codigoProgFinal", OperacaoRelatorio.BETWEEN, programaPPAInicial.getCodigo(), programaPPAFinal.getCodigo(), 1, false));
        }
        if (contaDespesa != null) {
            parametros.add(new ParametrosRelatorios(" c.codigo ", ":codigo", null, OperacaoRelatorio.LIKE, contaDespesa.getCodigoSemZerosAoFinal() + "%", null, 2, false));
        }
    }

    private void parametrosFitroUnidade(List<ParametrosRelatorios> parametros) {
        List<Long> idsUnidades = Lists.newArrayList();
        if (!hierarquiasOrganizacionais.isEmpty()) {
            for (HierarquiaOrganizacional hierarquiaOrganizacional : hierarquiasOrganizacionais) {
                idsUnidades.add(hierarquiaOrganizacional.getSubordinada().getId());
            }
            parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        SimpleDateFormat formatar = new SimpleDateFormat("yyyy");
        if (exercicio == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício deve ser informado.");
        }
        if (dataReferencia == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Referência deve ser informado.");
        } else if (exercicio != null && formatar.format(dataReferencia).compareTo(exercicio.getAno().toString()) != 0) {
            ve.adicionarMensagemDeCampoObrigatorio("A Data de Referência deve estar dentro do exercício selecionado.");
        }
        if (programaPPAInicial != null
            && programaPPAFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Programa Final deve ser informado.");
        }
        if (programaPPAFinal != null
            && programaPPAInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Programa Inicial deve ser informado.");
        }
        ve.lancarException();
    }

    public void recuperarUltimoPpaDoExercicio() {
        try {
            if (exercicio != null) {
                ppa = ppaFacade.ultimoPpaDoExercicio(exercicio);
            } else {
                ppa = null;
            }
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public List<ProgramaPPA> completarProgramaPPA(String parte) {
        if (ppa != null) {
            return programaPPAFacade.buscarProgramasPorPPA(parte.trim(), ppa);
        }
        return Lists.newArrayList();
    }

    public List<Conta> completarContaDespesa(String parte) {
        return contaFacade.listaFiltrandoContaDespesa(parte, getSistemaFacade().getExercicioCorrente());
    }

    public List<HierarquiaOrganizacional> getHierarquiasOrganizacionais() {
        return hierarquiasOrganizacionais;
    }

    public void setHierarquiasOrganizacionais(List<HierarquiaOrganizacional> hierarquiasOrganizacionais) {
        this.hierarquiasOrganizacionais = hierarquiasOrganizacionais;
    }

    public Date getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(Date dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    public ProgramaPPA getProgramaPPAInicial() {
        return programaPPAInicial;
    }

    public void setProgramaPPAInicial(ProgramaPPA programaPPAInicial) {
        this.programaPPAInicial = programaPPAInicial;
    }

    public ProgramaPPA getProgramaPPAFinal() {
        return programaPPAFinal;
    }

    public void setProgramaPPAFinal(ProgramaPPA programaPPAFinal) {
        this.programaPPAFinal = programaPPAFinal;
    }

    public Conta getContaDespesa() {
        return contaDespesa;
    }

    public void setContaDespesa(Conta contaDespesa) {
        this.contaDespesa = contaDespesa;
    }

    public PPA getPpa() {
        return ppa;
    }

    public void setPpa(PPA ppa) {
        this.ppa = ppa;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }
}
