package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;

public class VOVeiculo implements Serializable {
    private String marca;
    private String modelo;
    private String placa;

    public VOVeiculo() {
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }
}
