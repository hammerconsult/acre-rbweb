/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.util.Date;

/**
 * @author reidocrime
 */
@Entity

@Inheritance(strategy = InheritanceType.JOINED)
@Audited
public class ConfiguracaoEvento extends SuperEntidade implements ValidadorVigencia {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Obrigatorio
    @Nullable
    @ManyToOne
    @Etiqueta(value = "Evento Contábil")
    @Pesquisavel
    private EventoContabil eventoContabil;
    @Tabelavel
    @Etiqueta("Tipo de Lançamento")
    @Obrigatorio
    @Nullable
    @Column(name = "TIPOLANCAMENTO")
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    private TipoLancamento tipoLancamento;
    @Tabelavel
    @Etiqueta("Início de Vigência")
    @Nullable
    @Temporal(javax.persistence.TemporalType.DATE)
    @Pesquisavel
    @Obrigatorio
    private Date inicioVigencia;
    @Tabelavel
    @Etiqueta("Fim de Vigência")
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fimVigencia;
    @Transient
    private ModoListagem modoListagem;
    @Transient
    private Boolean erroDuranteProcessamento;
    @Transient
    private String mensagemDeErro;

    public ConfiguracaoEvento() {
        super();
        inicioVigencia = new Date();
        tipoLancamento = TipoLancamento.NORMAL;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    @Override
    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    @Override
    public void setInicioVigencia(Date inicioVigenci) {
        this.inicioVigencia = inicioVigenci;
    }

    @Override
    public Date getFimVigencia() {
        return fimVigencia;
    }

    @Override
    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public Boolean getErroDuranteProcessamento() {
        return erroDuranteProcessamento != null ? erroDuranteProcessamento : Boolean.FALSE;
    }

    public void setErroDuranteProcessamento(Boolean erroDuranteProcessamento) {
        this.erroDuranteProcessamento = erroDuranteProcessamento;
    }

    public String getMensagemDeErro() {
        return mensagemDeErro;
    }

    public void setMensagemDeErro(String mensagemDeErro) {
        this.mensagemDeErro = mensagemDeErro;
    }

    public TipoLancamento getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(TipoLancamento tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    public ModoListagem getModoListagem() {
        return modoListagem;
    }

    public void setModoListagem(ModoListagem modoListagem) {
        this.modoListagem = modoListagem;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ConfiguracaoEvento)) {
            return false;
        }
        ConfiguracaoEvento other = (ConfiguracaoEvento) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        try {
            return "Lançamento: " + this.tipoLancamento + ", evento contábil: " + this.eventoContabil.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    public String getMensagemLogErroTransporteConfiguracao() {
        try {
            return "Lançamento " + this.tipoLancamento
                + ",vigente em " + DataUtil.getDataFormatada(this.inicioVigencia)
                + ", evento contábil: " + (this.eventoContabil != null ? this.eventoContabil.toString() : "");
        } catch (Exception ex) {
            return "";
        }
    }

    public enum ModoListagem {

        VIGENTE("Vigente"),
        ENCERRADO("Encerrado"),
        TODOS("Todos");
        private String descricao;

        public String getDescricao() {
            return descricao;
        }

        private ModoListagem(String descricao) {
            this.descricao = descricao;
        }
    }
}
