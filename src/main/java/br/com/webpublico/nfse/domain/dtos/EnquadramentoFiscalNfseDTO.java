package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.nfse.domain.dtos.enums.*;
import br.com.webpublico.tributario.enumeration.RegimeTributarioNfseDTO;
import br.com.webpublico.webreportdto.dto.tributario.TipoIssqnNfseDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by rodolfo on 27/10/17.
 * Created by rodolfo on 27/10/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class EnquadramentoFiscalNfseDTO {
    private Long id;
    private TipoPorteNfeDTO porte;
    private TipoContribuinteNfseDTO tipoContribuinte;
    private TipoIssqnNfseDTO tipoIssqn;
    private RegimeTributarioNfseDTO regimeTributario;
    private ClassificacaoAtividadeNfseDTO classificacaoAtividade;
    private TipoNotaFiscalServicoNfseDTO tipoNotaFiscal;
    private Boolean substitutoTributario;
    private Boolean emitenteNfse;
    private Boolean ativo;
    private Boolean simplesNacional;
    private AnexoLei1232006NfseDTO anexoLei1232006;
    private RegimeEspecialTributacaoNfseDTO regimeEspecialTributacao;
    private Boolean instituicaoFinanceira;

    public EnquadramentoFiscalNfseDTO() {

    }

    public TipoPorteNfeDTO getPorte() {
        return porte;
    }

    public void setPorte(TipoPorteNfeDTO porte) {
        this.porte = porte;
    }

    public TipoContribuinteNfseDTO getTipoContribuinte() {
        return tipoContribuinte;
    }

    public void setTipoContribuinte(TipoContribuinteNfseDTO tipoContribuinte) {
        this.tipoContribuinte = tipoContribuinte;
    }

    public RegimeTributarioNfseDTO getRegimeTributario() {
        return regimeTributario;
    }

    public void setRegimeTributario(RegimeTributarioNfseDTO regimeTributario) {
        this.regimeTributario = regimeTributario;
    }

    public ClassificacaoAtividadeNfseDTO getClassificacaoAtividade() {
        return classificacaoAtividade;
    }

    public void setClassificacaoAtividade(ClassificacaoAtividadeNfseDTO classificacaoAtividade) {
        this.classificacaoAtividade = classificacaoAtividade;
    }

    public TipoNotaFiscalServicoNfseDTO getTipoNotaFiscal() {
        return tipoNotaFiscal;
    }

    public void setTipoNotaFiscal(TipoNotaFiscalServicoNfseDTO tipoNotaFiscal) {
        this.tipoNotaFiscal = tipoNotaFiscal;
    }

    public Boolean getSubstitutoTributario() {
        return substitutoTributario;
    }

    public void setSubstitutoTributario(Boolean substitutoTributario) {
        this.substitutoTributario = substitutoTributario;
    }

    public Boolean getSimplesNacional() {
        return simplesNacional;
    }

    public void setSimplesNacional(Boolean simplesNacional) {
        this.simplesNacional = simplesNacional;
    }

    public AnexoLei1232006NfseDTO getAnexoLei1232006() {
        return anexoLei1232006;
    }

    public void setAnexoLei1232006(AnexoLei1232006NfseDTO anexoLei1232006) {
        this.anexoLei1232006 = anexoLei1232006;
    }

    public TipoIssqnNfseDTO getTipoIssqn() {
        return tipoIssqn;
    }

    public void setTipoIssqn(TipoIssqnNfseDTO tipoIssqn) {
        this.tipoIssqn = tipoIssqn;
    }

    public Boolean getEmitenteNfse() {
        return emitenteNfse;
    }

    public void setEmitenteNfse(Boolean emitenteNfse) {
        this.emitenteNfse = emitenteNfse;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RegimeEspecialTributacaoNfseDTO getRegimeEspecialTributacao() {
        return regimeEspecialTributacao;
    }

    public void setRegimeEspecialTributacao(RegimeEspecialTributacaoNfseDTO regimeEspecialTributacao) {
        this.regimeEspecialTributacao = regimeEspecialTributacao;
    }

    public Boolean getInstituicaoFinanceira() {
        return instituicaoFinanceira;
    }

    public void setInstituicaoFinanceira(Boolean instituicaoFinanceira) {
        this.instituicaoFinanceira = instituicaoFinanceira;
    }

    @JsonIgnore
    public boolean getSuperSimples() {
        return TipoIssqnNfseDTO.SIMPLES_NACIONAL.equals(getTipoIssqn());
    }

    @JsonIgnore
    public boolean getMei() {
        return TipoIssqnNfseDTO.MEI.equals(getTipoIssqn());
    }
}
