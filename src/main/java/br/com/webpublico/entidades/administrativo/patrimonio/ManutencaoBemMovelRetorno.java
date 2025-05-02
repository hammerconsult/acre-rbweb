package br.com.webpublico.entidades.administrativo.patrimonio;

import br.com.webpublico.entidades.DetentorArquivoComposicao;
import br.com.webpublico.entidades.EventoBem;
import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by zaca on 15/05/17.
 */
@Entity
@Audited
@Etiqueta("Manutenção de Bens Móveis - Retorno")
public class ManutencaoBemMovelRetorno extends EventoBem implements PossuidorArquivo {

    @Etiqueta("Manutenção Realizada")
    @Tabelavel
    private String manutencaoRealizada;

    @Etiqueta("Retorno da Manutenção")
    @Tabelavel
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    private Date retornoEm;

    @Etiqueta("Valor da Manutenção")
    @Tabelavel
    @Monetario
    private BigDecimal valorManutencao;

    @OneToOne
    @Etiqueta("Manutenção de Bens Móveis - Remessa")
    private ManutencaoBemMovelEntrada bemMovelEntrada;

    @Invisivel
    @OneToOne(cascade = CascadeType.ALL)
    private DetentorArquivoComposicao detentorArquivoComposicao;

    public ManutencaoBemMovelRetorno() {
        super(TipoEventoBem.MANUTENCAOBEMMOVEISRETORNO, TipoOperacao.NENHUMA_OPERACAO);
    }

    public String getManutencaoRealizada() {
        return manutencaoRealizada;
    }

    public void setManutencaoRealizada(String manutencaoRealizada) {
        this.manutencaoRealizada = manutencaoRealizada;
    }

    public Date getRetornoEm() {
        return retornoEm;
    }

    public void setRetornoEm(Date retornoEm) {
        this.retornoEm = retornoEm;
    }

    public BigDecimal getValorManutencao() {
        return valorManutencao;
    }

    public void setValorManutencao(BigDecimal valorManutencao) {
        this.valorManutencao = valorManutencao;
    }

    public ManutencaoBemMovelEntrada getBemMovelEntrada() {
        return bemMovelEntrada;
    }

    public void setBemMovelEntrada(ManutencaoBemMovelEntrada bemMovelEntrada) {
        this.bemMovelEntrada = bemMovelEntrada;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return this.detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }
}
