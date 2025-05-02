package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoProcessoDebito;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@Etiqueta("Processo de Protesto")
public class ProcessoDeProtesto extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    @Etiqueta("Código")
    private Long codigo;
    @Enumerated(EnumType.STRING)
    private SituacaoProcessoDebito situacao;
    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Data de Lançamento")
    private Date lancamento;
    @Etiqueta("Número do Protocolo")
    private String numeroProtocolo;
    private String motivo;
    @ManyToOne
    private UsuarioSistema usuarioIncluiu;
    @ManyToOne
    @Etiqueta("Ato Legal")
    private AtoLegal atoLegal;
    @Enumerated(EnumType.STRING)
    private TipoCadastroTributario tipoCadastroTributario;
    @ManyToOne
    private Cadastro cadastroProtesto;
    @ManyToOne
    private Pessoa pessoaProtesto;
    @Etiqueta("Arquivos")
    @OneToMany(mappedBy = "processoDeProtesto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProcessoDeProtestoArquivo> arquivos;
    @OneToMany(mappedBy = "processoDeProtesto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemProcessoDeProtesto> itens;
    @OneToMany(mappedBy = "processoDeProtesto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CdaProcessoDeProtesto> cdas;
    @ManyToOne
    @Etiqueta("Documento")
    private DocumentoOficial documentoOficial;
    private String motivoCancelamento;

    public ProcessoDeProtesto() {
        super();
        arquivos = Lists.newArrayList();
        itens = Lists.newArrayList();
        cdas = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public SituacaoProcessoDebito getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoProcessoDebito situacao) {
        this.situacao = situacao;
    }

    public Date getLancamento() {
        return lancamento;
    }

    public void setLancamento(Date lancamento) {
        this.lancamento = lancamento;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public UsuarioSistema getUsuarioIncluiu() {
        return usuarioIncluiu;
    }

    public void setUsuarioIncluiu(UsuarioSistema usuarioIncluiu) {
        this.usuarioIncluiu = usuarioIncluiu;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public List<ProcessoDeProtestoArquivo> getArquivos() {
        return arquivos;
    }

    public void setArquivos(List<ProcessoDeProtestoArquivo> arquivos) {
        this.arquivos = arquivos;
    }

    public List<ItemProcessoDeProtesto> getItens() {
        return itens;
    }

    public void setItens(List<ItemProcessoDeProtesto> itens) {
        this.itens = itens;
    }

    public DocumentoOficial getDocumentoOficial() {
        return documentoOficial;
    }

    public void setDocumentoOficial(DocumentoOficial documentoOficial) {
        this.documentoOficial = documentoOficial;
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    public Cadastro getCadastroProtesto() {
        return cadastroProtesto;
    }

    public void setCadastroProtesto(Cadastro cadastroProtesto) {
        this.cadastroProtesto = cadastroProtesto;
    }

    public Pessoa getPessoaProtesto() {
        return pessoaProtesto;
    }

    public void setPessoaProtesto(Pessoa pessoaProtesto) {
        this.pessoaProtesto = pessoaProtesto;
    }

    public List<CdaProcessoDeProtesto> getCdas() {
        return cdas;
    }

    public void setCdas(List<CdaProcessoDeProtesto> cdas) {
        this.cdas = cdas;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }

    public boolean isCancelado() {
        return SituacaoProcessoDebito.CANCELADO.equals(situacao);
    }
}
