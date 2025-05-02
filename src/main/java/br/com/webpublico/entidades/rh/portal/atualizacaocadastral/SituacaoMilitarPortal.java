package br.com.webpublico.entidades.rh.portal.atualizacaocadastral;

import br.com.webpublico.enums.CategoriaCertificadoMilitar;
import br.com.webpublico.enums.TipoSituacaoMilitar;
import br.com.webpublico.pessoa.dto.SituacaoMilitarDTO;
import br.com.webpublico.util.DataUtil;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Created by peixe on 29/08/17.
 */
@Entity
public class SituacaoMilitarPortal implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private Long id;
    @Enumerated(EnumType.STRING)
    private TipoSituacaoMilitar tipoSituacaoMilitar;
    private String certificadoMilitar;
    private String serieCertificadoMilitar;
    @Enumerated(EnumType.STRING)
    private CategoriaCertificadoMilitar categoriaCertificadoMilitar;
    private Date dataEmissao;
    private String orgaoEmissao;

    public SituacaoMilitarPortal() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoSituacaoMilitar getTipoSituacaoMilitar() {
        return tipoSituacaoMilitar;
    }

    public void setTipoSituacaoMilitar(TipoSituacaoMilitar tipoSituacaoMilitar) {
        this.tipoSituacaoMilitar = tipoSituacaoMilitar;
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

    public CategoriaCertificadoMilitar getCategoriaCertificadoMilitar() {
        return categoriaCertificadoMilitar;
    }

    public void setCategoriaCertificadoMilitar(CategoriaCertificadoMilitar categoriaCertificadoMilitar) {
        this.categoriaCertificadoMilitar = categoriaCertificadoMilitar;
    }

    public Date getDataEmissao() {
        return dataEmissao != null ? DataUtil.dataSemHorario(dataEmissao) : dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public String getOrgaoEmissao() {
        return orgaoEmissao;
    }

    public void setOrgaoEmissao(String orgaoEmissao) {
        this.orgaoEmissao = orgaoEmissao;
    }

    public static SituacaoMilitarPortal dtoToSituacaoMilitarPortal(SituacaoMilitarDTO dto) {
        SituacaoMilitarPortal situacao = new SituacaoMilitarPortal();
        situacao.setCategoriaCertificadoMilitar(dto.getCategoriaCertificadoMilitar() != null ? CategoriaCertificadoMilitar.valueOf(dto.getCategoriaCertificadoMilitar().name()) : null);
        situacao.setCertificadoMilitar(dto.getCertificadoMilitar());
        situacao.setDataEmissao(dto.getDataEmissaoMilitar());
        situacao.setOrgaoEmissao(dto.getOrgaoEmissaoMilitar());
        situacao.setSerieCertificadoMilitar(dto.getSerieCertificadoMilitar());
        situacao.setTipoSituacaoMilitar(dto.getTipoSituacaoMilitar() != null ? TipoSituacaoMilitar.valueOf(dto.getTipoSituacaoMilitar().name()) : null);
        return situacao;
    }

    public boolean isCamposPreenchidos() {
        if (this.categoriaCertificadoMilitar == null
            && this.certificadoMilitar == null
            && this.orgaoEmissao == null
            && this.serieCertificadoMilitar == null
            && this.tipoSituacaoMilitar == null) {
            return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SituacaoMilitarPortal that = (SituacaoMilitarPortal) o;
        return tipoSituacaoMilitar == that.tipoSituacaoMilitar
            && Objects.equals(certificadoMilitar != null ? certificadoMilitar.trim() : null, that.certificadoMilitar != null ? that.certificadoMilitar.trim() : null)
            && Objects.equals(serieCertificadoMilitar != null ? serieCertificadoMilitar.trim() : null, that.serieCertificadoMilitar != null ? that.serieCertificadoMilitar.trim() : null)
            && categoriaCertificadoMilitar == that.categoriaCertificadoMilitar
            && Objects.equals(DataUtil.getDataFormatada(dataEmissao), DataUtil.getDataFormatada(that.dataEmissao))
            && Objects.equals(orgaoEmissao != null ? orgaoEmissao.toUpperCase().trim() : null, that.orgaoEmissao != null ? that.orgaoEmissao.toUpperCase().trim() : null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tipoSituacaoMilitar, certificadoMilitar, serieCertificadoMilitar, categoriaCertificadoMilitar, dataEmissao, orgaoEmissao);
    }
}
