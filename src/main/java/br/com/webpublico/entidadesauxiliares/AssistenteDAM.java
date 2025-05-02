package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.HistoricoImpressaoDAM;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AssistenteDAM implements Serializable {

    private Long idParcela;
    private List<Long> idsParcela;
    private Long idDam;
    private List<Long> idsDam;
    private String usuario;
    private HistoricoImpressaoDAM.TipoImpressao tipoImpressaoDAM;
    private Date vencimentoDAM;
    private Long idPessoa;
    private Long idSubvencaoParcela;
    private String nomeArquivoJasper;
    private HashMap<String, Object> parameters;
    private List<Long> idsSubvencaoParcela;
    private Long idValorDivida;
    private Boolean validarParcelaEmAberto;

    public AssistenteDAM() {
        this.validarParcelaEmAberto = Boolean.TRUE;
    }

    public Long getIdParcela() {
        return idParcela;
    }

    public void setIdParcela(Long idParcela) {
        this.idParcela = idParcela;
    }

    public List<Long> getIdsParcela() {
        return idsParcela;
    }

    public void setIdsParcela(List<Long> idsParcela) {
        this.idsParcela = idsParcela;
    }

    public List<Long> getIdsDam() {
        return idsDam;
    }

    public void setIdsDam(List<Long> idsDam) {
        this.idsDam = idsDam;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public HistoricoImpressaoDAM.TipoImpressao getTipoImpressaoDAM() {
        return tipoImpressaoDAM;
    }

    public void setTipoImpressaoDAM(HistoricoImpressaoDAM.TipoImpressao tipoImpressaoDAM) {
        this.tipoImpressaoDAM = tipoImpressaoDAM;
    }

    public String getNomeArquivoJasper() {
        return nomeArquivoJasper;
    }

    public void setNomeArquivoJasper(String nomeArquivoJasper) {
        this.nomeArquivoJasper = nomeArquivoJasper;
    }

    public HashMap<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(HashMap<String, Object> parameters) {
        this.parameters = parameters;
    }

    public Date getVencimentoDAM() {
        return vencimentoDAM;
    }

    public void setVencimentoDAM(Date vencimentoDAM) {
        this.vencimentoDAM = vencimentoDAM;
    }

    public Long getIdDam() {
        return idDam;
    }

    public void setIdDam(Long idDam) {
        this.idDam = idDam;
    }

    public Long getIdPessoa() {
        return idPessoa;
    }

    public void setIdPessoa(Long idPessoa) {
        this.idPessoa = idPessoa;
    }

    public Long getIdSubvencaoParcela() {
        return idSubvencaoParcela;
    }

    public void setIdSubvencaoParcela(Long idSubvencaoParcela) {
        this.idSubvencaoParcela = idSubvencaoParcela;
    }

    public List<Long> getIdsSubvencaoParcela() {
        return idsSubvencaoParcela;
    }

    public void setIdsSubvencaoParcela(List<Long> idsSubvencaoParcela) {
        this.idsSubvencaoParcela = idsSubvencaoParcela;
    }

    public Long getIdValorDivida() {
        return idValorDivida;
    }

    public void setIdValorDivida(Long idValorDivida) {
        this.idValorDivida = idValorDivida;
    }

    public Boolean getValidarParcelaEmAberto() {
        return validarParcelaEmAberto;
    }

    public void setValidarParcelaEmAberto(Boolean validarParcelaEmAberto) {
        this.validarParcelaEmAberto = validarParcelaEmAberto;
    }
}
