package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Bairro;
import br.com.webpublico.entidades.Logradouro;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.enums.TipoImovel;
import com.google.common.collect.Lists;

import java.util.List;

public class FiltroMalaDiretaGeralImovel {

    private String inscricaoIncial;
    private String inscricaoFinal;
    private String setorInicial;
    private String setorFinal;
    private String quadraInicial;
    private String quadraFinal;
    private String loteInicial;
    private String loteFinal;
    private Bairro bairro;
    private List<Bairro> bairros;
    private Logradouro logradouro;
    private List<Logradouro> logradouros;
    private TipoImovel tipoImovel;
    private Boolean ativo;
    private Pessoa proprietario;
    private List<Pessoa> proprietarios;
    private Pessoa compromissario;
    private List<Pessoa> compromissarios;

    public FiltroMalaDiretaGeralImovel() {
        bairros = Lists.newArrayList();
        logradouros = Lists.newArrayList();
        proprietarios = Lists.newArrayList();
        compromissarios = Lists.newArrayList();
        ativo = true;
    }


    public String getInscricaoIncial() {
        return inscricaoIncial;
    }

    public void setInscricaoIncial(String inscricaoIncial) {
        this.inscricaoIncial = inscricaoIncial;
    }

    public String getInscricaoFinal() {
        return inscricaoFinal;
    }

    public void setInscricaoFinal(String inscricaoFinal) {
        this.inscricaoFinal = inscricaoFinal;
    }

    public String getSetorInicial() {
        return setorInicial;
    }

    public void setSetorInicial(String setorInicial) {
        this.setorInicial = setorInicial;
    }

    public String getSetorFinal() {
        return setorFinal;
    }

    public void setSetorFinal(String setorFinal) {
        this.setorFinal = setorFinal;
    }

    public String getQuadraInicial() {
        return quadraInicial;
    }

    public void setQuadraInicial(String quadraInicial) {
        this.quadraInicial = quadraInicial;
    }

    public String getQuadraFinal() {
        return quadraFinal;
    }

    public void setQuadraFinal(String quadraFinal) {
        this.quadraFinal = quadraFinal;
    }

    public String getLoteInicial() {
        return loteInicial;
    }

    public void setLoteInicial(String loteInicial) {
        this.loteInicial = loteInicial;
    }

    public String getLoteFinal() {
        return loteFinal;
    }

    public void setLoteFinal(String loteFinal) {
        this.loteFinal = loteFinal;
    }

    public TipoImovel getTipoImovel() {
        return tipoImovel;
    }

    public void setTipoImovel(TipoImovel tipoImovel) {
        this.tipoImovel = tipoImovel;
    }

    public List<Bairro> getBairros() {
        return bairros;
    }

    public List<Logradouro> getLogradouros() {
        return logradouros;
    }

    public Bairro getBairro() {
        return bairro;
    }

    public void setBairro(Bairro bairro) {
        this.bairro = bairro;
    }

    public Logradouro getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(Logradouro logradouro) {
        this.logradouro = logradouro;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Pessoa getProprietario() {
        return proprietario;
    }

    public void setProprietario(Pessoa proprietario) {
        this.proprietario = proprietario;
    }

    public Pessoa getCompromissario() {
        return compromissario;
    }

    public void setCompromissario(Pessoa compromissario) {
        this.compromissario = compromissario;
    }

    public List<Pessoa> getProprietarios() {
        return proprietarios;
    }

    public List<Pessoa> getCompromissarios() {
        return compromissarios;
    }

    public void addProprietario() {
        addProprietario(this.proprietario);
        this.proprietario = null;
    }

    public void addCompromissario() {
        addCompromissario(this.compromissario);
        this.compromissario = null;
    }

    public void addLogradouro() {
        addLogradouro(this.logradouro);
        this.logradouro = null;
    }

    public void addBairro() {
        addBairro(this.bairro);
        this.bairro = null;
    }

    public void addProprietario(Pessoa proprietario) {
        if (!proprietarios.contains(proprietario)) {
            proprietarios.add(proprietario);
        }
    }

    public void addCompromissario(Pessoa compromissario) {
        if (!compromissarios.contains(compromissario)) {
            compromissarios.add(compromissario);
        }
    }

    public void addLogradouro(Logradouro logradouro) {
        if (!logradouros.contains(logradouro)) {
            logradouros.add(logradouro);
        }
    }

    public void removerProprietario(Pessoa proprietario) {
        if (proprietarios.contains(proprietario)) {
            proprietarios.remove(proprietario);
        }
    }

    public void removerCompromissario(Pessoa compromissario) {
        if (compromissarios.contains(compromissario)) {
            compromissarios.remove(compromissario);
        }
    }

    public void removerLogradouro(Logradouro logradouro) {
        if (logradouros.contains(logradouro)) {
            logradouros.remove(logradouro);
        }
    }

    public void addBairro(Bairro bairro) {
        if (!bairros.contains(bairro)) {
            bairros.add(bairro);
        }
    }

    public void removerBairro(Bairro bairro) {
        if (bairros.contains(bairro)) {
            bairros.remove(bairro);
        }
    }
}
