package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoCessao;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 08/05/14
 * Time: 16:35
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Efetivação da Cessão")
public class LoteEfetivacaoCessao extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número")
    private Long numero;

    @Etiqueta("Tipo de Cessão")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private TipoCessao tipoCessao;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data da Efetivação")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataEfetivacao;

    @Obrigatorio
    @Tabelavel
    @Etiqueta("Responsável")
    @ManyToOne
    private UsuarioSistema responsavel;

    @Obrigatorio
    @Etiqueta("Unidade Organizacional")
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;

    /**
     *Utilizado na pesquisa genérica
     */
    @Tabelavel
    @Transient
    @Etiqueta("Unidade Organizacional")
    private String descricaoUnidade;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @OneToMany(mappedBy = "loteEfetivacaoCessao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EfetivacaoCessao> listaEfetivacaoCessao;

    public LoteEfetivacaoCessao() {
        super();
        listaEfetivacaoCessao = new ArrayList<>();
        tipoCessao = TipoCessao.INTERNO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<EfetivacaoCessao> getListaEfetivacaoCessao() {
        return listaEfetivacaoCessao;
    }

    public void setListaEfetivacaoCessao(List<EfetivacaoCessao> listaEfetivacaoCessao) {
        this.listaEfetivacaoCessao = listaEfetivacaoCessao;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Date getDataEfetivacao() {
        return dataEfetivacao;
    }

    public void setDataEfetivacao(Date dataEfetivacao) {
        this.dataEfetivacao = dataEfetivacao;
    }

    public UsuarioSistema getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(UsuarioSistema responsavel) {
        this.responsavel = responsavel;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public TipoCessao getTipoCessao() {
        return tipoCessao;
    }

    public void setTipoCessao(TipoCessao tipoCessao) {
        this.tipoCessao = tipoCessao;
    }

    public String getDescricaoUnidade() {
        return descricaoUnidade;
    }

    public void setDescricaoUnidade(String descricaoUnidade) {
        this.descricaoUnidade = descricaoUnidade;
    }

    public Boolean isExterno() {
        return TipoCessao.EXTERNO.equals(this.tipoCessao);
    }

    public Boolean isInterno() {
        return TipoCessao.INTERNO.equals(this.tipoCessao);
    }

    @Override
    public String toString() {
        return String.valueOf(numero) + " - " + DataUtil.getDataFormatada(dataEfetivacao);
    }
}
