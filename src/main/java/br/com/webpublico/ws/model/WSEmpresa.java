package br.com.webpublico.ws.model;


import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoIssqn;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

public class WSEmpresa {
    private Long id;
    private String tipoPessoa;
    private String cpfCnpj;
    private String inscricaoEstadual;
    private String naturezaJuridica;
    private String pessoa;
    private Boolean autonomo;
    private String tipoAutonomo;
    private String cmc;
    private TipoIssqn tipoIssqn;
    private Date abertura;
    private List<WSSocioEmpresa> socios;
    private List<WSAlvara> alvaras;
    private String nomeFantasia;
    private String classificacaoAtividade;

    public WSEmpresa() {
    }

    public WSEmpresa(CadastroEconomico cadastroEconomico) {
        id = cadastroEconomico.getId();
        socios = Lists.newArrayList();
        alvaras = Lists.newArrayList();
        tipoPessoa = cadastroEconomico.getPessoa().getTipoPessoa();
        cpfCnpj = cadastroEconomico.getPessoa().getCpf_Cnpj();
        inscricaoEstadual = cadastroEconomico.getPessoa().getRg_InscricaoEstadual();
        naturezaJuridica = cadastroEconomico.getNaturezaJuridica() != null ? cadastroEconomico.getNaturezaJuridica().getDescricao() : "";
        pessoa = cadastroEconomico.getPessoa().getNome();
        nomeFantasia = cadastroEconomico.getPessoa() instanceof PessoaJuridica ? ((PessoaJuridica)cadastroEconomico.getPessoa()).getNomeFantasia() : "";
        autonomo = cadastroEconomico.getTipoAutonomo() != null;
        tipoAutonomo = cadastroEconomico.getTipoAutonomo() != null ? cadastroEconomico.getTipoAutonomo().getDescricao() : "";
        cmc = cadastroEconomico.getInscricaoCadastral();
        abertura = cadastroEconomico.getAbertura();
        if (cadastroEconomico.getEnquadramentoVigente() != null) {
            tipoIssqn = cadastroEconomico.getEnquadramentoVigente().getTipoIssqn();
            classificacaoAtividade = cadastroEconomico.getClassificacaoAtividade() != null ? cadastroEconomico.getClassificacaoAtividade().getDescricao() : "";
        }
        if (cadastroEconomico.getSociedadeCadastroEconomico() != null) {
            for (SociedadeCadastroEconomico sociedade : cadastroEconomico.getSociedadeCadastroEconomico()) {
                socios.add(new WSSocioEmpresa(sociedade));
            }
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipoPessoa() {
        return tipoPessoa;
    }

    public void setTipoPessoa(String tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getNaturezaJuridica() {
        return naturezaJuridica;
    }

    public void setNaturezaJuridica(String naturezaJuridica) {
        this.naturezaJuridica = naturezaJuridica;
    }

    public String getPessoa() {
        return pessoa;
    }

    public void setPessoa(String pessoa) {
        this.pessoa = pessoa;
    }

    public String getTipoAutonomo() {
        return tipoAutonomo;
    }

    public void setTipoAutonomo(String tipoAutonomo) {
        this.tipoAutonomo = tipoAutonomo;
    }

    public String getCmc() {
        return cmc;
    }

    public void setCmc(String cmc) {
        this.cmc = cmc;
    }

    public TipoIssqn getTipoIssqn() {
        return tipoIssqn;
    }

    public void setTipoIssqn(TipoIssqn tipoIssqn) {
        this.tipoIssqn = tipoIssqn;
    }

    public Date getAbertura() {
        return abertura;
    }

    public void setAbertura(Date abertura) {
        this.abertura = abertura;
    }

    public List<WSSocioEmpresa> getSocios() {
        return socios;
    }

    public void setSocios(List<WSSocioEmpresa> socios) {
        this.socios = socios;
    }

    public List<WSAlvara> getAlvaras() {
        return alvaras;
    }

    public void setAlvaras(List<WSAlvara> alvaras) {
        this.alvaras = alvaras;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getClassificacaoAtividade() {
        return classificacaoAtividade;
    }

    public void setClassificacaoAtividade(String classificacaoAtividade) {
        this.classificacaoAtividade = classificacaoAtividade;
    }

    public Boolean getAutonomo() {
        return autonomo;
    }

    public void setAutonomo(Boolean autonomo) {
        this.autonomo = autonomo;
    }
}
