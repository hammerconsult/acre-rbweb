package br.com.webpublico.entidades;

import br.com.webpublico.controle.portaltransparencia.entidades.PaginaPrefeituraPortal;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.PortalTransparenciaSituacao;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
@Etiqueta("Anexo Geral do Portal da Transparência")
public class AnexoPortalTransparencia extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Data do Cadastro")
    private Date dataCadastro;
    @ManyToOne
    @Etiqueta("Usuário")
    private UsuarioSistema usuarioSistema;
    @ManyToOne
    @Etiqueta("Unidade Administrativa")
    private UnidadeOrganizacional unidadeOrganizacional;
    @Obrigatorio
    @Etiqueta("Nome")
    private String nome;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Mês")
    private Mes mes;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Situação")
    private PortalTransparenciaSituacao situacao;
    @OneToOne
    @Obrigatorio
    @Etiqueta("Arquivo")
    private Arquivo arquivo;
    @Etiqueta("Observações")
    private String observacoes;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Pagina Portal")
    private PaginaPrefeituraPortal paginaPrefeituraPortal;
    @Transient
    private HierarquiaOrganizacional hierarquia;

    public AnexoPortalTransparencia() {
        criadoEm = System.nanoTime();
        situacao = PortalTransparenciaSituacao.NAO_PUBLICADO;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public PortalTransparenciaSituacao getSituacao() {
        return situacao;
    }

    public void setSituacao(PortalTransparenciaSituacao situacao) {
        this.situacao = situacao;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public PaginaPrefeituraPortal getPaginaPrefeituraPortal() {
        return paginaPrefeituraPortal;
    }

    public void setPaginaPrefeituraPortal(PaginaPrefeituraPortal paginaPrefeituraPortal) {
        this.paginaPrefeituraPortal = paginaPrefeituraPortal;
    }

    public HierarquiaOrganizacional getHierarquia() {
        return hierarquia;
    }

    public void setHierarquia(HierarquiaOrganizacional hierarquia) {
        this.hierarquia = hierarquia;
    }

    @Override
    public String toString() {
        return nome + "(" + (mes != null ? mes.getDescricao() + "/" : "") + exercicio.getAno() + ")";
    }
}
