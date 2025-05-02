package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@GrupoDiagrama(nome = "Tributario")
@Entity
@Audited
public class ClassificacaoConstrucao extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Código")
    private Long codigo;
    @Etiqueta("Habite-se")
    @ManyToOne
    private Habitese habitese;
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @Temporal(TemporalType.DATE)
    private Date dataClassificacao;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Habitese getHabitese() {
        return habitese;
    }

    public void setHabitese(Habitese habitese) {
        this.habitese = habitese;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Date getDataClassificacao() {
        return dataClassificacao;
    }

    public void setDataClassificacao(Date dataClassificacao) {
        this.dataClassificacao = dataClassificacao;
    }
}
