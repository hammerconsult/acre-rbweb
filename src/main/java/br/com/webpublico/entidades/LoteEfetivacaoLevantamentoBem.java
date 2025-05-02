package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 04/12/13
 * Time: 11:08
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Table(name = "LOTEEFETLEVANTBEM")
@Etiqueta(value = "Efetivação do Levantamento de Bem", genero = "M")
public class LoteEfetivacaoLevantamentoBem extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código")
    private Long codigo;

    @Obrigatorio
    @Etiqueta("Usuário da Efetivação")
    @ManyToOne
    private UsuarioSistema usuarioSistema;

    @Etiqueta("Usuário da Efetivação")
    @Transient
    @Tabelavel
    private String descricaoUsuario;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data da Efetivação")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataEfetivacao;

    @Etiqueta("Unidade Organizacional")
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;

    @Etiqueta("Unidade Orçamentária")
    @Transient
    @Tabelavel
    private String descricaoUnidadeOrc;

    @Etiqueta("Unidade Orçamentária")
    @ManyToOne
    private UnidadeOrganizacional unidadeOrcamentaria;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Situação")
    @Enumerated(EnumType.STRING)
    private SituacaoEventoBem situacao;

    @OneToMany(mappedBy = "lote", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<EfetivacaoLevantamentoBem> efetivacoes;

    @OneToMany(mappedBy = "loteEfetivacao", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<EfetivacaoLevantamentoInformacoes> informacoes;

    public LoteEfetivacaoLevantamentoBem(LoteEfetivacaoLevantamentoBem lote) {
        this.id = lote.getId();
        this.codigo = lote.getCodigo();
        this.dataEfetivacao = lote.getDataEfetivacao();
        this.unidadeOrganizacional = lote.getUnidadeOrganizacional();
        this.situacao = lote.getSituacao();
    }

    public LoteEfetivacaoLevantamentoBem() {
        super();
        efetivacoes = Lists.newArrayList();
        informacoes = Lists.newArrayList();
        situacao = SituacaoEventoBem.CONCLUIDO;
    }

    public String getDescricaoUsuario() {
        return descricaoUsuario;
    }

    public void setDescricaoUsuario(String descricaoUsuario) {
        this.descricaoUsuario = descricaoUsuario;
    }

    public String getDescricaoUnidadeOrc() {
        return descricaoUnidadeOrc;
    }

    public void setDescricaoUnidadeOrc(String descricaoUnidadeOrc) {
        this.descricaoUnidadeOrc = descricaoUnidadeOrc;
    }

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

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Date getDataEfetivacao() {
        return dataEfetivacao;
    }

    public void setDataEfetivacao(Date dataEfetivacao) {
        this.dataEfetivacao = dataEfetivacao;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public UnidadeOrganizacional getUnidadeOrcamentaria() {
        return unidadeOrcamentaria;
    }

    public void setUnidadeOrcamentaria(UnidadeOrganizacional unidadeOrcamentaria) {
        this.unidadeOrcamentaria = unidadeOrcamentaria;
    }

    public List<EfetivacaoLevantamentoBem> getEfetivacoes() {
        return efetivacoes;
    }

    public void setEfetivacoes(List<EfetivacaoLevantamentoBem> efetivacoes) {
        this.efetivacoes = efetivacoes;
    }

    public SituacaoEventoBem getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoEventoBem situacao) {
        this.situacao = situacao;
    }

    public Boolean getEstornado() {
        return SituacaoEventoBem.ESTORNADO.equals(this.situacao);
    }

    public List<EfetivacaoLevantamentoInformacoes> getInformacoes() {
        if (informacoes == null) {
            informacoes = Lists.newArrayList();
        }
        Collections.sort(informacoes);
        return informacoes;
    }

    public void setInformacoes(List<EfetivacaoLevantamentoInformacoes> informacoes) {
        this.informacoes = informacoes;
    }

    public List<LevantamentoBemPatrimonial> getLevantamentosBemPatrimonial() {
        List<LevantamentoBemPatrimonial> levantamentos = Lists.newArrayList();
        if (efetivacoes != null && !efetivacoes.isEmpty()) {
            for (EfetivacaoLevantamentoBem efetivacao : efetivacoes) {
                levantamentos.add(efetivacao.getLevantamento());
            }
        }
        return levantamentos;
    }
}
