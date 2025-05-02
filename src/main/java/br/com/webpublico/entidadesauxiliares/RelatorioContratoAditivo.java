package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.webreportdto.dto.administrativo.RelatorioContratoAditivoDTO;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

public class RelatorioContratoAditivo {

    private Long idAditivo;
    private String aditivo;
    private String tipo;
    private Date dataLancamento;

    public RelatorioContratoAditivo() {
    }

    public static List<RelatorioContratoAditivoDTO> aditivosToDto (List<RelatorioContratoAditivo> aditivos) {
        List<RelatorioContratoAditivoDTO> retorno = Lists.newArrayList();
        for (RelatorioContratoAditivo aditivo : aditivos) {
            retorno.add(aditivoToDto(aditivo));
        }
        return retorno;
    }

    public static RelatorioContratoAditivoDTO aditivoToDto (RelatorioContratoAditivo aditivo) {
        RelatorioContratoAditivoDTO retorno = new RelatorioContratoAditivoDTO();
        retorno.setIdAditivo(aditivo.getIdAditivo());
        retorno.setAditivo(aditivo.getAditivo());
        retorno.setTipo(aditivo.getTipo());
        retorno.setDataLancamento(aditivo.getDataLancamento());
        return retorno;
    }

    public String getAditivo() {
        return aditivo;
    }

    public Long getIdAditivo() {
        return idAditivo;
    }

    public void setIdAditivo(Long idAditivo) {
        this.idAditivo = idAditivo;
    }

    public void setAditivo(String aditivo) {
        this.aditivo = aditivo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }
}
