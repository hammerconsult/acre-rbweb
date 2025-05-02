package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.enums.SituacaoCalculo;
import br.com.webpublico.enums.SituacaoCalculoAlvara;
import br.com.webpublico.enums.TipoAlvara;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class VOAlvara implements Serializable {
    private Long id;
    private Long idAlvara;
    private Long idCadastro;
    private Integer ano;
    private Boolean emitir;
    private Boolean isPessoaJuridica;
    private Date dataCalculo;
    private Date vencimentoAlvara;
    private TipoVoAlvara tipoVoAlvara;
    private TipoAlvara tipoAlvara;
    private SituacaoCalculoAlvara situacaoCalculoAlvara;
    private Exercicio exercicio;
    private List<VOAlvaraItens> itens;
    private List<VOAlvaraCnaes> cnaes;
    private Boolean dispensaLicenciamento;

    public VOAlvara() {
        itens = Lists.newArrayList();
        cnaes = Lists.newArrayList();
        dispensaLicenciamento = Boolean.FALSE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdAlvara() {
        return idAlvara;
    }

    public void setIdAlvara(Long idAlvara) {
        this.idAlvara = idAlvara;
    }

    public Long getIdCadastro() {
        return idCadastro;
    }

    public void setIdCadastro(Long idCadastro) {
        this.idCadastro = idCadastro;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Boolean getEmitir() {
        return emitir;
    }

    public void setEmitir(Boolean emitir) {
        this.emitir = emitir;
    }

    public Boolean getPessoaJuridica() {
        return isPessoaJuridica;
    }

    public void setPessoaJuridica(Boolean pessoaJuridica) {
        isPessoaJuridica = pessoaJuridica;
    }

    public Date getDataCalculo() {
        return dataCalculo;
    }

    public void setDataCalculo(Date dataCalculo) {
        this.dataCalculo = dataCalculo;
    }

    public Date getVencimentoAlvara() {
        return vencimentoAlvara;
    }

    public void setVencimentoAlvara(Date vencimentoAlvara) {
        this.vencimentoAlvara = vencimentoAlvara;
    }

    public TipoVoAlvara getTipoVoAlvara() {
        return tipoVoAlvara;
    }

    public void setTipoVoAlvara(TipoVoAlvara tipoVoAlvara) {
        this.tipoVoAlvara = tipoVoAlvara;
    }

    public TipoAlvara getTipoAlvara() {
        return tipoAlvara;
    }

    public void setTipoAlvara(TipoAlvara tipoAlvara) {
        this.tipoAlvara = tipoAlvara;
    }

    public SituacaoCalculoAlvara getSituacaoCalculoAlvara() {
        return situacaoCalculoAlvara;
    }

    public void setSituacaoCalculoAlvara(SituacaoCalculoAlvara situacaoCalculoAlvara) {
        this.situacaoCalculoAlvara = situacaoCalculoAlvara;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public List<VOAlvaraItens> getItens() {
        return itens;
    }

    public void setItens(List<VOAlvaraItens> itens) {
        this.itens = itens;
    }

    public List<VOAlvaraCnaes> getCnaes() {
        return cnaes;
    }

    public void setCnaes(List<VOAlvaraCnaes> cnaes) {
        this.cnaes = cnaes;
    }

    public Boolean getDispensaLicenciamento() {
        return dispensaLicenciamento;
    }

    public void setDispensaLicenciamento(Boolean dispensaLicenciamento) {
        this.dispensaLicenciamento = dispensaLicenciamento;
    }

    public enum TipoVoAlvara {
        PROCESSO_CALCULO, CALCULO;

        TipoVoAlvara() {
        }
    }
}
