package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoTransfMovimentoPessoa;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 05/02/15
 * Time: 08:28
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Solicitação de Transferência de Movimentos da Pessoa")
public class TransferenciaMovPessoa extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Número")
    private Long numeroTransferencia;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Data/Hora")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataTransferencia;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Situação")
    @Enumerated(EnumType.STRING)
    private SituacaoTransfMovimentoPessoa situacao;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Responsável")
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Pessoa de Origem")
    @Obrigatorio
    @ManyToOne
    private Pessoa pessoaOrigem;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Pessoa de Destino")
    @Obrigatorio
    @ManyToOne
    private Pessoa pessoaDestino;
    @Obrigatorio
    @Etiqueta("Motivo")
    private String motivo;
    @Invisivel
    @OneToOne(cascade = CascadeType.ALL)
    private DetentorArquivoComposicao detentorArquivoComposicao;
    private Boolean transfereBcis;
    private Boolean transfereBces;
    private Boolean transfereBcrs;
    private Boolean transfereMovimentosTributario;
    private Boolean transfereMovContabeis;
    private Boolean transferirDocumentosPessoais;
    private Boolean transferirEnderecos;
    private Boolean transferirTelefones;
    private Boolean transferirInformacoesBancarias;
    private Boolean transferirDependentes;
    private Boolean transferirPensoesAlimenticias;
    private Boolean inativaPessoaTransferida;
    private Boolean transferirCadastrosRH;
    private Boolean transferirRespDependentes;
    private Boolean transferirRespPensoesAliment;
    @OneToMany(mappedBy = "transferenciaMovPessoa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransferenciaMovPessoaBci> bcis;
    @OneToMany(mappedBy = "transferenciaMovPessoa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransferenciaMovPessoaBce> bces;
    @OneToMany(mappedBy = "transferenciaMovPessoa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransferenciaMovPessoaBcr> bcrs;
    @OneToMany(mappedBy = "transferenciaMovPessoa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParecerTransferenciaMovimentoPessoa> pareceresTransferecia;

    public TransferenciaMovPessoa() {
        super();
        bcis = new ArrayList();
        bces = new ArrayList();
        bcrs = new ArrayList();
        pareceresTransferecia = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumeroTransferencia() {
        return numeroTransferencia;
    }

    public void setNumeroTransferencia(Long numeroTransferencia) {
        this.numeroTransferencia = numeroTransferencia;
    }

    public Date getDataTransferencia() {
        return dataTransferencia;
    }

    public void setDataTransferencia(Date dataTransferencia) {
        this.dataTransferencia = dataTransferencia;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Pessoa getPessoaOrigem() {
        return pessoaOrigem;
    }

    public void setPessoaOrigem(Pessoa pessoaOrigem) {
        this.pessoaOrigem = pessoaOrigem;
    }

    public Pessoa getPessoaDestino() {
        return pessoaDestino;
    }

    public void setPessoaDestino(Pessoa pessoaDestino) {
        this.pessoaDestino = pessoaDestino;
    }

    public Boolean getTransfereBcis() {
        return transfereBcis != null ? transfereBcis : false;
    }

    public void setTransfereBcis(Boolean transfereBcis) {
        this.transfereBcis = transfereBcis;
    }

    public Boolean getTransfereBces() {
        return transfereBces != null ? transfereBces : false;
    }

    public void setTransfereBces(Boolean transfereBces) {
        this.transfereBces = transfereBces;
    }

    public Boolean getTransfereMovimentosTributario() {
        return transfereMovimentosTributario != null ? transfereMovimentosTributario : false;
    }

    public void setTransfereMovimentosTributario(Boolean transfereMovimentosTributario) {
        this.transfereMovimentosTributario = transfereMovimentosTributario;
    }

    public Boolean getTransfereBcrs() {
        return transfereBcrs != null ? transfereBcrs : false;
    }

    public void setTransfereBcrs(Boolean transfereBcrs) {
        this.transfereBcrs = transfereBcrs;
    }

    public Boolean getInativaPessoaTransferida() {
        return inativaPessoaTransferida != null ? inativaPessoaTransferida : false;
    }

    public void setInativaPessoaTransferida(Boolean inativaPessoaTransferida) {
        this.inativaPessoaTransferida = inativaPessoaTransferida;
    }

    public Boolean getTransfereMovContabeis() {
        return transfereMovContabeis != null ? transfereMovContabeis : false;
    }

    public void setTransfereMovContabeis(Boolean transfereMovContabeis) {
        this.transfereMovContabeis = transfereMovContabeis;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public SituacaoTransfMovimentoPessoa getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoTransfMovimentoPessoa situacao) {
        this.situacao = situacao;
    }

    public List<TransferenciaMovPessoaBci> getBcis() {
        return bcis;
    }

    public void setBcis(List<TransferenciaMovPessoaBci> bcis) {
        this.bcis = bcis;
    }

    public List<TransferenciaMovPessoaBce> getBces() {
        return bces;
    }

    public void setBces(List<TransferenciaMovPessoaBce> bces) {
        this.bces = bces;
    }

    public List<TransferenciaMovPessoaBcr> getBcrs() {
        return bcrs;
    }

    public void setBcrs(List<TransferenciaMovPessoaBcr> bcrs) {
        this.bcrs = bcrs;
    }

    public List<ParecerTransferenciaMovimentoPessoa> getPareceresTransferecia() {
        return pareceresTransferecia;
    }

    public void setPareceresTransferecia(List<ParecerTransferenciaMovimentoPessoa> pareceresTransferecia) {
        this.pareceresTransferecia = pareceresTransferecia;
    }

    public Boolean getTransferirDocumentosPessoais() {
        return transferirDocumentosPessoais != null ? transferirDocumentosPessoais : false;
    }

    public void setTransferirDocumentosPessoais(Boolean transferirDocumentosPessoais) {
        this.transferirDocumentosPessoais = transferirDocumentosPessoais;
    }

    public Boolean getTransferirEnderecos() {
        return transferirEnderecos != null ? transferirEnderecos : false;
    }

    public void setTransferirEnderecos(Boolean transferirEnderecos) {
        this.transferirEnderecos = transferirEnderecos;
    }

    public Boolean getTransferirTelefones() {
        return transferirTelefones != null ? transferirTelefones : false;
    }

    public void setTransferirTelefones(Boolean transferirTelefones) {
        this.transferirTelefones = transferirTelefones;
    }

    public Boolean getTransferirInformacoesBancarias() {
        return transferirInformacoesBancarias != null ? transferirInformacoesBancarias : false;
    }

    public void setTransferirInformacoesBancarias(Boolean transferirInformacoesBancarias) {
        this.transferirInformacoesBancarias = transferirInformacoesBancarias;
    }

    public Boolean getTransferirDependentes() {
        return transferirDependentes != null ? transferirDependentes : false;
    }

    public void setTransferirDependentes(Boolean transferirDependentes) {
        this.transferirDependentes = transferirDependentes;
    }

    public Boolean getTransferirPensoesAlimenticias() {
        return transferirPensoesAlimenticias != null ? transferirPensoesAlimenticias : false;
    }

    public void setTransferirPensoesAlimenticias(Boolean transferirPensoesAlimenticias) {
        this.transferirPensoesAlimenticias = transferirPensoesAlimenticias;
    }

    public Boolean getTransferirCadastrosRH() {
        return transferirCadastrosRH != null ? transferirCadastrosRH : false;
    }

    public void setTransferirCadastrosRH(Boolean transferirCadastrosRH) {
        this.transferirCadastrosRH = transferirCadastrosRH;
    }

    public Boolean getTransferirRespDependentes() {
        return transferirRespDependentes != null ? transferirRespDependentes : false;
    }

    public void setTransferirRespDependentes(Boolean transferirRespDependentes) {
        this.transferirRespDependentes = transferirRespDependentes;
    }

    public Boolean getTransferirRespPensoesAliment() {
        return transferirRespPensoesAliment != null ? transferirRespPensoesAliment : false;
    }

    public void setTransferirRespPensoesAliment(Boolean transferirRespPensoesAliment) {
        this.transferirRespPensoesAliment = transferirRespPensoesAliment;
    }

    public List<Long> buscarListaIdsCadastrosSelecionadosParaClausulaIn(TransferenciaMovPessoa transferenciaMovPessoa) {
        List<Long> ids = Lists.newArrayList();
        if (this.getBcrs() != null && this.getBcrs().size() > 0) {
            for (TransferenciaMovPessoaBcr transferenciaMovPessoaBcr : this.getBcrs()) {
                ids.add(transferenciaMovPessoaBcr.getCadastroRural().getId());

            }
        }
        if (this.getBcis() != null && this.getBcis().size() > 0) {
            for (TransferenciaMovPessoaBci transferenciaMovPessoaBci : this.getBcis()) {
                ids.add(transferenciaMovPessoaBci.getCadastroImobiliario().getId());

            }
        }
        if (this.getBces() != null && this.getBces().size() > 0) {
            for (TransferenciaMovPessoaBce transferenciaMovPessoaBce : this.getBces()) {
                if (transferenciaMovPessoaBce.getCadastroEconomico().getPessoa().getId().equals(transferenciaMovPessoa.getPessoaOrigem().getId())) {
                    ids.add(transferenciaMovPessoaBce.getCadastroEconomico().getId());
                }
            }
        }
        return ids;
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
        return "Nº " + numeroTransferencia + " Data " + DataUtil.getDataFormatada(dataTransferencia) + " Responsável " + usuarioSistema.toString();
    }
}
