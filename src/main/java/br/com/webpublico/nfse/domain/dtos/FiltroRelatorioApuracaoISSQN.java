package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.Servico;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.util.Strings;

import java.util.List;

public class FiltroRelatorioApuracaoISSQN {

    private Mes mesInicial;
    private Exercicio exercicioInicial;
    private Mes mesFinal;
    private Exercicio exercicioFinal;
    private CadastroEconomico cadastroEconomico;
    private List<CadastroEconomico> cadastrosEconomicos;
    private String cnpjInicial;
    private String cnpjFinal;
    private Servico servico;
    private List<Servico> servicos;

    public FiltroRelatorioApuracaoISSQN() {
        servicos = Lists.newArrayList();
        cadastrosEconomicos = Lists.newArrayList();
    }

    public String montarDescricaoFiltros() {
        StringBuilder retorno = new StringBuilder();
        retorno.append(" Competência Inicial: ").append(mesInicial.getNumeroMes()).append("/").append(exercicioInicial.getAno());
        retorno.append(" Competência Final: ").append(mesFinal.getNumeroMes()).append("/").append(exercicioFinal.getAno());
        if (cadastrosEconomicos != null && !cadastrosEconomicos.isEmpty()) {
            retorno.append(" Cadastro(s) Econômico(s): ").append(StringUtils.join(getInscricoesCadastrosEconomicos(), ", ")).append("; ");
        }
        if (Strings.isNotEmpty(cnpjInicial)) {
            retorno.append(" CNPJ Inicial: ").append(cnpjInicial).append("; ");
        }
        if (Strings.isNotEmpty(cnpjFinal)) {
            retorno.append(" CNPJ Final: ").append(cnpjFinal).append("; ");
        }
        if (servicos != null && !servicos.isEmpty()) {
            retorno.append(" Serviço(s): ").append(StringUtils.join(getCodigosServicos(), ",")).append("; ");
        }
        return retorno.toString();
    }

    public String montarClausulaWhere() {
        StringBuilder sql = new StringBuilder();
        sql.append(" where (extract(year from competencia) >= ").append(exercicioInicial.getAno()).append(" and extract(month from competencia) >= ").append(mesInicial.getNumeroMes()).append(" )");
        sql.append(" and (extract(year from competencia) <= ").append(exercicioFinal.getAno()).append(" and extract(month from competencia) <= ").append(mesFinal.getNumeroMes()).append(" )");
        if (cadastrosEconomicos != null && !cadastrosEconomicos.isEmpty()) {
            for(CadastroEconomico cmc : cadastrosEconomicos) {
                sql.append(" and idCadastroEconomico in (").append(cmc.getId());
                if(!(cadastrosEconomicos.get(cadastrosEconomicos.size()-1) == cmc)) {
                    sql.append(", ");
                }
                else {
                    sql.append(") ");
                }
            }
        }
        if (Strings.isNotEmpty(cnpjInicial)) {
            sql.append(" and cnpjPrestador >= ").append(StringUtil.retornaApenasNumeros(cnpjInicial));
        }
        if (Strings.isNotEmpty(cnpjFinal)) {
            sql.append(" and cnpjPrestador <= ").append(StringUtil.retornaApenasNumeros(cnpjFinal));
        }
        if (servicos != null && !servicos.isEmpty()) {
            for(Servico servico : servicos) {
                sql.append(" and idServico in (").append(servico.getId());
                if(!(servicos.get(servicos.size()-1) == servico)) {
                    sql.append(", ");
                }
                else {
                    sql.append(") ");
                }
            }
        }
        return sql.toString();
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public List<CadastroEconomico> getCadastrosEconomicos() {
        return cadastrosEconomicos;
    }

    public void setCadastrosEconomicos(List<CadastroEconomico> cadastrosEconomicos) {
        this.cadastrosEconomicos = cadastrosEconomicos;
    }

    public String getCnpjInicial() {
        return cnpjInicial;
    }

    public void setCnpjInicial(String cnpjInicial) {
        this.cnpjInicial = cnpjInicial;
    }

    public String getCnpjFinal() {
        return cnpjFinal;
    }

    public void setCnpjFinal(String cnpjFinal) {
        this.cnpjFinal = cnpjFinal;
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }

    public List<Servico> getServicos() {
        return servicos;
    }

    public void setServicos(List<Servico> servicos) {
        this.servicos = servicos;
    }

    public Mes getMesInicial() {
        return mesInicial;
    }

    public void setMesInicial(Mes mesInicial) {
        this.mesInicial = mesInicial;
    }

    public Exercicio getExercicioInicial() {
        return exercicioInicial;
    }

    public void setExercicioInicial(Exercicio exercicioInicial) {
        this.exercicioInicial = exercicioInicial;
    }

    public Mes getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(Mes mesFinal) {
        this.mesFinal = mesFinal;
    }

    public Exercicio getExercicioFinal() {
        return exercicioFinal;
    }

    public void setExercicioFinal(Exercicio exercicioFinal) {
        this.exercicioFinal = exercicioFinal;
    }

    public void addServico() {
        Util.adicionarObjetoEmLista(servicos, servico);
        servico = null;
    }

    public void removeServico(Servico servico) {
        servicos.remove(servico);
    }


    public List<Long> getIdsServicos() {
        List<Long> ids = Lists.newArrayList();
        for (Servico s : servicos) {
            ids.add(s.getId());
        }
        return ids;
    }

    public List<String> getCodigosServicos() {
        List<String> codigos = Lists.newArrayList();
        for (Servico s : servicos) {
            codigos.add(s.getCodigo());
        }
        return codigos;
    }

    public void addCadastro() {
        Util.adicionarObjetoEmLista(cadastrosEconomicos, cadastroEconomico);
        cadastroEconomico = null;
    }

    public void removeCadastro(CadastroEconomico ce) {
        cadastrosEconomicos.remove(ce);
    }

    public List<Long> getIdsCadastrosEconomicos() {
        List<Long> ids = Lists.newArrayList();
        for (CadastroEconomico ce : cadastrosEconomicos) {
            ids.add(ce.getId());
        }
        return ids;
    }

    public List<String> getInscricoesCadastrosEconomicos() {
        List<String> inscricoes = Lists.newArrayList();
        for (CadastroEconomico ce : cadastrosEconomicos) {
            inscricoes.add(ce.getInscricaoCadastral());
        }
        return inscricoes;
    }

    public void validarCamposObrigatorios() {
        ValidacaoException ve = new ValidacaoException();
        if (exercicioInicial == null || mesInicial == null || exercicioFinal == null || mesFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Período deve ser informado.");
        }
        ve.lancarException();
    }
}
