package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.ClassificacaoAtividade;
import br.com.webpublico.enums.SituacaoCadastralCadastroEconomico;
import br.com.webpublico.enums.TipoIssqn;
import br.com.webpublico.enums.TipoPorte;
import br.com.webpublico.tributario.enumeration.GrauDeRiscoDTO;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import java.util.List;

public class FiltroMalaDiretaGeralEconomico {

    private String inscricaoIncial;
    private String inscricaoFinal;
    private CNAE cnae;
    private List<CNAE> cnaes;
    private ClassificacaoAtividade classificacao;
    private List<ClassificacaoAtividade> classificacoes;
    private SituacaoCadastralCadastroEconomico situacao;
    private List<SituacaoCadastralCadastroEconomico> situacoes;
    private TipoAutonomo tipoAutonomo;
    private List<TipoAutonomo> tiposAutonomos;
    private NaturezaJuridica naturezaJuridica;
    private List<NaturezaJuridica> naturezasJuridicas;
    private Bairro bairro;
    private List<Bairro> bairros;
    private Logradouro logradouro;
    private List<Logradouro> logradouros;
    private TipoIssqn tipoIssqn;
    private List<TipoIssqn> tiposIssqn;
    private TipoPorte porte;
    private List<TipoPorte> portes;
    private Pessoa pessoa;
    private List<Pessoa> pessoas;
    private Pessoa socio;
    private List<Pessoa> socios;
    private GrauDeRiscoDTO grauDeRisco;

    public FiltroMalaDiretaGeralEconomico() {
        bairros = Lists.newArrayList();
        logradouros = Lists.newArrayList();
        cnaes = Lists.newArrayList();
        cnaes = Lists.newArrayList();
        classificacoes = Lists.newArrayList();
        situacoes = Lists.newArrayList();
        tiposAutonomos = Lists.newArrayList();
        naturezasJuridicas = Lists.newArrayList();
        bairros = Lists.newArrayList();
        logradouros = Lists.newArrayList();
        tiposIssqn = Lists.newArrayList();
        portes = Lists.newArrayList();
        pessoas = Lists.newArrayList();
        socios = Lists.newArrayList();
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

    public CNAE getCnae() {
        return cnae;
    }

    public void setCnae(CNAE cnae) {
        this.cnae = cnae;
    }

    public ClassificacaoAtividade getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(ClassificacaoAtividade classificacao) {
        this.classificacao = classificacao;
    }

    public SituacaoCadastralCadastroEconomico getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoCadastralCadastroEconomico situacao) {
        this.situacao = situacao;
    }

    public TipoAutonomo getTipoAutonomo() {
        return tipoAutonomo;
    }

    public void setTipoAutonomo(TipoAutonomo tipoAutonomo) {
        this.tipoAutonomo = tipoAutonomo;
    }

    public NaturezaJuridica getNaturezaJuridica() {
        return naturezaJuridica;
    }

    public void setNaturezaJuridica(NaturezaJuridica naturezaJuridica) {
        this.naturezaJuridica = naturezaJuridica;
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

    public TipoIssqn getTipoIssqn() {
        return tipoIssqn;
    }

    public void setTipoIssqn(TipoIssqn tipoIssqn) {
        this.tipoIssqn = tipoIssqn;
    }

    public TipoPorte getPorte() {
        return porte;
    }

    public void setPorte(TipoPorte porte) {
        this.porte = porte;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Pessoa getSocio() {
        return socio;
    }

    public void setSocio(Pessoa socio) {
        this.socio = socio;
    }

    public List<CNAE> getCnaes() {
        return cnaes;
    }

    public List<ClassificacaoAtividade> getClassificacoes() {
        return classificacoes;
    }

    public List<SituacaoCadastralCadastroEconomico> getSituacoes() {
        return situacoes;
    }

    public List<TipoAutonomo> getTiposAutonomos() {
        return tiposAutonomos;
    }

    public List<NaturezaJuridica> getNaturezasJuridicas() {
        return naturezasJuridicas;
    }

    public List<Bairro> getBairros() {
        return bairros;
    }

    public List<Logradouro> getLogradouros() {
        return logradouros;
    }

    public List<TipoIssqn> getTiposIssqn() {
        return tiposIssqn;
    }

    public List<TipoPorte> getPortes() {
        return portes;
    }

    public List<Pessoa> getPessoas() {
        return pessoas;
    }

    public List<Pessoa> getSocios() {
        return socios;
    }

    public GrauDeRiscoDTO getGrauDeRisco() {
        return grauDeRisco;
    }

    public void setGrauDeRisco(GrauDeRiscoDTO grauDeRisco) {
        this.grauDeRisco = grauDeRisco;
    }

    public void addClassificacao() {
        Util.adicionarObjetoEmLista(classificacoes, classificacao);
        classificacao = null;
    }

    public void addCNAE() {
        Util.adicionarObjetoEmLista(cnaes, cnae);
        cnae = null;
    }

    public void addSituacao() {
        Util.adicionarObjetoEmLista(situacoes, situacao);
        situacao = null;
    }

    public void addTipoAutonomo() {
        Util.adicionarObjetoEmLista(tiposAutonomos, tipoAutonomo);
        tipoAutonomo = null;
    }

    public void addNaturezaJuridica() {
        Util.adicionarObjetoEmLista(naturezasJuridicas, naturezaJuridica);
        naturezaJuridica = null;
    }

    public void addBairro() {
        Util.adicionarObjetoEmLista(bairros, bairro);
        bairro = null;
    }

    public void addLogradouro() {
        Util.adicionarObjetoEmLista(logradouros, logradouro);
        logradouro = null;
    }

    public void addTipoIss() {
        Util.adicionarObjetoEmLista(tiposIssqn, tipoIssqn);
        tipoIssqn = null;
    }

    public void addPorte() {
        Util.adicionarObjetoEmLista(portes, porte);
        porte = null;
    }

    public void addPessoa() {
        Util.adicionarObjetoEmLista(pessoas, pessoa);
        pessoa = null;
    }

    public void addSocio() {
        Util.adicionarObjetoEmLista(socios, socio);
        socio = null;
    }

    public void removerDaLista(List list, Object object) {
        if (list.contains(object)) {
            list.remove(object);
        }
    }
}
