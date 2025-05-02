package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioEmpresasIrregulares", pattern = "/relatorio/rol-empresas-irregulares", viewId = "/faces/tributario/rolempresasirregulares/relatorio/relatorio.xhtml")
})
public class RelatorioEmpresasIrregularesControlador {


    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;

    private Long codigoInicial;
    private Long codigoFinal;
    private Integer exercicioInicial;
    private Integer exercicioFinal;
    private String inscricaoCadastralInicial;
    private String inscricaoCadastralFinal;
    private List<Servico> servicos;
    private Date inseridaEmInicial;
    private Date inseridaEmFinal;
    private Date removidaEmInicial;
    private Date removidaEmFinal;
    private List<Irregularidade> irregularidades;
    private String numeroProtocolo;
    private String anoProtocolo;
    private List<MonitoramentoFiscal> monitoramentos;
    private List<UsuarioSistema> usuarios;
    private UsuarioSistema usuario;
    private MonitoramentoFiscal monitoramento;
    private Servico servico;
    private Irregularidade irregularidade;
    private SituacaoEmpresaIrregular.Situacao situacao;
    private CadastroEconomico cadastroEconomico;

    @URLAction(mappingId = "novoRelatorioEmpresasIrregulares", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoRelatorio() {
        servicos = Lists.newArrayList();
        irregularidades = Lists.newArrayList();
        monitoramentos = Lists.newArrayList();
        usuarios = Lists.newArrayList();
    }

    public void adicionarUsuario() {
        usuarios.add(usuario);
        usuario = null;
    }

    public void adicionarMonitoramento() {
        monitoramentos.add(monitoramento);
        monitoramento = null;
    }

    public void adicionarIrregularidade() {
        irregularidades.add(irregularidade);
        irregularidade = null;
    }

    public void adicionarServico() {
        servicos.add(servico);
        servico = null;
    }

    private void validarFiltros() throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        if ((codigoInicial != null || codigoFinal != null) && (codigoInicial == null || codigoFinal == null)) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar o código inicial e final");
        }
        if ((exercicioInicial != null || exercicioFinal != null) && (exercicioInicial == null || exercicioFinal == null)) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar o exercício inicial e final");
        }
        if ((!Strings.isNullOrEmpty(inscricaoCadastralInicial) || !Strings.isNullOrEmpty(inscricaoCadastralFinal)) && (Strings.isNullOrEmpty(inscricaoCadastralInicial) || Strings.isNullOrEmpty(inscricaoCadastralFinal))) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar o C.M.C inicial e final");
        }
        if ((inseridaEmInicial != null || inseridaEmFinal != null) && (inseridaEmInicial == null || inseridaEmFinal == null)) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar a data inserida inicial e final");
        }
        if ((removidaEmInicial != null || removidaEmFinal != null) && (removidaEmInicial == null || removidaEmFinal == null)) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar a data removida inicial e final");
        }
        if ((!Strings.isNullOrEmpty(anoProtocolo) || !Strings.isNullOrEmpty(numeroProtocolo)) && (Strings.isNullOrEmpty(anoProtocolo) || Strings.isNullOrEmpty(numeroProtocolo))) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar o número e ano do protocolo");
        }
        ve.lancarException();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarFiltros();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setApi("tributario/empresas-irregulares/");
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setNomeRelatorio("Relatório de Empresas Irregulares");
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "Tributário");
            dto.adicionarParametro("USUARIO", sistemaControlador.getUsuarioCorrente().getNome(),false);
            dto.adicionarParametro("condicao", montarCondicao());
            dto.adicionarParametro("NOMERELATORIO", "Relatório de Empresas Irregulares");
            ReportService.getInstance().gerarRelatorio(sistemaControlador.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    private String montarCondicao() {
        String condicao = "";
        String complemento = " where ";

        if (codigoInicial != null) {
            condicao += complemento + " empresa.codigo between " + codigoInicial + " and " + codigoFinal;
            complemento = " and ";
        }
        if (exercicioInicial != null) {
            condicao += complemento + " ex.ano between " + exercicioInicial + " and " + exercicioFinal;
            complemento = " and ";
        }
        if (!Strings.isNullOrEmpty(inscricaoCadastralInicial)) {
            condicao += complemento + " ce.inscricaoCadastral between " + inscricaoCadastralInicial + " and " + inscricaoCadastralFinal;
            complemento = " and ";
        }
        if (inseridaEmInicial != null) {
            condicao += complemento + " empresa.id in (select emp.id " +
                "                               from SituacaoEmpresaIrregular situacao " +
                "                                       inner join EMPRESAIRREGULAR emp on SITUACAO.EMPRESAIRREGULAR_ID = emp.ID " +
                "                               where (situacao.situacao = '" + SituacaoEmpresaIrregular.Situacao.INSERIDA.name() + "') " +
                "                                   and (trunc(situacao.data) between '" + DataUtil.getDataFormatada(inseridaEmInicial) + "'" +
                "                                   and '" + DataUtil.getDataFormatada(inseridaEmFinal) + "')) ";
            complemento = " and ";
        }
        if (removidaEmInicial != null) {
            condicao += complemento + " empresa.id in (select emp.id " +
                "                               from SituacaoEmpresaIrregular situacao " +
                "                                       inner join EMPRESAIRREGULAR emp on SITUACAO.EMPRESAIRREGULAR_ID = emp.ID " +
                "                               where (situacao.situacao = '" + SituacaoEmpresaIrregular.Situacao.RETIRADA.name() + "') " +
                "                                   and (trunc(situacao.data) between '" + DataUtil.getDataFormatada(removidaEmInicial) + "'" +
                "                                   and '" + DataUtil.getDataFormatada(removidaEmFinal) + "')) ";
            complemento = " and ";
        }
        if (!Strings.isNullOrEmpty(anoProtocolo)) {
            condicao += complemento + " situacao.numeroProtocolo = " + numeroProtocolo;
            condicao += " and situacao.anoProtocolo = " + anoProtocolo;
            complemento = " and ";
        }
        if (situacao != null) {
            condicao += complemento + " empresa.id in (select emp.id " +
                "                               from SituacaoEmpresaIrregular situacao " +
                "                                       inner join EMPRESAIRREGULAR emp on SITUACAO.EMPRESAIRREGULAR_ID = emp.ID " +
                "                               where situacao.situacao = '" + situacao.name() + "')";
            complemento = " and ";
        }
        if (this.cadastroEconomico != null) {
            condicao += complemento + " ce.id = " + cadastroEconomico.getId();
            complemento = " and ";
        }
        if (!servicos.isEmpty()) {
            condicao += complemento + "SERVICO.ID in (" + Util.getIdsDaListaAsString(servicos) + ")";
            complemento = " and ";
        }
        if (!irregularidades.isEmpty()) {
            condicao += complemento + "irregularidade.ID in (" + Util.getIdsDaListaAsString(irregularidades) + ")";
            complemento = " and ";
        }
        if (!monitoramentos.isEmpty()) {
            condicao += complemento + "mf.ID in (" + Util.getIdsDaListaAsString(monitoramentos) + ")";
            complemento = " and ";
        }
        if (!usuarios.isEmpty()) {
            condicao += complemento + "us.ID in (" + Util.getIdsDaListaAsString(usuarios) + ")";
        }
        return condicao;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public List<SelectItem> getTodasSituacoes() {
        return Util.getListSelectItemSemCampoVazio(SituacaoEmpresaIrregular.Situacao.values());
    }

    public SituacaoEmpresaIrregular.Situacao getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoEmpresaIrregular.Situacao situacao) {
        this.situacao = situacao;
    }

    public UsuarioSistema getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioSistema usuario) {
        this.usuario = usuario;
    }

    public MonitoramentoFiscal getMonitoramento() {
        return monitoramento;
    }

    public void setMonitoramento(MonitoramentoFiscal monitoramento) {
        this.monitoramento = monitoramento;
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }

    public Irregularidade getIrregularidade() {
        return irregularidade;
    }

    public void setIrregularidade(Irregularidade irregularidade) {
        this.irregularidade = irregularidade;
    }

    public Long getCodigoInicial() {
        return codigoInicial;
    }

    public void setCodigoInicial(Long codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    public Long getCodigoFinal() {
        return codigoFinal;
    }

    public void setCodigoFinal(Long codigoFinal) {
        this.codigoFinal = codigoFinal;
    }

    public Integer getExercicioInicial() {
        return exercicioInicial;
    }

    public void setExercicioInicial(Integer exercicioInicial) {
        this.exercicioInicial = exercicioInicial;
    }

    public Integer getExercicioFinal() {
        return exercicioFinal;
    }

    public void setExercicioFinal(Integer exercicioFinal) {
        this.exercicioFinal = exercicioFinal;
    }

    public String getInscricaoCadastralInicial() {
        return inscricaoCadastralInicial;
    }

    public void setInscricaoCadastralInicial(String inscricaoCadastralInicial) {
        this.inscricaoCadastralInicial = inscricaoCadastralInicial;
    }

    public String getInscricaoCadastralFinal() {
        return inscricaoCadastralFinal;
    }

    public void setInscricaoCadastralFinal(String inscricaoCadastralFinal) {
        this.inscricaoCadastralFinal = inscricaoCadastralFinal;
    }

    public List<Servico> getServicos() {
        return servicos;
    }

    public void setServicos(List<Servico> servicos) {
        this.servicos = servicos;
    }

    public Date getInseridaEmInicial() {
        return inseridaEmInicial;
    }

    public void setInseridaEmInicial(Date inseridaEmInicial) {
        this.inseridaEmInicial = inseridaEmInicial;
    }

    public Date getInseridaEmFinal() {
        return inseridaEmFinal;
    }

    public void setInseridaEmFinal(Date inseridaEmFinal) {
        this.inseridaEmFinal = inseridaEmFinal;
    }

    public Date getRemovidaEmInicial() {
        return removidaEmInicial;
    }

    public void setRemovidaEmInicial(Date removidaEmInicial) {
        this.removidaEmInicial = removidaEmInicial;
    }

    public Date getRemovidaEmFinal() {
        return removidaEmFinal;
    }

    public void setRemovidaEmFinal(Date removidaEmFinal) {
        this.removidaEmFinal = removidaEmFinal;
    }

    public List<Irregularidade> getIrregularidades() {
        return irregularidades;
    }

    public void setIrregularidades(List<Irregularidade> irregularidades) {
        this.irregularidades = irregularidades;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public String getAnoProtocolo() {
        return anoProtocolo;
    }

    public void setAnoProtocolo(String anoProtocolo) {
        this.anoProtocolo = anoProtocolo;
    }

    public List<MonitoramentoFiscal> getMonitoramentos() {
        return monitoramentos;
    }

    public void setMonitoramentos(List<MonitoramentoFiscal> monitoramentos) {
        this.monitoramentos = monitoramentos;
    }

    public List<UsuarioSistema> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<UsuarioSistema> usuarios) {
        this.usuarios = usuarios;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}
