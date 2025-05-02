package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.nfse.domain.dtos.LoteImportacaoDocumentoRecebidoNfseDTO;
import br.com.webpublico.nfse.domain.dtos.NfseEntity;
import br.com.webpublico.nfse.enums.SituacaoLoteDocumentoRecebido;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;


@Entity
@Audited
@Etiqueta("Lote de Documentos Recebidos")
public class LoteDocumentoRecebido extends SuperEntidade implements NfseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CadastroEconomico prestador;
    @Enumerated(EnumType.STRING)
    private SituacaoLoteDocumentoRecebido situacao;
    private String xml;
    private String log;
    private String numero;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "lote")
    private List<ServicoDeclarado> documentos;

    public LoteDocumentoRecebido() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CadastroEconomico getPrestador() {
        return prestador;
    }

    public void setPrestador(CadastroEconomico prestador) {
        this.prestador = prestador;
    }

    public SituacaoLoteDocumentoRecebido getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoLoteDocumentoRecebido situacao) {
        this.situacao = situacao;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public List<ServicoDeclarado> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<ServicoDeclarado> documentos) {
        this.documentos = documentos;
    }

    @Override
    public LoteImportacaoDocumentoRecebidoNfseDTO toNfseDto() {
        LoteImportacaoDocumentoRecebidoNfseDTO dto = new LoteImportacaoDocumentoRecebidoNfseDTO();
        dto.setId(this.id);
        dto.setLog(this.log);
        dto.setSituacao(this.situacao.toNfseDto());
        dto.setPrestador(this.prestador.toSimpleNfseDto());
        dto.setNumero(this.numero);
        return dto;
    }

    public void addLog(String s) {
        if (log == null) {
            log = "";
        }
        log += s + "</br>";
    }
}
