package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.nfse.domain.dtos.NotaFiscalSearchDTO;
import br.com.webpublico.nfse.enums.MotivoCancelamentoNota;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Audited
public class LoteCancelamentoDeclaracaoPrestacaoServico extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Data Solicitação")
    private Date dataCancelamento;


    @Obrigatorio
    @Etiqueta("Motivo")
    @Enumerated(EnumType.STRING)
    private MotivoCancelamentoNota motivoCancelamento;

    @ManyToOne
    private UsuarioSistema fiscalResponsavel;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo Documento")
    private CancelamentoDeclaracaoPrestacaoServico.TipoDocumento tipoDocumento;

    @Etiqueta("Observação")
    private String observacoesCancelamento;

    @Etiqueta("Observação do Fiscal")
    private String observacoesFiscal;

    @Transient
    private List<NotaFiscalSearchDTO> notasFiscais;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataCancelamento() {
        return dataCancelamento;
    }

    public void setDataCancelamento(Date dataCancelamento) {
        this.dataCancelamento = dataCancelamento;
    }

    public MotivoCancelamentoNota getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(MotivoCancelamentoNota motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }

    public UsuarioSistema getFiscalResponsavel() {
        return fiscalResponsavel;
    }

    public void setFiscalResponsavel(UsuarioSistema fiscalResponsavel) {
        this.fiscalResponsavel = fiscalResponsavel;
    }

    public CancelamentoDeclaracaoPrestacaoServico.TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(CancelamentoDeclaracaoPrestacaoServico.TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getObservacoesCancelamento() {
        return observacoesCancelamento;
    }

    public void setObservacoesCancelamento(String observacoesCancelamento) {
        this.observacoesCancelamento = observacoesCancelamento;
    }

    public String getObservacoesFiscal() {
        return observacoesFiscal;
    }

    public void setObservacoesFiscal(String observacoesFiscal) {
        this.observacoesFiscal = observacoesFiscal;
    }

    public List<NotaFiscalSearchDTO> getNotasFiscais() {
        return notasFiscais;
    }

    public void setNotasFiscais(List<NotaFiscalSearchDTO> notasFiscais) {
        this.notasFiscais = notasFiscais;
    }

    @Override
    public void realizarValidacoes() {
        super.realizarValidacoes();
        if (notasFiscais == null || notasFiscais.isEmpty()) {
            throw new ValidacaoException("Nenhuma nota fiscal informada para cancelamento.");
        }
    }
}
