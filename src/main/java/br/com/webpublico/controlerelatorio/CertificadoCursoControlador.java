package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Capacitacao;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.MatriculaFP;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * Created by carlos on 03/11/15.
 */
@ManagedBean(name = "certificadoCursoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "certificadoCurso", pattern = "/rh/certificado-curso", viewId = "/faces/rh/administracaodepagamento/capacitacaoservidor/certificadocurso/novo.xhtml")
})
public class CertificadoCursoControlador extends AbstractReport implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(CertificadoCursoControlador.class);

    private MatriculaFP matriculaFP;
    @EJB
    private MatriculaFPFacade matriculaFPFacade;
    private Capacitacao capacitacao;
    private String funcao;
    private PessoaFisica responsavel;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private EventoCapacitacaoFacade eventoCapacitacaoFacade;
    private String filtros;
    private Boolean todosServidores;
    @EJB
    private SistemaFacade sistemaFacade;

    public List<Capacitacao> completarCapacitacao(String parte) {
        return eventoCapacitacaoFacade.listaEventosCapacitacao(parte);
    }

    public CertificadoCursoControlador() {
        geraNoDialog = true;
    }

    public List<MatriculaFP> completarMatriculaFP(String parte) {
        return matriculaFPFacade.buscarMatriculasPorCapacitacao(capacitacao, parte);
    }

    @URLAction(mappingId = "certificadoCurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        setTodosServidores(true);
        capacitacao = null;
        matriculaFP = null;
        responsavel = null;
        funcao = "Diretor Escola Municipal do Servidor";
    }

    private void validarGeracaoRelatorio() {
        ValidacaoException ve = new ValidacaoException();
        if (capacitacao == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Evento de Capacitação");
        }
        if (responsavel == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Responsvél pelo Certificado.");
        }
        if (funcao == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Função do Responsável pelo Certificado. Ex: Diretor do Departamento de PMRB  ");
        }
        ve.lancarException();
    }

    public void gerarRelatorio() {
        try {
            validarGeracaoRelatorio();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setApi("rh/relatorio-certificado-curso/");
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
        dto.setNomeParametroBrasao("IMAGEM");
        dto.setNomeRelatorio("CERTIFICADO CURSO");
        dto.adicionarParametro("RESPONSAVEL", responsavel.getNome());
        dto.adicionarParametro("FUNCAO", funcao);
        dto.adicionarParametro("LOCAL", getLocal());
        dto.adicionarParametro("TEXTO", getTexto().replace("$EVENTO", capacitacao.getNome()).replace
            ("$INICIO", DataUtil.getDataFormatada(capacitacao.getDataInicio())).replace
            ("$CARGAHORARIA", capacitacao.getCargaHoraria().toString()));
        dto.adicionarParametro("ID_CAPACITACAO", capacitacao.getId());
        dto.adicionarParametro("TODOS_SERVIDORES", todosServidores);
        if (matriculaFP != null) {
            dto.adicionarParametro("PESSOA_ID", matriculaFP.getPessoa().getId());
            dto.adicionarParametro("MATRICULA", matriculaFP.getMatricula());
        }
        return dto;
    }

    public String getTexto() {
        return "    Pela participação no(a) $EVENTO " +
            "realizado(a) pela Secretaria de Administração e Gestão de Pessoas, através da Escola do Servidor Municipal," +
            " no dia $INICIO, com carga horária de $CARGAHORARIA hora/aula.";
    }

    public MatriculaFP getMatriculaFP() {
        return matriculaFP;
    }

    public void setMatriculaFP(MatriculaFP matriculaFP) {
        this.matriculaFP = matriculaFP;
    }

    public Capacitacao getCapacitacao() {
        return capacitacao;
    }

    public void setCapacitacao(Capacitacao capacitacao) {
        this.capacitacao = capacitacao;
    }

    public String getFiltros() {
        return filtros;
    }

    public void setFiltros(String filtros) {
        this.filtros = filtros;
    }

    public Boolean getTodosServidores() {
        return todosServidores;
    }

    public void setTodosServidores(Boolean todosServidores) {
        this.todosServidores = todosServidores;
    }

    public String getFuncao() {
        return funcao;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }

    public PessoaFisica getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(PessoaFisica responsavel) {
        this.responsavel = responsavel;
    }

    public String getLocal() {
        String cidade = "";
        try {
            HierarquiaOrganizacional raizHierarquia = hierarquiaOrganizacionalFacade.getRaizHierarquia(getSistemaFacade().getDataOperacao());
            cidade = raizHierarquia.getSubordinada().getCidade() + " - " + raizHierarquia.getSubordinada().getUf();
        } catch (NullPointerException e) {
            cidade = "Rio Branco - Acre";
        }
        DateTime dataOperacao = new DateTime(getSistemaFacade().getDataOperacao());
        cidade += ", " + DataUtil.getDescricaoMes(dataOperacao.getMonthOfYear()) + " de " + dataOperacao.getYear();
        return cidade;
    }
}
