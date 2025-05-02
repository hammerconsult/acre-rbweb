package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.DAMResultadoParcela;
import br.com.webpublico.enums.TipoEmissaoMarcaFogo;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

@Entity
@Audited
public class DebitoMarcaFogo extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private MarcaFogo marcaFogo;
    @Enumerated(EnumType.STRING)
    private TipoEmissaoMarcaFogo tipoEmissao;
    @ManyToOne
    private CalculoTaxasDiversas calculoTaxasDiversas;
    @ManyToOne
    private DocumentoOficial documentoOficial;
    @Transient
    private List<DAMResultadoParcela> listaDAMResultadoParcela;
    private boolean imprimiuDocumento;

    public DebitoMarcaFogo() {
        this.imprimiuDocumento = Boolean.FALSE;
        this.listaDAMResultadoParcela = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public MarcaFogo getMarcaFogo() {
        return marcaFogo;
    }

    public void setMarcaFogo(MarcaFogo marcaFogo) {
        this.marcaFogo = marcaFogo;
    }

    public TipoEmissaoMarcaFogo getTipoEmissao() {
        return tipoEmissao;
    }

    public void setTipoEmissao(TipoEmissaoMarcaFogo tipoEmissao) {
        this.tipoEmissao = tipoEmissao;
    }

    public CalculoTaxasDiversas getCalculoTaxasDiversas() {
        return calculoTaxasDiversas;
    }

    public void setCalculoTaxasDiversas(CalculoTaxasDiversas calculoTaxasDiversas) {
        this.calculoTaxasDiversas = calculoTaxasDiversas;
    }

    public DocumentoOficial getDocumentoOficial() {
        return documentoOficial;
    }

    public void setDocumentoOficial(DocumentoOficial documentoOficial) {
        this.documentoOficial = documentoOficial;
    }

    public List<DAMResultadoParcela> getListaDAMResultadoParcela() {
        return listaDAMResultadoParcela;
    }

    public void setListaDAMResultadoParcela(List<DAMResultadoParcela> listaDAMResultadoParcela) {
        this.listaDAMResultadoParcela = listaDAMResultadoParcela;
    }

    public boolean hasDebitoEmAberto() {
        return getListaDAMResultadoParcela().stream().anyMatch(i -> !i.getResultadoParcela().isPago());
    }

    public boolean isImprimiuDocumento() {
        return imprimiuDocumento;
    }

    public void setImprimiuDocumento(boolean imprimiuDocumento) {
        this.imprimiuDocumento = imprimiuDocumento;
    }
}
