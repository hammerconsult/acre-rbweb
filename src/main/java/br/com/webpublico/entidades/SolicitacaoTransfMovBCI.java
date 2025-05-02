package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoSolicitacaoTransferenciaMovBCI;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@GrupoDiagrama(nome = "Tributario")
@Etiqueta("Solicitação de Transferência de Movimentação do Cadastro Imobiliário")
public class SolicitacaoTransfMovBCI extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Número")
    private Long numeroSolicitacao;

    @Pesquisavel
    @Tabelavel
    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Data da Solicitação")
    private Date dataSolicitacao;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número Protocolo")
    private String numeroProtocolo;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Ano Protocolo")
    private String anoProtocolo;
    @Etiqueta("Motivo")
    private String motivo;
    private String enderecoCadastroOrigem;
    private String enderecoCadastroDestino;

    @Etiqueta("Inativar Inscrição Imobiliária")
    private Boolean inativarInscricaoImob;
    @Etiqueta("Transferir Dívidas Ativas")
    private Boolean transferirDividasAtivas;
    @Etiqueta("Transferir Cáculos e Revisões de IPTU")
    private Boolean transferirCalculosRevisaoIPTU;
    @Etiqueta("Transferir Isenções de IPTU")
    private Boolean transferirIsencoesIPTU;
    @Etiqueta("Transferir Parcelamentos")
    private Boolean transferirParcelamentos;
    @Etiqueta("Transferir Debitos e DAMs")
    private Boolean transferirDebitosDams;
    @Etiqueta("Transferir Certidões")
    private Boolean transferirCertidoes;
    @Etiqueta("Transferir Lançamentos de ITBI")
    private Boolean transferirLancamentosITBI;
    @Etiqueta("Transferir Processos de Débitos")
    private Boolean transferirProcessosDebitos;

    @Pesquisavel
    @Tabelavel
    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação")
    private SituacaoSolicitacaoTransferenciaMovBCI situacao;

    @Pesquisavel
    @Tabelavel
    @ManyToOne
    @Etiqueta("Responsável")
    private UsuarioSistema responsavel;
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    @Etiqueta("Cadastro Imobiliário de Origem")
    private CadastroImobiliario cadastroOrigem;
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    @Etiqueta("Cadastro Imobiliário de Destino")
    private CadastroImobiliario cadastroDestino;
    @OneToMany(mappedBy = "solicitacao", orphanRemoval = true)
    @Etiqueta("Arquivos da Solicitação")
    private List<ArquivoTransferenciaMovBCI> arquivos;

    public SolicitacaoTransfMovBCI() {
        this.inativarInscricaoImob = Boolean.FALSE;
        this.transferirDividasAtivas = Boolean.FALSE;
        this.transferirCalculosRevisaoIPTU = Boolean.FALSE;
        this.transferirIsencoesIPTU = Boolean.FALSE;
        this.transferirParcelamentos = Boolean.FALSE;
        this.transferirDebitosDams = Boolean.FALSE;
        this.transferirCertidoes = Boolean.FALSE;
        this.transferirLancamentosITBI = Boolean.FALSE;
        this.transferirProcessosDebitos = Boolean.FALSE;
        this.arquivos = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumeroSolicitacao() {
        return numeroSolicitacao;
    }

    public void setNumeroSolicitacao(Long numeroSolicitacao) {
        this.numeroSolicitacao = numeroSolicitacao;
    }

    public Date getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(Date dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public String getAnoProtocolo() {
        return anoProtocolo;
    }

    public void setAnoProtocolo(String anoProtocolo) {
        this.anoProtocolo = anoProtocolo;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Boolean getInativarInscricaoImob() {
        return inativarInscricaoImob;
    }

    public void setInativarInscricaoImob(Boolean inativarInscricaoImob) {
        this.inativarInscricaoImob = inativarInscricaoImob;
    }

    public Boolean getTransferirDividasAtivas() {
        return transferirDividasAtivas;
    }

    public void setTransferirDividasAtivas(Boolean transferirDividasAtivas) {
        this.transferirDividasAtivas = transferirDividasAtivas;
    }

    public Boolean getTransferirCalculosRevisaoIPTU() {
        return transferirCalculosRevisaoIPTU;
    }

    public void setTransferirCalculosRevisaoIPTU(Boolean transferirCalculosRevisaoIPTU) {
        this.transferirCalculosRevisaoIPTU = transferirCalculosRevisaoIPTU;
    }

    public Boolean getTransferirIsencoesIPTU() {
        return transferirIsencoesIPTU;
    }

    public void setTransferirIsencoesIPTU(Boolean transferirIsencoesIPTU) {
        this.transferirIsencoesIPTU = transferirIsencoesIPTU;
    }

    public Boolean getTransferirParcelamentos() {
        return transferirParcelamentos;
    }

    public void setTransferirParcelamentos(Boolean transferirParcelamentos) {
        this.transferirParcelamentos = transferirParcelamentos;
    }

    public Boolean getTransferirDebitosDams() {
        return transferirDebitosDams;
    }

    public void setTransferirDebitosDams(Boolean transferirDebitosDams) {
        this.transferirDebitosDams = transferirDebitosDams;
    }

    public Boolean getTransferirCertidoes() {
        return transferirCertidoes;
    }

    public void setTransferirCertidoes(Boolean transferirCertidoes) {
        this.transferirCertidoes = transferirCertidoes;
    }

    public Boolean getTransferirLancamentosITBI() {
        return transferirLancamentosITBI;
    }

    public void setTransferirLancamentosITBI(Boolean transferirLancamentosITBI) {
        this.transferirLancamentosITBI = transferirLancamentosITBI;
    }

    public Boolean getTransferirProcessosDebitos() {
        return transferirProcessosDebitos;
    }

    public void setTransferirProcessosDebitos(Boolean transferirProcessosDebitos) {
        this.transferirProcessosDebitos = transferirProcessosDebitos;
    }

    public SituacaoSolicitacaoTransferenciaMovBCI getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoSolicitacaoTransferenciaMovBCI situacao) {
        this.situacao = situacao;
    }

    public UsuarioSistema getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(UsuarioSistema responsavel) {
        this.responsavel = responsavel;
    }

    public CadastroImobiliario getCadastroOrigem() {
        return cadastroOrigem;
    }

    public void setCadastroOrigem(CadastroImobiliario cadastroOrigem) {
        this.cadastroOrigem = cadastroOrigem;
    }

    public CadastroImobiliario getCadastroDestino() {
        return cadastroDestino;
    }

    public void setCadastroDestino(CadastroImobiliario cadastroDestino) {
        this.cadastroDestino = cadastroDestino;
    }

    public String getEnderecoCadastroOrigem() {
        return enderecoCadastroOrigem;
    }

    public void setEnderecoCadastroOrigem(String enderecoCadastroOrigem) {
        this.enderecoCadastroOrigem = enderecoCadastroOrigem;
    }

    public String getEnderecoCadastroDestino() {
        return enderecoCadastroDestino;
    }

    public void setEnderecoCadastroDestino(String enderecoCadastroDestino) {
        this.enderecoCadastroDestino = enderecoCadastroDestino;
    }

    public List<ArquivoTransferenciaMovBCI> getArquivos() {
        return arquivos;
    }

    public void setArquivos(List<ArquivoTransferenciaMovBCI> arquivos) {
        this.arquivos = arquivos;
    }

    @Override
    public String toString() {
        String descricao = "";
        if(numeroSolicitacao != null) {
            descricao += numeroSolicitacao;
            if(cadastroOrigem != null) {
                descricao += " - ";
            }
        }
        if(cadastroOrigem != null) {
            descricao += "Cadastro de Origem: " + cadastroOrigem.getInscricaoCadastral();
            if(cadastroDestino != null) {
                descricao += " | ";
            }
        }
        if(cadastroDestino != null) {
            descricao += "Cadastro de Destino: " + cadastroDestino.getInscricaoCadastral();
        }
        return descricao;
    }
}
