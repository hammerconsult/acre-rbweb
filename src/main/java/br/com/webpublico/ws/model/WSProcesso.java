package br.com.webpublico.ws.model;

import br.com.webpublico.entidades.DocumentoProcesso;
import br.com.webpublico.entidades.Processo;
import br.com.webpublico.entidades.ProcessoArquivo;
import br.com.webpublico.entidades.Tramite;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

public class WSProcesso {

    private Integer numero;
    private Integer ano;
    private String situacao;
    private String responsavel;
    private String cadastradoPor;
    private String numeroAntigo;
    private String numeroSAJ;
    private String requerente;
    private String assunto;
    private String resumo;
    private String obs;
    private Date dataHora;
    private Boolean protocolo;
    private List<WSTramite> tramites;
    private List<WSDocumento> documentos;
    private List<WSProcesso> processosSubordinados;
    private List<WSArquivo> arquivos;
    private WSTramite finalizador;

    public WSProcesso(Processo processo) {
        processosSubordinados = Lists.newArrayList();
        tramites = Lists.newLinkedList();
        documentos = Lists.newArrayList();
        arquivos = Lists.newArrayList();
        this.ano = processo.getAno();
        this.numero = processo.getNumero();
        this.assunto = processo.getAssunto();
        if (!processo.getProtocolo()) {
            this.assunto = processo.getSubAssunto().getAssunto().getNome() + " - " + processo.getSubAssunto().getDescricao();
        }
        this.cadastradoPor = processo.getUoCadastro().getDescricao();
        this.dataHora = processo.getDataRegistro();
        this.numeroAntigo = processo.getNumeroProcessoAntigo();
        this.numeroSAJ = processo.getNumeroProcessoSAJ();
        this.obs = processo.getObservacoes();
        this.requerente = processo.getPessoa().getNome();
        this.responsavel = processo.getResponsavelCadastro().getNome();
        this.resumo = processo.getObjetivo();
        this.situacao = processo.getSituacao().getDescricao();
        this.protocolo = processo.getProtocolo();
        for (Tramite t : processo.getTramites()) {
            adicionar(t);
        }
        for (DocumentoProcesso d : processo.getDocumentoProcesso()) {
            adicionar(d);
        }
        for (Processo p : processo.getProcessosSubordinados()) {
            adicionar(p);
        }
        for (ProcessoArquivo a : processo.getArquivos()) {
            adicionar(a);
        }
        if (processo.getTramiteFinalizador() != null) {
            this.finalizador = new WSTramite(processo.getTramiteFinalizador());
        }
    }

    public WSProcesso() {
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public String getCadastradoPor() {
        return cadastradoPor;
    }

    public void setCadastradoPor(String cadastradoPor) {
        this.cadastradoPor = cadastradoPor;
    }

    public String getNumeroAntigo() {
        return numeroAntigo;
    }

    public void setNumeroAntigo(String numeroAntigo) {
        this.numeroAntigo = numeroAntigo;
    }

    public String getNumeroSAJ() {
        return numeroSAJ;
    }

    public void setNumeroSAJ(String numeroSAJ) {
        this.numeroSAJ = numeroSAJ;
    }

    public String getRequerente() {
        return requerente;
    }

    public void setRequerente(String requerente) {
        this.requerente = requerente;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public String getResumo() {
        return resumo;
    }

    public void setResumo(String resumo) {
        this.resumo = resumo;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public Date getDataHora() {
        return dataHora;
    }

    public void setDataHora(Date dataHora) {
        this.dataHora = dataHora;
    }

    public List<WSTramite> getTramites() {
        return tramites;
    }

    public void setTramites(List<WSTramite> tramites) {
        this.tramites = tramites;
    }

    public Boolean getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(Boolean processo) {
        this.protocolo = processo;
    }

    public List<WSDocumento> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<WSDocumento> documentos) {
        this.documentos = documentos;
    }

    public WSTramite getFinalizador() {
        return finalizador;
    }

    public void setFinalizador(WSTramite finalizador) {
        this.finalizador = finalizador;
    }

    public List<WSProcesso> getProcessosSubordinados() {
        return processosSubordinados;
    }

    public void setProcessosSubordinados(List<WSProcesso> processosSubordinados) {
        this.processosSubordinados = processosSubordinados;
    }

    public List<WSArquivo> getArquivos() {
        return arquivos;
    }

    public void setArquivos(List<WSArquivo> arquivos) {
        this.arquivos = arquivos;
    }

    private void adicionar(Tramite t) {
        WSTramite tramite = new WSTramite(t);
        this.tramites.add(tramite);
    }

    private void adicionar(DocumentoProcesso d) {
        WSDocumento doc = new WSDocumento(d);
        this.documentos.add(doc);
    }

    private void adicionar(Processo d) {
        WSProcesso doc = new WSProcesso(d);
        this.processosSubordinados.add(doc);
    }

    private void adicionar(ProcessoArquivo d) {
        WSArquivo arq = new WSArquivo();
        arq.setNome(d.getArquivo().getNome());
        arq.setMimeType(d.getArquivo().getMimeType());
        arq.setDados(d.getArquivo().getByteArrayDosDados());
        this.arquivos.add(arq);
    }
}
