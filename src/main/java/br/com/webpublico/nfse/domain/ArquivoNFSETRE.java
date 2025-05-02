package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.DetentorArquivoComposicao;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Etiqueta("Arquivo NFSE - TRE/TSE")
@Entity
@Audited
public class ArquivoNFSETRE extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue
    private Long id;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Data de Geração")
    @Temporal(TemporalType.DATE)
    private Date dataGeracao;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Usuário de Geração")
    @ManyToOne
    private UsuarioSistema usuarioGeracao;

    private Integer numeroNotificacaoTre;

    @Obrigatorio
    @Etiqueta("Ano da Remessa")
    private Integer anoRemessa;

    @Obrigatorio
    @Etiqueta("Mês da Remessa")
    private Integer mesRemessa;

    private Integer numeroLoteRemessa;

    private Integer anoRemessaCorrecao;

    private Integer mesRemessaCorrecao;

    private Integer numeroLoteRemessaCorrecao;

    @Obrigatorio
    @Etiqueta("Abrangência Inicial")
    @Temporal(TemporalType.DATE)
    private Date abrangenciaInicial;

    @Obrigatorio
    @Etiqueta("Abrangência Final")
    @Temporal(TemporalType.DATE)
    private Date abrangenciaFinal;

    @Obrigatorio
    @Etiqueta("Arquivo de CPF/CNPJ")
    @OneToOne(cascade = CascadeType.ALL)
    private DetentorArquivoComposicao detentorArquivoComposicao;

    private Integer totalLinhas;

    @OneToMany(mappedBy = "arquivoNFSETRE")
    private List<ArquivoNFSETREDetalhe> detalhes;

    @OneToOne(cascade = CascadeType.ALL)
    private Arquivo arquivoGerado;

    public ArquivoNFSETRE() {
        super();
        this.numeroNotificacaoTre = 0;
        this.numeroLoteRemessa = 1;
        this.anoRemessaCorrecao = 0;
        this.mesRemessaCorrecao = 0;
        this.numeroLoteRemessaCorrecao = 0;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(Date dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    public Integer getAnoRemessa() {
        return anoRemessa;
    }

    public void setAnoRemessa(Integer anoRemessa) {
        this.anoRemessa = anoRemessa;
    }

    public Integer getMesRemessa() {
        return mesRemessa;
    }

    public void setMesRemessa(Integer mesRemessa) {
        this.mesRemessa = mesRemessa;
    }

    public UsuarioSistema getUsuarioGeracao() {
        return usuarioGeracao;
    }

    public void setUsuarioGeracao(UsuarioSistema usuarioGeracao) {
        this.usuarioGeracao = usuarioGeracao;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public Integer getNumeroNotificacaoTre() {
        return numeroNotificacaoTre;
    }

    public void setNumeroNotificacaoTre(Integer numeroNotificacaoTre) {
        this.numeroNotificacaoTre = numeroNotificacaoTre;
    }

    public Integer getNumeroLoteRemessa() {
        return numeroLoteRemessa;
    }

    public void setNumeroLoteRemessa(Integer numeroLoteRemessa) {
        this.numeroLoteRemessa = numeroLoteRemessa;
    }

    public Integer getAnoRemessaCorrecao() {
        return anoRemessaCorrecao;
    }

    public void setAnoRemessaCorrecao(Integer anoRemessaCorrecao) {
        this.anoRemessaCorrecao = anoRemessaCorrecao;
    }

    public Integer getMesRemessaCorrecao() {
        return mesRemessaCorrecao;
    }

    public void setMesRemessaCorrecao(Integer mesRemessaCorrecao) {
        this.mesRemessaCorrecao = mesRemessaCorrecao;
    }

    public Integer getNumeroLoteRemessaCorrecao() {
        return numeroLoteRemessaCorrecao;
    }

    public void setNumeroLoteRemessaCorrecao(Integer numeroLoteRemessaCorrecao) {
        this.numeroLoteRemessaCorrecao = numeroLoteRemessaCorrecao;
    }

    public Integer getTotalLinhas() {
        return totalLinhas;
    }

    public void setTotalLinhas(Integer totalLinhas) {
        this.totalLinhas = totalLinhas;
    }

    public Date getAbrangenciaInicial() {
        return abrangenciaInicial;
    }

    public void setAbrangenciaInicial(Date abrangenciaInicial) {
        this.abrangenciaInicial = abrangenciaInicial;
    }

    public Date getAbrangenciaFinal() {
        return abrangenciaFinal;
    }

    public void setAbrangenciaFinal(Date abrangenciaFinal) {
        this.abrangenciaFinal = abrangenciaFinal;
    }

    public List<ArquivoNFSETREDetalhe> getDetalhes() {
        return detalhes;
    }

    public void setDetalhes(List<ArquivoNFSETREDetalhe> detalhes) {
        this.detalhes = detalhes;
    }

    public Arquivo getArquivoGerado() {
        return arquivoGerado;
    }

    public void setArquivoGerado(Arquivo arquivoGerado) {
        this.arquivoGerado = arquivoGerado;
    }
}
