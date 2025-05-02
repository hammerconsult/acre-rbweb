update LANCEPREGAO
set PROPOSTAFORNECEDOR_ID = 10495316556
where id in (select lanc.id
             from LANCEPREGAO lanc
                      inner join rodadapregao rod on rod.id = lanc.RODADAPREGAO_ID
                      inner join itempregao item on item.id = rod.ITEMPREGAO_ID
             where PROPOSTAFORNECEDOR_ID = 10495316574);


update ITEMCONTRATOITEMPROPFORNEC set ITEMPROPOSTAFORNECEDOR_ID = 10495316557
where id = 10558635054;

delete from ITEMPROPFORNEC
where LOTEPROPOSTAFORNECEDOR_ID = (select lote.id from LOTEPROPFORNEC lote where lote.PROPOSTAFORNECEDOR_ID = 10495316574);

delete from LOTEPROPFORNEC
where id = (select lote.id from LOTEPROPFORNEC lote where lote.PROPOSTAFORNECEDOR_ID = 10495316574);

delete from PROPOSTAFORNECEDOR
where id = 10495316574;


delete from PROPOSTAFORNECEDOR
where id = 10719719887;


update STATUSFORNECEDORLICITACAO set LICITACAOFORNECEDOR_ID = 715515656 where id = 717079228;

delete from LICITACAODOCTOFORNECEDOR
where LICITACAOFORNECEDOR_ID = 715515611;

delete from LICITACAOFORNECEDOR where id = 715515611;

update propostafornecedor prop set prop.LICITACAOFORNECEDOR_ID = (select lf.id
                                                                  from LICITACAOFORNECEDOR lf
                                                                  where lf.LICITACAO_ID = prop.LICITACAO_ID
                                                                    and lf.EMPRESA_ID = prop.FORNECEDOR_ID);

update REGISTROSOLMATEXT set TIPOAVALIACAO = 'MENOR_PRECO'
where TIPOAVALIACAO = 'MAIOR_OFERTA_DE_PRECO';
