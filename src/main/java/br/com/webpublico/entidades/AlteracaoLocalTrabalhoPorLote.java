package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author octavio
 */
@GrupoDiagrama(nome = "RecursosHumanos")
@Table(name = "ALTERACAOLOCALTRABPORLOTE")
@Etiqueta("Alteração Local de Trabalho Por Lote")
@Entity
@Audited
public class AlteracaoLocalTrabalhoPorLote extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Ato Legal")
    @OneToOne
    private AtoLegal atoLegal;
    @Tabelavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta(value = "Data Refrência da Vigência")
    private Date dataReferencia;
    @Invisivel
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "alterLocTrabalhoLotacao")
    private List<AlteracaoVinculoLotacao> alteracoesVinculoLotacao;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Antiga Unidade Organizacional")
    private UnidadeOrganizacional unidadeOrganizacionalAtual;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Nova Unidade Organizacional (Vigente)")
    private UnidadeOrganizacional novaUnidadeOrganizacional;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Recursos FP")
    private RecursoFP recursoFP;
    @ManyToOne
    private UsuarioSistema usuarioSistema;

    public AlteracaoLocalTrabalhoPorLote() {
        alteracoesVinculoLotacao = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public Date getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(Date dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    public List<AlteracaoVinculoLotacao> getAlteracoesVinculoLotacao() {
        return alteracoesVinculoLotacao;
    }

    public void setAlteracoesVinculoLotacao(List<AlteracaoVinculoLotacao> alteracoesVinculoLotacao) {
        this.alteracoesVinculoLotacao = alteracoesVinculoLotacao;
    }

    public UnidadeOrganizacional getUnidadeOrganizacionalAtual() {
        return unidadeOrganizacionalAtual;
    }

    public void setUnidadeOrganizacionalAtual(UnidadeOrganizacional unidadeOrganizacionalAtual) {
        this.unidadeOrganizacionalAtual = unidadeOrganizacionalAtual;
    }

    public UnidadeOrganizacional getNovaUnidadeOrganizacional() {
        return novaUnidadeOrganizacional;
    }

    public void setNovaUnidadeOrganizacional(UnidadeOrganizacional novaUnidadeOrganizacional) {
        this.novaUnidadeOrganizacional = novaUnidadeOrganizacional;
    }

    public RecursoFP getRecursoFP() {
        return recursoFP;
    }

    public void setRecursoFP(RecursoFP recursoFP) {
        this.recursoFP = recursoFP;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }
}
