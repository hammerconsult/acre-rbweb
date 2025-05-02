package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoVerificacaoBemMovel;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@Etiqueta("Verificação de Bens Móveis")
public class VerificacaoBemMovel extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Código")
    private Long codigo;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Temporal(TemporalType.DATE)
    @Etiqueta("Início da Verificação")
    private Date inicioVerificacao;

    @Pesquisavel
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data da Conclusão")
    private Date dataConclusao;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;

    @ManyToOne
    @Etiqueta("Órgão")
    private UnidadeOrganizacional unidadeOrganizacional;

    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação")
    private SituacaoVerificacaoBemMovel situacao;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @Tabelavel
    @Transient
    @Obrigatorio
    @Etiqueta("Órgão")
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    @Transient
    private Bem bem;

    @Invisivel
    @OneToMany(mappedBy = "verificacaoBemMovel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemVerificacaoBemMovel> bensVerificacao;

    public VerificacaoBemMovel() {
        bensVerificacao = Lists.newArrayList();
        situacao = SituacaoVerificacaoBemMovel.EM_ELABORACAO;
        inicioVerificacao = new Date();
    }

    public VerificacaoBemMovel(VerificacaoBemMovel verificacaoBemMovel, HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.setId(verificacaoBemMovel.getId());
        this.setCodigo(verificacaoBemMovel.getCodigo());
        this.setInicioVerificacao(verificacaoBemMovel.getInicioVerificacao());
        this.setDescricao(verificacaoBemMovel.getDescricao());
        this.setSituacao(verificacaoBemMovel.getSituacao());
        this.setHierarquiaOrganizacional(hierarquiaOrganizacional);
    }

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

    public Date getInicioVerificacao() {
        return inicioVerificacao;
    }

    public void setInicioVerificacao(Date inicioVerificacao) {
        this.inicioVerificacao = inicioVerificacao;
    }

    public Date getDataConclusao() {
        return dataConclusao;
    }

    public void setDataConclusao(Date dataConclusao) {
        this.dataConclusao = dataConclusao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public SituacaoVerificacaoBemMovel getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoVerificacaoBemMovel situacao) {
        this.situacao = situacao;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        if (hierarquiaOrganizacional != null) {
            unidadeOrganizacional = hierarquiaOrganizacional.getSubordinada();
        }
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public Bem getBem() {
        return bem;
    }

    public void setBem(Bem bem) {
        this.bem = bem;
    }

    public List<ItemVerificacaoBemMovel> getBensVerificacao() {
        return bensVerificacao;
    }

    public void setBensVerificacao(List<ItemVerificacaoBemMovel> bensVerificacao) {
        this.bensVerificacao = bensVerificacao;
    }

    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    @Override
    public String toString() {
        try {
            return codigo + " - " + descricao;
        } catch (NullPointerException e) {
            return "";
        }
    }
}
