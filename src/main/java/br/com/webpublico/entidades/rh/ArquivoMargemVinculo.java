package br.com.webpublico.entidades.rh;

import br.com.webpublico.entidades.ExportaArquivoMargem;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.VinculoFP;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by William on 14/11/2018.
 */
@Entity
@Audited
public class ArquivoMargemVinculo extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private VinculoFP vinculoFP;
    @ManyToOne
    private ExportaArquivoMargem exportaArquivoMargem;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public ExportaArquivoMargem getExportaArquivoMargem() {
        return exportaArquivoMargem;
    }

    public void setExportaArquivoMargem(ExportaArquivoMargem exportaArquivoMargem) {
        this.exportaArquivoMargem = exportaArquivoMargem;
    }
}
