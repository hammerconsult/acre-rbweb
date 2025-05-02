package br.com.webpublico.entidades;

import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.PortalTipoAnexo;
import br.com.webpublico.enums.PortalTransparenciaSituacao;
import br.com.webpublico.enums.PortalTransparenciaTipo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Buzatto
 * Date: 30/12/13
 * Time: 08:40
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Anexos - Portal da Transparência")
public class PortalTransparencia implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Nome")
    private String nome;
    @OneToOne
    @Tabelavel
    @Etiqueta("Arquivo")
    private Arquivo arquivo;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Mês")
    private Mes mes;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Situação")
    private PortalTransparenciaSituacao situacao;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo")
    private PortalTransparenciaTipo tipo;
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Etiqueta("Tipo Anexo")
    private PortalTipoAnexo tipoAnexo;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Observações")
    private String observacoes;
    @Invisivel
    @Transient
    private Long criadoEm;
    @ManyToOne
    @Etiqueta("Usuário")
    private UsuarioSistema usuarioSistema;
    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Data do Cadastro")
    private Date dataCadastro;

    public PortalTransparencia() {
        criadoEm = System.nanoTime();
        situacao = PortalTransparenciaSituacao.NAO_PUBLICADO;
    }

    public PortalTransparencia(String nome, String observacoes, Arquivo arquivo, Exercicio exercicio, Mes mes, PortalTransparenciaSituacao situacao) {
        this.nome = nome;
        this.observacoes = observacoes;
        this.arquivo = arquivo;
        this.exercicio = exercicio;
        this.mes = mes;
        this.situacao = situacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
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

    public PortalTransparenciaTipo getTipo() {
        return tipo;
    }

    public void setTipo(PortalTransparenciaTipo tipo) {
        this.tipo = tipo;
    }

    public PortalTipoAnexo getTipoAnexo() {
        return tipoAnexo;
    }

    public void setTipoAnexo(PortalTipoAnexo tipoAnexo) {
        this.tipoAnexo = tipoAnexo;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public String getAdicionadoEmPorUsuario() {
        return dataCadastro != null && usuarioSistema != null ? "Criado em " + DataUtil.getDataFormatadaDiaHora(dataCadastro) + ", por " + usuarioSistema.getNome() : "";
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        return nome + "(" + mes.getDescricao() + "/" + exercicio.getAno() + ")";
    }
}
