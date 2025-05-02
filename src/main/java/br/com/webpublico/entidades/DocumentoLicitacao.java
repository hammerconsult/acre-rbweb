package br.com.webpublico.entidades;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.ITipoDocumentoAnexo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.commons.io.FilenameUtils;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Audited
public class DocumentoLicitacao extends SuperEntidade implements ITipoDocumentoAnexo {

    @Transient
    private static final List<String> EXTENSOES_PERMITIDAS = Lists.newArrayList("pdf", "txt", "rtf", "doc", "docx", "xls", "xlsx", "odt",
        "ods", "sxw", "zip", "7z", "rar", "dwg", "dwt", "dxf", "dwf", "dwfx", "svg", "sldprt", "sldasm", "dgn", "ifc", "skp",
        "3ds", "dae", "obj", "rfa", "rte");

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private DetentorDocumentoLicitacao detentorDocumentoLicitacao;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @Etiqueta("Tipo de Documento Anexo")
    @Obrigatorio
    @ManyToOne
    private TipoDocumentoAnexo tipoDocumentoAnexo;
    @Etiqueta("Tipo de Registro")
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private TipoRegistro tipoRegistro;
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Arquivo arquivo;
    private String link;
    private String observacao;
    private Boolean exibirPortalTransparencia;
    private Boolean enviarPncc;
    @Etiqueta("Sequencial Criado pelo PNCP ao Realizar o Envio do Documento")
    @Tabelavel(campoSelecionado = false)
    private String sequencialDocumentoIdPncp;


    public DocumentoLicitacao() {
        super();
        exibirPortalTransparencia = Boolean.FALSE;
        enviarPncc = Boolean.FALSE;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DetentorDocumentoLicitacao getDetentorDocumentoLicitacao() {
        return detentorDocumentoLicitacao;
    }

    public void setDetentorDocumentoLicitacao(DetentorDocumentoLicitacao detentorDocumentoLicitacao) {
        this.detentorDocumentoLicitacao = detentorDocumentoLicitacao;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public TipoDocumentoAnexo getTipoDocumentoAnexo() {
        return tipoDocumentoAnexo;
    }

    public void setTipoDocumentoAnexo(TipoDocumentoAnexo tipoDocumentoAnexo) {
        this.tipoDocumentoAnexo = tipoDocumentoAnexo;
    }

    public TipoRegistro getTipoRegistro() {
        return tipoRegistro;
    }

    public void setTipoRegistro(TipoRegistro tipoRegistro) {
        this.tipoRegistro = tipoRegistro;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Boolean getExibirPortalTransparencia() {
        if (exibirPortalTransparencia == null) {
            exibirPortalTransparencia = Boolean.FALSE;
        }
        return exibirPortalTransparencia;
    }

    public void setExibirPortalTransparencia(Boolean exibirPortalTransparencia) {
        this.exibirPortalTransparencia = exibirPortalTransparencia;
    }

    public Boolean getEnviarPncc() {
        return enviarPncc;
    }

    public void setEnviarPncc(Boolean enviarPncc) {
        this.enviarPncc = enviarPncc;
    }

    public String getSequencialDocumentoIdPncp() {
        return sequencialDocumentoIdPncp;
    }

    public void setSequencialDocumentoIdPncp(String sequencialDocumentoIdPncp) {
        this.sequencialDocumentoIdPncp = sequencialDocumentoIdPncp;
    }

    @Override
    public void realizarValidacoes() {
        super.realizarValidacoes();
        ValidacaoException ve = new ValidacaoException();
        if (TipoRegistro.ARQUIVO.equals(tipoRegistro)) {
            if (arquivo == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O arquivo deve ser informado.");
            } else {
                if (enviarPncc) {
                    if (!EXTENSOES_PERMITIDAS.contains(FilenameUtils.getExtension(arquivo.getNome()))) {
                        ve.adicionarMensagemDeCampoObrigatorio("O envio para o PNCP não permite a extensão " + FilenameUtils.getExtension(arquivo.getNome()) + ". ");
                    }
                    if (arquivo.getTamanho() / 1024 / 1024 > 30) {
                        ve.adicionarMensagemDeCampoObrigatorio("O envio para o PNCP não permite arquivos com tamanho superior a 30Mb. ");
                    }
                }
            }
        }
        if (TipoRegistro.LINK.equals(tipoRegistro)) {
            if (enviarPncc) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O envio para o PNCP não permite Links. Por favor, selecione um arquivo.");
            } else if (Strings.isNullOrEmpty(link)) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Link deve ser informado.");
            }
        }
        ve.lancarException();
    }

    public enum TipoRegistro {
        ARQUIVO("Arquivo"),
        LINK("Link");

        private String descricao;

        TipoRegistro(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }
}

