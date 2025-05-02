package br.com.webpublico.entidades;

import br.com.webpublico.enums.NaturezaDocumentoMarcaFogo;
import br.com.webpublico.enums.TipoEmissaoMarcaFogo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.beust.jcommander.internal.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Audited
public class ParametroMarcaFogo extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;

    @Etiqueta("Exercício")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    private Exercicio exercicio;

    @OneToMany(mappedBy = "parametroMarcaFogo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaxaMarcaFogo> taxas;

    @OneToMany(mappedBy = "parametroMarcaFogo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentoMarcaFogo> documentos;

    @OneToMany(mappedBy = "parametroMarcaFogo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CertidaoMarcaFogo> certidoes;

    public ParametroMarcaFogo() {
        super();
        taxas = Lists.newArrayList();
        documentos = Lists.newArrayList();
        certidoes = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public List<TaxaMarcaFogo> getTaxas() {
        return taxas;
    }

    public List<TaxaMarcaFogo> getTaxasPorTipoEmissao(TipoEmissaoMarcaFogo tipoEmissao) {
        return taxas.stream().filter(t -> tipoEmissao.equals(t.getTipoEmissao())).collect(Collectors.toList());
    }

    public void setTaxas(List<TaxaMarcaFogo> taxas) {
        this.taxas = taxas;
    }

    public List<DocumentoMarcaFogo> getDocumentos() {
        return documentos;
    }

    public List<DocumentoMarcaFogo> getDocumentosPorNatureza(NaturezaDocumentoMarcaFogo naturezaDocumento) {
        return documentos.stream()
            .filter(d -> d.getNaturezaDocumento().equals(naturezaDocumento))
            .collect(Collectors.toList());
    }

    public void setDocumentos(List<DocumentoMarcaFogo> documentos) {
        this.documentos = documentos;
    }

    public List<CertidaoMarcaFogo> getCertidoes() {
        return certidoes;
    }

    public void setCertidoes(List<CertidaoMarcaFogo> certidoes) {
        this.certidoes = certidoes;
    }
}
