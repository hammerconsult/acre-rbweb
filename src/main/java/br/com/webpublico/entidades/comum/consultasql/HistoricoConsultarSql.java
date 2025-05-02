package br.com.webpublico.entidades.comum.consultasql;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.*;
import java.util.Date;

@Entity
@Etiqueta("Histórico Consultar SQL")
public class HistoricoConsultarSql extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataRegistro;
    private Integer quantidadeDeLinhasAlteradas;
    private String sqlExecutado;
    private String motivo;

    public HistoricoConsultarSql() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public String getSqlExecutado() {
        return sqlExecutado;
    }

    public void setSqlExecutado(String sqlExecutado) {
        this.sqlExecutado = sqlExecutado;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Integer getQuantidadeDeLinhasAlteradas() {
        return quantidadeDeLinhasAlteradas;
    }

    public void setQuantidadeDeLinhasAlteradas(Integer quantidadeDeLinhasAlteradas) {
        this.quantidadeDeLinhasAlteradas = quantidadeDeLinhasAlteradas;
    }
}
