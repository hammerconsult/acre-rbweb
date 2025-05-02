package br.com.webpublico.controle;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidadesauxiliares.DataTablePesquisaGenerico;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;
import br.com.webpublico.enums.ClassificacaoAtividade;
import br.com.webpublico.enums.SituacaoCadastralCadastroEconomico;
import br.com.webpublico.enums.TipoIssqn;
import br.com.webpublico.negocios.CadastroEconomicoFacade;
import br.com.webpublico.tributario.enumeration.GrauDeRiscoDTO;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.primefaces.event.FileUploadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

@ManagedBean
@ViewScoped
public class PesquisaCadastroEconomicoControlador extends ComponentePesquisaGenerico implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(PrettyControlador.class);

    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    private List<SituacaoCadastralCadastroEconomico> situacao;
    private TipoIssqn tipoIss;


    public PesquisaCadastroEconomicoControlador() {
    }

    @Override
    public void getCampos() {
        itens.add(new ItemPesquisaGenerica("", "", null));
        itens.add(new ItemPesquisaGenerica("obj.inscricaoCadastral", "C.M.C", String.class));
        itens.add(new ItemPesquisaGenerica("obj.pessoa", "CPF/CNPJ ou Nome/Razão Social", Pessoa.class, false, true));
        itens.add(new ItemPesquisaGenerica("cnae.codigoCnae", "Código do cnae", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("cnae.descricao", "Descrição do cnae", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("cnae.grauDeRisco", "Grau de Risco", GrauDeRiscoDTO.class, true, true));
        itens.add(new ItemPesquisaGenerica("obj.classificacaoAtividade", "Classificação da Atividade", ClassificacaoAtividade.class, true, false));
        if (situacao == null) {
            itens.add(new ItemPesquisaGenerica("situacao.situacaoCadastral", "Situação do Cadastro", SituacaoCadastralCadastroEconomico.class, true, true));
        }
        itens.add(new ItemPesquisaGenerica("(select max(per.permissaoTransporte.numero) from Permissionario per where per.cadastroEconomico = obj and coalesce(per.permissaoTransporte.finalVigencia, current_date) >= current_date)", "Número da Permissão Transporte", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("obj.pessoa", "Nome Fantasia", Pessoa.class, false, true));
        itens.add(new ItemPesquisaGenerica("situacao.situacaoCadastral", "Situação Atual", SituacaoCadastralCadastroEconomico.class, true, true));

        itensOrdenacao.add(new ItemPesquisaGenerica("", "", null));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.inscricaoCadastral", "C.M.C", String.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.pessoa", "CPF/CNPJ ou Nome/Razão Social", Pessoa.class, false, true));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.classificacaoAtividade", "Classificação da Atividade", ClassificacaoAtividade.class, true, false));
        if (situacao == null) {
            itensOrdenacao.add(new ItemPesquisaGenerica("situacao.situacaoCadastral", "Situação do Cadastro", SituacaoCadastralCadastroEconomico.class, true, true));
        }
        itensOrdenacao.add(new ItemPesquisaGenerica("situacao.situacaoCadastral", "Situação do Cadastro", SituacaoCadastralCadastroEconomico.class, true, true));

    }

    @Override
    public String getHqlContador() {
        return "select count(distinct obj.id) from  CadastroEconomico  obj ";
    }

    @Override
    public String getComplementoQuery() {
        StringBuilder hql = new StringBuilder("");
        hql.append(" left join obj.economicoCNAE cnaes ");
        hql.append(" left join cnaes.cnae cnae ");
        hql.append(" left join obj.sociedadeCadastroEconomico socio ");
        hql.append(" left join obj.situacaoCadastroEconomico situacao ");
        hql.append(" left join obj.enquadramentos enquadramento ");
        hql.append(super.getComplementoQuery());
        return hql.toString();
    }

    @Override
    public String getHqlConsulta() {
        return "select distinct new CadastroEconomico(obj.id, " +
            "obj.pessoa, " +
            "obj.inscricaoCadastral) " +
            "from CadastroEconomico obj ";
    }


    @Override
    public String montaCondicao() {
        String condicao = "";
        if (camposPesquisa != null) {
            for (DataTablePesquisaGenerico dataTablePesquisaGenerico : camposPesquisa) {
                if (validaItem(dataTablePesquisaGenerico)) {

                    String alias = "";
                    if (dataTablePesquisaGenerico.getValuePesquisa().trim().isEmpty()) {
                        String condicaoDoCampo = primeiraLetraMinuscula(dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao());

                        if (!dataTablePesquisaGenerico.getItemPesquisaGenerica().isPertenceOutraClasse() && !dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao().contains("obj.")) {
                            alias = "obj.";
                        }
                        condicao += alias + condicaoDoCampo + " is null and ";

                    } else {
                        String condicaoIsString = "";

                        String condicaoDoCampo = primeiraLetraMinuscula(dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao());
                        String valueDoCampo = dataTablePesquisaGenerico.getValuePesquisa();
                        Class<?> classe;
                        boolean isString = true;
                        boolean isValor = false;
                        boolean isDate = false;
                        try {
                            classe = (Class<?>) dataTablePesquisaGenerico.getItemPesquisaGenerica().getTipo();
                            if (!condicaoDoCampo.contains("to_number")) {
                                condicaoIsString = "lower(to_char(" + alias + condicaoDoCampo + "))";
                            } else {
                                condicaoIsString = alias + condicaoDoCampo;
                            }

                            if (classe.equals(Integer.class) || classe.equals(Long.class)) {
                                isString = false;
                                isValor = true;
                                condicaoIsString = alias + condicaoDoCampo + "";
                                valueDoCampo = "to_number(to_char('" + valueDoCampo + "'))";
                            }
                            if (classe.equals(Date.class)) {
                                isString = false;
                                isDate = true;
                                condicaoIsString = alias + condicaoDoCampo;
                                String valeuFimData = "";
                                if (dataTablePesquisaGenerico.getValeuPesquisaDataFim() != null) {
                                    if (!dataTablePesquisaGenerico.getValeuPesquisaDataFim().isEmpty()) {
                                        valeuFimData = " <= to_date('" + dataTablePesquisaGenerico.getValeuPesquisaDataFim() + "','dd/MM/yyyy')";
                                    }
                                }

                                boolean dateInicioFimPreenchido = false;
                                if (!valueDoCampo.isEmpty() && !valeuFimData.isEmpty()) {
                                    valueDoCampo = " to_date('" + valueDoCampo + "','dd/MM/yyyy')" + "-" + " to_date('" + dataTablePesquisaGenerico.getValeuPesquisaDataFim() + "','dd/MM/yyyy')";
                                    dateInicioFimPreenchido = true;
                                }
                                if (!dateInicioFimPreenchido) {
                                    if (!valueDoCampo.isEmpty()) {
                                        valueDoCampo = " >= to_date('" + valueDoCampo + "','dd/MM/yyyy')";
                                    }
                                    if (!valeuFimData.isEmpty()) {
                                        valueDoCampo = valeuFimData;
                                    }
                                }


                            }
                            if (classe.equals(BigDecimal.class) || classe.equals(Double.class)) {
                                condicaoIsString = alias + condicaoDoCampo + "";
                                isString = false;
                                isValor = true;
                            }
                            if (classe.equals(Boolean.class)) {
                                condicaoIsString = alias + condicaoDoCampo + "";
                                isString = false;
                                isValor = true;
                            }
                            if (classe.equals(Pessoa.class)) {
                                if ("Nome Fantasia".equals(dataTablePesquisaGenerico.getItemPesquisaGenerica().getLabel())) {
                                    condicao += alias + condicaoDoCampo + " in (select pj from PessoaJuridica pj where lower(pj.nomeFantasia) like '%" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase() + "%') ";
                                } else {
                                    condicao += " (" + alias + condicaoDoCampo + " in (select pf from PessoaFisica pf where lower(pf.nome) like '%" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase() + "%' or (replace(replace(replace(pf.cpf,'.',''),'-',''),'/','') like '%" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase().replace(".", "").replace("-", "").replace("/", "") + "%'))"
                                        + " or " + alias + condicaoDoCampo + " in (select pj from PessoaJuridica pj where lower(pj.razaoSocial) like '%" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase() + "%'or (replace(replace(replace(pj.cnpj,'.',''),'-',''),'/','') like '%" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase().replace(".", "").replace("-", "").replace("/", "") + "%')))";
                                }
                            } else if (dataTablePesquisaGenerico.getItemPesquisaGenerica().iseEnum()) {
                                condicao += alias + condicaoDoCampo + " = '" + valueDoCampo + "'";
                            } else if (dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao().contains("cpf") || dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao().contains("cnpj")) {
                                condicao += "(" + alias + condicaoDoCampo + " LIKE '%" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase() + "%' or (replace(replace(replace(" + alias + condicaoDoCampo + ",'.',''),'-',''),'/','')) like '%" + dataTablePesquisaGenerico.getValuePesquisa().toLowerCase().replace(".", "").replace("-", "").replace("/", "") + "%')";
                            } else {
                                condicao += processaIntervalo(condicaoIsString, valueDoCampo, isString, isValor, isDate, dataTablePesquisaGenerico.getItemPesquisaGenerica().isStringExata());
                            }
                            if (dataTablePesquisaGenerico.getItemPesquisaGenerica().getSubCondicao() != null) {
                                condicao += " and " + alias + dataTablePesquisaGenerico.getItemPesquisaGenerica().getSubCondicao();
                            }
                            condicao += " and ";
                        } catch (NullPointerException e) {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao tentar montar a condição de pesquisa!", ""));
                        }
                    }
                }
            }
        }
        condicao += " 1=1";
        return condicao;
    }

    @Override
    public void executarConsulta(String sql, String sqlCount) {
        super.executarConsulta(sql, sqlCount);
        if (Util.isNotNull(camposOrdenacao)) {
            for (ItemPesquisaGenerica itemPesquisaGenerica : camposOrdenacao) {
                if (Util.isNotNull(itemPesquisaGenerica.getCondicao()) && itemPesquisaGenerica.getCondicao().equals("obj.pessoa")) {
                    if (itemPesquisaGenerica.getTipoOrdenacao().equals("asc")) {
                        Collections.sort(lista, new OrdenaPessoaPorNomeRazaoSocialAsc());
                    } else if (itemPesquisaGenerica.getTipoOrdenacao().equals("desc")) {
                        Collections.sort(lista, new OrdenaPessoaPorNomeRazaoSocialDesc());
                    }
                }
            }
        }
        lista = Lists.newArrayList(Sets.newHashSet(lista));
        List<CadastroEconomico> cadastroEconomicos = Lists.newArrayList();
        for (Object o : lista) {
            CadastroEconomico ce = (CadastroEconomico) o;
            ce = cadastroEconomicoFacade.recuperarParaPesquisaGenerica(ce.getId());
            cadastroEconomicos.add(ce);
        }
        lista.clear();
        lista.addAll(cadastroEconomicos);
    }

    @Override
    public Integer getTotalDeRegistrosExistentes() {
        return super.getTotalDeRegistrosExistentes();
    }


    public void setSituacao(List<SituacaoCadastralCadastroEconomico> situacao) {
        this.situacao = situacao;
    }

    public void setTipoIss(TipoIssqn tipoIss) {
        this.tipoIss = tipoIss;
    }

    public class OrdenaPessoaPorNomeRazaoSocialAsc implements Comparator<CadastroEconomico> {
        @Override
        public int compare(CadastroEconomico o1, CadastroEconomico o2) {
            return o1.getPessoa().getNome().compareTo(o2.getPessoa().getNome());
        }
    }

    public class OrdenaPessoaPorNomeRazaoSocialDesc implements Comparator<CadastroEconomico> {
        @Override
        public int compare(CadastroEconomico o1, CadastroEconomico o2) {
            return o2.getPessoa().getNome().compareTo(o1.getPessoa().getNome());
        }
    }

    public void uploadArquivo(FileUploadEvent event) {
        try {
            cadastroEconomicoFacade.importarPlanilhaSequenciaNfse(event.getFile().getInputstream());
            FacesUtil.addOperacaoRealizada("Arquivo importado com sucesso!");
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Falha ao importar a planilha de sequencia de nfse. " + ex.getMessage());
            logger.error("Falha ao importar a planilha de sequencia de nfse. ", ex);
        }
    }

    public void uploadArquivoCMC(FileUploadEvent event) {
        try {
            importarPlanilhaCMC(event.getFile().getInputstream());
            FacesUtil.addOperacaoRealizada("Arquivo importado com sucesso!");
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Falha ao importar a planilha de sequencia de nfse. " + ex.getMessage());
            logger.error("Falha ao importar a planilha de sequencia de nfse. ", ex);
        }
    }

    public void importarPlanilhaCMC(InputStream inputStream) throws IOException, InvalidFormatException {
        Workbook workbook = WorkbookFactory.create(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = sheet.iterator();
        while (iterator.hasNext()) {
            try {
                Row row = iterator.next();
                if (row.getRowNum() == 0) {
                    continue;
                }
                Double cmc = row.getCell(0).getNumericCellValue();
                String cpfCnpj = row.getCell(1).getStringCellValue();
                String nome = row.getCell(2).getStringCellValue();
                String email = row.getCell(4).getStringCellValue();
                if (email.contains(",")) {
                    email = email.split(",")[0];
                }
                String regimeTributario = row.getCell(5).getStringCellValue();
                String tipoIss = row.getCell(6).getStringCellValue();
                String emitente = row.getCell(7).getStringCellValue();
                String substituto = row.getCell(8).getStringCellValue();
                String regimeEspecial = row.getCell(9).getStringCellValue();
                Date dataLiberacao = row.getCell(10).getDateCellValue();

                CadastroEconomico cadastroEconomico = cadastroEconomicoFacade.recuperaPorInscricao(String.valueOf(cmc.intValue()));
                if (cadastroEconomico != null) {
                    cadastroEconomicoFacade.ajustarDadosDaPlanilha(cpfCnpj, email, regimeTributario, emitente, tipoIss, cadastroEconomico);
                }
            } catch (Exception ex) {
                logger.error("Falha ao importar o cmc . ", ex.getMessage());
            }
        }
    }
}
