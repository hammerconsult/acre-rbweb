package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@GrupoDiagrama(nome = "IPTU")
@Entity
@Audited
@Etiqueta("Parâmetro de Exportação IPTU")
public class ParametroExportacaoIPTU implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Tipo de Parâmetro")
    private TipoParametroExportacaoIPTU tipoParametro;
    private String identificacaoRegistroSegmento;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Número do Convênio")
    private String numeroConvenio;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Nome do Arquivo")
    private String nomeDoArquivo;
    private String identificacaoProduto;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Versão do Layout")
    private String numeroVersaoLayout;
    private String contribuinteIGN;
    private String segmentoFebrabanCovenente;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Moeda do Convênio")
    @Enumerated(EnumType.STRING)
    private MoedaConvenio moedaConvenio;
    private int casasDecimaisMoeda;
    private Boolean receberAposVencimento;
    @Enumerated(EnumType.STRING)
    private FormatoData formatoData;

    private String msgSegG01Notificacao; //mensagem01
    private String msgSegG02Notificacao; //mensagem02
    private String msgSegG03Notificacao; //mensagem03
    private String msgSegG04Notificacao; //mensagem04

    private String msgSegX01Devedores; //mensagem05
    private String msgSegX02Devedores; //mensagem06

    private String msgSegX03NaoDevedores; //mensagem07
    private String msgSegX04NaoDevedores; //mensagem08

    private String msgSegX05Parcelamento; //mensagem09
    @Transient
    private Long criadoEm;

    public ParametroExportacaoIPTU() {
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoParametroExportacaoIPTU getTipoParametro() {
        return tipoParametro;
    }

    public void setTipoParametro(TipoParametroExportacaoIPTU tipoParametro) {
        this.tipoParametro = tipoParametro;
    }

    public String getIdentificacaoRegistroSegmento() {
        return identificacaoRegistroSegmento;
    }

    public void setIdentificacaoRegistroSegmento(String identificacaoRegistroSegmento) {
        this.identificacaoRegistroSegmento = identificacaoRegistroSegmento;
    }

    public String getNumeroConvenio() {
        return numeroConvenio;
    }

    public void setNumeroConvenio(String numeroConvenio) {
        this.numeroConvenio = numeroConvenio;
    }

    public String getNomeDoArquivo() {
        return nomeDoArquivo;
    }

    public void setNomeDoArquivo(String nomeDoArquivo) {
        this.nomeDoArquivo = nomeDoArquivo;
    }

    public String getIdentificacaoProduto() {
        return identificacaoProduto;
    }

    public void setIdentificacaoProduto(String identificacaoProduto) {
        this.identificacaoProduto = identificacaoProduto;
    }

    public String getNumeroVersaoLayout() {
        return numeroVersaoLayout;
    }

    public void setNumeroVersaoLayout(String numeroVersaoLayout) {
        this.numeroVersaoLayout = numeroVersaoLayout;
    }

    public String getContribuinteIGN() {
        return contribuinteIGN;
    }

    public void setContribuinteIGN(String contribuinteIGN) {
        this.contribuinteIGN = contribuinteIGN;
    }

    public String getSegmentoFebrabanCovenente() {
        return segmentoFebrabanCovenente;
    }

    public void setSegmentoFebrabanCovenente(String segmentoFebrabanCovenente) {
        this.segmentoFebrabanCovenente = segmentoFebrabanCovenente;
    }

    public MoedaConvenio getMoedaConvenio() {
        return moedaConvenio;
    }

    public void setMoedaConvenio(MoedaConvenio moedaConvenio) {
        this.moedaConvenio = moedaConvenio;
    }

    public int getCasasDecimaisMoeda() {
        return casasDecimaisMoeda;
    }

    public void setCasasDecimaisMoeda(int casasDecimaisMoeda) {
        this.casasDecimaisMoeda = casasDecimaisMoeda;
    }

    public Boolean getReceberAposVencimento() {
        return receberAposVencimento != null ? receberAposVencimento : false;
    }

    public void setReceberAposVencimento(Boolean receberAposVencimento) {
        this.receberAposVencimento = receberAposVencimento;
    }

    public FormatoData getFormatoData() {
        return formatoData;
    }

    public void setFormatoData(FormatoData formatoData) {
        this.formatoData = formatoData;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public String getMsgSegG01Notificacao() {
        return msgSegG01Notificacao;
    }

    public void setMsgSegG01Notificacao(String msgSegG01Notificacao) {
        this.msgSegG01Notificacao = msgSegG01Notificacao;
    }

    public String getMsgSegG02Notificacao() {
        return msgSegG02Notificacao;
    }

    public void setMsgSegG02Notificacao(String msgSegG02Notificacao) {
        this.msgSegG02Notificacao = msgSegG02Notificacao;
    }

    public String getMsgSegG03Notificacao() {
        return msgSegG03Notificacao;
    }

    public void setMsgSegG03Notificacao(String msgSegG03Notificacao) {
        this.msgSegG03Notificacao = msgSegG03Notificacao;
    }

    public String getMsgSegG04Notificacao() {
        return msgSegG04Notificacao;
    }

    public void setMsgSegG04Notificacao(String msgSegG04Notificacao) {
        this.msgSegG04Notificacao = msgSegG04Notificacao;
    }

    public String getMsgSegX01Devedores() {
        return msgSegX01Devedores;
    }

    public void setMsgSegX01Devedores(String msgSegX01Devedores) {
        this.msgSegX01Devedores = msgSegX01Devedores;
    }

    public String getMsgSegX02Devedores() {
        return msgSegX02Devedores;
    }

    public void setMsgSegX02Devedores(String msgSegX02Devedores) {
        this.msgSegX02Devedores = msgSegX02Devedores;
    }

    public String getMsgSegX03NaoDevedores() {
        return msgSegX03NaoDevedores;
    }

    public void setMsgSegX03NaoDevedores(String msgSegX03NaoDevedores) {
        this.msgSegX03NaoDevedores = msgSegX03NaoDevedores;
    }

    public String getMsgSegX04NaoDevedores() {
        return msgSegX04NaoDevedores;
    }

    public void setMsgSegX04NaoDevedores(String msgSegX04NaoDevedores) {
        this.msgSegX04NaoDevedores = msgSegX04NaoDevedores;
    }

    public String getMsgSegX05Parcelamento() {
        return msgSegX05Parcelamento;
    }

    public void setMsgSegX05Parcelamento(String msgSegX05Parcelamento) {
        this.msgSegX05Parcelamento = msgSegX05Parcelamento;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    public enum TipoParametroExportacaoIPTU implements EnumComDescricao {
        IPTU("1 - IPTU", "1");
        private String descricao;
        private String valor;

        private TipoParametroExportacaoIPTU(String descricao, String valor) {
            this.descricao = descricao;
            this.valor = valor;
        }

        public String getDescricao() {
            return descricao;
        }

        public String getValor() {
            return valor;
        }
    }

    public enum MoedaConvenio implements EnumComDescricao{
        VALOR_FIXO("Valor Fixo (Real)", "6");
        private String descricao;
        private String valor;
        private MoedaConvenio(String descricao, String valor) {
            this.descricao = descricao;
            this.valor = valor;
        }

        public String getDescricao() {
            return descricao;
        }

        public String getValor() {
            return valor;
        }
    }

    public enum FormatoData {
        AAAAMMDD("yyyyMMdd", "4");
        private String formato;
        private String valor;

        private FormatoData(String formato, String valor) {
            this.valor = valor;
            this.formato = formato;
        }

        public String getValor() {
            return valor;
        }

        public String getFormato() {
            return formato;
        }
    }
}
