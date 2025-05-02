package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.DetalhadoResumido;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Criado por Mateus
 * Data: 09/03/2017.
 */
public abstract class RelatorioQDDSuperControlador extends RelatorioContabilSuperControlador {

    protected Funcao funcao;
    protected SubFuncao subFuncao;
    protected SubAcaoPPA subAcaoPPA;
    protected List<SubAcaoPPA> subAcoes;
    protected DetalhadoResumido detalhadoResumido;
    protected Boolean detalharConta = Boolean.TRUE;

    public void limparCampos() {
        super.limparCamposGeral();
        this.funcao = null;
        this.subFuncao = null;
        this.subAcaoPPA = null;
        this.subAcoes = Lists.newArrayList();
        this.detalhadoResumido = DetalhadoResumido.DETALHADO;
        this.detalharConta = Boolean.TRUE;
    }

    public List<SelectItem> getTiposRelatorio() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (DetalhadoResumido dr : DetalhadoResumido.values()) {
            toReturn.add(new SelectItem(dr, dr.getDescricao()));
        }
        return toReturn;
    }

    public Exercicio buscarExercicioPelaDataReferencia() {
        return getExercicioFacade().recuperarExercicioPeloAno(DataUtil.getAno(dataReferencia));
    }

    public List<ParametrosRelatorios> adicionarParametrosGerais(List<ParametrosRelatorios> parametros) {
        parametros.add(new ParametrosRelatorios(null, ":DATAREFERENCIA", null, OperacaoRelatorio.IGUAL, getDataReferenciaFormatada(), null, 0, true));
        parametros.add(new ParametrosRelatorios(null, ":ID_EXERCICIO", null, OperacaoRelatorio.IGUAL, buscarExercicioPelaDataReferencia().getId(), null, 0, false));
        if (subAcaoPPA != null) {
            parametros.add(new ParametrosRelatorios(" sb.id ", ":idSubAcao", null, OperacaoRelatorio.IGUAL, subAcaoPPA.getId(), null, 1, false));
        }
        if (conta != null) {
            parametros.add(new ParametrosRelatorios(" c.codigo ", ":codigo", null, OperacaoRelatorio.LIKE, conta.getCodigoSemZerosAoFinal() + "%", null, 1, false));
            if (!detalharConta)
                parametros.add(new ParametrosRelatorios(" conta ", ":conta", null, OperacaoRelatorio.LIKE, conta.getCodigo() + "%", null, 4, false));
        }
        if (funcao != null) {
            parametros.add(new ParametrosRelatorios(" func.codigo ", ":codigoFuncao", null, OperacaoRelatorio.IGUAL, funcao.getCodigo(), null, 1, false));
        }
        if (subFuncao != null) {
            parametros.add(new ParametrosRelatorios(" sub.codigo ", ":codigoSubfuncao", null, OperacaoRelatorio.IGUAL, subFuncao.getCodigo(), null, 1, false));
        }
        if (fonteDeRecursos != null) {
            parametros.add(new ParametrosRelatorios(" fte.id ", ":idFonte", null, OperacaoRelatorio.IGUAL, fonteDeRecursos.getId(), null, 2, false));
        }
        adicionarFontes(parametros, " fte.codigo ", ":codigosFontes", 2);
        if (subAcoes != null && !subAcoes.isEmpty()) {
            parametros.add(montarParametroEFiltrosSubAcoes());
        }
        return parametros;
    }

    private ParametrosRelatorios montarParametroEFiltrosSubAcoes() {
        List<Long> idsSubAcoes = Lists.newArrayList();
        String juncao = "";
        filtros += " SubProjeto/Atividade: ";
        for (SubAcaoPPA subAcao : subAcoes) {
            idsSubAcoes.add(subAcao.getId());
            filtros += juncao + subAcao.getCodigo();
            juncao = ", ";
        }
        filtros += " -";
        return new ParametrosRelatorios(" sb.id ", ":idsSubAcoes", null, OperacaoRelatorio.IN, idsSubAcoes, null, 1, false);
    }

    public Funcao getFuncao() {
        return funcao;
    }

    public void setFuncao(Funcao funcao) {
        this.funcao = funcao;
    }

    public SubFuncao getSubFuncao() {
        return subFuncao;
    }

    public void setSubFuncao(SubFuncao subFuncao) {
        this.subFuncao = subFuncao;
    }

    public SubAcaoPPA getSubAcaoPPA() {
        return subAcaoPPA;
    }

    public void setSubAcaoPPA(SubAcaoPPA subAcaoPPA) {
        this.subAcaoPPA = subAcaoPPA;
    }

    public List<SubAcaoPPA> getSubAcoes() {
        return subAcoes;
    }

    public void setSubAcoes(List<SubAcaoPPA> subAcoes) {
        this.subAcoes = subAcoes;
    }

    public DetalhadoResumido getDetalhadoResumido() {
        return detalhadoResumido;
    }

    public void setDetalhadoResumido(DetalhadoResumido detalhadoResumido) {
        this.detalhadoResumido = detalhadoResumido;
    }

    public Boolean getDetalharConta() {
        return detalharConta;
    }

    public void setDetalharConta(Boolean detalharConta) {
        this.detalharConta = detalharConta;
    }
}
