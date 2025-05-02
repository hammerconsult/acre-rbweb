package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by AndreGustavo on 10/04/2014.
 */
@Audited
@Entity
@Etiqueta("Alteração do Status da Pessoa")
public class AlteracaoStatusPessoa implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Invisivel
    private Integer numero;
    @Invisivel
    private Integer ano;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número")
    private String numeroCompleto;
    @Tabelavel
    @Etiqueta("Motivo")
    private String motivo;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Pessoa")
    private Pessoa pessoa;
    @Temporal(TemporalType.TIMESTAMP)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data")
    private Date data;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Usuário do Sistema")
    private UsuarioSistema usuarioSistema;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo de Processo")
    private TipoProcessoAlteracaoCadastroPessoa tipoProcesso;
    @Transient
    private Long criadoEm;

    public AlteracaoStatusPessoa() {
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public String getNumeroCompleto() {
        return numeroCompleto;
    }

    public void setNumeroCompleto(String numeroCompleto) {
        this.numeroCompleto = numeroCompleto;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }


    public TipoProcessoAlteracaoCadastroPessoa getTipoProcesso() {
        return tipoProcesso;
    }

    public void setTipoProcesso(TipoProcessoAlteracaoCadastroPessoa tipoProcesso) {
        this.tipoProcesso = tipoProcesso;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    public enum TipoProcessoAlteracaoCadastroPessoa {
        ATIVACAO("Ativação"),
        INATIVACAO("Inativação"),
        BAIXA("Baixa"),
        SUSPENSAO("Suspensão");

        private String descricao;

        TipoProcessoAlteracaoCadastroPessoa(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return getDescricao();
        }

    }

}
