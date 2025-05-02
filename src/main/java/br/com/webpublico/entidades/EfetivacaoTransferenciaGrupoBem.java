package br.com.webpublico.entidades;

import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Length;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@Table(name = "EFETIVACAOTRANSFGRUPOBEM")
@Etiqueta(value = "Efetivação de Transferência Grupo Patrimonial")
public class EfetivacaoTransferenciaGrupoBem extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Número")
    private Long numero;

    @Etiqueta("Data da Efetivação")
    @Temporal(TemporalType.DATE)
    private Date dataEfetivacao;

    @Obrigatorio
    @ManyToOne
    @Etiqueta("Solicitação")
    private SolicitacaoTransferenciaGrupoBem solicitacao;

    @Obrigatorio
    @Etiqueta("Descrição")
    @Length(maximo = 3000)
    private String descricao;

    @ManyToOne
    @Etiqueta("Responsável")
    private UsuarioSistema responsavel;

    @Invisivel
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @OneToMany(mappedBy = "efetivacao")
    private List<ItemEfetivacaoTransferenciaGrupoBem> itens;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public SolicitacaoTransferenciaGrupoBem getSolicitacao() {
        return solicitacao;
    }

    public void setSolicitacao(SolicitacaoTransferenciaGrupoBem solicitacao) {
        this.solicitacao = solicitacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public UsuarioSistema getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(UsuarioSistema responsavel) {
        this.responsavel = responsavel;
    }

    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public List<ItemEfetivacaoTransferenciaGrupoBem> getItens() {
        return itens;
    }

    public void setItens(List<ItemEfetivacaoTransferenciaGrupoBem> itens) {
        this.itens = itens;
    }

    public String getHistoricoRazao() {
        return "Efetivação de Transferência Grupo Bem Móvel nº " + getNumero();
    }
}
