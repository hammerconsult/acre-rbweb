package br.com.webpublico.negocios;

import br.com.webpublico.entidades.NFSAvulsa;
import br.com.webpublico.entidadesauxiliares.AutenticaNFSAvulsa;
import br.com.webpublico.entidadesauxiliares.Criptografia;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.GeradorChaveAutenticacao;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.ws.model.WSNFSAvulsa;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: André Gustavo
 * Date: 20/03/14
 * Time: 08:43
 */
@Stateless
public class ConsultaAutenticidadeNFSAvulsaFacade implements Serializable {

    private static Logger logger = LoggerFactory.getLogger(ConsultaAutenticidadeNFSAvulsaFacade.class);
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public String geraNumeroAutenticidade(Date dataEmissao, String numeroNota, String cpfCnpjNota) {

        String diaEmissao = StringUtils.leftPad(DataUtil.getDataFormatada(dataEmissao, "dd"), 2, "0");
        String mesEmissao = StringUtils.leftPad(DataUtil.getDataFormatada(dataEmissao, "MM"), 2, "0");
        String anoEmissao = StringUtils.leftPad(DataUtil.getDataFormatada(dataEmissao, "yyyy"), 4, "0");

        String chave = new StringBuilder(diaEmissao).append(mesEmissao).append(anoEmissao).toString();

        //System.out.println("CHAVE ---- " + chave);

        Criptografia criptografia = new Criptografia("1W8T jU3aR", chave);

//Autenticidade com Número da Nota
        String numero = StringUtils.leftPad(numeroNota, 10, "0");
        //System.out.println("numero --- " + numero);
        String restoNumero = numero.substring(10, 11);
        //System.out.println("restoNumero --- " + restoNumero);
        //System.out.println("Senha numero --- " + numero);
        criptografia.setPSenha(numero);
        String numeroAutenticidadeNumeroNota = criptografia.getRSenhaCripto();
        //System.out.println("NumeroAutenticacaoNota sem corte ----- " + numeroAutenticidadeNumeroNota);


        numeroAutenticidadeNumeroNota = new StringBuilder(numeroAutenticidadeNumeroNota.trim().substring(1, 2))
            .append(numeroAutenticidadeNumeroNota.trim().substring(3, 4))
            .append(numeroAutenticidadeNumeroNota.trim().substring(5, 6))
            .append(numeroAutenticidadeNumeroNota.trim().substring(7, 8))
            .append(numeroAutenticidadeNumeroNota.trim().substring(9, 10))
            .append(numeroAutenticidadeNumeroNota.trim().substring(11, 12))
            .append(numeroAutenticidadeNumeroNota.trim().substring(13, 14))
            .append(numeroAutenticidadeNumeroNota.trim().substring(15, 16))
            .append(numeroAutenticidadeNumeroNota.trim().substring(17, 18))
            .append(numeroAutenticidadeNumeroNota.trim().substring(19, 20))
            .toString();

        //System.out.println("NumeroAutenticacaoNota ----- " + numeroAutenticidadeNumeroNota);

//Autenticidade com CPF/CNPJ
        //System.out.println("cpfCNPJNota ----- " + cpfCnpjNota);
        String cpfCnpj = StringUtils.leftPad(Util.removeMascaras(cpfCnpjNota), 14, "0");
        //System.out.println("CPFCNPJ ---- " + cpfCnpj);
        String restoCpfCnpj = cpfCnpj.substring(10, 13);
        cpfCnpj = cpfCnpj.substring(0, 10);
        //System.out.println("Senha CPF ----- " + cpfCnpj);
        criptografia.setPSenha(cpfCnpj);
        String numeroAutenticidadeCpfCnpj = criptografia.getRSenhaCripto();

        numeroAutenticidadeCpfCnpj = new StringBuilder(numeroAutenticidadeCpfCnpj.trim().substring(1, 2))
            .append(numeroAutenticidadeCpfCnpj.trim().substring(3, 4))
            .append(numeroAutenticidadeCpfCnpj.trim().substring(5, 6))
            .append(numeroAutenticidadeCpfCnpj.trim().substring(7, 8))
            .append(numeroAutenticidadeCpfCnpj.trim().substring(9, 10))
            .append(numeroAutenticidadeCpfCnpj.trim().substring(11, 12))
            .append(numeroAutenticidadeCpfCnpj.trim().substring(13, 14))
            .append(numeroAutenticidadeCpfCnpj.trim().substring(15, 16))
            .append(numeroAutenticidadeCpfCnpj.trim().substring(17, 18))
            .append(numeroAutenticidadeCpfCnpj.trim().substring(19, 20))
            .toString();


//Autenticidade com data de Emissão
        String dataNFA;
        String dataFormatada = DataUtil.getDataFormatada(dataEmissao, "yyyyMMdd");
        if (Integer.parseInt(mesEmissao) < 10) {
            dataNFA = new StringBuilder(dataFormatada.substring(0, 2))
                .append(dataFormatada.substring(4, 5))
                .append(dataFormatada.substring(7, 8))
                .append(restoCpfCnpj.trim())
                .append(restoNumero.trim())
                .toString();
        } else {
            dataNFA = new StringBuilder(dataFormatada.substring(0, 2))
                .append(dataFormatada.substring(4, 6))
                .append(dataFormatada.substring(7, 8))
                .append(restoCpfCnpj.trim())
                .append(restoNumero.trim())
                .toString();
        }


        String data = StringUtils.leftPad(dataNFA, 10, "0");
        //System.out.println("DATA NFA --- " + dataNFA);
        criptografia.setPSenha(dataNFA);
        String autenticidadeDataEmissao = criptografia.getRSenhaCripto();

        autenticidadeDataEmissao = new StringBuilder(autenticidadeDataEmissao.trim().substring(1, 2))
            .append(autenticidadeDataEmissao.trim().substring(3, 4))
            .append(autenticidadeDataEmissao.trim().substring(5, 6))
            .append(autenticidadeDataEmissao.trim().substring(7, 8))
            .append(autenticidadeDataEmissao.trim().substring(9, 10))
            .append(autenticidadeDataEmissao.trim().substring(11, 18))
            .append(autenticidadeDataEmissao.trim().substring(19, 20))
            .toString();

        StringBuilder numeroAutenticidade = new StringBuilder(numeroAutenticidadeNumeroNota)
            .append(autenticidadeDataEmissao)
            .append(numeroAutenticidadeCpfCnpj);


        int j = 0;
        int k = numeroAutenticidade.length() - 1;
        //System.out.println("tamanho -- " + numeroAutenticidade.length());
        int contador = 0;
        String numeroFinal = "";


//        while (j < k) {
//            contador++;
//            j++;
//            if (contador == 4) {
//                contador = j + 1;
//                if (contador < k) {
//                    //System.out.println("-----------------------------------------------------------");
//                    //System.out.println(0 + "," + j);
//                    //System.out.println(contador + "," + k);
//                    //System.out.println("-----------------------------------------------------------");
//                }
//                j++;
//
//                contador = 0;
//            }
//        };

        while (j < k) {
            contador++;
            j++;
            if (contador == 4) {
                contador = j + 1;
                if (contador < k) {
                    numeroAutenticidade = new StringBuilder(numeroAutenticidade.substring(0, j) + "." + numeroAutenticidade.substring(contador - 1, k));
                }
                j++;
                //   k++;
                contador = 0;
            }
            //System.out.println("J --- " + j);
            //System.out.println("K --- " + k);
        }
        //System.out.println(numeroFinal);
        //System.out.println(numeroAutenticidade.toString());

        return numeroAutenticidade.toString();

    }

    public String converteBytesParaString(byte[] value) throws UnsupportedEncodingException {
        byte[] base64 = Base64.encodeBase64(value);
        String retorno = new String(base64, "UTF-8");
        return retorno;
    }

    public String geraCodigo(Date dataEmissao, String numeroNota, String cpfCnpj) {
        String dataFormatada = DataUtil.getDataFormatada(dataEmissao, "ddMMyyyy");
        String cpfCnpjSemMascara = "";
        if (cpfCnpj != null) {
            cpfCnpjSemMascara = Util.removeMascaras(cpfCnpj);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(dataFormatada).append(numeroNota).append(cpfCnpjSemMascara);
        return sb.toString();
    }

    public String gerarCodigoAutenticidade(Date dataEmissao, String numeroNota, String cpfCnpj) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(geraCodigo(dataEmissao, numeroNota, cpfCnpj).getBytes());
            return Util.substituiCaracterEspecial(converteBytesParaString(md.digest()).toUpperCase(), "A");
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public WSNFSAvulsa notaAutenticada(AutenticaNFSAvulsa autenticaNFSAvulsa) {
        try {
            String sql = " select nf.* from NFSAvulsa nf " +
                " left join cadastroeconomico ce on ce.id = nf.cmctomador_id " +
                " left join PessoaFisica pf on pf.id = coalesce(nf.tomador_id, ce.pessoa_id) " +
                " left join PessoaJuridica pj on pj.id = coalesce(nf.tomador_id, ce.pessoa_id)  " +
                " where nf.numero = :numeroNota " +
                "   and replace(replace(replace(coalesce(pf.cpf,pj.cnpj),'.',''),'-',''),'/','') like :cpfCnpj " +
                "   and trunc(nf.emissao) = trunc(:emissao) ";
            Query q = em.createNativeQuery(sql, NFSAvulsa.class);
            q.setParameter("numeroNota", Long.valueOf(autenticaNFSAvulsa.getNumeroNota()));
            q.setParameter("cpfCnpj", StringUtil.retornaApenasNumeros(autenticaNFSAvulsa.getCpfCnpj()));
            q.setParameter("emissao", autenticaNFSAvulsa.getDataLancamento());
            if (!q.getResultList().isEmpty()) {
                NFSAvulsa nota = (NFSAvulsa) q.getResultList().get(0);
                String autenticidade = GeradorChaveAutenticacao
                    .geraChaveDeAutenticacao(Util.dateToString(autenticaNFSAvulsa.getDataLancamento()) + autenticaNFSAvulsa.getNumeroNota() + StringUtil.retornaApenasNumeros(autenticaNFSAvulsa.getCpfCnpj()), GeradorChaveAutenticacao.TipoAutenticacao.NFSAVULSA);
                if (autenticidade.equals(StringUtil.removeCaracteresEspeciaisSemEspaco(autenticaNFSAvulsa.getNumeroAutenticidade()).trim())) {
                    return new WSNFSAvulsa(nota);
                }
            }
        } catch (Exception e) {
            logger.trace("Erro: ", e);
        }
        return null;
    }

    public boolean validouNotaFiscalAvulsa(AutenticaNFSAvulsa autenticaNFSAvulsa) {
        return notaAutenticada(autenticaNFSAvulsa) != null;
    }
}
