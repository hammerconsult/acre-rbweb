package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 05/08/14
 * Time: 09:19
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Infração de Fiscalização de Secretaria")
@Table(name = "INFRACAOFISCSECRETARIA")
public class InfracaoFiscalizacaoSecretaria implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta(value = "Secretaria")
    @Obrigatorio
    @ManyToOne
    private SecretariaFiscalizacao secretariaFiscalizacao;
    @Pesquisavel
    @Tabelavel
    @Etiqueta(value = "Código")
    private Integer codigo;
    @Pesquisavel
    @Tabelavel
    @Etiqueta(value = "Descrição Reduzida")
    @Obrigatorio
    private String descricaoReduzida;
    @Pesquisavel
    @Tabelavel
    @Etiqueta(value = "Descrição Detalhada")
    @Obrigatorio
    private String descricaoDetalhada;
    @Pesquisavel
    @Tabelavel
    @Etiqueta(value = "Situação")
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private Situacao situacao;
    @Transient
    private Long criadoEm;

    public InfracaoFiscalizacaoSecretaria() {
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SecretariaFiscalizacao getSecretariaFiscalizacao() {
        return secretariaFiscalizacao;
    }

    public void setSecretariaFiscalizacao(SecretariaFiscalizacao secretariaFiscalizacao) {
        this.secretariaFiscalizacao = secretariaFiscalizacao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getDescricaoReduzida() {
        return descricaoReduzida;
    }

    public void setDescricaoReduzida(String descricaoReduzida) {
        this.descricaoReduzida = descricaoReduzida;
    }

    public String getDescricaoDetalhada() {
        return descricaoDetalhada;
    }

    public void setDescricaoDetalhada(String descricaoDetalhada) {
        this.descricaoDetalhada = descricaoDetalhada;
    }

    public Situacao getSituacao() {
        return situacao;
    }

    public void setSituacao(Situacao situacao) {
        this.situacao = situacao;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public enum Situacao {
        ATIVO("Ativo"), INATIVO("Inativo");

        private String descricao;

        Situacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof InfracaoFiscalizacaoSecretaria)) {
            return false;
        }
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }
}
