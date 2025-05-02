package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * @author Fabio
 */
@Entity
@Etiqueta("Linha da Exportação de Débitos IPTU")
public class ExportacaoDebitosIPTULinha extends SuperEntidade implements Comparable<ExportacaoDebitosIPTULinha> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long indice;
    @ManyToOne
    @Etiqueta("Exportação")
    private ExportacaoDebitosIPTU exportacaoDebitosIPTU;
    private String linha;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ExportacaoDebitosIPTU getExportacaoDebitosIPTU() {
        return exportacaoDebitosIPTU;
    }

    public void setExportacaoDebitosIPTU(ExportacaoDebitosIPTU exportacaoDebitosIPTU) {
        this.exportacaoDebitosIPTU = exportacaoDebitosIPTU;
    }

    public String getLinha() {
        return linha;
    }

    public void setLinha(String linha) {
        this.linha = linha;
    }

    public Long getIndice() {
        return indice;
    }

    public void setIndice(Long indice) {
        this.indice = indice;
    }

    @Override
    public int compareTo(ExportacaoDebitosIPTULinha o) {
        return this.getIndice().compareTo(o.getIndice());
    }
}
