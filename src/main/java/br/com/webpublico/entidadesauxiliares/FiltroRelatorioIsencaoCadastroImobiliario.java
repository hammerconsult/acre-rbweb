package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.tributario.TipoCategoriaIsencaoIPTU;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.tributario.AgrupamentoRelatorioDTO;
import br.com.webpublico.webreportdto.dto.tributario.OrigemIsencaoIPTU;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Wellington Abdo on 24/08/2016.
 */
public class FiltroRelatorioIsencaoCadastroImobiliario implements Serializable {

    //    Filtros Imóvel
    private Exercicio exercicioInicial;
    private Exercicio exercicioFinal;
    private String inscricaoInicial;
    private String inscricaoFinal;
    private String setorInicial;
    private String setorFinal;
    private ValorPossivel valorPossivel;
    private List<ValorPossivel> valoresPatrimonio = Lists.newArrayList();

    //Origem Isenção
    private OrigemIsencaoIPTU origemIsencao;

    //Filtros de Isenção
    private ProcessoIsencaoIPTU processo;
    private TipoCategoriaIsencaoIPTU tipoCategoriaIsencaoIPTU;
    private CategoriaIsencaoIPTU categoria;
    private TipoEfetivacao tipoEfetivacao;

    private List<UsuarioSistema> usuarios;
    private UsuarioSistema usuarioSistema;
    private AgrupamentoRelatorioDTO agrupamentoRelatorio;
    private Boolean detalhado;
    private String criteriosUtilizados;
    private Map<String, Map<String, String>> criteriosUtilizadosExcel;

    public Exercicio getExercicioInicial() {
        return exercicioInicial;
    }

    public void setExercicioInicial(Exercicio exercicioInicial) {
        this.exercicioInicial = exercicioInicial;
    }

    public Exercicio getExercicioFinal() {
        return exercicioFinal;
    }

    public void setExercicioFinal(Exercicio exercicioFinal) {
        this.exercicioFinal = exercicioFinal;
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

    public String getSetorInicial() {
        return setorInicial;
    }

    public void setSetorInicial(String setorInicial) {
        this.setorInicial = setorInicial;
    }

    public String getSetorFinal() {
        return setorFinal;
    }

    public void setSetorFinal(String setorFinal) {
        this.setorFinal = setorFinal;
    }

    public OrigemIsencaoIPTU getOrigemIsencao() {
        return origemIsencao;
    }

    public void setOrigemIsencao(OrigemIsencaoIPTU origemIsencao) {
        this.origemIsencao = origemIsencao;
    }

    public ProcessoIsencaoIPTU getProcesso() {
        return processo;
    }

    public void setProcesso(ProcessoIsencaoIPTU processo) {
        this.processo = processo;
    }

    public TipoCategoriaIsencaoIPTU getTipoCategoriaIsencaoIPTU() {
        return tipoCategoriaIsencaoIPTU;
    }

    public void setTipoCategoriaIsencaoIPTU(TipoCategoriaIsencaoIPTU tipoCategoriaIsencaoIPTU) {
        this.tipoCategoriaIsencaoIPTU = tipoCategoriaIsencaoIPTU;
    }

    public CategoriaIsencaoIPTU getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaIsencaoIPTU categoria) {
        this.categoria = categoria;
    }

    public TipoEfetivacao getTipoEfetivacao() {
        return tipoEfetivacao;
    }

    public void setTipoEfetivacao(TipoEfetivacao tipoEfetivacao) {
        this.tipoEfetivacao = tipoEfetivacao;
    }

    public List<UsuarioSistema> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<UsuarioSistema> usuarios) {
        this.usuarios = usuarios;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public String getCriteriosUtilizados() {
        return criteriosUtilizados;
    }

    public void setCriteriosUtilizados(String criteriosUtilizados) {
        this.criteriosUtilizados = criteriosUtilizados;
    }

    public AgrupamentoRelatorioDTO getAgrupamentoRelatorio() {
        return agrupamentoRelatorio;
    }

    public void setAgrupamentoRelatorio(AgrupamentoRelatorioDTO agrupamentoRelatorio) {
        this.agrupamentoRelatorio = agrupamentoRelatorio;
    }

    public Boolean getDetalhado() {
        return detalhado;
    }

    public void setDetalhado(Boolean detalhado) {
        this.detalhado = detalhado;
    }

    public ValorPossivel getValorPossivel() {
        return valorPossivel;
    }

    public void setValorPossivel(ValorPossivel valorPossivel) {
        this.valorPossivel = valorPossivel;
    }

    public List<ValorPossivel> getValoresPatrimonio() {
        return valoresPatrimonio;
    }

    public void setValoresPatrimonio(List<ValorPossivel> valoresPatrimonio) {
        this.valoresPatrimonio = valoresPatrimonio;
    }

    public void addValorPatrimonio() {
        if (valoresPatrimonio.contains(valorPossivel)) return;
        valoresPatrimonio.add(valorPossivel);
        valorPossivel = null;
    }

    public void removeValorPatrimonio(ValorPossivel remover) {
        valoresPatrimonio.remove(remover);
    }

    public Map<String, Map<String, String>> getCriteriosUtilizadosExcel() {
        return criteriosUtilizadosExcel;
    }

    public void setCriteriosUtilizadosExcel(Map<String, Map<String, String>> criteriosUtilizadosExcel) {
        this.criteriosUtilizadosExcel = criteriosUtilizadosExcel;
    }

    public void inicializarFiltros(Exercicio exercicio) {
        exercicioInicial = exercicio;
        exercicioFinal = exercicio;
        inscricaoInicial = "100000000000000";
        inscricaoFinal = "999999999999999";
        setorInicial = "";
        setorFinal = "";
        origemIsencao = OrigemIsencaoIPTU.TODOS;
        processo = null;
        tipoCategoriaIsencaoIPTU = null;
        categoria = null;
        tipoEfetivacao = TipoEfetivacao.TODOS;
        agrupamentoRelatorio = AgrupamentoRelatorioDTO.EXERCICIO;
        usuarios = new ArrayList<>();
        detalhado = false;
    }

    public void validarFiltros() {
        ValidacaoException ve = new ValidacaoException();
        if (agrupamentoRelatorio == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo 'Agrupar Por' deve ser informado.");
        }
        if (exercicioInicial != null && exercicioInicial.getAno() != null && exercicioFinal != null && exercicioFinal.getAno() != null &&
            exercicioInicial.getAno().compareTo(exercicioFinal.getAno()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Exercício Inicial deve ser menor ou igual que o Exercício Final!");
        }
        ve.lancarException();
    }

    public String getLoginUsuariosSelecionados() {
        StringBuilder in = new StringBuilder();
        String juncao = "";
        if (usuarios != null && usuarios.isEmpty()) {
            for (UsuarioSistema usuario : usuarios) {
                in.append(juncao);
                in.append(usuario.getLogin());
                juncao = ", ";
            }
        }
        return in.toString();
    }

    public void adicionarUsuario() {
        if (usuarios == null) {
            usuarios = new ArrayList();
        }
        if (usuarioSistema == null) {
            FacesUtil.addOperacaoNaoPermitida("Selecione um usuário.");
            return;
        }
        if (usuarios.contains(usuarioSistema)) {
            FacesUtil.addOperacaoNaoPermitida("Usuário já adicionado.");
            return;
        }
        usuarios.add(usuarioSistema);
        usuarioSistema = null;
    }

    public void removerUsuario(UsuarioSistema usuarioSistema) {
        usuarios.remove(usuarioSistema);
    }

    public boolean habilitarFiltrosIsencao() {
        return OrigemIsencaoIPTU.TODOS.equals(origemIsencao) || OrigemIsencaoIPTU.PROCESSO_ISENCAO.equals(origemIsencao);
    }

    public enum TipoEfetivacao {
        TODOS("Todos", "Processos Efetivados e não Efetivados"),
        EFETIVADOS("Somente Efetivados", "Somente Processos Efetivados"),
        NAO_EFETIVADOS("Somente não Efetivados", "Somente Processos não Efetivados");

        private String descricao;
        private String descricaoFiltro;

        TipoEfetivacao(String descricao, String descricaoFiltro) {
            this.descricao = descricao;
            this.descricaoFiltro = descricaoFiltro;
        }

        public String getDescricao() {
            return descricao;
        }

        public String getDescricaoFiltro() {
            return descricaoFiltro;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }
}
