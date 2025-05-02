package br.com.webpublico.entidadesauxiliares;


import br.com.webpublico.enums.TipoExclusaoContrato;

import java.util.Date;

public class ExclusaoContratoVO  {

    private Long id;
    private Long numero;
    private Date dataExclusao;
    private TipoExclusaoContrato tipoExclusao;
    private String historico;
    private String usuario;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Date getDataExclusao() {
        return dataExclusao;
    }

    public void setDataExclusao(Date dataExclusao) {
        this.dataExclusao = dataExclusao;
    }

    public TipoExclusaoContrato getTipoExclusao() {
        return tipoExclusao;
    }

    public void setTipoExclusao(TipoExclusaoContrato tipoExclusao) {
        this.tipoExclusao = tipoExclusao;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}
