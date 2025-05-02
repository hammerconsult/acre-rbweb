package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.VOOrdemGeralMonitoramento;
import br.com.webpublico.enums.SituacaoMonitoramentoFiscal;
import br.com.webpublico.enums.SituacaoOrdemGeralMonitoramento;
import br.com.webpublico.enums.TipoUsuarioTributario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.OrdemGeralMonitoramentoFacade;
import br.com.webpublico.negocios.PessoaFacade;
import br.com.webpublico.negocios.UsuarioSistemaFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.persistence.criteria.*;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioMonitoramentoFiscal", pattern = "/relatorio/monitoramento-fiscal/", viewId = "/faces/tributario/monitoramentofiscal/relatorio/relatorio.xhtml")
})
@ManagedBean
public class RelatorioMonitoramentoFiscalControlador {

    @EJB
    private OrdemGeralMonitoramentoFacade ordemGeralMonitoramentoFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;

    private List<OrdemGeralMonitoramento> ordensGeral;
    private OrdemGeralMonitoramento ordem;
    private Date dataProgramadaInicial;
    private Date dataProgramadaFinal;
    private UsuarioSistema usuario;
    private SituacaoOrdemGeralMonitoramento[] situacoesOrdem;
    private List<MonitoramentoFiscal> monitoramentos;
    private MonitoramentoFiscal monitoramento;
    private Date dataInicialMonitoramento;
    private Date dataFinalMonitoramento;
    private String inscricaoCadastralInicial;
    private String inscricaoCadastralFinal;
    private String cnpjMonitoramento;
    private UsuarioSistema fiscal;
    private SituacaoMonitoramentoFiscal situacaoMonitoramento;
    private String numeroProtocolo;
    private String anoProtocolo;
    private String filtrosUtilizados;

    @URLAction(mappingId = "novoRelatorioMonitoramentoFiscal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        ordensGeral = Lists.newArrayList();
        monitoramentos = Lists.newArrayList();
    }

    private void validarFiltros() {
        ValidacaoException ve = new ValidacaoException();
        if ((dataProgramadaInicial != null || dataProgramadaFinal != null) && (dataProgramadaInicial == null || dataProgramadaFinal == null)) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar a data programada inicial e final");
        } else if ((dataProgramadaInicial != null && dataProgramadaFinal != null) && dataProgramadaInicial.compareTo(dataProgramadaFinal) > 0) {
            ve.adicionarMensagemDeCampoObrigatorio("A data programada inicial não pode ser maior que a data programada final");
        }
        if ((dataInicialMonitoramento != null || dataFinalMonitoramento != null) && (dataInicialMonitoramento == null || dataFinalMonitoramento == null)) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar a data inicial e final do monitoramento");
        } else if ((dataInicialMonitoramento != null && dataFinalMonitoramento != null) && dataInicialMonitoramento.compareTo(dataFinalMonitoramento) > 0) {
            ve.adicionarMensagemDeCampoObrigatorio("A data inicial do monitormaneot não pode ser maior que a data final do monitoramento");
        }
        if ((!Strings.isNullOrEmpty(inscricaoCadastralInicial) || !Strings.isNullOrEmpty(inscricaoCadastralFinal)) && (Strings.isNullOrEmpty(inscricaoCadastralInicial) || Strings.isNullOrEmpty(inscricaoCadastralFinal))) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar o C.M.C inicial e final");
        }
        if ((!Strings.isNullOrEmpty(anoProtocolo) || !Strings.isNullOrEmpty(numeroProtocolo)) && (Strings.isNullOrEmpty(anoProtocolo) || Strings.isNullOrEmpty(numeroProtocolo))) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar o número e ano do protocolo");
        }
        ve.lancarException();
    }

    public void gerarRelatorio() {
        try {
            validarFiltros();
            AbstractReport abstractReport = AbstractReport.getAbstractReport();
            abstractReport.setGeraNoDialog(true);
            List<VOOrdemGeralMonitoramento> dados = ordemGeralMonitoramentoFacade.buscarDadosRelatorio(montarCriteriaComOsFiltros());
            if (dados.isEmpty()) {
                new ValidacaoException().adicionarMensagemDeOperacaoNaoRealizada("Nenhum registro foi encontrado com os filtros utilizados").lancarException();
            }
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(dados);
            HashMap parametros = Maps.newHashMap();
            parametros.put("MODULO", "Tributário");
            parametros.put("BRASAO", abstractReport.getCaminhoImagem());
            parametros.put("USUARIO", sistemaControlador.getUsuarioCorrente().getNome());
            parametros.put("NOMERELATORIO", "RELATÓRIO DE ACOMPANHAMENTO DE MONITORAMENTO FISCAL");
            parametros.put("FILTROS", filtrosUtilizados);
            try {
                abstractReport.gerarRelatorioComDadosEmCollection("RelatorioMonitoramentosFiscais.jasper", parametros, ds);
            } catch (Exception e) {
                FacesUtil.addErroAoGerarRelatorio(e.getLocalizedMessage());
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private CriteriaQuery montarCriteriaComOsFiltros() {
        filtrosUtilizados = "";
        CriteriaBuilder builder = ordemGeralMonitoramentoFacade.getEm().getCriteriaBuilder();
        CriteriaQuery<OrdemGeralMonitoramento> query = builder.createQuery(OrdemGeralMonitoramento.class);
        Root<OrdemGeralMonitoramento> root = query.from(OrdemGeralMonitoramento.class);
        Join<MonitoramentoFiscal, OrdemGeralMonitoramento> monitoramentoFiscal = root.join("monitoramentosFiscais", JoinType.LEFT);
        Join<FiscalMonitoramento, MonitoramentoFiscal> fiscalMonitoramento = monitoramentoFiscal.join("fiscaisMonitoramento", JoinType.LEFT);
        Join<CadastroEconomico, MonitoramentoFiscal> cadastroEconomico = monitoramentoFiscal.join("cadastroEconomico");
        List<Predicate> predicates = Lists.newArrayList();
        if (!ordensGeral.isEmpty()) {
            filtrosUtilizados += "Ordens Geral: ";
            for (OrdemGeralMonitoramento ordem : ordensGeral) {
                filtrosUtilizados += ordem.getNumero() + ", ";
            }
            filtrosUtilizados = filtrosUtilizados.substring(0, filtrosUtilizados.length() - 2);
            filtrosUtilizados += "; ";
            predicates.add(root.in(ordensGeral));
        }
        if (dataProgramadaInicial != null) {
            predicates.add(builder.or(builder.between(root.get("dataInicial"), dataProgramadaInicial, dataProgramadaFinal),
                builder.between(root.get("dataFinal"), dataProgramadaInicial, dataProgramadaFinal)));
            filtrosUtilizados += "Data Programada Inicial: " + DataUtil.getDataFormatada(dataProgramadaInicial) + "; ";
            filtrosUtilizados += "Data Programada Final: " + DataUtil.getDataFormatada(dataProgramadaFinal) + "; ";
        }
        if (usuario != null) {
            predicates.add(builder.equal(fiscalMonitoramento.get("auditorFiscal"), usuario));
            filtrosUtilizados += "Usuário Responsável pela Ordem Geral: " + usuario.getLogin() + "; ";
        }
        if (situacoesOrdem != null && situacoesOrdem.length > 0) {
            predicates.add(root.get("situacaoOGM").in(situacoesOrdem));
            filtrosUtilizados += "Situações da Ordem Geral: ";
            for (SituacaoOrdemGeralMonitoramento situacao : situacoesOrdem) {
                filtrosUtilizados += situacao.getDescricao() + ", ";
            }
            filtrosUtilizados = filtrosUtilizados.substring(0, filtrosUtilizados.length() - 2);
            filtrosUtilizados += "; ";
        }
        if (!monitoramentos.isEmpty()) {
            predicates.add(monitoramentoFiscal.in(monitoramentos));
            filtrosUtilizados += "Monitoramentos: ";
            for (MonitoramentoFiscal monitoramento : monitoramentos) {
                filtrosUtilizados += monitoramento.toString() + ", ";
            }
            filtrosUtilizados = filtrosUtilizados.substring(0, filtrosUtilizados.length() - 2);
            filtrosUtilizados += "; ";
        }
        if (dataInicialMonitoramento != null) {
            predicates.add(builder.or(builder.between(monitoramentoFiscal.get("dataInicialMonitoramento"), dataInicialMonitoramento, dataFinalMonitoramento),
                builder.between(monitoramentoFiscal.get("dataFinalMonitoramento"), dataInicialMonitoramento, dataFinalMonitoramento)));
            filtrosUtilizados += "Data Monitoramento Inicial: " + DataUtil.getDataFormatada(dataInicialMonitoramento) + "; ";
            filtrosUtilizados += "Data Monitoramento Final: " + DataUtil.getDataFormatada(dataFinalMonitoramento) + "; ";
        }
        if (!Strings.isNullOrEmpty(cnpjMonitoramento)) {
            Pessoa pessoaBuscar = pessoaFacade.buscarPessoaPorCpfOrCnpj(cnpjMonitoramento.trim());
            predicates.add(builder.equal(cadastroEconomico.get("pessoa"), pessoaBuscar));
            filtrosUtilizados += "CNPJ: " + cnpjMonitoramento + "; ";
        }
        if (fiscal != null) {
            predicates.add(builder.equal(fiscalMonitoramento.get("auditorFiscal"), fiscal));
            filtrosUtilizados += "Fiscal Responsável pelo Monitoramento: " + fiscal.getLogin() + "; ";
        }
        if (situacaoMonitoramento != null) {
            predicates.add(builder.equal(monitoramentoFiscal.get("situacaoMF"), situacaoMonitoramento));
            filtrosUtilizados += "Situação do Monitoramento: " + situacaoMonitoramento.getDescricao() + "; ";
        }
        if (!Strings.isNullOrEmpty(anoProtocolo)) {
            predicates.add(builder.and(builder.equal(root.<Comparable>get("anoProtocolo"), anoProtocolo.trim()), builder.equal(root.<Comparable>get("numeroProtocolo"), numeroProtocolo.trim())));
            filtrosUtilizados += "Protocolo: " + (numeroProtocolo + "/" + anoProtocolo) + "; ";
        }
        if (!filtrosUtilizados.isEmpty()) {
            filtrosUtilizados = filtrosUtilizados.substring(0, filtrosUtilizados.length() - 2);
        }
        return query.select(root).distinct(true).where(builder.and(predicates.toArray(new Predicate[0])));
    }

    private void validarAdicionarOrdem() {
        ValidacaoException ve = new ValidacaoException();
        if (ordensGeral.contains(ordem)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível adicionar a mesma ordem mais de uma vez");
        }
        ve.lancarException();
    }

    public void adicionarOrdem() {
        try {
            validarAdicionarOrdem();
            ordensGeral.add(ordem);
            ordem = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarAdicionarMonitoramento() {
        ValidacaoException ve = new ValidacaoException();
        if (monitoramentos.contains(monitoramento)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível adicionar o mesmo monitoramento mais de uma vez");
        }
        ve.lancarException();
    }

    public void adicionarMonitoramento() {
        try {
            validarAdicionarMonitoramento();
            monitoramentos.add(monitoramento);
            monitoramento = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public List<UsuarioSistema> completarFiscais(String parte) {
        return usuarioSistemaFacade.listaFiltrandoUsuarioSistemaPorTipo(parte.trim(), TipoUsuarioTributario.FISCAL_TRIBUTARIO);
    }

    public List<SituacaoOrdemGeralMonitoramento> todasAsSituacoesOrdem() {
        return Arrays.asList(SituacaoOrdemGeralMonitoramento.values());
    }

    public List<SituacaoMonitoramentoFiscal> todasAsSituacoesMonitoramento() {
        return Arrays.asList(SituacaoMonitoramentoFiscal.values());
    }

    public OrdemGeralMonitoramentoFacade getOrdemGeralMonitoramentoFacade() {
        return ordemGeralMonitoramentoFacade;
    }

    public void setOrdemGeralMonitoramentoFacade(OrdemGeralMonitoramentoFacade ordemGeralMonitoramentoFacade) {
        this.ordemGeralMonitoramentoFacade = ordemGeralMonitoramentoFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public void setUsuarioSistemaFacade(UsuarioSistemaFacade usuarioSistemaFacade) {
        this.usuarioSistemaFacade = usuarioSistemaFacade;
    }

    public List<OrdemGeralMonitoramento> getOrdensGeral() {
        return ordensGeral;
    }

    public void setOrdensGeral(List<OrdemGeralMonitoramento> ordensGeral) {
        this.ordensGeral = ordensGeral;
    }

    public OrdemGeralMonitoramento getOrdem() {
        return ordem;
    }

    public void setOrdem(OrdemGeralMonitoramento ordem) {
        this.ordem = ordem;
    }

    public Date getDataProgramadaInicial() {
        return dataProgramadaInicial;
    }

    public void setDataProgramadaInicial(Date dataProgramadaInicial) {
        this.dataProgramadaInicial = dataProgramadaInicial;
    }

    public Date getDataProgramadaFinal() {
        return dataProgramadaFinal;
    }

    public void setDataProgramadaFinal(Date dataProgramadaFinal) {
        this.dataProgramadaFinal = dataProgramadaFinal;
    }

    public UsuarioSistema getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioSistema usuario) {
        this.usuario = usuario;
    }

    public SituacaoOrdemGeralMonitoramento[] getSituacoesOrdem() {
        return situacoesOrdem;
    }

    public void setSituacoesOrdem(SituacaoOrdemGeralMonitoramento[] situacoesOrdem) {
        this.situacoesOrdem = situacoesOrdem;
    }

    public List<MonitoramentoFiscal> getMonitoramentos() {
        return monitoramentos;
    }

    public void setMonitoramentos(List<MonitoramentoFiscal> monitoramentos) {
        this.monitoramentos = monitoramentos;
    }

    public MonitoramentoFiscal getMonitoramento() {
        return monitoramento;
    }

    public void setMonitoramento(MonitoramentoFiscal monitoramento) {
        this.monitoramento = monitoramento;
    }

    public Date getDataInicialMonitoramento() {
        return dataInicialMonitoramento;
    }

    public void setDataInicialMonitoramento(Date dataInicialMonitoramento) {
        this.dataInicialMonitoramento = dataInicialMonitoramento;
    }

    public Date getDataFinalMonitoramento() {
        return dataFinalMonitoramento;
    }

    public void setDataFinalMonitoramento(Date dataFinalMonitoramento) {
        this.dataFinalMonitoramento = dataFinalMonitoramento;
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

    public String getCnpjMonitoramento() {
        return cnpjMonitoramento;
    }

    public void setCnpjMonitoramento(String cnpjMonitoramento) {
        this.cnpjMonitoramento = cnpjMonitoramento;
    }

    public UsuarioSistema getFiscal() {
        return fiscal;
    }

    public void setFiscal(UsuarioSistema fiscal) {
        this.fiscal = fiscal;
    }

    public SituacaoMonitoramentoFiscal getSituacaoMonitoramento() {
        return situacaoMonitoramento;
    }

    public void setSituacaoMonitoramento(SituacaoMonitoramentoFiscal situacaoMonitoramento) {
        this.situacaoMonitoramento = situacaoMonitoramento;
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

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public void setPessoaFacade(PessoaFacade pessoaFacade) {
        this.pessoaFacade = pessoaFacade;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public String getFiltrosUtilizados() {
        return filtrosUtilizados;
    }

    public void setFiltrosUtilizados(String filtrosUtilizados) {
        this.filtrosUtilizados = filtrosUtilizados;
    }
}
