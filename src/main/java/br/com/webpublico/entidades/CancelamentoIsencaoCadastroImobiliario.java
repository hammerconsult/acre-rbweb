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

@Entity
@Cacheable
@Audited
@Table(name = "CANCELAMENTOISENCAOCI")
@Etiqueta("Cancelamento de Isenção do Cadastro Imobiliário")
public class CancelamentoIsencaoCadastroImobiliario implements Serializable{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    @OneToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Usuário Resposável")
    private UsuarioSistema usuarioSistema;
    @Temporal(TemporalType.TIMESTAMP)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data de Operação")
    private Date dataOperacao;
    @Invisivel
    @Transient
    private Long criadoEm;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Motivo")
    private String motivo;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Isenção")
    private IsencaoCadastroImobiliario isencao;

    public CancelamentoIsencaoCadastroImobiliario() {
        this.criadoEm = System.currentTimeMillis();
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String observacoes) {
        this.motivo = observacoes;
    }

    public IsencaoCadastroImobiliario getIsencao() {
        return isencao;
    }

    public void setIsencao(IsencaoCadastroImobiliario isencao) {
        this.isencao = isencao;
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
