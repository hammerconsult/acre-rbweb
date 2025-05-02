package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.SituacaoFuncional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.SituacaoFuncionalFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
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
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Paschualleto
 * Date: 15/07/14
 * Time: 18:17
 * To change this template use File | Settings | File Templates.
 */
@ViewScoped
@ManagedBean
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-provimentos-por-servidor", pattern = "/relatorio-provimentos-por-servidor/", viewId = "/faces/rh/relatorios/relatorioprovimentosagrupservidor.xhtml")
})
public class RelatorioProvAgrupServidorControlador implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private SituacaoFuncionalFacade situacaoFuncionalFacade;
    private SituacaoFuncional situacaoFuncional;
    private Date dtInicial;
    private Date dtFinal;
    private String matriculaInicial;
    private String matriculaFinal;
    private Boolean todasSituacoes;
    private Boolean relDetalhado;
    private String filtro;
    protected static final Logger logger = LoggerFactory.getLogger(RelatorioProvAgrupServidorControlador.class);

    public RelatorioProvAgrupServidorControlador() {
        this.relDetalhado = Boolean.FALSE;
        this.matriculaInicial = null;
        this.matriculaFinal = null;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setNomeParametroBrasao("BRASAO");
            dto.setNomeRelatorio("RELATÓRIO DE PROVIMENTOS POR SERVIDO");
            dto.adicionarParametro("SQL", gerarSql());
            dto.adicionarParametro("DATAINICIAL", DataUtil.getDataFormatada(dtInicial));
            dto.adicionarParametro("DATAFINAL", DataUtil.getDataFormatada(dtFinal));
            dto.adicionarParametro("DETALHADO", relDetalhado);
            dto.adicionarParametro("FILTROS", filtro);
            dto.adicionarParametro("MODULO", "Recursos Humanos");
            dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO E GESTÃO");
            dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DE PROVIMENTOS POR SERVIDOR");
            dto.adicionarParametro("USUARIO", sistemaControlador.getUsuarioCorrente().getNome(), false);
            dto.setApi("rh/relatorio-de-provimentos-agrupado-por-servidor/");
            ReportService.getInstance().gerarRelatorio(getSistemaControlador().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
            logger.error("Houve um erro ao tentar gerar o relatório. " + e);
        }
    }

    private String gerarSql() {
        StringBuilder stb = new StringBuilder();
        String juncao = " and ";
        filtro = "";
        filtro += "Ocorrência entre " + DataUtil.getDataFormatada(dtInicial) + " e " + DataUtil.getDataFormatada(dtFinal) + " ";
        if (!todasSituacoes) {
            stb.append(juncao).append(" v.id in(select situacaoContrato.contratofp_id from SituacaoContratoFP situacaoContrato inner join situacaoFuncionaL sit on sit.id = situacaoContrato.situacaoFuncional_id ")
                .append(" where to_date('").append(DataUtil.getDataFormatada(dtFinal)).append("', 'DD/MM/YYYY') between trunc(situacaoContrato.inicioVigencia) and coalesce(trunc(situacaoContrato.finalVigencia), to_date('").append(DataUtil.getDataFormatada(dtFinal)).append("', 'DD/MM/YYYY')) and sit.codigo = ").append(situacaoFuncional.getCodigo()).append(" )");
            filtro += " na Situação " + situacaoFuncional.getDescricao() + " ";
        }

        if (this.matriculaInicial != null && !this.matriculaInicial.trim().isEmpty() && this.matriculaFinal != null && !this.matriculaFinal.trim().isEmpty()) {
            stb.append(juncao).append(" M.MATRICULA BETWEEN ").append(this.matriculaInicial).append(juncao).append(this.matriculaFinal);
            filtro += "para as Matrículas de " + matriculaInicial + " à " + matriculaFinal + " ";
        }
        return stb.toString();
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (todasSituacoes == null || (!todasSituacoes && situacaoFuncional == null)) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Situação deve ser informado.");
        }
        if (!this.matriculaInicial.trim().isEmpty() && !this.matriculaFinal.trim().isEmpty()) {
            if (Integer.parseInt(matriculaFinal) < Integer.parseInt(matriculaInicial)) {
                ve.adicionarMensagemDeCampoObrigatorio("A Matrícula Inicial deve ser inferior a Matrícula Final.");
            }
        }
        if (this.dtInicial == null || this.dtFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar um intervalo de datas.");
        }
        if (this.dtInicial.after(this.dtFinal)) {
            ve.adicionarMensagemDeCampoObrigatorio("A Data Inicial deve ser inferior a Data Final.");
        }
        ve.lancarException();
    }

    @URLAction(mappingId = "relatorio-provimentos-por-servidor", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        this.todasSituacoes = Boolean.TRUE;
        this.relDetalhado = Boolean.FALSE;
        this.dtInicial = new Date();
        this.dtFinal = new Date();
        this.matriculaInicial = null;
        this.matriculaFinal = null;
        this.situacaoFuncional = null;
    }

    public List<SituacaoFuncional> completaSituacao(String parte) {
        return situacaoFuncionalFacade.listaFiltrandoCodigoDescricao(parte.trim());
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public SituacaoFuncional getSituacaoFuncional() {
        return situacaoFuncional;
    }

    public void setSituacaoFuncional(SituacaoFuncional situacaoFuncional) {
        this.situacaoFuncional = situacaoFuncional;
    }

    public Date getDtInicial() {
        return dtInicial;
    }

    public void setDtInicial(Date dtInicial) {
        this.dtInicial = dtInicial;
    }

    public Date getDtFinal() {
        return dtFinal;
    }

    public void setDtFinal(Date dtFinal) {
        this.dtFinal = dtFinal;
    }

    public Boolean getTodasSituacoes() {
        return todasSituacoes;
    }

    public void setTodasSituacoes(Boolean todasSituacoes) {
        this.todasSituacoes = todasSituacoes;
    }

    public Boolean getRelDetalhado() {
        return relDetalhado;
    }

    public void setRelDetalhado(Boolean relDetalhado) {
        this.relDetalhado = relDetalhado;
    }

    public String getMatriculaInicial() {
        return matriculaInicial;
    }

    public void setMatriculaInicial(String matriculaInicial) {
        this.matriculaInicial = matriculaInicial;
    }

    public String getMatriculaFinal() {
        return matriculaFinal;
    }

    public void setMatriculaFinal(String matriculaFinal) {
        this.matriculaFinal = matriculaFinal;
    }
}
