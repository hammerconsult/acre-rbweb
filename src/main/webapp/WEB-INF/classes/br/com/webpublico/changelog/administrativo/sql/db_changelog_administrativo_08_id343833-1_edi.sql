update ITEMSAIDAMATERIAL ITEM
set item.MATERIAL_ID = (select mov.material_id from MOVIMENTOESTOQUE mov where mov.id = item.MOVIMENTOESTOQUE_ID);

update ITEMENTRADAMATERIAL ITEM
set item.MATERIAL_ID = (select mov.material_id from MOVIMENTOESTOQUE mov where mov.id = item.MOVIMENTOESTOQUE_ID);

update ITEMSAIDAMATERIAL
set QUANTIDADE     = 3700,
    QUANTIDADETOTAL= 3700,
    VALORUNITARIO  = 0.8968,
    VALORTOTAL     = 3318.40
where id = 10807691316;

update ENTRADAMATERIAL
set DATAENTRADA = to_date('26/01/2023 10:33:53', 'dd/MM/yyyy HH24:mi:ss')
where id = 10916449611;

update SAIDAMATERIAL
set DATASAIDA     = to_date('26/01/2023 15:33:53', 'dd/MM/yyyy HH24:mi:ss'),
    DATACONCLUSAO = to_date('26/01/2023 15:33:53', 'dd/MM/yyyy HH24:mi:ss')
where id = 10900109821;

update ITEMSAIDAMATERIAL
set VALORTOTAL    = 62477.11,
    VALORUNITARIO = 12495.422
where id = 10900109822;

update ITEMSAIDAMATERIAL
set QUANTIDADE      = 1267,
    QUANTIDADETOTAL = 1267,
    VALORUNITARIO   = 183.6106,
    VALORTOTAL      = 232634.67
where id = 10807660691;

update ITEMSAIDAMATERIAL
set QUANTIDADE      = 10,
    QUANTIDADETOTAL = 10,
    VALORUNITARIO   = 4019.3560,
    VALORTOTAL      = 40193.56
where id = 10839773015;
