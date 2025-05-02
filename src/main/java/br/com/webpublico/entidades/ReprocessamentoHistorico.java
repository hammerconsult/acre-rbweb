package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoReprocessamentoHistorico;
import br.com.webpublico.util.DataUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
public class ReprocessamentoHistorico extends SuperEntidade implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.DATE)
    private Date dataInicial;
    @Temporal(TemporalType.DATE)
    private Date dataFinal;
    private Integer processados;
    private Integer processadosComErro;
    private Integer processadosSemErro;
    private Integer total;
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataHoraInicio;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataHoraTermino;
    @Enumerated(EnumType.STRING)
    private TipoReprocessamentoHistorico tipoReprocessamentoHistorico;
    @NotAudited
    @OneToMany(mappedBy = "reprocessamentoHistorico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReprocessamentoLog> mensagens;
    private String filtrosUtilizados;

    public ReprocessamentoHistorico() {
        super();
        mensagens = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getProcessados() {
        return processados;
    }

    public void setProcessados(Integer processados) {
        this.processados = processados;
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

    public List<ReprocessamentoLog> getMensagens() {
        return mensagens;
    }

    public void setMensagens(List<ReprocessamentoLog> mensagens) {
        this.mensagens = mensagens;
    }

    public TipoReprocessamentoHistorico getTipoReprocessamentoHistorico() {
        return tipoReprocessamentoHistorico;
    }

    public void setTipoReprocessamentoHistorico(TipoReprocessamentoHistorico tipoReprocessamentoHistorico) {
        this.tipoReprocessamentoHistorico = tipoReprocessamentoHistorico;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getFiltrosUtilizados() {
        return filtrosUtilizados;
    }

    public void setFiltrosUtilizados(String filtrosUtilizados) {
        this.filtrosUtilizados = filtrosUtilizados;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public int getQuantidade() {
        return (total - processados);
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

    public List<ReprocessamentoLog> getMensagensSucesso() {
        List<ReprocessamentoLog> retorno = Lists.newArrayList();
        for (ReprocessamentoLog mensagem : mensagens) {
            if (!mensagem.getLogDeErro() && !Strings.isNullOrEmpty(mensagem.getClasseObjeto())) {
                retorno.add(mensagem);
            }
        }
        return retorno;
    }

    public List<ReprocessamentoLog> getMensagensFalha() {
        List<ReprocessamentoLog> retorno = Lists.newArrayList();
        for (ReprocessamentoLog mensagem : mensagens) {
            if (mensagem.getLogDeErro() && !Strings.isNullOrEmpty(mensagem.getClasseObjeto())) {
                retorno.add(mensagem);
            }
        }
        return retorno;
    }

    public void atualizarTotal(Integer total) {
        if (this.total == null) {
            this.total = 0;
        }
        this.total = this.total + total;
    }

    @Override
    public String toString() {
        return "Reprocessados: " + processados + " - Data Inicial: " + DataUtil.getDataFormatada(dataInicial) + " à Data Final: " + DataUtil.getDataFormatada(dataFinal) + "(Reprocessado em: " + DataUtil.getDataFormatada(dataHoraInicio) + ")";
    }
}
