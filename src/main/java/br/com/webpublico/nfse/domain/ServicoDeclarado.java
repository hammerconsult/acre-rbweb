package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.nfse.enums.TipoServicoDeclarado;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
public class ServicoDeclarado extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoServicoDeclarado tipoServicoDeclarado;

    @Tabelavel
    @Pesquisavel
    @ManyToOne
    private TipoDocumentoServicoDeclarado tipoDocServicoDeclarado;

    @Tabelavel
    @Pesquisavel
    private Long numero;

    @Temporal(TemporalType.DATE)
    private Date emissao;

    @ManyToOne
    private CadastroEconomico cadastroEconomico;

    @Tabelavel
    @Pesquisavel
    @ManyToOne(cascade = CascadeType.ALL)
    private DeclaracaoPrestacaoServico declaracaoPrestacaoServico;

    @ManyToOne
    private LoteDocumentoRecebido lote;


    public ServicoDeclarado() {
        emissao = new Date();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoServicoDeclarado getTipoServicoDeclarado() {
        return tipoServicoDeclarado;
    }

    public void setTipoServicoDeclarado(TipoServicoDeclarado tipoServicoDeclarado) {
        this.tipoServicoDeclarado = tipoServicoDeclarado;
    }

    public TipoDocumentoServicoDeclarado getTipoDocServicoDeclarado() {
        return tipoDocServicoDeclarado;
    }

    public void setTipoDocServicoDeclarado(TipoDocumentoServicoDeclarado tipoDocServicoDeclarado) {
        this.tipoDocServicoDeclarado = tipoDocServicoDeclarado;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Date getEmissao() {
        return emissao;
    }

    public void setEmissao(Date emissao) {
        this.emissao = emissao;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public DeclaracaoPrestacaoServico getDeclaracaoPrestacaoServico() {
        return declaracaoPrestacaoServico;
    }

    public void setDeclaracaoPrestacaoServico(DeclaracaoPrestacaoServico declaracaoPrestacaoServico) {
        this.declaracaoPrestacaoServico = declaracaoPrestacaoServico;
    }

    public LoteDocumentoRecebido getLote() {
        return lote;
    }

    public void setLote(LoteDocumentoRecebido lote) {
        this.lote = lote;
    }

    @Override
    public String toString() {
        return tipoDocServicoDeclarado.getDescricao() +
            " Nº. " + numero;
    }

}
