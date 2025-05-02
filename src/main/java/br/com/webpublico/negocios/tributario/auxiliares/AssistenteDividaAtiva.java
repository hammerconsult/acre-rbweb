package br.com.webpublico.negocios.tributario.auxiliares;

import br.com.webpublico.entidadesauxiliares.OrdenaResultadoParcelaPorVencimento;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.negocios.tributario.dao.JdbcDividaAtivaDAO;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.DetailProcessAsync;
import com.google.common.collect.Lists;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AssistenteDividaAtiva implements DetailProcessAsync {

    final String sql = "select distinct pvd.id as pvd_id, " +
        " vd.id as vd_id, " +
        " calc.cadastro_id as cadastro_id, " +
        " div.id as divida_id, " +
        " pes.id as pessoa_id, " +
        " pvd.opcaopagamento_id as opcao_id, " +
        " pvd.vencimento, " +
        " calc.tipoCalculo, " +
        " calc.id, " +
        " pvd.sequenciaParcela, " +
        " div.descricao, " +
        " spvd.referencia, " +
        " ex.ano ";

    final String from = " from parcelavalordivida pvd " +
        "inner join opcaopagamento op on op.id = pvd.opcaopagamento_id " +
        "inner join valordivida vd on vd.id = pvd.VALORDIVIDA_ID " +
        "inner join divida div on div.id = vd.DIVIDA_ID " +
        "inner join exercicio ex on ex.id = vd.EXERCICIO_ID " +
        "inner join SITUACAOPARCELAVALORDIVIDA spvd on spvd.id = pvd.situacaoAtual_id " +
        "inner join calculo calc on calc.id = vd.calculo_id " +
        "inner join calculopessoa cp on cp.calculo_id = calc.id " +
        "inner join pessoa pes on pes.id = cp.pessoa_id " +
        "left join pessoafisica pf on pf.id = pes.id " +
        "left join pessoajuridica pj on pj.id = pes.id ";
    JdbcDividaAtivaDAO dao;
    private List<ResultadoParcela> parcelas;
    private TipoCadastroTributario tipoCadastroTributario;
    private boolean consultando;
    private boolean inscrevendo;
    private boolean finalizouComErro;
    private boolean gerando;
    private Integer total;
    private Integer inscritos;
    private Integer totalGerar;
    private Integer gerados;
    private boolean podeGerarLivro;
    private boolean acabouTudo;
    private StringBuilder join;
    private StringBuilder where;
    private StringBuilder camposAdicionais;
    private String mensagemDeErro;
    private String usuario;
    private String descricao;

    public AssistenteDividaAtiva() {
        parcelas = new ArrayList<>();
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        dao = (JdbcDividaAtivaDAO) ap.getBean("dividaAtivaDAO");
        acabouTudo = false;

    }

    public List<ResultadoParcela> getParcelas() {
        Collections.sort(parcelas, new OrdenaResultadoParcelaPorVencimento());
        return parcelas;
    }

    public boolean isConsultando() {
        return consultando;
    }

    public boolean isGerando() {
        return gerando;
    }

    public synchronized void iniciaConsulta() {
        parcelas = new ArrayList<>();
        consultando = true;
        podeGerarLivro = false;
        join = new StringBuilder("");
        camposAdicionais = new StringBuilder("");
        where = new StringBuilder(" where spvd.SITUACAOPARCELA = :situacaoAberto and COALESCE(op.promocional,0) = 0 ");
    }

    public synchronized void finalizaConsulta(List<ResultadoParcela> parcelas) {
        this.consultando = false;
        this.parcelas = parcelas;
    }

    public void inicializaIncricao() {
        inscrevendo = true;
        inscritos = 0;
        total = parcelas.size();
    }

    public void finalizaInscricao() {
        inscrevendo = false;
        podeGerarLivro = true;
    }

    public void finalizarInscricaoComErro(String mensagem) {
        inscrevendo = false;
        podeGerarLivro = false;
        finalizouComErro = true;
        mensagemDeErro = mensagem;
    }

    public void inicializaGeracaoLivro(Integer total) {
        gerando = true;
        gerados = 0;
        totalGerar = total;
    }

    public void finalizaGeracaoLivro() {
        gerando = false;
        acabouTudo = true;
    }

    @Override
    public String getUsuario() {
        return usuario;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public Integer getTotal() {
        return total;
    }

    @Override
    public Double getPorcentagemExecucao() {
        Double porcentagem = getPorcentagemDaInscricao();
        DecimalFormat formato = new DecimalFormat("#.##");
        porcentagem = Double.parseDouble(formato.format(porcentagem));
        return porcentagem;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getInscritos() {
        return inscritos;
    }

    public synchronized void conta() {
        inscritos++;
    }

    public boolean isInscrevendo() {
        return inscrevendo;
    }

    public Double getPorcentagemDaInscricao() {
        if (inscritos == null || total == null) {
            return 0d;
        }
        return (inscritos.doubleValue() / total.doubleValue()) * 100;
    }

    public boolean isPodeGerarLivro() {
        return podeGerarLivro;
    }

    public void inicializaSingleton() {
        parcelas = Lists.newArrayList();
        consultando = false;
        inscrevendo = false;
        podeGerarLivro = false;
        gerando = false;
        total = 0;
        inscritos = 0;
        gerados = 0;
        totalGerar = 0;
        acabouTudo = false;
        finalizouComErro = false;
    }

    public Integer getGerados() {
        return gerados;
    }

    public Integer getTotalGerar() {
        return totalGerar;
    }

    public synchronized void contaGeracao() {
        gerados++;
    }

    public Double getPorcentagemDaGeracaoLivro() {
        if (gerados == null || totalGerar == null) {
            return 0d;
        }
        return (gerados.doubleValue() / totalGerar.doubleValue()) * 100;
    }

    public boolean isAcabouTudo() {
        return acabouTudo;
    }

    public void addJoin(String join) {
        this.join.append(" ").append(join);
    }

    public void addCamposAdicionadis(String campo) {
        this.camposAdicionais.append(", ").append(campo);
    }

    public void addWhere(String where) {
        this.where.append(" ").append(where);
    }

    public String getSQL() {
        return sql + camposAdicionais.toString() + from + join.toString() + where.toString();
    }

    public String getMensagemDeErro() {
        return mensagemDeErro;
    }

    public void setMensagemDeErro(String mensagemDeErro) {
        this.mensagemDeErro = mensagemDeErro;
    }

    public boolean isFinalizouComErro() {
        return finalizouComErro;
    }

    public void setFinalizouComErro(boolean finalizouComErro) {
        this.finalizouComErro = finalizouComErro;
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }
}
