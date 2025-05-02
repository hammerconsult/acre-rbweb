package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoVistoriaOtt;
import br.com.webpublico.enums.TipoParecerVistoriaOtt;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Table(name = "VISTORIAVEICULOOTT")
@Entity
@GrupoDiagrama(nome = "RBTrans")
@Audited
@Etiqueta("Vistoria de Veículo da OTT")
public class VistoriaVeiculoOtt extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Operadora")
    private OperadoraTecnologiaTransporte operadoraTecTransporte;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Veículo")
    private VeiculoOperadoraTecnologiaTransporte veiculoOtTransporte;
    @ManyToOne
    @Etiqueta("Pessoa")
    private PessoaFisica responsavel;
    @Tabelavel
    @Temporal(TemporalType.DATE)
    @Pesquisavel
    @Etiqueta("Data da Vistoria")
    private Date dataVistoria;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Situação")
    @Enumerated(EnumType.STRING)
    private SituacaoVistoriaOtt situacao;
    @Etiqueta("Parecer")
    private String parecer;
    @Etiqueta("Tipo Parecer")
    @Enumerated(EnumType.STRING)
    private TipoParecerVistoriaOtt tipoDeParecer;
    @Tabelavel
    @Temporal(TemporalType.DATE)
    @Pesquisavel
    @Etiqueta("Data vencimento")
    private Date vencimento;
    @OneToOne(cascade = CascadeType.ALL)
    private DetentorArquivoComposicao detentorArquivoComposicao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OperadoraTecnologiaTransporte getOperadoraTecTransporte() {
        return operadoraTecTransporte;
    }

    public void setOperadoraTecTransporte(OperadoraTecnologiaTransporte operadoraTecTransporte) {
        this.operadoraTecTransporte = operadoraTecTransporte;
    }

    public VeiculoOperadoraTecnologiaTransporte getVeiculoOtTransporte() {
        return veiculoOtTransporte;
    }

    public void setVeiculoOtTransporte(VeiculoOperadoraTecnologiaTransporte veiculoOtTransporte) {
        this.veiculoOtTransporte = veiculoOtTransporte;
    }

    public PessoaFisica getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(PessoaFisica responsavel) {
        this.responsavel = responsavel;
    }

    public Date getDataVistoria() {
        return dataVistoria;
    }

    public void setDataVistoria(Date dataVistoria) {
        this.dataVistoria = dataVistoria;
    }

    public SituacaoVistoriaOtt getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoVistoriaOtt situacao) {
        this.situacao = situacao;
    }

    public String getParecer() {
        return parecer;
    }

    public void setParecer(String parecer) {
        this.parecer = parecer;
    }

    public TipoParecerVistoriaOtt getTipoDeParecer() {
        return tipoDeParecer;
    }

    public void setTipoDeParecer(TipoParecerVistoriaOtt tipoDeParecer) {
        this.tipoDeParecer = tipoDeParecer;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }
}
