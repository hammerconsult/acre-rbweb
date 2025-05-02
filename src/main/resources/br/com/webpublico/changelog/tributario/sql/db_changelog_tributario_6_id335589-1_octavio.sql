delete from baixaveiculoperm
where id in (select baixa.id from permissaotransporte pt
             inner join permissionario p on pt.id = p.permissaotransporte_id
             inner join cadastroeconomico cmc on p.cadastroeconomico_id = cmc.id
             inner join veiculopermissionario v on pt.id = v.permissaotransporte_id
             inner join baixaveiculoperm baixa on v.id = baixa.veiculopermissionario_id
             where cmc.inscricaocadastral = '1165909'
             and v.id = (select max(vp.id) from veiculopermissionario vp
                         inner join permissaotransporte permissao on vp.permissaotransporte_id = permissao.id
                         inner join permissionario on permissao.id = permissionario.permissaotransporte_id
                         where permissionario.cadastroeconomico_id = cmc.id));

update veiculotransporte
set placa = 'QLU9053'
where id = (select vt.id from permissaotransporte pt
            inner join permissionario p on pt.id = p.permissaotransporte_id
            inner join cadastroeconomico cmc on p.cadastroeconomico_id = cmc.id
            inner join veiculopermissionario v on pt.id = v.permissaotransporte_id
            inner join veiculotransporte vt on v.veiculotransporte_id = vt.id
            where cmc.inscricaocadastral = '1165909'
            and v.id = (select max(vp.id) from veiculopermissionario vp
                        inner join permissaotransporte permissao on vp.permissaotransporte_id = permissao.id
                        inner join permissionario on permissao.id = permissionario.permissaotransporte_id
                        where permissionario.cadastroeconomico_id = cmc.id));

update veiculopermissionario
set iniciovigencia = finalvigencia,
    finalvigencia = null
where id = (select v.id from permissaotransporte pt
            inner join permissionario p on pt.id = p.permissaotransporte_id
            inner join cadastroeconomico cmc on p.cadastroeconomico_id = cmc.id
            inner join veiculopermissionario v on pt.id = v.permissaotransporte_id
            where cmc.inscricaocadastral = '1165909'
            and v.id = (select max(vp.id) from veiculopermissionario vp
                        inner join permissaotransporte permissao on vp.permissaotransporte_id = permissao.id
                        inner join permissionario on permissao.id = permissionario.permissaotransporte_id
                        where permissionario.cadastroeconomico_id = cmc.id));
