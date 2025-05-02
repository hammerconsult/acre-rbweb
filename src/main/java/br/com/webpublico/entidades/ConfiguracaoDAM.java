package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
@Etiqueta("Configuração de DAM")
public class ConfiguracaoDAM implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    private String descricao;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Código da Febraban")
    private Integer codigoFebraban;
    private String instrucaoLinha1;
    private String instrucaoLinha2;
    private String instrucaoLinha3;
    private String instrucaoLinha4;
    private String instrucaoLinha5;
    private String identificacaoSegmento;
    @Transient
    private Long criadoEm;
    private String codigoFormatado;
    @ManyToOne
    private Divida dividaDamNaoEncontrado;
    @ManyToOne
    private Tributo tributoDamNaoEncontrado;
    @ManyToOne
    private Pessoa pessoaDamNaoEncontrado;

    public ConfiguracaoDAM() {
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getCodigoFebraban() {
        return codigoFebraban;
    }

    public void setCodigoFebraban(Integer codigoFebraban) {
        this.codigoFebraban = codigoFebraban;
    }

    public String getInstrucaoLinha1() {
        return instrucaoLinha1;
    }

    public void setInstrucaoLinha1(String instrucaoLinha1) {
        this.instrucaoLinha1 = instrucaoLinha1;
    }

    public String getInstrucaoLinha2() {
        return instrucaoLinha2;
    }

    public void setInstrucaoLinha2(String instrucaoLinha2) {
        this.instrucaoLinha2 = instrucaoLinha2;
    }

    public String getInstrucaoLinha3() {
        return instrucaoLinha3;
    }

    public void setInstrucaoLinha3(String instrucaoLinha3) {
        this.instrucaoLinha3 = instrucaoLinha3;
    }

    public String getInstrucaoLinha4() {
        return instrucaoLinha4;
    }

    public void setInstrucaoLinha4(String instrucaoLinha4) {
        this.instrucaoLinha4 = instrucaoLinha4;
    }

    public String getInstrucaoLinha5() {
        return instrucaoLinha5;
    }

    public void setInstrucaoLinha5(String instrucaoLinha5) {
        this.instrucaoLinha5 = instrucaoLinha5;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public String getCodigoFormatado() {
        return codigoFormatado;
    }

    public void setCodigoFormatado(Integer param) {
        this.codigoFormatado = String.format("%04d", param);
    }

    public String getIdentificacaoSegmento() {
        return identificacaoSegmento;
    }

    public void setIdentificacaoSegmento(String identificacaoSegmento) {
        this.identificacaoSegmento = identificacaoSegmento;
    }

    public Divida getDividaDamNaoEncontrado() {
        return dividaDamNaoEncontrado;
    }

    public void setDividaDamNaoEncontrado(Divida dividaDamNaoEncontrado) {
        this.dividaDamNaoEncontrado = dividaDamNaoEncontrado;
    }

    public Tributo getTributoDamNaoEncontrado() {
        return tributoDamNaoEncontrado;
    }

    public void setTributoDamNaoEncontrado(Tributo tributoDamNaoEncontrado) {
        this.tributoDamNaoEncontrado = tributoDamNaoEncontrado;
    }

    public Pessoa getPessoaDamNaoEncontrado() {
        return pessoaDamNaoEncontrado;
    }

    public void setPessoaDamNaoEncontrado(Pessoa pessoaDamNaoEncontrado) {
        this.pessoaDamNaoEncontrado = pessoaDamNaoEncontrado;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return descricao + " - " + codigoFebraban;
    }
}
