package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.beust.jcommander.internal.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Fabio
 */
@GrupoDiagrama(nome = "CadastroImobiliario")
@Entity

@Audited
@Etiqueta("Transferência de Proprietário")
public class TransferenciaProprietario extends SuperEntidade implements Serializable, PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número")
    private Long codigo;
    private static final long serialVersionUID = 1L;
    @Etiqueta("Cadastro Imobiliário")
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    private CadastroImobiliario cadastroImobiliario;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data Transferência")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataTransferencia;
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
    @OneToMany(mappedBy = "transferenciaProprietario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PessoaTransferenciaProprietario> proprietarios;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivo;
    @OneToMany(mappedBy = "transferenciaProprietario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PessoaTransferenciaProprietarioAnterior> proprietariosAnteriores;
    @ManyToOne
    private UsuarioSistema usuarioSistema;

    public TransferenciaProprietario() {
        proprietarios = Lists.newArrayList();
        proprietariosAnteriores = Lists.newArrayList();
    }

    public CadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
    }

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

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public String getAnoProtocolo() {
        return anoProtocolo;
    }

    public void setAnoProtocolo(String anoProtocolo) {
        this.anoProtocolo = anoProtocolo;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public Date getDataTransferencia() {
        return dataTransferencia;
    }

    public void setDataTransferencia(Date dataTransferencia) {
        this.dataTransferencia = dataTransferencia;
    }

    public List<PessoaTransferenciaProprietario> getProprietarios() {
        return proprietarios;
    }

    public void setProprietarios(List<PessoaTransferenciaProprietario> proprietarios) {
        this.proprietarios = proprietarios;
    }

    public List<PessoaTransferenciaProprietarioAnterior> getProprietariosAnteriores() {
        return proprietariosAnteriores;
    }

    public void setProprietariosAnteriores(List<PessoaTransferenciaProprietarioAnterior> proprietariosAnteriores) {
        this.proprietariosAnteriores = proprietariosAnteriores;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivo;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivo = detentorArquivoComposicao;
    }
}
