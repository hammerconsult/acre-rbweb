package br.com.webpublico.entidades;

import br.com.webpublico.enums.OpcaoReadaptacao;
import br.com.webpublico.enums.StatusReadaptacao;
import br.com.webpublico.enums.TipoReadaptacao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Carnage
 * Date: 16/07/13
 * Time: 10:58
 * To change this template use File | Settings | File Templates.
 */
@GrupoDiagrama(nome = "RecursosHumanos")
@Entity

@Audited
@Etiqueta("Readaptação")
public class Readaptacao implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Servidor")
    private ContratoFP contratoFP;
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    @Etiqueta("CID")
    private CID cid;
    @Obrigatorio
    @Etiqueta("Funções compatíveis")
    private String descricao;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Tipo de Readaptação")
    @Enumerated(EnumType.STRING)
    private TipoReadaptacao tipoReadaptacao;
    @ElementCollection(targetClass = OpcaoReadaptacao.class)
    @CollectionTable(name = "OPCAO_READAPTACAO", joinColumns =
    @JoinColumn(name = "READAPTACAO_ID"))
    @Column(name = "OPCAO", nullable = false)
    @Enumerated(EnumType.STRING)
    @Etiqueta("Opções de Readaptações")
    private List<OpcaoReadaptacao> opcoes;
    @ManyToOne
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Novo cargo")
    @Pesquisavel
    private Cargo cargo;
    @Etiqueta("Vigência")
    @OneToMany(mappedBy = "readaptacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VigenciaReadaptacao> vigenciaReadaptacoes;
    @Tabelavel
    @Etiqueta("Status da Readaptação")
    @Enumerated(EnumType.STRING)
    private StatusReadaptacao statusReadaptacao;
    @Invisivel
    @Transient
    private Long criadoEm;


    public Readaptacao() {
        this.criadoEm = System.nanoTime();
        opcoes = new ArrayList<>();
        vigenciaReadaptacoes = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TipoReadaptacao getTipoReadaptacao() {
        return tipoReadaptacao;
    }

    public void setTipoReadaptacao(TipoReadaptacao tipoReadaptacao) {
        this.tipoReadaptacao = tipoReadaptacao;
    }

    public CID getCid() {
        return cid;
    }

    public void setCid(CID cid) {
        this.cid = cid;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    public List<OpcaoReadaptacao> getOpcoes() {
        return opcoes;
    }

    public void setOpcoes(List<OpcaoReadaptacao> opcoes) {
        this.opcoes = opcoes;
    }

    public List<VigenciaReadaptacao> getVigenciaReadaptacoes() {
        return vigenciaReadaptacoes;
    }

    public void setVigenciaReadaptacoes(List<VigenciaReadaptacao> vigenciaReadaptacoes) {
        this.vigenciaReadaptacoes = vigenciaReadaptacoes;
    }

    public StatusReadaptacao getStatusReadaptacao() {
        return statusReadaptacao;
    }

    public void setStatusReadaptacao(StatusReadaptacao statusReadaptacao) {
        this.statusReadaptacao = statusReadaptacao;
    }

    @Override
    public String toString() {
        return contratoFP.toString();
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
