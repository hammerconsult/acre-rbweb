package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.text.DecimalFormat;

@Entity
@Cacheable
@Audited
public class EventoConfiguradoBCI implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private EventoCalculo eventoCalculo;
    @ManyToOne
    private ConfiguracaoTributario configuracaoTributario;
    @Transient
    private Long criadoEm;
    @Enumerated(EnumType.STRING)
    private Incidencia incidencia;
    @Enumerated(EnumType.STRING)
    private Identificacao identificacao;
    private String pattern;

    public EventoConfiguradoBCI() {
        criadoEm = System.nanoTime();
        pattern = "#,##0.00";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventoCalculo getEventoCalculo() {
        return eventoCalculo;
    }

    public void setEventoCalculo(EventoCalculo eventoCalculo) {
        this.eventoCalculo = eventoCalculo;
    }

    public ConfiguracaoTributario getConfiguracaoTributario() {
        return configuracaoTributario;
    }

    public void setConfiguracaoTributario(ConfiguracaoTributario configuracaoTributario) {
        this.configuracaoTributario = configuracaoTributario;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Identificacao getIdentificacao() {
        return identificacao;
    }

    public void setIdentificacao(Identificacao identificacao) {
        this.identificacao = identificacao;
    }

    public Incidencia getIncidencia() {
        return incidencia;
    }

    public void setIncidencia(Incidencia incidencia) {
        this.incidencia = incidencia;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean equals(Object o) {
        return IdentidadeDaEntidade.calcularEquals(o, this);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    public enum Incidencia {
        CADASTRO("Cadastro"), CONSTRUCAO("Construção");
        private String descricao;

        private Incidencia(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum Identificacao {
        VALOR_VENAL("Valor Venal"), FATOR_CORRECAO("Fator de Correção"), EVENTO_COMPLEMENTAR("Evento Complementar");
        private String descricao;

        private Identificacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public  String formataNumero(Object valor) {
        if(pattern == null){
            return String.format("%.2f", valor);
        }
        return new DecimalFormat(pattern).format(valor);
    }

    public int extrairDecimal(int casa, double valor) {
        return ((int)(valor * Math.pow(10, casa))) % 10;
    }
}
