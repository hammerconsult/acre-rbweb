package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoProcessoDebito;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Wellington on 07/12/2015.
 */
@Table(name = "PROCALTERACAOVENCPARCELA")
@Entity
@Audited
public class ProcessoAlteracaoVencimentoParcela implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código")
    private Long codigo;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Data de Lançamento")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date realizadoEm;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número do Protocolo")
    private String numeroProtocolo;
    @Obrigatorio
    @Etiqueta("Motivo")
    private String motivo;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Usuário Responsável")
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Situação")
    private SituacaoProcessoDebito situacao;
    @OneToMany(mappedBy = "procAlteracaoVencimentoParc", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemProcessoAlteracaoVencimentoParcela> itensProcessoAlteracaoVencParc;
    @Transient
    @Etiqueta("Cadastros")
    @Tabelavel
    private List<String> cadastros;

    @ManyToOne
    private UsuarioSistema usuarioEstorno;

    @Temporal(TemporalType.DATE)
    private Date dataEstorno;

    private String motivoEstorno;


    public ProcessoAlteracaoVencimentoParcela() {
        itensProcessoAlteracaoVencParc = new ArrayList<ItemProcessoAlteracaoVencimentoParcela>();
        cadastros = new ArrayList<>();
    }

    public ProcessoAlteracaoVencimentoParcela(Long id,
                                              Long codigo,
                                              Date realizadoEm,
                                              String numeroProtocolo,
                                              SituacaoProcessoDebito situacao) {
        this.id = id;
        this.codigo = codigo;
        this.realizadoEm = realizadoEm;
        this.numeroProtocolo = numeroProtocolo;
        this.situacao = situacao;
        this.cadastros = new ArrayList<>();
    }

    public ProcessoAlteracaoVencimentoParcela(Long id,
                                              Long codigo,
                                              Date realizadoEm,
                                              String numeroProtocolo,
                                              SituacaoProcessoDebito situacao,
                                              Integer ano) {
        this.id = id;
        this.codigo = codigo;
        this.realizadoEm = realizadoEm;
        this.numeroProtocolo = numeroProtocolo;
        this.situacao = situacao;
        this.cadastros = new ArrayList<>();
    }

    public ProcessoAlteracaoVencimentoParcela(Long id) {
        this.id = id;
        this.cadastros = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getRealizadoEm() {
        return realizadoEm;
    }

    public void setRealizadoEm(Date realizadoEm) {
        this.realizadoEm = realizadoEm;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public List<ItemProcessoAlteracaoVencimentoParcela> getItensProcessoAlteracaoVencParc() {
        return itensProcessoAlteracaoVencParc;
    }

    public void setItensProcessoAlteracaoVencParc(List<ItemProcessoAlteracaoVencimentoParcela> itensProcessoAlteracaoVencParc) {
        this.itensProcessoAlteracaoVencParc = itensProcessoAlteracaoVencParc;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public SituacaoProcessoDebito getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoProcessoDebito situacao) {
        this.situacao = situacao;
    }

    public List<String> getCadastros() {
        return cadastros;
    }

    public void setCadastros(List<String> cadastros) {
        this.cadastros = cadastros;
    }

    public UsuarioSistema getUsuarioEstorno() {
        return usuarioEstorno;
    }

    public void setUsuarioEstorno(UsuarioSistema usuarioEstorno) {
        this.usuarioEstorno = usuarioEstorno;
    }

    public Date getDataEstorno() {
        return dataEstorno;
    }

    public void setDataEstorno(Date dataEstorno) {
        this.dataEstorno = dataEstorno;
    }

    public String getMotivoEstorno() {
        return motivoEstorno;
    }

    public void setMotivoEstorno(String motivoEstorno) {
        this.motivoEstorno = motivoEstorno;
    }

    public Boolean isSituacaoEstornado() {
        return SituacaoProcessoDebito.ESTORNADO.equals(situacao);
    }
}
