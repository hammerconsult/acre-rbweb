package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoTelefone;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.StringUtil;
import com.google.common.base.CharMatcher;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@GrupoDiagrama(nome = "CDA")
@Entity
@Audited
public class TelefoneDadosRetPessoaCda implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private DadosRetificacaoPessoaCda dado;
    private Long sequencia;
    private String numero;
    @Enumerated(EnumType.STRING)
    private TipoTelefone tipo;
    @Transient
    private Long criadoEm;

    public TelefoneDadosRetPessoaCda() {
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DadosRetificacaoPessoaCda getDado() {
        return dado;
    }

    public void setDado(DadosRetificacaoPessoaCda dado) {
        this.dado = dado;
    }

    public Long getSequencia() {
        return sequencia;
    }

    public void setSequencia(Long sequencia) {
        this.sequencia = sequencia;
    }

    public String getNumero() {
        if (numero != null) {
            return StringUtil.retornaApenasNumeros(StringUtil.removeCaracteresEspeciais(numero).replace(" ", "").replace("=", ""));
        }
        return null;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public TipoTelefone getTipo() {
        return tipo;
    }

    public void setTipo(TipoTelefone tipo) {
        this.tipo = tipo;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }
}
