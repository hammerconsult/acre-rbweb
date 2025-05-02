package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ArquivoPermissionarioConcessionario;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.ItemPermissionarioConcessionario;
import br.com.webpublico.enums.SituacaoContratoCEASA;
import br.com.webpublico.enums.SituacaoContratoRendasPatrimoniais;
import br.com.webpublico.enums.TipoPermissaoRBTrans;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * Created by Desenvolvimento on 23/08/2016.
 */
@Stateless
public class ArquivoPermissionarioConcessionarioFacade extends AbstractFacade<ArquivoPermissionarioConcessionario> {

    private static String NOME_ARQUIVO = "arquivo_permissionario_concessionario.txt";

    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private static final String LINE_SEPARATOR = "\n";

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ArquivoPermissionarioConcessionarioFacade() {
        super(ArquivoPermissionarioConcessionario.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ArquivoPermissionarioConcessionario salvarMerge(ArquivoPermissionarioConcessionario selecionado) {
        selecionado.setCodigo(singletonGeradorCodigo.getProximoCodigo(ArquivoPermissionarioConcessionario.class, "codigo"));
        return em.merge(selecionado);
    }

    @Override
    public ArquivoPermissionarioConcessionario recuperar(Object id) {
        ArquivoPermissionarioConcessionario arq = super.recuperar(id);
        arq.getItensArquivo().size();
        return arq;
    }

    public void gerarItensArquivo(ArquivoPermissionarioConcessionario arquivo) {
        String sql = "SELECT distinct " +
            "     2 AS REGISTRO," +
            "     'F' AS TIPOPESSOA," +
            "     REPLACE(REPLACE(REPLACE(COALESCE(PF.CPF,'0'),'.',''),'-',''),'/','') AS CPF_CNPJ," +
            "     PF.NOME AS NOMEPERMISSIONARIO," +
            "     TO_CHAR(PT.INICIOVIGENCIA, 'dd/MM/yyyy') AS DATAPERMISSAO," +
            "     null AS VALIDADE," +
            "     CASE WHEN PT.TIPOPERMISSAORBTRANS = :frete THEN :freteiro " +
            "          WHEN PT.TIPOPERMISSAORBTRANS = :taxi THEN :taxista " +
            "          WHEN PT.TIPOPERMISSAORBTRANS = :moto_taxi THEN :moto_taxista " +
            "     ELSE NULL END AS TIPOPERMISSAORBTRANS     " +
            " FROM PERMISSIONARIO P" +
            " INNER JOIN PERMISSAOTRANSPORTE PT ON PT.ID = P.PERMISSAOTRANSPORTE_ID" +
            " INNER JOIN CADASTROECONOMICO CE ON CE.ID = P.CADASTROECONOMICO_ID" +
            " INNER JOIN PESSOAFISICA PF ON PF.ID = CE.PESSOA_ID" +
            " WHERE :DATAATUAL BETWEEN P.INICIOVIGENCIA AND COALESCE(P.FINALVIGENCIA, :DATAATUAL)" +
            " UNION " +
            " SELECT distinct " +
            "      2 AS REGISTRO," +
            "      'F' AS TIPOPESSOA," +
            "      REPLACE(REPLACE(REPLACE(COALESCE(PF.CPF,'0'),'.',''),'-',''),'/','') AS CPF_CNPJ," +
            "      PF.NOME AS NOMEPERMISSIONARIO," +
            "      TO_CHAR(CEASA.DATAINICIO, 'dd/MM/yyyy') AS DATAPERMISSAO," +
            "      TO_CHAR(CEASA.DATAFIM, 'dd/MM/yyyy') AS VALIDADE," +
            "      'CONTRATOS_DE_CEASA' AS TIPOPERMISSIONARIO" +
            " FROM CONTRATOCEASA CEASA" +
            " INNER JOIN CADASTROECONOMICO CE ON CE.ID = CEASA.LOCATARIO_ID" +
            " INNER JOIN PESSOAFISICA PF ON PF.ID = CE.PESSOA_ID" +
            " WHERE :DATAATUAL BETWEEN CEASA.DATAINICIO AND COALESCE(CEASA.DATAFIM, :DATAATUAL) " +
            " AND CEASA.SITUACAOCONTRATO = :ceasa_ativo " +
            " UNION " +
            " SELECT distinct " +
            "      2 AS REGISTRO," +
            "      'F' AS TIPOPESSOA," +
            "      REPLACE(REPLACE(REPLACE(COALESCE(PF.CPF,'0'),'.',''),'-',''),'/','') AS CPF_CNPJ," +
            "      PF.NOME AS NOMEPERMISSIONARIO," +
            "      TO_CHAR(RENDAS.DATAINICIO, 'dd/MM/yyyy') AS DATAPERMISSAO," +
            "      TO_CHAR(RENDAS.DATAFIM, 'dd/MM/yyyy') AS VALIDADE," +
            "      'CONTRATOS_DE_RENDAS_PATRIMONIAIS' AS TIPOPERMISSIONARIO" +
            "  FROM CONTRATORENDASPATRIMONIAIS RENDAS" +
            " INNER JOIN PESSOAFISICA PF ON PF.ID = RENDAS.LOCATARIO_ID" +
            " WHERE :DATAATUAL BETWEEN RENDAS.DATAINICIO AND COALESCE(RENDAS.DATAFIM, :DATAATUAL)" +
            " AND RENDAS.SITUACAOCONTRATO = :rendas_ativo";

        Query q = em.createNativeQuery(sql);
        q.setParameter("DATAATUAL", DataUtil.dataSemHorario(sistemaFacade.getDataOperacao()));
        q.setParameter("frete", TipoPermissaoRBTrans.FRETE.name());
        q.setParameter("taxi", TipoPermissaoRBTrans.TAXI.name());
        q.setParameter("moto_taxi", TipoPermissaoRBTrans.MOTO_TAXI.name());
        q.setParameter("freteiro", ItemPermissionarioConcessionario.TipoArquivoPermissionarioConcessionario.FRETEIROS.name());
        q.setParameter("taxista", ItemPermissionarioConcessionario.TipoArquivoPermissionarioConcessionario.TAXISTAS.name());
        q.setParameter("moto_taxista", ItemPermissionarioConcessionario.TipoArquivoPermissionarioConcessionario.MOTO_TAXISTAS.name());
        q.setParameter("rendas_ativo", SituacaoContratoRendasPatrimoniais.ATIVO.name());
        q.setParameter("ceasa_ativo", SituacaoContratoCEASA.ATIVO.name());
        List<Object[]> resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            arquivo.getItensArquivo().clear();
            for (Object[] obj : resultList) {
                String cpfCnpj = String.valueOf(obj[2]);
                Boolean cpfValido = Util.valida_CpfCnpj(cpfCnpj);
                if (!verificarSeJaExisteCpfCnpjAdicionado(cpfCnpj, arquivo.getItensArquivo()) || !cpfValido) {
                    ItemPermissionarioConcessionario item = new ItemPermissionarioConcessionario(arquivo);
                    item.setRegistro(((BigDecimal) obj[0]).intValue());
                    item.setTipoPessoa(String.valueOf(obj[1]));
                    item.setCpfCnpj(cpfCnpj);
                    item.setValido(cpfValido);
                    item.setNomePermissionario(obj[3] != null ? StringUtil.removeAcentuacao((String) obj[3]) : "");
                    try {
                        item.setDataPermissao(DataUtil.getDateParse(String.valueOf(obj[4])));
                    } catch (Exception ex) {
                        item.setDataPermissao(null);
                    }
                    try {
                        item.setDataVencimento(DataUtil.getDateParse(String.valueOf(obj[5])));
                    } catch (Exception ex) {
                        item.setDataVencimento(DataUtil.getUltimoDiaAno(sistemaFacade.getExercicioCorrente().getAno()+1));
                    }
                    try {
                        item.setTipoPermissionario(ItemPermissionarioConcessionario.TipoArquivoPermissionarioConcessionario.valueOf((String) obj[6]));
                    } catch (Exception ex) {
                        item.setTipoPermissionario(null);
                    }
                    arquivo.getItensArquivo().add(item);
                }
            }
        }
    }

    private boolean verificarSeJaExisteCpfCnpjAdicionado(String cpfCnpj, List<ItemPermissionarioConcessionario> itensArquivo) {
        for (ItemPermissionarioConcessionario item : itensArquivo) {
            if (item.getCpfCnpj().equals(cpfCnpj)) {
                return true;
            }
        }
        return false;
    }

    public void gerarHeaderArquivo(PrintWriter gravarArq, ArquivoPermissionarioConcessionario arquivo) {
        StringBuffer header = new StringBuffer();
        HierarquiaOrganizacional prefeitura = hierarquiaOrganizacionalFacade.getRaizHierarquia(sistemaFacade.getDataOperacao());
        header.append(1)
            .append(prefeitura.getSubordinada().getUf())
            .append("04034583000122")
            .append(DataUtil.getDataFormatada(arquivo.getDataOperacao()).replace("/", ""))
            .append("0000000000")
            .append(DataUtil.getDataFormatada(arquivo.getDataOperacao(), "yyyyMM"))
            .append("0001")
            .append("000000")
            .append("0000")
            .append(100).append("ATSEPER")
            .append(String.format("%-167s", " "));
        gravarArq.printf(header.toString() + LINE_SEPARATOR);
    }

    public void gerarDetailArquivo(PrintWriter gravarArq, ArquivoPermissionarioConcessionario arquivo) {
        for (ItemPermissionarioConcessionario item : arquivo.getItensArquivo()) {
            if (item.getValido()) {
                gravarArq.printf(item.getConcatenacaoDosAtributos() + LINE_SEPARATOR);
            }
        }
    }

    public void gerarTrailerArquivo(PrintWriter gravarArq, ArquivoPermissionarioConcessionario arquivo) {
        StringBuffer footer = new StringBuffer();
        int size = 0;

        StringBuilder detail = new StringBuilder();
        for (ItemPermissionarioConcessionario item : arquivo.getItensArquivo()) {
            if (item.getValido()) {
                size++;
                detail.append(item.getConcatenacaoDosAtributos());
            }
        }

        Checksum checksum = new CRC32();
        checksum.update(detail.toString().getBytes(), 0, detail.toString().getBytes().length);

        footer.append(9)
            .append(String.format("%09d", size))
            .append(String.format("%032d", checksum.getValue()))
            .append(String.format("%-190s", " "));
        gravarArq.printf(footer.toString());
    }

    public void downloadArquivo(ArquivoPermissionarioConcessionario arquivo) throws IOException {
        escreverNoResponse(getBytes(criarArquivo(arquivo)), NOME_ARQUIVO);
    }

    public File criarArquivo(ArquivoPermissionarioConcessionario arquivo) throws IOException {
        File file = new File(NOME_ARQUIVO);
        FileWriter arq = new FileWriter(file);
        PrintWriter gravarArq = new PrintWriter(arq);

        gerarHeaderArquivo(gravarArq, arquivo);
        gerarDetailArquivo(gravarArq, arquivo);
        gerarTrailerArquivo(gravarArq, arquivo);

        arq.close();
        return file;
    }

    public void escreverNoResponse(byte[] bytes, String nome) throws IOException {
        if (bytes != null && bytes.length > 0) {
            HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            response.setContentType("application/txt");
            response.setHeader("Content-Disposition", "inline; filename=" + NOME_ARQUIVO);
            response.setContentLength(bytes.length);
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(bytes, 0, bytes.length);
            outputStream.flush();
            outputStream.close();
            FacesContext.getCurrentInstance().responseComplete();
        }
    }

    public byte[] getBytes(File file) throws IOException {
        int len = (int) file.length();
        byte[] sendBuf = new byte[len];
        FileInputStream inFile = null;
        try {
            inFile = new FileInputStream(file);
            inFile.read(sendBuf, 0, len);
        } catch (FileNotFoundException fnfex) {
            logger.error(fnfex.getMessage());
        } catch (IOException ioex) {
            logger.error(ioex.getMessage());
        } finally {
            inFile.close();
        }
        return sendBuf;
    }
}
