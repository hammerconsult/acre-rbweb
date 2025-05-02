package br.com.webpublico.entidadesauxiliares.rh.esocial;

import br.com.webpublico.enums.rh.esocial.TipoOperacaoESocial;
import br.com.webpublico.interfaces.IHistoricoEsocial;

import java.text.SimpleDateFormat;
import java.util.Date;

public class VWContratoFP implements IHistoricoEsocial {
    private Long id;
    private String matricula;
    private String nome;
    private Date inicioVigencia;
    private TipoOperacaoESocial tipoOperacaoESocial;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public TipoOperacaoESocial getTipoOperacaoESocial() {
        return tipoOperacaoESocial;
    }

    public void setTipoOperacaoESocial(TipoOperacaoESocial tipoOperacaoESocial) {
        this.tipoOperacaoESocial = tipoOperacaoESocial;
    }

    @Override
    public String getDescricaoCompleta() {
        return this.matricula + "/" + this.nome;
    }

    @Override
    public String getIdentificador() {
        return this.getId().toString();
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public String getInicioVigenciaFormatado() {
        if (inicioVigencia != null) {
            SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
            return sf.format(inicioVigencia);
        }
        return "";
    }

    @Override
    public String toString() {
        return this.matricula + " - " + this.nome;
    }
}
