merge into movimentoestoque m
    using (select mov.id                as id_mov,
                  item.saidamaterial_id as id_origem,
                  item.numeroitem       as numero_item,
                  item.id               as id_item
           from movimentoestoque mov
                    inner join itemsaidamaterial item on item.movimentoestoque_id = mov.id)
        dados
    on (m.id = dados.id_mov)
    when matched then
        update
            set m.idorigem = dados.id_origem,
                m.numeroitem = dados.numero_item;


merge into movimentoestoque m
    using (select mov.id                  as id_mov,
                  item.entradamaterial_id as id_origem,
                  item.numeroitem         as numero_item,
                  item.id                 as id_item
           from movimentoestoque mov
                    inner join itementradamaterial item on item.movimentoestoque_id = mov.id)
        dados
    on (m.id = dados.id_mov)
    when matched then
        update
            set m.idorigem = dados.id_origem,
                m.numeroitem = dados.numero_item;

merge into movimentoestoque m
    using (select mov.id                 as id_mov,
                  iem.entradamaterial_id as id_origem,
                  iem.numeroitem         as numero_item,
                  item.id                as id_item
           from movimentoestoque mov
                    inner join itementradamaterialestorno item on item.movimentoestoque_id = mov.id
                    inner join itementradamaterial iem on iem.id = item.itementradamaterial_id)
        dados
    on (m.id = dados.id_mov)
    when matched then
        update
            set m.idorigem = dados.id_origem,
                m.numeroitem = dados.numero_item;


merge into movimentoestoque m
    using (select mov.id                         as id_mov,
                  item.conversaounidademedida_id as id_origem,
                  item.numeroitem                as numero_item,
                  item.id                        as id_item
           from movimentoestoque mov
                    inner join itemconversaounidademedmat item on item.movimentoestoqueentrada_id = mov.id)
        dados
    on (m.id = dados.id_mov)
    when matched then
        update
            set m.idorigem = dados.id_origem,
                m.numeroitem = dados.numero_item;


merge into movimentoestoque m
    using (select mov.id                         as id_mov,
                  item.conversaounidademedida_id as id_origem,
                  item.numeroitem                as numero_item,
                  item.id                        as id_item
           from movimentoestoque mov
                    inner join itemconversaounidademedmat item on item.movimentoestoquesaida_id = mov.id)
        dados
    on (m.id = dados.id_mov)
    when matched then
        update
            set m.idorigem = dados.id_origem,
                m.numeroitem = dados.numero_item;
