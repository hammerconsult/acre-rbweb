package br.com.webpublico.entidades;

import br.com.webpublico.entidades.usertype.DireitosUserType;
import br.com.webpublico.entidades.usertype.TipoGrupoUsuarioUserType;
import br.com.webpublico.enums.TipoGrupoUsuario;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.envers.Audited;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author Fabio
 */
@GrupoDiagrama(nome = "Segurança")
@Entity

@Audited
@Etiqueta("Grupo de Usuário")
@TypeDefs({
        @TypeDef(name = "tipoGrupoUsuario", typeClass = TipoGrupoUsuarioUserType.class),
        @TypeDef(name = "direitos", typeClass = DireitosUserType.class)})
public class GrupoUsuario implements Serializable {

    private static final long serialVersionUID = 1L;
    protected static final Logger LOGGER = LoggerFactory.getLogger(GrupoUsuario.class);
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    @Invisivel
    private Long id;
    @Tabelavel
    @Etiqueta("Nome")
    @Pesquisavel
    @Column(length = 45, unique = true)
    private String nome;
    @Type(type = "tipoGrupoUsuario")
    private TipoGrupoUsuario tipo = TipoGrupoUsuario.AUTORIZACAO;
    @OrderBy("login")
    @ManyToMany
    @JoinTable(name = "GRUPOUSUARIOSISTEMA", joinColumns =
    @JoinColumn(name = "GRUPOUSUARIO_ID", referencedColumnName = "ID"), inverseJoinColumns =
    @JoinColumn(name = "USUARIOSISTEMA_ID", referencedColumnName = "ID"))
    private List<UsuarioSistema> usuarios = Lists.newLinkedList();
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("diaDaSemana")
    @JoinTable(name = "GRUPOUSUARIOPERIODO", joinColumns =
    @JoinColumn(name = "GRUPOUSUARIO_ID", referencedColumnName = "ID"), inverseJoinColumns =
    @JoinColumn(name = "PERIODO_ID", referencedColumnName = "ID"))
    private List<Periodo> periodos = Lists.newLinkedList();
    @OneToMany(mappedBy = "grupoUsuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemGrupoUsuario> itens = Lists.newLinkedList();
    @OneToMany(mappedBy = "grupoUsuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GrupoUsuarioNotificacao> notificacoes;

    public GrupoUsuario() {
        periodos = Lists.newArrayList();
        itens = Lists.newArrayList();
        notificacoes = Lists.newLinkedList();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GrupoUsuario) {
            GrupoUsuario other = (GrupoUsuario) obj;
            return Objects.equal(this.nome, other.nome);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.nome);
    }

    public void addUsuario(UsuarioSistema usuario) {
        if (!usuarios.contains(usuario)) {
            usuarios.add(usuario);
        }
    }

    public void removeUsuario(UsuarioSistema usuario) {
        usuarios.remove(usuario);
    }

    public void addPeriodo(Periodo periodo) {
        if (!periodos.contains(periodo)) {
            periodos.add(periodo);
        }
    }

    public void removePeriodo(Periodo periodo) {
        periodos.remove(periodo);
    }

    public void addItem(ItemGrupoUsuario item) {
        itens.add(item);
        item.setGrupoUsuario(this);
    }

    public boolean isPermitidoAcessoLeitura(GrupoRecurso grupoRecurso) {
        for (ItemGrupoUsuario item : itens) {
            if (item.getGrupoRecurso().equals(grupoRecurso)) {
                if (item.getLeitura()) {
                    return isDentroPeriodoPermitido();
                }
            }
        }
        return false;
    }

    public boolean isPermitidoAcessoEscrita(GrupoRecurso grupoRecurso) {
        for (ItemGrupoUsuario item : itens) {
            if (item.getGrupoRecurso().equals(grupoRecurso)) {
                if (item.getEscrita()) {
                    return isDentroPeriodoPermitido();
                }
            }
        }
        return false;
    }

    public boolean isPermitidoAcessoExclusao(GrupoRecurso grupoRecurso) {
        for (ItemGrupoUsuario item : itens) {
            if (item.getGrupoRecurso().equals(grupoRecurso)) {
                if (item.getExclusao()) {
                    return isDentroPeriodoPermitido();
                }
            }
        }
        return false;
    }

    public boolean isPermitidoAcesso(GrupoRecurso grupoRecurso) {
        for (ItemGrupoUsuario item : itens) {
            if (item.getGrupoRecurso().equals(grupoRecurso)) {
                return isDentroPeriodoPermitido();
            }
        }
        return false;
    }

    private boolean isDentroPeriodoPermitido() {
        if (tipo.isPermitido()) {
            return isDentroDoHorarioEstipulado();
        } else {
            return !isDentroDoHorarioEstipulado();
        }
    }

    private boolean isDentroDoHorarioEstipulado() {
        DateTime agora = new DateTime(DateTimeUtils.currentTimeMillis());
        if (periodos.isEmpty()) {
            return true;
        }
        for (Periodo periodo : periodos) {
            if (periodo.isNoPeriodo(agora)) {
                return true;
            }
        }
        LOGGER.trace("Não, está fora do horário estipulado para o grupo.");
        return false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setItens(List<ItemGrupoUsuario> itens) {
        this.itens = itens;
    }

    public void setPeriodos(List<Periodo> periodos) {
        this.periodos = periodos;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public TipoGrupoUsuario getTipo() {
        return tipo;
    }

    public void setTipo(TipoGrupoUsuario tipo) {
        this.tipo = tipo;
    }

    public List<UsuarioSistema> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<UsuarioSistema> usuarios) {
        this.usuarios = Lists.newArrayList(usuarios);
    }

    public List<Periodo> getPeriodos() {
        return periodos;
    }

    public List<ItemGrupoUsuario> getItens() {
        return itens;
    }

    public List<GrupoUsuarioNotificacao> getNotificacoes() {
        return notificacoes;
    }

    public void setNotificacoes(List<GrupoUsuarioNotificacao> notificacoes) {
        this.notificacoes = notificacoes;
    }

    @Override
    public String toString() {
        return "GrupoUsuario{" + "id=" + id + ", nome=" + nome + ", tipo=" + tipo + '}';
    }

    public String getAsNomeToString(){
        return this.getNome();
    }
}
