package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.nfse.domain.dtos.enums.ConstrucaoCivilStatusNfseDTO;

import java.util.Date;
import java.util.List;

public class ConstrucaoCivilNfseDTO implements NfseDTO {

    private Long id;
    private PrestadorServicoNfseDTO prestador;
    private Long codigoObra;
    private PessoaNfseDTO responsavel;
    private ConstrucaoCivilLocalizacaoNfseDTO localizacao;
    private Date dataAprovacaoProjeto;
    private Date dataInicio;
    private Date dataConclusao;
    private String art;
    private String numeroAlvara;
    private Boolean incorporacao;
    private String matriculaImovel;
    private String numeroHabitese;
    private CadastroImobiliarioNfseDTO imovel;
    private ConstrucaoCivilStatusNfseDTO status;
    private List<ConstrucaoCivilProfissionalNfseDTO> profissionais;

    public ConstrucaoCivilNfseDTO() {
    }

    public ConstrucaoCivilNfseDTO(Long id,
                                  PrestadorServicoNfseDTO prestador,
                                  PessoaNfseDTO responsavel,
                                  ConstrucaoCivilLocalizacaoNfseDTO localizacao,
                                  Long codigoObra,
                                  Date dataAprovacaoProjeto,
                                  Date dataInicio,
                                  Date dataConclusao,
                                  String art,
                                  String numeroAlvara,
                                  Boolean incorporacao,
                                  String matriculaImovel,
                                  String numeroHabitese,
                                  CadastroImobiliarioNfseDTO imovel,
                                  ConstrucaoCivilStatusNfseDTO status,
                                  List<ConstrucaoCivilProfissionalNfseDTO> profissionais) {
        this.id = id;
        this.prestador = prestador;
        this.responsavel = responsavel;
        this.localizacao = localizacao;
        this.codigoObra = codigoObra;
        this.dataAprovacaoProjeto = dataAprovacaoProjeto;
        this.dataInicio = dataInicio;
        this.dataConclusao = dataConclusao;
        this.art = art;
        this.numeroAlvara = numeroAlvara;
        this.incorporacao = incorporacao;
        this.matriculaImovel = matriculaImovel;
        this.numeroHabitese = numeroHabitese;
        this.imovel = imovel;
        this.status = status;
        this.profissionais = profissionais;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PrestadorServicoNfseDTO getPrestador() {
        return prestador;
    }

    public void setPrestador(PrestadorServicoNfseDTO prestador) {
        this.prestador = prestador;
    }

    public Long getCodigoObra() {
        return codigoObra;
    }

    public void setCodigoObra(Long codigoObra) {
        this.codigoObra = codigoObra;
    }

    public PessoaNfseDTO getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(PessoaNfseDTO responsavel) {
        this.responsavel = responsavel;
    }

    public ConstrucaoCivilLocalizacaoNfseDTO getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(ConstrucaoCivilLocalizacaoNfseDTO localizacao) {
        this.localizacao = localizacao;
    }

    public Date getDataAprovacaoProjeto() {
        return dataAprovacaoProjeto;
    }

    public void setDataAprovacaoProjeto(Date dataAprovacaoProjeto) {
        this.dataAprovacaoProjeto = dataAprovacaoProjeto;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataConclusao() {
        return dataConclusao;
    }

    public void setDataConclusao(Date dataConclusao) {
        this.dataConclusao = dataConclusao;
    }

    public String getArt() {
        return art;
    }

    public void setArt(String art) {
        this.art = art;
    }

    public String getNumeroAlvara() {
        return numeroAlvara;
    }

    public void setNumeroAlvara(String numeroAlvara) {
        this.numeroAlvara = numeroAlvara;
    }

    public Boolean getIncorporacao() {
        return incorporacao;
    }

    public void setIncorporacao(Boolean incorporacao) {
        this.incorporacao = incorporacao;
    }

    public String getMatriculaImovel() {
        return matriculaImovel;
    }

    public void setMatriculaImovel(String matriculaImovel) {
        this.matriculaImovel = matriculaImovel;
    }

    public String getNumeroHabitese() {
        return numeroHabitese;
    }

    public void setNumeroHabitese(String numeroHabitese) {
        this.numeroHabitese = numeroHabitese;
    }

    public CadastroImobiliarioNfseDTO getImovel() {
        return imovel;
    }

    public void setImovel(CadastroImobiliarioNfseDTO imovel) {
        this.imovel = imovel;
    }

    public ConstrucaoCivilStatusNfseDTO getStatus() {
        return status;
    }

    public void setStatus(ConstrucaoCivilStatusNfseDTO status) {
        this.status = status;
    }

    public List<ConstrucaoCivilProfissionalNfseDTO> getProfissionais() {
        return profissionais;
    }

    public void setProfissionais(List<ConstrucaoCivilProfissionalNfseDTO> profissionais) {
        this.profissionais = profissionais;
    }
}
