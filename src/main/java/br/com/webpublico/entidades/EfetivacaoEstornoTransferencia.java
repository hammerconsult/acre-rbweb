package br.com.webpublico.entidades;

import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 25/09/14
 * Time: 14:53
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Table(name = "EFETIVACAOESTORNOTRANSF")
@Etiqueta("Efetivação de Estorno de Transferência")
public class EfetivacaoEstornoTransferencia extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    @Pesquisavel
    @Tabelavel
    @Etiqueta("Código")
    private Long codigo;

    @Temporal(TemporalType.DATE)
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data da Efetivação")
    private Date dataEfetivacao;

    @Obrigatorio
    @Tabelavel
    @ManyToOne
    @Etiqueta("Efetivador")
    private UsuarioSistema efetivador;

    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Solicitação de Estorno de Transferência")
    @OneToOne
    private SolicitacaoEstornoTransferencia solicitacaoEstorno;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @OneToMany(mappedBy = "efetivacaoEstorno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemEfetivacaoEstornoTransferencia> listaItemEfetivacaoEstornoTransferencia;

    public EfetivacaoEstornoTransferencia() {
        super();
        listaItemEfetivacaoEstornoTransferencia = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SolicitacaoEstornoTransferencia getSolicitacaoEstorno() {
        return solicitacaoEstorno;
    }

    public void setSolicitacaoEstorno(SolicitacaoEstornoTransferencia solicitacaoEstorno) {
        this.solicitacaoEstorno = solicitacaoEstorno;
    }

    public List<ItemEfetivacaoEstornoTransferencia> getListaItemEfetivacaoEstornoTransferencia() {
        return listaItemEfetivacaoEstornoTransferencia;
    }

    public void setListaItemEfetivacaoEstornoTransferencia(List<ItemEfetivacaoEstornoTransferencia> listaItemEfetivacaoEstornoTransferencia) {
        this.listaItemEfetivacaoEstornoTransferencia = listaItemEfetivacaoEstornoTransferencia;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public Date getDataEfetivacao() {
        return dataEfetivacao;
    }

    public void setDataEfetivacao(Date dataEfetivacao) {
        this.dataEfetivacao = dataEfetivacao;
    }

    public UsuarioSistema getEfetivador() {
        return efetivador;
    }

    public void setEfetivador(UsuarioSistema efetivador) {
        this.efetivador = efetivador;
    }
}
