update ITEMSOLICITACAOEXTERNO
set VALORTOTAL = VALORUNITARIO
where TIPOCONTROLE = 'VALOR';

update ITEMSOLICITACAOEXTERNO ITEM
set ITEM.DESCRICAOPRODUTO =
        (SELECT OC.DESCRICAO
         FROM ITEMSOLICITACAOEXTERNO ISE
                  INNER JOIN OBJETOCOMPRA OC ON OC.ID = ISE.OBJETOCOMPRA_ID
         WHERE ISE.ID = ITEM.ID)
WHERE ITEM.DESCRICAOPRODUTO IS NULL;
