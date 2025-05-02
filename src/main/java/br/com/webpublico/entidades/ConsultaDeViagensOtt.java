package br.com.webpublico.entidades;

import java.io.Serializable;
import java.util.Date;

/**
 * @author octavio
 */
public class ConsultaDeViagensOtt implements Serializable {
    private Long id;
    private OperadoraTecnologiaTransporte operadoraTecTransporte;
    private Date dataInicialViagem;
    private Date dataFinalViagem;

    public ConsultaDeViagensOtt() {
    }

    public OperadoraTecnologiaTransporte getOperadoraTecTransporte() {
        return operadoraTecTransporte;
    }

    public void setOperadoraTecTransporte(OperadoraTecnologiaTransporte operadoraTecTransporte) {
        this.operadoraTecTransporte = operadoraTecTransporte;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataInicialViagem() {
        return dataInicialViagem;
    }

    public void setDataInicialViagem(Date dataInicialViagem) {
        this.dataInicialViagem = dataInicialViagem;
    }

    public Date getDataFinalViagem() {
        return dataFinalViagem;
    }

    public void setDataFinalViagem(Date dataFinalViagem) {
        this.dataFinalViagem = dataFinalViagem;
    }
}
