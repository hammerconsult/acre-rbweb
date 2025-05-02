package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.CNAE;
import br.com.webpublico.entidades.NaturezaJuridica;
import br.com.webpublico.entidades.TipoAutonomo;
import br.com.webpublico.enums.ClassificacaoAtividade;
import br.com.webpublico.tributario.enumeration.GrauDeRiscoDTO;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoAlvara;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 21/07/15
 * Time: 16:57
 * To change this template use File | Settings | File Templates.
 */
public class FiltroRelacaoLancamentoAlvara extends AbstractFiltroRelacaoLancamentoDebito {

    private TipoAlvara tipoAlvara;
    private String inscricaoInicial;
    private String inscricaoFinal;
    private Integer provisorio;
    private ClassificacaoAtividade classificacaoAtividade;
    private NaturezaJuridica naturezaJuridica;
    private GrauDeRiscoDTO grauDeRisco;
    private TipoAutonomo tipoAutonomo;
    private Integer mei;
    private List<CNAE> cnaes;

    public FiltroRelacaoLancamentoAlvara() {
        this.cnaes = Lists.newArrayList();
        setSituacoes(new SituacaoParcela[]{SituacaoParcela.EM_ABERTO});
        setTipoRelatorio(TipoRelatorio.RESUMIDO);
        setSomenteTotalizador(Boolean.FALSE);
        provisorio = 2;
        mei = 2;
    }

    public TipoAlvara getTipoAlvara() {
        return tipoAlvara;
    }

    public void setTipoAlvara(TipoAlvara tipoAlvara) {
        this.tipoAlvara = tipoAlvara;
    }

    public String getInscricaoInicial() {
        return inscricaoInicial;
    }

    public void setInscricaoInicial(String inscricaoInicial) {
        this.inscricaoInicial = inscricaoInicial;
    }

    public String getInscricaoFinal() {
        return inscricaoFinal;
    }

    public void setInscricaoFinal(String inscricaoFinal) {
        this.inscricaoFinal = inscricaoFinal;
    }

    public Integer getProvisorio() {
        return provisorio;
    }

    public void setProvisorio(Integer provisorio) {
        this.provisorio = provisorio;
    }

    public ClassificacaoAtividade getClassificacaoAtividade() {
        return classificacaoAtividade;
    }

    public void setClassificacaoAtividade(ClassificacaoAtividade classificacaoAtividade) {
        this.classificacaoAtividade = classificacaoAtividade;
    }

    public NaturezaJuridica getNaturezaJuridica() {
        return naturezaJuridica;
    }

    public void setNaturezaJuridica(NaturezaJuridica naturezaJuridica) {
        this.naturezaJuridica = naturezaJuridica;
    }

    public GrauDeRiscoDTO getGrauDeRisco() {
        return grauDeRisco;
    }

    public void setGrauDeRisco(GrauDeRiscoDTO grauDeRisco) {
        this.grauDeRisco = grauDeRisco;
    }

    public TipoAutonomo getTipoAutonomo() {
        return tipoAutonomo;
    }

    public void setTipoAutonomo(TipoAutonomo tipoAutonomo) {
        this.tipoAutonomo = tipoAutonomo;
    }

    public Integer getMei() {
        return mei;
    }

    public void setMei(Integer mei) {
        this.mei = mei;
    }

    public List<CNAE> getCnaes() {
        return cnaes;
    }

    public void setCnaes(List<CNAE> cnaes) {
        this.cnaes = cnaes;
    }

    public String getListaIdCnaes() {
        int i = 1;
        String lista = "";
        for (CNAE cnae : cnaes) {
            if (i < cnaes.size()) {
                lista += cnae.getId() + ", ";
            } else {
                lista += cnae.getId();
            }
            i++;
        }
        return lista;
    }

    public String getListaCodigoCnaes() {
        int i = 1;
        String lista = "";
        for (CNAE cnae : cnaes) {
            if (i < cnaes.size()) {
                lista += cnae.getCodigoCnae() + ", ";
            } else {
                lista += cnae.getCodigoCnae();
            }
            i++;
        }
        return lista;
    }
}

