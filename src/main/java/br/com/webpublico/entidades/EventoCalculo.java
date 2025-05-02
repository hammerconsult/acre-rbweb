package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Cacheable
@Audited
@Etiqueta("Evento de Cálculo")
public class EventoCalculo implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long id;
    @Tabelavel
    @Etiqueta("Identificação")
    @Pesquisavel
    private String identificacao;
    @Tabelavel
    @Etiqueta("Descrição")
    @Pesquisavel
    private String descricao;
    @Etiqueta("Regra")
    @Tabelavel
    private String regra;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Tributo")
    @JsonIgnore
    private Tributo tributo;
    @ManyToOne
    @JsonIgnore
    private EventoCalculo descontoSobre;
    @Enumerated(EnumType.STRING)
    private TipoLancamento tipoLancamento;
    private Long criadoEm;
    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Inicio de Vigência")
    private Date inicioVigencia;
    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Final de Vigência")
    private Date finalVigencia;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Sigla")
    private String sigla;
    @Transient
    private Object resutlado;
    @Transient
    private String identificadorParaDesconto;
    @ManyToOne
    @JsonIgnore
    private EventoCalculo eventoImunidade;


    public EventoCalculo() {
        criadoEm = System.nanoTime();

    }

    public EventoCalculo getDescontoSobre() {
        return descontoSobre;
    }

    public void setDescontoSobre(EventoCalculo descontoSobre) {
        this.descontoSobre = descontoSobre;
    }

    public Object getResutlado() {
        return resutlado;
    }

    public void setResutlado(Object resutlado) {
        this.resutlado = resutlado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentificacao() {
        return identificacao;
    }

    public void setIdentificacao(String identificacao) {
        this.identificacao = identificacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getRegra() {
        return regra;
    }

    public void setRegra(String regra) {
        this.regra = regra;
    }

    public Tributo getTributo() {
        return tributo;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
    }

    public TipoLancamento getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(TipoLancamento tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getIdentificadorParaDesconto() {
        String retorno = identificadorParaDesconto;
        if (retorno == null && descontoSobre != null) {
            retorno = descontoSobre.getIdentificacao();
        }
        return retorno;
    }

    public void setIdentificadorParaDesconto(String identificadorParaDesconto) {
        this.identificadorParaDesconto = identificadorParaDesconto;
    }

    public EventoCalculo getEventoImunidade() {
        return eventoImunidade;
    }

    public void setEventoImunidade(EventoCalculo eventoImunidade) {
        this.eventoImunidade = eventoImunidade;
    }

    @Override
    public boolean equals(Object o) {
        return IdentidadeDaEntidade.calcularEquals(o, this);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    public enum TipoLancamento {
        IMPOSTO("Imposto", true), TAXA("Taxa", true), CALCULO_COMPLEMENTAR("Calculo Complementar", false), DESCONTO("Desconto", false);
        private String descricao;
        private Boolean lancaValor;

        private TipoLancamento(String descricao, Boolean lancaValor) {
            this.descricao = descricao;
            this.lancaValor = lancaValor;
        }

        public String getDescricao() {
            return descricao;
        }

        public Boolean getLancaValor() {
            return lancaValor;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }
}
