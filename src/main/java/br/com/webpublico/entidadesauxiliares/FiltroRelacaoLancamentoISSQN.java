package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.NaturezaJuridica;
import br.com.webpublico.enums.ClassificacaoAtividade;
import br.com.webpublico.tributario.enumeration.GrauDeRiscoDTO;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoCalculoISS;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 03/08/15
 * Time: 14:34
 * To change this template use File | Settings | File Templates.
 */
public class FiltroRelacaoLancamentoISSQN extends AbstractFiltroRelacaoLancamentoDebito {


    private TipoCalculoISS tipoCalculoISS;
    private List<TipoCalculoISS> tiposCalculoISS;
    private String inscricaoInicial;
    private String inscricaoFinal;
    private Long mesReferencia;
    private ClassificacaoAtividade classificacaoAtividade;
    private List<ClassificacaoAtividade> classificacoesAtividade;
    private NaturezaJuridica naturezaJuridica;
    private List<NaturezaJuridica> naturezasJuridica;
    private GrauDeRiscoDTO grauDeRisco;
    private Integer mei;


    public FiltroRelacaoLancamentoISSQN() {
        setSituacoes(new SituacaoParcela[]{SituacaoParcela.EM_ABERTO});
        setTipoRelatorio(TipoRelatorio.RESUMIDO);
        setSomenteTotalizador(Boolean.FALSE);
        this.mei = 2;
    }

    public TipoCalculoISS getTipoCalculoISS() {
        return tipoCalculoISS;
    }

    public void setTipoCalculoISS(TipoCalculoISS tipoCalculoISS) {
        this.tipoCalculoISS = tipoCalculoISS;
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

    public Long getMesReferencia() {
        return mesReferencia;
    }

    public void setMesReferencia(Long mesReferencia) {
        this.mesReferencia = mesReferencia;
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

    public Integer getMei() {
        return mei;
    }

    public void setMei(Integer mei) {
        this.mei = mei;
    }

    public List<TipoCalculoISS> getTiposCalculoISS() {
        return tiposCalculoISS;
    }

    public void setTiposCalculoISS(List<TipoCalculoISS> tiposCalculoISS) {
        this.tiposCalculoISS = tiposCalculoISS;
    }

    public List<ClassificacaoAtividade> getClassificacoesAtividade() {
        return classificacoesAtividade;
    }

    public void setClassificacoesAtividade(List<ClassificacaoAtividade> classificacoesAtividade) {
        this.classificacoesAtividade = classificacoesAtividade;
    }

    public List<NaturezaJuridica> getNaturezasJuridica() {
        return naturezasJuridica;
    }

    public void setNaturezasJuridica(List<NaturezaJuridica> naturezasJuridica) {
        this.naturezasJuridica = naturezasJuridica;
    }

    public void addTipoIss() {
        try {
            validarTipoIss();
            if (tiposCalculoISS == null) {
                tiposCalculoISS = Lists.newArrayList();
            }
            tiposCalculoISS.add(tipoCalculoISS);
            tipoCalculoISS = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void delTipoIss(TipoCalculoISS tipoCalculoISS) {
        tiposCalculoISS.remove(tipoCalculoISS);
    }

    private void validarTipoIss() {
        ValidacaoException ve = new ValidacaoException();
        if (tiposCalculoISS != null && tipoCalculoISS != null && tiposCalculoISS.contains(tipoCalculoISS)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Tipo de ISSQN já adicionado!");
        }
        ve.lancarException();
    }

    public String inTipoIss() {
        StringBuilder in = new StringBuilder();
        String juncao = " ";

        if (tiposCalculoISS != null && tiposCalculoISS.size() > 0) {
            for (TipoCalculoISS tipo : tiposCalculoISS) {
                in.append(juncao);
                in.append("'").append(tipo.name()).append("'");
                juncao = ", ";
            }
        }
        return in.toString();
    }

    public String filtroTipoIss() {
        StringBuilder selecionados = new StringBuilder();

        if (tiposCalculoISS != null && tiposCalculoISS.size() > 0) {
            for (TipoCalculoISS tipo : tiposCalculoISS) {
                selecionados.append(tipo.getDescricao());
                selecionados.append("; ");
            }
        }
        return selecionados.toString();
    }

    public void addClassificacaoAtividade() {
        try {
            validarClassificacaoAtividade();
            if (classificacoesAtividade == null) {
                classificacoesAtividade = Lists.newArrayList();
            }
            classificacoesAtividade.add(classificacaoAtividade);
            classificacaoAtividade = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void delClassificacaoAtividade(ClassificacaoAtividade classificacaoAtividade) {
        classificacoesAtividade.remove(classificacaoAtividade);
    }

    private void validarClassificacaoAtividade() {
        ValidacaoException ve = new ValidacaoException();
        if (classificacoesAtividade != null && classificacaoAtividade != null && classificacoesAtividade.contains(classificacaoAtividade)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Classificação de Atividade já adicionada!");
        }
        ve.lancarException();
    }

    public String inClassificacaoAtividade() {
        StringBuilder in = new StringBuilder();
        String juncao = " ";

        if (classificacoesAtividade != null && classificacoesAtividade.size() > 0) {
            for (ClassificacaoAtividade classi : classificacoesAtividade) {
                in.append(juncao);
                in.append("'").append(classi.name()).append("'");
                juncao = ", ";
            }
        }
        return in.toString();
    }

    public String filtroClassificacaoAtividade() {
        StringBuilder selecionados = new StringBuilder();

        if (classificacoesAtividade != null && classificacoesAtividade.size() > 0) {
            for (ClassificacaoAtividade classi : classificacoesAtividade) {
                selecionados.append(classi.getDescricao());
                selecionados.append("; ");
            }
        }
        return selecionados.toString();
    }

    public void addNaturezaJuridica() {
        try {
            validarNaturezaJuridica();
            if (naturezasJuridica == null) {
                naturezasJuridica = Lists.newArrayList();
            }
            naturezasJuridica.add(naturezaJuridica);
            naturezaJuridica = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void delNaturezaJuridica(NaturezaJuridica naturezaJuridica) {
        naturezasJuridica.remove(naturezaJuridica);
    }

    private void validarNaturezaJuridica() {
        ValidacaoException ve = new ValidacaoException();
        if (naturezasJuridica != null && naturezaJuridica != null && naturezasJuridica.contains(naturezaJuridica)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Natureza Jurídica já adicionada!");
        }
        ve.lancarException();
    }

    public String inNaturezaJuridica() {
        StringBuilder in = new StringBuilder();
        String juncao = " ";

        if (naturezasJuridica != null && naturezasJuridica.size() > 0) {
            for (NaturezaJuridica natureza : naturezasJuridica) {
                in.append(juncao);
                in.append("'").append(natureza.getId()).append("'");
                juncao = ", ";
            }
        }
        return in.toString();
    }

    public String filtroNaturezaJuridica() {
        StringBuilder selecionados = new StringBuilder();

        if (naturezasJuridica != null && naturezasJuridica.size() > 0) {
            for (NaturezaJuridica natureza : naturezasJuridica) {
                selecionados.append(natureza.getDescricao());
                selecionados.append("; ");
            }
        }
        return selecionados.toString();
    }
}
