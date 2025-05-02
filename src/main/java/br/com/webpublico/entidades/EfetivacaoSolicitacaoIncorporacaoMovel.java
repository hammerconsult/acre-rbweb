package br.com.webpublico.entidades;

import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Desenvolvimento on 02/02/2016.
 */
@Entity
@Audited
@Table(name = "EFETSOLICIINCORPOMOVEL")
@Etiqueta("Efetivação de Solicitação de Incorporação de Bem Móvel")
public class EfetivacaoSolicitacaoIncorporacaoMovel extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Código")
    private Long codigo;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data da Efetivação")
    @Temporal(TemporalType.DATE)
    private Date dateEfetivaacao;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    @Etiqueta("Responsável")
    private UsuarioSistema responsavel;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @OneToOne
    @Etiqueta("Solicitação de Incorporção Móvel")
    private SolicitacaoIncorporacaoMovel solicitacaoIncorporacao;

    @Invisivel
    @OneToMany(mappedBy = "efetivacao", cascade = CascadeType.ALL)
    private List<ItemEfetivacaoIncorporacaoMovel> itensEfetivacao;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    public EfetivacaoSolicitacaoIncorporacaoMovel() {
        super();
        itensEfetivacao = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public Date getDateEfetivaacao() {
        return dateEfetivaacao;
    }

    public void setDateEfetivaacao(Date dateEfetivaacao) {
        this.dateEfetivaacao = dateEfetivaacao;
    }

    public UsuarioSistema getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(UsuarioSistema responsavel) {
        this.responsavel = responsavel;
    }

    public SolicitacaoIncorporacaoMovel getSolicitacaoIncorporacao() {
        return solicitacaoIncorporacao;
    }

    public void setSolicitacaoIncorporacao(SolicitacaoIncorporacaoMovel solicitacaoIncorporacao) {
        this.solicitacaoIncorporacao = solicitacaoIncorporacao;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public List<ItemEfetivacaoIncorporacaoMovel> getItensEfetivacao() {
        return itensEfetivacao;
    }

    public void setItensEfetivacao(List<ItemEfetivacaoIncorporacaoMovel> itensEfetivacao) {
        this.itensEfetivacao = itensEfetivacao;
    }

    public Boolean isSolicitacaoNull(){
        return solicitacaoIncorporacao == null;
    }

    @Override
    public String toString() {
        return codigo + " - " + DataUtil.getDataFormatada(dateEfetivaacao) + " - " + solicitacaoIncorporacao.getUnidadeAdministrativa();
    }
}
