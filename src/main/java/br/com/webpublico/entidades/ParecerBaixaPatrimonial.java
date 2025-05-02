package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoParecer;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 10/06/14
 * Time: 18:24
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Parecer de Baixa de Bem Móvel")
public class ParecerBaixaPatrimonial extends SuperEntidade implements PossuidorArquivo {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;

    @Etiqueta("Código")
    @Pesquisavel
    @Tabelavel
    private Long codigo;

    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Data do Parecer")
    @Temporal(TemporalType.DATE)
    private Date dateParecer;

    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @OneToOne
    @Etiqueta("Solicitação de Baixa")
    private SolicitacaoBaixaPatrimonial solicitacaoBaixa;

    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Parecerista")
    private UsuarioSistema parecerista;

    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação do Parecer")
    private SituacaoParecer situacaoParecer;

    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Justificativa")
    private String justificativa;

    @Etiqueta("Tipo de Bem")
    @Enumerated(EnumType.STRING)
    private TipoBem tipoBem;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @Invisivel
    @OneToMany(mappedBy = "parecerBaixa", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ItemParecerBaixaPatrimonial> listaItemParecer;

    public ParecerBaixaPatrimonial() {
        super();
        this.listaItemParecer = new ArrayList<>();
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

    public List<ItemParecerBaixaPatrimonial> getListaItemParecer() {
        return listaItemParecer;
    }

    public void setListaItemParecer(List<ItemParecerBaixaPatrimonial> listaItemParecer) {
        this.listaItemParecer = listaItemParecer;
    }

    public SolicitacaoBaixaPatrimonial getSolicitacaoBaixa() {
        return solicitacaoBaixa;
    }

    public void setSolicitacaoBaixa(SolicitacaoBaixaPatrimonial solicitacaoBaixa) {
        this.solicitacaoBaixa = solicitacaoBaixa;
    }

    public UsuarioSistema getParecerista() {
        return parecerista;
    }

    public void setParecerista(UsuarioSistema parecerista) {
        this.parecerista = parecerista;
    }

    public SituacaoParecer getSituacaoParecer() {
        return situacaoParecer;
    }

    public void setSituacaoParecer(SituacaoParecer situacaoParecer) {
        this.situacaoParecer = situacaoParecer;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public Date getDateParecer() {
        return dateParecer;
    }

    public void setDateParecer(Date dateParecer) {
        this.dateParecer = dateParecer;
    }

    public TipoBem getTipoBem() {
        return tipoBem;
    }

    public void setTipoBem(TipoBem tipoBem) {
        this.tipoBem = tipoBem;
    }

    public boolean deferido() {
        return this.situacaoParecer.equals(SituacaoParecer.DEFERIDO);
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    @Override
    public String toString() {
        try {
            return getSolicitacaoBaixa().toString();
        } catch (Exception ex) {
            return "";
        }
    }
}
