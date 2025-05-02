package br.com.webpublico.entidades;

import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Romanini
 * Date: 03/01/14
 * Time: 17:50
 * To change this template use File | Settings | File Templates.
 */
@Audited
@Entity
@Etiqueta("Reprocessamento Contábil Histórico")
@Table(name = "REPROCESSCONTABILHISTORICO")
public class ReprocessamentoContabilHistorico extends SuperEntidade implements Serializable {
    private static final String formatoDataHora = "%d:%2$TM:%2$TS%n";
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Etiqueta(value = "Data")
    private Date dataHistorico;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Etiqueta(value = "Data Inicial")
    private Date dataInicial;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Etiqueta(value = "Data Final")
    private Date dataFinal;
    private Double quantoFalta;
    @Etiqueta(value = "Processados")
    private Integer processados;
    @Etiqueta("Processados com Erro")
    private Integer processadosComErro;
    @Tabelavel
    @Etiqueta("Processados sem Erro")
    private Integer processadosSemErro;
    @Tabelavel
    @Etiqueta(value = "Total")
    private Integer total;
    private Long decorrido;
    @ManyToOne
    @Etiqueta(value = "Usuário")
    private UsuarioSistema usuarioSistema;
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    private Long tempo;
    @Temporal(TemporalType.TIMESTAMP)
    @Tabelavel
    @Etiqueta("Data/Hora de Início")
    private Date dataHoraInicio;
    @Temporal(TemporalType.TIMESTAMP)
    @Tabelavel
    @Etiqueta("Data/Hora de Término")
    private Date dataHoraTermino;
    @Tabelavel
    @Etiqueta("Tempo")
    @Transient
    private String tempoFormatado;
    @Etiqueta(value = "Usuário")
    @Transient
    @Tabelavel
    private String usuario;
    private Boolean selecionarEntidadesLog;
    private Boolean reprocessamentoInicial;
    @OneToMany(mappedBy = "reprocesscontabilhistorico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReprocessamentoLancamentoContabilLog> mensagens;

    public ReprocessamentoContabilHistorico() {
        criadoEm = System.nanoTime();
        mensagens = new ArrayList<ReprocessamentoLancamentoContabilLog>();
        total = 0;
    }


    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getQuantoFalta() {
        return quantoFalta;
    }

    public void setQuantoFalta(Double quantoFalta) {
        this.quantoFalta = quantoFalta;
    }

    public Date getDataHistorico() {
        return dataHistorico;
    }

    public void setDataHistorico(Date dataHistorico) {
        this.dataHistorico = dataHistorico;
    }

    public Integer getProcessados() {
        return processados;
    }

    public void setProcessados(Integer processados) {
        this.processados = processados;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = this.total + total;
    }

    public Long getDecorrido() {
        return decorrido;
    }

    public void setDecorrido(Long decorrido) {
        this.decorrido = decorrido;
        if (decorrido != null) {
            setTempoFormatado(formatarTempo(decorrido));
        }
    }

    public Long getTempo() {
        return tempo;
    }

    public void setTempo(Long tempo) {
        this.tempo = tempo;
    }

    public String getTempoFormatado() {
        if (decorrido != null) {
            setTempoFormatado(formatarTempo(decorrido));
        }
        return tempoFormatado;
    }

    private String formatarTempo(Long decorrido) {
        if (decorrido != null) {
            return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(decorrido),
                TimeUnit.MILLISECONDS.toMinutes(decorrido) -
                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(decorrido)),
                TimeUnit.MILLISECONDS.toSeconds(decorrido) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(decorrido)));
        }
        return "00:00:00";
    }

    public void setTempoFormatado(String tempoFormatado) {
        this.tempoFormatado = tempoFormatado;
    }

    public Date getDataHoraInicio() {
        return dataHoraInicio;
    }

    public void setDataHoraInicio(Date dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
    }

    public Date getDataHoraTermino() {
        return dataHoraTermino;
    }

    public void setDataHoraTermino(Date dataHoraTermino) {
        this.dataHoraTermino = dataHoraTermino;
    }

    public List<ReprocessamentoLancamentoContabilLog> getMensagens() {
        return mensagens;
    }

    public void setMensagens(List<ReprocessamentoLancamentoContabilLog> mensagens) {
        this.mensagens = mensagens;
    }

    public Boolean getSelecionarEntidadesLog() {
        return selecionarEntidadesLog;
    }

    public void setSelecionarEntidadesLog(Boolean selecionarEntidadesLog) {
        this.selecionarEntidadesLog = selecionarEntidadesLog;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public Double getPorcentagemDoCalculo() {
        if (processados == null || total == null) {
            return 0d;
        }
        return (processados.doubleValue() / total.doubleValue()) * 100;
    }

    public String getTempoDecorrido() {
        long HOUR = TimeUnit.HOURS.toMillis(1);

        decorrido = (System.currentTimeMillis() - tempo);

        return String.format(formatoDataHora, decorrido / HOUR, decorrido % HOUR);
    }

    public String getTempoEstimado() {
        try {
            long HOUR = TimeUnit.HOURS.toMillis(1);
            long unitario = (System.currentTimeMillis() - tempo) / (processados + 1);
            Double qntoFalta = (unitario * (total - processados.doubleValue()));

            return String.format(formatoDataHora, qntoFalta.longValue() / HOUR, qntoFalta.longValue() % HOUR);
        } catch (Exception e) {
            return "0";
        }
    }

    public int getQuantidade() {
        return (total - processados);
    }

    public static String getFormatoDataHora() {
        return formatoDataHora;
    }

    public Boolean getReprocessamentoInicial() {
        return reprocessamentoInicial;
    }

    public void setReprocessamentoInicial(Boolean reprocessamentoInicial) {
        this.reprocessamentoInicial = reprocessamentoInicial;
    }

    public Integer getProcessadosComErro() {
        return processadosComErro;
    }

    public void setProcessadosComErro(Integer processadosComErro) {
        this.processadosComErro = processadosComErro;
    }

    public Integer getProcessadosSemErro() {
        return processadosSemErro;
    }

    public void setProcessadosSemErro(Integer processadosSemErro) {
        this.processadosSemErro = processadosSemErro;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public List<String> recuperarTipoEventos() {
        try {
            HashSet<String> retorno = new HashSet<>();
            for (ReprocessamentoLancamentoContabilLog mensagen : this.mensagens) {
                if (mensagen.getEventoContabil() != null) {
                    retorno.add(mensagen.getEventoContabil().getTipoEventoContabil().getDescricao());
                }
            }
            return new ArrayList<>(retorno);
        } catch (Exception e) {
            return new ArrayList<String>();
        }
    }

    public List<ReprocessamentoLancamentoContabilLog> getMensagensSucesso() {
        List<ReprocessamentoLancamentoContabilLog> retorno = Lists.newArrayList();
        for (ReprocessamentoLancamentoContabilLog mensagem : mensagens) {
            if (!mensagem.getLogDeErro() && !Strings.isNullOrEmpty(mensagem.getClasseObjeto())) {
                retorno.add(mensagem);
            }
        }
        return retorno;
    }

    public List<ReprocessamentoLancamentoContabilLog> getMensagensFalha() {
        List<ReprocessamentoLancamentoContabilLog> retorno = Lists.newArrayList();
        for (ReprocessamentoLancamentoContabilLog mensagem : mensagens) {
            if (mensagem.getLogDeErro() && !Strings.isNullOrEmpty(mensagem.getClasseObjeto())) {
                retorno.add(mensagem);
            }
        }
        return retorno;
    }

    @Override
    public String toString() {
        return "Reprocessados: " + processados + " - Data Inicial: " + DataUtil.getDataFormatada(dataInicial) + " à Data Final: " + DataUtil.getDataFormatada(dataFinal) + "(Reprocessado em: " + DataUtil.getDataFormatada(dataHistorico) + ")";
    }
}
