package br.com.webpublico.entidades;

import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by andregustavo on 12/01/2015.
 */
@Entity
@Audited
@Etiqueta("Certidão de Óbito RBTrans")
public class CertidaoObitoRBTrans extends SuperEntidade implements PossuidorArquivo {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Matrícula")
    private String matricula;
    @Etiqueta("Data de Falecimento")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataFalecimento;
    @Etiqueta("Local de Sepultamento")
    private String localSepultamento;
    @Etiqueta("Nome do Ofício")
    private String nomeOficio;
    @OneToOne
    private TransferenciaPermissaoTransporte transferencia;
    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    public CertidaoObitoRBTrans() {
    }

    public CertidaoObitoRBTrans(TransferenciaPermissaoTransporte transferencia) {
        this.transferencia = transferencia;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public Date getDataFalecimento() {
        return dataFalecimento;
    }

    public void setDataFalecimento(Date dataFalecimento) {
        this.dataFalecimento = dataFalecimento;
    }

    public String getLocalSepultamento() {
        return localSepultamento;
    }

    public void setLocalSepultamento(String localSepultamento) {
        this.localSepultamento = localSepultamento;
    }

    public String getNomeOficio() {
        return nomeOficio;
    }

    public void setNomeOficio(String nomeOficio) {
        this.nomeOficio = nomeOficio;
    }

    public TransferenciaPermissaoTransporte getTransferencia() {
        return transferencia;
    }

    public void setTransferencia(TransferenciaPermissaoTransporte transferencia) {
        this.transferencia = transferencia;
    }

    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }
}
