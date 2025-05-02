package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.TipoDocumentoOficialPortal;

import java.io.Serializable;

public class DadosSolicitacaoCertidaoPortal implements Serializable {
    private TipoDocumentoOficialPortal tipoDocumentoOficialPortal;
    private TipoCadastro tipoCadastro;

    public DadosSolicitacaoCertidaoPortal(TipoDocumentoOficialPortal tipoDocumentoOficialPortal, TipoCadastro tipoCadastro) {
        this.tipoDocumentoOficialPortal = tipoDocumentoOficialPortal;
        this.tipoCadastro = tipoCadastro;
    }

    public TipoDocumentoOficialPortal getTipoDocumentoOficialPortal() {
        return tipoDocumentoOficialPortal;
    }

    public void setTipoDocumentoOficialPortal(TipoDocumentoOficialPortal tipoDocumentoOficialPortal) {
        this.tipoDocumentoOficialPortal = tipoDocumentoOficialPortal;
    }

    public TipoCadastro getTipoCadastro() {
        return tipoCadastro;
    }

    public void setTipoCadastro(TipoCadastro tipoCadastro) {
        this.tipoCadastro = tipoCadastro;
    }

    public boolean isPessoaFisica() {
        return TipoCadastro.PESSOA_FISICA.equals(tipoCadastro);
    }

    public boolean isPessoaJuridica() {
        return TipoCadastro.PESSOA_JURIDICA.equals(tipoCadastro);
    }

    public boolean isCadastroImobiliario() {
        return TipoCadastro.IMOBILIARIO.equals(tipoCadastro);
    }

    public enum TipoCadastro {
        PESSOA_FISICA,
        PESSOA_JURIDICA,
        IMOBILIARIO;

        TipoCadastro() {
        }
    }
}
