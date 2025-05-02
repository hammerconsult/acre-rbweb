package br.com.webpublico.entidades.rh.arquivos;

import br.com.webpublico.entidades.Sicap;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.SicapTipoArquivo;

import javax.persistence.*;

/**
 * @Author peixe on 15/07/2016  18:45.
 */
@Entity
public class TipoSicapArquivo extends SuperEntidade {


    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Sicap sicap;
    @Enumerated(EnumType.STRING)
    private SicapTipoArquivo sicapTipoArquivo;

    public TipoSicapArquivo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SicapTipoArquivo getSicapTipoArquivo() {
        return sicapTipoArquivo;
    }

    public void setSicapTipoArquivo(SicapTipoArquivo sicapTipoArquivo) {
        this.sicapTipoArquivo = sicapTipoArquivo;
    }

    public Sicap getSicap() {
        return sicap;
    }

    public void setSicap(Sicap sicap) {
        this.sicap = sicap;
    }

    @Override
    public String toString() {
        return sicapTipoArquivo != null ? sicapTipoArquivo.getDescricao() : "";
    }
}
