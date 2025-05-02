package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoLogradouro;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@GrupoDiagrama(nome = "Endereçamento")
@Entity
@Audited
public class Logradouro implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Setor")
    private Setor setor;
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código")
    private Long codigo;
    @Pesquisavel
    @ManyToOne
    @Tabelavel
    @Etiqueta("Tipo de Logradouro")
    private TipoLogradouro tipoLogradouro;
    @Tabelavel
    @Column(length = 40)
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Nome Atual")
    private String nome;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Nome Usual")
    private String nomeUsual;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Nome Antigo")
    private String nomeAntigo;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Situação")
    @Enumerated(EnumType.STRING)
    private SituacaoLogradouro situacao;
    @Column(length = 70)
    private String migracaoChave;
    @OneToMany(mappedBy = "logradouro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LogradouroArquivo> arquivos;
    @Transient
    private String codigoString;

    public Logradouro() {
        arquivos = new ArrayList<>();
    }

    public Logradouro(Long id, String migracaoChave) {
        this.id = id;
        this.migracaoChave = migracaoChave;
    }

    public Logradouro(TipoLogradouro tipoLogradouro, String nome) {
        this.tipoLogradouro = tipoLogradouro;
        this.nome = nome;
    }

    public Logradouro(Long id, Setor setor, Long codigo, TipoLogradouro tipoLogradouro, String nome, String nomeUsual, String nomeAntigo, SituacaoLogradouro situacao) {
        this.id = id;
        this.setor = setor;
        this.codigo = codigo;
        this.tipoLogradouro = tipoLogradouro;
        this.nome = nome;
        this.nomeUsual = nomeUsual;
        this.nomeAntigo = nomeAntigo;
        this.situacao = situacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoLogradouro getTipoLogradouro() {
        return tipoLogradouro;
    }

    public void setTipoLogradouro(TipoLogradouro tipoLogradouro) {
        this.tipoLogradouro = tipoLogradouro;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public SituacaoLogradouro getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoLogradouro situacao) {
        this.situacao = situacao;
    }

    public String getMigracaoChave() {
        return migracaoChave;
    }

    public void setMigracaoChave(String migracaoChave) {
        this.migracaoChave = migracaoChave;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getNomeUsual() {
        return nomeUsual != null ? nomeUsual : "";
    }

    public void setNomeUsual(String nomeUsual) {
        this.nomeUsual = nomeUsual;
    }

    public String getNomeAntigo() {
        return nomeAntigo;
    }

    public void setNomeAntigo(String nomeAntigo) {
        this.nomeAntigo = nomeAntigo;
    }

    public List<LogradouroArquivo> getArquivos() {
        return arquivos;
    }

    public void setArquivos(List<LogradouroArquivo> arquivos) {
        this.arquivos = arquivos;
    }

    public String getCodigoString() {
        return String.format("%08d", codigo);
    }

    public void setCodigoString(String codigoString) {
        this.codigo = Long.parseLong(codigoString);
    }

    public Setor getSetor() {
        return setor;
    }

    public void setSetor(Setor setor) {
        this.setor = setor;
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
        if (!(object instanceof Logradouro)) {
            return false;
        }
        Logradouro other = (Logradouro) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        if (codigo != null) {
            sb.append(codigo).append(" - ");
        }
        if (tipoLogradouro != null && tipoLogradouro.getDescricao() != null) {
            sb.append(tipoLogradouro.toString().trim()).append(" ");
        }
        if (nome != null) {
            sb.append(nome);
        }

        return sb.toString().trim();
    }
}
