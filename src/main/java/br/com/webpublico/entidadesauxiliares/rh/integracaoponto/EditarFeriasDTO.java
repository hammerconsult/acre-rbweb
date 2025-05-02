package br.com.webpublico.entidadesauxiliares.rh.integracaoponto;

import br.com.webpublico.entidades.ConcessaoFeriasLicenca;
import br.com.webpublico.util.DataUtil;

public class EditarFeriasDTO {

    private String idRbweb;
    private String vinculoRbWeb;
    private String tipo;
    private String dataInicio;
    private String dataFim;

    public EditarFeriasDTO() {}

    public EditarFeriasDTO(ConcessaoFeriasLicenca concessaoFeriasLicenca) {
        this.idRbweb = concessaoFeriasLicenca.getId().toString();
        this.vinculoRbWeb = concessaoFeriasLicenca.getPeriodoAquisitivoFL().getContratoFP().getId().toString();
        this.tipo = "FERIAS";
        this.dataInicio = DataUtil.getDataFormatadaDiaHora(concessaoFeriasLicenca.getDataInicial(), "yyyy-MM-dd HH:mm:ss");
        this.dataFim = DataUtil.getDataFormatadaDiaHora(concessaoFeriasLicenca.getDataFinal(), "yyyy-MM-dd HH:mm:ss");
    }

    public String getIdRbweb() {
        return idRbweb;
    }

    public void setIdRbweb(String idRbweb) {
        this.idRbweb = idRbweb;
    }

    public String getVinculoRbWeb() {
        return vinculoRbWeb;
    }

    public void setVinculoRbWeb(String vinculoRbWeb) {
        this.vinculoRbWeb = vinculoRbWeb;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(String dataInicio) {
        this.dataInicio = dataInicio;
    }

    public String getDataFim() {
        return dataFim;
    }

    public void setDataFim(String dataFim) {
        this.dataFim = dataFim;
    }
}
