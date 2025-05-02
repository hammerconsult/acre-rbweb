package br.com.webpublico.entidadesauxiliares.rh.integracaoponto;

public class InformacoesAfastamentoFeriasDTO {
    private String servidorId;
    private String vinculoId;
    private String dataInicio;
    private String dataFim;
    private String tipo;
    private String idRbweb;

    public InformacoesAfastamentoFeriasDTO() {
    }

    public String getServidorId() {
        return servidorId;
    }

    public void setServidorId(String servidorId) {
        this.servidorId = servidorId;
    }

    public String getVinculoId() {
        return vinculoId;
    }

    public void setVinculoId(String vinculoId) {
        this.vinculoId = vinculoId;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getIdRbweb() {
        return idRbweb;
    }

    public void setIdRbweb(String idRbweb) {
        this.idRbweb = idRbweb;
    }
}
