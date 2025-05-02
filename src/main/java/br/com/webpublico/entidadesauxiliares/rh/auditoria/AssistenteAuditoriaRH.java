package br.com.webpublico.entidadesauxiliares.rh.auditoria;

import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.enums.TipoRevisaoAuditoria;
import br.com.webpublico.enums.rh.TipoAuditoriaRH;

import java.util.Date;

public class AssistenteAuditoriaRH {
    private TipoAuditoriaRH tipoAuditoriaRH;
    private VinculoFP vinculoFP;
    private TipoRevisaoAuditoria tipoRevisao;
    private UsuarioSistema usuarioSistema;
    private Date dataInicial;
    private Date dataFinal;
    private Long idAuditoria;

    public AssistenteAuditoriaRH() {
    }

    public TipoAuditoriaRH getTipoAuditoriaRH() {
        return tipoAuditoriaRH;
    }

    public void setTipoAuditoriaRH(TipoAuditoriaRH tipoAuditoriaRH) {
        this.tipoAuditoriaRH = tipoAuditoriaRH;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public TipoRevisaoAuditoria getTipoRevisao() {
        return tipoRevisao;
    }

    public void setTipoRevisao(TipoRevisaoAuditoria tipoRevisao) {
        this.tipoRevisao = tipoRevisao;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Long getIdAuditoria() {
        return idAuditoria;
    }

    public void setIdAuditoria(Long idAuditoria) {
        this.idAuditoria = idAuditoria;
    }
}
