INSERT INTO TIPOMANUAL (ID, DESCRICAO, ORDEM, HABILITAREXIBICAO)
VALUES (hibernate_sequence.nextval, 'Utilização do Sistema', 1, 1);

INSERT INTO MANUALNFSE (ID, TIPOMANUAL_ID, NOME, RESUMO, ORDEM, HABILITAREXIBICAO, DETENTORARQUIVOCOMPOSICAO_ID)
VALUES (hibernate_sequence.nextval,
        (select id from TIPOMANUAL where DESCRICAO = 'Utilização do Sistema'),
        'Manual da Pessoa Física e Tomador de Serviços',
        'O objetivo deste manual é orientar a pessoa física, o tomador de serviços e as empresas de fora do município de como realizar o cadastramento no site e acessar o sistema.',
        1,
        1,
        null);
INSERT INTO MANUALNFSE (ID, TIPOMANUAL_ID, NOME, RESUMO, ORDEM, HABILITAREXIBICAO, DETENTORARQUIVOCOMPOSICAO_ID)
VALUES (hibernate_sequence.nextval,
        (select id from TIPOMANUAL where DESCRICAO = 'Utilização do Sistema'),
        'Manual do Prestador de Serviços Emitente de NFS-e',
        'O objetivo deste manual é orientar a pessoa juridica prestador de serviços de como realizar o cadastramento no site e acessar o sistema, emissão de NFS-e.',
        2,
        1,
        null);
INSERT INTO MANUALNFSE (ID, TIPOMANUAL_ID, NOME, RESUMO, ORDEM, HABILITAREXIBICAO, DETENTORARQUIVOCOMPOSICAO_ID)
VALUES (hibernate_sequence.nextval,
        (select id from TIPOMANUAL where DESCRICAO = 'Utilização do Sistema'),
        'LayOut exportação NFSe - XML',
        'O objetivo deste manual é informar o LayOut dos dados enviados via XML das Notas Fiscais de Serviços Eletronicos gerados pelo sistema, para ser importados nos sistemas Legados.',
        3,
        1,
        null);
