package br.com.webpublico.entidades;


import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
@GrupoDiagrama(nome = "RBTrans")
public class CertificadoRenovacaoOTT extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Condutor")
    @ManyToOne
    private CondutorOperadoraTecnologiaTransporte condutorOtt;
    @Etiqueta("Veiculo")
    @ManyToOne
    private VeiculoOperadoraTecnologiaTransporte veiculoOtt;
    @Temporal(TemporalType.DATE)
    private Date vencimento;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataEmissao;
    @ManyToOne
    private DocumentoOficial documentoOficial;

    public CertificadoRenovacaoOTT() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public CondutorOperadoraTecnologiaTransporte getCondutorOtt() {
        return condutorOtt;
    }

    public void setCondutorOtt(CondutorOperadoraTecnologiaTransporte condutorOtt) {
        this.condutorOtt = condutorOtt;
    }

    public VeiculoOperadoraTecnologiaTransporte getVeiculoOtt() {
        return veiculoOtt;
    }

    public void setVeiculoOtt(VeiculoOperadoraTecnologiaTransporte veiculoOtt) {
        this.veiculoOtt = veiculoOtt;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public DocumentoOficial getDocumentoOficial() {
        return documentoOficial;
    }

    public void setDocumentoOficial(DocumentoOficial documentoOficial) {
        this.documentoOficial = documentoOficial;
    }
}
