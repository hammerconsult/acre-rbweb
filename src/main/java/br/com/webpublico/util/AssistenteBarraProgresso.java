package br.com.webpublico.util;

import br.com.webpublico.agendamentotarefas.SingletonAgendamentoTarefas;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.exception.ValidacaoException;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import net.sf.jasperreports.engine.JasperPrint;

import javax.faces.application.FacesMessage;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class AssistenteBarraProgresso implements DetailProcessAsync {

    private UUID id;
    private static final String formatoDataHora = "%d:%2$TM:%2$TS%n";
    private Integer calculados, total;
    private Long decorrido, tempo;
    private Double qntoFalta;
    private String descricaoProcesso;
    private UsuarioSistema usuarioSistema;
    private String usuarioBancoDados;
    private Exercicio exercicio;
    private Date dataAtual;
    private List<String> mensagens;
    private List<JasperPrint> itensJasperPrint;
    private String caminhoReport;
    private String caminhoSubReport;
    private String caminhoImagemReport;
    private Object selecionado;
    private List<FacesMessage> mensagensValidacaoFacesUtil;
    private Object filtro;
    private Boolean registroNovo;
    private StringBuilder logHtml;
    private boolean executando;
    private boolean interromper;
    private SingletonAgendamentoTarefas singletonAgendamentoTarefas;
    private ValidacaoException validacaoException;
    private String ip;
    private int quantidadeErros;

    public AssistenteBarraProgresso() {
        id = UUID.randomUUID();
        itensJasperPrint = Lists.newArrayList();
        mensagens = Lists.newArrayList();
        mensagensValidacaoFacesUtil = Lists.newArrayList();
        registroNovo = Boolean.TRUE;
        this.logHtml = new StringBuilder();
        executando = false;
        interromper = false;
        quantidadeErros = 0;
        validacaoException = new ValidacaoException();
        zerarContadoresProcesso();
        adicionarProcessoAoAcompanhamento();
    }

    public AssistenteBarraProgresso(UsuarioSistema usuarioSistema, String descricaoProcesso, Integer total) {
        this();
        this.usuarioSistema = usuarioSistema;
        this.descricaoProcesso = descricaoProcesso;
        this.total = total;
        adicionarProcessoAoAcompanhamento();
    }

    public AssistenteBarraProgresso(String descricaoProcesso, Integer total) {
        this();
        this.descricaoProcesso = descricaoProcesso;
        this.total = total;
        adicionarProcessoAoAcompanhamento();
    }

    public AssistenteBarraProgresso(int total, Object selecionado, Date dataAtual) {
        this();
        this.total = total;
        this.selecionado = selecionado;
        this.dataAtual = dataAtual;
        adicionarProcessoAoAcompanhamento();
    }

    public AssistenteBarraProgresso(Integer total) {
        this();
        this.total = total;
        adicionarProcessoAoAcompanhamento();
    }

    public Boolean getRegistroNovo() {
        return registroNovo;
    }

    public void setRegistroNovo(Boolean registroNovo) {
        this.registroNovo = registroNovo;
    }

    public static String getFormatoDataHora() {
        return formatoDataHora;
    }

    public synchronized void conta() {
        contar(1);
    }

    public Object getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Object selecionado) {
        this.selecionado = selecionado;
    }

    public Object getFiltro() {
        return filtro;
    }

    public void setFiltro(Object filtro) {
        this.filtro = filtro;
    }

    public int getQuantidadeErros() {
        return quantidadeErros;
    }

    public void setQuantidadeErros(int quantidadeErros) {
        this.quantidadeErros = quantidadeErros;
    }

    public void contar(int quantidade) {
        calculados = calculados + quantidade;
        if (getPorcentagemDoCalculo() >= 100) {
            removerProcessoDoAcompanhamento();
        }
    }

    private void adicionarProcessoAoAcompanhamento() {
        if (total != null && total > 0)
            getSingletonAgendamentoTarefas().addProcesso(this);
    }

    public void removerProcessoDoAcompanhamento() {
        getSingletonAgendamentoTarefas().removeProcesso(this);
    }

    private SingletonAgendamentoTarefas getSingletonAgendamentoTarefas() {
        if (singletonAgendamentoTarefas == null) {
            singletonAgendamentoTarefas = (SingletonAgendamentoTarefas) Util.getSpringBeanPeloNome("singletonAgendamentoTarefas");
        }
        return singletonAgendamentoTarefas;
    }

    public Double getPorcentagemDoCalculo() {
        try {
            return new BigDecimal(((calculados.doubleValue() / total.doubleValue()) * 100)).setScale(0, RoundingMode.UP).doubleValue();
        } catch (Exception ex) {
            return 0d;
        }
    }

    public Boolean getAndamento() {
        return getPorcentagemDoCalculo() > 0 && getPorcentagemDoCalculo() < 100.0;
    }

    public String getTempoDecorrido() {
        long HOUR = TimeUnit.HOURS.toMillis(1);
        decorrido = (System.currentTimeMillis() - tempo);
        return String.format(formatoDataHora, decorrido / HOUR, decorrido % HOUR);
    }

    public String getTempoEstimado() {
        long HOUR = TimeUnit.HOURS.toMillis(1);
        try {
            long unitario = (System.currentTimeMillis() - tempo) / (calculados + 1);
            qntoFalta = (unitario * (total - calculados.doubleValue()));
            return String.format(formatoDataHora, qntoFalta.longValue() / HOUR, qntoFalta.longValue() % HOUR);
        } catch (Exception ex) {
            return "00:00:00";
        }
    }

    public Integer getCalculados() {
        return calculados;
    }

    public void setCalculados(Integer calculados) {
        this.calculados = calculados;
        if (calculados.equals(total)) {
            removerProcessoDoAcompanhamento();
        }
    }

    @Override
    public Integer getTotal() {
        return total;
    }

    @Override
    public Double getPorcentagemExecucao() {
        return getPorcentagemDoCalculo();
    }

    public void setTotal(Integer total) {
        this.total = total;
        adicionarProcessoAoAcompanhamento();
    }

    public Long getDecorrido() {
        return decorrido;
    }

    public void setDecorrido(Long decorrido) {
        this.decorrido = decorrido;
    }

    public Long getTempo() {
        return tempo;
    }

    public void setTempo(Long tempo) {
        this.tempo = tempo;
    }

    public Double getQntoFalta() {
        return qntoFalta;
    }

    public void setQntoFalta(Double qntoFalta) {
        this.qntoFalta = qntoFalta;
    }

    public String getDescricaoProcesso() {
        return Strings.isNullOrEmpty(descricaoProcesso) ? "Processo Assíncrono " + id : descricaoProcesso;
    }

    public void setDescricaoProcesso(String descricaoProcesso) {
        this.descricaoProcesso = descricaoProcesso;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public String getUsuarioBancoDados() {
        return usuarioBancoDados;
    }

    public void setUsuarioBancoDados(String usuarioBancoDados) {
        this.usuarioBancoDados = usuarioBancoDados;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Date getDataAtual() {
        return dataAtual;
    }

    public void setDataAtual(Date dataAtual) {
        this.dataAtual = dataAtual;
    }

    public List<String> getMensagens() {
        return mensagens;
    }

    public void setMensagens(List<String> mensagens) {
        this.mensagens = mensagens;
    }

    public List<JasperPrint> getItensJasperPrint() {
        return itensJasperPrint;
    }

    public void setItensJasperPrint(List<JasperPrint> itensJasperPrint) {
        this.itensJasperPrint = itensJasperPrint;
    }

    public String getCaminhoReport() {
        return caminhoReport;
    }

    public void setCaminhoReport(String caminhoReport) {
        this.caminhoReport = caminhoReport;
    }

    public String getCaminhoSubReport() {
        return caminhoSubReport;
    }

    public void setCaminhoSubReport(String caminhoSubReport) {
        this.caminhoSubReport = caminhoSubReport;
    }

    public String getCaminhoImagemReport() {
        return caminhoImagemReport;
    }

    public void setCaminhoImagemReport(String caminhoImagemReport) {
        this.caminhoImagemReport = caminhoImagemReport;
    }

    public ValidacaoException getValidacaoException() {
        return validacaoException;
    }

    public void setValidacaoException(ValidacaoException validacaoException) {
        this.validacaoException = validacaoException;
    }

    public boolean isExecutando() {
        return executando;
    }

    public void setExecutando(boolean executando) {
        this.executando = executando;
        if (!this.executando) {
            removerProcessoDoAcompanhamento();
        }
    }

    public boolean isInterromper() {
        return interromper;
    }

    public void setInterromper(boolean interromper) {
        this.interromper = interromper;
    }

    public void zerarContadoresProcesso() {
        setCalculados(0);
        setTotal(0);
        setTempo(System.currentTimeMillis());
    }

    public StringBuilder getLogHtml() {
        return logHtml;
    }

    public void setLogHtml(StringBuilder logHtml) {
        this.logHtml = logHtml;
    }

    public void adicionarLogHtml(String log) {
        this.getLogHtml()
            .append(DataUtil.getDataFormatada(new Date(), "dd/MM/yyyy hh:mm:ss"))
            .append(" - ")
            .append(log)
            .append(". ")
            .append("</br>");
    }

    public synchronized void incrementarContador() {
        conta();
    }

    public void decrementarContador() {
        this.calculados--;
    }

    public List<FacesMessage> getMensagensValidacaoFacesUtil() {
        return mensagensValidacaoFacesUtil;
    }

    public void setMensagensValidacaoFacesUtil(List<FacesMessage> mensagensValidacaoFacesUtil) {
        this.mensagensValidacaoFacesUtil = mensagensValidacaoFacesUtil;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public static AssistenteBarraProgresso getAssistenteBarraProgresso(Date dataAtual, Object selecionado, UsuarioSistema usuarioSistema) {
        AssistenteBarraProgresso assistenteBarraProgresso = new AssistenteBarraProgresso();
        assistenteBarraProgresso.zerarContadoresProcesso();
        assistenteBarraProgresso.setDataAtual(dataAtual);
        assistenteBarraProgresso.setSelecionado(selecionado);
        assistenteBarraProgresso.setUsuarioSistema(usuarioSistema);
        return assistenteBarraProgresso;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssistenteBarraProgresso that = (AssistenteBarraProgresso) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String getUsuario() {
        return usuarioSistema != null ? usuarioSistema.getNome() : "";
    }

    @Override
    public String getDescricao() {
        return descricaoProcesso;
    }
}
