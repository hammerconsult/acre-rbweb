package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import com.google.common.collect.Lists;

import java.util.List;

public class AssistenteExclusaoMateriais {

    private ExclusaoMateriais selecionado;
    private SaidaMaterial saidaMaterial;
    private EntradaMaterial entradaMaterial;
    private RequisicaoMaterial requisicaoMaterial;
    private AprovacaoMaterial aprovacaoMaterial;
    private String mensagemAlerta;
    private String classeAlerta;
    private String movimento;
    private String urlMovimento;
    private Boolean avaliacaoPossuiSaida;
    private List<ExclusaoMateriaisVO> relacionamentosDependentes;
    private List<ExclusaoMateriaisVO> movimentos;
    private List<ExclusaoMateriaisItensVO> itens;

    public AssistenteExclusaoMateriais() {
        movimentos = Lists.newArrayList();
        avaliacaoPossuiSaida = false;
    }

    public Boolean getAvaliacaoPossuiSaida() {
        return avaliacaoPossuiSaida;
    }

    public void setAvaliacaoPossuiSaida(Boolean avaliacaoPossuiSaida) {
        this.avaliacaoPossuiSaida = avaliacaoPossuiSaida;
    }

    public List<ExclusaoMateriaisVO> getMovimentos() {
        return movimentos;
    }

    public void setMovimentos(List<ExclusaoMateriaisVO> movimentos) {
        this.movimentos = movimentos;
    }

    public List<ExclusaoMateriaisVO> getRelacionamentosDependentes() {
        return relacionamentosDependentes;
    }

    public void setRelacionamentosDependentes(List<ExclusaoMateriaisVO> relacionamentosDependentes) {
        this.relacionamentosDependentes = relacionamentosDependentes;
    }

    public SaidaMaterial getSaidaMaterial() {
        return saidaMaterial;
    }

    public void setSaidaMaterial(SaidaMaterial saidaMaterial) {
        this.saidaMaterial = saidaMaterial;
    }

    public EntradaMaterial getEntradaMaterial() {
        return entradaMaterial;
    }

    public void setEntradaMaterial(EntradaMaterial entradaMaterial) {
        this.entradaMaterial = entradaMaterial;
    }

    public RequisicaoMaterial getRequisicaoMaterial() {
        return requisicaoMaterial;
    }

    public void setRequisicaoMaterial(RequisicaoMaterial requisicaoMaterial) {
        this.requisicaoMaterial = requisicaoMaterial;
    }

    public AprovacaoMaterial getAprovacaoMaterial() {
        return aprovacaoMaterial;
    }

    public void setAprovacaoMaterial(AprovacaoMaterial aprovacaoMaterial) {
        this.aprovacaoMaterial = aprovacaoMaterial;
    }

    public String getMensagemAlerta() {
        return mensagemAlerta;
    }

    public void setMensagemAlerta(String mensagemAlerta) {
        this.mensagemAlerta = mensagemAlerta;
    }

    public String getClasseAlerta() {
        return classeAlerta;
    }

    public void setClasseAlerta(String classeAlerta) {
        this.classeAlerta = classeAlerta;
    }

    public String getMovimento() {
        return movimento;
    }

    public void setMovimento(String movimento) {
        this.movimento = movimento;
    }

    public String getUrlMovimento() {
        return urlMovimento;
    }

    public void setUrlMovimento(String urlMovimento) {
        this.urlMovimento = urlMovimento;
    }

    public ExclusaoMateriais getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(ExclusaoMateriais selecionado) {
        this.selecionado = selecionado;
    }

    public List<ExclusaoMateriaisItensVO> getItens() {
        return itens;
    }

    public void setItens(List<ExclusaoMateriaisItensVO> itens) {
        this.itens = itens;
    }
}
