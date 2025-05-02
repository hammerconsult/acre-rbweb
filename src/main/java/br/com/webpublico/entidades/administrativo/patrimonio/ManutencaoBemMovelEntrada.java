package br.com.webpublico.entidades.administrativo.patrimonio;


import br.com.webpublico.entidades.DetentorArquivoComposicao;
import br.com.webpublico.entidades.EventoBem;
import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.enums.administrativo.TipoManutencao;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by zaca on 12/05/17.
 */
@Entity
@Audited
@Etiqueta("Manutenção de Bens Móveis - Remessa")
public class ManutencaoBemMovelEntrada extends EventoBem implements PossuidorArquivo {

    private static final Long serialVersionUID = 1l;

    @Etiqueta(" Tipo de Manutenção ")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private TipoManutencao tipoManutencao;

    @Etiqueta(" Descrição da Manutenção Proposta ")
    @Tabelavel
    @Obrigatorio
    private String manutencaoProposta;

    @Etiqueta(" Início da Manutenção ")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    private Date inicioEm;

    @Invisivel
    @OneToOne(cascade = CascadeType.ALL)
    private DetentorArquivoComposicao detentorArquivoComposicao;

    public ManutencaoBemMovelEntrada() {
        super(TipoEventoBem.MANUTENCAOBEMMOVEISENTRADA, TipoOperacao.NENHUMA_OPERACAO);
    }

    public TipoManutencao getTipoManutencao() {
        return tipoManutencao;
    }

    public void setTipoManutencao(TipoManutencao tipoManutencao) {
        this.tipoManutencao = tipoManutencao;
    }

    public String getManutencaoProposta() {
        return manutencaoProposta;
    }

    public void setManutencaoProposta(String manutencaoProposta) {
        this.manutencaoProposta = manutencaoProposta;
    }

    public Date getInicioEm() {
        return inicioEm;
    }

    public void setInicioEm(Date inicioEm) {
        this.inicioEm = inicioEm;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    @Override
    public String toString() {
        return DataUtil.getDataFormatada(inicioEm)+ " - "+ getBem() + " - " + tipoManutencao.getDescricao();
    }
}
