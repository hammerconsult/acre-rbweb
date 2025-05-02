package br.com.webpublico.entidades;

import br.com.webpublico.interfaces.PossuidorArquivo;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
public class VistoriaVeiculoSaud extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue
    private Long id;
    @Temporal(TemporalType.DATE)
    private Date dataCadastro;
    @Temporal(TemporalType.DATE)
    private Date dataVistoria;
    @ManyToOne
    private VeiculoSaud veiculoSaud;
    @ManyToOne
    private Pessoa vistoriador;
    @ManyToOne(cascade = CascadeType.ALL)
    private DetentorArquivoComposicao detentorArquivoComposicao;


    public VistoriaVeiculoSaud() {
        super();
        this.dataCadastro = new Date();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Date getDataVistoria() {
        return dataVistoria;
    }

    public void setDataVistoria(Date dataVistoria) {
        this.dataVistoria = dataVistoria;
    }

    public VeiculoSaud getVeiculoSaud() {
        return veiculoSaud;
    }

    public void setVeiculoSaud(VeiculoSaud veiculoSaud) {
        this.veiculoSaud = veiculoSaud;
    }

    public Pessoa getVistoriador() {
        return vistoriador;
    }

    public void setVistoriador(Pessoa vistoriador) {
        this.vistoriador = vistoriador;
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
