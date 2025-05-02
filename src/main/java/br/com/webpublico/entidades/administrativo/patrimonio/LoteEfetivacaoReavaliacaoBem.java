package br.com.webpublico.entidades.administrativo.patrimonio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoOperacaoReavaliacaoBens;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by William on 22/10/2015.
 */

@Audited
@Entity
@Table(name = "LOTEEFETIVACAOREAVALIACAO")
@Etiqueta("Efetivação de Reavaliação de Bens Móveis")
public class LoteEfetivacaoReavaliacaoBem extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código")
    private Long codigo;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Unidade Organizacional")
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Usuário da Efetivação")
    @ManyToOne
    private UsuarioSistema usuarioSistema;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data da Efetivação")
    @Temporal(TemporalType.DATE)
    private Date dataEfetivacao;

    @Invisivel
    @OneToMany(mappedBy = "lote", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<EfetivacaoReavaliacaoBem> efetivacoes;

    @Etiqueta("Tipo de Bem")
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private TipoBem tipoBem;

    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Tipo de Reavaliação do Bem")
    @Enumerated(EnumType.STRING)
    private TipoOperacaoReavaliacaoBens tipoOperacaoBem;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @Transient
    @Etiqueta("Unidade Administrativa")
    @Obrigatorio
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    public LoteEfetivacaoReavaliacaoBem() {
        super();
        efetivacoes = new ArrayList<>();
    }

    public List<EfetivacaoReavaliacaoBem> getEfetivacoes() {
        return efetivacoes;
    }

    public void setEfetivacoes(List<EfetivacaoReavaliacaoBem> efetivacoes) {
        this.efetivacoes = efetivacoes;
    }

    public Date getDataEfetivacao() {
        return dataEfetivacao;
    }

    public void setDataEfetivacao(Date dataEfetivacao) {
        this.dataEfetivacao = dataEfetivacao;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
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

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public TipoOperacaoReavaliacaoBens getTipoOperacaoBem() {
        return tipoOperacaoBem;
    }

    public void setTipoOperacaoBem(TipoOperacaoReavaliacaoBens tipoOperacaoBem) {
        this.tipoOperacaoBem = tipoOperacaoBem;
    }

    public TipoBem getTipoBem() {
        return tipoBem;
    }

    public void setTipoBem(TipoBem tipoBem) {
        this.tipoBem = tipoBem;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
        if (this.hierarquiaOrganizacional != null) {
            setUnidadeOrganizacional(this.hierarquiaOrganizacional.getSubordinada());
        }
    }

    @Override
    public String toString() {
        return codigo + " - " + unidadeOrganizacional.getDescricao();
    }

    public List<EfetivacaoReavaliacaoBem> efetivacoesAceitas() {
        List<EfetivacaoReavaliacaoBem> retorno = new ArrayList<>();
        for (EfetivacaoReavaliacaoBem etb : this.getEfetivacoes()) {
            if (etb.getSituacaoEventoBem().equals(SituacaoEventoBem.FINALIZADO)) {
                retorno.add(etb);
            }
        }
        return retorno;
    }

    public Boolean isReavaliacaoBemAumentativa() {
        return TipoOperacaoReavaliacaoBens.REAVALIACAO_BENS_MOVEIS_AUMENTATIVA.equals(this.tipoOperacaoBem)
            || TipoOperacaoReavaliacaoBens.REAVALIACAO_BENS_IMOVEIS_AUMENTATIVA.equals(this.tipoOperacaoBem);
    }
}
