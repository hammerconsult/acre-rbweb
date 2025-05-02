package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.text.DecimalFormat;

@Entity
@Cacheable
@Audited
public class EventoConfiguradoBCI extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ConfiguracaoTributario configuracaoTributario;
    @Etiqueta("Grupo de Atributo")
    @Obrigatorio
    @ManyToOne
    private GrupoAtributo grupoAtributo;
    @Etiqueta("Evento de Cálculo")
    @Obrigatorio
    @ManyToOne
    private EventoCalculo eventoCalculo;
    @Etiqueta("Incidência")
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private Incidencia incidencia;
    @Etiqueta("Identificação")
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private Identificacao identificacao;
    private String pattern;

    public EventoConfiguradoBCI() {
        super();
        pattern = "#,##0.00";
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConfiguracaoTributario getConfiguracaoTributario() {
        return configuracaoTributario;
    }

    public void setConfiguracaoTributario(ConfiguracaoTributario configuracaoTributario) {
        this.configuracaoTributario = configuracaoTributario;
    }

    public GrupoAtributo getGrupoAtributo() {
        return grupoAtributo;
    }

    public void setGrupoAtributo(GrupoAtributo grupoAtributo) {
        this.grupoAtributo = grupoAtributo;
    }

    public EventoCalculo getEventoCalculo() {
        return eventoCalculo;
    }

    public void setEventoCalculo(EventoCalculo eventoCalculo) {
        this.eventoCalculo = eventoCalculo;
    }

    public Incidencia getIncidencia() {
        return incidencia;
    }

    public void setIncidencia(Incidencia incidencia) {
        this.incidencia = incidencia;
    }

    public Identificacao getIdentificacao() {
        return identificacao;
    }

    public void setIdentificacao(Identificacao identificacao) {
        this.identificacao = identificacao;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
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

    public String formataNumero(Object valor) {
        if (pattern == null) {
            return String.format("%.2f", valor);
        }
        return new DecimalFormat(pattern).format(valor);
    }

    public int extrairDecimal(int casa, double valor) {
        return ((int) (valor * Math.pow(10, casa))) % 10;
    }
}
