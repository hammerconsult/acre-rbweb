package br.com.webpublico.ws.model;

import br.com.webpublico.entidades.*;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class WSImovel implements Serializable {
    private Long id;
    private String inscricao;
    private String setor;
    private String quadra;
    private String lote;
    private String logradouro;
    private String bairro;
    private BigDecimal areaConstruida;
    private BigDecimal areaTerreno;
    private List<WSEventoCalculadoImovel> eventos;
    private List<String> proprietarios;
    private List<String> compromissarios;
    private List<WSConstrucao> construcoes;
    private String numero;
    private String complemento;
    private String testada;
    private String trechoLogradouro;
    private String nomePropriedade;
    private String tipoAreaRural;

    public WSImovel() {
    }

    public WSImovel(Long id, String inscricaoCadastral) {
        this.id = id;
        this.inscricao = inscricaoCadastral;
    }

    public WSImovel(CadastroRural cr) {
        id = cr.getId();
        eventos = Lists.newArrayList();
        proprietarios = Lists.newArrayList();
        compromissarios = Lists.newArrayList();
        construcoes = Lists.newArrayList();
        inscricao = cr.getCodigo().toString();
        nomePropriedade = cr.getNomePropriedade();
        tipoAreaRural = cr.getTipoAreaRural().getDescricao();
        logradouro = cr.getLocalizacaoLote();
        try {
            areaTerreno = new BigDecimal(cr.getAreaLote());
        } catch (Exception ignored) {
        }
        for (PropriedadeRural propriedade : cr.getPropriedadesAtuais()) {
            proprietarios.add(propriedade.getPessoa().getNomeCpfCnpj());
        }
    }

    public WSImovel(CadastroImobiliario bci) {
        id = bci.getId();
        eventos = Lists.newArrayList();
        proprietarios = Lists.newArrayList();
        compromissarios = Lists.newArrayList();
        construcoes = Lists.newArrayList();
        inscricao = bci.getInscricaoCadastral();
        numero = bci.getNumero();
        complemento = bci.getComplementoEndereco();
        if (bci.getLote() != null) {
            areaTerreno = BigDecimal.valueOf(bci.getLote().getAreaLote());
            if (bci.getLote().getSetor() != null) {
                setor = bci.getLote().getSetor().getCodigo();
            }
            if (bci.getLote().getQuadra() != null) {
                quadra = bci.getLote().getQuadra().getCodigo();
            }
            if (bci.getLote().getTestadaPrincipal() != null) {
                try {
                    logradouro = bci.getLote().getTestadaPrincipal().getFace().getLogradouroBairro().getLogradouro().getTipoLogradouro().getDescricao() + " " + bci.getLote().getTestadaPrincipal().getFace().getLogradouroBairro().getLogradouro().getNome();
                    bairro = bci.getLote().getTestadaPrincipal().getFace().getLogradouroBairro().getBairro().getDescricao();
                    trechoLogradouro = bci.getLote().getTestadaPrincipal().getFace().getLogradouroBairro().getLogradouro().toString();
                    testada = bci.getLote().getTestadaPrincipal().getFace().getCodigoFace() + " - " + bci.getLote().getTestadaPrincipal().getFace().getLado();
                } catch (Exception e) {

                }
            }
            lote = bci.getLote().getCodigoLote();

        }
        areaConstruida = bci.getAreaTotalConstruida();
        for (EventoCalculoBCI ev : bci.getEventosCalculoBCI()) {
            WSEventoCalculadoImovel wsEv = new WSEventoCalculadoImovel();
            wsEv.setDescricao(ev.getEventoCalculo().getEventoCalculo().getDescricao());
            wsEv.setIdentificacao(ev.getEventoCalculo().getEventoCalculo().getIdentificacao());
            wsEv.setValor(ev.getValorFormatado());

            eventos.add(wsEv);
        }
        for (Construcao c : bci.getConstrucoesAtivas()) {
            construcoes.add(new WSConstrucao(c));
        }
        for (Propriedade propriedade : bci.getPropriedadeVigente()) {
            proprietarios.add(propriedade.getPessoa().getNomeCpfCnpj());
        }
        for (Compromissario compromissario : bci.getCompromissarioVigente()) {
            compromissarios.add(compromissario.getCompromissario().getNomeCpfCnpj());
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInscricao() {
        return inscricao;
    }

    public void setInscricao(String inscricao) {
        this.inscricao = inscricao;
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }

    public String getQuadra() {
        return quadra;
    }

    public void setQuadra(String quadra) {
        this.quadra = quadra;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }


    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public BigDecimal getAreaConstruida() {
        return areaConstruida;
    }

    public void setAreaConstruida(BigDecimal areaConstruida) {
        this.areaConstruida = areaConstruida;
    }

    public List<WSEventoCalculadoImovel> getEventos() {
        return eventos;
    }

    public void setEventos(List<WSEventoCalculadoImovel> eventos) {
        this.eventos = eventos;
    }

    public List<String> getProprietarios() {
        return proprietarios;
    }

    public void setProprietarios(List<String> proprietarios) {
        this.proprietarios = proprietarios;
    }

    public List<WSConstrucao> getConstrucoes() {
        return construcoes;
    }

    public void setConstrucoes(List<WSConstrucao> construcoes) {
        this.construcoes = construcoes;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public List<String> getCompromissarios() {
        return compromissarios;
    }

    public void setCompromissarios(List<String> compromissarios) {
        this.compromissarios = compromissarios;
    }

    public String getTestada() {

        return testada;
    }

    public void setTestada(String testada) {
        this.testada = testada;
    }

    public String getTrechoLogradouro() {
        return trechoLogradouro;
    }

    public void setTrechoLogradouro(String trechoLogradouro) {
        this.trechoLogradouro = trechoLogradouro;
    }

    public BigDecimal getAreaTerreno() {
        return areaTerreno;
    }

    public void setAreaTerreno(BigDecimal areaTerreno) {
        this.areaTerreno = areaTerreno;
    }

    public String getNomePropriedade() {
        return nomePropriedade;
    }

    public void setNomePropriedade(String nomePropriedade) {
        this.nomePropriedade = nomePropriedade;
    }

    public String getTipoAreaRural() {
        return tipoAreaRural;
    }

    public void setTipoAreaRural(String tipoAreaRural) {
        this.tipoAreaRural = tipoAreaRural;
    }
}
