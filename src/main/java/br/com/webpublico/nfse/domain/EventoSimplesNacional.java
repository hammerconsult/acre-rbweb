package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.DetentorArquivoComposicao;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.nfse.domain.dtos.EventoSimplesNacionalNfseDTO;
import br.com.webpublico.nfse.domain.dtos.NfseEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
public class EventoSimplesNacional extends SuperEntidade implements PossuidorArquivo, NfseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date dataImportacao;

    @ManyToOne
    private UsuarioSistema usuarioImportacao;

    @OneToOne(cascade = CascadeType.ALL)
    private DetentorArquivoComposicao arquivoImportacao;

    public EventoSimplesNacional() {
        super();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataImportacao() {
        return dataImportacao;
    }

    public void setDataImportacao(Date dataImportacao) {
        this.dataImportacao = dataImportacao;
    }

    public UsuarioSistema getUsuarioImportacao() {
        return usuarioImportacao;
    }

    public void setUsuarioImportacao(UsuarioSistema usuarioImportacao) {
        this.usuarioImportacao = usuarioImportacao;
    }

    public DetentorArquivoComposicao getArquivoImportacao() {
        return arquivoImportacao;
    }

    public void setArquivoImportacao(DetentorArquivoComposicao arquivoImportacao) {
        this.arquivoImportacao = arquivoImportacao;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return arquivoImportacao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.arquivoImportacao = detentorArquivoComposicao;
    }


    @Override
    public EventoSimplesNacionalNfseDTO toNfseDto() {
        EventoSimplesNacionalNfseDTO eventoSimplesNacionalNfseDTO = new EventoSimplesNacionalNfseDTO();
        eventoSimplesNacionalNfseDTO.setDataImportacao(dataImportacao);
        eventoSimplesNacionalNfseDTO.setUsuarioImportacao(usuarioImportacao.getLogin());
        return eventoSimplesNacionalNfseDTO;
    }
}
