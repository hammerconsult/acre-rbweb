package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoArquivoLogAlvaraRedeSim;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class ArquivoLogAlvaraRedeSim extends SuperEntidade implements PossuidorArquivo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivo;
    @Enumerated(EnumType.STRING)
    private TipoArquivoLogAlvaraRedeSim tipoArquivo;

    public ArquivoLogAlvaraRedeSim() {
        this.detentorArquivo = new DetentorArquivoComposicao();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoArquivoLogAlvaraRedeSim getTipoArquivo() {
        return tipoArquivo;
    }

    public void setTipoArquivo(TipoArquivoLogAlvaraRedeSim tipoArquivo) {
        this.tipoArquivo = tipoArquivo;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivo;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentoArquivo) {
        this.detentorArquivo = detentoArquivo;
    }
}
