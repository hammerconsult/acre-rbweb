package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.entidades.CNAE;
import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.Servico;
import br.com.webpublico.enums.SituacaoCadastralCadastroEconomico;
import br.com.webpublico.enums.TipoPorte;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.util.Strings;

import java.util.Date;
import java.util.List;

public class FiltroRelatorioCadastroEconomicoSemMovimento {
    private Date dataInicial;
    private Date dataFinal;
    private CadastroEconomico cadastroEconomico;
    private List<CadastroEconomico> cadastrosEconomicos;
    private String cnpjInicial;
    private String cnpjFinal;
    private TipoPorte porte;
    private List<TipoPorte> portes;
    private SituacaoCadastralCadastroEconomico situacaoCadastral;
    private List<SituacaoCadastralCadastroEconomico> situacoesCadastrais;
    private CNAE cnae;
    private List<CNAE> cnaes;
    private Servico servico;
    private List<Servico> servicos;

    public FiltroRelatorioCadastroEconomicoSemMovimento() {
        dataInicial = new Date();
        dataFinal = new Date();
        cadastrosEconomicos = Lists.newArrayList();
        portes = Lists.newArrayList();
        situacoesCadastrais = Lists.newArrayList();
        cnaes = Lists.newArrayList();
        servicos = Lists.newArrayList();
    }

    public String montarDescricaoFiltros() {
        StringBuilder retorno = new StringBuilder();
        if (dataInicial != null) {
            retorno.append(" Data Inicial: ").append(DataUtil.getDataFormatada(dataInicial)).append("; ");
        }
        if (dataFinal != null) {
            retorno.append(" Data Final: ").append(DataUtil.getDataFormatada(dataFinal)).append("; ");
        }
        if (cadastrosEconomicos != null && !cadastrosEconomicos.isEmpty()) {
            retorno.append(" Cadastro(s) Econômico(s): ");
            String juncao = "";
            for (CadastroEconomico ce : cadastrosEconomicos) {
                retorno.append(juncao).append(ce.getInscricaoCadastral());
                juncao = ", ";
            }
            retorno.append("; ");
        }
        if (Strings.isNotEmpty(cnpjInicial)) {
            retorno.append(" CNPJ Inicial: ").append(cnpjInicial).append("; ");
        }
        if (Strings.isNotEmpty(cnpjFinal)) {
            retorno.append(" CNPJ Final: ").append(cnpjFinal).append("; ");
        }
        if (portes != null && !portes.isEmpty()) {
            retorno.append(" Porte(s): ");
            String juncao = "";
            for (TipoPorte p : portes) {
                retorno.append(juncao).append(p.getDescricao());
                juncao = ", ";
            }
            retorno.append("; ");
        }
        if (situacoesCadastrais != null && !situacoesCadastrais.isEmpty()) {
            retorno.append(" Situação(ões): ");
            String juncao = "";
            for (SituacaoCadastralCadastroEconomico s : situacoesCadastrais) {
                retorno.append(juncao).append(s.getDescricao());
                juncao = ", ";
            }
            retorno.append("; ");
        }
        if (cnaes != null && !cnaes.isEmpty()) {
            retorno.append(" CNAE(s): ");
            String juncao = "";
            for (CNAE c : cnaes) {
                retorno.append(juncao).append(c.getCodigoCnae());
                juncao = ", ";
            }
            retorno.append("; ");
        }
        if (servicos != null && !servicos.isEmpty()) {
            retorno.append(" Serviços(s): ");
            String juncao = "";
            for (Servico s : servicos) {
                retorno.append(juncao).append(s.getCodigo());
                juncao = ", ";
            }
            retorno.append("; ");
        }
        return retorno.toString();
    }

    public void validarCampos() throws ValidacaoException {
        ValidacaoException coe = new ValidacaoException();
        if (dataInicial == null) {
            coe.adicionarMensagemDeCampoObrigatorio("O campo Data Inicial deve ser informado");
        }
        if (dataFinal == null) {
            coe.adicionarMensagemDeCampoObrigatorio("O campo Data Final deve ser informado");
        }
        if (dataInicial.after(dataFinal)) {
            coe.adicionarMensagemDeOperacaoNaoPermitida("O campo Data Final deve ser maior que a Data Inicial.");
        }
        coe.lancarException();
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public List<CadastroEconomico> getCadastrosEconomicos() {
        return cadastrosEconomicos;
    }

    public void setCadastrosEconomicos(List<CadastroEconomico> cadastrosEconomicos) {
        this.cadastrosEconomicos = cadastrosEconomicos;
    }

    public String getCnpjInicial() {
        return cnpjInicial;
    }

    public void setCnpjInicial(String cnpjInicial) {
        this.cnpjInicial = cnpjInicial;
    }

    public String getCnpjFinal() {
        return cnpjFinal;
    }

    public void setCnpjFinal(String cnpjFinal) {
        this.cnpjFinal = cnpjFinal;
    }

    public TipoPorte getPorte() {
        return porte;
    }

    public void setPorte(TipoPorte porte) {
        this.porte = porte;
    }

    public SituacaoCadastralCadastroEconomico getSituacaoCadastral() {
        return situacaoCadastral;
    }

    public void setSituacaoCadastral(SituacaoCadastralCadastroEconomico situacaoCadastral) {
        this.situacaoCadastral = situacaoCadastral;
    }

    public CNAE getCnae() {
        return cnae;
    }

    public void setCnae(CNAE cnae) {
        this.cnae = cnae;
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }

    public List<CNAE> getCnaes() {
        return cnaes;
    }

    public void setCnaes(List<CNAE> cnaes) {
        this.cnaes = cnaes;
    }

    public List<Servico> getServicos() {
        return servicos;
    }

    public void setServicos(List<Servico> servicos) {
        this.servicos = servicos;
    }

    public List<TipoPorte> getPortes() {
        return portes;
    }

    public void setPortes(List<TipoPorte> portes) {
        this.portes = portes;
    }

    public List<SituacaoCadastralCadastroEconomico> getSituacoesCadastrais() {
        return situacoesCadastrais;
    }

    public void setSituacoesCadastrais(List<SituacaoCadastralCadastroEconomico> situacoesCadastrais) {
        this.situacoesCadastrais = situacoesCadastrais;
    }

    public void add(List lista, Object obj) {
        if (obj == null)
            return;
        if (lista == null) {
            lista = Lists.newArrayList();
        }
        if (!lista.contains(obj)) {
            lista.add(obj);
        }
    }

    public void remove(List lista, Object obj) {
        lista.remove(obj);
    }

    public void addCadastro() {
        add(cadastrosEconomicos, cadastroEconomico);
        cadastroEconomico = null;
    }

    public void removeCadastro(CadastroEconomico cadastroEconomico) {
        remove(cadastrosEconomicos, cadastroEconomico);
    }

    public void addPorte() {
        add(portes, porte);
        porte = null;
    }

    public void removePorte(TipoPorte porte) {
        remove(portes, porte);
    }

    public void addSituacao() {
        add(situacoesCadastrais, situacaoCadastral);
        situacaoCadastral = null;
    }

    public void removeSituacao(SituacaoCadastralCadastroEconomico situacaoCadastral) {
        remove(situacoesCadastrais, situacaoCadastral);
    }

    public void addCnae() {
        add(cnaes, cnae);
        cnae = null;
    }

    public void removeCnae(CNAE cnae) {
        remove(cnaes, cnae);
    }

    public void addServico() {
        add(servicos, servico);
        servico = null;
    }

    public void removeServico(Servico servico) {
        remove(servicos, servico);
    }

    public List<Long> getIdsCadastros() {
        List<Long> ids = Lists.newArrayList();
        for (CadastroEconomico ce : cadastrosEconomicos) {
            ids.add(ce.getId());
        }
        return ids;
    }

    public List<String> getNamesPortes() {
        List<String> names = Lists.newArrayList();
        for (TipoPorte p : portes) {
            names.add(p.name());
        }
        return names;
    }

    public List<String> getNamesSituacoesCadastrais() {
        List<String> names = Lists.newArrayList();
        for (SituacaoCadastralCadastroEconomico sit : situacoesCadastrais) {
            names.add(sit.name());
        }
        return names;
    }

    public List<Long> getIdsCnaes() {
        List<Long> ids = Lists.newArrayList();
        for (CNAE c : cnaes) {
            ids.add(c.getId());
        }
        return ids;
    }

    public List<Long> getIdsServicos() {
        List<Long> ids = Lists.newArrayList();
        for (Servico c : servicos) {
            ids.add(c.getId());
        }
        return ids;
    }
}
