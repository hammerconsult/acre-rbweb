package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoMovimentoProcessoLicitatorio;
import br.com.webpublico.enums.TipoSolicitacaoRegistroPreco;
import br.com.webpublico.interfaces.EntidadeDetendorDocumentoLicitacao;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 30/07/14
 * Time: 08:36
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited

@Table(name = "SOLICITACAOMATERIALEXT")
@Etiqueta("Solicitação de Adesão à Ata de Registro de Preço")
public class SolicitacaoMaterialExterno extends SuperEntidade implements EntidadeDetendorDocumentoLicitacao {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Número")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private Integer numero;

    @Etiqueta("Exercício")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    private Exercicio exercicio;

    @Etiqueta("Data da Solicitação")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Temporal(TemporalType.DATE)
    private Date dataSolicitacao;

    @Etiqueta("Tipo Solicitação")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Enumerated(EnumType.STRING)
    private TipoSolicitacaoRegistroPreco tipoSolicitacaoRegistroPreco;

    @Etiqueta("Órgão/Entidade/Fundo Solicitante")
    @Obrigatorio
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;

    @Transient
    @Tabelavel
    @Etiqueta("Órgão/Entidade/Fundo Solicitante")
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    @Etiqueta("Órgão/Entidade/Fundo Gerenciador")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @ManyToOne
    private PessoaJuridica pessoaJuridica;

    @Etiqueta("Número Ata Registro de Preço Externa")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private Integer numeroAtaExterna;

    @Etiqueta("Ata de Registro de Preço")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @ManyToOne
    private AtaRegistroPreco ataRegistroPreco;

    @Etiqueta("Objeto")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Length(maximo = 3000)
    private String objeto;

    @Etiqueta("Justificativa")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Length(maximo = 3000)
    private String justificativa;

    @Etiqueta("Documento")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @ManyToOne
    private Documento documento;

    @Invisivel
    @Etiqueta("Itens da Solicitação")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "solicitacaoMaterialExterno")
    private List<ItemSolicitacaoExterno> itensDaSolicitacao;

    @Invisivel
    @Etiqueta("Adesões da Solicitação de Registro de Preço Interno")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "solicitacaoMaterialExterno")
    private List<Adesao> adesoes;

    @Etiqueta("Documento Autorização do Gerenciador")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private String documentoGerenciador;

    @Etiqueta("Documento Autorização do Fornecedor")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private String documentoAutorizacao;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private DetentorDocumentoLicitacao detentorDocumentoLicitacao;

    public SolicitacaoMaterialExterno() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Date getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(Date dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public TipoSolicitacaoRegistroPreco getTipoSolicitacaoRegistroPreco() {
        return tipoSolicitacaoRegistroPreco;
    }


    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.unidadeOrganizacional = null;
        if (hierarquiaOrganizacional != null) {
            this.unidadeOrganizacional = hierarquiaOrganizacional.getSubordinada();
        }
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setTipoSolicitacaoRegistroPreco(TipoSolicitacaoRegistroPreco tipoSolicitacaoRegistroPreco) {
        this.tipoSolicitacaoRegistroPreco = tipoSolicitacaoRegistroPreco;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public PessoaJuridica getPessoaJuridica() {
        return pessoaJuridica;
    }

    public void setPessoaJuridica(PessoaJuridica pessoaJuridica) {
        this.pessoaJuridica = pessoaJuridica;
    }

    public Integer getNumeroAtaExterna() {
        return numeroAtaExterna;
    }

    public void setNumeroAtaExterna(Integer numeroAtaExterna) {
        this.numeroAtaExterna = numeroAtaExterna;
    }

    public AtaRegistroPreco getAtaRegistroPreco() {
        return ataRegistroPreco;
    }

    public void setAtaRegistroPreco(AtaRegistroPreco ataRegistroPreco) {
        this.ataRegistroPreco = ataRegistroPreco;
    }

    public String getObjeto() {
        return objeto;
    }

    public void setObjeto(String objeto) {
        this.objeto = objeto;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public Documento getDocumento() {
        return documento;
    }

    public void setDocumento(Documento documento) {
        this.documento = documento;
    }

    public List<ItemSolicitacaoExterno> getItensDaSolicitacao() {
        return itensDaSolicitacao;
    }

    public void setItensDaSolicitacao(List<ItemSolicitacaoExterno> itensDaSolicitacao) {
        this.itensDaSolicitacao = itensDaSolicitacao;
    }

    public List<Adesao> getAdesoes() {
        return adesoes;
    }

    public void setAdesoes(List<Adesao> adesoes) {
        this.adesoes = adesoes;
    }

    public String getDocumentoAutorizacao() {
        return documentoAutorizacao;
    }

    public void setDocumentoAutorizacao(String documentoAutorizacao) {
        this.documentoAutorizacao = documentoAutorizacao;
    }

    @Override
    public DetentorDocumentoLicitacao getDetentorDocumentoLicitacao() {
        return detentorDocumentoLicitacao;
    }

    @Override
    public void setDetentorDocumentoLicitacao(DetentorDocumentoLicitacao detentorDocumentoLicitacao) {
        this.detentorDocumentoLicitacao = detentorDocumentoLicitacao;
    }

    @Override
    public TipoMovimentoProcessoLicitatorio getTipoAnexo() {
        return TipoMovimentoProcessoLicitatorio.SOLICITACAO_ADESAO_EXTERNA;
    }

    public String getDocumentoGerenciador() {
        return documentoGerenciador;
    }

    public void setDocumentoGerenciador(String documentoGerenciador) {
        this.documentoGerenciador = documentoGerenciador;
    }

    @Override
    public String toString() {
        return numero + "/" + exercicio + " - " + DataUtil.getDataFormatada(dataSolicitacao) + " Tipo Solicitação: " + tipoSolicitacaoRegistroPreco.getDescricao();
    }

    public boolean isSolicitacaoInterna() {
        try {
            return TipoSolicitacaoRegistroPreco.INTERNA.equals(this.getTipoSolicitacaoRegistroPreco());
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean isSolicitacaoExterna() {
        try {
            return TipoSolicitacaoRegistroPreco.EXTERNA.equals(this.getTipoSolicitacaoRegistroPreco());
        } catch (Exception ex) {
            return false;
        }
    }
}
