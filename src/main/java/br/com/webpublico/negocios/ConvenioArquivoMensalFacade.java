package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.ConfiguracaoContabilArquivoLayout;
import br.com.webpublico.entidades.ConvenioArquivoMensal;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.enums.TipoConvenioArquivoMensal;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.ExcelUtil;
import com.google.common.collect.Lists;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

@Stateless
public class ConvenioArquivoMensalFacade extends AbstractFacade<ConvenioArquivoMensal> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private ConfiguracaoContabilFacade configuracaoContabilFacade;

    public ConvenioArquivoMensalFacade() {
        super(ConvenioArquivoMensal.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Date getDataOperacao() {
        return sistemaFacade.getDataOperacao();
    }

    public Exercicio getExercicioCorrente() {
        return sistemaFacade.getExercicioCorrente();
    }

    @Override
    public void preSave(ConvenioArquivoMensal entidade) {
        ValidacaoException ve = new ValidacaoException();
        if (hasArquivoParaExercicioMesETipo(entidade)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um arquivo cadastrado para o exercício, mês e tipo.");
        }
        ve.lancarException();
    }

    public StreamedContent downloadArquivo(ConvenioArquivoMensal selecionado) {
        if (selecionado.getArquivo() != null) {
            return arquivoFacade.montarArquivoParaDownloadPorArquivo(selecionado.getArquivo());
        }
        return null;
    }

    public Arquivo gerarArquivo(ConvenioArquivoMensal selecionado) throws Exception {
        ExcelUtil excel = new ExcelUtil();
        ConfiguracaoContabilArquivoLayout arquivoLayout = configuracaoContabilFacade.buscarArquivoLayouPorTipo(selecionado.getTipo());
        String nomeArquivoSemExtensao = selecionado.getTipo().name() + "_" + selecionado.getMes().name().substring(0, 3) + "_" + selecionado.getExercicio().getAno();
        if (arquivoLayout != null) {
            excel.gerarExcelUtilizandoArquivoLayout(
                buscarObjetosPorTipo(selecionado),
                arquivoLayout.getArquivo(),
                arquivoLayout.getNumeroAba() - 1,
                arquivoLayout.getNumeroLinhaInicial() - 1,
                arquivoLayout.getAjustarTamanhoColuna(),
                nomeArquivoSemExtensao
            );
        } else {
            excel.gerarExcelXLSX(
                selecionado.getTipo().getDescricao(),
                "",
                buscarTitulosPorTipo(selecionado),
                buscarObjetosPorTipo(selecionado),
                null,
                false
            );
        }
        return arquivoFacade.novoArquivoMemoria(montarArquivo(excel, nomeArquivoSemExtensao));
    }

    private Arquivo montarArquivo(ExcelUtil excel, String nomeArquivoSemExtensao) throws FileNotFoundException {
        InputStream inputStream = new FileInputStream(excel.getFile());
        boolean isXlsx = excel.getFile().getName().trim().toLowerCase().contains(ExcelUtil.XLSX_EXTENCAO);
        Arquivo arquivo = new Arquivo();
        arquivo.setNome(excel.getFile().getName());
        arquivo.setMimeType(isXlsx ? ExcelUtil.XLSX_CONTENTTYPE : ExcelUtil.XLS_CONTENTTYPE);
        arquivo.setTamanho(excel.getFile().length());
        arquivo.setInputStream(inputStream);
        arquivo.setNome(nomeArquivoSemExtensao + (isXlsx ? ExcelUtil.XLSX_EXTENCAO : ExcelUtil.XLS_EXTENCAO));
        return arquivo;
    }

    private List<String> buscarTitulosPorTipo(ConvenioArquivoMensal selecionado) {
        List<String> titulos = Lists.newArrayList();
        if (TipoConvenioArquivoMensal.CONVENIO_RECEITA.equals(selecionado.getTipo())) {
            titulos.add("Nº");
            titulos.add("Convênio/Contrato");
            titulos.add("Fonte de Recursos");
            titulos.add("Objeto");
            titulos.add("Concedente");
            titulos.add("Vigência");
            titulos.add("Repasse");
            titulos.add("Contrapartida");
            titulos.add("Total");
            titulos.add("Valor do desembolso recebido (R$)");
            titulos.add("Órgão Executor");
        }
        if (TipoConvenioArquivoMensal.CONVENIO_DESPESA.equals(selecionado.getTipo())) {
            titulos.add("Nº");
            titulos.add("Termo");
            titulos.add("Objeto");
            titulos.add("Convenente");
            titulos.add("CNPJ Convenente");
            titulos.add("Vigência");
            titulos.add("Repasse");
            titulos.add("Contrapartida");
            titulos.add("Aditivo");
            titulos.add("Total");
            titulos.add("Valor Desembolsado");
            titulos.add("Órgão Gestor");
        }
        return titulos;
    }

    private List<Object[]> buscarObjetosPorTipo(ConvenioArquivoMensal selecionado) {
        if (TipoConvenioArquivoMensal.CONVENIO_RECEITA.equals(selecionado.getTipo()))
            return buscarConveniosReceitas(selecionado);
        if (TipoConvenioArquivoMensal.CONVENIO_DESPESA.equals(selecionado.getTipo()))
            return buscarConveniosDespesas(selecionado);
        return Lists.newArrayList();
    }

    private List<Object[]> buscarConveniosDespesas(ConvenioArquivoMensal selecionado) {
        String sql = " select cast(row_number() over (order by substr(trim(cd.numero), length(trim(cd.numero)) - 3, length(trim(cd.numero))), " +
            "    to_number(regexp_replace(cd.numero, '[^0-9]', ''))) as varchar(5)) as numero," +
            "       trim(cd.numero)                                  as termo, " +
            "       cd.nomeprojeto                                   as objeto, " +
            "       pj.razaosocial                                   as convenente, " +
            "       FORMATACPFCNPJ(pj.cnpj)                          as cnpjConvenente, " +
            "       to_char(cd.dataVigenciaFinal, 'dd/MM/yyyy')      as vigencia, " +
            "       'R$ ' || to_char(cd.valorRepasse, 'FM999G999G999G999G990D90') as repasse, " +
            "       'R$ ' || to_char(cd.valorContrapartida, 'FM999G999G999G999G990D90') as contrapartida, " +
            "       'R$ ' || to_char(cd.valorConvenioAditivado, 'FM999G999G999G999G990D90') as aditivo, " +
            "       'R$ ' || to_char((coalesce(cd.valorRepasse, 0) + coalesce(cd.valorContrapartida, 0) + coalesce(cd.valorConvenioAditivado, 0)), 'FM999G999G999G999G990D90') as total, " +
            "       'R$ ' || to_char(sum(coalesce(cdl.valorliberadoconcedente, 0)), 'FM999G999G999G999G990D90') as valorDesembolsado, " +
            "       ent.nome                                         as orgaoGestor " +
            " from conveniodespesa cd " +
            "         inner join entidadeBeneficiaria eb on eb.id = cd.entidadebeneficiaria_id " +
            "         inner join pessoaJuridica pj on pj.id = eb.pessoajuridica_id " +
            "         inner join entidade ent on ent.id = cd.orgaoconvenente_id " +
            "         left join convenioDespesaLiberacao cdl on cd.id = cdl.conveniodespesa_id " +
            " where trunc(cd.datavigenciainicial) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataFinal, 'dd/MM/yyyy') " +
            " group by trim(cd.numero), cd.nomeprojeto, pj.razaosocial, pj.cnpj, cd.dataVigenciaFinal, cd.valorRepasse, " +
            "         cd.valorContrapartida, cd.valorConvenioAditivado, ent.nome, to_number(regexp_replace(cd.numero, '[^0-9]', '')) " +
            " order by substr(trim(cd.numero), length(trim(cd.numero)) - 3, length(trim(cd.numero))), " +
            "         to_number(regexp_replace(cd.numero, '[^0-9]', ''))";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataInicial", DataUtil.montarDataFormatada(1, selecionado.getMes().getNumeroMes() - 1, selecionado.getExercicio().getAno()));
        q.setParameter("dataFinal", DataUtil.montarDataFormatada(DataUtil.ultimoDiaDoMes(selecionado.getMes().getNumeroMes()), selecionado.getMes().getNumeroMes() - 1, selecionado.getExercicio().getAno()));
        return q.getResultList();
    }

    private List<Object[]> buscarConveniosReceitas(ConvenioArquivoMensal selecionado) {
        String sql = " select cast(row_number() over (order by to_number(regexp_replace(cr.numero, '[^0-9]', ''))) as varchar(5)) as numero, " +
            "       trim(cr.numero)                                                   as termo, " +
            "       cast(rtrim(xmlagg(xmlelement(e, fonte.codigofonte, ', ').extract('//text()') order by fonte.codigofonte).getclobval(), ', ') as varchar(255)) as fontes, " +
            "       cr.nomeconvenio                                                   as objeto, " +
            "       pj.razaosocial                                                    as concedente, " +
            "       to_char(cr.vigenciafinal, 'dd/MM/yyyy')                           as vigencia, " +
            "       'R$ ' || to_char(cr.valorrepasse, 'FM999G999G999G999G990D90')             as repasse, " +
            "       'R$ ' || to_char(cr.valorcontrapartida, 'FM999G999G999G999G990D90')       as contrapartida, " +
            "       'R$ ' || to_char((coalesce(cr.valorrepasse, 0) + coalesce(cr.valorcontrapartida, 0)), 'FM999G999G999G999G990D90') as total, " +
            "       'R$ ' || to_char((select sum(coalesce(crl.VALORLIBERADOCONCEDENTE, 0)) as totalConcedente " +
            "                         from convenioreceitaliberacao crl where crl.CONVENIORECEITA_ID = cr.id), 'FM999G999G999G999G990D90') as valorLiberado, " +
            "       ent.nome                                                          as orgaoGestor " +
            " from convenioreceita cr " +
            "         inner join entidaderepassadora er on er.id = cr.entidaderepassadora_id " +
            "         inner join pessoajuridica pj on pj.id = er.pessoajuridica_id " +
            "         inner join entidade ent on ent.id = cr.ENTIDADECONVENENTE_ID " +
            "         inner join (select distinct conta.codigo as codigofonte, con.id as idconvenio " +
            "                     from convenioreceita con " +
            "                              inner join conveniorecconta conrc on conrc.convenioreceita_id = con.id " +
            "                              inner join conta on conrc.conta_id = conta.id) fonte on fonte.idconvenio = cr.id " +
            " where trunc(cr.vigenciainicial) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataFinal, 'dd/MM/yyyy') " +
            " group by to_number(regexp_replace(cr.numero, '[^0-9]', '')), trim(cr.numero), cr.nomeconvenio, pj.razaosocial, pj.cnpj, " +
            "         cr.vigenciafinal, cr.valorrepasse, cr.valorcontrapartida, cr.id, ent.nome " +
            " order by to_number(regexp_replace(cr.numero, '[^0-9]', '')) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataInicial", DataUtil.montarDataFormatada(1, selecionado.getMes().getNumeroMes() - 1, selecionado.getExercicio().getAno()));
        q.setParameter("dataFinal", DataUtil.montarDataFormatada(DataUtil.ultimoDiaDoMes(selecionado.getMes().getNumeroMes()), selecionado.getMes().getNumeroMes() - 1, selecionado.getExercicio().getAno()));
        return q.getResultList();
    }

    private boolean hasArquivoParaExercicioMesETipo(ConvenioArquivoMensal selecionado) {
        String sql = " select * from convenioarquivomensal where exercicio_id = :idExercicio and mes = :mesArquivo and tipo = :tipoArquivo ";
        if (selecionado.getId() != null) {
            sql += " and id <> :idArquivo ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("idExercicio", selecionado.getExercicio().getId());
        q.setParameter("mesArquivo", selecionado.getMes().name());
        q.setParameter("tipoArquivo", selecionado.getTipo().name());
        if (selecionado.getId() != null) {
            q.setParameter("idArquivo", selecionado.getId());
        }
        return !q.getResultList().isEmpty();
    }
}
