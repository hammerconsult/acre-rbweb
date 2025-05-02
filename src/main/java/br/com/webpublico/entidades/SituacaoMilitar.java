/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.rh.portal.atualizacaocadastral.SituacaoMilitarPortal;
import br.com.webpublico.enums.CategoriaCertificadoMilitar;
import br.com.webpublico.enums.TipoSituacaoMilitar;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.pessoa.dto.SituacaoMilitarDTO;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * @author Munif
 */
@GrupoDiagrama(nome = "CadastroUnico")
@Entity

@Audited
public class SituacaoMilitar extends DocumentoPessoal {

    @Tabelavel
    @Enumerated(EnumType.STRING)
    private TipoSituacaoMilitar tipoSituacaoMilitar;
    @Tabelavel
    @Column(length = 20)
    @Size(max = 20)
    private String certificadoMilitar;
    @Tabelavel
    @Column(length = 20)
    @Size(max = 20)
    private String serieCertificadoMilitar;
    @Tabelavel
    @Enumerated(EnumType.STRING)
    private CategoriaCertificadoMilitar categoriaCertificadoMilitar;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Etiqueta("Data de emissão")
    private Date dataEmissao;
    @Tabelavel
    @Etiqueta("Orgão emissor")
    @Size(max = 20)
    private String orgaoEmissao;

    public CategoriaCertificadoMilitar getCategoriaCertificadoMilitar() {
        return categoriaCertificadoMilitar;
    }

    public void setCategoriaCertificadoMilitar(CategoriaCertificadoMilitar categoriaCertificadoMilitar) {
        this.categoriaCertificadoMilitar = categoriaCertificadoMilitar;
    }

    public String getCertificadoMilitar() {
        return certificadoMilitar;
    }

    public void setCertificadoMilitar(String certificadoMilitar) {
        this.certificadoMilitar = certificadoMilitar;
    }

    public String getSerieCertificadoMilitar() {
        return serieCertificadoMilitar;
    }

    public void setSerieCertificadoMilitar(String serieCertificadoMilitar) {
        this.serieCertificadoMilitar = serieCertificadoMilitar;
    }

    public TipoSituacaoMilitar getTipoSituacaoMilitar() {
        return tipoSituacaoMilitar;
    }

    public void setTipoSituacaoMilitar(TipoSituacaoMilitar tipoSituacaoMilitar) {
        this.tipoSituacaoMilitar = tipoSituacaoMilitar;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public String getOrgaoEmissao() {
        if (orgaoEmissao != null) {
            return orgaoEmissao.toUpperCase();
        }
        return orgaoEmissao;
    }

    public void setOrgaoEmissao(String orgaoEmissao) {
        this.orgaoEmissao = orgaoEmissao != null ? orgaoEmissao.toUpperCase() : null;
    }

    @Override
    public String toString() {
        String certificado = "";
        String serie = "";
        String categoria = "";
        String tipo = "";
        if (certificadoMilitar != null) {
            certificado = certificadoMilitar;
        }
        if (serieCertificadoMilitar != null) {
            serie = serieCertificadoMilitar;
        }
        if (categoriaCertificadoMilitar != null) {
            categoria = categoriaCertificadoMilitar.getDescricao();
        }
        if (tipoSituacaoMilitar != null) {
            tipo = tipoSituacaoMilitar.getDescricao();
        }
        return certificado + "  " + serie + "  " + categoria + "  " + tipo;
    }

    public static SituacaoMilitarDTO toSituacaoMilitarDTO(SituacaoMilitar situacaoMilitar) {
        if (situacaoMilitar == null) {
            return null;
        }
        SituacaoMilitarDTO dto = new SituacaoMilitarDTO();
        dto.setCategoriaCertificadoMilitar(situacaoMilitar.getCategoriaCertificadoMilitar() != null ? br.com.webpublico.pessoa.enumeration.CategoriaCertificadoMilitar.valueOf(situacaoMilitar.getCategoriaCertificadoMilitar().name()) : null);
        dto.setCertificadoMilitar(situacaoMilitar.getCertificadoMilitar());
        dto.setDataEmissaoMilitar(situacaoMilitar.getDataEmissao());
        dto.setId(situacaoMilitar.getId());
        dto.setOrgaoEmissaoMilitar(situacaoMilitar.getOrgaoEmissao());
        dto.setSerieCertificadoMilitar(situacaoMilitar.getSerieCertificadoMilitar());
        dto.setTipoSituacaoMilitar(situacaoMilitar.getTipoSituacaoMilitar() != null ? br.com.webpublico.pessoa.enumeration.TipoSituacaoMilitar.valueOf(situacaoMilitar.getTipoSituacaoMilitar().name()) : null);
        return dto;
    }

    public static SituacaoMilitarDTO toSituacaoMilitarPortalDTO(SituacaoMilitarPortal situacaoMilitar) {
        if (situacaoMilitar == null) {
            return null;
        }
        SituacaoMilitarDTO dto = new SituacaoMilitarDTO();
        dto.setCategoriaCertificadoMilitar(situacaoMilitar.getCategoriaCertificadoMilitar() != null ? br.com.webpublico.pessoa.enumeration.CategoriaCertificadoMilitar.valueOf(situacaoMilitar.getCategoriaCertificadoMilitar().name()) : null);
        dto.setCertificadoMilitar(situacaoMilitar.getCertificadoMilitar());
        dto.setDataEmissaoMilitar(situacaoMilitar.getDataEmissao());
        dto.setId(situacaoMilitar.getId());
        dto.setOrgaoEmissaoMilitar(situacaoMilitar.getOrgaoEmissao());
        dto.setSerieCertificadoMilitar(situacaoMilitar.getSerieCertificadoMilitar());
        dto.setTipoSituacaoMilitar(situacaoMilitar.getTipoSituacaoMilitar() != null ? br.com.webpublico.pessoa.enumeration.TipoSituacaoMilitar.valueOf(situacaoMilitar.getTipoSituacaoMilitar().name()) : null);
        return dto;
    }

    public static SituacaoMilitar toSituacaoMilitar(PessoaFisica pessoaFisica, SituacaoMilitarPortal situacaoMilitar) {
        if (situacaoMilitar == null) {
            return null;
        }
        SituacaoMilitar sit = new SituacaoMilitar();
        sit.setCategoriaCertificadoMilitar(situacaoMilitar.getCategoriaCertificadoMilitar());
        sit.setCertificadoMilitar(situacaoMilitar.getCertificadoMilitar());
        sit.setDataEmissao(situacaoMilitar.getDataEmissao());
        sit.setPessoaFisica(pessoaFisica);
        sit.setOrgaoEmissao(situacaoMilitar.getOrgaoEmissao());
        sit.setSerieCertificadoMilitar(situacaoMilitar.getSerieCertificadoMilitar());
        sit.setTipoSituacaoMilitar(situacaoMilitar.getTipoSituacaoMilitar());
        sit.setDataRegistro(new Date());
        return sit;
    }
}
